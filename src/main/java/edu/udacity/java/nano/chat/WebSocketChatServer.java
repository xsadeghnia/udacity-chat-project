package edu.udacity.java.nano.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */

@Component
@ServerEndpoint("/chat")
public class WebSocketChatServer {

    /**
     * All chat sessions.
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();
    private static Map<String, String> sessionIdToUsernameMap = new ConcurrentHashMap<>();

    public static boolean isUserOnline(String userName){
       return onlineSessions.containsKey(userName);
    }

    public WebSocketChatServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectMapper objectMapper = new ObjectMapper();
                while (true){
                    try {
                        ChatResponse chatResponse = ChatResponse.createUserCountResponse(onlineSessions.values().size());
                        String responseStr = objectMapper.writeValueAsString(chatResponse);
                        for (Session s : onlineSessions.values()){
                            s.getBasicRemote().sendText(responseStr);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * Open connection, 1) add session, 2) add user.
     */
    @OnOpen
    public void onOpen(Session session) {

    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Deserialize request json string to request object.
            ChatRequest chatRequest = objectMapper.readValue(jsonStr, ChatRequest.class);
            switch (chatRequest.getType()){
                case ChatRequest.ENTER: {
                    // Update internal maps.
                    String username = chatRequest.getArg1();
                    onlineSessions.put(username, session);
                    sessionIdToUsernameMap.put(session.getId(), username);
                    // Create response object.
                    ChatResponse welcomeResponse = ChatResponse.createWelcomeResponse(username);
                    // Serialize response object to json.
                    String welcomeStr = objectMapper.writeValueAsString(welcomeResponse);
                    // Send response json string to client.
                    session.getBasicRemote().sendText(welcomeStr);

                    // Create USER_JOINED response object.
                    ChatResponse joinedResponse = ChatResponse.createUserJoinedResponse(username);
                    String joinedStr = objectMapper.writeValueAsString(joinedResponse);
                    for (Session s : onlineSessions.values()){
                        s.getBasicRemote().sendText(joinedStr);
                    }
                    ChatResponse listResponse = ChatResponse.createUserListResponse(
                            new ArrayList<>(onlineSessions.keySet()));
                    String listStr = objectMapper.writeValueAsString(listResponse);
                    session.getBasicRemote().sendText(listStr);
                    break;
                }
                case ChatRequest.CHAT: {
                    // Create response object.
                    ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
                     // Serialize.
                    String responseStr = objectMapper.writeValueAsString(receivedResponse);
                    // Send response json string to client.
                    session.getBasicRemote().sendText(responseStr);
                    //
                    String username = sessionIdToUsernameMap.get(session.getId());
                    String message = chatRequest.getArg1();
                    ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
                    // Serialize notify message.
                    String notifyStr = objectMapper.writeValueAsString(notifyResponse);
                    // Send to all sessions
                    for (Session s : onlineSessions.values()){
                        s.getBasicRemote().sendText(notifyStr);
                    }

                    break;
                }
                case ChatRequest.DIRECT: {

                    ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
                    String responseStr = objectMapper.writeValueAsString(receivedResponse);
                    session.getBasicRemote().sendText(responseStr);
                    //
                    String username = sessionIdToUsernameMap.get(session.getId());
                    String message = chatRequest.getArg1();
                    ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
                    // Serialize
                    String notifyStr = objectMapper.writeValueAsString(notifyResponse);
                    // send to a person.
                    Session receiverSession = onlineSessions.get(chatRequest.getArg2());
                    for (Session s:onlineSessions.values()){
                        if (s == receiverSession){
                            s.getBasicRemote().sendText(notifyStr);
                        }
                    }
                    break;
                }
                case ChatRequest.LEAVE: {
                    // Leave logic
                    String username = sessionIdToUsernameMap.get(session.getId());
                    onlineSessions.remove(username);
                    sessionIdToUsernameMap.remove(session.getId());

                    // Create response object.
                    ChatResponse byeResponse = ChatResponse.createByeResponse();
                    // Serialize
                    String responseStr = objectMapper.writeValueAsString(byeResponse);
                    // Send response json string to client.
                    session.getBasicRemote().sendText(responseStr);

                    ChatResponse userLeft = ChatResponse.createUserLeftResponse(username);
                    String leftStr = objectMapper.writeValueAsString(userLeft);
                    for (Session s : onlineSessions.values()) {
                        s.getBasicRemote().sendText(leftStr);
                    }

                    break;
                }
                default:{
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connection, 1) remove session, 2) update user.
     */
    @OnClose
    public void onClose(Session session) {
        String username = sessionIdToUsernameMap.get(session.getId());
        onlineSessions.remove(username);
        sessionIdToUsernameMap.remove(session.getId());
        System.out.println("close!");

    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        String username = sessionIdToUsernameMap.get(session.getId());
        onlineSessions.remove(username);
        sessionIdToUsernameMap.remove(session.getId());
        System.out.println("error!");
    }

}

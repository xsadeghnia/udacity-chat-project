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

    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();
    private static Map<String, String> sessionIdToUsernameMap = new ConcurrentHashMap<>();

    public static boolean isUserOnline(String userName){
       return onlineSessions.containsKey(userName);
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketChatServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        ChatResponse chatResponse = ChatResponse.createUserCountResponse(onlineSessions.values().size());
                        sendToAll(chatResponse);
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

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        try {
            // Deserialize request json string to request object.
            ChatRequest chatRequest = objectMapper.readValue(jsonStr, ChatRequest.class);
            switch (chatRequest.getType()){
                case ChatRequest.ENTER: {

                    String username = chatRequest.getArg1();
                    onlineSessions.put(username, session);
                    sessionIdToUsernameMap.put(session.getId(), username);

                    ChatResponse welcomeResponse = ChatResponse.createWelcomeResponse(username);
                    sendToSession(session, welcomeResponse);

                    ChatResponse joinedResponse = ChatResponse.createUserJoinedResponse(username);
                    sendToAll(joinedResponse);

                    ChatResponse listResponse = ChatResponse.createUserListResponse(
                            new ArrayList<>(onlineSessions.keySet()));
                    sendToSession(session,listResponse );

                    break;
                }
                case ChatRequest.CHAT: {
                    ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
                    sendToSession(session, receivedResponse);

                    String username = sessionIdToUsernameMap.get(session.getId());
                    String message = chatRequest.getArg1();
                    ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
                    sendToAll(notifyResponse);

                    break;
                }
                case ChatRequest.DIRECT: {
                    ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
                    sendToSession(session, receivedResponse);

                    String username = sessionIdToUsernameMap.get(session.getId());
                    String message = chatRequest.getArg1();
                    ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
                    Session receiverSession = onlineSessions.get(chatRequest.getArg2());
                    sendToSession(receiverSession,notifyResponse);

                    break;
                }
                case ChatRequest.LEAVE: {
                    removeUser(session);

                    ChatResponse byeResponse = ChatResponse.createByeResponse();
                    sendToSession(session, byeResponse);

                    String username = sessionIdToUsernameMap.get(session.getId());
                    ChatResponse userLeft = ChatResponse.createUserLeftResponse(username);
                    sendToAll(userLeft);

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

    @OnClose
    public void onClose(Session session) {
        removeUser(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        removeUser(session);
    }

    private void sendToSession(Session session, ChatResponse chatResponse) throws IOException {
        String responseStr = objectMapper.writeValueAsString(chatResponse);
        session.getBasicRemote().sendText(responseStr);
    }

    private void sendToAll(ChatResponse chatResponse) throws IOException {
        for (Session s:onlineSessions.values()){
            sendToSession(s,chatResponse);
        }
    }
    private  void removeUser(Session session){
        String username = sessionIdToUsernameMap.get(session.getId());
        onlineSessions.remove(username);
        sessionIdToUsernameMap.remove(session.getId());
    }
}

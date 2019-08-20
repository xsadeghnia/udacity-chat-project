package edu.udacity.java.nano.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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

    private static void sendMessageToAll(String msg) {
        //TODO: add send message method.
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
                    String username = chatRequest.getArg();
                    onlineSessions.put(username, session);
                    sessionIdToUsernameMap.put(session.getId(), username);

                    // Create response object.
                    ChatResponse chatResponse = new ChatResponse();
                    chatResponse.setType(ChatResponse.WELCOME);
                    chatResponse.setArg1("");
                    chatResponse.setArg2("");

                    // Serialize response object to json.
                    String responseStr = objectMapper.writeValueAsString(chatResponse);

                    // Send response json string to client.
                    session.getBasicRemote().sendText(responseStr);
                    break;
                }
                case ChatRequest.CHAT: {
                    break;
                }
                case ChatRequest.LEAVE: {

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
        //TODO: add close connection.
        System.out.println("close!");

    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
       // error.printStackTrace();
        System.out.println("error!");
    }

}

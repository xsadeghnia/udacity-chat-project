package edu.udacity.java.nano.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class WebSocketChatServer implements ChatEndpoint {

    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();
    private static Map<String, String> sessionIdToUsernameMap = new ConcurrentHashMap<>();

    public static boolean isUserOnline(String userName){
       return onlineSessions.containsKey(userName);
    }

    @Resource
    private ChatService chatService;

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
                    chatService.handleEnterReq(chatRequest, this, session);
                    break;
                }
                case ChatRequest.CHAT: {
                    String username = sessionIdToUsernameMap.get(session.getId());
                    chatService.handleChatRequest(chatRequest,this, username);

                    break;
                }
                case ChatRequest.DIRECT: {
                    String username = sessionIdToUsernameMap.get(session.getId());
                    chatService.handleDirectChatRequest(chatRequest, this,username);

                    break;
                }
                case ChatRequest.LEAVE: {
                    String username = sessionIdToUsernameMap.get(session.getId());
                    chatService.handleLeaveRequest(this,username);
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

    @Override
    public void send(String username, ChatResponse chatResponse) throws IOException {
        Session session = onlineSessions.get(username);
        sendToSession(session, chatResponse);
    }

    @Override
    public void sendToAll(ChatResponse chatResponse) throws IOException {
        sendToAllSessions(chatResponse);
    }

    @Override
    public void registerUsername(String username, Session session) {
        onlineSessions.put(username, session);
        sessionIdToUsernameMap.put(session.getId(), username);
    }

    @Override
    public void unregisterUsername(String username) {
        Session session = onlineSessions.get(username);
        removeUser(session);
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>(onlineSessions.keySet());
    }

    private void sendToSession(Session session, ChatResponse chatResponse) throws IOException {
        String responseStr = objectMapper.writeValueAsString(chatResponse);
        session.getBasicRemote().sendText(responseStr);
    }

    private void sendToAllSessions(ChatResponse chatResponse) throws IOException {
        for (Session s:onlineSessions.values()){
            sendToSession(s,chatResponse);
        }
    }
    private void removeUser(Session session){
        String username = sessionIdToUsernameMap.get(session.getId());
        onlineSessions.remove(username);
        sessionIdToUsernameMap.remove(session.getId());
    }
}

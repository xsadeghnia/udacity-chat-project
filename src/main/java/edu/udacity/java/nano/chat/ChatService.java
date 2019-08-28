package edu.udacity.java.nano.chat;

import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

interface ChatEndpoint {
    void send(String username , ChatResponse chatResponse) throws IOException;
    void sendToAll(ChatResponse chatResponse) throws IOException;
    void registerUsername(String username, Session session);
    void unregisterUsername(String username);
    List<String> getAllUsernames();
}

@Service
public class ChatService {

    public void handleEnterReq(ChatRequest chatRequest, ChatEndpoint chatEndpoint, Session session)
            throws IOException {
        String username = chatRequest.getArg1();
        chatEndpoint.registerUsername(username, session);

        ChatResponse welcomeResponse = ChatResponse.createWelcomeResponse(username);
        chatEndpoint.send(username, welcomeResponse);

        ChatResponse joinedResponse = ChatResponse.createUserJoinedResponse(username);
        chatEndpoint.sendToAll(joinedResponse);

        ChatResponse listResponse = ChatResponse.createUserListResponse(
                chatEndpoint.getAllUsernames());
        chatEndpoint.send(username, listResponse);
    }

    public void  handleChatRequest(ChatRequest chatRequest, ChatEndpoint chatEndpoint,String username)
            throws IOException {
        ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
        chatEndpoint.send(username, receivedResponse);

        String message = chatRequest.getArg1();
        ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
        chatEndpoint.sendToAll(notifyResponse);
    }

    public void  handleDirectChatRequest(ChatRequest chatRequest, ChatEndpoint chatEndpoint, String username)
            throws IOException {
        ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
        chatEndpoint.send(username, receivedResponse);

        String message = chatRequest.getArg1();
        String targetUsername = chatRequest.getArg2();
        ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
        chatEndpoint.send(targetUsername, notifyResponse);
    }
    public void handleLeaveRequest(ChatEndpoint chatEndpoint, String username)
            throws IOException {
        chatEndpoint.unregisterUsername(username);

        ChatResponse byeResponse = ChatResponse.createByeResponse();
        chatEndpoint.send(username, byeResponse);

        ChatResponse userLeft = ChatResponse.createUserLeftResponse(username);
        chatEndpoint.sendToAll(userLeft);
    }

}

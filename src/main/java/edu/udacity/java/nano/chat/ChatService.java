package edu.udacity.java.nano.chat;


import javax.websocket.Session;
import java.io.IOException;
import java.util.List;

interface ChatEndpoint {
    void send(String username , ChatResponse chatResponse) throws IOException;
    void sendToAll(ChatResponse chatResponse) throws IOException;
    void registerUsername(String username, Session session);
    void unregisterUsername(String username);
    int getNumOfUsers();
    List<String> getAllUsernames();
}

public class ChatService {

    private ChatEndpoint chatEndpoint;

    public ChatService(ChatEndpoint chatEndpoint) {
        this.chatEndpoint = chatEndpoint;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        ChatResponse chatResponse = ChatResponse.createUserCountResponse(chatEndpoint.getNumOfUsers());
                        chatEndpoint.sendToAll(chatResponse);
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

    public void handleEnterReq(ChatRequest chatRequest, Session session)
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

    public void  handleChatRequest(ChatRequest chatRequest, String username)
            throws IOException {
        ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
        chatEndpoint.send(username, receivedResponse);

        String message = chatRequest.getArg1();
        ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
        chatEndpoint.sendToAll(notifyResponse);
    }

    public void  handleDirectChatRequest(ChatRequest chatRequest, String username)
            throws IOException {
        ChatResponse receivedResponse = ChatResponse.createReceivedResponse();
        chatEndpoint.send(username, receivedResponse);

        String message = chatRequest.getArg1();
        String targetUsername = chatRequest.getArg2();
        ChatResponse notifyResponse = ChatResponse.createNotifyResponse(username, message);
        chatEndpoint.send(targetUsername, notifyResponse);
    }
    public void handleLeaveRequest(String username)
            throws IOException {
        ChatResponse byeResponse = ChatResponse.createByeResponse();
        chatEndpoint.send(username, byeResponse);

        chatEndpoint.unregisterUsername(username);

        ChatResponse userLeft = ChatResponse.createUserLeftResponse(username);
        chatEndpoint.sendToAll(userLeft);
    }

}

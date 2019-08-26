package edu.udacity.java.nano.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatResponse {
    public static final String WELCOME = "WELCOME";
    public static final String RECEIVED = "RECEIVED";
    public static final String NOTIFY = "NOTIFY";
    public static final String BYE = "BYE";
    public static final String COUNT= "COUNT";
    public static final String USER_JOINED = "USER_JOINED";
    public static final String USER_LIST = "USER_LIST";
    public static final String USER_LEFT = "USER_LEFT";

    public static ChatResponse createWelcomeResponse(String username){
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setType(ChatResponse.WELCOME);
        chatResponse.setArg1(username);
        chatResponse.setArg2("");
        return chatResponse;
    }

    public static ChatResponse createUserJoinedResponse(String username){
        ChatResponse joiendResponse = new ChatResponse();
        joiendResponse.setType(ChatResponse.USER_JOINED);
        joiendResponse.setArg1(username);
        joiendResponse.setArg2("");
        return joiendResponse;
    }

    public static ChatResponse createUserListResponse(List<String> list){
        ChatResponse userListResponse = new ChatResponse();
        userListResponse.setType(ChatResponse.USER_LIST);
        userListResponse.setArg1("");
        userListResponse.setArg2("");
        userListResponse.setList(list);
        return userListResponse;
    }

    public static ChatResponse createUserLeftResponse(String username){
        ChatResponse userLeftResponse = new ChatResponse();
        userLeftResponse.setType(ChatResponse.USER_LEFT);
        userLeftResponse.setArg1(username);
        userLeftResponse.setArg2("");
        return userLeftResponse;
    }

    public static ChatResponse createReceivedResponse(){
        ChatResponse receivedResponse = new ChatResponse();
        receivedResponse.setType(ChatResponse.RECEIVED);
        receivedResponse.setArg1("");
        receivedResponse.setArg2("");
        return receivedResponse;
    }

    public static ChatResponse createNotifyResponse(String senderUsername, String message){
        ChatResponse notifyResponse = new ChatResponse();
        notifyResponse.setType(ChatResponse.NOTIFY);
        notifyResponse.setArg1(senderUsername);
        notifyResponse.setArg2(message);
        return notifyResponse;
    }

    public static ChatResponse createByeResponse(){
        ChatResponse byeResponse = new ChatResponse();
        byeResponse.setType(ChatResponse.BYE);
        byeResponse.setArg1("");
        byeResponse.setArg2("");
        return byeResponse;
    }

    public static ChatResponse createUserCountResponse(int userCount){
        ChatResponse userCountResponse = new ChatResponse();
        userCountResponse.setType(ChatResponse.COUNT);
        userCountResponse.setArg1("" + userCount);
        userCountResponse.setArg2("");
        return userCountResponse;
    }

    private String type;
    private String arg1;
    private String arg2;
    private List<String> list;

    private ChatResponse() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}

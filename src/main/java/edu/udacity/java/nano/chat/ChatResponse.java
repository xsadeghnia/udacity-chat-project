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

    private String type;
    private String arg1;
    private String arg2;
    private List<String> list;

    public ChatResponse() {
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

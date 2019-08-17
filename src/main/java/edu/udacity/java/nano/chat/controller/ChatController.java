package edu.udacity.java.nano.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ChatController {
    @RequestMapping("/login")
    public String showLogin(){
        return "login";
    }
}

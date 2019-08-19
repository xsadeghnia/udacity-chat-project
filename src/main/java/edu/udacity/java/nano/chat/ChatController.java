package edu.udacity.java.nano.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChatController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @PostMapping("/")
    public String index(String username, Model model) {
        if (username == null || username.isEmpty()){
            model.addAttribute("emptyUsername","");
            return "login";
        }

        return "";
    }
}

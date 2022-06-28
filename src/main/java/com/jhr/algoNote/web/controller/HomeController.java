package com.jhr.algoNote.web.controller;

import com.jhr.algoNote.config.auth.dto.SessionMember;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        SessionMember user = (SessionMember) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
        }
        return "home";
    }
}

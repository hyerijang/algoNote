package com.jhr.algoNote.controller;

import com.jhr.algoNote.config.auth.dto.SessionMember;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final HttpSession httpSession;

    @RequestMapping("/admin")
    public String adminPage(Model model) {
        log.info("admin controller");
        SessionMember user = (SessionMember) httpSession.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
        }
        return "adminPage";
    }
}

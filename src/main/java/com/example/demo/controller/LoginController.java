package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping(value = {"/auth/login"})
    public String login(Authentication authentication, HttpSession session) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home"; // or whatever your post-login page is
        }
        // Clear exception after it's been displayed
        session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        return "auth/login"; // Renders the Thymeleaf template (e.g., auth/login.html)
    }

}

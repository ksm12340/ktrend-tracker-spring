package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.dto.UserJoinRequest;
import com.ktrend.ktrendtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/join")
    public String joinForm() {
        return "auth/join";
    }

    @PostMapping("/join")
    public String join(UserJoinRequest request, Model model) {
        try {
            userService.join(request);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/join";
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        return "auth/login";
    }
}
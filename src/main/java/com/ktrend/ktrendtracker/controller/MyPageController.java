package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import com.ktrend.ktrendtracker.service.FavoriteService;
import com.ktrend.ktrendtracker.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final FavoriteService favoriteService;
    private final HistoryService historyService;

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("favoriteCount", favoriteService.getFavorites(userDetails).size());
        model.addAttribute("historyCount", historyService.getHistories(userDetails).size());
        return "mypage/mypage";
    }
}

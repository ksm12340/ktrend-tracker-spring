package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import com.ktrend.ktrendtracker.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/history")
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("histories", historyService.getHistories(userDetails));
        return "history/list";
    }

    @PostMapping("/history/delete")
    public String delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long historyId
    ) {
        historyService.deleteHistory(userDetails, historyId);
        return "redirect:/history";
    }
}
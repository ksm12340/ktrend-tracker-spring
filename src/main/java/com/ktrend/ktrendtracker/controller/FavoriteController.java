package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import com.ktrend.ktrendtracker.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/favorites")
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("favorites", favoriteService.getFavorites(userDetails));
        return "favorite/list";
    }

    @PostMapping("/favorites")
    public String add(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String keyword
    ) {
        favoriteService.addFavorite(userDetails, keyword);
        return "redirect:/favorites";
    }

    @PostMapping("/favorites/delete")
    public String delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String keyword
    ) {
        favoriteService.deleteFavorite(userDetails, keyword);
        return "redirect:/favorites";
    }
}
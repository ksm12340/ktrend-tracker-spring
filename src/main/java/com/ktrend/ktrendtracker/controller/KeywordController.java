package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.entity.TrendData;
import com.ktrend.ktrendtracker.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import com.ktrend.ktrendtracker.service.HistoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
@Controller
@RequiredArgsConstructor
public class KeywordController {

    private final TrendService trendService;
    private final HistoryService historyService;

    @GetMapping("/keyword/analysis")
    public String analysis(
            @RequestParam(defaultValue = "아이폰") String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        List<TrendData> trendDataList = trendService.getTrendData(keyword);

        historyService.saveHistory(userDetails, keyword);

        model.addAttribute("keyword", keyword);
        model.addAttribute("trendDataList", trendDataList);

        return "keyword/analysis";
    }

    @GetMapping("/keyword/compare")
    public String compare(
            @RequestParam(required = false) String keywords,
            Model model
    ) {
        if (keywords == null || keywords.isBlank()) {
            keywords = "아이폰,갤럭시,손흥민";
        }

        String[] keywordArray = keywords.split(",");

        Map<String, List<TrendData>> compareData = new LinkedHashMap<>();

        for (String keyword : keywordArray) {
            String trimmedKeyword = keyword.trim();

            if (!trimmedKeyword.isBlank()) {
                compareData.put(trimmedKeyword, trendService.getTrendData(trimmedKeyword));
            }
        }

        model.addAttribute("keywords", keywords);
        model.addAttribute("compareData", compareData);

        return "keyword/compare";
    }
}
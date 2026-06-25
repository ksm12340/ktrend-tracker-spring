package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.entity.TrendData;
import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import com.ktrend.ktrendtracker.service.HistoryService;
import com.ktrend.ktrendtracker.service.TrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class KeywordController {

    private final TrendService trendService;
    private final HistoryService historyService;

    @GetMapping("/keyword/analysis")
    public String analysis(
            @RequestParam(defaultValue = "아이폰") String keyword,
            @RequestParam(defaultValue = "7d") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        String trimmedKeyword = keyword == null || keyword.isBlank() ? "아이폰" : keyword.trim();
        List<TrendData> trendDataList = trendService.getTrendData(trimmedKeyword, period, startDate, endDate);

        historyService.saveHistory(userDetails, trimmedKeyword);

        model.addAttribute("keyword", trimmedKeyword);
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("trendDataList", trendDataList);

        return "keyword/analysis";
    }

    @GetMapping("/keyword/compare")
    public String compare(
            @RequestParam(required = false) String keywords,
            @RequestParam(defaultValue = "7d") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        if (keywords == null || keywords.isBlank()) {
            keywords = "아이폰,갤럭시,손흥민";
        }

        String[] keywordArray = keywords.split(",");
        List<String> limitedKeywords = new ArrayList<>();
        boolean overLimit = false;

        for (String keyword : keywordArray) {
            String trimmedKeyword = keyword.trim();

            if (!trimmedKeyword.isBlank() && !limitedKeywords.contains(trimmedKeyword)) {
                if (limitedKeywords.size() == 5) {
                    overLimit = true;
                    break;
                }

                limitedKeywords.add(trimmedKeyword);
            }
        }

        Map<String, List<TrendData>> compareData =
                trendService.getCompareTrendData(
                        limitedKeywords,
                        period,
                        startDate,
                        endDate
                );

        if (overLimit) {
            model.addAttribute("message", "키워드 비교는 최대 5개까지만 가능합니다. 앞의 5개만 비교했습니다.");
        }

        model.addAttribute("keywords", String.join(",", limitedKeywords));
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("compareData", compareData);

        return "keyword/compare";
    }
}
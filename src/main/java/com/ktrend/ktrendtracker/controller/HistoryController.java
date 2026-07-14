package com.ktrend.ktrendtracker.controller;

import com.ktrend.ktrendtracker.entity.SearchHistory;
import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import com.ktrend.ktrendtracker.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    public static class GroupedHistory {
        private String keywordName;
        private int count;
        private java.time.LocalDateTime searchedAt;
        private List<Long> historyIds;

        public GroupedHistory(String keywordName, java.time.LocalDateTime searchedAt, Long historyId) {
            this.keywordName = keywordName;
            this.count = 1;
            this.searchedAt = searchedAt;
            this.historyIds = new ArrayList<>();
            this.historyIds.add(historyId);
        }

        public void increment(Long historyId) {
            this.count++;
            this.historyIds.add(historyId);
        }

        public String getKeywordName() { return keywordName; }
        public int getCount() { return count; }
        public java.time.LocalDateTime getSearchedAt() { return searchedAt; }
        public List<Long> getHistoryIds() { return historyIds; }

        public String getHistoryIdsString() {
            return historyIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }
    }

    @GetMapping("/history")
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<SearchHistory> histories = historyService.getHistories(userDetails);
        List<GroupedHistory> groupedHistories = new ArrayList<>();

        GroupedHistory current = null;
        for (SearchHistory h : histories) {
            String kwName = h.getKeyword().getKeywordName();
            if (current == null || !current.getKeywordName().equals(kwName)) {
                current = new GroupedHistory(kwName, h.getSearchedAt(), h.getHistoryId());
                groupedHistories.add(current);
            } else {
                current.increment(h.getHistoryId());
            }
        }

        model.addAttribute("groupedHistories", groupedHistories);
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

    @PostMapping("/history/deleteAll")
    public String deleteAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        historyService.deleteAllHistories(userDetails);
        return "redirect:/history";
    }

    @PostMapping("/history/deleteSelected")
    public String deleteSelected(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("historyIds") List<Long> historyIds
    ) {
        if (historyIds != null && !historyIds.isEmpty()) {
            historyService.deleteHistories(userDetails, historyIds);
        }
        return "redirect:/history";
    }
}

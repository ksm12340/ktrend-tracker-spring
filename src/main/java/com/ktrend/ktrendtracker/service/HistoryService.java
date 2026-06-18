package com.ktrend.ktrendtracker.service;

import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.SearchHistory;
import com.ktrend.ktrendtracker.entity.User;
import com.ktrend.ktrendtracker.repository.SearchHistoryRepository;
import com.ktrend.ktrendtracker.repository.UserRepository;
import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;
    private final TrendService trendService;

    public void saveHistory(CustomUserDetails userDetails, String keywordName) {
        if (userDetails == null || keywordName == null || keywordName.isBlank()) {
            return;
        }

        User user = getLoginUser(userDetails);
        Keyword keyword = trendService.findOrCreateKeyword(keywordName);

        searchHistoryRepository.save(new SearchHistory(user, keyword));
    }

    public List<SearchHistory> getHistories(CustomUserDetails userDetails) {
        User user = getLoginUser(userDetails);
        return searchHistoryRepository.findByUserOrderBySearchedAtDesc(user);
    }

    public void deleteHistory(CustomUserDetails userDetails, Long historyId) {
        User user = getLoginUser(userDetails);

        SearchHistory history = searchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("검색 기록을 찾을 수 없습니다."));

        if (!history.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        searchHistoryRepository.delete(history);
    }

    private User getLoginUser(CustomUserDetails userDetails) {
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
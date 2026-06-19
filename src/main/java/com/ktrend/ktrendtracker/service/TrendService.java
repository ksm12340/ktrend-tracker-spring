package com.ktrend.ktrendtracker.service;

import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.TrendData;
import com.ktrend.ktrendtracker.repository.KeywordRepository;
import com.ktrend.ktrendtracker.repository.TrendDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final KeywordRepository keywordRepository;
    private final TrendDataRepository trendDataRepository;
    private final NaverDatalabService naverDatalabService;

    private static final int MAX_SAMPLE_DAYS = 365;
    private static final List<String> POPULAR_KEYWORD_POOL = List.of(
            "아이폰", "갤럭시", "손흥민", "AI", "부산 여행",
            "뉴진스", "전기차", "챗GPT", "제주도", "취업",
            "야구", "웹툰", "무신사", "넷플릭스", "반도체"
    );

    public Keyword findOrCreateKeyword(String keywordName) {
        String trimmedKeywordName = normalizeKeyword(keywordName);

        return keywordRepository.findByKeywordName(trimmedKeywordName)
                .orElseGet(() -> keywordRepository.save(new Keyword(trimmedKeywordName)));
    }

    public List<TrendData> getTrendData(String keywordName) {
        return getTrendData(keywordName, "7d", null, null);
    }

    public List<TrendData> getTrendData(String keywordName, String period, LocalDate startDate, LocalDate endDate) {
        Keyword keyword = findOrCreateKeyword(keywordName);
        createSampleTrendDataIfNeeded(keyword);

        LocalDate today = LocalDate.now();
        LocalDate searchEndDate = today;
        LocalDate searchStartDate = today.minusDays(getPeriodDays(period) - 1L);

        if ("custom".equals(period) && startDate != null && endDate != null) {
            searchStartDate = startDate;
            searchEndDate = endDate;

            if (searchStartDate.isAfter(searchEndDate)) {
                LocalDate temp = searchStartDate;
                searchStartDate = searchEndDate;
                searchEndDate = temp;
            }
        }

        List<TrendData> naverTrendDataList = naverDatalabService.getTrendData(
                keyword,
                searchStartDate,
                searchEndDate
        );

        if (!naverTrendDataList.isEmpty()) {
            return naverTrendDataList;
        }

        return trendDataRepository.findByKeywordAndTrendDateBetweenOrderByTrendDateAsc(
                keyword,
                searchStartDate,
                searchEndDate
        );
    }

    public List<String> getPopularKeywords() {
        List<String> popularKeywords = new ArrayList<>(POPULAR_KEYWORD_POOL);

        long seed = LocalDateTime.now().getDayOfYear() * 100L + LocalDateTime.now().getHour();
        Collections.shuffle(popularKeywords, new Random(seed));

        popularKeywords.sort(Comparator.comparingInt(this::getPopularScore).reversed());

        return popularKeywords.stream()
                .limit(6)
                .toList();
    }

    public int getPeriodDays(String period) {
        if (period == null) {
            return 7;
        }

        return switch (period) {
            case "30d" -> 30;
            case "90d" -> 90;
            case "1y" -> 365;
            default -> 7;
        };
    }

    private String normalizeKeyword(String keywordName) {
        if (keywordName == null || keywordName.isBlank()) {
            return "아이폰";
        }

        return keywordName.trim();
    }

    private int getPopularScore(String keyword) {
        int timeWeight = LocalDateTime.now().getHour() * 7 + LocalDate.now().getDayOfYear();
        return Math.abs((keyword.hashCode() + timeWeight) % 100);
    }

    private void createSampleTrendDataIfNeeded(Keyword keyword) {
        List<TrendData> savedTrendDataList = trendDataRepository.findByKeywordOrderByTrendDateAsc(keyword);
        Set<LocalDate> savedDates = new HashSet<>();

        for (TrendData trendData : savedTrendDataList) {
            savedDates.add(trendData.getTrendDate());
        }

        Random random = new Random(keyword.getKeywordName().hashCode());
        LocalDate today = LocalDate.now();
        int baseValue = 35 + Math.abs(keyword.getKeywordName().hashCode() % 30);

        for (int i = MAX_SAMPLE_DAYS - 1; i >= 0; i--) {
            LocalDate trendDate = today.minusDays(i);

            if (savedDates.contains(trendDate)) {
                continue;
            }

            int weeklyWave = (int) (Math.sin(i / 7.0) * 12);
            int randomWave = random.nextInt(31) - 15;
            int value = Math.max(0, Math.min(100, baseValue + weeklyWave + randomWave));

            TrendData trendData = new TrendData(
                    keyword,
                    trendDate,
                    value,
                    "DAILY"
            );

            trendDataRepository.save(trendData);
        }
    }
}
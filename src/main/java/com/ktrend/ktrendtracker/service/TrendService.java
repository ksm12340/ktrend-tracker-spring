package com.ktrend.ktrendtracker.service;

import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.TrendData;
import com.ktrend.ktrendtracker.repository.KeywordRepository;
import com.ktrend.ktrendtracker.repository.TrendDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final KeywordRepository keywordRepository;
    private final TrendDataRepository trendDataRepository;

    public Keyword findOrCreateKeyword(String keywordName) {
        return keywordRepository.findByKeywordName(keywordName)
                .orElseGet(() -> keywordRepository.save(new Keyword(keywordName)));
    }

    public List<TrendData> getTrendData(String keywordName) {
        Keyword keyword = findOrCreateKeyword(keywordName);

        List<TrendData> trendDataList = trendDataRepository.findByKeywordOrderByTrendDateAsc(keyword);

        if (trendDataList.isEmpty()) {
            createSampleTrendData(keyword);
            trendDataList = trendDataRepository.findByKeywordOrderByTrendDateAsc(keyword);
        }

        return trendDataList;
    }

    private void createSampleTrendData(Keyword keyword) {
        Random random = new Random();

        for (int i = 6; i >= 0; i--) {
            int value = 40 + random.nextInt(61);

            TrendData trendData = new TrendData(
                    keyword,
                    LocalDate.now().minusDays(i),
                    value,
                    "DAILY"
            );

            trendDataRepository.save(trendData);
        }
    }
}
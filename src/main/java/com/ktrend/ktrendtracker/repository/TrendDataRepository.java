package com.ktrend.ktrendtracker.repository;

import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.TrendData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrendDataRepository extends JpaRepository<TrendData, Long> {

    List<TrendData> findByKeywordOrderByTrendDateAsc(Keyword keyword);

    List<TrendData> findByKeywordAndTrendDateBetweenOrderByTrendDateAsc(
            Keyword keyword,
            LocalDate startDate,
            LocalDate endDate
    );
}
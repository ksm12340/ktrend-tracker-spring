package com.ktrend.ktrendtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "trend_data")
public class TrendData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @Column(nullable = false)
    private LocalDate trendDate;

    @Column(nullable = false)
    private Integer interestValue;

    @Column(length = 20)
    private String periodType;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TrendData(Keyword keyword, LocalDate trendDate, Integer interestValue, String periodType) {
        this.keyword = keyword;
        this.trendDate = trendDate;
        this.interestValue = interestValue;
        this.periodType = periodType;
    }
}
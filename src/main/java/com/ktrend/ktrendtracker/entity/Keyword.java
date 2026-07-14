package com.ktrend.ktrendtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "keywords")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @Column(nullable = false, unique = true, length = 20)
    private String keywordName;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Keyword(String keywordName) {
        this.keywordName = keywordName;
    }

    public Keyword(String keywordName, String category) {
        this.keywordName = keywordName;
        this.category = category;
    }
}
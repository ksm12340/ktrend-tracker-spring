package com.ktrend.ktrendtracker.repository;

import com.ktrend.ktrendtracker.entity.SearchHistory;
import com.ktrend.ktrendtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistory> findByUserOrderBySearchedAtDesc(User user);
}
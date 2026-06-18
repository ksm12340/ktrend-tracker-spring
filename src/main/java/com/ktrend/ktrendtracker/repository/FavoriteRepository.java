package com.ktrend.ktrendtracker.repository;

import com.ktrend.ktrendtracker.entity.Favorite;
import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserOrderByCreatedAtDesc(User user);

    boolean existsByUserAndKeyword(User user, Keyword keyword);

    void deleteByUserAndKeyword(User user, Keyword keyword);
}
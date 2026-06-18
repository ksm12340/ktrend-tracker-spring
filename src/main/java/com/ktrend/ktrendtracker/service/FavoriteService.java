package com.ktrend.ktrendtracker.service;

import com.ktrend.ktrendtracker.entity.Favorite;
import com.ktrend.ktrendtracker.entity.Keyword;
import com.ktrend.ktrendtracker.entity.User;
import com.ktrend.ktrendtracker.repository.FavoriteRepository;
import com.ktrend.ktrendtracker.repository.UserRepository;
import com.ktrend.ktrendtracker.sec.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TrendService trendService;

    public List<Favorite> getFavorites(CustomUserDetails userDetails) {
        User user = getLoginUser(userDetails);
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public void addFavorite(CustomUserDetails userDetails, String keywordName) {
        User user = getLoginUser(userDetails);
        Keyword keyword = trendService.findOrCreateKeyword(keywordName);

        if (!favoriteRepository.existsByUserAndKeyword(user, keyword)) {
            favoriteRepository.save(new Favorite(user, keyword));
        }
    }

    @Transactional
    public void deleteFavorite(CustomUserDetails userDetails, String keywordName) {
        User user = getLoginUser(userDetails);
        Keyword keyword = trendService.findOrCreateKeyword(keywordName);

        favoriteRepository.deleteByUserAndKeyword(user, keyword);
    }

    private User getLoginUser(CustomUserDetails userDetails) {
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
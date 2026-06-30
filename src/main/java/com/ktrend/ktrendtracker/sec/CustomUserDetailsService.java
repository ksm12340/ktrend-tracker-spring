package com.ktrend.ktrendtracker.sec;

import com.ktrend.ktrendtracker.entity.User;
import com.ktrend.ktrendtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        if (loginId == null || loginId.isBlank()) {
            throw new UsernameNotFoundException("아이디를 입력해주세요.");
        }

        if (!loginId.equals(loginId.trim())) {
            throw new UsernameNotFoundException("아이디 앞뒤에는 공백을 사용할 수 없습니다.");
        }

        if (loginId.contains(" ")) {
            throw new UsernameNotFoundException("아이디에는 공백을 사용할 수 없습니다.");
        }

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return new CustomUserDetails(user);
    }
}
package com.ktrend.ktrendtracker.service;

import com.ktrend.ktrendtracker.dto.UserJoinRequest;
import com.ktrend.ktrendtracker.entity.User;
import com.ktrend.ktrendtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(UserJoinRequest request) {
        String loginId = request.getLoginId();
        String email = request.getEmail().trim();
        String name = request.getName().trim();
        String password = request.getPassword();
        String passwordCheck = request.getPasswordCheck();

        if (loginId == null || loginId.isBlank()) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }

        if (!loginId.equals(loginId.trim())) {
            throw new IllegalArgumentException("아이디 앞뒤에는 공백을 사용할 수 없습니다.");
        }

        if (loginId.contains(" ")) {
            throw new IllegalArgumentException("아이디에는 공백을 사용할 수 없습니다.");
        }

        if (!loginId.matches("^[a-zA-Z0-9_]{4,20}$")) {
            throw new IllegalArgumentException("아이디는 영문, 숫자, 밑줄(_)만 사용하여 4~20자로 입력해주세요.");
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }

        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(
                loginId,
                encodedPassword,
                name,
                email
        );

        userRepository.save(user);
    }
}
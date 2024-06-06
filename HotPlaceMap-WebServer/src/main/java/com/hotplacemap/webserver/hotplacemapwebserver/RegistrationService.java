package com.hotplacemap.webserver.hotplacemapwebserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String name, String username, String password) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("이름을 기입해주세요!");
        }
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("아이디를 기입해주세요!");
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("비밀번호를 기입해주세요!");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다!");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsAdmin(false);

        userRepository.save(user);
    }
}

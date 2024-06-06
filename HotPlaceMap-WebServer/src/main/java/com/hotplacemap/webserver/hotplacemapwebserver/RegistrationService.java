package com.hotplacemap.webserver.hotplacemapwebserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

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

    public void updateUser(String username, String name, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (StringUtils.hasText(name)) {
                user.setName(name);
            }
            if (StringUtils.hasText(password)) {
                user.setPassword(passwordEncoder.encode(password));
            }
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다!");
        }
    }

    public void deleteUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
        } else {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다!");
        }
    }
}
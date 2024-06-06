package com.hotplacemap.webserver.hotplacemapwebserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            jwtUtil.invalidateToken(token);
            return ResponseEntity.ok("로그아웃 되었습니다!");
        }
        return ResponseEntity.badRequest().body("Invalid token.");
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String tokenHeader,
                                        @RequestBody RegistrationRequest registrationRequest) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            registrationService.updateUser(username, registrationRequest.getName(), registrationRequest.getPassword());
            return ResponseEntity.ok("정보가 변경되었습니다!");
        }
        return ResponseEntity.badRequest().body("Invalid token.");
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            registrationService.deleteUser(username);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다!");
        }
        return ResponseEntity.badRequest().body("Invalid token.");
    }
}

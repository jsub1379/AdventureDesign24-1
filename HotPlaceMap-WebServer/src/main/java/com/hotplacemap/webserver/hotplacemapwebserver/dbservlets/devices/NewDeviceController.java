package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.devices;

import com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
@RequestMapping("/devices")
public class NewDeviceController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/new")
    public ResponseEntity<?> addNewDevice(@RequestParam int fId, @RequestParam String authCode) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        if (authCode.length() > 12) {
            return ResponseEntity.badRequest().body("인증 코드가 너무 깁니다");
        }
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO device (f_id, auth_code) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fId);
                stmt.setString(2, authCode);
                stmt.executeUpdate();
                return ResponseEntity.ok("장치가 성공적으로 추가되었습니다.");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}
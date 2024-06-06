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
public class ModifyDeviceController {

    @Autowired
    private DataSource dataSource;

    @PutMapping("/modify")
    public ResponseEntity<?> modifyDevice(@RequestParam int deviceId, @RequestParam String newAuthCode) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        if (newAuthCode.length() > 12) {
            return ResponseEntity.badRequest().body("인증 코드가 너무 깁니다");
        }
        String sql = "UPDATE device SET auth_code = ? WHERE device_id = ?";
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newAuthCode);
                stmt.setInt(2, deviceId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    return ResponseEntity.ok("장치가 성공적으로 수정되었습니다.");
                } else {
                    return ResponseEntity.badRequest().body("해당 장치가 존재하지 않습니다");
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}
package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.facilities;

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
@RequestMapping("/facilities")
public class NewFacilityController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/new")
    public ResponseEntity<?> addNewFacility(@RequestParam int bId, @RequestParam(required = false) String fName, @RequestParam(required = false) String fAddress) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        if (fName != null && fName.length() > 30) {
            return ResponseEntity.badRequest().body("시설 이름이 너무 깁니다");
        }
        if (fAddress != null && fAddress.length() > 40) {
            return ResponseEntity.badRequest().body("주소가 너무 깁니다");
        }
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO facility (b_id, facility_name, facility_address) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, bId);
                stmt.setString(2, fName);
                stmt.setString(3, fAddress);
                stmt.executeUpdate();
                return ResponseEntity.ok("시설이 성공적으로 추가되었습니다.");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

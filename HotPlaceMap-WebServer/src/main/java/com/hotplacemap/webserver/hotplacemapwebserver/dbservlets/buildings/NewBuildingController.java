package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.buildings;

import com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
@RequestMapping("/buildings")
public class NewBuildingController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/new")
    public ResponseEntity<?> addNewBuilding(@RequestParam String bname, @RequestParam String address, @RequestParam double latitude, @RequestParam double longitude) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        if (bname == null || bname.isEmpty() || bname.length() > 30 || address == null || address.isEmpty() || address.length() > 80) {
            return ResponseEntity.badRequest().body("입력 형식이 올바르지 않습니다");
        }
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO building (building_name, building_address, latitude, longitude) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bname);
                stmt.setString(2, address);
                stmt.setDouble(3, latitude);
                stmt.setDouble(4, longitude);
                stmt.executeUpdate();
                return ResponseEntity.ok("건물이 성공적으로 추가되었습니다.");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

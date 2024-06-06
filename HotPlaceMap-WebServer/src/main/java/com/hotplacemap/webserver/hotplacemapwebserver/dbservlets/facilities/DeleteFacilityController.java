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
public class DeleteFacilityController {

    @Autowired
    private DataSource dataSource;

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFacility(@RequestParam int fid) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM facility WHERE facility_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fid);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    return ResponseEntity.ok("시설이 성공적으로 삭제되었습니다.");
                } else {
                    return ResponseEntity.badRequest().body("해당 시설이 존재하지 않습니다");
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

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
public class ModifyFacilityController {

    @Autowired
    private DataSource dataSource;

    @PutMapping("/modify")
    public ResponseEntity<?> modifyFacility(@RequestParam int fid, @RequestParam(required = false) String fName, @RequestParam(required = false) String fAddress) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        StringBuilder sql = new StringBuilder("UPDATE facility SET ");
        if (fName != null && !fName.isEmpty()) {
            if (fName.length() > 30) return ResponseEntity.badRequest().body("시설 이름이 너무 깁니다");
            sql.append("facility_name = ?, ");
        }
        if (fAddress != null && !fAddress.isEmpty()) {
            if (fAddress.length() > 40) return ResponseEntity.badRequest().body("주소가 너무 깁니다");
            sql.append("facility_address = ?, ");
        }
        sql.delete(sql.length() - 2, sql.length()); // Remove trailing comma and space
        sql.append(" WHERE facility_id = ?");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int index = 1;
                if (fName != null && !fName.isEmpty()) stmt.setString(index++, fName);
                if (fAddress != null && !fAddress.isEmpty()) stmt.setString(index++, fAddress);
                stmt.setInt(index, fid);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    return ResponseEntity.ok("시설이 성공적으로 수정되었습니다.");
                } else {
                    return ResponseEntity.badRequest().body("해당 시설이 존재하지 않습니다");
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

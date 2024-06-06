package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.buildings;

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
@RequestMapping("/buildings")
public class ModifyBuildingController {

    @Autowired
    private DataSource dataSource;

    @PutMapping("/modify")
    public ResponseEntity<?> modifyBuilding(@RequestParam int bid, @RequestParam(required = false) String bname, @RequestParam(required = false) String address, @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude) {
        if (!UserAuth.userIsAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용 가능한 기능입니다!");
        }
        StringBuilder sql = new StringBuilder("UPDATE building SET ");
        if (bname != null && !bname.isEmpty()) {
            if (bname.length() > 30) return ResponseEntity.badRequest().body("건 이름이 너무 깁니다");
            sql.append("building_name = ?, ");
        }
        if (address != null && !address.isEmpty()) {
            if (address.length() > 80) return ResponseEntity.badRequest().body("주소가 너무 깁니다");
            sql.append("building_address = ?, ");
        }
        if (latitude != null) {
            sql.append("latitude = ?, ");
        }
        if (longitude != null) {
            sql.append("longitude = ?, ");
        }
        sql.delete(sql.length() - 2, sql.length()); // Remove trailing comma and space
        sql.append(" WHERE building_id = ?");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int index = 1;
                if (bname != null && !bname.isEmpty()) stmt.setString(index++, bname);
                if (address != null && !address.isEmpty()) stmt.setString(index++, address);
                if (latitude != null) stmt.setDouble(index++, latitude);
                if (longitude != null) stmt.setDouble(index++, longitude);
                stmt.setInt(index, bid);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    return ResponseEntity.ok("건물이 성공적으로 수정되었습니다.");
                } else {
                    return ResponseEntity.badRequest().body("해당 건물이 존재하지 않습니다");
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

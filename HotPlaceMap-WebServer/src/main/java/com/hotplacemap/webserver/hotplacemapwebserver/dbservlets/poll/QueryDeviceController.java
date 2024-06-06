package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.devices;

import com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RestController
@RequestMapping("/devices")
public class QueryDeviceController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/query")
    public ResponseEntity<?> queryDevice(@RequestParam int fid) {
        if (!UserAuth.userIsValid()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("올바르지 않은 접근경로입니다");
        }

        String summarySql = "SELECT total_count, oldest_measurement_time FROM facility_measurement_summary WHERE facility_id=?";
        String regressionSql = "SELECT weight, bias FROM linear_regression_params WHERE fid=?";

        try (Connection conn = dataSource.getConnection()) {
            int totalCount;
            String measurementTime;
            double weight;
            double bias;

            try (PreparedStatement summaryStmt = conn.prepareStatement(summarySql)) {
                summaryStmt.setInt(1, fid);
                try (ResultSet rs = summaryStmt.executeQuery()) {
                    if (rs.next()) {
                        totalCount = rs.getInt("total_count");
                        measurementTime = rs.getString("oldest_measurement_time");
                    } else {
                        return ResponseEntity.badRequest().body("해당 시설이 존재하지 않습니다");
                    }
                }
            }

            try (PreparedStatement regressionStmt = conn.prepareStatement(regressionSql)) {
                regressionStmt.setInt(1, fid);
                try (ResultSet rs = regressionStmt.executeQuery()) {
                    if (rs.next()) {
                        weight = rs.getDouble("weight");
                        bias = rs.getDouble("bias");
                    } else {
                        return ResponseEntity.badRequest().body("회귀 파라미터를 찾을 수 없습니다");
                    }
                }
            }

            double y = weight * totalCount + bias;
            String result = "y=" + y + ", measurement_time=" + measurementTime;
            return ResponseEntity.ok(result);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

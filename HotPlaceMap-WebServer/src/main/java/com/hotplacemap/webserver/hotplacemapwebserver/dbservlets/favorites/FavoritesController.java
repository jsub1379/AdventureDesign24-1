package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets.favorites;

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
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/getFavs")
    public ResponseEntity<?> getFavorites() {
        String username = UserAuth.getUserName();
        String getUserSql = "SELECT user_id FROM user WHERE username=?";
        String getFavsSql = "SELECT f_id FROM favorites WHERE u_id=?";

        try (Connection conn = dataSource.getConnection()) {
            int userId;

            try (PreparedStatement userStmt = conn.prepareStatement(getUserSql)) {
                userStmt.setString(1, username);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자를 찾을 수 없습니다");
                    }
                }
            }

            try (PreparedStatement favStmt = conn.prepareStatement(getFavsSql)) {
                favStmt.setInt(1, userId);
                try (ResultSet rs = favStmt.executeQuery()) {
                    StringBuilder favs = new StringBuilder();
                    while (rs.next()) {
                        if (favs.length() > 0) {
                            favs.append(",");
                        }
                        favs.append(rs.getInt("f_id"));
                    }
                    return ResponseEntity.ok(favs.toString());
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
    @GetMapping("/isFav")
    public ResponseEntity<?> isFavorite(@RequestParam int fid) {
        String username = UserAuth.getUserName();
        String getUserSql = "SELECT user_id FROM user WHERE username=?";
        String isFavSql = "SELECT 1 FROM favorites WHERE u_id=? AND f_id=?";

        try (Connection conn = dataSource.getConnection()) {
            int userId;

            try (PreparedStatement userStmt = conn.prepareStatement(getUserSql)) {
                userStmt.setString(1, username);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자를 찾을 수 없습니다");
                    }
                }
            }

            try (PreparedStatement favStmt = conn.prepareStatement(isFavSql)) {
                favStmt.setInt(1, userId);
                favStmt.setInt(2, fid);
                try (ResultSet rs = favStmt.executeQuery()) {
                    if (rs.next()) {
                        return ResponseEntity.ok(true);
                    } else {
                        return ResponseEntity.ok(false);
                    }
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
    @PostMapping("/toggleFav")
    public ResponseEntity<?> toggleFavorite(@RequestParam int fid) {
        String username = UserAuth.getUserName();
        String getUserSql = "SELECT user_id FROM user WHERE username=?";
        String isFavSql = "SELECT 1 FROM favorites WHERE u_id=? AND f_id=?";
        String addFavSql = "INSERT INTO favorites (u_id, f_id) VALUES (?, ?)";
        String removeFavSql = "DELETE FROM favorites WHERE u_id=? AND f_id=?";

        try (Connection conn = dataSource.getConnection()) {
            int userId;

            try (PreparedStatement userStmt = conn.prepareStatement(getUserSql)) {
                userStmt.setString(1, username);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자를 찾을 수 없습니다");
                    }
                }
            }

            try (PreparedStatement favStmt = conn.prepareStatement(isFavSql)) {
                favStmt.setInt(1, userId);
                favStmt.setInt(2, fid);
                try (ResultSet rs = favStmt.executeQuery()) {
                    if (rs.next()) {
                        try (PreparedStatement removeStmt = conn.prepareStatement(removeFavSql)) {
                            removeStmt.setInt(1, userId);
                            removeStmt.setInt(2, fid);
                            removeStmt.executeUpdate();
                            return ResponseEntity.ok("즐겨찾기에서 제거되었습니다");
                        }
                    } else {
                        try (PreparedStatement addStmt = conn.prepareStatement(addFavSql)) {
                            addStmt.setInt(1, userId);
                            addStmt.setInt(2, fid);
                            addStmt.executeUpdate();
                            return ResponseEntity.ok("즐겨찾기에 추가되었습니다");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류: " + e.getMessage());
        }
    }
}

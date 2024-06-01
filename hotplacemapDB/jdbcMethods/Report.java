package adventuredesign11;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Report {

    public boolean report(String photoUrl, String reportContent, int estimatedActualPersonnel, String username, int facilityId) {
        String getUserIdQuery = "SELECT user_id FROM user WHERE username = ?";
        String insertReportQuery = "INSERT INTO report (photo_url, report_content, estimated_actual_personnel, u_id, f_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement getUserIdStatement = connection.prepareStatement(getUserIdQuery);
             PreparedStatement insertReportStatement = connection.prepareStatement(insertReportQuery)) {

            getUserIdStatement.setString(1, username);
            ResultSet userResultSet = getUserIdStatement.executeQuery();
            if (!userResultSet.next()) {
                return false;
            }
            int userId = userResultSet.getInt("user_id");

            insertReportStatement.setString(1, photoUrl);
            insertReportStatement.setString(2, reportContent);
            insertReportStatement.setInt(3, estimatedActualPersonnel);
            insertReportStatement.setInt(4, userId);
            insertReportStatement.setInt(5, facilityId);
            insertReportStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

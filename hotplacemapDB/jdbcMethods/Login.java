package adventuredesign11;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    // 회원가입 메서드
    public boolean register(String name, String username, String password) {
        String checkUserQuery = "SELECT * FROM user WHERE username = ?";
        String insertUserQuery = "INSERT INTO user (name, username, password) VALUES (?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkUserQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertUserQuery)) {

            // 중복되는 username 확인
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Username already exists.");
                return false;
            }

            // 사용자 정보 삽입
            insertStatement.setString(1, name);
            insertStatement.setString(2, username);
            insertStatement.setString(3, password);
            insertStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 로그인 메서드
    public boolean login(String username, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 회원탈퇴 메서드
    public boolean deleteUser(String username, String password) {
        String checkUserQuery = "SELECT * FROM user WHERE username = ? AND password = ?";
        String deleteUserQuery = "DELETE FROM user WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkUserQuery);
             PreparedStatement deleteStatement = connection.prepareStatement(deleteUserQuery)) {

            // 사용자가 존재하는지 확인
            checkStatement.setString(1, username);
            checkStatement.setString(2, password);
            ResultSet resultSet = checkStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("User not found or incorrect password.");
                return false;
            }

            // 사용자 삭제
            deleteStatement.setString(1, username);
            deleteStatement.setString(2, password);
            deleteStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

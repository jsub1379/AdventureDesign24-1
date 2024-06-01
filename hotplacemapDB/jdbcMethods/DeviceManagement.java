package adventuredesign11;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DeviceManagement {

    public boolean updateDevice(int deviceId, int facilityId, String mac) {
        String updateDeviceQuery = "UPDATE device SET f_id = ?, MAC = ? WHERE device_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateDeviceQuery)) {
             
            statement.setInt(1, facilityId);
            statement.setString(2, mac);
            statement.setInt(3, deviceId);

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDevice(int deviceId) {
        String deleteDeviceQuery = "DELETE FROM device WHERE device_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteDeviceQuery)) {
             
            statement.setInt(1, deviceId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

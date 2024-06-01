package adventuredesign11;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuildingFacility {

    public ArrayList<String> getBuildings() {
        String query = "SELECT building_name FROM building";
        ArrayList<String> buildings = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                buildings.add(resultSet.getString("building_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buildings;
    }

    public int getBuildingId(int choice) {
        String query = "SELECT building_id FROM building LIMIT 1 OFFSET ?";
        int buildingId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, choice - 1);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                buildingId = resultSet.getInt("building_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buildingId;
    }

    public ArrayList<String> getFacilities(int buildingId) {
        String query = "SELECT facility_name FROM facility WHERE b_id = ?";
        ArrayList<String> facilities = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, buildingId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                facilities.add(resultSet.getString("facility_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facilities;
    }

    public int getFacilityId(int buildingId, int choice) {
        String query = "SELECT facility_id FROM facility WHERE b_id = ? LIMIT 1 OFFSET ?";
        int facilityId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, buildingId);
            statement.setInt(2, choice - 1);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                facilityId = resultSet.getInt("facility_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return facilityId;
    }
}

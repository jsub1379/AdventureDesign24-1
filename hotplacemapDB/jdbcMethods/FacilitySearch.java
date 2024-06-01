package adventuredesign11;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FacilitySearch {

    public ArrayList<String> searchFacilities(String keyword) {
        ArrayList<String> facilities = new ArrayList<>();

        String queryByFacilityName = "SELECT facility_name FROM facility WHERE facility_name LIKE ?";
        String queryByBuildingName = "SELECT facility.facility_name FROM facility " +
                                     "JOIN building ON facility.b_id = building.building_id " +
                                     "WHERE building.building_name LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statementByFacilityName = connection.prepareStatement(queryByFacilityName);
             PreparedStatement statementByBuildingName = connection.prepareStatement(queryByBuildingName)) {

            statementByFacilityName.setString(1, "%" + keyword + "%");
            ResultSet resultSetFacilityName = statementByFacilityName.executeQuery();
            while (resultSetFacilityName.next()) {
                facilities.add(resultSetFacilityName.getString("facility_name"));
            }

            statementByBuildingName.setString(1, "%" + keyword + "%");
            ResultSet resultSetBuildingName = statementByBuildingName.executeQuery();
            while (resultSetBuildingName.next()) {
                facilities.add(resultSetBuildingName.getString("facility_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return facilities;
    }

    public int getFacilityIdByName(String facilityName) {
        String query = "SELECT facility_id FROM facility WHERE facility_name = ?";
        int facilityId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, facilityName);
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

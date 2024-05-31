package adventuredesign11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class DatabaseConnection {
        private static final String DB_URL = "jdbc:mysql://hotplacemap-db.cwmg6nnupeuw.ap-northeast-2.rds.amazonaws.com:3306/HotPlaceMapDB";
        private static final String DB_USER = "Admin2Kuzo";
        private static final String DB_PASSWORD = "ocqAJS4AECkMvgy";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }
package egibo.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {
    try (Connection connection =
             DatabaseConnection.getConnection()) {

        if (connection.isValid(2)) {
            System.out.println(
                "Connected to eGIBO database successfully!"
            );
        }

    } catch (SQLException ex) {
        System.out.println(
            "Database connection failed: "
            + ex.getMessage()
        );
      }
    }
}


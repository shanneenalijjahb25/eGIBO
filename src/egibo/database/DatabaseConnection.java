
package egibo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/egibo_db";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD =
            "Database@123";

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                DB_USER,
                DB_PASSWORD
        );
    }
}


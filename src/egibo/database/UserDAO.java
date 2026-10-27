package egibo.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean register(
            String username,
            String password,
            double dailyCapacity) throws Exception {

        String sql = "INSERT INTO users "
                + "(username, password_hash, daily_capacity) "
                + "VALUES (?, ?, ?)";

        String hash = PasswordHasher.hashPassword(password);

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setDouble(3, dailyCapacity);

            return ps.executeUpdate() == 1;
        }
    }

    public double loginAndGetCapacity(
            String username,
            String password) throws Exception {

        String sql = "SELECT password_hash, daily_capacity "
                + "FROM users WHERE username = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }

                String hash = rs.getString("password_hash");

                if (PasswordHasher.verifyPassword(password, hash)) {
                    return rs.getDouble("daily_capacity");
                }

                return -1;
            }
        }
    }
    
    public int getUserId(String username) throws Exception {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }

                return -1;
            }
        }
    }
}


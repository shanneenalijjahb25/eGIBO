
package egibo.database;

import egibo.model.FixedTask;
import egibo.model.FlexibleTask;
import egibo.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;

public class TaskDAO {

    public boolean saveTask(String username, Task task)
            throws SQLException {

        String sql = "INSERT INTO tasks "
                + "(username, title, description, due_date, importance, "
                + "task_type, estimated_hours, start_time, end_time, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setDate(4, Date.valueOf(task.getDueDate()));
            ps.setInt(5, task.getImportance());
            ps.setString(6, task.getTypeLabel());
            ps.setDouble(7, task.getRemainingHours());

            if (task instanceof FixedTask) {
                FixedTask fixed = (FixedTask) task;

                ps.setTime(8, Time.valueOf(fixed.getStartTime()));
                ps.setTime(9, Time.valueOf(fixed.getEndTime()));
            } else {
                ps.setNull(8, java.sql.Types.TIME);
                ps.setNull(9, java.sql.Types.TIME);
            }

            ps.setString(10, task.getStatus().name());

            return ps.executeUpdate() == 1;
        }
    }
}
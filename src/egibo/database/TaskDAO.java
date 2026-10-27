package egibo.database;

import egibo.model.Task;
import egibo.model.FixedTask;
import egibo.model.FlexibleTask;
import egibo.model.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public boolean saveTask(int userId, Task task) throws SQLException {
        String sql = "INSERT INTO tasks "
                + "(user_id, title, description, task_type, hours, "
                + "importance, due_date, status, start_time, end_time, "
                + "work_start_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task instanceof FixedTask ? "FIXED" : "FLEXIBLE");
            ps.setDouble(5, task.getHours());
            ps.setInt(6, task.getImportance());
            ps.setDate(7, Date.valueOf(task.getDueDate()));
            ps.setString(8, task.getStatus().name());

            if (task instanceof FixedTask) {
                FixedTask fixed = (FixedTask) task;
                ps.setTime(9, Time.valueOf(fixed.getStartTime()));
                ps.setTime(10, Time.valueOf(fixed.getEndTime()));
                ps.setNull(11, java.sql.Types.TIME);
            } else {
                ps.setNull(9, java.sql.Types.TIME);
                ps.setNull(10, java.sql.Types.TIME);

                FlexibleTask flexible = (FlexibleTask) task;
                if (flexible.getWorkStartTime() != null) {
                    ps.setTime(11, Time.valueOf(flexible.getWorkStartTime()));
                } else {
                    ps.setNull(11, java.sql.Types.TIME);
                }
            }

            if (ps.executeUpdate() == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        task.setTaskId(keys.getInt(1));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<Task> loadTasks(int userId) throws SQLException {
        List<Task> loadedTasks = new ArrayList<>();

        String sql = "SELECT * FROM tasks WHERE user_id = ? "
                + "ORDER BY due_date, importance DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("task_type");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    double hours = rs.getDouble("hours");
                    int importance = rs.getInt("importance");

                    LocalDate dueDate = rs.getDate("due_date").toLocalDate();

                    Task task;

                    if ("FIXED".equals(type)) {
                        LocalTime start = rs.getTime("start_time").toLocalTime();
                        LocalTime end = rs.getTime("end_time").toLocalTime();

                        task = new FixedTask(
                                start, end, title, description,
                                hours, importance, dueDate
                        );
                    } else {
                        Time workTime = rs.getTime("work_start_time");
                        LocalTime workStart = workTime == null
                                ? null : workTime.toLocalTime();

                        task = new FlexibleTask(
                                title, description, hours, importance,
                                dueDate, workStart
                        );
                    }

                    task.setTaskId(rs.getInt("task_id"));
                    task.setStatus(Status.valueOf(rs.getString("status")));

                    loadedTasks.add(task);
                }
            }
        }

        return loadedTasks;
    }

    public boolean updateTaskStatus(
            int userId, Task task) throws SQLException {

        String sql = "UPDATE tasks SET status = ? "
                + "WHERE task_id = ? AND user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, task.getStatus().name());
            ps.setInt(2, task.getTaskId());
            ps.setInt(3, userId);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteTask(
            int userId, Task task) throws SQLException {

        String sql = "DELETE FROM tasks "
                + "WHERE task_id = ? AND user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, task.getTaskId());
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        }
    }
}
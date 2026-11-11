package egibo.model;

import egibo.database.TaskDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TaskManager {

    private static final Comparator<Task> RANK = new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            int byStatus = a.getStatus().compareTo(b.getStatus());
            if (byStatus != 0) {
                return byStatus;
            }

            int byImportance = Integer.compare(
                    b.getImportance(), a.getImportance());

            if (byImportance != 0) {
                return byImportance;
            }

            return a.getDueDate().compareTo(b.getDueDate());
        }
    };

    private final List<Task> tasks;
    private final TaskDAO taskDAO;
    private final int userId;

    // Existing constructor: keeps in-memory behavior.
    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.taskDAO = new TaskDAO();
        this.userId = -1;
    }

    // Database constructor: loads tasks belonging to this user.
    public TaskManager(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        this.tasks = new ArrayList<>();
        this.taskDAO = new TaskDAO();
        this.userId = userId;

        this.tasks.addAll(taskDAO.loadTasks(userId));
    }

    public void addFlexibleTask(
            String title, String description, double hours,
            int importance, LocalDate dueDate,
            LocalTime workStartTime) {

        FlexibleTask task = new FlexibleTask(
                title, description, hours, importance,
                dueDate, workStartTime);

        saveNewTask(task);
        tasks.add(task);
    }

    public void addFixedTask(
            LocalTime startTime, LocalTime endTime,
            String title, String description, double hours,
            int importance, LocalDate dueDate) {

        FixedTask task = new FixedTask(
                startTime, endTime, title, description,
                0.0, importance, dueDate);

        saveNewTask(task);
        tasks.add(task);
    }

    private void saveNewTask(Task task) {
        if (userId > 0) {
            try {
                if (!taskDAO.saveTask(userId, task)) {
                    throw new SQLException("Task was not saved.");
                }
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Could not save task to the database.", e);
            }
        }
    }
    
        public void updateTask(Task originalTask, Task editedTask) {
        int index = tasks.indexOf(originalTask);

        if (index == -1) {
            throw new IllegalArgumentException("Task not found.");
        }

        // Preserve the original task's ID and status.
        editedTask.setTaskId(originalTask.getTaskId());
       editedTask.setStatus(originalTask.getStatus() == Status.OVERDUE
        ? Status.PENDING : originalTask.getStatus());

        if (userId > 0) {
            try {
                if (!taskDAO.updateTask(userId, editedTask)) {
                    throw new SQLException("Task was not updated.");
                }
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Could not update task in the database.", e);
            }
        }

        // Replace the old task in the in-memory list.
        tasks.set(index, editedTask);
    }

    public void removeTask(Task task) {
        if (!tasks.contains(task)) {
            return;
        }

        if (userId > 0 && task.getTaskId() > 0) {
            try {
                if (!taskDAO.deleteTask(userId, task)) {
                    throw new SQLException("Task was not deleted.");
                }
            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Could not delete task from the database.", e);
            }
        }

        tasks.remove(task);
    }

    public void markTaskDone(Task task) {
        if (!tasks.contains(task)) {
            return;
        }

        Status previousStatus = task.getStatus();
        task.markDone();

        if (userId > 0 && task.getTaskId() > 0) {
            try {
                if (!taskDAO.updateTaskStatus(userId, task)) {
                    task.setStatus(previousStatus);
                    throw new SQLException("Task status was not updated.");
                }
            } catch (SQLException e) {
                task.setStatus(previousStatus);
                throw new IllegalStateException(
                        "Could not update task status in the database.", e);
            }
        }
    }
public void markTaskPending(Task task) {
    if (!tasks.contains(task)) {
        return;
    }

    Status previousStatus = task.getStatus();
    task.setStatus(Status.PENDING);

    if (userId > 0 && task.getTaskId() > 0) {
        try {
            if (!taskDAO.updateTaskStatus(userId, task)) {
                task.setStatus(previousStatus);
                throw new SQLException("Task status was not updated.");
            }
        } catch (SQLException e) {
            task.setStatus(previousStatus);
            throw new IllegalStateException(
                    "Could not update task status in the database.", e);
        }
    }
}
    // Clears the in-memory list only.
    public void clear() {
        tasks.clear();
    }

    public List<Task> getAllTasks() {
        List<Task> sortedList = new ArrayList<>(tasks);
    java.time.LocalDate today = java.time.LocalDate.now();

    // 1. Automatically update status to OVERDUE for past pending tasks
    for (Task t : sortedList) {
        if (t.getStatus() != Status.DONE && t.getDueDate() != null && t.getDueDate().isBefore(today)) {
            t.setStatus(Status.OVERDUE);
        }
    }

    // 2. Sort: OVERDUE tasks first, then by due date
    sortedList.sort((t1, t2) -> {
        boolean t1Overdue = (t1.getStatus() == Status.OVERDUE);
        boolean t2Overdue = (t2.getStatus() == Status.OVERDUE);

        if (t1Overdue && !t2Overdue) return -1; // Move t1 up
        if (!t1Overdue && t2Overdue) return 1;  // Move t2 up

        // Secondary sort by due date (earlier dates first)
        if (t1.getDueDate() != null && t2.getDueDate() != null) {
            return t1.getDueDate().compareTo(t2.getDueDate());
        }
        return 0;
    });

    return sortedList;
    }
    
    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> dailyList = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getDueDate() != null && t.getDueDate().equals(date)) {
                dailyList.add(t);
            }
        }
        return dailyList;
    }
    
    public List<Task> getTasksForToday() {
        return getTasksForDate(LocalDate.now());
    }

    public List<Task> getRankedTasksForToday() {
        List<Task> sorted = getTasksForToday();
        Collections.sort(sorted, RANK);
        return sorted;
    }
    
    public double getTotalRemainingHoursForToday() {
        double total = 0.0;
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task.getDueDate() != null && task.getDueDate().equals(today)) {
                total += task.getRemainingHours();
            }
        }
        return total;
    }
    
    public int getPendingCountForToday() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task.getDueDate() != null && task.getDueDate().equals(today) && task.getStatus() == Status.PENDING) {
                count++;
            }
        }
        return count;
    }
    
    public int getCompletedCountForToday() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task.getDueDate() != null && task.getDueDate().equals(today) && task.getStatus() == Status.DONE) {
                count++;
            }
        }
        return count;
    }
    
    public boolean willExceedCapacity(java.time.LocalDate date, double newTaskHours, double dailyCapacity) {
        double currentBookedHours = 0.0;

        // Sum up hours for tasks already scheduled on that date
        for (egibo.model.Task t : getAllTasks()) {
            if (t.getDueDate() != null && t.getDueDate().equals(date)) {
                currentBookedHours += t.getHours();
            }
        }

        // Check if adding the new task breaches the daily capacity limit
        return (currentBookedHours + newTaskHours) > dailyCapacity;
    }
    
}


package Repositories;

import Handlers.DBService.DatabaseConnection;
import Model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TaskRepository {

    private final String getAllTasksQuery = "SELECT t.id, t.goal_id, t.title, t.status " +
            "FROM tasks t " +
            "INNER JOIN goals g ON t.goal_id = g.id " +
            "WHERE g.user_id = ? " +
            "ORDER BY " +
            "CASE " +
            "WHEN t.status = 'pending' THEN 1 " +
            "WHEN t.status = 'in-progress' THEN 2 " +
            "WHEN t.status = 'completed' THEN 3 " +
            "ELSE 4 " +
            "END";

    private final String insertTaskQuery = "INSERT INTO tasks (goal_id, title) VALUES (?, ?)";
    private final String getLastInsertIdQuery = "SELECT LAST_INSERT_ID()";
    private final String getTaskDetailquery = "SELECT * FROM tasks WHERE id = ?";
    private final String updateTaskquery = "UPDATE tasks SET title = ?, status = ? WHERE id = ?";
    private final String deleteTaskquery = "DELETE FROM tasks WHERE id = ?";


    public List<Task> getAllTasks(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(getAllTasksQuery)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setGoalId(rs.getInt("goal_id"));
                    task.setTitle(rs.getString("title"));
                    task.setStatus(rs.getString("status"));
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public List<Task> createTasks(int goalId, List<Task> tasks) throws SQLException {
        List<Task> createdTasks = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertTaskQuery);
                PreparedStatement getIdStmt = conn.prepareStatement(getLastInsertIdQuery)) {

            for (Task task : tasks) {
                insertStmt.setInt(1, goalId);
                insertStmt.setString(2, task.getTitle());
                insertStmt.executeUpdate();

                try (ResultSet rs = getIdStmt.executeQuery()) {
                    if (rs.next()) {
                        int taskId = rs.getInt(1);
                        Task createdTask = Task.getTaskDetail(taskId);
                        createdTasks.add(createdTask);
                    }
                }
            }
        }
        return createdTasks;
    }

    public Task getTaskDetail(int taskId) throws SQLException {
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(getTaskDetailquery)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setGoalId(rs.getInt("goal_id"));
                    task.setTitle(rs.getString("title"));
                    task.setStatus(rs.getString("status"));
                    return task;
                }
            }
        }
        return null;
    }

    public boolean updateTask(Task task) throws SQLException {
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(updateTaskquery)) {
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getStatus());
            stmt.setInt(3, task.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean deleteTask(int taskId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(deleteTaskquery)) {
            stmt.setInt(1, taskId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean taskExists(int taskId) throws SQLException {
        String query = "SELECT COUNT(*) FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public Map<String, Integer> getTaskCounts(int userId) throws SQLException {
        String query = "SELECT t.status, COUNT(*) as count FROM tasks t " +
                "JOIN goals g ON t.goal_id = g.id " +
                "WHERE g.user_id = ? GROUP BY t.status";
        Map<String, Integer> counts = new HashMap<>();
        counts.put("completed", 0);
        counts.put("pending", 0);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    counts.put(status, count);
                }
            }
        }
        return counts;
    }

    public boolean toggleTaskStatus(int taskId, int userId) throws SQLException {
        String query = "UPDATE tasks SET status = CASE WHEN status = 'completed' THEN 'pending' ELSE 'completed' END WHERE id = ? AND goal_id IN (SELECT id FROM goals WHERE user_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public String getTaskStatus(int taskId, int userId) throws SQLException {
        String query = "SELECT t.status FROM tasks t JOIN goals g ON t.goal_id = g.id WHERE t.id = ? AND g.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

}

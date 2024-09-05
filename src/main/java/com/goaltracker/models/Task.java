package com.goaltracker.models;

import java.sql.*;

import com.goaltracker.services.DBService.DatabaseConnection;

public class Task {

    private int id;
    private int goalId;
    private String title;
    private String status;

    public Task() {
    }

    public Task(int id, int goalId, String title, String status) {
        this.id = id;
        this.goalId = goalId;
        this.title = title;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Task getTaskDetail(int taskId) throws SQLException {
        Task task = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tasks WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, taskId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setGoalId(rs.getInt("goal_id"));
                    task.setTitle(rs.getString("title"));
                    task.setStatus(rs.getString("status"));
                }
            }
        }

        return task;
    }

}

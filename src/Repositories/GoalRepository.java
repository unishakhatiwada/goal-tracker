package Repositories;

import Handlers.DBService.DatabaseConnection;
import Model.Goal;
import Model.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GoalRepository {

    private final String selectAllGoalsQuery = "SELECT g.id, g.title, g.purpose, g.deadline, g.status, g.user_id, t.id as taskId, t.title as taskTitle "
            +
            "FROM goals g LEFT JOIN tasks t ON g.id = t.goal_id WHERE g.user_id = ?";

    private final String insertGoalQuery = "INSERT INTO goals (title, purpose, deadline, user_id) VALUES (?, ?, ?, ?)";
    private final String insertTaskQuery = "INSERT INTO tasks (title, goal_id) VALUES (?, ?)";
    private final String selectGoalByIdQuery = "SELECT g.id, g.title, g.purpose, g.deadline, g.status, g.user_id, t.id as taskId, t.title as taskTitle "
            +
            "FROM goals g LEFT JOIN tasks t ON g.id = t.goal_id WHERE g.id = ?";
    private final String updateGoalQuery = "UPDATE goals SET title = ?, purpose = ?, deadline = ?, status = ? WHERE id = ?";
    private final String deleteGoalQuery = "DELETE FROM goals WHERE id = ?";
    private final String deleteTasksByGoalIdQuery = "DELETE FROM tasks WHERE goal_id = ?";

    public List<Goal> getAllGoals(int userId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(selectAllGoalsQuery)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Goal goal = findOrCreateGoal(goals, rs);
                    addTaskToGoal(rs, goal);
                }
            }
        }
        return goals;
    }

    public int createGoal(Goal goal, int userId) throws SQLException {
        int goalId;
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(insertGoalQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, goal.getTitle());
                stmt.setString(2, goal.getPurpose());
                stmt.setObject(3, goal.getDeadlineAsLocalDate()); // Use LocalDate
                stmt.setInt(4, userId);
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        goalId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve the goal ID.");
                    }
                }
            }

            if (goal.getTasks() != null && !goal.getTasks().isEmpty()) {
                try (PreparedStatement stmt = conn.prepareStatement(insertTaskQuery)) {
                    for (Task task : goal.getTasks()) {
                        stmt.setString(1, task.getTitle());
                        stmt.setInt(2, goalId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
        }
        return goalId;
    }

    public boolean updateGoal(Goal goal) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(updateGoalQuery)) {
            stmt.setString(1, goal.getTitle());
            stmt.setString(2, goal.getPurpose());
            stmt.setObject(3, goal.getDeadlineAsLocalDate());
            stmt.setString(4, goal.getStatus());
            stmt.setInt(5, goal.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Optional<Goal> getGoalById(int goalId) throws SQLException {
        Goal goal = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(selectGoalByIdQuery)) {
            stmt.setInt(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    goal = new Goal();
                    goal.setId(rs.getInt("id"));
                    goal.setUserId(rs.getInt("user_id"));
                    goal.setTitle(rs.getString("title"));
                    goal.setPurpose(rs.getString("purpose"));
                    goal.setDeadline(rs.getString("deadline"));
                    goal.setStatus(rs.getString("status"));

                    goal.setTasks(new ArrayList<>());

                    // Add tasks to goal if present
                    do {
                        addTaskToGoal(rs, goal);
                    } while (rs.next());
                }
            }
        }
        return Optional.ofNullable(goal);
    }

    public boolean deleteGoal(int goalId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete associated tasks
            try (PreparedStatement stmt = conn.prepareStatement(deleteTasksByGoalIdQuery)) {
                stmt.setInt(1, goalId);
                stmt.executeUpdate();
            }

            // Delete the goal
            try (PreparedStatement stmt = conn.prepareStatement(deleteGoalQuery)) {
                stmt.setInt(1, goalId);
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        }
    }

    private Goal findOrCreateGoal(List<Goal> goals, ResultSet rs) throws SQLException {
        int goalId = rs.getInt("id");
        for (Goal g : goals) {
            if (g.getId() == goalId) {
                return g;
            }
        }
        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setTitle(rs.getString("title"));
        goal.setPurpose(rs.getString("purpose"));
        goal.setDeadline(rs.getString("deadline"));
        goal.setStatus(rs.getString("status"));
        goal.setUserId(rs.getInt("user_id")); // Add this line to set the userId
        goal.setTasks(new ArrayList<>());
        goals.add(goal);
        return goal;
    }

    private void addTaskToGoal(ResultSet rs, Goal goal) throws SQLException {
        int taskId = rs.getInt("taskId");
        if (taskId > 0) { // Task exists
            Task task = new Task();
            task.setId(taskId);
            task.setTitle(rs.getString("taskTitle"));
            task.setGoalId(goal.getId()); // Set the goalId for the task
            goal.getTasks().add(task);
        }
    }
}

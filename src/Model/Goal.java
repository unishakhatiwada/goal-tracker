package Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Handlers.DBService.DatabaseConnection;
import java.sql.*;

public class Goal {

    private int id;
    private String title;
    private String purpose;
    private String deadline;
    private String status;
    private int userId;
    private List<Task> tasks;

    public Goal() {
    }

    public Goal(int id, String title, String purpose, String deadline, String status, int userId) {
        this.id = id;
        this.title = title;
        this.purpose = purpose;
        this.deadline = deadline;
        this.status = status;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public LocalDate getDeadlineAsLocalDate() {
        return LocalDate.parse(deadline, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static Goal getGoalDetail(int goalId) throws SQLException {
        Goal goal = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM goals WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, goalId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    goal = new Goal();
                    goal.setId(rs.getInt("id"));
                    goal.setTitle(rs.getString("title"));
                    goal.setPurpose(rs.getString("purpose"));
                    goal.setDeadline(rs.getString("deadline"));
                    goal.setStatus(rs.getString("status"));
                    goal.setUserId(rs.getInt("user_id"));
                }
            }
        }
        return goal;
    }
}

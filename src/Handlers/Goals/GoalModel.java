package Handlers.Goals;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GoalModel {
    private int id;
    private String title;
    private String purpose;
    private boolean completion;
    private String deadline; // Changed to String
    private String createdAt; // Changed to String
    private String updatedAt; // Changed to String

    // Default constructor required for Gson
    public GoalModel() {}

    // Constructor with all parameters
    public GoalModel(int id, String title, String purpose, boolean completion, String deadline, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.purpose = purpose;
        this.completion = completion;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public boolean isCompletion() {
        return completion;
    }

    public void setCompletion(boolean completion) {
        this.completion = completion;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods to convert strings to LocalDateTime
    public LocalDateTime getDeadlineAsDateTime() {
        return LocalDateTime.parse(deadline, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getCreatedAtAsDateTime() {
        return LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getUpdatedAtAsDateTime() {
        return LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME);
    }
}

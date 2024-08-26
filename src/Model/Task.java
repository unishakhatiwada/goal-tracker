package Model;

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

    // Getters and Setters
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
    
}

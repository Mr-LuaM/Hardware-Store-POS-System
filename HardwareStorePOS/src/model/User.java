package model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private int isDeleted; // Renamed from is_deleted to isDeleted for naming consistency

    // Default constructor
    public User() {
    }

    // Constructor for new users (without userId)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isDeleted = 0; // Default to active
    }

    // Constructor with all parameters, including userId
    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isDeleted = 0; // Default to active
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}

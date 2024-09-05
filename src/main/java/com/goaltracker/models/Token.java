package com.goaltracker.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Token {

    private int id;
    private int userId;
    private String token;

    public Token() {}

    public Token(int id, String token, int userId) {
        this.id = id;
        this.token = token;
        this.userId = userId;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String generateAndStoreToken(Connection conn, int userId) throws SQLException {
        this.token = UUID.randomUUID().toString();
        this.userId = userId;

        String query = "INSERT INTO tokens (user_id, token) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.userId);
            stmt.setString(2, this.token);
            stmt.executeUpdate();
        }
        return this.token;
    }
}
package com.example.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_control_db";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASS = ""; // Replace with your MySQL password

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
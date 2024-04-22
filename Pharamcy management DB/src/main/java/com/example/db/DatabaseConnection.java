package com.example.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/pharmacy1";
    static final String USER = "root";
    static final String PASSWORD = "123455";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}

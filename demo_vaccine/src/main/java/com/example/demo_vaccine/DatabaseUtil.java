package com.example.demo_vaccine;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/vaccine_management";
    private static final String USER = "root"; // replace with your database username
    private static final String PASSWORD = ""; // replace with your database password

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

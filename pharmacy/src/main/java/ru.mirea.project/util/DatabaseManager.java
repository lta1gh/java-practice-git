package ru.mirea.project.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL =
            System.getenv().getOrDefault("DB_URL",
                    "jdbc:mysql://localhost:3306/pharmacy?useUnicode=true&characterEncoding=UTF-8");
    private static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", "root");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
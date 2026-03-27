package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnection {
    private static JDBCConnection instance;

    private static final String URL = "jdbc:mysql://localhost:3306/meeting_room";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private JDBCConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("khong tim thay mysql driver", e);
        }
    }

    public static JDBCConnection getInstance() {
        if (instance == null) {
            instance = new JDBCConnection();
        }
        return instance;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("dong connection that bai: " + e.getMessage());
            }
        }
    }
}
package org.example.dao.impl;

import org.example.dao.interfaces.IUserdao;
import org.example.model.User;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Userdao implements IUserdao {
    private static Userdao instance;

    private Userdao() {
    }

    public static Userdao getInstance() {
        if (instance == null) {
            instance = new Userdao();
        }
        return instance;
    }

    @Override
    public boolean createUser(User user) {
        String sql = "insert into users(username, password_hash, full_name, email, phone, role, status) " +
                "values (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println("loi createUser: " + e.getMessage());
        }

        return false;
    }

    @Override
    public User findById(int userId) {
        String sql = "select * from users where user_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findById User: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "select * from users where username = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findByUsername: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "select * from users where email = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findByEmail: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "select * from users order by user_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (Exception e) {
            System.out.println("loi getAllUsers: " + e.getMessage());
        }

        return users;
    }

    @Override
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "select * from users where role = ? order by user_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("loi getUsersByRole: " + e.getMessage());
        }

        return users;
    }

    private User mapUser(ResultSet rs) throws Exception {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
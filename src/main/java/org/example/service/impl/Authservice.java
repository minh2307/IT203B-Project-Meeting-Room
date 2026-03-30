package org.example.service.impl;

import org.example.dao.impl.Userdao;
import org.example.dao.interfaces.IUserdao;
import org.example.model.User;
import org.example.service.interfaces.IAuthservice;
import org.example.util.JDBCConnection;
import org.example.util.PasswordHash;
import org.example.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Authservice implements IAuthservice {
    private static Authservice instance;
    private final IUserdao userdao;

    private Authservice() {
        this.userdao = Userdao.getInstance();
    }

    public static Authservice getInstance() {
        if (instance == null) {
            instance = new Authservice();
        }
        return instance;
    }

    @Override
    public boolean registerEmployee(String username, String fullName, String email, String phone, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("username khong duoc de trong");
            return false;
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("ho ten khong duoc de trong");
            return false;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("email khong hop le");
            return false;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println("so dien thoai khong hop le");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("mat khau khong duoc de trong");
            return false;
        }

        if (userdao.findByUsername(username.trim()) != null) {
            System.out.println("username da ton tai");
            return false;
        }

        if (userdao.findByEmail(email.trim()) != null) {
            System.out.println("email da ton tai");
            return false;
        }

        String hashedPassword = PasswordHash.hashPassword(password.trim());

        User user = new User(
                username.trim(),
                hashedPassword,
                fullName.trim(),
                email.trim(),
                phone.trim(),
                "employee",
                "active"
        );

        return userdao.createUser(user);
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("username khong duoc de trong");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("mat khau khong duoc de trong");
            return null;
        }

        User user = userdao.findByUsername(username.trim());
        if (user == null) {
            System.out.println("khong tim thay tai khoan");
            return null;
        }

        if (!"active".equalsIgnoreCase(user.getStatus())) {
            System.out.println("tai khoan dang bi khoa");
            return null;
        }

        boolean isValid = PasswordHash.checkPassword(password.trim(), user.getPasswordHash());
        if (!isValid) {
            System.out.println("sai mat khau");
            return null;
        }

        return user;
    }

    public User getUserProfile(int userId) {
        String sql = "select user_id, username, password_hash, full_name, email, phone, role, status, created_at, updated_at " +
                "from users where user_id = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return user;
                }
            }
        } catch (Exception e) {
            System.out.println("loi lay ho so: " + e.getMessage());
        }

        return null;
    }

    public boolean updateUserProfile(int userId, String fullName, String email, String phone) {
        if (userId <= 0) {
            System.out.println("user id khong hop le");
            return false;
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("ho ten khong duoc de trong");
            return false;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            System.out.println("email khong hop le");
            return false;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            System.out.println("so dien thoai khong hop le");
            return false;
        }

        String checkEmailSql = "select user_id from users where email = ? and user_id <> ?";
        String updateSql = "update users set full_name = ?, email = ?, phone = ?, updated_at = current_timestamp where user_id = ?";

        try (Connection conn = JDBCConnection.getConnection()) {
            try (PreparedStatement checkPs = conn.prepareStatement(checkEmailSql)) {
                checkPs.setString(1, email.trim());
                checkPs.setInt(2, userId);

                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("email da duoc su dung boi tai khoan khac");
                        return false;
                    }
                }
            }

            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, fullName.trim());
                updatePs.setString(2, email.trim());
                updatePs.setString(3, phone.trim());
                updatePs.setInt(4, userId);

                return updatePs.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("loi cap nhat ho so: " + e.getMessage());
            return false;
        }
    }
}
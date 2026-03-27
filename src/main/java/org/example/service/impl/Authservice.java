package org.example.service.impl;

import org.example.dao.impl.Userdao;
import org.example.dao.interfaces.IUserdao;
import org.example.model.User;
import org.example.service.interfaces.IAuthservice;
import org.example.util.PasswordHash;

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

        if (password == null || password.trim().isEmpty()) {
            System.out.println("mat khau khong duoc de trong");
            return false;
        }

        if (userdao.findByUsername(username.trim()) != null) {
            System.out.println("username da ton tai");
            return false;
        }

        if (email != null && !email.trim().isEmpty() && userdao.findByEmail(email.trim()) != null) {
            System.out.println("email da ton tai");
            return false;
        }

        String hashedPassword = PasswordHash.hashPassword(password.trim());

        User user = new User(
                username.trim(),
                hashedPassword,
                fullName.trim(),
                email == null ? null : email.trim(),
                phone == null ? null : phone.trim(),
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
}
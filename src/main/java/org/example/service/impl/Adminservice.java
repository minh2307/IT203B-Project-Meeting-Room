package org.example.service.impl;

import org.example.dao.impl.Userdao;
import org.example.dao.interfaces.IUserdao;
import org.example.model.User;
import org.example.service.interfaces.IAdminservice;
import org.example.util.PasswordHash;

public class Adminservice implements IAdminservice {
    private static Adminservice instance;
    private final IUserdao userdao;

    private Adminservice() {
        this.userdao = Userdao.getInstance();
    }

    public static Adminservice getInstance() {
        if (instance == null) {
            instance = new Adminservice();
        }
        return instance;
    }

    @Override
    public boolean createSupportStaff(String username, String fullName, String email, String phone, String password) {
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
                "support",
                "active"
        );

        return userdao.createUser(user);
    }
}
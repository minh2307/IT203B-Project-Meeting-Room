package org.example.service.impl;

import org.example.dao.impl.Userdao;
import org.example.dao.interfaces.IUserdao;
import org.example.model.User;
import org.example.service.interfaces.IAdminservice;
import org.example.util.PasswordHash;
import org.example.util.ValidationUtil;

import java.util.Collections;
import java.util.List;

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
                "support",
                "active"
        );

        return userdao.createUser(user);
    }

    public List<User> getSupportStaffs() {
        List<User> users = userdao.getUsersByRole("support");
        return users == null ? Collections.emptyList() : users;
    }
}
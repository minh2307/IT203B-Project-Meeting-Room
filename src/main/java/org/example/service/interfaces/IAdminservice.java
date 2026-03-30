package org.example.service.interfaces;

import org.example.model.User;

import java.util.List;

public interface IAdminservice {
    boolean createSupportStaff(String username, String fullName, String email, String phone, String password);

    List<User> getSupportStaffs();

}
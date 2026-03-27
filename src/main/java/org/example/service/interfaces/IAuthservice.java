package org.example.service.interfaces;

import org.example.model.User;

public interface IAuthservice {
    boolean registerEmployee(String username, String fullName, String email, String phone, String password);

    User login(String username, String password);
}
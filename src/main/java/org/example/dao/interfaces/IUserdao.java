package org.example.dao.interfaces;

import org.example.model.User;

import java.util.List;

public interface IUserdao {
    boolean createUser(User user);

    User findById(int userId);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> getAllUsers();

    List<User> getUsersByRole(String role);
}
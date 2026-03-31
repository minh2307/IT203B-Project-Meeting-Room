package org.example.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthserviceTest {

    private Authservice auth;

    @BeforeEach
    void setUp() {
        auth = Authservice.getInstance();
    }

    @Test
    void register_blankUsername() {
        assertFalse(auth.registerEmployee("   ", "A", "a@gmail.com", "0912", "123"));
    }

    @Test
    void register_invalidEmail() {
        assertFalse(auth.registerEmployee("user", "A", "abc", "0912", "123"));
    }

    @Test
    void login_blank() {
        assertNull(auth.login(" ", " "));
    }
}
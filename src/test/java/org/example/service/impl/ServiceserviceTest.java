package org.example.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceserviceTest {

    private Serviceservice service;

    @BeforeEach
    void setUp() {
        service = Serviceservice.getInstance();
    }

    @Test
    void addService_negativePrice() {
        assertFalse(service.addService("Nuoc", BigDecimal.valueOf(-1), "chai", "desc", "active"));
    }

    @Test
    void delete_invalidId() {
        assertFalse(service.deleteService(0));
    }
}
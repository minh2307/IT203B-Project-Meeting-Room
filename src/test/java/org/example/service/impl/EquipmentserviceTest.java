package org.example.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EquipmentserviceTest {

    private Equipmentservice service;

    @BeforeEach
    void setUp() {
        service = Equipmentservice.getInstance();
    }

    @Test
    void addEquipment_negativeQuantity() {
        assertFalse(service.addEquipment("May chieu", "mobile", -1, "desc", "active"));
    }

    @Test
    void updateQuantity_invalid() {
        assertFalse(service.updateQuantity(0, 5));
    }
}
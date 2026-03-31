package org.example.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoomserviceTest {

    private Roomservice service;

    @BeforeEach
    void setUp() {
        service = Roomservice.getInstance();
    }

    @Test
    void addRoom_invalidCapacity() {
        assertFalse(service.addRoom("A", 0, "T1", "desc", "available"));
    }

    @Test
    void addRoom_blankName() {
        assertFalse(service.addRoom("   ", 10, "T1", "desc", "available"));
    }

    @Test
    void find_invalidId() {
        assertNull(service.findById(0));
    }
}
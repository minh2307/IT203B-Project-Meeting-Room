package org.example.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class BookingserviceTest {

    private Bookingservice service;

    @BeforeEach
    void setUp() {
        service = Bookingservice.getInstance();
    }

    @Test
    void createBooking_invalidUser() {
        assertFalse(service.createBooking(
                0, 1, "hop", "desc", 5,
                future(1), future(2),
                "", Collections.emptyMap(), Collections.emptyMap()
        ));
    }

    @Test
    void createBooking_timeInvalid() {
        assertFalse(service.createBooking(
                1, 1, "hop", "desc", 5,
                future(3), future(2),
                "", Collections.emptyMap(), Collections.emptyMap()
        ));
    }

    @Test
    void getAvailableRooms_invalidInput() {
        assertTrue(service.getAvailableRooms(null, future(2), 5).isEmpty());
    }

    private Timestamp future(int h) {
        return Timestamp.from(
                LocalDateTime.now()
                        .plusHours(h)
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toInstant()
        );
    }
}
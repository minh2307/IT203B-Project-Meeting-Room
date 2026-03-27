package org.example.dao.interfaces;

import org.example.model.Booking;
import org.example.model.Room;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

public interface IBookingdao {
    List<Room> getAvailableRooms(Timestamp startTime, Timestamp endTime, int participantCount);

    boolean hasTimeConflict(Connection conn, int roomId, Timestamp startTime, Timestamp endTime);

    int addBooking(Connection conn, Booking booking);
}
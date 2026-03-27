package org.example.service.interfaces;

import org.example.model.Booking;
import org.example.model.Room;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface IBookingservice {
    Timestamp getCurrentVietnamTime();

    List<Room> getAvailableRooms(Timestamp startTime, Timestamp endTime, int participantCount);

    boolean createBooking(int userId,
                          int roomId,
                          String meetingTitle,
                          String meetingDescription,
                          int participantCount,
                          Timestamp startTime,
                          Timestamp endTime,
                          String note,
                          Map<Integer, Integer> equipmentRequests);

    List<Booking> getBookingsByUser(int userId);
}
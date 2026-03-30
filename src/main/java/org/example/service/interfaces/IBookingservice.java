package org.example.service.interfaces;

import org.example.model.Booking;
import org.example.model.Room;

import java.sql.Timestamp;
import java.time.LocalDate;
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
                          Map<Integer, Integer> equipmentRequests,
                          Map<Integer, Integer> serviceRequests);

    List<Booking> getBookingsByUser(int userId);

    boolean cancelPendingBooking(int userId, int bookingId);

    List<Booking> getPendingBookings();

    boolean approveBooking(int bookingId);

    boolean rejectBooking(int bookingId, String rejectReason);

    boolean assignSupportStaff(int bookingId, int supportStaffId);

    List<Booking> getAssignedBookingsBySupport(int supportStaffId, LocalDate workDate);

    boolean updatePreparationStatus(int bookingId, int supportStaffId, String preparationStatus);

    List<Booking> getAllBookings();
}
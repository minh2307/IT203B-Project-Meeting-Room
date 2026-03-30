package org.example.dao.interfaces;

import org.example.model.Booking;
import org.example.model.Room;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface IBookingdao {
    List<Room> getAvailableRooms(Timestamp startTime, Timestamp endTime, int participantCount);

    boolean hasTimeConflict(Connection conn, int roomId, Timestamp startTime, Timestamp endTime);

    int addBooking(Connection conn, Booking booking);

    Room findRoomById(Connection conn, int roomId);

    Booking findBookingById(int bookingId);

    List<Booking> getPendingBookings();

    boolean hasApprovedConflict(Connection conn, int bookingId, int roomId, Timestamp startTime, Timestamp endTime);

    boolean updateBookingStatus(Connection conn, int bookingId, String bookingStatus, String noteAppend);

    boolean assignSupportStaff(int bookingId, int supportStaffId);

    List<Booking> getAssignedBookingsBySupport(int supportStaffId, Date workDate);

    boolean updatePreparationStatus(int bookingId, int supportStaffId, String preparationStatus);

    List<Booking> getBookingsByUser(int userId);

    boolean cancelPendingBooking(int bookingId, int userId);

    List<Booking> getAllBookings();
}
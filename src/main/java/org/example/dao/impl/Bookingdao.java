package org.example.dao.impl;

import org.example.dao.interfaces.IBookingdao;
import org.example.model.Booking;
import org.example.model.Room;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Bookingdao implements IBookingdao {
    private static Bookingdao instance;

    private Bookingdao() {
    }

    public static Bookingdao getInstance() {
        if (instance == null) {
            instance = new Bookingdao();
        }
        return instance;
    }

    @Override
    public List<Room> getAvailableRooms(Timestamp startTime, Timestamp endTime, int participantCount) {
        List<Room> rooms = new ArrayList<>();

        String sql = "select * from rooms r " +
                "where r.status = 'available' " +
                "and r.capacity >= ? " +
                "and not exists ( " +
                "   select 1 from bookings b " +
                "   where b.room_id = r.room_id " +
                "   and b.booking_status in ('pending', 'approved') " +
                "   and b.start_time < ? " +
                "   and b.end_time > ? " +
                ") " +
                "order by r.room_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, participantCount);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRoom(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("loi getAvailableRooms: " + e.getMessage());
        }

        return rooms;
    }

    @Override
    public boolean hasTimeConflict(Connection conn, int roomId, Timestamp startTime, Timestamp endTime) {
        String sql = "select count(*) from bookings " +
                "where room_id = ? " +
                "and booking_status in ('pending', 'approved') " +
                "and start_time < ? " +
                "and end_time > ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setTimestamp(2, endTime);
            ps.setTimestamp(3, startTime);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("loi hasTimeConflict: " + e.getMessage());
        }

        return true;
    }

    @Override
    public int addBooking(Connection conn, Booking booking) {
        String sql = "insert into bookings(user_id, room_id, meeting_title, meeting_description, participant_count, start_time, end_time, booking_status, note) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getRoomId());
            ps.setString(3, booking.getMeetingTitle());
            ps.setString(4, booking.getMeetingDescription());
            ps.setInt(5, booking.getParticipantCount());
            ps.setTimestamp(6, booking.getStartTime());
            ps.setTimestamp(7, booking.getEndTime());
            ps.setString(8, booking.getBookingStatus());
            ps.setString(9, booking.getNote());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int bookingId = rs.getInt(1);
                        booking.setBookingId(bookingId);
                        return bookingId;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("loi addBooking: " + e.getMessage());
        }

        return -1;
    }

    public Room findRoomById(Connection conn, int roomId) {
        String sql = "select * from rooms where room_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("loi findRoomById: " + e.getMessage());
        }

        return null;
    }

    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings where user_id = ? order by booking_id desc";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("loi getBookingsByUser: " + e.getMessage());
        }

        return bookings;
    }

    private Booking mapBooking(ResultSet rs) throws Exception {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("user_id"),
                rs.getInt("room_id"),
                rs.getString("meeting_title"),
                rs.getString("meeting_description"),
                rs.getInt("participant_count"),
                rs.getTimestamp("start_time"),
                rs.getTimestamp("end_time"),
                rs.getString("booking_status"),
                rs.getString("note"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }

    private Room mapRoom(ResultSet rs) throws Exception {
        return new Room(
                rs.getInt("room_id"),
                rs.getString("room_name"),
                rs.getInt("capacity"),
                rs.getString("location"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
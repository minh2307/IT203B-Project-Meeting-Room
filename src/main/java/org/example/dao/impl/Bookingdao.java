package org.example.dao.impl;

import org.example.dao.interfaces.IBookingdao;
import org.example.model.Booking;
import org.example.model.Room;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.Date;
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

        try (Connection conn = JDBCConnection.getConnection();
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
        String sql = "insert into bookings(user_id, room_id, meeting_title, meeting_description, participant_count, start_time, end_time, booking_status, assigned_support_id, preparation_status, note) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getRoomId());
            ps.setString(3, booking.getMeetingTitle());
            ps.setString(4, booking.getMeetingDescription());
            ps.setInt(5, booking.getParticipantCount());
            ps.setTimestamp(6, booking.getStartTime());
            ps.setTimestamp(7, booking.getEndTime());
            ps.setString(8, booking.getBookingStatus());

            if (booking.getAssignedSupportId() == null) {
                ps.setNull(9, java.sql.Types.INTEGER);
            } else {
                ps.setInt(9, booking.getAssignedSupportId());
            }

            ps.setString(10, booking.getPreparationStatus());
            ps.setString(11, booking.getNote());

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

    @Override
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

    @Override
    public Booking findBookingById(int bookingId) {
        String sql = "select * from bookings where booking_id = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBooking(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("loi findBookingById: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Booking> getPendingBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings where booking_status = 'pending' order by start_time asc, booking_id asc";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (Exception e) {
            System.out.println("loi getPendingBookings: " + e.getMessage());
        }

        return bookings;
    }

    @Override
    public boolean hasApprovedConflict(Connection conn, int bookingId, int roomId, Timestamp startTime, Timestamp endTime) {
        String sql = "select count(*) from bookings " +
                "where booking_id <> ? " +
                "and room_id = ? " +
                "and booking_status = 'approved' " +
                "and start_time < ? " +
                "and end_time > ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, roomId);
            ps.setTimestamp(3, endTime);
            ps.setTimestamp(4, startTime);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("loi hasApprovedConflict: " + e.getMessage());
        }

        return true;
    }

    @Override
    public boolean updateBookingStatus(Connection conn, int bookingId, String bookingStatus, String noteAppend) {
        String sql = "update bookings " +
                "set booking_status = ?, " +
                "note = case " +
                "           when ? is null or trim(?) = '' then note " +
                "           when note is null or trim(note) = '' then ? " +
                "           else concat(note, ' | ', ?) " +
                "       end, " +
                "updated_at = current_timestamp " +
                "where booking_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingStatus);
            ps.setString(2, noteAppend);
            ps.setString(3, noteAppend);
            ps.setString(4, noteAppend);
            ps.setString(5, noteAppend);
            ps.setInt(6, bookingId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi updateBookingStatus: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean assignSupportStaff(int bookingId, int supportStaffId) {
        String sql = "update bookings set assigned_support_id = ?, " +
                "preparation_status = coalesce(preparation_status, 'preparing'), " +
                "updated_at = current_timestamp " +
                "where booking_id = ? and booking_status = 'approved'";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supportStaffId);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi assignSupportStaff: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Booking> getAssignedBookingsBySupport(int supportStaffId, Date workDate) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings " +
                "where assigned_support_id = ? " +
                "and booking_status = 'approved' " +
                "and date(start_time) >= ? " +
                "order by start_time asc, booking_id asc";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supportStaffId);
            ps.setDate(2, workDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("loi getAssignedBookingsBySupport: " + e.getMessage());
        }

        return bookings;
    }

    @Override
    public List<Booking> getAllAssignedBookingsBySupport(int supportStaffId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings " +
                "where assigned_support_id = ? " +
                "and booking_status = 'approved' " +
                "order by start_time asc, booking_id asc";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supportStaffId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapBooking(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("loi getAllAssignedBookingsBySupport: " + e.getMessage());
        }

        return bookings;
    }

    @Override
    public boolean updatePreparationStatus(int bookingId, int supportStaffId, String preparationStatus) {
        String sql = "update bookings set preparation_status = ?, updated_at = current_timestamp " +
                "where booking_id = ? and assigned_support_id = ? and booking_status = 'approved'";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, preparationStatus);
            ps.setInt(2, bookingId);
            ps.setInt(3, supportStaffId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi updatePreparationStatus: " + e.getMessage());
        }

        return false;
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings where user_id = ? order by start_time desc, booking_id desc";

        try (Connection conn = JDBCConnection.getConnection();
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

    @Override
    public boolean cancelPendingBooking(int bookingId, int userId) {
        String sql = "update bookings set booking_status = 'cancelled', updated_at = current_timestamp " +
                "where booking_id = ? and user_id = ? and booking_status = 'pending'";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi cancelPendingBooking: " + e.getMessage());
        }

        return false;
    }

    @Override
    public int countBookingsByStatus(String bookingStatus) {
        String sql = "select count(*) from bookings where booking_status = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bookingStatus);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("loi countBookingsByStatus: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public int countBookingsInMonth(int year, int month) {
        String sql = "select count(*) from bookings where year(start_time) = ? and month(start_time) = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("loi countBookingsInMonth: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public List<String> getTopUsedRooms(int limit) {
        List<String> results = new ArrayList<>();

        String sql = "select r.room_name, count(*) as total_bookings, coalesce(sum(b.participant_count), 0) as total_participants " +
                "from bookings b " +
                "join rooms r on b.room_id = r.room_id " +
                "where b.booking_status = 'approved' " +
                "group by r.room_id, r.room_name " +
                "order by total_bookings desc, r.room_name asc " +
                "limit ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(
                            "ten phong: " + rs.getString("room_name")
                                    + " | so booking duoc duyet: " + rs.getInt("total_bookings")
                                    + " | tong nguoi tham gia: " + rs.getInt("total_participants")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("loi getTopUsedRooms: " + e.getMessage());
        }

        return results;
    }

    private Booking mapBooking(ResultSet rs) throws Exception {
        Integer assignedSupportId = rs.getObject("assigned_support_id") == null
                ? null
                : rs.getInt("assigned_support_id");

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
                assignedSupportId,
                rs.getString("preparation_status"),
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

    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings order by start_time desc, booking_id desc";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (Exception e) {
            System.out.println("loi getAllBookings: " + e.getMessage());
        }

        return bookings;
    }
}
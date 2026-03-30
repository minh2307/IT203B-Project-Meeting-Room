package org.example.dao.impl;

import org.example.dao.interfaces.IRoomdao;
import org.example.model.Room;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Roomdao implements IRoomdao {
    private static Roomdao instance;

    private Roomdao() {
    }

    public static Roomdao getInstance() {
        if (instance == null) {
            instance = new Roomdao();
        }
        return instance;
    }

    @Override
    public boolean addRoom(Room room) {
        String sql = "insert into rooms(room_name, capacity, location, description, status) " +
                "values (?, ?, ?, ?, ?)";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setString(4, room.getDescription());
            ps.setString(5, room.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        room.setRoomId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println("loi addRoom: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateRoom(Room room) {
        String sql = "update rooms set room_name = ?, capacity = ?, location = ?, description = ?, status = ? " +
                "where room_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getRoomName());
            ps.setInt(2, room.getCapacity());
            ps.setString(3, room.getLocation());
            ps.setString(4, room.getDescription());
            ps.setString(5, room.getStatus());
            ps.setInt(6, room.getRoomId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("loi updateRoom: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteRoom(int roomId) {
        String sql = "delete from rooms where room_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("loi deleteRoom: " + e.getMessage());
        }

        return false;
    }

    @Override
    public Room findById(int roomId) {
        String sql = "select * from rooms where room_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findById Room: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "select * from rooms order by room_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }

        } catch (Exception e) {
            System.out.println("loi getAllRooms: " + e.getMessage());
        }

        return rooms;
    }

    public Room mapRoom(ResultSet rs) throws Exception {
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
    public Room findByName(String roomName) {
        String sql = "select * from rooms where lower(trim(room_name)) = lower(trim(?)) limit 1";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findByName Room: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Room> searchByName(String keyword) {
        List<Room> rooms = new ArrayList<>();
        String sql = "select * from rooms where lower(room_name) like lower(?) order by room_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRoom(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("loi searchByName: " + e.getMessage());
        }

        return rooms;
    }

    @Override
    public boolean hasRelatedBookings(int roomId) {
        String sql = "select count(*) from bookings where room_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("loi hasRelatedBookings: " + e.getMessage());
        }

        return false;
    }
}
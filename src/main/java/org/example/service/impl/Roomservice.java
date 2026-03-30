package org.example.service.impl;

import org.example.dao.impl.Roomdao;
import org.example.model.Room;
import org.example.service.interfaces.IRoomservice;

import java.util.List;

public class Roomservice implements IRoomservice {
    private static Roomservice instance;
    private final Roomdao roomdao;

    private Roomservice() {
        this.roomdao = Roomdao.getInstance();
    }

    public static Roomservice getInstance() {
        if (instance == null) {
            instance = new Roomservice();
        }
        return instance;
    }

    @Override
    public boolean addRoom(String roomName, int capacity, String location, String description, String status) {
        if (roomName == null || roomName.trim().isEmpty()) {
            System.out.println("ten phong khong duoc de trong");
            return false;
        }

        if (capacity <= 0) {
            System.out.println("suc chua phai lon hon 0");
            return false;
        }

        Room duplicateRoom = roomdao.findByName(roomName.trim());
        if (duplicateRoom != null) {
            System.out.println("ten phong da ton tai");
            return false;
        }

        if (location == null || location.trim().isEmpty()) {
            location = "chua cap nhat";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        status = normalizeStatus(status);
        if (!isValidStatus(status)) {
            System.out.println("trang thai phong khong hop le");
            return false;
        }

        Room room = new Room(
                roomName.trim(),
                capacity,
                location.trim(),
                description.trim(),
                status
        );

        return roomdao.addRoom(room);
    }

    @Override
    public boolean updateRoom(int roomId, String roomName, int capacity, String location, String description, String status) {
        if (roomId <= 0) {
            System.out.println("id phong khong hop le");
            return false;
        }

        Room existingRoom = roomdao.findById(roomId);
        if (existingRoom == null) {
            System.out.println("khong tim thay phong");
            return false;
        }

        if (roomName == null || roomName.trim().isEmpty()) {
            System.out.println("ten phong khong duoc de trong");
            return false;
        }

        if (capacity <= 0) {
            System.out.println("suc chua phai lon hon 0");
            return false;
        }

        Room duplicateRoom = roomdao.findByName(roomName.trim());
        if (duplicateRoom != null && duplicateRoom.getRoomId() != roomId) {
            System.out.println("ten phong da ton tai");
            return false;
        }

        if (location == null || location.trim().isEmpty()) {
            location = "chua cap nhat";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        status = normalizeStatus(status);
        if (!isValidStatus(status)) {
            System.out.println("trang thai phong khong hop le");
            return false;
        }

        Room room = new Room(
                roomId,
                roomName.trim(),
                capacity,
                location.trim(),
                description.trim(),
                status,
                existingRoom.getCreatedAt(),
                null
        );

        return roomdao.updateRoom(room);
    }

    @Override
    public boolean deleteRoom(int roomId) {
        if (roomId <= 0) {
            System.out.println("id phong khong hop le");
            return false;
        }

        Room existingRoom = roomdao.findById(roomId);
        if (existingRoom == null) {
            System.out.println("khong tim thay phong");
            return false;
        }

        if (roomdao.hasRelatedBookings(roomId)) {
            System.out.println("khong the xoa phong vi phong da co du lieu booking lien quan");
            return false;
        }

        return roomdao.deleteRoom(roomId);
    }

    @Override
    public Room findById(int roomId) {
        if (roomId <= 0) {
            System.out.println("id phong khong hop le");
            return null;
        }

        return roomdao.findById(roomId);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomdao.getAllRooms();
    }

    @Override
    public List<Room> searchRoomsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return roomdao.getAllRooms();
        }

        return roomdao.searchByName(keyword.trim());
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "available";
        }
        return status.trim().toLowerCase();
    }

    private boolean isValidStatus(String status) {
        return "available".equalsIgnoreCase(status)
                || "maintenance".equalsIgnoreCase(status)
                || "unavailable".equalsIgnoreCase(status);
    }
}
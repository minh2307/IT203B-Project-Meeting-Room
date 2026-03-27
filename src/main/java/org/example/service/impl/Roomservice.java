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

        if (location == null || location.trim().isEmpty()) {
            location = "chua cap nhat";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        if (status == null || status.trim().isEmpty()) {
            status = "available";
        }

        Room room = new Room(
                roomName.trim(),
                capacity,
                location.trim(),
                description.trim(),
                status.trim()
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

        if (location == null || location.trim().isEmpty()) {
            location = "chua cap nhat";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        if (status == null || status.trim().isEmpty()) {
            status = "available";
        }

        Room room = new Room(
                roomId,
                roomName.trim(),
                capacity,
                location.trim(),
                description.trim(),
                status.trim(),
                null,
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
}
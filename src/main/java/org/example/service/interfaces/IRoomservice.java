package org.example.service.interfaces;

import org.example.model.Room;

import java.util.List;

public interface IRoomservice {
    boolean addRoom(String roomName, int capacity, String location, String description, String status);

    boolean updateRoom(int roomId, String roomName, int capacity, String location, String description, String status);

    boolean deleteRoom(int roomId);

    Room findById(int roomId);

    List<Room> getAllRooms();

    List<Room> searchRoomsByName(String keyword);

}
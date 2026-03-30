package org.example.dao.interfaces;

import org.example.model.Room;

import java.util.List;

public interface IRoomdao {
    boolean addRoom(Room room);

    boolean updateRoom(Room room);

    boolean deleteRoom(int roomId);

    Room findById(int roomId);

    List<Room> getAllRooms();

    Room findByName(String roomName);

    List<Room> searchByName(String keyword);

    boolean hasRelatedBookings(int roomId);
}
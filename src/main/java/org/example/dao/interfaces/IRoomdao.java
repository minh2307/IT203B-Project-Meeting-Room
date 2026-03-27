package org.example.dao.interfaces;

import org.example.model.Room;

import java.util.List;

public interface IRoomdao {
    boolean addRoom(Room room);

    boolean updateRoom(Room room);

    boolean deleteRoom(int roomId);

    Room findById(int roomId);

    List<Room> getAllRooms();
}
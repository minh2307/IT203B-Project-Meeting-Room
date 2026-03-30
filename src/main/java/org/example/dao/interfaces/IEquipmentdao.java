package org.example.dao.interfaces;

import org.example.model.Equipment;

import java.util.List;

public interface IEquipmentdao {
    List<Equipment> getAllEquipments();
    Equipment findById(int equipmentId);
    Equipment findByName(String equipmentName);
    boolean addEquipment(Equipment equipment);
    boolean updateEquipment(Equipment equipment);
    boolean deleteEquipment(int equipmentId);
    boolean updateQuantity(int equipmentId, int quantity);
    boolean hasRelatedBookingDetails(int equipmentId);
}
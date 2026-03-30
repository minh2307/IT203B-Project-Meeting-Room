package org.example.service.interfaces;

import org.example.model.Equipment;

import java.util.List;

public interface IEquipmentservice {
    List<Equipment> getAllEquipments();
    Equipment findById(int equipmentId);
    boolean addEquipment(String equipmentName, String equipmentType, int quantity, String description, String status);
    boolean updateEquipment(int equipmentId, String equipmentName, String equipmentType, int quantity, String description, String status);
    boolean deleteEquipment(int equipmentId);
    boolean updateQuantity(int equipmentId, int quantity);
}
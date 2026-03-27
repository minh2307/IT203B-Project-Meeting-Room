package org.example.service.interfaces;

import org.example.model.Equipment;

import java.util.List;

public interface IEquipmentservice {
    List<Equipment> getAllEquipments();

    boolean updateQuantity(int equipmentId, int quantity);
}
package org.example.dao.interfaces;

import org.example.model.Equipment;

import java.util.List;

public interface IEquipmentdao {
    List<Equipment> getAllEquipments();

    Equipment findById(int equipmentId);

    boolean updateQuantity(int equipmentId, int quantity);
}
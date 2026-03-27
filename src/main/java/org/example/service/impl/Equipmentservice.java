package org.example.service.impl;

import org.example.dao.impl.Equipmentdao;
import org.example.dao.interfaces.IEquipmentdao;
import org.example.model.Equipment;
import org.example.service.interfaces.IEquipmentservice;

import java.util.List;

public class Equipmentservice implements IEquipmentservice {
    private static Equipmentservice instance;
    private final IEquipmentdao equipmentdao;

    private Equipmentservice() {
        this.equipmentdao = Equipmentdao.getInstance();
    }

    public static Equipmentservice getInstance() {
        if (instance == null) {
            instance = new Equipmentservice();
        }
        return instance;
    }

    @Override
    public List<Equipment> getAllEquipments() {
        return equipmentdao.getAllEquipments();
    }

    @Override
    public boolean updateQuantity(int equipmentId, int quantity) {
        if (equipmentId <= 0) {
            System.out.println("id thiet bi khong hop le");
            return false;
        }

        if (quantity < 0) {
            System.out.println("so luong khong duoc am");
            return false;
        }

        Equipment equipment = equipmentdao.findById(equipmentId);
        if (equipment == null) {
            System.out.println("khong tim thay thiet bi");
            return false;
        }

        return equipmentdao.updateQuantity(equipmentId, quantity);
    }
}
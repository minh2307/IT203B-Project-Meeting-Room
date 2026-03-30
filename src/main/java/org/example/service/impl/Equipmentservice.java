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
    public Equipment findById(int equipmentId) {
        if (equipmentId <= 0) {
            System.out.println("id thiet bi khong hop le");
            return null;
        }
        return equipmentdao.findById(equipmentId);
    }

    @Override
    public boolean addEquipment(String equipmentName, String equipmentType, int quantity, String description, String status) {
        if (equipmentName == null || equipmentName.trim().isEmpty()) {
            System.out.println("ten thiet bi khong duoc de trong");
            return false;
        }

        if (quantity < 0) {
            System.out.println("so luong khong duoc am");
            return false;
        }

        Equipment duplicate = equipmentdao.findByName(equipmentName.trim());
        if (duplicate != null) {
            System.out.println("ten thiet bi da ton tai");
            return false;
        }

        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            equipmentType = "mobile";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        status = normalizeStatus(status);
        if (!isValidStatus(status)) {
            System.out.println("trang thai thiet bi khong hop le");
            return false;
        }

        Equipment equipment = new Equipment(
                equipmentName.trim(),
                equipmentType.trim(),
                quantity,
                description.trim(),
                status
        );

        return equipmentdao.addEquipment(equipment);
    }

    @Override
    public boolean updateEquipment(int equipmentId, String equipmentName, String equipmentType, int quantity, String description, String status) {
        if (equipmentId <= 0) {
            System.out.println("id thiet bi khong hop le");
            return false;
        }

        Equipment oldEquipment = equipmentdao.findById(equipmentId);
        if (oldEquipment == null) {
            System.out.println("khong tim thay thiet bi");
            return false;
        }

        if (equipmentName == null || equipmentName.trim().isEmpty()) {
            System.out.println("ten thiet bi khong duoc de trong");
            return false;
        }

        if (quantity < 0) {
            System.out.println("so luong khong duoc am");
            return false;
        }

        Equipment duplicate = equipmentdao.findByName(equipmentName.trim());
        if (duplicate != null && duplicate.getEquipmentId() != equipmentId) {
            System.out.println("ten thiet bi da ton tai");
            return false;
        }

        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            equipmentType = oldEquipment.getEquipmentType();
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        status = normalizeStatus(status);
        if (!isValidStatus(status)) {
            System.out.println("trang thai thiet bi khong hop le");
            return false;
        }

        Equipment equipment = new Equipment(
                equipmentId,
                equipmentName.trim(),
                equipmentType.trim(),
                quantity,
                description.trim(),
                status,
                oldEquipment.getCreatedAt(),
                null
        );

        return equipmentdao.updateEquipment(equipment);
    }

    @Override
    public boolean deleteEquipment(int equipmentId) {
        if (equipmentId <= 0) {
            System.out.println("id thiet bi khong hop le");
            return false;
        }

        Equipment oldEquipment = equipmentdao.findById(equipmentId);
        if (oldEquipment == null) {
            System.out.println("khong tim thay thiet bi");
            return false;
        }

        if (equipmentdao.hasRelatedBookingDetails(equipmentId)) {
            System.out.println("khong the xoa thiet bi vi da co du lieu booking lien quan");
            return false;
        }

        return equipmentdao.deleteEquipment(equipmentId);
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

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "active";
        }
        return status.trim().toLowerCase();
    }

    private boolean isValidStatus(String status) {
        return "active".equalsIgnoreCase(status)
                || "maintenance".equalsIgnoreCase(status)
                || "inactive".equalsIgnoreCase(status)
                || "available".equalsIgnoreCase(status);
    }
}
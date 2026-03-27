package org.example.model;

import java.sql.Timestamp;

public class Equipment {
    private int equipmentId;
    private String equipmentName;
    private String equipmentType;
    private int quantity;
    private String description;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Equipment() {
    }

    public Equipment(int equipmentId, String equipmentName, String equipmentType,
                     int quantity, String description, String status,
                     Timestamp createdAt, Timestamp updatedAt) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Equipment(String equipmentName, String equipmentType, int quantity,
                     String description, String status) {
        this.equipmentName = equipmentName;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.description = description;
        this.status = status;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
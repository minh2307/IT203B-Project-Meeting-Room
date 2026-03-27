package org.example.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Bookingdetail {
    private int bookingDetailId;
    private int bookingId;
    private Integer equipmentId;
    private Integer serviceId;
    private int quantity;
    private BigDecimal unitPrice;
    private String detailType;
    private String note;
    private Timestamp createdAt;

    public Bookingdetail() {
    }

    public Bookingdetail(int bookingDetailId, int bookingId, Integer equipmentId, Integer serviceId,
                         int quantity, BigDecimal unitPrice, String detailType,
                         String note, Timestamp createdAt) {
        this.bookingDetailId = bookingDetailId;
        this.bookingId = bookingId;
        this.equipmentId = equipmentId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.detailType = detailType;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Bookingdetail(int bookingId, Integer equipmentId, Integer serviceId,
                         int quantity, BigDecimal unitPrice, String detailType, String note) {
        this.bookingId = bookingId;
        this.equipmentId = equipmentId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.detailType = detailType;
        this.note = note;
    }

    public int getBookingDetailId() {
        return bookingDetailId;
    }

    public void setBookingDetailId(int bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
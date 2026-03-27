package org.example.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Service {
    private int serviceId;
    private String serviceName;
    private BigDecimal unitPrice;
    private String unit;
    private String description;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Service() {
    }

    public Service(int serviceId, String serviceName, BigDecimal unitPrice, String unit,
                   String description, String status, Timestamp createdAt, Timestamp updatedAt) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Service(String serviceName, BigDecimal unitPrice, String unit, String description, String status) {
        this.serviceName = serviceName;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.description = description;
        this.status = status;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
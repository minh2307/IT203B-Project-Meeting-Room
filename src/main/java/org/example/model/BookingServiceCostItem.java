package org.example.model;

import java.math.BigDecimal;

public class BookingServiceCostItem {
    private int serviceId;
    private String serviceName;
    private String unit;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public BookingServiceCostItem() {
    }

    public BookingServiceCostItem(int serviceId, String serviceName, String unit,
                                  int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
package org.example.service.impl;

import org.example.dao.impl.Servicedao;
import org.example.dao.interfaces.IServicedao;
import org.example.model.Service;
import org.example.service.interfaces.IServiceservice;

import java.math.BigDecimal;
import java.util.List;

public class Serviceservice implements IServiceservice {
    private static Serviceservice instance;
    private final IServicedao servicedao;

    private Serviceservice() {
        this.servicedao = Servicedao.getInstance();
    }

    public static Serviceservice getInstance() {
        if (instance == null) {
            instance = new Serviceservice();
        }
        return instance;
    }

    @Override
    public List<Service> getAllServices() {
        return servicedao.getAllServices();
    }

    @Override
    public Service findById(int serviceId) {
        if (serviceId <= 0) {
            System.out.println("id dich vu khong hop le");
            return null;
        }
        return servicedao.findById(serviceId);
    }

    @Override
    public boolean addService(String serviceName, BigDecimal unitPrice, String unit, String description, String status) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            System.out.println("ten dich vu khong duoc de trong");
            return false;
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("don gia khong hop le");
            return false;
        }

        Service duplicate = servicedao.findByName(serviceName.trim());
        if (duplicate != null) {
            System.out.println("ten dich vu da ton tai");
            return false;
        }

        if (unit == null || unit.trim().isEmpty()) {
            unit = "lan";
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        status = normalizeStatus(status);
        if (!isValidStatus(status)) {
            System.out.println("trang thai dich vu khong hop le");
            return false;
        }

        Service service = new Service(
                serviceName.trim(),
                unitPrice,
                unit.trim(),
                description.trim(),
                status
        );

        return servicedao.addService(service);
    }

    @Override
    public boolean updateService(int serviceId, String serviceName, BigDecimal unitPrice, String unit, String description, String status) {
        if (serviceId <= 0) {
            System.out.println("id dich vu khong hop le");
            return false;
        }

        Service oldService = servicedao.findById(serviceId);
        if (oldService == null) {
            System.out.println("khong tim thay dich vu");
            return false;
        }

        if (serviceName == null || serviceName.trim().isEmpty()) {
            System.out.println("ten dich vu khong duoc de trong");
            return false;
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("don gia khong hop le");
            return false;
        }

        Service duplicate = servicedao.findByName(serviceName.trim());
        if (duplicate != null && duplicate.getServiceId() != serviceId) {
            System.out.println("ten dich vu da ton tai");
            return false;
        }

        if (unit == null || unit.trim().isEmpty()) {
            unit = oldService.getUnit();
        }

        if (description == null || description.trim().isEmpty()) {
            description = "";
        }

        status = normalizeStatus(status);
        if (!isValidStatus(status)) {
            System.out.println("trang thai dich vu khong hop le");
            return false;
        }

        Service service = new Service(
                serviceId,
                serviceName.trim(),
                unitPrice,
                unit.trim(),
                description.trim(),
                status,
                oldService.getCreatedAt(),
                null
        );

        return servicedao.updateService(service);
    }

    @Override
    public boolean deleteService(int serviceId) {
        if (serviceId <= 0) {
            System.out.println("id dich vu khong hop le");
            return false;
        }

        Service oldService = servicedao.findById(serviceId);
        if (oldService == null) {
            System.out.println("khong tim thay dich vu");
            return false;
        }

        if (servicedao.hasRelatedBookingDetails(serviceId)) {
            System.out.println("khong the xoa dich vu vi da co du lieu booking lien quan");
            return false;
        }

        return servicedao.deleteService(serviceId);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "active";
        }
        return status.trim().toLowerCase();
    }

    private boolean isValidStatus(String status) {
        return "active".equalsIgnoreCase(status)
                || "inactive".equalsIgnoreCase(status);
    }
}
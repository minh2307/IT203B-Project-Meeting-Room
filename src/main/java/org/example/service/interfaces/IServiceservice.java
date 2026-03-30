package org.example.service.interfaces;

import org.example.model.Service;
import java.math.BigDecimal;
import java.util.List;

public interface IServiceservice {
    List<Service> getAllServices();
    Service findById(int serviceId);
    boolean addService(String serviceName, BigDecimal unitPrice, String unit, String description, String status);
    boolean updateService(int serviceId, String serviceName, BigDecimal unitPrice, String unit, String description, String status);
    boolean deleteService(int serviceId);
}
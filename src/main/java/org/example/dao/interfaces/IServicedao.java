package org.example.dao.interfaces;

import org.example.model.Service;
import java.util.List;

public interface IServicedao {
    List<Service> getAllServices();
    Service findById(int serviceId);
    Service findByName(String serviceName);
    boolean addService(Service service);
    boolean updateService(Service service);
    boolean deleteService(int serviceId);
    boolean hasRelatedBookingDetails(int serviceId);
}
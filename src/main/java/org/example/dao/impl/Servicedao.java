package org.example.dao.impl;

import org.example.dao.interfaces.IServicedao;
import org.example.model.Service;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Servicedao implements IServicedao {
    private static Servicedao instance;

    private Servicedao() {
    }

    public static Servicedao getInstance() {
        if (instance == null) {
            instance = new Servicedao();
        }
        return instance;
    }

    @Override
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "select * from services order by service_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                services.add(mapService(rs));
            }
        } catch (Exception e) {
            System.out.println("loi getAllServices: " + e.getMessage());
        }

        return services;
    }

    @Override
    public Service findById(int serviceId) {
        String sql = "select * from services where service_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapService(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("loi findById Service: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Service findByName(String serviceName) {
        String sql = "select * from services where lower(trim(service_name)) = lower(trim(?)) limit 1";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serviceName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapService(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("loi findByName Service: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean addService(Service service) {
        String sql = "insert into services(service_name, unit_price, unit, description, status) values (?, ?, ?, ?, ?)";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, service.getServiceName());
            ps.setBigDecimal(2, service.getUnitPrice());
            ps.setString(3, service.getUnit());
            ps.setString(4, service.getDescription());
            ps.setString(5, service.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        service.setServiceId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("loi addService: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateService(Service service) {
        String sql = "update services set service_name = ?, unit_price = ?, unit = ?, description = ?, status = ? where service_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, service.getServiceName());
            ps.setBigDecimal(2, service.getUnitPrice());
            ps.setString(3, service.getUnit());
            ps.setString(4, service.getDescription());
            ps.setString(5, service.getStatus());
            ps.setInt(6, service.getServiceId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi updateService: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteService(int serviceId) {
        String sql = "delete from services where service_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi deleteService: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean hasRelatedBookingDetails(int serviceId) {
        String sql = "select count(*) from booking_details where service_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("loi hasRelatedBookingDetails service: " + e.getMessage());
        }

        return false;
    }

    private Service mapService(ResultSet rs) throws Exception {
        return new Service(
                rs.getInt("service_id"),
                rs.getString("service_name"),
                rs.getBigDecimal("unit_price"),
                rs.getString("unit"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
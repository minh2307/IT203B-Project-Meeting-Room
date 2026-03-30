package org.example.dao.impl;

import org.example.dao.interfaces.IBookingdetaildao;
import org.example.model.Bookingdetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Bookingdetaildao implements IBookingdetaildao {
    private static Bookingdetaildao instance;

    private Bookingdetaildao() {
    }

    public static Bookingdetaildao getInstance() {
        if (instance == null) {
            instance = new Bookingdetaildao();
        }
        return instance;
    }

    @Override
    public boolean isEquipmentAvailable(Connection conn, int equipmentId, int requiredQuantity) {
        String sql = "select quantity, status from equipments where equipment_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int quantity = rs.getInt("quantity");
                    String status = rs.getString("status");

                    boolean validStatus =
                            "active".equalsIgnoreCase(status) ||
                                    "available".equalsIgnoreCase(status);

                    return validStatus && quantity >= requiredQuantity;
                }
            }
        } catch (Exception e) {
            System.out.println("loi isEquipmentAvailable: " + e.getMessage());
        }

        return false;
    }

    public BigDecimal getServiceUnitPrice(Connection conn, int serviceId) {
        String sql = "select unit_price, status from services where service_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");

                    if (!"active".equalsIgnoreCase(status)) {
                        return null;
                    }

                    return rs.getBigDecimal("unit_price");
                }
            }
        } catch (Exception e) {
            System.out.println("loi getServiceUnitPrice: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean addBookingDetail(Connection conn, Bookingdetail bookingdetail) {
        String sql = "insert into booking_details(booking_id, equipment_id, service_id, quantity, unit_price, detail_type, note) " +
                "values (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingdetail.getBookingId());

            if (bookingdetail.getEquipmentId() == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, bookingdetail.getEquipmentId());
            }

            if (bookingdetail.getServiceId() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, bookingdetail.getServiceId());
            }

            ps.setInt(4, bookingdetail.getQuantity());
            ps.setBigDecimal(5, bookingdetail.getUnitPrice());
            ps.setString(6, bookingdetail.getDetailType());
            ps.setString(7, bookingdetail.getNote());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("loi addBookingDetail: " + e.getMessage());
        }

        return false;
    }
}
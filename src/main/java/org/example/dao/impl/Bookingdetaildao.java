package org.example.dao.impl;

import org.example.dao.interfaces.IBookingdetaildao;
import org.example.model.BookingServiceCostItem;
import org.example.model.Bookingdetail;
import org.example.util.JDBCConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Override
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

    @Override
    public BigDecimal getTotalServiceCostByBookingId(int bookingId) {
        String sql = "select coalesce(sum(quantity * unit_price), 0) as total_cost " +
                "from booking_details " +
                "where booking_id = ? and detail_type = 'service'";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total_cost");
                    return total == null ? BigDecimal.ZERO : total;
                }
            }
        } catch (Exception e) {
            System.out.println("loi getTotalServiceCostByBookingId: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    @Override
    public List<BookingServiceCostItem> getServiceCostDetailsByBookingId(int bookingId) {
        List<BookingServiceCostItem> items = new ArrayList<>();

        String sql = "select s.service_id, s.service_name, s.unit, bd.quantity, bd.unit_price, " +
                "(bd.quantity * bd.unit_price) as line_total " +
                "from booking_details bd " +
                "join services s on bd.service_id = s.service_id " +
                "where bd.booking_id = ? and bd.detail_type = 'service' " +
                "order by s.service_name asc";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookingServiceCostItem item = new BookingServiceCostItem(
                            rs.getInt("service_id"),
                            rs.getString("service_name"),
                            rs.getString("unit"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price"),
                            rs.getBigDecimal("line_total")
                    );
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.out.println("loi getServiceCostDetailsByBookingId: " + e.getMessage());
        }

        return items;
    }

    @Override
    public BigDecimal getServiceRevenueByDate(LocalDate date) {
        String sql = "select coalesce(sum(bd.quantity * bd.unit_price), 0) as total_revenue " +
                "from booking_details bd " +
                "join bookings b on bd.booking_id = b.booking_id " +
                "where bd.detail_type = 'service' " +
                "and b.booking_status = 'approved' " +
                "and date(b.start_time) = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total_revenue");
                    return total == null ? BigDecimal.ZERO : total;
                }
            }
        } catch (Exception e) {
            System.out.println("loi getServiceRevenueByDate: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getServiceRevenueByMonth(int year, int month) {
        String sql = "select coalesce(sum(bd.quantity * bd.unit_price), 0) as total_revenue " +
                "from booking_details bd " +
                "join bookings b on bd.booking_id = b.booking_id " +
                "where bd.detail_type = 'service' " +
                "and b.booking_status = 'approved' " +
                "and year(b.start_time) = ? " +
                "and month(b.start_time) = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total_revenue");
                    return total == null ? BigDecimal.ZERO : total;
                }
            }
        } catch (Exception e) {
            System.out.println("loi getServiceRevenueByMonth: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    @Override
    public List<String> getTopUsedServices(int limit) {
        List<String> results = new ArrayList<>();

        String sql = "select s.service_name, " +
                "coalesce(sum(bd.quantity), 0) as total_quantity, " +
                "coalesce(sum(bd.quantity * bd.unit_price), 0) as total_revenue " +
                "from booking_details bd " +
                "join services s on bd.service_id = s.service_id " +
                "join bookings b on bd.booking_id = b.booking_id " +
                "where bd.detail_type = 'service' " +
                "and b.booking_status = 'approved' " +
                "group by s.service_id, s.service_name " +
                "order by total_quantity desc, s.service_name asc " +
                "limit ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String serviceName = rs.getString("service_name");
                    int totalQuantity = rs.getInt("total_quantity");
                    BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                    if (totalRevenue == null) {
                        totalRevenue = BigDecimal.ZERO;
                    }

                    results.add(
                            "ten dich vu: " + serviceName
                                    + " | tong so luong: " + totalQuantity
                                    + " | tong doanh thu: " + totalRevenue.toPlainString()
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("loi getTopUsedServices: " + e.getMessage());
        }

        return results;
    }
}
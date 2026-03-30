package org.example.dao.interfaces;

import org.example.model.BookingServiceCostItem;
import org.example.model.Bookingdetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface IBookingdetaildao {
    boolean isEquipmentAvailable(Connection conn, int equipmentId, int requiredQuantity);

    BigDecimal getServiceUnitPrice(Connection conn, int serviceId);

    boolean addBookingDetail(Connection conn, Bookingdetail bookingdetail);

    BigDecimal getTotalServiceCostByBookingId(int bookingId);

    List<BookingServiceCostItem> getServiceCostDetailsByBookingId(int bookingId);

    BigDecimal getServiceRevenueByDate(LocalDate date);

    BigDecimal getServiceRevenueByMonth(int year, int month);

    List<String> getTopUsedServices(int limit);
}
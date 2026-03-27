package org.example.dao.interfaces;

import org.example.model.Bookingdetail;

import java.sql.Connection;

public interface IBookingdetaildao {
    boolean isEquipmentAvailable(Connection conn, int equipmentId, int requiredQuantity);

    boolean addBookingDetail(Connection conn, Bookingdetail bookingdetail);
}
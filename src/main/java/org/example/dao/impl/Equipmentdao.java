package org.example.dao.impl;

import org.example.dao.interfaces.IEquipmentdao;
import org.example.model.Equipment;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Equipmentdao implements IEquipmentdao {
    private static Equipmentdao instance;

    private Equipmentdao() {
    }

    public static Equipmentdao getInstance() {
        if (instance == null) {
            instance = new Equipmentdao();
        }
        return instance;
    }

    @Override
    public List<Equipment> getAllEquipments() {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "select * from equipments order by equipment_id";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                equipments.add(mapEquipment(rs));
            }

        } catch (Exception e) {
            System.out.println("loi getAllEquipments: " + e.getMessage());
        }

        return equipments;
    }

    @Override
    public Equipment findById(int equipmentId) {
        String sql = "select * from equipments where equipment_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEquipment(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findById Equipment: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean updateQuantity(int equipmentId, int quantity) {
        String sql = "update equipments set quantity = ? where equipment_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, equipmentId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("loi updateQuantity: " + e.getMessage());
        }

        return false;
    }

    private Equipment mapEquipment(ResultSet rs) throws Exception {
        return new Equipment(
                rs.getInt("equipment_id"),
                rs.getString("equipment_name"),
                rs.getString("equipment_type"),
                rs.getInt("quantity"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
package org.example.dao.impl;

import org.example.dao.interfaces.IEquipmentdao;
import org.example.model.Equipment;
import org.example.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    public Equipment findByName(String equipmentName) {
        String sql = "select * from equipments where lower(trim(equipment_name)) = lower(trim(?)) limit 1";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipmentName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEquipment(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("loi findByName Equipment: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean addEquipment(Equipment equipment) {
        String sql = "insert into equipments(equipment_name, equipment_type, quantity, description, status) " +
                "values (?, ?, ?, ?, ?)";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipment.getEquipmentName());
            ps.setString(2, equipment.getEquipmentType());
            ps.setInt(3, equipment.getQuantity());
            ps.setString(4, equipment.getDescription());
            ps.setString(5, equipment.getStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        equipment.setEquipmentId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            System.out.println("loi addEquipment: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean updateEquipment(Equipment equipment) {
        String sql = "update equipments set equipment_name = ?, equipment_type = ?, quantity = ?, description = ?, status = ? " +
                "where equipment_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipment.getEquipmentName());
            ps.setString(2, equipment.getEquipmentType());
            ps.setInt(3, equipment.getQuantity());
            ps.setString(4, equipment.getDescription());
            ps.setString(5, equipment.getStatus());
            ps.setInt(6, equipment.getEquipmentId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("loi updateEquipment: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean deleteEquipment(int equipmentId) {
        String sql = "delete from equipments where equipment_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipmentId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("loi deleteEquipment: " + e.getMessage());
        }

        return false;
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

    @Override
    public boolean hasRelatedBookingDetails(int equipmentId) {
        String sql = "select count(*) from booking_details where equipment_id = ?";

        try (Connection conn = JDBCConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            System.out.println("loi hasRelatedBookingDetails: " + e.getMessage());
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
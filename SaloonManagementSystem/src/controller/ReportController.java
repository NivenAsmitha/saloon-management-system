package controller;

import database.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportController {

    public BigDecimal totalRevenue(LocalDate from, LocalDate to) {
        final String sql = "SELECT COALESCE(SUM(total_amount),0) AS total " +
                           "FROM bookings " +
                           "WHERE status='COMPLETED' AND booking_date BETWEEN ? AND ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public Map<String, Integer> topServices(LocalDate from, LocalDate to, int limit) {
        final String sql =
            "SELECT s.name AS name, COALESCE(SUM(bs.quantity),0) AS qty " +
            "FROM booking_services bs " +
            "JOIN bookings b ON b.id = bs.booking_id " +
            "JOIN services s ON s.id = bs.service_id " +
            "WHERE b.booking_date BETWEEN ? AND ? " +
            "GROUP BY s.name " +
            "ORDER BY qty DESC " +
            "LIMIT ?";
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("name"), rs.getInt("qty"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    public Map<String, Integer> topProducts(LocalDate from, LocalDate to, int limit) {
        final String sql =
            "SELECT p.name AS name, COALESCE(SUM(bp.quantity),0) AS qty " +
            "FROM booking_products bp " +
            "JOIN bookings b ON b.id = bp.booking_id " +
            "JOIN products p ON p.id = bp.product_id " +
            "WHERE b.booking_date BETWEEN ? AND ? " +
            "GROUP BY p.name " +
            "ORDER BY qty DESC " +
            "LIMIT ?";
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("name"), rs.getInt("qty"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return map;
    }
}

package controller;

import database.DatabaseManager;
import model.Booking;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookingController {

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();

        b.setId(rs.getInt("id"));
        b.setCustomerName(rs.getString("customer_name"));

        Object age = rs.getObject("customer_age");
        b.setCustomerAge(age != null ? ((Number) age).intValue() : null);

        b.setCustomerPhone(rs.getString("customer_phone"));

        Date d = rs.getDate("booking_date");
        b.setBookingDate(d != null ? d.toLocalDate() : null);

        Time t = rs.getTime("booking_time");
        b.setBookingTime(t != null ? t.toLocalTime() : null);

        Object emp = rs.getObject("employee_id");
        b.setEmployeeId(emp != null ? ((Number) emp).intValue() : null);

        b.setStatus(rs.getString("status"));

        BigDecimal total = rs.getBigDecimal("total_amount");
        b.setTotalAmount(total != null ? total : BigDecimal.ZERO);

        Object created = rs.getObject("created_by");
        b.setCreatedBy(created != null ? ((Number) created).intValue() : null);

        Timestamp ts = rs.getTimestamp("created_at");
        b.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

        return b;
    }

    public List<Booking> getAll() {
        String sql = "SELECT * FROM bookings ORDER BY id DESC";
        List<Booking> list = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean create(Booking b) {
        String sql = "INSERT INTO bookings " +
                "(customer_name, customer_age, customer_phone, booking_date, booking_time, employee_id, status, total_amount, created_by) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nullToEmpty(b.getCustomerName()));
            if (b.getCustomerAge() != null) ps.setInt(2, b.getCustomerAge()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, nullToEmpty(b.getCustomerPhone()));
            if (b.getBookingDate() != null) ps.setDate(4, Date.valueOf(b.getBookingDate())); else ps.setNull(4, Types.DATE);
            if (b.getBookingTime() != null) ps.setTime(5, Time.valueOf(b.getBookingTime())); else ps.setNull(5, Types.TIME);
            if (b.getEmployeeId() != null) ps.setInt(6, b.getEmployeeId()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, b.getStatus() != null ? b.getStatus() : "PENDING");
            ps.setBigDecimal(8, b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO);
            if (b.getCreatedBy() != null) ps.setInt(9, b.getCreatedBy()); else ps.setNull(9, Types.INTEGER);

            int rows = ps.executeUpdate();
            if (rows == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) b.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(Booking b) {
        String sql = "UPDATE bookings SET customer_name=?, customer_age=?, customer_phone=?, booking_date=?, booking_time=?, " +
                     "employee_id=?, status=?, total_amount=?, created_by=? WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nullToEmpty(b.getCustomerName()));
            if (b.getCustomerAge() != null) ps.setInt(2, b.getCustomerAge()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, nullToEmpty(b.getCustomerPhone()));
            if (b.getBookingDate() != null) ps.setDate(4, Date.valueOf(b.getBookingDate())); else ps.setNull(4, Types.DATE);
            if (b.getBookingTime() != null) ps.setTime(5, Time.valueOf(b.getBookingTime())); else ps.setNull(5, Types.TIME);
            if (b.getEmployeeId() != null) ps.setInt(6, b.getEmployeeId()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, b.getStatus() != null ? b.getStatus() : "PENDING");
            ps.setBigDecimal(8, b.getTotalAmount() != null ? b.getTotalAmount() : BigDecimal.ZERO);
            if (b.getCreatedBy() != null) ps.setInt(9, b.getCreatedBy()); else ps.setNull(9, Types.INTEGER);
            ps.setInt(10, b.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM bookings WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ---------- Booking line helpers ----------
    public boolean addServiceToBooking(int bookingId, int serviceId, int qty, BigDecimal price) {
        String sql = "INSERT INTO booking_services (booking_id, service_id, quantity, price) VALUES (?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, serviceId);
            ps.setInt(3, Math.max(1, qty));
            ps.setBigDecimal(4, price != null ? price : BigDecimal.ZERO);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean addProductToBooking(int bookingId, int productId, int qty, BigDecimal price) {
        String sql = "INSERT INTO booking_products (booking_id, product_id, quantity, price) VALUES (?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, productId);
            ps.setInt(3, Math.max(1, qty));
            ps.setBigDecimal(4, price != null ? price : BigDecimal.ZERO);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public BigDecimal recomputeTotal(int bookingId) {
        String svc = "SELECT COALESCE(SUM(quantity * price), 0) AS total FROM booking_services WHERE booking_id = ?";
        String prod = "SELECT COALESCE(SUM(quantity * price), 0) AS total FROM booking_products WHERE booking_id = ?";

        BigDecimal total = BigDecimal.ZERO;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps1 = c.prepareStatement(svc);
             PreparedStatement ps2 = c.prepareStatement(prod)) {

            ps1.setInt(1, bookingId);
            try (ResultSet r1 = ps1.executeQuery()) {
                if (r1.next() && r1.getBigDecimal("total") != null) {
                    total = total.add(r1.getBigDecimal("total"));
                }
            }

            ps2.setInt(1, bookingId);
            try (ResultSet r2 = ps2.executeQuery()) {
                if (r2.next() && r2.getBigDecimal("total") != null) {
                    total = total.add(r2.getBigDecimal("total"));
                }
            }

            try (PreparedStatement upd = c.prepareStatement("UPDATE bookings SET total_amount=? WHERE id=?")) {
                upd.setBigDecimal(1, total);
                upd.setInt(2, bookingId);
                upd.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    // ---------- New Bill creation ----------
    public int createBill(int bookingId, BigDecimal total, int createdBy) {
        String sql = "INSERT INTO bills (booking_id, total_amount, created_by) VALUES (?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (bookingId > 0) {
                ps.setInt(1, bookingId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setBigDecimal(2, total != null ? total : BigDecimal.ZERO);
            ps.setInt(3, createdBy);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

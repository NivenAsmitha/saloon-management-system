package controller;

import database.DatabaseManager;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class EmployeeController {

    // Safely map a ResultSet row to Employee using setters (no constructor assumptions)
    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setPhone(rs.getString("phone"));
        e.setEmail(rs.getString("email"));
        e.setAddress(rs.getString("address"));
        // salary can be NULL
        BigDecimal salary = rs.getBigDecimal("salary");
        e.setSalary(salary != null ? salary : BigDecimal.ZERO);
        // hire_date can be NULL
        Date hd = rs.getDate("hire_date");
        e.setHireDate(hd != null ? hd.toLocalDate() : null);
        // user_id can be NULL → use getObject to allow null
        Object uid = rs.getObject("user_id");
        e.setUserId(uid != null ? ((Number) uid).intValue() : null);
        return e;
    }

    public List<Employee> getAll() {
        String sql = "SELECT id, name, phone, email, address, salary, hire_date, user_id " +
                     "FROM employees ORDER BY id DESC";
        List<Employee> list = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean create(Employee e) {
        String sql = "INSERT INTO employees(name, phone, email, address, salary, hire_date, user_id) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, e.getName());
            ps.setString(2, e.getPhone());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getAddress());
            ps.setBigDecimal(5, e.getSalary() != null ? e.getSalary() : BigDecimal.ZERO);
            if (e.getHireDate() != null) {
                ps.setDate(6, java.sql.Date.valueOf(e.getHireDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            if (e.getUserId() != null) {
                ps.setInt(7, e.getUserId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean update(Employee e) {
        String sql = "UPDATE employees SET name=?, phone=?, email=?, address=?, salary=?, hire_date=?, user_id=? " +
                     "WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, e.getName());
            ps.setString(2, e.getPhone());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getAddress());
            ps.setBigDecimal(5, e.getSalary() != null ? e.getSalary() : BigDecimal.ZERO);
            if (e.getHireDate() != null) {
                ps.setDate(6, java.sql.Date.valueOf(e.getHireDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            if (e.getUserId() != null) {
                ps.setInt(7, e.getUserId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, e.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

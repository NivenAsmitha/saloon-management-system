package controller;

import database.DatabaseManager;

import java.security.SecureRandom;
import java.sql.*;

public class UserController {

    private static final String ABC = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%";
    private static final SecureRandom RNG = new SecureRandom();

    // ---------- Password helpers ----------
    public String generateTempPassword(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ABC.charAt(RNG.nextInt(ABC.length())));
        }
        return sb.toString();
    }

    // ---------- Create users ----------
    /** Create a user with a generated temp password and given role (ADMIN/EMPLOYEE). must_change_password=1. */
    public boolean createUserWithTempPassword(String username, String role, String[] outPlainPassword) {
        String temp = generateTempPassword(10);
        if (outPlainPassword != null && outPlainPassword.length > 0) {
            outPlainPassword[0] = temp; // show once in UI
        }

        final String sql = "INSERT INTO users (username, password, role, must_change_password) VALUES (?,?,?,1)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, temp); // NOTE: plain text for demo; hash in production
            ps.setString(3, role);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Create an EMPLOYEE user with a temp password you provide (useful from Employee form). */
    public int createEmployeeUser(String username, String tempPassword) {
        final String sql = "INSERT INTO users (username, password, role, must_change_password) VALUES (?,?, 'EMPLOYEE', 1)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, tempPassword == null || tempPassword.isEmpty() ? "Pass@123" : tempPassword);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // failed
    }

    // ---------- Password change ----------
    /** Change password and clear must_change_password flag. */
    public boolean changePassword(int userId, String newPassword) {
        final String sql = "UPDATE users SET password=?, must_change_password=0 WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newPassword); // NOTE: hash in production
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------- Optional small helpers ----------
    public boolean userExists(String username) {
        final String sql = "SELECT 1 FROM users WHERE username=? LIMIT 1";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // if error, treat as not found
        }
    }

    public Integer getUserIdByUsername(String username) {
        final String sql = "SELECT id FROM users WHERE username=? LIMIT 1";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

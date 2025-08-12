package controller;

import database.DatabaseManager;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    /**
     * Authenticate with plain-text password (demo).
     * Returns a populated User on success, or null if no match.
     */
    public User authenticate(String username, String password) {
        final String sql = "SELECT id, username, role FROM users WHERE username = ? AND password = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // or log properly
        }
        return null;
    }

    /**
     * Returns true if the user must change their password (first login or admin reset).
     */
    public boolean mustChangePassword(int userId) {
        final String sql = "SELECT must_change_password FROM users WHERE id = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object v = rs.getObject("must_change_password");
                    if (v instanceof Boolean) return (Boolean) v;
                    if (v instanceof Number) return ((Number) v).intValue() != 0;
                    return "1".equals(String.valueOf(v));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* ---------------- Optional helpers for debugging / seeding ----------------

    // Print current users to console (useful while wiring DB)
    public void debugPrintUsers() {
        final String sql = "SELECT id, username, password, role, must_change_password FROM users";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("---- USERS ----");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        String.valueOf(rs.getObject("must_change_password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // One-time seed if 'admin' doesn't exist
    public void ensureDefaultAdmin() {
        final String check = "SELECT 1 FROM users WHERE username='admin' LIMIT 1";
        final String insert = "INSERT INTO users (username, password, role, must_change_password) VALUES ('admin','admin123','ADMIN',0)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(check);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                try (PreparedStatement ins = c.prepareStatement(insert)) {
                    ins.executeUpdate();
                    System.out.println("Seeded default admin (admin/admin123).");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    -------------------------------------------------------------------------- */
}

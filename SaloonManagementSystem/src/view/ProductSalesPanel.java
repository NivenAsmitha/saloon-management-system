package view;

import database.DatabaseManager;
import model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductSalesPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Bill ID", "Customer Name", "Total Amount", "Created By", "Date"}, 0
    );
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField searchField = new JTextField(20);

    // --- keep compatibility with old call sites ---
    public ProductSalesPanel(User ignored) {
        this();
    }

    // --- actual constructor used now ---
    public ProductSalesPanel() {
        setLayout(new BorderLayout());

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadSales());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refresh);
        top.add(new JLabel("Search:"));
        top.add(searchField);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Link sorter to table for filtering
        table.setRowSorter(sorter);

        // Search functionality: live filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        loadSales();
    }

    private void filter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // case-insensitive
        }
    }

    private void loadSales() {
        model.setRowCount(0);
        String sql = """
            SELECT b.id AS bill_id,
                   COALESCE(bk.customer_name, 'Walk-in') AS customer_name,
                   b.total_amount,
                   u.username AS created_by,
                   b.created_at
            FROM bills b
            LEFT JOIN bookings bk ON b.booking_id = bk.id
            LEFT JOIN users u ON b.created_by = u.id
            ORDER BY b.id DESC
        """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("bill_id"),
                        rs.getString("customer_name"),
                        rs.getBigDecimal("total_amount"),
                        rs.getString("created_by"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading sales: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

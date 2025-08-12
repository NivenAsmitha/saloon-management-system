package view;

import model.User;
import controller.UserController;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard(User user) {
        super("Admin Dashboard - " + (user != null ? user.getUsername() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 192, 203)); // Pink
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("CHAMI SALOON - Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.BLACK);

        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ===== TOP TOOLBAR =====
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(255, 228, 225)); // Lighter pink

        JButton btnCreateUser = new JButton("Create User");
        styleButton(btnCreateUser, new Color(255, 105, 180), Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout, new Color(255, 160, 122), Color.WHITE);

        topBar.add(btnCreateUser);
        topBar.add(btnLogout);

        add(topBar, BorderLayout.BEFORE_FIRST_LINE);

        // ===== TABS =====
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabs.addTab("Products", wrapCard(new ProductManagementPanel()));
        tabs.addTab("Services", wrapCard(new ServiceManagementPanel()));
        tabs.addTab("Employees", wrapCard(new EmployeeManagementPanel()));
        tabs.addTab("Bookings", wrapCard(new BookingManagementPanel()));
        tabs.addTab("Reports", wrapCard(new ReportPanel()));

        add(tabs, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(255, 228, 225));
        JButton exitBtn = new JButton("Exit");
        styleButton(exitBtn, new Color(255, 69, 0), Color.WHITE);
        exitBtn.addActionListener(e -> System.exit(0));
        bottomPanel.add(exitBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnCreateUser.addActionListener(e -> onCreateUser());
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
    }

    private void onCreateUser() {
        JTextField username = new JTextField();
        JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "EMPLOYEE"});

        int ok = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Username:", username, "Role:", role},
                "Create User",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (ok == JOptionPane.OK_OPTION) {
            String uname = username.getText().trim();
            String r = role.getSelectedItem().toString();

            if (uname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] outTemp = new String[1];
            UserController uc = new UserController();
            boolean created = uc.createUserWithTempPassword(uname, r, outTemp);

            if (created) {
                JOptionPane.showMessageDialog(
                        this,
                        "User created.\n\nUsername: " + uname +
                                "\nTemporary password: " + outTemp[0] +
                                "\n\nShare this with the user. They should log in and change it.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create user (maybe username exists).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel wrapCard(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 240, 245)); // Light pink
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}

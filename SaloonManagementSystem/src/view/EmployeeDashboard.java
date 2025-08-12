package view;

import controller.LoginController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class EmployeeDashboard extends JFrame {
    private final User user;
    private final JTabbedPane tabs = new JTabbedPane();

    // Kept as fields so we can remove them later
    private ChangePasswordPanel changePwdPanel;
    private JPanel banner;

    public EmployeeDashboard(User user) {
        super("Employee Dashboard - " + (user != null ? user.getUsername() : ""));
        this.user = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== Dashboard Header ======
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 192, 203)); // Pink
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("CHAMI SALOON - Employee Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.BLACK);

        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ====== Main Tabbed Pane ======
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Wrap each panel in a colored background
        tabs.addTab("Bookings", wrapCard(new BookingManagementPanel()));
        tabs.addTab("Services", wrapCard(new ServiceManagementPanel()));
        tabs.addTab("Products", wrapCard(new ProductListPanel()));
        tabs.addTab("Sales", wrapCard(new ProductSalesPanel()));
        tabs.addTab("Billing", wrapCard(new BillingPanel(user)));

        add(tabs, BorderLayout.CENTER);

        // ====== Footer with Exit Button ======
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(255, 228, 225));
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitBtn.setBackground(new Color(255, 105, 180));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitBtn.addActionListener(e -> System.exit(0));
        bottom.add(exitBtn);
        add(bottom, BorderLayout.SOUTH);

        // ====== Check if password change required ======
        if (user != null) {
            LoginController lc = new LoginController();
            boolean mustChange = lc.mustChangePassword(user.getId());
            if (mustChange) addChangePasswordUI();
        }
    }

    private JPanel wrapCard(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 240, 245)); // Light pink
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }

    private void addChangePasswordUI() {
        changePwdPanel = new ChangePasswordPanel(user.getId(), this::onPasswordChanged);
        tabs.addTab("Change Password", changePwdPanel);
        tabs.setSelectedComponent(changePwdPanel);

        banner = new JPanel(new BorderLayout());
        banner.setBackground(new Color(255, 240, 200));
        JLabel msg = new JLabel("You must change your password to continue using the system.");
        msg.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        banner.add(msg, BorderLayout.CENTER);
        add(banner, BorderLayout.NORTH);

        revalidate();
        repaint();
    }

    private void onPasswordChanged() {
        if (changePwdPanel != null) {
            int idx = tabs.indexOfComponent(changePwdPanel);
            if (idx >= 0) tabs.removeTabAt(idx);
            changePwdPanel = null;
        }
        if (banner != null) {
            remove(banner);
            banner = null;
        }

        int productsIdx = tabs.indexOfTab("Products");
        if (productsIdx >= 0) tabs.setSelectedIndex(productsIdx);

        JOptionPane.showMessageDialog(
                this,
                "Password updated. You can continue working.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        revalidate();
        repaint();
    }
}

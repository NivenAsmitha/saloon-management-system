package view;

import controller.LoginController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JButton loginBtn = new JButton("Login");
    private final JButton exitBtn = new JButton("Exit");
    private final LoginController loginController = new LoginController();

    public LoginFrame() {
        super("Saloon Management - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 300);
        setLocationRelativeTo(null);

        // Root panel with light pink gradient background
        JPanel rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 240, 245), // Light pink top
                        0, getHeight(), new Color(255, 228, 235) // Light pink bottom
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // ======== Title ========
        JLabel title = new JLabel("CHAMI SALOON", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(139, 69, 19)); // Brown text
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        rootPanel.add(title, BorderLayout.NORTH);

        // ======== Form ========
        JPanel form = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 200)); // White with transparency
                g2d.fillRoundRect(50, 30, getWidth() - 100, getHeight() - 60, 15, 15);

                g2d.setColor(new Color(255, 182, 193)); // Pink border
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(50, 30, getWidth() - 101, getHeight() - 61, 15, 15);
            }
        };
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.anchor = GridBagConstraints.WEST;

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setForeground(new Color(139, 69, 19));
        styleTextField(usernameField);

        gc.gridx = 0;
        gc.gridy = 0;
        form.add(userLabel, gc);
        gc.gridx = 1;
        form.add(usernameField, gc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passLabel.setForeground(new Color(139, 69, 19));
        styleTextField(passwordField);

        gc.gridx = 0;
        gc.gridy = 1;
        form.add(passLabel, gc);
        gc.gridx = 1;
        form.add(passwordField, gc);

        // Buttons
        styleButton(loginBtn, new Color(255, 105, 180), Color.WHITE); // Pink
        styleButton(exitBtn, new Color(255, 160, 122), Color.WHITE); // Salmon

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        btns.add(loginBtn);
        btns.add(exitBtn);

        gc.gridx = 1;
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.EAST;
        form.add(btns, gc);

        rootPanel.add(form, BorderLayout.CENTER);
        add(rootPanel);
        getRootPane().setDefaultButton(loginBtn);

        // Actions
        loginBtn.addActionListener(this::onLogin);
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 182, 193), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        field.setBackground(Color.WHITE);
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void onLogin(ActionEvent e) {
        String u = usernameField.getText().trim();
        String p = new String(passwordField.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password.", "Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = loginController.authenticate(u, p);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new EmployeeDashboard(user).setVisible(true);
            }
            dispose();
        });
    }
}

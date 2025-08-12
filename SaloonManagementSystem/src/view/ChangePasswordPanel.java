package view;

import controller.UserController;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {
    private final int userId;
    private final Runnable onSuccess;
    private final JPasswordField pf1 = new JPasswordField(16);
    private final JPasswordField pf2 = new JPasswordField(16);

    public ChangePasswordPanel(int userId, Runnable onSuccess) {
        this.userId = userId;
        this.onSuccess = onSuccess;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("New password:"), g);
        g.gridx = 1; form.add(pf1, g);
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Confirm password:"), g);
        g.gridx = 1; form.add(pf2, g);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save Password");
        btns.add(save);

        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        save.addActionListener(e -> savePassword());
    }

    private void savePassword() {
        String p1 = new String(pf1.getPassword());
        String p2 = new String(pf2.getPassword());
        if (p1.isEmpty()) { JOptionPane.showMessageDialog(this, "Password required."); return; }
        if (!p1.equals(p2)) { JOptionPane.showMessageDialog(this, "Passwords do not match."); return; }

        UserController uc = new UserController();
        boolean ok = uc.changePassword(userId, p1);
        if (ok) {
            if (onSuccess != null) onSuccess.run();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

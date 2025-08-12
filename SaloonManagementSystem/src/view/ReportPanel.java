package view;

import controller.ReportController;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ReportPanel extends JPanel {
    private final ReportController controller = new ReportController();
    private final JTextField from = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 10);
    private final JTextField to = new JTextField(LocalDate.now().toString(), 10);
    private final JTextArea output = new JTextArea(14, 60);

    public ReportPanel() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton run = new JButton("Run Report");
        top.add(new JLabel("From (YYYY-MM-DD):"));
        top.add(from);
        top.add(new JLabel("To:"));
        top.add(to);
        top.add(run);
        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        run.addActionListener(e -> runReport());
        runReport();
    }

    private void runReport() {
        try {
            LocalDate f = LocalDate.parse(from.getText().trim());
            LocalDate t = LocalDate.parse(to.getText().trim());

            BigDecimal revenue = controller.totalRevenue(f, t);
            Map<String, Integer> svc = controller.topServices(f, t, 5);
            Map<String, Integer> prod = controller.topProducts(f, t, 5);

            StringBuilder sb = new StringBuilder();
            sb.append("=== Revenue (Completed bookings) ===\n");
            sb.append("Total: ").append(revenue).append("\n\n");

            sb.append("=== Top Services ===\n");
            svc.forEach((k,v) -> sb.append(k).append(" : ").append(v).append("\n"));
            sb.append("\n=== Top Products ===\n");
            prod.forEach((k,v) -> sb.append(k).append(" : ").append(v).append("\n"));

            output.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

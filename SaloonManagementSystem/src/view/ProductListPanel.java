package view;

import controller.ProductController;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListPanel extends JPanel {
    private final ProductController productController = new ProductController();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Description","Price","Stock","Created At"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; } // read-only
    };

    private final JTable table = new JTable(model);
    private List<Product> cached; // keep a copy for filtering
    private final JTextField searchField = new JTextField(18);

    public ProductListPanel() {
        setLayout(new BorderLayout());

        // Top bar: search + refresh
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(refresh);
        add(top, BorderLayout.NORTH);

        // Table
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events
        refresh.addActionListener(e -> load());
        searchField.addActionListener(e -> applyFilter());
        searchField.getDocument().addDocumentListener(new SimpleDocListener(this::applyFilter));

        // initial load
        load();
    }

    private void load() {
        cached = productController.getAll();
        fillTable(cached);
    }

    private void applyFilter() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) { fillTable(cached); return; }
        List<Product> filtered = cached.stream()
                .filter(p ->
                        (p.getName()!=null && p.getName().toLowerCase().contains(q)) ||
                        (p.getDescription()!=null && p.getDescription().toLowerCase().contains(q))
                )
                .collect(Collectors.toList());
        fillTable(filtered);
    }

    private void fillTable(List<Product> items) {
        model.setRowCount(0);
        if (items == null) return;
        for (Product p : items) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getPrice(),
                    p.getStockQuantity(),
                    p.getCreatedAt()
            });
        }
    }

    // tiny helper for live filter without extra deps
    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable r;
        SimpleDocListener(Runnable r){ this.r = r; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
    }
}

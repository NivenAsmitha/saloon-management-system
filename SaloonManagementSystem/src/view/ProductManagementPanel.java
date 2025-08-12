package view;

import controller.ProductController;
import model.Product;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductManagementPanel extends JPanel {

    private final ProductController controller = new ProductController();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Description", "Price", "Stock"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // make table read-only
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField searchField = new JTextField(20);

    public ProductManagementPanel() {
        setLayout(new BorderLayout());

        // Top panel with buttons + search
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");

        top.add(refresh);
        top.add(add);
        top.add(edit);
        top.add(delete);
        top.add(new JLabel("Search:"));
        top.add(searchField);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Enable sorting and filtering
        table.setRowSorter(sorter);

        // Live search functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        // Button actions
        refresh.addActionListener(e -> load());
        add.addActionListener(e -> addDialog());
        edit.addActionListener(e -> editDialog());
        delete.addActionListener(e -> deleteSelected());

        load();
    }

    private void filter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // case-insensitive
        }
    }

    private void load() {
        model.setRowCount(0);
        List<Product> list = controller.getAll();
        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getPrice(),
                    p.getStockQuantity()
            });
        }
    }

    private void addDialog() {
        JTextField name = new JTextField();
        JTextField desc = new JTextField();
        JTextField price = new JTextField("0");
        JTextField stock = new JTextField("0");

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Name:", name, "Description:", desc, "Price:", price, "Stock:", stock},
                "Add Product", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Product p = new Product();
                p.setName(name.getText().trim());
                p.setDescription(desc.getText().trim());
                p.setPrice(new BigDecimal(price.getText().trim()));
                p.setStockQuantity(Integer.parseInt(stock.getText().trim()));
                if (controller.create(p)) load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        int row = table.convertRowIndexToModel(viewRow); // adjust for filter
        int id = (int) model.getValueAt(row, 0);

        JTextField name = new JTextField(model.getValueAt(row, 1).toString());
        JTextField desc = new JTextField(model.getValueAt(row, 2).toString());
        JTextField price = new JTextField(model.getValueAt(row, 3).toString());
        JTextField stock = new JTextField(model.getValueAt(row, 4).toString());

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Name:", name, "Description:", desc, "Price:", price, "Stock:", stock},
                "Edit Product", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Product p = new Product();
                p.setId(id);
                p.setName(name.getText().trim());
                p.setDescription(desc.getText().trim());
                p.setPrice(new BigDecimal(price.getText().trim()));
                p.setStockQuantity(Integer.parseInt(stock.getText().trim()));
                if (controller.update(p)) load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        int row = table.convertRowIndexToModel(viewRow); // adjust for filter
        int id = (int) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Delete product " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            if (controller.delete(id)) load();
        }
    }
}

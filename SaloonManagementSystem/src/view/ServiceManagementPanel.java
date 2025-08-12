package view;

import controller.ServiceController;
import model.Service;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ServiceManagementPanel extends JPanel {

    private final ServiceController controller = new ServiceController();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Description", "Price", "Duration(min)"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // make table read-only
        }
    };
    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField searchField = new JTextField(20);

    public ServiceManagementPanel() {
        setLayout(new BorderLayout());

        // Top panel with buttons and search
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

        // Link table with sorter for filtering
        table.setRowSorter(sorter);

        // Live search filter
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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // Case-insensitive
        }
    }

    private void load() {
        model.setRowCount(0); // clear table
        List<Service> list = controller.getAll();
        for (Service s : list) {
            model.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getDescription(),
                    s.getPrice(),
                    s.getDurationMinutes()
            });
        }
    }

    private void addDialog() {
        JTextField name = new JTextField();
        JTextField desc = new JTextField();
        JTextField price = new JTextField("0");
        JTextField dur = new JTextField("30");

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Name:", name, "Description:", desc, "Price:", price, "Duration (min):", dur},
                "Add Service", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Service s = new Service();
                s.setName(name.getText().trim());
                s.setDescription(desc.getText().trim());
                s.setPrice(new BigDecimal(price.getText().trim()));
                s.setDurationMinutes(Integer.parseInt(dur.getText().trim()));

                if (controller.create(s)) load();
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
        JTextField dur = new JTextField(model.getValueAt(row, 4).toString());

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Name:", name, "Description:", desc, "Price:", price, "Duration (min):", dur},
                "Edit Service", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Service s = new Service();
                s.setId(id);
                s.setName(name.getText().trim());
                s.setDescription(desc.getText().trim());
                s.setPrice(new BigDecimal(price.getText().trim()));
                s.setDurationMinutes(Integer.parseInt(dur.getText().trim()));

                if (controller.update(s)) load();
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

        if (JOptionPane.showConfirmDialog(this, "Delete service " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            if (controller.delete(id)) load();
        }
    }
}

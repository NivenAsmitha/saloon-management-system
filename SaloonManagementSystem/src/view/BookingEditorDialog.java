package view;

import controller.BookingController;
import controller.ProductController;
import controller.ServiceController;
import model.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class BookingEditorDialog extends JDialog {
    private final BookingController bookingController = new BookingController();
    private final ServiceController serviceController = new ServiceController();
    private final ProductController productController = new ProductController();

    private final JTextField tfCustomer = new JTextField(16);
    private final JTextField tfAge = new JTextField(6);
    private final JTextField tfPhone = new JTextField(12);
    private final JTextField tfDate = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField tfTime = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString(), 6);
    private final JTextField tfEmployeeId = new JTextField(6);
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"PENDING","CONFIRMED","COMPLETED","CANCELLED"});
    private final JLabel lblTotal = new JLabel("0.00");

    private final DefaultTableModel svcModel = new DefaultTableModel(new String[]{"Service ID","Name","Qty","Price","Line Total"}, 0);
    private final JTable tblServices = new JTable(svcModel);

    private final DefaultTableModel prodModel = new DefaultTableModel(new String[]{"Product ID","Name","Qty","Price","Line Total"}, 0);
    private final JTable tblProducts = new JTable(prodModel);

    private Map<Integer, String> services;
    private Map<Integer, String> products;

    private BigDecimal total = BigDecimal.ZERO;

    public interface SaveHandler { void onSaved(); }

    public BookingEditorDialog(Frame owner, SaveHandler onSaved) {
        super(owner, "New Booking", true);
        setSize(780, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Top form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.anchor = GridBagConstraints.WEST;

        int r=0;
        g.gridx=0; g.gridy=r; form.add(new JLabel("Customer:"), g);
        g.gridx=1; form.add(tfCustomer, g);
        g.gridx=2; form.add(new JLabel("Age:"), g);
        g.gridx=3; form.add(tfAge, g);
        r++;
        g.gridx=0; g.gridy=r; form.add(new JLabel("Phone:"), g);
        g.gridx=1; form.add(tfPhone, g);
        g.gridx=2; form.add(new JLabel("Date (YYYY-MM-DD):"), g);
        g.gridx=3; form.add(tfDate, g);
        r++;
        g.gridx=0; g.gridy=r; form.add(new JLabel("Time (HH:MM):"), g);
        g.gridx=1; form.add(tfTime, g);
        g.gridx=2; form.add(new JLabel("Employee ID:"), g);
        g.gridx=3; form.add(tfEmployeeId, g);
        r++;
        g.gridx=0; g.gridy=r; form.add(new JLabel("Status:"), g);
        g.gridx=1; form.add(cbStatus, g);
        g.gridx=2; form.add(new JLabel("Total:"), g);
        g.gridx=3; form.add(lblTotal, g);

        add(form, BorderLayout.NORTH);

        // Center with two tables and add-row controls
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);

        // Services panel
        JPanel svcPanel = new JPanel(new BorderLayout());
        JPanel svcAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Item> svcCombo = new JComboBox<>();
        JTextField svcQty = new JTextField("1", 4);
        JTextField svcPrice = new JTextField("0", 6);
        JButton addSvc = new JButton("Add Service");
        svcAdd.add(new JLabel("Service:"));
        svcAdd.add(svcCombo);
        svcAdd.add(new JLabel("Qty:"));
        svcAdd.add(svcQty);
        svcAdd.add(new JLabel("Price:"));
        svcAdd.add(svcPrice);
        svcAdd.add(addSvc);
        svcPanel.add(svcAdd, BorderLayout.NORTH);
        svcPanel.add(new JScrollPane(tblServices), BorderLayout.CENTER);

        // Products panel
        JPanel prodPanel = new JPanel(new BorderLayout());
        JPanel prodAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<Item> prodCombo = new JComboBox<>();
        JTextField prodQty = new JTextField("1", 4);
        JTextField prodPrice = new JTextField("0", 6);
        JButton addProd = new JButton("Add Product");
        prodAdd.add(new JLabel("Product:"));
        prodAdd.add(prodCombo);
        prodAdd.add(new JLabel("Qty:"));
        prodAdd.add(prodQty);
        prodAdd.add(new JLabel("Price:"));
        prodAdd.add(prodPrice);
        prodAdd.add(addProd);
        prodPanel.add(prodAdd, BorderLayout.NORTH);
        prodPanel.add(new JScrollPane(tblProducts), BorderLayout.CENTER);

        split.setTopComponent(svcPanel);
        split.setBottomComponent(prodPanel);
        add(split, BorderLayout.CENTER);

        // Bottom buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save = new JButton("Save Booking");
        JButton cancel = new JButton("Cancel");
        btns.add(save); btns.add(cancel);
        add(btns, BorderLayout.SOUTH);

        // Load dropdown data
        services = serviceController.getIdNameMap();
        for (var e : services.entrySet()) svcCombo.addItem(new Item(e.getKey(), e.getValue()));
        products = productController.getIdNameMap();
        for (var e : products.entrySet()) prodCombo.addItem(new Item(e.getKey(), e.getValue()));

        // Add line handlers
        addSvc.addActionListener(e -> {
            try {
                Item it = (Item) svcCombo.getSelectedItem();
                int q = Math.max(1, Integer.parseInt(svcQty.getText().trim()));
                BigDecimal p = new BigDecimal(svcPrice.getText().trim());
                BigDecimal line = p.multiply(BigDecimal.valueOf(q));
                svcModel.addRow(new Object[]{it.id, it.label, q, p, line});
                updateTotal();
            } catch (Exception ex) { showErr(ex); }
        });

        addProd.addActionListener(e -> {
            try {
                Item it = (Item) prodCombo.getSelectedItem();
                int q = Math.max(1, Integer.parseInt(prodQty.getText().trim()));
                BigDecimal p = new BigDecimal(prodPrice.getText().trim());
                BigDecimal line = p.multiply(BigDecimal.valueOf(q));
                prodModel.addRow(new Object[]{it.id, it.label, q, p, line});
                updateTotal();
            } catch (Exception ex) { showErr(ex); }
        });

        // Save booking
        save.addActionListener(e -> {
            try {
                Booking b = new Booking();
                b.setCustomerName(tfCustomer.getText().trim());
                b.setCustomerAge(tfAge.getText().isEmpty() ? null : Integer.parseInt(tfAge.getText().trim()));
                b.setCustomerPhone(tfPhone.getText().trim());
                b.setBookingDate(LocalDate.parse(tfDate.getText().trim()));
                // tolerate HH:MM
                String t = tfTime.getText().trim();
                if (t.length() == 5) t = t + ":00";
                b.setBookingTime(LocalTime.parse(t));
                b.setEmployeeId(tfEmployeeId.getText().isEmpty() ? null : Integer.parseInt(tfEmployeeId.getText().trim()));
                b.setStatus(cbStatus.getSelectedItem().toString());
                b.setTotalAmount(total);
                // created_by is optional—leave null or set current user id if you track it

                if (!bookingController.create(b)) {
                    JOptionPane.showMessageDialog(this, "Failed to create booking.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int bookingId = b.getId();

                // Persist service lines
                for (int i=0; i<svcModel.getRowCount(); i++) {
                    int sid = (int) svcModel.getValueAt(i, 0);
                    int q = (int) svcModel.getValueAt(i, 2);
                    BigDecimal p = (BigDecimal) svcModel.getValueAt(i, 3);
                    bookingController.addServiceToBooking(bookingId, sid, q, p);
                }
                // Persist product lines
                for (int i=0; i<prodModel.getRowCount(); i++) {
                    int pid = (int) prodModel.getValueAt(i, 0);
                    int q = (int) prodModel.getValueAt(i, 2);
                    BigDecimal p = (BigDecimal) prodModel.getValueAt(i, 3);
                    bookingController.addProductToBooking(bookingId, pid, q, p);
                }

                // Final recompute and close
                bookingController.recomputeTotal(bookingId);
                JOptionPane.showMessageDialog(this, "Booking saved. ID = " + bookingId, "Success", JOptionPane.INFORMATION_MESSAGE);
                if (onSaved != null) onSaved.onSaved();
                dispose();
            } catch (Exception ex) { showErr(ex); }
        });

        cancel.addActionListener(e -> dispose());
    }

    private void updateTotal() {
        total = BigDecimal.ZERO;
        for (int i=0; i<svcModel.getRowCount(); i++) {
            total = total.add((BigDecimal) svcModel.getValueAt(i, 4));
        }
        for (int i=0; i<prodModel.getRowCount(); i++) {
            total = total.add((BigDecimal) prodModel.getValueAt(i, 4));
        }
        lblTotal.setText(total.toPlainString());
    }

    private void showErr(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class Item {
        final int id; final String label;
        Item(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}

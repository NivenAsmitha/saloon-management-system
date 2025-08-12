package view;

import controller.BookingController;
import controller.ProductController;
import controller.ServiceController;
import model.Booking;
import model.Product;
import model.Service;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BillingPanel extends JPanel {

    private final User currentUser;
    private final BookingController bookingController = new BookingController();
    private final ServiceController serviceController = new ServiceController();
    private final ProductController productController = new ProductController();

    private final JTextField tfCustomer = new JTextField(16);
    private final JTextField tfAge = new JTextField(6);
    private final JTextField tfPhone = new JTextField(12);
    private final JTextField tfDate = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField tfTime = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString(), 8);

    private final DefaultTableModel svcModel = new DefaultTableModel(
            new String[]{"ID","Service","Qty","Price","Line Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return c == 2 || c == 3; }
        @Override public Class<?> getColumnClass(int c) {
            return switch (c) {
                case 0 -> Integer.class;
                case 2 -> Integer.class;
                case 3,4 -> BigDecimal.class;
                default -> String.class;
            };
        }
    };
    private final JTable svcTable = new JTable(svcModel);

    private final DefaultTableModel prodModel = new DefaultTableModel(
            new String[]{"ID","Product","Qty","Price","Line Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return c == 2 || c == 3; }
        @Override public Class<?> getColumnClass(int c) {
            return switch (c) {
                case 0 -> Integer.class;
                case 2 -> Integer.class;
                case 3,4 -> BigDecimal.class;
                default -> String.class;
            };
        }
    };
    private final JTable prodTable = new JTable(prodModel);

    private final JLabel lblSvcTotal = new JLabel("0.00");
    private final JLabel lblProdTotal = new JLabel("0.00");
    private final JLabel lblGrand = new JLabel("0.00");

    private JComboBox<Item> svcCombo;
    private JComboBox<Item> prodCombo;
    private JTextField svcQtyField;
    private JTextField svcPriceField;
    private JTextField prodQtyField;
    private JTextField prodPriceField;

    private boolean recalcLock = false;

    public BillingPanel(User currentUser) {
        this.currentUser = currentUser;
        buildUI();
        hookModelListeners();
        loadChoices();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel hdr = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.anchor = GridBagConstraints.WEST;
        int r=0;
        g.gridx=0; g.gridy=r; hdr.add(new JLabel("Customer:"), g);
        g.gridx=1; hdr.add(tfCustomer, g);
        g.gridx=2; hdr.add(new JLabel("Age:"), g);
        g.gridx=3; hdr.add(tfAge, g);
        r++;
        g.gridx=0; g.gridy=r; hdr.add(new JLabel("Phone:"), g);
        g.gridx=1; hdr.add(tfPhone, g);
        g.gridx=2; hdr.add(new JLabel("Date:"), g);
        g.gridx=3; hdr.add(tfDate, g);
        r++;
        g.gridx=0; g.gridy=r; hdr.add(new JLabel("Time:"), g);
        g.gridx=1; hdr.add(tfTime, g);

        add(hdr, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);
        split.setTopComponent(buildServicesPanel());
        split.setBottomComponent(buildProductsPanel());
        add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());

        JPanel totals = new JPanel(new GridBagLayout());
        GridBagConstraints t = new GridBagConstraints();
        t.insets = new Insets(6,8,6,8); t.anchor = GridBagConstraints.EAST;
        int tr=0;
        t.gridx=0; t.gridy=tr; totals.add(new JLabel("Services Total:"), t);
        t.gridx=1; totals.add(lblSvcTotal, t);
        tr++;
        t.gridx=0; t.gridy=tr; totals.add(new JLabel("Products Total:"), t);
        t.gridx=1; totals.add(lblProdTotal, t);
        tr++;
        t.gridx=0; t.gridy=tr; totals.add(new JLabel("Grand Total:"), t);
        lblGrand.setFont(lblGrand.getFont().deriveFont(Font.BOLD));
        t.gridx=1; totals.add(lblGrand, t);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSavePrint = new JButton("Save & Print");
        JButton btnPrintOnly = new JButton("Print Only");
        JButton btnClear = new JButton("Clear");
        actions.add(btnClear);
        actions.add(btnPrintOnly);
        actions.add(btnSavePrint);

        bottom.add(totals, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        btnClear.addActionListener(e -> clearAll());
        btnPrintOnly.addActionListener(e -> printReceipt(-1));
        btnSavePrint.addActionListener(e -> onSaveAndPrint());
    }

    private JPanel buildServicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Services"));

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        svcCombo = new JComboBox<>();
        svcQtyField = new JTextField("1",4);
        svcPriceField = new JTextField("0",6);
        JButton add = new JButton("Add Service");
        addRow.add(new JLabel("Service:")); addRow.add(svcCombo);
        addRow.add(new JLabel("Qty:")); addRow.add(svcQtyField);
        addRow.add(new JLabel("Price:")); addRow.add(svcPriceField);
        addRow.add(add);
        panel.add(addRow, BorderLayout.NORTH);

        panel.add(new JScrollPane(svcTable), BorderLayout.CENTER);

        JButton remove = new JButton("Remove Selected");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(remove);
        panel.add(south, BorderLayout.SOUTH);

        svcCombo.addActionListener(e -> {
            Item it = (Item) svcCombo.getSelectedItem();
            if (it != null) svcPriceField.setText(it.price.toPlainString());
        });

        add.addActionListener(e -> {
            try {
                Item it = (Item) svcCombo.getSelectedItem();
                int q = Math.max(1, Integer.parseInt(svcQtyField.getText().trim()));
                BigDecimal p = new BigDecimal(svcPriceField.getText().trim());
                BigDecimal line = p.multiply(BigDecimal.valueOf(q));
                svcModel.addRow(new Object[]{it.id, it.label, q, p, line});
                recalcFromTables();
            } catch (Exception ex) { showErr(ex); }
        });

        remove.addActionListener(e -> {
            int row = svcTable.getSelectedRow();
            if (row >= 0) svcModel.removeRow(row);
            recalcFromTables();
        });

        return panel;
    }

    private JPanel buildProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Products"));

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prodCombo = new JComboBox<>();
        prodQtyField = new JTextField("1",4);
        prodPriceField = new JTextField("0",6);
        JButton add = new JButton("Add Product");
        addRow.add(new JLabel("Product:")); addRow.add(prodCombo);
        addRow.add(new JLabel("Qty:")); addRow.add(prodQtyField);
        addRow.add(new JLabel("Price:")); addRow.add(prodPriceField);
        addRow.add(add);
        panel.add(addRow, BorderLayout.NORTH);

        panel.add(new JScrollPane(prodTable), BorderLayout.CENTER);

        JButton remove = new JButton("Remove Selected");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(remove);
        panel.add(south, BorderLayout.SOUTH);

        prodCombo.addActionListener(e -> {
            Item it = (Item) prodCombo.getSelectedItem();
            if (it != null) prodPriceField.setText(it.price.toPlainString());
        });

        add.addActionListener(e -> {
            try {
                Item it = (Item) prodCombo.getSelectedItem();
                int q = Math.max(1, Integer.parseInt(prodQtyField.getText().trim()));
                BigDecimal p = new BigDecimal(prodPriceField.getText().trim());
                BigDecimal line = p.multiply(BigDecimal.valueOf(q));
                prodModel.addRow(new Object[]{it.id, it.label, q, p, line});
                recalcFromTables();
            } catch (Exception ex) { showErr(ex); }
        });

        remove.addActionListener(e -> {
            int row = prodTable.getSelectedRow();
            if (row >= 0) prodModel.removeRow(row);
            recalcFromTables();
        });

        return panel;
    }

    private void hookModelListeners() {
        svcModel.addTableModelListener(e -> {
            if (!recalcLock) recalcFromTables();
        });
        prodModel.addTableModelListener(e -> {
            if (!recalcLock) recalcFromTables();
        });
    }

    private void loadChoices() {
        svcCombo.removeAllItems();
        List<Service> services = serviceController.getAllServices();
        for (Service s : services) {
            svcCombo.addItem(new Item(s.getId(), s.getName(), s.getPrice()));
        }

        prodCombo.removeAllItems();
        List<Product> products = productController.getAllProducts();
        for (Product p : products) {
            prodCombo.addItem(new Item(p.getId(), p.getName(), p.getPrice()));
        }
    }

    private void recalcFromTables() {
        recalcLock = true;
        BigDecimal s = BigDecimal.ZERO;
        BigDecimal p = BigDecimal.ZERO;

        for (int i=0; i<svcModel.getRowCount(); i++) {
            int qty = parseInt(svcModel.getValueAt(i, 2), 1);
            BigDecimal price = parseDecimal(svcModel.getValueAt(i, 3), BigDecimal.ZERO);
            BigDecimal line = price.multiply(BigDecimal.valueOf(qty));
            svcModel.setValueAt(line, i, 4);
            s = s.add(line);
        }
        for (int i=0; i<prodModel.getRowCount(); i++) {
            int qty = parseInt(prodModel.getValueAt(i, 2), 1);
            BigDecimal price = parseDecimal(prodModel.getValueAt(i, 3), BigDecimal.ZERO);
            BigDecimal line = price.multiply(BigDecimal.valueOf(qty));
            prodModel.setValueAt(line, i, 4);
            p = p.add(line);
        }

        lblSvcTotal.setText(s.toPlainString());
        lblProdTotal.setText(p.toPlainString());
        lblGrand.setText(s.add(p).toPlainString());
        recalcLock = false;
    }

    private void clearAll() {
        tfCustomer.setText("");
        tfAge.setText("");
        tfPhone.setText("");
        tfDate.setText(LocalDate.now().toString());
        tfTime.setText(LocalTime.now().withSecond(0).withNano(0).toString());
        svcModel.setRowCount(0);
        prodModel.setRowCount(0);
        recalcFromTables();
    }

    private void onSaveAndPrint() {
        try {
            Booking b = new Booking();
            b.setCustomerName(tfCustomer.getText().trim().isEmpty() ? "Walk-in" : tfCustomer.getText().trim());
            b.setCustomerAge(tfAge.getText().trim().isEmpty() ? null : Integer.parseInt(tfAge.getText().trim()));
            b.setCustomerPhone(tfPhone.getText().trim());
            b.setBookingDate(LocalDate.parse(tfDate.getText().trim()));
            String t = tfTime.getText().trim();
            if (t.length()==5) t += ":00";
            b.setBookingTime(LocalTime.parse(t));
            b.setEmployeeId(null);
            b.setStatus("COMPLETED");
            b.setTotalAmount(new BigDecimal(lblGrand.getText()));
            b.setCreatedBy(currentUser != null ? currentUser.getId() : null);

            if (!bookingController.create(b)) {
                JOptionPane.showMessageDialog(this, "Failed to save booking.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int bookingId = b.getId();

            for (int i=0; i<svcModel.getRowCount(); i++) {
                int id = parseInt(svcModel.getValueAt(i, 0), 0);
                int qty = parseInt(svcModel.getValueAt(i, 2), 1);
                BigDecimal price = parseDecimal(svcModel.getValueAt(i, 3), BigDecimal.ZERO);
                bookingController.addServiceToBooking(bookingId, id, qty, price);
            }
            for (int i=0; i<prodModel.getRowCount(); i++) {
                int id = parseInt(prodModel.getValueAt(i, 0), 0);
                int qty = parseInt(prodModel.getValueAt(i, 2), 1);
                BigDecimal price = parseDecimal(prodModel.getValueAt(i, 3), BigDecimal.ZERO);
                bookingController.addProductToBooking(bookingId, id, qty, price);
            }

            bookingController.recomputeTotal(bookingId);

            // Save bill
            bookingController.createBill(bookingId, b.getTotalAmount(), currentUser.getId());

            printReceipt(bookingId);
            JOptionPane.showMessageDialog(this, "Saved as booking #" + bookingId, "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAll();
        } catch (Exception ex) { showErr(ex); }
    }

    private void printReceipt(int bookingId) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Saloon Bill ===\n");
        if (bookingId > -1) sb.append("Booking ID: ").append(bookingId).append("\n");
        sb.append("Date: ").append(tfDate.getText().trim()).append("  Time: ").append(tfTime.getText().trim()).append("\n");
        sb.append("Customer: ").append(tfCustomer.getText().trim().isEmpty() ? "Walk-in" : tfCustomer.getText().trim());
        if (!tfAge.getText().trim().isEmpty()) sb.append(" (").append(tfAge.getText().trim()).append(")");
        sb.append("\nPhone: ").append(tfPhone.getText().trim()).append("\n\n");

        if (svcModel.getRowCount()>0) {
            sb.append("-- Services --\n");
            for (int i=0;i<svcModel.getRowCount();i++) {
                String name = String.valueOf(svcModel.getValueAt(i,1));
                int qty = parseInt(svcModel.getValueAt(i,2), 1);
                BigDecimal price = parseDecimal(svcModel.getValueAt(i,3), BigDecimal.ZERO);
                BigDecimal line = parseDecimal(svcModel.getValueAt(i,4), BigDecimal.ZERO);
                sb.append(String.format("%dx %-20s @ %s = %s%n", qty, name, price.toPlainString(), line.toPlainString()));
            }
            sb.append("Services Total: ").append(lblSvcTotal.getText()).append("\n\n");
        }
        if (prodModel.getRowCount()>0) {
            sb.append("-- Products --\n");
            for (int i=0;i<prodModel.getRowCount();i++) {
                String name = String.valueOf(prodModel.getValueAt(i,1));
                int qty = parseInt(prodModel.getValueAt(i,2), 1);
                BigDecimal price = parseDecimal(prodModel.getValueAt(i,3), BigDecimal.ZERO);
                BigDecimal line = parseDecimal(prodModel.getValueAt(i,4), BigDecimal.ZERO);
                sb.append(String.format("%dx %-20s @ %s = %s%n", qty, name, price.toPlainString(), line.toPlainString()));
            }
            sb.append("Products Total: ").append(lblProdTotal.getText()).append("\n\n");
        }
        sb.append("GRAND TOTAL: ").append(lblGrand.getText()).append("\n");
        sb.append("====================\n");
        sb.append("Thank you!\n");

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Saloon Bill");
        job.setPrintable(new SimpleTextPrintable(sb.toString()));
        if (job.printDialog()) {
            try { job.print(); } catch (PrinterException e) { showErr(e); }
        }
    }

    private static class SimpleTextPrintable implements Printable {
        private final String text;
        public SimpleTextPrintable(String text) { this.text = text; }
        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) {
            if (pageIndex > 0) return NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            double x = pf.getImageableX(), y = pf.getImageableY();
            int lh = g.getFontMetrics().getHeight();
            int maxW = (int) pf.getImageableWidth();
            int yy = 0;
            for (String line : text.split("\n")) {
                while (line.length() * 6 > maxW) {
                    int cut = Math.min(line.length(), Math.max(1, maxW / 6));
                    g2.drawString(line.substring(0, cut), (int)x, (int)(y + yy));
                    yy += lh;
                    line = line.substring(cut);
                }
                g2.drawString(line, (int)x, (int)(y + yy));
                yy += lh;
            }
            return PAGE_EXISTS;
        }
    }

    private static class Item {
        final int id;
        final String label;
        final BigDecimal price;
        Item(int id, String label, BigDecimal price){ this.id=id; this.label=label; this.price=price; }
        @Override public String toString(){ return label; }
    }

    private int parseInt(Object val, int def) {
        try { if (val instanceof Integer i) return i; return Integer.parseInt(String.valueOf(val)); }
        catch (Exception e) { return def; }
    }
    private BigDecimal parseDecimal(Object val, BigDecimal def) {
        try { if (val instanceof BigDecimal b) return b; return new BigDecimal(String.valueOf(val)); }
        catch (Exception e) { return def; }
    }

    private void showErr(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

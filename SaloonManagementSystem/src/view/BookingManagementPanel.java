package view;

import controller.BookingController;
import model.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BookingManagementPanel extends JPanel {
    private final BookingController controller = new BookingController();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Customer","Phone","Date","Time","EmployeeID","Status","Total","CreatedBy"}, 0
    );
    private final JTable table = new JTable(model);

    public BookingManagementPanel() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton recompute = new JButton("Recompute Total");
        top.add(refresh); top.add(add); top.add(edit); top.add(delete); top.add(recompute);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh.addActionListener(e -> load());
        add.addActionListener(e -> addDialog());
        edit.addActionListener(e -> editDialog());
        delete.addActionListener(e -> deleteSelected());
        recompute.addActionListener(e -> recompute());

        load();
    }

    private void load() {
        model.setRowCount(0);
        List<Booking> list = controller.getAll();
        for (Booking b : list) {
            model.addRow(new Object[]{
                    b.getId(), b.getCustomerName(), b.getCustomerPhone(), b.getBookingDate(), b.getBookingTime(),
                    b.getEmployeeId(), b.getStatus(), b.getTotalAmount(), b.getCreatedBy()
            });
        }
    }

    private void addDialog() {
        JTextField cust = new JTextField();
        JTextField phone = new JTextField();
        JTextField date = new JTextField(LocalDate.now().toString());
        JTextField time = new JTextField(LocalTime.now().withSecond(0).withNano(0).toString());
        JTextField emp = new JTextField();
        JComboBox<String> status = new JComboBox<>(new String[]{"PENDING","CONFIRMED","COMPLETED","CANCELLED"});
        JTextField total = new JTextField("0");
        JTextField createdBy = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Customer:", cust, "Phone:", phone, "Date (YYYY-MM-DD):", date, "Time (HH:MM):", time,
                        "Employee ID (optional):", emp, "Status:", status, "Total:", total, "Created By (user id):", createdBy},
                "Add Booking", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                Booking b = new Booking();
                b.setCustomerName(cust.getText().trim());
                b.setCustomerPhone(phone.getText().trim());
                b.setBookingDate(LocalDate.parse(date.getText().trim()));
                b.setBookingTime(LocalTime.parse(time.getText().trim()+":00".substring(0,3))); // tolerate HH:MM
                b.setEmployeeId(emp.getText().isEmpty() ? null : Integer.parseInt(emp.getText().trim()));
                b.setStatus(status.getSelectedItem().toString());
                b.setTotalAmount(new BigDecimal(total.getText().trim()));
                b.setCreatedBy(createdBy.getText().isEmpty() ? null : Integer.parseInt(createdBy.getText().trim()));
                if (controller.create(b)) load();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void editDialog() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row,0);
        JTextField cust = new JTextField(model.getValueAt(row,1).toString());
        JTextField phone = new JTextField(model.getValueAt(row,2).toString());
        JTextField date = new JTextField(String.valueOf(model.getValueAt(row,3)));
        JTextField time = new JTextField(String.valueOf(model.getValueAt(row,4)));
        JTextField emp = new JTextField(String.valueOf(model.getValueAt(row,5)));
        JComboBox<String> status = new JComboBox<>(new String[]{"PENDING","CONFIRMED","COMPLETED","CANCELLED"});
        status.setSelectedItem(model.getValueAt(row,6));
        JTextField total = new JTextField(String.valueOf(model.getValueAt(row,7)));
        JTextField createdBy = new JTextField(String.valueOf(model.getValueAt(row,8)));

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Customer:", cust, "Phone:", phone, "Date (YYYY-MM-DD):", date, "Time (HH:MM):", time,
                        "Employee ID (optional):", emp, "Status:", status, "Total:", total, "Created By (user id):", createdBy},
                "Edit Booking", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                Booking b = new Booking();
                b.setId(id);
                b.setCustomerName(cust.getText().trim());
                b.setCustomerPhone(phone.getText().trim());
                b.setBookingDate(LocalDate.parse(date.getText().trim()));
                b.setBookingTime(LocalTime.parse(time.getText().trim()+":00".substring(0,3)));
                b.setEmployeeId(emp.getText().isEmpty() ? null : Integer.parseInt(emp.getText().trim()));
                b.setStatus(status.getSelectedItem().toString());
                b.setTotalAmount(new BigDecimal(total.getText().trim()));
                b.setCreatedBy(createdBy.getText().isEmpty() ? null : Integer.parseInt(createdBy.getText().trim()));
                if (controller.update(b)) load();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row,0);
        if (JOptionPane.showConfirmDialog(this, "Delete booking "+id+"?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            if (controller.delete(id)) load();
        }
    }

    private void recompute() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) model.getValueAt(row,0);
        controller.recomputeTotal(id);
        load();
    }
}

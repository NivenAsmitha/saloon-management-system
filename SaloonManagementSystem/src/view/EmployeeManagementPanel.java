package view;

import controller.EmployeeController;
import controller.UserController;
import model.Employee;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private final EmployeeController empController = new EmployeeController();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Phone","Email","Address","Salary","Hire Date","User ID"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Make table read-only
        }
    };

    private final JTable table = new JTable(model);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private final JTextField searchField = new JTextField(20);

    public EmployeeManagementPanel() {
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
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // Case-insensitive
        }
    }

    private void load() {
        model.setRowCount(0);
        List<Employee> list = empController.getAll();
        for (Employee e : list) {
            model.addRow(new Object[]{
                    e.getId(), e.getName(), e.getPhone(), e.getEmail(), e.getAddress(),
                    e.getSalary(), e.getHireDate(), e.getUserId()
            });
        }
    }

    /** Add employee + optional linked login */
    private void addDialog() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();
        JTextField address = new JTextField();
        JTextField salary = new JTextField("0");
        JTextField hire = new JTextField(LocalDate.now().toString());

        JTextField username = new JTextField();
        JPasswordField tempPass = new JPasswordField();

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{
                        "Name:", name, "Phone:", phone, "Email:", email, "Address:", address,
                        "Salary:", salary, "Hire Date (YYYY-MM-DD):", hire,
                        "Username (optional):", username,
                        "Temp Password (optional):", tempPass
                },
                "Add Employee", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Integer userId = null;
                String uname = username.getText().trim();
                String tpass = new String(tempPass.getPassword());

                if (!uname.isEmpty()) {
                    UserController uc = new UserController();
                    if (tpass.isEmpty()) {
                        tpass = "Pass@123";
                    }
                    int newUserId = uc.createEmployeeUser(uname, tpass);
                    if (newUserId <= 0) {
                        JOptionPane.showMessageDialog(this, "Failed to create user (username may exist).", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    userId = newUserId;

                    JOptionPane.showMessageDialog(this,
                            "Employee user created.\n\nUsername: " + uname +
                                    "\nTemporary Password: " + tpass +
                                    "\n(They must change it at first login.)",
                            "User Created", JOptionPane.INFORMATION_MESSAGE);
                }

                Employee emp = new Employee();
                emp.setName(name.getText().trim());
                emp.setPhone(phone.getText().trim());
                emp.setEmail(email.getText().trim());
                emp.setAddress(address.getText().trim());
                emp.setSalary(new BigDecimal(salary.getText().trim()));
                emp.setHireDate(LocalDate.parse(hire.getText().trim()));
                emp.setUserId(userId);

                if (empController.create(emp)) {
                    load();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) model.getValueAt(row, 0);

        JTextField name = new JTextField(val(row, 1));
        JTextField phone = new JTextField(val(row, 2));
        JTextField email = new JTextField(val(row, 3));
        JTextField address = new JTextField(val(row, 4));
        JTextField salary = new JTextField(val(row, 5));
        JTextField hire = new JTextField(String.valueOf(model.getValueAt(row, 6)));
        JTextField userIdField = new JTextField(String.valueOf(model.getValueAt(row, 7)));

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{
                        "Name:", name, "Phone:", phone, "Email:", email, "Address:", address,
                        "Salary:", salary, "Hire Date (YYYY-MM-DD):", hire,
                        "User ID (optional):", userIdField
                },
                "Edit Employee", JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            try {
                Employee emp = new Employee();
                emp.setId(id);
                emp.setName(name.getText().trim());
                emp.setPhone(phone.getText().trim());
                emp.setEmail(email.getText().trim());
                emp.setAddress(address.getText().trim());
                emp.setSalary(new BigDecimal(salary.getText().trim()));
                emp.setHireDate(LocalDate.parse(hire.getText().trim()));
                String uidText = userIdField.getText().trim();
                emp.setUserId(uidText.isEmpty() ? null : Integer.parseInt(uidText));

                if (empController.update(emp)) {
                    load();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update employee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        int row = table.convertRowIndexToModel(viewRow);
        int id = (int) model.getValueAt(row, 0);

        if (JOptionPane.showConfirmDialog(this, "Delete employee " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {
            if (empController.delete(id)) load();
        }
    }

    private String val(int row, int col) {
        Object o = model.getValueAt(row, col);
        return o == null ? "" : o.toString();
    }
}

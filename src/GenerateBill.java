import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class GenerateBill extends JFrame {
    private JTable billsTable, itemsTable;
    private JComboBox<PatientItem> patientCombo;
    private JButton generateBtn, viewBtn, payBtn;
    private JTextField amountField;
    private BillManager billManager;
    private DecimalFormat currencyFormat = new DecimalFormat("₹#,##0.00");

    public GenerateBill() {
        setTitle("Bill Generation System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        billManager = new BillManager();

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("BILL GENERATION SYSTEM");
        titleLabel.setBounds(400, 20, 400, 30);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Patient Selection
        JLabel patientLabel = new JLabel("Select Patient:");
        patientLabel.setBounds(50, 70, 150, 25);
        patientLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        patientLabel.setForeground(Color.WHITE);
        panel.add(patientLabel);

        patientCombo = new JComboBox<>();
        patientCombo.setBounds(200, 70, 300, 25);
        loadPatients();
        panel.add(patientCombo);

        // Buttons
        viewBtn = new JButton("View Bills");
        viewBtn.setBounds(550, 70, 150, 25);
        styleButton(viewBtn, new Color(59, 89, 182));
        viewBtn.addActionListener(e -> loadBills());
        panel.add(viewBtn);

        generateBtn = new JButton("Generate Bill");
        generateBtn.setBounds(720, 70, 150, 25);
        styleButton(generateBtn, new Color(50, 150, 50));
        generateBtn.addActionListener(e -> showGenerateBillDialog());
        panel.add(generateBtn);

        // Bills Table
        String[] billColumns = {"Bill ID", "Issue Date", "Due Date", "Total Amount", "Paid", "Status"};
        DefaultTableModel billModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return Integer.class; // Bill ID
                    case 1: return Date.class;   // Issue Date
                    case 2: return Date.class;    // Due Date
                    case 3: return Double.class;  // Total Amount
                    case 4: return Double.class;  // Paid
                    case 5: return String.class;  // Status
                    default: return Object.class;
                }
            }
        };

        billsTable = new JTable(billModel);
        billsTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        billsTable.setRowHeight(25);
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billsTable.getSelectionModel().addListSelectionListener(e -> showBillDetails());

        // Set custom renderer for currency columns
        billsTable.setDefaultRenderer(Double.class, new CurrencyRenderer());

        JScrollPane billsScroll = new JScrollPane(billsTable);
        billsScroll.setBounds(50, 120, 1100, 200);
        panel.add(billsScroll);

        // Bill Items Table
        String[] itemColumns = {"Description", "Quantity", "Unit Price", "Total"};
        DefaultTableModel itemModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return String.class;  // Description
                    case 1: return Integer.class; // Quantity
                    case 2: return Double.class;  // Unit Price
                    case 3: return Double.class;  // Total
                    default: return Object.class;
                }
            }
        };

        itemsTable = new JTable(itemModel);
        itemsTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        itemsTable.setRowHeight(25);

        // Set custom renderer for currency columns
        itemsTable.setDefaultRenderer(Double.class, new CurrencyRenderer());

        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setBounds(50, 350, 800, 200);
        panel.add(itemsScroll);

        // Payment Section
        JLabel amountLabel = new JLabel("Payment Amount:");
        amountLabel.setBounds(900, 350, 150, 25);
        amountLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        amountLabel.setForeground(Color.WHITE);
        panel.add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(1050, 350, 100, 25);
        panel.add(amountField);

        payBtn = new JButton("Record Payment");
        payBtn.setBounds(900, 400, 250, 30);
        styleButton(payBtn, new Color(150, 50, 150));
        payBtn.addActionListener(e -> recordPayment());
        panel.add(payBtn);

        setVisible(true);
    }

    // Currency renderer for formatting numbers
    private class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = currencyFormat.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
        }
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
    }

    private void loadPatients() {
        try {
            Conn c = new Conn();
            String query = "SELECT patient_id, Name FROM Patient_Info ORDER BY Name";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    patientCombo.addItem(new PatientItem(
                            rs.getInt("patient_id"),
                            rs.getString("Name"),
                            0
                    ));
                }
            }
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void loadBills() {
        PatientItem patient = (PatientItem) patientCombo.getSelectedItem();
        if (patient == null) {
            showError("Please select a patient");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) billsTable.getModel();
        model.setRowCount(0);

        List<Bill> bills = billManager.getBillsByPatient(patient.getPatientId());
        for (Bill bill : bills) {
            model.addRow(new Object[]{
                    bill.getBillId(),
                    bill.getIssueDate(),
                    bill.getDueDate(),
                    bill.getTotalAmount(),  // Store as Double, renderer will format
                    bill.getPaidAmount(),    // Store as Double, renderer will format
                    bill.getStatus()
            });
        }
    }

    private void showBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int billId = (int) billsTable.getValueAt(selectedRow, 0);
        PatientItem patient = (PatientItem) patientCombo.getSelectedItem();

        List<Bill> bills = billManager.getBillsByPatient(patient.getPatientId());
        Bill selectedBill = bills.stream()
                .filter(b -> b.getBillId() == billId)
                .findFirst()
                .orElse(null);

        if (selectedBill != null) {
            DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
            model.setRowCount(0);

            for (BillItem item : selectedBill.getItems()) {
                model.addRow(new Object[]{
                        item.getDescription(),
                        item.getQuantity(),
                        item.getUnitPrice(),  // Store as Double, renderer will format
                        item.getTotal()       // Store as Double, renderer will format
                });
            }
        }
    }

    private void showGenerateBillDialog() {
        PatientItem patient = (PatientItem) patientCombo.getSelectedItem();
        if (patient == null) {
            showError("Please select a patient first");
            return;
        }

        JDialog dialog = new JDialog(this, "Generate New Bill", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());

        String[] columns = {"Description", "Quantity", "Unit Price", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3; // Only action column is not editable
            }
        };

        JTable itemsTable = new JTable(model);
        itemsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        itemsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), itemsTable));
        model.addRow(new Object[]{"", 1, 0.0, "Delete"});

        itemsTable.setDefaultRenderer(Double.class, new CurrencyRenderer());

        itemsTable.setDefaultEditor(Integer.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    Integer.parseInt(getCellEditorValue().toString());
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid integer for quantity",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });

        itemsTable.setDefaultEditor(Double.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    Double.parseDouble(getCellEditorValue().toString());
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid number for price",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });

        itemsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        itemsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), itemsTable));

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        model.addRow(new Object[]{"", 1, 0.0, "Delete"});

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JButton addBtn = new JButton("Add New Row");
        addBtn.addActionListener(e -> model.addRow(new Object[]{"", 1, 0.0, "Delete"}));
        inputPanel.add(addBtn);

        JPanel datePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField issueDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        JTextField dueDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        datePanel.add(new JLabel("Issue Date (YYYY-MM-DD):"));
        datePanel.add(issueDateField);
        datePanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        datePanel.add(dueDateField);

        JLabel totalLabel = new JLabel("Total: ₹0.00", JLabel.RIGHT);
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        model.addTableModelListener(e -> {
            double total = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    int qty = (model.getValueAt(i, 1) != null) ? Integer.parseInt(model.getValueAt(i, 1).toString()) : 0;
                    double price = (model.getValueAt(i, 2) != null) ? Double.parseDouble(model.getValueAt(i, 2).toString()) : 0;
                    total += qty * price;
                } catch (Exception ex) {
                    // Ignore invalid rows for total calculation
                }
            }
            totalLabel.setText(String.format("Total: ₹%.2f", total));
        });

        JButton saveBtn = new JButton("Generate Bill");
        saveBtn.addActionListener(e -> {
            try {
                Date issueDate = Date.valueOf(issueDateField.getText());
                Date dueDate = Date.valueOf(dueDateField.getText());

                Bill bill = new Bill(0, patient.getPatientId(), null, null,
                        issueDate, dueDate, 0, 0, "Pending");

                double total = 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    String desc = (String) model.getValueAt(i, 0);
                    if (desc == null || desc.trim().isEmpty()) continue;

                    int qty = 1;
                    double price = 0;

                    try {
                        qty = (model.getValueAt(i, 1) != null) ? Integer.parseInt(model.getValueAt(i, 1).toString()) : 1;
                        price = (model.getValueAt(i, 2) != null) ? Double.parseDouble(model.getValueAt(i, 2).toString()) : 0;
                    } catch (NumberFormatException ex) {
                        showError("Invalid quantity or price in row " + (i+1));
                        return;
                    }

                    total += qty * price;
                    bill.addItem(new BillItem(0, 0, desc, qty, price));
                }

                if (bill.getItems().isEmpty()) {
                    showError("Please add at least one bill item");
                    return;
                }

                bill.setTotalAmount(total);

                if (billManager.createBill(bill)) {
                    JOptionPane.showMessageDialog(dialog, "Bill generated successfully!");
                    loadBills();
                    dialog.dispose();
                } else {
                    showError("Failed to generate bill");
                }
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.CENTER);
        bottomPanel.add(saveBtn, BorderLayout.EAST);

        dialog.add(datePanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(inputPanel, BorderLayout.SOUTH);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void recordPayment() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a bill first");
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                showError("Payment amount must be positive");
                return;
            }

            int billId = (int) billsTable.getValueAt(selectedRow, 0);
            PatientItem patient = (PatientItem) patientCombo.getSelectedItem();

            JOptionPane.showMessageDialog(this,
                    String.format("Payment of ₹%.2f recorded for Bill #%d", amount, billId));

            amountField.setText("");
            loadBills();
        } catch (NumberFormatException e) {
            showError("Please enter a valid payment amount");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new GenerateBill();
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean isPushed;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            // Perform the delete action
            ((DefaultTableModel)table.getModel()).removeRow(table.getEditingRow());
        }
        isPushed = false;
        return label;
    }
}
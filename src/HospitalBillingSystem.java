import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

public class HospitalBillingSystem extends JFrame {
    private JTextField idTypeField, idNumberField, nameField, genderField, ageField, depositField, contactField;
    private JTextField treatmentField, descriptionField, unitPriceField, quantityField;
    private JLabel totalLabel, paidLabel, returnLabel;
    private JButton calculateBtn, saveBtn, printBtn, clearBtn, addBtn;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private Conn conn;
    private JComboBox<Patient__Item> patientCombo;
    private DecimalFormat currencyFormat = new DecimalFormat("₹#,##0.00");

    public HospitalBillingSystem() {
        // Set look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Hospital Billing System");
        setSize(1100, 750); // Increased size slightly
        setLocationRelativeTo(null);
        setVisible(true);
        // Initialize database connection
        conn = new Conn();

        // Main panel with scroll pane
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        add(mainPanel);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("HOSPITAL BILLING SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Center panel with scroll
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 240, 240));

        // Patient Panel - now with scroll pane
        JPanel patientPanel = new JPanel();
        patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));
        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient Information"));
        patientPanel.setBackground(Color.WHITE);
        patientPanel.setPreferredSize(new Dimension(800, 300));

        patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadPatients();
        patientCombo.addActionListener(e -> loadPatientDetails());
        patientPanel.add(patientCombo);
        patientPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Patient details in a grid
        JPanel patientDetailsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        patientDetailsPanel.setBackground(Color.WHITE);

        addLabelAndField(patientDetailsPanel, "ID Number:", idNumberField = createNonEditableField());
        addLabelAndField(patientDetailsPanel, "Name:", nameField = createNonEditableField());
        addLabelAndField(patientDetailsPanel, "Gender:", genderField = createNonEditableField());
        addLabelAndField(patientDetailsPanel, "Age:", ageField = createNonEditableField());
        addLabelAndField(patientDetailsPanel, "Contact:", contactField = createNonEditableField());

        patientPanel.add(patientDetailsPanel);
        centerPanel.add(patientPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Treatment Panel
        JPanel treatmentPanel = new JPanel(new BorderLayout());
        treatmentPanel.setBorder(BorderFactory.createTitledBorder("Treatment Details"));
        treatmentPanel.setBackground(Color.WHITE);

        JPanel treatmentFieldsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        treatmentFieldsPanel.setBackground(Color.WHITE);

        addLabelAndField(treatmentFieldsPanel, "Treatment*:", treatmentField = new JTextField());
        addLabelAndField(treatmentFieldsPanel, "Description*:", descriptionField = new JTextField());
        addLabelAndField(treatmentFieldsPanel, "Unit Price*:", unitPriceField = new JTextField());
        addLabelAndField(treatmentFieldsPanel, "Quantity:", quantityField = new JTextField("1"));

        treatmentPanel.add(treatmentFieldsPanel, BorderLayout.CENTER);

        addBtn = new JButton("Add to Bill");
        styleButton(addBtn, new Color(34, 139, 34));
        addBtn.addActionListener(e -> addTreatmentToBill());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addBtn);
        treatmentPanel.add(buttonPanel, BorderLayout.SOUTH);

        centerPanel.add(treatmentPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create a wrapper panel for the table and calculations
        JPanel tableCalcPanel = new JPanel(new BorderLayout(10, 10));

        // Items Table
        String[] columns = {"Description", "Quantity", "Unit Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return String.class;
                    case 1: return Integer.class;
                    case 2: return Double.class;
                    case 3: return Double.class;
                    default: return Object.class;
                }
            }
        };

        itemsTable = new JTable(tableModel);
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemsTable.setRowHeight(25);
        itemsTable.setDefaultRenderer(Double.class, new CurrencyRenderer());

        JScrollPane tableScroll = new JScrollPane(itemsTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Bill Items"));
        tableScroll.setPreferredSize(new Dimension(600, 200));
        tableCalcPanel.add(tableScroll, BorderLayout.CENTER);

        // Calculation Panel
        JPanel calcPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        calcPanel.setBorder(BorderFactory.createTitledBorder("Payment Summary"));
        calcPanel.setBackground(Color.WHITE);

        addCalculationLabel(calcPanel, "Total:", totalLabel = new JLabel("0.00"));
        addCalculationLabel(calcPanel, "Paid Amount:", paidLabel = new JLabel("0.00"));
        addCalculationLabel(calcPanel, "Return Amount:", returnLabel = new JLabel("0.00"));

        tableCalcPanel.add(calcPanel, BorderLayout.SOUTH);
        centerPanel.add(tableCalcPanel);

        // Wrap center panel in scroll pane
        JScrollPane centerScroll = new JScrollPane(centerPanel);
        centerScroll.setBorder(null);
        mainPanel.add(centerScroll, BorderLayout.CENTER);

        // Action buttons panel
        JPanel buttonActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonActionPanel.setBackground(new Color(240, 240, 240));

        calculateBtn = new JButton("Calculate Payment");
        styleButton(calculateBtn, new Color(70, 130, 180));
        calculateBtn.addActionListener(new CalculateListener());
        buttonActionPanel.add(calculateBtn);

        saveBtn = new JButton("Save Bill");
        styleButton(saveBtn, new Color(34, 139, 34));
        saveBtn.addActionListener(new SaveListener());
        buttonActionPanel.add(saveBtn);

        printBtn = new JButton("Print Bill");
        styleButton(printBtn, new Color(218, 165, 32));
        printBtn.addActionListener(new PrintListener());
        buttonActionPanel.add(printBtn);

        clearBtn = new JButton("Clear All");
        styleButton(clearBtn, new Color(178, 34, 34));
        clearBtn.addActionListener(new ClearListener());
        buttonActionPanel.add(clearBtn);

        mainPanel.add(buttonActionPanel, BorderLayout.SOUTH);

        JButton viewBillsBtn = new JButton("View All Bills");
        styleButton(viewBillsBtn, new Color(75, 0, 130)); // Purple color
        viewBillsBtn.addActionListener(new ViewBillsListener());
        buttonActionPanel.add(viewBillsBtn);
    }

    private JTextField createNonEditableField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        return field;
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(label);

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(textField);
    }

    private void addCalculationLabel(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(label);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(valueLabel);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker()),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setOpaque(true);
    }

    private void loadPatients() {
        try {
            String query = "SELECT patient_id, Name FROM Patient_Info ORDER BY Name";
            try (Statement stmt = conn.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    patientCombo.addItem(new Patient__Item(
                            rs.getInt("patient_id"),
                            rs.getString("Name")
                    ));
                }
            }
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void loadPatientDetails() {
        Patient__Item selectedPatient = (Patient__Item) patientCombo.getSelectedItem();
        if (selectedPatient != null) {
            try {
                String query = "SELECT ID_Number, Name, Gender, Age, Contact " +
                        "FROM Patient_Info WHERE patient_id = ?";
                PreparedStatement stmt = conn.getConnection().prepareStatement(query);
                stmt.setInt(1, selectedPatient.getPatientId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    idNumberField.setText(rs.getString("ID_Number"));
                    nameField.setText(rs.getString("Name"));
                    genderField.setText(rs.getString("Gender"));
                    ageField.setText(rs.getString("Age"));
                    contactField.setText(rs.getString("Contact"));
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }


    private void addTreatmentToBill() {
        try {
            String treatment = treatmentField.getText().trim();
            String description = descriptionField.getText().trim();

            if (treatment.isEmpty() || description.isEmpty()) {
                showError("Please enter both treatment and description");
                return;
            }

            double unitPrice = Double.parseDouble(unitPriceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            if (unitPrice <= 0 || quantity <= 0) {
                showError("Price and quantity must be positive values");
                return;
            }

            double total = unitPrice * quantity;

            tableModel.addRow(new Object[]{
                    description,
                    quantity,
                    unitPrice,
                    total
            });

            // Clear fields
            descriptionField.setText("");
            unitPriceField.setText("");
            quantityField.setText("1");

            // Update total
            updateTotals();
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for price and quantity");
        }
    }

    private void updateTotals() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (Double) tableModel.getValueAt(i, 3);
        }
        totalLabel.setText(currencyFormat.format(total));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Inner classes for event listeners
    private class CalculateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                double paid = Double.parseDouble(JOptionPane.showInputDialog(
                        HospitalBillingSystem.this,
                        "Enter paid amount:",
                        totalLabel.getText()));

                double total = Double.parseDouble(totalLabel.getText().replaceAll("[^\\d.]", ""));
                double returnAmount = paid - total;

                paidLabel.setText(currencyFormat.format(paid));
                returnLabel.setText(currencyFormat.format(returnAmount));
            } catch (NumberFormatException ex) {
                showError("Please enter a valid number");
            }
        }
    }

    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (tableModel.getRowCount() == 0) {
                showError("Please add at least one treatment to the bill");
                return;
            }

            Patient__Item selectedPatient = (Patient__Item) patientCombo.getSelectedItem();
            if (selectedPatient == null) {
                showError("Please select a patient first");
                return;
            }

            try {
                conn.getConnection().setAutoCommit(false);

                // Insert bill
                String billQuery = "INSERT INTO Bills (patient_id, issue_date, due_date, total_amount, paid_amount, status) " +
                        "VALUES (?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), ?, ?, ?)";
                PreparedStatement billStmt = conn.getConnection().prepareStatement(billQuery, Statement.RETURN_GENERATED_KEYS);

                billStmt.setInt(1, selectedPatient.getPatientId());

                double total = Double.parseDouble(totalLabel.getText().replaceAll("[^\\d.]", ""));
                double paid = Double.parseDouble(paidLabel.getText().replaceAll("[^\\d.]", ""));
                String status = paid >= total ? "Paid" : (paid > 0 ? "Partially Paid" : "Pending");

                billStmt.setDouble(2, total);
                billStmt.setDouble(3, paid);
                billStmt.setString(4, status);

                billStmt.executeUpdate();

                // Get generated bill ID
                ResultSet rs = billStmt.getGeneratedKeys();
                int billId = 0;
                if (rs.next()) {
                    billId = rs.getInt(1);
                }

                // Insert bill items
                String itemQuery = "INSERT INTO Bill_Items (bill_id, description, quantity, unit_price) " +
                        "VALUES (?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.getConnection().prepareStatement(itemQuery);

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    itemStmt.setInt(1, billId);
                    itemStmt.setString(2, (String) tableModel.getValueAt(i, 0));
                    itemStmt.setInt(3, (Integer) tableModel.getValueAt(i, 1));
                    itemStmt.setDouble(4, (Double) tableModel.getValueAt(i, 2));
                    itemStmt.addBatch();
                }

                itemStmt.executeBatch();

                conn.getConnection().commit();

                JOptionPane.showMessageDialog(HospitalBillingSystem.this,
                        "Bill saved successfully with ID: " + billId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                try {
                    conn.getConnection().rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                showError("Database error: " + ex.getMessage());
            } finally {
                try {
                    conn.getConnection().setAutoCommit(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private class PrintListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (tableModel.getRowCount() == 0) {
                showError("No items in bill to print");
                return;
            }

            // Create bill content
            StringBuilder bill = new StringBuilder();
            bill.append("HOSPITAL BILL\n");
            bill.append("====================\n");
            bill.append("Patient: ").append(patientCombo.getSelectedItem()).append("\n");
            bill.append("Contact: ").append(contactField.getText()).append("\n");
            bill.append("Gender: ").append(genderField.getText()).append("\n");
            bill.append("Age: ").append(ageField.getText()).append("\n\n");

            bill.append("TREATMENTS\n");
            bill.append("--------------------------------------------------\n");
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                bill.append(String.format("%-30s %3d x %8.2f = %8.2f\n",
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1),
                        tableModel.getValueAt(i, 2),
                        tableModel.getValueAt(i, 3)));
            }

            bill.append("\n");
            bill.append("Total: ").append(totalLabel.getText()).append("\n");
            bill.append("Paid: ").append(paidLabel.getText()).append("\n");
            bill.append("Return: ").append(returnLabel.getText()).append("\n");

            // Display bill
            JTextArea textArea = new JTextArea(bill.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(HospitalBillingSystem.this,
                    new JScrollPane(textArea),
                    "Bill Receipt", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Clear all fields
            patientCombo.setSelectedIndex(-1);
            contactField.setText("");
            genderField.setText("");
            ageField.setText("");

            treatmentField.setText("");
            descriptionField.setText("");
            unitPriceField.setText("");
            quantityField.setText("1");

            // Clear table
            tableModel.setRowCount(0);

            // Reset calculations
            totalLabel.setText("0.00");
            paidLabel.setText("0.00");
            returnLabel.setText("0.00");
        }
    }

    private class CurrencyRenderer extends DefaultTableCellRenderer {
        public CurrencyRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value instanceof Number) {
                value = currencyFormat.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
        }
    }
    class ViewBillsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String query = "SELECT b.bill_id, p.Name, b.issue_date, b.total_amount, b.paid_amount, b.status " +
                        "FROM Bills b JOIN Patient_Info p ON b.patient_id = p.patient_id " +
                        "ORDER BY b.issue_date DESC";

                ResultSet rs = conn.getConnection().createStatement().executeQuery(query);

                DefaultTableModel model = new DefaultTableModel(
                        new Object[]{"Bill ID", "Patient Name", "Issue Date", "Total", "Paid", "Status", "Balance"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                while (rs.next()) {
                    double total = rs.getDouble("total_amount");
                    double paid = rs.getDouble("paid_amount");
                    double balance = total - paid;

                    model.addRow(new Object[]{
                            rs.getInt("bill_id"),
                            rs.getString("Name"),
                            rs.getDate("issue_date"),
                            currencyFormat.format(total),
                            currencyFormat.format(paid),
                            rs.getString("status"),
                            currencyFormat.format(balance)
                    });
                }

                JTable billsTable = new JTable(model);
                billsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                billsTable.setRowHeight(25);

                // Add double-click listener to make payments
                billsTable.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            int row = billsTable.getSelectedRow();
                            int billId = (int) billsTable.getValueAt(row, 0);
                            double total = Double.parseDouble(billsTable.getValueAt(row, 3).toString().replaceAll("[^\\d.]", ""));
                            double paid = Double.parseDouble(billsTable.getValueAt(row, 4).toString().replaceAll("[^\\d.]", ""));
                            double balance = total - paid;

                            if (balance > 0) {
                                makeAdditionalPayment(billId, balance);
                            } else {
                                JOptionPane.showMessageDialog(HospitalBillingSystem.this,
                                        "This bill is already fully paid",
                                        "Payment Info", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                });

                JScrollPane scrollPane = new JScrollPane(billsTable);
                scrollPane.setPreferredSize(new Dimension(900, 400));

                JOptionPane.showMessageDialog(HospitalBillingSystem.this, scrollPane,
                        "All Bills - Double click to make payment", JOptionPane.PLAIN_MESSAGE);

            } catch (SQLException ex) {
                showError("Error loading bills: " + ex.getMessage());
            }
        }
    }

    private void makeAdditionalPayment(int billId, double balanceDue) {
        try {
            // Get payment amount
            String input = JOptionPane.showInputDialog(
                    this,
                    "Balance Due: " + currencyFormat.format(balanceDue) +
                            "\nEnter payment amount:",
                    "Additional Payment",
                    JOptionPane.PLAIN_MESSAGE);

            if (input == null || input.trim().isEmpty()) return;

            double payment = Double.parseDouble(input);

            if (payment <= 0) {
                showError("Payment amount must be positive");
                return;
            }

            if (payment > balanceDue) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Payment amount exceeds balance due. Accept overpayment?",
                        "Confirm Overpayment",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) return;
            }

            // Update database
            conn.getConnection().setAutoCommit(false);

            // Get current paid amount
            String getQuery = "SELECT paid_amount, total_amount FROM Bills WHERE bill_id = ?";
            PreparedStatement getStmt = conn.getConnection().prepareStatement(getQuery);
            getStmt.setInt(1, billId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                double currentPaid = rs.getDouble("paid_amount");
                double totalAmount = rs.getDouble("total_amount");
                double newPaid = currentPaid + payment;
                String newStatus = newPaid >= totalAmount ? "Paid" : "Partially Paid";

                // Update payment
                String updateQuery = "UPDATE Bills SET paid_amount = ?, status = ? WHERE bill_id = ?";
                PreparedStatement updateStmt = conn.getConnection().prepareStatement(updateQuery);
                updateStmt.setDouble(1, newPaid);
                updateStmt.setString(2, newStatus);
                updateStmt.setInt(3, billId);
                updateStmt.executeUpdate();

                conn.getConnection().commit();

                JOptionPane.showMessageDialog(this,
                        "Payment of " + currencyFormat.format(payment) + " applied to Bill ID: " + billId +
                                "\nNew paid amount: " + currencyFormat.format(newPaid) +
                                "\nStatus: " + newStatus,
                        "Payment Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid number");
        } catch (SQLException ex) {
            try {
                conn.getConnection().rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            showError("Database error: " + ex.getMessage());
        } finally {
            try {
                conn.getConnection().setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Set system properties before creating the UI
        System.setProperty("swing.aatext", "true");
        System.setProperty("awt.useSystemAAFontSettings", "lcd");

        SwingUtilities.invokeLater(() -> {
            try {
                // Force metal look and feel if system L&F has issues
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                // Create and show the UI
                HospitalBillingSystem system = new HospitalBillingSystem();
                system.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}


class Patient__Item {
    private int patientId;
    private String name;

    public Patient__Item(int patientId, String name) {
        this.patientId = patientId;
        this.name = name;
    }

    public int getPatientId() {
        return patientId;
    }

    @Override
    public String toString() {
        return name + " (ID: " + patientId + ")";
    }
}


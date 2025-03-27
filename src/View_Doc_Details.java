import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class View_Doc_Details extends JFrame {
    private JTable doctorTable;
    private JTextField searchField;
    private JButton searchButton, refreshButton, viewPatientsButton;
    private JComboBox<String> specializationFilter;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(View_Doc_Details::new);
    }

    public View_Doc_Details() {
        setTitle("Doctors Information");
        setSize(900, 600); // Adjusted size after removing room column
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("DOCTORS INFORMATION");
        titleLabel.setBounds(300, 20, 400, 30);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Search and Filter Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(50, 70, 800, 80);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(new Color(90, 156, 163));
        panel.add(controlPanel);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        searchLabel.setForeground(Color.WHITE);
        controlPanel.add(searchLabel);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        controlPanel.add(searchField);

        searchButton = new JButton("Search");
        styleButton(searchButton, new Color(59, 89, 182));
        searchButton.addActionListener(this::searchDoctors);
        controlPanel.add(searchButton);

        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(50, 150, 50));
        refreshButton.addActionListener(e -> loadDoctorData());
        controlPanel.add(refreshButton);

        JLabel filterLabel = new JLabel("Specialization:");
        filterLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        filterLabel.setForeground(Color.WHITE);
        controlPanel.add(filterLabel);

        specializationFilter = new JComboBox<>();
        specializationFilter.setPreferredSize(new Dimension(180, 30));
        specializationFilter.addItem("All Specializations");
        controlPanel.add(specializationFilter);

        viewPatientsButton = new JButton("View Patients");
        viewPatientsButton.setPreferredSize(new Dimension(220, 30));
        styleButton(viewPatientsButton, new Color(150, 50, 150));
        viewPatientsButton.addActionListener(this::viewAssignedPatients);
        controlPanel.add(viewPatientsButton);

        // Table setup with room number removed
        String[] columnNames = {"ID", "Name", "Specialization", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID
                return String.class;
            }
        };

        doctorTable = new JTable(model);
        doctorTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        doctorTable.setRowHeight(25);
        doctorTable.setAutoCreateRowSorter(true);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Specialization
        doctorTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Contact

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBounds(50, 150, 800, 400);
        panel.add(scrollPane);

        loadSpecializations();
        loadDoctorData();
        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
    }

    private void loadSpecializations() {
        try {
            Conn c = new Conn();
            String query = "SELECT DISTINCT specialization FROM Doctors";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    specializationFilter.addItem(rs.getString("specialization"));
                }
            }
        } catch (SQLException e) {
            showError("Error loading specializations: " + e.getMessage());
        }
    }

    private void loadDoctorData() {
        DefaultTableModel model = (DefaultTableModel) doctorTable.getModel();
        model.setRowCount(0);

        String selectedSpec = specializationFilter.getSelectedItem().toString();
        boolean filterBySpec = !selectedSpec.equals("All Specializations");

        try {
            Conn c = new Conn();
            String query = filterBySpec
                    ? "SELECT doctor_id, name, specialization, contact FROM Doctors WHERE specialization = ?"
                    : "SELECT doctor_id, name, specialization, contact FROM Doctors";

            try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                if (filterBySpec) {
                    pstmt.setString(1, selectedSpec);
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                                rs.getInt("doctor_id"),
                                rs.getString("name"),
                                rs.getString("specialization"),
                                rs.getString("contact")
                        };
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException e) {
            showError("Error loading doctor data: " + e.getMessage());
        }
    }

    private void searchDoctors(ActionEvent e) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadDoctorData();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) doctorTable.getModel();
        model.setRowCount(0);

        String selectedSpec = specializationFilter.getSelectedItem().toString();
        boolean filterBySpec = !selectedSpec.equals("All Specializations");

        try {
            Conn c = new Conn();
            String query = "SELECT doctor_id, name, specialization, contact FROM Doctors WHERE " +
                    "(name LIKE ? OR contact LIKE ?) " +
                    (filterBySpec ? "AND specialization = ?" : "");

            try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                pstmt.setString(1, "%" + searchTerm + "%");
                pstmt.setString(2, "%" + searchTerm + "%");

                if (filterBySpec) {
                    pstmt.setString(3, selectedSpec);
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                                rs.getInt("doctor_id"),
                                rs.getString("name"),
                                rs.getString("specialization"),
                                rs.getString("contact")
                        };
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Error searching doctors: " + ex.getMessage());
        }
    }

    private void viewAssignedPatients(ActionEvent e) {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a doctor first",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) doctorTable.getValueAt(selectedRow, 0);
        String doctorName = (String) doctorTable.getValueAt(selectedRow, 1);

        // Create a dialog to show assigned patients
        JDialog patientDialog = new JDialog(this, "Patients of Dr. " + doctorName, true);
        patientDialog.setSize(600, 400);
        patientDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        patientDialog.add(panel);

        // Table for patients
        String[] columns = {"Patient ID", "Name", "Age", "Gender", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable patientTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(patientTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data
        try {
            Conn c = new Conn();
            String query = "SELECT ID_Number, Name, Age, Gender, Contact FROM Patient_Info " +
                    "WHERE doctor_id = ?";

            try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                pstmt.setInt(1, doctorId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                                rs.getString("ID_Number"),
                                rs.getString("Name"),
                                rs.getInt("Age"),
                                rs.getString("Gender"),
                                rs.getString("Contact")
                        };
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Error loading assigned patients: " + ex.getMessage());
        }

        patientDialog.setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
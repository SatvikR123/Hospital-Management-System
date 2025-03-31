import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class View_Patient_Info extends JFrame {
    private JTable patientTable;
    private JTextField searchField;
    private JButton searchButton, refreshButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(View_Patient_Info::new);
    }

    public View_Patient_Info() {
        setTitle("Patients Information");
        setSize(1000, 650); // Increased size to accommodate more columns
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("ALL PATIENT'S INFORMATION");
        titleLabel.setBounds(300, 20, 400, 30);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Adding Image at Top Right
        ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\Desktop\\hospital management system\\Hospital-Management-System\\all patient info.png");
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setBounds(880, 10, 100, 100); // Positioned at top-right
        panel.add(imageLabel);

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(50, 70, 900, 40);
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(90, 156, 163));
        panel.add(searchPanel);

        JLabel searchLabel = new JLabel("Search Patient:");
        searchLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        searchLabel.setForeground(Color.WHITE);
        searchPanel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchPanel.add(searchField);

        searchButton = new JButton("Search");
        styleButton(searchButton, new Color(59, 89, 182));
        searchButton.addActionListener(this::searchPatient);
        searchPanel.add(searchButton);

        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(50, 150, 50));
        refreshButton.addActionListener(e -> loadPatientData());
        searchPanel.add(refreshButton);

        // Table setup with your columns
        String[] columnNames = {
                "ID Type", "ID_Number", "Name", "Gender",
                "Age", "Deposit", "Contact"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Integer.class; // Age
                if (columnIndex == 5) return Double.class;  // Deposit
                return String.class;
            }
        };

        patientTable = new JTable(model);
        patientTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        patientTable.setRowHeight(25);
        patientTable.setAutoCreateRowSorter(true);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        // Set column widths
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID Type
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(100); // ID Number
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Gender
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(50);  // Age
        patientTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Deposit
        patientTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Contact


        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBounds(50, 130, 900, 450); // Adjusted size
        panel.add(scrollPane);

        loadPatientData();
        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 30));
    }

    private void loadPatientData() {
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        model.setRowCount(0);

        try {
            Conn c = new Conn();
            String query = "SELECT * FROM Patient_Info";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Object[] row = {
                            rs.getString("ID_TYPE"),
                            rs.getString("ID_Number"),
                            rs.getString("Name"),
                            rs.getString("Gender"),
                            rs.getInt("Age"),
                            rs.getDouble("Deposit"),
                            rs.getString("Contact")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading patient data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchPatient(ActionEvent e) {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPatientData();
            return;
        }

        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        model.setRowCount(0);

        try {
            Conn c = new Conn();

            String query = "SELECT * FROM Patient_Info WHERE " +
                    "ID_Number LIKE ? OR Name LIKE ? OR Contact LIKE ?";

            try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                pstmt.setString(1, "%" + searchTerm + "%");
                pstmt.setString(2, "%" + searchTerm + "%");
                pstmt.setString(3, "%" + searchTerm + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Object[] row = {
                                rs.getString("ID_TYPE"),
                                rs.getString("ID_Number"),
                                rs.getString("Name"),
                                rs.getString("Gender"),
                                rs.getInt("Age"),
                                rs.getDouble("Deposit"),
                                rs.getString("Contact"),
                        };
                        model.addRow(row);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching patients: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

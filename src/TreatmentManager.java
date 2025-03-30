import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class TreatmentManager extends JFrame {
    private JTable treatmentTable;
    private JTextField searchField;
    private JButton searchButton, refreshButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TreatmentManager::new);
    }

    public TreatmentManager() {
        setTitle("Treatment Manager");
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("TREATMENT MANAGER");
        titleLabel.setBounds(300, 20, 400, 30);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Adding Image at Top Right
        ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\Desktop\\hospital management system\\Hospital-Management-System\\patient treatment.png");
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setBounds(880, 10, 100, 100); // Positioned at top-right
        panel.add(imageLabel);

        // Table setup
        String[] columnNames = {"Treatment ID", "Patient ID", "Doctor ID", "Date", "Diagnosis", "Prescription", "Notes"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        treatmentTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(treatmentTable);
        scrollPane.setBounds(50, 130, 900, 450);
        panel.add(scrollPane);

        loadTreatmentData();
        setVisible(true);
    }

    private void loadTreatmentData() {
        DefaultTableModel model = (DefaultTableModel) treatmentTable.getModel();
        model.setRowCount(0);

        try {
            Conn c = new Conn();
            String query = "SELECT * FROM Treatments";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("treatment_id"),
                            rs.getInt("patient_id"),
                            rs.getInt("doctor_id"),
                            rs.getDate("treatment_date"),
                            rs.getString("diagnosis"),
                            rs.getString("prescription"),
                            rs.getString("notes")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading treatment data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

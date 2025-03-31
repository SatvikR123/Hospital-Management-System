import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentManager extends JFrame {
    private Conn connection;
    private JTable treatmentTable;
    private JTextField searchField;
    private JButton searchButton, refreshButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TreatmentManager::new);
    }

    public boolean addTreatment(Treatment treatment) {
        String query = "INSERT INTO Treatments (patient_id, doctor_id, treatment_date, " +
                "diagnosis, prescription, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, treatment.getPatientId());
            pstmt.setInt(2, treatment.getDoctorId());
            pstmt.setDate(3, treatment.getTreatmentDate());
            pstmt.setString(4, treatment.getDiagnosis());
            pstmt.setString(5, treatment.getPrescription());
            pstmt.setString(6, treatment.getNotes());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding treatment: " + e.getMessage());
            return false;
        }
    }

    public TreatmentManager() {
        this.connection = new Conn();
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
        ImageIcon icon = new ImageIcon("C:\\Users\\DELL\\Desktop\\hospitaljava\\Hospital-Management-System\\patient treatment.png");
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

    public List<Treatment> getTreatmentsByPatient(int patientId) {
        List<Treatment> treatments = new ArrayList<>();
        String query = "SELECT * FROM Treatments WHERE patient_id = ? ORDER BY treatment_date DESC";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                treatments.add(new Treatment(
                        rs.getInt("treatment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("treatment_date"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching treatments: " + e.getMessage());
        }
        return treatments;
    }

    // Update treatment
    public boolean updateTreatment(Treatment treatment) {
        String query = "UPDATE Treatments SET diagnosis = ?, prescription = ?, notes = ? " +
                "WHERE treatment_id = ?";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, treatment.getDiagnosis());
            pstmt.setString(2, treatment.getPrescription());
            pstmt.setString(3, treatment.getNotes());
            pstmt.setInt(4, treatment.getTreatmentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating treatment: " + e.getMessage());
            return false;
        }
    }

    // Delete treatment
    public boolean deleteTreatment(int treatmentId) {
        String query = "DELETE FROM Treatments WHERE treatment_id = ?";

        try (PreparedStatement pstmt = connection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, treatmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting treatment: " + e.getMessage());
            return false;
        }
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

class Treatment {
    private int treatmentId;
    private int patientId;
    private int doctorId;
    private Date treatmentDate;
    private String diagnosis;
    private String prescription;
    private String notes;

    public Treatment(int treatmentId, int patientId, int doctorId, Date treatmentDate,
                     String diagnosis, String prescription, String notes) {
        this.treatmentId = treatmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.treatmentDate = treatmentDate;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
    }

    // Getters and Setters
    public int getTreatmentId() { return treatmentId; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public Date getTreatmentDate() { return treatmentDate; }
    public String getDiagnosis() { return diagnosis; }
    public String getPrescription() { return prescription; }
    public String getNotes() { return notes; }

    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public void setTreatmentDate(Date treatmentDate) { this.treatmentDate = treatmentDate; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Treatment [ID=" + treatmentId + ", Patient=" + patientId +
                ", Doctor=" + doctorId + ", Date=" + treatmentDate + "]";
    }
}
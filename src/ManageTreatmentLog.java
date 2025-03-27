import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ManageTreatmentLog extends JFrame {
    private JTable treatmentTable;
    private JComboBox<PatientItem> patientCombo;
    private JButton viewBtn, addBtn, editBtn, deleteBtn;
    private TreatmentManager treatmentManager;

    public ManageTreatmentLog() {
        setTitle("Treatment Log Management");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        treatmentManager = new TreatmentManager();

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Title Label
        JLabel titleLabel = new JLabel("TREATMENT LOG MANAGEMENT");
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
        viewBtn = new JButton("View Treatments");
        viewBtn.setBounds(550, 70, 150, 25);
        styleButton(viewBtn, new Color(59, 89, 182));
        viewBtn.addActionListener(e -> loadTreatments());
        panel.add(viewBtn);

        addBtn = new JButton("Add Treatment");
        addBtn.setBounds(720, 70, 150, 25);
        styleButton(addBtn, new Color(50, 150, 50));
        addBtn.addActionListener(e -> showAddTreatmentDialog());
        panel.add(addBtn);

        deleteBtn = new JButton("Delete Treatment");
        deleteBtn.setBounds(720, 110, 150, 25);
        styleButton(deleteBtn, new Color(200, 50, 50));
        deleteBtn.addActionListener(e -> deleteTreatment());
        panel.add(deleteBtn);

        // Treatment Table
        String[] columns = {"Treatment ID", "Date", "Doctor", "Diagnosis", "Prescription", "Notes"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        treatmentTable = new JTable(model);
        treatmentTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        treatmentTable.setRowHeight(25);
        treatmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        treatmentTable.getColumnModel().getColumn(5).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(treatmentTable);
        scrollPane.setBounds(50, 150, 1100, 450);
        panel.add(scrollPane);

        setVisible(true);
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

    private void loadTreatments() {
        PatientItem selectedPatient = (PatientItem) patientCombo.getSelectedItem();
        if (selectedPatient == null) {
            showError("Please select a patient");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) treatmentTable.getModel();
        model.setRowCount(0);

        List<Treatment> treatments = treatmentManager.getTreatmentsByPatient(selectedPatient.getPatientId());

        try {
            Conn c = new Conn();
            for (Treatment treatment : treatments) {
                // Get doctor name
                String doctorQuery = "SELECT name FROM Doctors WHERE doctor_id = ?";
                try (PreparedStatement pstmt = c.getConnection().prepareStatement(doctorQuery)) {
                    pstmt.setInt(1, treatment.getDoctorId());
                    ResultSet rs = pstmt.executeQuery();
                    String doctorName = rs.next() ? rs.getString("name") : "Unknown";

                    model.addRow(new Object[]{
                            treatment.getTreatmentId(),
                            treatment.getTreatmentDate(),
                            doctorName,
                            treatment.getDiagnosis(),
                            treatment.getPrescription(),
                            treatment.getNotes()
                    });
                }
            }
        } catch (SQLException e) {
            showError("Error loading doctor names: " + e.getMessage());
        }
    }

    private void showAddTreatmentDialog() {
        PatientItem patient = (PatientItem) patientCombo.getSelectedItem();
        if (patient == null) {
            showError("Please select a patient first");
            return;
        }

        JDialog dialog = new JDialog(this, "Add New Treatment", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        // Doctor selection
        JComboBox<String> doctorCombo = new JComboBox<>();
        try {
            Conn c = new Conn();
            String query = "SELECT doctor_id, name FROM Doctors";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    doctorCombo.addItem(rs.getInt("doctor_id") + " - " + rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            showError("Error loading doctors: " + e.getMessage());
        }

        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        JTextField diagnosisField = new JTextField();
        JTextField prescriptionField = new JTextField();
        JTextArea notesArea = new JTextArea();
        notesArea.setLineWrap(true);

        dialog.add(new JLabel("Doctor:"));
        dialog.add(doctorCombo);
        dialog.add(new JLabel("Date (YYYY-MM-DD):"));
        dialog.add(dateField);
        dialog.add(new JLabel("Diagnosis:"));
        dialog.add(diagnosisField);
        dialog.add(new JLabel("Prescription:"));
        dialog.add(prescriptionField);
        dialog.add(new JLabel("Notes:"));
        dialog.add(new JScrollPane(notesArea));

        JButton saveBtn = new JButton("Save Treatment");
        saveBtn.addActionListener(e -> {
            try {
                int doctorId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
                Date date = Date.valueOf(dateField.getText());

                Treatment treatment = new Treatment(
                        0, // ID will be auto-generated
                        patient.getPatientId(),
                        doctorId,
                        date,
                        diagnosisField.getText(),
                        prescriptionField.getText(),
                        notesArea.getText()
                );

                if (treatmentManager.addTreatment(treatment)) {
                    JOptionPane.showMessageDialog(dialog, "Treatment added successfully!");
                    loadTreatments();
                    dialog.dispose();
                } else {
                    showError("Failed to add treatment");
                }
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    private void deleteTreatment() {
        int selectedRow = treatmentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a treatment to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this treatment?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int treatmentId = (int) treatmentTable.getValueAt(selectedRow, 0);
            if (treatmentManager.deleteTreatment(treatmentId)) {
                JOptionPane.showMessageDialog(this, "Treatment deleted successfully!");
                loadTreatments();
            } else {
                showError("Failed to delete treatment");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new ManageTreatmentLog();
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentSystem extends JFrame {
    private JButton bookBtn, cancelBtn;
    private JTable appointmentTable;
    private JComboBox<Patient_Item> patientCombo;
    private JComboBox<String> doctorCombo;
    private JTextField dateField, timeField, reasonField;

    public AppointmentSystem() {
        setTitle("Appointment Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(90, 156, 163));
        add(panel);

        // Initialize components first
        doctorCombo = new JComboBox<>();
        patientCombo = new JComboBox<>();
        dateField = new JTextField();
        timeField = new JTextField();
        reasonField = new JTextField();
        bookBtn = new JButton("Book Appointment");
        cancelBtn = new JButton("Cancel Appointment");
        appointmentTable = new JTable();

        // Title Label
        JLabel titleLabel = new JLabel("APPOINTMENT MANAGEMENT");
        titleLabel.setBounds(350, 20, 400, 30);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Patient Selection
        JLabel patientLabel = new JLabel("Patient:");
        patientLabel.setBounds(50, 70, 100, 25);
        patientLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        patientLabel.setForeground(Color.WHITE);
        panel.add(patientLabel);

        patientCombo.setBounds(150, 70, 200, 25);
        panel.add(patientCombo);

        // Doctor Selection
        JLabel doctorLabel = new JLabel("Doctor:");
        doctorLabel.setBounds(50, 110, 100, 25);
        doctorLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        doctorLabel.setForeground(Color.WHITE);
        panel.add(doctorLabel);

        doctorCombo.setBounds(150, 110, 200, 25);
        doctorCombo.setEnabled(false);
        panel.add(doctorCombo);

        // Date Field
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(50, 150, 150, 25);
        dateLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        dateLabel.setForeground(Color.WHITE);
        panel.add(dateLabel);

        dateField.setBounds(200, 150, 150, 25);
        panel.add(dateField);

        // Time Field
        JLabel timeLabel = new JLabel("Time (HH:MM):");
        timeLabel.setBounds(50, 190, 150, 25);
        timeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        timeLabel.setForeground(Color.WHITE);
        panel.add(timeLabel);

        timeField.setBounds(200, 190, 150, 25);
        panel.add(timeField);

        // Reason Field
        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setBounds(50, 230, 100, 25);
        reasonLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        reasonLabel.setForeground(Color.WHITE);
        panel.add(reasonLabel);

        reasonField.setBounds(150, 230, 200, 25);
        panel.add(reasonField);

        // Buttons
        bookBtn.setBounds(400, 150, 180, 30);
        styleButton(bookBtn, new Color(59, 89, 182));
        bookBtn.addActionListener(this::bookAppointment);
        panel.add(bookBtn);

        cancelBtn.setBounds(400, 190, 180, 30);
        styleButton(cancelBtn, new Color(200, 50, 50));
        cancelBtn.addActionListener(this::cancelAppointment);
        panel.add(cancelBtn);

        // Table setup
        String[] columns = {"Appointment ID", "Doctor", "Patient", "Date", "Time", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentTable.setModel(model);
        appointmentTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
        appointmentTable.setRowHeight(25);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBounds(50, 280, 900, 250);
        panel.add(scrollPane);

        // Set up patient combo listener after components are initialized
        patientCombo.addActionListener(e -> {
            Patient_Item selectedPatient = (Patient_Item)patientCombo.getSelectedItem();
            if (selectedPatient != null) {
                loadDoctorForPatient(selectedPatient.getDoctorId());
            }
        });

        // Load data
        loadPatients();
        loadAppointments();
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
            String query = "SELECT patient_id, Name, doctor_id FROM Patient_Info";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    patientCombo.addItem(new Patient_Item(
                            rs.getInt("patient_id"),
                            rs.getString("Name"),
                            rs.getInt("doctor_id")
                    ));
                }
            }
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void loadDoctorForPatient(int doctorId) {
        if (doctorCombo != null) {  // Additional null check for safety
            doctorCombo.removeAllItems();
            try {
                Conn c = new Conn();
                String query = "SELECT doctor_id, name FROM Doctors WHERE doctor_id = ?";
                try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                    pstmt.setInt(1, doctorId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        doctorCombo.addItem(rs.getInt("doctor_id") + " - " + rs.getString("name"));
                        doctorCombo.setEnabled(false);
                    }
                }
            } catch (SQLException e) {
                showError("Error loading assigned doctor: " + e.getMessage());
            }
        }
    }

    private void loadAppointments() {
        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.setRowCount(0);

        try {
            Conn c = new Conn();
            String query = "SELECT a.appointment_id, d.name AS doctor_name, p.Name AS patient_name, " +
                    "a.appointment_date, a.appointment_time, a.status, a.reason " +
                    "FROM Appointments a " +
                    "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
                    "JOIN Patient_Info p ON a.patient_id = p.patient_id";
            try (Statement stmt = c.getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("appointment_id"),
                            rs.getString("doctor_name"),
                            rs.getString("patient_name"),
                            rs.getDate("appointment_date"),
                            rs.getTime("appointment_time"),
                            rs.getString("status"),
                            rs.getString("reason")
                    });
                }
            }
        } catch (SQLException e) {
            showError("Error loading appointments: " + e.getMessage());
        }
    }

    private void bookAppointment(ActionEvent e) {
        if (validateInputs()) {
            try {
                int doctorId = Integer.parseInt(doctorCombo.getSelectedItem().toString().split(" - ")[0]);
                int patientId = Integer.parseInt(patientCombo.getSelectedItem().toString().split(" - ")[0]);
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                Date time = new SimpleDateFormat("HH:mm").parse(timeField.getText());
                String reason = reasonField.getText();

                Conn c = new Conn();
                String query = "INSERT INTO Appointments (patient_id, doctor_id, appointment_date, " +
                        "appointment_time, reason) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                    pstmt.setInt(1, patientId);
                    pstmt.setInt(2, doctorId);
                    pstmt.setDate(3, new java.sql.Date(date.getTime()));
                    pstmt.setTime(4, new Time(time.getTime()));
                    pstmt.setString(5, reason);

                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                        loadAppointments();
                        clearFields();
                    }
                }
            } catch (Exception ex) {
                showError("Error booking appointment: " + ex.getMessage());
            }
        }
    }

    private void cancelAppointment(ActionEvent e) {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an appointment to cancel");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int appointmentId = (int) appointmentTable.getValueAt(selectedRow, 0);
                Conn c = new Conn();
                String query = "UPDATE Appointments SET status = 'Cancelled' WHERE appointment_id = ?";
                try (PreparedStatement pstmt = c.getConnection().prepareStatement(query)) {
                    pstmt.setInt(1, appointmentId);
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                        loadAppointments();
                    }
                }
            } catch (SQLException ex) {
                showError("Error cancelling appointment: " + ex.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        if (doctorCombo.getSelectedIndex() < 0 || patientCombo.getSelectedIndex() < 0 ||
                dateField.getText().isEmpty() || timeField.getText().isEmpty()) {
            showError("Please fill all required fields");
            return false;
        }

        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
            new SimpleDateFormat("HH:mm").parse(timeField.getText());
            return true;
        } catch (Exception e) {
            showError("Invalid date or time format. Use YYYY-MM-DD and HH:MM");
            return false;
        }
    }

    private void clearFields() {
        dateField.setText("");
        timeField.setText("");
        reasonField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new AppointmentSystem();
    }
}

class Patient_Item {
    private int patientId;
    private String name;
    private int doctorId;

    public Patient_Item(int patientId, String name, int doctorId) {
        this.patientId = patientId;
        this.name = name;
        this.doctorId = doctorId;
    }

    @Override
    public String toString() {
        return patientId + " - " + name;
    }

    public int getDoctorId() {
        return doctorId;
    }
}

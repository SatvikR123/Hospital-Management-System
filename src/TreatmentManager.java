import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentManager {
    private Conn connection;

    public TreatmentManager() {
        this.connection = new Conn();
    }

    // Add new treatment
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

    // Get all treatments for a patient
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
class PatientItem {
    private int patientId;
    private String name;
    private int doctorId;

    public PatientItem(int patientId, String name, int doctorId) {
        this.patientId = patientId;
        this.name = name;
        this.doctorId = doctorId;
    }

    @Override
    public String toString() {
        return name + " (ID: " + patientId + ")";
    }

    public int getPatientId() {
        return patientId;
    }
}
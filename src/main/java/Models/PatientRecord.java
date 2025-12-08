package Models;

public class PatientRecord {
    private String patientName;
    private String recordId;
    private String diagnosis;
    private String date;

    public PatientRecord(String patientName, String recordId, String diagnosis, String date) {
        this.patientName = patientName;
        this.recordId = recordId;
        this.diagnosis = diagnosis;
        this.date = date;
    }

    public String getPatientName() { return patientName; }
    public String getRecordId() { return recordId; }
    public String getDiagnosis() { return diagnosis; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return patientName + " - " + recordId;
    }
}

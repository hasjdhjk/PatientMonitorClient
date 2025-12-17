package Services;

import Models.Patient;
import Models.PatientRecord;
import Models.PatientRecordIO;
import Models.AddedPatientDB;

import java.time.LocalDate;
import java.util.List;

public class PatientDischargeService {

    public static Runnable onDischarge = null;

    public static void discharge(Patient patient, String diagnosis) {

        PatientRecord record = new PatientRecord(
                patient.getName(),
                "REC-" + patient.getId(),
                diagnosis,
                LocalDate.now().toString()
        );

        List<PatientRecord> records = PatientRecordIO.loadRecords();
        records.add(record);
        PatientRecordIO.saveRecords(records);

        AddedPatientDB.removePatient(patient);

        if (onDischarge != null) {
            onDischarge.run();
        }
    }
}
package Services;

import Models.Patients.AddedPatientDB;
import Models.Patients.Patient;
import Models.Patients.PatientRecord;
import Models.Patients.PatientRecordIO;
import Models.Vitals.LiveVitals;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import NetWork.ServerConfig;

public class PatientDischargeService {

    public static Runnable onDischarge = null;

    // HTTP client
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    public static void discharge(Patient patient, String diagnosis) {

        if (patient == null) return;

        // Save discharge record locally
        PatientRecord record = new PatientRecord(
                patient.getName(),
                "REC-" + patient.getId(),
                diagnosis,
                LocalDate.now().toString()
        );

        List<PatientRecord> records = PatientRecordIO.loadRecords();
        records.add(record);
        PatientRecordIO.saveRecords(records);

        // Delete from server DB
        final String doctor = NetWork.Session.getDoctorEmail();
        final int patientId = patient.getId();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {

                String url = ServerConfig.url(
                        "/api/patient/discharge?doctor="
                                + URLEncoder.encode(doctor, StandardCharsets.UTF_8)
                                + "&id=" + patientId
                );

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json; charset=utf-8")
                        // POST is easiest (Jetty/Servlet default tends to allow it)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() != 200) {
                    throw new RuntimeException("HTTP " + resp.statusCode() + " body=" + resp.body());
                }

                JsonObject out = JsonParser.parseString(resp.body()).getAsJsonObject();
                if (out.has("ok") && !out.get("ok").getAsBoolean()) {
                    String err = out.has("error") ? out.get("error").getAsString() : "Unknown server error";
                    throw new RuntimeException(err);
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    // Remove from local cache after DB success
                    AddedPatientDB.removePatient(patient);
                    LiveVitals.removeShared(patientId);
                    // Notify UI to refresh
                    if (onDischarge != null) onDischarge.run();

                } catch (Exception ex) {
                    ex.printStackTrace();

                    JOptionPane.showMessageDialog(
                            null,
                            "Discharge failed (DB not updated): " + ex.getMessage(),
                            "Discharge Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }
}
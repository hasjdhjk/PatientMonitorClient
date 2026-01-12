package Services;

import Models.Patient;
import Models.PatientRecord;
import Models.PatientRecordIO;
import Models.AddedPatientDB;

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

public class PatientDischargeService {

    public static Runnable onDischarge = null;

    // ===== Server config =====
    private static final String SERVER_BASE = "https://bioeng-fguys-app.impaas.uk"; // tsuru
//    private static final String SERVER_BASE = "http://localhost:8080/PatientServer";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    /**
     * Discharge flow:
     * 1) save local record (AccountPage reads this)
     * 2) call server to delete patient from DB
     * 3) remove from local cache
     * 4) trigger UI refresh callback
     */
    public static void discharge(Patient patient, String diagnosis) {

        // 0) basic guard
        if (patient == null) return;

        // 1) Save discharge record locally (AccountPage uses this)
        PatientRecord record = new PatientRecord(
                patient.getName(),
                "REC-" + patient.getId(),
                diagnosis,
                LocalDate.now().toString()
        );

        List<PatientRecord> records = PatientRecordIO.loadRecords();
        records.add(record);
        PatientRecordIO.saveRecords(records);

        // 2) Call server to delete from DB (async, avoid freezing UI)
        final String doctor = "demo"; // TODO: replace with logged-in doctor username later
        final int patientId = patient.getId();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {

                String url = SERVER_BASE
                        + "/api/patient/discharge?doctor="
                        + URLEncoder.encode(doctor, StandardCharsets.UTF_8)
                        + "&id=" + patientId;

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

                    // 3) Remove from local cache only after DB success
                    AddedPatientDB.removePatient(patient);

                    // 4) notify UI to reload from DB
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
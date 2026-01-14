package Models.Patients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PatientRecordIO {

    private static final String RECORD_FILE =
            System.getProperty("user.home") + "/patient_records.json";

    private static final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();

    private static final Type LIST_TYPE =
            new TypeToken<List<PatientRecord>>(){}.getType();

    // =====================================================
    // Load JSON
    // =====================================================
    public static List<PatientRecord> loadRecords() {
        File file = new File(RECORD_FILE);

        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            List<PatientRecord> list = gson.fromJson(reader, LIST_TYPE);
            return (list != null) ? list : new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =====================================================
    // Save JSON
    // =====================================================
    public static void saveRecords(List<PatientRecord> list) {
        try (FileWriter writer = new FileWriter(RECORD_FILE)) {
            gson.toJson(list, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // Import from CSV
    // Format: name, id, diagnosis, date
    // =====================================================
    public static List<PatientRecord> importCSV(JFrame parent) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select patient CSV");

        List<PatientRecord> imported = new ArrayList<>();

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {

            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(",");

                    // auto fill missing fields
                    String name = p.length > 0 ? p[0].trim() : "Unknown";
                    String id = p.length > 1 ? p[1].trim() : "N/A";
                    String diag = p.length > 2 ? p[2].trim() : "No diagnosis";
                    String date = p.length > 3 ? p[3].trim() : "Unknown";

                    imported.add(new PatientRecord(name, id, diag, date));
                }

                JOptionPane.showMessageDialog(parent, "Imported " + imported.size() + " records!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "CSV read error.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        return imported;
    }
}
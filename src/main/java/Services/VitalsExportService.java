package Services;

import Models.VitalRecord;
import Models.VitalRecordIO;

import javax.swing.*;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VitalsExportService {

    public static void exportCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("vital_report_24h.csv"));

            if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            FileWriter fw = new FileWriter(chooser.getSelectedFile());

            fw.write("Time,HR (bpm),Resp Rate (breaths/min),Temp (°C),SpO2 (%)\n");

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

            List<VitalRecord> records = VitalRecordIO.loadAll();
            for (VitalRecord r : records) {
                fw.write(String.format(
                        "%s,%.1f,%.1f,%.2f,%.1f\n",
                        r.getTimestamp().toLocalTime().format(fmt),
                        r.getAvgHeartRate(),
                        r.getAvgRespRate(),
                        r.getAvgTemperature(),
                        r.getAvgSpO2()
                ));
            }

            fw.close();

            JOptionPane.showMessageDialog(null, "CSV exported successfully");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Export failed",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

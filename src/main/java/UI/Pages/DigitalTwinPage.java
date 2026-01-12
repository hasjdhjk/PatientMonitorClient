package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.DigitalTwinPanel;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DigitalTwinPage extends JPanel {

    private final MainWindow window;
    private final List<Patient> allPatients;

    private int currentIndex = 0;
    private Patient currentPatient;

    private final DigitalTwinPanel digitalTwinPanel;
    private final BaseTile twinTile;

    public DigitalTwinPage(MainWindow window) {
        this.window = window;
        this.allPatients = AddedPatientDB.getAll();

        if (!allPatients.isEmpty()) {
            currentPatient = allPatients.get(0);
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        digitalTwinPanel = new DigitalTwinPanel();

        twinTile = new BaseTile(1200, 750, 30, true);
        twinTile.setLayout(new BorderLayout());
        twinTile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        twinTile.add(digitalTwinPanel, BorderLayout.CENTER);

        add(twinTile, BorderLayout.CENTER);

        if (currentPatient != null) {
            pushPatientToTwin();
        }
    }

    /** 🔑 唯一真正需要做的同步点 */
    private void pushPatientToTwin() {
        // ① 先让 dashboard 切换到对应 patientId（用于它的 fetch ../api/patient?id=...）
        digitalTwinPanel.setSelectedPatientId(currentPatient.getId());

        // ② 你原来这段 vitals 仍然可以保留（可选：即时刷新 UI）
        String[] parts = currentPatient.getBloodPressure().split("/");
        int sys = Integer.parseInt(parts[0].trim());
        int dia = Integer.parseInt(parts[1].trim());

        digitalTwinPanel.setVitals(
                currentPatient.getHeartRate(),
                20,   // RR placeholder
                20,   // SpO2 placeholder
                sys,
                dia,
                currentPatient.getTemperature()
        );
    }

    public void setPatient(Patient patient) {
        this.currentPatient = patient;
        this.currentIndex = allPatients.indexOf(patient);
        pushPatientToTwin(); // ⭐ 关键
    }

    public void nextPatient() {
        if (allPatients.isEmpty()) return;
        currentIndex = (currentIndex + 1) % allPatients.size();
        currentPatient = allPatients.get(currentIndex);
        pushPatientToTwin();
    }

    public void previousPatient() {
        if (allPatients.isEmpty()) return;
        currentIndex = (currentIndex - 1 + allPatients.size()) % allPatients.size();
        currentPatient = allPatients.get(currentIndex);
        pushPatientToTwin();
    }
}
package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.DigitalTwinPanel;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatusTrackerPage extends JPanel {

    private final MainWindow window;
    private final List<Patient> allPatients;

    private int currentIndex = 0;
    private Patient currentPatient;

    private final DigitalTwinPanel digitalTwinPanel;
    private final BaseTile twinTile;

    public StatusTrackerPage(MainWindow window) {
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

    private void pushPatientToTwin() {
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
        pushPatientToTwin();
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
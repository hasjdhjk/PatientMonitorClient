package UI;

import Models.Patient;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private PatientGridPanel gridPanel;

    public MainWindow() {
        setTitle("Patient Monitoring System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        gridPanel = new PatientGridPanel();

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

//        loadPatientsFromServer();
        loadMockPatients();

        setVisible(true);
    }

//    private void loadPatientsFromServer() {
//        List<Patient> patients = ApiClient.getPatients();
//
//        for (Patient p : patients) {
//            gridPanel.addPatient(p);
//        }
//    }

    // for testing only
    private void loadMockPatients() {
        java.util.List<Patient> mock = new java.util.ArrayList<>();
        mock.add(new Patient(1, "John", "Doe", 82, 36.8, "120/80"));
        mock.add(new Patient(2, "Sarah", "Smith", 95, 37.5, "140/90"));
        mock.add(new Patient(3, "Michael", "Brown", 130, 39.2, "160/100"));

        for (Patient p : mock) {
            gridPanel.addPatient(p);
        }
    }

}

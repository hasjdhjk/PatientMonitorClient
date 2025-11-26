package UI;

import Models.Patient;

import javax.swing.*;
import java.awt.*;

public class PatientDetailsWindow extends JFrame {

    private Patient patient;

    public PatientDetailsWindow(Patient patient) {
        this.patient = patient;

        setTitle("Patient Details - " + patient.getName());
        setSize(400, 300);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(patient.getId())));

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(patient.getName()));

        infoPanel.add(new JLabel("Heart Rate:"));
        infoPanel.add(new JLabel(String.valueOf(patient.getHeartRate()) + " bpm"));

        infoPanel.add(new JLabel("Temperature:"));
        infoPanel.add(new JLabel(String.valueOf(patient.getTemperature()) + " °C"));

        infoPanel.add(new JLabel("Blood Pressure:"));
        infoPanel.add(new JLabel(patient.getBloodPressure()));

        add(infoPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

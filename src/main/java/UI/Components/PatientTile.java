package UI.Components;

import Models.Patient;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PatientTile extends JPanel {

    public PatientTile(Patient patient, MainWindow window) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(150, 150));

        add(new JLabel(patient.getName()));
        add(new JLabel("HR: " + patient.getHeartRate()));
        add(new JLabel("Temp: " + patient.getTemperature()));

        // Click to open detail page
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                window.showPatientDetail(patient);
            }
        });
    }
}

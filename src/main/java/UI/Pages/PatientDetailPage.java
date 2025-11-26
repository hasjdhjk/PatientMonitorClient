package UI.Pages;

import Models.Patient;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class PatientDetailPage extends JPanel {

    public PatientDetailPage(MainWindow window, Patient patient) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel(patient.getName(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(0, 2, 20, 20));
        info.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        info.add(new JLabel("Heart Rate:"));
        info.add(new JLabel(patient.getHeartRate() + " bpm"));

        info.add(new JLabel("Temperature:"));
        info.add(new JLabel(patient.getTemperature() + " °C"));

        info.add(new JLabel("Blood Pressure:"));
        info.add(new JLabel(patient.getBloodPressure()));

        add(info, BorderLayout.CENTER);
    }
}

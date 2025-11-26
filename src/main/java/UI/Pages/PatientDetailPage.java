package UI.Pages;

import Models.Patient;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class PatientDetailPage extends JPanel {

    private Patient patient;
    private MainWindow window;

    public PatientDetailPage(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());
    }

    public void setPatient(Patient p) {
        this.patient = p;
        removeAll();

        JLabel title = new JLabel(p.getName(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(0, 2, 10, 10));
        info.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        info.add(new JLabel("Heart Rate:"));
        info.add(new JLabel(p.getHeartRate() + " bpm"));

        info.add(new JLabel("Temperature:"));
        info.add(new JLabel(p.getTemperature() + " °C"));

        info.add(new JLabel("Blood Pressure:"));
        info.add(new JLabel(p.getBloodPressure()));

        add(info, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}

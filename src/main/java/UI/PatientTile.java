package UI;

import javax.swing.*;
import java.awt.event.MouseAdapter;

public class PatientTile extends JPanel {

    private Patient patient;

    public PatientTile(Patient patient) {
        this.patient = patient;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(new JLabel("Name: " + patient.getName()));
        add(new JLabel("HR: " + patient.getHeartRate()));
        add(new JLabel("Temp: " + patient.getTemperature()));

        setBackground(Color.GREEN); // normal state

        // Add click listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PatientDetailsWindow(patient);
            }
        });
    }

    public void setAlertState(boolean critical) {
        setBackground(critical ? Color.RED : Color.GREEN);
    }
}

package UI;

import javax.swing.*;
import java.awt.*;

public class PatientGridPanel extends JPanel {

    public PatientGridPanel() {
        setLayout(new GridLayout(0, 3, 10, 10));
    }

    public void addPatient(Patient patient) {
        add(new PatientTile(patient));
        revalidate();
        repaint();
    }
}

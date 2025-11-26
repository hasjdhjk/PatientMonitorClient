package UI.Components;

import Models.Patient;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PatientTile extends JPanel {

    public PatientTile(Patient patient, MainWindow window) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        setBackground(new Color(255, 255, 255));
        setPreferredSize(new Dimension(150, 150));

        add(Box.createVerticalStrut(10));
        add(new JLabel(patient.getName()));
        add(new JLabel("HR: " + patient.getHeartRate()));
        add(new JLabel("Temp: " + patient.getTemperature()));
        add(new JLabel("BP: " + patient.getBloodPressure()));

        // Click → View details
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                window.showPatientDetails(patient);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e, patient);
                }
            }
        });
    }

    private void showContextMenu(MouseEvent e, Patient patient) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem edit = new JMenuItem("Edit");
        JMenuItem delete = new JMenuItem("Delete");

        menu.add(edit);
        menu.add(delete);

        menu.show(this, e.getX(), e.getY());
    }
}

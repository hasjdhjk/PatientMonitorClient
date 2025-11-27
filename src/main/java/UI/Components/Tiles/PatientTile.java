package UI.Components.Tiles;

import Models.Patient;
import UI.MainWindow;
import UI.Components.ECGPanel;
import UI.Components.StickyButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PatientTile extends BaseTile {

    public PatientTile(Patient patient, MainWindow window) {
        super(390, 320, 30);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // top: name + stick on top button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(patient.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        top.add(nameLabel, BorderLayout.WEST);

        StickyButton star = new StickyButton();
        top.add(star, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // middle left ecg
        ECGPanel ecg = new ECGPanel();
        add(ecg, BorderLayout.CENTER);

        // middle right vitals
        JPanel vitals = new JPanel();
        vitals.setLayout(new BoxLayout(vitals, BoxLayout.Y_AXIS));
        vitals.setOpaque(false);
        vitals.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        vitals.add(label("HR: " + patient.getHeartRate()));
        vitals.add(Box.createVerticalStrut(10));
        vitals.add(label("Temp: " + patient.getTemperature()));
        vitals.add(Box.createVerticalStrut(10));
        vitals.add(label("BP: " + patient.getBloodPressure()));

        add(vitals, BorderLayout.EAST);

        // cick to show detail
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    window.showPatientDetails(patient);
                }
            }
        });
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 18));
        return l;
    }
}

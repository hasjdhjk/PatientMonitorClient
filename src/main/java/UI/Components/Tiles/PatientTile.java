package UI.Components.Tiles;

import Models.Patient;
import Services.PatientDischargeService;
import UI.Components.RoundedButton;
import UI.MainWindow;
import UI.Components.ECGPanel;
import UI.Components.StickyButton;
import UI.Pages.HomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PatientTile extends BaseTile {

    public PatientTile(Patient patient, MainWindow window, HomePage homePage) {
        super(370, 320, 30, true);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // top: name + stick on top button
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(patient.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        top.add(nameLabel, BorderLayout.WEST);

        StickyButton star = new StickyButton(patient, homePage);
        top.add(star, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // middle left ecg
        ECGPanel ecg = new ECGPanel();
        add(ecg, BorderLayout.CENTER);
        //ecg.setHeartRate(patient.getHeartRate());

        // middle right vitals
        JPanel vitals = new JPanel();
        vitals.setLayout(new BoxLayout(vitals, BoxLayout.Y_AXIS));
        vitals.setOpaque(false);
        vitals.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        vitals.add(label("Gender: " + patient.getGender()));
        vitals.add(Box.createVerticalStrut(10));
        vitals.add(label("Age: " + patient.getAge()));
        vitals.add(Box.createVerticalStrut(10));
        vitals.add(label("BP: " + patient.getBloodPressure()));

        add(vitals, BorderLayout.EAST);

        // bottom: discharge button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        RoundedButton dischargeBtn = new RoundedButton("Discharge");

        dischargeBtn.addActionListener(e -> {

            String reason = JOptionPane.showInputDialog(
                    this,
                    "Enter discharge note / diagnosis:",
                    "Discharge Patient",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (reason == null || reason.trim().isEmpty()) {
                return;
            }

            PatientDischargeService.discharge(patient, reason);
            homePage.refresh();
        });

        bottom.add(dischargeBtn);
        add(bottom, BorderLayout.SOUTH);

        // click to show detail
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    window.showLiveMonitoring(patient); // StatusTrackerPage 内部会负责同步 DigitalTwinPanel
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
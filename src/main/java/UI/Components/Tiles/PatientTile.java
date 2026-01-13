package UI.Components.Tiles;

import Models.LiveVitals;
import Models.Patient;
import Services.PatientDischargeService;
import Services.AlertManager;
import UI.Components.RoundedButton;
import UI.MainWindow;
import UI.Components.StickyButton;
import UI.Pages.HomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;

public class PatientTile extends BaseTile {

    private final Patient patient;

    // UI labels (dynamic)
    private JLabel hrLabel;
    private JLabel tempLabel;
    private JLabel spo2Label;
    private JLabel rrLabel;

    // UI labels (static)
    private JLabel genderLabel;
    private JLabel ageLabel;
    private JLabel bpLabel;

    private Timer refreshTimer;
    private Timer flashTimer;
    private boolean flashOn = false;

    private static final Color BG_NORMAL = Color.WHITE;
    private static final Color BG_FLASH = new Color(255, 240, 240);

    // styling
    private static final Color BORDER_NORMAL = new Color(220, 225, 230);
    private static final Color BORDER_WARN = new Color(245, 158, 11);
    private static final Color BORDER_DANGER = new Color(220, 38, 38);

    public PatientTile(Patient patient, MainWindow window, HomePage homePage) {
        super(370, 320, 30, true);
        this.patient = patient;

        setLayout(new BorderLayout());
        setBackground(BG_NORMAL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORMAL, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // top: name + stick
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(patient.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        top.add(nameLabel, BorderLayout.WEST);

        StickyButton star = new StickyButton(patient, homePage);
        top.add(star, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // center: dynamic vitals (left) + static info (right)
        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setOpaque(false);

        center.add(buildDynamicVitalsPanel());
        center.add(buildStaticInfoPanel(patient));

        add(center, BorderLayout.CENTER);

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

            if (reason == null || reason.trim().isEmpty()) return;

            // stop any active alert sound for this patient first
            AlertManager.getInstance().updateAlert(
                    patient,
                    LiveVitals.VitalsSeverity.NORMAL,
                    Collections.emptyList()
            );

            // stop timers before discharge (optional safety)
            stopRefreshTimer();

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
                    window.showLiveMonitoring(patient);
                }
            }
        });

        // start live updates from shared LiveVitals
        startLiveRefresh(patient);

        // If HomePage refresh removes this tile, stop timers + clear alert to avoid "ghost" beeps
        addHierarchyListener(ev -> {
            if ((ev.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    stopRefreshTimer();
                }
            }
        });
    }

    private JPanel buildDynamicVitalsPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Live Vitals");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(title);
        p.add(Box.createVerticalStrut(10));

        hrLabel = label("HR: -- bpm");
        tempLabel = label("Temp: -- °C");
        spo2Label = label("SpO₂: -- %");
        rrLabel = label("Resp: -- /min");

        p.add(hrLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(tempLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(spo2Label);
        p.add(Box.createVerticalStrut(6));
        p.add(rrLabel);

        return p;
    }

    private JPanel buildStaticInfoPanel(Patient patient) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Patient Info");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(title);
        p.add(Box.createVerticalStrut(10));

        genderLabel = label("Gender: " + safe(patient.getGender()));
        ageLabel = label("Age: " + patient.getAge());
        bpLabel = label("BP: " + safe(patient.getBloodPressure()));

        p.add(genderLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(ageLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(bpLabel);

        return p;
    }

    private void startLiveRefresh(Patient patient) {
        LiveVitals live = LiveVitals.getShared(patient.getId(), patient.getBloodPressure());

        refreshTimer = new Timer(1000, e -> {
            // read shared simulated vitals
            int hr = (int) Math.round(live.getHeartRate());
            double temp = live.getTemperature();
            int spo2 = (int) Math.round(live.getSpO2());
            int rr = (int) Math.round(live.getRespRate());

            hrLabel.setText("HR: " + hr + " bpm");
            tempLabel.setText(String.format("Temp: %.1f °C", temp));
            spo2Label.setText("SpO₂: " + spo2 + " %");
            rrLabel.setText("Resp: " + rr + " /min");

            // BP may be updated by simulator; keep UI synced
            bpLabel.setText("BP: " + safe(live.getBloodPressure()));

            // severity highlight + flash
            LiveVitals.VitalsSeverity sev = live.getVitalsSeverity();
            Color borderColor =
                    (sev == LiveVitals.VitalsSeverity.DANGER) ? BORDER_DANGER :
                            (sev == LiveVitals.VitalsSeverity.WARNING) ? BORDER_WARN :
                                    BORDER_NORMAL;

            // update global alert manager (handles beep cadence + history, de-spammed internally)
            AlertManager.getInstance().updateAlert(
                    patient,
                    sev,
                    live.getAlertCauses(sev)
            );

            if (sev == LiveVitals.VitalsSeverity.DANGER) {
                startFlash(BORDER_DANGER, BORDER_NORMAL);
            } else if (sev == LiveVitals.VitalsSeverity.WARNING) {
                startFlash(BORDER_WARN, BORDER_NORMAL);
            } else {
                stopFlash(borderColor);
            }
        });

        refreshTimer.start();
    }

    private void stopRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
        }
        if (flashTimer != null) {
            flashTimer.stop();
            flashTimer = null;
        }
        flashOn = false;
        setBackground(BG_NORMAL);

        // Clear this patient's alert state when we stop updating (prevents lingering WARNING/DANGER in AlertManager)
        if (patient != null) {
            AlertManager.getInstance().updateAlert(
                    patient,
                    LiveVitals.VitalsSeverity.NORMAL,
                    Collections.emptyList()
            );
        }
    }

    private void startFlash(Color onColor, Color offColor) {
        // If already flashing, keep current timer but allow color to change
        if (flashTimer != null && flashTimer.isRunning()) {
            // Update immediately to new colors by restarting the timer
            flashTimer.stop();
            flashTimer = null;
        }

        flashOn = false;
        flashTimer = new Timer(400, e -> {
            flashOn = !flashOn;

            Color c = flashOn ? onColor : offColor;

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(c, 2),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            // Only flash background for DANGER (red). For WARNING keep background stable.
            if (onColor.equals(BORDER_DANGER)) {
                setBackground(flashOn ? BG_FLASH : BG_NORMAL);
            } else {
                setBackground(BG_NORMAL);
            }
            repaint();
        });
        flashTimer.start();
    }

    private void stopFlash(Color borderColor) {
        if (flashTimer != null) {
            flashTimer.stop();
            flashTimer = null;
        }
        flashOn = false;

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        setBackground(BG_NORMAL);
        repaint();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 16));
        return l;
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "--" : s;
    }
}
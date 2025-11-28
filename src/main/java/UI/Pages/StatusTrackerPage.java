package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.Tiles.BaseTile;
import UI.Components.ECGPanel;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StatusTrackerPage extends JPanel {

    private MainWindow window;
    private List<Patient> allPatients;

    private int currentIndex = 0;
    private Patient currentPatient;

    private JLabel patientNameLabel;
    private JPanel metricsContainer;
    private JPanel ecgPanelContainer;
    private JPanel timeSelector;

    public StatusTrackerPage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());

        // container for scrolling
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // set background color
        Color bg = new Color(240, 240, 240);
        content.setBackground(bg);
        scroll.getViewport().setBackground(bg);

        // Load all patients
        allPatients = AddedPatientDB.getAll();
        if (!allPatients.isEmpty()) {
            currentPatient = allPatients.get(0);
        }

        // patient name and arrow to switch to next patient
        JPanel topBar = new JPanel();
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        JButton leftArrow = new JButton("<");
        styleArrow(leftArrow);

        JButton rightArrow = new JButton(">");
        styleArrow(rightArrow);

        patientNameLabel = new JLabel("", SwingConstants.CENTER);
        patientNameLabel.setFont(new Font("Arial", Font.BOLD, 25));

        topBar.add(leftArrow);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(patientNameLabel);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(rightArrow);

        content.add(topBar);

        // arrow
        leftArrow.addActionListener(e -> switchPatient(-1));
        rightArrow.addActionListener(e -> switchPatient(+1));

        // select target time
        timeSelector = new BaseTile(600, 80, 30, true);
        timeSelector.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));

        addTimeButton(timeSelector, "Hour");
        addTimeButton(timeSelector, "Day");
        addTimeButton(timeSelector, "Week");
        addTimeButton(timeSelector, "Month");

        JPanel timeWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timeWrap.setOpaque(false);
        timeWrap.add(timeSelector);

        content.add(timeWrap);

        // vitals
        metricsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        metricsContainer.setOpaque(false);
        content.add(metricsContainer);

        // ecg
        ecgPanelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ecgPanelContainer.setOpaque(false);
        content.add(ecgPanelContainer);

        refreshDisplay();
    }

    // time selector buttons
    private void addTimeButton(JPanel container, String text) {
        JButton b = new JButton(text);

        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        container.add(b);
    }

    // arrows
    private void styleArrow(JButton arrow) {
        arrow.setFont(new Font("Arial", Font.BOLD, 30));
        arrow.setFocusPainted(false);
        arrow.setBorderPainted(false);
        arrow.setOpaque(false);
        arrow.setContentAreaFilled(false);
        arrow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // switch patients (allow looping)
    private void switchPatient(int direction) {
        if (allPatients.isEmpty()) return;

        currentIndex = (currentIndex + direction + allPatients.size()) % allPatients.size();
        currentPatient = allPatients.get(currentIndex);

        refreshDisplay();
    }

    // redraw page for patient
    private void refreshDisplay() {
        if (currentPatient == null) return;

        patientNameLabel.setText(currentPatient.getName());

        // metrics
        metricsContainer.removeAll();

        BaseTile heartRateTile = new BaseTile(360, 400, 50, true);
        heartRateTile.setLayout(new BoxLayout(heartRateTile, BoxLayout.Y_AXIS));
        heartRateTile.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        heartRateTile.add(makeMetricLabel("Heart Rate"));
        heartRateTile.add(Box.createVerticalStrut(10));
        heartRateTile.add(makeMetricLabel(currentPatient.getHeartRate() + " bpm"));
        metricsContainer.add(heartRateTile);

        BaseTile bpTile = new BaseTile(360, 400, 50, true);
        bpTile.setLayout(new BoxLayout(bpTile, BoxLayout.Y_AXIS));
        bpTile.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bpTile.add(makeMetricLabel("Blood Pressure"));
        bpTile.add(Box.createVerticalStrut(10));
        bpTile.add(makeMetricLabel(currentPatient.getBloodPressure()));
        metricsContainer.add(bpTile);

        BaseTile tempTile = new BaseTile(360, 400, 50, true);
        tempTile.setLayout(new BoxLayout(tempTile, BoxLayout.Y_AXIS));
        tempTile.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tempTile.add(makeMetricLabel("Body Temperature"));
        tempTile.add(Box.createVerticalStrut(10));
        tempTile.add(makeMetricLabel("" + currentPatient.getTemperature()));
        metricsContainer.add(tempTile);

        // ecg
        ecgPanelContainer.removeAll();

        BaseTile ecgTile = new BaseTile(1160, 250, 30, true);
        ecgTile.setLayout(new BorderLayout());
        ecgTile.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        ecgTile.add(new ECGPanel(), BorderLayout.CENTER);

        ecgPanelContainer.add(ecgTile);

        revalidate();
        repaint();
    }

    private JLabel makeMetricLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 20));
        return l;
    }

    // called when clicking a tile on home page
    public void setPatient(Patient p) {
        this.currentPatient = p;
        this.currentIndex = AddedPatientDB.getAll().indexOf(p);
        refreshDisplay();
    }
}

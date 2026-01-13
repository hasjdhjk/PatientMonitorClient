package UI.Pages;

import Models.LiveVitals;
import Models.Patient;
import Models.VitalRecord;
import Models.VitalRecordIO;
import Services.ECGSimulatorService;
import Services.MinuteAveragingService;
import Services.RespSimulatorService;
import Services.VitalsPdfReportService;
import Services.VitalTableJsonExportService;
import UI.Components.Tiles.WaveformPanel;
import UI.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LiveMonitoringPage extends JPanel {

    /* ===== THEME ===== */
    private static final Color BG_MAIN = new Color(245, 247, 250);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BORDER = new Color(220, 225, 230);

    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color RED = new Color(220, 38, 38);
    private static final Color AMBER = new Color(234, 179, 8);

    /* ===== DATA ===== */
    private final LiveVitals vitals;
    private final ECGSimulatorService ecgSim;
    private final RespSimulatorService respSim;
    private final MinuteAveragingService averagingService;
    private final MainWindow window;
    private final Patient patient;

    private int timeWindowSeconds = 10;

    private JLabel hrValue, spo2Value, respValue, bpValue;
    private JTextField timeWindowField;
    private JLabel liveClock;

    private WaveformPanel ecgPanel;
    private WaveformPanel respPanel;

    private DefaultTableModel historyModel;

    public LiveMonitoringPage(Patient patient, MainWindow window) {
        this.window = window;
        this.patient = patient;

        vitals = LiveVitals.getShared(patient.getId(), patient.getBloodPressure());
        ecgSim = new ECGSimulatorService();
        respSim = new RespSimulatorService();
        averagingService = new MinuteAveragingService(vitals);

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(BG_MAIN);

        top.add(buildHeader());
        top.add(buildTopBar());

        add(top, BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        startLiveLoop();
        startClock();
    }

    /* ================= HEADER ================= */

    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 56));

        JLabel info = new JLabel(
                "Patient: " + patient.getName() +
                        "    ID: " + patient.getId()
        );
        info.setFont(new Font("Arial", Font.BOLD, 15));
        info.setForeground(TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        right.setBackground(BG_CARD);

        JButton digitalTwin = new JButton("Digital Twin");
        digitalTwin.addActionListener(e -> window.showStatusTracker(patient));

        Integer[] hours = {1, 2, 4, 6, 12, 24, 48, 72};
        JComboBox<Integer> hourSelect = new JComboBox<>(hours);
        hourSelect.setSelectedItem(24);

        JButton exportPdf = new JButton("Export Report (PDF)");
        exportPdf.setBackground(BLUE);
        exportPdf.setForeground(Color.WHITE);
        exportPdf.setOpaque(true);
        exportPdf.setBorderPainted(false);

        exportPdf.addActionListener(e ->
                VitalsPdfReportService.exportPdfForLastHours(
                        patient,
                        (Integer) hourSelect.getSelectedItem()
                )
        );

        JButton exportJson = new JButton("Export Table (JSON)");
        exportJson.addActionListener(e ->
                VitalTableJsonExportService.exportJson(patient.getId())
        );

        right.add(digitalTwin);
        right.add(exportJson);
        right.add(new JLabel("Last"));
        right.add(hourSelect);
        right.add(new JLabel("hours"));
        right.add(exportPdf);

        header.add(info, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    /* ================= TOP BAR ================= */

    private JPanel buildTopBar() {

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        left.setBackground(BG_CARD);

        left.add(label("Time Window (s):"));

        timeWindowField = new JTextField(String.valueOf(timeWindowSeconds), 5);
        left.add(timeWindowField);

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> applyTimeWindow());

        left.add(apply);
        left.add(preset("10s", 10));
        left.add(preset("30s", 30));
        left.add(preset("60s", 60));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(BG_CARD);

        liveClock = label("");

        JLabel live = new JLabel("LIVE");
        live.setOpaque(true);
        live.setBackground(new Color(220, 252, 231));
        live.setForeground(GREEN);
        live.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        right.add(liveClock);
        right.add(live);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    /* ================= CONTENT ================= */

    private JPanel buildMainContent() {

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_MAIN);
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        content.add(buildVitalsRow());
        content.add(Box.createVerticalStrut(22));

        ecgPanel = new WaveformPanel("ECG (mV)", RED);
        respPanel = new WaveformPanel("Resp (ΔZ)", GREEN);

        updateAxes();

        content.add(wrap(ecgPanel, "ECG Waveform"));
        content.add(Box.createVerticalStrut(22));
        content.add(wrap(respPanel, "Respiratory Waveform"));
        content.add(Box.createVerticalStrut(22));
        content.add(buildHistoryTable());

        return content;
    }

    /* ================= VITAL CARDS ================= */

    private JPanel buildVitalsRow() {

        JPanel row = new JPanel(new GridLayout(1, 4, 18, 0));
        row.setBackground(BG_MAIN);

        hrValue = valueLabel();
        spo2Value = valueLabel();
        respValue = valueLabel();
        bpValue = valueLabel();
        bpValue.setText(patient.getBloodPressure());

        row.add(card("Heart Rate", hrValue, "bpm"));
        row.add(card("SpO₂", spo2Value, "%"));
        row.add(card("Resp Rate", respValue, "breaths / min"));
        row.add(card("Blood Pressure", bpValue, "mmHg"));

        return row;
    }

    /* ================= TABLE ================= */

    private JPanel buildHistoryTable() {

        String[] cols = {"Time", "HR (bpm)", "Resp (/min)", "Temp (°C)", "SpO₂ (%)"};
        historyModel = new DefaultTableModel(cols, 0);

        JTable table = new JTable(historyModel);
        table.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(1000, 190));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createLineBorder(BORDER));

        JLabel title = new JLabel("Vital History (Minute Averages)");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        p.add(title, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    /* ================= LIVE LOOP ================= */

    private void startLiveLoop() {

        new Timer(1000, e -> {

            averagingService.sample();

            int hr = (int) vitals.getHeartRate();
            int rr = (int) vitals.getRespRate();
            int spo2 = (int) vitals.getSpO2();

            hrValue.setText(String.valueOf(hr));
            respValue.setText(String.valueOf(rr));
            spo2Value.setText(String.valueOf(spo2));

            hrValue.setForeground(hr > 120 ? RED : hr < 50 ? AMBER : TEXT_PRIMARY);
            spo2Value.setForeground(spo2 < 92 ? RED : TEXT_PRIMARY);
            respValue.setForeground(rr > 25 || rr < 10 ? AMBER : TEXT_PRIMARY);

            ecgPanel.addSamples(ecgSim.nextSamples(25, hr), timeWindowSeconds * 100);
            respPanel.addSamples(respSim.nextSamples(10, rr), timeWindowSeconds * 30);

            refreshHistory();
        }).start();
    }

    private void refreshHistory() {
        historyModel.setRowCount(0);
        for (VitalRecord r : VitalRecordIO.loadAll()) {
            if (r.getPatientId() != patient.getId()) continue;
            historyModel.addRow(new Object[]{
                    r.getTimestamp().toLocalTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    String.format("%.1f", r.getAvgHeartRate()),
                    String.format("%.1f", r.getAvgRespRate()),
                    String.format("%.2f", r.getAvgTemperature()),
                    String.format("%.1f", r.getAvgSpO2())
            });
        }
    }

    /* ================= HELPERS ================= */

    private JPanel wrap(JPanel p, String title) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel h = new JLabel(title);
        h.setFont(new Font("Arial", Font.BOLD, 15));
        h.setForeground(TEXT_PRIMARY);
        c.add(h, BorderLayout.NORTH);
        c.add(p, BorderLayout.CENTER);
        return c;
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("--");
        l.setFont(new Font("Arial", Font.BOLD, 36));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    private JPanel card(String title, JLabel value, String unit) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(TEXT_MUTED);

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        unitLabel.setForeground(TEXT_MUTED);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(value);
        center.add(Box.createVerticalStrut(4));
        center.add(unitLabel);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);

        return card;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JButton preset(String t, int s) {
        JButton b = new JButton(t);
        b.addActionListener(e -> {
            timeWindowSeconds = s;
            timeWindowField.setText(String.valueOf(s));
            updateAxes();
        });
        return b;
    }

    private void applyTimeWindow() {
        try {
            timeWindowSeconds = Integer.parseInt(timeWindowField.getText());
        } catch (Exception ignored) {}
        updateAxes();
    }

    private void updateAxes() {
        ecgPanel.setAxis(-2, 2, timeWindowSeconds);
        respPanel.setAxis(-1.5, 1.5, timeWindowSeconds);
    }

    private void startClock() {
        new Timer(1000, e ->
                liveClock.setText(
                        LocalTime.now()
                                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                )
        ).start();
    }
}

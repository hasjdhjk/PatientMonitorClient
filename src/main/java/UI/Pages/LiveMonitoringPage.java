package UI.Pages;

import Models.LiveVitals;
import Models.Patient;
import Models.VitalRecord;
import Models.VitalRecordIO;
import Services.*;
import UI.Components.Tiles.WaveformPanel;
import UI.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LiveMonitoringPage extends JPanel {

    /* ===== CLINICAL LIGHT THEME ===== */
    private static final Color BG_MAIN = new Color(245, 247, 250);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BG_WAVE = new Color(250, 251, 252);
    private static final Color BORDER = new Color(220, 225, 230);

    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);

    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color RED = new Color(220, 38, 38);

    /* ===== DATA ===== */
    private final LiveVitals vitals;
    private final PatientSimulatorService vitalsSim;
    private final ECGSimulatorService ecgSim;
    private final RespSimulatorService respSim;
    private final MinuteAveragingService averagingService;

    private int timeWindowSeconds = 10;

    private JLabel hrValue, spo2Value, respValue, bpValue;
    private JTextField timeWindowField;
    private JLabel liveClock;

    private WaveformPanel ecgPanel;
    private WaveformPanel respPanel;

    private DefaultTableModel historyModel;

    private final MainWindow window;

    /* ===== ALARM LIMITS ===== */
    private static final int HR_LOW = 50;
    private static final int HR_HIGH = 120;
    private static final int SPO2_LOW = 92;
    private static final int RESP_LOW = 10;
    private static final int RESP_HIGH = 25;

    public LiveMonitoringPage(Patient patient, MainWindow window) {
        this.window = window;

        vitals = new LiveVitals(patient.getId());
        vitalsSim = new PatientSimulatorService(vitals);
        ecgSim = new ECGSimulatorService();
        respSim = new RespSimulatorService();
        averagingService = new MinuteAveragingService(vitals);

        setLayout(new BorderLayout());
        setBackground(BG_MAIN);

        JPanel topStack = new JPanel();
        topStack.setLayout(new BoxLayout(topStack, BoxLayout.Y_AXIS));
        topStack.setBackground(BG_MAIN);

        topStack.add(buildHeader(patient));
        topStack.add(buildTopBar());

        add(topStack, BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        startLiveLoop();
        startClock();
    }

    /* ================= HEADER ================= */

    private JPanel buildHeader(Patient patient) {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 48));

        JLabel info = new JLabel(
                "Patient: " + patient.getName() +
                        "    ID: " + patient.getId() +
                        "    Room: ICU-204"
        );
        info.setFont(new Font("Arial", Font.BOLD, 14));
        info.setForeground(TEXT_PRIMARY);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setBackground(BG_CARD);

        JButton status = new JButton("Digital Twin");
        status.setFocusPainted(false);
        status.addActionListener(e -> window.showStatusTracker(patient));

        JButton export = new JButton("Export CSV");
        export.setFocusPainted(false);
        export.addActionListener(e -> VitalsExportService.exportCSV());

        JButton print = new JButton("Print Report");
        print.setFocusPainted(false);
        print.addActionListener(e -> VitalsPrintService.print());

        right.add(status);
        right.add(export);
        right.add(print);

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
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.setBackground(BG_CARD);

        left.add(label("Time Window (seconds):"));

        timeWindowField = new JTextField(String.valueOf(timeWindowSeconds), 5);
        timeWindowField.setPreferredSize(new Dimension(60, 30));
        left.add(timeWindowField);

        JButton apply = new JButton("Apply");
        apply.setPreferredSize(new Dimension(80, 30));
        apply.setBackground(BLUE);
        apply.setForeground(Color.WHITE);
        apply.setOpaque(true);
        apply.setBorderPainted(false);
        apply.setFocusPainted(false);
        apply.setFont(new Font("Arial", Font.BOLD, 12));
        apply.addActionListener(e -> applyTimeWindow());
        left.add(apply);

        left.add(preset("5s", 5));
        left.add(preset("10s", 10));
        left.add(preset("30s", 30));
        left.add(preset("60s", 60));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        right.setBackground(BG_CARD);

        liveClock = label("");

        JLabel live = new JLabel("LIVE");
        live.setOpaque(true);
        live.setBackground(new Color(220, 252, 231));
        live.setForeground(GREEN);
        live.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        live.setFont(new Font("Arial", Font.BOLD, 12));

        right.add(liveClock);
        right.add(live);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JButton preset(String t, int s) {
        JButton b = new JButton(t);
        b.setPreferredSize(new Dimension(60, 30));
        b.addActionListener(e -> {
            timeWindowSeconds = s;
            timeWindowField.setText(String.valueOf(s));
            clearWaveforms();
            updateAxes();
        });
        return b;
    }

    private void applyTimeWindow() {
        try {
            timeWindowSeconds = Integer.parseInt(timeWindowField.getText());
        } catch (Exception e) {
            timeWindowSeconds = 10;
            timeWindowField.setText("10");
        }
        clearWaveforms();
        updateAxes();
    }

    /* ================= CONTENT ================= */

    private JPanel buildMainContent() {

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_MAIN);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        content.add(buildVitalsRow());
        content.add(Box.createVerticalStrut(20));

        ecgPanel = new WaveformPanel("ECG (mV)", RED);
        respPanel = new WaveformPanel("Resp (ΔZ)", GREEN);

        ecgPanel.setBackground(BG_WAVE);
        respPanel.setBackground(BG_WAVE);

        ecgPanel.setPreferredSize(new Dimension(1000, 320));
        respPanel.setPreferredSize(new Dimension(1000, 320));

        updateAxes();

        content.add(wrap(ecgPanel, "ECG Waveform", "Lead II — mV"));
        content.add(Box.createVerticalStrut(25));
        content.add(wrap(respPanel, "Respiratory Waveform", "Impedance Pneumography — ΔZ"));
        content.add(Box.createVerticalStrut(25));
        content.add(buildHistoryTable());

        return content;
    }

    private JPanel buildVitalsRow() {

        JPanel row = new JPanel(new GridLayout(1, 4, 15, 0));
        row.setBackground(BG_MAIN);

        hrValue = valueLabel();
        spo2Value = valueLabel();
        respValue = valueLabel();
        bpValue = valueLabel();
        bpValue.setText("120 / 80");

        row.add(card("Heart Rate", hrValue, "bpm"));
        row.add(card("SpO₂", spo2Value, "%"));
        row.add(card("Resp Rate", respValue, "breaths/min"));
        row.add(card("Blood Pressure", bpValue, "mmHg"));

        return row;
    }

    private JLabel valueLabel() {
        JLabel l = new JLabel("--");
        l.setFont(new Font("Arial", Font.BOLD, 28));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    private JPanel card(String t, JLabel v, String u) {

        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(BG_CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel tl = new JLabel(t);
        tl.setForeground(TEXT_MUTED);

        JLabel ul = new JLabel(u);
        ul.setForeground(TEXT_MUTED);

        c.add(tl);
        c.add(Box.createVerticalStrut(6));
        c.add(v);
        c.add(ul);

        return c;
    }

    private JPanel wrap(WaveformPanel p, String t, String s) {

        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(BG_CARD);
        w.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel h = new JLabel(t + " — " + s);
        h.setFont(new Font("Arial", Font.BOLD, 14));
        h.setForeground(TEXT_PRIMARY);

        w.add(h, BorderLayout.NORTH);
        w.add(p, BorderLayout.CENTER);

        return w;
    }

    /* ================= TABLE ================= */

    private JPanel buildHistoryTable() {

        String[] cols = {"Time", "HR (bpm)", "Resp (breaths/min)", "Temp (°C)", "SpO₂ (%)"};
        historyModel = new DefaultTableModel(cols, 0);

        JTable table = new JTable(historyModel);
        table.setRowHeight(26);
        table.setGridColor(BORDER);
        table.setForeground(TEXT_PRIMARY);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(1000, 170));

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_MAIN);
        p.add(new JLabel("Vital History (minute averages)"), BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    /* ================= LIVE LOOP ================= */

    private void startLiveLoop() {

        new Timer(1000, e -> {

            vitalsSim.update(1);
            averagingService.sample();

            int hr = (int) vitals.getHeartRate();
            int rr = (int) vitals.getRespRate();
            int sp = (int) vitals.getSpO2();

            hrValue.setText(String.valueOf(hr));
            respValue.setText(String.valueOf(rr));
            spo2Value.setText(String.valueOf(sp));

            hrValue.setForeground(hr < HR_LOW || hr > HR_HIGH ? RED : TEXT_PRIMARY);
            respValue.setForeground(rr < RESP_LOW || rr > RESP_HIGH ? RED : TEXT_PRIMARY);
            spo2Value.setForeground(sp < SPO2_LOW ? RED : TEXT_PRIMARY);

            ecgPanel.addSamples(ecgSim.nextSamples(25, hr), timeWindowSeconds * 100);
            respPanel.addSamples(respSim.nextSamples(10, rr), timeWindowSeconds * 30);

            refreshHistory();
        }).start();
    }

    private void refreshHistory() {
        historyModel.setRowCount(0);
        for (VitalRecord r : VitalRecordIO.loadAll()) {
            historyModel.addRow(new Object[]{
                    r.getTimestamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    String.format("%.1f", r.getAvgHeartRate()),
                    String.format("%.1f", r.getAvgRespRate()),
                    String.format("%.2f", r.getAvgTemperature()),
                    String.format("%.1f", r.getAvgSpO2())
            });
        }
    }

    private void clearWaveforms() {
        ecgPanel.clear();
        respPanel.clear();
    }

    private void updateAxes() {
        ecgPanel.setAxis(-2, 2, timeWindowSeconds);
        respPanel.setAxis(-1.5, 1.5, timeWindowSeconds);
    }

    private void startClock() {
        new Timer(1000, e ->
                liveClock.setText(LocalTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        ).start();
    }
}

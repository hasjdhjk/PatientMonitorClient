package UI.Components;

import Models.Vitals.AlertRecord;
import Models.Vitals.LiveVitals;
import Services.AlertManager;
import UI.MainWindow;
import Models.Patients.AddedPatientDB;
import Models.Patients.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlertHistorySidebar extends JPanel {

    private static final int SIDEBAR_WIDTH = 450;

    private final MainWindow window;

    private final JLabel titleLabel = new JLabel("Active Alerts");
    private final JLabel subtitleLabel = new JLabel("0 Critical · 0 Warnings");

    private final JPanel listPanel = new JPanel();
    private final JScrollPane scrollPane;

    private final Timer refreshTimer;
    private int lastHistorySize = -1;

    // keep track of which alerts have been acknowledged (hidden from sidebar)
    private final Set<String> acknowledgedKeys = new HashSet<>();

    public AlertHistorySidebar(MainWindow window) {
        this.window = window;

        Color sidebarBg = new Color(246, 247, 249);

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(sidebarBg);

        // full height divider line on the LEFT of the sidebar
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 232, 236)));

        setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        setMinimumSize(new Dimension(SIDEBAR_WIDTH, 0));
        setMaximumSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));

        add(buildHeader(), BorderLayout.NORTH);

        // List panel blends into sidebar background
        listPanel.setOpaque(true);
        listPanel.setBackground(getBackground());
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(10, 12, 12, 12));

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);

        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(getBackground());

        add(scrollPane, BorderLayout.CENTER);

        refreshTimer = new Timer(1000, e -> refreshIfNeeded());
        refreshTimer.start();

        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(true);
        header.setBackground(getBackground());

        // Only a bottom separator line
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 232, 236)),
                new EmptyBorder(14, 14, 14, 14)
        ));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(20, 20, 20));

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(120, 125, 135));

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);

        return header;
    }

    private void refreshIfNeeded() {
        List<AlertRecord> history = AlertManager.getInstance().getHistory();
        if (history.size() != lastHistorySize) {
            refresh();
        } else {
            repaint(); // keeps "time ago" feeling alive
        }
    }

    public void refresh() {
        List<AlertRecord> history = AlertManager.getInstance().getHistory();
        lastHistorySize = history.size();

        // newest first
        Collections.reverse(history);

        // Count critical/warning but only those not acknowledged (since sidebar shows unacked)
        int critical = 0, warning = 0;
        for (AlertRecord r : history) {
            if (acknowledgedKeys.contains(keyOf(r))) continue;
            if (r.getSeverity() == LiveVitals.VitalsSeverity.DANGER) critical++;
            if (r.getSeverity() == LiveVitals.VitalsSeverity.WARNING) warning++;
        }
        subtitleLabel.setText(critical + " Critical · " + warning + " Warnings");

        listPanel.removeAll();

        int max = 30;
        int shown = 0;

        for (AlertRecord r : history) {
            if (shown >= max) break;
            if (acknowledgedKeys.contains(keyOf(r))) continue;

            JPanel card = buildCard(r);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(12));
            shown++;
        }

        if (shown == 0) {
            JLabel none = new JLabel("No unacknowledged alerts.");
            none.setFont(new Font("Arial", Font.PLAIN, 12));
            none.setForeground(new Color(120, 125, 135));
            none.setBorder(new EmptyBorder(8, 4, 0, 0));
            none.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(none);
        }

        // pushes extra vertical space to the bottom so cards don't stretch
        listPanel.add(Box.createVerticalGlue());

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel buildCard(AlertRecord r) {
        LiveVitals.VitalsSeverity sev = r.getSeverity();

        Color border;
        Color bg;
        Color tagColor;

        if (sev == LiveVitals.VitalsSeverity.DANGER) {
            border = new Color(235, 84, 84);
            bg = new Color(255, 245, 245);
            tagColor = new Color(235, 84, 84);
        } else if (sev == LiveVitals.VitalsSeverity.WARNING) {
            border = new Color(230, 163, 57);
            bg = new Color(255, 250, 237);
            tagColor = new Color(230, 163, 57);
        } else {
            border = new Color(210, 215, 222);
            bg = Color.WHITE;
            tagColor = new Color(120, 125, 135);
        }

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        // Fixed height
        int CARD_HEIGHT = 185;
        card.setPreferredSize(new Dimension(Short.MAX_VALUE, CARD_HEIGHT));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, CARD_HEIGHT));
        card.setMinimumSize(new Dimension(0, CARD_HEIGHT));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // top row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tag = new JLabel(sev == LiveVitals.VitalsSeverity.DANGER ? "CRITICAL" :
                sev == LiveVitals.VitalsSeverity.WARNING ? "WARNING" : "INFO");
        tag.setFont(new Font("Arial", Font.BOLD, 12));
        tag.setForeground(tagColor);

        JLabel timeAgo = new JLabel(formatTimeAgo(r.getTimestamp()));
        timeAgo.setFont(new Font("Arial", Font.PLAIN, 12));
        timeAgo.setForeground(new Color(120, 125, 135));

        topRow.add(tag, BorderLayout.WEST);
        topRow.add(timeAgo, BorderLayout.EAST);

        // middle
        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<String> causes = (r.getCauses() == null) ? new ArrayList<>() : r.getCauses();
        String headline = causes.isEmpty() ? "Vitals out of range" : causes.get(0);

        JLabel headlineLabel = new JLabel(headline);
        headlineLabel.setFont(new Font("Arial", Font.BOLD, 13));
        headlineLabel.setForeground(new Color(25, 25, 25));
        headlineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel patientLine = new JLabel("Patient " + r.getPatientName() + " · ID " + r.getPatientId());
        patientLine.setFont(new Font("Arial", Font.PLAIN, 12));
        patientLine.setForeground(new Color(90, 95, 105));
        patientLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        mid.add(headlineLabel);
        mid.add(Box.createVerticalStrut(4));
        mid.add(patientLine);

        if (causes.size() > 1) {
            JLabel extra = new JLabel(String.join(", ", causes.subList(1, causes.size())));
            extra.setFont(new Font("Arial", Font.PLAIN, 11));
            extra.setForeground(new Color(120, 125, 135));
            extra.setBorder(new EmptyBorder(6, 0, 0, 0));
            extra.setAlignmentX(Component.LEFT_ALIGNMENT);
            mid.add(extra);
        }

        // bottom row buttons
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton viewBtn = new JButton("View Patient");
        JButton ackBtn = new JButton("Acknowledge");

        styleButton(viewBtn, true, tagColor);
        styleButton(ackBtn, false, tagColor);

        // View Patient (unchanged from earlier working version)
        viewBtn.addActionListener(e -> {
            Patient p = findPatientById(r.getPatientId());
            if (p != null) {
                window.showLiveMonitoring(p);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Patient not found (may have been discharged).",
                        "View Patient",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        });

        // hides it from the sidebar immediately
        ackBtn.addActionListener(e -> {
            acknowledgedKeys.add(keyOf(r));
            refresh();
        });

        bottom.add(viewBtn);
        bottom.add(Box.createHorizontalStrut(10));
        bottom.add(ackBtn);
        bottom.add(Box.createHorizontalGlue());

        // Stack inside the card
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(topRow);
        center.add(Box.createVerticalStrut(6));
        center.add(mid);
        center.add(bottom);

        card.add(center, BorderLayout.CENTER);

        return card;
    }

    private void styleButton(JButton btn, boolean filled, Color accent) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 30));

        if (filled) {
            btn.setBackground(accent);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new LineBorder(accent, 1, true));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(accent);
            btn.setBorder(new LineBorder(accent, 1, true));
        }

        btn.setOpaque(true);
    }

    private Patient findPatientById(int id) {
        for (Patient p : AddedPatientDB.getAll()) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    // key for acknowledging a specific record
    private String keyOf(AlertRecord r) {
        long ms = (r.getTimestamp() == null) ? 0L : r.getTimestamp().toEpochMilli();
        return r.getPatientId() + "|" + r.getSeverity() + "|" + ms;
    }

    private String formatTimeAgo(Instant ts) {
        if (ts == null) return "";
        long s = Duration.between(ts, Instant.now()).getSeconds();
        if (s < 0) s = 0;
        if (s < 60) return s + "s ago";
        long m = s / 60;
        if (m < 60) return m + "m ago";
        long h = m / 60;
        if (h < 24) return h + "h ago";
        long d = h / 24;
        return d + "d ago";
    }
}

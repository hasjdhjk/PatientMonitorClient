package UI.Components;

import Models.VitalRecord;
import Models.VitalRecordIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class VitalHistoryPanel extends JPanel {

    private final DefaultTableModel model;

    public VitalHistoryPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Vital History (averaged values)"));

        model = new DefaultTableModel(
                new String[]{
                        "Time",
                        "HR (bpm)",
                        "Resp Rate (breaths/min)",
                        "Temp (°C)",
                        "SpO₂ (%)"
                },
                0
        );

        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void refresh() {
        model.setRowCount(0);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (VitalRecord r : VitalRecordIO.loadAll()) {
            model.addRow(new Object[]{
                    r.getTimestamp().format(fmt),
                    String.format("%.1f", r.getAvgHeartRate()),
                    String.format("%.1f", r.getAvgRespRate()),
                    String.format("%.2f", r.getAvgTemperature()),
                    String.format("%.1f", r.getAvgSpO2())
            });
        }
    }
}

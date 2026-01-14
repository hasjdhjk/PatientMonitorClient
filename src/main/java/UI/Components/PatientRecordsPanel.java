package UI.Components;

import Models.Patients.PatientRecord;
import Models.Patients.PatientRecordIO;
import Models.Patients.PatientRecordRenderer;
import Services.PatientDischargeService;
import UI.Components.PlaceHolders.PlaceholderTextField;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PatientRecordsPanel extends RoundedPanel {

    private DefaultListModel<PatientRecord> recordModel = new DefaultListModel<>();
    private DefaultListModel<PatientRecord> filteredModel = new DefaultListModel<>();
    private JList<PatientRecord> recordList;
    private PlaceholderTextField searchField;

    // Creates the records panel UI with search, import, delete, and clear controls.
    public PatientRecordsPanel(Runnable onBack) {
        super(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Buttons
        RoundedButton backBtn = new RoundedButton("← Back");
        backBtn.addActionListener(e -> onBack.run());

        RoundedButton importBtn = new RoundedButton("Import CSV");
        RoundedButton deleteBtn = new RoundedButton("Delete Selected");
        RoundedButton clearBtn = new RoundedButton("Clear All");

        searchField = new PlaceholderTextField("Search...");
        searchField.setPreferredSize(new Dimension(200, 34));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setOpaque(false);
        topBar.add(backBtn);
        topBar.add(importBtn);
        topBar.add(deleteBtn);
        topBar.add(clearBtn);
        topBar.add(searchField);

        // List
        recordList = new JList<>(filteredModel);
        recordList.setCellRenderer(new PatientRecordRenderer());

        recordList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    PatientRecord r = recordList.getSelectedValue();
                    if (r != null) showRecordPreview(r);
                }
            }
        });

        // Search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String q = searchField.getText().trim().toLowerCase();
                filteredModel.clear();

                if (q.isEmpty()) {
                    resetFilter();
                    return;
                }

                for (int i = 0; i < recordModel.size(); i++) {
                    PatientRecord r = recordModel.get(i);
                    if (r.matches(q)) filteredModel.addElement(r);
                }
            }

            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        // Import CSV
        importBtn.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            List<PatientRecord> newOnes = PatientRecordIO.importCSV(parent);
            for (PatientRecord pr : newOnes)
                recordModel.addElement(pr);

            resetFilter();
            saveToJson();
        });

        // Delete
        deleteBtn.addActionListener(e -> {
            PatientRecord selected = recordList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a record to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this, "Delete this record?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            recordModel.removeElement(selected);
            filteredModel.removeElement(selected);
            saveToJson();
        });

        // Clear
        clearBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete ALL records?",
                    "Clear All",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            recordModel.clear();
            filteredModel.clear();
            saveToJson();
        });

        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(recordList), BorderLayout.CENTER);

        // Load
        loadFromJson();
        resetFilter();
    }
    // Helpers
    // Copies the full record list into the filtered model (used when clearing search).
    private void resetFilter() {
        filteredModel.clear();
        for (int i = 0; i < recordModel.size(); i++)
            filteredModel.addElement(recordModel.get(i));
    }
    // Loads patient records from the JSON file into the list model.
    private void loadFromJson() {
        recordModel.clear();
        for (PatientRecord r : PatientRecordIO.loadRecords())
            recordModel.addElement(r);
    }
    // Saves the current record list model back to the JSON file.
    private void saveToJson() {
        PatientRecordIO.saveRecords(Collections.list(recordModel.elements()));
    }
    // Shows a pop-up dialog displaying the selected record details.
    private void showRecordPreview(PatientRecord r) {
        String msg = "Name: " + r.getPatientName() + "\n"
                + "Record ID: " + r.getRecordId() + "\n"
                + "Diagnosis: " + r.getDiagnosis() + "\n"
                + "Date: " + r.getDate();

        JOptionPane.showMessageDialog(
                this, msg, "Record Details", JOptionPane.INFORMATION_MESSAGE
        );
    }
    // Reloads records from disk and refreshes the displayed list (e.g., after discharge updates).
    public void reloadFromDisk() {
        loadFromJson();
        resetFilter();
        PatientDischargeService.onDischarge = this::reloadFromDisk;
        System.out.println("Record panel reloaded");
    }
}
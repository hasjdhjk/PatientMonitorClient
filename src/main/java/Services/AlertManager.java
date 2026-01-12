package Services;

import Models.AlertRecord;
import Models.Patient;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class AlertManager {
    private static final AlertManager INSTANCE = new AlertManager();

    public static AlertManager getInstance() {
        return INSTANCE;
    }

    private final Map<Integer, Patient.VitalsSeverity> active = new HashMap<>();
    private final List<AlertRecord> history = new ArrayList<>();

    private javax.swing.Timer soundTimer;
    private Patient.VitalsSeverity currentlyPlaying = Patient.VitalsSeverity.NORMAL;

    private AlertManager() {}

    public synchronized void updateAlert(Patient patient, Patient.VitalsSeverity newSev, List<String> causes) {
        int id = patient.getId();
        Patient.VitalsSeverity oldSev = active.getOrDefault(id, Patient.VitalsSeverity.NORMAL);

        if (newSev == Patient.VitalsSeverity.NORMAL) {
            active.remove(id);
        } else {
            active.put(id, newSev);
        }

        // Log ONLY if severity changed (prevents spam every time your blink timer runs)
        if (newSev != oldSev) {
            if (newSev != Patient.VitalsSeverity.NORMAL) {
                history.add(new AlertRecord(id, patient.getName(), newSev, causes, Instant.now()));
            } else {
                // Optional: log "resolved" events too if you want:
                // history.add(new AlertRecord(id, patient.getName(), Patient.VitalsSeverity.NORMAL, List.of("Alert resolved"), Instant.now()));
            }
        }

        refreshSound();
    }

    public synchronized List<AlertRecord> getHistory() {
        return new ArrayList<>(history);
    }

    public synchronized void clearHistory() {
        history.clear();
    }

    private void refreshSound() {
        Patient.VitalsSeverity highest = getHighestActiveSeverity();

        if (highest == currentlyPlaying) return; // no change

        stopSoundTimer();

        currentlyPlaying = highest;

        if (highest == Patient.VitalsSeverity.DANGER) {
            // Fast repeating beep (e.g., every 300ms)
            soundTimer = new javax.swing.Timer(300, e -> Toolkit.getDefaultToolkit().beep());
            soundTimer.start();
        } else if (highest == Patient.VitalsSeverity.WARNING) {
            // Slower repeating beep (e.g., every 700ms)
            soundTimer = new javax.swing.Timer(700, e -> Toolkit.getDefaultToolkit().beep());
            soundTimer.start();
        }
        // NORMAL => no timer
    }

    private Patient.VitalsSeverity getHighestActiveSeverity() {
        boolean anyDanger = active.values().stream().anyMatch(s -> s == Patient.VitalsSeverity.DANGER);
        if (anyDanger) return Patient.VitalsSeverity.DANGER;

        boolean anyWarning = active.values().stream().anyMatch(s -> s == Patient.VitalsSeverity.WARNING);
        if (anyWarning) return Patient.VitalsSeverity.WARNING;

        return Patient.VitalsSeverity.NORMAL;
    }

    private void stopSoundTimer() {
        if (soundTimer != null) {
            soundTimer.stop();
            soundTimer = null;
        }
    }
}

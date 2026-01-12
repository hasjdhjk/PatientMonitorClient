package UI.Components;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;

public class DigitalTwinPanel extends JPanel {

    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private volatile boolean pageLoaded = false;

    // Keep the latest requested values so HomePage/StatusTracker can call in any order.
    private volatile Integer pendingPatientId = null;
    private volatile Vitals pendingVitals = null;

    // Your Jetty context path is /PatientServer
    private static final String DASHBOARD_URL =
            "https://bioeng-fguys-app.impaas.uk/digital_twin/dashboard.html";

    public DigitalTwinPanel() {
        setLayout(new BorderLayout());
        add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            engine = webView.getEngine();

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    pageLoaded = true;
                    System.out.println("Digital Twin HTML loaded: " + engine.getLocation());

                    // After load, try to flush any pending updates.
                    flushPending();
                }
            });

            System.out.println("Loading Digital Twin dashboard: " + DASHBOARD_URL);
            engine.load(DASHBOARD_URL);
            jfxPanel.setScene(new Scene(webView));
        });
    }

    /**
     * Tell the dashboard which patient id to poll from:
     * fetch("../api/patient?id=ID")
     */
    public void setSelectedPatientId(int patientId) {
        // Store latest request (so even if called before page is ready, it will apply later)
        pendingPatientId = patientId;

        Platform.runLater(this::trySendSelectedPatientId);
    }

    /**
     * Optional: push vitals directly into the dashboard (it also polls DB).
     */
    public void setVitals(int hr, int rr, int spo2, int sys, int dia, double temp) {
        int safeRr = rr > 0 ? rr : 12;
        int safeSpo2 = spo2 > 0 ? spo2 : 98;

        pendingVitals = new Vitals(hr, safeRr, safeSpo2, sys, dia, temp);

        Platform.runLater(this::trySendVitals);
    }

    // ===================== Internals =====================

    private void flushPending() {
        // Called after page load; attempt both updates.
        trySendSelectedPatientId();
        trySendVitals();
    }

    private void trySendSelectedPatientId() {
        if (!pageLoaded || engine == null) return;
        if (pendingPatientId == null) return;

        // Wait until the JS function exists to avoid "undefined is not a function".
        runWhenJsFunctionAvailable(
                "setSelectedPatientIdFromJava",
                () -> {
                    Integer id = pendingPatientId;
                    if (id == null) return;
                    String js = String.format("window.setSelectedPatientIdFromJava(%d);", id);
                    engine.executeScript(js);
                },
                30
        );
    }

    private void trySendVitals() {
        if (!pageLoaded || engine == null) return;
        if (pendingVitals == null) return;

        runWhenJsFunctionAvailable(
                "switchToVitalsFromJava",
                () -> {
                    Vitals v = pendingVitals;
                    if (v == null) return;
                    String js = String.format(
                            "window.switchToVitalsFromJava(%d,%d,%d,%d,%d,%.1f);",
                            v.hr, v.rr, v.spo2, v.sys, v.dia, v.temp
                    );
                    engine.executeScript(js);
                },
                30
        );
    }

    /**
     * Polls for `window.<fnName>` existence before running `action`.
     * This avoids JSException: TypeError: undefined is not a function
     * when Java calls into JS too early.
     */
    private void runWhenJsFunctionAvailable(String fnName, Runnable action, int retries) {
        if (!pageLoaded || engine == null) return;

        boolean exists;
        try {
            Object r = engine.executeScript(
                    "(typeof window !== 'undefined' && typeof window." + fnName + " === 'function')"
            );
            exists = Boolean.TRUE.equals(r);
        } catch (Exception e) {
            exists = false;
        }

        if (exists) {
            try {
                action.run();
            } catch (Exception e) {
                System.out.println("DigitalTwinPanel: JS call failed for " + fnName + ": " + e.getMessage());
            }
            return;
        }

        if (retries <= 0) {
            System.out.println("DigitalTwinPanel: JS function not ready: " + fnName);
            return;
        }

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(ev -> runWhenJsFunctionAvailable(fnName, action, retries - 1));
        pause.play();
    }

    private static final class Vitals {
        final int hr;
        final int rr;
        final int spo2;
        final int sys;
        final int dia;
        final double temp;

        Vitals(int hr, int rr, int spo2, int sys, int dia, double temp) {
            this.hr = hr;
            this.rr = rr;
            this.spo2 = spo2;
            this.sys = sys;
            this.dia = dia;
            this.temp = temp;
        }
    }
}
package UI.Components;

import Models.Vitals.LiveVitals;
import Services.PatientSimulatorService;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import NetWork.ServerConfig;
import NetWork.Session;

public class DigitalTwinPanel extends JPanel {

    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private volatile boolean pageLoaded = false;

    // Cached doctor email used to build the dashboard URL (?doctor=...)
    private volatile String doctorEmail = "demo";

    // Keep the latest requested values so HomePage/StatusTracker can call in any order.
    private volatile Integer pendingPatientId = null;
    private volatile Vitals pendingVitals = null;
    // Avoid spamming logs when dashboard JS functions are not ready
    private final java.util.Set<String> warnedJsFns = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    // Simulator driving dashboard
    private final LiveVitals simulatedVitals = new LiveVitals(0);
    private final PatientSimulatorService simulator = new PatientSimulatorService(simulatedVitals);
    private ScheduledExecutorService simExec;
    private static final String DASHBOARD_URL = ServerConfig.dashboardUrl();

    private static String jsString(String s) {
        if (s == null) return "''";
        // Escape backslashes and single quotes for JS single-quoted strings
        return "'" + s.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    // Sends doctor/session context values into the dashboard JavaScript environment when available.
    private void trySendContextToDashboard() {
        if (!pageLoaded || engine == null) return;

        String doctor = (doctorEmail == null || doctorEmail.isBlank()) ? Session.getDoctorEmail() : doctorEmail;
        String apiBase = ServerConfig.baseUrl();

        String js = "window.__doctor = " + jsString(doctor) + ";" +
                    "window.__apiBase = " + jsString(apiBase) + ";";
        try {
            engine.executeScript(js);
        } catch (Exception ignored) {}
    }

    // Requests a refresh of the dashboard JS context after login/logout or session changes
    public void refreshDashboardContext() {
        Platform.runLater(this::trySendContextToDashboard);
    }

    private String buildDashboardUrl() {
        String base = DASHBOARD_URL;
        if (base == null || base.isBlank()) return base;

        String doc = (doctorEmail == null || doctorEmail.isBlank()) ? "demo" : doctorEmail.trim();
        String encoded;
        try {
            encoded = URLEncoder.encode(doc, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encoded = "demo";
        }

        // Preserve any existing query string
        String joiner = base.contains("?") ? "&" : "?";
        return base + joiner + "doctor=" + encoded;
    }

    private void reloadDashboard() {
        Platform.runLater(() -> {
            if (engine == null) return;
            pageLoaded = false;
            engine.load(buildDashboardUrl());
        });
    }

    // Called by pages to update which doctor the dashboard should use
    public void setDoctorEmail(String email) {
        this.doctorEmail = (email == null || email.isBlank()) ? "demo" : email.trim();
        reloadDashboard();
    }

    // Creates the panel and loads the digital twin dashboard inside an embedded JavaFX WebView.
    public DigitalTwinPanel() {
        setLayout(new BorderLayout());
        add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            // Capture current session doctor before the first load (avoids defaulting to demo)
            doctorEmail = Session.getDoctorEmail();

            WebView webView = new WebView();
            engine = webView.getEngine();

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    pageLoaded = true;
                    System.out.println("Digital Twin HTML loaded: " + engine.getLocation());

                    // Provide logged-in doctor to the dashboard page
                    trySendContextToDashboard();

                    // Extra safety: if the dashboard exposes setDoctorFromJava, update it too
                    try {
                        engine.executeScript("window.setDoctorFromJava && window.setDoctorFromJava(" + jsString(doctorEmail) + ");");
                    } catch (Exception ignored) {}

                    // After load, try to flush any pending updates.
                    flushPending();

                    // Re-apply context after pending calls
                    trySendContextToDashboard();

                    // Start simulation pushing vitals into dashboard
                    startSimulation();
                }
            });

            String url = buildDashboardUrl();
            System.out.println("Loading Digital Twin dashboard: " + url);
            engine.load(url);
            jfxPanel.setScene(new Scene(webView));
        });
    }

    // Sets the patient ID so the dashboard knows which patient to display or poll for data.
    public void setSelectedPatientId(int patientId) {
        // Store latest request (so even if called before page is ready, it will apply later)
        pendingPatientId = patientId;
        Platform.runLater(this::trySendSelectedPatientId);
    }

    // push vitals directly into the dashboard
    public void setVitals(int hr, int rr, int spo2, int sys, int dia, double temp) {
        int safeRr = rr > 0 ? rr : 12;
        int safeSpo2 = spo2 > 0 ? spo2 : 98;

        pendingVitals = new Vitals(hr, safeRr, safeSpo2, sys, dia, temp);
        Platform.runLater(this::trySendVitals);
    }

    // Simulator
    private void startSimulation() {
        stopSimulation();

        simExec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DashboardSimulator");
            t.setDaemon(true);
            return t;
        });

        simExec.scheduleAtFixedRate(() -> {
            // Update simulated values
            simulator.update(1.0);

            int hr = (int) Math.round(simulatedVitals.getHeartRate());
            int rr = (int) Math.round(simulatedVitals.getRespRate());
            int spo2 = (int) Math.round(simulatedVitals.getSpO2());
            double temp = simulatedVitals.getTemperature();

            int sys = 120;
            int dia = 80;

            // Push into dashboard (this already waits for JS function existence)
            setVitals(hr, rr, spo2, sys, dia, temp);

        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopSimulation() {
        if (simExec != null) {
            simExec.shutdownNow();
            simExec = null;
        }
    }

    @Override
    public void removeNotify() {
        stopSimulation();
        super.removeNotify();
    }

    // Flushes any pending patient ID or vitals updates after the page has finished loading.
    private void flushPending() {
        // Called after page load; attempt both updates.
        trySendSelectedPatientId();
        trySendVitals();
    }

    // Attempts to send the currently selected patient ID into the dashboard JavaScript once ready.
    private void trySendSelectedPatientId() {
        if (!pageLoaded || engine == null) return;
        if (pendingPatientId == null) return;

        trySendContextToDashboard();

        // Wait until the JS function exists to avoid "undefined is not a function".
        runWhenJsFunctionAvailable(
                "setSelectedPatientIdFromJava",
                () -> {
                    Integer id = pendingPatientId;
                    if (id == null) return;
                    String js = String.format("window.setSelectedPatientIdFromJava(%d);", id);
                    engine.executeScript(js);
                },
                60
        );
    }

    // Attempts to send the latest vitals into the dashboard JavaScript once ready.
    private void trySendVitals() {
        if (!pageLoaded || engine == null) return;
        if (pendingVitals == null) return;

        trySendContextToDashboard();

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
                60
        );
    }

    // avoids JSException: TypeError: undefined is not a function when Java calls into JS too early.
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
            if (warnedJsFns.add(fnName)) {
                System.out.println("DigitalTwinPanel: JS function not ready: " + fnName);
            }
            return;
        }

        PauseTransition pause = new PauseTransition(Duration.millis(150));
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

        // Stores a single set of vitals values to be sent to the dashboard.
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
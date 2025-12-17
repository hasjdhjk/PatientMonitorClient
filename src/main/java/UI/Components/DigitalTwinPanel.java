package UI.Components;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class DigitalTwinPanel extends JPanel {

    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private boolean pageLoaded = false;

    public DigitalTwinPanel() {
        setLayout(new BorderLayout());
        add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            engine = webView.getEngine();

            // LISTEN FOR PAGE LOAD
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    pageLoaded = true;
                    System.out.println("Digital Twin HTML loaded");
                }
            });

            URL url = getClass().getResource("/digital_twin/dashboard.html");
            if (url == null) {
                throw new RuntimeException("Cannot find /digital_twin/dashboard.html");
            }

            engine.load(url.toExternalForm());
            jfxPanel.setScene(new Scene(webView));
        });
    }

    public void setVitals(int hr, int rr, int spo2, int sys, int dia, double temp) {
        Platform.runLater(() -> {
            if (!pageLoaded || engine == null) return;

            String js = String.format(
                    "window.switchToVitalsFromJava(%d,%d,%d,%d,%d,%.1f);",
                    hr, rr, spo2, sys, dia, temp
            );

            engine.executeScript(js);
        });
    }
}
package UI.Components;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class ECGPanel extends JPanel {

    private JFXPanel fxPanel = new JFXPanel();
    private WebEngine engine;

    public ECGPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(260, 120));
        add(fxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> {
            WebView webView = new WebView();
            engine = webView.getEngine();
            engine.load(getClass().getResource("/digital_twin/ecg.html").toExternalForm());
            fxPanel.setScene(new Scene(webView));
        });
    }

    public void setHeartRate(int hr) {
        Platform.runLater(() -> {
            if (engine != null) {
                engine.executeScript("setHeartRate(" + hr + ")");
            }
        });
    }
}

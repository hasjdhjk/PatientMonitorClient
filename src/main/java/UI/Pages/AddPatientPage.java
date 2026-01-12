package UI.Pages;

import UI.Components.Tiles.RoundedButton;

import UI.MainWindow;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.Tiles.BaseTile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class AddPatientPage extends JPanel {

    private final MainWindow mainWindow;

    private PlaceholderTextField givenNameField;
    private PlaceholderTextField familyNameField;
    private PlaceholderTextField idField;             // UI 里保留，但不再作为必填/不发给 server
    private PlaceholderTextField heartRateField;
    private PlaceholderTextField temperatureField;
    private PlaceholderTextField bloodPressureField;

    // ====== Server config ======
    private static final String SERVER_BASE = "http://localhost:8080/PatientServer";
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    // TODO: 将来接好登录后，从当前登录用户拿到 doctor username
    private static final String DOCTOR_USERNAME = "demo";

    public AddPatientPage(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Add Patient", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(245, 245, 245));
        wrapper.add(Box.createVerticalGlue());

        BaseTile form = new BaseTile(720, 800, 45, false);
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(720, 800));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        givenNameField = field(form, "Given Name", "Enter first name");
        familyNameField = field(form, "Family Name", "Enter last name");
        idField = field(form, "Patient ID (optional)", "Leave empty (DB auto ID)");
        heartRateField = field(form, "Heart Rate (bpm)", "e.g. 72");
        temperatureField = field(form, "Temperature (°C)", "e.g. 36.5");
        bloodPressureField = field(form, "Blood Pressure", "e.g. 120/80");

        form.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton cancelBtn = new RoundedButton("Cancel");
        RoundedButton addBtn = new RoundedButton("Add Patient");

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);
        form.add(buttonPanel);

        wrapper.add(form);
        wrapper.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        cancelBtn.addActionListener(e -> mainWindow.showHomePage());
        addBtn.addActionListener(e -> addPatientToServerAsync(addBtn));
    }

    private PlaceholderTextField field(JPanel parent, String labelText, String placeholder) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

        BaseTile tile = new BaseTile(600, 65, 40, false);
        tile.setMaximumSize(new Dimension(600, 65));
        tile.setLayout(new BorderLayout());
        tile.setAlignmentX(Component.LEFT_ALIGNMENT);

        PlaceholderTextField field = new PlaceholderTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 15));
        field.setOpaque(false);

        tile.add(field, BorderLayout.CENTER);

        parent.add(label);
        parent.add(tile);
        parent.add(Box.createVerticalStrut(15));

        return field;
    }

    private void addPatientToServerAsync(JButton addBtn) {
        String given = safeText(givenNameField.getText());
        String family = safeText(familyNameField.getText());
        String hrText = safeText(heartRateField.getText());
        String tempText = safeText(temperatureField.getText());
        String bp = safeText(bloodPressureField.getText());

        if (given.isEmpty() || family.isEmpty()) {
            error("Name cannot be empty");
            return;
        }

        int hr;
        double temp;

        try {
            hr = Integer.parseInt(hrText);
            temp = Double.parseDouble(tempText);
        } catch (NumberFormatException e) {
            error("Please enter valid numeric values for HR / Temp");
            return;
        }

        if (hr < 30 || hr > 200) {
            error("Heart rate must be between 30 and 200 bpm");
            return;
        }
        if (temp < 30 || temp > 45) {
            error("Temperature must be between 30 and 45 °C");
            return;
        }

        // very basic BP validation
        if (!bp.contains("/")) {
            error("Blood pressure format should be like 120/80");
            return;
        }

        addBtn.setEnabled(false);

        final int finalHr = hr;
        final double finalTemp = temp;
        final String finalBp = bp;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {

                // ✅ 构造“服务器期望字段名”的 JSON（不要用 Patient 类去 Gson）
                JsonObject body = new JsonObject();
                body.addProperty("doctor", DOCTOR_USERNAME);
                body.addProperty("givenname", given);
                body.addProperty("familyname", family);
                body.addProperty("heartrate", finalHr);
                body.addProperty("temperature", finalTemp);
                body.addProperty("bp", finalBp);

                // debug: 看看你到底发了什么（很关键）
                System.out.println("POST /api/patient body = " + body.toString());

                String url = SERVER_BASE + "/api/patient";

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() != 200) {
                    throw new RuntimeException("HTTP " + resp.statusCode() + " body=" + resp.body());
                }

                // 兼容旧 gson：new JsonParser().parse(...)
                JsonObject out = new JsonParser().parse(resp.body()).getAsJsonObject();

                if (out.has("ok") && !out.get("ok").getAsBoolean()) {
                    throw new RuntimeException(out.has("error") ? out.get("error").getAsString() : "Server error");
                }

                return null;
            }

            @Override
            protected void done() {
                addBtn.setEnabled(true);

                try {
                    get();

                    JOptionPane.showMessageDialog(AddPatientPage.this,
                            "Patient added to database successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    clear();
                    mainWindow.showHomePage();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    error("Failed to add patient to database: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    private String safeText(String s) {
        if (s == null) return "";
        s = s.trim();

        // 如果你的 PlaceholderTextField 会把 placeholder 当 text 返回，这里顺手过滤掉
        if (s.equalsIgnoreCase("Enter first name")) return "";
        if (s.equalsIgnoreCase("Enter last name")) return "";
        if (s.equalsIgnoreCase("Leave empty (DB auto ID)")) return "";
        if (s.equalsIgnoreCase("e.g. 72")) return "";
        if (s.equalsIgnoreCase("e.g. 36.5")) return "";
        if (s.equalsIgnoreCase("e.g. 120/80")) return "";

        return s;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void clear() {
        givenNameField.setText("");
        familyNameField.setText("");
        idField.setText("");
        heartRateField.setText("");
        temperatureField.setText("");
        bloodPressureField.setText("");
    }
}
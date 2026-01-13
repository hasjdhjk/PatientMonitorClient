package UI.Pages;

import UI.Components.RoundedButton;
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
import NetWork.ServerConfig;

public class AddPatientPage extends JPanel {

    private final MainWindow mainWindow;

    private PlaceholderTextField givenNameField;
    private PlaceholderTextField familyNameField;
    private PlaceholderTextField idField;

    private PlaceholderTextField genderField;
    private PlaceholderTextField ageField;
    private PlaceholderTextField bloodPressureField;

    // ====== Server config ======
    private static final HttpClient HTTP = HttpClient.newHttpClient();

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

        genderField = field(form, "Gender", "e.g. Female / Male / Other");
        ageField = field(form, "Age", "e.g. 32");
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
        String gender = safeText(genderField.getText());
        String ageText = safeText(ageField.getText());
        String bp = safeText(bloodPressureField.getText());

        if (given.isEmpty() || family.isEmpty()) {
            error("Name cannot be empty");
            return;
        }
        if (gender.isEmpty()) {
            error("Gender cannot be empty");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            error("Please enter a valid numeric age");
            return;
        }
        if (age < 0 || age > 130) {
            error("Age must be between 0 and 130");
            return;
        }

        if (!bp.contains("/")) {
            error("Blood pressure format should be like 120/80");
            return;
        }

        addBtn.setEnabled(false);

        final int finalAge = age;
        final String finalBp = bp;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {

                JsonObject body = new JsonObject();
                body.addProperty("doctor", NetWork.Session.getDoctorEmail());
                body.addProperty("givenname", given);
                body.addProperty("familyname", family);
                body.addProperty("gender", gender);
                body.addProperty("age", finalAge);
                body.addProperty("bp", finalBp);

                System.out.println("POST /api/patient body = " + body.toString());

                String url = ServerConfig.url("/api/patient");

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json; charset=utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());

                if (resp.statusCode() != 200) {
                    throw new RuntimeException("HTTP " + resp.statusCode() + " body=" + resp.body());
                }

                JsonObject out = JsonParser.parseString(resp.body()).getAsJsonObject();
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

        if (s.equalsIgnoreCase("Enter first name")) return "";
        if (s.equalsIgnoreCase("Enter last name")) return "";
        if (s.equalsIgnoreCase("Leave empty (DB auto ID)")) return "";
        if (s.equalsIgnoreCase("e.g. Female / Male / Other")) return "";
        if (s.equalsIgnoreCase("e.g. 32")) return "";
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
        genderField.setText("");
        ageField.setText("");
        bloodPressureField.setText("");
    }
}
package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;
import UI.Components.PlaceHolders.PlaceholderTextField;

import javax.swing.*;
import java.awt.*;

public class AddPatientPage extends JPanel {

    private MainWindow mainWindow;

    // ---------- UI components ----------
    private PlaceholderTextField givenNameField;
    private PlaceholderTextField familyNameField;
    private PlaceholderTextField idField;
    private PlaceholderTextField heartRateField;
    private PlaceholderTextField temperatureField;
    private PlaceholderTextField bloodPressureField;


    private JButton addButton;
    private JButton cancelButton;

    // ---------- constructor ----------
    public AddPatientPage(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initUI();
        initActions();
    }

    // ---------- build UI ----------
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Add Patient");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form panel (center)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Given name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Given Name:"), gbc);

        gbc.gridx = 1;
        givenNameField = new PlaceholderTextField("Please Eneter the Given Name");
        formPanel.add(givenNameField, gbc);

        // Row 2: Family name
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Family Name:"), gbc);

        gbc.gridx = 1;
        familyNameField = new PlaceholderTextField("Please Enter family name");
        formPanel.add(familyNameField, gbc);

        // Row 3: Patient ID
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Patient ID:"), gbc);

        gbc.gridx = 1;
        idField = new PlaceholderTextField("e.g. 100001");
        formPanel.add(idField, gbc);

        // Row 4: Heart rate
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Heart Rate (bpm):"), gbc);

        gbc.gridx = 1;
        heartRateField = new PlaceholderTextField("e.g. 72");
        formPanel.add(heartRateField, gbc);

        // Row 5: Temperature
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Temperature (°C):"), gbc);

        gbc.gridx = 1;
        temperatureField = new PlaceholderTextField("e.g. 36.5");
        formPanel.add(temperatureField, gbc);

        // Row 6: Blood pressure
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Blood Pressure (e.g. 120/80):"), gbc);

        gbc.gridx = 1;
        BaseTile bpTile = new BaseTile(650, 75, 50, false);
        bloodPressureField = new PlaceholderTextField("Enter email");
        bpTile.add(bloodPressureField);
        formPanel.add(bloodPressureField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons (bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));

        cancelButton = new JButton("Cancel");
        addButton = new JButton("Add Patient");

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // ---------- actions ----------
    private void initActions() {

        // Cancel -> go back home
        cancelButton.addActionListener(e -> mainWindow.showHomePage());

        // Add patient
        addButton.addActionListener(e -> {

            try {
                // Read inputs
                String givenName = givenNameField.getText().trim();
                String familyName = familyNameField.getText().trim();
                int id = Integer.parseInt(idField.getText().trim());
                int heartRate = Integer.parseInt(heartRateField.getText().trim());
                double temperature = Double.parseDouble(temperatureField.getText().trim());
                String bloodPressure = bloodPressureField.getText().trim();

                // Validation
                if (givenName.isEmpty() || familyName.isEmpty()) {
                    showError("Name cannot be empty");
                    return;
                }

                if (heartRate < 30 || heartRate > 200) {
                    showError("Heart rate must be between 30 and 200 bpm");
                    return;
                }

                if (temperature < 30 || temperature > 45) {
                    showError("Temperature must be between 30 and 45 °C");
                    return;
                }

                if (bloodPressure.isEmpty() || !bloodPressure.contains("/")) {
                    showError("Blood pressure format should be like 120/80");
                    return;
                }

                // Create patient
                Patient patient = new Patient(
                        id,
                        givenName,
                        familyName,
                        heartRate,
                        temperature,
                        bloodPressure
                );

                // Save
                AddedPatientDB.addPatient(patient);

                JOptionPane.showMessageDialog(
                        this,
                        "Patient added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Back to home
                clearFields();
                mainWindow.showHomePage();

            } catch (NumberFormatException ex) {
                showError("Please enter valid numeric values");
            }
        });
    }

    // ---------- helpers ----------
    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void clearFields() {
        givenNameField.setText("");
        familyNameField.setText("");
        idField.setText("");
        heartRateField.setText("");
        temperatureField.setText("");
        bloodPressureField.setText("");
    }
}

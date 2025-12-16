package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class AddPatientPage extends JPanel {

    private final MainWindow mainWindow;

    private PlaceholderTextField givenNameField;
    private PlaceholderTextField familyNameField;
    private PlaceholderTextField idField;
    private PlaceholderTextField heartRateField;
    private PlaceholderTextField temperatureField;
    private PlaceholderTextField bloodPressureField;

    public AddPatientPage(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initUI();
    }

    // initialize ui
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // title
        JLabel title = new JLabel("Add Patient", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // wrapper
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(new Color(245, 245, 245));

        wrapper.add(Box.createVerticalGlue());

        // container
        BaseTile form = new BaseTile(720, 800, 45, false);
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(720, 800));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Fields (left aligned)
        givenNameField = field(form, "Given Name", "Enter first name");
        familyNameField = field(form, "Family Name", "Enter last name");
        idField = field(form, "Patient ID", "e.g. 100001");
        heartRateField = field(form, "Heart Rate (bpm)", "e.g. 72");
        temperatureField = field(form, "Temperature (°C)", "e.g. 36.5");
        bloodPressureField = field(form, "Blood Pressure", "e.g. 120/80");

        form.add(Box.createVerticalStrut(25));

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Cancel");
        JButton addBtn = new JButton("Add Patient");

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);
        form.add(buttonPanel);

        wrapper.add(form);
        wrapper.add(Box.createVerticalGlue());

        // scroll
        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // button action
        cancelBtn.addActionListener(e -> mainWindow.showHomePage());
        addBtn.addActionListener(e -> addPatient());
    }

    // helpers
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

    private void addPatient() {
        try {
            String given = givenNameField.getText().trim();
            String family = familyNameField.getText().trim();
            int id = Integer.parseInt(idField.getText().trim());
            int hr = Integer.parseInt(heartRateField.getText().trim());
            double temp = Double.parseDouble(temperatureField.getText().trim());
            String bp = bloodPressureField.getText().trim();

            if (given.isEmpty() || family.isEmpty()) {
                error("Name cannot be empty");
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

            if (!bp.contains("/")) {
                error("Blood pressure format should be like 120/80");
                return;
            }

            Patient patient = new Patient(id, given, family, hr, temp, bp);
            AddedPatientDB.addPatient(patient);

            JOptionPane.showMessageDialog(this,
                    "Patient added successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            clear();
            mainWindow.showHomePage();

        } catch (NumberFormatException e) {
            error("Please enter valid numeric values");
        }
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
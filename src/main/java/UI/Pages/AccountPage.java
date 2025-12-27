package UI.Pages;

import UI.Components.PatientRecordsPanel;
import UI.Components.PlaceHolders.PlaceholderPasswordField;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.Tiles.BaseTile;
import UI.Components.Tiles.RoundedButton;
import UI.MainWindow;
import Utilities.SettingManager;

import javax.swing.*;
import java.awt.*;

public class AccountPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PatientRecordsPanel recordsPanel;

    private final Color themeBg = new Color(245, 247, 250);

    private Container formRoot;

    public AccountPage(MainWindow window) {

        setLayout(new BorderLayout());
        setBackground(themeBg);

        // ----------- CardLayout Panel -----------
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(themeBg);

        // Main settings page (AddPatientPage style)
        JPanel mainSettings = buildMainSettingsPage();

        recordsPanel = new PatientRecordsPanel(() ->
                cardLayout.show(cardPanel, "settings")
        );

        cardPanel.add(mainSettings, "settings");
        cardPanel.add(recordsPanel, "records");

        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "settings");
    }

    // ======================== Main Settings Page  ========================
    private JPanel buildMainSettingsPage() {
        boolean dark = new SettingManager().isDarkMode();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(themeBg);

        // title (same style as AddPatientPage)
        JLabel title = new JLabel("Doctor Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        root.add(title, BorderLayout.NORTH);

        // wrapper
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(themeBg);

        wrapper.add(Box.createVerticalGlue());

        // container (BaseTile form like AddPatientPage)
        BaseTile form = new BaseTile(720, 800, 45, false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(720, 800));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        form.setBackground(Color.WHITE);

        // Keep reference for theme updates
        this.formRoot = form;

        // Fields (same style as AddPatientPage)
        PlaceholderTextField nameField = new PlaceholderTextField("Raymond");
        PlaceholderTextField ageField = new PlaceholderTextField("20");
        PlaceholderTextField idField = new PlaceholderTextField("DOC123456");
        PlaceholderTextField specialtyField = new PlaceholderTextField("Cardiac Surgeon");
        PlaceholderTextField emailField = new PlaceholderTextField("doctor@mail.com");
        PlaceholderPasswordField passwordField = new PlaceholderPasswordField("Enter password");

        addField(form, "Name", nameField);
        addField(form, "Age", ageField);
        addField(form, "ID Number", idField);
        addField(form, "Specialty", specialtyField);
        addField(form, "Email", emailField);
        addPasswordField(form, "Password", passwordField);

        form.add(Box.createVerticalStrut(25));

        // buttons (keep RoundedButton)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton recordsBtn = new RoundedButton("View Patient Records");
        recordsBtn.setRadius(18);
        recordsBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        recordsBtn.setBackground(new Color(46, 134, 193));
        recordsBtn.setForeground(Color.WHITE);

        RoundedButton saveBtn = new RoundedButton("Save Settings");
        saveBtn.setRadius(18);
        saveBtn.setFont(new Font("Arial", Font.PLAIN, 18));

        buttonPanel.add(recordsBtn);
        buttonPanel.add(saveBtn);
        form.add(buttonPanel);

        wrapper.add(form);
        wrapper.add(Box.createVerticalGlue());

        // scroll
        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scrollPane, BorderLayout.CENTER);

        // actions
        saveBtn.addActionListener(e -> saveDoctorSettings());

        recordsBtn.addActionListener(e -> {
            recordsPanel.reloadFromDisk();
            cardLayout.show(cardPanel, "records");
        });

        // apply theme
        fixInputColors(form, dark);

        return root;
    }

    // ======================== Add fields ========================
    private void addField(JPanel parent, String labelText, PlaceholderTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

        BaseTile tile = new BaseTile(600, 65, 40, false);
        tile.setMaximumSize(new Dimension(600, 65)); // fixed width like AddPatientPage
        tile.setLayout(new BorderLayout());
        tile.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 15));
        field.setOpaque(false);

        tile.add(field, BorderLayout.CENTER);

        parent.add(label);
        parent.add(tile);
        parent.add(Box.createVerticalStrut(15));
    }

    private void addPasswordField(JPanel parent, String labelText, PlaceholderPasswordField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));

        BaseTile tile = new BaseTile(600, 65, 40, false);
        tile.setMaximumSize(new Dimension(600, 65)); // fixed width like AddPatientPage
        tile.setLayout(new BorderLayout());
        tile.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 15));
        field.setOpaque(false);

        tile.add(field, BorderLayout.CENTER);

        parent.add(label);
        parent.add(tile);
        parent.add(Box.createVerticalStrut(15));
    }

    // ======================== Save Doctor Info ========================
    private void saveDoctorSettings() {
        JOptionPane.showMessageDialog(this, "Settings saved.");
    }

    // ======================== Theme Apply ========================
    private void fixInputColors(Container root, boolean dark) {
        Color inputFg = dark ? new Color(235, 235, 235) : Color.BLACK;
        Color tileBg = dark ? new Color(45, 48, 56) : Color.WHITE;

        for (Component c : root.getComponents()) {
            if (c instanceof BaseTile bt) {
                bt.setBackground(tileBg);
            }

            if (c instanceof JTextField tf) {
                tf.setForeground(inputFg);
                tf.setCaretColor(inputFg);
            } else if (c instanceof JPasswordField pf) {
                pf.setForeground(inputFg);
                pf.setCaretColor(inputFg);
            }

            if (c instanceof Container child) {
                fixInputColors(child, dark);
            }
        }
    }

    public void applyThemeToInputs() {
        boolean dark = new SettingManager().isDarkMode();
        if (formRoot != null) {
            fixInputColors(formRoot, dark);
        }
    }
}
package UI.Pages;

import Models.DoctorProfile;
import Services.DoctorProfileService;
import UI.Components.PatientRecordsPanel;
import UI.Components.PlaceHolders.PlaceholderPasswordField;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.RoundedButton;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;
import Utilities.SettingManager;

import javax.swing.*;
import java.awt.*;

public class AccountPage extends JPanel {

    private final MainWindow window;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PatientRecordsPanel recordsPanel;

    private final Color themeBg = new Color(245, 247, 250);
    private Container formRoot;

    // Form fields
    private PlaceholderTextField firstNameField, lastNameField, idField, ageField, specialtyField, emailField;
    private PlaceholderPasswordField passwordField;

    // Real profile data source
    private final DoctorProfileService profileService = new DoctorProfileService();
    private DoctorProfile profile;

    public AccountPage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setBackground(themeBg);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(themeBg);

        JPanel accountMain = buildAccountMain();

        recordsPanel = new PatientRecordsPanel(() ->
                cardLayout.show(cardPanel, "account")
        );

        cardPanel.add(accountMain, "account");
        cardPanel.add(recordsPanel, "records");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "account");
    }

    // ===================== MAIN ACCOUNT PAGE =====================
    private JPanel buildAccountMain() {
        boolean dark = new SettingManager().isDarkMode();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(themeBg);

        // ---------- Header ----------
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(themeBg);
        header.setBorder(BorderFactory.createEmptyBorder(30, 60, 20, 60));

        JLabel title = new JLabel("Doctor Account");
        title.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel subtitle = new JLabel("Manage your clinician profile and access patient records");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(Color.GRAY);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        // ---------- Scrollable content ----------
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(themeBg);

        // ---------- Main card ----------
        BaseTile form = new BaseTile(1200, 700, 45, false);
        form.setMaximumSize(new Dimension(1200, 700));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(0, 40, 70, 40));
        form.setBackground(Color.WHITE);

        this.formRoot = form;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 20, 14, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // ---------- Fields (load real profile) ----------
        this.profile = profileService.load();

        this.firstNameField = new PlaceholderTextField(profile.getFirstName());
        this.lastNameField  = new PlaceholderTextField(profile.getLastName());
        this.idField = new PlaceholderTextField(profile.getIdNumber());
        this.ageField = new PlaceholderTextField(String.valueOf(profile.getAge()));
        this.specialtyField = new PlaceholderTextField(profile.getSpecialty());
        this.emailField = new PlaceholderTextField(profile.getEmail());
        this.passwordField = new PlaceholderPasswordField("Enter password"); // 不持久化

        int row = 0;

        addField(form, "First Name", firstNameField, gbc, 0, row);
        addField(form, "Last Name", lastNameField, gbc, 1, row++);

        addField(form, "ID Number", this.idField, gbc, 1, row++);

        addField(form, "Age", this.ageField, gbc, 0, row);
        addField(form, "Specialty", this.specialtyField, gbc, 1, row++);

        addField(form, "Email", this.emailField, gbc, 0, row);
        addField(form, "Password", this.passwordField, gbc, 1, row++);

        // ---------- Buttons ----------
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        buttons.setOpaque(false);

        RoundedButton recordsBtn = new RoundedButton("View Patient Records");
        recordsBtn.setRadius(18);
        recordsBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        recordsBtn.setBackground(new Color(46, 134, 193));
        recordsBtn.setForeground(Color.WHITE);

        RoundedButton saveBtn = new RoundedButton("Save Settings");
        saveBtn.setRadius(18);
        saveBtn.setFont(new Font("Arial", Font.PLAIN, 18));

        buttons.add(recordsBtn);
        buttons.add(saveBtn);

        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(35, 0, 0, 0);
        form.add(buttons, gbc);

        content.add(form);
        content.add(Box.createVerticalStrut(40));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        // ---------- Actions ----------
        saveBtn.addActionListener(e -> saveDoctorProfile());

        recordsBtn.addActionListener(e -> {
            recordsPanel.reloadFromDisk();
            cardLayout.show(cardPanel, "records");
        });

        fixInputColors(form, dark);
        return root;
    }

    // ===================== FIELD BUILDER =====================
    private void addField(
            JPanel parent,
            String labelText,
            JComponent field,
            GridBagConstraints gbc,
            int col,
            int row
    ) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridx = col;
        gbc.gridy = row * 2;
        parent.add(label, gbc);

        BaseTile tile = new BaseTile(460, 65, 38, false);
        tile.setLayout(new BorderLayout());
        tile.setBackground(Color.WHITE);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 18));
        field.setOpaque(false);

        gbc.gridy = row * 2 + 1;
        parent.add(tile, gbc);

        tile.add(field, BorderLayout.CENTER);
    }

    // ===================== SAVE REAL PROFILE =====================
    private void saveDoctorProfile() {
        if (profile == null) profile = DoctorProfile.defaults();

        String firstName = safe(firstNameField);
        String lastName = safe(lastNameField);
        String id = safe(idField);
        String specialty = safe(specialtyField);
        String email = safe(emailField);

        int age;
        try {
            age = Integer.parseInt(safe(ageField));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setSpecialty(specialty);

        profile.setIdNumber(id);
        profile.setAge(age);
        profile.setEmail(email);

        profileService.save(profile);

        window.getTopBar().updateDoctorInfo(
                profile.getFullName(),
                profile.getSpecialty()
        );
        JOptionPane.showMessageDialog(this, "Profile saved.");
    }

    private String safe(JTextField tf) {
        return tf == null ? "" : tf.getText().trim();
    }

    // ===================== RELOAD WHEN PAGE SHOWN =====================
    public void reloadProfile() {
        DoctorProfile p = profileService.load();
        this.profile = p;

        if (firstNameField != null) firstNameField.setText(p.getFirstName());
        if (lastNameField != null) lastNameField.setText(p.getLastName());
        if (idField != null) idField.setText(p.getIdNumber());
        if (ageField != null) ageField.setText(String.valueOf(p.getAge()));
        if (specialtyField != null) specialtyField.setText(p.getSpecialty());
        if (emailField != null) emailField.setText(p.getEmail());
    }

    // ===================== THEME =====================
    private void fixInputColors(Container root, boolean dark) {
        Color inputFg = dark ? new Color(235, 235, 235) : Color.BLACK;
        Color tileBg = dark ? new Color(45, 48, 56) : Color.WHITE;

        for (Component c : root.getComponents()) {
            if (c instanceof BaseTile bt) bt.setBackground(tileBg);
            if (c instanceof JTextField tf) {
                tf.setForeground(inputFg);
                tf.setCaretColor(inputFg);
            }
            if (c instanceof JPasswordField pf) {
                pf.setForeground(inputFg);
                pf.setCaretColor(inputFg);
            }
            if (c instanceof Container child) fixInputColors(child, dark);
        }
    }

    public void applyThemeToInputs() {
        boolean dark = new SettingManager().isDarkMode();
        if (formRoot != null) fixInputColors(formRoot, dark);
    }
}
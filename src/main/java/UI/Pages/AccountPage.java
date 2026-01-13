package UI.Pages;

import Models.DoctorProfile;
import Services.AccountMetaService;
import Services.DoctorProfileService;
import UI.Components.PatientRecordsPanel;
import UI.Components.PlaceHolders.PlaceholderPasswordField;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.RoundedButton;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;
import Utilities.SettingManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AccountPage extends JPanel {

    private final MainWindow window;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PatientRecordsPanel recordsPanel;

    private final Color themeBg = new Color(245, 247, 250);
    private Container formRoot;

    // Existing profile
    private final DoctorProfileService profileService = new DoctorProfileService();
    private DoctorProfile profile;

    // New meta (createdAt/lastLogin/avatar)
    private final AccountMetaService metaService = new AccountMetaService();
    private AccountMetaService.Meta meta;

    // UI fields (screenshot-like)
    private PlaceholderTextField fullNameField;
    private PlaceholderTextField emailField;
    private PlaceholderTextField organizationField;
    private JComboBox<String> roleCombo;
    private PlaceholderPasswordField passwordField; // optional, not persisted

    private RoundedButton saveBtn;
    private RoundedButton recordsBtn;
    private JButton changePhotoBtn;
    private JButton removePhotoBtn;

    private JLabel headerName;
    private JLabel headerEmail;
    private StatusBadge statusBadge;
    private AvatarCircle avatarCircle;

    private MetadataCard userIdCard;
    private MetadataCard lastLoginCard;
    private MetadataCard createdCard;

    // state for dirty check
    private String originalFullName = "";
    private String originalOrg = "";
    private String originalRole = "";

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

    // ===================== MAIN ACCOUNT PAGE (screenshot version) =====================
    private JPanel buildAccountMain() {
        boolean dark = new SettingManager().isDarkMode();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(themeBg);

        // ---------- Header ----------
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(themeBg);
        header.setBorder(BorderFactory.createEmptyBorder(30, 60, 18, 60));

        JLabel title = new JLabel("Account Settings");
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(new Color(17, 24, 39));

        JLabel subtitle = new JLabel("Manage your account information, security settings, and preferences");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(107, 114, 128));

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        root.add(header, BorderLayout.NORTH);

        // ---------- Scrollable content ----------
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(themeBg);

        // ========== Load data ==========
        this.profile = profileService.load();
        this.meta = metaService.loadOrInit();

        // ---------- Main white card ----------
        BaseTile card = new BaseTile(1200, 600, 45, false);
        card.setMaximumSize(new Dimension(1200, 600));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        this.formRoot = card;

        // ----- top row inside card: avatar + name/email + buttons + status -----
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        // Left section: avatar (far left) + name/email centered to avatar + buttons below avatar
        JPanel leftProfile = new JPanel(new GridBagLayout());
        leftProfile.setOpaque(false);

        GridBagConstraints lp = new GridBagConstraints();
        lp.insets = new Insets(0, 0, 0, 0);
        lp.fill = GridBagConstraints.NONE;
        lp.anchor = GridBagConstraints.WEST;

        // ---- Avatar ----
        avatarCircle = new AvatarCircle(74);
        avatarCircle.setImagePath(meta.avatarPath);

        lp.gridx = 0;
        lp.gridy = 0;
        lp.weightx = 0;
        lp.weighty = 0;
        leftProfile.add(avatarCircle, lp);

        // ---- Name/Email: vertically centered relative to avatar ----
        JPanel nameCol = new JPanel();
        nameCol.setOpaque(false);
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS));
        nameCol.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        headerName = new JLabel(profile.getFullName());
        headerName.setFont(new Font("Arial", Font.BOLD, 22));
        headerName.setForeground(new Color(17, 24, 39));

        headerEmail = new JLabel(profile.getEmail());
        headerEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        headerEmail.setForeground(new Color(75, 85, 99));

        // Add vertical glue so the block (name+email) is centered within the avatar height
        nameCol.add(Box.createVerticalGlue());
        nameCol.add(headerName);
        nameCol.add(Box.createVerticalStrut(4));
        nameCol.add(headerEmail);
        nameCol.add(Box.createVerticalGlue());

        lp.gridx = 0;
        lp.gridy = 0;
        lp.weightx = 0;
        lp.fill = GridBagConstraints.HORIZONTAL;
        lp.insets = new Insets(0, 90, 0, 0); // gap between avatar and text
        leftProfile.add(nameCol, lp);

        // ---- Buttons: lower, under avatar only (doesn't affect name/email alignment) ----
        JPanel photoBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        photoBtnRow.setOpaque(false);
        changePhotoBtn = smallOutlineButton("Change Photo");
        removePhotoBtn = smallOutlineButton("Remove");
        photoBtnRow.add(changePhotoBtn);
        photoBtnRow.add(removePhotoBtn);

        lp.gridx = 0;
        lp.gridy = 1;
        lp.weightx = 0;
        lp.fill = GridBagConstraints.NONE;
        lp.insets = new Insets(14, 0, 0, 0); // push buttons down
        leftProfile.add(photoBtnRow, lp);

        // filler so row 1 doesn't stretch text column
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        lp.gridx = 1;
        lp.gridy = 1;
        lp.weightx = 1;
        lp.fill = GridBagConstraints.HORIZONTAL;
        lp.insets = new Insets(14, 0, 0, 0);
        leftProfile.add(filler, lp);

        topRow.add(leftProfile, BorderLayout.WEST);

        JPanel rightTop = new JPanel();
        rightTop.setOpaque(false);
        rightTop.setLayout(new BoxLayout(rightTop, BoxLayout.Y_AXIS));

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgeRow.setOpaque(false);
        statusBadge = new StatusBadge("Active");
        badgeRow.add(statusBadge);

        JPanel rightBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightBtnRow.setOpaque(false);

        recordsBtn = new RoundedButton("Patient Records");
        recordsBtn.setRadius(18);
        recordsBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        recordsBtn.setColors(
                new Color(243, 244, 246),
                new Color(229, 231, 235),
                new Color(209, 213, 219),
                new Color(31, 41, 55)
        );

        rightBtnRow.add(recordsBtn);

        rightTop.add(badgeRow);
        rightTop.add(rightBtnRow);

        topRow.add(rightTop, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);

        // ----- form area -----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Fields mapped to your model:
        // - FULL NAME -> first+last
        // - EMAIL -> email (read-only recommended)
        // - ORGANIZATION -> specialty (closest field you have)
        // - ROLE -> dropdown (not in model; we store only UI state by default)
        fullNameField = new PlaceholderTextField(profile.getFullName());
        emailField = new PlaceholderTextField(profile.getEmail());
        organizationField = new PlaceholderTextField(profile.getSpecialty());
        passwordField = new PlaceholderPasswordField("Enter password"); // not persisted

        roleCombo = new JComboBox<>(new String[]{
                "Clinician", "Consultant", "Surgeon", "Nurse", "Admin", "Viewer", "Earth Science"
        });
        String initialRole = (meta != null && meta.role != null && !meta.role.isBlank()) ? meta.role : "Clinician";
        roleCombo.setSelectedItem(initialRole);
        roleCombo.setOpaque(true);
        roleCombo.setBackground(Color.WHITE);

        int row = 0;
        // row 0
        addLabeledInput(form, "FULL NAME", fullNameField, gbc, 0, row);
        addLabeledInput(form, "EMAIL ADDRESS", emailField, gbc, 1, row++);
        // row 1
        addLabeledInput(form, "ORGANIZATION", organizationField, gbc, 0, row);
        addLabeledCombo(form, "ROLE", roleCombo, gbc, 1, row++);

        // Optional: keep password row (like settings), you can remove if not needed
        addLabeledInput(form, "PASSWORD", passwordField, gbc, 0, row);
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        gbc.gridx = 1;
        gbc.gridy = row * 2;
        form.add(spacer, gbc);
        row++;

        // Save button row
        JPanel saveRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        saveRow.setOpaque(false);
        saveRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        saveBtn = new RoundedButton("Save Changes");
        saveBtn.setRadius(18);
        saveBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        saveBtn.setColors(
                new Color(37, 99, 235),
                new Color(59, 130, 246),
                new Color(29, 78, 216),
                Color.WHITE
        );
        saveBtn.setEnabled(false);

        saveRow.add(saveBtn);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(form, BorderLayout.CENTER);
        centerWrap.add(saveRow, BorderLayout.SOUTH);

        card.add(centerWrap, BorderLayout.CENTER);

        content.add(card);
        content.add(Box.createVerticalStrut(22));

        // ---------- Metadata card ----------
        BaseTile metaCard = new BaseTile(1200, 260, 45, false);
        metaCard.setMaximumSize(new Dimension(1200, 260));
        metaCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        metaCard.setLayout(new BorderLayout());
        metaCard.setBackground(Color.WHITE);
        metaCard.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JLabel metaTitle = new JLabel("Account Metadata");
        metaTitle.setFont(new Font("Arial", Font.BOLD, 20));
        metaTitle.setForeground(new Color(17, 24, 39));
        metaCard.add(metaTitle, BorderLayout.NORTH);

        JPanel metaGrid = new JPanel(new GridLayout(1, 3, 18, 0));
        metaGrid.setOpaque(false);
        metaGrid.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        String userId = profile.getIdNumber() == null || profile.getIdNumber().isBlank()
                ? "DOC-UNKNOWN" : profile.getIdNumber();

        userIdCard = new MetadataCard("USER ID", userId, "");
        lastLoginCard = new MetadataCard("LAST LOGIN", humanizeAgo(meta.lastLogin), formatDateTime(meta.lastLogin));
        createdCard = new MetadataCard("ACCOUNT CREATED", formatDate(meta.createdAt), daysAgo(meta.createdAt) + " days ago");

        metaGrid.add(userIdCard);
        metaGrid.add(lastLoginCard);
        metaGrid.add(createdCard);

        metaCard.add(metaGrid, BorderLayout.CENTER);

        content.add(metaCard);
        content.add(Box.createVerticalStrut(40));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);

        // ---------- Behaviour ----------
        // email should be read-only like screenshot
        emailField.setEditable(false);
        emailField.setOpaque(false);

        // store original for dirty check
        originalFullName = safe(fullNameField);
        originalOrg = safe(organizationField);
        originalRole = Objects.toString(roleCombo.getSelectedItem(), "");

        // listeners => dirty check
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshSaveEnabled(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshSaveEnabled(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshSaveEnabled(); }
        };
        fullNameField.getDocument().addDocumentListener(dl);
        organizationField.getDocument().addDocumentListener(dl);
        roleCombo.addActionListener(e -> refreshSaveEnabled());

        saveBtn.addActionListener(e -> saveDoctorProfileFromScreenshotForm());

        recordsBtn.addActionListener(e -> {
            recordsPanel.reloadFromDisk();
            cardLayout.show(cardPanel, "records");
        });

        changePhotoBtn.addActionListener(e -> onChangePhoto());
        removePhotoBtn.addActionListener(e -> onRemovePhoto());

        fixInputColors(card, dark);
        return root;
    }

    // ===================== Save logic (maps back to your DoctorProfile) =====================
    private void saveDoctorProfileFromScreenshotForm() {
        if (profile == null) profile = DoctorProfile.defaults();

        String full = safe(fullNameField);
        String[] parts = full.trim().split("\\s+", 2);
        String first = parts.length > 0 ? parts[0] : "";
        String last = parts.length > 1 ? parts[1] : "";

        profile.setFirstName(first);
        profile.setLastName(last);

        // Map ORGANIZATION -> specialty (since your model has no org field)
        profile.setSpecialty(safe(organizationField));

        // Email is read-only in UI; keep it
        profileService.save(profile);

        // Update top UI
        headerName.setText(profile.getFullName());
        headerEmail.setText(profile.getEmail());

        // Update TopBar
        window.getTopBar().updateDoctorInfo(profile.getFullName(), profile.getSpecialty());

        // Save selected role to meta and persist
        String selectedRole = Objects.toString(roleCombo.getSelectedItem(), "");
        meta.role = selectedRole;
        metaService.saveRole(selectedRole);

        // Update originals & button
        originalFullName = safe(fullNameField);
        originalOrg = safe(organizationField);
        originalRole = Objects.toString(roleCombo.getSelectedItem(), "");
        refreshSaveEnabled();

        JOptionPane.showMessageDialog(this, "Profile saved.");
    }

    private void refreshSaveEnabled() {
        boolean dirty =
                !Objects.equals(safe(fullNameField), originalFullName) ||
                        !Objects.equals(safe(organizationField), originalOrg) ||
                        !Objects.equals(Objects.toString(roleCombo.getSelectedItem(), ""), originalRole);

        saveBtn.setEnabled(dirty);
        // optional: gray-out style could be added here if your RoundedButton supports it
    }

    // ===================== Avatar actions =====================
    private void onChangePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose an avatar image");
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File chosen = chooser.getSelectedFile();
        try {
            // save to ~/.patientmonitor/avatar.png
            Path dir = metaService.getDirPath();
            Files.createDirectories(dir);
            Path out = dir.resolve("avatar.png");

            // copy (we keep extension .png; if user selects jpg it still works for ImageIO reading often,
            // but safer would be re-encode; keeping minimal: just copy)
            Files.copy(chosen.toPath(), out, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            meta.avatarPath = out.toString();
            metaService.saveAvatarPath(meta.avatarPath);

            avatarCircle.setImagePath(meta.avatarPath);
            avatarCircle.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to set avatar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemovePhoto() {
        try {
            if (meta.avatarPath != null) {
                Files.deleteIfExists(Path.of(meta.avatarPath));
            }
        } catch (Exception ignored) {}

        meta.avatarPath = null;
        metaService.saveAvatarPath(null);

        avatarCircle.setImagePath(null);
        avatarCircle.repaint();
    }

    // ===================== Reload when page shown =====================
    public void reloadProfile() {
        DoctorProfile p = profileService.load();
        this.profile = p;

        if (fullNameField != null) fullNameField.setText(p.getFullName());
        if (organizationField != null) organizationField.setText(p.getSpecialty());
        if (emailField != null) emailField.setText(p.getEmail());
        if (headerName != null) headerName.setText(p.getFullName());
        if (headerEmail != null) headerEmail.setText(p.getEmail());

        // update user id card if you want
        if (userIdCard != null) {
            String userId = p.getIdNumber() == null || p.getIdNumber().isBlank() ? "DOC-UNKNOWN" : p.getIdNumber();
            userIdCard.setValueText(userId);
        }

        // refresh meta
        meta = metaService.loadOrInit();
        if (lastLoginCard != null) {
            lastLoginCard.setValueText(humanizeAgo(meta.lastLogin));
            lastLoginCard.setSubText(formatDateTime(meta.lastLogin));
        }
        if (createdCard != null) {
            createdCard.setValueText(formatDate(meta.createdAt));
            createdCard.setSubText(daysAgo(meta.createdAt) + " days ago");
        }

        // re-apply saved role to combo
        if (roleCombo != null) {
            String initialRole = (meta != null && meta.role != null && !meta.role.isBlank()) ? meta.role : Objects.toString(roleCombo.getSelectedItem(), "Clinician");
            roleCombo.setSelectedItem(initialRole);
        }

        // originals reset
        originalFullName = safe(fullNameField);
        originalOrg = safe(organizationField);
        originalRole = Objects.toString(roleCombo.getSelectedItem(), "");
        refreshSaveEnabled();
    }

    // ===================== Field builders =====================
    private void addLabeledInput(JPanel parent, String labelText, JComponent field,
                                 GridBagConstraints gbc, int col, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(107, 114, 128));

        gbc.gridx = col;
        gbc.gridy = row * 2;
        parent.add(label, gbc);

        BaseTile tile = new BaseTile(520, 62, 38, false);
        tile.setLayout(new BorderLayout());
        tile.setBackground(Color.WHITE);

        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        field.setOpaque(false);

        gbc.gridy = row * 2 + 1;
        parent.add(tile, gbc);
        tile.add(field, BorderLayout.CENTER);
    }

    private void addLabeledCombo(JPanel parent, String labelText, JComboBox<String> combo,
                                 GridBagConstraints gbc, int col, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(107, 114, 128));

        gbc.gridx = col;
        gbc.gridy = row * 2;
        parent.add(label, gbc);

        // Use a simple bordered panel instead of BaseTile to avoid clipping the combo UI
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setOpaque(true);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        combo.setFont(new Font("Arial", Font.PLAIN, 16));
        combo.setOpaque(true);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setPreferredSize(new Dimension(520, 44));

        box.add(combo, BorderLayout.CENTER);

        gbc.gridy = row * 2 + 1;
        parent.add(box, gbc);
    }

    // ===================== Theme helpers =====================
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

    // ===================== small UI bits =====================
    private JButton smallOutlineButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(new Color(243, 244, 246));
        b.setForeground(new Color(37, 99, 235));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return b;
    }

    private String safe(JTextField tf) {
        return tf == null ? "" : tf.getText().trim();
    }

    // ===================== formatting helpers =====================
    private String humanizeAgo(LocalDateTime t) {
        if (t == null) return "";
        Duration d = Duration.between(t, LocalDateTime.now());
        long minutes = Math.max(0, d.toMinutes());
        if (minutes < 60) return minutes + " minutes ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        long days = hours / 24;
        return days + " days ago";
    }

    private String formatDateTime(LocalDateTime t) {
        if (t == null) return "";
        return t.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm"));
    }

    private String formatDate(LocalDateTime t) {
        if (t == null) return "";
        return t.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
    }

    private long daysAgo(LocalDateTime t) {
        if (t == null) return 0;
        Duration d = Duration.between(t, LocalDateTime.now());
        return Math.max(0, d.toDays());
    }

    // ===================== inner components (no extra files needed) =====================
    private static class StatusBadge extends JPanel {
        public StatusBadge(String text) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
            JLabel dot = new JLabel("●");
            dot.setForeground(new Color(34, 197, 94));
            JLabel label = new JLabel(text);
            label.setForeground(new Color(22, 101, 52));
            label.setFont(new Font("Arial", Font.BOLD, 13));
            add(dot);
            add(label);
            setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(220, 252, 231)); // green-100
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 14, d.height + 6);
        }
    }

    private static class MetadataCard extends BaseTile {
        private final JLabel value = new JLabel();
        private final JLabel sub = new JLabel();

        public MetadataCard(String title, String valueText, String subText) {
            super(1, 1, 26, false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

            JLabel t = new JLabel(title);
            t.setFont(new Font("Arial", Font.BOLD, 12));
            t.setForeground(new Color(107, 114, 128));

            value.setText(valueText);
            value.setFont(new Font("Arial", Font.BOLD, 16));
            value.setForeground(new Color(17, 24, 39));

            sub.setText(subText);
            sub.setFont(new Font("Arial", Font.PLAIN, 12));
            sub.setForeground(new Color(107, 114, 128));

            add(t);
            add(Box.createVerticalStrut(12));
            add(value);
            add(Box.createVerticalStrut(6));
            add(sub);
        }

        public void setValueText(String v) { value.setText(v); }
        public void setSubText(String s) { sub.setText(s); }
    }

    private static class AvatarCircle extends JComponent {
        private final int size;
        private String imagePath;

        public AvatarCircle(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        public void setImagePath(String path) {
            this.imagePath = (path == null || path.isBlank()) ? null : path;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // ring
            g2.setColor(new Color(229, 231, 235));
            g2.fillOval(0, 0, w, h);

            int pad = 3;
            Shape clip = new Ellipse2D.Double(pad, pad, w - pad * 2, h - pad * 2);
            g2.setClip(clip);

            boolean drawn = false;
            if (imagePath != null) {
                try {
                    Image img = ImageIO.read(new File(imagePath));
                    if (img != null) {
                        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                        g2.drawImage(scaled, 0, 0, null);
                        drawn = true;
                    }
                } catch (Exception ignored) {}
            }

            if (!drawn) {
                // fallback initials
                g2.setColor(new Color(243, 244, 246));
                g2.fillOval(0, 0, w, h);
                g2.setColor(new Color(107, 114, 128));
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                String s = "DR";
                FontMetrics fm = g2.getFontMetrics();
                int x = (w - fm.stringWidth(s)) / 2;
                int y = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(s, x, y);
            }

            g2.setClip(null);
            g2.dispose();
        }
    }
}
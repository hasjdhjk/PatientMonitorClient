package UI.Pages;
import NetWork.ApiClient;
import NetWork.Session;

import UI.MainWindow;
import UI.Components.RoundedButton;
import UI.Components.RoundedPanel;
import Utilities.LanguageManager;
import Utilities.SettingManager;
import Utilities.ThemeManager;

import Models.DoctorProfile;
import Services.AccountMetaService;
import Services.DoctorProfileService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Settings page (clean version) + Avatar upload:
 * - Default avatar: drawn initials circle
 * - Click avatar to upload image (png/jpg/jpeg)
 * - Image is copied into user home folder (.patient-monitor/avatars)
 * - Path persisted in SettingManager (avatarPath)
 */
public class SettingsPage extends JPanel {

    // Dark palette
    private static final Color DARK_BG = new Color(24, 26, 30);
    private static final Color DARK_CARD = new Color(32, 35, 41);
    private static final Color DARK_TEXT = new Color(235, 235, 235);
    private static final Color DARK_MUTED = new Color(170, 170, 170);

    private final MainWindow window;
    private final SettingManager settings = new SettingManager();

    private JCheckBox darkModeToggle;
    private JComboBox<String> languageDropdown;
    private JPasswordField deletePasswordField;

    // Keep SettingsPage identity/avatar in sync with AccountPage
    private final DoctorProfileService profileService = new DoctorProfileService();
    private final AccountMetaService metaService = new AccountMetaService();
    private AccountMetaService.Meta meta;

    // Resolved display values (from Session first, then local profile fallback)
    private String displayName = "Doctor";
    private String displayEmail = "";

    // Avatar label (clickable)
    private JLabel avatarLabel;

    // Layout constants
    private static final int COLUMN_W = 540;
    private static final int ROW_H = 68;
    private static final int DROPDOWN_ROW_H = 72;

    public SettingsPage(MainWindow window) {
        this.window = window;

        // Load account meta (avatar path) and resolve display identity
        this.meta = metaService.loadOrInit();
        resolveDisplayIdentity();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Load persisted settings
        LanguageManager.setLanguage(settings.getLanguage());

        add(buildMainCard(), BorderLayout.CENTER);

        // Apply theme on load
        ThemeManager.apply(window, settings.isDarkMode());
        applyLocalTheme();
    }

    // Main UI builder
    private JComponent buildMainCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(true);

        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.setPreferredSize(new Dimension(720, 520));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Column controls overall width & alignment
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setAlignmentX(Component.CENTER_ALIGNMENT);
        column.setMaximumSize(new Dimension(COLUMN_W, Integer.MAX_VALUE));

        // Profile header
        JComponent profile = buildProfileHeader();
        profile.setAlignmentX(Component.CENTER_ALIGNMENT);
        column.add(profile);
        column.add(Box.createVerticalStrut(22));

        // Dark mode + Language
        initControls(); // IMPORTANT: init before adding rows

        column.add(buildGroupCard(
                buildToggleRow("Dark mode", darkModeToggle)
        ));
        column.add(Box.createVerticalStrut(22));

        // Danger zone: permanent delete account
        column.add(buildDeleteAccountCard());
        column.add(Box.createVerticalStrut(22));

        // Buttons: Reset + Log out
        column.add(buildButtonsRow());
        column.add(Box.createVerticalGlue());

        content.add(column);

        card.add(content, BorderLayout.CENTER);
        outer.add(card);

        // Theme apply on this built card
        // applyLocalTheme(card, title);

        return outer;
    }

    private void initControls() {
        // Dark mode toggle
        darkModeToggle = new JCheckBox();
        darkModeToggle.setOpaque(false);
        darkModeToggle.setSelected(settings.isDarkMode());
        darkModeToggle.addActionListener(e -> {
            settings.setDarkMode(darkModeToggle.isSelected());
            ThemeManager.apply(window, settings.isDarkMode());

            // keep your previous call if exists
            try {
                window.getAccountPage().applyThemeToInputs();
            } catch (Exception ignored) {}

            refreshPage();
        });

        // Language dropdown
        languageDropdown = new JComboBox<>(new String[]{"en", "zh"});
        languageDropdown.setSelectedItem(settings.getLanguage());
        languageDropdown.setFont(new Font("Dialog", Font.PLAIN, 16));
        languageDropdown.setPreferredSize(new Dimension(160, 34));

        languageDropdown.addActionListener(e -> {
            String lang = (String) languageDropdown.getSelectedItem();
            settings.setLanguage(lang);
            LanguageManager.setLanguage(lang);
            refreshPage();
        });

        // White background + BLACK text (your requirement)
        languageDropdown.setForeground(Color.BLACK);
        languageDropdown.setBackground(Color.WHITE);
        languageDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setForeground(Color.BLACK);
                label.setBackground(isSelected ? new Color(230, 230, 230) : Color.WHITE);
                return label;
            }
        });
    }

    private JComponent buildButtonsRow() {
        RoundedButton resetBtn = new RoundedButton(" " + LanguageManager.t("settings.reset") + " ");
        resetBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        resetBtn.addActionListener(e -> {
            settings.resetToDefaults();
            LanguageManager.setLanguage(settings.getLanguage());
            ThemeManager.apply(window, settings.isDarkMode());
            refreshPage();
        });

        RoundedButton logoutBtn = new RoundedButton(" " + LanguageManager.t("settings.logout") + " ");
        logoutBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        logoutBtn.addActionListener(e -> window.logout());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnRow.add(resetBtn);
        btnRow.add(logoutBtn);

        // Wrap in a rounded panel to match grouped UI
        RoundedPanel actionsCard = new RoundedPanel(new BorderLayout());
        actionsCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        actionsCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionsCard.setMaximumSize(new Dimension(COLUMN_W, Integer.MAX_VALUE));
        actionsCard.add(btnRow, BorderLayout.CENTER);

        return actionsCard;
    }

    private JComponent buildProfileHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(COLUMN_W, Integer.MAX_VALUE));
        header.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Avatar
        JComponent avatar = buildAvatarPicker(92);

        // Right text (Doctor + email)
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 34));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = new JLabel(displayEmail);
        emailLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(nameLabel);
        text.add(Box.createVerticalStrut(6));
        if (displayEmail != null && !displayEmail.isBlank()) text.add(emailLabel);

        header.add(avatar);
        header.add(text);

        return header;
    }

    private void resolveDisplayIdentity() {
        DoctorProfile p = profileService.load();
        if (p == null) p = DoctorProfile.defaults();

        // Name
        String name = Session.getDoctorFullName();
        if (name == null || name.isBlank() || "demo".equalsIgnoreCase(name.trim())) {
            name = p.getFullName();
        }
        if (name == null || name.isBlank()) name = "Doctor";
        displayName = name;

        // Email
        String email = Session.getDoctorEmail();
        if (email == null || email.isBlank() || "demo".equalsIgnoreCase(email.trim())) {
            email = p.getEmail();
        }
        displayEmail = (email == null ? "" : email.trim());
    }

    private String currentAvatarPath() {
        if (meta == null) meta = metaService.loadOrInit();
        return (meta == null || meta.avatarPath == null) ? "" : meta.avatarPath;
    }


    // Avatar Upload
    private JComponent buildAvatarPicker(int size) {
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(size, size));
        avatarLabel.setMinimumSize(new Dimension(size, size));
        avatarLabel.setMaximumSize(new Dimension(size, size));
        avatarLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatarLabel.setToolTipText("Click to change profile picture");

        updateAvatarIcon(size);

        avatarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    chooseAndSaveAvatar(size);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem remove = new JMenuItem("Remove photo");
                    remove.addActionListener(ev -> {
                        meta.avatarPath = null;
                        metaService.saveAvatarPath(null);
                        updateAvatarIcon(size);
                        refreshPage();
                    });
                    menu.add(remove);
                    menu.show(avatarLabel, e.getX(), e.getY());
                }
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(avatarLabel, BorderLayout.CENTER);
        return wrap;
    }

    private void chooseAndSaveAvatar(int size) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a profile picture");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Images (png, jpg, jpeg)", "png", "jpg", "jpeg"
        ));

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File src = chooser.getSelectedFile();
        if (src == null || !src.exists()) return;

        try {
            String ext = getFileExtension(src.getName());
            if (ext.isBlank()) ext = "png";

            // Copy to the same account meta directory used by AccountPage
            Path dir = metaService.getDirPath();
            Files.createDirectories(dir);

            String filename = "avatar_" + System.currentTimeMillis() + "." + ext;
            Path dst = dir.resolve(filename);

            Files.copy(src.toPath(), dst, StandardCopyOption.REPLACE_EXISTING);

            meta.avatarPath = dst.toString();
            metaService.saveAvatarPath(meta.avatarPath);

            updateAvatarIcon(size);
            refreshPage();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to set profile picture.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAvatarIcon(int size) {
        String path = currentAvatarPath();
        File f = (path == null || path.isBlank()) ? null : new File(path);

        try {
            if (f != null && f.exists()) {
                BufferedImage img = ImageIO.read(f);
                if (img != null) {
                    BufferedImage circle = makeCircleAvatar(img, size);
                    avatarLabel.setIcon(new ImageIcon(circle));
                    avatarLabel.setText(null);
                    return;
                }
            }
        } catch (Exception ignored) {}

        // fallback: default initials
        BufferedImage fallback = drawInitialsAvatar(size, displayName);
        avatarLabel.setIcon(new ImageIcon(fallback));
        avatarLabel.setText(null);
    }

    private static String getFileExtension(String name) {
        int i = name.lastIndexOf('.');
        if (i < 0 || i == name.length() - 1) return "";
        return name.substring(i + 1).toLowerCase();
    }

    private static BufferedImage makeCircleAvatar(BufferedImage src, int size) {
        int w = src.getWidth();
        int h = src.getHeight();
        double scale = Math.max((double) size / w, (double) size / h);
        int nw = (int) Math.round(w * scale);
        int nh = (int) Math.round(h * scale);

        BufferedImage scaled = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();

        int x = (nw - size) / 2;
        int y = (nh - size) / 2;
        BufferedImage cropped = scaled.getSubimage(Math.max(0, x), Math.max(0, y), size, size);

        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Double(0, 0, size, size));
        g2.drawImage(cropped, 0, 0, null);
        g2.dispose();

        return out;
    }

    private static BufferedImage drawInitialsAvatar(int size, String name) {
        String initials = InitialsAvatar.computeInitials(name);
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color top = new Color(173, 132, 255);
        Color bottom = new Color(255, 205, 102);
        GradientPaint gp = new GradientPaint(0, 0, top, 0, size, bottom);
        g2.setPaint(gp);
        g2.fillOval(0, 0, size, size);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Dialog", Font.BOLD, (int) (size * 0.30)));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(initials);
        int h = fm.getAscent();
        int x = (size - w) / 2;
        int y = (size + h) / 2 - 4;
        g2.drawString(initials, x, y);

        g2.dispose();
        return out;
    }

    // Group + rows (fixed width to keep alignment)
    private JComponent buildGroupCard(JComponent... rows) {
        RoundedPanel group = new RoundedPanel(new BorderLayout());
        group.setBorder(new EmptyBorder(6, 10, 6, 10));

        group.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.setMaximumSize(new Dimension(COLUMN_W, Integer.MAX_VALUE));
        group.setPreferredSize(new Dimension(COLUMN_W, group.getPreferredSize().height));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0; i < rows.length; i++) {
            list.add(rows[i]);
            if (i != rows.length - 1) {
                JSeparator sep = new JSeparator();
                sep.setOpaque(false);
                list.add(sep);
            }
        }

        group.add(list, BorderLayout.CENTER);
        return group;
    }

    private JComponent buildToggleRow(String title, JCheckBox toggle) {
        JPanel row = baseRow(title);
        toggle.setPreferredSize(new Dimension(54, 28));
        row.add(toggle, BorderLayout.EAST);
        return row;
    }

    private JComponent buildDropdownRow(String title, JComboBox<String> dropdown) {
        JPanel row = baseRow(title);
        dropdown.setPreferredSize(new Dimension(160, 34));
        row.add(dropdown, BorderLayout.EAST);
        return row;
    }

    private JPanel baseRow(String title) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(14, 10, 14, 10));


        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Dialog", Font.PLAIN, 20));
        row.add(t, BorderLayout.WEST);

        return row;
    }

    // Theme
    private void applyLocalTheme() {
        setBackground(settings.isDarkMode() ? DARK_BG : Color.WHITE);
    }

    private void applyLocalTheme(RoundedPanel card, JLabel title) {
        boolean dark = settings.isDarkMode();

        setBackground(dark ? DARK_BG : Color.WHITE);

        try {
            card.setFillColor(dark ? DARK_CARD : Color.WHITE);
        } catch (Exception ignored) {}

        title.setForeground(dark ? DARK_TEXT : Color.BLACK);

        if (darkModeToggle != null) {
            darkModeToggle.setForeground(dark ? DARK_TEXT : Color.BLACK);
        }

        // Dropdown readable in dark mode too
        if (languageDropdown != null) {
            languageDropdown.setBackground(Color.WHITE);
            languageDropdown.setForeground(Color.BLACK);
        }

        updateAllLabels(this, dark ? DARK_TEXT : Color.BLACK, dark ? DARK_MUTED : Color.GRAY);
    }

    private void updateAllLabels(Container root, Color text, Color muted) {
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel lbl) {
                if (lbl.getFont() != null && lbl.getFont().getSize() <= 18) lbl.setForeground(muted);
                else lbl.setForeground(text);
            } else if (c instanceof Container child) {
                updateAllLabels(child, text, muted);
            }
        }
    }

    private void refreshPage() {
        // Re-resolve identity/avatar every refresh so this page stays in sync with AccountPage
        meta = metaService.loadOrInit();
        resolveDisplayIdentity();
        removeAll();
        add(buildMainCard(), BorderLayout.CENTER);
        revalidate();
        repaint();
        ThemeManager.apply(window, settings.isDarkMode());
        applyLocalTheme();
    }

    // Delete Account Card
    private JComponent buildDeleteAccountCard() {
        // Password field for re-auth
        deletePasswordField = new JPasswordField();
        deletePasswordField.setFont(new Font("Dialog", Font.PLAIN, 16));
        deletePasswordField.setPreferredSize(new Dimension(260, 34));
        deletePasswordField.setMaximumSize(new Dimension(260, 34));

        JLabel title = new JLabel("Danger zone");
        title.setFont(new Font("Dialog", Font.BOLD, 18));

        JLabel hint = new JLabel("Permanently delete your account (cannot be undone)");
        hint.setFont(new Font("Dialog", Font.PLAIN, 14));

        JPanel passwordRow = new JPanel(new BorderLayout(12, 0));
        passwordRow.setOpaque(false);
        JLabel pwLabel = new JLabel("Password");
        pwLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        passwordRow.add(pwLabel, BorderLayout.WEST);
        passwordRow.add(deletePasswordField, BorderLayout.EAST);

        RoundedButton deleteBtn = new RoundedButton(" Delete account ");
        deleteBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        // try to visually indicate danger without relying on custom button internals
        deleteBtn.setForeground(Color.WHITE);
        try {
            deleteBtn.setBackground(new Color(180, 50, 50));
            deleteBtn.setOpaque(true);
        } catch (Exception ignored) {}

        deleteBtn.addActionListener(e -> {
            String email = Session.getDoctorEmail();
            String pw = new String(deletePasswordField.getPassword());

            if (email == null || email.isBlank() || "demo".equalsIgnoreCase(email.trim())) {
                JOptionPane.showMessageDialog(this,
                        "No logged-in account found.",
                        "Delete account",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (pw == null || pw.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter your password to delete your account.",
                        "Delete account",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Strong confirmation: user must type DELETE
            String typed = JOptionPane.showInputDialog(this,
                    "This will permanently delete your account.\nType DELETE to confirm:",
                    "Confirm deletion",
                    JOptionPane.WARNING_MESSAGE);

            if (typed == null) return; // cancelled
            if (!"DELETE".equals(typed.trim())) {
                JOptionPane.showMessageDialog(this,
                        "Confirmation text did not match. Account was not deleted.",
                        "Delete account",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            ApiClient.SimpleResponse res = ApiClient.deleteAccount(email, pw);
            if (res == null) {
                JOptionPane.showMessageDialog(this,
                        "Request failed (no response).",
                        "Delete account",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (res.status != null && res.status.equalsIgnoreCase("ok")) {
                // clear local session and return to login
                Session.clear();
                JOptionPane.showMessageDialog(this,
                        "Account deleted.",
                        "Delete account",
                        JOptionPane.INFORMATION_MESSAGE);
                window.logout();
            } else {
                String msg = (res.message == null || res.message.isBlank()) ? "Delete failed" : res.message;
                JOptionPane.showMessageDialog(this,
                        msg,
                        "Delete account",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(title);
        center.add(Box.createVerticalStrut(6));
        center.add(hint);
        center.add(Box.createVerticalStrut(12));
        center.add(passwordRow);
        center.add(Box.createVerticalStrut(14));
        center.add(deleteBtn);

        RoundedPanel dangerCard = new RoundedPanel(new BorderLayout());
        dangerCard.setBorder(new EmptyBorder(14, 14, 14, 14));
        dangerCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        dangerCard.setMaximumSize(new Dimension(COLUMN_W, Integer.MAX_VALUE));
        dangerCard.add(center, BorderLayout.CENTER);

        return dangerCard;
    }

    // Avatar (initials) fallback component (kept for compatibility)
    private static class InitialsAvatar extends JPanel {
        private final int size;
        private final String initials;

        InitialsAvatar(int size, String name) {
            this.size = size;
            this.initials = computeInitials(name);
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color top = new Color(173, 132, 255);
            Color bottom = new Color(255, 205, 102);
            GradientPaint gp = new GradientPaint(0, 0, top, 0, size, bottom);
            g2.setPaint(gp);
            g2.fillOval(0, 0, size, size);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Dialog", Font.BOLD, (int) (size * 0.30)));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(initials);
            int h = fm.getAscent();
            int x = (size - w) / 2;
            int y = (size + h) / 2 - 4;
            g2.drawString(initials, x, y);

            g2.dispose();
        }

        // changed to public so SettingsPage can reuse it for fallback image rendering
        public static String computeInitials(String name) {
            if (name == null || name.isBlank()) return "D";
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
            String a = parts[0].substring(0, 1).toUpperCase();
            String b = parts[parts.length - 1].substring(0, 1).toUpperCase();
            return a + b;
        }
    }
}
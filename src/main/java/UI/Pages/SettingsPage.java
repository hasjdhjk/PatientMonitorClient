package UI.Pages;

import UI.MainWindow;
import UI.Components.RoundedButton;
import UI.Components.RoundedPanel;
import Utilities.LanguageManager;
import Utilities.SettingManager;
import Utilities.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class SettingsPage extends JPanel {

    private static final Color DARK_BG = new Color(24, 26, 30);
    private static final Color DARK_CARD = new Color(32, 35, 41);
    private static final Color DARK_TEXT = new Color(235, 235, 235);
    private static final Color DARK_MUTED = new Color(170, 170, 170);
    private static final Color DARK_INPUT = new Color(45, 48, 56);

    private final MainWindow window;
    private final SettingManager settings = new SettingManager();;

    private JCheckBox darkModeToggle;
    private JComboBox<String> languageDropdown;

    public SettingsPage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Load persisted settings
        LanguageManager.setLanguage(settings.getLanguage());

        add(buildCenterCard(), BorderLayout.CENTER);

        // Apply theme on load
        ThemeManager.apply(window, settings.isDarkMode());
    }

    private JComponent buildCenterCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(true);

        RoundedPanel card = new RoundedPanel(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(520, 360));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(LanguageManager.t("settings.title"));
        title.setFont(new Font("Dialog", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(18));

        // Dark mode
        darkModeToggle = new JCheckBox(LanguageManager.t("settings.darkMode"));
        darkModeToggle.setOpaque(false);
        darkModeToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        darkModeToggle.setSelected(settings.isDarkMode());
        darkModeToggle.addActionListener(e -> {
            settings.setDarkMode(darkModeToggle.isSelected());
            ThemeManager.apply(window, settings.isDarkMode());
            window.getAccountPage().applyThemeToInputs();

            refreshTexts();
        });


        content.add(darkModeToggle);
        content.add(Box.createVerticalStrut(18));

        // Language
        JLabel langLabel = new JLabel(LanguageManager.t("settings.language"));
        langLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        langLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(langLabel);
        content.add(Box.createVerticalStrut(6));

        languageDropdown = new JComboBox<>(new String[]{"en", "zh"});
        languageDropdown.setMaximumSize(new Dimension(200, 32));
        languageDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        languageDropdown.setSelectedItem(settings.getLanguage());
        languageDropdown.addActionListener(e -> {
            String lang = (String) languageDropdown.getSelectedItem();
            settings.setLanguage(lang);
            LanguageManager.setLanguage(lang);

            refreshTexts();
        });

        content.add(languageDropdown);
        content.add(Box.createVerticalStrut(18));


        content.add(Box.createVerticalStrut(24));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton resetBtn = new RoundedButton(" " + LanguageManager.t("settings.reset") + " ");
        resetBtn.setFont(new Font("Dialog", Font.BOLD, 14));

        resetBtn.addActionListener(e -> {
            settings.resetToDefaults();
            LanguageManager.setLanguage(settings.getLanguage());

            // Update controls
            darkModeToggle.setSelected(settings.isDarkMode());
            languageDropdown.setSelectedItem(settings.getLanguage());

            ThemeManager.apply(window, settings.isDarkMode());
            refreshTexts();
        });

        RoundedButton logoutBtn = new RoundedButton(" " + LanguageManager.t("settings.logout") + " ");
        logoutBtn.setFont(new Font("Dialog", Font.BOLD, 14));

        logoutBtn.addActionListener(e -> window.logout());

        btnRow.add(resetBtn);
        btnRow.add(logoutBtn);

        content.add(btnRow);

        card.add(content, BorderLayout.CENTER);

        outer.add(card);
        return outer;
    }

    private void refreshTexts() {
        removeAll();
        add(buildCenterCard(), BorderLayout.CENTER);
        revalidate();
        repaint();
        ThemeManager.apply(window, settings.isDarkMode());
    }
    private void applyLocalTheme(RoundedPanel card, JLabel title, JLabel langLabel, JLabel note) {
        boolean dark = settings.isDarkMode();

        // Page background
        setBackground(dark ? DARK_BG : Color.WHITE);

        // Card background
        card.setFillColor(dark ? DARK_CARD : Color.WHITE);

        // Text colors
        title.setForeground(dark ? DARK_TEXT : Color.BLACK);
        langLabel.setForeground(dark ? DARK_TEXT : Color.BLACK);
        note.setForeground(dark ? DARK_MUTED : Color.GRAY);

        // Controls
        darkModeToggle.setOpaque(false);
        darkModeToggle.setForeground(dark ? DARK_TEXT : Color.BLACK);

        languageDropdown.setForeground(dark ? DARK_TEXT : Color.BLACK);
        languageDropdown.setBackground(dark ? DARK_INPUT : Color.WHITE);
    }

}

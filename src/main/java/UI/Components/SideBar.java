package UI.Components;
import Services.AlertManager;
import Models.LiveVitals;

import Utilities.SettingManager;
import UI.MainWindow;
import Utilities.ImageLoader;
import Models.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SideBar extends JPanel {

    private List<JButton> allButtons = new ArrayList<>();
    private Color selectedColor = new Color(178, 198, 250, 255);
    private Color normalColor = new Color(255, 255, 255);
    private Color hoverColor = new Color(240, 240, 240);
    private Color darkSidebarColor = new Color(40, 42, 46);

    public SideBar(MainWindow window) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        SettingManager settings = new SettingManager();

        JPanel topButtons = new JPanel();
        topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.Y_AXIS));
        topButtons.setOpaque(false);

        // Home (default selected)
        JButton homeButton =
                makeSidebarButton("Home", "home", Color.BLACK, MainWindow.PAGE_HOME, window);
        setSelectedButton(homeButton);
        topButtons.add(homeButton);
        topButtons.add(Box.createVerticalStrut(10));

        // LIVE MONITORING BUTTON
        JButton liveBtn = createButton("Live Monitoring", "status", Color.BLACK);
        liveBtn.addActionListener(e -> {

            // TEMP patient (until real selection is wired)
            Patient dummyPatient =
                    new Patient(1, "John", "Anderson", "male", 36, "120/80");

            window.showLiveMonitoring(dummyPatient);
            setSelectedButton(liveBtn);
        });
        allButtons.add(liveBtn);
        topButtons.add(liveBtn);
        topButtons.add(Box.createVerticalStrut(10));

        // Other buttons
        topButtons.add(
                makeSidebarButton("Add Patient", "add", Color.BLACK, MainWindow.PAGE_ADD, window)
        );
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(
                makeSidebarButton("Digital Twin", "digitalTwin", Color.BLACK, MainWindow.PAGE_STATUS, window)
        );
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(
                makeSidebarButton("Account", "account", Color.BLACK, MainWindow.PAGE_ACCOUNT, window)
        );
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(
                makeSidebarButton("Settings", "settings", Color.BLACK, MainWindow.PAGE_SETTINGS, window)
        );

        add(topButtons, BorderLayout.NORTH);

        // Logout button (bottom)
        JButton logoutBtn = createButton("Log Out", "logout", Color.RED);
        logoutBtn.addActionListener(e -> {
            AlertManager.getInstance().disableAlerts();
            LiveVitals.clearAllShared();
            window.showPage(MainWindow.PAGE_LOGIN);
        });


        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(210, logoutBtn.getPreferredSize().height + 20));
        bottomPanel.add(logoutBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Button helpers
    private JButton makeSidebarButton(
            String text, String iconName, Color textColor, String pageName, MainWindow window) {

        JButton btn = createButton(text, iconName, textColor);
        btn.addActionListener(e -> {
            window.showPage(pageName);
            setSelectedButton(btn);
        });
        allButtons.add(btn);
        return btn;
    }

    private JButton createButton(String text, String iconName, Color textColor) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setForeground(textColor);
        btn.setFocusPainted(false);

        ImageIcon icon = ImageLoader.loadImage("icon_" + iconName, "Icons", 22);
        btn.setIcon(icon);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);

        btn.setPreferredSize(new Dimension(210, 60));
        btn.setMaximumSize(new Dimension(210, 60));
        btn.setMinimumSize(new Dimension(210, 60));

        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!isSelected(btn)) {
                    btn.setOpaque(true);
                    btn.setContentAreaFilled(true);
                    btn.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (isSelected(btn)) {
                    btn.setOpaque(true);
                    btn.setContentAreaFilled(true);
                    btn.setBackground(selectedColor);
                } else {
                    btn.setOpaque(false);
                    btn.setContentAreaFilled(false);
                }
            }
        });

        return btn;
    }

    private boolean isSelected(JButton btn) {
        return btn.getBackground().equals(selectedColor) && btn.isOpaque();
    }

    private void setSelectedButton(JButton selected) {
        for (JButton b : allButtons) {
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setBackground(normalColor);
        }
        selected.setOpaque(true);
        selected.setContentAreaFilled(true);
        selected.setBackground(selectedColor);
    }

    public void setSelected(String label) {
        for (JButton b : allButtons) {
            if (b.getText().equalsIgnoreCase(label)) {
                setSelectedButton(b);
                return;
            }
        }
        System.out.println("⚠ Sidebar button not found: " + label);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int h = getHeight();
        Color bg = new SettingManager().isDarkMode()
                ? darkSidebarColor
                : normalColor;

        g2.setColor(bg);
        g2.fillRect(0, 0, 210, h);

        for (int i = 0; i < 10; i++) {
            int alpha = 40 - i * 4;
            if (alpha < 0) alpha = 0;
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRect(210 + i, 0, 1, h);
        }

        g2.dispose();
    }
}

package UI.Components;

import UI.MainWindow;
import Utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SideBar extends JPanel {

    private List<JButton> allButtons = new ArrayList<>();
    private Color selectedColor = new Color(178, 198, 250, 255); // high light
    private Color normalColor = new Color(255, 255, 255);   // button background
    private Color hoverColor = new Color(240, 240, 240);

    public SideBar(MainWindow window) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // top buttons
        JPanel topButtons = new JPanel();
        topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.Y_AXIS));
        topButtons.setOpaque(false);

        // set home button as default selected
        JButton homeButton = makeSidebarButton("Home", "home", Color.BLACK, MainWindow.PAGE_HOME, window);
        setSelectedButton(homeButton);

        topButtons.add(homeButton);
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Add Patient","add", Color.BLACK, MainWindow.PAGE_ADD, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Status Tracker","status", Color.BLACK, MainWindow.PAGE_STATUS, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Account", "account", Color.BLACK, MainWindow.PAGE_ACCOUNT, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Settings","settings", Color.BLACK, MainWindow.PAGE_SETTINGS, window));

        add(topButtons, BorderLayout.NORTH);

        // logout always on the bottom
        JButton logoutBtn = createButton("Log Out", "logout", Color.RED);
        logoutBtn.addActionListener(e -> System.exit(0));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Create a normal sidebar button and register it in the list
    private JButton makeSidebarButton(String text, String iconName, Color textColor, String pageName, MainWindow window) {
        JButton btn = createButton(text, iconName, textColor);

        btn.addActionListener(e -> {
            window.showPage(pageName);
            setSelectedButton(btn);
        });

        allButtons.add(btn);
        return btn;
    }

    // Reusable button creation method
    private JButton createButton(String text, String iconName, Color textColor) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setForeground(textColor);
        btn.setFocusPainted(false);

        // Load icon
        ImageIcon icon = ImageLoader.loadImage("icon_" + iconName, "Icons", 22);
        btn.setIcon(icon);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);

        // Button size
        btn.setPreferredSize(new Dimension(210, 60));
        btn.setMaximumSize(new Dimension(210, 60));
        btn.setMinimumSize(new Dimension(210, 60));

        // Default: transparent
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        // ===== HOVER EFFECT =====
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

    // Highlight the selected button
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw the solid white sidebar on the left leave some space for shadow
        g2.setColor(normalColor);
        g2.fillRect(0, 0, 210, h);

        // shadow on the right
        for (int i = 0; i < 10; i++) {
            int alpha = 40 - i * 4;
            if (alpha < 0) alpha = 0;

            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRect(210 + i, 0, 1, h); // start after 200px
        }

        g2.dispose();
    }



}


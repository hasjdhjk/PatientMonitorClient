package UI.Components;

import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SideBar extends JPanel {

    private List<JButton> allButtons = new ArrayList<>();
    private Color selectedColor = new Color(255, 255, 255); // light gray highlight
    private Color normalColor = new Color(230, 230, 230);   // sidebar background

    public SideBar(MainWindow window) {

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 0));
        setBackground(normalColor);
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // top buttons
        JPanel topButtons = new JPanel();
        topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.Y_AXIS));
        topButtons.setOpaque(false);

        topButtons.add(makeSidebarButton("Home", MainWindow.PAGE_HOME, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("+ Add Patient", MainWindow.PAGE_ADD, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Status Tracker", MainWindow.PAGE_STATUS, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Account", MainWindow.PAGE_ACCOUNT, window));
        topButtons.add(Box.createVerticalStrut(10));
        topButtons.add(makeSidebarButton("Settings", MainWindow.PAGE_SETTINGS, window));

        add(topButtons, BorderLayout.NORTH);

        // logout always on the bottom
        JButton logoutBtn = createButton("Log Out");
        logoutBtn.addActionListener(e -> System.exit(0));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(logoutBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Create a normal sidebar button and register it in the list
    private JButton makeSidebarButton(String text, String iconName, String pageName, MainWindow window) {
        JButton btn = createButton(text);

        btn.addActionListener(e -> {
            window.showPage(pageName);
            setSelectedButton(btn);
        });

        allButtons.add(btn);
        return btn;
    }

    // Reusable button creation method
    private JButton createButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);

        // === Button height (taller vertically) ===
        btn.setPreferredSize(new Dimension(180, 60));
        btn.setMaximumSize(new Dimension(180, 60));
        btn.setMinimumSize(new Dimension(180, 60));

        // === No background normally ===
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        return btn;
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
}

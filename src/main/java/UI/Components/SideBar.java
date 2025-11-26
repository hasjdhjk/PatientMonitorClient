package UI.Components;

import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class SideBar extends JPanel {

    public SideBar(MainWindow window) {
        setLayout(new GridLayout(10, 1, 0, 10));
        setPreferredSize(new Dimension(200, 0));
        setBackground(new Color(230, 230, 230));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        add(createButton("Home", e -> window.showPage(MainWindow.PAGE_HOME)));
        add(createButton("+ Add Patient", e -> window.showPage(MainWindow.PAGE_ADD)));
        add(createButton("Status Tracker", e -> window.showPage(MainWindow.PAGE_STATUS)));
        add(createButton("Account", e -> window.showPage(MainWindow.PAGE_ACCOUNT)));
        add(createButton("Settings", e -> window.showPage(MainWindow.PAGE_SETTINGS)));

        add(Box.createVerticalGlue());
        add(createButton("Log Out", e -> System.exit(0)));
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }
}

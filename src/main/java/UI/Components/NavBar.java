package UI.Components;

import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class NavBar extends JPanel {

    public NavBar(MainWindow window) {

        setLayout(new GridLayout(1, 4));
        setPreferredSize(new Dimension(400, 60));
        setBackground(new Color(240, 240, 240));

        JButton homeBtn = new JButton("Home");
        JButton profileBtn = new JButton("Profile");
        JButton addBtn = new JButton("+");
        JButton settingsBtn = new JButton("⚙");

        homeBtn.addActionListener(e -> window.showPage(MainWindow.PAGE_HOME));
        addBtn.addActionListener(e -> window.showPage(MainWindow.PAGE_ADD));
        settingsBtn.addActionListener(e -> window.showPage(MainWindow.PAGE_SETTINGS));

        add(homeBtn);
        add(profileBtn);
        add(addBtn);
        add(settingsBtn);
    }
}

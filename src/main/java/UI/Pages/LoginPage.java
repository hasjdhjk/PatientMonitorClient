package UI.Pages;

import UI.Components.Tiles.BaseTile;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JPanel {

    public LoginPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // top logo bar
        JLabel logo = new JLabel("HealthTrack", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 28));
        logo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(logo, BorderLayout.NORTH);

        // center wrapper
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);

        // ----- CENTER LOGIN CARD (BaseTile) -----
        BaseTile card = new BaseTile(420, 420, 40, false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        card.setOpaque(false);  // BaseTile handles background

        // title
        JLabel title = new JLabel("Doctor Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // email field
        JTextField emailField = new JTextField();
        styleTextField(emailField, "Enter your email");

        // password field
        JPasswordField passwordField = new JPasswordField();
        styleTextField(passwordField, "Enter your password");

        // sign in button
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setBackground(new Color(68, 104, 140));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(300, 45));
        loginBtn.setMaximumSize(new Dimension(300, 45));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // footer: forgot password + register
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setForeground(Color.GRAY);

        JLabel register = new JLabel("Register");
        register.setForeground(new Color(50, 100, 200));
        register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        footer.add(forgot, BorderLayout.WEST);
        footer.add(register, BorderLayout.EAST);

        // Add components to card
        card.add(title);
        card.add(Box.createVerticalStrut(25));
        card.add(emailField);
        card.add(Box.createVerticalStrut(20));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(25));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(footer);

        centerWrap.add(card);
        add(centerWrap, BorderLayout.CENTER);

        // copyright
        JLabel copyright = new JLabel(
                "© 2025 HealthTrack. All Rights Reserved.",
                SwingConstants.CENTER
        );
        copyright.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(copyright, BorderLayout.SOUTH);
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }
}

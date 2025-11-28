package UI.Pages;

import UI.Components.PlaceholderTextField;
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

        // login panel
        BaseTile card = new BaseTile(420, 460, 40, false);   // disabled hover
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // title
        JLabel title = new JLabel("Doctor Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // email label
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // email field (base tile)
        BaseTile emailFieldTile = new BaseTile(320, 60, 40, true);
        emailFieldTile.setLayout(new BorderLayout());
        PlaceholderTextField emailField = new PlaceholderTextField("Enter your email");
        styleTextField(emailField);
        emailFieldTile.add(emailField, BorderLayout.CENTER);

        // password label
        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        pwdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // password field
        BaseTile pwdFieldTile = new BaseTile(320, 60, 40, true);
        pwdFieldTile.setLayout(new BorderLayout());
        PlaceholderTextField passwordField = new PlaceholderTextField("Enter your password");
        styleTextField(passwordField);
        pwdFieldTile.add(passwordField, BorderLayout.CENTER);

        // sign in button
        BaseTile signInTile = new BaseTile(320, 60, 40, true);
        signInTile.setBackground(new Color(68, 104, 140)); // override BaseTile color
        signInTile.setLayout(new BorderLayout());

        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        signInTile.add(loginBtn, BorderLayout.CENTER);

        // reset password and register
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setForeground(Color.GRAY);
        forgot.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // left padding +30

        JLabel register = new JLabel("Register");
        register.setForeground(new Color(50, 100, 200));
        register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        register.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // left padding +30

        footer.add(forgot, BorderLayout.WEST);
        footer.add(register, BorderLayout.EAST);

        // add components to card
        card.add(title);
        card.add(Box.createVerticalStrut(25));

        card.add(wrapLeft(emailLabel));
        card.add(Box.createVerticalStrut(5));
        card.add(emailFieldTile);
        card.add(Box.createVerticalStrut(20));

        card.add(wrapLeft(pwdLabel));
        card.add(Box.createVerticalStrut(5));
        card.add(pwdFieldTile);
        card.add(Box.createVerticalStrut(25));

        card.add(signInTile);
        card.add(Box.createVerticalStrut(20));

        card.add(footer);

        centerWrap.add(card);
        add(centerWrap, BorderLayout.CENTER);

        // footer copyright
        JLabel copyright = new JLabel(
                "© 2025 HealthTrack. All Rights Reserved.",
                SwingConstants.CENTER
        );
        copyright.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(copyright, BorderLayout.SOUTH);
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 15));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setOpaque(false); // BaseTile draws background
    }

    // force the label in the box layout to left
    private JPanel wrapLeft(JComponent c) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setOpaque(false);
        panel.add(c);
        return panel;
    }

}

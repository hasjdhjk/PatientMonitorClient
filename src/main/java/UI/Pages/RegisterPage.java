package UI.Pages;

import NetWork.ApiClient;
import UI.Components.PlaceholderTextField;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class RegisterPage extends JPanel {

    public RegisterPage(MainWindow mainWindow) {
        setLayout(new GridLayout(1, 2));
        setBackground(Color.WHITE);

        // ============================================================
        // LEFT BLUE PANEL
        // ============================================================
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(65, 88, 208));   // beautiful blue
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel getStarted = new JLabel("Get Started", SwingConstants.CENTER);
        getStarted.setFont(new Font("Arial", Font.BOLD, 32));
        getStarted.setForeground(Color.WHITE);
        getStarted.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel haveAcc = new JLabel("Already have an account?");
        haveAcc.setFont(new Font("Arial", Font.PLAIN, 16));
        haveAcc.setForeground(Color.white);
        haveAcc.setAlignmentX(Component.CENTER_ALIGNMENT);
        haveAcc.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Login button to go back
        BaseTile loginTile = new BaseTile(200, 50, 40, true);
        loginTile.setBackground(new Color(255, 255, 255));  // white button
        loginTile.setLayout(new BorderLayout());

        JButton loginBtn = new JButton("Log in");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setForeground(new Color(65, 88, 208));
        loginBtn.setFocusPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loginTile.add(loginBtn, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> {
            mainWindow.showLoginPage();
        });

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(getStarted);
        leftPanel.add(haveAcc);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(loginTile);
        leftPanel.add(Box.createVerticalGlue());


        // ============================================================
        // RIGHT WHITE PANEL
        // ============================================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        gbc.gridy = 0;
        rightPanel.add(title, gbc);

        // Email field
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rightPanel.add(emailLabel, gbc);

        gbc.gridy++;
        PlaceholderTextField emailField = new PlaceholderTextField("Enter email");
        styleField(emailField);
        rightPanel.add(emailField, gbc);

        // Password field
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rightPanel.add(passLabel, gbc);

        gbc.gridy++;
        PlaceholderTextField passwordField = new PlaceholderTextField("Enter password");
        styleField(passwordField);
        rightPanel.add(passwordField, gbc);

        // Given name
        gbc.gridy++;
        JLabel givenLabel = new JLabel("Given Name");
        givenLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rightPanel.add(givenLabel, gbc);

        gbc.gridy++;
        PlaceholderTextField givenField = new PlaceholderTextField("Enter first name");
        styleField(givenField);
        rightPanel.add(givenField, gbc);

        // Family name
        gbc.gridy++;
        JLabel familyLabel = new JLabel("Family Name");
        familyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        rightPanel.add(familyLabel, gbc);

        gbc.gridy++;
        PlaceholderTextField familyField = new PlaceholderTextField("Enter last name");
        styleField(familyField);
        rightPanel.add(familyField, gbc);

        // Checkbox
        gbc.gridy++;
        JCheckBox termsCheck = new JCheckBox("I accept the terms of the agreement");
        termsCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        termsCheck.setBackground(Color.WHITE);
        rightPanel.add(termsCheck, gbc);

        // Sign up button
        gbc.gridy++;
        BaseTile signUpTile = new BaseTile(320, 60, 40, true);
        signUpTile.setBackground(new Color(68, 104, 140));
        signUpTile.setLayout(new BorderLayout());

        JButton signUpBtn = new JButton("Sign up");
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 16));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setFocusPainted(false);
        signUpBtn.setContentAreaFilled(false);
        signUpBtn.setBorderPainted(false);
        signUpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        signUpTile.add(signUpBtn, BorderLayout.CENTER);

        rightPanel.add(signUpTile, gbc);

        // Hook button logic
        signUpBtn.addActionListener(e -> {
            if (!termsCheck.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "You must accept the terms before continuing.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ApiClient.SimpleResponse res =
                    ApiClient.register(
                            emailField.getText().trim(),
                            passwordField.getText().trim(),
                            givenField.getText().trim(),
                            familyField.getText().trim()
                    );

            if (res == null) {
                JOptionPane.showMessageDialog(this,
                        "Unable to connect to server.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!"ok".equals(res.status)) {
                JOptionPane.showMessageDialog(this,
                        res.message,
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Registration successful! Check your email to verify your account.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            mainWindow.showLoginPage();
        });


        // Add left + right panels
        add(leftPanel);
        add(rightPanel);
    }


    private void styleField(JTextField field) {
        field.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(320, 45));
    }
}

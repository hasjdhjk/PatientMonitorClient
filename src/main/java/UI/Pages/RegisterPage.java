package UI.Pages;

import NetWork.ApiClient;
import UI.Components.PlaceHolders.PlaceholderPasswordField;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class RegisterPage extends JPanel {

    public RegisterPage(MainWindow mainWindow) {

        setLayout(new GridLayout(1, 2));

        // left blue
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(65, 88, 208));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel titleLeft = new JLabel("Get Started", SwingConstants.CENTER);
        titleLeft.setFont(new Font("Arial", Font.BOLD, 50));
        titleLeft.setForeground(Color.WHITE);
        titleLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLeft = new JLabel("Already have an account?");
        subtitleLeft.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLeft.setForeground(Color.WHITE);
        subtitleLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLeft.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        BaseTile loginTile = new BaseTile(160, 65, 40, true);
        loginTile.setMaximumSize(new Dimension(200, 60));
        loginTile.setBackground(Color.WHITE);
        loginTile.setLayout(new BorderLayout());

        JButton loginBtn = new JButton("Log in");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setForeground(new Color(65, 88, 208));
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginTile.add(loginBtn, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> mainWindow.showLoginPage());

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(titleLeft);
        leftPanel.add(subtitleLeft);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(loginTile);
        leftPanel.add(Box.createVerticalGlue());

        // right
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(120, 120, 100, 120));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 150));

        // email
        JLabel emailLabel = label("Email");
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        BaseTile emailTile = inputTile();
        PlaceholderTextField emailField = new PlaceholderTextField("Enter email");
        styleTextField(emailField);
        emailTile.add(emailField);

        // password
        JLabel passwordLabel = label("Password");
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        BaseTile passwordTile = inputTile();
        PlaceholderPasswordField passwordField = new PlaceholderPasswordField("Enter password");
        styleTextField(passwordField);
        passwordTile.add(passwordField);

        // given name
        JLabel givenLabel = label("Given Name");
        givenLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        BaseTile givenTile = inputTile();
        PlaceholderTextField givenField = new PlaceholderTextField("Enter first name");
        styleTextField(givenField);
        givenTile.add(givenField);

        // family name
        JLabel familyLabel = label("Family Name");
        familyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        BaseTile familyTile = inputTile();
        PlaceholderTextField familyField = new PlaceholderTextField("Enter last name");
        styleTextField(familyField);
        familyTile.add(familyField);

        // check box
        JCheckBox termsCheck = new JCheckBox("I accept the terms of the agreement");
        termsCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        termsCheck.setBackground(Color.WHITE);
        termsCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        // sign up button
        BaseTile signUpTile = new BaseTile(650, 75, 50, true);
        signUpTile.setMaximumSize(new Dimension(650, 75));
        signUpTile.setBackground(new Color(68, 104, 140));
        signUpTile.setLayout(new BorderLayout());
        JButton signUpBtn = new JButton("Sign up");
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setContentAreaFilled(false);
        signUpBtn.setBorderPainted(false);
        signUpBtn.setFocusPainted(false);
        signUpTile.add(signUpBtn, BorderLayout.CENTER);
        signUpTile.setAlignmentX(Component.LEFT_ALIGNMENT);

        // sign up
        signUpBtn.addActionListener(e -> {

            if (!termsCheck.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "You must accept the terms before continuing.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            ApiClient.SimpleResponse res = ApiClient.register(
                    emailField.getText().trim(),
                    passwordField.getText().trim(),
                    givenField.getText().trim(),
                    familyField.getText().trim()
            );

            if (res == null) {
                JOptionPane.showMessageDialog(this,
                        "Unable to connect to server.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
                    "Registration successful! Check your email.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            mainWindow.showLoginPage();
        });


        // add to right panel
        rightPanel.add(title);
        rightPanel.add(Box.createVerticalStrut(20));

        rightPanel.add(emailLabel);
        rightPanel.add(emailTile);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(passwordLabel);
        rightPanel.add(passwordTile);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(givenLabel);
        rightPanel.add(givenTile);
        rightPanel.add(Box.createVerticalStrut(15));

        rightPanel.add(familyLabel);
        rightPanel.add(familyTile);
        rightPanel.add(Box.createVerticalStrut(20));

        rightPanel.add(termsCheck);
        rightPanel.add(Box.createVerticalStrut(20));

        rightPanel.add(signUpTile);

        add(leftPanel);
        add(rightPanel);
    }


    // create label
    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // input fields
    private BaseTile inputTile() {
        BaseTile tile = new BaseTile(650, 75, 50, false);
        tile.setMaximumSize(new Dimension(650, 75));
        tile.setLayout(new BorderLayout());
        tile.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tile;
    }

    // text field
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 15));
        field.setOpaque(false);
    }
}

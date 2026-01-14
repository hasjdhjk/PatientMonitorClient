package UI.Pages;

import NetWork.ApiClient;
import NetWork.Session;
import UI.Components.ImagePanel;
import UI.Components.PlaceHolders.PlaceholderPasswordField;
import UI.Components.PlaceHolders.PlaceholderTextField;
import UI.Components.Tiles.BaseTile;
import UI.MainWindow;
import Utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends ImagePanel {

    private PlaceholderTextField emailField;
    private PlaceholderPasswordField passwordField;

    public LoginPage(MainWindow mainWindow) {
        super(ImageLoader.loadImage("bg", "UI", 1000).getImage());
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // top logo bar
        // Logo image at top
        ImageIcon logoImg = ImageLoader.loadImageScaled("icon_logo", "Icons", 300);
        JLabel logo = new JLabel(logoImg);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));

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
        BaseTile emailFieldTile = new BaseTile(320, 30, 40, true);
        emailFieldTile.setLayout(new BorderLayout());
        emailField = new PlaceholderTextField("Enter your email");
        styleTextField(emailField);
        emailFieldTile.add(emailField, BorderLayout.CENTER);

        // password label
        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        pwdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // password field
        BaseTile pwdFieldTile = new BaseTile(320, 30, 40, true);
        pwdFieldTile.setLayout(new BorderLayout());
        passwordField = new PlaceholderPasswordField("Enter your password");
        styleTextField(passwordField);
        pwdFieldTile.add(passwordField, BorderLayout.CENTER);

        // sign in button
        BaseTile signInTile = new BaseTile(320, 30, 40, true);
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
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            ApiClient.LoginResponse res = ApiClient.login(email, password);

            if (res == null) {
                JOptionPane.showMessageDialog(this,
                        "Cannot connect to server.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!"ok".equals(res.status)) {
                JOptionPane.showMessageDialog(this,
                        res.message,
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Persist the logged-in doctor identity for subsequent API calls
            Session.setDoctorEmail(email);

            // Update top bar immediately with best available display name
            String display = email;
            String gn = res.givenName;
            String fn = res.familyName;
            if (gn != null) gn = gn.trim();
            if (fn != null) fn = fn.trim();
            String full = ((gn == null ? "" : gn) + " " + (fn == null ? "" : fn)).trim();
            if (!full.isBlank()) display = full;
            mainWindow.getTopBar().updateDoctorInfo(display, "");

            clearFields();

            // notify other pages (e.g. Digital Twin) that doctor context changed
            mainWindow.onDoctorLoggedIn();
        });

        // reset password and register
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel forgot = new JLabel("Forgot Password?");
        forgot.setForeground(Color.GRAY);
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // left padding +30
        // hook up with api client (backend)
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // TODO: open reset password dialog
                System.out.println("Open forgot password dialog");
            }
        });

        // register
        JLabel register = new JLabel("Register");
        register.setForeground(new Color(50, 100, 200));
        register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        register.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // left padding +30
        // hook up with api client (backend)
        register.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // register page
                mainWindow.showRegisterPage();
            }
        });

        footer.add(forgot, BorderLayout.WEST);
        footer.add(register, BorderLayout.EAST);

        // add components to card
        card.add(title);
        card.add(Box.createVerticalStrut(10));

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

        JPanel vertical = new JPanel();
        vertical.setLayout(new BoxLayout(vertical, BoxLayout.Y_AXIS));
        vertical.setOpaque(false);

        vertical.add(logo);
        vertical.add(Box.createVerticalStrut(15)); // space between logo and card
        vertical.add(card);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(vertical);

        add(centerWrap, BorderLayout.CENTER);


        // footer copyright
        JLabel copyright = new JLabel(
                "© 2025 HealthTrack. All Rights Reserved.",
                SwingConstants.CENTER
        );
        copyright.setForeground(new Color(255, 255, 255));
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

    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}

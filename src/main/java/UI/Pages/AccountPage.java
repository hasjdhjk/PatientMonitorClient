package UI.Pages;

import UI.Components.PatientRecordsPanel;
import UI.Components.Tiles.RoundedButton;
import UI.Components.Tiles.RoundedPanel;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;

public class AccountPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private PatientRecordsPanel recordsPanel;
    private Color themePrimary = new Color(52, 152, 219);
    private Color themeBg = new Color(245, 247, 250);

    public AccountPage(MainWindow window) {

        setLayout(new BorderLayout());
        setBackground(themeBg);

        // ----------- CardLayout Panel -----------
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(themeBg);

        // Main settings page
        JPanel mainSettings = buildMainSettingsPage();

        recordsPanel = new PatientRecordsPanel(() ->
                cardLayout.show(cardPanel, "settings")
        );

        cardPanel.add(mainSettings, "settings");
        cardPanel.add(recordsPanel, "records");

        add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "settings");
    }

    // ======================== Main Settings Page ========================
    private JPanel buildMainSettingsPage() {

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        mainWrapper.setOpaque(false);

        JPanel panel = new RoundedPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Doctor Account");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 15, 25));
        form.setOpaque(false);

        form.add(new JLabel("Name:"));       form.add(new JTextField("Raymond"));
        form.add(new JLabel("Age:"));        form.add(new JTextField("20"));
        form.add(new JLabel("ID Number:"));  form.add(new JTextField("DOC123456"));
        form.add(new JLabel("Specialty:"));  form.add(new JTextField("Cardiac Surgeon"));
        form.add(new JLabel("Email:"));      form.add(new JTextField("doctor@mail.com"));
        form.add(new JLabel("Password:"));   form.add(new JPasswordField(""));

        panel.add(form, BorderLayout.CENTER);

        JPanel bottomButtons = new JPanel();
        bottomButtons.setOpaque(false);
        bottomButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 10));

        RoundedButton saveBtn = new RoundedButton("Save Settings");
        saveBtn.setRadius(18);
        saveBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        saveBtn.addActionListener(e -> saveDoctorSettings());

        RoundedButton recordsBtn = new RoundedButton("View Patient Records");
        recordsBtn.setRadius(18);
        recordsBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        recordsBtn.setBackground(new Color(46, 134, 193));
        recordsBtn.setForeground(Color.WHITE);

        recordsBtn.addActionListener(e -> {
            recordsPanel.reloadFromDisk();
            cardLayout.show(cardPanel, "records");
        });

        bottomButtons.add(recordsBtn);
        bottomButtons.add(saveBtn);

        panel.add(bottomButtons, BorderLayout.SOUTH);
        mainWrapper.add(panel, BorderLayout.CENTER);

        return mainWrapper;
    }

    // ======================== Save Doctor Info ========================
    private void saveDoctorSettings() {
        JOptionPane.showMessageDialog(this, "Settings saved.");
    }
}
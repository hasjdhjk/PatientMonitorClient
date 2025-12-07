package UI.Pages;

import Models.PatientRecord;
import Models.PatientRecordRenderer;
import UI.Components.Tiles.RoundedButton;
import UI.Components.Tiles.RoundedPanel;
import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class SettingsPage extends JPanel {

    private MainWindow window;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public SettingsPage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(230, 230, 230));

        // CardLayout container
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Two pages
        cardPanel.add(buildSettingsMainPanel(), "settings");
        cardPanel.add(buildPatientRecordsPage(), "records");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "settings");
    }

    // ======================
    // Main Settings Page
    // ======================
    private JPanel buildSettingsMainPanel() {

        JPanel container = new RoundedPanel(new BorderLayout(20, 20));
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Doctor Settings");
        label.setFont(new Font("Arial", Font.BOLD, 26));

        // Form with doctor info
        JPanel form = new JPanel(new GridLayout(3, 2, 15, 15));
        form.setOpaque(false);
        form.add(new JLabel("Name:"));
        form.add(new JTextField("雷蒙"));
        form.add(new JLabel("Age:"));
        form.add(new JTextField("20"));
        form.add(new JLabel("ID Number:"));
        form.add(new JTextField("DOC123456"));
        form.add(new JLabel("Specialty:"));
        form.add(new JTextField("Cardiac Surgeon"));
        form.add(new JLabel("Email:"));
        form.add(new JTextField(""));
        form.add(new JLabel("Password:"));
        form.add(new JPasswordField(""));


        // Rounded button for navigation
        RoundedButton viewBtn = new RoundedButton("View Patient Records");
        viewBtn.setRadius(18);
        viewBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Mouse click event → go to records page
        viewBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(cardPanel, "records");
            }
        });

        container.add(label, BorderLayout.NORTH);
        container.add(form, BorderLayout.CENTER);
        container.add(viewBtn, BorderLayout.SOUTH);

        return container;
    }

    // ======================
    // Patient Records Subpage
    // ======================
    private JPanel buildPatientRecordsPage() {

        JPanel page = new RoundedPanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Patient Records");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        // Back button
        RoundedButton back = new RoundedButton("← Back to Settings");
        back.setFont(new Font("Arial", Font.PLAIN, 14));
        back.setRadius(14);
        back.addActionListener(e -> cardLayout.show(cardPanel, "settings"));

        // Import button
        RoundedButton importBtn = new RoundedButton("Import Records");
        importBtn.setRadius(14);
        importBtn.setFont(new Font("Arial", Font.PLAIN, 14));

        // List Model
        DefaultListModel<PatientRecord> recordModel = new DefaultListModel<>();
        JList<PatientRecord> recordList = new JList<>(recordModel);

        // Custom renderer (让 UI 看起来更专业)
        recordList.setCellRenderer(new PatientRecordRenderer());

        // Import Logic
        importBtn.addActionListener(e -> importRecordsFromCSV(recordModel));

        // Top bar layout
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setOpaque(false);
        topBar.add(back);
        topBar.add(importBtn);

        page.add(topBar, BorderLayout.NORTH);
        page.add(title, BorderLayout.WEST);
        page.add(new JScrollPane(recordList), BorderLayout.CENTER);

        return page;
    }
    private void importRecordsFromCSV(DefaultListModel<PatientRecord> model) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Patient Records CSV");

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Scanner sc = new Scanner(chooser.getSelectedFile())) {

                while (sc.hasNextLine()) {
                    String[] parts = sc.nextLine().split(",");
                    if (parts.length >= 4) {
                        PatientRecord pr = new PatientRecord(
                                parts[0].trim(),
                                parts[1].trim(),
                                parts[2].trim(),
                                parts[3].trim()
                        );
                        model.addElement(pr);
                    }
                }

                JOptionPane.showMessageDialog(this, "Records imported successfully!");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Failed to read file.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
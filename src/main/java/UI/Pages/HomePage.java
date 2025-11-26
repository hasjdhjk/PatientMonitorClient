package UI.Pages;

import UI.MainWindow;
import UI.Components.PatientTile;
import Models.Patient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends JPanel {

    private MainWindow window;

    public HomePage(MainWindow window) {
        this.window = window;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Home", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(title, BorderLayout.NORTH);

        // Scrollable grid
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(grid);
        add(scroll, BorderLayout.CENTER);

        // Mock data
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient(1, "Raymond", "Lee", 82, 36.8, "120/80"));
        patients.add(new Patient(2, "Jack", "Wong", 90, 37.0, "110/75"));
        patients.add(new Patient(3, "David", "Chan", 130, 39.1, "160/95"));

        for (Patient p : patients) {
            grid.add(new PatientTile(p, window));
        }

        // Add tile (for adding patient)
        JButton addTile = new JButton("+");
        addTile.setFont(new Font("Arial", Font.BOLD, 40));
        addTile.addActionListener(e -> window.showPage(MainWindow.PAGE_ADD));
        grid.add(addTile);
    }
}

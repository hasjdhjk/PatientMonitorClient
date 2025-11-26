package UI.Pages;

import Models.Patient;
import UI.Components.SearchBar;
import UI.Components.Tiles.AddTile;
import UI.MainWindow;

import UI.Components.Tiles.PatientTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends JPanel {

    public HomePage(MainWindow window) {
        setLayout(new BorderLayout());

        // initialize top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));

        // search bar
        top.add(new SearchBar());

        add(top, BorderLayout.NORTH);

        // Scrollable grid
        JPanel grid = new JPanel(new GridLayout(0, 3, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null); // remove border
        add(scroll, BorderLayout.CENTER);

        // Mock Patients
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient(1, "Raymond", "Lee", 82, 36.8, "120/80"));
        patients.add(new Patient(2, "Jack", "Wong", 90, 37.0, "110/75"));
        patients.add(new Patient(3, "David", "Chan", 130, 39.1, "160/95"));

        for (Patient p : patients) {
            grid.add(new PatientTile(p, window));
        }

        // Add patient tile
        grid.add(new AddTile(window));
    }
}

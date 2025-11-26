package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.SearchBar;
import UI.Components.Tiles.AddTile;
import UI.Components.WrapLayout;
import UI.MainWindow;

import UI.Components.Tiles.PatientTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends JPanel {
    private JPanel grid; // contains all patient grids
    private MainWindow window;

    public HomePage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());

        // initialize top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));

        // search bar
        SearchBar searchBar = new SearchBar();
        top.add(searchBar, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // scrollable grid
        grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null); // remove border
        add(scroll, BorderLayout.CENTER);

        // Mock Patients
        AddedPatientDB.addPatient(new Patient(1, "Raymond", "Ren", 82, 36.8, "120/80"));
        AddedPatientDB.addPatient(new Patient(2, "Jackson", "Zhou", 90, 37.0, "110/75"));
        AddedPatientDB.addPatient(new Patient(3, "David", "Wong", 130, 39.1, "160/95"));
        AddedPatientDB.addPatient(new Patient(4, "Xuan", "LiFeng", 82, 36.8, "120/80"));
        AddedPatientDB.addPatient(new Patient(5, "Martin", "Holloway", 90, 37.0, "110/75"));
        AddedPatientDB.addPatient(new Patient(6, "Harry", "Tan", 130, 39.1, "160/95"));

        // initial grid refresh
        refreshGrid(AddedPatientDB.getAll());

        // search function
        searchBar.addSearchListener(text -> {
            List<Patient> result = AddedPatientDB.search(text);
            refreshGrid(result);
        });
    }

    private void refreshGrid(List<Patient> patients) {
        grid.removeAll();

        for (Patient p : patients) {
            grid.add(new PatientTile(p, window));
        }

        // Add patient tile
        grid.add(new AddTile(window));

        grid.revalidate();
        grid.repaint();
    }
}

package UI.Pages;

import Models.AddedPatientDB;
import Models.Patient;
import UI.Components.SearchBar;
import UI.Components.Tiles.AddTile;
import UI.Components.WrapLayout;
import UI.MainWindow;

import UI.Components.Tiles.PatientTile;
import Utilities.SettingManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends JPanel {
    private JPanel grid; // contains all patient grids
    private MainWindow window;
    private String currentFilter = "";
    private final SettingManager settings = new SettingManager();

    public HomePage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());

        boolean darkMode = settings.isDarkMode();
        Color appBg = darkMode ? new Color(18, 18, 20) : new Color(245, 245, 245);
        setBackground(appBg);

        // initialize top panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        top.setBackground(appBg);

        // search bar
        SearchBar searchBar = new SearchBar();
        top.add(searchBar, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // scrollable grid
        grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        grid.setBackground(appBg);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null); // remove border
        add(scroll, BorderLayout.CENTER);

        // Mock Patients
        AddedPatientDB.addPatient(new Patient(1, "Stupid", "Raymond", 82, 36.8, "120/80"));
        AddedPatientDB.addPatient(new Patient(2, "Genius", "Harry", 90, 37.0, "110/75"));
//        AddedPatientDB.addPatient(new Patient(3, "David", "Wong", 130, 39.1, "160/95"));
//        AddedPatientDB.addPatient(new Patient(4, "Xuan", "Li Feng", 82, 36.8, "120/80"));
//        AddedPatientDB.addPatient(new Patient(5, "Martin", "Holloway", 90, 37.0, "110/75"));
//        AddedPatientDB.addPatient(new Patient(6, "Harry", "Tan", 130, 39.1, "160/95"));

        // initial grid refresh
        refresh();

        // search function – update filter text and refresh
        searchBar.addSearchListener(text -> {
            currentFilter = text;
            refresh();
        });
    }

    // refresh the list based on currentFilter and refresh grid
    public void refresh() {
        List<Patient> base = currentFilter == null || currentFilter.isEmpty()
                ? AddedPatientDB.getAll()
                : AddedPatientDB.search(currentFilter);
        refreshGrid(base);
    }

    private void refreshGrid(List<Patient> patients) {
        grid.removeAll();

        boolean darkMode = settings.isDarkMode();
        Color tileBg = darkMode ? new Color(36, 36, 42) : Color.WHITE;

        for (Patient p : AddedPatientDB.getSorted(patients)) {
            PatientTile t = new PatientTile(p, window, this);
            t.setBackground(tileBg);
            grid.add(t);
        }

        AddTile add = new AddTile(window);
        add.setBackground(tileBg);
        grid.add(add);

        grid.revalidate();
        grid.repaint();
    }

    public void onPageShown() {
        refresh();
    }

}

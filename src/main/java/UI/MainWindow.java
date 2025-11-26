package UI;

import UI.Pages.HomePage;
import UI.Pages.PatientDetailPage;
import UI.Pages.AddPatientPage;
import UI.Pages.SettingsPage;
import UI.Components.NavBar;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Page identifiers
    public static final String PAGE_HOME = "home";
    public static final String PAGE_DETAILS = "details";
    public static final String PAGE_ADD = "add";
    public static final String PAGE_SETTINGS = "settings";

    private HomePage homePage;

    public MainWindow() {
        setTitle("Patient Monitoring System");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main center panel with pages
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // initialize pages
        homePage = new HomePage(this);
        PatientDetailPage detailPage = new PatientDetailPage(this);
//        AddPatientPage addPage = new AddPatientPage(this);
//        SettingsPage settings = new SettingsPage();

        // add pages to main panel
        mainPanel.add(homePage, PAGE_HOME);
        mainPanel.add(detailPage, PAGE_DETAILS);
//        mainPanel.add(addPage, PAGE_ADD);
//        mainPanel.add(settings, PAGE_SETTINGS);

        // main panel to center
        add(mainPanel, BorderLayout.CENTER);

        // bottom navigation bar
        NavBar nav = new NavBar(this);
        add(nav, BorderLayout.SOUTH);

        setVisible(true);
    }

    // switch pages
    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }

    // pass data to detail page
    public void showPatientDetail(Models.Patient p) {
        PatientDetailPage detailPage = new PatientDetailPage(this);
        detailPage.setPatient(p);
        mainPanel.add(detailPage, PAGE_DETAILS); // overwrite previous
        showPage(PAGE_DETAILS);
    }
}

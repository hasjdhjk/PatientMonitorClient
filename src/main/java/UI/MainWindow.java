package UI;

import Models.Patient;
import UI.Components.SideBar;
import UI.Components.TopBar;
import UI.Pages.*;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel pageContainer;
    private SideBar sidebar;

    public static final String PAGE_HOME = "home";
    public static final String PAGE_DETAILS = "details";
    public static final String PAGE_ADD = "add";
    public static final String PAGE_STATUS = "status";
    public static final String PAGE_ACCOUNT = "account";
    public static final String PAGE_SETTINGS = "settings";

    private HomePage homePage;
    private StatusTrackerPage statusTrackerPage;

    public MainWindow() {
        setTitle("Patient Monitor");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // top bar
        add(new TopBar(), BorderLayout.NORTH);

        // sidebar (tabs for page switching)
        sidebar = new SideBar(this);
        add(sidebar, BorderLayout.WEST);

        // page container (card layout shows only 1 page at a time)
        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);

        // Pages
        homePage = new HomePage(this);
        pageContainer.add(homePage, PAGE_HOME);

        statusTrackerPage = new StatusTrackerPage(this);
        pageContainer.add(statusTrackerPage, PAGE_STATUS);

//        pageContainer.add(new AddPatientPage(), PAGE_ADD);
//        pageContainer.add(new SettingsPage(), PAGE_SETTINGS);
//        pageContainer.add(new AccountPage(), PAGE_ACCOUNT);

        add(pageContainer, BorderLayout.CENTER);

        setVisible(true);
    }

    public void showPage(String pageName) {
        cardLayout.show(pageContainer, pageName);
    }

    public void showStatusTracker(Patient patient) {
        sidebar.setSelected("Status Tracker");  // you already have something similar
        statusTrackerPage.setPatient(patient);  // new function we will add
        showPage(PAGE_STATUS);
    }

//    public void showPatientDetails(Models.Patient p) {
//        PatientDetailPage detailPage = new PatientDetailPage(this, p);
//        pageContainer.add(detailPage, PAGE_DETAILS); // replace
//        showPage(PAGE_DETAILS);
//    }
}

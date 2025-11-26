package UI;

import UI.Components.SideBar;
import UI.Components.TopBar;
import UI.Pages.*;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel pageContainer;

    public static final String PAGE_HOME = "home";
    public static final String PAGE_DETAILS = "details";
    public static final String PAGE_ADD = "add";
    public static final String PAGE_STATUS = "status";
    public static final String PAGE_ACCOUNT = "account";
    public static final String PAGE_SETTINGS = "settings";

    public MainWindow() {
        setTitle("Patient Monitor");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === Top Bar ===
        add(new TopBar(), BorderLayout.NORTH);

        // === Sidebar ===
        SideBar sidebar = new SideBar(this);
        add(sidebar, BorderLayout.WEST);

        // === Page Container (CardLayout) ===
        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);

        // Pages
        pageContainer.add(new HomePage(this), PAGE_HOME);
//        pageContainer.add(new AddPatientPage(), PAGE_ADD);
//        pageContainer.add(new SettingsPage(), PAGE_SETTINGS);
//        pageContainer.add(new StatusTrackerPage(), PAGE_STATUS);
//        pageContainer.add(new AccountPage(), PAGE_ACCOUNT);

        add(pageContainer, BorderLayout.CENTER);

        setVisible(true);
    }

    public void showPage(String pageName) {
        cardLayout.show(pageContainer, pageName);
    }

    public void showPatientDetails(Models.Patient p) {
        PatientDetailPage detailPage = new PatientDetailPage(this, p);
        pageContainer.add(detailPage, PAGE_DETAILS); // replace
        showPage(PAGE_DETAILS);
    }
}

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
    public static final String PAGE_LOGIN = "login";
    public static final String PAGE_REGISTER = "register";
    public static final String PAGE_ADD = "add";
    public static final String PAGE_STATUS = "status";
    public static final String PAGE_ACCOUNT = "account";
    public static final String PAGE_SETTINGS = "settings";

    private HomePage homePage;
    private LoginPage loginPage;
    private RegisterPage registerPage;
    private StatusTrackerPage statusTrackerPage;

    public MainWindow() {
        setTitle("Patient Monitor");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- CardLayout container ---
        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);

        // --- Create pages ONCE ---
        loginPage = new LoginPage(this);
        registerPage = new RegisterPage(this);
        homePage = new HomePage(this);
        statusTrackerPage = new StatusTrackerPage(this);

        // --- Add pages to CardLayout ---
        pageContainer.add(loginPage, PAGE_LOGIN);
        pageContainer.add(registerPage, PAGE_REGISTER);
        pageContainer.add(homePage, PAGE_HOME);
        pageContainer.add(statusTrackerPage, PAGE_STATUS);

        // show login at start
        add(pageContainer, BorderLayout.CENTER);
        cardLayout.show(pageContainer, PAGE_LOGIN);

        setVisible(true);
    }

//    public MainWindow() {
//        setTitle("Patient Monitor");
//        setSize(1920, 1080);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new BorderLayout());
//
//        // top bar
//        add(new TopBar(), BorderLayout.NORTH);
//
//        // sidebar (tabs for page switching)
//        sidebar = new SideBar(this);
//        add(sidebar, BorderLayout.WEST);
//
//        // page container (card layout shows only 1 page at a time)
//        cardLayout = new CardLayout();
//        pageContainer = new JPanel(cardLayout);
//
//        // Pages
//        homePage = new HomePage(this);
//        statusTrackerPage = new StatusTrackerPage(this);
//
//        pageContainer.add(homePage, PAGE_HOME);
//        pageContainer.add(statusTrackerPage, PAGE_STATUS);
//

    /// /        pageContainer.add(new AddPatientPage(), PAGE_ADD);
    /// /        pageContainer.add(new SettingsPage(), PAGE_SETTINGS);
    /// /        pageContainer.add(new AccountPage(), PAGE_ACCOUNT);
//
//        add(pageContainer, BorderLayout.CENTER);
//
//        setVisible(true);
//    }

    public void showLoginPage() {
        showPage(PAGE_LOGIN);
    }

    public void showRegisterPage() {
        showPage(PAGE_REGISTER);
    }

    public void showHomePage() {
        showPage(PAGE_HOME);
    }

    public void showStatusTracker(Patient patient) {
        sidebar.setSelected("Status Tracker");
        statusTrackerPage.setPatient(patient);
        showPage(PAGE_STATUS);
    }

    public void showPage(String pageName) {
        cardLayout.show(pageContainer, pageName);
    }

}
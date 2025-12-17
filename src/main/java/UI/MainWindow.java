package UI;

import Models.Patient;
import UI.Components.SideBar;
import UI.Components.TopBar;
import UI.Pages.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel pageContainer;
    private TopBar topBar;
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
    private SettingsPage settingsPage;
    private AccountPage accountPage;
    private AddPatientPage addPatientPage;
    private JPanel cardPanel;


    public MainWindow() {
        setTitle("Patient Monitor");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // initialize sidebar and top bar
        topBar = new TopBar();
        sidebar = new SideBar(this);
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        topBar.setVisible(true);
        sidebar.setVisible(true);

        // card layout
        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);

        // initialize pages
        loginPage = new LoginPage(this);
        registerPage = new RegisterPage(this);
        homePage = new HomePage(this);
        statusTrackerPage = new StatusTrackerPage(this);
        //settingsPage = new SettingsPage(this);
        accountPage = new AccountPage(this);
        //addPatientPage = new AddPatientPage(this);
        settingsPage = new SettingsPage(this);
        addPatientPage = new AddPatientPage(this);

        pageContainer.add(loginPage, PAGE_LOGIN);
        pageContainer.add(registerPage, PAGE_REGISTER);
        pageContainer.add(homePage, PAGE_HOME);
        pageContainer.add(statusTrackerPage, PAGE_STATUS);
        //pageContainer.add(settingsPage, PAGE_SETTINGS);
        pageContainer.add(accountPage, PAGE_ACCOUNT);
        //pageContainer.add(addPatientPage, PAGE_ADD);
        pageContainer.add(settingsPage, PAGE_SETTINGS);
        pageContainer.add(addPatientPage, PAGE_ADD);

        // show login at start
        add(pageContainer, BorderLayout.CENTER);
        cardLayout.show(pageContainer,PAGE_ACCOUNT);
        //PAGE_LOGIN
        setVisible(true);
    }
//    public MainWindow() {
//        setTitle("Patient Monitor");
//        setSize(1920, 1080);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new BorderLayout());
//
//        // initialize sidebar and top bar
//        topBar = new TopBar();
//        sidebar = new SideBar(this);
//        add(topBar, BorderLayout.NORTH);
//        add(sidebar, BorderLayout.WEST);
//        topBar.setVisible(false);
//        sidebar.setVisible(false);
//
//        // card layout
//        cardLayout = new CardLayout();
//        pageContainer = new JPanel(cardLayout);
//
//        // initialize pages
//        loginPage = new LoginPage(this);
//        registerPage = new RegisterPage(this);
//        homePage = new HomePage(this);
//        statusTrackerPage = new StatusTrackerPage(this);
//
//        pageContainer.add(loginPage, PAGE_LOGIN);
//        pageContainer.add(registerPage, PAGE_REGISTER);
//        pageContainer.add(homePage, PAGE_HOME);
//        pageContainer.add(statusTrackerPage, PAGE_STATUS);
//
//        // show login at start
//        add(pageContainer, BorderLayout.CENTER);
//        cardLayout.show(pageContainer, PAGE_LOGIN);
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
        boolean isAuthPage = pageName.equals(PAGE_LOGIN) || pageName.equals(PAGE_REGISTER);

        sidebar.setVisible(!isAuthPage);
        topBar.setVisible(!isAuthPage);
        if (pageName.equals(PAGE_LOGIN)) loginPage.clearFields();

        cardLayout.show(pageContainer, pageName);

        revalidate();
        repaint();
    }

    public void navigateTo(String pageName){
        cardLayout.show(cardPanel, pageName);
    }
}
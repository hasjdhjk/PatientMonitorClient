package UI;

import Services.DoctorProfileService;
import Utilities.SettingManager;
import Utilities.ThemeManager;

import Models.Patients.Patient;
import UI.Components.SideBar;
import UI.Components.TopBar;
import UI.Pages.*;

import javax.swing.*;
import java.awt.*;

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
    public static final String PAGE_LIVE = "live";

    private HomePage homePage;
    private LoginPage loginPage;
    private RegisterPage registerPage;
    private DigitalTwinPage digitalTwinPage;
    private SettingsPage settingsPage;
    private AccountPage accountPage;
    private AddPatientPage addPatientPage;
    private LiveMonitoringPage liveMonitoringPage;

    public MainWindow() {
        setTitle("Patient Monitor");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= Top bar & sidebar =================
        topBar = new TopBar(this);
        sidebar = new SideBar(this);
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        topBar.setVisible(true);
        sidebar.setVisible(true);

        // ================= Card layout =================
        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);

        // ================= Pages =================
        loginPage = new LoginPage(this);
        registerPage = new RegisterPage(this);
        homePage = new HomePage(this);
        digitalTwinPage = new DigitalTwinPage(this);
        settingsPage = new SettingsPage(this);
        accountPage = new AccountPage(this);
        addPatientPage = new AddPatientPage(this);

        Patient dummyPatient = new Patient(
                1, "John", "Anderson", "Male", 36, "120/80"
        );
        liveMonitoringPage = new LiveMonitoringPage(dummyPatient, this);

        // ================= Add to card container =================
        pageContainer.add(loginPage, PAGE_LOGIN);
        pageContainer.add(registerPage, PAGE_REGISTER);
        pageContainer.add(homePage, PAGE_HOME);
        pageContainer.add(digitalTwinPage, PAGE_STATUS);
        pageContainer.add(settingsPage, PAGE_SETTINGS);
        pageContainer.add(accountPage, PAGE_ACCOUNT);
        pageContainer.add(addPatientPage, PAGE_ADD);
        pageContainer.add(liveMonitoringPage, PAGE_LIVE);

        add(pageContainer, BorderLayout.CENTER);
        showPage(PAGE_LOGIN);
        //cardLayout.show(pageContainer, PAGE_HOME);

        // ================= Theme =================
        SettingManager settings = new SettingManager();
        ThemeManager.apply(this, settings.isDarkMode());

        DoctorProfileService ps = new DoctorProfileService();
        var p = ps.load();
        topBar.updateDoctorInfo(p.getFullName(), p.getSpecialty());

        setVisible(true);
    }

    // ================= Navigation =================

    public void showLoginPage() {
        showPage(PAGE_LOGIN);
    }

    public void showRegisterPage() {
        showPage(PAGE_REGISTER);
    }

    public void showHomePage() {
        homePage.onPageShown();
        showPage(PAGE_HOME);
    }

    public void showStatusTracker(Patient patient) {
        sidebar.setSelected("Status Tracker");
        digitalTwinPage.setPatient(patient);
        showPage(PAGE_STATUS);
    }

    public void showAddPatientPage() {
        sidebar.setSelected("Add Patient");
        showPage(PAGE_ADD);
    }

    public void showLiveMonitoring(Patient patient) {
        sidebar.setSelected("Live Monitoring");

        pageContainer.remove(liveMonitoringPage);
        liveMonitoringPage = new LiveMonitoringPage(patient, this);
        pageContainer.add(liveMonitoringPage, PAGE_LIVE);

        showPage(PAGE_LIVE);
    }

    public void showPage(String pageName) {
        boolean isAuthPage =
                pageName.equals(PAGE_LOGIN) || pageName.equals(PAGE_REGISTER);

        sidebar.setVisible(!isAuthPage);
        topBar.setVisible(!isAuthPage);

        if (pageName.equals(PAGE_LOGIN)) {
            loginPage.clearFields();
        }

        if (pageName.equals(PAGE_ACCOUNT)) {
            // Ensure the Account page reflects the latest stored clinician profile
            accountPage.reloadProfile();
        }

        cardLayout.show(pageContainer, pageName);
        revalidate();
        repaint();
    }

    public void logout() {
        showPage(PAGE_LOGIN);
    }

    public AccountPage getAccountPage() {
        return accountPage;
    }
    public TopBar getTopBar() {return topBar;}
    public void onDoctorLoggedIn() {
        cardLayout.show(pageContainer, PAGE_HOME);

        if (topBar != null) {
        }
        if (sidebar != null) {
            sidebar.setVisible(true);
        }
    }
}

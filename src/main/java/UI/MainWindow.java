package UI;

import NetWork.Session;
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

    // Creates the main application window and initialises all pages, navigation, and theme settings.
    public MainWindow() {
        setTitle("Patient Monitor");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top bar & sidebar
        topBar = new TopBar(this);
        sidebar = new SideBar(this);
        add(topBar, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        topBar.setVisible(true);
        sidebar.setVisible(true);

        // Card layout
        cardLayout = new CardLayout();
        pageContainer = new JPanel(cardLayout);

        // Pages
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

        // Pages
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

        // Theme
        SettingManager settings = new SettingManager();
        ThemeManager.apply(this, settings.isDarkMode());

        refreshTopBarDoctorInfo();

        setVisible(true);
    }

    // Navigates the UI to the login page.
    public void showLoginPage() {
        showPage(PAGE_LOGIN);
    }

    // Navigates the UI to the register page
    public void showRegisterPage() {
        showPage(PAGE_REGISTER);
    }

    // Navigates to the home page and enables alert behaviour for live monitoring
    public void showHomePage() {
        Services.AlertManager.getInstance().enableAlerts();
        homePage.onPageShown();
        showPage(PAGE_HOME);
    }

    // Navigates to the status tracker (digital twin) page for the given patient
    public void showStatusTracker(Patient patient) {
        sidebar.setSelected("Status Tracker");
        digitalTwinPage.setPatient(patient);
        showPage(PAGE_STATUS);
    }

    // Navigates to the add patient page.
    public void showAddPatientPage() {
        sidebar.setSelected("Add Patient");
        showPage(PAGE_ADD);
    }

    // Navigates to the live monitoring page and swaps in a page instance for the chosen patient.
    public void showLiveMonitoring(Patient patient) {
        sidebar.setSelected("Live Monitoring");

        pageContainer.remove(liveMonitoringPage);
        liveMonitoringPage = new LiveMonitoringPage(patient, this);
        pageContainer.add(liveMonitoringPage, PAGE_LIVE);

        showPage(PAGE_LIVE);
    }

    // Shows a named page and toggles top-level UI components based on authentication state.
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

    // Logs out the current user by returning to the login page.
    public void logout() {
        showPage(PAGE_LOGIN);
    }

    // Updates the UI after a successful login, including top bar doctor identity display.
    public void onDoctorLoggedIn() {
        // Ensure UI switches out of auth pages
        showHomePage();

        // Refresh top bar doctor info using the logged-in identity
        refreshTopBarDoctorInfo();

        if (sidebar != null) {
            sidebar.setVisible(true);
        }
        if (topBar != null) {
            topBar.setVisible(true);
        }
    }

    // Refreshes the top bar doctor details using the current session information
    private void refreshTopBarDoctorInfo() {
        if (topBar == null) return;

        String email = Session.getDoctorEmail();
        if (email != null && !email.isBlank() && !"demo".equalsIgnoreCase(email.trim())) {
            topBar.updateDoctorInfo(Session.getDoctorFullName(), Session.getDoctorRole());
        } else {
            topBar.updateDoctorInfo("Demo", "");
        }
    }
    // Returns the account page instance for external components that need to refresh profile state
    public AccountPage getAccountPage() {
        return accountPage;
    }
    // Returns the top bar component for pages that need to update doctor display information
    public TopBar getTopBar() {return topBar;}
}

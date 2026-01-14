package UI.Pages;

import Models.Patients.AddedPatientDB;
import Models.Patients.Patient;
import Services.PatientDischargeService;

import UI.Components.SearchBar;
import UI.Components.Tiles.AddTile;
import UI.Components.WrapLayout;
import UI.MainWindow;
import UI.Components.Tiles.PatientTile;
import Utilities.SettingManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import UI.Components.AlertHistorySidebar;
import NetWork.ServerConfig;

public class HomePage extends JPanel {
    private JPanel grid;
    private MainWindow window;
    private String currentFilter = "";
    private final SettingManager settings = new SettingManager();

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    public HomePage(MainWindow window) {
        this.window = window;

        setLayout(new BorderLayout());

        boolean darkMode = settings.isDarkMode();
        Color appBg = darkMode ? new Color(18, 18, 20) : new Color(245, 245, 245);
        setBackground(appBg);

        // top (Search bar row)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        top.setBackground(appBg);

        SearchBar searchBar = new SearchBar();
        top.add(searchBar);

        // center (Grid + Scroll)
        grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        grid.setBackground(appBg);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(appBg);

        // Wrap only the main content (top + grid) so the sidebar isn't pushed down by NORTH
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(appBg);
        centerWrapper.add(top, BorderLayout.NORTH);
        centerWrapper.add(scroll, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // right (Alert sidebar)
        AlertHistorySidebar sidebar = new AlertHistorySidebar(window);
        add(sidebar, BorderLayout.EAST);

        PatientDischargeService.onDischarge = this::reloadFromServerAsync;

        reloadFromServerAsync();
        refresh();

        searchBar.addSearchListener(text -> {
            currentFilter = text;
            refresh();
        });
    }

    public void reloadFromServerAsync() {
        final String doctorUsername = NetWork.Session.getDoctorEmail();

        SwingWorker<List<Patient>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                String doctor = URLEncoder.encode(doctorUsername, StandardCharsets.UTF_8);

                String url = ServerConfig.url("/api/patients?doctor=" + doctor);

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() != 200) {
                    throw new RuntimeException("GET " + url + " failed: HTTP " + resp.statusCode() + " body=" + resp.body());
                }

                Type listType = new TypeToken<List<Patient>>() {}.getType();
                return GSON.fromJson(resp.body(), listType);
            }

            @Override
            protected void done() {
                try {
                    List<Patient> fromServer = get();
                    AddedPatientDB.replaceAll(fromServer);
                } catch (Exception e) {
                    e.printStackTrace();
                    // If fetch fails, keep current cache (avoid wiping UI)
                }
                refresh();
            }
        };

        worker.execute();
    }

    public void syncPatientsFromServerAsync() {
        reloadFromServerAsync();
    }

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
        reloadFromServerAsync();
    }
}

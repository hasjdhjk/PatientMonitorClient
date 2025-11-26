package UI;

public class MainWindow extends JFrame {

    private PatientGridPanel gridPanel;

    public MainWindow() {
        setTitle("Patient Monitoring System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gridPanel = new PatientGridPanel();

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        loadPatientsFromServer();

        setVisible(true);
    }

    private void loadPatientsFromServer() {
        List<Patient> patients = ApiClient.getPatients();

        for (Patient p : patients) {
            gridPanel.addPatient(p);
        }
    }
}

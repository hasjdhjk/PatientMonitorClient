import UI.MainWindow;
import UI.Pages.AddPatientPage;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow(); 
        });
    }
}

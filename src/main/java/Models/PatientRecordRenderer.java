package Models;

import javax.swing.*;
import java.awt.*;

public class PatientRecordRenderer extends JPanel implements ListCellRenderer<PatientRecord> {

    private JLabel name = new JLabel();
    private JLabel recordId = new JLabel();
    private JLabel diagnosis = new JLabel();
    private JLabel date = new JLabel();

    public PatientRecordRenderer() {
        setLayout(new GridLayout(2, 2));
        setOpaque(true);

        name.setFont(new Font("Arial", Font.BOLD, 14));
        diagnosis.setFont(new Font("Arial", Font.PLAIN, 12));
        recordId.setFont(new Font("Arial", Font.PLAIN, 12));
        date.setFont(new Font("Arial", Font.PLAIN, 12));

        add(name);
        add(recordId);
        add(diagnosis);
        add(date);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends PatientRecord> list, PatientRecord value,
            int index, boolean isSelected, boolean cellHasFocus) {

        name.setText(value.getPatientName());
        recordId.setText("ID: " + value.getRecordId());
        diagnosis.setText("Diagnosis: " + value.getDiagnosis());
        date.setText("Date: " + value.getDate());

        if (isSelected) {
            setBackground(new Color(200, 220, 255));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }
}
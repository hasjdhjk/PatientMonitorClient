package Models;

import javax.swing.*;
import java.awt.*;

public class PatientRecordRenderer extends JPanel implements ListCellRenderer<PatientRecord> {

    private JLabel name = new JLabel();
    private JLabel id = new JLabel();

    public PatientRecordRenderer() {
        setLayout(new BorderLayout());
        add(name, BorderLayout.CENTER);
        add(id, BorderLayout.EAST);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends PatientRecord> list,
            PatientRecord value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        name.setText(value.getPatientName());
        id.setText(value.getRecordId());

        if (isSelected) {
            setBackground(new Color(200, 220, 255));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }
}
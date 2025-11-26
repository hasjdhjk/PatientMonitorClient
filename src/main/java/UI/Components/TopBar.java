package UI.Components;

import javax.swing.*;
import java.awt.*;

// thin bar at top displaying name of app
public class TopBar extends JPanel {

    public TopBar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 50));
        setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Patient Monitor");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));

        add(title, BorderLayout.WEST);
    }
}

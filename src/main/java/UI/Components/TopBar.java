package UI.Components;

import Utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {

    private static final Color BAR_COLOR = new Color(63, 90, 227);

    private JLabel title;

    public TopBar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 90));
        setBackground(BAR_COLOR);

        add(buildLeftSection(), BorderLayout.WEST);
        add(buildRightSection(), BorderLayout.EAST);
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        forceWhiteText(this);
    }

    private void forceWhiteText(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JLabel lbl) {
                lbl.setForeground(Color.WHITE);
            }
            if (comp instanceof Container child) {
                forceWhiteText(child);
            }
        }
    }

    // ================= LEFT: LOGO + TITLE =================
    private JPanel buildLeftSection() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel logo = new JLabel(
                ImageLoader.loadImageScaled("icon_logo2", "Icons", 100)
        );

        title = new JLabel("Patient Monitor");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);

        left.add(logo);
        left.add(title);

        return left;
    }

    // ================= RIGHT: ICON BUTTON =================
    private JPanel buildRightSection() {
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        right.setOpaque(false);

        JButton iconButton = new JButton(
                ImageLoader.loadImage("icon_settings", "Icons", 32)
        );

        iconButton.setFocusPainted(false);
        iconButton.setBorderPainted(false);
        iconButton.setContentAreaFilled(false);
        iconButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconButton.setToolTipText("Settings");

        right.add(iconButton);

        return right;
    }
}

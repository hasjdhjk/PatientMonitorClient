package Utilities;
import UI.Components.Tiles.BaseTile;

import UI.Components.Tiles.RoundedButton;
import UI.Components.Tiles.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public final class ThemeManager {

    private ThemeManager() {}

    public static void apply(JFrame frame, boolean darkMode) {
        Color appBg = darkMode ? new Color(24, 26, 30) : Color.WHITE;
        Color fg = darkMode ? new Color(230, 230, 230) : Color.BLACK;
        Color card  = darkMode ? new Color(36, 36, 42) : Color.WHITE;


        // Standard Swing defaults
        UIManager.put("Panel.background", appBg);
        UIManager.put("Label.foreground", fg);
        UIManager.put("Button.foreground", fg);
        UIManager.put("CheckBox.foreground", fg);
        UIManager.put("CheckBox.background", appBg);
        UIManager.put("ComboBox.foreground", fg);
        UIManager.put("ComboBox.background", darkMode ? new Color(45, 48, 56) : Color.WHITE);
        UIManager.put("TextField.foreground", fg);
        UIManager.put("TextField.background", darkMode ? new Color(45, 48, 56) : Color.WHITE);

        //SwingUtilities.updateComponentTreeUI(frame);

        // Custom components
        updateTree(frame.getContentPane(), darkMode, appBg, fg, card);

        frame.revalidate();
        frame.repaint();
    }

    private static void updateTree(Component c, boolean darkMode, Color appBg, Color fg, Color card) {
        if (c instanceof JComponent jc) {
            jc.setForeground(fg);
        }

        if (c instanceof BaseTile bt) {
            bt.setBackground(card);
        } else if (c instanceof JPanel p) {
            p.setBackground(appBg);
        }

        if (c instanceof RoundedPanel rp) {
            rp.setFillColor(card);
        }

        if (c instanceof RoundedButton rb) {
            if (darkMode) {
                rb.setColors(
                        new Color(90, 140, 255),
                        new Color(120, 165, 255),
                        new Color(70, 120, 230),
                        Color.WHITE
                );
            } else {
                rb.setColors(
                        new Color(70, 130, 250),
                        new Color(100, 150, 255),
                        new Color(50, 110, 220),
                        Color.WHITE
                );
            }
        }

        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                updateTree(child, darkMode, appBg, fg, card);
            }
        }
    }

}

package Utilities;
import UI.Components.Tiles.BaseTile;
import UI.Components.TopBar;
import UI.Components.Tiles.RoundedButton;
import UI.Components.Tiles.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public final class ThemeManager {

    private ThemeManager() {}
    private static final Color TOPBAR_BG_LIGHT = new Color(63, 90, 227);
    private static final Color TOPBAR_BG_DARK  = new Color(32, 48, 140);

    private static final Color TOPBAR_TEXT_LIGHT = Color.WHITE;
    private static final Color TOPBAR_TEXT_DARK  = Color.WHITE;

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
        if (c instanceof JTextField tf) {
            tf.setOpaque(false);
            tf.setForeground(darkMode ? new Color(230, 230, 230) : Color.BLACK);
            tf.setCaretColor(darkMode ? new Color(230, 230, 230) : Color.BLACK);

            tf.setBackground(darkMode ? new Color(70, 70, 78) : new Color(245, 245, 245));



        }

        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                updateTree(child, darkMode, appBg, fg, card);
            }
        }

        if (c instanceof TopBar tb) {
            tb.setOpaque(true);

            Color bg = darkMode ? TOPBAR_BG_DARK : TOPBAR_BG_LIGHT;
            tb.setBackground(bg);

            Color text = darkMode ? TOPBAR_TEXT_DARK : TOPBAR_TEXT_LIGHT;

            for (Component child : tb.getComponents()) {
                if (child instanceof JLabel lbl) {
                    lbl.setForeground(text);
                }
                if (child instanceof JButton btn) {
                    btn.setForeground(text);
                }
            }
        }

    }

}

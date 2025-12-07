package UI.Components.Tiles;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int radius = 20;
    public RoundedPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(245, 245, 245));  // 浅灰背景
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        super.paintComponent(g);
    }
}

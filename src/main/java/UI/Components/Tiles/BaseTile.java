package UI.Components.Tiles;

import javax.swing.*;
import java.awt.*;

public class BaseTile extends JPanel {

    private int radius = 30;
    private int shadowSize = 15;

    public BaseTile() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // ==== BLURRED SHADOW ====
        for (int i = 0; i < shadowSize; i++) {
            int alpha = (int) (1 - i * (1 / shadowSize)); // fade out
            if (alpha < 0) alpha = 0;

            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRoundRect(
                    i,
                    i,
                    w - i * 2,
                    h - i * 2,
                    radius + i,
                    radius + i
            );
        }

        // ==== MAIN WHITE TILE ====
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(
                shadowSize,
                shadowSize,
                w - shadowSize * 2,
                h - shadowSize * 2,
                radius,
                radius
        );

        g2.dispose();
        super.paintComponent(g);
    }
}


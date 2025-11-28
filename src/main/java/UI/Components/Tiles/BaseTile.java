package UI.Components.Tiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BaseTile extends JPanel {

    private int radius;
    private int shadowSize = 15;
    private Color tileColor = Color.WHITE;
    private boolean hasHoverEffect;

    public BaseTile(int width, int height, int radius, boolean hasHoverEffect) {
        this.radius = radius;
        setOpaque(false);
        setPreferredSize(new Dimension(width, height));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (hasHoverEffect) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tileColor = new Color(240, 240, 240);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tileColor = Color.WHITE;
                    repaint();
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // blurry shadow
        for (int i = 0; i < shadowSize; i++) {
            int alpha = (int) (1 - i * (1 / shadowSize));
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

        // main background
        g2.setColor(tileColor);
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

    @Override
    public void setBackground(Color bg) {
        this.tileColor = bg;
        repaint();
    }
}

package UI.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// stick a patient tile on top
public class StickyButton extends JPanel {

    private boolean selected = false;

    public StickyButton() {
        setPreferredSize(new Dimension(40, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                repaint();
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(selected ? new Color(255, 215, 0) : new Color(220, 220, 220));
        g2.fillOval(0, 0, getWidth(), getHeight());

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2f));

        // simple star icon
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int r = 12;

        Polygon star = new Polygon();
        for (int i = 0; i < 5; i++) {
            double theta = Math.toRadians(i * 72 - 90);
            double theta2 = theta + Math.toRadians(36);
            star.addPoint((int)(cx + r * Math.cos(theta)), (int)(cy + r * Math.sin(theta)));
            star.addPoint((int)(cx + (r/2) * Math.cos(theta2)), (int)(cy + (r/2) * Math.sin(theta2)));
        }

        g2.drawPolygon(star);

        g2.dispose();
    }
}

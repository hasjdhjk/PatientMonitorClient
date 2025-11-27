package UI.Components;

import javax.swing.*;
import java.awt.*;

public class ECGPanel extends JPanel {

    public ECGPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(250, 120));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // background rounded rectangle
        g2.setColor(new Color(234, 234, 234));
        g2.fillRoundRect(0, 0, w, h, 30, 30);

//        g2.setColor(new Color(200, 200, 200));
//        g2.setStroke(new BasicStroke(2f));
//        g2.drawRoundRect(0, 0, w, h, 30, 30);

        // draw simple ECG wave mock
        g2.setColor(new Color(255, 0, 0));
        g2.setStroke(new BasicStroke(2.4f));

        int mid = h / 2;

        int[] xs = new int[w];
        int[] ys = new int[w];

        for (int i = 0; i < w; i++) {
            double t = i / 25.0;
            ys[i] = (int) (mid + 15 * Math.sin(t) + 5 * Math.sin(t * 3));
            xs[i] = i;
        }

        g2.drawPolyline(xs, ys, w);
        g2.dispose();
    }
}

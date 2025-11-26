package UI.Components.Tiles;

import Utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;

// to set the background a round circle rectangle
public class BaseTile extends JPanel {
    protected Image bgImage;

    public BaseTile() {
        bgImage = ImageLoader.loadImage("background_roundSquare", "Backgrounds", 300).getImage();
        setOpaque(false);
        setPreferredSize(new Dimension(300,300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}

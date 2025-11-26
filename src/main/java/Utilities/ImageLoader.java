package Utilities;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {

    public static ImageIcon loadImage(String imageName, String folderName, int size) {
        try {
            URL url = ImageIcon.class.getResource("/" + folderName +"/" + imageName + ".png");
            if (url == null) return null;

            Image img = new ImageIcon(url).getImage();
            Image scaled = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

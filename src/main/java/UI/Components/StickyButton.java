package UI.Components;

import Models.Patient;
import UI.Pages.HomePage;
import Utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// stick a patient tile on top
public class StickyButton extends JButton {

    private boolean selected = false;

    public StickyButton(Patient patient) {
        // set image
        ImageIcon normalImage = ImageLoader.loadImage("stickybutton", "UI", 40);
        ImageIcon selectedImage = ImageLoader.loadImage("stickybutton_highlight", "UI", 40);
        setIcon(normalImage);

        // Appearance cleanup
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                patient.setSticky(selected);
                setIcon(selected ? selectedImage : normalImage);
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

}

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
    private final ImageIcon normalImage;
    private final ImageIcon selectedImage;

    public StickyButton(Patient patient, HomePage homePage) {
        // set image
        normalImage = ImageLoader.loadImage("stickybutton", "UI", 35);
        selectedImage = ImageLoader.loadImage("stickybutton_highlight", "UI", 35);
        selected = patient.isSticky();

        // appearance cleanup
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // set initial icon after configuring button
        setIcon(selected ? selectedImage : normalImage);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                patient.setSticky(selected);
                setIcon(selected ? selectedImage : normalImage);

                homePage.refresh();
            }
        });
    }

    public boolean isSelected() {
        return selected;
    }

}

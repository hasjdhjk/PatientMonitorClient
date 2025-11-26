package UI.Components.Tiles;

import UI.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AddTile extends BaseTile {

    public AddTile(MainWindow window) {
        super(390, 300, 30);

        JLabel plus = new JLabel("+");
        plus.setFont(new Font("Arial", Font.BOLD, 50));
        plus.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(plus);
        add(Box.createVerticalGlue());

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                window.showPage(MainWindow.PAGE_ADD);
            }
        });
    }
}

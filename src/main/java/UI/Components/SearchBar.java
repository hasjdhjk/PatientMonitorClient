package UI.Components;

import UI.Components.Tiles.BaseTile;
import Utilities.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class SearchBar extends BaseTile {

    public SearchBar() {
        super(400, 65, 40);
        setLayout(new BorderLayout());

        // search field
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        searchField.setOpaque(false);

        // icon
        JLabel searchLabel = new JLabel(ImageLoader.loadImage("icon_search", "Icons", 25));
        add(searchLabel, BorderLayout.WEST);

        add(searchField, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30)); // make sure text inside teh border
    }
}

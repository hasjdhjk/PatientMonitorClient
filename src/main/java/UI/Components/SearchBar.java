package UI.Components;

import UI.Components.Tiles.BaseTile;

import javax.swing.*;
import java.awt.*;

public class SearchBar extends BaseTile {

    public SearchBar() {
        super(400, 65, 40);
        setLayout(new BorderLayout());

        JTextField searchField = new JTextField();
        searchField.setBorder(null);
        searchField.setOpaque(false);

        add(searchField, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30)); // make sure text inside teh border
    }
}

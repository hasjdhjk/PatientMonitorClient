package UI.Components;

import UI.Components.Tiles.BaseTile;

import javax.swing.*;
import java.awt.*;

public class SearchBar extends BaseTile {

    public SearchBar() {
        super(450, 65);
        setLayout(new BorderLayout());

        JTextField searchField = new JTextField();
        searchField.setBorder(null);
        searchField.setOpaque(false);

        add(searchField, BorderLayout.CENTER);
    }
}

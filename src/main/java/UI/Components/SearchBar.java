package UI.Components;

import UI.Components.Tiles.BaseTile;

import javax.swing.*;
import java.awt.*;

public class SearchBar extends BaseTile {

    public SearchBar() {
        setPreferredSize(new Dimension(300, 50));
        setLayout(new BorderLayout());

        JTextField searchField = new JTextField();
        searchField.setBorder(null);
        searchField.setOpaque(false);

        add(searchField, BorderLayout.CENTER);
    }
}

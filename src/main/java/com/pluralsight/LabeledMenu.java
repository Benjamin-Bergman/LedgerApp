// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.*;
import com.googlecode.lanterna.gui2.LinearLayout.*;
import com.googlecode.lanterna.input.*;

import java.util.*;

/**
 * Represents a list of options accessible via keyboard shortcuts.
 */
class LabeledMenu extends BasicWindow {
    private Collection<LabeledButton> buttons;

    LabeledMenu(String title) {
        super(title);
        buttons = List.of();
        render();
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        for (LabeledButton button : buttons)
            if (button.handleKeyStroke(key) == Result.HANDLED)
                return false;

        return super.handleInput(key);
    }

    /**
     * Regenerates this menu's layout with a new set of buttons.
     *
     * @param newButtons The new buttons to use.
     */
    final void render(LabeledButton... newButtons) {
        buttons = Arrays.stream(newButtons).toList();

        setCloseWindowWithEscape(true);

        var comp = new Panel();
        var justify = LinearLayout.createLayoutData(Alignment.Fill, GrowPolicy.CanGrow);

        for (var button : buttons)
            comp.addComponent(button, justify);

        setComponent(comp);
    }
}

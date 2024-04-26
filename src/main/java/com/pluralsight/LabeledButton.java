// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.*;

/**
 * Represents a {@link Button} with a highlighted character to indicate keyboard shortcuts.
 */
final class LabeledButton extends Button {
    @SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized")
    private char highlighted;

    /**
     * @param label       The text of the button.
     * @param highlighted The character to highlight.
     */
    LabeledButton(String label, char highlighted) {
        super(label);
        setHighlighted(highlighted);
    }

    /**
     * @param label       The text of the button.
     * @param highlighted The character to highlight.
     * @param action      Action to fire when the user triggers the button by pressing the enter or the space key.
     */
    LabeledButton(String label, char highlighted, Runnable action) {
        super(label, action);
        setHighlighted(highlighted);
    }

    /**
     * @return The highlighted character.
     */
    char getHighlighted() {
        return highlighted;
    }

    /**
     * Updates the highlighted character.
     *
     * @param highlighted The new character to highlight.
     */
    void setHighlighted(char highlighted) {
        this.highlighted = highlighted;
        setRenderer(createDefaultRenderer());
    }

    @Override
    protected ButtonRenderer createDefaultRenderer() {
        return new LabeledButtonRenderer(getLabel().indexOf(highlighted));
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if (
            (keyStroke.getKeyType() == KeyType.Character) &&
            keyStroke.getCharacter().toString().equalsIgnoreCase(String.valueOf(highlighted))
        ) {
            triggerActions();
            return Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }

    private record LabeledButtonRenderer(int highlighted) implements ButtonRenderer {
        private static int getLabelShift(Button button, TerminalSize size) {
            int availableSpace = size.getColumns() - 2;
            if (availableSpace <= 0) return 0;
            int widthInColumns = TerminalTextUtils.getColumnWidth(button.getLabel());
            if (availableSpace <= widthInColumns) return 0;
            return (availableSpace - widthInColumns) / 2;
        }

        @Override
        public TerminalPosition getCursorLocation(Button button) {
            if (button.getThemeDefinition().isCursorVisible())
                return new TerminalPosition(1 + getLabelShift(button, button.getSize()) + highlighted, 0);
            return null;
        }

        @Override
        public TerminalSize getPreferredSize(Button button) {
            return new TerminalSize(TerminalTextUtils.getColumnWidth(button.getLabel()) + 2, 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics textGUIGraphics, Button button) {
            ThemeDefinition themeDefinition = button.getThemeDefinition();
            if (button.isFocused()) textGUIGraphics.applyThemeStyle(themeDefinition.getActive());
            else textGUIGraphics.applyThemeStyle(themeDefinition.getInsensitive());
            textGUIGraphics.fill(' ');
            textGUIGraphics.setCharacter(0, 0, themeDefinition.getCharacter("LEFT_BORDER", '<'));
            textGUIGraphics.setCharacter(textGUIGraphics.getSize().getColumns() - 1, 0, themeDefinition.getCharacter("RIGHT_BORDER", '>'));

            int labelShift = getLabelShift(button, textGUIGraphics.getSize());
            if (button.isFocused()) textGUIGraphics.applyThemeStyle(themeDefinition.getSelected());
            else textGUIGraphics.applyThemeStyle(themeDefinition.getNormal());
            textGUIGraphics.putString(1 + labelShift, 0, button.getLabel());

            if (!button.isFocused()) {
                textGUIGraphics.applyThemeStyle(themeDefinition.getPreLight());
                textGUIGraphics.enableModifiers(SGR.UNDERLINE);
                textGUIGraphics.setCharacter(1 + labelShift + highlighted, 0, button.getLabel().charAt(highlighted));
            }
        }
    }
}

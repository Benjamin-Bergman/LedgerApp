// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight.components;

import com.googlecode.lanterna.TextColor.*;
import com.googlecode.lanterna.gui2.*;

/**
 * Represents a {@link TextBox} which can be visually marked as invalid.
 */
public class ErrorTextBox extends TextBox {
    private boolean isGood;

    /**
     * Creates a new {@code ErrorTextBox}. It starts off good.
     */
    public ErrorTextBox() {
        isGood = true;
        setRenderer(new ErrorTextBoxRenderer());
    }

    /**
     * @return Whether this text box is marked as invalid.
     */
    public boolean isBad() {
        return !isGood;
    }

    /**
     * Marks this text box as valid or invalid.
     *
     * @param isBad {@code true} to mark this text box as invalid.
     */
    public void setBad(boolean isBad) {
        isGood = !isBad;
        invalidate();
    }

    private class ErrorTextBoxRenderer extends DefaultTextBoxRenderer {
        @SuppressWarnings({"ReassignedVariable", "MethodWithMultipleLoops"})
        @Override
        public void drawComponent(TextGUIGraphics graphics, TextBox component) {
            super.drawComponent(graphics, component);

            if (isGood)
                return;

            for (int row = 0; row < graphics.getSize().getRows(); row++)
                for (int column = 0; column < graphics.getSize().getColumns(); column++)
                    graphics.setCharacter(
                        column,
                        row,
                        graphics.getCharacter(column, row)
                            .withBackgroundColor(ANSI.RED_BRIGHT)
                            .withForegroundColor(ANSI.BLACK));
        }
    }
}

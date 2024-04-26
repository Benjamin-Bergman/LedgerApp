// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.TextColor.*;
import com.googlecode.lanterna.gui2.*;

final class ErrorTextBox extends TextBox {
    private boolean isGood = true;

    ErrorTextBox() {
        isGood = true;
        setRenderer(new ErrorTextBoxRenderer());
    }

    boolean isBad() {
        return !isGood;
    }

    void setBad(boolean isBad) {
        isGood = !isBad;
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

// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Window.*;
import com.googlecode.lanterna.input.*;

import java.util.*;

class IntPicker extends AbstractInteractableComponent<IntPicker> {
    private final int defaultValue;
    private int maxValue, minValue, selectedValue, weakMax;
    private boolean isListFocused, isTyping;
    private String format;
    private Window popup;

    IntPicker(int defaultValue, int minValue, int maxValue) {
        this.defaultValue = defaultValue;
        selectedValue = defaultValue;
        isListFocused = false;
        isTyping = false;
        //noinspection AssignmentToNull
        popup = null;
        this.minValue = minValue;
        setMaxValue(maxValue);
    }

    public final void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        //noinspection NumericCastThatLosesPrecision
        int digits = (int) Math.log10(maxValue) + 1;
        //noinspection StringConcatenationMissingWhitespace
        format = "%0" + digits + 'd';
        //noinspection NumericCastThatLosesPrecision
        weakMax = (int) Math.pow(10, digits);
        invalidate();
    }

    public final void setMinValue(int minValue) {
        this.minValue = minValue;
        invalidate();
    }

    public boolean hasChanged() {
        return defaultValue != getSelectedValue();
    }

    public int getSelectedValue() {
        return Math.min(Math.max(selectedValue, minValue), maxValue);
    }

    private int successor(int i) {
        return (i == maxValue) ? minValue : (i + 1);
    }

    private int predecessor(int i) {
        return (i == minValue) ? maxValue : (i - 1);
    }

    private void setSelection(int value) {
        selectedValue = value;
        invalidate();
    }

    private void defocusList() {
        isListFocused = false;
        if (popup != null)
            popup.close();
        //noinspection AssignmentToNull
        popup = null;
    }

    private void focusList() {
        isListFocused = true;

        popup = new BasicWindow() {
            @Override
            public boolean handleInput(KeyStroke key) {
                return handleKeyStroke(key) == Result.HANDLED;
            }
        };
        popup.setComponent(new IntPickerPopup());
        popup.setHints(List.of(Hint.NO_DECORATIONS, Hint.FIXED_POSITION, Hint.NO_FOCUS, Hint.MENU_POPUP, Hint.NO_POST_RENDERING));
        popup.setPosition(toGlobal(new TerminalPosition(0, -1)));
        ((WindowBasedTextGUI) getTextGUI()).addWindow(popup);
        ((WindowBasedTextGUI) getTextGUI()).setActiveWindow(popup);
    }

    @Override
    protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        if (popup != null)
            popup.close();

        setSelection(getSelectedValue());
        isTyping = false;

        super.afterLeaveFocus(direction, nextInFocus);
    }

    @Override
    protected InteractableRenderer<IntPicker> createDefaultRenderer() {
        return new IntPickerRenderer();
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    @Override
    protected Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            var c = keyStroke.getCharacter();
            int i = "0123456789".indexOf(c);
            if (i != -1) {
                setSelection(((selectedValue * 10) + i) % weakMax);
                isTyping = true;
                return Result.HANDLED;
            }
        }
        if (keyStroke.getKeyType() == KeyType.Backspace) {
            setSelection(selectedValue / 10);
            isTyping = true;
            return Result.HANDLED;
        }
        if (!isListFocused && isActivationStroke(keyStroke)) {
            focusList();
            return Result.HANDLED;
        }
        if (isListFocused) {
            if (isActivationStroke(keyStroke)) {
                defocusList();
                return Result.HANDLED;
            }
            //noinspection SwitchStatementWithoutDefaultBranch,EnumSwitchStatementWhichMissesCases
            switch (keyStroke.getKeyType()) {
                case Backspace -> {
                }
                case ArrowUp -> {
                    setSelection(successor(getSelectedValue()));
                    return Result.HANDLED;
                }
                case ArrowDown -> {
                    setSelection(predecessor(getSelectedValue()));
                    return Result.HANDLED;
                }
                case Escape, Delete -> {
                    defocusList();
                    return Result.HANDLED;
                }
                case Tab, ReverseTab, ArrowLeft, ArrowRight -> defocusList();
            }
        }

        return super.handleKeyStroke(keyStroke);
    }

    private class IntPickerRenderer implements InteractableRenderer<IntPicker> {
        @Override
        public TerminalPosition getCursorLocation(IntPicker intPicker) {
            return TerminalPosition.TOP_LEFT_CORNER;
        }

        @Override
        public TerminalSize getPreferredSize(IntPicker intPicker) {
            return new TerminalSize(4, 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics textGUIGraphics, IntPicker intPicker) {
            var definition = getTheme().getDefinition(IntPicker.class);

            textGUIGraphics.applyThemeStyle(isFocused() ? definition.getPreLight() : definition.getNormal());

            textGUIGraphics.putString((getSize().getColumns() - 4) / 2, 0, format.formatted(isTyping ? selectedValue : getSelectedValue()));
        }
    }

    private class IntPickerPopup extends AbstractComponent<IntPickerPopup> {
        @Override
        protected ComponentRenderer<IntPickerPopup> createDefaultRenderer() {
            return new IntPickerPopupRenderer();
        }

        @SuppressWarnings("InnerClassTooDeeplyNested")
        private class IntPickerPopupRenderer implements ComponentRenderer<IntPickerPopup> {
            @Override
            public TerminalSize getPreferredSize(IntPickerPopup intPickerPopup) {
                return new TerminalSize(4, 3);
            }

            @Override
            public void drawComponent(TextGUIGraphics textGUIGraphics, IntPickerPopup intPickerPopup) {
                var definition = getTheme().getDefinition(IntPickerPopup.class);
                textGUIGraphics.applyThemeStyle(definition.getInsensitive());
                textGUIGraphics.putString(0, 0, format.formatted(successor(getSelectedValue())));
                textGUIGraphics.putString(0, 2, format.formatted(predecessor(getSelectedValue())));
                textGUIGraphics.applyThemeStyle(definition.getActive());
                textGUIGraphics.putString(0, 1, format.formatted(isTyping ? selectedValue : getSelectedValue()));
            }
        }
    }
}

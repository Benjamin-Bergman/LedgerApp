// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight.components;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Window.*;
import com.googlecode.lanterna.input.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Represents a picker that lets users select an integer from within a specified range.
 * The range is inclusive on both ends.
 */
public class IntPicker extends AbstractInteractableComponent<IntPicker> {
    private final int defaultValue;
    private final List<IntConsumer> onUpdateSubscribers;
    private int maxValue, minValue, selectedValue, weakMax, maxDigits;
    private boolean isListFocused;
    private String format;
    private Window popup;

    /**
     * @param defaultValue The initial value to select.
     * @param minValue     The minimum value to allow (inclusive).
     * @param maxValue     The maximum value to allow (inclusive).
     */
    public IntPicker(int defaultValue, int minValue, int maxValue) {
        this.defaultValue = defaultValue;
        selectedValue = defaultValue;
        isListFocused = false;
        //noinspection AssignmentToNull
        popup = null;
        onUpdateSubscribers = new CopyOnWriteArrayList<>();
        this.minValue = minValue;
        setMaxValue(maxValue);
    }

    /**
     * Subscribes to changes in this picker's value.
     *
     * @param consumer A callback to be run when the value changes.
     */
    public final void onUpdate(IntConsumer consumer) {
        onUpdateSubscribers.add(consumer);
    }

    /**
     * Sets the maximum value for this picker.
     *
     * @param maxValue The inclusive upper bound.
     */
    public final void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        //noinspection NumericCastThatLosesPrecision
        maxDigits = (int) Math.log10(maxValue) + 1;
        //noinspection StringConcatenationMissingWhitespace
        format = "%0" + maxDigits + 'd';
        //noinspection NumericCastThatLosesPrecision
        weakMax = (int) Math.pow(10, maxDigits);
        invalidate();
    }

    /**
     * Sets the minimum value for this picker.
     *
     * @param minValue The inclusive lower bound.
     */
    public final void setMinValue(int minValue) {
        this.minValue = minValue;
        invalidate();
    }

    /**
     * @return {@code true} if the currently selected int is its default value.
     */
    public boolean isDefault() {
        return defaultValue != getSelectedValue();
    }

    /**
     * @return The currently selected value.
     */
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
        var computed = getSelectedValue();
        onUpdateSubscribers.forEach(consumer -> consumer.accept(computed));
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

        super.afterLeaveFocus(direction, nextInFocus);
    }

    @Override
    protected InteractableRenderer<IntPicker> createDefaultRenderer() {
        return new IntPickerRenderer();
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    protected Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            var c = keyStroke.getCharacter();
            int i = "0123456789".indexOf(c);
            if (i != -1) {
                setSelection(((selectedValue * 10) + i) % weakMax);
                return Result.HANDLED;
            }
        }
        if (keyStroke.getKeyType() == KeyType.Backspace) {
            setSelection(selectedValue / 10);
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
            return new TerminalSize(maxDigits, 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics textGUIGraphics, IntPicker intPicker) {
            var definition = getTheme().getDefinition(IntPicker.class);

            textGUIGraphics.applyThemeStyle(isFocused() ? definition.getPreLight() : definition.getNormal());

            textGUIGraphics.putString((getSize().getColumns() - maxDigits) / 2, 0, format.formatted(isFocused() ? selectedValue : getSelectedValue()));
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
                return new TerminalSize(maxDigits, 3);
            }

            @Override
            public void drawComponent(TextGUIGraphics textGUIGraphics, IntPickerPopup intPickerPopup) {
                var definition = getTheme().getDefinition(IntPickerPopup.class);
                textGUIGraphics.applyThemeStyle(definition.getInsensitive());
                textGUIGraphics.putString(0, 0, format.formatted(successor(getSelectedValue())));
                textGUIGraphics.putString(0, 2, format.formatted(predecessor(getSelectedValue())));
                textGUIGraphics.applyThemeStyle(definition.getActive());
                textGUIGraphics.putString(0, 1, format.formatted(selectedValue));
            }
        }
    }
}

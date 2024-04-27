// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight.components;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Window.*;
import com.googlecode.lanterna.input.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Represents a picker that lets users select a month.
 */
public final class MonthPicker extends AbstractInteractableComponent<MonthPicker> {
    private final Month defaultValue;
    private final List<Consumer<Month>> onUpdateSubscribers;
    private final List<Consumer<Boolean>> onRolloverSubscribers;
    private Month selectedValue;
    private boolean isListFocused;
    private Window popup;
    private String typed;

    /**
     * @param defaultValue The initial month to select.
     */
    public MonthPicker(Month defaultValue) {
        this.defaultValue = defaultValue;
        selectedValue = defaultValue;
        isListFocused = false;
        //noinspection AssignmentToNull
        popup = null;
        typed = "";
        onUpdateSubscribers = new CopyOnWriteArrayList<>();
        onRolloverSubscribers = new CopyOnWriteArrayList<>();
    }

    private static Month successor(Month month) {
        return month.plus(1);
    }

    private static Month predecessor(Month month) {
        return month.minus(1);
    }

    private static Optional<Month> tryParse(String s) {
        try {
            return Optional.of(Month.of(Integer.parseInt(s)));
        } catch (NumberFormatException | DateTimeException e) {
            return Optional.empty();
        }
    }

    /**
     * Subscribes to when this picker "rolls over" from December to January or vice versa.
     * The callback will be called with {@code true} when the rollover is going up, or {@code false} otherwise.
     *
     * @param consumer A callback to be run when the value rolls over.
     */
    public void onRollover(Consumer<Boolean> consumer) {
        onRolloverSubscribers.add(consumer);
    }

    /**
     * Increases this picker's value by one month.
     */
    public void increment() {
        setSelection(successor(getSelectedValue()));
        if (selectedValue == Month.JANUARY)
            onRolloverSubscribers.forEach(consumer -> consumer.accept(true));
    }

    /**
     * Decreases this picker's value by one month.
     */
    public void decrement() {
        setSelection(predecessor(getSelectedValue()));
        if (selectedValue == Month.DECEMBER)
            onRolloverSubscribers.forEach(consumer -> consumer.accept(false));
    }

    /**
     * Subscribes to changes in this picker's value.
     *
     * @param consumer A callback to be run when the value changes.
     */
    public void onUpdate(Consumer<Month> consumer) {
        onUpdateSubscribers.add(consumer);
    }

    /**
     * @return {@code true} if the currently selected int is its default value.
     */
    public boolean isDefault() {
        return defaultValue == getSelectedValue();
    }

    /**
     * @return The currently selected value.
     */
    public Month getSelectedValue() {
        if (typed.isEmpty())
            return selectedValue;
        return Arrays
            .stream(Month.values())
            .filter(mo -> mo.name().toLowerCase().startsWith(typed.toLowerCase()))
            .findFirst()
            .orElseGet(() -> tryParse(typed).orElseGet(() -> selectedValue));
    }

    private void setSelection(Month value) {
        selectedValue = value;
        typed = "";
        onUpdateSubscribers.forEach(consumer -> consumer.accept(selectedValue));
        invalidate();
    }

    private void defocusList() {
        isListFocused = false;
        if (popup != null)
            popup.close();
        //noinspection AssignmentToNull
        popup = null;
    }

    private void updateTyped() {
        if (typed.length() > 3)
            typed = typed.substring(typed.length() - 3);
        var computed = getSelectedValue();
        onUpdateSubscribers.forEach(consumer -> consumer.accept(computed));
        invalidate();
    }

    private void focusList() {
        isListFocused = true;

        popup = new BasicWindow() {
            @Override
            public boolean handleInput(KeyStroke key) {
                return handleKeyStroke(key) == Result.HANDLED;
            }
        };
        popup.setComponent(new MonthPickerPopup());
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
    protected InteractableRenderer<MonthPicker> createDefaultRenderer() {
        return new MonthPickerRenderer();
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    protected Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Character) {
            typed += keyStroke.getCharacter();
            updateTyped();
            return Result.HANDLED;
        }
        if ((keyStroke.getKeyType() == KeyType.Backspace) && !typed.isEmpty()) {
            typed = typed.substring(0, typed.length() - 1);
            updateTyped();
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
                    increment();
                    return Result.HANDLED;
                }
                case ArrowDown -> {
                    decrement();
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

    private String getInputValue() {
        return typed.isEmpty() ? selectedValue.getDisplayName(TextStyle.SHORT, Locale.getDefault()) : "%3s".formatted(typed);
    }

    private class MonthPickerRenderer implements InteractableRenderer<MonthPicker> {
        @Override
        public TerminalPosition getCursorLocation(MonthPicker monthPicker) {
            return TerminalPosition.TOP_LEFT_CORNER;
        }

        @Override
        public TerminalSize getPreferredSize(MonthPicker monthPicker) {
            return new TerminalSize(3, 1);
        }

        @Override
        public void drawComponent(TextGUIGraphics textGUIGraphics, MonthPicker monthPicker) {
            var definition = getTheme().getDefinition(IntPicker.class);

            textGUIGraphics.applyThemeStyle(isFocused() ? definition.getPreLight() : definition.getNormal());

            textGUIGraphics.putString((getSize().getColumns() - 3) / 2, 0, isFocused() ? getInputValue() : getSelectedValue().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        }
    }

    private class MonthPickerPopup extends AbstractComponent<MonthPickerPopup> {
        @Override
        protected ComponentRenderer<MonthPickerPopup> createDefaultRenderer() {
            return new MonthPickerPopupRenderer();
        }

        @SuppressWarnings("InnerClassTooDeeplyNested")
        private class MonthPickerPopupRenderer implements ComponentRenderer<MonthPickerPopup> {
            @Override
            public TerminalSize getPreferredSize(MonthPickerPopup monthPickerPopup) {
                return new TerminalSize(3, 3);
            }

            @Override
            public void drawComponent(TextGUIGraphics textGUIGraphics, MonthPickerPopup monthPickerPopup) {
                var definition = getTheme().getDefinition(MonthPickerPopup.class);
                textGUIGraphics.applyThemeStyle(definition.getInsensitive());
                textGUIGraphics.putString(0, 0, successor(getSelectedValue()).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                textGUIGraphics.putString(0, 2, predecessor(getSelectedValue()).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
                textGUIGraphics.applyThemeStyle(definition.getActive());
                textGUIGraphics.putString(0, 1, getInputValue());
            }
        }
    }
}

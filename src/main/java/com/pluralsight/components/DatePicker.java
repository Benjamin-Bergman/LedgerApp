// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight.components;

import com.googlecode.lanterna.gui2.*;

import java.time.*;
import java.util.function.*;

/**
 * Represents a date picker.
 */
@SuppressWarnings("FeatureEnvy")
public final class DatePicker extends Panel {
    private final IntPicker dayPicker, yearPicker;
    private final MonthPicker monthPicker;
    private Consumer<LocalDate> listener;

    /**
     * Creates a new instance whose default value is {@code LocalDate.now()}.
     */
    public DatePicker() {
        this(LocalDate.now());
    }

    /**
     * Creates a new instance with the given default date.
     *
     * @param defaultDate The initial value of this picker.
     */
    public DatePicker(LocalDate defaultDate) {
        super(new LinearLayout(Direction.HORIZONTAL));

        listener = null;

        monthPicker = new MonthPicker(defaultDate.getMonth());
        dayPicker = new IntPicker(defaultDate.getDayOfMonth(), 1, defaultDate.lengthOfMonth());
        yearPicker = new IntPicker(defaultDate.getYear(), 0, 9_999);
        addComponent(monthPicker);
        addComponent(dayPicker);
        addComponent(yearPicker);

        dayPicker.onRollover(x -> {
            if (x) monthPicker.increment();
            else {
                monthPicker.decrement();
                dayPicker.setSelection(LocalDate.of(yearPicker.getSelectedValue(), monthPicker.getSelectedValue(), 1).lengthOfMonth());
            }
        });
        monthPicker.onRollover(x -> {
            if (x) yearPicker.increment();
            else yearPicker.decrement();
        });

        monthPicker.onUpdate(m -> dayPicker.setMaxValue(LocalDate.of(yearPicker.getSelectedValue(), m, 1).lengthOfMonth()));

        yearPicker.onUpdate(y -> dayPicker.setMaxValue(LocalDate.of(y, monthPicker.getSelectedValue(), 1).lengthOfMonth()));

        dayPicker.onUpdate(x -> update());
        monthPicker.onUpdate(x -> update());
        yearPicker.onUpdate(x -> update());
    }

    /**
     * @return {@code true} if the currently selected date is the default.
     */
    public boolean isDefault() {
        return monthPicker.isDefault() && dayPicker.isDefault() && yearPicker.isDefault();
    }

    /**
     * @return The current value selected by the picker.
     */
    public LocalDate dateValue() {
        return LocalDate.of(yearPicker.getSelectedValue(), monthPicker.getSelectedValue(), dayPicker.getSelectedValue());
    }

    /**
     * Sets the currently selected date.
     *
     * @param date The date to set to.
     */
    public void setDate(LocalDate date) {
        monthPicker.setSelection(date.getMonth());
        dayPicker.setSelection(date.getDayOfMonth());
        yearPicker.setSelection(date.getYear());
    }

    /**
     * Set a listener for when the selected date changes.
     *
     * @param listener The listener to set.
     */
    public void setChangeListener(Consumer<LocalDate> listener) {
        this.listener = listener;
    }

    private void update() {
        if (listener != null) listener.accept(dateValue());
    }
}

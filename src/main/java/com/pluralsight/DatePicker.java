// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;

import java.time.*;

/**
 * Represents a date picker.
 */
@SuppressWarnings("FeatureEnvy")
public final class DatePicker extends Panel {
    private final IntPicker monthPicker, dayPicker, yearPicker;

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
        monthPicker = new IntPicker(defaultDate.getMonthValue(), 1, 12);
        dayPicker = new IntPicker(defaultDate.getDayOfMonth(), 1, defaultDate.lengthOfMonth());
        yearPicker = new IntPicker(defaultDate.getYear(), 0, 9_999);
        addComponent(monthPicker);
        addComponent(dayPicker);
        addComponent(yearPicker);

        monthPicker.onUpdate(m -> dayPicker.setMaxValue(LocalDate.of(yearPicker.getSelectedValue(), m, 1).lengthOfMonth()));

        yearPicker.onUpdate(y -> dayPicker.setMaxValue(LocalDate.of(y, monthPicker.getSelectedValue(), 1).lengthOfMonth()));
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
}

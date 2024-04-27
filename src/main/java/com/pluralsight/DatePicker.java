// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;

import java.time.*;

public final class DatePicker extends Panel {
    private final IntPicker monthPicker, dayPicker, yearPicker;

    public DatePicker() {
        super(new LinearLayout(Direction.HORIZONTAL));
        var today = LocalDate.now();
        monthPicker = new IntPicker(today.getMonthValue(), 1, 12);
        dayPicker = new IntPicker(today.getDayOfMonth(), 1, today.lengthOfMonth());
        yearPicker = new IntPicker(today.getYear(), 0, 9999);
        addComponent(monthPicker);
        addComponent(dayPicker);
        addComponent(yearPicker);

        monthPicker.onUpdate(m -> dayPicker.setMaxValue(LocalDate.of(yearPicker.getSelectedValue(), m, 1).lengthOfMonth()));

        yearPicker.onUpdate(y -> dayPicker.setMaxValue(LocalDate.of(y, monthPicker.getSelectedValue(), 1).lengthOfMonth()));
    }

    public boolean isDefault() {
        return monthPicker.isDefault() && dayPicker.isDefault() && yearPicker.isDefault();
    }

    public LocalDate dateValue() {
        return LocalDate.of(yearPicker.getSelectedValue(), monthPicker.getSelectedValue(), dayPicker.getSelectedValue());
    }
}

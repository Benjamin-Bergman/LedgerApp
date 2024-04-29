// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight.components;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;

/**
 * Represents a picker which allows a user to enter a monetary value.
 */
public final class MoneyPicker extends ErrorTextBox {
    private static final Pattern MONEY_PATTERN = Pattern.compile("^\\$?(?!\\.$)[0-9]*(?:\\.[0-9]{0,2})?$");
    private static final Pattern ZERO_PATTERN = Pattern.compile("^\\$?0*\\.?0*$");
    private Consumer<? super OptionalDouble> listener;

    /**
     * Creates a new picker.
     */
    public MoneyPicker() {
        listener = null;
        setBad(true);
        setTextChangeListener((text, user) -> {
            var money = MONEY_PATTERN.matcher(text);
            var zero = ZERO_PATTERN.matcher(text);
            setBad(!money.matches() || zero.matches());
            if (listener != null) listener.accept(moneyValue());
        });
    }

    /**
     * @return The amount of money entered, or {@code OptionalDouble.empty()} if the entered amount is invalid.
     */
    public OptionalDouble moneyValue() {
        if (isBad())
            return OptionalDouble.empty();
        return OptionalDouble.of(Double.parseDouble(
            getText().replace('$', ' ')
        ));
    }

    /**
     * Set a callback to run when the money value changes.
     *
     * @param listener The callback to run.
     */
    public void setChangeListener(Consumer<? super OptionalDouble> listener) {
        this.listener = listener;
    }
}

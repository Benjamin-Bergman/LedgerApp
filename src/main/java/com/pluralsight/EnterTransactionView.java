// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;

import java.util.*;
import java.util.regex.*;

/**
 * Represents the view for entering a row into a financial ledger.
 */
final class EnterTransactionView extends BasicWindow {
    private static final Pattern MONEY_PATTERN = Pattern.compile("^\\$?(?!\\.$)([0-9]*(?:\\.[0-9]{0,2})?)$");
    private static final Pattern ZERO_PATTERN = Pattern.compile("^\\$?0*\\.0*$");

    EnterTransactionView(boolean credit) {
        super(credit ? "Enter a Credit" : "Enter a Debit");

        setHints(List.of(Hint.MODAL, Hint.CENTERED));
        setCloseWindowWithEscape(true);

        Panel panel = new Panel(new GridLayout(2));
        new Label("Amount").addTo(panel);
        var amountInput = new ErrorTextBox();
        panel.addComponent(amountInput);
        new Label("Item").addTo(panel);
        var itemInput = new TextBox().addTo(panel);
        new Label("Vendor").addTo(panel);
        var vendorInput = new TextBox().addTo(panel);

        amountInput.setTextChangeListener((text, user) -> {
            var money = MONEY_PATTERN.matcher(text);
            var zero = ZERO_PATTERN.matcher(text);
            amountInput.setBad(!money.matches() || zero.matches());
        });

        setComponent(panel);
    }
}

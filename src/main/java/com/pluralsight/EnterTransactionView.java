// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;

import java.util.*;

/**
 * Represents the view for entering a row into a financial ledger.
 */
final class EnterTransactionView extends BasicWindow {
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
            amountInput.setBad(true);
        });

        setComponent(panel);
    }
}

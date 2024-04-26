// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;

import java.time.*;
import java.util.*;
import java.util.regex.*;

/**
 * Represents the view for entering a row into a financial ledger.
 */
final class EnterTransactionView extends BasicWindow {
    private static final Pattern MONEY_PATTERN = Pattern.compile("^\\$?(?!\\.$)([0-9]*(?:\\.[0-9]{0,2})?)$");
    private static final Pattern ZERO_PATTERN = Pattern.compile("^\\$?0*\\.0*$");

    EnterTransactionView(boolean credit, TransactionDatabase db) {
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

        // Date input
        // Time input

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Button("Submit", () -> trySubmit(amountInput, itemInput, vendorInput, credit, db)));

        setComponent(panel);
    }

    private void trySubmit(ErrorTextBox amountInput, TextBox itemInput, TextBox vendorInput, boolean credit, TransactionDatabase db) {
        if (amountInput.isBad()) new MessageDialogBuilder()
            .setTitle("Error")
            .setText("Invalid money amount!")
            .addButton(MessageDialogButton.OK)
            .build()
            .showDialog(getTextGUI());
        else if (new MessageDialogBuilder()
                     .setTitle("Confirm")
                     .setText("Are you sure?")
                     .addButton(MessageDialogButton.Yes)
                     .addButton(MessageDialogButton.Cancel)
                     .build()
                     .showDialog(getTextGUI()) == MessageDialogButton.Yes) {
            db.addTransaction(new Transaction(
                LocalDateTime.now(),
                itemInput.getText(),
                vendorInput.getText(),
                (credit ? 1 : -1) * Double.parseDouble(amountInput.getText())));
            close();
        }
    }
}

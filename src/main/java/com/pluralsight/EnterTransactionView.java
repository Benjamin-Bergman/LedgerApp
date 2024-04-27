// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.*;

import java.time.*;
import java.util.*;
import java.util.regex.*;

/**
 * Represents the view for entering a row into a financial ledger.
 */
final class EnterTransactionView extends BasicWindow {
    private static final Pattern MONEY_PATTERN = Pattern.compile("^\\$?(?!\\.$)[0-9]*(?:\\.[0-9]{0,2})?$");
    private static final Pattern ZERO_PATTERN = Pattern.compile("^\\$?0*\\.?0*$");

    private final ErrorTextBox amountInput;
    private final TextBox itemInput, vendorInput;
    private final DatePicker dateInput;

    EnterTransactionView(boolean credit, TransactionDatabase db) {
        super(credit ? "Enter a Credit" : "Enter a Debit");

        setHints(List.of(Hint.MODAL, Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));
        new Label("Amount").addTo(panel);
        amountInput = new ErrorTextBox();
        amountInput.setBad(true);
        panel.addComponent(amountInput);
        new Label("Item").addTo(panel);
        itemInput = new TextBox().addTo(panel);
        new Label("Vendor").addTo(panel);
        vendorInput = new TextBox().addTo(panel);

        new Label("Date").addTo(panel);
        dateInput = new DatePicker();
        panel.addComponent(dateInput);

        amountInput.setTextChangeListener((text, user) -> {
            var money = MONEY_PATTERN.matcher(text);
            var zero = ZERO_PATTERN.matcher(text);
            amountInput.setBad(!money.matches() || zero.matches());
        });

        // Date input
        // Time input

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Button("Submit", () -> trySubmit(credit, db)));

        setComponent(panel);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape)
            tryClose();

        return super.handleInput(key);
    }

    private void tryClose() {
        if (amountInput.getText().isEmpty()
            && itemInput.getText().isEmpty()
            && vendorInput.getText().isEmpty()
            && dateInput.isDefault()) {
            close();
            return;
        }

        if (new MessageDialogBuilder()
                .setTitle("Cancel")
                .setText("Are you sure you want to exit?")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .build()
                .showDialog(getTextGUI()) == MessageDialogButton.Yes)
            close();
    }

    private void trySubmit(boolean credit, TransactionDatabase db) {
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
                LocalDateTime.of(dateInput.dateValue(), LocalTime.now()),
                itemInput.getText(),
                vendorInput.getText(),
                (credit ? 1 : -1) * Double.parseDouble(
                    amountInput.getText().replace('$', ' ')
                ))
            );
            close();
        }
    }
}

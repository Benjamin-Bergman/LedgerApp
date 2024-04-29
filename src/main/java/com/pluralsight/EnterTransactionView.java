// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.*;
import com.pluralsight.components.*;

import java.time.*;
import java.util.*;

/**
 * Represents the view for entering a row into a financial ledger.
 */
final class EnterTransactionView extends BasicWindow {
    private final MoneyPicker amountInput;
    private final ErrorTextBox itemInput, vendorInput;
    private final DatePicker dateInput;
    private final TimePicker timeInput;

    EnterTransactionView(boolean credit, TransactionDatabase db) {
        super(credit ? "Enter a Credit" : "Enter a Debit");

        setHints(List.of(Hint.MODAL, Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));
        new Label("Amount").addTo(panel);
        amountInput = new MoneyPicker();
        panel.addComponent(amountInput);
        new Label("Item").addTo(panel);
        itemInput = new ErrorTextBox();
        itemInput.addTo(panel);
        itemInput.setBad(true);
        new Label("Vendor").addTo(panel);
        vendorInput = new ErrorTextBox();
        vendorInput.addTo(panel);
        vendorInput.setBad(true);

        new Label("Date").addTo(panel);
        dateInput = new DatePicker();
        panel.addComponent(dateInput);

        new Label("Time").addTo(panel);
        timeInput = new TimePicker();
        panel.addComponent(timeInput);

        itemInput.setTextChangeListener((text, user) -> {
            itemInput.setBad(text.isEmpty());
        });
        vendorInput.setTextChangeListener((text, user) -> {
            vendorInput.setBad(text.isEmpty());
        });

        panel.addComponent(new Button("Exit", this::tryClose));
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
            && dateInput.isDefault()
            && timeInput.isDefault()) {
            close();
            return;
        }

        if (new MessageDialogBuilder()
                .setTitle("Confirm")
                .setText("Are you sure you want to exit?")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .build()
                .showDialog(getTextGUI()) == MessageDialogButton.Yes)
            close();
    }

    private void trySubmit(boolean credit, TransactionDatabase db) {
        var amtBad = amountInput.isBad();
        var dscBad = itemInput.isBad();
        var vndBad = vendorInput.isBad();

        StringBuilder errors = new StringBuilder();
        if (amtBad)
            errors.append("Invalid money amount").append(System.lineSeparator());
        if (dscBad)
            errors.append("Invalid item").append(System.lineSeparator());
        if (vndBad)
            errors.append("Invalid vendor").append(System.lineSeparator());

        if (!errors.isEmpty()) new MessageDialogBuilder()
            .setTitle("Error")
            .setText(errors.toString().trim())
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
            //noinspection OptionalGetWithoutIsPresent
            db.addTransaction(new Transaction(
                LocalDateTime.of(dateInput.dateValue(), timeInput.timeValue()),
                itemInput.getText(),
                vendorInput.getText(),
                (credit ? 1 : -1) * amountInput.moneyValue().getAsDouble())
            );
            close();
        }
    }
}

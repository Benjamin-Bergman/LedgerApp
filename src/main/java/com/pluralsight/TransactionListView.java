// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.*;
import com.googlecode.lanterna.gui2.LinearLayout.*;
import com.googlecode.lanterna.input.*;
import com.pluralsight.components.*;

import java.util.*;

final class TransactionListView extends BasicWindow {
    private final TransactionDatabase database;
    private final LabeledButton[] buttons;

    TransactionListView(TransactionDatabase database) {
        super("Transactions");

        this.database = database;

        var layout = new Panel(new LinearLayout(Direction.HORIZONTAL));
        setComponent(layout);

        var list = new TransactionList();
        layout.addComponent(list);
        for (var t : database)
            list.addItem(t);

        var controls = new Panel(new LinearLayout(Direction.VERTICAL));
        layout.addComponent(controls);

        // Sorting modes here

        var justify = LinearLayout.createLayoutData(Alignment.Fill, GrowPolicy.CanGrow);

        controls.addComponent(new Separator(Direction.HORIZONTAL), justify);
        controls.addComponent(new Label("Reports:"));

        buttons = new LabeledButton[]{
            new LabeledButton("Month To Date", 'M', () -> showReport(ReportType.MONTH_TO_DATE)),
            new LabeledButton("Prior Month", 'P', () -> showReport(ReportType.PRIOR_MONTH)),
            new LabeledButton("Year To Date", 'Y', () -> showReport(ReportType.YEAR_TO_DATE)),
            new LabeledButton("Prior Year", 'r', () -> showReport(ReportType.PRIOR_YEAR)),
            new LabeledButton("Exit", 'x', this::tryClose)
        };

        for (var button : buttons)
            controls.addComponent(button, justify);

        setHints(List.of(Hint.CENTERED));
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) {
            tryClose();
            return false;
        }
        for (LabeledButton button : buttons)
            if (button.handleKeyStroke(key) == Result.HANDLED)
                return false;

        return super.handleInput(key);
    }

    private void tryClose() {
        close();
    }

    private void showReport(ReportType type) {

    }

    private enum ReportType {
        MONTH_TO_DATE,
        PRIOR_MONTH,
        YEAR_TO_DATE,
        PRIOR_YEAR
    }

    private static final class TransactionList extends AbstractListBox<Transaction, TransactionList> {
        @Override
        protected ListItemRenderer<Transaction, TransactionList> createDefaultListItemRenderer() {
            return new TransactionRenderer();
        }

        @SuppressWarnings("InnerClassTooDeeplyNested")
        private static final class TransactionRenderer extends ListItemRenderer<Transaction, TransactionList> {
            @Override
            public String getLabel(TransactionList listBox, int index, Transaction item) {
                return super.getLabel(listBox, index, item);
            }
        }
    }
}

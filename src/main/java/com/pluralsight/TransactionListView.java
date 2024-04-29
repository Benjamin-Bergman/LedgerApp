// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Interactable.*;
import com.googlecode.lanterna.gui2.LinearLayout.*;
import com.googlecode.lanterna.input.*;
import com.pluralsight.ReportView.*;
import com.pluralsight.components.*;

import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * Represents a view of a list of transactions.
 */
final class TransactionListView extends BasicWindow {
    private final TransactionDatabase database;
    private final LabeledButton[] buttons;
    private final TransactionList transactions;
    private FilterOptions filter;

    TransactionListView(TransactionDatabase database) {
        super("Transactions");

        filter = new FilterOptions(null, null, null, null, null, null);

        this.database = database;

        var layout = new Panel(new LinearLayout(Direction.HORIZONTAL));
        setComponent(layout);

        transactions = new TransactionList();
        layout.addComponent(transactions);
        generateList();

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
        getTextGUI().addWindowAndWait(new ReportView(type, database, op -> {
            filter = op.override(filter);
            generateList();
        }));
    }

    private void generateList() {
        transactions.clearItems();

        for (var t : database)
            if (filter.test(t))
                transactions.addItem(t);
    }

    /**
     * Represents filtering options for a list of transaction.
     * Any filter can be {@code null} to not use it.
     * Note that date filtering is half-open, while amount filtering is closed.
     *
     * @param after       The transaction must have occurred on or after this date.
     * @param before      The transaction must have occurred strictly before this date.
     * @param description The transaction's description must contain this text.
     * @param vendor      The transaction's vendor must contain this text.
     * @param minAmount   The transaction must be for at least this much money.
     * @param maxAmount   The transaction must be for at most this much money.
     */
    @SuppressWarnings("PackageVisibleInnerClass")
    record FilterOptions(LocalDate after, LocalDate before, String description, String vendor, Double minAmount,
                         Double maxAmount) implements Predicate<Transaction> {
        @SuppressWarnings({"OverlyComplexMethod", "FeatureEnvy"})
        @Override
        public boolean test(Transaction t) {
            return
                ((after == null) || !t.date().isBefore(after))
                && ((before == null) || t.date().isBefore(before))
                && ((description == null) || t.description().toLowerCase().contains(description.toLowerCase()))
                && ((vendor == null) || t.vendor().toLowerCase().contains(vendor.toLowerCase()))
                && ((minAmount == null) || (t.amount() >= minAmount))
                && ((maxAmount == null) || (t.amount() <= maxAmount));
        }

        FilterOptions override(FilterOptions other) {
            return new FilterOptions(
                Optional.ofNullable(after).orElseGet(other::after),
                Optional.ofNullable(before).orElseGet(other::before),
                Optional.ofNullable(description).orElseGet(other::description),
                Optional.ofNullable(vendor).orElseGet(other::vendor),
                Optional.ofNullable(minAmount).orElseGet(other::minAmount),
                Optional.ofNullable(maxAmount).orElseGet(other::maxAmount));
        }
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

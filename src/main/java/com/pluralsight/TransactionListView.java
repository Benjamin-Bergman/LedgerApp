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
    private final Label liveReports;
    private final SettingsPanel settings;
    private FilterOptions filter;

    TransactionListView(TransactionDatabase database) {
        super("Transactions");

        filter = new FilterOptions(null, null, null, null, null, null);

        this.database = database;

        var layout = new Panel(new LinearLayout(Direction.HORIZONTAL));
        setComponent(layout);

        var dataColumn = new Panel(new LinearLayout(Direction.VERTICAL));
        layout.addComponent(dataColumn);

        liveReports = new Label("");
        dataColumn.addComponent(liveReports);
        transactions = new TransactionList();
        dataColumn.addComponent(transactions);
        generateList();

        var controls = new Panel(new LinearLayout(Direction.VERTICAL));
        layout.addComponent(controls);

        var justify = LinearLayout.createLayoutData(Alignment.Fill, GrowPolicy.CanGrow);

        settings = new SettingsPanel();
        controls.addComponent(settings, justify);

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
            filter = op;
            settings.redraw();
            generateList();
        }));
    }

    @SuppressWarnings("ReassignedVariable")
    private void generateList() {
        transactions.clearItems();

        int total = 0;
        int visible = 0;
        double totalAmount = 0;

        for (var t : database) {
            total++;
            if (filter.test(t)) {
                visible++;
                totalAmount += t.amount();
                transactions.addItem(t);
            }
        }

        //noinspection HardcodedFileSeparator
        liveReports.setText("Showing %d/%d transactions totalling $%.2f".formatted(visible, total, totalAmount));
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
    @SuppressWarnings({"PackageVisibleInnerClass", "ParameterHidesMemberVariable"})
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
                && ((minAmount == null) || (Math.abs(t.amount()) >= minAmount))
                && ((maxAmount == null) || (Math.abs(t.amount()) <= maxAmount));
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

        FilterOptions withAfter(LocalDate after) {
            return new FilterOptions(after, before, description, vendor, minAmount, maxAmount);
        }

        FilterOptions withBefore(LocalDate before) {
            return new FilterOptions(after, before, description, vendor, minAmount, maxAmount);
        }

        FilterOptions withDescription(String description) {
            return new FilterOptions(after, before, description, vendor, minAmount, maxAmount);
        }

        FilterOptions withVendor(String vendor) {
            return new FilterOptions(after, before, description, vendor, minAmount, maxAmount);
        }

        FilterOptions withMin(Double minAmount) {
            return new FilterOptions(after, before, description, vendor, minAmount, maxAmount);
        }

        FilterOptions withMax(Double maxAmount) {
            return new FilterOptions(after, before, description, vendor, minAmount, maxAmount);
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

    @SuppressWarnings("FeatureEnvy")
    private final class SettingsPanel extends Panel {
        private final DatePicker before, after;
        private final CheckBox beforeEnabled, afterEnabled;
        private final TextBox description, vendor;
        private final MoneyPicker minValue, maxValue;

        @SuppressWarnings("NestedAssignment")
        SettingsPanel() {
            super(new GridLayout(2));

            var justify = LinearLayout.createLayoutData(Alignment.Fill, GrowPolicy.CanGrow);

            addComponent(new Label("Before"));
            var beforeRow = new Panel(new LinearLayout(Direction.HORIZONTAL));
            beforeRow.addComponent(beforeEnabled = new CheckBox());
            beforeRow.addComponent(before = new DatePicker());
            addComponent(beforeRow);

            addComponent(new Label("After"));
            var afterRow = new Panel(new LinearLayout(Direction.HORIZONTAL));
            afterRow.addComponent(afterEnabled = new CheckBox());
            afterRow.addComponent(after = new DatePicker());
            addComponent(afterRow);

            addComponent(new Label("Item"));
            addComponent(description = new TextBox(), justify);

            addComponent(new Label("Vendor"));
            addComponent(vendor = new TextBox(), justify);

            addComponent(new Label("Min $"));
            addComponent(minValue = new MoneyPicker(), justify);

            addComponent(new Label("Max $"));
            addComponent(maxValue = new MoneyPicker(), justify);

            beforeEnabled.addListener(enabled -> {
                filter = filter.withBefore(enabled ? before.dateValue() : null);
                generateList();
            });
            afterEnabled.addListener(enabled -> {
                filter = filter.withAfter(enabled ? after.dateValue() : null);
                generateList();
            });
            // before, after
            description.setTextChangeListener((text, auto) -> {
                filter = filter.withDescription(text.isEmpty() ? "" : text);
                generateList();
            });
            vendor.setTextChangeListener((text, auto) -> {
                filter = filter.withVendor(text.isEmpty() ? "" : text);
                generateList();
            });
            minValue.setChangeListener(val -> {
                val.ifPresentOrElse(v -> filter = filter.withMin(v),
                    () -> filter = filter.withMin(null));
                generateList();
            });
            maxValue.setChangeListener(val -> {
                val.ifPresentOrElse(v -> filter = filter.withMax(v),
                    () -> filter = filter.withMax(null));
                generateList();
            });

            redraw();
        }

        private void redraw() {
            if (filter.before() == null)
                beforeEnabled.setChecked(false);
            else {
                beforeEnabled.setChecked(true);
                before.setDate(filter.before());
            }

            if (filter.after() == null)
                afterEnabled.setChecked(false);
            else {
                afterEnabled.setChecked(true);
                after.setDate(filter.after());
            }

            description.setText((filter.description() == null) ? "" : filter.description());
            vendor.setText((filter.vendor() == null) ? "" : filter.vendor());

            minValue.setText((filter.minAmount() == null) ? "" : "$%.2f".formatted(filter.minAmount()));
            maxValue.setText((filter.maxAmount() == null) ? "" : "$%.2f".formatted(filter.maxAmount()));
        }
    }
}

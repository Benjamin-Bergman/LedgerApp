// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.*;
import com.pluralsight.TransactionListView.*;
import com.pluralsight.components.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class ReportView extends DialogWindow {
    ReportView(ReportType reportType, Iterable<Transaction> db, Consumer<? super FilterOptions> onShow) {
        super(reportType.getReportName());

        var result = StreamSupport
            .stream(db.spliterator(), true)
            .filter(reportType)
            .reduce(
                new Tuple(0, 0),
                (tup, tra) -> new Tuple(tup.count() + 1, tup.total() + tra.amount()),
                (t1, t2) -> new Tuple(t1.count() + t2.count(), t1.total() + t2.total()));

        var display = new Panel();
        display.addComponent(new Label("%d transactions totalling $%.2f".formatted(result.count(), result.total())));

        var buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        display.addComponent(buttons);
        buttons.addComponent(new LabeledButton("Exit", 'x', this::close));
        buttons.addComponent(new LabeledButton("Show", 'S', () -> {
            onShow.accept(reportType.getFilter());
            close();
        }));

        setComponent(display);

        setHints(List.of(Hint.CENTERED, Hint.MODAL));
        setCloseWindowWithEscape(true);
    }

    @Override
    public boolean handleInput(KeyStroke key) {
        if ((key.getKeyType() == KeyType.Character)
            && ((key.getCharacter() == 'x') || (key.getCharacter() == 'X'))) {
            close();
            return false;
        }

        return super.handleInput(key);
    }

    /**
     * The type of some report
     */
    enum ReportType implements Predicate<Transaction> {
        MONTH_TO_DATE("Month To Date", new FilterOptions(LocalDate.now().withDayOfMonth(1), null, null, null, null, null)),
        PRIOR_MONTH("Prior Month", new FilterOptions(LocalDate.now().minusMonths(1).withDayOfMonth(1), LocalDate.now().withDayOfMonth(1), null, null, null, null)),
        YEAR_TO_DATE("Year To Date", new FilterOptions(LocalDate.now().withMonth(1).withDayOfMonth(1), null, null, null, null, null)),
        PRIOR_YEAR("Prior Year", new FilterOptions(LocalDate.now().minusYears(1).withMonth(1).withDayOfMonth(1), LocalDate.now().withMonth(1).withDayOfMonth(1), null, null, null, null));

        private final String reportName;
        private final FilterOptions filter;

        ReportType(String reportName, FilterOptions filter) {
            this.reportName = reportName;
            this.filter = filter;
        }

        @Override
        public boolean test(Transaction transaction) {
            return filter.test(transaction);
        }

        String getReportName() {
            return reportName;
        }

        FilterOptions getFilter() {
            return filter;
        }
    }

    private record Tuple(int count, double total) {
    }
}
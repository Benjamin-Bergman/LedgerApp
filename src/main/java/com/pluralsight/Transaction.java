// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight;

import java.time.*;

/**
 * Represents a financial transaction.
 *
 * @param dateTime    When this transaction occurred.
 * @param description What this transaction was for.
 * @param vendor      Who this transaction was with.
 * @param amount      How much money this transaction exchanged.
 */
public record Transaction(LocalDateTime dateTime, String description, String vendor, Double amount) {
    @Override
    public String toString() {
        return "[%tY-%<tm-%<td %<tH:%<tM:%<TS] %.2f %s %s for %s".formatted(
            dateTime,
            Math.abs(amount),
            (amount > 0) ? "from" : "to",
            vendor,
            description);
    }

    /**
     * @return The date when this transaction occurred.
     */
    public LocalDate date() {
        return dateTime.toLocalDate();
    }

    /**
     * @return The time when this transaction occurred.
     */
    public LocalTime time() {
        return dateTime.toLocalTime();
    }
}

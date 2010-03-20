package org.mifos.calendar;

import org.joda.time.DateTime;

public class SameDayStrategy implements DateAdjustmentStrategy {

    private final DateTime originalScheduledDate;

    public SameDayStrategy(final DateTime originalScheduledDate) {
        this.originalScheduledDate = originalScheduledDate;
    }

    @Override
    public DateTime adjust(@SuppressWarnings("unused") final DateTime adjustTo) {
        return originalScheduledDate;
    }
}

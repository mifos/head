package org.mifos.calendar;

import java.util.List;

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
    
    public List<DateTime> adjust (List<DateTime> dates) {
        //TODO keithp. Implement this to default to adjust just the first date
        return null;
    }

}

package org.mifos.calendar;

import org.joda.time.DateTime;

public class SameDayStrategy implements DateAdjustmentStrategy {

    @Override
    public DateTime adjust(DateTime sameDay) {
        return sameDay;
    }

}

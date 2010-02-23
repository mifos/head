package org.mifos.schedule.internal;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.calendar.DateAdjustmentStrategy;
import org.mifos.schedule.ScheduledEvent;

public class BasicMoratoriumStrategy implements DateAdjustmentStrategy {

    private final List<Holiday> upcomingMoratoria;
    private final List<Days> workingDays;
    private final ScheduledEvent scheduledEvent;

    public BasicMoratoriumStrategy(final List<Holiday> upcomingMoratoria, final List<Days> workingDays,
            final ScheduledEvent scheduledEvent) {
        this.upcomingMoratoria = upcomingMoratoria;
        this.workingDays = workingDays;
        this.scheduledEvent = scheduledEvent;
    }

    @Override
    public DateTime adjust(final DateTime startingFrom) {

        DateTime adjustedDate = startingFrom;

        for (Holiday holiday : this.upcomingMoratoria) {
            if (holiday.encloses(adjustedDate.toDate())) {
                adjustedDate = holiday.adjust(adjustedDate, this.workingDays, this.scheduledEvent);
            }
        }

        return adjustedDate;
    }

    @Override
    public List<DateTime> adjust(final List<DateTime> dates) {
        // TODO Auto-generated method stub
        return null;
    }

}

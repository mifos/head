package org.mifos.clientportfolio.newloan.domain;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.internal.DailyScheduledEvent;
import org.mifos.schedule.internal.MonthlyOnDateScheduledEvent;
import org.mifos.schedule.internal.MonthlyOnWeekAndWeekDayScheduledEvent;
import org.mifos.schedule.internal.WeeklyScheduledEvent;

public class RecurringScheduledEventFactoryImpl implements RecurringScheduledEventFactory {

	@Override
	public ScheduledEvent createScheduledEventFrom(MeetingBO meeting) {
		
        RecurrenceType period = meeting.getRecurrenceType();
        int every = meeting.getRecurAfter();
        int dayOfWeek = 0;
        if (meeting.getMeetingDetails().getWeekDay() != null) {
            dayOfWeek = WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(meeting.getMeetingDetails().getWeekDay().getValue());
        }

        int dayOfMonth = 0;
        if (meeting.getMeetingDetails().getDayNumber() != null) {
            dayOfMonth = meeting.getMeetingDetails().getDayNumber();
        }
        int weekOfMonth = 0;
        if (meeting.getMeetingDetails().getWeekRank() != null) {
            weekOfMonth = meeting.getMeetingDetails().getWeekRank().getValue();
        }
        
        return createScheduledEvent(period, every, dayOfWeek, dayOfMonth, weekOfMonth);
	}
	
    private ScheduledEvent createScheduledEvent(final RecurrenceType period, final int every,
            final int dayOfWeek, final int dayOfMonth, final int weekOfMonth) {

        ScheduledEvent recurringEvent;
        switch (period) {
        case WEEKLY:
            recurringEvent = new WeeklyScheduledEvent(every, dayOfWeek);
            break;
        case MONTHLY:
            if (weekOfMonth != 0 && dayOfWeek != 0) {
                recurringEvent = new MonthlyOnWeekAndWeekDayScheduledEvent(every, weekOfMonth, dayOfWeek);
            }
            else if (dayOfMonth != 0) {
                recurringEvent = new MonthlyOnDateScheduledEvent(every, dayOfMonth);
            } else {
                throw new IllegalStateException("not enough information to create a monthly scheduled event");
            }
            break;
        case DAILY:
            recurringEvent = new DailyScheduledEvent(every);
            break;
        default:
            throw new IllegalStateException("unknown recurring period type");
        }

        return recurringEvent;
    }
}
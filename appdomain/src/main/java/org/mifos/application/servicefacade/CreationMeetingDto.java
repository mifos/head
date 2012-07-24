package org.mifos.application.servicefacade;

import org.joda.time.LocalDate;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;

public class CreationMeetingDto {
    private LocalDate meetingStartDate;
    private String meetingPlace;
    private Short recurrenceType;
    private Short dayNumber;
    private Short weekDay;
    private Short rankOfDay;
    private Short recurAfter;

    public LocalDate getMeetingStartDate() {
        return meetingStartDate;
    }

    public void setMeetingStartDate(LocalDate meetingStartDate) {
        this.meetingStartDate = meetingStartDate;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public Short getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(Short recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Short getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Short dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Short getRecurAfter() {
        return recurAfter;
    }

    public void setRecurAfter(Short recurAfter) {
        this.recurAfter = recurAfter;
    }
    
    public WeekDay getWeekDay() {
        if (null != weekDay) {
            return WeekDay.getJodaWeekDay(weekDay);
        }
        else {
            return WeekDay.MONDAY;
        }
    }

    public RankOfDay getRankOfDay() {
        if (null != rankOfDay) {
            return RankOfDay.getRankOfDay(rankOfDay);
        }
        else {
            return RankOfDay.FIRST;
        }
    }

    public MeetingBO toBO() throws MeetingException {
        MeetingBO meeting = null;
        if (recurrenceType.equals(RecurrenceType.WEEKLY.getValue())) {
            meeting = new MeetingBO(getDayNumber(), getRecurAfter(), getMeetingStartDate().toDateMidnight().toDate(),
                    MeetingType.CUSTOMER_MEETING, getMeetingPlace());
        } else {
            meeting = new MeetingBO(getWeekDay(), getRankOfDay(), getRecurAfter(),
                    getMeetingStartDate().toDateMidnight().toDate(), MeetingType.CUSTOMER_MEETING, getMeetingPlace());
        }
        return meeting;
    }
}
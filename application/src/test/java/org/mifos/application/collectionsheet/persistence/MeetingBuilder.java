/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.MeetingRecurrenceEntity;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;

/**
 *
 */
public class MeetingBuilder {

    private MeetingType meetingType = MeetingType.CUSTOMER_MEETING;
    private RecurrenceType recurrenceType = RecurrenceType.WEEKLY;
    private Short recurAfter = Short.valueOf("1");
    private WeekDay weekDay = WeekDay.getJodaWeekDay(new DateTime().getDayOfWeek());
    private RankType weekRank = null;
    private Short dayNumber = null;
    private Date startDate = new DateTime().toDate();
    private final String meetingLocation = "test-meeting-location";
    private MasterPersistence masterPersistence = null;
    
    public MeetingBuilder() {        
    }
    
    public MeetingBuilder(MasterPersistence masterPersistence) {
        this.masterPersistence = masterPersistence;
    }
    
    public MeetingBO build() {

        final MeetingBO meeting = new MeetingBO(meetingType, startDate, meetingLocation);
        final MeetingDetailsEntity meetingDetailsEntity = new MeetingDetailsEntity(meeting, recurrenceType, recurAfter);
        final MeetingRecurrenceEntity meetingRecurrenceEntity = new MeetingRecurrenceEntity(weekDay,
                meetingDetailsEntity);
        meetingRecurrenceEntity.setDayNumber(dayNumber);

        if (weekRank != null) {
            RankOfDaysEntity rankOfDays = new RankOfDaysEntity(weekRank);
            meetingRecurrenceEntity.setRankOfDays(rankOfDays);
        }

        meetingDetailsEntity.setMeetingRecurrence(meetingRecurrenceEntity);

        meeting.setMeetingDetails(meetingDetailsEntity);

        return meeting;
    }

    public MeetingBO buildMonthlyFor(final RankType rank, final WeekDay weekDay) throws MeetingException {
        return new MeetingBO(weekDay, rank, recurAfter, startDate, meetingType, meetingLocation, masterPersistence);
    }

    public MeetingBO buildMonthlyForDayNumber(final int dayNumber) throws MeetingException {
        return new MeetingBO((short) dayNumber, recurAfter, startDate, meetingType, meetingLocation);
    }

    public MeetingBuilder daily() {
        this.recurrenceType = RecurrenceType.DAILY;
        return this;
    }

    public MeetingBuilder weekly() {
        this.recurrenceType = RecurrenceType.WEEKLY;
        return this;
    }

    public MeetingBuilder monthly() {
        this.recurrenceType = RecurrenceType.MONTHLY;
        return this;
    }

    public MeetingBuilder every(final Integer recurrence) {
        this.recurAfter = recurrence.shortValue();
        return this;
    }

    public MeetingBuilder onWeek(final RankType withWeek) {
        this.weekRank = withWeek;
        return this;
    }

    public MeetingBuilder onDayOfMonth(final int withDayOfMonth) {
        this.dayNumber = (short) withDayOfMonth;
        return this;
    }

    public MeetingBuilder occuringOnA(final WeekDay meetingDay) {
        this.weekDay = meetingDay;
        return this;
    }

    public MeetingBuilder customerMeeting() {
        this.meetingType = MeetingType.CUSTOMER_MEETING;
        return this;
    }

    public MeetingBuilder periodicFeeMeeting() {
        this.meetingType = MeetingType.PERIODIC_FEE;
        return this;
    }

    public MeetingBuilder savingsInterestCalulationSchedule() {
        this.meetingType = MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
        return this;
    }

    public MeetingBuilder savingsInterestPostingSchedule() {
        this.meetingType = MeetingType.SAVINGS_INTEREST_POSTING;
        return this;
    }

    public MeetingBuilder startingToday() {
        this.startDate = new DateTime().toDate();
        this.weekDay = WeekDay.getJodaWeekDay(new DateTime().getDayOfWeek());
        return this;
    }

    public MeetingBuilder withStartDate(final DateTime withStartDate) {
        this.startDate = withStartDate.toDate();
        this.weekDay = WeekDay.getJodaWeekDay(withStartDate.getDayOfWeek());
        return this;
    }

    public MeetingBuilder withSameRecurrenceAs(final MeetingBO meeting) {
        recurrenceType = meeting.getRecurrenceType();
        recurAfter = meeting.getRecurAfter();
        return this;
    }
}

/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.application.meeting.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.MeetingTemplate;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.schedule.ScheduledDateGeneration;

/**
 * A better name for MeetingBO would be along the lines of "ScheduledEvent". To
 * see what a "meeting" can be look at {@link MeetingType}. It encompasses not
 * only a customer meeting, but also financial events like loan installments,
 * interest posting and the like. This should be refactored, perhaps from a
 * ScheduledEvent base class with subclasses that correspond to the different
 * MeetingType entries. In this way a member like meetingPlace could be
 * associated with the CustomerMeeting rather than all MeetingTypes.
 */
public class MeetingBO extends BusinessObject {

    private final Integer meetingId;

    private MeetingDetailsEntity meetingDetails;

    // TODO: make it final while migrating create meeting
    private MeetingTypeEntity meetingType;

    private Date meetingStartDate;

    private String meetingPlace;

    /*
     * TODO: This looks like it should be a local variable in each of the places
     * which uses it. I don't see it being used outside a single method.
     */
    private final GregorianCalendar gc = new DateTimeService().getCurrentDateTime().toGregorianCalendar();

    private FiscalCalendarRules fiscalCalendarRules = null;

    private MasterPersistence masterPersistence = null;

    public FiscalCalendarRules getFiscalCalendarRules() {
        if (fiscalCalendarRules == null) {
            fiscalCalendarRules = new FiscalCalendarRules();
        }
        return this.fiscalCalendarRules;
    }

    public void setFiscalCalendarRules(FiscalCalendarRules fiscalCalendarRules) {
        this.fiscalCalendarRules = fiscalCalendarRules;
    }

    public MasterPersistence getMasterPersistence() {
        if (masterPersistence == null) {
            masterPersistence = new MasterPersistence();
        }
        return this.masterPersistence;
    }

    public void setMasterPersistence(MasterPersistence masterPersistence) {
        this.masterPersistence = masterPersistence;
    }

    /**
     * default constructor for hibernate
     */
    protected MeetingBO() {
        this.meetingId = null;
        this.meetingDetails = null;
        this.meetingType = null;
        this.meetingStartDate = null;
    }

    /**
     * minimal legal constructor
     */
    public MeetingBO(final MeetingType meetingType, final Date startDate, final String meetingLocation) {
        this.meetingId = null;
        this.meetingDetails = null;
        this.meetingType = new MeetingTypeEntity(meetingType);
        this.meetingStartDate = startDate;
        this.meetingPlace = meetingLocation;
    }

    public MeetingBO(final RecurrenceType recurrenceType, final Short recurAfter, final Date startDate, final MeetingType meetingType)
            throws MeetingException {
        this(recurrenceType, Short.valueOf("1"), WeekDay.MONDAY, null, recurAfter, startDate, meetingType,
                "meetingPlace");
    }

    public MeetingBO(final WeekDay weekDay, final RankType rank, final Short recurAfter, final Date startDate, final MeetingType meetingType,
            final String meetingPlace) throws MeetingException {
        this(weekDay, rank, recurAfter, startDate, meetingType, meetingPlace, new MasterPersistence());
    }

    public MeetingBO(final WeekDay weekDay, final RankType rank, final Short recurAfter, final Date startDate, final MeetingType meetingType,
            final String meetingPlace, final MasterPersistence masterPersistence) throws MeetingException {
        this(RecurrenceType.MONTHLY, null, weekDay, rank, recurAfter, startDate, meetingType, meetingPlace, masterPersistence);
    }

    public MeetingBO(final Short dayNumber, final Short recurAfter, final Date startDate, final MeetingType meetingType, final String meetingPlace)
            throws MeetingException {
        this(RecurrenceType.MONTHLY, dayNumber, null, null, recurAfter, startDate, meetingType, meetingPlace);
    }

    public MeetingBO(final WeekDay weekDay, final Short recurAfter, final Date startDate, final MeetingType meetingType, final String meetingPlace)
            throws MeetingException {
        this(RecurrenceType.WEEKLY, null, weekDay, null, recurAfter, startDate, meetingType, meetingPlace);
    }

    public MeetingBO(final MeetingTemplate template) throws MeetingException {
        this(template.getReccurenceType(), template.getDateNumber(), template.getWeekDay(), template.getRankType(),
                template.getRecurAfter(), template.getStartDate(), template.getMeetingType(), template
                        .getMeetingPlace());
    }

    private MeetingBO(final RecurrenceType recurrenceType, final Short dayNumber, final WeekDay weekDay, final RankType rank, final Short recurAfter,
            final Date startDate, final MeetingType meetingType, final String meetingPlace) throws MeetingException {
        this(recurrenceType, dayNumber, weekDay, rank, recurAfter, startDate, meetingType, meetingPlace, new MasterPersistence());
    }
    private MeetingBO(final RecurrenceType recurrenceType, final Short dayNumber, final WeekDay weekDay, final RankType rank, final Short recurAfter,
            final Date startDate, final MeetingType meetingType, final String meetingPlace, final MasterPersistence masterPersistence) throws MeetingException {
        setMasterPersistence(masterPersistence);
        this.validateFields(recurrenceType, startDate, meetingType, meetingPlace);
        this.meetingDetails = new MeetingDetailsEntity(new RecurrenceTypeEntity(recurrenceType), dayNumber, weekDay,
                rank, recurAfter, this);
        // TODO: remove this check after meeting create is migrated.
        if (meetingType != null) {
            this.meetingType = new MeetingTypeEntity(meetingType);
        }
        this.meetingId = null;
        this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(startDate.getTime());
        this.meetingPlace = meetingPlace;
    }

    public MeetingBO(final Short dayNumber, final Short recurAfter, final Date startDate, final MeetingType meetingType, final String meetingPlace,
            final Short weekNumber) throws MeetingException {

        this(RecurrenceType.MONTHLY, null, WeekDay.getWeekDay(dayNumber), RankType.getRankType(weekNumber), recurAfter,
                startDate, meetingType, meetingPlace);

    }

    public MeetingBO(final int recurrenceId, final Short dayNumber, final Short recurAfter, final Date startDate, final MeetingType meetingType,
            final String meetingPlace) throws MeetingException {

        this(RecurrenceType.WEEKLY, null, WeekDay.getWeekDay(dayNumber), null, recurAfter, startDate, meetingType,
                meetingPlace);

    }

    public MeetingDetailsEntity getMeetingDetails() {
        return meetingDetails;
    }

    public Integer getMeetingId() {
        return meetingId;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(final String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public Date getMeetingStartDate() {
        return meetingStartDate;
    }

    public void setMeetingStartDate(final Date meetingStartDate) {
        this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(meetingStartDate);
    }

    public void setStartDate(final Date startDate) {
        this.meetingStartDate = startDate;
    }

    public Date getStartDate() {
        return meetingStartDate;
    }

    public MeetingTypeEntity getMeetingType() {
        return meetingType;
    }

    public MeetingType getMeetingTypeEnum() {
        return meetingType.asEnum();
    }

    public void setMeetingType(final MeetingTypeEntity meetingType) {
        this.meetingType = meetingType;
    }

    public boolean isMonthlyOnDate() {
        return getMeetingDetails().isMonthlyOnDate();
    }

    public boolean isWeekly() {
        return getMeetingDetails().isWeekly();
    }

    public boolean isMonthly() {
        return getMeetingDetails().isMonthly();
    }

    public void save() throws MeetingException {
        try {
            new MeetingPersistence().createOrUpdate(this);
        } catch (PersistenceException pe) {
            throw new MeetingException(pe);
        }
    }

    public void update(final WeekDay weekDay, final String meetingPlace) throws MeetingException {
        validateMeetingPlace(meetingPlace);
        getMeetingDetails().getMeetingRecurrence().updateWeekDay(weekDay);
        this.meetingPlace = meetingPlace;
    }

    public void update(final WeekDay weekDay, final RankType rank, final String meetingPlace) throws MeetingException {
        validateMeetingPlace(meetingPlace);
        getMeetingDetails().getMeetingRecurrence().update(weekDay, rank);
        this.meetingPlace = meetingPlace;
    }

    public void update(final Short dayNumber, final String meetingPlace) throws MeetingException {
        validateMeetingPlace(meetingPlace);
        getMeetingDetails().getMeetingRecurrence().updateDayNumber(dayNumber);
        this.meetingPlace = meetingPlace;
    }

    private void validateFields(final RecurrenceType recurrenceType, final Date startDate, final MeetingType meetingType,
            final String meetingPlace) throws MeetingException {
        if (recurrenceType == null) {
            throw new MeetingException(MeetingConstants.INVALID_RECURRENCETYPE);
        }
        if (startDate == null) {
            throw new MeetingException(MeetingConstants.INVALID_STARTDATE);
        }
        if (meetingType == null) {
            throw new MeetingException(MeetingConstants.INVALID_MEETINGTYPE);
        }
        validateMeetingPlace(meetingPlace);
    }

    private void validateMeetingPlace(final String meetingPlace) throws MeetingException {
        if (StringUtils.isBlank(meetingPlace)) {
            throw new MeetingException(MeetingConstants.INVALID_MEETINGPLACE);
        }
    }

    public boolean isValidMeetingDate(final Date meetingDate, final Date endDate) throws MeetingException {
        validateMeetingDate(meetingDate);
        validateEndDate(endDate);
        Date currentScheduleDate = getFirstDate(getStartDate());
        Calendar c = Calendar.getInstance();
        c.setTime(currentScheduleDate);
        if (!HolidayUtils.isWorkingDay(c)) {
            currentScheduleDate = HolidayUtils.getNextWorkingDay(c).getTime();
        }
        Date meetingDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());
        Date endDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(endDate.getTime());
        if (meetingDateWOTimeStamp.compareTo(endDateWOTimeStamp) > 0) {
            return false;
        }

        while (currentScheduleDate.compareTo(meetingDateWOTimeStamp) < 0
                && currentScheduleDate.compareTo(endDateWOTimeStamp) < 0) {
            currentScheduleDate = getNextDate(currentScheduleDate);
            c.setTime(currentScheduleDate);
            if (!HolidayUtils.isWorkingDay(c)) {
                currentScheduleDate = HolidayUtils.getNextWorkingDay(c).getTime();
            }

        }

        boolean isRepaymentIndepOfMeetingEnabled;
        try {
            isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        } catch (PersistenceException e) {
            throw new MeetingException(e);
        }
        if (isRepaymentIndepOfMeetingEnabled) {
            return currentScheduleDate.compareTo(endDateWOTimeStamp) <= 0;
        } else {
            // If repayment date is dependend on meeting date, then they need to
            // match
            return currentScheduleDate.compareTo(endDateWOTimeStamp) <= 0 && currentScheduleDate
                    .compareTo(meetingDateWOTimeStamp) == 0;
        }
    }

    public boolean isValidMeetingDate(final Date meetingDate, final int occurrences) throws MeetingException {
        validateMeetingDate(meetingDate);
        validateOccurences(occurrences);
        Date currentScheduleDate = getFirstDate(getStartDate());
        Date meetingDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());

        for (int currentNumber = 1; currentScheduleDate.compareTo(meetingDateWOTimeStamp) < 0
                && currentNumber < occurrences; currentNumber++) {
            currentScheduleDate = getNextDate(currentScheduleDate);
        }

        boolean isRepaymentIndepOfMeetingEnabled;
        try {
            isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        } catch (PersistenceException e) {
            throw new MeetingException(e);
        }
        if (!isRepaymentIndepOfMeetingEnabled) {
            // If repayment date is dependend on meeting date, then they need to
            // match
            return currentScheduleDate.compareTo(meetingDateWOTimeStamp) == 0;
        }

        return true;
    }

    /**
     * start from the next schedule date after the first valid date falling
     * after start date, and loop till current schedule date is after meeting
     * date
     */
    public Date getNextScheduleDateAfterRecurrence(final Date meetingDate) throws MeetingException {
        Date currentScheduleDate = getNextScheduleDateAfterRecurrenceWithoutAdjustment(meetingDate);
        // return
        // HolidayUtils.adjustDate(HolidayUtils.getCalendar(currentScheduleDate),
        // this).getTime();
        return HolidayUtils.adjustDate(DateUtils.getCalendarDate(currentScheduleDate.getTime()), this).getTime();
        // return currentScheduleDate;
    }

    public Date getNextScheduleDateAfterRecurrenceWithoutAdjustment(final Date afterDate) throws MeetingException {
        validateMeetingDate(afterDate);
        Date from = getFirstDate(getStartDate());
        Date currentScheduleDate = getNextDate(from);
        // Date currentScheduleDate=getNextDate(getStartDate());
        while (currentScheduleDate.compareTo(afterDate) <= 0) {
            currentScheduleDate = getNextDate(currentScheduleDate);
        }
        return currentScheduleDate;
    }

    public Date getPrevScheduleDateAfterRecurrence(final Date meetingDate) throws MeetingException {
        validateMeetingDate(meetingDate);
        Date prevScheduleDate = null;
        /*
         * Current schedule date as next meeting date after start date till this
         * date is after given meeting date or increment current schedule date
         * to next meeting date from current schedule date return the last but
         * one current schedule date as prev schedule date
         */
        Date currentScheduleDate = getNextDate(getStartDate());
        while (currentScheduleDate.compareTo(meetingDate) < 0) {
            prevScheduleDate = currentScheduleDate;
            currentScheduleDate = getNextDate(currentScheduleDate);
        }
        return prevScheduleDate;
    }

    // TODO - keithw - I think this method gets all meeting dates from (meetingStartDate) up to (endDate)
    public List<Date> getAllDates(final Date endDate) throws MeetingException {
        validateEndDate(endDate);
        List meetingDates = new ArrayList();
        for (Date meetingDate = getFirstDate(getStartDate()); meetingDate.compareTo(endDate) <= 0; meetingDate = getNextDate(meetingDate)) {
            meetingDates.add(HolidayUtils.adjustDate(DateUtils.getCalendarDate(meetingDate.getTime()), this).getTime());
        }
        return meetingDates;
    }

    /**
     * @deprecated - please use {@link ScheduledDateGeneration#generateScheduledDates(int, org.joda.time.DateTime, org.mifos.schedule.ScheduledEvent)}
     */
    @Deprecated
    public List<Date> getAllDates(final int occurrences) throws MeetingException {
        return getAllDates(occurrences, true);
    }

    /**
     * @deprecated - please use {@link ScheduledDateGeneration#generateScheduledDates(int, org.joda.time.DateTime, org.mifos.schedule.ScheduledEvent)}
     */
    @Deprecated
    public List<Date> getAllDates(final int occurrences, final boolean adjustForHolidays) throws MeetingException {
        validateOccurences(occurrences);
        List meetingDates = new ArrayList();
        Date meetingDate = getFirstDate(getStartDate());

        for (int dateCount = 0; dateCount < occurrences; dateCount++) {
            if (adjustForHolidays) {
                meetingDates.add(HolidayUtils.adjustDate(DateUtils.getCalendarDate(meetingDate.getTime()), this)
                        .getTime());
            } else {
                meetingDates.add(meetingDate);
            }
            meetingDate = getNextDate(meetingDate);
        }
        return meetingDates;
    }

    public List<Date> getAllDatesWithRepaymentIndepOfMeetingEnabled(final int occurrences, final boolean adjustForHolidays)
            throws MeetingException {
        validateOccurences(occurrences);
        List meetingDates = new ArrayList();
        Date meetingDate = getFirstDateWithRepaymentIndepOfMeetingEnabled(getStartDate());

        for (int dateCount = 0; dateCount < occurrences; dateCount++) {
            if (adjustForHolidays) {
                meetingDates.add(HolidayUtils.adjustDate(DateUtils.getCalendarDate(meetingDate.getTime()), this)
                        .getTime());
            } else {
                meetingDates.add(meetingDate);
            }
            meetingDate = getNextDateWithRepaymentIndepOfMeetingEnabled(meetingDate);
        }
        return meetingDates;
    }

    private void validateMeetingDate(final Date meetingDate) throws MeetingException {
        if (meetingDate == null) {
            throw new MeetingException(MeetingConstants.INVALID_MEETINGDATE);
        }
    }

    private void validateOccurences(final int occurrences) throws MeetingException {
        if (occurrences <= 0) {
            throw new MeetingException(MeetingConstants.INVALID_OCCURENCES);
        }
    }

    private void validateEndDate(final Date endDate) throws MeetingException {
        if (endDate == null || endDate.compareTo(getStartDate()) < 0) {
            throw new MeetingException(MeetingConstants.INVALID_ENDDATE);
        }
    }

    /**
     * @deprecated - please use {@link ScheduledDateGeneration#generateScheduledDates}
     */
    @Deprecated
    public Date getFirstDate(final Date startDate) {
        if (isWeekly()) {
            return getFirstDateForWeek(startDate);
        } else if (isMonthly()) {
            return getFirstDateForMonth(startDate);
        } else {
            return getFirstDateForDay(startDate);
        }
    }

    /**
     * @deprecated - please use {@link ScheduledDateGeneration#generateScheduledDates}
     */
    @Deprecated
    private Date getNextDate(final Date startDate) {
        if (isWeekly()) {
            return getNextDateForWeek(startDate);
        } else if (isMonthly()) {
            return getNextDateForMonth(startDate);
        } else {
            return getNextDateForDay(startDate);
        }
    }

    private Date getFirstDateWithRepaymentIndepOfMeetingEnabled(final Date startDate) {
        if (isWeekly()) {
            return getFirstDateForWeek(startDate);
        } else if (isMonthly()) {
            return getFirstDateForMonthWithRepaymentIndepOfMeetingEnabled(startDate);
        } else {
            return getFirstDateForDay(startDate);
        }
    }

    private Date getNextDateWithRepaymentIndepOfMeetingEnabled(final Date startDate) {
        if (isWeekly()) {
            return getNextDateForWeek(startDate);
        } else if (isMonthly()) {
            return getNextDateForMonthWithRepaymentIndepOfMeetingEnabled(startDate);
        } else {
            return getNextDateForDay(startDate);
        }
    }


    /**
     * @deprecated - Please use {@link CalendarUtils#getNextDateForDay}.
     */
    @Deprecated
    private Date getFirstDateForDay(final Date startDate) {
        return getNextDateForDay(startDate);
    }

    /**
     * @deprecated - Please use {@link CalendarUtils#getNextDateForDay}.
     */
    @Deprecated
    private Date getNextDateForDay(final Date startDate) {
        gc.setTime(startDate);
        gc.add(Calendar.DAY_OF_WEEK, getMeetingDetails().getRecurAfter());
        return gc.getTime();
    }

    /**
     * Set the day of week according to given start day to the require weekday, i.e. so it matches the meeting week day.
     *
     * e.g. - If start date is Monday 9 June 2008 and meeting week day is Tuesday, then roll forward the date to Tuesday
     * 10 June 2008 - or if start date is Sunday 8 June 2008 and meeting week day is Saturday, then roll forward the
     * date to Saturday 14 June 2008 - or if start date is Tuesday 10 2008 June and meeting week day is Monday, then
     * roll forward the date to Monday 16 June 2008 - or if start date is Sunday 8 June 2008 and meeting week day is
     * Sunday, then keep the date as Sunday 8 June 2008 - or if start date is Saturday 7 June 2008 and meeting week day
     * is Sunday, then roll forward the date to Sunday 9 June 2008
     *
     * @deprecated - Please use {@link CalendarUtils#getFirstDateForWeek(Date, int)} instead. Also when generating
     *             schedules please use {@link ScheduledDateGeneration#generateScheduledDates}.
     */
    @Deprecated
    Date getFirstDateForWeek(final Date startDate) {
        final GregorianCalendar firstDateForWeek = new GregorianCalendar();
        firstDateForWeek.setTime(startDate);
        int startDateWeekDay = firstDateForWeek.get(Calendar.DAY_OF_WEEK);
        int meetingWeekDay = getMeetingDetails().getWeekDay().getValue();

        // Calculate amount of days that need adding to roll forward to the
        // meeting day
        int amountOfDaysToAdd = meetingWeekDay - startDateWeekDay;
        if (amountOfDaysToAdd < 0) {
            // amountOfDaysToAdd can result in a negative (e.g.
            // Calendar.SATURDAY (7) is greater than Calendar.SUNDAY (1),
            // if so then will add 7 to roll forward a week
            amountOfDaysToAdd += 7;
        }
        firstDateForWeek.add(Calendar.DAY_OF_WEEK, amountOfDaysToAdd);
        return firstDateForWeek.getTime();
    }

    /**
     * @deprecated - please use {@link ScheduledDateGeneration#generateScheduledDates}
     */
    @Deprecated
    private Date getNextDateForWeek(final Date startDate) {
        gc.setTime(startDate);
        gc.add(Calendar.WEEK_OF_MONTH, getMeetingDetails().getRecurAfter());
        return gc.getTime();
    }

    /**
     * for monthly on date return the next date falling on the same day. If date has passed, pass in the date of next
     * month, adjust to day number if day number exceed total number of days in month.
     *
     * @deprecated - Please use {@link CalendarUtils#getFirstDateForMonthOnDate()} or
     *             {@link CalendarUtils#getFirstDayForMonthUsingWeekRankAndWeekday} instead. Also when generating
     *             schedules please use {@link ScheduledDateGeneration#generateScheduledDates}.
     */
    @Deprecated
    private Date getFirstDateForMonth(final Date startDate) {
        Date scheduleDate = null;
        gc.setTime(startDate);

        if (isMonthlyOnDate()) {
            int dt = gc.get(GregorianCalendar.DATE);
            // if date passed in, is after the date on which schedule has to
            // lie, move to next month
            if (dt > getMeetingDetails().getDayNumber()) {
                gc.add(GregorianCalendar.MONTH, 1);
            }
            // set the date on which schedule has to lie
            int M1 = gc.get(GregorianCalendar.MONTH);
            gc.set(GregorianCalendar.DATE, getMeetingDetails().getDayNumber());
            int M2 = gc.get(GregorianCalendar.MONTH);
            int daynum = getMeetingDetails().getDayNumber();
            while (M1 != M2) {
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                gc.set(GregorianCalendar.DATE, daynum - 1);
                M2 = gc.get(GregorianCalendar.MONTH);
                daynum--;
            }
            scheduleDate = gc.getTime();

        } else {
            // if current weekday is after the weekday on which schedule has to
            // lie, move to next week
            if (gc.get(Calendar.DAY_OF_WEEK) > getMeetingDetails().getWeekDay().getValue()) {
                gc.add(Calendar.WEEK_OF_MONTH, 1);
            }
            // set the weekday on which schedule has to lie
            gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
            // if week rank is First, Second, Third or Fourth, Set the
            // respective week.
            // if current week rank is after the weekrank on which schedule has
            // to lie, move to next month
            if (!getMeetingDetails().getWeekRank().equals(RankType.LAST)) {
                if (gc.get(Calendar.DAY_OF_WEEK_IN_MONTH) > getMeetingDetails().getWeekRank().getValue()) {
                    gc.add(GregorianCalendar.MONTH, 1);
                    gc.set(GregorianCalendar.DATE, 1);
                }
                // set the weekrank on which schedule has to lie
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, getMeetingDetails().getWeekRank().getValue());
                scheduleDate = gc.getTime();
            } else {// scheduleData.getWeekRank()=Last
                int M1 = gc.get(GregorianCalendar.MONTH);
                // assumption: there are 5 weekdays in the month
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
                int M2 = gc.get(GregorianCalendar.MONTH);
                // if assumption fails, it means there exists 4 weekdays in a
                // month, return last weekday date
                // if M1==M2, means there exists 5 weekdays otherwise 4 weekdays
                // in a month
                if (M1 != M2) {
                    gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                    gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 4);
                }
                scheduleDate = gc.getTime();
            }
        }
        return scheduleDate;
    }

    /**
     * for monthly is on date add the number of months after which meeting is to
     * recur, and then adjust the date for day on which meeting is to occur
     *
     * @deprecated - Please use {@link CalendarUtils#getNextDateForMonthOnDate()} or
     *             {@link CalendarUtils#getNextDayForMonthUsingWeekRankAndWeekday} instead.
     */
    @Deprecated
    private Date getNextDateForMonth(final Date startDate) {
        Date scheduleDate = null;
        gc.setTime(startDate);
        if (isMonthlyOnDate()) {
            // move to next month and return date.
            gc.add(GregorianCalendar.MONTH, getMeetingDetails().getRecurAfter());
            int M1 = gc.get(GregorianCalendar.MONTH);
            gc.set(GregorianCalendar.DATE, getMeetingDetails().getDayNumber());
            int M2 = gc.get(GregorianCalendar.MONTH);
            int daynum = getMeetingDetails().getDayNumber();
            while (M1 != M2) {
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                gc.set(GregorianCalendar.DATE, daynum - 1);
                M2 = gc.get(GregorianCalendar.MONTH);
                daynum--;
            }
            scheduleDate = gc.getTime();
        } else {
            if (!getMeetingDetails().getWeekRank().equals(RankType.LAST)) {
                // apply month recurrence
                gc.add(GregorianCalendar.MONTH, getMeetingDetails().getRecurAfter());
                gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, getMeetingDetails().getWeekRank().getValue());
                scheduleDate = gc.getTime();
            } else {// weekCount=-1
                gc.set(GregorianCalendar.DATE, 15);
                gc.add(GregorianCalendar.MONTH, getMeetingDetails().getRecurAfter());
                gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
                int M1 = gc.get(GregorianCalendar.MONTH);
                // assumption: there are 5 weekdays in the month
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
                int M2 = gc.get(GregorianCalendar.MONTH);
                // if assumption fails, it means there exists 4 weekdays in a
                // month, return last weekday date
                // if M1==M2, means there exists 5 weekdays otherwise 4 weekdays
                // in a month
                if (M1 != M2) {
                    gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                    gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 4);
                }
                scheduleDate = gc.getTime();
            }
        }
        return scheduleDate;
    }

    private Date getFirstDateForMonthWithRepaymentIndepOfMeetingEnabled(final Date startDate) {
        Date scheduleDate = null;
        gc.setTime(startDate);

        if (isMonthlyOnDate()) {
            int dt = gc.get(GregorianCalendar.DATE);
            // if date passed in, is after the date on which schedule has to
            // lie, move to next month
            if (dt > getMeetingDetails().getDayNumber()) {
                gc.add(GregorianCalendar.MONTH, 1);
            }
            // set the date on which schedule has to lie
            int M1 = gc.get(GregorianCalendar.MONTH);
            gc.set(GregorianCalendar.DATE, getMeetingDetails().getDayNumber());
            int M2 = gc.get(GregorianCalendar.MONTH);
            int daynum = getMeetingDetails().getDayNumber();
            while (M1 != M2) {
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                gc.set(GregorianCalendar.DATE, daynum - 1);
                M2 = gc.get(GregorianCalendar.MONTH);
                daynum--;
            }
            scheduleDate = gc.getTime();
        } else {
            // if current weekday is after the weekday on which schedule has to
            // lie, move to next week
            if (gc.get(Calendar.DAY_OF_WEEK) > getMeetingDetails().getWeekDay().getValue()) {
                gc.add(Calendar.WEEK_OF_MONTH, 1);
            }
            // set the weekday on which schedule has to lie
            gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
            // if week rank is First, Second, Third or Fourth, Set the
            // respective week.
            // if current week rank is after the weekrank on which schedule has
            // to lie, move to next month
            if (!getMeetingDetails().getWeekRank().equals(RankType.LAST)) {
                if (gc.get(Calendar.DAY_OF_WEEK_IN_MONTH) > getMeetingDetails().getWeekRank().getValue()) {
                    gc.add(GregorianCalendar.MONTH, 1);
                    gc.set(GregorianCalendar.DATE, 1);
                }
                // set the weekrank on which schedule has to lie
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, getMeetingDetails().getWeekRank().getValue());
                scheduleDate = gc.getTime();
            } else {// scheduleData.getWeekRank()=Last
                int M1 = gc.get(GregorianCalendar.MONTH);
                // assumption: there are 5 weekdays in the month
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
                int M2 = gc.get(GregorianCalendar.MONTH);
                // if assumption fails, it means there exists 4 weekdays in a
                // month, return last weekday date
                // if M1==M2, means there exists 5 weekdays otherwise 4 weekdays
                // in a month
                if (M1 != M2) {
                    gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                    gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 4);
                }
                scheduleDate = gc.getTime();
            }
        }
        return scheduleDate;
    }

    private Date getNextDateForMonthWithRepaymentIndepOfMeetingEnabled(final Date startDate) {
        Date scheduleDate = null;
        gc.setTime(startDate);
        if (isMonthlyOnDate()) {
            // move to next month and return date.
            gc.add(GregorianCalendar.MONTH, getMeetingDetails().getRecurAfter());
            int M1 = gc.get(GregorianCalendar.MONTH);
            gc.set(GregorianCalendar.DATE, getMeetingDetails().getDayNumber());
            int M2 = gc.get(GregorianCalendar.MONTH);
            int daynum = getMeetingDetails().getDayNumber();
            while (M1 != M2) {
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                gc.set(GregorianCalendar.DATE, daynum - 1);
                M2 = gc.get(GregorianCalendar.MONTH);
                daynum--;
            }
            scheduleDate = gc.getTime();
        } else {
            if (!getMeetingDetails().getWeekRank().equals(RankType.LAST)) {
                // apply month recurrence
                gc.add(GregorianCalendar.MONTH, getMeetingDetails().getRecurAfter());
                gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, getMeetingDetails().getWeekRank().getValue());
                scheduleDate = gc.getTime();
            } else {// weekCount=-1
                gc.set(GregorianCalendar.DATE, 15);
                gc.add(GregorianCalendar.MONTH, getMeetingDetails().getRecurAfter());
                gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
                int M1 = gc.get(GregorianCalendar.MONTH);
                // assumption: there are 5 weekdays in the month
                gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 5);
                int M2 = gc.get(GregorianCalendar.MONTH);
                // if assumption fails, it means there exists 4 weekdays in a
                // month, return last weekday date
                // if M1==M2, means there exists 5 weekdays otherwise 4 weekdays
                // in a month
                if (M1 != M2) {
                    gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) - 1);
                    gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 4);
                }
                scheduleDate = gc.getTime();
            }
        }
        return scheduleDate;
    }

    /*
     * This seems like it is trying to answer the question of whether meetings
     * for meetingToBeMatched and meetingToBeMatchedWith overlap. For example a
     * weekly meeting occurring every 2 weeks potentially overlaps with a
     * meeting occurring every 4 weeks.
     */
    public static boolean isMeetingMatched(final MeetingBO meetingToBeMatched, final MeetingBO meetingToBeMatchedWith) {
        return meetingToBeMatched != null
                && meetingToBeMatchedWith != null
                && meetingToBeMatched.getMeetingDetails().getRecurrenceType().getRecurrenceId().equals(
                        meetingToBeMatchedWith.getMeetingDetails().getRecurrenceType().getRecurrenceId())
                && isMultiple(meetingToBeMatchedWith.getMeetingDetails().getRecurAfter(), meetingToBeMatched
                        .getMeetingDetails().getRecurAfter());
    }

    private static boolean isMultiple(final Short valueToBeChecked, final Short valueToBeCheckedWith) {
        return valueToBeChecked % valueToBeCheckedWith == 0;
    }

    public void setMeetingDetails(final MeetingDetailsEntity meetingDetails) {
        this.meetingDetails = meetingDetails;
    }

    public RecurrenceType getRecurrenceType() {
        return meetingDetails.getRecurrenceTypeEnum();
    }

    public Short getRecurAfter() {
        return meetingDetails.getRecurAfter();
    }

    /*
     * Get the start date of the "interval" surrounding a given date
     * For example assume March 1 is a Monday and that weeks are defined to start on
     * Monday.  If this is a weekly meeting on a Wednesday then the "interval"
     * for Wednesday March 10 is the week from Monday March 8 to Sunday March 14,
     * and this method would return March 8.
     */
    public LocalDate startDateForMeetingInterval(LocalDate date) {
        LocalDate startOfMeetingInterval = date;
        if (isWeekly()) {
            int weekDay = WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(getFiscalCalendarRules().getStartOfWeekWeekDay().getValue());
            while (startOfMeetingInterval.getDayOfWeek() != weekDay) {
                startOfMeetingInterval = startOfMeetingInterval.minusDays(1);
            }
        } else if (isMonthly()) {
            int dayOfMonth = date.getDayOfMonth();
            startOfMeetingInterval = startOfMeetingInterval.minusDays(dayOfMonth - 1);
        } else {
            // for days we return the same day
            startOfMeetingInterval =  date;
        }
        return startOfMeetingInterval;
    }

    public boolean queryDateIsInMeetingIntervalForFixedDate(LocalDate queryDate, LocalDate fixedDate) {
        LocalDate startOfMeetingInterval = startDateForMeetingInterval(fixedDate);
        LocalDate endOfMeetingInterval;
        if (isWeekly()) {
            endOfMeetingInterval = startOfMeetingInterval.plusWeeks(getRecurAfter());
        } else if (isMonthly()) {
            endOfMeetingInterval = startOfMeetingInterval.plusMonths(getRecurAfter());
        } else {
            // we don't handle meeting intervals in days
            return false;
        }
        return (queryDate.isEqual(startOfMeetingInterval) ||
                queryDate.isAfter(startOfMeetingInterval)) &&
                queryDate.isBefore(endOfMeetingInterval);
    }

}

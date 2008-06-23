/**

 * MeetingBO.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.meeting.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.meeting.MeetingTemplate;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

/**
 * A better name for MeetingBO would be along the lines of "ScheduledEvent".
 * To see what a "meeting" can be look at {@link MeetingType}.  It encompasses
 * not only a customer meeting, but also financial events like loan installments,
 * interest posting and the like.  This should be refactored, perhaps from a 
 * ScheduledEvent base class with subclasses that correspond to the different
 * MeetingType entries.  In this way a member like meetingPlace could be 
 * associated with the CustomerMeeting rather than all MeetingTypes.
 */
public class MeetingBO extends BusinessObject {
	
	private final Integer meetingId;

	private final MeetingDetailsEntity meetingDetails;

	//TODO: make it final while migrating create meeting
	private MeetingTypeEntity meetingType;

	private Date meetingStartDate;

	private String meetingPlace;
	
	/* TODO: This looks like it should be a local variable in
	   each of the places which uses it.  I don't see it being
	   used outside a single method. */
	private GregorianCalendar gc = new GregorianCalendar();
	
	public MeetingBO(RecurrenceType recurrenceType, Short recurAfter, 
			Date startDate, MeetingType meetingType)
	throws MeetingException {
		this(recurrenceType, Short.valueOf("1"), WeekDay.MONDAY, null, 
				recurAfter, startDate, meetingType, "meetingPlace");
	}
	
	public MeetingBO(WeekDay weekDay, RankType rank, Short recurAfter, 
			Date startDate, MeetingType meetingType, String meetingPlace)
	throws MeetingException {
		this(RecurrenceType.MONTHLY, null, weekDay, rank, recurAfter, 
				startDate, meetingType,meetingPlace);
	}
	
	public MeetingBO(Short dayNumber, Short recurAfter, Date startDate, 
			MeetingType meetingType, String meetingPlace)
	throws MeetingException {
		this(RecurrenceType.MONTHLY, dayNumber, null, null, recurAfter, 
				startDate, meetingType, meetingPlace);
	}
	
	public MeetingBO(WeekDay weekDay, Short recurAfter, Date startDate, 
			MeetingType meetingType, String meetingPlace)
	throws MeetingException {
		this(RecurrenceType.WEEKLY, null, weekDay, null, recurAfter, 
				startDate, meetingType, meetingPlace);
	}
	
	protected MeetingBO() {
		this.meetingId = null;
		this.meetingDetails = null;
		this.meetingType = null;
		this.meetingStartDate = null;
	}

    public MeetingBO(MeetingTemplate template) throws MeetingException {
        this(template.getReccurenceType(), template.getDateNumber(), template.getWeekDay(),
                template.getRankType(), template.getRecurAfter(), template.getStartDate(),
                template.getMeetingType(), template.getMeetingPlace());
    }

    private MeetingBO(RecurrenceType recurrenceType, Short dayNumber,
			WeekDay weekDay, RankType rank, Short recurAfter, 
			Date startDate, MeetingType meetingType, String meetingPlace)
	throws MeetingException {
	  	this.validateFields(recurrenceType,startDate,meetingType,meetingPlace);
		this.meetingDetails = new MeetingDetailsEntity(
				new RecurrenceTypeEntity(recurrenceType), dayNumber, 
				weekDay, rank, recurAfter, this);
		//TODO: remove this check after meeting create is migrated.
	  	if(meetingType!=null)
			this.meetingType = new MeetingTypeEntity(meetingType);
		this.meetingId = null;
		this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(startDate.getTime());
		this.meetingPlace = meetingPlace;
	}
	
	public MeetingBO(Short dayNumber, Short recurAfter, Date startDate, 
			MeetingType meetingType, String meetingPlace, Short weekNumber)
	throws MeetingException {
		
		this(RecurrenceType.MONTHLY, null, WeekDay.getWeekDay(dayNumber), RankType.getRankType(weekNumber), recurAfter, 
				startDate, meetingType, meetingPlace);
		
	}
	public MeetingBO(int recurrenceId,Short dayNumber, Short recurAfter, Date startDate, 
			MeetingType meetingType, String meetingPlace)
	throws MeetingException {
		
		this(RecurrenceType.WEEKLY, null, WeekDay.getWeekDay(dayNumber), null, recurAfter, 
				startDate, meetingType, meetingPlace);
		
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

	public void setMeetingPlace(String meetingPlace) {
		this.meetingPlace = meetingPlace;
	}

	public Date getMeetingStartDate() {
		return meetingStartDate;
	}

	public void setMeetingStartDate(Date meetingStartDate) {
		this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(meetingStartDate);
	}

	public void setStartDate(Date startDate) {
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

	public void setMeetingType(MeetingTypeEntity meetingType) {
		this.meetingType = meetingType;
	}

	public boolean isMonthlyOnDate(){
		return getMeetingDetails().isMonthlyOnDate();
	}
	
	public boolean isWeekly(){
		return getMeetingDetails().isWeekly();
	}

	public boolean isMonthly(){
		return getMeetingDetails().isMonthly();
	}
	
	public void save() throws MeetingException{
		try{
			new MeetingPersistence().createOrUpdate(this);
		}catch(PersistenceException pe){
			throw new MeetingException(pe);
		}
	}
	
	public void update(WeekDay weekDay, String meetingPlace)throws MeetingException{
		validateMeetingPlace(meetingPlace);
		getMeetingDetails().getMeetingRecurrence().updateWeekDay(weekDay);
		this.meetingPlace=meetingPlace;
	}
	
	public void update(WeekDay weekDay, RankType rank, String meetingPlace)throws MeetingException{
		validateMeetingPlace(meetingPlace);
		getMeetingDetails().getMeetingRecurrence().update(weekDay, rank);
		this.meetingPlace=meetingPlace;
	}

	public void update(Short dayNumber, String meetingPlace)throws MeetingException{
		validateMeetingPlace(meetingPlace);
		getMeetingDetails().getMeetingRecurrence().updateDayNumber(dayNumber);
		this.meetingPlace=meetingPlace;
	}
	
	private void validateFields(RecurrenceType recurrenceType, Date startDate, MeetingType meetingType, String meetingPlace)throws MeetingException{
		if(recurrenceType == null)
			throw new MeetingException(MeetingConstants.INVALID_RECURRENCETYPE);
		if(startDate == null)
			throw new MeetingException(MeetingConstants.INVALID_STARTDATE);
		if(meetingType == null)
			throw new MeetingException(MeetingConstants.INVALID_MEETINGTYPE);
		validateMeetingPlace(meetingPlace);
	}
	
	private void validateMeetingPlace(String meetingPlace)throws MeetingException{
		if(StringUtils.isNullOrEmpty(meetingPlace))
			throw new MeetingException(MeetingConstants.INVALID_MEETINGPLACE);
	}
	
	public boolean isValidMeetingDate(Date meetingDate, Date endDate)
            throws MeetingException {
		validateMeetingDate(meetingDate);
		validateEndDate(endDate);
		Date currentScheduleDate=getFirstDate(getStartDate());
		Date meetingDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());
		Date endDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(endDate.getTime());
		if(meetingDateWOTimeStamp.compareTo(endDateWOTimeStamp)>0)
			return false;

		while(currentScheduleDate.compareTo(meetingDateWOTimeStamp)<0 && currentScheduleDate.compareTo(endDateWOTimeStamp)<0)
			currentScheduleDate=getNextDate(currentScheduleDate);
		
		boolean isRepaymentIndepOfMeetingEnabled;
		try {
			isRepaymentIndepOfMeetingEnabled = ConfigurationPersistence.isRepaymentIndepOfMeetingEnabled();
		} catch (PersistenceException e) {
			throw new MeetingException(e);
		}
		if (isRepaymentIndepOfMeetingEnabled) {
			return (currentScheduleDate.compareTo(endDateWOTimeStamp)<=0);
		} else {
			// If repayment date is dependend on meeting date, then they need to match
			return (currentScheduleDate.compareTo(endDateWOTimeStamp)<=0 && currentScheduleDate.compareTo(meetingDateWOTimeStamp)==0);
		}		
	}
	
	public boolean isValidMeetingDate(Date meetingDate, int occurrences)
		throws MeetingException {
		validateMeetingDate(meetingDate);
		validateOccurences(occurrences);
		Date currentScheduleDate=getFirstDate(getStartDate());
		Date meetingDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());
		
		for(int currentNumber=1; (currentScheduleDate.compareTo(meetingDateWOTimeStamp)<0) && currentNumber<occurrences; currentNumber++)
			currentScheduleDate=getNextDate(currentScheduleDate);
		
		boolean isRepaymentIndepOfMeetingEnabled;
		try {
			isRepaymentIndepOfMeetingEnabled = ConfigurationPersistence.isRepaymentIndepOfMeetingEnabled();
		} catch (PersistenceException e) {
			throw new MeetingException(e);
		}
		if (!isRepaymentIndepOfMeetingEnabled) {
			// If repayment date is dependend on meeting date, then they need to match
			return (currentScheduleDate.compareTo(meetingDateWOTimeStamp)==0);
		}
		
		return true;
	}
	
	
	/**
	 * start from the next schedule date after the first valid date falling after start date, 
	 * and loop till current schedule date is after meeting date
	 */
	public Date getNextScheduleDateAfterRecurrence(Date meetingDate)
			throws MeetingException {
		Date currentScheduleDate = getNextScheduleDateAfterRecurrenceWithoutAdjustment(meetingDate);
		//return HolidayUtils.adjustDate(HolidayUtils.getCalendar(currentScheduleDate), this).getTime();
		return HolidayUtils.adjustDate(
				DateUtils.getCalendarDate(currentScheduleDate.getTime()), this)
				.getTime();
		//return currentScheduleDate;
	}

	public Date getNextScheduleDateAfterRecurrenceWithoutAdjustment(Date afterDate)throws MeetingException{
		validateMeetingDate(afterDate);
		Date from = getFirstDate(getStartDate());
		Date currentScheduleDate=getNextDate(from);
		//Date currentScheduleDate=getNextDate(getStartDate());
		while(currentScheduleDate.compareTo(afterDate)<=0) {
			currentScheduleDate=getNextDate(currentScheduleDate);
		}
		return currentScheduleDate;
	}
	
	public Date getPrevScheduleDateAfterRecurrence(Date meetingDate)throws MeetingException{
		validateMeetingDate(meetingDate);
		Date prevScheduleDate=null;
		/* Current schedule date as next meeting date after start date till this date is after given meeting date
		 * or increment current schedule date to next meeting date from current schedule date
		 * return the last but one current schedule date as prev schedule date
		 */
		Date currentScheduleDate=getNextDate(getStartDate());		
		while(currentScheduleDate.compareTo(meetingDate)<0){
			prevScheduleDate = currentScheduleDate;
			currentScheduleDate = getNextDate(currentScheduleDate);
		}
		return prevScheduleDate;
	}
	
	
	public List<Date> getAllDates(Date endDate)throws MeetingException{
		validateEndDate(endDate);
		List meetingDates= new ArrayList();
		for (Date meetingDate = getFirstDate(getStartDate()); meetingDate
				.compareTo(endDate) <= 0; meetingDate = getNextDate(meetingDate)) {
			meetingDates.add(HolidayUtils.adjustDate(DateUtils.getCalendarDate(meetingDate.getTime()), this).getTime());
		}
		return meetingDates;
	}

	public List<Date> getAllDates(int occurrences)
		throws MeetingException{
		return getAllDates(occurrences, true);
	}
	
	public List<Date> getAllDates(int occurrences, boolean adjustForHolidays)throws MeetingException{
		validateOccurences(occurrences);
		List meetingDates=new ArrayList();
		Date meetingDate = getFirstDate(getStartDate());
		
		for(int dateCount=0;dateCount<occurrences ;dateCount++){
			if (adjustForHolidays) {
				meetingDates.add(HolidayUtils.adjustDate(DateUtils.getCalendarDate(meetingDate.getTime()), this).getTime());
			} else {
				meetingDates.add(meetingDate);				
			}
			meetingDate = getNextDate(meetingDate);
		}
		return meetingDates;
	}

	public List<Date> getAllDatesWithRepaymentIndepOfMeetingEnabled(
			int occurrences) throws MeetingException {
		return getAllDatesWithRepaymentIndepOfMeetingEnabled(occurrences, true);
	}

	public List<Date> getAllDatesWithRepaymentIndepOfMeetingEnabled(
			int occurrences, boolean adjustForHolidays) throws MeetingException {
		validateOccurences(occurrences);
		List meetingDates = new ArrayList();
		Date meetingDate = getFirstDateWithRepaymentIndepOfMeetingEnabled(getStartDate());

		for (int dateCount = 0; dateCount < occurrences; dateCount++) {
			if (adjustForHolidays) {
				meetingDates.add(HolidayUtils.adjustDate(
						DateUtils.getCalendarDate(meetingDate.getTime()), this)
						.getTime());				
			} else {
				meetingDates.add(meetingDate);				
			}
			meetingDate = getNextDateWithRepaymentIndepOfMeetingEnabled(meetingDate);
		}
		return meetingDates;
	}
	
	private void validateMeetingDate(Date meetingDate)throws MeetingException{
		if (meetingDate==null)
			throw new MeetingException(MeetingConstants.INVALID_MEETINGDATE);
	}
	
	private void validateOccurences(int occurrences)throws MeetingException{
		if (occurrences<=0)
			throw new MeetingException(MeetingConstants.INVALID_OCCURENCES);
	}
	
	private void validateEndDate(Date endDate)throws MeetingException{
		if(endDate == null || endDate.compareTo(getStartDate())<0)
			throw new MeetingException(MeetingConstants.INVALID_ENDDATE);
	}
	
	private Date getFirstDate(Date startDate){
		if(isWeekly())
			return getFirstDateForWeek(startDate);
		else if(isMonthly())
			return getFirstDateForMonth(startDate);
		else
			return getFirstDateForDay(startDate);
	}
	
	private Date getNextDate(Date startDate){
		if(isWeekly())
			return getNextDateForWeek(startDate);
		else if(isMonthly())
			return getNextDateForMonth(startDate);
		else
			return getNextDateForDay(startDate);
	}
	private Date getFirstDateWithRepaymentIndepOfMeetingEnabled(Date startDate){
		if(isWeekly())
			return getFirstDateForWeek(startDate);
		else if(isMonthly())
			return getFirstDateForMonthWithRepaymentIndepOfMeetingEnabled(startDate);
		else
			return getFirstDateForDay(startDate);
	}
	
	private Date getNextDateWithRepaymentIndepOfMeetingEnabled(Date startDate){
		if(isWeekly())
			return getNextDateForWeek(startDate);
		else if(isMonthly())
			return getNextDateForMonthWithRepaymentIndepOfMeetingEnabled(startDate);
		else
			return getNextDateForDay(startDate);
	}
	
	private Date getFirstDateForDay(Date startDate){
		return getNextDateForDay(startDate);
	}
	
	private Date getNextDateForDay(Date startDate){
		gc.setTime(startDate);
		gc.add(Calendar.DAY_OF_WEEK, getMeetingDetails().getRecurAfter());
		return gc.getTime();		
	}
	
	/**
	 * Set the day of week according to given start day to the require weekday, i.e. so it matches the meeting week day.
	 * 
	 * e.g.
	 * 	- If start date is Monday 9 June 2008 and meeting week day is Tuesday, then roll forward the date to Tuesday 10 June 2008
	 *  - or if start date is Sunday 8 June 2008 and meeting week day is Saturday, then roll forward the date to Saturday 14 June 2008
	 *  - or if start date is Tuesday 10 2008 June and meeting week day is Monday, then roll forward the date to Monday 16 June 2008
	 *  - or if start date is Sunday 8 June 2008 and meeting week day is Sunday, then keep the date as Sunday 8 June 2008
	 *  - or if start date is Saturday 7 June 2008 and meeting week day is Sunday, then roll forward the date to Sunday 9 June 2008
	 *  
	 */
	Date getFirstDateForWeek(Date startDate) {
		final GregorianCalendar firstDateForWeek = new GregorianCalendar();
		firstDateForWeek.setTime(startDate);
		int startDateWeekDay = firstDateForWeek.get(Calendar.DAY_OF_WEEK);
		int meetingWeekDay = getMeetingDetails().getWeekDay().getValue();
		
		// Calculate amount of days that need adding to roll forward to the meeting day
		int amountOfDaysToAdd = (meetingWeekDay - startDateWeekDay);
		if (amountOfDaysToAdd < 0) {
			// amountOfDaysToAdd can result in a negative (e.g. Calendar.SATURDAY (7) is greater than Calendar.SUNDAY (1),
			// if so then will add 7 to roll forward a week
			amountOfDaysToAdd += 7;
		}
		firstDateForWeek.add(Calendar.DAY_OF_WEEK, amountOfDaysToAdd);
		return firstDateForWeek.getTime();
	}
	
	private Date getNextDateForWeek(Date startDate){
		gc.setTime(startDate);
		gc.add(Calendar.WEEK_OF_MONTH,getMeetingDetails().getRecurAfter());
		return gc.getTime();
	}
	
	/**
	 * for monthly on date return the next date falling on the same day. 
	 * If date has passed, pass in the date of next month, 
	 * adjust to day number if day number exceed total number of days in month 
	 */
	private Date getFirstDateForMonth(Date startDate){
		Date scheduleDate=null;
		gc.setTime(startDate);

		if (isMonthlyOnDate()){
			int dt = gc.get(GregorianCalendar.DATE);
			//if date passed in, is after the date on which schedule has to lie, move to next month 
			if(dt> getMeetingDetails().getDayNumber())
				gc.add(GregorianCalendar.MONTH,1);
			//set the date on which schedule has to lie
			int M1 = gc.get(GregorianCalendar.MONTH);
			gc.set(GregorianCalendar.DATE,getMeetingDetails().getDayNumber());
			int M2 = gc.get(GregorianCalendar.MONTH);
			int daynum=getMeetingDetails().getDayNumber();
			while(M1!=M2){
				gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
				gc.set(GregorianCalendar.DATE,daynum-1);
				M2 = gc.get(GregorianCalendar.MONTH);
				daynum--;
			}
			scheduleDate=gc.getTime();
			
		}else{
			//if current weekday is after the weekday on which schedule has to lie, move to next week
			if (gc.get(Calendar.DAY_OF_WEEK)>getMeetingDetails().getWeekDay().getValue())
				gc.add(Calendar.WEEK_OF_MONTH,1);
			//set the weekday on which schedule has to lie
			gc.set(Calendar.DAY_OF_WEEK,getMeetingDetails().getWeekDay().getValue());
			//if week rank is First, Second, Third or Fourth, Set the respective week.
			//if current week rank is after the weekrank on which schedule has to lie, move to next month
			if(!getMeetingDetails().getWeekRank().equals(RankType.LAST)){
				if(gc.get(Calendar.DAY_OF_WEEK_IN_MONTH)>getMeetingDetails().getWeekRank().getValue()){
					gc.add(GregorianCalendar.MONTH,1);
					gc.set(GregorianCalendar.DATE,1);
				}
				//set the weekrank on which schedule has to lie
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
				scheduleDate=gc.getTime();
			}
			else {//scheduleData.getWeekRank()=Last
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays in a month
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}
	
	/**
	 * for monthly is on date add the number of months after which meeting is to recur, 
	 * and then adjust the date for day on which meeting is to occur
	 */
	private Date getNextDateForMonth(Date startDate){
		Date scheduleDate=null;
		gc.setTime(startDate);
		if(isMonthlyOnDate()){
			//move to next month and return date.
			gc.add(GregorianCalendar.MONTH,getMeetingDetails().getRecurAfter());
			int M1 = gc.get(GregorianCalendar.MONTH);
			gc.set(GregorianCalendar.DATE,getMeetingDetails().getDayNumber());
			int M2 = gc.get(GregorianCalendar.MONTH);
			int daynum=getMeetingDetails().getDayNumber();
			while(M1!=M2){
				gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
				gc.set(GregorianCalendar.DATE,daynum-1);
				M2 = gc.get(GregorianCalendar.MONTH);
				daynum--;
			}
			scheduleDate=gc.getTime();
		}else{
			if(!getMeetingDetails().getWeekRank().equals(RankType.LAST))
			{
				//apply month recurrence
				gc.add(GregorianCalendar.MONTH,getMeetingDetails().getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK,getMeetingDetails().getWeekDay().getValue());
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
				scheduleDate=gc.getTime();
			}else{//weekCount=-1
				gc.set(GregorianCalendar.DATE,15);
				gc.add(GregorianCalendar.MONTH,getMeetingDetails().getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK,getMeetingDetails().getWeekDay().getValue());
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays	in a month			
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}	
	private Date getFirstDateForMonthWithRepaymentIndepOfMeetingEnabled(Date startDate){
		Date scheduleDate=null;
		gc.setTime(startDate);

		if (isMonthlyOnDate()){
			int disbursalDateValue = gc.get(GregorianCalendar.DATE);
			
			gc.set(GregorianCalendar.DAY_OF_WEEK,getMeetingDetails().getDayNumber());
			gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
			int fisrtRepaymentDateValue= gc.get(GregorianCalendar.DATE);
			//if date passed in, is after the date on which schedule has to lie, move to next month 
			if(disbursalDateValue>=fisrtRepaymentDateValue)
				gc.add(GregorianCalendar.MONTH,1);
			
			gc.set(GregorianCalendar.DAY_OF_WEEK,getMeetingDetails().getDayNumber());
			gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
			
			scheduleDate=gc.getTime();
			
		}else{
			//if current weekday is after the weekday on which schedule has to lie, move to next week
			if (gc.get(Calendar.DAY_OF_WEEK)>getMeetingDetails().getWeekDay().getValue())
				gc.add(Calendar.WEEK_OF_MONTH,1);
			//set the weekday on which schedule has to lie
			gc.set(Calendar.DAY_OF_WEEK,getMeetingDetails().getWeekDay().getValue());
			//if week rank is First, Second, Third or Fourth, Set the respective week.
			//if current week rank is after the weekrank on which schedule has to lie, move to next month
			if(!getMeetingDetails().getWeekRank().equals(RankType.LAST)){
				if(gc.get(Calendar.DAY_OF_WEEK_IN_MONTH)>getMeetingDetails().getWeekRank().getValue()){
					gc.add(GregorianCalendar.MONTH,1);
					gc.set(GregorianCalendar.DATE,1);
				}
				//set the weekrank on which schedule has to lie
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
				scheduleDate=gc.getTime();
			}
			else {//scheduleData.getWeekRank()=Last
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays in a month
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}	
	
	private Date getNextDateForMonthWithRepaymentIndepOfMeetingEnabled(Date startDate){
		Date scheduleDate=null;
		gc.setTime(startDate);
		if(isMonthlyOnDate()){
//			move to next month and return date.
			gc.add(GregorianCalendar.MONTH,getMeetingDetails().getRecurAfter());
			gc.set(GregorianCalendar.DAY_OF_WEEK,getMeetingDetails().getDayNumber());
			gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
			scheduleDate=gc.getTime();
		}else{
			if(!getMeetingDetails().getWeekRank().equals(RankType.LAST))
			{
				//apply month recurrence
				gc.add(GregorianCalendar.MONTH,getMeetingDetails().getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK,getMeetingDetails().getWeekDay().getValue());
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,getMeetingDetails().getWeekRank().getValue());
				scheduleDate=gc.getTime();
			}else{//weekCount=-1
				gc.set(GregorianCalendar.DATE,15);
				gc.add(GregorianCalendar.MONTH,getMeetingDetails().getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK,getMeetingDetails().getWeekDay().getValue());
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays	in a month			
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}

}

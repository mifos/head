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
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

/**
 * This class encapsulate the meeting
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

	//	TODO: change return type to date
	public Calendar getMeetingStartDate() {
		Calendar cal = new GregorianCalendar();
		if(meetingStartDate!=null)
			cal.setTime(meetingStartDate);
		return cal;
	}

	//TODO: change parameter to date
	public void setMeetingStartDate(Calendar meetingStartDate) {
		this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(meetingStartDate.getTimeInMillis());
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
		return (currentScheduleDate.compareTo(endDateWOTimeStamp)<=0 && currentScheduleDate.compareTo(meetingDateWOTimeStamp)==0);
	}
	
	public boolean isValidMeetingDate(Date meetingDate, int occurrences)throws MeetingException{
		validateMeetingDate(meetingDate);
		validateOccurences(occurrences);
		Date currentScheduleDate=getFirstDate(getStartDate());
		Date meetingDateWOTimeStamp = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());
		
		for(int currentNumber=1; (currentScheduleDate.compareTo(meetingDateWOTimeStamp)<0) && currentNumber<occurrences; currentNumber++)
			currentScheduleDate=getNextDate(currentScheduleDate);
		return (currentScheduleDate.compareTo(meetingDateWOTimeStamp)==0);
	}
	
	public Date getNextScheduleDateAfterRecurrence(Date meetingDate)throws MeetingException{
		validateMeetingDate(meetingDate);
		Date currentScheduleDate=getNextDate(getFirstDate(getStartDate()));
		//Date currentScheduleDate=getNextDate(getStartDate());
		for(; currentScheduleDate.compareTo(meetingDate)<=0;
			currentScheduleDate=getNextDate(currentScheduleDate));
		
		//return HolidayUtils.adjustDate(HolidayUtils.getCalendar(currentScheduleDate), this).getTime();
		return HolidayUtils.adjustDate(DateUtils.getCalendarDate(currentScheduleDate.getTime()), this).getTime();
		//return currentScheduleDate;
	}
	
	
	public Date getNextScheduleDateAfterRecurrenceWithoutAdjustment(Date meetingDate)throws MeetingException{
		validateMeetingDate(meetingDate);
		Date currentScheduleDate=getNextDate(getFirstDate(getStartDate()));
		//Date currentScheduleDate=getNextDate(getStartDate());
		for(; currentScheduleDate.compareTo(meetingDate)<=0;
			currentScheduleDate=getNextDate(currentScheduleDate));
		
		return currentScheduleDate;
	}
	
	public Date getPrevScheduleDateAfterRecurrence(Date meetingDate)throws MeetingException{
		validateMeetingDate(meetingDate);
		Date prevScheduleDate=null;
		for(Date currentScheduleDate=getNextDate(getStartDate()); 
			(currentScheduleDate.compareTo(meetingDate)<0);
			prevScheduleDate = currentScheduleDate,currentScheduleDate = getNextDate(currentScheduleDate));
		return prevScheduleDate;
	}
	
	
	public List<Date> getAllDates(Date endDate)throws MeetingException{
		validateEndDate(endDate);
		List meetingDates= new ArrayList();
		for(Date meetingDate = getFirstDate(getStartDate()); meetingDate.compareTo(endDate)<=0; meetingDate = getNextDate(meetingDate))
			meetingDates.add(meetingDate);
			
		return meetingDates;
	}
	
	public List<Date> getAllDates(int occurrences)throws MeetingException{
		validateOccurences(occurrences);
		List meetingDates=new ArrayList();
		Date meetingDate = getFirstDate(getStartDate());
		
		for(int dateCount=0;dateCount<occurrences ;dateCount++){
			//meetingDates.add(meetingDate);
			meetingDates.add(HolidayUtils.adjustDate(DateUtils.getCalendarDate(meetingDate.getTime()), this).getTime());
			meetingDate = getNextDate(meetingDate);
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
	
	
	private Date getFirstDateForDay(Date startDate){
		return getNextDateForDay(startDate);
	}
	
	private Date getNextDateForDay(Date startDate){
		gc.setTime(startDate);
		gc.add(Calendar.DAY_OF_WEEK, getMeetingDetails().getRecurAfter());
		return gc.getTime();		
	}
	
	private Date getFirstDateForWeek(Date startDate){
		gc.setTime(startDate);
		if (gc.get(Calendar.DAY_OF_WEEK)> getMeetingDetails().getWeekDay().getValue()){
			gc.add(Calendar.WEEK_OF_MONTH,1);
		}
		gc.set(Calendar.DAY_OF_WEEK, getMeetingDetails().getWeekDay().getValue());
		return gc.getTime();
	}
	
	private Date getNextDateForWeek(Date startDate){
		gc.setTime(startDate);
		gc.add(Calendar.WEEK_OF_MONTH,getMeetingDetails().getRecurAfter());
		return gc.getTime();
	}
	
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

}

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

import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.util.helpers.DateUtils;

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
	
	private GregorianCalendar gc = new GregorianCalendar();
	
	protected MeetingBO() {
		this.meetingId = null;
		this.meetingDetails = null;
		this.meetingType = null;
		this.meetingStartDate = null;
	}

	public MeetingBO(RecurrenceType recurrenceType, Short recurAfter, Date startDate, MeetingType meetingType){
		this(recurrenceType, Short.valueOf("1"), WeekDay.MONDAY, null, recurAfter, startDate, meetingType);
	}
	
	public MeetingBO(WeekDay weekDay, RankType rank, Short recurAfter, Date startDate, MeetingType meetingType){
		this(RecurrenceType.MONTHLY, null, weekDay, rank, recurAfter, startDate, meetingType);
	}
	
	public MeetingBO(Short dayNumber, Short recurAfter, Date startDate, MeetingType meetingType){
		this(RecurrenceType.MONTHLY, dayNumber, null, null, recurAfter, startDate, meetingType);
	}
	
	public MeetingBO(WeekDay weekDay, Short recurAfter, Date startDate, MeetingType meetingType){
		this(RecurrenceType.WEEKLY, null, weekDay, null, recurAfter, startDate, meetingType);
	}
	
	private MeetingBO(RecurrenceType recurrenceType, Short dayNumber, WeekDay weekDay, RankType rank, Short recurAfter, Date startDate, MeetingType meetingType){
		this.meetingDetails =  new MeetingDetailsEntity(new RecurrenceTypeEntity(recurrenceType), dayNumber, weekDay, rank, recurAfter, this);
		//TODO: remove this check after meeting create is migrated.
		if(meetingType!=null)
			this.meetingType = new MeetingTypeEntity(meetingType);
		this.meetingId = null;
		this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(startDate.getTime());		
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
		cal.setTime(meetingStartDate);
		return cal;
	}

	//TODO: change parameter to date
	public void setMeetingStartDate(Calendar meetingStartDate) {
		this.meetingStartDate = DateUtils.getDateWithoutTimeStamp(meetingStartDate.getTimeInMillis());
	}

	public Date getStartDate() {
		return meetingStartDate;
	}

	public MeetingTypeEntity getMeetingType() {
		return meetingType;
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
	/**
	 * This function returns the meeting schedule based on the values set in the
	 * meeting object
	 * 
	 * @return
	 */
	public String getMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceTypeEntity rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = "Recur every "
							+ this.meetingDetails.getRecurAfter()
							+ " Week(s) "
							+ " on "
							+ getDayString(this.meetingDetails.getWeekDay().getValue());

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					MeetingRecurrenceEntity mr = this.meetingDetails
							.getMeetingRecurrence();
					Short dayNumber = mr.getDayNumber();
					Short day = null;
					Short rank = null;
					if(mr.getWeekDayValue()!=null)
						day = mr.getWeekDayValue().getValue();
					if(mr.getRankOfDays()!=null)
						rank = mr.getRankOfDays().getId();

					if (null != dayNumber) {
						meeetingInfo = "Recur on day " + dayNumber.shortValue()
								+ " of every "
								+ this.meetingDetails.getRecurAfter()
								+ " month(s) ";
					} else {
						// bug 27987 -- added months at the end
						meeetingInfo = "Recur on " + getRankString(rank) + " "
								+ getDayString(day) + " of every "
								+ this.meetingDetails.getRecurAfter()
								+ " month(s) ";
					}

				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the meeting schedule based on the values set in the
	 * meeting object
	 * 
	 * @return
	 */

	public String getSimpleMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceTypeEntity rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = "Periodicity: Recur every "
							+ this.meetingDetails.getRecurAfter() + " Week(s) ";

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					meeetingInfo = "Periodicity: Recur  every "
							+ this.meetingDetails.getRecurAfter()
							+ " month(s) ";
				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the meeting schedule based on the values set in the
	 * meeting object
	 * 
	 * @return String
	 */
	public String getShortMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceTypeEntity rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = this.meetingDetails.getRecurAfter()
							+ " week(s) ";

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					meeetingInfo = this.meetingDetails.getRecurAfter()
							+ " month(s) ";
				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the day of week based on the passed value
	 * 
	 * @param day
	 * @return
	 */
	private String getDayString(Short day) {
		String weekDay = "";
		switch (day.shortValue()) {
		case 1:
			weekDay = "Sunday";
			break;
		case 2:
			weekDay = "Monday";
			break;
		case 3:
			weekDay = "Tuesday";
			break;
		case 4:
			weekDay = "Wednesday";
			break;
		case 5:
			weekDay = "Thrusday";
			break;
		case 6:
			weekDay = "Friday";
			break;
		case 7:
			weekDay = "Saturday";
			break;

		}
		return weekDay;
	}

	/**
	 * This function returns the rank string for the day
	 * 
	 * @param rank
	 * @return
	 */
	private String getRankString(Short rank) {
		String rankString = "";
		switch (rank.shortValue()) {
		case 1:
			rankString = "First";
			break;
		case 2:
			rankString = "Second";
			break;

		case 3:
			rankString = "Third";
			break;
		case 4:
			rankString = "Forth";
			break;
		case 5:
			rankString = "Last";
			break;

		}

		return rankString;

	}
	
	public boolean isValidMeetingDate(Date meetingDate, Date endDate)throws MeetingException{
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
		Date currentScheduleDate=getNextDate(getStartDate());
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
			meetingDates.add(meetingDate);
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
		if(endDate == null || endDate.compareTo(getStartDate())<=0)
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

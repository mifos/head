/**

 * MeetingActionForm.java    version: 1.0

 

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
package org.mifos.application.meeting.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * @author rajenders
 * 
 */
public class MeetingActionForm extends MifosActionForm {
	private static final long serialVersionUID = 44l;

	 MifosLogger meetingLogger = MifosLogManager.getLogger(LoggerConstants.MEETINGLOGGER);

	/**
	 * This would hold the meetingId
	 */
	private String meetingId;
	/**
	 * This would the meeting frequency for the meeting
	 */
	private String frequency;

	/**
	 * This would hold the recur week if week is being selected
	 */
	private String recurWeek;

	/**
	 * This would hold the day of week if weekly meeting is selected
	 */
	private String weekDay;

	/**
	 * This would hold the mothtype checkbox if month is selected
	 */
	private String monthType;

	/**
	 * This would hold the month day passed by user if he selects the month
	 */
	private String monthDay;

	/**
	 * This would hold the month's month if month is selected
	 */
	private String monthMonth;

	/**
	 * This would hold the month rank if month is selected
	 */

	private String monthRank;

	/**
	 * This would hold the month weekday if month is selected
	 */
	private String monthWeek;

	/**
	 * This would hold the month if second checkbox in month is selected
	 */
	private String monthMonthRank;

	/**
	 * This would hold the palace of the meeting
	 */
	private String meetingPlace;
	
	/**
	 * This would hold where we need to go
	 */
	private String  customerId;
	
	/**
	 * This would hold the version no
	 */
	private String versionNo;

	/**
	 * This function returns the versionNo
	 * @return Returns the versionNo.
	 */
	
	public String getVersionNo() {
		return versionNo;
	}

	/**
	 * This function sets the versionNo
	 * @param versionNo the versionNo to set.
	 */
	
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}



	/**
	 * This function returns the meetingPlace
	 * @return Returns the meetingPlace.
	 */
	
	public String getMeetingPlace() {
		return meetingPlace;
	}

	/**
	 * This function sets the meetingPlace
	 * @param meetingPlace the meetingPlace to set.
	 */
	
	public void setMeetingPlace(String meetingPlace) {
		this.meetingPlace = meetingPlace;
	}

	/**
	 * This function returns the monthRank
	 * 
	 * @return Returns the monthRank.
	 */

	public String getMonthRank() {
		return monthRank;
	}

	/**
	 * This function sets the monthRank
	 * 
	 * @param monthRank
	 *            the monthRank to set.
	 */

	public void setMonthRank(String monthRank) {
		this.monthRank = monthRank;
	}

	/**
	 * This function returns the monthWeek
	 * 
	 * @return Returns the monthWeek.
	 */

	public String getMonthWeek() {
		return monthWeek;
	}

	/**
	 * This function sets the monthWeek
	 * 
	 * @param monthWeek
	 *            the monthWeek to set.
	 */

	public void setMonthWeek(String monthWeek) {
		this.monthWeek = monthWeek;
	}

	/**
	 * This function returns the weekDay
	 * 
	 * @return Returns the weekDay.
	 */

	public String getWeekDay() {
		return weekDay;
	}

	/**
	 * This function sets the weekDay
	 * 
	 * @param weekDay
	 *            the weekDay to set.
	 */

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	/**
	 * This function returns the monthMonth
	 * 
	 * @return Returns the monthMonth.
	 */

	public String getMonthMonth() {
		return monthMonth;
	}

	/**
	 * This function sets the monthMonth
	 * 
	 * @param monthMonth
	 *            the monthMonth to set.
	 */

	public void setMonthMonth(String monthMonth) {
		this.monthMonth = monthMonth;
	}

	/**
	 * This function returns the monthDay
	 * 
	 * @return Returns the monthDay.
	 */

	public String getMonthDay() {
		return monthDay;
	}

	/**
	 * This function sets the monthDay
	 * 
	 * @param monthDay
	 *            the monthDay to set.
	 */

	public void setMonthDay(String monthDay) {
		this.monthDay = monthDay;
	}

	/**
	 * This function returns the monthType
	 * 
	 * @return Returns the monthType.
	 */

	public String getMonthType() {
		return monthType;
	}

	/**
	 * This function sets the monthType
	 * 
	 * @param monthType
	 *            the monthType to set.
	 */

	public void setMonthType(String monthType) {
		this.monthType = monthType;
	}

	/**
	 * This function returns the recurWeek
	 * 
	 * @return Returns the recurWeek.
	 */

	public String getRecurWeek() {
		return recurWeek;
	}

	/**
	 * This function sets the recurWeek
	 * 
	 * @param recurWeek
	 *            the recurWeek to set.
	 */

	public void setRecurWeek(String recurWeek) {
		this.recurWeek = recurWeek;
	}

	/**
	 * This function returns the frequency
	 * 
	 * @return Returns the frequency.
	 */

	public String getFrequency() {
		return frequency;
	}

	/**
	 * This function sets the frequency
	 * 
	 * @param frequency
	 *            the frequency to set.
	 */

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/**
	 * This function returns the monthMonthRank
	 * 
	 * @return Returns the monthMonthRank.
	 */

	public String getMonthMonthRank() {
		return monthMonthRank;
	}

	/**
	 * This function sets the monthMonthRank
	 * 
	 * @param monthMonthRank
	 *            the monthMonthRank to set.
	 */

	public void setMonthMonthRank(String monthMonthRank) {
		this.monthMonthRank = monthMonthRank;
	}

	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		String methodCalled = request.getParameter("method");

		if (null != methodCalled) {

			if ("cancel".equals(methodCalled) || "load".equals(methodCalled)
					||"get".equals(methodCalled)||"loadMeeting".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}
			else
			{
				try
				{

				meetingLogger.info("Selected meeting recurrence type is "+ frequency);
				if (null!= frequency && frequency.equals(MeetingConstants.MONTHLY))
				{
					if(null!= monthType&& monthType.equals(MeetingConstants.MONTH_WITHOUT_RANK))
					{
						//validate the value for month day
						
						meetingLogger.info("Selected meeting monthly type is "+ monthType);
						
						
						if( null!=monthDay)
						{
							// i will always get the number here
							  short mDay = Short.valueOf(monthDay).shortValue();
								meetingLogger.info("entered value for month day is  "+ mDay);
							  
							  //check whether the entered value lies in the month range 
								
								boolean inValid = false;
								  ActionErrors errors=new ActionErrors();
							  if(mDay <1 || mDay >31 )
							  {
								  inValid =true;
									errors.add(MeetingConstants.KEYINVALIDMONTH,new ActionMessage(MeetingConstants.KEYINVALIDMONTH));
							  }
							short recurAfter = Short.valueOf(this.monthMonth).shortValue();
							  if ( recurAfter <1)
							  {
								  inValid =true;
								errors.add(MeetingConstants.KEYINVALIDRECURAFTER,new ActionMessage(MeetingConstants.KEYINVALIDRECURAFTER));
							  }
							  if (inValid) return errors;							  
							
						}
					}
					else if(null!= monthType&& monthType.equals(MeetingConstants.MONTH_WITH_RANK))
					{
						  ActionErrors errors=new ActionErrors();  
							short recurAfter = Short.valueOf(this.monthMonthRank).shortValue();

							  if ( recurAfter <1)
							  {
								errors.add(MeetingConstants.KEYINVALIDRECURAFTER,new ActionMessage(MeetingConstants.KEYINVALIDRECURAFTER));
								return errors;
							  }
							
							return errors;
						
					}

				}
				else if (null!= frequency && frequency.equals(MeetingConstants.WEEKLY))
				{
					  ActionErrors errors=new ActionErrors();  
					  
					  short recurWeek = Short.valueOf(this.recurWeek).shortValue();

					  if ( recurWeek <1)
					  {
						errors.add(MeetingConstants.KEYINVALIDRECURAFTER,new ActionMessage(MeetingConstants.KEYINVALIDRECURAFTER));
						return errors;
					  }
					
				}
				}
				catch ( NumberFormatException nfe)
				{
					//TODO what to do here 
				}
			}
			

		}

		return null;
	}

	/**
	 * This function will returns the meeting details
	 */
	public MeetingDetails getMeetingDetails() {
		MeetingDetails meetingDetails = new MeetingDetails();

		RecurrenceType recurrenceType = new RecurrenceType();
		MeetingRecurrence meetingRecurrence = new MeetingRecurrence();
		
	
		if (this.frequency != null) {

			try {
				Short freq =Short.valueOf(this.frequency);
				recurrenceType.setRecurrenceId(freq);
				meetingLogger.info("inside frequency"+this.frequency);
				//if user has selected the week we need to set the proper values
				if (freq.shortValue() == MeetingConstants.WEEK) {
					
					
					meetingLogger.info("recur after in week"+this.recurWeek);
					//first set the recur after for week  
					if (null != this.recurWeek) {
						meetingDetails.setRecurAfter(Short
								.parseShort(this.recurWeek));
						
						

					} else {
						// TODO this is an error throw error message
					}
					
					//set the days for the week
					if(null!=this.weekDay)
					{

						meetingRecurrence.setWeekDay(Short.valueOf(this.weekDay));
						
					}else {
						// TODO this is an error throw error message
					}
					
					
				} else if (freq.shortValue() == MeetingConstants.MONTH) {
					if (null != monthType) {
						
						
						Short mt = Short.valueOf(this.monthType);
						if ( mt.shortValue()== MeetingConstants.MONTHRECURDAY) {
							if(null!=monthMonth)
							{
							meetingDetails.setRecurAfter(Short
									.parseShort(this.monthMonth));
							}
							else
							{
								//TODO this is an error throw error message
							}
							
							if(null!=this.monthDay)
							{
								meetingRecurrence.setDayNumber(Short.valueOf(this.monthDay));
							}
							else
							{
									// TODO this is an error throw error message
							}

						} else if (mt.shortValue() == MeetingConstants.MONTHRECURRANK) {
							if(null!=this.monthMonthRank)
							{
							meetingDetails.setRecurAfter(Short
									.parseShort(this.monthMonthRank));
							}
							else
							{
								//TODO this is an error throw error message
							}
							if( null!=this.monthRank)
							{
								meetingRecurrence.setRankOfDays(Short.valueOf(this.monthRank));
							}
							else
							{
								//TODO this is an error throw error message
							}
							if( null!=this.monthWeek)
							{
								meetingRecurrence.setWeekDay(Short.valueOf(this.monthWeek));
								
							}
							else
							{
								//TODO this is an error throw error message
							}



						}
					} else {
						// TODO this is an error throw error message
					}

				}else
				{
						//	TODO this is an error throw error message
				}

			} catch (NumberFormatException e) {
				// TODO throw proper exception in most of cases we will get some
				// value
			}
		}
		meetingDetails.setRecurrenceType(recurrenceType);
		meetingDetails.setMeetingRecurrence(meetingRecurrence);

		return meetingDetails;
	}

	/**
	 * This function returns the meetingId
	 * @return Returns the meetingId.
	 */
	
	public String getMeetingId() {
		return meetingId;
	}

	/**
	 * This function sets the meetingId
	 * @param meetingId the meetingId to set.
	 */
	
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	/**
	 * This function returns the customerId
	 * @return Returns the customerId.
	 */
	
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * This function sets the customerId
	 * @param customerId the customerId to set.
	 */
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

   public void clearForm()
   {
	   this.customerId="";
	   this.frequency="";
	   this.meetingId="";
	   this.meetingPlace="";
	   this.monthDay="";
	   this.monthMonth="";
	   this.monthMonthRank="";
	   this.monthRank="";
	   this.monthType="";
	   this.monthWeek="";
	   this.recurWeek="";
	   this.versionNo="";
	   this.weekDay="";
   }
}

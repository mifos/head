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
 
package org.mifos.application.meeting.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;

public class MeetingActionForm extends BaseActionForm {
	private static final long serialVersionUID = 44l;

	private String meetingId;

	private String frequency;

	private String recurWeek;
	
	private String weekDay;

	private String monthType;
	
	private String monthDay;
	
	private String dayRecurMonth;
	
	private String monthRank;
	
	private String monthWeek;

	private String recurMonth;
	
	private String meetingPlace;
	
	private String customerLevel;
	
	private String input;
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getDayRecurMonth() {
		return dayRecurMonth;
	}

	public void setDayRecurMonth(String dayRecurMonth) {
		this.dayRecurMonth = dayRecurMonth;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getMeetingPlace() {
		return meetingPlace;
	}

	public void setMeetingPlace(String meetingPlace) {
		this.meetingPlace = meetingPlace;
	}

	public String getMonthDay() {
		return monthDay;
	}

	public void setMonthDay(String monthDay) {
		this.monthDay = monthDay;
	}

	public String getMonthRank() {
		return monthRank;
	}

	public void setMonthRank(String monthRank) {
		this.monthRank = monthRank;
	}

	public String getMonthType() {
		return monthType;
	}

	public void setMonthType(String monthType) {
		this.monthType = monthType;
	}

	public String getMonthWeek() {
		return monthWeek;
	}

	public void setMonthWeek(String monthWeek) {
		this.monthWeek = monthWeek;
	}

	public String getRecurMonth() {
		return recurMonth;
	}

	public void setRecurMonth(String recurMonth) {
		this.recurMonth = recurMonth;
	}

	public String getRecurWeek() {
		return recurWeek;
	}

	public void setRecurWeek(String recurWeek) {
		this.recurWeek = recurWeek;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getCustomerLevel() {
		return customerLevel;
	}

	public void setCustomerLevel(String customerLevel) {
		this.customerLevel = customerLevel;
	}

	public CustomerLevel getCustomerLevelValue() {
		return CustomerLevel.getLevel(Short.valueOf(getCustomerLevel()));
	}
	
	public WeekDay getWeekDayValue() {
		return StringUtils.isNullAndEmptySafe(getWeekDay())?WeekDay.getWeekDay(Short.valueOf(getWeekDay())):null;
	}
	
	public RecurrenceType getRecurrenceType() {
		return StringUtils.isNullAndEmptySafe(getFrequency())?RecurrenceType.fromInt(Short.valueOf(getFrequency())):null;
	}
	
	public boolean isMonthlyOnDate() {
		return getRecurrenceType().equals(RecurrenceType.MONTHLY) && getMonthType().equals(MeetingConstants.MONTHLY_ON_DATE); 
	}
	
	public Short getRecurWeekValue() {
		return getShortValue(getRecurWeek());
	}
	
	public Short getRecurMonthValue() {
		return getShortValue(getRecurMonth());
	}
	
	public Short getDayRecurMonthValue() {
		return getShortValue(getDayRecurMonth());
	}
	
	public Short getMonthDayValue() {
		return getShortValue(getMonthDay());
	}
	
	public RankType getMonthRankValue() {
		return StringUtils.isNullAndEmptySafe(getMonthRank())?RankType.getRankType(Short.valueOf(getMonthRank())):null;
	}
	
	public WeekDay getMonthWeekValue() {
		return StringUtils.isNullAndEmptySafe(getMonthWeek())?WeekDay.getWeekDay(Short.valueOf(getMonthWeek())):null;
	}
	
	public Integer getMeetingIdValue() {
		return getIntegerValue(meetingId);
	}
	
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request){
		String method = request.getParameter("method");
		request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
		ActionErrors errors = null;
		try{
			errors = validateFields(request, method);
		}
		catch(ApplicationException ae){
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
			
		}
		return errors;
	}
	
	private ActionErrors validateFields(HttpServletRequest request, String method)throws ApplicationException{
		ActionErrors errors = null;
		if(Methods.create.toString().equals(method) || Methods.update.toString().equals(method))
			errors = validateMeeting();
		return errors;
	}
	
	private ActionErrors validateMeeting(){
		ActionErrors errors = new ActionErrors();
		if(getRecurrenceType()==null)
			errors.add(MeetingConstants.INVALID_RECURRENCETYPE, new ActionMessage(MeetingConstants.INVALID_RECURRENCETYPE));
		else if(getRecurrenceType().equals(RecurrenceType.WEEKLY))
			validateWeeklyMeeting(errors);
		else if(getRecurrenceType().equals(RecurrenceType.MONTHLY))
			validateMonthlyMeeting(errors);
		
		if(StringUtils.isNullOrEmpty(getMeetingPlace()))
			errors.add(MeetingConstants.INVALID_MEETINGPLACE, new ActionMessage(MeetingConstants.INVALID_MEETINGPLACE));
		return errors;
	}
	
	private void validateWeeklyMeeting(ActionErrors errors){
		if(getWeekDayValue()==null || getRecurWeekValue()==null)
			errors.add(MeetingConstants.ERRORS_SPECIFY_WEEKDAY_AND_RECURAFTER, new ActionMessage(MeetingConstants.ERRORS_SPECIFY_WEEKDAY_AND_RECURAFTER));
	}
	
	private void validateMonthlyMeeting(ActionErrors errors){
		if(StringUtils.isNullAndEmptySafe(monthType) && monthType.equals(MeetingConstants.MONTHLY_ON_DATE)){
			if(getMonthDayValue()==null || getDayRecurMonthValue()==null)
				errors.add(MeetingConstants.ERRORS_SPECIFY_DAYNUM_AND_RECURAFTER, new ActionMessage(MeetingConstants.ERRORS_SPECIFY_DAYNUM_AND_RECURAFTER));
		}else if(getMonthRankValue()==null || getMonthWeekValue() ==null || getRecurMonthValue()==null)
				errors.add(MeetingConstants.ERRORS_SPECIFY_MONTHLY_MEETING_ON_WEEKDAY, new ActionMessage(MeetingConstants.ERRORS_SPECIFY_MONTHLY_MEETING_ON_WEEKDAY));
	}
	

	public String getMeetingId() {
		return meetingId;
	}
	
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
}

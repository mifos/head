/**
 * MeetingScheduleHelper.java version:1.0
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

package org.mifos.framework.components.repaymentschedule;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;

import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;

import org.mifos.framework.components.scheduler.Constants;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.ScheduleInputsIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 *
 *  This class is the helper class which perform schedule related tasks
 */
public class MeetingScheduleHelper
{
/**
	 * This method returns the scheduler object
	 * @param Meeting
	 * @param isHolidayRequired
	 * @return  SchedularIntf
	 * @throws RepaymentScheduleException
	 */

	public static SchedulerIntf getSchedulerObject(Meeting meeting,boolean holidayWeekOffRequired) throws RepaymentScheduleException,SchedulerException
	{
		validate(meeting);


		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();

		try
		{

			ScheduleDataIntf scheduleData;

			scheduleData=SchedulerFactory.getScheduleData(getReccurence(recurrenceId));

			return getScheduler(scheduleData , meeting,holidayWeekOffRequired);
		}
		catch(SchedulerException scheduleException)
		{
			throw scheduleException;
		}



	}

	private static void validate(Meeting meeting) throws RepaymentScheduleException
	{

	}

/**
	 * This method returns reccurence
	 * @param reccurenceId
	 * @return  String
	 */
	public static String getReccurence(Short recurrenceId)
	{

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("MeetingSchedulerHelper:getReccurence  "+recurrenceId);
		if (recurrenceId.intValue() == 1)
			return Constants.WEEK;

		if (recurrenceId.intValue() == 2)
			return Constants.MONTH;

		if (recurrenceId.intValue() == 3)
			return Constants.MONTH;

		return "";

	}

	private static SchedulerIntf getScheduler( ScheduleDataIntf scheduleData,Meeting meeting , boolean holidayWeekOffRequired) throws RepaymentScheduleException,SchedulerException
	{

			SchedulerIntf scheduler;
			ScheduleInputsIntf scheduleInputs;

			scheduler = SchedulerFactory.getScheduler();

			if(holidayWeekOffRequired)
			{
				scheduler.setWeekOffList(getWeekOffList());
				scheduler.setHolidayList(getHolidayList());
				scheduler.setScheduleHolidayOption(getHolidayConfig());
			}


			scheduleInputs=SchedulerFactory.getScheduleInputs();
			scheduleInputs.setStartDate(meeting.getMeetingStartDate().getTime());

			scheduleData.setRecurAfter(meeting.getMeetingDetails().getRecurAfter().intValue());

			if(scheduleData.getClass().getName().equals("org.mifos.framework.components.scheduler.DayData"))
			{
					throw new RepaymentScheduleException(RepaymentScheduleConstansts.NOT_SUPPORTED_FREQUENCY_TYPE);

			}
			else
			if(scheduleData.getClass().getName().equals("org.mifos.framework.components.scheduler.WeekData"))
			{

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("MeetingSchedulerHelper:getScheduler  returning week object ");
				scheduleData.setWeekDay(meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay().intValue());



			}
			else
			if(scheduleData.getClass().getName().equals("org.mifos.framework.components.scheduler.MonthData"))
			{
					MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).info("MeetingSchedulerHelper:getScheduler  returning month object ");
					Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
					if(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber() != null)
						scheduleData.setDayNumber(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber().intValue());
					else
					{
								scheduleData.setWeekDay(meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay().intValue());
								scheduleData.setWeekRank(meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays().intValue());


					}



			}
			else
			throw new RepaymentScheduleException(RepaymentScheduleConstansts.NOT_SUPPORTED_FREQUENCY_TYPE);

			scheduleInputs.setScheduleData(scheduleData);
			scheduler.setScheduleInputs(scheduleInputs);

			return scheduler;



    }
/**
	 * This method merges frequency
	 * @param repaymentFrequency
	 * @param meetingToMerge
	 * @return  Meeting
	 */
	public static Meeting mergeFrequency(Meeting repaymentFrequency, Meeting meetingToMerge)
	{
			Meeting meetingToReturn = new Meeting();
			meetingToReturn.setMeetingStartDate(repaymentFrequency.getMeetingStartDate());
			meetingToReturn.setMeetingPlace("");
			meetingToReturn.setMeetingType(repaymentFrequency.getMeetingType());

			MeetingRecurrence  meetingRecToReturn = new MeetingRecurrence();
			meetingRecToReturn.setDayNumber(repaymentFrequency.getMeetingDetails().getMeetingRecurrence().getDayNumber());
			meetingRecToReturn.setRankOfDays(repaymentFrequency.getMeetingDetails().getMeetingRecurrence().getRankOfDays());
			meetingRecToReturn.setWeekDay(repaymentFrequency.getMeetingDetails().getMeetingRecurrence().getWeekDay());

			MeetingDetails meetingDetailsToReturn = new MeetingDetails();
			meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
			meetingDetailsToReturn.setRecurAfter(meetingToMerge.getMeetingDetails().getRecurAfter());
			meetingDetailsToReturn.setRecurrenceType(repaymentFrequency.getMeetingDetails().getRecurrenceType());

			meetingToReturn.setMeetingDetails(meetingDetailsToReturn);

			return meetingToReturn;

	}

	public static Meeting getFrequency(Meeting repaymentFrequency,java.util.Date startDate)
	{

				Calendar startCal = new GregorianCalendar();
				startCal.setTime(startDate);

				Meeting meetingToReturn = new Meeting();
				meetingToReturn.setMeetingStartDate(startCal);
				meetingToReturn.setMeetingPlace("");
				meetingToReturn.setMeetingType(repaymentFrequency.getMeetingType());

				MeetingRecurrence  meetingRecToReturn = null;


				if(repaymentFrequency.getMeetingDetails().getRecurrenceType().getRecurrenceId()==RepaymentScheduleConstansts.RECCURENCE_WEEKLY)
				{
					meetingRecToReturn = handleDayOfWeek(startDate);

				}
				else
					meetingRecToReturn = handleMonth(startDate);




				MeetingDetails meetingDetailsToReturn = new MeetingDetails();
				meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
				meetingDetailsToReturn.setRecurAfter(repaymentFrequency.getMeetingDetails().getRecurAfter());
				meetingDetailsToReturn.setRecurrenceType(repaymentFrequency.getMeetingDetails().getRecurrenceType());

				meetingToReturn.setMeetingDetails(meetingDetailsToReturn);

				return meetingToReturn;

	}

	private static MeetingRecurrence handleDayOfWeek(Date startDate)
	{
		MeetingRecurrence  meetingRecToReturn = new MeetingRecurrence();

		Calendar c = new GregorianCalendar();
		c.setTime(startDate);

		int dayOfWeek = getWeekDay(c.get(Calendar.DAY_OF_WEEK));
		meetingRecToReturn.setWeekDay(new Short(new Integer(dayOfWeek).shortValue()));

		return meetingRecToReturn;
	}

	private static int getWeekDay(int weekDay)
	{
		if(weekDay == Calendar.SUNDAY)
			return 1;
		if(weekDay == Calendar.MONDAY)
			return 2;
		if(weekDay == Calendar.TUESDAY)
			return 3;
		if(weekDay == Calendar.WEDNESDAY)
			return 4;
		if(weekDay == Calendar.THURSDAY)
			return 5;
		if(weekDay == Calendar.FRIDAY)
			return 6;
		if(weekDay == Calendar.SATURDAY)
			return 7;

		return 1;

	}

	private static MeetingRecurrence handleMonth(Date startDate)
	{
		MeetingRecurrence  meetingRecToReturn = new MeetingRecurrence();

		Calendar c = new GregorianCalendar();
		c.setTime(startDate);

		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		meetingRecToReturn.setDayNumber(new Short(new Integer(dayOfMonth).shortValue()));

		return meetingRecToReturn;
	}

	// the following below methods will get removed after configuration is implemented
    private static List getWeekOffList()
    {
		// need to get this from configuration
		List weekOffList =  new ArrayList();

		return weekOffList;

	}

    private static List getHolidayList()
    {
		// need to get this from configuration
		return new ArrayList();


	}

    private static int getHolidayConfig()
    {
		// need to get this from configuration
		return Constants.NEXT_SCH_DATE;

	}

	// to remove after implementing configuration
	static List getHolidayList(String value){
		  java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
			List holidayList=new ArrayList();
			java.util.StringTokenizer st= new java.util.StringTokenizer(value,",");
			String str;
			java.util.Date dt;
			while(st.hasMoreTokens()){
				str=st.nextToken();
				try{
					dt=df.parse(str);
					holidayList.add(dt);
				}catch(java.text.ParseException pe){

				}
			}
			return holidayList;
	}
	
	private static SchedulerIntf getScheduler(ScheduleDataIntf scheduleData,
			MeetingBO meeting) throws RepaymentScheduleException,
			SchedulerException {
		SchedulerIntf scheduler;
		ScheduleInputsIntf scheduleInputs;
		scheduler = SchedulerFactory.getScheduler();
		scheduleInputs = SchedulerFactory.getScheduleInputs();
		scheduleInputs.setStartDate(meeting.getMeetingStartDate().getTime());
		scheduleData.setRecurAfter(meeting.getMeetingDetails().getRecurAfter()
				.intValue());
		if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.DayData")) {
			throw new RepaymentScheduleException(
					RepaymentScheduleConstansts.NOT_SUPPORTED_FREQUENCY_TYPE);
		} else if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.WeekData")) {
			MifosLogManager
					.getLogger(LoggerConstants.REPAYMENTSCHEDULAR)
					.debug(
							"MeetingSchedulerHelper:getScheduler  returning week object ");
			scheduleData.setWeekDay(meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getWeekDayId()
					.intValue());
		} else if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.MonthData")) {
			MifosLogManager
					.getLogger(LoggerConstants.REPAYMENTSCHEDULAR)
					.info(
							"MeetingSchedulerHelper:getScheduler  returning month object ");
			Short recurrenceId = meeting.getMeetingDetails()
					.getRecurrenceType().getRecurrenceId();
			if (meeting.getMeetingDetails().getMeetingRecurrence()
					.getDayNumber() != null)
				scheduleData.setDayNumber(meeting.getMeetingDetails()
						.getMeetingRecurrence().getDayNumber().intValue());
			else {
				scheduleData.setWeekDay(meeting.getMeetingDetails()
						.getMeetingRecurrence().getWeekDay().getWeekDayId()
						.intValue());
				scheduleData.setWeekRank(meeting.getMeetingDetails()
						.getMeetingRecurrence().getRankOfDays()
						.getRankOfDayId().intValue());
			}
		} else
			throw new RepaymentScheduleException(
					RepaymentScheduleConstansts.NOT_SUPPORTED_FREQUENCY_TYPE);

		scheduleInputs.setScheduleData(scheduleData);
		scheduler.setScheduleInputs(scheduleInputs);
		return scheduler;
	}

	public static SchedulerIntf getSchedulerObject(MeetingBO meeting)
			throws RepaymentScheduleException, SchedulerException {
		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType()
				.getRecurrenceId();
		try {
			ScheduleDataIntf scheduleData;
			scheduleData = SchedulerFactory
					.getScheduleData(getReccurence(recurrenceId));
			return getScheduler(scheduleData, meeting);
		} catch (SchedulerException scheduleException) {
			throw scheduleException;
		}
	}

}

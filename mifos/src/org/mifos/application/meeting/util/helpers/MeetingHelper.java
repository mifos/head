/**
 *
 */
package org.mifos.application.meeting.util.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.framework.components.scheduler.Constants;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.ScheduleInputsIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;

public class MeetingHelper {

	public MeetingHelper() {
		super();
	}

	/**
	 * This function returns the meeting object based on the passed values
	 * 
	 * @param frequency
	 * @param recurAfter
	 * @param meetingTypeId
	 * @return
	 */
	public static Meeting geMeeting(String frequency, String recurAfter,
			Short meetingTypeId) {

		Meeting meeting = new Meeting();
		try {
			MeetingType meetingType = new MeetingType();
			meetingType.setMeetingTypeId(meetingTypeId);
			meeting.setMeetingType(meetingType);

			MeetingDetails meetingDetails = new MeetingDetails();
			Short frequencyId = null;
			if (null != frequency) {
				frequencyId = Short.valueOf(frequency);
			}
			if (null != recurAfter) {
				meetingDetails.setRecurAfter(Short.valueOf(recurAfter));
			}

			RecurrenceType recurrenceType = new RecurrenceType();
			recurrenceType.setRecurrenceId(frequencyId);

			meetingDetails.setRecurrenceType(recurrenceType);
			meetingDetails.setMeetingRecurrence(new MeetingRecurrence());

			meeting.setMeetingDetails(meetingDetails);
			meeting.setMeetingPlace("Loan Meeting Place");
		} catch (Exception e) {
			// TODO change this
			e.printStackTrace();
		}
		return meeting;
	}
	
	public static SchedulerIntf getSchedulerObject(MeetingBO meeting)
			throws SchedulerException {
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
	

	public static SchedulerIntf getSchedulerObject(MeetingBO meeting,
			boolean holidayWeekOffRequired) throws SchedulerException {
		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType()
				.getRecurrenceId();
		try {

			ScheduleDataIntf scheduleData;

			scheduleData = SchedulerFactory
					.getScheduleData(getReccurence(recurrenceId));

			return getScheduler(scheduleData, meeting, holidayWeekOffRequired);
		} catch (SchedulerException scheduleException) {
			throw scheduleException;
		}

	}

	public static String getReccurence(Short recurrenceId) {
		if (recurrenceId.intValue() == 1)
			return Constants.WEEK;

		if (recurrenceId.intValue() == 2)
			return Constants.MONTH;

		if (recurrenceId.intValue() == 3)
			return Constants.MONTH;

		return "";

	}

	private static SchedulerIntf getScheduler(ScheduleDataIntf scheduleData,
			MeetingBO meeting, boolean holidayWeekOffRequired)
			throws SchedulerException {

		SchedulerIntf scheduler;
		ScheduleInputsIntf scheduleInputs;

		scheduler = SchedulerFactory.getScheduler();

		if (holidayWeekOffRequired) {
			scheduler.setWeekOffList(getWeekOffList());
			scheduler.setHolidayList(getHolidayList());
			scheduler.setScheduleHolidayOption(getHolidayConfig());
		}

		scheduleInputs = SchedulerFactory.getScheduleInputs();
		scheduleInputs.setStartDate(meeting.getMeetingStartDate().getTime());

		scheduleData.setRecurAfter(meeting.getMeetingDetails().getRecurAfter()
				.intValue());

		if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.DayData")) {
			throw new SchedulerException(
					MeetingConstants.NOT_SUPPORTED_FREQUENCY_TYPE);

		} else if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.WeekData")) {

			scheduleData.setWeekDay(meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getId().intValue());

		} else if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.MonthData")) {

			Short recurrenceId = meeting.getMeetingDetails()
					.getRecurrenceType().getRecurrenceId();
			if (meeting.getMeetingDetails().getMeetingRecurrence()
					.getDayNumber() != null)
				scheduleData.setDayNumber(meeting.getMeetingDetails()
						.getMeetingRecurrence().getDayNumber().intValue());
			else {
				scheduleData
						.setWeekDay(meeting.getMeetingDetails()
								.getMeetingRecurrence().getWeekDay().getId()
								.intValue());
				scheduleData.setWeekRank(meeting.getMeetingDetails()
						.getMeetingRecurrence().getRankOfDays().getId()
						.intValue());

			}

		} else
			throw new SchedulerException(
					MeetingConstants.NOT_SUPPORTED_FREQUENCY_TYPE);

		scheduleInputs.setScheduleData(scheduleData);
		scheduler.setScheduleInputs(scheduleInputs);

		return scheduler;

	}

	private static List getWeekOffList() {
		// need to get this from configuration
		List weekOffList = new ArrayList();

		return weekOffList;

	}

	private static List getHolidayList() {
		// need to get this from configuration
		return new ArrayList();

	}

	private static int getHolidayConfig() {
		// need to get this from configuration
		return Constants.NEXT_SCH_DATE;

	}
	
	private static SchedulerIntf getScheduler(ScheduleDataIntf scheduleData,
			MeetingBO meeting) throws SchedulerException {
		SchedulerIntf scheduler;
		ScheduleInputsIntf scheduleInputs;
		scheduler = SchedulerFactory.getScheduler();
		scheduleInputs = SchedulerFactory.getScheduleInputs();
		scheduleInputs.setStartDate(meeting.getMeetingStartDate().getTime());
		scheduleData.setRecurAfter(meeting.getMeetingDetails().getRecurAfter()
				.intValue());
		if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.DayData")) {
			throw new SchedulerException(
					MeetingConstants.NOT_SUPPORTED_FREQUENCY_TYPE);
		} else if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.WeekData")) {
			scheduleData.setWeekDay(meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDayValue().getValue()
					.intValue());
		} else if (scheduleData.getClass().getName().equals(
				"org.mifos.framework.components.scheduler.MonthData")) {
			Short recurrenceId = meeting.getMeetingDetails()
					.getRecurrenceType().getRecurrenceId();
			if (meeting.getMeetingDetails().getMeetingRecurrence()
					.getDayNumber() != null)
				scheduleData.setDayNumber(meeting.getMeetingDetails()
						.getMeetingRecurrence().getDayNumber().intValue());
			else {
				scheduleData.setWeekDay(meeting.getMeetingDetails()
						.getMeetingRecurrence().getWeekDayValue().getValue()
						.intValue());
				scheduleData.setWeekRank(meeting.getMeetingDetails()
						.getMeetingRecurrence().getRankOfDays()
						.getId().intValue());
			}
		} else
			throw new SchedulerException(
					MeetingConstants.NOT_SUPPORTED_FREQUENCY_TYPE);

		scheduleInputs.setScheduleData(scheduleData);
		scheduler.setScheduleInputs(scheduleInputs);
		return scheduler;
	}


}

package org.mifos.framework.components.scheduler.helpers;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.scheduler.ScheduleDataIntf;
import org.mifos.framework.components.scheduler.ScheduleInputsIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerFactory;
import org.mifos.framework.components.scheduler.SchedulerIntf;

public class SchedulerHelper {
	
	public static  SchedulerIntf getScheduler(MeetingBO meeting)throws SchedulerException{
		Short recurrenceId = meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId();
		ScheduleDataIntf scheduleData;
		scheduleData=SchedulerFactory.getScheduleData(recurrenceId);
		return getScheduler(scheduleData , meeting);
	}
	
	public static SchedulerIntf getScheduler( ScheduleDataIntf scheduleData, MeetingBO meeting)throws SchedulerException{
		SchedulerIntf scheduler = SchedulerFactory.getScheduler();
		ScheduleInputsIntf scheduleInputs=SchedulerFactory.getScheduleInputs();
		scheduleInputs.setStartDate(meeting.getMeetingStartDate().getTime());
		scheduleData.setRecurAfter(meeting.getMeetingDetails().getRecurAfter().intValue());

		if(scheduleData.getClass().getName().equals("org.mifos.framework.components.scheduler.WeekData")){
			scheduleData.setWeekDay(meeting.getMeetingDetails().getMeetingRecurrence().getWeekDayValue().getValue());
		}
		else if(scheduleData.getClass().getName().equals("org.mifos.framework.components.scheduler.MonthData")){
			if(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber() != null)
				scheduleData.setDayNumber(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber().intValue());
			else{
				if(meeting.getMeetingDetails().getMeetingRecurrence().getWeekDayValue()!=null)
					scheduleData.setWeekDay(meeting.getMeetingDetails().getMeetingRecurrence().getWeekDayValue().getValue());
				if(meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays()!=null)
					scheduleData.setWeekRank(meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays().getId());
			}
		}
		scheduleInputs.setScheduleData(scheduleData);
		scheduler.setScheduleInputs(scheduleInputs);
		return scheduler;
	}
}

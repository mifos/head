/**
 *
 */
package org.mifos.application.meeting.util.helpers;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.StringUtils;

public class MeetingHelper {

	public MeetingHelper() {
		super();
	}

	public String getMessage(MeetingBO meeting, UserContext userContext) {
		String key;
		Object []args = new Object[3];
		initializeLocale(meeting, userContext.getLocaleId());
		if(meeting.isWeekly()){
			key = MeetingConstants.WEEK_SCHEDULE;
			args[0]=meeting.getMeetingDetails().getRecurAfter();
			WeekDay weekDay = meeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDayValue();
			args[1]=MessageLookup.getInstance().lookup(weekDay, userContext);
		}
		else if(meeting.isMonthlyOnDate()){
			key = MeetingConstants.MONTH_DAY_SCHEDULE;
			args[0]=meeting.getMeetingDetails().getDayNumber();
			args[1]=meeting.getMeetingDetails().getRecurAfter();
		}
		else{ 
			key = MeetingConstants.MONTH_SCHEDULE;
			args[0]=meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays().getName();
			args[1]=meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay().getName();
			args[2]=meeting.getMeetingDetails().getRecurAfter();
		}
		return StringUtils.getMessageWithSubstitution(MeetingConstants.MEETING_RESOURCE, userContext.getPreferredLocale(), key, args);
	}
	
	public String getMessageWithFrequency(MeetingBO meeting, UserContext userContext){
		String key = null;
		Object []args = new Object[1];
		if(meeting.isWeekly())
			key = MeetingConstants.WEEK_FREQUENCY;		
		else if(meeting.isMonthly())
			key = MeetingConstants.MONTH_FREQUENCY;
		args[0]=meeting.getMeetingDetails().getRecurAfter();
		
		return StringUtils.getMessageWithSubstitution(MeetingConstants.MEETING_RESOURCE, userContext.getPreferredLocale(), key, args);
	}
	
	public String getDetailMessageWithFrequency(MeetingBO meeting, UserContext userContext){
		String key = null;
		Object []args = new Object[1];
		if(meeting.isWeekly())
			key = MeetingConstants.WEEK_SCHEDULE_SHORT;		
		else if(meeting.isMonthly())
			key = MeetingConstants.MONTH_SCHEDULE_SHORT;
		args[0]=meeting.getMeetingDetails().getRecurAfter();
		
		return StringUtils.getMessageWithSubstitution(MeetingConstants.MEETING_RESOURCE, userContext.getPreferredLocale(), key, args);
	}
	
	private void initializeLocale(MeetingBO meeting, Short localeId){
		if(meeting.isWeekly())
			meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay().setLocaleId(localeId);
		else if(meeting.isMonthly() && !meeting.isMonthlyOnDate()){
			meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay().setLocaleId(localeId);
			meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays().setLocaleId(localeId);
		}
	}
}

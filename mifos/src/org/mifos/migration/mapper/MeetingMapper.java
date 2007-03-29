package org.mifos.migration.mapper;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.migration.generated.MonthlyMeeting;
import org.mifos.migration.generated.WeekDayChoice;
import org.mifos.migration.generated.WeekDayOccurence;
import org.mifos.migration.generated.WeeklyMeeting;

public class MeetingMapper {

	public static WeekDay mapWeekDayChoiceToWeekDay(WeekDayChoice weekDayChoice) {
		return WeekDay.valueOf(weekDayChoice.toString());
	}
	
	public static WeekDayChoice mapWeekDayToWeekDayChoice(WeekDay weekDay) {
		return WeekDayChoice.valueOf(weekDay.toString());
	}
	
	public static RankType mapWeekDayOccurenceToRankType(WeekDayOccurence weekDayOccurence) {
		return RankType.valueOf(weekDayOccurence.toString());
	}
	
	public static WeekDayOccurence mapRankTypeToWeekDayOccurence(RankType rank) {
		return WeekDayOccurence.valueOf(rank.toString());
	}
	
	public static MeetingBO mapMonthlyMeetingToMeetingBO(MonthlyMeeting meeting) {
		short monthsBetweenMeetings = meeting.getMonthsBetweenMeetings();
		Date startDate = new Date();
		MeetingType meetingType = MeetingType.CUSTOMER_MEETING;
		String location = meeting.getLocation();
	
		MeetingBO newMeeting = null;
		
		try {
			if (meeting.getDayOfMonth() != null) {
				// monthly frequency is implicit in the constructor
				newMeeting = new MeetingBO(
						meeting.getDayOfMonth(),
						monthsBetweenMeetings,
						startDate,
						meetingType,
						location);					
			} else {
				// monthly frequency is implicit in the constructor
				newMeeting = new MeetingBO(
						mapWeekDayChoiceToWeekDay(meeting.getMeetingWeekDay()),
						mapWeekDayOccurenceToRankType(meeting.getMeetingWeekDayOccurence()),
						monthsBetweenMeetings,
						startDate,
						meetingType,
						location);					
			}
		} catch (MeetingException me) {
			throw new RuntimeException(me);
		}	
		
		return newMeeting;
	}

	public static MeetingBO mapWeeklyMeetingToMeetingBO(WeeklyMeeting meeting) {
		short weeksBetweenMeetings = meeting.getWeeksBetweenMeetings();
		Date startDate = new Date();
		MeetingType meetingType = MeetingType.CUSTOMER_MEETING;
		String location = meeting.getLocation();
	
		MeetingBO newMeeting = null;
		
		try {
			// weekly frequency is implicit in the constructor
			newMeeting = new MeetingBO(
					mapWeekDayChoiceToWeekDay(meeting.getMeetingWeekDay()),
					weeksBetweenMeetings,
					startDate,
					meetingType,
					location);					
		} catch (MeetingException me) {
			throw new RuntimeException(me);
		}	
		
		return newMeeting;
	}
	
	public static MonthlyMeeting mapMeetingBOToMonthlyMeeting(MeetingBO meeting) {
		if (!meeting.isMonthly()) {
			throw new IllegalArgumentException("Meeting required to be monthly but was not.");
		}
		if (meeting.getMeetingTypeEnum() != MeetingType.CUSTOMER_MEETING) {
			throw new IllegalArgumentException("Meeting required to be a CUSTOMER_MEETING but was not.");
		}
		short monthsBetweenMeetings = meeting.getMeetingDetails().getRecurAfter();
		// start date is ignored
		String location = meeting.getMeetingPlace();
	
		MonthlyMeeting newMeeting = new MonthlyMeeting();
		newMeeting.setLocation(location);
		newMeeting.setMonthsBetweenMeetings(monthsBetweenMeetings);
		
		MeetingDetailsEntity meetingDetails = meeting.getMeetingDetails();

		if (meetingDetails.isMonthlyOnDate()) {
			newMeeting.setDayOfMonth(meetingDetails.getDayNumber());
		} else {
			newMeeting.setMeetingWeekDay(MeetingMapper.mapWeekDayToWeekDayChoice(meetingDetails.getWeekDay()));
			newMeeting.setMeetingWeekDayOccurence(MeetingMapper.mapRankTypeToWeekDayOccurence(meetingDetails.getWeekRank()));
		}
		
		return newMeeting;
	}

	public static WeeklyMeeting mapMeetingBOToWeeklyMeeting(MeetingBO meeting) {
		if (!meeting.isWeekly()) {
			throw new IllegalArgumentException("Meeting required to be weekly but was not.");
		}
		if (meeting.getMeetingTypeEnum() != MeetingType.CUSTOMER_MEETING) {
			throw new IllegalArgumentException("Meeting required to be a CUSTOMER_MEETING but was not.");
		}
		short weeksBetweenMeetings = meeting.getMeetingDetails().getRecurAfter();
		// start date is ignored
		String location = meeting.getMeetingPlace();
	
		WeeklyMeeting newMeeting = new WeeklyMeeting();
		newMeeting.setLocation(location);
		newMeeting.setWeeksBetweenMeetings(weeksBetweenMeetings);
		
		newMeeting.setMeetingWeekDay(mapWeekDayToWeekDayChoice(meeting.getMeetingDetails().getWeekDay()));
				
		return newMeeting;
	}

}

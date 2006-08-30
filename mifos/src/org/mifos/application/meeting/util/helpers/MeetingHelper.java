/**
 * 
 */
package org.mifos.application.meeting.util.helpers;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.MeetingRecurrenceEntity;
import org.mifos.application.meeting.business.MeetingTypeEntity;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;

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

	public static Meeting convertMeetingM2toM1(MeetingBO M2meeting) {
		Meeting meetingToReturn = new Meeting();
		meetingToReturn.setMeetingStartDate(M2meeting.getMeetingStartDate());
		meetingToReturn.setMeetingPlace(M2meeting.getMeetingPlace());
		MeetingType meetingType = new MeetingType();
		meetingType.setMeetingTypeId(M2meeting.getMeetingType()
				.getMeetingTypeId());
		meetingToReturn.setMeetingType(meetingType);

		MeetingRecurrence meetingRecToReturn = new MeetingRecurrence();
		meetingRecToReturn.setDayNumber(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays()!=null){
			meetingRecToReturn.setRankOfDays(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getRankOfDays().getRankOfDayId());
		}
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay()!=null){
			meetingRecToReturn.setWeekDay(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getWeekDayId());
		}

		MeetingDetails meetingDetailsToReturn = new MeetingDetails();
		meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
		meetingDetailsToReturn.setRecurAfter(M2meeting.getMeetingDetails()
				.getRecurAfter());

		RecurrenceType recurrenceType = new RecurrenceType();
		recurrenceType.setRecurrenceId(M2meeting.getMeetingDetails()
				.getRecurrenceType().getRecurrenceId());

		meetingDetailsToReturn.setRecurrenceType(recurrenceType);

		meetingToReturn.setMeetingDetails(meetingDetailsToReturn);

		return meetingToReturn;

	}
	
	public static MeetingBO convertMeetingM1oM2(Meeting meeting) {
		MeetingBO meetingToReturn = new MeetingBO();
		
		meetingToReturn.setMeetingStartDate(meeting.getMeetingStartDate());
		meetingToReturn.setMeetingPlace(meeting.getMeetingPlace());
		
		MeetingTypeEntity meetingType = new MeetingTypeEntity();
		meetingType.setMeetingTypeId(meeting.getMeetingType().getMeetingTypeId());
		meetingToReturn.setMeetingType(meetingType);

		MeetingRecurrenceEntity meetingRecToReturn = new MeetingRecurrenceEntity();
		meetingRecToReturn.setDayNumber(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber());
		if(meeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays()!=null){
			RankOfDaysEntity rankOfDays = new RankOfDaysEntity();
			rankOfDays.setRankOfDayId(meeting.getMeetingDetails()
					.getMeetingRecurrence().getRankOfDays());
			meetingRecToReturn.setRankOfDays(rankOfDays);
		}
		
		if(meeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay()!=null){
			WeekDaysEntity weekDay = new WeekDaysEntity();
			weekDay.setWeekDayId(meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay());
			meetingRecToReturn.setWeekDay(weekDay);
		}

		MeetingDetailsEntity meetingDetailsToReturn = new MeetingDetailsEntity();
		meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
		meetingDetailsToReturn.setRecurAfter(meeting.getMeetingDetails()
				.getRecurAfter());

		RecurrenceTypeEntity recurrenceType = new RecurrenceTypeEntity();
		recurrenceType.setRecurrenceId(meeting.getMeetingDetails()
				.getRecurrenceType().getRecurrenceId());

		meetingDetailsToReturn.setRecurrenceType(recurrenceType);

		meetingToReturn.setMeetingDetails(meetingDetailsToReturn);

		return meetingToReturn;

	}
}

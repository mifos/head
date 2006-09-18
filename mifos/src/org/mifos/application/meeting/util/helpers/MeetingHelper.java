/**
 * 
 */
package org.mifos.application.meeting.util.helpers;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
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
		
		if(M2meeting.getMeetingType()!=null){
			MeetingType meetingType = new MeetingType();
			meetingType.setMeetingTypeId(M2meeting.getMeetingType()
					.getMeetingTypeId());
			meetingToReturn.setMeetingType(meetingType);
		}

		MeetingRecurrence meetingRecToReturn = new MeetingRecurrence();
		meetingRecToReturn.setDayNumber(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays()!=null){
			meetingRecToReturn.setRankOfDays(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getRankOfDays().getId());
		}
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDayValue()!=null){
			meetingRecToReturn.setWeekDay(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDayValue().getValue());
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
	
	//TODO: remove following method when integrating meeting with modules
	public static MeetingBO convertMeetingM1oM2(Meeting meeting){
		MeetingBO meetingToReturn = null;
		org.mifos.application.meeting.util.helpers.MeetingType meetingType = org.mifos.application.meeting.util.helpers.MeetingType.getMeetingType(meeting.getMeetingType().getMeetingTypeId());
		org.mifos.application.meeting.util.helpers.RecurrenceType recurrenceType = org.mifos.application.meeting.util.helpers.RecurrenceType.getRecurrenceType(meeting.getMeetingDetails().getRecurrenceType().getRecurrenceId());
		Short recurAfter = meeting.getMeetingDetails().getRecurAfter();
		try{
			if(recurrenceType.equals(org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY)){
				if(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber()!=null)
					meetingToReturn = new MeetingBO(meeting.getMeetingDetails().getMeetingRecurrence().getDayNumber(),
							recurAfter, meeting.getMeetingStartDate().getTime(), meetingType, meeting.getMeetingPlace());
				else
					meetingToReturn = new MeetingBO(WeekDay.getWeekDay(meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay()),RankType.getRankType(meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays()),
							recurAfter, meeting.getMeetingStartDate().getTime(), meetingType, meeting.getMeetingPlace());
			}				
			else if(recurrenceType.equals(org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY))
				meetingToReturn = new MeetingBO(WeekDay.getWeekDay(meeting.getMeetingDetails()
						.getMeetingRecurrence().getWeekDay()), recurAfter, meeting.getMeetingStartDate().getTime(), meetingType, meeting.getMeetingPlace() );
			else
				meetingToReturn = new MeetingBO(recurrenceType, recurAfter, meeting.getMeetingStartDate().getTime(), meetingType);
		}catch(MeetingException me){}
		meetingToReturn.setMeetingPlace(meeting.getMeetingPlace());
		return meetingToReturn;
	}
}

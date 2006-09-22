/**
 *
 */
package org.mifos.application.meeting.util.helpers;

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

	

}

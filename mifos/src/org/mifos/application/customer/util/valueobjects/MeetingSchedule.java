/**
 *
 */
package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class MeetingSchedule extends ValueObject {
	private String locationOfMeeting;

	/**
	 * Method which returns the locationOfMeeting
	 * @return Returns the locationOfMeeting.
	 */
	public String getLocationOfMeeting() {
		return locationOfMeeting;
	}

	/**
	 * Method which sets the locationOfMeeting
	 * @param locationOfMeeting The locationOfMeeting to set.
	 */
	public void setLocationOfMeeting(String locationOfMeeting) {
		this.locationOfMeeting = locationOfMeeting;
	}

	public String getMeetingSchedule(){
		// System.out.println("---------------------- In meeting schedule method");
		return "Recur Every week on Wednesday at 11:00 am";
	}
}

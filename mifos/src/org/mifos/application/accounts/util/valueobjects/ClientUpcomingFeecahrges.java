/**
 * 
 */
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author mohammedn
 *
 */
public class ClientUpcomingFeecahrges extends ValueObject {

	/**
	 * 
	 */
	public ClientUpcomingFeecahrges() {
	}
	
	public ClientUpcomingFeecahrges(String feeName,Money amount) {
		this.feeName=feeName;
		this.amount=amount;
	}
	
	public ClientUpcomingFeecahrges(String feeName,Money amount,Meeting meeting) {
		this.feeName=feeName;
		this.amount=amount;
		this.meeting=meeting;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3543265475761L;
	
	private String feeName;
	
	private Money amount;
	
	private Meeting meeting;

	
	/**
	 * @return Returns the meetingId.
	 */
	public Meeting getMeeting() {
		return meeting;
	}

	/**
	 * @param meetingId The meetingId to set.
	 */
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	/**
	 * @return Returns the amount.
	 */
	public Money getAmount() {
		return amount;
	}

	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(Money amount) {
		this.amount = amount;
	}

	/**
	 * @return Returns the feeName.
	 */
	public String getFeeName() {
		return feeName;
	}

	/**
	 * @param feeName The feeName to set.
	 */
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	
	
}

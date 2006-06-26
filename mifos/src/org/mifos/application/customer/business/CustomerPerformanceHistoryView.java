package org.mifos.application.customer.business;

public class CustomerPerformanceHistoryView {
	
	private Integer meetingsAttended;
	private Integer meetingsMissed;
	private String lastLoanAmount;
	
	public Integer getMeetingsAttended() {
		return meetingsAttended;
	}
	public void setMeetingsAttended(Integer meetingsAttended) {
		this.meetingsAttended = meetingsAttended;
	}
	public Integer getMeetingsMissed() {
		return meetingsMissed;
	}
	public void setMeetingsMissed(Integer meetingsMissed) {
		this.meetingsMissed = meetingsMissed;
	}
	public String getLastLoanAmount() {
		return lastLoanAmount;
	}
	public void setLastLoanAmount(String lastLoanAmount) {
		this.lastLoanAmount = lastLoanAmount;
	}
	

}

package org.mifos.application.fees.business;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class FeeView extends View{
	private Short feeId;
	private String feeName;
	private Double amount;
	private boolean periodic;
	private MeetingBO feeSchedule;
	private Short feeRemoved;
	
	public FeeView(Short feeId, String feeName, Double amount, boolean periodic, MeetingBO feeSchedule){
		this.feeId = feeId;
		this.feeName = feeName;
		this.amount = amount;
		this.periodic = periodic;
		this.feeSchedule = feeSchedule;
		this.feeRemoved = YesNoFlag.NO.getValue();
	}
	
	public Money getAmount() {
		return new Money(amount.toString());
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public boolean isPeriodic() {
		return periodic;
	}
	
	public void setPeriodic(boolean periodic) {
		this.periodic = periodic;
	}
	
	public Short getFeeId() {
		return feeId;
	}
	
	public String getFeeName() {
		return feeName;
	}
	
	public MeetingBO getFeeSchedule() {
		return feeSchedule;
	}
	
	public void setFeeRemoved(Short feeRemoved) {
		this.feeRemoved = feeRemoved;
	}
	
	public boolean isFeeRemoved(){
		return feeRemoved.equals(YesNoFlag.YES.getValue());
	}
}

package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

public class FeeView extends View{
	private String feeId;
	private String feeName;
	private String amount;
	private boolean periodic;
	private String feeSchedule;
	private Short feeRemoved;
	private String feeFormula;
	private Short localeId;

	public FeeView(){}
	
	public FeeView(FeeBO fee){
		this.feeId = fee.getFeeId().toString();
		this.feeName = fee.getFeeName();
		if(fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
			this.amount = ((AmountFeeBO)fee).getFeeAmount().toString();
			this.feeFormula = "";
		}
		else { 
			this.amount = ((RateFeeBO)fee).getRate().toString();
			this.feeFormula = ((RateFeeBO)fee).getFeeFormula().getFormulaString(localeId);
		}
		this.periodic = fee.isPeriodic();
		if(fee.isPeriodic())
			this.feeSchedule = fee.getFeeFrequency().getFeeMeetingFrequency().getShortMeetingSchedule();
		this.feeRemoved = YesNoFlag.NO.getValue();
	}
	
	public FeeView(FeeBO fee,Short localeId){
		this.feeId = fee.getFeeId().toString();
		this.feeName = fee.getFeeName();
		if(fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
			this.amount = ((AmountFeeBO)fee).getFeeAmount().toString();
			this.feeFormula = "";
		}
		else { 
			this.amount = ((RateFeeBO)fee).getRate().toString();
			this.feeFormula = ((RateFeeBO)fee).getFeeFormula().getFormulaString(localeId);
		}
		this.periodic = fee.isPeriodic();
		if(fee.isPeriodic())
			this.feeSchedule = fee.getFeeFrequency().getFeeMeetingFrequency().getShortMeetingSchedule();
		this.feeRemoved = YesNoFlag.NO.getValue();
	}
	
	public String getFeeSchedule() {
		return feeSchedule;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public Money getAmountMoney() {
		return new Money(amount);
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public boolean isPeriodic() {
		return periodic;
	}
	
	public String getFeeId() {
		return feeId;
	}
		
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}
	
	public String getFeeName() {
		return feeName;
	}
	
	public Short getFeeRemoved() {
		return feeRemoved;
	}
	
	public void setFeeRemoved(Short feeRemoved) {
		this.feeRemoved = feeRemoved;
	}
	
	public boolean isRemoved(){
		return feeRemoved.equals(YesNoFlag.YES.getValue());
	}	
	
	public Short getFeeIdValue() {
		return StringUtils.isNullAndEmptySafe(feeId) ? Short.valueOf(feeId) : null;
	}
	
	public Double getAmountDoubleValue() {
		return new Money(amount).getAmountDoubleValue();
	}

	public String getFeeFormula() {
		return feeFormula;
	}

	public Short getLocaleId() {
		return localeId;
	}
	
	
}

package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.View;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.LocalizationConverter;

public class FeeView extends View {
	private String feeId;

	private String feeName;

	private String amount;

	private boolean periodic;

	private String feeSchedule;

	private Short feeRemoved;

	private String feeFormula;

	private Short localeId;

	private RecurrenceType frequencyType;

	public FeeView() {
	}

	public FeeView(UserContext userContext, FeeBO fee) {
		if (userContext != null)
			localeId = userContext.getLocaleId();
		this.feeId = fee.getFeeId().toString();
		this.feeName = fee.getFeeName();
		if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
			this.amount = ((AmountFeeBO) fee).getFeeAmount().toString();
			this.feeFormula = "";
		} else {
			this.amount = ((RateFeeBO) fee).getRate().toString();
			this.feeFormula = ((RateFeeBO) fee).getFeeFormula()
					.getFormulaString(localeId);
		}
		this.periodic = fee.isPeriodic();
		if (fee.isPeriodic()) {
			MeetingBO feeMeeting = fee.getFeeFrequency()
					.getFeeMeetingFrequency();
			this.feeSchedule = new MeetingHelper().getMessageWithFrequency(
					feeMeeting, userContext);
			if (feeMeeting.isMonthly())
				this.frequencyType = RecurrenceType.MONTHLY;
			else if (feeMeeting.isWeekly())
				this.frequencyType = RecurrenceType.WEEKLY;
			else
				this.frequencyType = RecurrenceType.DAILY;
		}
		this.feeRemoved = YesNoFlag.NO.getValue();
	}

	public String getFeeSchedule() {
		return feeSchedule;
	}

	public String getAmount() {
		return amount;
	}

	public Double getAmountMoney() {
		
		return LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(amount);
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

	public boolean isRemoved() {
		return feeRemoved.equals(YesNoFlag.YES.getValue());
	}

	public Short getFeeIdValue() {
		return StringUtils.isNullAndEmptySafe(feeId) ? Short.valueOf(feeId)
				: null;
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

	public RecurrenceType getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(RecurrenceType frequencyType) {
		this.frequencyType = frequencyType;
	}

}

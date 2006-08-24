package org.mifos.application.customer.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Money;

public class CustHistoricalDataActionForm extends BaseActionForm {
	
	private String mfiJoiningDate;

	private String productName;

	private String loanAmount;

	private String totalAmountPaid;

	private String interestPaid;

	private String missedPaymentsCount;

	private String totalPaymentsCount;

	private String commentNotes;

	private String loanCycleNumber;

	private String type;

	public String getCommentNotes() {
		return commentNotes;
	}

	public void setCommentNotes(String commentNotes) {
		this.commentNotes = commentNotes;
	}

	public String getInterestPaid() {
		return interestPaid;
	}

	public Money getInterestPaidValue() {
		return getMoney(getInterestPaid());
	}

	public void setInterestPaid(String interestPaid) {
		this.interestPaid = interestPaid;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public Money getLoanAmountValue() {
		return getMoney(getLoanAmount());
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getLoanCycleNumber() {
		return loanCycleNumber;
	}

	public Integer getLoanCycleNumberValue() {
		return getIntegerValue(getLoanCycleNumber());
	}

	public void setLoanCycleNumber(String loanCycleNumber) {
		this.loanCycleNumber = loanCycleNumber;
	}

	public String getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	public void setMfiJoiningDate(String mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}

	public String getMissedPaymentsCount() {
		return missedPaymentsCount;
	}

	public Integer getMissedPaymentsCountValue() {
		return getIntegerValue(getMissedPaymentsCount());
	}

	public void setMissedPaymentsCount(String missedPaymentsCount) {
		this.missedPaymentsCount = missedPaymentsCount;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTotalAmountPaid() {
		return totalAmountPaid;
	}

	public Money getTotalAmountPaidValue() {
		return getMoney(getTotalAmountPaid());
	}

	public void setTotalAmountPaid(String totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public String getTotalPaymentsCount() {
		return totalPaymentsCount;
	}

	public Integer getTotalPaymentsCountValue() {
		return getIntegerValue(getTotalPaymentsCount());
	}

	public void setTotalPaymentsCount(String totalPaymentsCount) {
		this.totalPaymentsCount = totalPaymentsCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

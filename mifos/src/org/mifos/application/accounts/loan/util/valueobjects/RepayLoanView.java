package org.mifos.application.accounts.loan.util.valueobjects;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class RepayLoanView extends View {
	
	private Money totalAmount;

	private String recieptNum;

	private Date recieptDate;

	private Short paymentTypeId;

	private Short personnelId;
	
	private Integer customerId;
	
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Short getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(Short paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public Date getRecieptDate() {
		return recieptDate;
	}

	public void setRecieptDate(Date recieptDate) {
		this.recieptDate = recieptDate;
	}

	public String getRecieptNum() {
		return recieptNum;
	}

	public void setRecieptNum(String recieptNum) {
		this.recieptNum = recieptNum;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	

}

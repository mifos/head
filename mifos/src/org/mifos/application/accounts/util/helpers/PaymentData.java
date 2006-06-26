package org.mifos.application.accounts.util.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.framework.util.helpers.Money;

public class PaymentData {

	private Money totalAmount;

	private Integer customerId;

	private Short personnelId;

	private Date transactionDate;

	private String recieptNum;

	private Date recieptDate;

	private Short paymentTypeId;

	private List<AccountPaymentData> accountPayments;

	public PaymentData(Money totalAmount, Short personnelId,Short paymentId,
			Date transactionDate) {
		accountPayments = new ArrayList<AccountPaymentData>();
		setTotalAmount(totalAmount);
		setPersonnelId(personnelId);
		setPaymentTypeId(paymentId);
		setTransactionDate(transactionDate);
	}

	public List<AccountPaymentData> getAccountPayments() {
		return accountPayments;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public Short getPaymentTypeId() {
		return paymentTypeId;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public Date getRecieptDate() {
		return recieptDate;
	}

	public String getRecieptNum() {
		return recieptNum;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	private void setAccountPayments(List<AccountPaymentData> accountPayments) {
		this.accountPayments = accountPayments;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	private void setPaymentTypeId(Short paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	private void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public void setRecieptDate(Date recieptDate) {
		this.recieptDate = recieptDate;
	}

	public void setRecieptNum(String recieptNum) {
		this.recieptNum = recieptNum;
	}

	private void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	private void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public void addAccountPaymentData(AccountPaymentData accountPaymentData) {
		accountPayments.add(accountPaymentData);
	}

}

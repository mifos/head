package org.mifos.application.accounts.business;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.business.View;
import org.mifos.framework.struts.tags.DateHelper;

public class TransactionHistoryView extends View {
	private Date transactionDate;

	private Integer paymentId;

	private Integer accountTrxnId;

	private String type;

	private String glcode;

	private String debit = "-";

	private String credit = "-";

	private String balance;

	private String clientName = "-";

	private Date postedDate;

	private String postedBy;

	private String notes = "-";

	private Locale locale = null;

	private String userPrefferedTransactionDate = null;

	private String userPrefferedPostedDate = null;

	public Integer getAccountTrxnId() {
		return accountTrxnId;
	}

	public void setAccountTrxnId(Integer accountTrxnId) {
		this.accountTrxnId = accountTrxnId;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
	}

	public String getGlcode() {
		return glcode;
	}

	public void setGlcode(String glcode) {
		this.glcode = glcode;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserPrefferedPostedDate(String userPrefferedPostedDate) {
		this.userPrefferedPostedDate = userPrefferedPostedDate;
	}

	public String getUserPrefferedPostedDate() {
		return DateHelper.getUserLocaleDate(getLocale(), getPostedDate()
				.toString());
	}

	public String getUserPrefferedTransactionDate() {
		return DateHelper.getUserLocaleDate(getLocale(), getTransactionDate()
				.toString());
	}

	public void setFinancialEnteries(Integer trxnId, Date trxnDate,
			String actionType, String glCode, String debit, String credit,
			Date postedDate, String notes) {
		this.accountTrxnId = trxnId;
		this.transactionDate = trxnDate;
		this.type = actionType;
		this.glcode = glCode;
		this.debit = debit;
		this.credit = credit;
		this.postedDate = postedDate;
		this.notes = notes;
	}

	public void setAccountingEnteries(Integer paymentId, String balance,
			String clientName, String postedBy) {
		this.paymentId = paymentId;
		this.balance = balance;
		this.clientName = clientName;
		this.postedBy = postedBy;
	}
}

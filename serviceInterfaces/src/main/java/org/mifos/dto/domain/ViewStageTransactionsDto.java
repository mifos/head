package org.mifos.dto.domain;

import java.math.BigDecimal;
import java.util.Date;

public class ViewStageTransactionsDto {

	private int transactionNo;
	private Date transactionDate;
	private String transactionType;
	private String officeLevel;
	private String displayName;
	private String mainAccount;
	private String amountAction;
	private String subAccount;
	private BigDecimal transactionAmount;
	private String narration;
	private String fromOfficeId;
	private int transactionID;
	private String audit;



	public String getAudit() {
		return audit;
	}
	public void setAudit(String audit) {
		this.audit = audit;
	}
	public int getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(int transactionNo) {
		this.transactionNo = transactionNo;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getOfficeLevel() {
		return officeLevel;
	}
	public void setOfficeLevel(String officeLevel) {
		this.officeLevel = officeLevel;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getMainAccount() {
		return mainAccount;
	}
	public void setMainAccount(String mainAccount) {
		this.mainAccount = mainAccount;
	}
	public String getAmountAction() {
		return amountAction;
	}
	public void setAmountAction(String amountAction) {
		this.amountAction = amountAction;
	}
	public String getSubAccount() {
		return subAccount;
	}
	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}
	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getFromOfficeId() {
		return fromOfficeId;
	}
	public void setFromOfficeId(String fromOfficeId) {
		this.fromOfficeId = fromOfficeId;
	}
	public int getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}


}

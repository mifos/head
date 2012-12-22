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
	
	private int CpBptransactionNo;
	private String CpBpsubAccount;
	private String CpBptransactionType; 
	private BigDecimal CpBptransactionAmount;
	
	
	
	public int getTransactionNo() {
		return transactionNo;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public String getOfficeLevel() {
		return officeLevel;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getMainAccount() {
		return mainAccount;
	}
	public String getAmountAction() {
		return amountAction;
	}
	public String getSubAccount() {
		return subAccount;
	}
	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}
	public String getNarration() {
		return narration;
	}
	public String getFromOfficeId() {
		return fromOfficeId;
	}
	public int getTransactionID() {
		return transactionID;
	}
	public String getAudit() {
		return audit;
	}
	public String getCpBpsubAccount() {
		return CpBpsubAccount;
	}
	public String getCpBptransactionType() {
		return CpBptransactionType;
	}
	public BigDecimal getCpBptransactionAmount() {
		return CpBptransactionAmount;
	}
	public int getCpBptransactionNo() {
		return CpBptransactionNo;
	}
	public void setTransactionNo(int transactionNo) {
		this.transactionNo = transactionNo;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public void setOfficeLevel(String officeLevel) {
		this.officeLevel = officeLevel;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setMainAccount(String mainAccount) {
		this.mainAccount = mainAccount;
	}
	public void setAmountAction(String amountAction) {
		this.amountAction = amountAction;
	}
	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}
	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public void setFromOfficeId(String fromOfficeId) {
		this.fromOfficeId = fromOfficeId;
	}
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}
	public void setAudit(String audit) {
		this.audit = audit;
	}
	public void setCpBpsubAccount(String cpBpsubAccount) {
		CpBpsubAccount = cpBpsubAccount;
	}
	public void setCpBptransactionType(String cpBptransactionType) {
		CpBptransactionType = cpBptransactionType;
	}
	public void setCpBptransactionAmount(BigDecimal cpBptransactionAmount) {
		CpBptransactionAmount = cpBptransactionAmount;
	}
	public void setCpBptransactionNo(int cpBptransactionNo) {
		CpBptransactionNo = cpBptransactionNo;
	}
	
	
}

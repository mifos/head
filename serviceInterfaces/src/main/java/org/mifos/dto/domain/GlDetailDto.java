package org.mifos.dto.domain;

import java.util.Date;

public class GlDetailDto {
	private int transactionNo;
	private int transactionID;
	private String chequeNo;
	private Date chequeDate;
	private String bankName;
	private String bankBranch;

	public int getTransactionNo() {
		return transactionNo;
	}
	public void setTransactionNo(int transactionNo) {
		this.transactionNo = transactionNo;
	}
	public int getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public Date getChequeDate() {
		Date dateValue=null;
		if (chequeDate != null) {
			dateValue = (Date) chequeDate.clone();
		}
		return dateValue;
		
	}
	public void setChequeDate(Date chequeDate) {
		Date dateValue=null;
		if (chequeDate != null) {
			dateValue = (Date) chequeDate.clone();
		}
		 this.chequeDate=dateValue;
		
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	
	
}

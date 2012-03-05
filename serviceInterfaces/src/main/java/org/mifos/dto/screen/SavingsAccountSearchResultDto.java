package org.mifos.dto.screen;

import java.io.Serializable;

@SuppressWarnings("PMD")
public class SavingsAccountSearchResultDto implements Serializable {
	
	private static final long serialVersionUID = 1985887616017223004L;
	
	private Short officeId;
    private String officeName;

    // ------------------------------------------------------------------------------

    private String clientName;
    private String clientGlobalCustNum;
    private String groupName;
    private String groupGlobalCustNum;
    private String centerName;
    private String centerGlobalCustNum;
    private String branchName;
    private short branchId;
    private short accountStatusId;
    private String loanOfficerName;
    private Short loanOfficerId;
    private String savingsGlobalAccountNum;
    private String status;
    
	public Short getOfficeId() {
		return officeId;
	}
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientGlobalCustNum() {
		return clientGlobalCustNum;
	}
	public void setClientGlobalCustNum(String clientGlobalCustNum) {
		this.clientGlobalCustNum = clientGlobalCustNum;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupGlobalCustNum() {
		return groupGlobalCustNum;
	}
	public void setGroupGlobalCustNum(String groupGlobalCustNum) {
		this.groupGlobalCustNum = groupGlobalCustNum;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getCenterGlobalCustNum() {
		return centerGlobalCustNum;
	}
	public void setCenterGlobalCustNum(String centerGlobalCustNum) {
		this.centerGlobalCustNum = centerGlobalCustNum;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public short getBranchId() {
		return branchId;
	}
	public void setBranchId(short branchId) {
		this.branchId = branchId;
	}
	public short getAccountStatusId() {
		return accountStatusId;
	}
	public void setAccountStatusId(short accountStatusId) {
		this.accountStatusId = accountStatusId;
	}
	public String getLoanOfficerName() {
		return loanOfficerName;
	}
	public void setLoanOfficerName(String loanOfficerName) {
		this.loanOfficerName = loanOfficerName;
	}
	public Short getLoanOfficerId() {
		return loanOfficerId;
	}
	public void setLoanOfficerId(Short loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}
	public String getSavingsGlobalAccountNum() {
		return savingsGlobalAccountNum;
	}
	public void setSavingsGlobalAccountNum(String savingsGlobalAccountNum) {
		this.savingsGlobalAccountNum = savingsGlobalAccountNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
}

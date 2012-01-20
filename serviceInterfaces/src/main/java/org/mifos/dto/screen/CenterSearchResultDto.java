package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CenterSearchResultDto implements Serializable {

    private Short officeId;
    private String officeName;

    // ------------------------------------------------------------------------------

    private String centerName;
    private String centerGlobalCustNum;
    private String branchName;
    private short branchId;
    private String loanOfficerName;
    private Short loanOfficerId;
    private short customerStatusId;
    private List<String> savingsGlobalAccountNum = new ArrayList<String>();
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
	public String getLoanOfficerName() {
		return loanOfficerName;
	}
	public void setLoanOfficerName(String loanOfficerName) {
		this.loanOfficerName = loanOfficerName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Short getLoanOfficerId() {
		return loanOfficerId;
	}
	public void setLoanOfficerId(Short loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}
	public short getCustomerStatusId() {
		return customerStatusId;
	}
	public void setCustomerStatusId(short customerStatusId) {
		this.customerStatusId = customerStatusId;
	}
	public List<String> getSavingsGlobalAccountNum() {
		return savingsGlobalAccountNum;
	}
	public void setSavingsGlobalAccountNum(List<String> savingsGlobalAccountNum) {
		this.savingsGlobalAccountNum = savingsGlobalAccountNum;
	}
	
}

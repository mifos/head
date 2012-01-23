/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class CenterSearchResultDto implements Serializable {
    
    private static final long serialVersionUID = -6668881719109275547L;
    
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

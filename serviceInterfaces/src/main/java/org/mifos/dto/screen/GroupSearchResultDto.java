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
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("PMD")
public class GroupSearchResultDto implements Serializable {	
    
    private static final long serialVersionUID = -3993245267863360016L;
    
    private Short officeId;
    private String officeName;

    // ------------------------------------------------------------------------------

    private String groupName;
    private String groupGlobalCustNum;
    private String centerName;
    private String centerGlobalCustNum;
    private String branchName;
    private short branchId;
    private short customerStatusId;
    private String loanOfficerName;
    private Short loanOfficerId;
    private List<String> loanGlobalAccountNum = new ArrayList<String>();
    private List<String> groupLoanGlobalAccountNum = new ArrayList<String>();
    private List<String> savingsGlobalAccountNum = new ArrayList<String>();
    private HashMap<String, Short> loanGlobalAccountStateIds = new HashMap<String, Short>();
    private HashMap<String, Short> savingsGlobalAccountStateIds = new HashMap<String, Short>();
    private HashMap<String, Short> groupLoanGlobalAccountStateIds = new HashMap<String, Short>();
    private String status;
    private boolean isGroupLoan;
    
    public List<String> getGroupLoanGlobalAccountNum() {
        return groupLoanGlobalAccountNum;
    }
    public void setGroupLoanGlobalAccountNum(List<String> groupLoanGlobalAccountNum) {
        this.groupLoanGlobalAccountNum = groupLoanGlobalAccountNum;
    }
	public boolean isGroupLoan() {
        return isGroupLoan;
    }
    public void setGroupLoan(boolean isGroupLoan) {
        this.isGroupLoan = isGroupLoan;
    }
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
	public List<String> getLoanGlobalAccountNum() {
		return loanGlobalAccountNum;
	}
	public void setLoanGlobalAccountNum(List<String> loanGlobalAccountNum) {
		this.loanGlobalAccountNum = loanGlobalAccountNum;
	}
	public List<String> getSavingsGlobalAccountNum() {
		return savingsGlobalAccountNum;
	}
	public void setSavingsGlobalAccountNum(List<String> savingsGlobalAccountNum) {
		this.savingsGlobalAccountNum = savingsGlobalAccountNum;
	}
	public short getCustomerStatusId() {
		return customerStatusId;
	}
	public void setCustomerStatusId(short customerStatusId) {
		this.customerStatusId = customerStatusId;
	}
    public HashMap<String, Short> getLoanGlobalAccountStateIds() {
        return loanGlobalAccountStateIds;
    }
    public void setLoanGlobalAccountStateIds(HashMap<String, Short> loanGlobalAccountStateIds) {
        this.loanGlobalAccountStateIds = loanGlobalAccountStateIds;
    }
    public HashMap<String, Short> getSavingsGlobalAccountStateIds() {
        return savingsGlobalAccountStateIds;
    }
    public void setSavingsGlobalAccountStateIds(HashMap<String, Short> savingsGlobalAccountStateIds) {
        this.savingsGlobalAccountStateIds = savingsGlobalAccountStateIds;
    }
    public HashMap<String, Short> getGroupLoanGlobalAccountStateIds() {
        return groupLoanGlobalAccountStateIds;
    }
    public void setGroupLoanGlobalAccountStateIds(HashMap<String, Short> groupLoanGlobalAccountStateIds) {
        this.groupLoanGlobalAccountStateIds = groupLoanGlobalAccountStateIds;
    }
    
}

/**

 * CustomerSearch.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.customer.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.business.View;

/**
 * This class acts a valueobject for searching customers.
 * 
 * @author ashishsm
 * 
 */
public class CustomerSearchView extends View {

	private Short loanOfficerId;

	private Short officeId;

	private String officeName;

	private int customerId;

	private String clientName;

	private String clientGlobalCustNum;

	private String groupName;

	private String groupGlobalCustNum;

	private String centerName;

	private String centerGlobalCustNum;

	private String branchName;

	private short branchId;

	private short customerStatus;

	private String loanOfficerName;

	private Collection loanGlobalAccountNum;

	private Collection savingsGlobalAccountNum;

	private short customerType;

	private String type;

	private String status;

	private short branchGlobalNum;

	private short loanOffcerGlobalNum;

	private String loanGlobalAccountNumber;

	private String savingsGlobalAccountNumber;

	public CustomerSearchView() {
		super();
	}

	public String getSavingsGlobalAccountNumber() {
		return savingsGlobalAccountNumber;
	}

	public void setSavingsGlobalAccountNumber(String savingsGlobalAccountNumber) {
		this.savingsGlobalAccountNumber = savingsGlobalAccountNumber;
	}

	public String getLoanGlobalAccountNumber() {
		return loanGlobalAccountNumber;
	}

	public void setLoanGlobalAccountNumber(String loanGlobalAccountNumber) {
		this.loanGlobalAccountNumber = loanGlobalAccountNumber;
	}

	public short getLoanOffcerGlobalNum() {
		return loanOffcerGlobalNum;
	}

	public void setLoanOffcerGlobalNum(short loanOffcerGlobalNum) {
		this.loanOffcerGlobalNum = loanOffcerGlobalNum;
	}

	public short getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(short customerStatus) {
		this.customerStatus = customerStatus;
		this.status = Short.toString(customerStatus);
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public short getCustomerType() {
		return customerType;
	}

	public void setCustomerType(short customerType) {
		this.customerType = customerType;
		this.type = Short.toString(customerType);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public short getBranchId() {
		return branchId;
	}

	public void setBranchId(short branchId) {
		this.branchId = branchId;
		this.branchGlobalNum = branchId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCenterGlobalCustNum() {
		return centerGlobalCustNum;
	}

	public void setCenterGlobalCustNum(String centerGlobalCustNum) {
		this.centerGlobalCustNum = centerGlobalCustNum;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getClientGlobalCustNum() {
		return clientGlobalCustNum;
	}

	public void setClientGlobalCustNum(String clientGlobalCustNum) {
		this.clientGlobalCustNum = clientGlobalCustNum;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getGroupGlobalCustNum() {
		return groupGlobalCustNum;
	}

	public void setGroupGlobalCustNum(String groupGlobalCustNum) {
		this.groupGlobalCustNum = groupGlobalCustNum;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Collection getLoanGlobalAccountNum() {
		return loanGlobalAccountNum;
	}

	public void setLoanGlobalAccountNum(Collection loanGlobalAccountNum) {
		this.loanGlobalAccountNum = loanGlobalAccountNum;
	}

	public String getLoanOfficerName() {
		return loanOfficerName;
	}

	public void setLoanOfficerName(String loanOfficerName) {
		this.loanOfficerName = loanOfficerName;
	}

	public Collection getSavingsGlobalAccountNum() {
		return savingsGlobalAccountNum;
	}

	public void setSavingsGlobalAccountNum(Collection savingsGlobalAccountNum) {
		this.savingsGlobalAccountNum = savingsGlobalAccountNum;
	}

	private Map searchNodeMap = new HashMap();

	public Map getSearchNodeMap() {
		return searchNodeMap;
	}

	public void setSearchNodeMap(Map searchNodeMap) {
		this.searchNodeMap = searchNodeMap;
	}

	public Short getLoanOfficerId() {
		return loanOfficerId;
	}

	public void setLoanOfficerId(Short loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
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

	public String getStatus() {
		return status;
	}

	public short getBranchGlobalNum() {
		return branchGlobalNum;
	}

	public void setBranchGlobalNum(short branchGlobalNum) {
		this.branchGlobalNum = branchGlobalNum;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
}

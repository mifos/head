/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.business.View;

/**
 * This class acts a valueobject for searching customers.
 */
public class CustomerSearch extends View {

    public CustomerSearch() {
        super();
    }

    private static final long serialVersionUID = 1432156567756L;

    private Short loanOfficerId;
    private Short officeId;
    private String officeName;

    private int customerId;

    // ------------------------------------------------------------------------------

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

    /**
     * @return Returns the savingsGlobalAccountNumber.
     */
    public String getSavingsGlobalAccountNumber() {
        return savingsGlobalAccountNumber;
    }

    /**
     * @param savingsGlobalAccountNumber
     *            The savingsGlobalAccountNumber to set.
     */
    public void setSavingsGlobalAccountNumber(String savingsGlobalAccountNumber) {
        this.savingsGlobalAccountNumber = savingsGlobalAccountNumber;
    }

    /**
     * @return Returns the loanGlobalAccountNumber.
     */
    public String getLoanGlobalAccountNumber() {
        return loanGlobalAccountNumber;
    }

    /**
     * @param loanGlobalAccountNumber
     *            The loanGlobalAccountNumber to set.
     */
    public void setLoanGlobalAccountNumber(String loanGlobalAccountNumber) {
        this.loanGlobalAccountNumber = loanGlobalAccountNumber;
    }

    /**
     * @return Returns the loanOffcerGlobalNum.
     */
    public short getLoanOffcerGlobalNum() {
        return loanOffcerGlobalNum;
    }

    /**
     * @param loanOffcerGlobalNum
     *            The loanOffcerGlobalNum to set.
     */
    public void setLoanOffcerGlobalNum(short loanOffcerGlobalNum) {
        this.loanOffcerGlobalNum = loanOffcerGlobalNum;
    }

    // -------------------------------------------------------------------------------------------

    /**
     * @return Returns the customerStatus.
     */
    public short getCustomerStatus() {
        return customerStatus;
    }

    /**
     * @param customerStatus
     *            The customerStatus to set.
     */
    public void setCustomerStatus(short customerStatus) {
        this.customerStatus = customerStatus;
        this.status = Short.toString(customerStatus);
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return Returns the customerType.
     */
    public short getCustomerType() {
        return customerType;
    }

    /**
     * @param customerType
     *            The customerType to set.
     */
    public void setCustomerType(short customerType) {
        this.customerType = customerType;
        this.type = Short.toString(customerType);
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Returns the branchId.
     */
    public short getBranchId() {
        return branchId;
    }

    /**
     * @param branchId
     *            The branchId to set.
     */
    public void setBranchId(short branchId) {
        this.branchId = branchId;
        this.branchGlobalNum = branchId;
    }

    /**
     * @return Returns the branchName.
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * @param branchName
     *            The branchName to set.
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * @return Returns the centerGlobalCustNum.
     */
    public String getCenterGlobalCustNum() {
        return centerGlobalCustNum;
    }

    /**
     * @param centerGlobalCustNum
     *            The centerGlobalCustNum to set.
     */
    public void setCenterGlobalCustNum(String centerGlobalCustNum) {
        this.centerGlobalCustNum = centerGlobalCustNum;
    }

    /**
     * @return Returns the centerName.
     */
    public String getCenterName() {
        return centerName;
    }

    /**
     * @param centerName
     *            The centerName to set.
     */
    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    /**
     * @return Returns the clientGlobalCustNum.
     */
    public String getClientGlobalCustNum() {
        return clientGlobalCustNum;
    }

    /**
     * @param clientGlobalCustNum
     *            The clientGlobalCustNum to set.
     */
    public void setClientGlobalCustNum(String clientGlobalCustNum) {
        this.clientGlobalCustNum = clientGlobalCustNum;
    }

    /**
     * @return Returns the clientName.
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * @param clientName
     *            The clientName to set.
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * @return Returns the groupGlobalCustNum.
     */
    public String getGroupGlobalCustNum() {
        return groupGlobalCustNum;
    }

    /**
     * @param groupGlobalCustNum
     *            The groupGlobalCustNum to set.
     */
    public void setGroupGlobalCustNum(String groupGlobalCustNum) {
        this.groupGlobalCustNum = groupGlobalCustNum;
    }

    /**
     * @return Returns the groupName.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName
     *            The groupName to set.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return Returns the loanGlobalAccountNum.
     */
    public Collection getLoanGlobalAccountNum() {
        return loanGlobalAccountNum;
    }

    /**
     * @param loanGlobalAccountNum
     *            The loanGlobalAccountNum to set.
     */
    public void setLoanGlobalAccountNum(Collection loanGlobalAccountNum) {
        this.loanGlobalAccountNum = loanGlobalAccountNum;
    }

    /**
     * @return Returns the loanOfficerName.
     */
    public String getLoanOfficerName() {
        return loanOfficerName;
    }

    /**
     * @param loanOfficerName
     *            The loanOfficerName to set.
     */
    public void setLoanOfficerName(String loanOfficerName) {
        this.loanOfficerName = loanOfficerName;
    }

    /**
     * @return Returns the savingsGlobalAccountNum.
     */
    public Collection getSavingsGlobalAccountNum() {
        return savingsGlobalAccountNum;
    }

    /**
     * @param savingsGlobalAccountNum
     *            The savingsGlobalAccountNum to set.
     */
    public void setSavingsGlobalAccountNum(Collection savingsGlobalAccountNum) {
        this.savingsGlobalAccountNum = savingsGlobalAccountNum;
    }

    // -------------------------------------------------------------------------------------------

    /**
     * This is the map which will hold the search parameters which come from the
     * jsp.
     */
    // private Map<String,String> searchNodeMap = new HashMap<String,String>();
    private Map searchNodeMap = new HashMap();

    /**
     * @return Returns the searchNodeMap}.
     */
    // public Map<String, String> getSearchNodeMap() {
    public Map getSearchNodeMap() {
        return searchNodeMap;
    }

    /**
     * @param searchNodeMap
     *            The searchNodeMap to set.
     */
    // public void setSearchNodeMap(Map<String, String> searchNodeMap) {
    public void setSearchNodeMap(Map searchNodeMap) {
        this.searchNodeMap = searchNodeMap;
    }

    /**
     * @return Returns the loanOfficerId.
     */
    public Short getLoanOfficerId() {
        return loanOfficerId;
    }

    /**
     * @param loanOfficerId
     *            The loanOfficerId to set.
     */
    public void setLoanOfficerId(Short loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    /**
     * @return Returns the officeId.
     */
    public Short getOfficeId() {
        return officeId;
    }

    /**
     * @param officeId
     *            The officeId to set.
     */
    public void setOfficeId(Short officeId) {
        this.officeId = officeId;
    }

    /**
     * @return Returns the officeName.
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName
     *            The officeName to set.
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * This function returns the branchGlobalNum
     *
     * @return Returns the branchGlobalNum.
     */

    public short getBranchGlobalNum() {
        return branchGlobalNum;
    }

    /**
     * This function sets the branchGlobalNum
     *
     * @param branchGlobalNum
     *            the branchGlobalNum to set.
     */

    public void setBranchGlobalNum(short branchGlobalNum) {
        this.branchGlobalNum = branchGlobalNum;
    }

    /**
     * @return Returns the customerId.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId
     *            The customerId to set.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

}

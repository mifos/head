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
package org.mifos.application.servicefacade;

import java.util.Date;
import java.util.List;

import org.mifos.customers.api.DataTransferObject;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;

/**
 * I am a DTO for data that is needed to display collection sheet form.
 */
public class CollectionSheetEntryFormDto implements DataTransferObject {

    private final List<OfficeDetailsDto> activeBranchesList;
    private final List<ListItem<Short>> paymentTypesList;
    private final List<PersonnelDto> loanOfficerList;
    private final List<CustomerDto> customerList;
    private final Short reloadFormAutomatically;
    private final Short centerHierarchyExists;
    private final Short backDatedTransactionAllowed;
    private final Date meetingDate;
    private final List<CustomerDto> groupsList;
    private final List<CustomerDto> membersList;

    public CollectionSheetEntryFormDto(List<OfficeDetailsDto> activeBranches, List<ListItem<Short>> paymentTypesDtoList,
            List<PersonnelDto> loanOfficerList, List<CustomerDto> customerList, Short reloadFormAutomatically,
            Short centerHierarchyExists, Short backDatedTransactionAllowed) {
        this.activeBranchesList = activeBranches;
        this.paymentTypesList = paymentTypesDtoList;
        this.loanOfficerList = loanOfficerList;
        this.customerList = customerList;
        this.reloadFormAutomatically = reloadFormAutomatically;
        this.centerHierarchyExists = centerHierarchyExists;
        this.backDatedTransactionAllowed = backDatedTransactionAllowed;
        this.meetingDate = null;
        this.groupsList=null;
        this.membersList=null;
    }

    public CollectionSheetEntryFormDto(List<OfficeDetailsDto> activeBranches, List<ListItem<Short>> paymentTypesDtoList,
            List<PersonnelDto> loanOfficerList, List<CustomerDto> customerList, Short reloadFormAutomatically,
            Short centerHierarchyExists, Short backDatedTransactionAllowed, Date meetingDate) {
        this.activeBranchesList = activeBranches;
        this.paymentTypesList = paymentTypesDtoList;
        this.loanOfficerList = loanOfficerList;
        this.customerList = customerList;
        this.reloadFormAutomatically = reloadFormAutomatically;
        this.centerHierarchyExists = centerHierarchyExists;
        this.backDatedTransactionAllowed = backDatedTransactionAllowed;
        this.meetingDate = meetingDate;
        this.groupsList=null;
        this.membersList=null;
    }
    
    public CollectionSheetEntryFormDto(List<OfficeDetailsDto> activeBranches, List<ListItem<Short>> paymentTypesDtoList,
            List<PersonnelDto> loanOfficerList, List<CustomerDto> customerList, Short reloadFormAutomatically,
            Short centerHierarchyExists, Short backDatedTransactionAllowed, Date meetingDate, List<CustomerDto> groupsList,List<CustomerDto> membersList) {
        this.activeBranchesList = activeBranches;
        this.paymentTypesList = paymentTypesDtoList;
        this.loanOfficerList = loanOfficerList;
        this.customerList = customerList;
        this.reloadFormAutomatically = reloadFormAutomatically;
        this.centerHierarchyExists = centerHierarchyExists;
        this.backDatedTransactionAllowed = backDatedTransactionAllowed;
        this.meetingDate = meetingDate;
        this.groupsList=groupsList;
        this.membersList=membersList;
    }

    public List<OfficeDetailsDto> getActiveBranchesList() {
        return this.activeBranchesList;
    }

    public List<ListItem<Short>> getPaymentTypesList() {
        return this.paymentTypesList;
    }

    public List<PersonnelDto> getLoanOfficerList() {
        return this.loanOfficerList;
    }

    public List<CustomerDto> getCustomerList() {
        return this.customerList;
    }

    public Short getReloadFormAutomatically() {
        return this.reloadFormAutomatically;
    }

    public Short getCenterHierarchyExists() {
        return this.centerHierarchyExists;
    }

    public Short getBackDatedTransactionAllowed() {
        return this.backDatedTransactionAllowed;
    }

    public java.util.Date getMeetingDate() {
        return this.meetingDate;
    }

	public List<CustomerDto> getGroupsList() {
		return groupsList;
	}

	public List<CustomerDto> getMembersList() {
		return membersList;
	}
    
}

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

import java.sql.Date;

import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;

/**
 * I am a decorator for {@link CollectionSheetEntryFormDto}.
 */
public class CollectionSheetEntryFormDtoDecorator {

    private final CollectionSheetEntryFormDto collectionSheetEntryFormDto;

    public CollectionSheetEntryFormDtoDecorator(CollectionSheetEntryFormDto CollectionSheetEntryFormDto) {
        this.collectionSheetEntryFormDto = CollectionSheetEntryFormDto;
    }

    public CustomerDto findSelectedCustomerById(final Integer selectedCustomerId) {

        for (CustomerDto customer : collectionSheetEntryFormDto.getCustomerList()) {
            if (selectedCustomerId.intValue() == customer.getCustomerId().intValue()) {
                return customer;
            }
        }
        return null;
    }
    
    public CustomerDto findSelectedGroupById(final Integer selectedGroupId) {

        for (CustomerDto customer : collectionSheetEntryFormDto.getGroupsList()) {
            if (selectedGroupId.intValue() == customer.getCustomerId().intValue()) {
                return customer;
            }
        }
        return null;
    }
    
    public CustomerDto findSelectedMemberById(final Integer selectedMemberId) {
    	
       if(collectionSheetEntryFormDto.getMembersList()!=null){
        for (CustomerDto customer : collectionSheetEntryFormDto.getMembersList()) {
            if (selectedMemberId.intValue() == customer.getCustomerId().intValue()) {
                return customer;
            }
        }
       }
        return null;
    }

    public PersonnelDto findSelectedLoanOfficerById(final Short loanOfficerId) {

        for (PersonnelDto loanOfficer : collectionSheetEntryFormDto.getLoanOfficerList()) {
            if (loanOfficerId.shortValue() == loanOfficer.getPersonnelId().shortValue()) {
                return loanOfficer;
            }
        }
        return null;
    }

    public OfficeDetailsDto findSelectedBranchOfficeById(final Short branchId) {
        for (OfficeDetailsDto branch : collectionSheetEntryFormDto.getActiveBranchesList()) {
            if (branchId.shortValue() == branch.getOfficeId().shortValue()) {
                return branch;
            }
        }
        return null;
    }

    public ListItem<Short> findSelectedPaymentTypeById(final Short paymentId) {

        for (ListItem<Short> paymentType : collectionSheetEntryFormDto.getPaymentTypesList()) {
            if (paymentId.shortValue() == paymentType.getId().shortValue()) {
                return paymentType;
            }
        }

        return null;
    }

    public Date getMeetingDateAsSqlDate() {
        return new java.sql.Date(collectionSheetEntryFormDto.getMeetingDate().getTime());
    }
}

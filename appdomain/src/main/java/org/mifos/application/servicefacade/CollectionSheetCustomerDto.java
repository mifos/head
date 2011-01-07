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
package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CollectionSheetCustomerDto {
    private Integer customerId;
    private String name;
    private Short levelId;
    private Integer parentCustomerId;
    private String searchId;
    private Short branchId;
    private Short attendanceId;
    private CollectionSheetCustomerAccountDto collectionSheetCustomerAccount;
    private List<CollectionSheetCustomerLoanDto> collectionSheetCustomerLoan = new ArrayList<CollectionSheetCustomerLoanDto>();
    private List<CollectionSheetCustomerSavingDto> collectionSheetCustomerSaving = new ArrayList<CollectionSheetCustomerSavingDto>();
    private List<CollectionSheetCustomerSavingDto> individualSavingAccounts = new ArrayList<CollectionSheetCustomerSavingDto>();

    public CollectionSheetCustomerDto() {
        // Default empty constructor for hibernate
    }

    public CollectionSheetCustomerDto(final Integer id, final String name, final Short level, final String searchId,
            final Short attendance, final Short branchId) {
        this.customerId = id;
        this.name = name;
        this.levelId = level;
        this.searchId = searchId;
        this.branchId = branchId;
        this.collectionSheetCustomerAccount = null;
        this.attendanceId = attendance;
        this.collectionSheetCustomerLoan = null;
        this.collectionSheetCustomerSaving = null;
    }

    public CollectionSheetCustomerDto(final CollectionSheetCustomerDto collectionSheetCustomer,
            final List<CollectionSheetCustomerLoanDto> collectionSheetCustomerLoan,
            final List<CollectionSheetCustomerSavingDto> collectionSheetCustomerSaving,
            final List<CollectionSheetCustomerSavingDto> individualSavingAccounts,
            final CollectionSheetCustomerAccountDto collectionSheetCustomerAccount) {
        this.customerId = collectionSheetCustomer.getCustomerId();
        this.name = collectionSheetCustomer.getName();
        this.levelId = collectionSheetCustomer.getLevelId();
        this.searchId = collectionSheetCustomer.getSearchId();
        this.branchId = collectionSheetCustomer.getBranchId();
        this.parentCustomerId = collectionSheetCustomer.getParentCustomerId();
        this.collectionSheetCustomerAccount = collectionSheetCustomerAccount;
        this.attendanceId = collectionSheetCustomer.getAttendanceId();
        this.collectionSheetCustomerLoan = collectionSheetCustomerLoan;
        this.collectionSheetCustomerSaving = collectionSheetCustomerSaving;
        this.individualSavingAccounts = individualSavingAccounts;
    }

    public String getName() {
        return this.name;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final Integer customerId) {
        this.customerId = customerId;
    }

    public Short getLevelId() {
        return this.levelId;
    }

    public void setLevelId(final Short levelId) {
        this.levelId = levelId;
    }

    public Integer getParentCustomerId() {
        return this.parentCustomerId;
    }

    public void setParentCustomerId(final Integer parentCustomerId) {
        this.parentCustomerId = parentCustomerId;
    }

    public Short getAttendanceId() {
        return this.attendanceId;
    }

    public void setAttendanceId(final Short attendanceId) {
        this.attendanceId = attendanceId;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSearchId(final String searchId) {
        this.searchId = searchId;
    }

    public Short getBranchId() {
        return this.branchId;
    }

    public List<CollectionSheetCustomerLoanDto> getCollectionSheetCustomerLoan() {
        return this.collectionSheetCustomerLoan;
    }

    public List<CollectionSheetCustomerSavingDto> getCollectionSheetCustomerSaving() {
        return this.collectionSheetCustomerSaving;
    }

    public CollectionSheetCustomerAccountDto getCollectionSheetCustomerAccount() {
        return this.collectionSheetCustomerAccount;
    }

    public List<CollectionSheetCustomerSavingDto> getIndividualSavingAccounts() {
        return this.individualSavingAccounts;
    }
}

/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.util.List;

/**
 *
 */
public class CollectionSheetDto {

    private final List<CollectionSheetCustomerDto> collectionSheetCustomer;

    public CollectionSheetDto(final List<CollectionSheetCustomerDto> collectionSheetCustomer) {
        this.collectionSheetCustomer = collectionSheetCustomer;
    }

    public List<CollectionSheetCustomerDto> getCollectionSheetCustomer() {
        return this.collectionSheetCustomer;
    }

    public void print() {

        doLog("");
        doLog("");
        doLog(">>>>>Printing CollectionSheetDto ");
        doLog("");
        doLog("Listing Customers...");

        for (CollectionSheetCustomerDto collectionSheetCustomer : this.getCollectionSheetCustomer()) {

            doLog("Customer: Id: " + collectionSheetCustomer.getCustomerId() + ", Name: "
                    + collectionSheetCustomer.getName() + ", Parent Id: "
                    + collectionSheetCustomer.getParentCustomerId() + ", Level: " + collectionSheetCustomer.getLevelId()
                    + ", Branch Id: " + collectionSheetCustomer.getBranchId() + ", Search Id: "
                    + collectionSheetCustomer.getSearchId() + ", Attendance Id: "
                    + collectionSheetCustomer.getAttendanceId());

            CollectionSheetCustomerAccountDto collectionSheetCustomerAccount = collectionSheetCustomer
                    .getCollectionSheetCustomerAccount();
            if (null != collectionSheetCustomerAccount) {
                doLog("            : A/C Collections");
                doLog("            : Account Id: " + collectionSheetCustomerAccount.getAccountId() + ", Currency Id: "
                        + collectionSheetCustomerAccount.getCurrencyId() + ", A/C Collections Due: "
                        + collectionSheetCustomerAccount.getTotalCustomerAccountCollectionFee());
            } else {
                doLog("            : No A/C Collections");
            }

            List<CollectionSheetCustomerLoanDto> collectionSheetCustomerLoans = collectionSheetCustomer
                    .getCollectionSheetCustomerLoan();
            if (null != collectionSheetCustomerLoans && collectionSheetCustomerLoans.size() > 0) {
                doLog("            : Loans");
                for (CollectionSheetCustomerLoanDto collectionSheetCustomerLoan : collectionSheetCustomerLoans) {
                    doLog("            : Account Id: " + collectionSheetCustomerLoan.getAccountId() + ", Currency Id: "
                            + collectionSheetCustomerLoan.getCurrencyId() + ", State Id: "
                            + collectionSheetCustomerLoan.getAccountStateId() + ", Disbursal?: "
                            + collectionSheetCustomerLoan.isDisbursalAccount() + ", Payment Due: "
                            + collectionSheetCustomerLoan.getTotalRepaymentDue() + ", Disbursement Due: "
                            + collectionSheetCustomerLoan.getTotalDisbursement());
                }
            } else {
                doLog("            : No Loans");
            }

            List<CollectionSheetCustomerSavingDto> collectionSheetCustomerSavings = collectionSheetCustomer
                    .getCollectionSheetCustomerSaving();
            if (null != collectionSheetCustomerSavings && collectionSheetCustomerSavings.size() > 0) {
                doLog("            : Normal Saving Accounts");
                for (CollectionSheetCustomerSavingDto collectionSheetCustomerSaving : collectionSheetCustomerSavings) {
                    doLog("            : Account Id: " + collectionSheetCustomerSaving.getAccountId()
                            + ", Currency Id: " + collectionSheetCustomerSaving.getCurrencyId() + ", Deposit Due: "
                            + collectionSheetCustomerSaving.getTotalDepositAmount());
                }
            } else {
                doLog("            : No Normal Saving Accounts");
            }

            List<CollectionSheetCustomerSavingDto> collectionSheetCustomerIndividualSavings = collectionSheetCustomer
                    .getIndividualSavingAccounts();
            if (null != collectionSheetCustomerIndividualSavings && collectionSheetCustomerIndividualSavings.size() > 0) {
                doLog("            : Individual Saving Accounts");
                for (CollectionSheetCustomerSavingDto collectionSheetCustomerSaving : collectionSheetCustomerIndividualSavings) {
                    doLog("            : Account Id: " + collectionSheetCustomerSaving.getAccountId()
                            + ", Currency Id: " + collectionSheetCustomerSaving.getCurrencyId() + ", Deposit Due: "
                            + collectionSheetCustomerSaving.getTotalDepositAmount());
                }
            } else {
                doLog("            : No Individual Saving Accounts");
            }
        }

    }

    private void doLog(String str) {
        System.out.println(str);
    }

}

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

import java.util.ArrayList;
import java.util.List;

/**
 * Holds customer details as part of saving a collection sheet
 */
public class SaveCollectionSheetCustomerDto {
    private Integer customerId;
    private Integer parentCustomerId;
    private Short attendanceId;
    private SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount;
    private List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans;
    private List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings;

    /*
     * About Individual Savings Accounts
     * 
     * All CENTER savings accounts are 'individual'. GROUP savings accounts can
     * be defined as 'individual'.
     * 
     * 'Individual' means that deposits and withdrawals can be entered for any
     * CLIENT customer that appears below the CENTER/GROUP as well as for the
     * CENTER/GROUP itself. However, GROUPs appearing below a CENTER cannot
     * deposit into or withdraw from a CENTER savings account.
     * 
     * CENTER and GROUP individual savings accounts appear in variable
     * saveCollectionSheetCustomerSavings above for the owning CENTER/GROUP but
     * appear in variable saveCollectionSheetCustomerIndividualSavings below for
     * CLIENTS
     */
    private List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings;

    private List<InvalidSaveCollectionSheetReason> validationErrors = new ArrayList<InvalidSaveCollectionSheetReason>();
    private Integer zeroInteger = 0;

    public SaveCollectionSheetCustomerDto(final Integer customerId, final Integer parentCustomerId,
            final Short attendanceId, final SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount,
            final List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans,
            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings,
            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings)
            throws SaveCollectionSheetException {

        validateInput(customerId, parentCustomerId, saveCollectionSheetCustomerLoans,
                saveCollectionSheetCustomerSavings, saveCollectionSheetCustomerIndividualSavings);
        if (validationErrors.size() > 0) {
            throw new SaveCollectionSheetException(validationErrors);
        }

        this.customerId = customerId;
        this.parentCustomerId = parentCustomerId;
        this.attendanceId = attendanceId;
        this.saveCollectionSheetCustomerAccount = saveCollectionSheetCustomerAccount;
        this.saveCollectionSheetCustomerLoans = saveCollectionSheetCustomerLoans;
        this.saveCollectionSheetCustomerSavings = saveCollectionSheetCustomerSavings;
        this.saveCollectionSheetCustomerIndividualSavings = saveCollectionSheetCustomerIndividualSavings;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getParentCustomerId() {
        return this.parentCustomerId;
    }

    public Short getAttendanceId() {
        return this.attendanceId;
    }

    public SaveCollectionSheetCustomerAccountDto getSaveCollectionSheetCustomerAccount() {
        return this.saveCollectionSheetCustomerAccount;
    }

    public List<SaveCollectionSheetCustomerLoanDto> getSaveCollectionSheetCustomerLoans() {
        return this.saveCollectionSheetCustomerLoans;
    }

    public List<SaveCollectionSheetCustomerSavingDto> getSaveCollectionSheetCustomerSavings() {
        return this.saveCollectionSheetCustomerSavings;
    }

    public List<SaveCollectionSheetCustomerSavingDto> getSaveCollectionSheetCustomerIndividualSavings() {
        return this.saveCollectionSheetCustomerIndividualSavings;
    }

    private void validateInput(Integer customerId, Integer parentCustomerId,
            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans,
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings,
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings) {

        if (customerId == null) {
            validationErrors.add(InvalidSaveCollectionSheetReason.CUSTOMERID_NULL);
        } else {
            if (customerId.compareTo(zeroInteger) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.CUSTOMERID_NEGATIVE);
            }
        }

        if (parentCustomerId != null) {
            if (parentCustomerId.compareTo(zeroInteger) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.PARENTCUSTOMERID_NEGATIVE);
            }
        }

        if (!uniqueLoanAccounts(saveCollectionSheetCustomerLoans)) {
            validationErrors.add(InvalidSaveCollectionSheetReason.ACCOUNT_LISTED_MORE_THAN_ONCE);
        }

        if (!uniqueSavingsAccounts(saveCollectionSheetCustomerSavings)) {
            validationErrors.add(InvalidSaveCollectionSheetReason.ACCOUNT_LISTED_MORE_THAN_ONCE);
        }

        if (!uniqueSavingsAccounts(saveCollectionSheetCustomerIndividualSavings)) {
            validationErrors.add(InvalidSaveCollectionSheetReason.ACCOUNT_LISTED_MORE_THAN_ONCE);
        }
    }

    private boolean uniqueLoanAccounts(List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans) {

        if (saveCollectionSheetCustomerLoans != null && saveCollectionSheetCustomerLoans.size() > 0) {

            for (Integer i = 0; i < saveCollectionSheetCustomerLoans.size() - 1; i++) {
                for (Integer j = i + 1; j < saveCollectionSheetCustomerLoans.size(); j++) {
                    if (saveCollectionSheetCustomerLoans.get(i).getAccountId().compareTo(
                            saveCollectionSheetCustomerLoans.get(j).getAccountId()) == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean uniqueSavingsAccounts(List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings) {

        if (saveCollectionSheetCustomerSavings != null && saveCollectionSheetCustomerSavings.size() > 0) {

            for (Integer i = 0; i < saveCollectionSheetCustomerSavings.size() - 1; i++) {
                for (Integer j = i + 1; j < saveCollectionSheetCustomerSavings.size(); j++) {
                    if (saveCollectionSheetCustomerSavings.get(i).getAccountId().compareTo(
                            saveCollectionSheetCustomerSavings.get(j).getAccountId()) == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

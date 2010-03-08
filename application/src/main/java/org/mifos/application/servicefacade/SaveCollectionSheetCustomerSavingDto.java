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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds savings account details as part of saving a collection sheet
 */
public class SaveCollectionSheetCustomerSavingDto {

    private Integer accountId;
    private Short currencyId;
    private BigDecimal totalDeposit;
    private BigDecimal totalWithdrawal;

    public SaveCollectionSheetCustomerSavingDto(Integer accountId, Short currencyId, BigDecimal totalDeposit,
            BigDecimal totalWithdrawal) throws SaveCollectionSheetException {

        validateInput(accountId, currencyId, totalDeposit, totalWithdrawal);
        if (validationErrors.size() > 0) {
            throw new SaveCollectionSheetException(validationErrors);
        }

        this.accountId = accountId;
        this.currencyId = currencyId;
        this.totalDeposit = totalDeposit;
        this.totalWithdrawal = totalWithdrawal;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public BigDecimal getTotalDeposit() {
        return this.totalDeposit;
    }

    public BigDecimal getTotalWithdrawal() {
        return this.totalWithdrawal;
    }

    /*
     * The Dto really ends here: All the fields and methods below are for
     * validation purposes
     */

    private List<InvalidSaveCollectionSheetReason> validationErrors = new ArrayList<InvalidSaveCollectionSheetReason>();
    private Integer zeroInteger = 0;
    private Short zeroShort = Short.valueOf("0");

    private void validateInput(Integer accountId, Short currencyId, BigDecimal totalDeposit, BigDecimal totalWithdrawal) {

        if (accountId == null) {
            validationErrors.add(InvalidSaveCollectionSheetReason.ACCOUNTID_NULL);
        } else {
            if (accountId.compareTo(zeroInteger) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.ACCOUNTID_NEGATIVE);
            }
        }

        if (currencyId == null) {
            validationErrors.add(InvalidSaveCollectionSheetReason.CURRENCYID_NULL);
        } else {
            if (currencyId.compareTo(zeroShort) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.CURRENCYID_NEGATIVE);
            }
        }

        if (totalDeposit != null) {
            if (totalDeposit.compareTo(BigDecimal.ZERO) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.TOTALDEPOSIT_NEGATIVE);
            }
        }

        if (totalWithdrawal != null) {
            if (totalWithdrawal.compareTo(BigDecimal.ZERO) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.TOTALWITHDRAWAL_NEGATIVE);
            }
        }
    }
}

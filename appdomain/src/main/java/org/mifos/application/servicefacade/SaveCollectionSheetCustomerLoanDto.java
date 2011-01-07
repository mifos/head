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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds loan account details as part of saving a collection sheet
 */
public class SaveCollectionSheetCustomerLoanDto {

    private Integer accountId;
    private Short currencyId;
    private BigDecimal totalLoanPayment;
    private BigDecimal totalDisbursement;

    public SaveCollectionSheetCustomerLoanDto(Integer accountId, Short currencyId, final BigDecimal totalLoanPayment,
            final BigDecimal totalDisbursement) throws SaveCollectionSheetException {

        validateInput(accountId, currencyId, totalLoanPayment, totalDisbursement);
        if (validationErrors.size() > 0) {
            throw new SaveCollectionSheetException(validationErrors);
        }

        this.accountId = accountId;
        this.currencyId = currencyId;
        this.totalLoanPayment = totalLoanPayment;
        this.totalDisbursement = totalDisbursement;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public BigDecimal getTotalLoanPayment() {
        return this.totalLoanPayment;
    }

    public BigDecimal getTotalDisbursement() {
        return this.totalDisbursement;
    }

    /*
     * The Dto really ends here: All the fields and methods below are for
     * validation purposes
     */

    private List<InvalidSaveCollectionSheetReason> validationErrors = new ArrayList<InvalidSaveCollectionSheetReason>();
    private final Integer zeroInteger = 0;
    private final Short zeroShort = Short.valueOf("0");

    private void validateInput(Integer accountId, Short currencyId, BigDecimal totalLoanPayment,
            BigDecimal totalDisbursement) {

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

        if (totalLoanPayment != null) {
            if (totalLoanPayment.compareTo(BigDecimal.ZERO) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.TOTALLOANPAYMENT_NEGATIVE);
            }
        }

        if (totalDisbursement != null) {
            if (totalDisbursement.compareTo(BigDecimal.ZERO) < 0) {
                validationErrors.add(InvalidSaveCollectionSheetReason.TOTALDISBURSEMENT_NEGATIVE);
            }
        }
    }
}

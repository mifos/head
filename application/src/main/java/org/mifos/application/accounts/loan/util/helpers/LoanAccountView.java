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

package org.mifos.application.accounts.loan.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryLoanInstallmentView;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class LoanAccountView extends View {

    private final Integer accountId;
    private final Integer customerId;
    private final String prdOfferingShortName;
    private final Short prdOfferingId;
    private final Short accountState;
    private final Short interestDeductedAtDisbursement;
    private final Money loanAmount;
    private Double amountPaidAtDisbursement;
    
    private final List<CollectionSheetEntryInstallmentView> accountTrxnDetails = new ArrayList<CollectionSheetEntryInstallmentView>();

    public LoanAccountView(Integer accountId, Integer customerId, String prdOfferingShortName,
            Short prdOfferingId, Short loanAccountState, Short interestDeductedAtDisbursement, Money loanAmount) {
        if (loanAmount == null) {
            throw new MifosRuntimeException("Null loanAmount is not allowed for LoanAccountView");
        }
        this.accountId = accountId;
        this.customerId = customerId;
        this.prdOfferingShortName = prdOfferingShortName;
        this.prdOfferingId = prdOfferingId;
        this.accountState = loanAccountState;
        this.interestDeductedAtDisbursement = interestDeductedAtDisbursement;
        this.loanAmount = loanAmount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public String getPrdOfferingShortName() {
        return prdOfferingShortName;
    }

    public Short getPrdOfferingId() {
        return prdOfferingId;
    }
    
    public Short getAccountSate() {
        return accountState;
    }

    public List<CollectionSheetEntryInstallmentView> getAccountTrxnDetails() {
        return accountTrxnDetails;
    }

    public void addTrxnDetails(List<CollectionSheetEntryInstallmentView> accountTrxnDetails) {
        if (null != accountTrxnDetails && accountTrxnDetails.size() > 0) {
            this.accountTrxnDetails.addAll(accountTrxnDetails);
        }
    }

    public Double getTotalAmountDue() {
        Money totalAmount = new Money(loanAmount.getCurrency());
        if (isDisbursalAccount()) {
            return amountPaidAtDisbursement;
        }

        if (accountTrxnDetails != null && accountTrxnDetails.size() > 0) {
            for (CollectionSheetEntryInstallmentView accountAction : accountTrxnDetails) {
                totalAmount = totalAmount.add(((CollectionSheetEntryLoanInstallmentView) accountAction)
                        .getTotalDueWithFees());
            }
        }
        return totalAmount.getAmountDoubleValue();
    }

    public boolean isInterestDeductedAtDisbursement() {
        return this.interestDeductedAtDisbursement > 0 ? true : false;
    }

    public Double getAmountPaidAtDisbursement() {
        return amountPaidAtDisbursement;
    }

    public void setAmountPaidAtDisbursement(Double amountPaidAtDisbursement) {
        this.amountPaidAtDisbursement = amountPaidAtDisbursement;
    }

    public Double getTotalDisburseAmount() {
        return isDisbursalAccount() ? this.loanAmount.getAmountDoubleValue() : 0.0;
    }

    public boolean isDisbursalAccount() {
        return getAccountSate().equals(AccountState.LOAN_APPROVED.getValue())
                || getAccountSate().equals(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());
    }
}

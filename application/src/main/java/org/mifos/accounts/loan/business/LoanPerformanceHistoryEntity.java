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

package org.mifos.accounts.loan.business;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.DateUtils;

public class LoanPerformanceHistoryEntity extends PersistentObject {

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private final Integer id;

    private final LoanBO loan;

    private Integer noOfPayments;

    private Date loanMaturityDate;

    protected LoanPerformanceHistoryEntity() {
        super();
        this.id = null;
        this.loan = null;
    }

    public LoanPerformanceHistoryEntity(LoanBO loan) {
        this.id = null;
        this.loan = loan;
        this.noOfPayments = 0;
    }

    public Date getLoanMaturityDate() {
        return loanMaturityDate;
    }

    void setLoanMaturityDate(Date loanMaturityDate) {
        this.loanMaturityDate = loanMaturityDate;
    }

    public Integer getNoOfPayments() {
        return noOfPayments;
    }

    void setNoOfPayments(Integer noOfPayments) {
        this.noOfPayments = noOfPayments;
    }

    public Short getDaysInArrears() {
        return loan.getDaysInArrears();
    }

    public Integer getTotalNoOfMissedPayments() {
        int noOfMissedPayments = 0;
        if (loan.getAccountState().getId().equals(AccountStates.LOANACC_ACTIVEINGOODSTANDING)
                || loan.getAccountState().getId().equals(AccountStates.LOANACC_OBLIGATIONSMET)
                || loan.getAccountState().getId().equals(AccountStates.LOANACC_WRITTENOFF)
                || loan.getAccountState().getId().equals(AccountStates.LOANACC_RESCHEDULED)
                || loan.getAccountState().getId().equals(AccountStates.LOANACC_BADSTANDING)) {
            List<AccountActionDateEntity> accountActionDateList = loan.getDetailsOfInstallmentsInArrears();
            if (!accountActionDateList.isEmpty()) {
                noOfMissedPayments = +accountActionDateList.size();
            }
            noOfMissedPayments = noOfMissedPayments + getNoOfBackDatedPayments();
        }
        return noOfMissedPayments;
    }

    private Integer getNoOfBackDatedPayments() {
        int noOfMissedPayments = 0;
        for (AccountPaymentEntity accountPaymentEntity : loan.getAccountPayments()) {
            Set<AccountTrxnEntity> accountTrxnEntityList = accountPaymentEntity.getAccountTrxns();
            for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntityList) {
                if (accountTrxnEntity.getAccountActionEntity().getId().equals(
                        AccountActionTypes.LOAN_REPAYMENT.getValue())
                        && DateUtils.getDateWithoutTimeStamp(accountTrxnEntity.getActionDate().getTime()).compareTo(
                                DateUtils.getDateWithoutTimeStamp(accountTrxnEntity.getDueDate().getTime())) > 0) {
                    noOfMissedPayments++;
                }
                if (accountTrxnEntity.getAccountActionEntity().getId().equals(
                        AccountActionTypes.LOAN_ADJUSTMENT.getValue())
                        && DateUtils.getDateWithoutTimeStamp(
                                accountTrxnEntity.getRelatedTrxn().getActionDate().getTime()).compareTo(
                                DateUtils.getDateWithoutTimeStamp(accountTrxnEntity.getRelatedTrxn().getDueDate()
                                        .getTime())) > 0) {
                    noOfMissedPayments--;
                }
            }
        }
        return noOfMissedPayments;
    }
}

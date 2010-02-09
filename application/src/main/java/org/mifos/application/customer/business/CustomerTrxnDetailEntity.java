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

package org.mifos.application.customer.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.Money;

public class CustomerTrxnDetailEntity extends AccountTrxnEntity {

    private final Set<FeesTrxnDetailEntity> feesTrxnDetails = new HashSet<FeesTrxnDetailEntity>();

    private final Money totalAmount;

    private final Money miscPenaltyAmount;

    private final Money miscFeeAmount;

    protected CustomerTrxnDetailEntity() {
        super();
        this.miscFeeAmount = null;
        this.miscPenaltyAmount = null;
        this.totalAmount = null;
    }

    public CustomerTrxnDetailEntity(final AccountPaymentEntity accountPayment, final AccountActionTypes accountActionType,
            final Short installmentId, final Date dueDate, final PersonnelBO personnel, final Date actionDate, final Money amount, final String comments,
            final AccountTrxnEntity relatedTrxn, final Money miscFeeAmount, final Money miscPenaltyAmount, final Persistence persistence) {
        super(accountPayment, accountActionType, installmentId, dueDate, personnel, null, actionDate, amount,
                comments, relatedTrxn, persistence);
        this.miscFeeAmount = miscFeeAmount;
        this.miscPenaltyAmount = miscPenaltyAmount;
        this.totalAmount = amount;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public Money getMiscFeeAmount() {
        return miscFeeAmount;
    }

    public Money getMiscPenaltyAmount() {
        return miscPenaltyAmount;
    }

    public Set<FeesTrxnDetailEntity> getFeesTrxnDetails() {
        return feesTrxnDetails;
    }

    public void addFeesTrxnDetail(final FeesTrxnDetailEntity feesTrxn) {
        feesTrxnDetails.add(feesTrxn);
    }

    public FeesTrxnDetailEntity getFeesTrxn(final Integer accountFeeId) {
        if (null != getFeesTrxnDetails() && feesTrxnDetails.size() > 0) {
            for (FeesTrxnDetailEntity feesTrxn : feesTrxnDetails) {

                if (feesTrxn.getAccountFees().getAccountFeeId().equals(accountFeeId)) {
                    return feesTrxn;
                }
            }
        }
        return null;
    }

    @Override
    protected AccountTrxnEntity generateReverseTrxn(final PersonnelBO loggedInUser, final String adjustmentComment)
            throws AccountException {
        MasterPersistence masterPersistence = new MasterPersistence();
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Inside generate reverse transaction method of loan trxn detail");
        String comment = null;
        if (null == adjustmentComment) {
            comment = getComments();
        } else {
            comment = adjustmentComment;
        }

        CustomerTrxnDetailEntity reverseAccntTrxn;

        reverseAccntTrxn = new CustomerTrxnDetailEntity(getAccountPayment(),
                AccountActionTypes.CUSTOMER_ADJUSTMENT, getInstallmentId(), getDueDate(),
                loggedInUser, getActionDate(), getAmount().negate(), comment, this, getMiscFeeAmount().negate(),
                getMiscPenaltyAmount().negate(), masterPersistence);

        if (null != getFeesTrxnDetails() && getFeesTrxnDetails().size() > 0) {
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "Before generating reverse entries for fees");
            for (FeesTrxnDetailEntity feeTrxnDetail : getFeesTrxnDetails()) {
                reverseAccntTrxn.addFeesTrxnDetail(feeTrxnDetail.generateReverseTrxn(reverseAccntTrxn));
            }
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
                    .debug("after generating reverse entries for fees");
        }
        return reverseAccntTrxn;
    }

}

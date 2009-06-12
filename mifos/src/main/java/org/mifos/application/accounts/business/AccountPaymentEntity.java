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

package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/*
 * Seems to be used to record information about a payment.
 * 
 * Has some duplicate information that is contained in {@link AccountTrxnEntity}
 */
public class AccountPaymentEntity extends PersistentObject {

    private final Integer paymentId = null;

    private final AccountBO account;

    private final PaymentTypeEntity paymentType;

    private final String receiptNumber;

    private final String voucherNumber;

    private final String checkNumber;

    private final Date receiptDate;

    private final String bankName;

    private final Date paymentDate;

    private Money amount;

    private Set<AccountTrxnEntity> accountTrxns;

    private MifosLogger logger;

    protected AccountPaymentEntity() {
        this(null, null, null, null, null, new DateTimeService().getCurrentJavaDateTime());
    }

    public AccountPaymentEntity(AccountBO account, Money amount, String receiptNumber, Date receiptDate,
            PaymentTypeEntity paymentType, Date paymentDate) {
        this.accountTrxns = new HashSet<AccountTrxnEntity>();
        this.paymentDate = paymentDate;
        this.account = account;
        this.receiptNumber = receiptNumber;
        this.paymentType = paymentType;
        this.receiptDate = receiptDate;
        this.amount = amount;
        this.bankName = null;
        this.voucherNumber = null;
        this.checkNumber = null;
        this.logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public AccountBO getAccount() {
        return account;
    }

    public PaymentTypeEntity getPaymentType() {
        return paymentType;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public Set<AccountTrxnEntity> getAccountTrxns() {
        return accountTrxns;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setAccountTrxns(Set<AccountTrxnEntity> accountTrxns) {
        this.accountTrxns = accountTrxns;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public void addAccountTrxn(AccountTrxnEntity accountTrxn) {
        accountTrxns.add(accountTrxn);
    }

    /**
     * Create reverse entries of all the transactions associated with this
     * payment and adds them to the set of transactions associated.
     */
    List<AccountTrxnEntity> reversalAdjustment(PersonnelBO personnel, String adjustmentComment) throws AccountException {
        List<AccountTrxnEntity> newlyAddedTrxns = null;
        this.setAmount(getAmount().subtract(getAmount()));
        logger.debug("The amount in account payment is " + getAmount().getAmountDoubleValue());

        if (null != getAccountTrxns() && getAccountTrxns().size() > 0) {
            newlyAddedTrxns = new ArrayList<AccountTrxnEntity>();
            logger.debug("The number of transactions before adjustment are " + getAccountTrxns().size());
            Set<AccountTrxnEntity> reverseAccntTrxns = generateReverseAccountTransactions(personnel, adjustmentComment);

            for (AccountTrxnEntity reverseAccntTrxn : reverseAccntTrxns) {
                addAccountTrxn(reverseAccntTrxn);
            }

            newlyAddedTrxns.addAll(reverseAccntTrxns);
        }

        logger.debug("After adding adjustment transactions the total no of transactions are "
                + getAccountTrxns().size());
        return newlyAddedTrxns;
    }

    private Set<AccountTrxnEntity> generateReverseAccountTransactions(PersonnelBO personnel, String adjustmentComment)
            throws AccountException {
        Set<AccountTrxnEntity> reverseAccntTrxns = new HashSet<AccountTrxnEntity>();
        for (AccountTrxnEntity accntTrxn : getAccountTrxns()) {
            logger.debug("Generating reverse transactions for transaction id " + accntTrxn.getAccountTrxnId());
            AccountTrxnEntity reverseAccntTrxn = accntTrxn.generateReverseTrxn(personnel, adjustmentComment);
            logger.debug("Amount associated with reverse transaction is "
                    + reverseAccntTrxn.getAmount().getAmountDoubleValue());
            reverseAccntTrxns.add(reverseAccntTrxn);
            logger.debug("After succesfully adding the reverse transaction");
        }
        return reverseAccntTrxns;
    }

    @Override
    public String toString() {
        return "{" + paymentId + ", " + account + ", " + paymentType + ", " + amount + "}";
    }
}

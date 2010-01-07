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

package org.mifos.application.collectionsheet.business;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class CollSheetLnDetailsEntity extends PersistentObject {

    private Long loanDetailsId;

    private CollSheetCustBO collectionSheetCustomer;

    private Integer collectionSheetId;

    private Integer accountId;

    private Money totalPrincipalDue;

    private Money originalLoanAmnt;

    private Short totalNoOfInstallments;

    private Short currentInstallmentNo;

    private Money principalDue;

    private Money interestDue;

    private Money feesDue;

    private Money penaltyDue;

    private Money principalOverDue;

    private Money interestOverDue;

    private Money feesOverDue;

    private Money penaltyOverDue;

    private Money amntToBeDisbursed;
    
    /*
     * Collection sheets can only handle one currency for now.  For the time being punt on
     * figuring it out and just use the default currency.
     */
    public MifosCurrency getCurrency() {
        return Money.getDefaultCurrency();
    }
    
    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Money getAmntToBeDisbursed() {
        return amntToBeDisbursed;
    }

    public void setAmntToBeDisbursed(Money amntToBeDisbursed) {
        this.amntToBeDisbursed = amntToBeDisbursed;
    }

    public Money getAmntToCloseLoan() {
        return getTotalPrincipalDue().add(getInterestDue()).add(getInterestOverDue()).add(getFeesDue()).add(
                getFeesOverDue()).add(getPenaltyDue()).add(getPenaltyOverDue());
    }

    public void setAmntToCloseLoan(Money amntToCloseLoan) {

    }

    public CollSheetCustBO getCollectionSheetCustomer() {
        return collectionSheetCustomer;
    }

    public void setCollectionSheetCustomer(CollSheetCustBO collectionSheetCustomer) {
        this.collectionSheetCustomer = collectionSheetCustomer;
    }

    public Integer getCollectionSheetId() {
        return collectionSheetId;
    }

    public void setCollectionSheetId(Integer collectionSheetId) {
        this.collectionSheetId = collectionSheetId;
    }

    public Short getCurrentInstallmentNo() {
        return currentInstallmentNo;
    }

    public void setCurrentInstallmentNo(Short currentInstallmentId) {
        this.currentInstallmentNo = currentInstallmentId;
    }

    public Money getFeesDue() {
        return feesDue;
    }

    public void setFeesDue(Money feesDue) {
        this.feesDue = feesDue;
    }

    public Money getFeesOverDue() {
        return feesOverDue;
    }

    public void setFeesOverDue(Money feesOverDue) {
        this.feesOverDue = feesOverDue;
    }

    public Money getInterestDue() {
        return interestDue;
    }

    public void setInterestDue(Money interestDue) {
        this.interestDue = interestDue;
    }

    public Money getInterestOverDue() {
        return interestOverDue;
    }

    public void setInterestOverDue(Money interestOverDue) {
        this.interestOverDue = interestOverDue;
    }

    public Long getLoanDetailsId() {
        return loanDetailsId;
    }

    public void setLoanDetailsId(Long loanDetailsId) {
        this.loanDetailsId = loanDetailsId;
    }

    public Money getOriginalLoanAmnt() {
        return originalLoanAmnt;
    }

    public void setOriginalLoanAmnt(Money originalLoanAmnt) {
        this.originalLoanAmnt = originalLoanAmnt;
    }

    public Money getPenaltyDue() {
        return penaltyDue;
    }

    public void setPenaltyDue(Money penaltyDue) {
        this.penaltyDue = penaltyDue;
    }

    public Money getPenaltyOverDue() {
        return penaltyOverDue;
    }

    public void setPenaltyOverDue(Money penaltyOverDue) {
        this.penaltyOverDue = penaltyOverDue;
    }

    public Money getPrincipalDue() {
        return principalDue;
    }

    public void setPrincipalDue(Money principalDue) {
        this.principalDue = principalDue;
    }

    public Money getPrincipalOverDue() {
        return principalOverDue;
    }

    public void setPrincipalOverDue(Money principalOverDue) {
        this.principalOverDue = principalOverDue;
    }

    /**
     * We store this in the database. Not sure whether this is intentional (e.g.
     * to ease reporting) or as a historical artifact.
     */
    public Money getTotalAmntDue() {
        return getTotalScheduledAmntDue().add(getTotalAmntOverDue());
    }

    /**
     * When reading from the database, we ignore this in favor of the individual
     * fields.
     */
    public void setTotalAmntDue(Money totalAmntDue) {
    }

    /**
     * We store this in the database. Not sure whether this is intentional (e.g.
     * to ease reporting) or as a historical artifact.
     */
    public Money getTotalAmntOverDue() {
        return new Money(getCurrency()).add(getPrincipalOverDue()).add(getInterestOverDue()).add(getFeesOverDue()).add(
                getPenaltyOverDue());
    }

    /**
     * When reading from the database, we ignore this in favor of the individual
     * fields.
     */
    public void setTotalAmntOverDue(Money totalAmntOverDue) {
    }

    public Short getTotalNoOfInstallments() {
        return totalNoOfInstallments;
    }

    public void setTotalNoOfInstallments(Short totalNoOfInstallments) {
        this.totalNoOfInstallments = totalNoOfInstallments;
    }

    public Money getTotalPrincipalDue() {
        return this.totalPrincipalDue;
    }

    public void setTotalPrincipalDue(Money totalPrincipalDue) {
        this.totalPrincipalDue = totalPrincipalDue;
    }

    /**
     * We store this in the database. Not sure whether this is intentional (e.g.
     * to ease reporting) or as a historical artifact.
     */
    public Money getTotalScheduledAmntDue() {
        return new Money(getCurrency()).add(getPrincipalDue()).add(getInterestDue()).add(getFeesDue()).add(getPenaltyDue());
    }

    /**
     * When reading from the database, we ignore this in favor of the individual
     * fields.
     */
    public void setTotalScheduledAmntDue(Money totalScheduledAmntDue) {
    }

    /**
     * This method sets account details from accountActionDate.This method also
     * calculates over due amount till the current installment and adds them to
     * the respedctive fields.Overdue amount for an installment is the amount
     * due till the (installment-1)
     */
    public void addAccountDetails(AccountActionDateEntity accountActionDate) throws SystemException,
            ApplicationException {
        LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
        LoanBO loan = new LoanPersistence().getAccount(loanSchedule.getAccount().getAccountId());
        this.accountId = loanSchedule.getAccount().getAccountId();
        this.currentInstallmentNo = loanSchedule.getInstallmentId();
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
                "installment no i n accounts actions date is 4$$$$$$$$$$$$$$$$$$"
                        + accountActionDate.getInstallmentId());
        this.principalDue = loanSchedule.getPrincipalDue();
        this.interestDue = loanSchedule.getInterestDue();
        this.penaltyDue = loanSchedule.getPenaltyDue().add(loanSchedule.getMiscPenalty());
        this.originalLoanAmnt = loan.getLoanAmount();
        this.feesDue = loanSchedule.getTotalFeeDue().add(loanSchedule.getMiscFeeDue());
        OverDueAmounts overDueAmounts = loan.getOverDueAmntsUptoInstallment(loanSchedule.getInstallmentId());
        this.principalOverDue = overDueAmounts.getPrincipalOverDue();
        this.feesOverDue = overDueAmounts.getFeesOverdue();
        this.penaltyOverDue = overDueAmounts.getPenaltyOverdue();
        this.interestOverDue = overDueAmounts.getInterestOverdue();
        this.totalNoOfInstallments = loan.getNoOfInstallments();
        this.totalPrincipalDue = this.originalLoanAmnt.subtract(overDueAmounts.getTotalPrincipalPaid());
    }

    /**
     * If the loanDetailsId is not null it compares for equality based on it
     * else it checks for collectionSheetCustomer not being null in which case
     * it returns true if both loanDetailsId and
     * collectionSheetLoanDetails.collSheetCustomer is equal to the
     * corresponding properties of the passed object else it calls
     * super.equals();
     * 
     * @param obj
     *            - Object to be compared for equality.
     * @return - Returns true if the objects are equal else returns false.
     */
    @Override
    public boolean equals(Object obj) {
        CollSheetLnDetailsEntity collectionSheetLoanDetailsObj = (CollSheetLnDetailsEntity) obj;
        if (null != loanDetailsId && null != collectionSheetLoanDetailsObj.getLoanDetailsId()) {
            return loanDetailsId.equals(collectionSheetLoanDetailsObj.getLoanDetailsId());
        } else if (null != this.collectionSheetCustomer
                && null != collectionSheetLoanDetailsObj.getCollectionSheetCustomer()) {
            return this.accountId.equals(collectionSheetLoanDetailsObj.getAccountId())
                    && this.collectionSheetCustomer.equals(collectionSheetLoanDetailsObj.getCollectionSheetCustomer());
        } else {
            return super.equals(collectionSheetLoanDetailsObj);
        }

    }

    @Override
    public int hashCode() {
        return this.accountId.hashCode();
    }

    /**
     * This sets accountId,amntToBeDisbursed and sets installment id to null
     * because installments haven't begun yet.
     */
    public void addDisbursalDetails(LoanBO loan) {
        MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("inside add disbursaldetails");

        this.accountId = loan.getAccountId();
        // TODO check in case of interest deducted at disbursement this should
        // show the loan amount to be disbursed.
        this.amntToBeDisbursed = loan.getLoanAmount();
        if (loan.isInterestDeductedAtDisbursement()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) loan
                    .getAccountActionDate(CollectionSheetConstants.FIRST_INSTALLMENT);
            this.interestDue = loanSchedule.getInterestDue();
            this.feesDue = loanSchedule.getTotalFeeDue();
        }
        this.currentInstallmentNo = null;
        this.totalNoOfInstallments = loan.getNoOfInstallments();
        this.originalLoanAmnt = loan.getLoanAmount();
        this.totalPrincipalDue = this.originalLoanAmnt;
    }

    public Money getTotalFees() {
        return MoneyUtils.add(getFeesDue(), getFeesOverDue());
    }

    public Money getTotalPenalty() {
        return MoneyUtils.add(getPenaltyDue(), getPenaltyOverDue());
    }
}

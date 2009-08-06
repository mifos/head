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

package org.mifos.application.accounts.loan.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanPaymentTypes;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.FeeInstallment;
import org.mifos.application.accounts.util.helpers.InstallmentDate;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.config.AccountingRules;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanBO extends AccountBO {

    private final LoanOfferingBO loanOffering;

    private final LoanSummaryEntity loanSummary;

    private MaxMinLoanAmount maxMinLoanAmount;

    private MaxMinNoOfInstall maxMinNoOfInstall;

    private LoanPrdPersistence loanPrdPersistence;

    private Money loanAmount;

    private Money loanBalance;

    private Short noOfInstallments;

    private Date disbursementDate;

    private MeetingBO loanMeeting;

    private Short intrestAtDisbursement;

    private GracePeriodTypeEntity gracePeriodType;

    private Short gracePeriodDuration;

    private Short gracePeriodPenalty;

    private final LoanPerformanceHistoryEntity performanceHistory;

    private InterestTypesEntity interestType;

    private Double interestRate;

    private FundBO fund;

    private Boolean redone;

    /**
     * Is this used? Is it related to the activity IDs in places like
     * {@link ActivityMapper#SAVING_CANCHANGESTATETO_PARTIALAPPLICATION} or
     * {@link SecurityConstants#FUNDS_CREATE_FUNDS} ?
     */
    private Integer businessActivityId;

    private Integer collateralTypeId;

    private String collateralNote;

    private Short groupFlag;

    private String stateSelected;

    private Set<LoanActivityEntity> loanActivityDetails;

    private LoanArrearsAgingEntity loanArrearsAgingEntity;

    // For Group loan with individual monitoring

    private LoanBO parentAccount;

    private Set<LoanBO> loanAccountDetails;

    // For Repayment Day

    private WeekDaysEntity monthWeek;

    private RankOfDaysEntity monthRank;

    private Short recurMonth;

    private Money rawAmountTotal;

    protected LoanBO() {
        this(null, null, null, null, null);
        this.loanPrdPersistence = null;
        this.loanActivityDetails = new HashSet<LoanActivityEntity>();
        this.redone = false;
        parentAccount = null;
        loanAccountDetails = new HashSet<LoanBO>();
    }

    // FIXME used by test, test should try to use other constructors or factory
    // methods
    private LoanBO(LoanOfferingBO loanOffering, LoanSummaryEntity loanSummary, MaxMinLoanAmount maxMinLoanAmount,
            MaxMinNoOfInstall maxMinNoOfInstall, LoanPerformanceHistoryEntity performanceHistory) {
        this.loanOffering = loanOffering;
        this.loanSummary = loanSummary;
        this.maxMinLoanAmount = maxMinLoanAmount;
        this.maxMinNoOfInstall = maxMinNoOfInstall;
        this.performanceHistory = performanceHistory;
    }

    private LoanBO(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer,
            AccountState accountState, Money loanAmount, Short noOfinstallments, Date disbursementDate,
            boolean interestDeductedAtDisbursement, Double interestRate, Short gracePeriodDuration, FundBO fund,
            List<FeeView> feeViews, List<CustomFieldView> customFields, Boolean isRedone,
            boolean isRepaymentIndepOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay) throws AccountException {
        this(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments, disbursementDate,
                interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews, customFields,
                isRedone, AccountTypes.LOAN_ACCOUNT, isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
    }

    public LoanBO(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer, AccountState accountState,
            Money loanAmount, Short noOfinstallments, Date disbursementDate, boolean interestDeductedAtDisbursement,
            Double interestRate, Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
            List<CustomFieldView> customFields, Boolean isRedone, Double maxLoanAmount, Double minLoanAmount,
            Short maxNoOfInstall, Short minNoOfInstall, boolean isRepaymentIndepOfMeetingEnabled,
            MeetingBO newMeetingForRepaymentDay) throws AccountException {
        this(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments, disbursementDate,
                interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews, customFields,
                isRedone, AccountTypes.LOAN_ACCOUNT, isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
        this.maxMinLoanAmount = new MaxMinLoanAmount(maxLoanAmount, minLoanAmount, this);
        this.maxMinNoOfInstall = new MaxMinNoOfInstall(maxNoOfInstall, minNoOfInstall, this);
    }

    private LoanBO(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer,
            AccountState accountState, Money loanAmount, Short noOfinstallments, Date disbursementDate,
            boolean interestDeductedAtDisbursement, Double interestRate, Short gracePeriodDuration, FundBO fund,
            List<FeeView> feeViews, List<CustomFieldView> customFields, Boolean isRedone, AccountTypes accountType,
            boolean isRepaymentIndepOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay) throws AccountException {
        super(userContext, customer, accountType, accountState);
        setCreateDetails();
        this.redone = isRedone;
        this.loanOffering = loanOffering;
        this.loanAmount = loanAmount;
        this.loanBalance = loanAmount;
        this.noOfInstallments = noOfinstallments;
        this.interestType = loanOffering.getInterestTypes();
        this.interestRate = interestRate;
        setInterestDeductedAtDisbursement(interestDeductedAtDisbursement);
        setGracePeriodTypeAndDuration(interestDeductedAtDisbursement, gracePeriodDuration, noOfinstallments);
        this.gracePeriodPenalty = Short.valueOf("0");
        this.fund = fund;
        this.loanMeeting = buildLoanMeeting(customer.getCustomerMeeting().getMeeting(), loanOffering
                .getLoanOfferingMeeting().getMeeting(), disbursementDate);
        buildAccountFee(feeViews);
        this.disbursementDate = disbursementDate;
        this.performanceHistory = new LoanPerformanceHistoryEntity(this);
        this.loanActivityDetails = new HashSet<LoanActivityEntity>();
        generateMeetingSchedule(isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
        this.loanSummary = buildLoanSummary();
        this.maxMinLoanAmount = null;
        this.maxMinNoOfInstall = null;
        addcustomFields(customFields);
    }

    public static LoanBO redoLoan(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer,
            AccountState accountState, Money loanAmount, Short noOfinstallments, Date disbursementDate,
            boolean interestDeductedAtDisbursement, Double interestRate, Short gracePeriodDuration, FundBO fund,
            List<FeeView> feeViews, List<CustomFieldView> customFields, Double maxLoanAmount, Double minLoanAmount,
            Short maxNoOfInstall, Short minNoOfInstall, boolean isRepaymentIndepOfMeetingEnabled,
            MeetingBO newMeetingForRepaymentDay) throws AccountException {
        if (loanOffering == null || loanAmount == null || noOfinstallments == null || disbursementDate == null
                || interestRate == null)
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION);

        if (!customer.isActive()) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
        }

        if (!loanOffering.isActive()) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);
        }

        if (interestDeductedAtDisbursement == true && noOfinstallments.shortValue() <= 1) {
            throw new AccountException(LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
        }

        if (!isDisbursementDateLessThanCurrentDate(disbursementDate)) {
            throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE_FOR_REDO_LOAN);
        }

        if (!isDibursementDateValidForRedoLoan(loanOffering, customer, disbursementDate)) {
            throw new AccountException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
        }

        if (isRepaymentIndepOfMeetingEnabled == false)
            if (!isDisbursementDateValid(customer, disbursementDate)) {
                throw new AccountException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
            }
        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments,
                disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews,
                customFields, true, maxLoanAmount, minLoanAmount, maxNoOfInstall, minNoOfInstall,
                isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
    }

    public static LoanBO createIndividualLoan(UserContext userContext, LoanOfferingBO loanOffering,
            CustomerBO customer, AccountState accountState, Money loanAmount, Short noOfinstallments,
            Date disbursementDate, boolean interestDeductedAtDisbursement, boolean isRepaymentIndepOfMeetingEnabled,
            Double interestRate, Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews,
            List<CustomFieldView> customFields) throws AccountException {

        commonValidationsForCreateAndRedoIndividualLoans(loanOffering, customer, loanAmount, noOfinstallments,
                disbursementDate, interestRate, isRepaymentIndepOfMeetingEnabled, interestDeductedAtDisbursement);

        if (isDisbursementDateLessThanCurrentDate(disbursementDate))
            throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);

        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments,
                disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews,
                customFields, false, AccountTypes.INDIVIDUAL_LOAN_ACCOUNT, false, null);
    }

    public static LoanBO redoIndividualLoan(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer,
            AccountState accountState, Money loanAmount, Short noOfinstallments, Date disbursementDate,
            boolean interestDeductedAtDisbursement, boolean isRepaymentIndepOfMeetingEnabled, Double interestRate,
            Short gracePeriodDuration, FundBO fund, List<FeeView> feeViews, List<CustomFieldView> customFields)
            throws AccountException {

        commonValidationsForCreateAndRedoIndividualLoans(loanOffering, customer, loanAmount, noOfinstallments,
                disbursementDate, interestRate, isRepaymentIndepOfMeetingEnabled, interestDeductedAtDisbursement);

        if (!isDisbursementDateLessThanCurrentDate(disbursementDate))
            throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);

        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments,
                disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews,
                customFields, false, AccountTypes.INDIVIDUAL_LOAN_ACCOUNT, false, null);
    }

    private static void commonValidationsForCreateAndRedoIndividualLoans(LoanOfferingBO loanOffering,
            CustomerBO customer, Money loanAmount, Short noOfinstallments, Date disbursementDate, Double interestRate,
            boolean isRepaymentIndepOfMeetingEnabled, boolean interestDeductedAtDisbursement) throws AccountException {
        if (isAnyLoanParamsNull(loanOffering, customer, loanAmount, noOfinstallments, disbursementDate, interestRate)) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION);
        }

        if (!customer.isActive()) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
        }

        if (!loanOffering.isActive()) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);
        }

        if (!isRepaymentIndepOfMeetingEnabled && !isDisbursementDateValid(customer, disbursementDate)) {
            throw new AccountException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
        }

        if (interestDeductedAtDisbursement && noOfinstallments.shortValue() <= 1) {
            throw new AccountException(LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
        }
    }

    private static boolean isAnyLoanParamsNull(Object... args) {
        return Arrays.asList(args).contains(null);
    }

    public static LoanBO createLoan(UserContext userContext, LoanOfferingBO loanOffering, CustomerBO customer,
            AccountState accountState, Money loanAmount, Short noOfinstallments, Date disbursementDate,
            boolean interestDeductedAtDisbursement, Double interestRate, Short gracePeriodDuration, FundBO fund,
            List<FeeView> feeViews, List<CustomFieldView> customFields, Double maxLoanAmount, Double minLoanAmount,
            Short maxNoOfInstall, Short minNoOfInstall, boolean isRepaymentIndepOfMeetingEnabled,
            MeetingBO newMeetingForRepaymentDay) throws AccountException {
        if (isAnyLoanParamsNull(loanOffering, customer, loanAmount, noOfinstallments, disbursementDate, interestRate))
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION);

        if (!customer.isActive()) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONCUSTOMERINACTIVE);
        }

        if (!loanOffering.isActive()) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTIONPRDINACTIVE);
        }

        if (isDisbursementDateLessThanCurrentDate(disbursementDate)) {
            throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
        }

        if (!isRepaymentIndepOfMeetingEnabled)
            if (!isDisbursementDateValid(customer, disbursementDate)) {
                throw new AccountException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
            }

        if (interestDeductedAtDisbursement && noOfinstallments.shortValue() <= 1) {

            throw new AccountException(LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
        }

        return new LoanBO(userContext, loanOffering, customer, accountState, loanAmount, noOfinstallments,
                disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeViews,
                customFields, false, maxLoanAmount, minLoanAmount, maxNoOfInstall, minNoOfInstall,
                isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
    }

    public static LoanBO createInstanceForTest(LoanOfferingBO loanOffering) {
        return new LoanBO(loanOffering, null, null, null, null);
    }

    public Integer getBusinessActivityId() {
        return businessActivityId;
    }

    public void setBusinessActivityId(Integer businessActivityId) {
        this.businessActivityId = businessActivityId;
    }

    public String getCollateralNote() {
        return collateralNote;
    }

    public void setCollateralNote(String collateralNote) {
        this.collateralNote = collateralNote;
    }

    public Integer getCollateralTypeId() {
        return collateralTypeId;
    }

    public void setCollateralTypeId(Integer collateralTypeId) {
        this.collateralTypeId = collateralTypeId;
    }

    public GracePeriodTypeEntity getGracePeriodType() {
        return gracePeriodType;
    }

    public GraceType getGraceType() {
        return gracePeriodType.asEnum();
    }

    void setGracePeriodType(GracePeriodTypeEntity gracePeriodType) {
        this.gracePeriodType = gracePeriodType;
    }

    void setGracePeriodType(GraceType type) {
        setGracePeriodType(new GracePeriodTypeEntity(type));
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public FundBO getFund() {
        return fund;
    }

    public void setFund(FundBO fund) {
        this.fund = fund;
    }

    public Short getGracePeriodDuration() {
        return gracePeriodDuration;
    }

    void setGracePeriodDuration(Short gracePeriodDuration) {
        this.gracePeriodDuration = gracePeriodDuration;
    }

    public Short getGracePeriodPenalty() {
        return gracePeriodPenalty;
    }

    void setGracePeriodPenalty(Short gracePeriodPenalty) {
        this.gracePeriodPenalty = gracePeriodPenalty;
    }

    public Short getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(Short groupFlag) {
        this.groupFlag = groupFlag;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public InterestTypesEntity getInterestType() {
        return interestType;
    }

    void setInterestType(InterestTypesEntity interestType) {
        this.interestType = interestType;
    }

    public boolean isInterestDeductedAtDisbursement() {
        return intrestAtDisbursement == LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT;
    }

    void setInterestDeductedAtDisbursement(boolean interestDedAtDisb) {
        this.intrestAtDisbursement = interestDedAtDisb ? Constants.YES : Constants.NO;
    }

    public Money getLoanAmount() {
        return loanAmount;
    }

    void setLoanAmount(Money loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Money getLoanBalance() {
        return loanBalance;
    }

    void setLoanBalance(Money loanBalance) {
        this.loanBalance = loanBalance;
    }

    public MeetingBO getLoanMeeting() {
        return loanMeeting;
    }

    void setLoanMeeting(MeetingBO loanMeeting) {
        this.loanMeeting = loanMeeting;
    }

    public LoanOfferingBO getLoanOffering() {
        return loanOffering;
    }

    public LoanSummaryEntity getLoanSummary() {
        return loanSummary;
    }

    public Short getNoOfInstallments() {
        return noOfInstallments;
    }

    void setNoOfInstallments(Short noOfInstallments) {
        this.noOfInstallments = noOfInstallments;
    }

    public String getStateSelected() {
        return stateSelected;
    }

    public void setStateSelected(String stateSelected) {
        this.stateSelected = stateSelected;
    }

    public LoanPerformanceHistoryEntity getPerformanceHistory() {
        return performanceHistory;
    }

    public Set<LoanActivityEntity> getLoanActivityDetails() {
        return loanActivityDetails;
    }

    public void addLoanActivity(LoanActivityEntity loanActivity) {
        this.loanActivityDetails.add(loanActivity);
    }

    public LoanArrearsAgingEntity getLoanArrearsAgingEntity() {
        return loanArrearsAgingEntity;
    }

    public void setLoanArrearsAgingEntity(LoanArrearsAgingEntity loanArrearsAgingEntity) {
        this.loanArrearsAgingEntity = loanArrearsAgingEntity;
    }

    @Override
    public AccountTypes getType() {
        return AccountTypes.getAccountType(getAccountType().getAccountTypeId());
    }

    @Override
    public boolean isOpen() {
        AccountState loanAccountState = AccountState.fromShort(getAccountState().getId());
        List<AccountState> notOpenAccountStates = Arrays.asList(AccountState.LOAN_CANCELLED,
                AccountState.LOAN_CLOSED_RESCHEDULED, AccountState.LOAN_CLOSED_OBLIGATIONS_MET,
                AccountState.LOAN_CLOSED_WRITTEN_OFF);
        return !notOpenAccountStates.contains(loanAccountState);
    }

    /**
     * Update LoanSummaryEntity by subtracting amount of removed fees.
     */
    @Override
    protected void updateTotalFeeAmount(Money totalFeeAmount) {
        LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
        loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees().subtract(totalFeeAmount));
    }

    @Override
    protected void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
        LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
        loanSummaryEntity.setOriginalPenalty(loanSummaryEntity.getOriginalPenalty().subtract(totalPenaltyAmount));
    }

    @Override
    public boolean isAdjustPossibleOnLastTrxn() {
        // adjustment is possible only if account state is
        // 1. active in good standing.
        // 2. active in bad standing.
        // 3. Closed - Obligation Met : Check permisssion first ; Can adjust
        // payment when account status is "closed-obligation met"

        if (!(getAccountState().getId().equals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue()) || getAccountState()
                .getId().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue()))) {

            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "State is not active hence adjustment is not possible");
            return false;
        }

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Total payments on this account is  " + getAccountPayments().size());
        AccountPaymentEntity accountPayment = getLastPmntToBeAdjusted();
        if (accountPayment != null) {
            for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
                LoanTrxnDetailEntity lntrxn = (LoanTrxnDetailEntity) accntTrxn;
                if (lntrxn.getInstallmentId().equals(Short.valueOf("0"))
                        || isAdjustmentForInterestDedAtDisb(lntrxn.getInstallmentId())) {
                    return false;
                }
            }
        }
        if (null != getLastPmntToBeAdjusted() && getLastPmntAmntToBeAdjusted() != 0) {
            return true;
        }
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Adjustment is not possible ");
        return false;
    }

    @Override
    protected void updateAccountActivity(Money principal, Money interest, Money fee, Money penalty, Short personnelId,
            String description) throws AccountException {
        try {
            PersonnelBO personnel = new PersonnelPersistence().getPersonnel(personnelId);
            LoanActivityEntity loanActivity = new LoanActivityEntity(this, personnel, description, principal,
                    loanSummary.getOriginalPrincipal().subtract(loanSummary.getPrincipalPaid()), interest, loanSummary
                            .getOriginalInterest().subtract(loanSummary.getInterestPaid()), fee, loanSummary
                            .getOriginalFees().subtract(loanSummary.getFeesPaid()), penalty, loanSummary
                            .getOriginalPenalty().subtract(loanSummary.getPenaltyPaid()), DateUtils
                            .getCurrentDateWithoutTimeStamp());
            this.addLoanActivity(loanActivity);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    @Override
    public void waiveAmountDue(WaiveEnum waiveType) throws AccountException {
        if (waiveType.equals(WaiveEnum.FEES)) {
            waiveFeeAmountDue();
        } else if (waiveType.equals(WaiveEnum.PENALTY)) {
            waivePenaltyAmountDue();
        }
    }

    @Override
    public void waiveAmountOverDue(WaiveEnum waiveType) throws AccountException {
        if (waiveType.equals(WaiveEnum.FEES)) {
            waiveFeeAmountOverDue();
        } else if (waiveType.equals(WaiveEnum.PENALTY)) {
            waivePenaltyAmountOverDue();
        }
    }

    public Money getTotalPrincipalAmount() {
        Money amount = new Money();
        List<AccountActionDateEntity> installments = getAllInstallments();
        for (AccountActionDateEntity accountActionDateEntity : installments) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipal());
        }
        return amount;
    }

    public Money getTotalPrincipalAmountInArrears() {
        Money amount = new Money();
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipal());
        }
        return amount;
    }

    public Money getTotalPrincipalAmountInArrearsAndOutsideLateness() throws PersistenceException {
        Money amount = new Money();
        loanPrdPersistence = new LoanPrdPersistence();
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            if (!accountActionDateEntity.isPaid()
                    && currentDate.getTime() - accountActionDateEntity.getActionDate().getTime() > loanPrdPersistence
                            .retrieveLatenessForPrd().intValue()
                            * 24 * 60 * 60 * 1000) {
                amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipalDue());
            }
        }
        return amount;
    }

    public Money getTotalInterestAmountInArrears() {
        Money amount = new Money();
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getInterest());
        }
        return amount;
    }

    public Money getTotalInterestAmountInArrearsAndOutsideLateness() throws PersistenceException {
        Money amount = new Money();
        loanPrdPersistence = new LoanPrdPersistence();
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            if (currentDate.getTime() - accountActionDateEntity.getActionDate().getTime() > loanPrdPersistence
                    .retrieveLatenessForPrd().intValue()
                    * 24 * 60 * 60 * 1000) {
                amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getInterest());
            }
        }
        return amount;
    }

    /**
     * Remove the fee from all unpaid current or future installments, and update
     * the loan accordingly.
     */
    @Override
    public final void removeFees(Short feeId, Short personnelId) throws AccountException {
        List<Short> installmentIds = getApplicableInstallmentIdsForRemoveFees();
        Money totalFeeAmount = new Money();
        if (installmentIds != null && installmentIds.size() != 0 && isFeeActive(feeId)) {

            FeeBO fee = getAccountFeesObject(feeId);
            if (havePaymentsBeenMade() && fee.doesFeeInvolveFractionalAmounts()) {
                throw new AccountException(AccountExceptionConstants.CANT_APPLY_FEE_EXCEPTION);
            }

            totalFeeAmount = updateAccountActionDateEntity(installmentIds, feeId);
            updateAccountFeesEntity(feeId);
            updateTotalFeeAmount(totalFeeAmount);
            FeeBO feesBO = getAccountFeesObject(feeId);
            String description = feesBO.getFeeName() + " " + AccountConstants.FEES_REMOVED;
            updateAccountActivity(null, null, totalFeeAmount, null, personnelId, description);

            if (!havePaymentsBeenMade()) {
                applyRounding();
            }

            try {
                (new AccountPersistence()).createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }

    }

    /**
     * Remove unpaid or partially paid fee from each installment whose id is in
     * installmentIdList, and return the total of all unpaid fees that were
     * removed.
     */
    @Override
    public Money updateAccountActionDateEntity(List<Short> intallmentIdList, Short feeId) {
        Money totalFeeAmount = new Money();
        Set<AccountActionDateEntity> accountActionDateEntitySet = this.getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
            if (intallmentIdList.contains(accountActionDateEntity.getInstallmentId())) {
                totalFeeAmount = totalFeeAmount.add(((LoanScheduleEntity) accountActionDateEntity).removeFees(feeId));
            }
        }
        return totalFeeAmount;
    }

    protected boolean isRoundedAmount(Double amount) {
        return isRoundedAmount(new Money(new BigDecimal(amount)));
    }

    protected boolean isRoundedAmount(Money amount) {
        return amount.equals(initialRound_v2(amount)) && amount.equals(finalRound_v2(amount));
    }

    protected boolean havePaymentsBeenMade() {
        for (AccountActionDateEntity accountActionDateEntity : getAllInstallments()) {
            LoanScheduleEntity installment = (LoanScheduleEntity) accountActionDateEntity;
            if (installment.isPaymentApplied()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void applyCharge(Short feeId, Double charge) throws AccountException, PersistenceException {
        List<AccountActionDateEntity> dueInstallments = getTotalDueInstallments();
        if (dueInstallments.isEmpty())
            throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
        if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
                || feeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
            applyMiscCharge(feeId, new Money(String.valueOf(charge)), dueInstallments.get(0));

            // Don't re-apply rounding to already-rounded charges, since
            // it will have no effect
            if (!havePaymentsBeenMade()) {
                applyRounding();
            }
        } else {
            FeeBO fee = new FeePersistence().getFee(feeId);

            if (havePaymentsBeenMade() && (fee.doesFeeInvolveFractionalAmounts() || !isRoundedAmount(charge))) {
                throw new AccountException(AccountExceptionConstants.CANT_APPLY_FEE_EXCEPTION);
            }

            if (fee.getFeeFrequency().getFeePayment() != null) {
                applyOneTimeFee(fee, charge, dueInstallments.get(0));
            } else {
                applyPeriodicFee(fee, charge, dueInstallments);
            }
            if (!havePaymentsBeenMade()) {
                applyRounding();
            }
        }
    }

    /**
     * It calculates over due amounts till installment 1 less than the one
     * passed,because whatever amount is associated with the current installment
     * it is the due amount and not the over due amount. It calculates that by
     * iterating over the accountActionDates associated and summing up all the
     * principal and principalPaid till installment-1 and then returning the
     * difference of the two.It also takes into consideration any miscellaneous
     * fee or miscellaneous penalty.
     * 
     * @param installmentId
     *            - Installment id till which we want over due amounts.
     * 
     */
    public OverDueAmounts getOverDueAmntsUptoInstallment(Short installmentId) throws AccountException {
        Set<AccountActionDateEntity> accountActionDateEntities = getAccountActionDates();
        OverDueAmounts totalOverDueAmounts = new OverDueAmounts();
        if (null != accountActionDateEntities && accountActionDateEntities.size() > 0) {
            Iterator<AccountActionDateEntity> accountActionDatesIterator = accountActionDateEntities.iterator();
            while (accountActionDatesIterator.hasNext()) {
                LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDatesIterator.next();

                if (accountActionDateEntity.getInstallmentId() < installmentId) {
                    OverDueAmounts dueAmounts = new OverDueAmounts();
                    dueAmounts = accountActionDateEntity.getDueAmnts();
                    totalOverDueAmounts.add(dueAmounts);
                }
            }
        }
        return totalOverDueAmounts;
    }

    public void disburseLoan(String recieptNum, Date transactionDate, Short paymentTypeId, PersonnelBO personnel,
            Date receiptDate, Short rcvdPaymentTypeId) throws AccountException {
        disburseLoan(recieptNum, transactionDate, paymentTypeId, personnel, receiptDate, rcvdPaymentTypeId, true);
    }

    public void disburseLoan(PersonnelBO personnel, Short rcvdPaymentTypeId, boolean persistChange)
            throws AccountException {
        disburseLoan(null, getDisbursementDate(), rcvdPaymentTypeId, personnel, null, rcvdPaymentTypeId, persistChange);
    }

    private void disburseLoan(String recieptNum, Date transactionDate, Short paymentTypeId, PersonnelBO personnel,
            Date receiptDate, Short rcvdPaymentTypeId, boolean persistChange) throws AccountException {
        AccountPaymentEntity accountPaymentEntity = null;
        addLoanActivity(buildLoanActivity(this.loanAmount, personnel, AccountConstants.LOAN_DISBURSAL, transactionDate));

        // if the trxn date is not equal to disbursementDate we need to
        // regenerate the installments
        if (!DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime()).equals(
                DateUtils.getDateWithoutTimeStamp(transactionDate.getTime()))) {
            this.disbursementDate = transactionDate;
            regeneratePaymentSchedule(false, null);
        }
        this.disbursementDate = transactionDate;
        AccountStateEntity newState;
        try {
            newState = (AccountStateEntity) (new MasterPersistence()).getPersistentObject(AccountStateEntity.class,
                    AccountStates.LOANACC_ACTIVEINGOODSTANDING);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }

        // update status change history also
        this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(this.getAccountState(), newState,
                personnel, this));
        this.setAccountState(newState);

        if (this.isInterestDeductedAtDisbursement()) {
            accountPaymentEntity = payInterestAtDisbursement(recieptNum, transactionDate, rcvdPaymentTypeId, personnel,
                    receiptDate);
        } else {
            try {
                if (new LoanPersistence().getFeeAmountAtDisbursement(this.getAccountId()) > 0.0)
                    accountPaymentEntity = insertOnlyFeeAtDisbursement(recieptNum, transactionDate, rcvdPaymentTypeId,
                            personnel);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
        if (null == accountPaymentEntity) {
            accountPaymentEntity = new AccountPaymentEntity(this, this.loanAmount, recieptNum, transactionDate,
                    new PaymentTypeEntity(paymentTypeId), new DateTimeService().getCurrentJavaDateTime());
        } else {
            accountPaymentEntity.setAmount(this.loanAmount.subtract(accountPaymentEntity.getAmount()));
        }

        // create trxn entry for disbursal
        LoanTrxnDetailEntity loanTrxnDetailEntity = null;
        try {
            loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                    (AccountActionEntity) new MasterPersistence().getPersistentObject(AccountActionEntity.class,
                            AccountActionTypes.DISBURSAL.getValue()), Short.valueOf("0"), transactionDate, personnel,
                    transactionDate, this.loanAmount, "-", null, this.loanAmount, new Money(), new Money(),
                    new Money(), new Money(), null);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }

        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        this.addAccountPayment(accountPaymentEntity);
        this.buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

        // Client performance entry
        updateCustomerHistoryOnDisbursement(this.loanAmount);
        if (getPerformanceHistory() != null)
            getPerformanceHistory().setLoanMaturityDate(getLastInstallmentAccountAction().getActionDate());

        if (persistChange) {
            try {
                new AccountPersistence().createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    public Money getTotalEarlyRepayAmount() {
        Money amount = new Money();
        List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
        List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
        for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getTotalDueWithFees());
        }

        for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipalDue());
        }
        return amount;
    }

    public void makeEarlyRepayment(Money totalAmount, String receiptNumber, Date recieptDate, String paymentTypeId,
            Short personnelId) throws AccountException {
        try {
            MasterPersistence masterPersistence = new MasterPersistence();
            PersonnelBO currentUser = new PersonnelPersistence().getPersonnel(personnelId);
            this.setUpdatedBy(personnelId);
            this.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, totalAmount, receiptNumber,
                    recieptDate, new PaymentTypeEntity(Short.valueOf(paymentTypeId)), new DateTimeService()
                            .getCurrentJavaDateTime());
            addAccountPayment(accountPaymentEntity);

            makeEarlyRepaymentForDueInstallments(accountPaymentEntity, AccountConstants.PAYMENT_RCVD,
                    AccountActionTypes.LOAN_REPAYMENT, currentUser);
            makeEarlyRepaymentForFutureInstallments(accountPaymentEntity, AccountConstants.PAYMENT_RCVD,
                    AccountActionTypes.LOAN_REPAYMENT, currentUser);

            if (getPerformanceHistory() != null) {
                getPerformanceHistory().setNoOfPayments(getPerformanceHistory().getNoOfPayments() + 1);
            }
            LoanActivityEntity loanActivity = buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
                    AccountConstants.LOAN_REPAYMENT, DateUtils.getCurrentDateWithoutTimeStamp());
            addLoanActivity(loanActivity);
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

            AccountStateEntity newAccountState = (AccountStateEntity) masterPersistence.getPersistentObject(
                    AccountStateEntity.class, AccountStates.LOANACC_OBLIGATIONSMET);
            addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(getAccountState(), newAccountState,
                    new PersonnelPersistence().getPersonnel(personnelId), this));
            setAccountState((AccountStateEntity) masterPersistence.getPersistentObject(AccountStateEntity.class,
                    AccountStates.LOANACC_OBLIGATIONSMET));
            setClosedDate(new DateTimeService().getCurrentJavaDateTime());

            // Client performance entry
            updateCustomerHistoryOnRepayment(totalAmount);
            this.delete(loanArrearsAgingEntity);
            loanArrearsAgingEntity = null;
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public void handleArrears() throws AccountException {
        MasterPersistence masterPersistence = new MasterPersistence();
        AccountStateEntity stateEntity;
        try {
            stateEntity = (AccountStateEntity) masterPersistence.getPersistentObject(AccountStateEntity.class,
                    AccountStates.LOANACC_BADSTANDING);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(this.getAccountState(),
                stateEntity, this.getPersonnel(), this);
        this.addAccountStatusChangeHistory(historyEntity);
        this.setAccountState(stateEntity);
        // String systemDate = DateUtils.getCurrentDate(Configuration
        // .getInstance().getSystemConfig().getMFILocale());
        // Date currrentDate =
        // DateUtils.getLocaleDate(Configuration.getInstance()
        // .getSystemConfig().getMFILocale(), systemDate);
        try {
            String systemDate = DateUtils.getCurrentDate();
            Date currrentDate = DateUtils.getLocaleDate(systemDate);
            this.setUpdatedDate(currrentDate);
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        }

        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public boolean isLastInstallment(Short installmentId) {
        Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();
        List<Object> objectList = Arrays.asList(accountActionDateSet.toArray());
        AccountActionDateEntity accountActionDateEntity = (AccountActionDateEntity) objectList
                .get(objectList.size() - 1);
        if (installmentId.equals(accountActionDateEntity.getInstallmentId()))
            return true;
        return false;
    }

    @Override
    protected void writeOff() throws AccountException {
        try {
            Short personnelId = this.getUserContext().getId();
            PersonnelBO currentUser = new PersonnelPersistence().getPersonnel(personnelId);

            this.setUpdatedBy(personnelId);
            this.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, getEarlyClosureAmount(), null,
                    null, new PaymentTypeEntity(Short.valueOf("1")), new DateTimeService().getCurrentJavaDateTime());
            this.addAccountPayment(accountPaymentEntity);
            makeEarlyRepaymentForDueInstallments(accountPaymentEntity, AccountConstants.LOAN_WRITTEN_OFF,
                    AccountActionTypes.WRITEOFF, currentUser);
            makeEarlyRepaymentForFutureInstallments(accountPaymentEntity, AccountConstants.LOAN_WRITTEN_OFF,
                    AccountActionTypes.WRITEOFF, currentUser);
            addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
                    AccountConstants.LOAN_WRITTEN_OFF, DateUtils.getCurrentDateWithoutTimeStamp()));
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
            // Client performance entry
            updateCustomerHistoryOnWriteOff();
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    @Override
    protected void reschedule() throws AccountException {
        try {
            Short personnelId = this.getUserContext().getId();
            PersonnelBO currentUser = new PersonnelPersistence().getPersonnel(personnelId);
            this.setUpdatedBy(personnelId);
            this.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, getEarlyClosureAmount(), null,
                    null, new PaymentTypeEntity(Short.valueOf("1")), new DateTimeService().getCurrentJavaDateTime());
            this.addAccountPayment(accountPaymentEntity);
            makeEarlyRepaymentForDueInstallments(accountPaymentEntity, AccountConstants.LOAN_RESCHEDULED,
                    AccountActionTypes.LOAN_RESCHEDULED, currentUser);
            makeEarlyRepaymentForFutureInstallments(accountPaymentEntity, AccountConstants.LOAN_RESCHEDULED,
                    AccountActionTypes.LOAN_RESCHEDULED, currentUser);
            addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
                    AccountConstants.LOAN_RESCHEDULED, DateUtils.getCurrentDateWithoutTimeStamp()));
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
            // Client performance entry using the same as write off.
            updateCustomerHistoryOnWriteOff();
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waiveFeeAmountDue() throws AccountException {
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
        LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
                .get(accountActionDateList.size() - 1);
        Money chargeWaived = accountActionDateEntity.waiveFeeCharges();
        if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
            updateTotalFeeAmount(chargeWaived);
            updateAccountActivity(null, null, chargeWaived, null, userContext.getId(), LoanConstants.FEE_WAIVED);
        }
        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waivePenaltyAmountDue() throws AccountException {
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
        LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
                .get(accountActionDateList.size() - 1);
        Money chargeWaived = accountActionDateEntity.waivePenaltyCharges();
        if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
            updateTotalPenaltyAmount(chargeWaived);
            updateAccountActivity(null, null, null, chargeWaived, userContext.getId(), LoanConstants.PENALTY_WAIVED);
        }
        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waiveFeeAmountOverDue() throws AccountException {
        Money chargeWaived = new Money();
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
        accountActionDateList.remove(accountActionDateList.size() - 1);
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            chargeWaived = chargeWaived.add(((LoanScheduleEntity) accountActionDateEntity).waiveFeeCharges());
        }
        if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
            updateTotalFeeAmount(chargeWaived);
            updateAccountActivity(null, null, chargeWaived, null, userContext.getId(), AccountConstants.AMOUNT
                    + chargeWaived + AccountConstants.WAIVED);
        }
        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waivePenaltyAmountOverDue() throws AccountException {
        Money chargeWaived = new Money();
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
        accountActionDateList.remove(accountActionDateList.size() - 1);
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            chargeWaived = chargeWaived.add(((LoanScheduleEntity) accountActionDateEntity).waivePenaltyCharges());
        }
        if (chargeWaived != null && chargeWaived.getAmountDoubleValue() > 0.0) {
            updateTotalPenaltyAmount(chargeWaived);
            updateAccountActivity(null, null, null, chargeWaived, userContext.getId(), AccountConstants.AMOUNT
                    + chargeWaived + AccountConstants.WAIVED);
        }
        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public Money getAmountTobePaidAtdisburtail(Date disbursalDate) throws AccountException {
        if (this.isInterestDeductedAtDisbursement()) {
            return getDueAmount(getAccountActionDate(Short.valueOf("1")));
        } else {
            try {
                return new Money(new LoanPersistence().getFeeAmountAtDisbursement(this.getAccountId()).toString());
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }

    }

    public Boolean hasPortfolioAtRisk() {
        List<AccountActionDateEntity> accountActionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            Calendar actionDate = new GregorianCalendar();
            actionDate.setTime(accountActionDateEntity.getActionDate());
            long diffInTermsOfDay = (new DateTimeService().getCurrentDateTime().getMillis() - actionDate
                    .getTimeInMillis())
                    / (24 * 60 * 60 * 1000);
            if (diffInTermsOfDay > 30) {
                return true;
            }
        }
        return false;
    }

    public Money getRemainingPrincipalAmount() {
        return loanSummary.getOriginalPrincipal().subtract(loanSummary.getPrincipalPaid());
    }

    public boolean isAccountActive() {
        return getState() == AccountState.LOAN_ACTIVE_IN_GOOD_STANDING
                || getState() == AccountState.LOAN_ACTIVE_IN_BAD_STANDING;
    }

    public void save() throws AccountException {
        try {
            this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(this.getAccountState(), this
                    .getAccountState(), (new PersonnelPersistence()).getPersonnel(userContext.getId()), this));
            new LoanPersistence().createOrUpdate(this);
            this.globalAccountNum = generateId(userContext.getBranchGlobalNum());
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION, e);
        }
    }

    public void save(AccountState accountState) throws AccountException {
        this.setAccountState(new AccountStateEntity(accountState));
        save();
    }

    public void updateLoan(Boolean interestDeductedAtDisbursment, Money loanAmount, Double interestRate,
            Short noOfInstallments, Date disbursmentDate, Short gracePeriodDuration, Integer businessActivityId,
            String collateralNote, Integer collateralTypeId, List<CustomFieldView> customFields,
            boolean isRepaymentIndepOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay) throws AccountException {
        if (interestDeductedAtDisbursment) {
            try {
                if (noOfInstallments <= 1)
                    throw new AccountException(LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
                setGracePeriodType((GracePeriodTypeEntity) new MasterPersistence().retrieveMasterEntity(GraceType.NONE
                        .getValue(), GracePeriodTypeEntity.class, getUserContext().getLocaleId()));
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        } else
            setGracePeriodType(getLoanOffering().getGracePeriodType());
        setLoanAmount(loanAmount);
        setInterestRate(interestRate);
        setNoOfInstallments(noOfInstallments);
        setDisbursementDate(disbursmentDate);
        setGracePeriodDuration(gracePeriodDuration);
        setInterestDeductedAtDisbursement(interestDeductedAtDisbursment);
        setBusinessActivityId(businessActivityId);
        setCollateralNote(collateralNote);
        setCollateralTypeId(collateralTypeId);
        if (getAccountState().getId().equals(AccountState.LOAN_APPROVED.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_PARTIAL_APPLICATION.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_PENDING_APPROVAL.getValue())) {
            if (isDisbursementDateLessThanCurrentDate(disbursementDate))
                throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
            regeneratePaymentSchedule(isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
        }
        try {
            updateCustomFields(customFields);
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        }
        loanSummary.setOriginalPrincipal(loanAmount);
        update();
    }

    public void updateLoan(Money loanAmount, Integer businessActivityId) throws AccountException {
        setLoanAmount(loanAmount);
        setBusinessActivityId(businessActivityId);
        update();
    }

    public Short getDaysInArrears() {
        Short daysInArrears = 0;
        if (isAccountActive()) {
            if (!getDetailsOfInstallmentsInArrears().isEmpty()) {
                AccountActionDateEntity accountActionDateEntity = getDetailsOfInstallmentsInArrears().get(0);
                daysInArrears = Short.valueOf(Long.valueOf(
                        calculateDays(accountActionDateEntity.getActionDate(), DateUtils
                                .getCurrentDateWithoutTimeStamp())).toString());
            }
        }
        return daysInArrears;
    }

    public void handleArrearsAging() throws AccountException {
        if (this.loanArrearsAgingEntity == null)
            this.loanArrearsAgingEntity = new LoanArrearsAgingEntity(this, getDaysInArrears(), getLoanSummary()
                    .getPrincipalDue(), getLoanSummary().getInterestDue(), getTotalPrincipalAmountInArrears(),
                    getTotalInterestAmountInArrears());
        else
            this.loanArrearsAgingEntity.update(getDaysInArrears(), getLoanSummary().getPrincipalDue(), getLoanSummary()
                    .getInterestDue(), getTotalPrincipalAmountInArrears(), getTotalInterestAmountInArrears(),
                    getCustomer());

        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException pe) {
            throw new AccountException(pe);
        }
    }

    public final void reverseLoanDisbursal(final PersonnelBO loggedInUser, final String note) throws AccountException {
        changeStatus(AccountState.LOAN_CANCELLED.getValue(), AccountStateFlag.LOAN_REVERSAL.getValue(), note);
        if (getAccountPayments() != null && getAccountPayments().size() > 0) {
            for (AccountPaymentEntity accountPayment : getAccountPayments()) {
                if (accountPayment.getAmount().getAmountDoubleValue() > 0.0) {
                    adjustPayment(accountPayment, loggedInUser, note);
                }
            }
        }
        addLoanActivity(buildLoanActivity(loanAmount, loggedInUser, "Disbursal Adjusted", DateUtils
                .getCurrentDateWithoutTimeStamp()));
        updateCustomerHistoryOnReverseLoan();
        try {
            new LoanPersistence().createOrUpdate(this);
        } catch (PersistenceException pe) {
            throw new AccountException(pe);
        }
    }

    protected void updatePerformanceHistoryOnAdjustment(int numberOfTransactions) {
        if (getPerformanceHistory() != null) {
            getPerformanceHistory().setNoOfPayments(getPerformanceHistory().getNoOfPayments() - numberOfTransactions);
        }
    }

    /*
     * PaymentData is the payment information entered in the UI
     * 
     * An AccountPaymentEntity is created from the PaymentData passed in.
     */
    @Override
    protected AccountPaymentEntity makePayment(PaymentData paymentData) throws AccountException {
        LoanPaymentTypes loanPaymentTypes = getLoanPaymentType(paymentData.getTotalAmount());
        if (loanPaymentTypes == null)
            throw new AccountException("errors.makePayment", new String[] { getGlobalAccountNum() });
        else if (loanPaymentTypes.equals(LoanPaymentTypes.PARTIAL_PAYMENT)) {
            handlePartialPayment(paymentData);
        } else if (loanPaymentTypes.equals(LoanPaymentTypes.FULL_PAYMENT)) {
            handleFullPayment(paymentData);
        } else if (loanPaymentTypes.equals(LoanPaymentTypes.FUTURE_PAYMENT)) {
            handleFuturePayment(paymentData);
        }
        AccountActionDateEntity lastAccountAction = getLastInstallmentAccountAction();
        AccountPaymentEntity accountPayment = new AccountPaymentEntity(this, paymentData.getTotalAmount(), paymentData
                .getRecieptNum(), paymentData.getRecieptDate(), new PaymentTypeEntity(paymentData.getPaymentTypeId()),
                paymentData.getTransactionDate());
        java.sql.Date paymentDate = new java.sql.Date(paymentData.getTransactionDate().getTime());
        for (AccountPaymentData accountPaymentData : paymentData.getAccountPayments()) {
            LoanScheduleEntity accountAction = (LoanScheduleEntity) getAccountActionDate(accountPaymentData
                    .getInstallmentId());
            if (accountAction.isPaid()) {
                throw new AccountException("errors.update", new String[] { getGlobalAccountNum() });
            }
            if (accountAction.getInstallmentId().equals(lastAccountAction.getInstallmentId())
                    && accountPaymentData.isPaid()) {
                changeLoanStatus(AccountState.LOAN_CLOSED_OBLIGATIONS_MET, paymentData.getPersonnel());
                this.setClosedDate(new DateTimeService().getCurrentJavaDateTime());
                // Client performance entry
                updateCustomerHistoryOnLastInstlPayment(paymentData.getTotalAmount());
                this.delete(loanArrearsAgingEntity);
                loanArrearsAgingEntity = null;
            }
            if (getState().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING)
                    && (loanPaymentTypes.equals(LoanPaymentTypes.FULL_PAYMENT) || loanPaymentTypes
                            .equals(LoanPaymentTypes.FUTURE_PAYMENT))) {
                changeLoanStatus(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, paymentData.getPersonnel());
                // Client performance entry
                updateCustomerHistoryOnPayment();
                this.delete(loanArrearsAgingEntity);
                loanArrearsAgingEntity = null;
            }

            if (getState().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING)
                    && (loanPaymentTypes.equals(LoanPaymentTypes.PARTIAL_PAYMENT))) {
                handleArrearsAging();
            }
            LoanPaymentData loanPaymentData = (LoanPaymentData) accountPaymentData;
            accountAction.setPaymentDetails(loanPaymentData, paymentDate);
            accountPaymentData.setAccountActionDate(accountAction);
            LoanTrxnDetailEntity accountTrxnBO;
            try {
                accountTrxnBO = new LoanTrxnDetailEntity(accountPayment, loanPaymentData, paymentData.getPersonnel(),
                        paymentData.getTransactionDate(), (AccountActionEntity) new MasterPersistence()
                                .getPersistentObject(AccountActionEntity.class, AccountActionTypes.LOAN_REPAYMENT
                                        .getValue()), loanPaymentData.getAmountPaidWithFeeForInstallment(),
                        AccountConstants.PAYMENT_RCVD);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
            accountPayment.addAccountTrxn(accountTrxnBO);

            loanSummary.updatePaymentDetails(loanPaymentData.getPrincipalPaid(), loanPaymentData.getInterestPaid(),
                    loanPaymentData.getPenaltyPaid().add(loanPaymentData.getMiscPenaltyPaid()), loanPaymentData
                            .getFeeAmountPaidForInstallment().add(loanPaymentData.getMiscFeePaid()));
            if (loanPaymentData.isPaid())
                performanceHistory.setNoOfPayments(getPerformanceHistory().getNoOfPayments() + 1);
        }
        addLoanActivity(buildLoanActivity(accountPayment.getAccountTrxns(), paymentData.getPersonnel(),
                AccountConstants.PAYMENT_RCVD, paymentData.getTransactionDate()));
        return accountPayment;
    }

    private void delete(PersistentObject objectoDelete) throws AccountException {

        if (objectoDelete != null) {
            try {
                new LoanPersistence().delete(objectoDelete);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    @Override
    protected Money getDueAmount(AccountActionDateEntity installment) {
        return ((LoanScheduleEntity) installment).getTotalDueWithFees();
    }

    private boolean isInstallmentPaid(Short installmentId, List<AccountActionDateEntity> allInstallments) {
        for (AccountActionDateEntity accountActionDate : allInstallments)
            if (accountActionDate.getInstallmentId().equals(installmentId))
                return accountActionDate.isPaid();
        return false;
    }

    @Override
    protected void updateInstallmentAfterAdjustment(List<AccountTrxnEntity> reversedTrxns) throws AccountException {
        int numberOfFullPayments = 0;
        List<AccountActionDateEntity> allInstallments = this.getAllInstallments();

        if (null != reversedTrxns && reversedTrxns.size() > 0) {
            for (AccountTrxnEntity accntTrxn : reversedTrxns) {
                Short prevInstallmentId = null;
                Short currentInstallmentId = accntTrxn.getInstallmentId();
                numberOfFullPayments = getIncrementedNumberOfFullPaymentsIfPaid(numberOfFullPayments, allInstallments,
                        prevInstallmentId, currentInstallmentId);

                if (!accntTrxn.getAccountActionEntity().getId().equals(
                        AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL.getValue())) {
                    LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accntTrxn;

                    loanSummary.updatePaymentDetails(loanTrxn.getPrincipalAmount(), loanTrxn.getInterestAmount(),
                            loanTrxn.getPenaltyAmount().add(loanTrxn.getMiscPenaltyAmount()), loanTrxn.getFeeAmount()
                                    .add(loanTrxn.getMiscFeeAmount()));
                    if (loanTrxn.getInstallmentId() != null && !loanTrxn.getInstallmentId().equals(Short.valueOf("0"))) {
                        LoanScheduleEntity accntActionDate = (LoanScheduleEntity) getAccountActionDate(loanTrxn
                                .getInstallmentId());
                        accntActionDate.updatePaymentDetails(loanTrxn.getPrincipalAmount(), loanTrxn
                                .getInterestAmount(), loanTrxn.getPenaltyAmount(), loanTrxn.getMiscPenaltyAmount(),
                                loanTrxn.getMiscFeeAmount());

                        accntActionDate.setPaymentStatus(PaymentStatus.UNPAID);
                        accntActionDate.setPaymentDate(null);
                        if (null != accntActionDate.getAccountFeesActionDetails()
                                && accntActionDate.getAccountFeesActionDetails().size() > 0) {
                            for (AccountFeesActionDetailEntity accntFeesAction : accntActionDate
                                    .getAccountFeesActionDetails()) {
                                FeesTrxnDetailEntity feesTrxnDetailEntity = loanTrxn.getFeesTrxn(accntFeesAction
                                        .getAccountFee().getAccountFeeId());
                                if (feesTrxnDetailEntity != null) {
                                    Money feeAmntAdjusted = feesTrxnDetailEntity.getFeeAmount();
                                    ((LoanFeeScheduleEntity) accntFeesAction).setFeeAmountPaid(accntFeesAction
                                            .getFeeAmountPaid().add(feeAmntAdjusted));
                                }
                            }
                        }
                    }
                }
            }
            boolean accountReOpened = false;
            AccountStateEntity currentAccountState = this.getAccountState();
            AccountStateEntity newAccountState = currentAccountState;

            if (getAccountStatusChangeHistory() != null && getAccountStatusChangeHistory().size() > 0
                    && !getAccountState().getId().equals(AccountState.LOAN_CANCELLED.getValue())) {
                List<Object> objectList = Arrays.asList(getAccountStatusChangeHistory().toArray());
                AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity = (AccountStatusChangeHistoryEntity) objectList
                        .get(objectList.size() - 1);
                if (accountStatusChangeHistoryEntity.getOldStatus().getId().equals(
                        AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue())) {
                    setAccountState(new AccountStateEntity(AccountState.LOAN_ACTIVE_IN_BAD_STANDING));
                } else if (currentAccountState.getId().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue())) {

                    setAccountState(accountStatusChangeHistoryEntity.getOldStatus());
                    newAccountState = accountStatusChangeHistoryEntity.getOldStatus();
                }
            }
            accountReOpened = isAccountReOpened(currentAccountState, newAccountState);

            if (accountReOpened && this.getCustomer().isClient()) {
                final ClientPerformanceHistoryEntity clientHistory = (ClientPerformanceHistoryEntity) this
                        .getCustomer().getPerformanceHistory();
                clientHistory.setNoOfActiveLoans(clientHistory.getNoOfActiveLoans() + 1);
            }

            // Reverse just one payment when reopening an account
            // Else reverse payments equal to number of transactions reversed.
            if (accountReOpened) {
                updatePerformanceHistoryOnAdjustment(1);
            } else if (reversedTrxns != null && reversedTrxns.size() > 0) {
                updatePerformanceHistoryOnAdjustment(numberOfFullPayments);
            }
            PersonnelBO personnel;
            try {
                personnel = new PersonnelPersistence().getPersonnel(getUserContext().getId());

                addLoanActivity(buildLoanActivity(reversedTrxns, personnel, AccountConstants.LOAN_ADJUSTED, DateUtils
                        .getCurrentDateWithoutTimeStamp()));
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    private int getIncrementedNumberOfFullPaymentsIfPaid(Integer numberOfFullPayments,
            List<AccountActionDateEntity> allInstallments, Short prevInstallmentId, Short currentInstallmentId) {
        if (!currentInstallmentId.equals(prevInstallmentId)) {
            if (isInstallmentPaid(currentInstallmentId, allInstallments))
                numberOfFullPayments++;
            prevInstallmentId = currentInstallmentId;
        }
        return numberOfFullPayments;
    }

    /**
     * This method checks if the loan account has been reopened because of
     * payment adjustments made.
     */
    private boolean isAccountReOpened(AccountStateEntity currentAccountState, AccountStateEntity newAccountState) {
        boolean reOpened = false;

        if (currentAccountState.isInState(AccountState.LOAN_CLOSED_OBLIGATIONS_MET)
                || currentAccountState.isInState(AccountState.LOAN_CLOSED_WRITTEN_OFF)
                && (newAccountState.isInState(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING) || newAccountState
                        .isInState(AccountState.LOAN_ACTIVE_IN_BAD_STANDING))) {
            reOpened = true;
        }
        return reOpened;
    }

    @Override
    protected void regenerateFutureInstallments(Short nextInstallmentId) throws AccountException {
        if ((!this.getAccountState().getId().equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue()))
                && (!this.getAccountState().getId().equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue()))
                && (!this.getAccountState().getId().equals(AccountState.LOAN_CANCELLED.getValue()))) {
            List<Date> meetingDates = null;
            int installmentSize = getLastInstallmentId();
            try {
                MeetingBO meeting = buildLoanMeeting(customer.getCustomerMeeting().getMeeting(), getLoanMeeting(),
                        getLoanMeeting().getMeetingStartDate());
                meetingDates = meeting.getAllDates(installmentSize);
            } catch (MeetingException me) {
                throw new AccountException(me);
            }
            updateLoanSchedule(nextInstallmentId, meetingDates);
        }
    }

    private void updateLoanSchedule(Short nextInstallmentId, List<Date> meetingDates) {
        short installmentId = nextInstallmentId;

        for (int count = nextInstallmentId; count <= meetingDates.size(); count++) {
            AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId);

            if (accountActionDate != null) {
                Date meetingDate = meetingDates.get(installmentId - 1);
                ((LoanScheduleEntity) accountActionDate).setActionDate(new java.sql.Date(meetingDate.getTime()));
            }
            installmentId++;
        }

    }

    protected final void applyRounding() throws AccountException {
        applyRounding_v2();
    }

    private int calculateDays(Date fromDate, Date toDate) {
        long y = 1000 * 60 * 60 * 24;
        long x = (getMFITime(toDate) / y) - (getMFITime(fromDate) / y);
        return (int) x;
    }

    private long getMFITime(Date date) {
        Calendar cal1 = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        cal1.setTimeZone(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
        cal1.setTime(date);
        return date.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
    }

    private Money getAccountFeeAmount(AccountFeesEntity accountFees, Money loanInterest) {

        // OK. No-arg constructor uses new calc methods
        Money accountFeeAmount = new Money();

        Double feeAmount = accountFees.getFeeAmount();

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Fee amount..." + feeAmount);

        if (accountFees.getFees().getFeeType().equals(RateAmountFlag.AMOUNT)) {
            accountFeeAmount = new Money(feeAmount.toString());
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "AccountFeeAmount for amount fee.." + feeAmount);
        } else if (accountFees.getFees().getFeeType().equals(RateAmountFlag.RATE)) {
            RateFeeBO rateFeeBO = new FeePersistence().getRateFee(accountFees.getFees().getFeeId());
            accountFeeAmount = new Money(getRateBasedOnFormula(feeAmount, rateFeeBO.getFeeFormula(), loanInterest));
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "AccountFeeAmount for Formula fee.." + feeAmount);
        }
        return accountFeeAmount;
    }

    @Override
    @Deprecated
    protected final List<FeeInstallment> handlePeriodic(AccountFeesEntity accountFees,
            List<InstallmentDate> installmentDates) throws AccountException {
        Money accountFeeAmount = accountFees.getAccountFeeAmount();
        MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency();
        List<Date> feeDates = getFeeDates(feeMeetingFrequency, installmentDates);
        ListIterator<Date> feeDatesIterator = feeDates.listIterator();
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        while (feeDatesIterator.hasNext()) {
            Date feeDate = feeDatesIterator.next();
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Handling periodic fee.." + feeDate);
            Short installmentId = getMatchingInstallmentId(installmentDates, feeDate);
            feeInstallmentList.add(buildFeeInstallment(installmentId, accountFeeAmount, accountFees));
        }
        return feeInstallmentList;
    }

    @Override
    protected final List<FeeInstallment> handlePeriodic(AccountFeesEntity accountFees,
            List<InstallmentDate> installmentDates, List<InstallmentDate> nonAdjustedInstallmentDates)
            throws AccountException {
        Money accountFeeAmount = accountFees.getAccountFeeAmount();
        MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency();
        List<Date> feeDates = getFeeDates(feeMeetingFrequency, nonAdjustedInstallmentDates);
        ListIterator<Date> feeDatesIterator = feeDates.listIterator();
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        while (feeDatesIterator.hasNext()) {
            Date feeDate = feeDatesIterator.next();
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Handling periodic fee.." + feeDate);

            Short installmentId = getMatchingInstallmentId(installmentDates, feeDate);
            feeInstallmentList.add(buildFeeInstallment(installmentId, accountFeeAmount, accountFees));

        }
        return feeInstallmentList;
    }

    private LoanActivityEntity buildLoanActivity(Collection<AccountTrxnEntity> accountTrxnDetails,
            PersonnelBO personnel, String comments, Date trxnDate) {
        Date activityDate = trxnDate;
        Money principal = new Money();
        Money interest = new Money();
        Money penalty = new Money();
        Money fees = new Money();
        for (AccountTrxnEntity accountTrxn : accountTrxnDetails) {
            if (!accountTrxn.getAccountActionEntity().getId().equals(
                    AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL.getValue())) {
                LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) accountTrxn;
                principal = principal.add(removeSign(loanTrxn.getPrincipalAmount()));
                interest = interest.add(removeSign(loanTrxn.getInterestAmount()));
                penalty = penalty.add(removeSign(loanTrxn.getPenaltyAmount())).add(
                        removeSign(loanTrxn.getMiscPenaltyAmount()));
                fees = fees.add(removeSign(loanTrxn.getMiscFeeAmount()));
                for (FeesTrxnDetailEntity feesTrxn : loanTrxn.getFeesTrxnDetails()) {
                    fees = fees.add(removeSign(feesTrxn.getFeeAmount()));
                }
            }
            if (accountTrxn.getAccountActionEntity().getId().equals(
                    AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL.getValue())
                    || accountTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.LOAN_REVERSAL.getValue())) {
                comments = "Loan Reversal";
            }
        }
        return new LoanActivityEntity(this, personnel, comments, principal, loanSummary.getOriginalPrincipal()
                .subtract(loanSummary.getPrincipalPaid()), interest, loanSummary.getOriginalInterest().subtract(
                loanSummary.getInterestPaid()), fees,
                loanSummary.getOriginalFees().subtract(loanSummary.getFeesPaid()), penalty, loanSummary
                        .getOriginalPenalty().subtract(loanSummary.getPenaltyPaid()), activityDate);
    }

    private LoanActivityEntity buildLoanActivity(Money totalPrincipal, PersonnelBO personnel, String comments,
            Date trxnDate) {
        Date activityDate = trxnDate;
        Money principal = totalPrincipal;
        Money interest = new Money();
        Money penalty = new Money();
        Money fees = new Money();
        return new LoanActivityEntity(this, personnel, comments, principal, loanSummary.getOriginalPrincipal()
                .subtract(loanSummary.getPrincipalPaid()), interest, loanSummary.getOriginalInterest().subtract(
                loanSummary.getInterestPaid()), fees,
                loanSummary.getOriginalFees().subtract(loanSummary.getFeesPaid()), penalty, loanSummary
                        .getOriginalPenalty().subtract(loanSummary.getPenaltyPaid()), activityDate);
    }

    private Short getInstallmentSkipToStartRepayment(boolean isRepaymentIndepOfMeetingEnabled) {
        boolean isInterestDeductedatDisbursement = false;
        if (isRepaymentIndepOfMeetingEnabled)
            isInterestDeductedatDisbursement = !isInterestDeductedAtDisbursement();
        else
            isInterestDeductedatDisbursement = isInterestDeductedAtDisbursement();
        if (isInterestDeductedatDisbursement)
            return (short) 0;
        else {
            if (getGraceType() == GraceType.PRINCIPALONLYGRACE || getGraceType() == GraceType.NONE) {
                return (short) 1;
            }
        }
        return (short) (getGracePeriodDuration() + 1);
    }

    private String getRateBasedOnFormula(Double rate, FeeFormulaEntity formula, Money loanInterest) {
        Double amountToCalculateOn = 1.0;
        if (formula.getId().equals(FeeFormula.AMOUNT.getValue())) {
            amountToCalculateOn = loanAmount.getAmountDoubleValue();
        } else if (formula.getId().equals(FeeFormula.AMOUNT_AND_INTEREST.getValue())) {
            amountToCalculateOn = (loanAmount.add(loanInterest)).getAmountDoubleValue();
        } else if (formula.getId().equals(FeeFormula.INTEREST.getValue())) {
            amountToCalculateOn = loanInterest.getAmountDoubleValue();
        }
        Double rateAmount = (rate * amountToCalculateOn) / 100;
        return rateAmount.toString();
    }

    private void populateAccountFeeAmount(Set<AccountFeesEntity> accountFees, Money loanInterest) {
        for (AccountFeesEntity accountFeesEntity : accountFees) {
            accountFeesEntity.setAccountFeeAmount(getAccountFeeAmount(accountFeesEntity, loanInterest));
        }
    }

    private Boolean isPrincipalZeroInAnyInstallmemt() {
        for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
            if (((LoanScheduleEntity) accountActionDate).isPrincipalZero()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of payment periods in the fiscal year for this loan.
     * 
     * This method contains two defects that are corrected in the _v2 version.
     * The defects are described below.
     * 
     * KEITH TODO: This appears to be incorrect. For example, if fiscal year =
     * 360 days interest rate = 1 percent recurrence = every 1 week then the
     * correct result should be ______ 360 / (7 * 1) = 51.428571 But because the
     * three factors are ints or shorts, the calculation is rounded to the
     * nearest integer, 51.0.
     * 
     * I have corrected the method here, but am not sure if this is correct
     * Should the method return the number of periods rounded to the nearest
     * integer? Note that the spreadsheet assumes that the number of periods is
     * an exact floating point number, not rounded.
     * 
     * Ththe formula is incorrect for monthly loans, when fiscal year is 365.
     * You should just divide recurAfter by 12.
     */
    private double getDecliningInterestAnnualPeriods() {
        RecurrenceType meetingFrequency = getLoanMeeting().getMeetingDetails().getRecurrenceTypeEnum();

        short recurAfter = getLoanMeeting().getMeetingDetails().getRecurAfter();

        double period = 0;

        if (meetingFrequency.equals(RecurrenceType.WEEKLY)) {
            period = getInterestDays() / (getDaysInWeek() * recurAfter);

        }
        /*
         * The use of monthly interest here does not distinguish between the 360
         * (with equal 30 day months) and the 365 day year cases. Should it?
         */
        else if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
            period = getInterestDays() / (getDaysInMonth() * recurAfter);
        }

        return period;
    }

    // read from configuration
    private int getInterestDays() {
        return AccountingRules.getNumberOfInterestDays().shortValue();
    }

    // read from configuration
    private int getDaysInWeek() {
        return AccountConstants.DAYS_IN_WEEK;
    }

    // read from configuration
    private int getDaysInMonth() {
        return AccountConstants.DAYS_IN_MONTH;
    }

    private void generateRepaymentSchedule(List<InstallmentDate> installmentDates,
            List<EMIInstallment> EMIInstallments, List<FeeInstallment> feeInstallmentList) throws AccountException {
        int count = installmentDates.size();
        for (int i = 0; i < count; i++) {
            InstallmentDate installmentDate = installmentDates.get(i);
            EMIInstallment em = EMIInstallments.get(i);
            LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(this, getCustomer(), installmentDate
                    .getInstallmentId(), new java.sql.Date(installmentDate.getInstallmentDueDate().getTime()),
                    PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());
            addAccountActionDate(loanScheduleEntity);
            for (FeeInstallment feeInstallment : feeInstallmentList) {
                if (feeInstallment.getInstallmentId() == installmentDate.getInstallmentId()
                        && !feeInstallment.getAccountFeesEntity().getFees().isTimeOfDisbursement()) {
                    LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(loanScheduleEntity,
                            feeInstallment.getAccountFeesEntity().getFees(), feeInstallment.getAccountFeesEntity(),
                            feeInstallment.getAccountFee());
                    loanScheduleEntity.addAccountFeesAction(loanFeeScheduleEntity);
                } else if (feeInstallment.getInstallmentId() == installmentDate.getInstallmentId()
                        && isInterestDeductedAtDisbursement()
                        && feeInstallment.getAccountFeesEntity().getFees().isTimeOfDisbursement()) {
                    LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(loanScheduleEntity,
                            feeInstallment.getAccountFeesEntity().getFees(), feeInstallment.getAccountFeesEntity(),
                            feeInstallment.getAccountFee());
                    loanScheduleEntity.addAccountFeesAction(loanFeeScheduleEntity);
                }
            }
        }
        buildRawAmountTotal();
    }

    private void validateSize(List installmentDates, List EMIInstallments) throws AccountException {
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Validating installment size  " + installmentDates.size());
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Validating emi installment size  " + EMIInstallments.size());
        if (installmentDates.size() != EMIInstallments.size())
            throw new AccountException(AccountConstants.DATES_MISMATCH);
    }

    private List<EMIInstallment> interestDeductedFirstPrincipalLast(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        if (getGraceType() == GraceType.GRACEONALLREPAYMENTS || getGraceType() == GraceType.PRINCIPALONLYGRACE)
            throw new AccountException(AccountConstants.INTERESTDEDUCTED_PRINCIPALLAST);
        if (getGraceType() == GraceType.NONE) {
            Money principalLastInstallment = getLoanAmount();
            Money interestFirstInstallment = loanInterest;

            EMIInstallment installment = null;
            installment = new EMIInstallment();
            installment.setPrincipal(new Money());
            installment.setInterest(interestFirstInstallment);
            emiInstallments.add(installment);
            for (int i = 1; i < getNoOfInstallments() - 1; i++) {
                installment = new EMIInstallment();
                installment.setPrincipal(new Money());
                installment.setInterest(new Money());
                emiInstallments.add(installment);
            }
            installment = new EMIInstallment();
            installment.setPrincipal(principalLastInstallment);
            installment.setInterest(new Money());
            emiInstallments.add(installment);
            return emiInstallments;
        }
        throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
    }

    public static Boolean isDisbursementDateValid(CustomerBO specifiedCustomer, Date disbursementDate)
            throws AccountException {
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("IsDisbursementDateValid invoked ");
        Boolean isValid = false;
        try {
            isValid = specifiedCustomer.getCustomerMeeting().getMeeting().isValidMeetingDate(disbursementDate,
                    DateUtils.getLastDayOfNextYear());
        } catch (MeetingException e) {
            throw new AccountException(e);
        }
        return isValid;
    }

    private void applyPeriodicFee(FeeBO fee, Double charge, List<AccountActionDateEntity> dueInstallments)
            throws AccountException, PersistenceException {
        AccountFeesEntity accountFee = getAccountFee(fee, charge);
        Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
        accountFeeSet.add(accountFee);
        populateAccountFeeAmount(accountFeeSet, loanSummary.getOriginalInterest());
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        for (AccountActionDateEntity accountActionDateEntity : dueInstallments)
            installmentDates.add(new InstallmentDate(accountActionDateEntity.getInstallmentId(),
                    accountActionDateEntity.getActionDate()));

        boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        // get installments without adjusting for holidays
        // note that this gets all installments, so this will only work if
        // adjusting periodic fees is done when no installments have been paid
        List<InstallmentDate> nonAdjustedInstallmentDates = getInstallmentDates(getLoanMeeting(), noOfInstallments,
                getInstallmentSkipToStartRepayment(isRepaymentIndepOfMeetingEnabled), isRepaymentIndepOfMeetingEnabled,
                false);
        List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(handlePeriodic(accountFee, installmentDates,
                nonAdjustedInstallmentDates));
        Money totalFeeAmountApplied = applyFeeToInstallments(feeInstallmentList, dueInstallments);
        updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
        updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee.getFeeName() + AccountConstants.APPLIED);
    }

    private void applyOneTimeFee(FeeBO fee, Double charge, AccountActionDateEntity accountActionDateEntity)
            throws AccountException {
        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
        AccountFeesEntity accountFee = new AccountFeesEntity(this, fee, charge, FeeStatus.ACTIVE.getValue(),
                new DateTimeService().getCurrentJavaDateTime(), null);
        Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
        accountFeeSet.add(accountFee);
        populateAccountFeeAmount(accountFeeSet, loanSummary.getOriginalInterest());
        List<AccountActionDateEntity> loanScheduleEntityList = new ArrayList<AccountActionDateEntity>();
        loanScheduleEntityList.add(loanScheduleEntity);
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        installmentDates.add(new InstallmentDate(accountActionDateEntity.getInstallmentId(), accountActionDateEntity
                .getActionDate()));
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        feeInstallmentList.add(handleOneTime(accountFee, installmentDates));
        Money totalFeeAmountApplied = applyFeeToInstallments(feeInstallmentList, loanScheduleEntityList);
        filterTimeOfDisbursementFees(loanScheduleEntity, fee);
        updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
        updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee.getFeeName() + AccountConstants.APPLIED);
    }

    protected boolean canApplyMiscCharge(Money charge) {
        return !havePaymentsBeenMade() || isRoundedAmount(charge);
    }

    private void applyMiscCharge(Short chargeType, Money charge, AccountActionDateEntity accountActionDateEntity)
            throws AccountException {

        if (!canApplyMiscCharge(charge)) {
            throw new AccountException(AccountExceptionConstants.CANT_APPLY_CHARGE_EXCEPTION);
        }

        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
        loanScheduleEntity.applyMiscCharge(chargeType, charge);
        updateLoanSummary(chargeType, charge);
        updateLoanActivity(chargeType, charge, "");
    }

    private void updateLoanSummary(Short chargeType, Money charge) {
        if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY)))
            getLoanSummary().updateOriginalPenalty(charge);
        else
            getLoanSummary().updateOriginalFees(charge);
    }

    private void updateLoanActivity(Short chargeType, Money charge, String comments) throws AccountException {
        try {
            PersonnelBO personnel = new PersonnelPersistence().getPersonnel(getUserContext().getId());
            LoanActivityEntity loanActivityEntity = null;
            if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY)))
                loanActivityEntity = new LoanActivityEntity(this, personnel, new Money(), new Money(), new Money(),
                        charge, getLoanSummary(), AccountConstants.MISC_PENALTY_APPLIED);
            else if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES)))
                loanActivityEntity = new LoanActivityEntity(this, personnel, new Money(), new Money(), charge,
                        new Money(), getLoanSummary(), AccountConstants.MISC_FEES_APPLIED);
            else
                loanActivityEntity = new LoanActivityEntity(this, personnel, new Money(), new Money(), charge,
                        new Money(), getLoanSummary(), comments);
            addLoanActivity(loanActivityEntity);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private Money applyFeeToInstallments(List<FeeInstallment> feeInstallmentList,
            List<AccountActionDateEntity> accountActionDateList) {
        Date lastAppliedDate = null;
        Money totalFeeAmountApplied = new Money();
        AccountFeesEntity accountFeesEntity = null;
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
            for (FeeInstallment feeInstallment : feeInstallmentList)
                if (feeInstallment.getInstallmentId().equals(loanScheduleEntity.getInstallmentId())) {
                    lastAppliedDate = loanScheduleEntity.getActionDate();
                    totalFeeAmountApplied = totalFeeAmountApplied.add(feeInstallment.getAccountFee());
                    /*
                     * AccountFeesActionDetailEntity
                     * accountFeesActionDetailEntity = new
                     * LoanFeeScheduleEntity( loanScheduleEntity, feeInstallment
                     * .getAccountFeesEntity().getFees(),
                     * feeInstallment.getAccountFeesEntity(),
                     * feeInstallment.getAccountFee()); loanScheduleEntity
                     * .addAccountFeesAction(accountFeesActionDetailEntity);
                     */
                    if (feeInstallment.getAccountFeesEntity().getFees().isPeriodic()
                            && loanScheduleEntity.isFeeAlreadyAttatched(feeInstallment.getAccountFeesEntity().getFees()
                                    .getFeeId())) {
                        LoanFeeScheduleEntity loanFeeScheduleEntity = (LoanFeeScheduleEntity) loanScheduleEntity
                                .getAccountFeesAction(feeInstallment.getAccountFeesEntity().getFees().getFeeId());
                        loanFeeScheduleEntity.setFeeAmount(loanFeeScheduleEntity.getFeeAmount().add(
                                feeInstallment.getAccountFee()));
                    } else {
                        AccountFeesActionDetailEntity accountFeesActionDetailEntity = new LoanFeeScheduleEntity(
                                loanScheduleEntity, feeInstallment.getAccountFeesEntity().getFees(), feeInstallment
                                        .getAccountFeesEntity(), feeInstallment.getAccountFee());
                        loanScheduleEntity.addAccountFeesAction(accountFeesActionDetailEntity);
                    }

                    accountFeesEntity = feeInstallment.getAccountFeesEntity();
                }
        }
        accountFeesEntity.setLastAppliedDate(lastAppliedDate);
        addAccountFees(accountFeesEntity);
        return totalFeeAmountApplied;
    }

    private void filterTimeOfDisbursementFees(LoanScheduleEntity loanScheduleEntity, FeeBO fee) {
        Short paymentType = fee.getFeeFrequency().getFeePayment().getId();
        if (paymentType.equals(FeePayment.TIME_OF_DISBURSMENT.getValue()) && !isInterestDeductedAtDisbursement()) {
            Set<AccountFeesActionDetailEntity> accountFeesDetailSet = loanScheduleEntity.getAccountFeesActionDetails();
            for (Iterator<AccountFeesActionDetailEntity> iter = accountFeesDetailSet.iterator(); iter.hasNext();) {
                AccountFeesActionDetailEntity accountFeesActionDetailEntity = iter.next();
                if (fee.equals(accountFeesActionDetailEntity.getFee())) {
                    iter.remove();
                }
            }
        }
    }

    private MeetingBO buildLoanMeeting(MeetingBO customerMeeting, MeetingBO loanOfferingMeeting, Date disbursementDate)
            throws AccountException {
        if (customerMeeting != null
                && loanOfferingMeeting != null
                && customerMeeting.getMeetingDetails().getRecurrenceType().getRecurrenceId().equals(
                        loanOfferingMeeting.getMeetingDetails().getRecurrenceType().getRecurrenceId())
                && isMultiple(loanOfferingMeeting.getMeetingDetails().getRecurAfter(), customerMeeting
                        .getMeetingDetails().getRecurAfter())) {

            RecurrenceType meetingFrequency = customerMeeting.getMeetingDetails().getRecurrenceTypeEnum();
            MeetingType meetingType = MeetingType.fromInt(customerMeeting.getMeetingType().getMeetingTypeId());
            Short recurAfter = loanOfferingMeeting.getMeetingDetails().getRecurAfter();
            try {
                MeetingBO meetingToReturn = null;
                if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
                    if (customerMeeting.isMonthlyOnDate())
                        meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getDayNumber(), recurAfter,
                                disbursementDate, meetingType, customerMeeting.getMeetingPlace());
                    else
                        meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getWeekDay(),
                                customerMeeting.getMeetingDetails().getWeekRank(), recurAfter, disbursementDate,
                                meetingType, customerMeeting.getMeetingPlace());
                } else if (meetingFrequency.equals(RecurrenceType.WEEKLY))
                    meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getMeetingRecurrence()
                            .getWeekDayValue(), recurAfter, disbursementDate, meetingType, customerMeeting
                            .getMeetingPlace());
                else
                    meetingToReturn = new MeetingBO(meetingFrequency, recurAfter, disbursementDate, meetingType);
                return meetingToReturn;
            } catch (MeetingException me) {
                throw new AccountException(me);
            }
        } else {
            throw new AccountException(AccountExceptionConstants.CHANGEINLOANMEETING);
        }
    }

    private boolean isMultiple(Short valueToBeChecked, Short valueToBeCheckedWith) {
        return valueToBeChecked % valueToBeCheckedWith == 0;
    }

    private LoanSummaryEntity buildLoanSummary() {
        Money interest = new Money();
        Money fees = new Money();
        Set<AccountActionDateEntity> actionDates = getAccountActionDates();
        if (actionDates != null && actionDates.size() > 0) {
            for (AccountActionDateEntity accountActionDate : actionDates) {
                LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
                interest = interest.add(loanSchedule.getInterest());
                fees = fees.add(loanSchedule.getTotalFees());
            }
        }
        fees = fees.add(getDisbursementFeeAmount());
        return new LoanSummaryEntity(this, loanAmount, interest, fees, rawAmountTotal);

    }

    private void buildRawAmountTotal() {
        Money interest = new Money();
        Money fees = new Money();
        Set<AccountActionDateEntity> actionDates = getAccountActionDates();
        if (actionDates != null && actionDates.size() > 0) {
            for (AccountActionDateEntity accountActionDate : actionDates) {
                LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
                interest = interest.add(loanSchedule.getInterest());
                fees = fees.add(loanSchedule.getTotalFees());
            }
        }
        fees = fees.add(getDisbursementFeeAmount());
        Money rawAmount = new Money();
        fees = Money.roundToCurrencyPrecision(fees);
        interest = Money.roundToCurrencyPrecision(interest);
        rawAmount = rawAmount.add(interest).add(fees);
        if (loanSummary == null) {
            // save it to LoanBO first and when loan summary is created it will
            // be retrieved and save to loan summary
            setRawAmountTotal(rawAmount);
        } else {
            loanSummary.setRawAmountTotal(rawAmount);
        }
    }

    private Money getDisbursementFeeAmount() {
        Money fees = new Money();
        for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
            if (!isInterestDeductedAtDisbursement() && accountFeesEntity.getFees().isTimeOfDisbursement()) {
                fees = fees.add(accountFeesEntity.getAccountFeeAmount());
            }
        }
        return fees;
    }

    private void buildAccountFee(List<FeeView> feeViews) {
        if (feeViews != null && feeViews.size() > 0) {
            for (FeeView feeView : feeViews) {
                FeeBO fee = new FeePersistence().getFee(feeView.getFeeIdValue());
                this.addAccountFees(new AccountFeesEntity(this, fee, feeView.getAmountMoney()));
            }
        }
    }

    private void setGracePeriodTypeAndDuration(boolean interestDeductedAtDisbursement, Short gracePeriodDuration,
            Short noOfInstallments) throws AccountException {
        if (interestDeductedAtDisbursement) {
            setGracePeriodType(GraceType.NONE);
            this.gracePeriodDuration = (short) 0;
        } else {
            if (!loanOffering.getGracePeriodType().getId().equals(GraceType.NONE.getValue()))
                if (gracePeriodDuration == null || gracePeriodDuration >= noOfInstallments)
                    throw new AccountException("errors.gracePeriod");
            setGracePeriodType(loanOffering.getGracePeriodType());
            this.gracePeriodDuration = gracePeriodDuration;
        }
    }

    private void updateCustomerHistoryOnLastInstlPayment(Money totalAmount) throws AccountException {
        try {
            getCustomer().updatePerformanceHistoryOnLastInstlPayment(this, totalAmount);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }
    }

    private void updateCustomerHistoryOnPayment() {
        if (getCustomer().isClient() && getCustomer().getPerformanceHistory() != null) {
            ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) getCustomer()
                    .getPerformanceHistory();
            clientPerfHistory.setNoOfActiveLoans(clientPerfHistory.getNoOfActiveLoans() - 1);
        }
    }

    private void updateCustomerHistoryOnDisbursement(Money disburseAmount) throws AccountException {
        try {
            getCustomer().updatePerformanceHistoryOnDisbursement(this, disburseAmount);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }
    }

    private void updateCustomerHistoryOnRepayment(Money totalAmount) throws AccountException {
        try {
            getCustomer().updatePerformanceHistoryOnRepayment(this, totalAmount);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }
    }

    private void updateCustomerHistoryOnWriteOff() throws AccountException {
        try {
            getCustomer().updatePerformanceHistoryOnWriteOff(this);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }
    }

    private void updateCustomerHistoryOnReverseLoan() throws AccountException {
        Money lastLoanAmount = new Money();
        try {
            lastLoanAmount = new LoanPersistence().getLastLoanAmountForCustomer(getCustomer().getCustomerId(),
                    getAccountId());
            customer.updatePerformanceHistoryOnReversal(this, lastLoanAmount);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }

    }

    private void regeneratePaymentSchedule(boolean isRepaymentIndepOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay)
            throws AccountException {
        Money miscFee = getMiscFee();
        Money miscPenalty = getMiscPenalty();
        try {
            new LoanPersistence().deleteInstallments(this.getAccountActionDates());
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        // Delete previous loan meeting if there is a new meeting for repayment
        // day
        if (isRepaymentIndepOfMeetingEnabled && newMeetingForRepaymentDay != null) {
            try {
                if (null != this.getLoanMeeting()) {
                    new LoanPersistence().delete(this.getLoanMeeting());
                }
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
        this.resetAccountActionDates();
        loanMeeting.setMeetingStartDate(disbursementDate);
        generateMeetingSchedule(isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) getAccountActionDate((short) 1);
        loanScheduleEntity.setMiscFee(miscFee);
        loanScheduleEntity.setMiscPenalty(miscPenalty);
        Money interest = new Money();
        Money fees = new Money();
        Money penalty = new Money();
        Money principal = new Money();
        Set<AccountActionDateEntity> actionDates = getAccountActionDates();
        if (actionDates != null && actionDates.size() > 0) {
            for (AccountActionDateEntity accountActionDate : actionDates) {
                LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
                principal = principal.add(loanSchedule.getPrincipal());
                interest = interest.add(loanSchedule.getInterest());
                fees = fees.add(loanSchedule.getTotalFees());
                penalty = penalty.add(loanSchedule.getTotalPenalty());
            }
        }
        fees = fees.add(getDisbursementFeeAmount());
        loanSummary.setOriginalInterest(interest);
        loanSummary.setOriginalFees(fees);
        loanSummary.setOriginalPenalty(penalty);
    }

    private AccountPaymentEntity payInterestAtDisbursement(String recieptNum, Date transactionDate,
            Short paymentTypeId, PersonnelBO personnel, Date receiptDate) throws AccountException {
        AccountActionDateEntity firstInstallment = null;
        for (AccountActionDateEntity accountActionDate : this.getAccountActionDates()) {
            if (accountActionDate.getInstallmentId().shortValue() == 1) {
                firstInstallment = accountActionDate;
                break;
            }
        }
        List<AccountActionDateEntity> installmentsToBePaid = new ArrayList<AccountActionDateEntity>();
        installmentsToBePaid.add(firstInstallment);

        PaymentData paymentData = getLoanAccountPaymentData(((LoanScheduleEntity) firstInstallment)
                .getTotalDueWithFees(), installmentsToBePaid, personnel, recieptNum, paymentTypeId, receiptDate,
                transactionDate);

        return makePayment(paymentData);

    }

    private AccountActionDateEntity getLastInstallmentAccountAction() {
        AccountActionDateEntity nextAccountAction = null;
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (null == nextAccountAction)
                    nextAccountAction = accountAction;
                else if (nextAccountAction.getInstallmentId() < accountAction.getInstallmentId())
                    nextAccountAction = accountAction;
            }
        }
        return nextAccountAction;
    }

    private Money getMiscFee() {
        Money miscFee = new Money();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            if (loanSchedule.getMiscFee() != null) {
                miscFee = miscFee.add(loanSchedule.getMiscFee());
            }
        }
        return miscFee;
    }

    private Money getMiscPenalty() {
        Money miscPenalty = new Money();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            if (loanSchedule.getMiscPenalty() != null) {
                miscPenalty = miscPenalty.add(loanSchedule.getMiscPenalty());
            }
        }
        return miscPenalty;
    }

    private List<AccountActionDateEntity> getListOfUnpaidInstallments() {
        List<AccountActionDateEntity> unpaidInstallmentList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (!accountActionDateEntity.isPaid()) {
                unpaidInstallmentList.add(accountActionDateEntity);
            }
        }
        return unpaidInstallmentList;
    }

    private List<AccountActionDateEntity> getListOfUnpaidFutureInstallments() {
        List<AccountActionDateEntity> unpaidInstallmentList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
            if (DateUtils.getCurrentDateWithoutTimeStamp().compareTo(
                    DateUtils.getDateWithoutTimeStamp(accountActionDateEntity.getActionDate().getTime())) <= 0) {
                unpaidInstallmentList.add(accountActionDateEntity);
            }
        }
        return unpaidInstallmentList;
    }

    private Money getEarlyClosureAmount() {
        Money amount = new Money();
        for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipal());
        }
        return amount;
    }

    private AccountPaymentEntity insertOnlyFeeAtDisbursement(String recieptNum, Date recieptDate, Short paymentTypeId,
            PersonnelBO personnel) throws AccountException {

        Money totalPayment = new Money();
        for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
            if (accountFeesEntity.isTimeOfDisbursement()) {
                totalPayment = totalPayment.add(accountFeesEntity.getAccountFeeAmount());
            }
        }

        loanSummary.updateFeePaid(totalPayment);

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, totalPayment, recieptNum,
                recieptDate, new PaymentTypeEntity(paymentTypeId), new DateTimeService().getCurrentJavaDateTime());

        LoanTrxnDetailEntity loanTrxnDetailEntity = null;

        try {
            List<AccountFeesEntity> applicableAccountFees = new ArrayList<AccountFeesEntity>();
            for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
                if (accountFeesEntity.isTimeOfDisbursement()) {
                    applicableAccountFees.add(accountFeesEntity);
                }
            }
            loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                    (AccountActionEntity) new MasterPersistence().getPersistentObject(AccountActionEntity.class,
                            AccountActionTypes.FEE_REPAYMENT.getValue()), Short.valueOf("0"), recieptDate, personnel,
                    recieptDate, totalPayment, "-", null, new Money(), new Money(), new Money(), new Money(),
                    new Money(), applicableAccountFees);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }

        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);

        addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), personnel,
                AccountConstants.PAYMENT_RCVD, recieptDate));
        return accountPaymentEntity;
    }

    private PaymentData getLoanAccountPaymentData(Money totalAmount, List<AccountActionDateEntity> accountActions,
            PersonnelBO personnel, String recieptId, Short paymentId, Date receiptDate, Date transactionDate) {
        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, personnel, paymentId, transactionDate);
        paymentData.setRecieptDate(receiptDate);
        paymentData.setRecieptNum(recieptId);
        return paymentData;
    }

    private LoanPaymentTypes getLoanPaymentType(Money amount) {
        if (amount.getAmountDoubleValue() == getTotalPaymentDue().getAmountDoubleValue())
            return LoanPaymentTypes.FULL_PAYMENT;
        else if (amount.getAmountDoubleValue() < getTotalPaymentDue().getAmountDoubleValue())
            return LoanPaymentTypes.PARTIAL_PAYMENT;
        else if (amount.getAmountDoubleValue() > getTotalPaymentDue().getAmountDoubleValue()
                && amount.getAmountDoubleValue() <= getTotalRepayableAmount().getAmountDoubleValue())
            return LoanPaymentTypes.FUTURE_PAYMENT;
        return null;
    }

    private void handlePartialPayment(PaymentData paymentData) {
        Money totalAmount = paymentData.getTotalAmount();
        for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
            if (totalAmount.getAmountDoubleValue() > 0.0) {
                LoanPaymentData loanPayment = new LoanPaymentData(accountActionDate, totalAmount);
                paymentData.addAccountPaymentData(loanPayment);
                totalAmount = totalAmount.subtract(loanPayment.getAmountPaidWithFeeForInstallment());
            }
        }
        if (totalAmount.isGreaterThan(new Money(totalAmount.getCurrency(), "0"))) {
            addAccountPaymentToNextInstallmentIfNecessary(paymentData, totalAmount);
        }
    }

    private void addAccountPaymentToNextInstallmentIfNecessary(PaymentData paymentData) {
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null) {
            addAccountPaymentToNextInstallmentIfNecessary(paymentData, new LoanPaymentData(nextInstallment),
                    nextInstallment);
        }
    }

    private void addAccountPaymentToNextInstallmentIfNecessary(PaymentData paymentData, Money totalAmount) {
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null) {
            addAccountPaymentToNextInstallmentIfNecessary(paymentData,
                    new LoanPaymentData(nextInstallment, totalAmount), nextInstallment);
        }
    }

    private void addAccountPaymentToNextInstallmentIfNecessary(PaymentData paymentData,
            LoanPaymentData loanPaymentData, AccountActionDateEntity nextInstallment) {
        if (!nextInstallment.isPaid()
                && DateUtils.getDateWithoutTimeStamp(nextInstallment.getActionDate().getTime()).equals(
                        DateUtils.getCurrentDateWithoutTimeStamp()))
            paymentData.addAccountPaymentData(loanPaymentData);
    }

    private void handleFullPayment(PaymentData paymentData) {
        for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
            paymentData.addAccountPaymentData(new LoanPaymentData(accountActionDate));
        }
        addAccountPaymentToNextInstallmentIfNecessary(paymentData);
    }

    private void handleFuturePayment(PaymentData paymentData) {
        Money totalAmount = paymentData.getTotalAmount();
        for (AccountActionDateEntity accountActionDate : getDetailsOfInstallmentsInArrears()) {
            LoanPaymentData loanPayment = new LoanPaymentData(accountActionDate);
            paymentData.addAccountPaymentData(loanPayment);
            totalAmount = totalAmount.subtract(loanPayment.getAmountPaidWithFeeForInstallment());
        }
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null && !nextInstallment.isPaid() && totalAmount.getAmountDoubleValue() > 0.0) {
            LoanPaymentData loanPayment;
            if (DateUtils.getDateWithoutTimeStamp(nextInstallment.getActionDate().getTime()).equals(
                    DateUtils.getCurrentDateWithoutTimeStamp())) {
                loanPayment = new LoanPaymentData(nextInstallment);
            } else {
                loanPayment = new LoanPaymentData(nextInstallment, totalAmount);
            }
            paymentData.addAccountPaymentData(loanPayment);
            totalAmount = totalAmount.subtract(loanPayment.getAmountPaidWithFeeForInstallment());
        }
        for (AccountActionDateEntity accountActionDate : getApplicableIdsForFutureInstallments()) {
            if (totalAmount.getAmountDoubleValue() > 0.0) {
                LoanPaymentData loanPayment = new LoanPaymentData(accountActionDate, totalAmount);
                paymentData.addAccountPaymentData(loanPayment);
                totalAmount = totalAmount.subtract(loanPayment.getAmountPaidWithFeeForInstallment());
            }
        }
    }

    private void changeLoanStatus(AccountState newAccountState, PersonnelBO personnel) throws AccountException {
        AccountStateEntity accountState = this.getAccountState();
        try {
            setAccountState((AccountStateEntity) (new MasterPersistence()).getPersistentObject(
                    AccountStateEntity.class, newAccountState.getValue()));
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(accountState, this.getAccountState(),
                personnel, this));
    }

    private Money getTotalRepayableAmount() {
        Money amount = new Money();
        for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForDueInstallments()) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getTotalDueWithFees());
        }
        for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForFutureInstallments()) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getTotalDueWithFees());
        }
        return amount;
    }

    private boolean isAdjustmentForInterestDedAtDisb(Short installmentId) {
        return installmentId.equals(Short.valueOf("1")) && isInterestDeductedAtDisbursement();
    }

    public boolean isRedone() {
        return this.redone;
    }

    Boolean getRedone() {
        return this.redone;
    }

    void setRedone(Boolean val) {
        this.redone = val;
    }

    private void makeEarlyRepaymentForDueInstallments(AccountPaymentEntity accountPaymentEntity, String comments,
            AccountActionTypes accountActionTypes, PersonnelBO currentUser) throws AccountException {
        MasterPersistence masterPersistence = new MasterPersistence();
        List<AccountActionDateEntity> dueInstallmentsList = getApplicableIdsForDueInstallments();
        for (AccountActionDateEntity accountActionDateEntity : dueInstallmentsList) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            Money principal = loanSchedule.getPrincipalDue();
            Money interest = loanSchedule.getInterestDue();
            Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
            Money penalty = loanSchedule.getPenaltyDue();
            Money totalAmt = principal.add(interest).add(fees).add(penalty);

            LoanTrxnDetailEntity loanTrxnDetailEntity;
            try {
                loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                        (AccountActionEntity) masterPersistence.getPersistentObject(AccountActionEntity.class,
                                accountActionTypes.getValue()), loanSchedule.getInstallmentId(), loanSchedule
                                .getActionDate(), currentUser, new DateTimeService().getCurrentJavaDateTime(),
                        totalAmt, comments, null, principal, interest, loanSchedule.getPenalty().subtract(
                                loanSchedule.getPenaltyPaid()), loanSchedule.getMiscFeeDue(), loanSchedule
                                .getMiscPenaltyDue(), null);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanSchedule
                    .getAccountFeesActionDetails()) {
                if (accountFeesActionDetailEntity.getFeeDue().getAmountDoubleValue() > 0) {
                    FeesTrxnDetailEntity feesTrxnDetailEntity = new FeesTrxnDetailEntity(loanTrxnDetailEntity,
                            accountFeesActionDetailEntity.getAccountFee(), accountFeesActionDetailEntity.getFeeDue());
                    loanTrxnDetailEntity.addFeesTrxnDetail(feesTrxnDetailEntity);
                }
            }

            accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);

            loanSchedule.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);

            loanSummary.updatePaymentDetails(principal, interest, penalty, fees);
        }
    }

    private void makeEarlyRepaymentForFutureInstallments(AccountPaymentEntity accountPaymentEntity, String comments,
            AccountActionTypes accountActionTypes, PersonnelBO currentUser) throws AccountException {
        MasterPersistence masterPersistence = new MasterPersistence();
        List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
        for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            Money principal = loanSchedule.getPrincipalDue();
            Money interest = loanSchedule.getInterestDue();
            Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
            Money penalty = loanSchedule.getPenaltyDue();

            LoanTrxnDetailEntity loanTrxnDetailEntity;
            try {
                loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity,
                        (AccountActionEntity) masterPersistence.getPersistentObject(AccountActionEntity.class,
                                accountActionTypes.getValue()), loanSchedule.getInstallmentId(), loanSchedule
                                .getActionDate(), currentUser, new DateTimeService().getCurrentJavaDateTime(),
                        principal, comments, null, principal, new Money(), new Money(), new Money(), new Money(), null);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }

            accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);

            loanSchedule.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);

            loanSummary.decreaseBy(null, interest, penalty, fees);
            loanSummary.updatePaymentDetails(principal, null, null, null);

        }

    }

    public int getDisbursementTerm() {
        List<AccountActionDateEntity> pastInstallments = getPastInstallments();
        List<AccountActionDateEntity> installmentsInDisbursement = new ArrayList<AccountActionDateEntity>();
        if (!pastInstallments.isEmpty()) {
            for (AccountActionDateEntity accountAction : pastInstallments) {
                if (accountAction.isPaid())
                    installmentsInDisbursement.add(accountAction);
            }
        }
        return installmentsInDisbursement.size();
    }

    public int getDaysWithoutPayment() throws PersistenceException {
        int daysWithoutPayment = 0;
        loanPrdPersistence = new LoanPrdPersistence();
        if (getDaysInArrears() > loanPrdPersistence.retrieveLatenessForPrd()) {
            daysWithoutPayment = getDaysInArrears().intValue();
        }
        return daysWithoutPayment;
    }

    public double getPaymentsInArrears() throws PersistenceException {
        Money principalInArrearsAndOutsideLateness = getTotalPrincipalAmountInArrearsAndOutsideLateness();
        Money totalPrincipal = getTotalPrincipalAmount();
        Money numOfInstallments = new Money(new BigDecimal(getNoOfInstallments()));
        return principalInArrearsAndOutsideLateness.multiply(numOfInstallments).divide(totalPrincipal)
                .getAmountDoubleValue();

    }

    public Money getNetOfSaving() {
        return getRemainingPrincipalAmount().subtract(getCustomer().getSavingsBalance());
    }

    public Set<LoanBO> getLoanAccountDetails() {
        return loanAccountDetails;
    }

    public void setLoanAccountDetails(Set<LoanBO> loanAccountDetails) {
        this.loanAccountDetails = loanAccountDetails;
    }

    public LoanBO getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(LoanBO parentAccount) {
        this.parentAccount = parentAccount;
    }

    public MaxMinLoanAmount getMaxMinLoanAmount() {
        return maxMinLoanAmount;
    }

    public MaxMinNoOfInstall getMaxMinNoOfInstall() {
        return maxMinNoOfInstall;
    }

    public RankOfDaysEntity getMonthRank() {
        return monthRank;
    }

    public void setMonthRank(RankOfDaysEntity monthRank) {
        this.monthRank = monthRank;
    }

    public WeekDaysEntity getMonthWeek() {
        return monthWeek;
    }

    public void setMonthWeek(WeekDaysEntity monthWeek) {
        this.monthWeek = monthWeek;
    }

    public Short getRecurMonth() {
        return recurMonth;
    }

    public void setRecurMonth(Short recurMonth) {
        this.recurMonth = recurMonth;
    }

    public WeekDay getMonthWeekValue() {
        return monthWeek != null ? WeekDay.getWeekDay(monthWeek.getId()) : null;
    }

    public RankType getWeekRank() {
        return monthRank != null ? RankType.getRankType(monthRank.getId()) : null;
    }

    public boolean isOfProductOffering(LoanOfferingBO loanOfferingBO) {
        return this.loanOffering.isOfSameOffering(loanOfferingBO);
    }

    /***********************************
     * Financial Calculation Refactoring
     ***********************************/

    private void generateMeetingSchedule(boolean isRepaymentIndepOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay)
            throws AccountException {

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Generating meeting schedule... ");

        // WHY?
        if (isRepaymentIndepOfMeetingEnabled && newMeetingForRepaymentDay != null) {
            setLoanMeeting(newMeetingForRepaymentDay);
        }
        List<InstallmentDate> installmentDates = getInstallmentDates(getLoanMeeting(), noOfInstallments,
                getInstallmentSkipToStartRepayment(isRepaymentIndepOfMeetingEnabled), isRepaymentIndepOfMeetingEnabled);

        // installment dates that have not been adjusted for holidays
        List<InstallmentDate> nonAdjustedInstallmentDates = getInstallmentDates(getLoanMeeting(), noOfInstallments,
                getInstallmentSkipToStartRepayment(isRepaymentIndepOfMeetingEnabled), isRepaymentIndepOfMeetingEnabled,
                false);

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Obtained intallments dates");

        Money loanInterest = getLoanInterest_v2();
        List<EMIInstallment> EMIInstallments = generateEMI_v2(loanInterest);

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Emi installment  obtained ");

        validateSize(installmentDates, EMIInstallments);
        List<FeeInstallment> feeInstallment = new ArrayList<FeeInstallment>();
        if (getAccountFees().size() != 0) {
            /*
             * KEITH TODO: The loan interest is not correct for declining
             * balance modes, and appears to be causing unit test to fail for
             * this mode. For declining-balance loans, to calculate the loan
             * interest you must either (a) apply a complicated formula (b)
             * compute the sum of interest paid across all installments.
             */
            populateAccountFeeAmount(getAccountFees(), loanInterest);
            feeInstallment = mergeFeeInstallments(getFeeInstallments(installmentDates, nonAdjustedInstallmentDates));
        }

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Fee installment obtained ");

        generateRepaymentSchedule(installmentDates, EMIInstallments, feeInstallment);

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Meeting schedule generated  ");

        applyRounding_v2();
    }

    /**
     * refactored to eliminate dependency on isRepaymentIndepOfMeetingEnabled.
     */
    private Short getInstallmentSkipToStartRepayment_v2() {
        if (isInterestDeductedAtDisbursement() || getGraceType() == GraceType.PRINCIPALONLYGRACE
                || getGraceType() == GraceType.NONE) {
            return (short) 0;
        } else
            // getGraceType() == GraceType.ALL
            return (short) getGracePeriodDuration();
    }

    // the decliningEPI amount = sum of interests for all installments
    private Money getDecliningEPIAmountNonGrace_v2(int numNonGraceInstallments) {
        Money principalBalance = getLoanAmount();
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numNonGraceInstallments));
        double interestRate = getInterestFractionalRatePerInstallment_v2();
        Money totalInterest = new Money("0");
        for (int i = 0; i < numNonGraceInstallments; i++) {
            Money interestThisPeriod = principalBalance.multiply(interestRate);
            totalInterest = totalInterest.add(interestThisPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
        }

        return totalInterest;
    }

    private Money getDecliningEPIAmount_v2() throws AccountException {

        Money interest = new Money("0");
        if (getGraceType().equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningEPIAmountGrace_v2();
            Money nonGraceInterestPayments = getDecliningEPIAmountNonGrace_v2(getNoOfInstallments()
                    - getGracePeriodDuration());
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        } else {
            interest = getDecliningEPIAmountNonGrace_v2(getNoOfInstallments());
        }
        return interest;
    }

    private Money getLoanInterest_v2() throws AccountException {

        Money interest = null;
        if (getLoanOffering().getInterestTypes().getId().equals(InterestType.FLAT.getValue())) {
            interest = getFlatInterestAmount_v2();
        }
        if (getLoanOffering().getInterestTypes().getId().equals(InterestType.DECLINING.getValue())) {
            interest = getDecliningInterestAmount_v2();
        } else if (getLoanOffering().getInterestTypes().getId().equals(InterestType.DECLINING_EPI.getValue())) {
            interest = getDecliningEPIAmount_v2();
        }
        return interest;
    }

    /**
     * remove dependence on installmentEndDate
     */
    private Money getFlatInterestAmount_v2() throws AccountException {
        // TODO: interest rate should be a BigDecimal ?
        Double interestRateDouble = getInterestRate();
        // TODO: durationInYears should be a BigDeciaml ?
        Double durationInYearsDouble = getTotalDurationInYears_v2();
        BigDecimal interestRate = new BigDecimal(interestRateDouble, Money.getInternalPrecisionAndRounding());
        BigDecimal durationInYears = new BigDecimal(durationInYearsDouble, Money.getInternalPrecisionAndRounding());
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Get interest duration in years..." + durationInYears);
        // the calls to Money.multiply() and Money.divide() round prematurely!
        Money interest = getLoanAmount().multiply(interestRate).multiply(durationInYears).divide(new BigDecimal("100"));
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Get interest accumulated..." + interest);
        return interest;
    }

    /**
     * Compute the total interest due on a declining-interest loan. Interest
     * during a principal-only grace period is calculated differently from
     * non-grace-periods.
     * <p>
     * The formula is as follows:
     * <p>
     * The total interest paid is I = Ig + In where Ig = interest paid during
     * any principal-only grace periods In = interest paid during regular
     * payment periods In = A - P A = total amount paid across regular payment
     * periods The formula for computing A is A = p * n where A = total amount
     * paid p = payment per installment n = number of regular (non-grace)
     * installments P = principal i = interest per period
     */
    private Money getDecliningInterestAmount_v2() throws AccountException {

        Money interest = new Money("0");
        if (getGraceType().equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningInterestAmountGrace_v2();
            Money nonGraceInterestPayments = getDecliningInterestAmountNonGrace_v2(getNoOfInstallments()
                    - getGracePeriodDuration());
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        } else
            interest = getDecliningInterestAmountNonGrace_v2(getNoOfInstallments());
        return interest;
    }

    // the business rules for DecliningEPI for grace periods are the same as
    // Declining's
    private Money getDecliningEPIAmountGrace_v2() {
        return getDecliningInterestAmountGrace_v2();
    }

    private Money getDecliningInterestAmountGrace_v2() {
        return getLoanAmount().multiply(getInterestFractionalRatePerInstallment_v2()).multiply(
                (double) (getGracePeriodDuration()));
    }

    private Money getDecliningInterestAmountNonGrace_v2(int numNonGraceInstallments) {
        Money totalPayments = getPaymentPerPeriodForDecliningInterest_v2(numNonGraceInstallments).multiply(
                (double) numNonGraceInstallments);
        return totalPayments.subtract(getLoanAmount());
    }

    /*
     * double --> BigDecimal
     */
    private double getTotalDurationInYears_v2() throws AccountException {
        int interestDays = getInterestDays();
        int daysInWeek = getDaysInWeek();
        int daysInMonth = getDaysInMonth();

        Short recurrenceType = this.getLoanMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId();

        int duration = (getNoOfInstallments()) * this.getLoanMeeting().getMeetingDetails().getRecurAfter();

        if (recurrenceType.equals(RecurrenceType.MONTHLY.getValue())) {
            double totalMonthDays = duration * daysInMonth;
            double durationInYears = totalMonthDays / AccountConstants.INTEREST_DAYS_360;
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Get total month days.." + totalMonthDays);
            return durationInYears;
        } else if (interestDays == AccountConstants.INTEREST_DAYS_360) {
            if (recurrenceType.equals(RecurrenceType.WEEKLY.getValue())) {
                double totalWeekDays = duration * daysInWeek;
                double durationInYears = totalWeekDays / AccountConstants.INTEREST_DAYS_360;
                MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
                        .debug("Get total week days.." + totalWeekDays);
                return durationInYears;
            }
            throw new AccountException(AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
        } else if (interestDays == AccountConstants.INTEREST_DAYS_365) {
            if (recurrenceType.equals(RecurrenceType.WEEKLY.getValue())) {
                MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Get interest week 365 days");
                double totalWeekDays = duration * daysInWeek;
                double durationInYears = totalWeekDays / AccountConstants.INTEREST_DAYS_365;
                return durationInYears;
            }
            throw new AccountException(AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
        } else
            throw new AccountException(AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
    }

    private List<EMIInstallment> generateEMI_v2(Money loanInterest) throws AccountException {
        if (isInterestDeductedAtDisbursement()) {
            /*
             * Interest deducted at disbursement has been cut from r1.1 so throw
             * an exception if we reach this code.
             */
            throw new AccountException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
            /*
             * if (getLoanOffering().isPrinDueLastInst()) { return
             * interestDeductedFirstPrincipalLast_v2(loanInterest); } else {
             * return interestDeductedAtDisbursement_v2(loanInterest); }
             */
        } else {

            if (getLoanOffering().isPrinDueLastInst()) {
                /*
                 * Principal due on last installment has been cut, so throw an
                 * exception if we reach this code.
                 */
                throw new AccountException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
                /*
                 * if (getLoanOffering().getInterestTypes().getId().equals(
                 * InterestType.FLAT.getValue())) { return
                 * principalInLastPayment_v2(loanInterest); } else if
                 * ((getLoanOffering().getInterestTypes().getId().equals(
                 * InterestType.DECLINING.getValue())) ||
                 * (getLoanOffering().getInterestTypes().getId().equals(
                 * InterestType.DECLINING_EPI.getValue()))) { return
                 * principalInLastPaymentDecliningInterest_v2(loanInterest); }
                 */
            } else if (getLoanOffering().getInterestTypes().getId().equals(InterestType.FLAT.getValue())) {
                return allFlatInstallments_v2(loanInterest);
            } else if (getLoanOffering().getInterestTypes().getId().equals(InterestType.DECLINING.getValue())) {
                return allDecliningInstallments_v2();
            } else if (getLoanOffering().getInterestTypes().getId().equals(InterestType.DECLINING_EPI.getValue())) {
                return allDecliningEPIInstallments_v2();
            }
        }

        throw new AccountException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
    }

    private List<EMIInstallment> interestDeductedAtDisbursement_v2(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        // grace can only be none
        if (getGraceType() == GraceType.GRACEONALLREPAYMENTS || getGraceType() == GraceType.PRINCIPALONLYGRACE)
            throw new AccountException(AccountConstants.INTERESTDEDUCTED_INVALIDGRACETYPE);

        if (getGraceType() == GraceType.NONE) {
            Money interestFirstInstallment = loanInterest;
            // principal starts only from the second installment
            Money principalPerInstallment = new Money(Double.toString(getLoanAmount().getAmountDoubleValue()
                    / (getNoOfInstallments() - 1)));
            EMIInstallment installment = new EMIInstallment();
            installment.setPrincipal(new Money());
            installment.setInterest(interestFirstInstallment);
            emiInstallments.add(installment);
            for (int i = 1; i < getNoOfInstallments(); i++) {
                installment = new EMIInstallment();
                installment.setPrincipal(principalPerInstallment);
                installment.setInterest(new Money());

                emiInstallments.add(installment);
            }
        }
        return emiInstallments;
    }

    private List<EMIInstallment> principalInLastPayment_v2(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        // grace can only be none
        if (getGraceType() == GraceType.PRINCIPALONLYGRACE)
            throw new AccountException(AccountConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
        if (getGraceType() == GraceType.NONE || getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
            Money principalLastInstallment = getLoanAmount();
            // principal starts only from the second installment
            Money interestPerInstallment = new Money(Double.toString(loanInterest.getAmountDoubleValue()
                    / getNoOfInstallments()));
            EMIInstallment installment = null;
            for (int i = 0; i < getNoOfInstallments() - 1; i++) {
                installment = new EMIInstallment();
                installment.setPrincipal(new Money());
                installment.setInterest(interestPerInstallment);
                emiInstallments.add(installment);
            }
            // principal set in the last installment
            installment = new EMIInstallment();
            installment.setPrincipal(principalLastInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
            return emiInstallments;
        }
        throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
    }

    private List<EMIInstallment> principalInLastPaymentDecliningInterest_v2(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        // grace can only be none
        if (getGraceType() == GraceType.PRINCIPALONLYGRACE)
            throw new AccountException(AccountConstants.PRINCIPALLASTPAYMENT_INVALIDGRACETYPE);
        if (getGraceType() == GraceType.NONE || getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
            Money principalLastInstallment = getLoanAmount();

            Money interestPerInstallment = new Money(Double.toString(getLoanAmount().getAmountDoubleValue()
                    * (getInterestRate() / 100 / getDecliningInterestAnnualPeriods())));
            EMIInstallment installment = null;
            for (int i = 0; i < getNoOfInstallments() - 1; i++) {
                installment = new EMIInstallment();
                installment.setPrincipal(new Money());
                installment.setInterest(interestPerInstallment);
                emiInstallments.add(installment);
            }
            // principal set in the last installment
            installment = new EMIInstallment();
            installment.setPrincipal(principalLastInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
            return emiInstallments;
        }
        throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
    }

    /****************
     * Loan calculation refactoring -- Flat-installment calculations, including
     * grace-period calculations.
     ****************/

    /**
     * Generate flat-interest installment variants based on the type of grace
     * period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest,
     * the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments.
     * The first grace installments are interest-only, and principal is paid off
     * with the remaining installments. NOTE: Principal-only grace period should
     * be disable for release 1.1.
     * </ul>
     */
    private List<EMIInstallment> allFlatInstallments_v2(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        if (getGraceType() == GraceType.NONE || getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateFlatInstallmentsNoGrace_v2(loanInterest);
        } else {
            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.
            emiInstallments = generateFlatInstallmentsInterestOnly_v2(loanInterest);
            emiInstallments.addAll(generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(loanInterest));
        }
        return emiInstallments;
    }

    /**
     * Divide principal and interest evenly among all installments, no grace
     * period
     */
    private List<EMIInstallment> generateFlatInstallmentsNoGrace_v2(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalPerInstallment = getLoanAmount().divide(
                new BigDecimal(getNoOfInstallments(), Money.getInternalPrecisionAndRounding()));
        Money interestPerInstallment = loanInterest.divide(new BigDecimal(getNoOfInstallments(), Money
                .getInternalPrecisionAndRounding()));
        for (int i = 0; i < getNoOfInstallments(); i++) {
            EMIInstallment installment = new EMIInstallment();
            installment.setPrincipal(principalPerInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }

    /**
     * Calculate the installments after grace period, in the case of
     * principal-only grace type for a flat-interest loan. Divide interest
     * evenly among all installments, but divide principle evenly among
     * installments after the grace period.
     */
    private List<EMIInstallment> generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(Money loanInterest)
            throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalPerInstallment = getLoanAmount().divide(
                new BigDecimal(getNoOfInstallments().shortValue() - getGracePeriodDuration().shortValue(), Money
                        .getInternalPrecisionAndRounding()));
        Money interestPerInstallment = loanInterest.divide(new BigDecimal(getNoOfInstallments(), Money
                .getInternalPrecisionAndRounding()));
        for (int i = getGracePeriodDuration(); i < getNoOfInstallments(); i++) {
            EMIInstallment installment = new EMIInstallment();
            installment.setPrincipal(principalPerInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }

    /**
     * Generate zero-payment installments for the duration of the grace period.
     * NOTE: Not used, since zero-payment installments are not added to the list
     * of all installments.
     */
    private List<EMIInstallment> generateSkippedGraceInstallments_v2() {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = new Money(new BigDecimal(0.0, Money.getInternalPrecisionAndRounding()));

        for (int i = 0; i < getGracePeriodDuration(); i++) {
            EMIInstallment installment = new EMIInstallment();
            installment.setInterest(zero);
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period.
     * Interest is divided evenly among all installments, but only interest is
     * paid during the grace period.
     */
    private List<EMIInstallment> generateFlatInstallmentsInterestOnly_v2(Money loanInterest) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = new Money(new BigDecimal(0.0, Money.getInternalPrecisionAndRounding()));

        Money interestPerInstallment = loanInterest.divide(new BigDecimal(getNoOfInstallments(), Money
                .getInternalPrecisionAndRounding()));

        for (int i = 0; i < getGracePeriodDuration(); i++) {
            EMIInstallment installment = new EMIInstallment();
            installment.setInterest(interestPerInstallment);
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    /*
     * Calculates equal payments per period for fixed payment,
     * declining-interest loan type. Uses formula from
     * http://confluence.mifos.org
     * :9090/display/Main/Declining+Balance+Example+Calcs The formula is copied
     * here: EMI = P * i / [1- (1+i)^-n] where p = principal (amount of loan) i
     * = rate of interest per installment period as a decimal (not percent) n =
     * no. of installments
     * 
     * Translated into program variables and method calls:
     * 
     * paymentPerPeriod = interestFractionalRatePerPeriod * getLoanAmount() / (
     * 1 - (1 + interestFractionalRatePerPeriod) ^ (-getNoOfInstallments()))
     * 
     * NOTE: Use double here, not BigDecimal, to calculate the factor that
     * getLoanAmount() is multiplied by. Since calculations all involve small
     * quantities, 64-bit precision is sufficient. It is is more accurate to use
     * floating-point, for quantities of small magnitude (say for very small
     * interest rates)
     * 
     * NOTE: These calculations do not take into account EPI or grace period
     * adjustments.
     */
    private Money getPaymentPerPeriodForDecliningInterest_v2(int numInstallments) {
        double factor = 0.0;
        if (interestRate == 0.0) {
            Money paymentPerPeriod = getLoanAmount().divide(
                    new BigDecimal(numInstallments, Money.getInternalPrecisionAndRounding()));
            return paymentPerPeriod;
        } else {
            factor = getInterestFractionalRatePerInstallment_v2()
                    / (1.0 - Math.pow(1.0 + getInterestFractionalRatePerInstallment_v2(), -numInstallments));
            Money paymentPerPeriod = getLoanAmount().multiply(
                    new BigDecimal(factor, Money.getInternalPrecisionAndRounding()));
            return paymentPerPeriod;
        }
    }

    private double getInterestFractionalRatePerInstallment_v2() {
        double f = interestRate / getDecliningInterestAnnualPeriods_v2() / 100;
        return f;
    }

    /**
     * Generate declining-interest installment variants based on the type of
     * grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest,
     * the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments.
     * The first grace installments are interest-only, and principal is paid off
     * with the remaining installments.
     * </ul>
     */
    private List<EMIInstallment> allDecliningInstallments_v2() throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        if (getGraceType() == GraceType.NONE || getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningInstallmentsNoGrace_v2(getNoOfInstallments());
        } else {

            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.

            emiInstallments = generateDecliningInstallmentsInterestOnly_v2();
            emiInstallments.addAll(generateDecliningInstallmentsAfterInterestOnlyGraceInstallments_v2());
        }
        return emiInstallments;
    }

    private List<EMIInstallment> generateDecliningEPIInstallmentsAfterInterestOnlyGraceInstallments_v2()
            throws AccountException {

        return generateDecliningEPIInstallmentsNoGrace_v2(getNoOfInstallments() - getGracePeriodDuration());
    }

    private List<EMIInstallment> allDecliningEPIInstallments_v2() throws AccountException {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        if (getGraceType() == GraceType.NONE || getGraceType() == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningEPIInstallmentsNoGrace_v2(getNoOfInstallments());
        } else {
            emiInstallments = generateDecliningEPIInstallmentsInterestOnly_v2();
            emiInstallments.addAll(generateDecliningEPIInstallmentsAfterInterestOnlyGraceInstallments_v2());
        }
        return emiInstallments;
    }

    /**
     * Return the list if payment installments for declining interest method,
     * for the number of installments specified.
     */
    private List<EMIInstallment> generateDecliningInstallmentsNoGrace_v2(int numInstallments) throws AccountException {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numInstallments);

        // Now calculate the details of each installment. These are the exact
        // values, and have not been
        // adjusted for rounding and precision factors.

        Money principalBalance = getLoanAmount();

        for (int i = 0; i < numInstallments; i++) {

            EMIInstallment installment = new EMIInstallment();

            Money interestThisPeriod = principalBalance.multiply(getInterestFractionalRatePerInstallment_v2());
            Money principalThisPeriod = paymentPerPeriod.subtract(interestThisPeriod);

            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalThisPeriod);
            principalBalance = principalBalance.subtract(principalThisPeriod);

            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    private List<EMIInstallment> generateDecliningEPIInstallmentsNoGrace_v2(int numInstallments)
            throws AccountException {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalBalance = getLoanAmount();
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numInstallments));
        double interestRate = getInterestFractionalRatePerInstallment_v2();

        for (int i = 0; i < numInstallments; i++) {
            EMIInstallment installment = new EMIInstallment();
            Money interestThisPeriod = principalBalance.multiply(interestRate);
            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalPerPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period.
     * Interest paid is on the outstanding balance, which during the grace
     * period is the entire principal amount.
     */
    private List<EMIInstallment> generateDecliningInstallmentsInterestOnly_v2() {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = new Money(new BigDecimal(0.0, Money.getInternalPrecisionAndRounding()));
        for (int i = 0; i < getGracePeriodDuration(); i++) {
            EMIInstallment installment = new EMIInstallment();
            installment.setInterest(this.getLoanAmount().multiply(getInterestFractionalRatePerInstallment_v2()));
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

    // same as Declining
    private List<EMIInstallment> generateDecliningEPIInstallmentsInterestOnly_v2() {

        return generateDecliningInstallmentsInterestOnly_v2();
    }

    /**
     * Calculate the installments after grace period, in the case of
     * principal-only grace type for a declining-interest loan. Calculation is
     * identical to the no-grace scenario except that the number of installments
     * is reduced by the grace period.
     */
    private List<EMIInstallment> generateDecliningInstallmentsAfterInterestOnlyGraceInstallments_v2()
            throws AccountException {

        return generateDecliningInstallmentsNoGrace_v2(getNoOfInstallments() - getGracePeriodDuration());
    }

    private List<EMIInstallment> interestDeductedFirstPrincipalLast_v2(Money loanInterest) throws AccountException {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        if (getGraceType() == GraceType.GRACEONALLREPAYMENTS || getGraceType() == GraceType.PRINCIPALONLYGRACE)
            throw new AccountException(AccountConstants.INTERESTDEDUCTED_PRINCIPALLAST);
        if (getGraceType() == GraceType.NONE) {
            Money principalLastInstallment = getLoanAmount();
            Money interestFirstInstallment = loanInterest;

            EMIInstallment installment = null;
            installment = new EMIInstallment();
            installment.setPrincipal(new Money());
            installment.setInterest(interestFirstInstallment);
            emiInstallments.add(installment);
            for (int i = 1; i < getNoOfInstallments() - 1; i++) {
                installment = new EMIInstallment();
                installment.setPrincipal(new Money());
                installment.setInterest(new Money());
                emiInstallments.add(installment);
            }
            installment = new EMIInstallment();
            installment.setPrincipal(principalLastInstallment);
            installment.setInterest(new Money());
            emiInstallments.add(installment);
            return emiInstallments;
        }
        throw new AccountException(AccountConstants.NOT_SUPPORTED_GRACE_TYPE);
    }

    /**
     * Corrects two defects:
     * <ul>
     * <li>period was being rounded to the closest integer because all of the
     * factors involved in the calculation are integers. First, convert the
     * factors to double values.
     * <li>calculation uses the wrong formula for monthly installments. Whether
     * fiscal year is 360 or 365, just consider a month to be 1/12 of a year.
     */
    private double getDecliningInterestAnnualPeriods_v2() {
        RecurrenceType meetingFrequency = getLoanMeeting().getMeetingDetails().getRecurrenceTypeEnum();

        short recurAfter = getLoanMeeting().getMeetingDetails().getRecurAfter();

        double period = 0;

        double numInterestDays = getInterestDays();
        double numDaysInWeek = getDaysInWeek();
        if (meetingFrequency.equals(RecurrenceType.WEEKLY)) {
            period = ((double) getInterestDays()) / (double) ((getDaysInWeek() * recurAfter));

        }
        /*
         * The use of monthly interest here does not distinguish between the 360
         * (with equal 30 day months) and the 365 day year cases. Should it?
         */
        else if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
            period = recurAfter * 12;
        }

        return period;
    }

    /***********************************
     * Revised fee calculations
     ***********************************/

    private Money getAccountFeeAmount_v2(AccountFeesEntity accountFees, Money loanInterest) {
        Money accountFeeAmount = new Money();
        Double feeAmount = accountFees.getFeeAmount();

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Fee amount..." + feeAmount);

        if (accountFees.getFees().getFeeType().equals(RateAmountFlag.AMOUNT)) {
            accountFeeAmount = new Money(feeAmount.toString());
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "AccountFeeAmount for amount fee.." + feeAmount);
        } else if (accountFees.getFees().getFeeType().equals(RateAmountFlag.RATE)) {
            RateFeeBO rateFeeBO = new FeePersistence().getRateFee(accountFees.getFees().getFeeId());
            accountFeeAmount = new Money(getRateBasedOnFormula(feeAmount, rateFeeBO.getFeeFormula(), loanInterest));
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "AccountFeeAmount for Formula fee.." + feeAmount);
        }
        return accountFeeAmount;
    }

    /**
     * for V1.1, assume that apply-rounding is applied only to "fresh" loans
     * that have no prior payments, and then only when rounding is needed --
     * when applying or removing charges that carry greater precision than the
     * rounding precision specified for applicable installments. TODO: correct
     * this after establishing business rules for what installments must be
     * re-rounded when changing the loan mid-stream.
     */
    protected List<AccountActionDateEntity> getInstallmentsToRound() {
        List<AccountActionDateEntity> installments = this.getAllInstallments();
        Collections.sort(installments);
        return installments;
    }

    /**
     * This method adjusts payments by applying rounding rules from
     * AccountingRules.
     * 
     * <h2>Summary of rounding rules</h2> There are two factors that make up a
     * rounding rule: precision and mode.
     * 
     * <dl>
     * <dt> <em>Precision</em>
     * <dd>specifies the degree of rounding, to the closest decimal place.
     * Example: 1 (closest Rupee), 0.5 (closest half-dollar, for example), 0.1,
     * 0.01 (closest US penny) 0.001, etc. Precision is limited by the currency
     * being used by the application. For example, US dollars limit the
     * precision to two decimal places (closest penny).
     * 
     * <dt> <em>Mode</em>
     * <dd>specfies how rounding occurs. Currently three modes are supported:
     * HALF_UP, FLOOR, CEILING.
     * </dl>
     * 
     * Three installment-rounding conventions apply to loan-installment
     * payments. Each specifies the precision and mode to be applied in certain
     * contexts:
     * 
     * <dl>
     * <dt> <em>Currency-rounding</em>
     * <dd>Round to the precision of the currency being used by the application.
     * 
     * <dt> <em>Initial rounding</em>
     * <dd>Round all installment's total payment but the last.
     * 
     * <dt> <em>Final rounding</em>
     * <dd>Round the last payment to a specified precision.
     * </dl>
     * 
     * 
     * <h2>Summary of rounding and adjustment logic</h2>
     * 
     * Assume we've calculated exact values for each installment's principal,
     * interest, and fees payment, and installment's total payment (their sum).
     * <p/>
     * The concept here is that exact values will be rounded and the amounts
     * that the customer actually pays will drift away from what's actually due,
     * resulting in the components of each installment not exactly adding up to
     * the total payment.
     * <p/>
     * Generally, within each installment but the last, the principal payment is
     * the "fall guy", making up for any difference. For the last installment,
     * the interest payment is the fall guy.
     * <p/>
     * Differences in total paid across all installments are made up in the last
     * installment.
     * <p/>
     * <h4>Rounding and adjusting total payments</h4>
     * First compute the rounded and adjusted totals for the loan. These are
     * used to adjust the final installment's payments.
     * <ul>
     * <li>Round the loan's exact total payments (sum of exact principal, exact
     * interest, exact fees) using final rounding.
     * <li>No need to round the principal, since it is entered using precision
     * of the prevailing currency.
     * <li>Round total fees using currency rounding
     * <li>Adjust the total interest so that rounded fees, principal, and
     * adjusted interest sum to the rounded total payments.
     * </ul>
     * </ul>
     * <h4>Non-grace-period installments except the last:</h4>
     * <ul>
     * <li>Round the installment's exact total payment using initial rounding.
     * <li>Round the installment's exact interest and fee payments using
     * currency rounding.
     * <li>Round each of the installment's account fees using currency rounding.
     * <li>Adjust the installment's principal to make up the difference between
     * the installment's rounded total payment and its rounded interest and fee
     * payments.
     * <li>After rounding and adjusting, the installment's (rounded) total
     * payment is exactly the sum of (rounded) principal, interest and fees.
     * </ul>
     * 
     * <h4>The last installment:</h4>
     * <ul>
     * <li>Correct for over- or underpayment of prior installment's payments due
     * to rounding:
     * <ul>
     * <li>Compute the loan's exact total payment as the sum of all
     * installment's exact principal, interest and fees.
     * <li>Round the loan's exact total payment using final rounding. This is
     * what the customer must pay to pay off the loan.
     * <li>Set the final installment's total payment to the difference between
     * the loan's rounded total payment and the sum of all prior installments'
     * (already rounded) payments.
     * </ul>
     * <li>Correct for over or underpayment of principal. Set the last
     * installment's principal payment to the difference between the loan amount
     * and the sum of all prior installment's principal payments, then round it
     * using currency rounding rules.
     * <li>Correct for over- or underpayment of fees:
     * <ul>
     * <li>Round the exact total fees using Currency rounding rules.
     * <li>Set one of last installment's fee payments to the difference between
     * the rounded total fees and the sum of all prior installments' (already
     * rounded) fee payments.
     * </ul>
     * <li>Finally, adjust the last installment's interest payment as the
     * difference between the last installment's total payment and the sum of
     * the last installment's principal and fee payments.
     * </ul>
     * 
     * <h4>Principal-only grace-period installments</h4>
     * 
     * The principal is always zero, and only interest and fees are paid. Here,
     * interest is the "fall guy", absorbing any rounding discrepancies:
     * <ul>
     * <li>Round the installment's total payments as above.
     * <li>Round the installment's fee payment as above.
     * <li>Adjust the interest to force interest and fee payments to add up to
     * the installment's total payment.
     * </ul>
     * <h4>Principal + interest grace-period installments</h4>
     * 
     * Calculations are the same as if there were no grace, since the
     * zero-payment installments are not included in the installment list at
     * all.
     */

    protected final void applyRounding_v2() {

        List<AccountActionDateEntity> installments = getInstallmentsToRound();

        RepaymentTotals totals = calculateInitialTotals_v2(installments);
        int installmentNum = 0;
        for (Iterator it = installments.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) it.next();
            installmentNum++;
            if (it.hasNext()) { // handle all but the last installment
                if (isGraceInstallment_v2(installmentNum)) {
                    roundAndAdjustGraceInstallment_v2(currentInstallment);
                } else if (getLoanOffering().getInterestTypes().getId().equals(InterestType.DECLINING_EPI.getValue())) {
                    roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(currentInstallment);
                } else {
                    roundAndAdjustButLastNonGraceInstallment_v2(currentInstallment);
                }
                updateRunningTotals_v2(totals, currentInstallment);
            } else {
                roundAndAdjustLastInstallment_v2(currentInstallment, totals);

            }
        } // for
    }

    private void updateRunningTotals_v2(RepaymentTotals totals, LoanScheduleEntity currentInstallment) {

        totals.runningPayments = totals.runningPayments.add(currentInstallment.getTotalPaymentDue());

        totals.runningPrincipal = totals.runningPrincipal.add(currentInstallment.getPrincipalDue());
        totals.runningAccountFees = totals.runningAccountFees.add(currentInstallment.getTotalFeeDue());
        totals.runningMiscFees = totals.runningMiscFees.add(currentInstallment.getMiscFeeDue());
        totals.runningPenalties = totals.runningPenalties.add(currentInstallment.getPenaltyDue());
    }

    private Money currencyRound_v2(Money m) {
        return Money.round(m, AccountingRules.getDigitsAfterDecimalMultiple(), AccountingRules
                .getCurrencyRoundingMode());
    }

    private Money initialRound_v2(Money m) {
        return Money.round(m, AccountingRules.getInitialRoundOffMultiple(), AccountingRules.getInitialRoundingMode());
    }

    private Money finalRound_v2(Money m) {
        return Money.round(m, AccountingRules.getFinalRoundOffMultiple(), AccountingRules.getFinalRoundingMode());
    }

    private Money getExactTotalPaymentDue_v2(List<AccountActionDateEntity> unpaidInstallments) {
        Money sum = new Money("0");
        for (Iterator it = unpaidInstallments.iterator(); it.hasNext();) {
            sum = sum.add(((LoanScheduleEntity) it.next()).getTotalPaymentDue());
        }
        return sum;
    }

    /**
     * A grace-period installment can appear in the loan schedule only if the
     * loan is setup with principal-only grace.
     */
    private boolean isGraceInstallment_v2(int installmentNum) {
        return (getGraceType().equals(GraceType.PRINCIPALONLYGRACE) && installmentNum <= getGracePeriodDuration());
    }

    /**
     * See Javadoc comment for method applyRounding() for business rules for
     * rounding and adjusting all installments but the last. LoanScheduleEntity
     * does not store the total payment due, directly, but it is the sum of
     * principal, interest, and non-miscellaneous fees.
     * <p>
     * 
     * how to set rounded fee for installment?????? This is what I want to do:
     * currentInstallment.setFee (currencyRound_v2 (currentInstallment.getFee));
     * 
     * Then I want to adjust principal, but need to extract the rounded fee,
     * like this: currentInstallment.setPrincipal(installmentRoundedTotalPayment
     * .subtract (currentInstallment.getInterest() .subtract
     * (currentInstallment.getFee());
     */
    private void roundAndAdjustButLastNonGraceInstallment_v2(LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = initialRound_v2(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);

        installment.setInterest(currencyRound_v2(installment.getInterest()));
        // TODO: above comment applies to principal
        installment.setPrincipal(roundedTotalInstallmentPaymentDue.subtract(installment.getInterestDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()).add(
                installment.getPrincipalPaid()));
    }

    private void roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = initialRound_v2(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setPrincipal(currencyRound_v2(installment.getPrincipal()));
        // TODO: above comment applies to principal
        installment.setInterest(roundedTotalInstallmentPaymentDue.subtract(installment.getPrincipalDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()));
    }

    /**
     * See JavaDoc comment for applyRounding_v2. TODO: handle fees
     */
    private void roundAndAdjustLastInstallment_v2(LoanScheduleEntity lastInstallment, RepaymentTotals totals) {

        roundInstallmentAccountFeesDue_v2(lastInstallment);
        Money installmentPayment = finalRound_v2(totals.roundedPaymentsDue.subtract(totals.runningPayments));
        lastInstallment
                .setPrincipal(currencyRound_v2(totals.getRoundedPrincipalDue().subtract(totals.runningPrincipal)));
        adjustLastInstallmentFees_v2(lastInstallment, totals);
        lastInstallment.setInterest(currencyRound_v2(installmentPayment.subtract(lastInstallment.getPrincipalDue())
                .subtract(lastInstallment.getTotalFeeDueWithMiscFeeDue()).subtract(lastInstallment.getPenaltyDue())));
    }

    /**
     * adjust the first fee in the installment's set of fees
     */
    private void adjustLastInstallmentFees_v2(LoanScheduleEntity lastInstallment, RepaymentTotals totals) {
        Set<AccountFeesActionDetailEntity> feeDetails = lastInstallment.getAccountFeesActionDetails();
        if (!(feeDetails == null) && !feeDetails.isEmpty()) {
            Money lastInstallmentFeeSum = new Money();
            for (AccountFeesActionDetailEntity e : feeDetails) {
                lastInstallmentFeeSum = lastInstallmentFeeSum.add(e.getFeeAmount());
            }
            for (Iterator it = feeDetails.iterator(); it.hasNext();) {
                AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) it.next();
                e.adjustFeeAmount(totals.roundedAccountFeesDue.subtract(totals.runningAccountFees).subtract(
                        lastInstallmentFeeSum));
                // just adjust the first fee
                return;
            }
        }
    }

    /**
     * For principal-only grace installments, adjust the interest to account for
     * rounding discrepancies.
     */
    private void roundAndAdjustGraceInstallment_v2(LoanScheduleEntity installment) {
        Money roundedInstallmentTotalPaymentDue = initialRound_v2(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setInterest(roundedInstallmentTotalPaymentDue.subtract(installment.getTotalFeeDueWithMiscFeeDue())
                .subtract(installment.getPenaltyDue()));
    }

    private RepaymentTotals calculateInitialTotals_v2(List<AccountActionDateEntity> installmentsToBeRounded) {

        RepaymentTotals totals = new RepaymentTotals();

        Money exactTotalInterestDue = new Money("0");
        Money exactTotalAccountFeesDue = new Money("0");
        Money exactTotalMiscFeesDue = new Money("0");
        Money exactTotalMiscPenaltiesDue = new Money("0");

        // principal due = loan amount less any payments on principal
        Money exactTotalPrincipalDue = getLoanAmount();
        for (AccountActionDateEntity e : this.getAllInstallments()) {
            LoanScheduleEntity installment = (LoanScheduleEntity) e;
            exactTotalPrincipalDue = exactTotalPrincipalDue.subtract(installment.getPrincipalPaid());
        }

        for (Iterator it = installmentsToBeRounded.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) it.next();
            exactTotalInterestDue = exactTotalInterestDue.add(currentInstallment.getInterestDue());
            exactTotalAccountFeesDue = exactTotalAccountFeesDue.add(currentInstallment.getTotalFeeDue());
            exactTotalMiscFeesDue = exactTotalMiscFeesDue.add(currentInstallment.getMiscFeeDue());
            exactTotalMiscPenaltiesDue = exactTotalMiscPenaltiesDue.add(currentInstallment.getMiscPenaltyDue());
        }
        Money exactTotalPaymentsDue = exactTotalInterestDue.add(exactTotalAccountFeesDue).add(exactTotalMiscFeesDue)
                .add(exactTotalMiscPenaltiesDue).add(exactTotalPrincipalDue);

        totals.setRoundedPaymentsDue(finalRound_v2(exactTotalPaymentsDue));
        totals.setRoundedAccountFeesDue(currencyRound_v2(exactTotalAccountFeesDue));
        totals.setRoundedMiscFeesDue(currencyRound_v2(exactTotalMiscFeesDue));
        totals.setRoundedMiscPenaltiesDue(currencyRound_v2(exactTotalMiscPenaltiesDue));
        totals.setRoundedPrincipalDue(exactTotalPrincipalDue);

        // Adjust interest to account for rounding discrepancies
        totals.setRoundedInterestDue(totals.getRoundedPaymentsDue().subtract(totals.getRoundedAccountFeesDue())
                .subtract(totals.getRoundedMiscFeesDue()).subtract(totals.getRoundedPenaltiesDue()).subtract(
                        totals.getRoundedMiscPenaltiesDue()).subtract(totals.getRoundedPrincipalDue()));
        return totals;
    }

    private void roundInstallmentAccountFeesDue_v2(LoanScheduleEntity installment) {

        for (Iterator feeActionIt = installment.getAccountFeesActionDetails().iterator(); feeActionIt.hasNext();) {
            AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) feeActionIt.next();
            e.roundFeeAmount(currencyRound_v2(e.getFeeDue().add(e.getFeeAmountPaid())));
        }

    }

    private Money getExactTotalFeesDue_v2(List<AccountActionDateEntity> installments) {

        Money totalFees = new Money("0");
        for (Iterator it = installments.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) it.next();
            totalFees = totalFees.add(currentInstallment.getTotalFeeDue());
        }
        return totalFees;
    }

    /**
     * A struct to hold totals that can be passed around during rounding
     * computations.
     */
    private class RepaymentTotals {
        // rounded or adjusted totals prior to rounding installments
        Money roundedPaymentsDue;
        Money roundedInterestDue;
        Money roundedAccountFeesDue;
        Money roundedMiscFeesDue;
        Money roundedPenaltiesDue;
        Money roundedMiscPenaltiesDue;
        Money roundedPrincipalDue;

        // running totals as installments are rounded
        Money runningPayments = new Money("0");
        Money runningAccountFees = new Money("0");
        Money runningPrincipal = new Money("0");
        Money runningMiscFees = new Money("0");
        Money runningPenalties = new Money("0");
        Money runningMiscPenalties = new Money("0");

        Money getRoundedPaymentsDue() {
            return roundedPaymentsDue;
        }

        void setRoundedPaymentsDue(Money roundedPaymentsDue) {
            this.roundedPaymentsDue = roundedPaymentsDue;
        }

        Money getRoundedInterestDue() {
            return roundedInterestDue;
        }

        void setRoundedInterestDue(Money roundedInterestDue) {
            this.roundedInterestDue = roundedInterestDue;
        }

        Money getRoundedAccountFeesDue() {
            return roundedAccountFeesDue;
        }

        void setRoundedAccountFeesDue(Money roundedAccountFeesDue) {
            this.roundedAccountFeesDue = roundedAccountFeesDue;
        }

        Money getRoundedMiscFeesDue() {
            return roundedMiscFeesDue;
        }

        void setRoundedMiscFeesDue(Money roundedMiscFeesDue) {
            this.roundedMiscFeesDue = roundedMiscFeesDue;
        }

        Money getRoundedPenaltiesDue() {
            return roundedPenaltiesDue;
        }

        void setRoundedPenaltiesDue(Money roundedPenaltiesDue) {
            this.roundedPenaltiesDue = roundedPenaltiesDue;
        }

        Money getRoundedMiscPenaltiesDue() {
            return roundedMiscPenaltiesDue;
        }

        void setRoundedMiscPenaltiesDue(Money roundedMiscPenaltiesDue) {
            this.roundedMiscPenaltiesDue = roundedMiscPenaltiesDue;
        }

        Money getRunningPayments() {
            return runningPayments;
        }

        void setRunningPayments(Money runningPayments) {
            this.runningPayments = runningPayments;
        }

        Money getRunningAccountFees() {
            return runningAccountFees;
        }

        void setRunningAccountFees(Money runningAccountFees) {
            this.runningAccountFees = runningAccountFees;
        }

        Money getRunningPrincipal() {
            return runningPrincipal;
        }

        void setRunningPrincipal(Money runningPrincipal) {
            this.runningPrincipal = runningPrincipal;
        }

        Money getRunningMiscFees() {
            return runningMiscFees;
        }

        void setRunningMiscFees(Money runningMiscFees) {
            this.runningMiscFees = runningMiscFees;
        }

        Money getRunningPenalties() {
            return runningPenalties;
        }

        void setRunningPenalties(Money runningPenalties) {
            this.runningPenalties = runningPenalties;
        }

        Money getRunningMiscPenalties() {
            return runningMiscPenalties;
        }

        void setRunningMiscPenalties(Money runningMiscPenalties) {
            this.runningMiscPenalties = runningMiscPenalties;
        }

        Money getRoundedPrincipalDue() {
            return roundedPrincipalDue;
        }

        void setRoundedPrincipalDue(Money roundedPrincipalDue) {
            this.roundedPrincipalDue = roundedPrincipalDue;
        }
    }

    static boolean isDisbursementDateAfterCustomerActivationDate(Date disbursementDate, CustomerBO customer) {
        return DateUtils.dateFallsOnOrBeforeDate(customer.getCustomerActivationDate(), disbursementDate);
    }

    static boolean isDisbursementDateAfterProductStartDate(Date disbursementDate, LoanOfferingBO loanOffering) {
        return DateUtils.dateFallsOnOrBeforeDate(loanOffering.getStartDate(), disbursementDate);
    }

    static boolean isDisbursementDateLessThanCurrentDate(Date disbursementDate) {
        if (DateUtils.dateFallsBeforeDate(disbursementDate, DateUtils.getCurrentDateWithoutTimeStamp()))
            return true;
        return false;
    }

    static boolean isDibursementDateValidForRedoLoan(LoanOfferingBO loanOffering, CustomerBO customer,
            Date disbursementDate) {
        return isDisbursementDateAfterCustomerActivationDate(disbursementDate, customer)
                && isDisbursementDateAfterProductStartDate(disbursementDate, loanOffering);
    }

    /*
     * Existing loan accounts before Mifos 1.1 release will have
     * loan_summary.raw_amount_total = 0
     */
    public boolean isLegacyLoan() {

        if ((loanSummary == null) || (loanSummary.getRawAmountTotal() == null)) {
            return true;
        }
        Money defaultAmount = new Money("0");
        Money rawAmountTotal = loanSummary.getRawAmountTotal();
        return rawAmountTotal.equals(defaultAmount);
    }

    /*
     * 999 Account = interest paid + fees paid - (raw interest + raw fees)
     * Notes: loan accounts before Mifos 1.1 release will have their 999
     * accounts calculated the old way which is the difference between the last
     * payment rounded amount and original amount
     */

    public Money calculate999Account(boolean lastPayment) {
        Money account999 = new Money("0");
        if (isLegacyLoan()) {
            return account999;
        }
        Money origInterestAndFees = loanSummary.getOriginalFees().add(loanSummary.getOriginalInterest());
        Money paidInterestAndFees = loanSummary.getFeesPaid().add(loanSummary.getInterestPaid());
        if (lastPayment) {
            assert (origInterestAndFees.equals(paidInterestAndFees));
        }
        Money rawAmountTotal = loanSummary.getRawAmountTotal();
        account999 = origInterestAndFees.subtract(rawAmountTotal);
        return account999;
    }

    public Money getRawAmountTotal() {
        return rawAmountTotal;
    }

    public void setRawAmountTotal(Money rawAmountTotal) {
        this.rawAmountTotal = rawAmountTotal;
    }

}

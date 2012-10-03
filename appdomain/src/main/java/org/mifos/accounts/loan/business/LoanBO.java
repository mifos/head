/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountOverpaymentEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.AccountTypeEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.business.PenaltiesTrxnDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.schedule.calculation.ScheduleCalculator;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.loan.util.helpers.LoanPaymentTypes;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.InstallmentRange;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.FeeInstallment;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.OverDueAmounts;
import org.mifos.accounts.util.helpers.OverpaymentStatus;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.clientportfolio.newloan.domain.CreationDetail;
import org.mifos.clientportfolio.newloan.domain.DefaultLoanScheduleRounder;
import org.mifos.clientportfolio.newloan.domain.DefaultLoanScheduleRounderHelper;
import org.mifos.clientportfolio.newloan.domain.EqualInstallmentGeneratorFactory;
import org.mifos.clientportfolio.newloan.domain.EqualInstallmentGeneratorFactoryImpl;
import org.mifos.clientportfolio.newloan.domain.InstallmentFeeCalculator;
import org.mifos.clientportfolio.newloan.domain.InstallmentFeeCalculatorFactory;
import org.mifos.clientportfolio.newloan.domain.InstallmentFeeCalculatorFactoryImpl;
import org.mifos.clientportfolio.newloan.domain.Loan;
import org.mifos.clientportfolio.newloan.domain.LoanDecliningInterestAnnualPeriodCalculator;
import org.mifos.clientportfolio.newloan.domain.LoanDecliningInterestAnnualPeriodCalculatorFactory;
import org.mifos.clientportfolio.newloan.domain.LoanDurationInAccountingYearsCalculator;
import org.mifos.clientportfolio.newloan.domain.LoanDurationInAccountingYearsCalculatorFactory;
import org.mifos.clientportfolio.newloan.domain.LoanInstallmentFactory;
import org.mifos.clientportfolio.newloan.domain.LoanInstallmentFactoryImpl;
import org.mifos.clientportfolio.newloan.domain.LoanInstallmentGenerator;
import org.mifos.clientportfolio.newloan.domain.LoanInterestCalculationDetails;
import org.mifos.clientportfolio.newloan.domain.LoanInterestCalculator;
import org.mifos.clientportfolio.newloan.domain.LoanInterestCalculatorFactory;
import org.mifos.clientportfolio.newloan.domain.LoanInterestCalculatorFactoryImpl;
import org.mifos.clientportfolio.newloan.domain.LoanProductOverridenDetail;
import org.mifos.clientportfolio.newloan.domain.LoanSchedule;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleConfiguration;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleRounder;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleRounderHelper;
import org.mifos.clientportfolio.newloan.domain.PrincipalWithInterestGenerator;
import org.mifos.clientportfolio.newloan.domain.RecurringScheduledEventFactory;
import org.mifos.clientportfolio.newloan.domain.RecurringScheduledEventFactoryImpl;
import org.mifos.config.AccountingRules;
import org.mifos.config.business.Configuration;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.screen.LoanAccountDetailDto;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.framework.util.helpers.Transformer;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoanBO extends AccountBO implements Loan {

    private static final Logger logger = LoggerFactory.getLogger(LoanBO.class);

    private LegacyPersonnelDao legacyPersonnelDao = ApplicationContextProvider.getBean(LegacyPersonnelDao.class);
    private LegacyAccountDao legacyAccountDao = ApplicationContextProvider.getBean(LegacyAccountDao.class);
    private Integer businessActivityId;

    private Money loanAmount;
    private Money loanBalance;
    private Short noOfInstallments;
    private Date disbursementDate;

    /**
     * @deprecated interest deducted at disbursement not supported since version 1.1!!
     */
    @Deprecated
    private Short intrestAtDisbursement;

    private Short gracePeriodDuration;
    private Short gracePeriodPenalty;
    private Double interestRate;
    private boolean redone;
    private Integer collateralTypeId;
    private String collateralNote;
    private Short groupFlag;
    private String stateSelected;
    private Short recurMonth;
    private Money rawAmountTotal;

    // one-to-one associations
    // For Group loan with individual monitoring
    private LoanBO parentAccount;
    private final LoanPerformanceHistoryEntity performanceHistory;
    private final LoanOfferingBO loanOffering;
    private final LoanSummaryEntity loanSummary;
    private MaxMinLoanAmount maxMinLoanAmount;
    private MaxMinInterestRate maxMinInterestRate;
    private MaxMinNoOfInstall maxMinNoOfInstall;
    private MeetingBO loanMeeting;
    private GracePeriodTypeEntity gracePeriodType;
    private InterestTypesEntity interestType;
    private FundBO fund;
    private LoanArrearsAgingEntity loanArrearsAgingEntity;
    private WeekDay monthWeek;
    private RankOfDay monthRank;

    // associations
    private List<LoanActivityEntity> loanActivityDetails;
    private List<AccountOverpaymentEntity> accountOverpayments;
    
    // automatic penalties
    private Set<AccountPenaltiesEntity> loanAccountPenalties;

    private Set<LoanBO> memberAccounts;

    // persistence
    private LoanPrdPersistence loanPrdPersistence;
    private LegacyLoanDao legacyLoanDao = null;
    private LegacyMasterDao legacyMasterDao = ApplicationContextProvider.getBean(LegacyMasterDao.class);

    public LegacyLoanDao getlegacyLoanDao() {
        if (null == legacyLoanDao) {
            legacyLoanDao = ApplicationContextProvider.getBean(LegacyLoanDao.class);
        }
        return legacyLoanDao;
    }

    public void setlegacyLoanDao(final LegacyLoanDao legacyLoanDao) {
        this.legacyLoanDao = legacyLoanDao;
    }

    /**
     * default constructor for hibernate usage
     */
    protected LoanBO() {
        this.loanPrdPersistence = null;
        this.loanActivityDetails = new ArrayList<LoanActivityEntity>();
        this.accountOverpayments = new ArrayList<AccountOverpaymentEntity>();
        this.loanAccountPenalties = new LinkedHashSet<AccountPenaltiesEntity>();
        this.memberAccounts = new LinkedHashSet<LoanBO>();
        this.redone = false;
        this.parentAccount = null;
        this.loanOffering = null;
        this.loanSummary = null;
        this.maxMinLoanAmount = null;
        this.maxMinInterestRate = null;
        this.maxMinNoOfInstall = null;
        this.performanceHistory = null;
    }

    /**
     * replace usage of this in builder with appropriate usage of static factory method.
     */
    @Deprecated
    public LoanBO(final LoanOfferingBO loanProduct, final Short numOfInstallments, final GraceType gracePeriodType,
            final AccountTypes accountType, final AccountState accountState, final CustomerBO customer,
            final Integer offsettingAllowable, Money loanAmount, Money loanBalance) {
        super(accountType, accountState, customer, offsettingAllowable, new LinkedHashSet<AccountActionDateEntity>(),
                new HashSet<AccountFeesEntity>(), null, null);
        this.loanOffering = loanProduct;
        this.noOfInstallments = numOfInstallments;
        this.gracePeriodType = new GracePeriodTypeEntity(gracePeriodType);
        this.loanAmount = loanAmount;
        this.loanBalance = loanBalance;

        this.loanSummary = buildLoanSummary();
        this.performanceHistory = null;
        this.loanAccountPenalties = new LinkedHashSet<AccountPenaltiesEntity>();
    }

    // opening balance loan constructor
    public LoanBO(LoanOfferingBO loanProduct, CustomerBO customer, AccountState loanState, LoanProductOverridenDetail overridenDetail,
            MeetingBO repaymentDayMeeting, LoanSchedule loanSchedule, LoanScheduleConfiguration configuration,
            InstallmentRange installmentRange, AmountRange loanAmountRange, CreationDetail creationDetail) {
        super(AccountTypes.LOAN_ACCOUNT, loanState, customer, loanSchedule.getRoundedLoanSchedules(), creationDetail);
        this.parentAccount = null; // used for GLIM loans and will be set in factory method for this.
        this.performanceHistory = new LoanPerformanceHistoryEntity(this);

        this.loanOffering = loanProduct;
        this.customer = customer;
        this.loanAmount = overridenDetail.getLoanAmount();
        this.noOfInstallments = Integer.valueOf(overridenDetail.getNumberOfInstallments()).shortValue();
        this.gracePeriodDuration = Integer.valueOf(overridenDetail.getGraceDuration()).shortValue();
        this.disbursementDate = overridenDetail.getDisbursementDate().toDateMidnight().toDate();

        List<AccountFeesEntity> accountFeeEntities = overridenDetail.getAccountFeeEntities();
        for (AccountFeesEntity accountFeesEntity : accountFeeEntities) {
            accountFeesEntity.setAccount(this);
        }
        this.accountFees = new HashSet<AccountFeesEntity>(accountFeeEntities);
        
        List<AccountPenaltiesEntity> accountPenaltyEntities = overridenDetail.getAccountPenaltyEntities();
        for(AccountPenaltiesEntity accountPenaltyEntity : accountPenaltyEntities) {
            accountPenaltyEntity.setAccount(this);
        }
        this.loanAccountPenalties = new HashSet<AccountPenaltiesEntity>(accountPenaltyEntities);

        // inherit properties from loan product
        this.interestType = new InterestTypesEntity(loanProduct.getInterestType());
        this.interestRate = overridenDetail.getInterestRate();
        this.gracePeriodType = new GracePeriodTypeEntity(loanProduct.getGraceType());

        this.loanActivityDetails = new ArrayList<LoanActivityEntity>();
        this.accountOverpayments = new ArrayList<AccountOverpaymentEntity>();
        this.rawAmountTotal = loanSchedule.getRawAmount();
        this.loanSummary = buildLoanSummary();

        this.maxMinNoOfInstall = new MaxMinNoOfInstall(installmentRange.getMinNoOfInstall(), installmentRange.getMaxNoOfInstall(), this);
        this.maxMinLoanAmount = new MaxMinLoanAmount(loanAmountRange.getMaxLoanAmount(), loanAmountRange.getMinLoanAmount(), this);
        this.maxMinInterestRate = new MaxMinInterestRate(loanProduct.getMaxInterestRate(), loanProduct.getMinInterestRate(), this);

        // legacy
        this.intrestAtDisbursement = Short.valueOf("0"); // false
        this.gracePeriodPenalty = Short.valueOf("0"); // is this used
        this.loanBalance = overridenDetail.getLoanAmount(); // whats this used for? remaining balance?
        try {
            if (configuration.isLoanScheduleIndependentOfCustomerMeetingEnabled()) {
                this.loanMeeting = repaymentDayMeeting;
            } else {
                this.loanMeeting = buildLoanMeeting(customer.getCustomerMeetingValue(), loanProduct.getLoanOfferingMeetingValue(), disbursementDate);
            }
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey());
        }

        this.memberAccounts = new LinkedHashSet<LoanBO>();
    }

    public static LoanBO openStandardLoanAccount(LoanOfferingBO loanProduct, CustomerBO customer,
            MeetingBO repaymentDayMeeting, LoanSchedule loanSchedule, AccountState loanState, FundBO fund,
            LoanProductOverridenDetail overridenDetail, LoanScheduleConfiguration configuration,
            InstallmentRange installmentRange, AmountRange loanAmountRange,
            CreationDetail creationDetail, PersonnelBO createdBy) {

        LoanBO standardLoan = new LoanBO(loanProduct, customer, loanState, overridenDetail, repaymentDayMeeting, loanSchedule, configuration, installmentRange, loanAmountRange, creationDetail);

        standardLoan.setFund(fund);
        standardLoan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(standardLoan.getAccountState(), standardLoan.getAccountState(), createdBy, standardLoan));
        return standardLoan;
    }

    public static LoanBO openGroupMemberLoanAccount(LoanBO parentLoan, LoanOfferingBO loanProduct, ClientBO member,
            MeetingBO repaymentDayMeeting, LoanSchedule loanSchedule, LoanProductOverridenDetail overridenDetail,
            LoanScheduleConfiguration configuration, InstallmentRange installmentRange, AmountRange loanAmountRange, CreationDetail creationDetail, PersonnelBO createdBy) {

        AccountState loanState = AccountState.LOAN_PENDING_APPROVAL;
        LoanBO groupMemberLoan = new LoanBO(loanProduct, member, loanState, overridenDetail, repaymentDayMeeting, loanSchedule, configuration, installmentRange, loanAmountRange, creationDetail);
        groupMemberLoan.setParentAccount(parentLoan);
        groupMemberLoan.markAsIndividualLoanAccount();
        groupMemberLoan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(groupMemberLoan.getAccountState(), groupMemberLoan.getAccountState(), createdBy, groupMemberLoan));
        return groupMemberLoan;
    }

    private void markAsIndividualLoanAccount() {
        this.accountType = new AccountTypeEntity(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT.getValue());
    }

    public Map<Integer, LoanScheduleEntity> getLoanScheduleEntityMap(){
        Collection<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        return CollectionUtils.asValueMap(loanScheduleEntities, new Transformer<LoanScheduleEntity, Integer>() {
            @Override
            public Integer transform(LoanScheduleEntity input) {
                return Integer.valueOf(input.getInstallmentId());
            }
        });
    }

    public Set<LoanScheduleEntity> getLoanScheduleEntities() {
        return CollectionUtils.collect(this.getAccountActionDates(), new Transformer<AccountActionDateEntity, LoanScheduleEntity>() {
            @Override
            public LoanScheduleEntity transform(AccountActionDateEntity input) {
                return (LoanScheduleEntity) input;
            }
        });
    }

    public Integer getBusinessActivityId() {
        return businessActivityId;
    }

    public void setBusinessActivityId(final Integer businessActivityId) {
        this.businessActivityId = businessActivityId;
    }

    public String getCollateralNote() {
        return collateralNote;
    }

    public void setCollateralNote(final String collateralNote) {
        this.collateralNote = collateralNote;
    }

    public Integer getCollateralTypeId() {
        return collateralTypeId;
    }

    public void setCollateralTypeId(final Integer collateralTypeId) {
        this.collateralTypeId = collateralTypeId;
    }

    public GracePeriodTypeEntity getGracePeriodType() {
        return gracePeriodType;
    }

    public GraceType getGraceType() {
        return gracePeriodType.asEnum();
    }

    void setGracePeriodType(final GracePeriodTypeEntity gracePeriodType) {
        this.gracePeriodType = gracePeriodType;
    }

    void setGracePeriodType(final GraceType type) {
        setGracePeriodType(new GracePeriodTypeEntity(type));
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    void setDisbursementDate(final Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public FundBO getFund() {
        return fund;
    }

    public void setFund(final FundBO fund) {
        this.fund = fund;
    }

    public Short getGracePeriodDuration() {
        return gracePeriodDuration;
    }

    void setGracePeriodDuration(final Short gracePeriodDuration) {
        this.gracePeriodDuration = gracePeriodDuration;
    }

    public Short getGracePeriodPenalty() {
        return gracePeriodPenalty;
    }

    void setGracePeriodPenalty(final Short gracePeriodPenalty) {
        this.gracePeriodPenalty = gracePeriodPenalty;
    }

    public Short getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(final Short groupFlag) {
        this.groupFlag = groupFlag;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    void setInterestRate(final Double interestRate) {
        this.interestRate = interestRate;
    }

    public InterestTypesEntity getInterestType() {
        return interestType;
    }

    void setInterestType(final InterestTypesEntity interestType) {
        this.interestType = interestType;
    }
    
    /**
     * Returns the set of {@link AccountPenaltiesEntity}s -- links to the penalties that apply to this loan.
     */
    public Set<AccountPenaltiesEntity> getAccountPenaltiesIncludingInactivePenalties() {
        return loanAccountPenalties;
    }

    public Set<AccountPenaltiesEntity> getAccountPenalties() {
        Set<AccountPenaltiesEntity> activeAccountPenalties = new HashSet<AccountPenaltiesEntity>();
        
        for (AccountPenaltiesEntity accountPenaltyEntity : getAccountPenaltiesIncludingInactivePenalties()) {
            if (accountPenaltyEntity.isActive()) {
                activeAccountPenalties.add(accountPenaltyEntity);
            }
        }
        return activeAccountPenalties;
    }
    
    public void addAccountPenalty(final AccountPenaltiesEntity penalty) {
       loanAccountPenalties.add(penalty);
    }

    public void removeAccountPenalty(final AccountPenaltiesEntity penalty) {
        loanAccountPenalties.remove(penalty);
    }
    
    protected void updateLoanAccountPenaltiesEntity(final Short penaltyId) {
        AccountPenaltiesEntity accountPenalty = getAccountPenalty(penaltyId);
        if (accountPenalty != null) {
            accountPenalty.changePenaltyStatus(PenaltyStatus.INACTIVE, getDateTimeService().getCurrentJavaDateTime());
            accountPenalty.setLastAppliedDate(null);
        }
    }
    
    /**
     * Return an {@link AccountPenaltiesEntity} that links this account to the given penalty, or null if the penalty does not apply
     * to this account.
     *
     * @param penaltyId
     *            the primary key of the {@link PenaltyBO} being sought.
     * @return
     */
    public AccountPenaltiesEntity getAccountPenalty(final Short penaltyId) {
        for (AccountPenaltiesEntity accountPenaltyEntity : this.getAccountPenalties()) {
            if (accountPenaltyEntity.getPenalty().getPenaltyId().equals(penaltyId)) {
                return accountPenaltyEntity;
            }
        }
        return null;
    }

    public PenaltyBO getAccountPenaltyObject(final Short penaltyId) {
        AccountPenaltiesEntity accountPenalty = getAccountPenalty(penaltyId);
        if (accountPenalty != null) {
            return accountPenalty.getPenalty();
        }
        return null;
    }

    public Boolean isPenaltyActive(final Short penaltyId) {
        AccountPenaltiesEntity accountPenalty = getAccountPenalty(penaltyId);
        return accountPenalty.isActive();
    }
    
    protected final Boolean isPenaltyAlreadyApplied(final PenaltyBO penalty) {
        return getAccountPenalty(penalty.getPenaltyId()) != null;
    }
    
    /**
     * If the given {@PenaltyBO} has not yet been applied to this account, build and return a new
     * {@link AccountPenaltiesEntity} linking this account to the penalty; otherwise return the link object with the penalty amount
     * replaced with the charge.
     *
     * @param penalty
     *            the penalty to apply or update
     * @param charge
     *            the amount to charge for the given penalty
     * @return return the new or updated {@link AccountPenaltiesEntity} linking this account to the penalty
     */
    protected final AccountPenaltiesEntity getAccountPenalty(final PenaltyBO penalty, final Double charge) {
        AccountPenaltiesEntity accountPenalty = null;
        if (!penalty.isOneTime() && isPenaltyAlreadyApplied(penalty)) {
            accountPenalty = getAccountPenalty(penalty.getPenaltyId());
            accountPenalty.setPenaltyAmount(charge);
            accountPenalty.setPenaltyStatus(PenaltyStatus.ACTIVE);
            accountPenalty.setStatusChangeDate(getDateTimeService().getCurrentJavaDateTime());
        } else {
            accountPenalty = new AccountPenaltiesEntity(this, penalty, charge, PenaltyStatus.ACTIVE.getValue(),
                    null, null);
        }
        return accountPenalty;
    }

    /**
     * @deprecated interest deducted at disbursement not supported since version 1.1!!
     */
    @Deprecated
    public boolean isInterestDeductedAtDisbursement() {
        return LoanConstants.INTEREST_DEDUCTED_AT_DISBURSEMENT.equals(intrestAtDisbursement);
    }

    /**
     * @deprecated interest deducted at disbursement not supported since version 1.1!!
     */
    @Deprecated
    void setInterestDeductedAtDisbursement(final boolean interestDedAtDisb) {
        this.intrestAtDisbursement = interestDedAtDisb ? Constants.YES : Constants.NO;
    }

    public Money getLoanAmount() {
        return loanAmount;
    }

    void setLoanAmount(final Money loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Money getLoanBalance() {
        return loanBalance;
    }

    void setLoanBalance(final Money loanBalance) {
        this.loanBalance = loanBalance;
    }

    public MeetingBO getLoanMeeting() {
        return loanMeeting;
    }

    void setLoanMeeting(final MeetingBO loanMeeting) {
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

    void setNoOfInstallments(final Short noOfInstallments) {
        this.noOfInstallments = noOfInstallments;
    }

    public String getStateSelected() {
        return stateSelected;
    }

    public void setStateSelected(final String stateSelected) {
        this.stateSelected = stateSelected;
    }

    public LoanPerformanceHistoryEntity getPerformanceHistory() {
        return performanceHistory;
    }

    public List<LoanActivityEntity> getLoanActivityDetails() {
        return loanActivityDetails;
    }

    public List<AccountOverpaymentEntity> getAccountOverpayments() {
        return accountOverpayments;
    }

    public void addAccountOverpayment(final AccountOverpaymentEntity overpayment) {
        this.accountOverpayments.add(overpayment);
    }

    public void addLoanActivity(final LoanActivityEntity loanActivity) {
        this.loanActivityDetails.add(loanActivity);
    }

    public LoanArrearsAgingEntity getLoanArrearsAgingEntity() {
        return loanArrearsAgingEntity;
    }

    public void setLoanArrearsAgingEntity(final LoanArrearsAgingEntity loanArrearsAgingEntity) {
        this.loanArrearsAgingEntity = loanArrearsAgingEntity;
    }

    public Set<LoanBO> getMemberAccounts() {
        return memberAccounts;
    }

    public void addMemberAccount(LoanBO memberAccount) {
       // this.memberAccounts.add(memberAccount);
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
    protected void updateTotalFeeAmount(final Money totalFeeAmount) {
        LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
        loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees().subtract(totalFeeAmount));
    }

    @Override
    protected void updateTotalPenaltyAmount(final Money totalPenaltyAmount) {
        LoanSummaryEntity loanSummaryEntity = this.getLoanSummary();
        loanSummaryEntity.setOriginalPenalty(loanSummaryEntity.getOriginalPenalty().subtract(totalPenaltyAmount));
    }

    @Override
    public boolean isAdjustPossibleOnLastTrxn() {
        // adjustment is possible only if account state is
        // 1. active in good standing.
        // 2. active in bad standing.
        // 3. Closed - Obligation Met : Check permission first ; Can adjust
        // payment when account status is "closed-obligation met"

        if (!(getAccountState().isLoanActiveInGoodStanding()
                || getAccountState().isLoanActiveInBadStanding() || getAccountState().isLoanClosedObligationsMet())) {
            logger.debug(
                    "State is not active hence adjustment is not possible");
            return false;
        }

        logger.debug(
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
        logger.debug("Adjustment is not possible ");
        return false;
    }

    @Override
    protected void updateAccountActivity(final Money principal, final Money interest, final Money fee,
            final Money penalty, final Short personnelId, final String description) throws AccountException {
        try {
            PersonnelBO personnel = legacyPersonnelDao.getPersonnel(personnelId);
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

    public void waiveAmountDue(final WaiveEnum waiveType) throws AccountException {
        if (waiveType.equals(WaiveEnum.FEES)) {
            waiveFeeAmountDue();
        } else if (waiveType.equals(WaiveEnum.PENALTY)) {
            waivePenaltyAmountDue();
        }
    }

    @Override
    public void waiveAmountOverDue(final WaiveEnum waiveType) throws AccountException {
        if (waiveType.equals(WaiveEnum.FEES)) {
            waiveFeeAmountOverDue();
        } else if (waiveType.equals(WaiveEnum.PENALTY)) {
            waivePenaltyAmountOverDue();
        }
    }

    public Money getTotalPrincipalAmount() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> installments = getAllInstallments();
        for (AccountActionDateEntity accountActionDateEntity : installments) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipal());
        }
        return amount;
    }
    
    public Money getTotalPrincipalDue() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> installments = getAllInstallments();
        for (AccountActionDateEntity accountActionDateEntity : installments) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipalDue());
        }
        return amount;
    }

    public Money getTotalPrincipalAmountInArrears() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
            amount = amount.add(loanScheduleEntity.getPrincipal().subtract(loanScheduleEntity.getPrincipalPaid()));
        }
        return amount;
    }

    public Money getTotalPrincipalAmountInArrearsAndOutsideLateness() throws PersistenceException {
        Money amount = new Money(getCurrency());
        loanPrdPersistence = new LoanPrdPersistence();
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            if (accountActionDateEntity.isNotPaid()
                    && currentDate.getTime() - accountActionDateEntity.getActionDate().getTime() > loanPrdPersistence
                            .retrieveLatenessForPrd().intValue()
                            * 24 * 60 * 60 * 1000) {
                amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipalDue());
            }
        }
        return amount;
    }

    public Money getTotalInterestAmountInArrears() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
            amount = amount.add(loanScheduleEntity.getInterest().subtract(loanScheduleEntity.getInterestPaid()));
        }
        return amount;
    }
    
    public Money getTotalInterestToBePaid() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> actionDateList = getAllInstallments();
        for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
            LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
            amount = amount.add(loanScheduleEntity.getInterest());
        }
        return amount;
    }

    public Money getTotalInterestAmountInArrearsAndOutsideLateness() throws PersistenceException {
        Money amount = new Money(getCurrency());
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
     * Remove the fee from all unpaid current or future installments, and update the loan accordingly.
     */
    @Override
    public final void removeFeesAssociatedWithUpcomingAndAllKnownFutureInstallments(final Short feeId,
            final Short personnelId) throws AccountException {
        List<Short> installmentIds = getApplicableInstallmentIdsForRemoveFees();
        Money totalFeeAmount;
        if (installmentIds != null && installmentIds.size() != 0 && isFeeActive(feeId)) {

            FeeBO fee = getAccountFeesObject(feeId);
            if (havePaymentsBeenMade() && fee.doesFeeInvolveFractionalAmounts()) {
                throw new AccountException(AccountExceptionConstants.CANT_REMOVE_FEE_EXCEPTION);
            }

            if (fee.isTimeOfDisbursement()) {
                AccountFeesEntity accountFee = getAccountFees(feeId);
                totalFeeAmount = accountFee.getAccountFeeAmount();
                removeAccountFee(accountFee);
                this.delete(accountFee);
            } else {
                totalFeeAmount = updateAccountActionDateEntity(installmentIds, feeId);
                updateAccountFeesEntity(feeId);
            }

            updateTotalFeeAmount(totalFeeAmount);

            String description = fee.getFeeName() + " " + AccountConstants.FEES_REMOVED;
            updateAccountActivity(null, null, totalFeeAmount, null, personnelId, description);

            if (!havePaymentsBeenMade()) {

            	LoanScheduleRounderHelper loanScheduleRounderHelper = new DefaultLoanScheduleRounderHelper();
                LoanScheduleRounder loanScheduleInstallmentRounder = new DefaultLoanScheduleRounder(loanScheduleRounderHelper);

                List<LoanScheduleEntity> unroundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
				List<LoanScheduleEntity> allExistingLoanSchedules = new ArrayList<LoanScheduleEntity>();

                List<AccountActionDateEntity> installmentsToRound = getInstallmentsToRound();
                for (AccountActionDateEntity installment : installmentsToRound) {
                	unroundedLoanSchedules.add((LoanScheduleEntity)installment);
				}
				List<AccountActionDateEntity> allExistingInstallments= this.getAllInstallments();
				for (AccountActionDateEntity installment : allExistingInstallments) {
					allExistingLoanSchedules.add((LoanScheduleEntity)installment);
				}
				List<LoanScheduleEntity> roundedLoanSchedules = loanScheduleInstallmentRounder.round(this.gracePeriodType.asEnum(), this.gracePeriodDuration, this.loanAmount,
                		this.interestType.asEnum(), unroundedLoanSchedules, allExistingLoanSchedules);

//                applyRounding_v2();
            }

            try {
                ApplicationContextProvider.getBean(LegacyAccountDao.class).createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }

    }

    /**
     * Remove unpaid or partially paid fee from each installment whose id is in installmentIdList, and return the total
     * of all unpaid fees that were removed.
     */
    @Override
    public Money updateAccountActionDateEntity(final List<Short> intallmentIdList, final Short feeId) {
        Money totalFeeAmount = new Money(getCurrency());
        Set<AccountActionDateEntity> accountActionDateEntitySet = this.getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
            if (intallmentIdList.contains(accountActionDateEntity.getInstallmentId())) {
                totalFeeAmount = totalFeeAmount.add(((LoanScheduleEntity) accountActionDateEntity).removeFees(feeId));
            }
        }
        return totalFeeAmount;
    }
    
    public Money removePenaltyFromLoanScheduleEntity(final List<Short> intallmentIdList, final Short penaltyId) {
        Money totalPenaltyAmount = new Money(getCurrency());
        Set<AccountActionDateEntity> accountActionDateEntitySet = this.getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
            if (intallmentIdList.contains(accountActionDateEntity.getInstallmentId())) {
                totalPenaltyAmount = totalPenaltyAmount.add(((LoanScheduleEntity) accountActionDateEntity).removePenalties(penaltyId));
            }
        }
        return totalPenaltyAmount;
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

    /**
     * Applies any type of charge to this loan.
     * <p>
     * Action by type:
     * </p>
     * <ul>
     * <li>a miscellaneous fee or penalty -- apply it to the next due installment</li>
     * <li>a one-time rate or amount fee -- apply it to the next due installment (if a rate fee, the charge argument is
     * the rate, otherwise it is the amount)</li>
     * <li>a periodic rate or amount fee -- apply it to all due installments (if a rate fee, the charge argument is the
     * rate, otherwise it is the amount). If not yet applied, then add it to all due installments, otherwise update the
     * charge.</li>
     * </ul>
     * <p>
     * Note that "due installments" means any unpaid installments due today or in the future.
     * </p>
     */
    @Override
    public void applyCharge(final Short feeId, final Double charge) throws AccountException, PersistenceException {
        List<AccountActionDateEntity> dueInstallments = getTotalDueInstallments();
        if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
                || feeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
            if (dueInstallments.isEmpty()) {
                dueInstallments.add(getLastUnpaidInstallment());
            }
            applyMiscCharge(feeId, new Money(getCurrency(), String.valueOf(charge)), dueInstallments.get(0));

            // Don't re-apply rounding to already-rounded charges, since
            // it will have no effect
            if (!havePaymentsBeenMade()) {

            	List<LoanScheduleEntity> unroundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
				List<LoanScheduleEntity> allExistingLoanSchedules = new ArrayList<LoanScheduleEntity>();

				List<AccountActionDateEntity> installmentsToRound = getInstallmentsToRound();
                for (AccountActionDateEntity installment : installmentsToRound) {
                	unroundedLoanSchedules.add((LoanScheduleEntity)installment);
				}

                List<AccountActionDateEntity> allExistingInstallments= this.getAllInstallments();
				for (AccountActionDateEntity installment : allExistingInstallments) {
					allExistingLoanSchedules.add((LoanScheduleEntity)installment);
				}

            	applyRoundingOnInstallments(unroundedLoanSchedules, allExistingLoanSchedules);
            }
        } else {
            if (dueInstallments.isEmpty()) {
                throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
            }
            FeeBO fee = getFeeDao().findById(feeId);

            if (havePaymentsBeenMade()
                    && (fee.doesFeeInvolveFractionalAmounts() || !MoneyUtils.isRoundedAmount(charge))) {
                throw new AccountException(AccountExceptionConstants.CANT_APPLY_FEE_EXCEPTION);
            }

            if (fee.getFeeFrequency().getFeePayment() != null) {
                applyOneTimeFee(fee, charge, dueInstallments.get(0));
            } else {
                applyPeriodicFee(fee, charge, dueInstallments);
            }

            if (!havePaymentsBeenMade()) {
            	List<LoanScheduleEntity> unroundedLoanSchedules = new ArrayList<LoanScheduleEntity>();
				List<LoanScheduleEntity> allExistingLoanSchedules = new ArrayList<LoanScheduleEntity>();

				List<AccountActionDateEntity> installmentsToRound = getInstallmentsToRound();
                for (AccountActionDateEntity installment : installmentsToRound) {
                	unroundedLoanSchedules.add((LoanScheduleEntity)installment);
				}

                List<AccountActionDateEntity> allExistingInstallments= this.getAllInstallments();
				for (AccountActionDateEntity installment : allExistingInstallments) {
					allExistingLoanSchedules.add((LoanScheduleEntity)installment);
				}

            	applyRoundingOnInstallments(unroundedLoanSchedules, allExistingLoanSchedules);
            }
        }
    }

    private void applyRoundingOnInstallments(List<LoanScheduleEntity> unroundedLoanSchedules, List<LoanScheduleEntity> allExistingLoanSchedules) {

    	LoanScheduleRounderHelper loanScheduleRounderHelper = new DefaultLoanScheduleRounderHelper();
        LoanScheduleRounder loanScheduleInstallmentRounder = new DefaultLoanScheduleRounder(loanScheduleRounderHelper);
		loanScheduleInstallmentRounder.round(this.gracePeriodType.asEnum(), this.gracePeriodDuration, this.loanAmount,
        		this.interestType.asEnum(), unroundedLoanSchedules, allExistingLoanSchedules);

	}

	private AccountActionDateEntity getLastUnpaidInstallment() throws AccountException {
        Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();
        List<AccountActionDateEntity> objectList = Arrays.asList(accountActionDateSet
                .toArray(new AccountActionDateEntity[accountActionDateSet.size()]));
        for (int i = objectList.size() - 1; i >= 0; i--) {
            AccountActionDateEntity accountActionDateEntity = objectList.get(i);
            if (accountActionDateEntity.isNotPaid()) {
                return accountActionDateEntity;
            }
        }
        throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
    }

    /**
     * It calculates over due amounts till installment 1 less than the one passed,because whatever amount is associated
     * with the current installment it is the due amount and not the over due amount. It calculates that by iterating
     * over the accountActionDates associated and summing up all the principal and principalPaid till installment-1 and
     * then returning the difference of the two.It also takes into consideration any miscellaneous fee or miscellaneous
     * penalty.
     *
     * @param installmentId
     *            - Installment id till which we want over due amounts.
     *
     */
    public OverDueAmounts getOverDueAmntsUptoInstallment(final Short installmentId) {
        Set<AccountActionDateEntity> accountActionDateEntities = getAccountActionDates();
        OverDueAmounts totalOverDueAmounts = new OverDueAmounts();
        if (null != accountActionDateEntities && accountActionDateEntities.size() > 0) {
            for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntities) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
                if (loanScheduleEntity.getInstallmentId() < installmentId) {
                    totalOverDueAmounts.add(loanScheduleEntity.getDueAmnts());
                }
            }
        }
        return totalOverDueAmounts;
    }

    /**
     * @throws PersistenceException
     * @deprecated use {@link LoanBO#disburseLoan(AccountPaymentEntity)}
     */
    @Deprecated
    public void disburseLoan(final String receiptNum, final Date transactionDate, final Short paymentTypeId,
            final PersonnelBO personnel, final Date receiptDate, final Short rcvdPaymentTypeId)
            throws AccountException, PersistenceException {
        disburseLoan(receiptNum, transactionDate, paymentTypeId, personnel, receiptDate, rcvdPaymentTypeId, true);
    }

    /**
     * @throws PersistenceException
     * @deprecated use {@link LoanBO#disburseLoan(AccountPaymentEntity)}
     */
    @Deprecated
    public void disburseLoan(final PersonnelBO personnel, final Short rcvdPaymentTypeId, final boolean persistChange)
            throws AccountException, PersistenceException {
        disburseLoan(null, getDisbursementDate(), rcvdPaymentTypeId, personnel, null, rcvdPaymentTypeId, persistChange);
    }

    private void disburseLoan(final String receiptNum, final Date transactionDate, final Short paymentTypeId,
            final PersonnelBO loggedInUser, final Date receiptDate, final Short rcvdPaymentTypeId,
            final boolean persistChange) throws AccountException, PersistenceException {

        if ((this.getState().compareTo(AccountState.LOAN_APPROVED) != 0)
                && (this.getState().compareTo(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER) != 0)) {
            throw new AccountException("Loan not in a State to be Disbursed: " + this.getState().toString());
        }

        if (this.getCustomer().isDisbursalPreventedDueToAnyExistingActiveLoansForTheSameProduct(this.getLoanOffering())) {
            throw new AccountException("errors.cannotDisburseLoan.because.otherLoansAreActive");
        }

        try {
            new ProductMixValidator().checkIfProductsOfferingCanCoexist(this);
        } catch (ServiceException e1) {
            throw new AccountException(e1.getMessage());
        }

        addLoanActivity(buildLoanActivity(this.loanAmount, loggedInUser, AccountConstants.LOAN_DISBURSAL,
                transactionDate));

        // if the trxn date is not equal to disbursementDate we need to
        // regenerate the installments
        if (!isFixedRepaymentSchedule() && !DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime()).equals(
                DateUtils.getDateWithoutTimeStamp(transactionDate.getTime()))) {
            final boolean lsimEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
            if (lsimEnabled) {
                // QUESTION: does minDays
                final int minDaysInterval = new ConfigurationPersistence().getConfigurationValueInteger(
                        MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY);
                this.disbursementDate = new DateTime(transactionDate).plusDays(minDaysInterval-1).toDate();
            }
            else {
                this.disbursementDate = transactionDate;
            }
            regeneratePaymentSchedule(lsimEnabled, null);
        }
        this.disbursementDate = transactionDate;

        final AccountStateEntity newState = new AccountStateEntity(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
        this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(this.getAccountState(), newState,
                loggedInUser, this));
        this.setAccountState(newState);

        //
        // Client performance entry
        updateCustomerHistoryOnDisbursement(this.loanAmount);
        if (getPerformanceHistory() != null) {
            getPerformanceHistory().setLoanMaturityDate(getLastInstallmentAccountAction().getActionDate());
        }

        //
        //
        // build up account payment related data
        AccountPaymentEntity accountPayment = null;
        if (this.isInterestDeductedAtDisbursement()) {
            // the 1st payment is made and creates an initial accountPaymentEntity.
            // This disbursal process carries on with that accountPaymentEntity by updating the 'amount' to the actual
            // disbursed amount.
            accountPayment = payInterestAtDisbursement(receiptNum, transactionDate, rcvdPaymentTypeId, loggedInUser,
                    receiptDate);
            accountPayment.setAmount(this.loanAmount.subtract(accountPayment.getAmount()));
        } else {
            // Disbursal process has to create its own accountPayment taking into account any disbursement fees
            Money feeAmountAtDisbursement = getFeesDueAtDisbursement();
            accountPayment = new AccountPaymentEntity(this, this.loanAmount.subtract(feeAmountAtDisbursement),
                    receiptNum, receiptDate, getPaymentTypeEntity(paymentTypeId), transactionDate);
            accountPayment.setCreatedByUser(loggedInUser);

            if (feeAmountAtDisbursement.isGreaterThanZero()) {
                processFeesAtDisbursement(accountPayment, feeAmountAtDisbursement);
            }
        }

        // create trxn entry for disbursal
        final LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPayment,
                AccountActionTypes.DISBURSAL, Short.valueOf("0"), transactionDate, loggedInUser, transactionDate,
                this.loanAmount, "-", null, this.loanAmount, new Money(getCurrency()), new Money(getCurrency()),
                new Money(getCurrency()), new Money(getCurrency()), null, null);

        accountPayment.addAccountTrxn(loanTrxnDetailEntity);
        this.addAccountPayment(accountPayment);
        this.buildFinancialEntries(accountPayment.getAccountTrxns());

        if (persistChange) {
            try {
                ApplicationContextProvider.getBean(LegacyAccountDao.class).createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    public void disburseLoan(final AccountPaymentEntity disbursalPayment) throws AccountException, PersistenceException {

        if (this.getLoanAmount().getAmount().compareTo(disbursalPayment.getAmount().getAmount()) != 0) {
            throw new AccountException("Loan Amount to be Disbursed Held on Database : "
                    + this.getLoanAmount().getAmount() + " does not match the Input Loan Amount to be Disbursed: "
                    + disbursalPayment.getAmount().getAmount());
        }

        disburseLoan(disbursalPayment.getReceiptNumber(), disbursalPayment.getPaymentDate(), disbursalPayment
                .getPaymentType().getId(), disbursalPayment.getCreatedByUser(), disbursalPayment.getReceiptDate(),
                disbursalPayment.getPaymentType().getId(), false);
    }

    public Money getEarlyRepayAmount() {
        return nextInstallmentAndArrears().add(principleOfFutureInstallments());
    }

    public Money waiverAmount() {
        LoanScheduleEntity nextInstallment = (LoanScheduleEntity) getDetailsOfNextInstallment();
        if (nextInstallment == null || nextInstallment.isPaid()) {
            return Money.zero(getCurrency());
        }
        return nextInstallment.getInterestDue();
    }

    private Money principleOfFutureInstallments() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> futureInstallments = getApplicableIdsForFutureInstallments();
        for (AccountActionDateEntity futureInstallment : futureInstallments) {
            amount = amount.add(((LoanScheduleEntity) futureInstallment).getPrincipalDue());
        }
        return amount;
    }

    private Money nextInstallmentAndArrears() {
        Money amount = new Money(getCurrency());
        List<AccountActionDateEntity> dueInstallments = getApplicableIdsForNextInstallmentAndArrears();
        for (AccountActionDateEntity dueInstallment : dueInstallments) {
            amount = amount.add(((LoanScheduleEntity) dueInstallment).getTotalDueWithFees());
        }
        return amount;
    }

    public void makeEarlyRepayment(final Money totalAmount, final Date transactionDate, final String receiptNumber,
            final Date receiptDate, final String paymentTypeId, final Short personnelId, boolean waiveInterest,
            Money interestDue) throws AccountException {

        makeEarlyRepayment(totalAmount, transactionDate, receiptNumber, receiptDate, paymentTypeId, personnelId,
                waiveInterest, interestDue, null);

    }
    
    public void makeEarlyRepayment(final Money totalAmount, final Date transactionDate,
                                   final String receiptNumber, final Date receiptDate,
                                   final String paymentTypeId, final Short personnelId,
                                   boolean waiveInterest, Money interestDue, Integer savingsPaymentId) throws AccountException {
        try {
            PersonnelBO currentUser = legacyPersonnelDao.getPersonnel(personnelId);
            this.setUpdatedBy(personnelId);
            this.setUpdatedDate(transactionDate);
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, totalAmount, receiptNumber,
                    receiptDate, getPaymentTypeEntity(Short.valueOf(paymentTypeId)), transactionDate);
            if (savingsPaymentId != null) {
                AccountPaymentEntity withdrawal = legacyAccountDao.findPaymentById(savingsPaymentId);
                accountPaymentEntity.setOtherTransferPayment(withdrawal);
            }
            addAccountPayment(accountPaymentEntity);

            makeEarlyRepaymentForArrears(accountPaymentEntity, AccountConstants.PAYMENT_RCVD,
                    AccountActionTypes.LOAN_REPAYMENT, currentUser);
            makeEarlyRepaymentForNextInstallment(currentUser, accountPaymentEntity, waiveInterest, interestDue);
            makeEarlyRepaymentForFutureInstallments(accountPaymentEntity, AccountConstants.PAYMENT_RCVD, AccountActionTypes.LOAN_REPAYMENT, currentUser);

            if (getPerformanceHistory() != null) {
                getPerformanceHistory().setNoOfPayments(getPerformanceHistory().getNoOfPayments() + 1);
            }
            LoanActivityEntity loanActivity = buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
                    AccountConstants.LOAN_REPAYMENT, transactionDate);
            addLoanActivity(loanActivity);
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());

            AccountStateEntity newAccountState = legacyMasterDao.getPersistentObject(
                    AccountStateEntity.class, AccountStates.LOANACC_OBLIGATIONSMET);
            addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(getAccountState(), newAccountState,
                    legacyPersonnelDao.getPersonnel(personnelId), this));
            setAccountState(legacyMasterDao.getPersistentObject(AccountStateEntity.class,
                    AccountStates.LOANACC_OBLIGATIONSMET));
            setClosedDate(transactionDate);

            // Client performance entry
            updateCustomerHistoryOnRepayment();
            this.delete(loanArrearsAgingEntity);
            loanArrearsAgingEntity = null;
            getlegacyLoanDao().createOrUpdate(this);

            // GLIM
            if (hasMemberAccounts()) {
                for (LoanBO memberAccount : this.memberAccounts) {
                    BigDecimal fraction = memberAccount.calcFactorOfEntireLoan();
                    memberAccount.makeEarlyRepayment(totalAmount.divide(fraction), transactionDate, receiptNumber, receiptDate,
                            paymentTypeId, personnelId, waiveInterest, interestDue, null);
                }
            }
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void makeEarlyRepaymentForNextInstallment(PersonnelBO currentUser, AccountPaymentEntity accountPaymentEntity,
                                                      boolean waiveInterest, Money interestDue) {
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null && nextInstallment.isNotPaid()) {
            if(waiveInterest){
                repayInstallmentWithInterestWaiver(nextInstallment,accountPaymentEntity, AccountConstants.PAYMENT_RCVD,
                        AccountActionTypes.LOAN_REPAYMENT, currentUser);
            }else{
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) nextInstallment;
                Money originalInterestDue = loanScheduleEntity.getInterestDue();
                repayInstallment(loanScheduleEntity, accountPaymentEntity, AccountActionTypes.LOAN_REPAYMENT, currentUser,
                        AccountConstants.PAYMENT_RCVD, interestDue);
                if (isDecliningBalanceInterestRecalculation()) {
                    loanSummary.decreaseBy(null, originalInterestDue.subtract(interestDue), null, null);
                }
            }
        }
    }

    public void handleArrears() throws AccountException {
        AccountStateEntity stateEntity;
        try {
            stateEntity = legacyMasterDao.getPersistentObject(AccountStateEntity.class,
                    AccountStates.LOANACC_BADSTANDING);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(this.getAccountState(),
                stateEntity, this.getPersonnel(), this);
        this.addAccountStatusChangeHistory(historyEntity);
        this.setAccountState(stateEntity);

        try {
            String systemDate = DateUtils.getCurrentDate();
            Date currrentDate = DateUtils.getLocaleDate(systemDate);
            this.setUpdatedDate(currrentDate);
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        }

        try {
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public boolean isLastInstallment(final Short installmentId) {
        Set<AccountActionDateEntity> accountActionDateSet = getAccountActionDates();
        List<Object> objectList = Arrays.asList(accountActionDateSet.toArray());
        AccountActionDateEntity accountActionDateEntity = (AccountActionDateEntity) objectList
                .get(objectList.size() - 1);
        if (installmentId.equals(accountActionDateEntity.getInstallmentId())) {
            return true;
        }
        return false;
    }

    @Override
    protected void writeOff(Date transactionDate) throws AccountException {
        try {
            if (!isTrxnDateValid(transactionDate,
                    new CustomerPersistence().getLastMeetingDateForCustomer(getCustomer().getCustomerId()),
                    new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled())) {
                throw new BusinessRuleException("errors.invalidTxndate");
            }
            Short personnelId = this.getUserContext().getId();
            PersonnelBO currentUser = legacyPersonnelDao.getPersonnel(personnelId);
            this.setUpdatedBy(personnelId);
            this.setUpdatedDate(transactionDate);
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, getEarlyClosureAmount(), null,
                    null, getPaymentTypeEntity(Short.valueOf("1")), transactionDate);
            this.addAccountPayment(accountPaymentEntity);

            makeEarlyRepaymentForArrears(accountPaymentEntity, AccountConstants.LOAN_WRITTEN_OFF,
                    AccountActionTypes.WRITEOFF, currentUser);
            //for past arrears installments writeOff and reschedule are the same as 'make early repayment'
            //but differ in processing for future installments
            makeWriteOffOrReschedulePaymentForFutureInstallments(accountPaymentEntity, AccountConstants.LOAN_WRITTEN_OFF,
                    AccountActionTypes.WRITEOFF, currentUser);
            addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
                    AccountConstants.LOAN_WRITTEN_OFF, transactionDate));
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
            // Client performance entry
            updateCustomerHistoryOnWriteOff();
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    @Override
    protected void reschedule(Date transactionDate) throws AccountException {
        try {
            if (!isTrxnDateValid(transactionDate,
                    new CustomerPersistence().getLastMeetingDateForCustomer(getCustomer().getCustomerId()),
                    new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled())) {
                throw new BusinessRuleException("errors.invalidTxndate");
            }
            Short personnelId = this.getUserContext().getId();
            PersonnelBO currentUser = legacyPersonnelDao.getPersonnel(personnelId);
            this.setUpdatedBy(personnelId);
            this.setUpdatedDate(transactionDate);
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, getEarlyClosureAmount(), null,
                    null, getPaymentTypeEntity(Short.valueOf("1")), transactionDate);
            this.addAccountPayment(accountPaymentEntity);
            makeEarlyRepaymentForArrears(accountPaymentEntity, AccountConstants.LOAN_RESCHEDULED,
                    AccountActionTypes.LOAN_RESCHEDULED, currentUser);
            //for past arrears installments writeOff and reschedule are the same as 'make early repayment'
            //but differ in processing for future installments
            makeWriteOffOrReschedulePaymentForFutureInstallments(accountPaymentEntity, AccountConstants.LOAN_RESCHEDULED,
                    AccountActionTypes.LOAN_RESCHEDULED, currentUser);
            addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
                    AccountConstants.LOAN_RESCHEDULED, transactionDate));
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
            // Client performance entry using the same as write off.
            updateCustomerHistoryOnWriteOff();
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }


    @Override
    public AccountPaymentEntity getLastPmntToBeAdjusted() {
        AccountPaymentEntity accntPmnt = null;
        /*MIFOS-5694: this is just workaround for more complex issue.
         *This condition should be removed when MIFOS-5692 is fixed.  
         */
        if (this.parentAccount != null) {
            accntPmnt = super.getLastPmntToBeAdjusted();
        } else {
            // MIFOS-4238: we don't want to show disbursal amount as an adjustment amount
            int i = 0;
            for (AccountPaymentEntity accntPayment : accountPayments) {
                i = i + 1;
                if (i == accountPayments.size()) {
                    break;
                }
                if (accntPayment.getAmount().isNonZero()) {
                    accntPmnt = accntPayment;
                    break;
                }

            }
        }
        return accntPmnt;
    }

    private void waiveFeeAmountDue() throws AccountException {
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForNextInstallmentAndArrears();
        LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
                .get(accountActionDateList.size() - 1);
        Money chargeWaived = accountActionDateEntity.waiveFeeCharges();
        Money principal = new Money(getCurrency());
        Money interest = new Money(getCurrency());
        Money penalty = new Money(getCurrency());
        if (chargeWaived != null && chargeWaived.isGreaterThanZero()) {
            updateTotalFeeAmount(chargeWaived);
            updateAccountActivity(principal, interest, chargeWaived, penalty, userContext.getId(),
                    LoanConstants.FEE_WAIVED);
        }
        try {
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waivePenaltyAmountDue() throws AccountException {
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForNextInstallmentAndArrears();
        LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountActionDateList
                .get(accountActionDateList.size() - 1);
        Money principal = new Money(getCurrency());
        Money interest = new Money(getCurrency());
        Money fee = new Money(getCurrency());
        Money chargeWaived = accountActionDateEntity.waivePenaltyCharges();
        if (chargeWaived != null && chargeWaived.isGreaterThanZero()) {
            updateTotalPenaltyAmount(chargeWaived);
            updateAccountActivity(principal, interest, fee, chargeWaived, userContext.getId(),
                    LoanConstants.PENALTY_WAIVED);
        }
        try {
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waiveFeeAmountOverDue() throws AccountException {
        Money chargeWaived = new Money(getCurrency());
        Money principal = new Money(getCurrency());
        Money interest = new Money(getCurrency());
        Money penalty = new Money(getCurrency());
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForNextInstallmentAndArrears();

        // Remove last installment only if there is a next installment exists
        // Fix for http://mifosforge.jira.com/browse/MIFOS-2397
        // FIXME There should be a cleaner way to separate next installment and past
        // installment.
        if (getDetailsOfNextInstallment() != null) {
            accountActionDateList.remove(accountActionDateList.size() - 1);
        }
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            chargeWaived = chargeWaived.add(((LoanScheduleEntity) accountActionDateEntity).waiveFeeCharges());
        }
        if (chargeWaived != null && chargeWaived.isGreaterThanZero()) {
            updateTotalFeeAmount(chargeWaived);
            updateAccountActivity(principal, interest, chargeWaived, penalty, userContext.getId(),
                    AccountConstants.AMOUNT + chargeWaived + AccountConstants.WAIVED);
        }
        try {
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void waivePenaltyAmountOverDue() throws AccountException {
        Money chargeWaived = new Money(getCurrency());
        Money principal = new Money(getCurrency());
        Money interest = new Money(getCurrency());
        Money fee = new Money(getCurrency());
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForNextInstallmentAndArrears();
        // Remove last installment only if a next installment exists
        // Fix for http://mifosforge.jira.com/browse/MIFOS-2826
        if (getDetailsOfNextInstallment() != null) {
            accountActionDateList.remove(accountActionDateList.size() - 1);
        }
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            chargeWaived = chargeWaived.add(((LoanScheduleEntity) accountActionDateEntity).waivePenaltyCharges());
        }
        if (chargeWaived != null && chargeWaived.isGreaterThanZero()) {
            updateTotalPenaltyAmount(chargeWaived);
            updateAccountActivity(principal, interest, fee, chargeWaived, userContext.getId(), AccountConstants.AMOUNT
                    + chargeWaived + AccountConstants.WAIVED);
        }
        try {
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public Money getAmountTobePaidAtdisburtail() {
        if (this.isInterestDeductedAtDisbursement()) {
            return getDueAmount(getAccountActionDate(Short.valueOf("1")));
        }

        return getlegacyLoanDao().getFeeAmountAtDisbursement(this.getAccountId(), getCurrency());
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

    /**
     * use service/dao for saving and creating loans
     */
    @Deprecated
    public void save() throws AccountException {
        try {
            this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(this.getAccountState(), this
                    .getAccountState(), legacyPersonnelDao.getPersonnel(userContext.getId()), this));
            getlegacyLoanDao().createOrUpdate(this);
            this.globalAccountNum = generateId(userContext.getBranchGlobalNum());
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION, e);
        }
    }

    public void updateLoan(final Boolean interestDeductedAtDisbursement, final Money loanAmount,
            final Double interestRate, final Short noOfInstallments, final Date disbursementDate,
            final Short gracePeriodDuration, final Integer businessActivityId, final String collateralNote,
            final Integer collateralTypeId, final List<CustomFieldDto> customFields,
            final boolean isRepaymentIndepOfMeetingEnabled, final MeetingBO newMeetingForRepaymentDay, final FundBO fund)
            throws AccountException {
        if (interestDeductedAtDisbursement) {
            try {
                if (noOfInstallments <= 1) {
                    throw new AccountException(LoanExceptionConstants.INVALIDNOOFINSTALLMENTS);
                }
                setGracePeriodType(legacyMasterDao.findMasterDataEntityWithLocale(GracePeriodTypeEntity.class, GraceType.NONE
                        .getValue()));
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        } else {
            setGracePeriodType(getLoanOffering().getGracePeriodType());
        }
        setLoanAmount(loanAmount);
        setInterestRate(interestRate);
        setNoOfInstallments(noOfInstallments);
        setGracePeriodDuration(gracePeriodDuration);
        setInterestDeductedAtDisbursement(interestDeductedAtDisbursement);
        setBusinessActivityId(businessActivityId);
        setCollateralNote(collateralNote);
        setCollateralTypeId(collateralTypeId);
        setFund(fund);
        if (getAccountState().getId().equals(AccountState.LOAN_APPROVED.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_PARTIAL_APPLICATION.getValue())
                || getAccountState().getId().equals(AccountState.LOAN_PENDING_APPROVAL.getValue())) {
            // only check the disbursement date if it has changed
            if (disbursementDate != null && !disbursementDate.equals(getDisbursementDate())
                    && isDisbursementDateLessThanCurrentDate(disbursementDate)) {
                throw new AccountException(LoanExceptionConstants.ERROR_INVALIDDISBURSEMENTDATE);
            }
            setDisbursementDate(disbursementDate);

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

    public void updateLoan(final Date disbursementDate, final Short noOfInstallments, final Money loanAmount, final Integer businessActivityId) throws AccountException {
        setLoanAmount(loanAmount);
        setNoOfInstallments(noOfInstallments);
        setDisbursementDate(disbursementDate);
        setBusinessActivityId(businessActivityId);
        MeetingBO meetingBO = ( isIndividualLoan() ? this.getParentAccount().getLoanMeeting() : this.getLoanMeeting());
        boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        regeneratePaymentSchedule(isRepaymentIndepOfMeetingEnabled, meetingBO);
        
        update();
    }

    public Short getDaysInArrears() {
        return getDaysInArrears(false);
    }

    public Short getDaysInArrears(boolean accountReOpened) {
        Short daysInArrears = 0;
        if (isAccountActive() || accountReOpened) {
            if (!getDetailsOfInstallmentsInArrears().isEmpty()) {
                AccountActionDateEntity accountActionDateEntity = getDetailsOfInstallmentsInArrears().get(0);
                daysInArrears = Short.valueOf(Long.valueOf(
                        calculateDays(accountActionDateEntity.getActionDate(), DateUtils
                                .getCurrentDateWithoutTimeStamp())).toString());
            }
        }
        return daysInArrears;
    }

    public final void reverseLoanDisbursal(final PersonnelBO loggedInUser, final String note) throws AccountException {
        changeStatus(AccountState.LOAN_CANCELLED, AccountStateFlag.LOAN_REVERSAL.getValue(), note, loggedInUser);
        if (getAccountPayments() != null && getAccountPayments().size() > 0) {
            for (AccountPaymentEntity accountPayment : getAccountPayments()) {
                if (accountPayment.getAmount().isGreaterThanZero()) {
                    adjustPayment(accountPayment, loggedInUser, note);
                }
            }
        }
        addLoanActivity(buildLoanActivity(loanAmount, loggedInUser, "Disbursal Adjusted", DateUtils.getCurrentDateWithoutTimeStamp()));
        updateCustomerHistoryOnReverseLoan();
    }

    protected void updatePerformanceHistoryOnAdjustment(final int numberOfTransactions) {
        if (getPerformanceHistory() != null) {
            getPerformanceHistory().setNoOfPayments(getPerformanceHistory().getNoOfPayments() - numberOfTransactions);
        }
    }
    
	public void recordOverpayment(Money balance, LocalDate paymentDate, PersonnelBO user, String receiptId, LocalDate receiptDate,
	        Short modeOfPayment) throws AccountException {
		
		if (balance.isGreaterThanZero()) {
			
			Date transactionDate = paymentDate.toDateMidnight().toDate();
			
			PaymentData paymentData = new PaymentData(balance, user, modeOfPayment, transactionDate);
			if (receiptId != null) {
			    paymentData.setReceiptNum(receiptId);
			}
			if (receiptDate != null) {
			    paymentData.setReceiptDate(receiptDate.toDateMidnight().toDate());
			}
			
			AccountPaymentEntity accountPaymentEntity = prePayment(paymentData);

			// update
    		
			Money overpayment = balance;
			
			List<AccountActionDateEntity> paidInstallments = getDetailsOfPaidInstallmentsOn(paymentDate);
			
			if (!paidInstallments.isEmpty()) {
				LoanScheduleEntity lastFullyPaidInstallment = (LoanScheduleEntity) paidInstallments.get(paidInstallments.size()-1);
				lastFullyPaidInstallment.updatePrincipalPaidby(accountPaymentEntity, user);
				
				LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity, lastFullyPaidInstallment, user, transactionDate,
		                AccountActionTypes.LOAN_REPAYMENT, AccountConstants.PAYMENT_RCVD, legacyLoanDao);
				accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
				
				PaymentAllocation paymentAllocation = new PaymentAllocation(overpayment.getCurrency());
				paymentAllocation.allocateForPrincipal(overpayment);
				
				this.loanSummary.updatePaymentDetails(paymentAllocation);
			}
			
			LoanPaymentTypes loanPaymentType = getLoanPaymentType(paymentData.getTotalAmount());
			postPayment(paymentData, accountPaymentEntity, loanPaymentType);
			
			addAccountPayment(accountPaymentEntity);
	        buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		}
	}
	
	public Money applyNewPaymentMechanism(LocalDate paymentDate, BigDecimal repaymentAmount, PersonnelBO user, 
	        String receiptId, LocalDate receiptDate, Short modeOfPayment) throws AccountException {
		
		Money totalAmount = new Money(getCurrency(), repaymentAmount);
		Date transactionDate = paymentDate.toDateMidnight().toDate();

		PaymentData paymentData = new PaymentData(totalAmount, user, modeOfPayment, transactionDate);
		if (receiptId != null) {
		    paymentData.setReceiptNum(receiptId);
		}
		if (receiptDate != null) {
		    paymentData.setReceiptDate(receiptDate.toDateMidnight().toDate());
		}
		
		AccountPaymentEntity accountPaymentEntity = prePayment(paymentData);

		Money balance = totalAmount;
		
		LoanPaymentTypes loanPaymentType = getLoanPaymentType(paymentData.getTotalAmount());
		
        // 1. pay off installments in arrears
		List<AccountActionDateEntity> inArrears = getDetailsOfInstallmentsInArrearsOn(paymentDate);
		for (AccountActionDateEntity accountActionDate : inArrears) {
            balance = ((LoanScheduleEntity) accountActionDate).applyPayment(accountPaymentEntity, balance, user, transactionDate);
        }

        // 2. pay off due installment (normal way)
		if (balance.isGreaterThanZero()) {
			AccountActionDateEntity upcomingInstallment = getDetailsOfNextInstallmentOn(paymentDate);
			balance = ((LoanScheduleEntity) upcomingInstallment).applyPayment(accountPaymentEntity, balance, user, transactionDate);
		}

		if (!accountPaymentEntity.getAccountTrxns().isEmpty()) {
		    postPayment(paymentData, accountPaymentEntity, loanPaymentType);

		    addAccountPayment(accountPaymentEntity);
		    buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
		}
		
        return balance;
	}

    /*
     * PaymentData is the payment information entered in the UI An AccountPaymentEntity is created from the PaymentData
     * passed in.
     */
    @Override
    protected AccountPaymentEntity makePayment(final PaymentData paymentData) throws AccountException {
        AccountPaymentEntity accountPaymentEntity = prePayment(paymentData);
        LoanPaymentTypes loanPaymentType = getLoanPaymentType(paymentData.getTotalAmount());
        ApplicationContextProvider.getBean(LoanBusinessService.class).applyPayment(paymentData, this, accountPaymentEntity);
        postPayment(paymentData, accountPaymentEntity, loanPaymentType);
        if (paymentData.getOverpaymentAmount() != null) {
            AccountOverpaymentEntity overpaymentEntity = new AccountOverpaymentEntity(this, accountPaymentEntity,
                    paymentData.getOverpaymentAmount(), OverpaymentStatus.UNCLEARED.getValue());
            addAccountOverpayment(overpaymentEntity);
        }
        
        // GLIM
        BigDecimal installmentsPaid = findNumberOfPaidInstallments();
        applyPaymentToMemberAccounts(paymentData, installmentsPaid);
        
        return accountPaymentEntity;
    }

    private void postPayment(PaymentData paymentData, AccountPaymentEntity accountPaymentEntity, LoanPaymentTypes loanPaymentType) throws AccountException {
        closeLoanIfRequired(paymentData);
        updateLoanStatus(paymentData, loanPaymentType);
        handleLoanArrearsAging(loanPaymentType);
        AccountPaymentEntity otherTransferPayment = paymentData.getOtherTransferPayment();
        if (otherTransferPayment != null) {
        	otherTransferPayment.setOtherTransferPayment(accountPaymentEntity);          
        }
        addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), paymentData.getPersonnel(),
                AccountConstants.PAYMENT_RCVD, paymentData.getTransactionDate()));
    }

    private void closeLoanIfRequired(PaymentData paymentData) throws AccountException {
        if (getLastInstallmentAccountAction().isPaid()) {
            closeLoan(paymentData);
        }
    }

    private AccountPaymentEntity prePayment(PaymentData paymentData) throws AccountException {
        validationForMakePayment(paymentData);
        return getAccountPaymentEntity(paymentData);
    }

    private void handleLoanArrearsAging(LoanPaymentTypes loanPaymentTypes) throws AccountException {
        if (isLoanInBadStanding() && loanPaymentTypes.equals(LoanPaymentTypes.PARTIAL_PAYMENT)) {
            handleArrearsAging();
        }
    }

    private void updateLoanStatus(PaymentData paymentData, LoanPaymentTypes loanPaymentTypes) throws AccountException {
        if (isLoanInBadStanding() && loanPaymentTypes.isFullOrFuturePayment()) {
            changeLoanToGoodStanding(paymentData);
        }
    }

    private void changeLoanToGoodStanding(PaymentData paymentData) throws AccountException {
        changeLoanStatus(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, paymentData.getPersonnel());
        // Client performance entry
        updateCustomerHistoryOnPayment();
        this.delete(loanArrearsAgingEntity);
        loanArrearsAgingEntity = null;
    }

    private void closeLoan(PaymentData paymentData) throws AccountException {
        changeLoanStatus(AccountState.LOAN_CLOSED_OBLIGATIONS_MET, paymentData.getPersonnel());
        this.setClosedDate(new DateTimeService().getCurrentJavaDateTime());
        // Client performance entry
        updateCustomerHistoryOnLastInstlPayment(paymentData.getTotalAmount());
        this.delete(loanArrearsAgingEntity);
        loanArrearsAgingEntity = null;
    }

    private boolean isLoanInBadStanding() {
        return getState().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING);
    }

    private AccountPaymentEntity getAccountPaymentEntity(PaymentData paymentData) {
        final AccountPaymentEntity accountPayment = new AccountPaymentEntity(this, paymentData.getTotalAmount(),
                paymentData.getReceiptNum(), paymentData.getReceiptDate(), getPaymentTypeEntity(paymentData
                        .getPaymentTypeId()), paymentData.getTransactionDate());
        accountPayment.setCreatedByUser(paymentData.getPersonnel());
        accountPayment.setComment(paymentData.getComment());

        //for savings transfers
        AccountPaymentEntity otherTransferPayment = paymentData.getOtherTransferPayment();
        if (otherTransferPayment != null) {
            accountPayment.setOtherTransferPayment(otherTransferPayment);
        }

        return accountPayment;
    }

    private void validationForMakePayment(PaymentData paymentData) throws AccountException {
        validateForLoanStatus();
        validateForTotalAmount(paymentData);
    }

    private void validateForTotalAmount(PaymentData paymentData) throws AccountException {
        if (!paymentAmountIsValid(paymentData.getTotalAmount(), Collections.<AccountPaymentParametersDto.PaymentOptions>emptySet())) {
            throw new AccountException("errors.makePayment", new String[] { getGlobalAccountNum() });
        }
    }

    private void validateForLoanStatus() throws AccountException {
        if ((this.getState().compareTo(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING) != 0)
                && (this.getState().compareTo(AccountState.LOAN_ACTIVE_IN_BAD_STANDING) != 0)) {
            throw new AccountException("Loan not in a State for a Repayment to be made: " + this.getState().toString());
        }
    }

    private void delete(final AbstractEntity objectoDelete) throws AccountException {

        if (objectoDelete != null) {
            try {
                getlegacyLoanDao().delete(objectoDelete);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    @Override
    protected Money getDueAmount(final AccountActionDateEntity installment) {
        return ((LoanScheduleEntity) installment).getTotalDueWithFees();
    }

    private boolean isInstallmentPaid(final Short installmentId, final List<AccountActionDateEntity> allInstallments) {
        for (AccountActionDateEntity accountActionDate : allInstallments) {
            if (accountActionDate.getInstallmentId().equals(installmentId)) {
                return accountActionDate.isPaid();
            }
        }
        return false;
    }

    @Override
    protected void updateInstallmentAfterAdjustment(final List<AccountTrxnEntity> reversedTrxns, PersonnelBO loggedInUser)
            throws AccountException {

        Money increaseInterest = new Money(this.getCurrency());
        Money increaseFees = new Money(this.getCurrency());
        Money increasePenalty = new Money(this.getCurrency());

        int numberOfFullPayments = 0;
        short numberOfInstalments = (short)reversedTrxns.size();
        List<AccountActionDateEntity> allInstallments = this.getAllInstallments();

        if (isNotEmpty(reversedTrxns)) {
            for (AccountTrxnEntity reversedTrxn : reversedTrxns) {
                Short prevInstallmentId = null;
                Short currentInstallmentId = reversedTrxn.getInstallmentId();
                numberOfFullPayments = getIncrementedNumberOfFullPaymentsIfPaid(numberOfFullPayments, allInstallments,
                        prevInstallmentId, currentInstallmentId);

                if (!reversedTrxn.isTrxnForReversalOfLoanDisbursal()) {
                    LoanTrxnDetailEntity loanReverseTrxn = (LoanTrxnDetailEntity) reversedTrxn;

                    loanSummary.updatePaymentDetails(loanReverseTrxn);
                    if (loanReverseTrxn.isNotEmptyTransaction()) {
                        LoanScheduleEntity installment = (LoanScheduleEntity) getAccountActionDate(loanReverseTrxn.getInstallmentId());
                        installment.updatePaymentDetailsForAdjustment(loanReverseTrxn);

                        /*
                         * John W - mifos-1986 - when adjusting a loan that is LOAN_CLOSED_OBLIGATIONS_MET and was
                         * closed by applying an early repayment... need to increase loan summary figures by the amount
                         * that they were decreased for future payments.
                         *
                         * This means... for paid installments add up the amount due (for interest, fees and penalties).
                         * The amount due is not necessarily zero for this case.
                         */
                        
                        if (installment.isPaid()) {
                            increaseInterest = increaseInterest.add(installment.getInterestDue()).
                                    add(loanReverseTrxn.getInterestAmount());
                            increaseFees = increaseFees.add(installment.getTotalFeesDue());
                            if (!this.noOfInstallments.equals(numberOfInstalments)) {
                            increaseFees = increaseFees.add(installment.getMiscFeeDue()).
                                    add(loanReverseTrxn.getMiscFeeAmount());
                            increasePenalty = increasePenalty.add(installment.getPenaltyDue()).
                                    add(loanReverseTrxn.getPenaltyAmount());
                            }
                        }

                        installment.recordForAdjustment();

                        if (installment.hasFees()) {
                            for (AccountFeesActionDetailEntity accntFeesAction : installment.getAccountFeesActionDetails()) {
                                loanReverseTrxn.adjustFees(accntFeesAction);
                            }
                        }
                        
                        if (installment.hasPenalties()) {
                            for(LoanPenaltyScheduleEntity entity : installment.getLoanPenaltyScheduleEntities()) {
                                loanReverseTrxn.adjustPenalties(entity);
                            }
                        }
                    }
                }
            }
            AccountStateEntity currentAccountState = this.getAccountState();
            AccountStateEntity newAccountState = currentAccountState;
            boolean statusChangeNeeded = false;
            if (isLoanActiveWithStatusChangeHistory()) {
                AccountStatusChangeHistoryEntity lastAccountStatusChange = getLastAccountStatusChange();
                if (lastAccountStatusChange.isLoanActive()) {
                    statusChangeNeeded = true;
                } else if (currentAccountState.isLoanClosedObligationsMet()) {
                    statusChangeNeeded = true;
                    newAccountState = lastAccountStatusChange.getOldStatus();
                }
            }
            boolean accountReOpened = isAccountReOpened(currentAccountState, newAccountState);
            updatePerformanceHistory(accountReOpened);

            /*
             * John W - mifos-1986 - see related comment above
             */
            if (accountReOpened) {
                loanSummary.increaseBy(null, increaseInterest, increasePenalty, increaseFees);
                // fix for MIFOS-3287
                this.setClosedDate(null);
            }

            // Reverse just one payment when reopening an account
            // Else reverse payments equal to number of transactions reversed.
            if (accountReOpened) {
                updatePerformanceHistoryOnAdjustment(1);
            } else if (reversedTrxns.size() > 0) {
                updatePerformanceHistoryOnAdjustment(numberOfFullPayments);
            }

            if (statusChangeNeeded) {
                Short daysInArrears = getDaysInArrears(accountReOpened);
                if (currentAccountState.isLoanClosedObligationsMet()) {
                    AccountState newStatus = AccountState.LOAN_ACTIVE_IN_BAD_STANDING;
                    if (daysInArrears == 0) {
                        newStatus = AccountState.LOAN_ACTIVE_IN_GOOD_STANDING;
                    }
                    changeStatus(newStatus, null, "Account Reopened", loggedInUser);
                } else {
                    if (daysInArrears == 0) {
                        if (!currentAccountState.isLoanActiveInGoodStanding()) {
                            changeStatus(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, null, "Account Adjusted", loggedInUser);
                        }
                    } else {
                        if (!currentAccountState.isLoanActiveInBadStanding()) {
                            changeStatus(AccountState.LOAN_ACTIVE_IN_BAD_STANDING, null, "Account Adjusted", loggedInUser);
                            handleArrearsAging();
                        }
                    }
                }
            }

            try {
                PersonnelBO personnel = legacyPersonnelDao.getPersonnel(getUserContext().getId());
                addLoanActivity(buildLoanActivity(reversedTrxns, personnel, AccountConstants.LOAN_ADJUSTED, DateUtils.getCurrentDateWithoutTimeStamp()));
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    private void updatePerformanceHistory(boolean accountReOpened) {
        if (accountReOpened && this.getCustomer().isClient()) {
            ClientPerformanceHistoryEntity clientHistory = (ClientPerformanceHistoryEntity) this.getCustomer().getPerformanceHistory();
            clientHistory.incrementNoOfActiveLoans();
            Money newLastLoanAmount = getlegacyLoanDao().findClientPerformanceHistoryLastLoanAmountWhenRepaidLoanAdjusted(
                    this.getCustomer().getCustomerId(), this.getAccountId());
            clientHistory.setLastLoanAmount(newLastLoanAmount);
        }

        if (accountReOpened && this.getCustomer().isGroup()) {
            final GroupPerformanceHistoryEntity groupHistory = (GroupPerformanceHistoryEntity) this.getCustomer().getPerformanceHistory();
            Money newLastGroupLoanAmount = getlegacyLoanDao()
                    .findGroupPerformanceHistoryLastLoanAmountWhenRepaidLoanAdjusted(
                            this.getCustomer().getCustomerId(), this.getAccountId());
            groupHistory.setLastGroupLoanAmount(newLastGroupLoanAmount);
        }
    }

    private boolean isLoanActiveWithStatusChangeHistory() {
        return hasAccountStatusChangeHistory() && !isLoanCancelled();
    }

    private boolean isLoanCancelled() {
        return getAccountState().getId().equals(AccountState.LOAN_CANCELLED.getValue());
    }

    private boolean hasAccountStatusChangeHistory() {
        return org.mifos.platform.util.CollectionUtils.isNotEmpty(getAccountStatusChangeHistory());
    }

    private int getIncrementedNumberOfFullPaymentsIfPaid(Integer numberOfFullPayments,
            final List<AccountActionDateEntity> allInstallments, Short prevInstallmentId,
            final Short currentInstallmentId) {
        if (!currentInstallmentId.equals(prevInstallmentId)) {
            if (isInstallmentPaid(currentInstallmentId, allInstallments)) {
                numberOfFullPayments++;
            }
        }
        return numberOfFullPayments;
    }

    /**
     * This method checks if the loan account has been reopened because of payment adjustments made.
     *
     * John W - Can't see anyway of reopening LOAN_CLOSED_WRITTEN_OFF account, should take this out during refactoring
     *
     */
    private boolean isAccountReOpened(final AccountStateEntity currentAccountState,
            final AccountStateEntity newAccountState) {
        boolean reOpened = false;

        if (currentAccountState.isInState(AccountState.LOAN_CLOSED_OBLIGATIONS_MET)
                || currentAccountState.isInState(AccountState.LOAN_CLOSED_WRITTEN_OFF)
                && (newAccountState.isInState(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING) || newAccountState
                        .isInState(AccountState.LOAN_ACTIVE_IN_BAD_STANDING))) {
            reOpened = true;
        }
        return reOpened;
    }

    /**
     * regenerate installments starting from nextInstallmentId
     */
    @Override
    protected void regenerateFutureInstallments(final AccountActionDateEntity nextInstallment,
            final List<Days> workingDays, final List<Holiday> holidays) throws AccountException {

        int numberOfInstallmentsToGenerate = getLastInstallmentId();

        MeetingBO meeting = buildLoanMeeting(customer.getCustomerMeeting().getMeeting(), getLoanMeeting(),
                getLoanMeeting().getMeetingStartDate());

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        LocalDate currentDate = new LocalDate();
        LocalDate thisIntervalStartDate = meeting.startDateForMeetingInterval(currentDate);
        LocalDate nextMatchingDate = new LocalDate(scheduledEvent.nextEventDateAfter(thisIntervalStartDate
                .toDateTimeAtStartOfDay()));
        DateTime futureIntervalStartDate = meeting.startDateForMeetingInterval(nextMatchingDate)
                .toDateTimeAtStartOfDay();

        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(
                workingDays, holidays);

        List<DateTime> meetingDates = dateGeneration.generateScheduledDates(numberOfInstallmentsToGenerate,
                futureIntervalStartDate, scheduledEvent, false);

        updateSchedule(nextInstallment.getInstallmentId(), meetingDates);
    }

    private int calculateDays(final Date fromDate, final Date toDate) {
        long y = 1000 * 60 * 60 * 24;
        long x = getMFITime(toDate) / y - getMFITime(fromDate) / y;
        return (int) x;
    }

    private long getMFITime(final Date date) {
        Calendar cal1 = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        cal1.setTimeZone(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
        cal1.setTime(date);
        return date.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
    }

    /**
     * Calculate and return the list of {@link FeeInstallment}s to be applied. A fee installment will apply to one of
     * the given loan installmentDates if the installmentIds match. Here's the criteria for matching a fee installment
     * to a loan installment: Calculate the dates in nonAdjustedInstallmentDates that the fee would be due if the fee
     * were to start today. For each unadjusted fee date, build a FeeInstallment object based on the installmentId of
     * the nearest loan installment date in the list installmentDates (this is what causes fees to pile up on a future
     * loan installment that has been pushed out of a holiday), and add it to the list to be returned.
     */
    @Override
    protected final List<FeeInstallment> handlePeriodic(final AccountFeesEntity accountFees,
            final List<InstallmentDate> installmentDates, final List<InstallmentDate> nonAdjustedInstallmentDates)
            throws AccountException {
        Money accountFeeAmount = accountFees.getAccountFeeAmount();
        MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency();

        // Generate the dates in nonAdjustedInstallmentDates that the fee would be due if
        // the fee were to start today
        List<Date> feeDates = getFeeDates(feeMeetingFrequency, nonAdjustedInstallmentDates, false);

        // For each unadjusted fee date, build a FeeInstallment object based on the installmentId of the
        // nearest loan installment date adjusted for holidays (this is what causes fees to pile up
        // on a future loan installment that has been pushed out of a holiday), and add it to the list to
        // be returned
        ListIterator<Date> feeDatesIterator = feeDates.listIterator();
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        while (feeDatesIterator.hasNext()) {
            Date feeDate = feeDatesIterator.next();
            logger.debug("Handling periodic fee.." + feeDate);

            Short installmentId = getMatchingInstallmentId(installmentDates, feeDate);
            feeInstallmentList.add(buildFeeInstallment(installmentId, accountFeeAmount, accountFees));

        }
        return feeInstallmentList;
    }

    private LoanActivityEntity buildLoanActivity(final Collection<AccountTrxnEntity> accountTrxnDetails,
            final PersonnelBO personnel, String comments, final Date trxnDate) {
        Date activityDate = trxnDate;
        Money principal = new Money(getCurrency());
        Money interest = new Money(getCurrency());
        Money penalty = new Money(getCurrency());
        Money fees = new Money(getCurrency());

        for (AccountTrxnEntity accountTrxn : accountTrxnDetails) {
            if (!accountTrxn.isTrxnForReversalOfLoanDisbursal()) {
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

            if (accountTrxn.isTrxnForReversalOfLoanDisbursal()
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

    public LoanActivityEntity buildLoanActivity(final Money totalPrincipal, final PersonnelBO personnel,
            final String comments, final Date trxnDate) {
        Money interest = new Money(getCurrency());
        Money penalty = new Money(getCurrency());
        Money fees = new Money(getCurrency());
        return new LoanActivityEntity(this, personnel, comments, totalPrincipal, loanSummary.getOriginalPrincipal()
                .subtract(loanSummary.getPrincipalPaid()), interest, loanSummary.getOriginalInterest().subtract(
                loanSummary.getInterestPaid()), fees,
                loanSummary.getOriginalFees().subtract(loanSummary.getFeesPaid()), penalty, loanSummary
                        .getOriginalPenalty().subtract(loanSummary.getPenaltyPaid()), trxnDate);
    }

    /**
     * @deprecated - see {@link InstallmentFeeCalculator}.
     */
    @Deprecated
    private void populateAccountFeeAmount(final Set<AccountFeesEntity> accountFees, final Money loanInterest) {
        for (AccountFeesEntity accountFeesEntity : accountFees) {
            Money accountFeeAmount1 = new Money(getCurrency());
            Double feeAmount = accountFeesEntity.getFeeAmount();
            if (accountFeesEntity.getFees().getFeeType() == RateAmountFlag.AMOUNT) {
                accountFeeAmount1 = new Money(getCurrency(), feeAmount.toString());
            } else if (accountFeesEntity.getFees().getFeeType()== RateAmountFlag.RATE) {
                RateFeeBO rateFeeBO = (RateFeeBO) getFeeDao().findById(accountFeesEntity.getFees().getFeeId());
                FeeFormulaEntity formula = rateFeeBO.getFeeFormula();
                Money amountToCalculateOn = new Money(getCurrency(), "1.0");
                if (formula.getId().equals(FeeFormula.AMOUNT.getValue())) {
                    amountToCalculateOn = loanAmount;
                } else if (formula.getId().equals(FeeFormula.AMOUNT_AND_INTEREST.getValue())) {
                    amountToCalculateOn = loanAmount.add(loanInterest);
                } else if (formula.getId().equals(FeeFormula.INTEREST.getValue())) {
                    amountToCalculateOn = loanInterest;
                }
                Double rateAmount = amountToCalculateOn.multiply(feeAmount).divide(100).getAmountDoubleValue();

                String rateBasedOnFormula = rateAmount.toString();
                accountFeeAmount1 = new Money(getCurrency(), rateBasedOnFormula);
            }
            Money accountFeeAmount = accountFeeAmount1;
            accountFeesEntity.setAccountFeeAmount(accountFeeAmount);
        }
    }

    public static Boolean isDisbursementDateValid(final CustomerBO specifiedCustomer, final Date disbursementDate)
            throws AccountException {
        logger.debug("IsDisbursementDateValid invoked ");
        try {
            MeetingBO meeting = specifiedCustomer.getCustomerMeeting().getMeeting();
            return meeting.isValidMeetingDate(disbursementDate, DateUtils.getLastDayOfNextYear());
        } catch (MeetingException e) {
            throw new AccountException(e);
        }
    }

    /**
     * The fee (new or to be updated) is applied to the given list of AccountActionDateEntity(s). Note that the entities
     * are the actual entity objects referenced by the loan, so this method acts by side-effect, adding fees to the
     * given entities.
     *
     * @param fee
     *            the periodic FeeBO to apply to the given AccountActionDateEntity(s)
     * @param charge
     *            the
     * @param dueInstallments
     * @throws AccountException
     * @throws PersistenceException
     */
    private void applyPeriodicFee(final FeeBO fee, final Double charge,
            final List<AccountActionDateEntity> dueInstallments) throws AccountException {

        // Create an AccountFeesEntity linking the loan to the given fee fee and charge if the fee hasn't been applied,
        // or
        // update the applied fee's AccountFeesEntity.feeAmount with the given charge. Then set the
        // AccountFeeEntity.accountFeeAmount to this loan's originalInterest.
        AccountFeesEntity accountFee = getAccountFee(fee, charge);
        Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
        accountFeeSet.add(accountFee);
        populateAccountFeeAmount(accountFeeSet, loanSummary.getOriginalInterest());

        // Extract the list of InstallmentDate(s) from the given AccountActionDateEntity(s). Note that
        // the installmentId(s) likely do not start with 1 since the fee may be applied after some
        // installment dates have passed.
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        for (AccountActionDateEntity accountActionDateEntity : dueInstallments) {
            installmentDates.add(new InstallmentDate(accountActionDateEntity.getInstallmentId(),
                    accountActionDateEntity.getActionDate()));
        }

        // Get the full list of all loan InstallmentDate(s), past, present and future, without adjusting for holidays.
        // This will work correctly only if adjusting periodic fees is done when no installments have been paid
//        boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
//        List<InstallmentDate> nonAdjustedInstallmentDates = getInstallmentDates(getLoanMeeting(), noOfInstallments,
//                getInstallmentSkipToStartRepayment(), isRepaymentIndepOfMeetingEnabled,
//                false);

        // Use handlePeriodic to adjust fee installments for holiday periods and combine multiple fee installments due
        // for the
        // same loan installment. Finally, apply these updated fees to the given dueInstallments list and update
        // loan summary and activity tables.
        /*
         * old way List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(handlePeriodic(accountFee,
         * installmentDates, nonAdjustedInstallmentDates));
         */
        // new way
        ScheduledEvent loanScheduledEvent = ScheduledEventFactory.createScheduledEventFrom(this.getMeetingForAccount());
        List<FeeInstallment> feeInstallmentList = FeeInstallment.createMergedFeeInstallmentsForOneFeeStartingWith(
                loanScheduledEvent, accountFee, dueInstallments.size(), dueInstallments.get(0).getInstallmentId());
        Money totalFeeAmountApplied = applyFeeToInstallments(feeInstallmentList, dueInstallments);
        updateLoanSummary(fee.getFeeId(), totalFeeAmountApplied);
        updateLoanActivity(fee.getFeeId(), totalFeeAmountApplied, fee.getFeeName() + AccountConstants.APPLIED);
    }

    private void applyOneTimeFee(final FeeBO fee, final Double charge,
            final AccountActionDateEntity accountActionDateEntity) throws AccountException {
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

    protected boolean canApplyMiscCharge(final Money charge) {
        return !havePaymentsBeenMade() || MoneyUtils.isRoundedAmount(charge);
    }
    
    public void applyPenalty(final Money charge, final int scheduleEntityId, final AccountPenaltiesEntity penaltiesEntity, final Date current) {
        LoanScheduleEntity loanScheduleEntity = new ArrayList<LoanScheduleEntity>(getLoanScheduleEntities()).get(scheduleEntityId - 1);
        PenaltyBO penalty = penaltiesEntity.getPenalty();
        LoanPenaltyScheduleEntity entity = loanScheduleEntity.getPenaltyScheduleEntity(penalty.getPenaltyId());
        Money money = new Money(getCurrency());

        loanScheduleEntity.setPenalty(loanScheduleEntity.getPenalty().add(charge));

        getLoanSummary().updateOriginalPenalty(charge);

        addLoanActivity(new LoanActivityEntity(this, personnel, money, money, money, charge, getLoanSummary(), penalty.getPenaltyName() + " applied"));

        if(entity == null) {
            loanScheduleEntity.addLoanPenaltySchedule(new LoanPenaltyScheduleEntity(loanScheduleEntity, penalty, penaltiesEntity, charge, current));
        } else {
            entity.setPenaltyAmount(entity.getPenaltyAmount().add(charge));
            entity.setLastApplied(current);
        }
        
        penaltiesEntity.setLastAppliedDate(new DateTimeService().getCurrentJavaDateTime());
    }
    
    private void applyMiscCharge(final Short chargeType, final Money charge,
            final AccountActionDateEntity accountActionDateEntity) throws AccountException {

        if (!canApplyMiscCharge(charge)) {
            throw new AccountException(AccountExceptionConstants.CANT_APPLY_CHARGE_EXCEPTION);
        }

        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
        loanScheduleEntity.applyMiscCharge(chargeType, charge);
        updateLoanSummary(chargeType, charge);
        updateLoanActivity(chargeType, charge, "");
    }

    private void updateLoanSummary(final Short chargeType, final Money charge) {
        if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
            getLoanSummary().updateOriginalPenalty(charge);
        } else {
            getLoanSummary().updateOriginalFees(charge);
        }
    }

    private void updateLoanActivity(final Short chargeType, final Money charge, final String comments)
            throws AccountException {
        try {
            PersonnelBO personnel = legacyPersonnelDao.getPersonnel(getUserContext().getId());
            LoanActivityEntity loanActivityEntity = null;
            if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                loanActivityEntity = new LoanActivityEntity(this, personnel, new Money(getCurrency()), new Money(
                        getCurrency()), new Money(getCurrency()), charge, getLoanSummary(),
                        AccountConstants.MISC_PENALTY_APPLIED);
            } else if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES))) {
                loanActivityEntity = new LoanActivityEntity(this, personnel, new Money(getCurrency()), new Money(
                        getCurrency()), charge, new Money(getCurrency()), getLoanSummary(),
                        AccountConstants.MISC_FEES_APPLIED);
            } else {
                loanActivityEntity = new LoanActivityEntity(this, personnel, new Money(getCurrency()), new Money(
                        getCurrency()), charge, new Money(getCurrency()), getLoanSummary(), comments);
            }
            addLoanActivity(loanActivityEntity);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private Money applyFeeToInstallments(final List<FeeInstallment> feeInstallmentList,
            final List<AccountActionDateEntity> accountActionDateList) {
        Date lastAppliedDate = null;
        Money totalFeeAmountApplied = new Money(getCurrency());
        AccountFeesEntity accountFeesEntity = null;
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
            for (FeeInstallment feeInstallment : feeInstallmentList) {
                if (feeInstallment.getInstallmentId().equals(loanScheduleEntity.getInstallmentId())) {
                    lastAppliedDate = loanScheduleEntity.getActionDate();
                    totalFeeAmountApplied = totalFeeAmountApplied.add(feeInstallment.getAccountFee());
                    /*
                     * AccountFeesActionDetailEntity accountFeesActionDetailEntity = new LoanFeeScheduleEntity(
                     * loanScheduleEntity, feeInstallment .getAccountFeesEntity().getFees(),
                     * feeInstallment.getAccountFeesEntity(), feeInstallment.getAccountFee()); loanScheduleEntity
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
        }
        accountFeesEntity.setLastAppliedDate(lastAppliedDate);
        addAccountFees(accountFeesEntity);
        return totalFeeAmountApplied;
    }

    private void filterTimeOfDisbursementFees(final LoanScheduleEntity loanScheduleEntity, final FeeBO fee) {
        Short paymentType = fee.getFeeFrequency().getFeePayment().getId();
        if (paymentType.equals(FeePayment.TIME_OF_DISBURSEMENT.getValue()) && !isInterestDeductedAtDisbursement()) {
            Set<AccountFeesActionDetailEntity> accountFeesDetailSet = loanScheduleEntity.getAccountFeesActionDetails();
            for (Iterator<AccountFeesActionDetailEntity> iter = accountFeesDetailSet.iterator(); iter.hasNext();) {
                AccountFeesActionDetailEntity accountFeesActionDetailEntity = iter.next();
                if (fee.equals(accountFeesActionDetailEntity.getFee())) {
                    iter.remove();
                }
            }
        }
    }

    private MeetingBO buildLoanMeeting(final MeetingBO customerMeeting, final MeetingBO loanOfferingMeeting,
            final Date disbursementDate) throws AccountException {

        // this is called from 'proper constructor' only if LSIM is disabled
        if (customerMeeting != null
                && loanOfferingMeeting != null
                && customerMeeting.hasSameRecurrenceAs(loanOfferingMeeting)
                &&  customerMeeting.recursOnMultipleOf(loanOfferingMeeting)) {

            RecurrenceType meetingFrequency = customerMeeting.getMeetingDetails().getRecurrenceTypeEnum();
            MeetingType meetingType = MeetingType.fromInt(customerMeeting.getMeetingType().getMeetingTypeId());
            Short recurAfter = loanOfferingMeeting.getMeetingDetails().getRecurAfter();
            try {
                MeetingBO meetingToReturn;
                if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
                    if (customerMeeting.isMonthlyOnDate()) {
                        meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getDayNumber(), recurAfter,
                                disbursementDate, meetingType, customerMeeting.getMeetingPlace());
                    } else {
                        meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getWeekDay(),
                                customerMeeting.getMeetingDetails().getWeekRank(), recurAfter, disbursementDate,
                                meetingType, customerMeeting.getMeetingPlace());
                    }
                } else if (meetingFrequency.equals(RecurrenceType.WEEKLY)) {
                    meetingToReturn = new MeetingBO(customerMeeting.getMeetingDetails().getMeetingRecurrence()
                            .getWeekDayValue(), recurAfter, disbursementDate, meetingType, customerMeeting
                            .getMeetingPlace());
                } else {
                    meetingToReturn = new MeetingBO(meetingFrequency, recurAfter, disbursementDate, meetingType);
                }
                return meetingToReturn;
            } catch (MeetingException me) {
                throw new AccountException(me);
            }
        }

        throw new AccountException(AccountExceptionConstants.CHANGEINLOANMEETING);
    }

    private LoanSummaryEntity buildLoanSummary() {
        Money interest = new Money(getCurrency());
        Money fees = new Money(getCurrency());
        Set<AccountActionDateEntity> actionDates = getAccountActionDates();
        if (actionDates != null && actionDates.size() > 0) {
            for (AccountActionDateEntity accountActionDate : actionDates) {
                LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
                interest = interest.add(loanSchedule.getInterest());
                fees = fees.add(loanSchedule.getTotalFeesDueWithMiscFee());
            }
        }
        fees = fees.add(getDisbursementFeeAmount());
        return new LoanSummaryEntity(this, loanAmount, interest, fees, rawAmountTotal);

    }

    private void updateLoanSummary(){
        Money interest = new Money(getCurrency());
        Money fees = new Money(getCurrency());
        Money principal = new Money(getCurrency());

        Set<LoanScheduleEntity> loanScheduleEntities = getLoanScheduleEntities();
        if (loanScheduleEntities != null && loanScheduleEntities.size() > 0) {
            for (AccountActionDateEntity accountActionDate : loanScheduleEntities) {
                LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
                principal = principal.add(loanSchedule.getPrincipal());
                interest = interest.add(loanSchedule.getInterest());
                fees = fees.add(loanSchedule.getTotalFeesDueWithMiscFee());
            }
        }
        fees = fees.add(getDisbursementFeeAmount());

        loanSummary.setOriginalPrincipal(principal);
        loanSummary.setOriginalInterest(interest);
        loanSummary.setOriginalFees(fees);
    }


    private Money getDisbursementFeeAmount() {
        Money fees = new Money(getCurrency());
        for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
            if (!isInterestDeductedAtDisbursement() && accountFeesEntity.getFees().isTimeOfDisbursement()) {
                fees = fees.add(accountFeesEntity.getAccountFeeAmount());
            }
        }
        return fees;
    }

    private void updateCustomerHistoryOnLastInstlPayment(final Money totalAmount) throws AccountException {
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
            clientPerfHistory.decrementNoOfActiveLoans();
        }
    }

    private void updateCustomerHistoryOnDisbursement(final Money disburseAmount) throws AccountException {
        try {
            getCustomer().updatePerformanceHistoryOnDisbursement(this, disburseAmount);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }
    }

    private void updateCustomerHistoryOnRepayment() throws AccountException {
        try {
            getCustomer().updatePerformanceHistoryOnRepayment(this, this.getLoanAmount());
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
        Money lastLoanAmount = new Money(getCurrency());
        try {
            customer.updatePerformanceHistoryOnReversal(this, lastLoanAmount);
        } catch (CustomerException e) {
            throw new AccountException(e);
        }
    }

    /**
     * pull this logic out of LoanBO entity and reuse LoanSchedule behaviour used from service facades at a service level
     */
    @Deprecated
    private void regeneratePaymentSchedule(final boolean isRepaymentIndepOfMeetingEnabled,
            final MeetingBO newMeetingForRepaymentDay) throws AccountException {
        Money miscFee = getMiscFee();
        Money miscPenalty = getMiscPenalty();
        try {
            getlegacyLoanDao().deleteInstallments(this.getAccountActionDates());
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        // Set new meeting if there is new one for repayment day
        // Delete previous loan meeting if loan is parent account and set individual loans(if any) loanMeeting same as parent 
        if (isRepaymentIndepOfMeetingEnabled && newMeetingForRepaymentDay != null &&
                !this.getLoanMeeting().equals(newMeetingForRepaymentDay)) {
            if ( null != this.getLoanMeeting() && !this.isIndividualLoan() ){
                this.delete(this.getLoanMeeting());
            }
            setLoanMeeting(newMeetingForRepaymentDay);
            if ( this.hasMemberAccounts()){
                for (LoanBO individualLoanBO : this.getMemberAccounts()){
                    individualLoanBO.setLoanMeeting(newMeetingForRepaymentDay);
                }
            }
        }
        this.resetAccountActionDates();
        loanMeeting.setMeetingStartDate(disbursementDate);

		RecurringScheduledEventFactory scheduledEventFactory = new RecurringScheduledEventFactoryImpl();
		ScheduledEvent meetingScheduledEvent = scheduledEventFactory.createScheduledEventFrom(this.loanMeeting);

		LoanInstallmentFactory loanInstallmentFactory = new LoanInstallmentFactoryImpl(scheduledEventFactory);
		LoanInstallmentGenerator loanInstallmentGenerator = loanInstallmentFactory.create(this.getLoanMeeting(), isRepaymentIndepOfMeetingEnabled);

		LocalDate actualDisbursementDate = new LocalDate(this.disbursementDate);
		List<InstallmentDate> installmentDates = loanInstallmentGenerator.generate(actualDisbursementDate, this.noOfInstallments, this.gracePeriodType.asEnum(), this.gracePeriodDuration, this.office.getOfficeId());

		Integer numberOfInstallments = installmentDates.size();
		GraceType graceType = this.gracePeriodType.asEnum();
		InterestType interestType = InterestType.fromInt(this.interestType.getId());
		Integer interestDays = AccountingRules.getNumberOfInterestDays().intValue();

		LoanDecliningInterestAnnualPeriodCalculator decliningInterestAnnualPeriodCalculator = new LoanDecliningInterestAnnualPeriodCalculatorFactory().create(loanMeeting.getRecurrenceType());
		Double decliningInterestAnnualPeriod = decliningInterestAnnualPeriodCalculator.calculate(loanMeeting.getRecurAfter().intValue(), interestDays);
		Double interestFractionalRatePerInstallment = interestRate / decliningInterestAnnualPeriod / 100;

		LoanDurationInAccountingYearsCalculator loanDurationInAccountingYearsCalculator = new LoanDurationInAccountingYearsCalculatorFactory().create(loanMeeting.getRecurrenceType());
		Double durationInYears = loanDurationInAccountingYearsCalculator.calculate(loanMeeting.getRecurAfter().intValue(), numberOfInstallments, interestDays);

		List<DateTime> scheduledInstallments = new ArrayList<DateTime>();
        for (InstallmentDate installmentDate : installmentDates) {
            scheduledInstallments.add(new DateTime(installmentDate.getInstallmentDueDate()));
        }
		LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetails(loanAmount, interestRate, graceType, gracePeriodDuration.intValue(),
		        numberOfInstallments, durationInYears, interestFractionalRatePerInstallment, actualDisbursementDate, scheduledInstallments);

		LoanInterestCalculatorFactory loanInterestCalculatorFactory = new LoanInterestCalculatorFactoryImpl();
		LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(interestType, this.loanOffering.isVariableInstallmentsAllowed());

		Money loanInterest = loanInterestCalculator.calculate(loanInterestCalculationDetails);

		EqualInstallmentGeneratorFactory equalInstallmentGeneratorFactory = new EqualInstallmentGeneratorFactoryImpl();
		PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest, this.loanOffering.isVariableInstallmentsAllowed());

		List<InstallmentPrincipalAndInterest> principalWithInterestInstallments = equalInstallmentGenerator.generateEqualInstallments(loanInterestCalculationDetails);
		List<LoanScheduleEntity> unroundedLoanSchedules = createUnroundedLoanSchedulesFromInstallments(installmentDates, loanInterest, this.loanAmount, meetingScheduledEvent, principalWithInterestInstallments, this.getAccountFees());

		Money rawAmount = calculateTotalFeesAndInterestForLoanSchedules(unroundedLoanSchedules);

		if (loanSummary == null) {
		    // save it to LoanBO first and when loan summary is created it will
		    // be retrieved and save to loan summary
		    setRawAmountTotal(rawAmount);
		} else {
		    loanSummary.setRawAmountTotal(rawAmount);
		}

		List<LoanScheduleEntity> allExistingLoanSchedules = new ArrayList<LoanScheduleEntity>();

		LoanScheduleRounderHelper loanScheduleRounderHelper = new DefaultLoanScheduleRounderHelper();
		LoanScheduleRounder loanScheduleInstallmentRounder = new DefaultLoanScheduleRounder(loanScheduleRounderHelper);

		List<LoanScheduleEntity> roundedLoanSchedules = loanScheduleInstallmentRounder.round(graceType, gracePeriodDuration, loanAmount,
				interestType, unroundedLoanSchedules, allExistingLoanSchedules);

		for (LoanScheduleEntity roundedLoanSchedule : roundedLoanSchedules) {
		    addAccountActionDate(roundedLoanSchedule);
		}
        LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) getAccountActionDate((short) 1);
        loanScheduleEntity.setMiscFee(miscFee);
        loanScheduleEntity.setMiscPenalty(miscPenalty);
        Money interest = new Money(getCurrency());
        Money fees = new Money(getCurrency());
        Money penalty = new Money(getCurrency());
        Money principal = new Money(getCurrency());
        Set<AccountActionDateEntity> actionDates = getAccountActionDates();
        if (actionDates != null && actionDates.size() > 0) {
            for (AccountActionDateEntity accountActionDate : actionDates) {
                LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDate;
                principal = principal.add(loanSchedule.getPrincipal());
                interest = interest.add(loanSchedule.getInterest());
                fees = fees.add(loanSchedule.getTotalFeesDueWithMiscFee());
                penalty = penalty.add(loanSchedule.getTotalPenalty());
            }
        }
        fees = fees.add(getDisbursementFeeAmount());
        loanSummary.setOriginalInterest(interest);
        loanSummary.setOriginalFees(fees);
        loanSummary.setOriginalPenalty(penalty);
    }

    private AccountPaymentEntity payInterestAtDisbursement(final String receiptNum, final Date transactionDate,
            final Short paymentTypeId, final PersonnelBO loggedInUser, final Date receiptDate) throws AccountException {

        AccountActionDateEntity firstInstallment = null;
        for (AccountActionDateEntity accountActionDate : this.getAccountActionDates()) {
            if (accountActionDate.getInstallmentId().shortValue() == 1) {
                firstInstallment = accountActionDate;
                break;
            }
        }

        PaymentData paymentData = PaymentData.createPaymentData(((LoanScheduleEntity) firstInstallment)
                .getTotalDueWithFees(), loggedInUser, paymentTypeId, transactionDate);
        paymentData.setReceiptDate(receiptDate);
        paymentData.setReceiptNum(receiptNum);

        // Pay 1st installment and return accountPayableEntity to disbursal process
        return makePayment(paymentData);

    }

    public AccountActionDateEntity getLastInstallmentAccountAction() {
        Set<AccountActionDateEntity> accountActionDateEntitySet = getAccountActionDates();
        AccountActionDateEntity nextAccountAction = null;
        if (isNotEmpty(accountActionDateEntitySet)) {
            nextAccountAction = java.util.Collections.max(accountActionDateEntitySet);
        }
        return nextAccountAction;
    }

    private Money getMiscFee() {
        Money miscFee = new Money(getCurrency());
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            if (loanSchedule.getMiscFee() != null) {
                miscFee = miscFee.add(loanSchedule.getMiscFee());
            }
        }
        return miscFee;
    }

    private Money getMiscPenalty() {
        Money miscPenalty = new Money(getCurrency());
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
            if (accountActionDateEntity.isNotPaid()) {
                unpaidInstallmentList.add(accountActionDateEntity);
            }
        }
        return unpaidInstallmentList;
    }

    private Money getEarlyClosureAmount() {
        Money amount = new Money(getCurrency());
        for (AccountActionDateEntity accountActionDateEntity : getListOfUnpaidInstallments()) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getPrincipal());
        }
        return amount;
    }

    public void processFeesAtDisbursement(final AccountPaymentEntity accountPayment,
            final Money feeAmountAtDisbursement) {

        loanSummary.updateFeePaid(feeAmountAtDisbursement);

        List<AccountFeesEntity> applicableAccountFees = new ArrayList<AccountFeesEntity>();
        for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
            if (accountFeesEntity.isTimeOfDisbursement()) {
                applicableAccountFees.add(accountFeesEntity);
            }
        }

        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPayment,
                AccountActionTypes.FEE_REPAYMENT, Short.valueOf("0"), accountPayment.getPaymentDate(), accountPayment
                        .getCreatedByUser(), accountPayment.getPaymentDate(), feeAmountAtDisbursement, "-", null,
                new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()),
                new Money(getCurrency()), applicableAccountFees, null);

        accountPayment.addAccountTrxn(loanTrxnDetailEntity);

        addLoanActivity(buildLoanActivity(accountPayment.getAccountTrxns(), accountPayment.getCreatedByUser(),
                AccountConstants.PAYMENT_RCVD, accountPayment.getPaymentDate()));
    }

    /**
     * Validate that a given payment amount is valid. Payments greater than the total outstanding amount due on the loan
     * are not valid.
     *
     * @param amount
     *            the amount of a payment
     * @return true if the payment amount will be accepted
     */
    @Override
    public boolean paymentAmountIsValid(final Money amount, Set<AccountPaymentParametersDto.PaymentOptions> options) {
        Money totalRepayableAmount = getTotalRepayableAmount();
        return (null != amount) && (amount.isGreaterThanOrEqualZero()) &&
                (options.contains(AccountPaymentParametersDto.PaymentOptions.ALLOW_OVERPAYMENTS) ||
                        amount.isLessThanOrEqual(totalRepayableAmount) || totalRepayableAmount.subtract(amount).isTinyAmount());
    }

    private LoanPaymentTypes getLoanPaymentType(final Money amount) {
        Money totalPaymentDue = getTotalPaymentDue();
        if (amount.equals(totalPaymentDue) || totalPaymentDue.subtract(amount).isTinyAmount()) {
            return LoanPaymentTypes.FULL_PAYMENT;
        } else if (amount.isLessThan(totalPaymentDue)) {
            return LoanPaymentTypes.PARTIAL_PAYMENT;
        } else if (amount.isGreaterThan(totalPaymentDue) && amount.isLessThanOrEqual(getTotalRepayableAmount())) {
            return LoanPaymentTypes.FUTURE_PAYMENT;
        }
        return null;
    }

    private void changeLoanStatus(final AccountState newAccountState, final PersonnelBO personnel)
            throws AccountException {
        AccountStateEntity accountState = this.getAccountState();
        try {
            setAccountState(legacyMasterDao.getPersistentObject(AccountStateEntity.class,
                    newAccountState.getValue()));
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(accountState, this.getAccountState(),
                personnel, this));
    }

    public Money getTotalRepayableAmount() {
        Money amount = new Money(getCurrency());
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            amount = amount.add(((LoanScheduleEntity) accountActionDateEntity).getTotalDueWithFees());
        }
        
        if(isDecliningBalanceInterestRecalculation()) {
            BigDecimal extraInterest = ApplicationContextProvider.getBean(ScheduleCalculatorAdaptor.class).getExtraInterest(this, DateUtils.getCurrentDateWithoutTimeStamp());
            Money extraInterestMoney = MoneyUtils.currencyRound(amount.add(new Money(amount.getCurrency(), extraInterest)));
            
            //MIFOS-5589 - formula sometimes makes repayable amount less than original principal.
            //Condition should be removed after further fixes.
            if(extraInterestMoney.isGreaterThanZero()) {
                amount = amount.add(extraInterestMoney);
            }
        }
        return amount;
    }

    private boolean isAdjustmentForInterestDedAtDisb(final Short installmentId) {
        return installmentId.equals(Short.valueOf("1")) && isInterestDeductedAtDisbursement();
    }

    public boolean isRedone() {
        return this.redone;
    }

    Boolean getRedone() {
        return this.redone;
    }

    void setRedone(final Boolean val) {
        this.redone = val;
    }

    private void makeEarlyRepaymentForArrears(final AccountPaymentEntity accountPaymentEntity,
            final String comments, final AccountActionTypes accountActionTypes, final PersonnelBO currentUser) {
        List<AccountActionDateEntity> applicableArrears = getApplicableIdsForArrears();
        for (AccountActionDateEntity applicableArrear : applicableArrears) {
            repayInstallment((LoanScheduleEntity)applicableArrear, accountPaymentEntity, accountActionTypes, currentUser, comments, ((LoanScheduleEntity)applicableArrear).getInterestDue());
        }
    }

    void repayInstallment(LoanScheduleEntity loanSchedule, AccountPaymentEntity accountPaymentEntity,
                                  AccountActionTypes accountActionTypes, PersonnelBO currentUser,
                                  String comments, Money interestDue) {
        Money principal = loanSchedule.getPrincipalDue();
        Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
        Money penalty = loanSchedule.getPenaltyDue();
        Money interest = interestDue.add(loanSchedule.getExtraInterestDue());
        Money totalAmt = principal.add(interest).add(fees).add(penalty);

        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity, accountActionTypes, loanSchedule
                .getInstallmentId(), loanSchedule.getActionDate(), currentUser, accountPaymentEntity.getPaymentDate(),
                totalAmt, comments, null, principal, interest,
                loanSchedule.getPenalty().subtract(loanSchedule.getPenaltyPaid()),
                loanSchedule.getMiscFeeDue(), loanSchedule.getMiscPenaltyDue(), null, null);

        addFeeTransactions(loanTrxnDetailEntity, loanSchedule.getAccountFeesActionDetails());
        addPenaltyTransactions(loanTrxnDetailEntity, loanSchedule.getLoanPenaltyScheduleEntities());
        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        loanSchedule.makeEarlyRepaymentEntries(LoanConstants.PAY_FEES_PENALTY_INTEREST,
                interestDue, accountPaymentEntity.getPaymentDate());
        setCalculatedInterestIfApplicable(loanTrxnDetailEntity, loanSchedule, interestDue);
        updatePaymentDetails(accountActionTypes, principal, interest, penalty, fees);
    }

    void repayInstallmentWithInterestWaiver(AccountActionDateEntity nextInstallment, final AccountPaymentEntity accountPaymentEntity,
                                              final String comments, final AccountActionTypes accountActionTypes, final PersonnelBO currentUser) {
        LoanScheduleEntity loanSchedule = (LoanScheduleEntity) nextInstallment;
        Money principal = loanSchedule.getPrincipalDue();
        Money interestDue = loanSchedule.getInterestDue();
        Money extraInterestDue = loanSchedule.getExtraInterestDue();
        Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
        Money penalty = loanSchedule.getPenaltyDue();
        Money totalAmt = principal.add(fees).add(penalty);

        LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity, accountActionTypes,
                loanSchedule.getInstallmentId(), loanSchedule.getActionDate(), currentUser,
                accountPaymentEntity.getPaymentDate(), totalAmt, comments, null, principal, extraInterestDue,
                loanSchedule.getPenalty().subtract(loanSchedule.getPenaltyPaid()), loanSchedule.getMiscFeeDue(), loanSchedule
                .getMiscPenaltyDue(), null, null);

        addFeeTransactions(loanTrxnDetailEntity, loanSchedule.getAccountFeesActionDetails());
        addPenaltyTransactions(loanTrxnDetailEntity, loanSchedule.getLoanPenaltyScheduleEntities());
        accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
        loanSchedule.makeEarlyRepaymentEntries(LoanConstants.PAY_FEES_PENALTY,
                Money.zero(getCurrency()), accountPaymentEntity.getPaymentDate());
        getLoanSummary().decreaseBy(null, interestDue, null, null);
        setCalculatedInterestIfApplicable(loanTrxnDetailEntity, loanSchedule, Money.zero(getCurrency()));
        updatePaymentDetails(accountActionTypes, principal, null, penalty, fees);
    }

    private void setCalculatedInterestIfApplicable(LoanTrxnDetailEntity loanTrxnDetailEntity,
                                                   LoanScheduleEntity loanSchedule, Money interestDue) {
        if (isDecliningBalanceInterestRecalculation()) {
            loanTrxnDetailEntity.computeAndSetCalculatedInterestOnPayment(
                    loanSchedule.getInterest(), loanSchedule.getExtraInterestPaid(), interestDue);
        }
    }

    private void makeEarlyRepaymentForFutureInstallments(final AccountPaymentEntity accountPaymentEntity,
            final String comments, final AccountActionTypes accountActionTypes, final PersonnelBO currentUser) {
        List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallments();
        for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            Money principal = loanSchedule.getPrincipalDue();
            Money interest = loanSchedule.getInterestDue();
            Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
            Money penalty = loanSchedule.getPenaltyDue();

            LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity, accountActionTypes,
                    loanSchedule.getInstallmentId(), loanSchedule.getActionDate(), currentUser,
                    accountPaymentEntity.getPaymentDate(), principal, comments, null, principal, new Money(getCurrency()),
                    new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()), null, null);

            accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
            loanSchedule.makeEarlyRepaymentEntries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST,
                    loanSchedule.getInterestDue(), accountPaymentEntity.getPaymentDate());
            loanSummary.decreaseBy(null, interest, penalty, fees);
            updatePaymentDetails(accountActionTypes, principal, null, null, null);
        }

    }

    private void makeWriteOffOrReschedulePaymentForFutureInstallments(final AccountPaymentEntity accountPaymentEntity,
            final String comments, final AccountActionTypes accountActionTypes, final PersonnelBO currentUser) {
        List<AccountActionDateEntity> futureInstallmentsList = getApplicableIdsForFutureInstallmentsForWriteOffOrReschedule();
        for (AccountActionDateEntity accountActionDateEntity : futureInstallmentsList) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
            Money principal = loanSchedule.getPrincipalDue();
            Money interest = loanSchedule.getInterestDue();
            Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
            Money penalty = loanSchedule.getPenaltyDue();

            LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity, accountActionTypes,
                    loanSchedule.getInstallmentId(), loanSchedule.getActionDate(), currentUser,
                    accountPaymentEntity.getPaymentDate(), principal, comments, null, principal, new Money(getCurrency()),
                    new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()), null, null);

            accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
            loanSchedule.makeEarlyRepaymentEntries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST,
                    loanSchedule.getInterestDue(), accountPaymentEntity.getPaymentDate());
            loanSummary.decreaseBy(null, interest, penalty, fees);
            updatePaymentDetails(accountActionTypes, principal, null, null, null);
        }

    }
    private void addFeeTransactions(LoanTrxnDetailEntity loanTrxnDetailEntity, Set<AccountFeesActionDetailEntity> accountFeesActionDetails) {
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {
            if (accountFeesActionDetailEntity.getFeeDue().isGreaterThanZero()) {
                FeesTrxnDetailEntity feesTrxnDetailEntity = new FeesTrxnDetailEntity(loanTrxnDetailEntity,
                        accountFeesActionDetailEntity.getAccountFee(), accountFeesActionDetailEntity.getFeeDue());
                loanTrxnDetailEntity.addFeesTrxnDetail(feesTrxnDetailEntity);
            }
        }
    }
    
    private void addPenaltyTransactions(LoanTrxnDetailEntity loanTrxnDetailEntity, Set<LoanPenaltyScheduleEntity> loanPenaltyScheduleEntities) {
        for (LoanPenaltyScheduleEntity loanPenaltyScheduleEntity : loanPenaltyScheduleEntities) {
            if (loanPenaltyScheduleEntity.getPenaltyDue().isGreaterThanZero()) {
                PenaltiesTrxnDetailEntity penaltiesTrxnDetailEntity = new PenaltiesTrxnDetailEntity(loanTrxnDetailEntity,
                        loanPenaltyScheduleEntity.getAccountPenalty(), loanPenaltyScheduleEntity.getPenaltyDue());
                loanTrxnDetailEntity.addPenaltiesTrxnDetail(penaltiesTrxnDetailEntity);
            }
        }
    }

    private void updatePaymentDetails(AccountActionTypes accountActionTypes, Money principal, Money interest, Money penalty, Money fees) {
        if (!accountActionTypes.isWrittenOffOrRescheduled()) {
            getLoanSummary().updatePaymentDetails(principal, interest, penalty, fees);
        }
    }

    public int getDisbursementTerm() {
        List<AccountActionDateEntity> pastInstallments = getPastInstallments();
        List<AccountActionDateEntity> installmentsInDisbursement = new ArrayList<AccountActionDateEntity>();
        if (!pastInstallments.isEmpty()) {
            for (AccountActionDateEntity accountAction : pastInstallments) {
                if (accountAction.isPaid()) {
                    installmentsInDisbursement.add(accountAction);
                }
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
        BigDecimal numOfInstallments = new BigDecimal(getNoOfInstallments());
        return principalInArrearsAndOutsideLateness.multiply(numOfInstallments).divide(totalPrincipal).doubleValue();

    }

    public Money getNetOfSaving() {
        return getRemainingPrincipalAmount().subtract(getCustomer().getSavingsBalance(getCurrency()));
    }

    public LoanBO getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(final LoanBO parentAccount) {
        this.parentAccount = parentAccount;
        if (parentAccount != null) {
            parentAccount.addMemberAccount(this);
        }
    }

    public MaxMinLoanAmount getMaxMinLoanAmount() {
        return maxMinLoanAmount;
    }

    public MaxMinInterestRate getMaxMinInterestRate() {
        return maxMinInterestRate;
    }

    public MaxMinNoOfInstall getMaxMinNoOfInstall() {
        return maxMinNoOfInstall;
    }

    public RankOfDay getMonthRank() {
        return monthRank;
    }

    public void setMonthRank(final RankOfDay monthRank) {
        this.monthRank = monthRank;
    }

    public WeekDay getMonthWeek() {
        return monthWeek;
    }

    public void setMonthWeek(final WeekDay monthWeek) {
        this.monthWeek = monthWeek;
    }

    public Short getRecurMonth() {
        return recurMonth;
    }

    public void setRecurMonth(final Short recurMonth) {
        this.recurMonth = recurMonth;
    }

    public WeekDay getMonthWeekValue() {
        return monthWeek;
    }

    public RankOfDay getWeekRank() {
        return monthRank;
    }

    public boolean isOfProductOffering(final LoanOfferingBO loanOfferingBO) {
        return this.loanOffering.isOfSameOffering(loanOfferingBO);
    }

    private Money calculateTotalFeesAndInterestForLoanSchedules(List<LoanScheduleEntity> unroundedLoanSchedules) {

    	Money zero = new Money(getCurrency());
        Money interest = zero;
        Money fees = zero;

        for (LoanScheduleEntity unroundedLoanSchedule : unroundedLoanSchedules) {
            interest = interest.add(unroundedLoanSchedule.getInterest());
            fees = fees.add(unroundedLoanSchedule.getTotalFeesDueWithMiscFee());
        }

        Money feeDisbursementAmount = zero;
        for (AccountFeesEntity accountFeesEntity : this.getAccountFees()) {
            if (accountFeesEntity.getFees().isTimeOfDisbursement()) {
                feeDisbursementAmount = fees.add(accountFeesEntity.getAccountFeeAmount());
            }
        }

        fees = fees.add(feeDisbursementAmount);
        fees = MoneyUtils.currencyRound(fees);
        interest = MoneyUtils.currencyRound(interest);

        Money rawAmount = interest.add(fees);
        return rawAmount;
    }

    private List<LoanScheduleEntity> createUnroundedLoanSchedulesFromInstallments(List<InstallmentDate> installmentDates,
            Money loanInterest, Money loanAmount, ScheduledEvent meetingScheduledEvent,
            List<InstallmentPrincipalAndInterest> principalWithInterestInstallments, Set<AccountFeesEntity> accountFees) {

        List<LoanScheduleEntity> unroundedLoanSchedules = new ArrayList<LoanScheduleEntity>();

        List<FeeInstallment> feeInstallments = new ArrayList<FeeInstallment>();
        if (!getAccountFees().isEmpty()) {

            InstallmentFeeCalculatorFactory installmentFeeCalculatorFactory = new InstallmentFeeCalculatorFactoryImpl();

            for (AccountFeesEntity accountFeesEntity : accountFees) {

                RateAmountFlag feeType = accountFeesEntity.getFees().getFeeType();
                InstallmentFeeCalculator installmentFeeCalculator = installmentFeeCalculatorFactory.create(getFeeDao(), feeType);

                Double feeAmount = accountFeesEntity.getFeeAmount();
                Money accountFeeAmount = installmentFeeCalculator.calculate(feeAmount, loanAmount, loanInterest, accountFeesEntity.getFees());
                accountFeesEntity.setAccountFeeAmount(accountFeeAmount);
            }
            feeInstallments = FeeInstallment.createMergedFeeInstallments(meetingScheduledEvent, accountFees, installmentDates.size());
        }

        int installmentIndex = 0;
        for (InstallmentDate installmentDate1 : installmentDates) {

            InstallmentPrincipalAndInterest em = principalWithInterestInstallments.get(installmentIndex);

            LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(this, getCustomer(), installmentDate1
                    .getInstallmentId(), new java.sql.Date(installmentDate1.getInstallmentDueDate().getTime()),
                    PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());

            for (FeeInstallment feeInstallment : feeInstallments) {
                if (feeInstallment.getInstallmentId().equals(installmentDate1.getInstallmentId())
                        && !feeInstallment.getAccountFeesEntity().getFees().isTimeOfDisbursement()) {

                    LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(loanScheduleEntity,
                            feeInstallment.getAccountFeesEntity().getFees(), feeInstallment.getAccountFeesEntity(),
                            feeInstallment.getAccountFee());
                    loanScheduleEntity.addAccountFeesAction(loanFeeScheduleEntity);

                } else if (feeInstallment.getInstallmentId().equals(installmentDate1.getInstallmentId())
                        && isInterestDeductedAtDisbursement()
                        && feeInstallment.getAccountFeesEntity().getFees().isTimeOfDisbursement()) {

                    // FIXME - keithw - isInterestDeductedAtDisbursement is not relevant but one integration test fails
                    // when this is removed. leaving in but test is most likely wrong. LoanBOIntegrationTest.testRemoveLoanDisbursalFee

                    LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity(loanScheduleEntity,
                            feeInstallment.getAccountFeesEntity().getFees(), feeInstallment.getAccountFeesEntity(),
                            feeInstallment.getAccountFee());
                    loanScheduleEntity.addAccountFeesAction(loanFeeScheduleEntity);

                }
            }

            unroundedLoanSchedules.add(loanScheduleEntity);
            installmentIndex++;
        }

        return unroundedLoanSchedules;
    }

    /**
     * for V1.1, assume that apply-rounding is applied only to "fresh" loans that have no prior payments, and then only
     * when rounding is needed -- when applying or removing charges that carry greater precision than the rounding
     * precision specified for applicable installments. TODO: correct this after establishing business rules for what
     * installments must be re-rounded when changing the loan mid-stream.
     */
    private List<AccountActionDateEntity> getInstallmentsToRound() {
        List<AccountActionDateEntity> installments = this.getAllInstallments();
        Collections.sort(installments);
        return installments;
    }

    public boolean isInterestWaived() {
        return loanOffering.isInterestWaived();
    }

	public void updateInstallmentSchedule(List<RepaymentScheduleInstallment> installments) {

		Map<Integer, LoanScheduleEntity> loanScheduleEntityLookUp = getLoanScheduleEntityMap();
		for (RepaymentScheduleInstallment installment : installments) {
			LoanScheduleEntity loanScheduleEntity = loanScheduleEntityLookUp
					.get(installment.getInstallment());
			loanScheduleEntity.setPrincipal(installment.getPrincipal());
			loanScheduleEntity.setInterest(installment.getInterest());
			loanScheduleEntity.setActionDate(new java.sql.Date(installment
					.getDueDateValue().getTime()));
		}

		updateLoanSummary();

	}

    public boolean isVariableInstallmentsAllowed() {
        return loanOffering.isVariableInstallmentsAllowed();
    }

    public boolean isFixedRepaymentSchedule() {
        return loanOffering.isFixedRepaymentSchedule();
    }

    public boolean paymentsAllowed() {
        AccountState state = getState();
        return (state.equals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING) ||
                state.equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING) ||
                state.equals(AccountState.CUSTOMER_ACCOUNT_ACTIVE));
    }

    public boolean paymentsNotAllowed() {
        return !paymentsAllowed();
    }

    public void recordSummaryAndPerfHistory(boolean paid, PaymentAllocation paymentAllocation) {
        loanSummary.updatePaymentDetails(paymentAllocation);
        if (paid) {
            performanceHistory.incrementPayments();
        }
    }

    /**
     * @deprecated pull out validation into validation classs
     */
    @Deprecated
    static boolean isDisbursementDateAfterCustomerActivationDate(final Date disbursementDate, final CustomerBO customer) {
        return DateUtils.dateFallsOnOrBeforeDate(customer.getCustomerActivationDate(), disbursementDate);
    }

    /**
     * @deprecated pull out validation into validation classs
     */
    @Deprecated
    static boolean isDisbursementDateAfterProductStartDate(final Date disbursementDate,
            final LoanOfferingBO loanOffering) {
        return DateUtils.dateFallsOnOrBeforeDate(loanOffering.getStartDate(), disbursementDate);
    }

    /**
     * @deprecated pull out validation into validation classs
     */
    @Deprecated
    static boolean isDisbursementDateLessThanCurrentDate(final Date disbursementDate) {
        if (DateUtils.dateFallsBeforeDate(disbursementDate, DateUtils.getCurrentDateWithoutTimeStamp())) {
            return true;
        }
        return false;
    }

    /**
     * @deprecated pull out validation into validation classs
     */
    @Deprecated
    static boolean isDibursementDateValidForRedoLoan(final LoanOfferingBO loanOffering, final CustomerBO customer,
            final Date disbursementDate) {
        return isDisbursementDateAfterCustomerActivationDate(disbursementDate, customer)
                && isDisbursementDateAfterProductStartDate(disbursementDate, loanOffering);
    }

    /*
     * Existing loan accounts before Mifos 1.1 release will have loan_summary.raw_amount_total = 0
     */
    public boolean isLegacyLoan() {

        if (loanSummary == null || loanSummary.getRawAmountTotal() == null) {
            return true;
        }
        Money defaultAmount = new Money(getCurrency(), "0");
        Money rawAmountTotal = loanSummary.getRawAmountTotal();
        return rawAmountTotal.equals(defaultAmount);
    }

    /*
     * 999 Account = interest paid + fees paid - (raw interest + raw fees) Notes: loan accounts before Mifos 1.1 release
     * will have their 999 accounts calculated the old way which is the difference between the last payment rounded
     * amount and original amount
     */
    public Money calculate999Account(final boolean lastPayment) {
        Money account999 = new Money(getCurrency(), "0");
        if (isLegacyLoan()) {
            return account999;
        }
        Money origInterestAndFees = loanSummary.getOriginalFees().add(loanSummary.getOriginalInterest());
        Money paidInterestAndFees = loanSummary.getFeesPaid().add(loanSummary.getInterestPaid());
        if (lastPayment) {
            assert origInterestAndFees.equals(paidInterestAndFees);
        }
        Money rawAmountTotal = loanSummary.getRawAmountTotal();
        account999 = origInterestAndFees.subtract(rawAmountTotal);
        return account999;
    }

    public Money getRawAmountTotal() {
        return rawAmountTotal;
    }

    public void setRawAmountTotal(final Money rawAmountTotal) {
        this.rawAmountTotal = rawAmountTotal;
    }

    public Short getIntrestAtDisbursement() {
        return this.intrestAtDisbursement;
    }

    public void setIntrestAtDisbursement(final Short intrestAtDisbursement) {
        this.intrestAtDisbursement = intrestAtDisbursement;
    }

    public Money getFeesDueAtDisbursement() {

        Money totalFeesDueAtDisbursement = new Money(getCurrency());
        if (getAccountFees() != null && getAccountFees().size() > 0) {
            for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
                if (accountFeesEntity.isTimeOfDisbursement()) {
                    totalFeesDueAtDisbursement = totalFeesDueAtDisbursement
                            .add(accountFeesEntity.getAccountFeeAmount());
                }
            }
        }
        return totalFeesDueAtDisbursement;
    }

    /*
     * In order to do audit logging, we need to get the name of the PaymentTypeEntity. A new instance constructed with
     * the paymentTypeId is not good enough for this, we need to get the lookup value loaded so that we can resolve the
     * name of the PaymentTypeEntity.
     */
    /**
     * @deprecated - is using persistence
     */
    @Deprecated
    public PaymentTypeEntity getPaymentTypeEntity(final short paymentTypeId) {
        return getlegacyLoanDao().loadPersistentObject(PaymentTypeEntity.class, paymentTypeId);
    }

    /*
     * A loan account knows its currency from its associated loan product
     *
     * @see org.mifos.accounts.business.AccountBO#getCurrency()
     */
    @Override
    public MifosCurrency getCurrency() {
        return getLoanOffering().getCurrency();
    }

    @Override
    public MeetingBO getMeetingForAccount() {
        return getLoanMeeting();
    }

    private void handleArrearsAging() throws AccountException {
        if (this.loanArrearsAgingEntity == null) {
            this.loanArrearsAgingEntity = new LoanArrearsAgingEntity(this, getDaysInArrears(), getLoanSummary()
                    .getPrincipalDue(), getLoanSummary().getInterestDue(), getTotalPrincipalAmountInArrears(),
                    getTotalInterestAmountInArrears());
        } else {
            this.loanArrearsAgingEntity.update(getDaysInArrears(), getLoanSummary().getPrincipalDue(), getLoanSummary()
                    .getInterestDue(), getTotalPrincipalAmountInArrears(), getTotalInterestAmountInArrears(),
                    getCustomer());
        }

        try {
            getlegacyLoanDao().createOrUpdate(this);
        } catch (PersistenceException pe) {
            throw new AccountException(pe);
        }
    }

    public List<RepaymentScheduleInstallment> toRepaymentScheduleDto(Locale userLocale) {

        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();

        for (AccountActionDateEntity actionDate : this.getAccountActionDates()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) actionDate;
            installments.add(loanSchedule.toDto());
        }

        Collections.sort(installments, new Comparator<RepaymentScheduleInstallment>() {
            @Override
			public int compare(final RepaymentScheduleInstallment act1, final RepaymentScheduleInstallment act2) {
                return act1.getInstallment().compareTo(act2.getInstallment());
            }
        });

        return installments;
    }

    public boolean isDecliningBalanceInterestRecalculation() {
        return loanOffering.isDecliningBalanceInterestRecalculation();
    }
    
    public boolean isDecliningBalanceEqualPrincipleCalculation() {
         return loanOffering.isDecliningBalanceEqualPrinciplecalculation();
    }

    public LoanAccountDetailDto toDto() {
        PrdOfferingDto productDetails = this.loanOffering.toDto();
        return new LoanAccountDetailDto(productDetails, this.globalAccountNum);
    }

    public void markAsCreatedWithBackdatedPayments() {
        this.redone = true;
    }

    public void approve(PersonnelBO createdBy, String comment, LocalDate approvalDate) {
        AccountStateEntity approvedState = new AccountStateEntity(AccountState.LOAN_APPROVED);
        AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(this.getAccountState()
                , approvedState, createdBy, this);


        AccountNotesEntity accountNotesEntity = new AccountNotesEntity(approvalDate.toDateMidnight().toDate(), comment, createdBy, this);

        this.addAccountStatusChangeHistory(historyEntity);
        this.setAccountState(approvedState);
        this.addAccountNotes(accountNotesEntity);
    }

    public void disburse(PersonnelBO createdBy, AccountPaymentEntity disbursalPayment) throws AccountException {
        Date transactionDate = disbursalPayment.getPaymentDate();

        addLoanActivity(buildLoanActivity(getLoanAmount(), createdBy, AccountConstants.LOAN_DISBURSAL, transactionDate));

        final AccountStateEntity newState = new AccountStateEntity(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
        addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(getAccountState(), newState,
                createdBy, this));
        setAccountState(newState);

        if (getPerformanceHistory() != null) {
            getPerformanceHistory().setLoanMaturityDate(getLastInstallmentAccountAction().getActionDate());
        }

        // build up account payment related data
        AccountPaymentEntity accountPayment = null;

        // Disbursal process has to create its own accountPayment taking into account any disbursement fees
        Money feeAmountAtDisbursement = getFeesDueAtDisbursement();
        accountPayment = new AccountPaymentEntity(this, getLoanAmount().subtract(feeAmountAtDisbursement),
                disbursalPayment.getReceiptNumber(), disbursalPayment.getReceiptDate(), getPaymentTypeEntity(disbursalPayment.getPaymentType().getId()), transactionDate);
        accountPayment.setCreatedByUser(createdBy);

        if (feeAmountAtDisbursement.isGreaterThanZero()) {
            processFeesAtDisbursement(accountPayment, feeAmountAtDisbursement);
        }

        // create trxn entry for disbursal
        final LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPayment,
                AccountActionTypes.DISBURSAL, Short.valueOf("0"), transactionDate, createdBy, transactionDate,
                getLoanAmount(), "-", null, getLoanAmount(), new Money(getCurrency()), new Money(getCurrency()),
                new Money(getCurrency()), new Money(getCurrency()), null, null);

        accountPayment.addAccountTrxn(loanTrxnDetailEntity);
        addAccountPayment(accountPayment);
        buildFinancialEntries(accountPayment.getAccountTrxns());
    }

    public void updateCustomer(CustomerBO customer) {
        this.customer = customer;
    }


/*
 * Mifos-4948 specific code
 */
    public void applyMifos4948FixPayment(Money totalMissedPayment) throws AccountException {

    	String comment = "MIFOS-4948 - Loan: " + this.getGlobalAccountNum() + " - Adding payment: " + totalMissedPayment ;

        try {
            PersonnelBO currentUser = legacyPersonnelDao.getPersonnel((short) 1);
            Date transactionDate = new DateTimeService().getCurrentJavaDateTime();
            AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(this, totalMissedPayment, null,
                    null, getPaymentTypeEntity(Short.valueOf("1")), transactionDate);
            addAccountPayment(accountPaymentEntity);
            accountPaymentEntity.setComment(comment);

            AccountActionTypes accountActionTypes;
            String accountConstants;
            if (this.getAccountState().getId().equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue()))
            {
            	accountActionTypes = AccountActionTypes.WRITEOFF;
            	accountConstants = AccountConstants.LOAN_WRITTEN_OFF;
            }
            else
            {
            	accountActionTypes = AccountActionTypes.LOAN_RESCHEDULED;
            	accountConstants = AccountConstants.LOAN_RESCHEDULED;
            }
            makeWriteOffOrReschedulePaymentForMifos4948(accountPaymentEntity, accountConstants,
            		accountActionTypes, currentUser);
            addLoanActivity(buildLoanActivity(accountPaymentEntity.getAccountTrxns(), currentUser,
            		accountConstants, transactionDate));
            buildFinancialEntries(accountPaymentEntity.getAccountTrxns());
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }
    private void makeWriteOffOrReschedulePaymentForMifos4948(final AccountPaymentEntity accountPaymentEntity,
            final String comments, final AccountActionTypes accountActionTypes, final PersonnelBO currentUser) {
        for (AccountActionDateEntity accountActionDateEntity : this.getAccountActionDates()) {
            LoanScheduleEntity loanSchedule = (LoanScheduleEntity) accountActionDateEntity;
        	if (loanSchedule.getPaymentStatus().equals((short)0) )
        	{
	            Money principal = loanSchedule.getPrincipalDue();
	            Money interest = loanSchedule.getInterestDue();
	            Money fees = loanSchedule.getTotalFeeDueWithMiscFeeDue();
	            Money penalty = loanSchedule.getPenaltyDue();

	            LoanTrxnDetailEntity loanTrxnDetailEntity = new LoanTrxnDetailEntity(accountPaymentEntity, accountActionTypes,
                        loanSchedule.getInstallmentId(), loanSchedule.getActionDate(), currentUser,
                        accountPaymentEntity.getPaymentDate(), principal, comments, null, principal, new Money(getCurrency()),
	                    new Money(getCurrency()), new Money(getCurrency()), new Money(getCurrency()), null, null);

	            accountPaymentEntity.addAccountTrxn(loanTrxnDetailEntity);
	            loanSchedule.makeEarlyRepaymentEntries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST,
                        loanSchedule.getInterestDue(), accountPaymentEntity.getPaymentDate());
	            loanSummary.decreaseBy(null, interest, penalty, fees);
	            updatePaymentDetails(accountActionTypes, principal, null, null, null);
        	}
        }

    }
    
    public List<LoanScheduleEntity> getDetailsOfLateInstallmentsPeriod(final LocalDate after, final LocalDate before) {
        List<LoanScheduleEntity> lateInstallments = new ArrayList<LoanScheduleEntity>();

        Set<AccountActionDateEntity> accountActionDates = getAccountActionDates();
        
        if (accountActionDates!= null && !accountActionDates.isEmpty()) {
            for (AccountActionDateEntity accountAction : accountActionDates) {
                if (!accountAction.isPaid() && accountAction.isAfter(after) && accountAction.isBefore(before)) {
                    lateInstallments.add((LoanScheduleEntity)accountAction);
                }
            }
        }
        return lateInstallments;
    }
    
    public Money getTotalPenalty(final MifosCurrency currency, final Short penaltyId) {
        Money cash = new Money(currency);
        
        for(LoanScheduleEntity installment : getLoanScheduleEntities()) {
            for(LoanPenaltyScheduleEntity entity : installment.getLoanPenaltyScheduleEntities()) {
                if(entity.getPenalty().getPenaltyId().equals(penaltyId)) {
                    cash = cash.add(entity.getPenaltyAmount());
                }
            }
        }
        
        return cash;
    }

	public void rescheduleRemainingUnpaidInstallments(LoanSchedule loanSchedule, LocalDate asOf) {
		
		int index = 0;
		for (AccountActionDateEntity entity : this.getDetailsOfUnpaidInstallmentsOn(asOf)) {
			LoanScheduleEntity installment = (LoanScheduleEntity) entity;
			
			LoanScheduleEntity recalculatedInstallment = loanSchedule.getRoundedLoanSchedules().get(index);
			index++;
			
			installment.setInterest(recalculatedInstallment.getInterest());
			installment.setPrincipal(recalculatedInstallment.getPrincipal());
		}
		
		this.loanSummary.setOriginalInterest(getTotalInterestToBePaid());
	}

    public void removePenalty(Short penaltyId, Short personnelId) throws AccountException {
        List<Short> installmentIds = getApplicableInstallmentIdsForRemovePenalties();
        Money totalPenaltyAmount = Money.zero();
        
        if (isPenaltyActive(penaltyId)) {
            PenaltyBO penalty = getAccountPenaltyObject(penaltyId);

            if (!installmentIds.isEmpty()) {
                if (havePaymentsBeenMade() && penalty.doesPenaltyInvolveFractionalAmounts()) {
                    throw new AccountException(AccountExceptionConstants.CANT_REMOVE_PENALTY_EXCEPTION);
                }

                totalPenaltyAmount = totalPenaltyAmount.add(removePenaltyFromLoanScheduleEntity(installmentIds, penaltyId));
                updateTotalPenaltyAmount(totalPenaltyAmount);
            }
            
            updateAccountPenaltiesEntity(penaltyId);
            String description = penalty.getPenaltyName() + " " + AccountConstants.PENALTIES_REMOVED;
            updateAccountActivity(null, null, totalPenaltyAmount, null, personnelId, description);
            
            try {
                ApplicationContextProvider.getBean(LegacyAccountDao.class).createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }
    
    private List<Short> getApplicableInstallmentIdsForRemovePenalties() {
        List<Short> installmentIdList = new ArrayList<Short>();
        for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForLateInstallments()) {
            installmentIdList.add(accountActionDateEntity.getInstallmentId());
        }
        
        AccountActionDateEntity accountActionDateEntity = getDetailsOfNextInstallment();
        if (accountActionDateEntity != null) {
            installmentIdList.add(accountActionDateEntity.getInstallmentId());
        }

        return installmentIdList;
    }
    
    private void updateAccountPenaltiesEntity(final Short penaltyId) {
        AccountPenaltiesEntity accountPenalties = getAccountPenalty(penaltyId);
        if (accountPenalties != null) {
            accountPenalties.changePenaltyStatus(PenaltyStatus.INACTIVE, getDateTimeService().getCurrentJavaDateTime());
            accountPenalties.setLastAppliedDate(null);
        }
    }
    
    private List<AccountActionDateEntity> getApplicableIdsForLateInstallments() {
        List<AccountActionDateEntity> lateActionDateList = new ArrayList<AccountActionDateEntity>();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null) {
            for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
                if (!accountActionDate.isPaid() && accountActionDate.getInstallmentId() < nextInstallment.getInstallmentId()) {
                    lateActionDateList.add(accountActionDate);
                }
            }
        } else {
            lateActionDateList.addAll(getAccountActionDates());
        }
        return lateActionDateList;
    }

    @SuppressWarnings("unused")
    @Deprecated
    private void applyPaymentToMemberAccounts(PaymentData paymentData) throws AccountException {
        Money totalPaymentAmount = paymentData.getTotalAmount();
        for (LoanBO memberAccount : this.memberAccounts) {
            BigDecimal factor = memberAccount.calcFactorOfEntireLoan();
            Money memberPaymentAmount = totalPaymentAmount.divide(factor);
            memberPaymentAmount = MoneyUtils.currencyRound(memberPaymentAmount);

            PaymentData memberPayment;
            
            if(getState().getValue()==(short)6) {
                memberPayment = new PaymentData(memberAccount.getTotalRepayableAmount(), paymentData.getPersonnel(),
                        paymentData.getPaymentTypeId(), paymentData.getTransactionDate());
            }
            else {
                memberPayment = new PaymentData(memberPaymentAmount, paymentData.getPersonnel(),
                        paymentData.getPaymentTypeId(), paymentData.getTransactionDate());
            }
            
            memberAccount.applyPayment(memberPayment);
        }
    }
    
    /** Makes payments for member accounts that will equalize number of paid installments to installmentsPaid parameter
     * Example: member accounts have currently paid 2.5 installments,
     * after calling this method with installmentsPaid = 5.3,
     * it will make payments for member accounts that pays 2.8 installments,
     * so after all member accounts will have 5,3 installments paid. 
     * 
     * @param installmentsPaid - number of installments that are currently paid on parent account.
     * 
     */
    private void applyPaymentToMemberAccounts(PaymentData paymentData, BigDecimal installmentsPaid) throws AccountException {
        
        for (LoanBO memberAccount : getMemberAccounts()) {
            if(validateNoOfInstallments(memberAccount)) {
                BigDecimal memberPaid = memberAccount.findNumberOfPaidInstallments();
                List<LoanScheduleEntity> memberInstallments = memberAccount.getLoanInstallments();
                Money memberPaymentAmount = new Money(getCurrency());
                int currentPayment;
                
                //Full payments for installments that are expected to be paid fully
                for(currentPayment = memberPaid.intValue(); currentPayment < installmentsPaid.intValue() ; currentPayment++) {
                    memberPaymentAmount = memberPaymentAmount.add(memberInstallments.get(currentPayment).getAmountToBePaidToGetExpectedProportion(BigDecimal.ONE));
                }
                
                BigDecimal afterComa = installmentsPaid.subtract(new BigDecimal(installmentsPaid.intValue()));
                //If there is fraction in number of installments to be paid
                if(afterComa.compareTo(BigDecimal.ZERO)==1) {
                    memberPaymentAmount = memberPaymentAmount.add(memberInstallments.get(currentPayment).getAmountToBePaidToGetExpectedProportion(afterComa));
                }
                
                //It prevents member account to be closed before parent due to small last payments in parent account. Member accounts will remain in minimalPayment unpaid until last payment for parent account will be submitted
                if(getState().compareTo(AccountState.LOAN_CLOSED_OBLIGATIONS_MET)!=0 && memberPaymentAmount.subtract((memberAccount.getTotalRepayableAmount())).isTinyAmount()) {
                    Double minimalPayment = Math.pow(10.0, (double)-AccountingRules.getDigitsAfterDecimal());
                    memberPaymentAmount = memberPaymentAmount.subtract(new Money(getCurrency(), minimalPayment));
                }
                
                memberPaymentAmount = MoneyUtils.currencyRound(memberPaymentAmount);
                       
                PaymentData memberPayment = new PaymentData(memberPaymentAmount, paymentData.getPersonnel(),
                        paymentData.getPaymentTypeId(), paymentData.getTransactionDate());
                
                if(!memberPaymentAmount.isTinyAmount() && !memberPaymentAmount.isLessThanZero()) {
                    memberAccount.applyPayment(memberPayment);
                }
            }
        }
    }
    
    private BigDecimal findNumberOfPaidInstallments() {
        List<LoanScheduleEntity> loanInstallments = getLoanInstallments();
        
        BigDecimal paidInstallments = new BigDecimal(0);
        
        for(LoanScheduleEntity installment : loanInstallments) {
            paidInstallments = paidInstallments.add(installment.getPaidProportion());
        }
        
        return paidInstallments;
    }
    
    private List<LoanScheduleEntity> getLoanInstallments() {     
        List<AccountActionDateEntity> installments = getAllInstallments();
        List<LoanScheduleEntity> schedule = new ArrayList<LoanScheduleEntity>();
       
        for (AccountActionDateEntity accountActionDateEntity : installments) {
          schedule.add((LoanScheduleEntity) accountActionDateEntity);
        }
        return schedule;
    }

    public boolean hasMemberAccounts() {
        return this.memberAccounts != null && !this.memberAccounts.isEmpty();
    }

    public BigDecimal calcFactorOfEntireLoan() {
        BigDecimal percentage = BigDecimal.ONE;
        if (this.parentAccount != null) {
            percentage = this.parentAccount.getLoanAmount().divide(this.loanAmount);
        }
        return percentage;
    }

    @Override
    public void adjustLastPayment(final String adjustmentComment, PersonnelBO loggedInUser) throws AccountException {
        if (isAdjustPossibleOnLastTrxn()) {
            logger.debug("Adjustment is possible hence attempting to adjust.");
            adjustPayment(getLastPmntToBeAdjusted(), loggedInUser, adjustmentComment);
            if (hasMemberAccounts()) {
                for (LoanBO memberAccount : this.memberAccounts) {
                    memberAccount.setUserContext(this.userContext);
                    memberAccount.adjustLastPayment(adjustmentComment, loggedInUser);
                }
                //MIFOS-5742: equalizing payment made to solve the problem with adjusting very small payment on GLIM account
                PaymentData equalizingPaymentData = new PaymentData(getLastPmntToBeAdjusted().getAmount(), loggedInUser, getLastPmntToBeAdjusted().getPaymentType().getId(), getLastPmntToBeAdjusted().getPaymentDate());
                BigDecimal installmentsPaid = findNumberOfPaidInstallments();
                applyPaymentToMemberAccounts(equalizingPaymentData, installmentsPaid);
            }
        } else if (this.parentAccount == null){ //MIFOS-5694: if member account has no payments it could mean that payment was made before 2.4.0, remove this condition when MIFOS-5692 is done 
            throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
        }
    }

    public LoanBO findMemberByGlobalNum(String globalAccNum) {
        LoanBO result = null;
        if (hasMemberAccounts()) {
            for (LoanBO member : this.memberAccounts) {
                if (member.getGlobalAccountNum().equals(globalAccNum)) {
                    result = member;
                    break;
                }
            }
        }
        return result;
    }

    public LoanBO findMemberById(Integer memberId) {
        LoanBO result = null;
        if (hasMemberAccounts()) {
            for (LoanBO member : this.memberAccounts) {
                if (member.getAccountId().equals(memberId)) {
                    result = member;
                    break;
                }
            }
        }
        return result;
    }
    
    
    public boolean isIndividualLoan(){
        return this.parentAccount != null && this.accountType.getAccountTypeId().equals(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT.getValue());
    }
    
    public boolean validateNoOfInstallments(LoanBO memberAccount) {
        return memberAccount.getNoOfInstallments()==getNoOfInstallments();
    }

    public void applyMifos5722Fix() throws AccountException, PersistenceException{
        if(!validateNoOfInstallments(getMemberAccounts().iterator().next()))
            return;

        PersonnelBO personnel = legacyPersonnelDao.getPersonnel((short) 1);
        UserContext userContext = new UserContext();
        userContext.setId(PersonnelConstants.SYSTEM_USER);
        String comment = "Mifos-5722";
            
        //clear accounts from previous payments
        for(LoanBO memberAccount : getMemberAccounts()) {
            memberAccount.setUserContext(userContext);
            memberAccount.setAccountState(getAccountState());
            for(AccountPaymentEntity payment : memberAccount.getAccountPayments()) {
                memberAccount.adjustPayment(payment, personnel, comment);
            }
        }

        List<LoanScheduleEntity> parentInstallments = getLoanInstallments();
        for(LoanBO memberAccount : getMemberAccounts()) {
            List<LoanScheduleEntity> memberInstallments = memberAccount.getLoanInstallments();
            for(int i = 0 ; i < parentInstallments.size() ; i++) {
                LoanScheduleEntity parentInstallment = parentInstallments.get(i);
                LoanScheduleEntity memberInstallment = memberInstallments.get(i);
                
                //remove fees and penalties from installment
                memberInstallment.removeAllFees();
                memberInstallment.removeAllPenalties();
                
                equalizeMiscFeesAndPenaltiesOnInstallments(parentInstallment, memberInstallment, memberAccount.calcFactorOfEntireLoan());
            }

            //remove fees and penalties from account
            while(memberAccount.getAccountFeesIncludingInactiveFees().iterator().hasNext()) {
                AccountFeesEntity fee = memberAccount.getAccountFeesIncludingInactiveFees().iterator().next();
                memberAccount.getAccountFeesIncludingInactiveFees().remove(fee);
            }

            while(memberAccount.getAccountPenaltiesIncludingInactivePenalties().iterator().hasNext()) {
                AccountPenaltiesEntity penalty = memberAccount.getAccountPenaltiesIncludingInactivePenalties().iterator().next();
                memberAccount.getAccountPenaltiesIncludingInactivePenalties().remove(penalty);
            }
        }

        //apply fees and penalties
        Set<AccountFeesEntity> fees = getAccountFees();
        Set<AccountPenaltiesEntity> penalties = getAccountPenalties();
        for(LoanBO memberAccount : getMemberAccounts()){
            for(AccountFeesEntity fee : fees) {
                try {
                    if(fee.getFees().getFeeType()== RateAmountFlag.RATE) {
                        RateFeeBO fbo = getFeeDao().findRateFeeById(fee.getFees().getFeeId());
                        if(fbo!=null) {
                            memberAccount.applyChargeMifos5722(fee.getFees().getFeeId(), fbo.getRate());
                        }
                    } else {
                        memberAccount.applyChargeMifos5722(fee.getFees().getFeeId(), fee.getFeeAmount()/memberAccount.calcFactorOfEntireLoan().doubleValue());
                    }
                } catch (AccountException e) {
                    e.printStackTrace();
                }
                for(AccountPenaltiesEntity penalty : penalties) {
                    memberAccount.addAccountPenalty(penalty);
                }
            }
        }
        applyAllPenaltiesToMemberAccounts();
        
        applyRoundingOnInstallments(getLoanInstallments(), getLoanInstallments());

        List<AccountPaymentEntity> parentPayments = getAccountPayments();

        BigDecimal currentAmount = BigDecimal.ZERO;
        for(int i = parentPayments.size()-2 ; i >= 0 ; i--) {
            AccountPaymentEntity currentPayment = parentPayments.get(i);
            currentAmount = currentAmount.add(currentPayment.getAmount().getAmount());
            BigDecimal currentProportion = findNumberOfInstallmentsPaidByAmount(currentAmount);
            PaymentData paymentData = new PaymentData(currentPayment.getAmount(), personnel,currentPayment.getPaymentType().getId(),currentPayment.getPaymentDate());
            applyPaymentToMemberAccounts(paymentData, currentProportion);
        }      
    }

    private BigDecimal findNumberOfInstallmentsPaidByAmount(BigDecimal amount) {
        BigDecimal number = BigDecimal.ZERO;
        for(LoanScheduleEntity installment : getLoanInstallments()) {
            if(amount.compareTo(installment.getTotalAmountOfInstallment().getAmount())>0) {
                amount = amount.subtract(installment.getTotalAmountOfInstallment().getAmount());
                number = number.add(BigDecimal.ONE);
            }
            else {
                number = number.add(installment.getProportionPaidBy(amount));
                break;
            }
        }

        return number;
    }

    private void applyAllPenaltiesToMemberAccounts() {
        List<LoanScheduleEntity> parentInstallments = getLoanInstallments();
        for(LoanBO memberAccount : getMemberAccounts()) {
            List<LoanScheduleEntity> memberInstallments = memberAccount.getLoanInstallments();
            for(int i = 0 ; i < parentInstallments.size() ; i++) {
                LoanScheduleEntity parentInstallment = parentInstallments.get(i);
                LoanScheduleEntity memberInstallment = memberInstallments.get(i);
                for(LoanPenaltyScheduleEntity penalty : parentInstallment.getLoanPenaltyScheduleEntities()) {
                    memberAccount.applyPenalty(penalty.getPenaltyAmount().divide(memberAccount.calcFactorOfEntireLoan()), memberInstallment.getInstallmentId(),penalty.getAccountPenalty(), DateUtils.getCurrentJavaDateTime());
                }
            }
        }
    }

    public boolean needsMifos5722Repair() {
        LoanBO memberAccount = getMemberAccounts().iterator().next();
        boolean result = (getAccountPayments().size() > memberAccount.getAccountPayments().size()+1)
                || (getAccountFees().size() != memberAccount.getAccountFees().size())
                || (getAccountPenalties().size() != memberAccount.getAccountPenalties().size());
        return result;
    }

    public void equalizeMiscFeesAndPenaltiesOnInstallments(LoanScheduleEntity parentInstallment, LoanScheduleEntity memberInstallment, BigDecimal factor) {
        Money miscFee = MoneyUtils.currencyRound(parentInstallment.getMiscFee().divide(factor));
        memberInstallment.setMiscFee(miscFee);
        Money miscPenalty = MoneyUtils.currencyRound(parentInstallment.getMiscPenalty().divide(factor));
        memberInstallment.setMiscPenalty(miscPenalty);
    }

    public void applyChargeMifos5722(final Short feeId, final Double charge) throws AccountException {
        List<AccountActionDateEntity> dueInstallments = getAllInstallments();

        FeeBO fee = getFeeDao().findById(feeId);     

        if (fee.getFeeFrequency().getFeePayment() != null) {
            applyOneTimeFee(fee, charge, dueInstallments.get(0));
        } else {
            applyPeriodicFee(fee, charge, dueInstallments);
        }
    } 
      
    public void applyMifos5763Fix() throws AccountException {
        MeetingBO meetingBO = getLoanMeeting();
        boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        
        for(LoanBO memberAccount : getMemberAccounts()) {
            memberAccount.setNoOfInstallments(getNoOfInstallments());
            memberAccount.setDisbursementDate(getDisbursementDate());
            memberAccount.setLoanMeeting(meetingBO);
            memberAccount.regeneratePaymentSchedule(isRepaymentIndepOfMeetingEnabled, meetingBO);
            
            memberAccount.update();
            
            for(int i = 0 ; i < getNoOfInstallments() ; i++) {
                memberAccount.getAccountActionDatesSortedByInstallmentId().get(i).setActionDate(getAccountActionDatesSortedByInstallmentId().get(i).getActionDate());
            }
        }
        
    }
}
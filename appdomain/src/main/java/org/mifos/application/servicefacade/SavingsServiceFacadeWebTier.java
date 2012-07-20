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

package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.savings.business.SavingsAccountActivationDetail;
import org.mifos.accounts.savings.business.SavingsAccountTypeInspector;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.interest.CalendarPeriod;
import org.mifos.accounts.savings.interest.CalendarPeriodHelper;
import org.mifos.accounts.savings.interest.EndOfDayDetail;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodCalculator;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodDetail;
import org.mifos.accounts.savings.interest.InterestCalculationPeriodResult;
import org.mifos.accounts.savings.interest.InterestCalculator;
import org.mifos.accounts.savings.interest.InterestPostingPeriodResult;
import org.mifos.accounts.savings.interest.NonCompoundingInterestCalculator;
import org.mifos.accounts.savings.interest.SavingsInterestCalculatorFactory;
import org.mifos.accounts.savings.interest.SavingsInterestDetail;
import org.mifos.accounts.savings.interest.SavingsProductHistoricalInterestDetail;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.AccountSearchResultsDto;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.AccountingRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.AuditLogDto;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.DueOnDateDto;
import org.mifos.dto.domain.FundTransferDto;
import org.mifos.dto.domain.NoteSearchDto;
import org.mifos.dto.domain.OpeningBalanceSavingsAccount;
import org.mifos.dto.domain.PaymentDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsAccountClosureDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SavingsStatusChangeHistoryDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.AdjustableSavingsPaymentDto;
import org.mifos.dto.screen.DepositWithdrawalReferenceDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.NotesSearchResultsDto;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.dto.screen.SavingsAdjustmentReferenceDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
import org.mifos.dto.screen.SavingsTransactionHistoryDto;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class SavingsServiceFacadeWebTier implements SavingsServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(SavingsServiceFacadeWebTier.class);

    private final SavingsDao savingsDao;
    private final SavingsProductDao savingsProductDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final HolidayDao holidayDao;

    @Autowired
    private LegacyAcceptedPaymentTypeDao legacyAcceptedPaymentTypeDao;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Autowired
    private LegacyAccountDao legacyAcccountDao;

    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    private CalendarPeriodHelper interestCalculationIntervalHelper = new CalendarPeriodHelper();
    private SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory = new SavingsInterestScheduledEventFactory();

    @Autowired
    public SavingsServiceFacadeWebTier(SavingsDao savingsDao, SavingsProductDao savingsProductDao,
            PersonnelDao personnelDao, CustomerDao customerDao, HolidayDao holidayDao) {
        this.savingsDao = savingsDao;
        this.savingsProductDao = savingsProductDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
        this.holidayDao = holidayDao;
    }

    @Override
    public PaymentDto deposit(SavingsDepositDto savingsDeposit) {
        return deposit(savingsDeposit, false);
    }

    @Override
    public PaymentDto deposit(SavingsDepositDto savingsDeposit, boolean inTransaction) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsDeposit.getSavingsId());

        try {
            personnelDao.checkAccessPermission(userContext, savingsAccount.getOfficeId(), savingsAccount.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }

        savingsAccount.updateDetails(userContext);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(Short.valueOf((short) user.getUserId()));
        CustomerBO customer = this.customerDao.findCustomerById(savingsDeposit.getCustomerId().intValue());

        Money totalAmount = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsDeposit.getAmount()));
        PaymentData payment = PaymentData.createPaymentData(totalAmount, createdBy, savingsDeposit.getModeOfPayment()
                .shortValue(), savingsDeposit.getDateOfDeposit().toDateMidnight().toDate());
        if (savingsDeposit.getDateOfReceipt() != null) {
            payment.setReceiptDate(savingsDeposit.getDateOfReceipt().toDateMidnight().toDate());
        }
        payment.setReceiptNum(savingsDeposit.getReceiptId());
        payment.setCustomer(customer);

        for (AccountActionDateEntity installment : savingsAccount.getTotalInstallmentsDue(savingsDeposit
                .getCustomerId().intValue())) {
            AccountPaymentData accountPaymentData = new SavingsPaymentData(installment);
            payment.addAccountPaymentData(accountPaymentData);
        }

        try {
            if (!inTransaction) {
                this.transactionHelper.startTransaction();
                this.transactionHelper.beginAuditLoggingFor(savingsAccount);
            }

            AccountPaymentEntity newPaymentEntity = savingsAccount.applyPayment(payment);
            this.savingsDao.save(savingsAccount);

            Date lastIntPostDate = savingsAccount.getLastIntPostDate();
            if (lastIntPostDate != null &&
                    DateUtils.dateFallsOnOrBeforeDate(payment.getTransactionDate(), lastIntPostDate)) {
                this.recalculateInterestPostings(savingsAccount.getAccountId(),
                        new LocalDate(payment.getTransactionDate()));
            }

            //commit
            if (inTransaction) {
                this.transactionHelper.flushSession();
            } else {
                this.transactionHelper.commitTransaction();
            }
            return newPaymentEntity.toDto();
        } catch (AccountException e) {
            if (!inTransaction) {
                this.transactionHelper.rollbackTransaction();
            }
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            if (!inTransaction) {
                this.transactionHelper.closeSession();
            }
        }
    }

    public void setTransactionHelper(HibernateTransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public PaymentDto withdraw(SavingsWithdrawalDto savingsWithdrawal) {
        return withdraw(savingsWithdrawal, false);
    }

    @Override
    public PaymentDto withdraw(SavingsWithdrawalDto savingsWithdrawal, boolean inTransaction) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsWithdrawal.getSavingsId());

        try {
            personnelDao.checkAccessPermission(userContext, savingsAccount.getOfficeId(), savingsAccount.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }

        savingsAccount.updateDetails(userContext);
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(Short.valueOf((short) user.getUserId()));

        CustomerBO customer = this.customerDao.findCustomerById(savingsWithdrawal.getCustomerId().intValue());

        Money totalAmount = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsWithdrawal.getAmount()));
        PaymentData payment = PaymentData.createPaymentData(totalAmount, createdBy, savingsWithdrawal
                .getModeOfPayment().shortValue(), savingsWithdrawal.getDateOfWithdrawal().toDateMidnight().toDate());
        if (savingsWithdrawal.getDateOfReceipt() != null) {
            payment.setReceiptDate(savingsWithdrawal.getDateOfReceipt().toDateMidnight().toDate());
        }
        payment.setReceiptNum(savingsWithdrawal.getReceiptId());
        payment.setCustomer(customer);

        LocalDate dateOfWithdrawal = new LocalDate(payment.getTransactionDate());
        if (withdrawalMakesBalanceNegativeOnDate(savingsAccount, payment.getTotalAmount(), dateOfWithdrawal)) {
            throw new BusinessRuleException("errors.insufficentbalance",
                    new String[] { savingsAccount.getGlobalAccountNum() });
        }

        try {
            if (!inTransaction) {
                this.transactionHelper.startTransaction();
            }
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);

            AccountPaymentEntity paymentEntity = savingsAccount.withdraw(payment, false);
            this.savingsDao.save(savingsAccount);

            Date lastIntPostDate = savingsAccount.getLastIntPostDate();
            if (lastIntPostDate != null &&
                    DateUtils.dateFallsOnOrBeforeDate(payment.getTransactionDate(), lastIntPostDate)) {
                this.recalculateInterestPostings(savingsAccount.getAccountId(),
                        new LocalDate(payment.getTransactionDate()));
            }

            //commit
            if (inTransaction) {
                this.transactionHelper.flushSession();
            } else {
                this.transactionHelper.commitTransaction();
            }

            return paymentEntity.toDto();
        } catch (AccountException e) {
            if (!inTransaction) {
                this.transactionHelper.rollbackTransaction();
            }
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            if (!inTransaction) {
                this.transactionHelper.closeSession();
            }
        }
    }

    @Override
    public PaymentDto adjustTransaction(SavingsAdjustmentDto savingsAdjustment) {
        return adjustTransaction(savingsAdjustment, false);
    }

    @Override
    public PaymentDto adjustTransaction(SavingsAdjustmentDto savingsAdjustment, boolean inTransaction) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsAdjustment.getSavingsId());

        try {
            personnelDao.checkAccessPermission(userContext, savingsAccount.getOfficeId(), savingsAccount.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }

        PersonnelBO updatedBy = this.personnelDao.findPersonnelById(userContext.getId());
        savingsAccount.updateDetails(userContext);

        Money amountAdjustedTo = new Money(savingsAccount.getCurrency(), BigDecimal.valueOf(savingsAdjustment
                .getAdjustedAmount()));

        AccountPaymentEntity adjustedPayment = savingsAccount.findPaymentById(savingsAdjustment.getPaymentId());
        PaymentDto otherTransferPayment = adjustedPayment.getOtherTransferPaymentDto();
        Money originalAmount = adjustedPayment.getAmount();

        LocalDate dateOfWithdrawal = savingsAdjustment.getTrxnDate();
        if (adjustedPayment.isSavingsWithdrawal() && originalAmount.isLessThan(amountAdjustedTo)) {
            Money addedWithdrawalAmount = amountAdjustedTo.subtract(originalAmount);
            if (withdrawalMakesBalanceNegative(savingsAccount, addedWithdrawalAmount, dateOfWithdrawal)) {
                throw new BusinessRuleException("errors.insufficentbalance",
                        new String[] { savingsAccount.getGlobalAccountNum() });
            }
        } else if (adjustedPayment.isSavingsDeposit() && originalAmount.isGreaterThan(amountAdjustedTo)) {
            Money substractedAmount = originalAmount.subtract(amountAdjustedTo);
            if (withdrawalMakesBalanceNegative(savingsAccount, substractedAmount, dateOfWithdrawal)) {
                throw new BusinessRuleException("errors.insufficentbalance",
                        new String[] { savingsAccount.getGlobalAccountNum() });
            }
        }

        try {
            if (!inTransaction) {
                this.transactionHelper.startTransaction();
            }
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);

            AccountPaymentEntity newPayment = savingsAccount.adjustUserAction(amountAdjustedTo, savingsAdjustment.getNote(),
                    savingsAdjustment.getTrxnDate(), updatedBy, savingsAdjustment.getPaymentId());
            recalculateInterestPostings(savingsAccount.getAccountId(), new LocalDate(adjustedPayment.getPaymentDate()));

            if (hasAccountNegativeBalance(savingsAccount)) {
                throw new BusinessRuleException("errors.insufficentbalance",
                        new String[] { savingsAccount.getGlobalAccountNum() });
            }

            this.savingsDao.save(savingsAccount);

            // savings-savings transfer adjustment
            if (otherTransferPayment != null && otherTransferPayment.isSavingsPayment()) {
                this.transactionHelper.flushAndClearSession();
                SavingsBO otherSavingsAccount = this.savingsDao.findById(otherTransferPayment.getAccountId());
                otherSavingsAccount.updateDetails(userContext);

                AccountPaymentEntity newOtherTransferPayment = otherSavingsAccount.adjustUserAction(amountAdjustedTo, savingsAdjustment.getNote(),
                        savingsAdjustment.getTrxnDate(), updatedBy, otherTransferPayment.getPaymentId());
                recalculateInterestPostings(savingsAccount.getAccountId(), new LocalDate(adjustedPayment.getPaymentDate()));

                if (hasAccountNegativeBalance(otherSavingsAccount)) {
                    throw new BusinessRuleException("errors.insufficentbalance",
                            new String[] { savingsAccount.getGlobalAccountNum() });
                }

                transactionHelper.flushAndClearSession();
                if (newPayment != null) {
                    newPayment = savingsAccount.findPaymentById(newPayment.getPaymentId());
                    newPayment.setOtherTransferPayment(newOtherTransferPayment);
                    newOtherTransferPayment.setOtherTransferPayment(newPayment);
                    legacyAcccountDao.updatePayment(newPayment);
                }
                this.savingsDao.save(otherSavingsAccount);
            }

            if (!inTransaction) {
                this.transactionHelper.commitTransaction();
            }

            return (newPayment == null) ? null : newPayment.toDto();
        } catch (BusinessRuleException e) {
            if (!inTransaction) {
                this.transactionHelper.rollbackTransaction();
            }
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            if (!inTransaction) {
                this.transactionHelper.rollbackTransaction();
            }
            throw new MifosRuntimeException(e.getMessage(), e);
        } finally {
            if (!inTransaction) {
                this.transactionHelper.closeSession();
            }
        }
    }

    public void setInterestCalculationIntervalHelper(CalendarPeriodHelper interestCalculationIntervalHelper) {
        this.interestCalculationIntervalHelper = interestCalculationIntervalHelper;
    }

    public void setSavingsInterestScheduledEventFactory(
            SavingsInterestScheduledEventFactory savingsInterestScheduledEventFactory) {
        this.savingsInterestScheduledEventFactory = savingsInterestScheduledEventFactory;
    }

    private void recalculateInterestPostings(Integer accountId, LocalDate affectingPaymentDate) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());

        SavingsBO account = this.savingsDao.findById(accountId);

        Date paymentDate = affectingPaymentDate.toDateMidnight().toDate();

        int removedPostings = account.countInterestPostingsForRecalculation(paymentDate);

        if (removedPostings > 0) {
            this.savingsDao.prepareForInterestRecalculation(account, paymentDate);
            this.transactionHelper.flushSession();
            for (int i = 0; i < removedPostings; i++) {
                postInterestForAccount(accountId, userContext, createdBy, true);
            }
        }
    }

    /**
     * This method is responsible for posting interest for the last posting period only.
     *
     * It assumes that interest calculation and interest posting frequencies cannot change on savings product of savings
     * account. It assumes that the interest posting date is correct and valid with respect to the interest posting
     * frequency of the product/account.
     */
    @Override
    public void postInterestForLastPostingPeriod(LocalDate dateBatchJobIsScheduled) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());

        List<Integer> accountIds = this.savingsDao
                .retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(dateBatchJobIsScheduled);
        for (Integer savingsId : accountIds) {
            postInterestForAccount(savingsId, userContext, createdBy, false);
        }
    }

    private void postInterestForAccount(Integer savingsId, UserContext userContext, PersonnelBO createdBy, boolean inTransaction) {

        SavingsBO savingsAccount = this.savingsDao.findById(Long.valueOf(savingsId));
        savingsAccount.updateDetails(userContext);

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(
                savingsAccount.getCurrency(), Long.valueOf(savingsId));

        LocalDate interestPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());

        InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory
                .createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());
        LocalDate startOfPeriod = postingSchedule.findFirstDateOfPeriodForMatchingDate(interestPostingDate);
        CalendarPeriod lastInterestPostingPeriod = new CalendarPeriod(startOfPeriod, interestPostingDate);

        InterestPostingPeriodResult interestPostingPeriodResult = determinePostingPeriodResult(
                lastInterestPostingPeriod, savingsAccount, allEndOfDayDetailsForAccount);
        savingsAccount.postInterest(postingSchedule, interestPostingPeriodResult, createdBy);

        StringBuilder postingInfoMessage = new StringBuilder().append("account id: ")
                .append(savingsAccount.getAccountId()).append("posting interest: ").append(interestPostingPeriodResult);

        logger.info(postingInfoMessage.toString());

        try {
            if (!inTransaction) {
                this.transactionHelper.startTransaction();
            }

            this.savingsDao.save(savingsAccount);

            if (!inTransaction) {
                this.transactionHelper.commitTransaction();
            }
        } catch (Exception e) {
            if (!inTransaction) {
                this.transactionHelper.rollbackTransaction();
            }
            throw new BusinessRuleException(savingsAccount.getAccountId().toString(), e);
        } finally {
            if (!inTransaction) {
                this.transactionHelper.closeSession();
            }
        }
    }

    private Money calculateAccountBalanceOn(LocalDate date, List<EndOfDayDetail> allEndOfDayDetailsForAccount,
            MifosCurrency currency) {

        Money balance = Money.zero(currency);
        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            if (endOfDayDetail.getDate().isBefore(date)) {
                balance = balance.add(endOfDayDetail.getResultantAmountForDay());
            }
        }
        return balance;
    }

    private boolean withdrawalMakesBalanceNegative(SavingsBO savings, Money withdrawalAmount, LocalDate trxnDate) {
        boolean negativeBalance = false;
        List<EndOfDayDetail> allEndOfDayDetailsForAccount = this.savingsDao.retrieveAllEndOfDayDetailsFor(
                savings.getCurrency(), savings.getAccountId().longValue());
        Money balance = Money.zero(savings.getCurrency());
        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            balance = balance.add(endOfDayDetail.getResultantAmountForDay());
            if (endOfDayDetail.getDate().isEqual(trxnDate)) {
                balance = balance.subtract(withdrawalAmount);
            }

            if (balance.isLessThanZero()) {
                negativeBalance = true;
                break;
            }
        }
        return negativeBalance;
    }

    // TODO - move into InterestPostingPeriodCalculator
    private InterestPostingPeriodResult doCalculateInterestForPostingPeriod(CalendarPeriod interestPostingPeriod,
            Money startingBalanceForPeriod, List<SavingsProductHistoricalInterestDetail> historicalInterestDetails,
            List<EndOfDayDetail> allEndOfDayDetailsForAccount, SavingsInterestDetail interestDetail,
            InterestScheduledEvent interestCalculationEvent) {

        InterestPostingPeriodResult postingPeriodResult = new InterestPostingPeriodResult(interestPostingPeriod);
        Money runningBalance = startingBalanceForPeriod;

        if (!allEndOfDayDetailsForAccount.isEmpty()) {

            List<CalendarPeriod> interestCalculationPeriods = new ArrayList<CalendarPeriod>();

            // 1. determine all valid interest calculation periods that fall within this posting period and create a
            // interest calculation period calculator for each one (to handle possible different interest rates)
            LocalDate firstActivityDate = allEndOfDayDetailsForAccount.get(0).getDate();

            if (interestPostingPeriod.contains(firstActivityDate)) {
                interestCalculationPeriods = this.interestCalculationIntervalHelper.determineAllPossiblePeriods(
                        firstActivityDate, interestCalculationEvent, interestPostingPeriod.getEndDate());
            } else {
                interestCalculationPeriods = this.interestCalculationIntervalHelper.determineAllPossiblePeriods(
                        interestPostingPeriod.getStartDate(), interestCalculationEvent,
                        interestPostingPeriod.getEndDate());
            }

            for (CalendarPeriod calendarPeriod : interestCalculationPeriods) {
                NonCompoundingInterestCalculator interestCalculationPeriodCalculator = createInterestCalculationPeriodCalculator(
                        interestDetail, interestCalculationEvent);
                SavingsProductHistoricalInterestDetail historicalInterestDetail = findMatchingHistoricalInterestDetail(
                        historicalInterestDetails, calendarPeriod);
                if (historicalInterestDetail != null) {
                    int accountingNumberOfInterestDaysInYear = AccountingRules.getNumberOfInterestDays();
                    SavingsInterestDetail historicalSavingsInterestDetail = new SavingsInterestDetail(
                            interestDetail.getInterestCalcType(), historicalInterestDetail.getInterestRate(),
                            accountingNumberOfInterestDaysInYear, historicalInterestDetail.getMinAmntForInt());
                    interestCalculationPeriodCalculator = createInterestCalculationPeriodCalculator(
                            historicalSavingsInterestDetail, interestCalculationEvent);
                }

                // 2. populate InterestCalculationPeriodDetail with valid end of day details for calculation period
                InterestCalculationPeriodDetail interestCalculationPeriodDetail = InterestCalculationPeriodDetail
                        .populatePeriodDetailBasedOnInterestCalculationInterval(calendarPeriod,
                                allEndOfDayDetailsForAccount, runningBalance);

                // 3. calculate average principal, total principal and interest details for calculation period.
                InterestCalculationPeriodResult calculationPeriodResult = interestCalculationPeriodCalculator
                        .calculateCalculationPeriodDetail(interestCalculationPeriodDetail);

                // 4. only sum the total principal as 'interest calculation periods are non-compounding'
                runningBalance = runningBalance.add(calculationPeriodResult.getTotalPrincipal());

                postingPeriodResult.add(calculationPeriodResult);
            }
        }
        postingPeriodResult.setPeriodBalance(runningBalance);

        return postingPeriodResult;
    }

    private SavingsProductHistoricalInterestDetail findMatchingHistoricalInterestDetail(
            List<SavingsProductHistoricalInterestDetail> historicalInterestDetails, CalendarPeriod calendarPeriod) {
        SavingsProductHistoricalInterestDetail match = null;
        SavingsProductHistoricalInterestDetail closestMatch = null;
        for (SavingsProductHistoricalInterestDetail historicalInterestDetail : historicalInterestDetails) {
            if (calendarPeriod.contains(historicalInterestDetail.getStartDate())) {
                match = historicalInterestDetail;
            }

            if (calendarPeriod.getEndDate().isBefore(historicalInterestDetail.getStartDate())) {
                closestMatch = historicalInterestDetail;
            }
        }

        if (match == null) {
            match = closestMatch;
        }

        return match;
    }

    private NonCompoundingInterestCalculator createInterestCalculationPeriodCalculator(
            SavingsInterestDetail interestDetail, InterestScheduledEvent interestCalculationEvent) {
        InterestCalculator interestCalculator = SavingsInterestCalculatorFactory.create(interestDetail);

        NonCompoundingInterestCalculator interestCalculationPeriodCalculator = new InterestCalculationPeriodCalculator(
                interestCalculator, interestCalculationEvent, interestCalculationIntervalHelper);
        return interestCalculationPeriodCalculator;
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    @Override
    public SavingsAccountClosureDto retrieveClosingDetails(Long savingsId, LocalDate closureDate) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);

        InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory
                .createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());
        LocalDate nextPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());
        LocalDate startOfPostingPeriod = postingSchedule.findFirstDateOfPeriodForMatchingDate(nextPostingDate);

        CalendarPeriod postingPeriodAtClosure;
        if (startOfPostingPeriod.isAfter(closureDate)) {
            postingPeriodAtClosure = new CalendarPeriod(closureDate, closureDate);
        } else {
            postingPeriodAtClosure = new CalendarPeriod(startOfPostingPeriod, closureDate);
        }

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(
                savingsAccount.getCurrency(), Long.valueOf(savingsId));

        InterestPostingPeriodResult postingPeriodAtClosureResult = determinePostingPeriodResult(postingPeriodAtClosure,
                savingsAccount, allEndOfDayDetailsForAccount);

        Money endOfAccountBalance = postingPeriodAtClosureResult.getPeriodBalance();
        Money interestAmountAtClosure = postingPeriodAtClosureResult.getPeriodInterest();

        List<ListElement> depositPaymentTypes = retrieveDepositPaymentTypes(userContext);

        return new SavingsAccountClosureDto(new LocalDate(), endOfAccountBalance.toString(),
                interestAmountAtClosure.toString(), depositPaymentTypes);
    }

    private InterestPostingPeriodResult determinePostingPeriodResult(CalendarPeriod postingPeriod,
            SavingsBO savingsAccount, List<EndOfDayDetail> allEndOfDayDetailsForAccount) {

        List<SavingsProductHistoricalInterestDetail> historicalInterestDetails = savingsAccount
                .getHistoricalInterestDetailsForPeriod(postingPeriod);

        MifosCurrency currencyInUse = savingsAccount.getCurrency();
        Money startingBalanceForPeriod = calculateAccountBalanceOn(postingPeriod.getStartDate(),
                allEndOfDayDetailsForAccount, currencyInUse);

        InterestCalcType interestCalcType = savingsAccount.getInterestCalcType();
        int accountingNumberOfInterestDaysInYear = AccountingRules.getNumberOfInterestDays();
        SavingsInterestDetail interestDetail = new SavingsInterestDetail(interestCalcType,
                savingsAccount.getInterestRate(), accountingNumberOfInterestDaysInYear,
                savingsAccount.getMinAmntForInt());

        InterestScheduledEvent interestCalculationEvent = new SavingsInterestScheduledEventFactory()
                .createScheduledEventFrom(savingsAccount.getInterestCalculationMeeting());

        return doCalculateInterestForPostingPeriod(postingPeriod, startingBalanceForPeriod, historicalInterestDetails,
                allEndOfDayDetailsForAccount, interestDetail, interestCalculationEvent);
    }

    @Override
    public void closeSavingsAccount(Long savingsId, String notes, SavingsWithdrawalDto closeAccountDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        LocalDate closureDate = closeAccountDto.getDateOfWithdrawal();

        // Assumption that all previous interest postings occured correctly
        InterestScheduledEvent postingSchedule = savingsInterestScheduledEventFactory
                .createScheduledEventFrom(savingsAccount.getInterestPostingMeeting());
        LocalDate nextPostingDate = new LocalDate(savingsAccount.getNextIntPostDate());
        LocalDate startOfPostingPeriod = postingSchedule.findFirstDateOfPeriodForMatchingDate(nextPostingDate);

        CalendarPeriod postingPeriodAtClosure;
        if (startOfPostingPeriod.isAfter(closureDate)) {
            postingPeriodAtClosure = new CalendarPeriod(closureDate, closureDate);
        } else {
            postingPeriodAtClosure = new CalendarPeriod(startOfPostingPeriod, closureDate);
        }

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(
                savingsAccount.getCurrency(), Long.valueOf(savingsId));

        InterestPostingPeriodResult postingPeriodAtClosureResult = determinePostingPeriodResult(postingPeriodAtClosure,
                savingsAccount, allEndOfDayDetailsForAccount);
        savingsAccount.postInterest(postingSchedule, postingPeriodAtClosureResult, createdBy);

        AccountNotesEntity notesEntity = new AccountNotesEntity(new DateTimeService().getCurrentJavaSqlDate(), notes,
                createdBy, savingsAccount);

        try {
            CustomerBO customer = savingsAccount.getCustomer();
            if (closeAccountDto.getCustomerId() != null) {
                List<CustomerBO> clientList = new CustomerPersistence().getActiveAndOnHoldChildren(savingsAccount
                        .getCustomer().getSearchId(), savingsAccount.getCustomer().getOfficeId(), CustomerLevel.CLIENT);

                for (CustomerBO client : clientList) {
                    if (closeAccountDto.getCustomerId().intValue() == client.getCustomerId().intValue()) {
                        customer = client;
                        break;
                    }
                }
            }

            Money amount = new Money(savingsAccount.getCurrency(), closeAccountDto.getAmount().toString());
            PaymentTypeEntity paymentType = new PaymentTypeEntity(closeAccountDto.getModeOfPayment().shortValue());
            Date receiptDate = null;
            if (closeAccountDto.getDateOfReceipt() != null) {
                receiptDate = closeAccountDto.getDateOfReceipt().toDateMidnight().toDate();
            }
            AccountPaymentEntity closeAccount = new AccountPaymentEntity(savingsAccount, amount,
                    closeAccountDto.getReceiptId(), receiptDate, paymentType, closeAccountDto.getDateOfWithdrawal()
                            .toDateMidnight().toDate());

            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);

            savingsAccount.closeAccount(closeAccount, notesEntity, customer, createdBy);
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (PersistenceException e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public DepositWithdrawalReferenceDto retrieveDepositWithdrawalReferenceData(Long savingsId, Integer customerId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            SavingsBO savingsAccount = savingsDao.findById(savingsId);

            String depositDue = savingsAccount.getTotalPaymentDue(customerId).toString();
            String withdrawalDue = "0";

            List<ListElement> clients = new ArrayList<ListElement>();
            if (savingsAccount.isGroupModelWithIndividualAccountability()) {
                List<CustomerBO> activeAndOnHoldClients = new CustomerPersistence().getActiveAndOnHoldChildren(
                        savingsAccount.getCustomer().getSearchId(), savingsAccount.getCustomer().getOfficeId(),
                        CustomerLevel.CLIENT);
                for (CustomerBO client : activeAndOnHoldClients) {
                    clients.add(new ListElement(client.getCustomerId(), client.getDisplayName()));
                }
            }

            List<AccountActionEntity> trxnTypes = new ArrayList<AccountActionEntity>();
            trxnTypes.add(new AccountBusinessService().getAccountAction(AccountActionTypes.SAVINGS_DEPOSIT.getValue(),
                    userContext.getLocaleId()));
            trxnTypes.add(new AccountBusinessService().getAccountAction(
                    AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), userContext.getLocaleId()));

            List<ListElement> transactionTypes = new ArrayList<ListElement>();
            for (AccountActionEntity accountActionEntity : trxnTypes) {
                LookUpValueEntity lookupValue = accountActionEntity.getLookUpValue();
                String messageText = lookupValue.getMessageText();
                if (StringUtils.isBlank(messageText)) {
                    messageText = ApplicationContextProvider.getBean(MessageLookup.class).lookup(
                            lookupValue.getPropertiesKey());
                }
                transactionTypes.add(new ListElement(accountActionEntity.getId().intValue(), messageText));
            }

            List<ListElement> depositPaymentTypes = retrieveDepositPaymentTypes(userContext);

            List<ListElement> withdrawalPaymentTypes = new ArrayList<ListElement>();
            List<PaymentTypeEntity> withdrawalPaymentEntityTypes = legacyAcceptedPaymentTypeDao
                    .getAcceptedPaymentTypesForATransaction(userContext.getLocaleId(),
                            TrxnTypes.savings_withdrawal.getValue());
            for (PaymentTypeEntity paymentTypeEntity : withdrawalPaymentEntityTypes) {

                LookUpValueEntity lookupValue = paymentTypeEntity.getLookUpValue();
                String messageText = lookupValue.getMessageText();
                if (StringUtils.isBlank(messageText)) {
                    messageText = ApplicationContextProvider.getBean(MessageLookup.class).lookup(
                            lookupValue.getPropertiesKey());
                }

                withdrawalPaymentTypes.add(new ListElement(paymentTypeEntity.getId().intValue(), messageText));
            }

            boolean backDatedTransactionsAllowed = AccountingRules.isBackDatedTxnAllowed();
            LocalDate defaultTransactionDate = new LocalDate();

            return new DepositWithdrawalReferenceDto(transactionTypes, depositPaymentTypes, withdrawalPaymentTypes,
                    clients, backDatedTransactionsAllowed, defaultTransactionDate, depositDue, withdrawalDue);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private List<ListElement> retrieveDepositPaymentTypes(UserContext userContext) {
        try {
            List<ListElement> depositPaymentTypes = new ArrayList<ListElement>();
            List<PaymentTypeEntity> acceptedPaymentEntityTypes = legacyAcceptedPaymentTypeDao
                    .getAcceptedPaymentTypesForATransaction(userContext.getLocaleId(),
                            TrxnTypes.savings_deposit.getValue());
            for (PaymentTypeEntity paymentTypeEntity : acceptedPaymentEntityTypes) {

                LookUpValueEntity lookupValue = paymentTypeEntity.getLookUpValue();
                String messageText = lookupValue.getMessageText();
                if (StringUtils.isBlank(messageText)) {
                    messageText = ApplicationContextProvider.getBean(MessageLookup.class).lookup(
                            lookupValue.getPropertiesKey());
                }

                depositPaymentTypes.add(new ListElement(paymentTypeEntity.getId().intValue(), messageText));
            }
            return depositPaymentTypes;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public SavingsAdjustmentReferenceDto retrieveAdjustmentReferenceData(Long savingsId) {
        SavingsBO savings = savingsDao.findById(savingsId);
        AccountPaymentEntity lastPayment = savings.findMostRecentPaymentByPaymentDate();
        Integer paymentId = (lastPayment == null) ? null : lastPayment.getPaymentId();
        return retrieveAdjustmentReferenceData(savingsId, paymentId);
    }

    @Override
    public SavingsAdjustmentReferenceDto retrieveAdjustmentReferenceData(Long savingsId, Integer paymentId) {

        SavingsBO savings = savingsDao.findById(savingsId);
        AccountPaymentEntity payment = (paymentId == null) ? null : savings.findPaymentById(paymentId);

        String clientName = null;
        String amount = null;
        boolean depositOrWithdrawal = false;
        if (payment != null) {
            amount = payment.getAmount().toString();
            depositOrWithdrawal = payment.isSavingsDepositOrWithdrawal();
        }
        if (!savings.getCustomer().isClient() && payment != null) {
            CustomerBO customer = null;
            for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
                customer = accountTrxn.getCustomer();
                break;
            }
            if (customer != null && customer.isClient()) {
                clientName = customer.getDisplayName();
            }
        }

        return new SavingsAdjustmentReferenceDto(clientName, amount, depositOrWithdrawal);
    }

    @Override
    public List<PrdOfferingDto> retrieveApplicableSavingsProductsForCustomer(Integer customerId) {

        List<PrdOfferingDto> applicableSavingsProducts = new ArrayList<PrdOfferingDto>();

        CustomerBO customer = this.customerDao.findCustomerById(customerId);

        applicableSavingsProducts = this.savingsProductDao.findSavingsProductByCustomerLevel(customer
                .getCustomerLevel());

        return applicableSavingsProducts;
    }

    @Override
    public SavingsProductReferenceDto retrieveSavingsProductReferenceData(Integer productId) {

        SavingsOfferingBO savingsProduct = this.savingsProductDao.findById(productId);

        List<ListElement> interestCalcTypeOptions = new ArrayList<ListElement>();
        List<InterestCalcTypeEntity> interestCalculationTypes = this.savingsProductDao
                .retrieveInterestCalculationTypes();
        for (InterestCalcTypeEntity entity : interestCalculationTypes) {
            interestCalcTypeOptions.add(new ListElement(entity.getId().intValue(), entity.getName()));
        }

        boolean savingsPendingApprovalEnabled = ProcessFlowRules.isSavingsPendingApprovalStateEnabled();

        return new SavingsProductReferenceDto(interestCalcTypeOptions, savingsProduct.toFullDto(),
                savingsPendingApprovalEnabled);
    }

    @Override
    public String createSavingsAccount(OpeningBalanceSavingsAccount openingBalanceSavingsAccount) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDate createdDate = new LocalDate();
        Integer createdById = user.getUserId();
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(createdById.shortValue());

        CustomerBO customer = this.customerDao.findCustomerBySystemId(openingBalanceSavingsAccount
                .getCustomerGlobalId());
        SavingsOfferingBO savingsProduct = this.savingsProductDao.findBySystemId(openingBalanceSavingsAccount
                .getProductGlobalId());

        AccountState savingsAccountState = AccountState.fromShort(openingBalanceSavingsAccount.getAccountState());
        Money recommendedOrMandatory = new Money(savingsProduct.getCurrency(),
                openingBalanceSavingsAccount.getRecommendedOrMandatoryAmount());
        Money openingBalance = new Money(savingsProduct.getCurrency(), openingBalanceSavingsAccount.getOpeningBalance());

        LocalDate activationDate = openingBalanceSavingsAccount.getActivationDate();

        CalendarEvent calendarEvents = this.holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());

        SavingsAccountActivationDetail activationDetails = SavingsBO.determineAccountActivationDetails(customer,
                savingsProduct, recommendedOrMandatory, savingsAccountState, calendarEvents, activationDate);

        SavingsBO savingsAccount = SavingsBO.createOpeningBalanceIndividualSavingsAccount(customer, savingsProduct,
                recommendedOrMandatory, savingsAccountState, createdDate, createdById, activationDetails, createdBy,
                openingBalance);
        System.out.println(openingBalanceSavingsAccount.getAccountNumber());
        savingsAccount.setGlobalAccountNum(openingBalanceSavingsAccount.getAccountNumber());

        try {
            this.transactionHelper.startTransaction();
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.flushSession();
            //savingsAccount.generateSystemId(createdBy.getOffice().getGlobalOfficeNum());
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
            return savingsAccount.getGlobalAccountNum();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public Long createSavingsAccount(SavingsAccountCreationDto savingsAccountCreation,
            List<QuestionGroupDetail> questionGroups) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDate createdDate = new LocalDate();
        Integer createdById = user.getUserId();
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(createdById.shortValue());

        CustomerBO customer = this.customerDao.findCustomerById(savingsAccountCreation.getCustomerId());
        SavingsOfferingBO savingsProduct = this.savingsProductDao.findById(savingsAccountCreation.getProductId());

        Money recommendedOrMandatory = new Money(savingsProduct.getCurrency(),
                savingsAccountCreation.getRecommendedOrMandatoryAmount());
        AccountState savingsAccountState = AccountState.fromShort(savingsAccountCreation.getAccountState());

        CalendarEvent calendarEvents = this.holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());

        SavingsAccountTypeInspector savingsAccountWrapper = new SavingsAccountTypeInspector(customer,
                savingsProduct.getRecommendedAmntUnit());

        try {
            SavingsBO savingsAccount = null;
            if (savingsAccountWrapper.isIndividualSavingsAccount()) {

                savingsAccount = SavingsBO.createIndividalSavingsAccount(customer, savingsProduct,
                        recommendedOrMandatory, savingsAccountState, createdDate, createdById, calendarEvents,
                        createdBy);

            } else if (savingsAccountWrapper.isJointSavingsAccountWithClientTracking()) {

                List<CustomerBO> activeAndOnHoldClients = new CustomerPersistence().getActiveAndOnHoldChildren(
                        customer.getSearchId(), customer.getOfficeId(), CustomerLevel.CLIENT);
                savingsAccount = SavingsBO.createJointSavingsAccount(customer, savingsProduct, recommendedOrMandatory,
                        savingsAccountState, createdDate, createdById, calendarEvents, createdBy,
                        activeAndOnHoldClients);
            }

            this.transactionHelper.startTransaction();
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.flushSession();
            savingsAccount.generateSystemId(createdBy.getOffice().getGlobalOfficeNum());
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.flushSession();

            // save question groups
            if (!questionGroups.isEmpty()) {
                Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Savings");
                QuestionGroupDetails questionGroupDetails = new QuestionGroupDetails(Integer.valueOf(user.getUserId())
                        .shortValue(), savingsAccount.getAccountId(), eventSourceId, questionGroups);
                questionnaireServiceFacade.saveResponses(questionGroupDetails);
            }

            this.transactionHelper.commitTransaction();
            return savingsAccount.getAccountId().longValue();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public Long createSavingsAccount(SavingsAccountCreationDto savingsAccountCreation) {
        return createSavingsAccount(savingsAccountCreation, new ArrayList<QuestionGroupDetail>());
    }

    @Override
    public AccountStatusDto retrieveAccountStatuses(Long savingsId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);

        try {
            List<ListElement> savingsStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeSavingsStates();

            List<AccountStateEntity> statusList = AccountStateMachines.getInstance().getSavingsStatusList(
                    savingsAccount.getAccountState());
            for (AccountStateEntity accountState : statusList) {
                savingsStatesList.add(new ListElement(accountState.getId().intValue(), accountState.getName()));
            }

            return new AccountStatusDto(savingsStatesList);
        } catch (StatesInitializationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateSavingsAccountStatus(AccountUpdateStatus updateStatus) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

        SavingsBO savingsAccount = this.savingsDao.findById(updateStatus.getSavingsId());
        savingsAccount.updateDetails(userContext);
        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);
            AccountState newStatus = AccountState.fromShort(updateStatus.getNewStatusId());

            // FIXME - keithw - refactor savings specific logic out of changeStatus and create savings statue machine
            // wrapper.
            savingsAccount.changeStatus(newStatus, updateStatus.getFlagId(), updateStatus.getComment(), loggedInUser);
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public SavingsAccountDepositDueDto retrieveDepositDueDetails(String globalAccountNum) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findBySystemId(globalAccountNum);

        try {
            personnelDao.checkAccessPermission(userContext, savingsAccount.getOfficeId(), savingsAccount.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }

        List<DueOnDateDto> previousDueDates = new ArrayList<DueOnDateDto>();

        SavingsScheduleEntity nextInstallment = (SavingsScheduleEntity) savingsAccount.getDetailsOfNextInstallment();
        Money totalDepositDue = Money.zero(savingsAccount.getCurrency());
        LocalDate nextDueDate = new LocalDate();
        if (nextInstallment != null) {
            nextDueDate = new LocalDate(nextInstallment.getActionDate());
            totalDepositDue = nextInstallment.getTotalDepositDue();
        }

        List<AccountActionDateEntity> scheduledDeposits = savingsAccount.getAccountActionDatesSortedByInstallmentId();
        for (AccountActionDateEntity scheduledDeposit : scheduledDeposits) {
            if (!scheduledDeposit.isPaid() && scheduledDeposit.isBefore(nextDueDate)) {
                SavingsScheduleEntity savingsScheduledDeposit = (SavingsScheduleEntity) scheduledDeposit;
                previousDueDates.add(new DueOnDateDto(scheduledDeposit.getActionDate(), MoneyUtils.currencyRound(
                        savingsScheduledDeposit.getTotalDepositDue()).toString()));
            }
        }

        DueOnDateDto nextDueDetail = new DueOnDateDto(
                new java.sql.Date(nextDueDate.toDateMidnight().toDate().getTime()), MoneyUtils.currencyRound(
                        totalDepositDue).toString());

        AccountStateEntity accountStateEntity = savingsAccount.getAccountState();

        return new SavingsAccountDepositDueDto(nextDueDetail, previousDueDates, accountStateEntity.getId(),
                accountStateEntity.getName());
    }

    @Override
    public List<SavingsRecentActivityDto> retrieveRecentSavingsActivities(Long savingsId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);

        return savingsAccount.getRecentAccountActivity(null);
    }

    @Override
    public List<SavingsTransactionHistoryDto> retrieveTransactionHistory(String globalAccountNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findBySystemId(globalAccountNum);
        CustomerBO customerBO = savingsAccount.getCustomer();
        savingsAccount.updateDetails(userContext);

        try {
            personnelDao.checkAccessPermission(userContext, customerBO.getOfficeId(), customerBO.getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
        }

        List<SavingsTransactionHistoryDto> savingsTransactionHistoryViewList = new ArrayList<SavingsTransactionHistoryDto>();

        // Check for order-by clause in AccountBO.hbm.xml,
        // AccountPayment.hbm.xml and AccountTrxnEntity.hbm.xml for
        // accountPaymentSet ,
        // accountTrxnSet and financialBoSet. They all should be set for their
        // primay key column desc in both. If stated is not there, the code
        // below will behave abnormally.
        List<AccountPaymentEntity> accountPaymentSet = savingsAccount.getAccountPayments();
        for (AccountPaymentEntity accountPaymentEntity : accountPaymentSet) {
            Set<AccountTrxnEntity> accountTrxnEntitySet = accountPaymentEntity.getAccountTrxns();
            for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntitySet) {
                Set<FinancialTransactionBO> financialTransactionBOSet = accountTrxnEntity.getFinancialTransactions();
                for (FinancialTransactionBO financialTransactionBO : financialTransactionBOSet) {
                    SavingsTransactionHistoryDto savingsTransactionHistoryDto = new SavingsTransactionHistoryDto();

                    savingsTransactionHistoryDto.setTransactionDate(financialTransactionBO.getActionDate());
                    String preferredTransactionDate = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(),
                            financialTransactionBO.getActionDate().toString());
                    savingsTransactionHistoryDto.setUserPrefferedTransactionDate(preferredTransactionDate);

                    savingsTransactionHistoryDto.setPaymentId(accountTrxnEntity.getAccountPayment().getPaymentId());
                    savingsTransactionHistoryDto.setAccountTrxnId(accountTrxnEntity.getAccountTrxnId());
                    savingsTransactionHistoryDto.setType(financialTransactionBO.getFinancialAction().getName());
                    savingsTransactionHistoryDto.setGlcode(financialTransactionBO.getGlcode().getGlcode());
                    savingsTransactionHistoryDto.setGlname(financialTransactionBO.getGlcode().getAssociatedCOA()
                            .getAccountName());
                    if (financialTransactionBO.isDebitEntry()) {
                        savingsTransactionHistoryDto.setDebit(String.valueOf(removeSign(financialTransactionBO
                                .getPostedAmount())));
                    } else if (financialTransactionBO.isCreditEntry()) {
                        savingsTransactionHistoryDto.setCredit(String.valueOf(removeSign(financialTransactionBO
                                .getPostedAmount())));
                    }
                    savingsTransactionHistoryDto.setBalance(String
                            .valueOf(removeSign(((SavingsTrxnDetailEntity) accountTrxnEntity).getBalance())));
                    savingsTransactionHistoryDto.setClientName(accountTrxnEntity.getCustomer().getDisplayName());
                    savingsTransactionHistoryDto.setPostedDate(financialTransactionBO.getPostedDate());
                    String preferredDate = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(),
                            financialTransactionBO.getPostedDate().toString());
                    savingsTransactionHistoryDto.setUserPrefferedPostedDate(preferredDate);
                    if (accountTrxnEntity.getPersonnel() != null) {
                        savingsTransactionHistoryDto.setPostedBy(accountTrxnEntity.getPersonnel().getDisplayName());
                    }
                    if (financialTransactionBO.getNotes() != null && !financialTransactionBO.getNotes().equals("")) {
                        savingsTransactionHistoryDto.setNotes(financialTransactionBO.getNotes());
                    }
                    savingsTransactionHistoryViewList.add(savingsTransactionHistoryDto);
                }
            }
        }

        return savingsTransactionHistoryViewList;
    }

    private String removeSign(Money amount) {
        if (amount.isLessThanZero()) {
            return amount.negate().toString();
        }

        return amount.toString();
    }

    @Override
    public List<SavingsStatusChangeHistoryDto> retrieveStatusChangeHistory(String globalAccountNum) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findBySystemId(globalAccountNum);
        savingsAccount.updateDetails(userContext);

        List<SavingsStatusChangeHistoryDto> dtoList = new ArrayList<SavingsStatusChangeHistoryDto>();

        List<AccountStatusChangeHistoryEntity> statusChangeHistory = savingsAccount.getAccountStatusChangeHistory();
        for (AccountStatusChangeHistoryEntity accountStatusChangeHistory : statusChangeHistory) {
            dtoList.add(accountStatusChangeHistory.toDto());
        }
        return dtoList;
    }

    @Override
    public void updateSavingsAccountDetails(Long savingsId, String recommendedOrMandatoryAmount,
            List<CustomFieldDto> customFields) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);

        Set<AccountCustomFieldEntity> accountCustomFields = savingsAccount.getAccountCustomFields();
        for (CustomFieldDto view : customFields) {
            boolean fieldPresent = false;
            if (CustomFieldType.DATE.getValue().equals(view.getFieldType())
                    && org.apache.commons.lang.StringUtils.isNotBlank(view.getFieldValue())) {
                try {
                    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,
                            userContext.getPreferredLocale());
                    String userfmt = DateUtils.convertToCurrentDateFormat(format.toPattern());
                    view.setFieldValue(DateUtils.convertUserToDbFmt(view.getFieldValue(), userfmt));
                } catch (InvalidDateException e) {
                    throw new BusinessRuleException(e.getMessage(), e);
                }
            }
            for (AccountCustomFieldEntity customFieldEntity : accountCustomFields) {
                if (customFieldEntity.getFieldId().equals(view.getFieldId())) {
                    fieldPresent = true;
                    customFieldEntity.setFieldValue(view.getFieldValue());
                }
            }
            if (!fieldPresent) {
                accountCustomFields.add(new AccountCustomFieldEntity(savingsAccount, view.getFieldId(), view
                        .getFieldValue()));
            }
        }

        Money amount = new Money(savingsAccount.getCurrency(), recommendedOrMandatoryAmount);

        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(savingsAccount);

            savingsAccount.update(amount, accountCustomFields);

            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public SavingsAccountDetailDto retrieveSavingsAccountDetails(Long savingsId) {
        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        try {
            personnelDao.checkAccessPermission(userContext, savingsAccount.getOfficeId(), savingsAccount.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
        }

        return savingsAccount.toDto();
    }

    @Override
    public SavingsAccountDetailDto retrieveSavingsAccountDetails(String globalAccountNum) {
        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        SavingsBO savingsAccount = this.savingsDao.findBySystemId(globalAccountNum);
        savingsAccount.setUserContext(userContext);
        try {
            personnelDao.checkAccessPermission(userContext, savingsAccount.getOfficeId(), savingsAccount.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
        }

        return savingsAccount.toDto();
    }

    @Override
    public void waiveNextDepositAmountDue(Long savingsId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
        try {
            this.transactionHelper.startTransaction();

            savingsAccount.waiveNextDepositAmountDue(loggedInUser);

            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public void waiveDepositAmountOverDue(Long savingsId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
        try {
            this.transactionHelper.startTransaction();

            savingsAccount.waiveAmountOverDue(loggedInUser);

            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public List<AuditLogDto> retrieveSavingsAccountAuditLogs(Long savingsId) {
        List<AuditLogDto> auditLogDtos = new ArrayList<AuditLogDto>();
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        try {
            List<AuditLogView> auditLogs = auditBusinessService.getAuditLogRecords(EntityType.SAVINGS.getValue(),
                    savingsId.intValue());
            for (AuditLogView auditLogView : auditLogs) {
                auditLogDtos.add(auditLogView.toDto());
            }
            return auditLogDtos;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public NotesSearchResultsDto retrievePagedNotesDto(NoteSearchDto noteSearch) {
        return this.savingsDao.searchNotes(noteSearch);
    }

    @Override
    public SavingsAccountDetailDto retrieveSavingsAccountNotes(Long savingsId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO savingsAccount = this.savingsDao.findById(savingsId);
        savingsAccount.updateDetails(userContext);
        return savingsAccount.toDto();
    }

    @Override
    public void addNote(CreateAccountNote accountNote) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(accountNote.getCreatedById().shortValue());
        SavingsBO savingsAccount = this.savingsDao.findById(accountNote.getAccountId().longValue());

        AccountNotesEntity accountNotes = new AccountNotesEntity(new java.sql.Date(accountNote.getCommentDate()
                .toDateMidnight().toDate().getTime()), accountNote.getComment(), createdBy, savingsAccount);

        try {
            this.transactionHelper.startTransaction();

            savingsAccount.updateDetails(userContext);
            savingsAccount.addAccountNotes(accountNotes);
            this.savingsDao.save(savingsAccount);
            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public List<CustomerSearchResultDto> retrieveCustomerThatQualifyForSavings(CustomerSearchDto customerSearchDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            List<CustomerSearchResultDto> pagedDetails = new ArrayList<CustomerSearchResultDto>();

            QueryResult customerForSavings = new CustomerPersistence().searchCustForSavings(
                    customerSearchDto.getSearchTerm(), userContext.getId());

            int position = this.resultsetOffset(customerSearchDto.getPage(), customerSearchDto.getPageSize());
            List<AccountSearchResultsDto> pagedResults = customerForSavings.get(position,
                    customerSearchDto.getPageSize());
            int i = 1;
            for (AccountSearchResultsDto customerBO : pagedResults) {
                CustomerSearchResultDto customer = new CustomerSearchResultDto();
                customer.setCustomerId(customerBO.getClientId());
                customer.setBranchName(customerBO.getOfficeName());
                customer.setGlobalId(customerBO.getGlobelNo());
                customer.setSearchIndex(i);

                customer.setCenterName(StringUtils.defaultIfEmpty(customerBO.getCenterName(), "--"));
                customer.setGroupName(StringUtils.defaultIfEmpty(customerBO.getGroupName(), "--"));
                customer.setClientName(customerBO.getClientName());

                pagedDetails.add(customer);
                i++;
            }
            return pagedDetails;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (HibernateSearchException e) {
            throw new MifosRuntimeException(e);
        }

    }

    /**
     * Calculate the correct offset in SQL "limit" clause.
     *
     * This method has protected visibility so that it can be tested.
     *
     * @param currentPage
     *            Current page number. Page number starts at 1.
     * @param itemsPerPage
     *            Number of entries per page.
     * @return
     */
    protected int resultsetOffset(int currentPage, int itemsPerPage) {
        return (currentPage - 1) * itemsPerPage;
    }

    @Override
    public CustomerDto retreieveCustomerDetails(Integer customerId) {
        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        return new CustomerDto(customerId, customer.getDisplayName(), customer.getGlobalCustNum(), customer.getStatus()
                .getValue(), customer.getCustomerLevel().getId(), customer.getVersionNo(), customer.getOfficeId(),
                customer.getLoanOfficerId());
    }

    @Override
    public void putSavingsBusinessKeyInSession(String globalAccountNum, HttpServletRequest request) {
        SavingsBO savingsBO = this.savingsDao.findBySystemId(globalAccountNum);
        try {
            SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, savingsBO, request);
        } catch (PageExpiredException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<CustomerSearchResultDto> retrieveCustomersThatQualifyForTransfer(CustomerSearchDto customerSearchDto) {
        // TODO return only clients with savings accounts
        return retrieveCustomerThatQualifyForSavings(customerSearchDto);
    }

    @Override
    public SavingsDetailDto retrieveSavingsDetail(String accountGlobalNum) {
        SavingsBO savingsAcc = this.savingsDao.findBySystemId(accountGlobalNum);
        SavingsDetailDto savingsDetailsDto = new SavingsDetailDto(savingsAcc.getGlobalAccountNum(), savingsAcc
                .getSavingsOffering().getPrdOfferingName(), savingsAcc.getAccountState().getId(), savingsAcc
                .getAccountState().getName(), savingsAcc.getSavingsBalance().toString());
        return savingsDetailsDto;
    }

    @Override
    public void fundTransfer(FundTransferDto fundTransferDto) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        SavingsBO targetAcc = this.savingsDao.findBySystemId(fundTransferDto.getTargetGlobalAccountNum());
        SavingsBO sourceAcc = this.savingsDao.findBySystemId(fundTransferDto.getSourceGlobalAccountNum());

        SavingsDepositDto depositDto;
        SavingsWithdrawalDto withdrawalDto;

        // prepare data
        try {
            depositDto = new SavingsDepositDto(targetAcc.getAccountId().longValue(), targetAcc.getCustomer()
                    .getCustomerId().longValue(), fundTransferDto.getTrxnDate(), fundTransferDto.getAmount()
                    .doubleValue(), this.legacyAcceptedPaymentTypeDao.getSavingsTransferId().intValue(),
                    fundTransferDto.getReceiptId(), fundTransferDto.getReceiptDate(), userContext.getPreferredLocale());

            withdrawalDto = new SavingsWithdrawalDto(sourceAcc.getAccountId().longValue(), sourceAcc.getCustomer()
                    .getCustomerId().longValue(), fundTransferDto.getTrxnDate(), fundTransferDto.getAmount()
                    .doubleValue(), this.legacyAcceptedPaymentTypeDao.getSavingsTransferId().intValue(),
                    fundTransferDto.getReceiptId(), fundTransferDto.getReceiptDate(), userContext.getPreferredLocale());
        } catch (PersistenceException ex) {
            throw new MifosRuntimeException(ex);
        }

        // transaction
        try {
            this.transactionHelper.startTransaction();

            PaymentDto deposit = deposit(depositDto, true);
            PaymentDto withdrawal = withdraw(withdrawalDto, true);

            // connect the two payments
            AccountPaymentEntity sourcePayment = sourceAcc.findPaymentById(withdrawal.getPaymentId());
            AccountPaymentEntity targetPayment = targetAcc.findPaymentById(deposit.getPaymentId());
            sourcePayment.setOtherTransferPayment(targetPayment);
            targetPayment.setOtherTransferPayment(sourcePayment);

            this.savingsDao.save(sourceAcc);
            this.savingsDao.save(targetAcc);

            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException ex) {
            this.transactionHelper.rollbackTransaction();
            throw ex;
        } catch (Exception ex) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(ex);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public List<AdjustableSavingsPaymentDto> retrievePaymentsForAdjustment(Integer accountId) {
        SavingsBO savingsAccount = this.savingsDao.findById(accountId);

        List<AdjustableSavingsPaymentDto> adjustablePayments = new ArrayList<AdjustableSavingsPaymentDto>();
        for (AccountPaymentEntity payment : savingsAccount.getAccountPayments()) {
            if (payment.isSavingsDepositOrWithdrawal() && payment.getAmount().isGreaterThan(Money.zero())) {
                AdjustableSavingsPaymentDto adjustableSavingsPaymentDto = new AdjustableSavingsPaymentDto(
                        payment.getPaymentId(), payment.getReceiptNumber(), payment.getAmount().getAmount(),
                        new LocalDate(payment.getPaymentDate()), (payment.getReceiptDate() == null) ? null
                                : new LocalDate(payment.getReceiptDate()), payment.getPaymentType().getName(),
                        payment.isSavingsDeposit());
                adjustablePayments.add(adjustableSavingsPaymentDto);
            }
        }

        Collections.sort(adjustablePayments, new Comparator<AdjustableSavingsPaymentDto>() {
            @Override
            public int compare(AdjustableSavingsPaymentDto o1, AdjustableSavingsPaymentDto o2) {
                int result;
                LocalDate firstDate = o1.getPaymentDate();
                LocalDate secondDate = o2.getPaymentDate();
                // sort by date
                if (firstDate.isAfter(secondDate)) {
                    result = -1;
                } else if (firstDate.isBefore(secondDate)) {
                    result = 1;
                } else { // withdrawal comes after deposit
                    if (o1.isWithdrawal() && !o2.isWithdrawal()) {
                        result = -1;
                    } else if (!o1.isWithdrawal() && o2.isWithdrawal()) {
                        result = 1;
                    } else {
                        result = 0;
                    }
                }
                return result;
            }
        });
        return adjustablePayments;
    }

    private boolean withdrawalMakesBalanceNegativeOnDate(SavingsBO savingsAccount, Money amount, LocalDate trxnDate) {

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(
                savingsAccount.getCurrency(), savingsAccount.getAccountId().longValue());
        MifosCurrency currencyInUse = savingsAccount.getCurrency();
        Money balanceOnDateOfWithdrawal = calculateAccountBalanceOn(trxnDate.plusDays(1), allEndOfDayDetailsForAccount,
                currencyInUse);

        return amount.isGreaterThan(balanceOnDateOfWithdrawal);
    }

    private boolean hasAccountNegativeBalance(SavingsBO savingsAccount) {
        boolean negativeBalance = false;
        List<EndOfDayDetail> allEndOfDayDetailsForAccount = savingsDao.retrieveAllEndOfDayDetailsFor(
                savingsAccount.getCurrency(), savingsAccount.getAccountId().longValue());
        Money balance = Money.zero(savingsAccount.getCurrency());

        for (EndOfDayDetail endOfDayDetail : allEndOfDayDetailsForAccount) {
            balance = balance.add(endOfDayDetail.getResultantAmountForDay());
            if (balance.isLessThanZero()) {
                negativeBalance = true;
                break;
            }
        }
        return negativeBalance;
    }
}

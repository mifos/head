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

package org.mifos.accounts.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.NonUniqueResultException;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountOverpaymentEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.LoanScheduleGenerationDto;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.Localization;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.AccountTrxDto;
import org.mifos.dto.domain.OverpaymentDto;
import org.mifos.dto.domain.PaymentDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A service class implementation to expose basic functions on loans. As an external API, this class should not expose
 * business objects, only DTOs.
 */
public class StandardAccountService implements AccountService {
	
    private LegacyAccountDao legacyAccountDao;
    private LegacyLoanDao legacyLoanDao;
    private LegacyAcceptedPaymentTypeDao acceptedPaymentTypePersistence;
    private PersonnelDao personnelDao;
    private CustomerDao customerDao;
    private LoanBusinessService loanBusinessService;
    private HibernateTransactionHelper transactionHelper;
    private MonthClosingServiceFacade monthClosingServiceFacade;
    private SavingsServiceFacade savingsServiceFacade;

    private LegacyMasterDao legacyMasterDao;

    @Autowired
    public StandardAccountService(LegacyAccountDao legacyAccountDao, LegacyLoanDao legacyLoanDao,
                                  LegacyAcceptedPaymentTypeDao acceptedPaymentTypePersistence, PersonnelDao personnelDao,
                                  CustomerDao customerDao, LoanBusinessService loanBusinessService,
                                  HibernateTransactionHelper transactionHelper, LegacyMasterDao legacyMasterDao,
                                  MonthClosingServiceFacade monthClosingServiceFacade, SavingsServiceFacade savingsServiceFacade) {
        this.legacyAccountDao = legacyAccountDao;
        this.legacyLoanDao = legacyLoanDao;
        this.acceptedPaymentTypePersistence = acceptedPaymentTypePersistence;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
        this.loanBusinessService = loanBusinessService;
        this.transactionHelper = transactionHelper;
        this.legacyMasterDao = legacyMasterDao;
        this.monthClosingServiceFacade = monthClosingServiceFacade;
        this.savingsServiceFacade = savingsServiceFacade;
    }

    @Override
    public void makePayment(AccountPaymentParametersDto accountPaymentParametersDto) {
        try {
            transactionHelper.startTransaction();
            makePaymentNoCommit(accountPaymentParametersDto);
            transactionHelper.commitTransaction();
        } catch (PersistenceException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (AccountException e) {
            transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public void makePaymentFromSavings(AccountPaymentParametersDto accountPaymentParametersDto, String savingsGlobalAccNum) {
        transactionHelper.flushAndClearSession();
        SavingsAccountDetailDto savingsAcc = savingsServiceFacade.retrieveSavingsAccountDetails(savingsGlobalAccNum);
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long savingsId = savingsAcc.getAccountId().longValue();
        Long customerId = accountPaymentParametersDto.getCustomer().getCustomerId().longValue();
        LocalDate dateOfWithdrawal = accountPaymentParametersDto.getPaymentDate();
        Double amount = accountPaymentParametersDto.getPaymentAmount().doubleValue();
        Integer modeOfPayment = accountPaymentParametersDto.getPaymentType().getValue().intValue();
        String receiptId = accountPaymentParametersDto.getReceiptId();
        LocalDate dateOfReceipt = accountPaymentParametersDto.getReceiptDate();
        Locale preferredLocale = Localization.getInstance().getLocaleById(user.getPreferredLocaleId());

        SavingsWithdrawalDto savingsWithdrawalDto = new SavingsWithdrawalDto(savingsId, customerId, dateOfWithdrawal, amount,
                modeOfPayment, receiptId, dateOfReceipt, preferredLocale);
        try {
            transactionHelper.startTransaction();
            PaymentDto withdrawal = this.savingsServiceFacade.withdraw(savingsWithdrawalDto, true);
            makePaymentNoCommit(accountPaymentParametersDto, withdrawal.getPaymentId());
            transactionHelper.commitTransaction();
        } catch (AccountException e) {
            transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (BusinessRuleException e) {
            transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void makePayments(List<AccountPaymentParametersDto> accountPaymentParametersDtoList)
            throws PersistenceException, AccountException {
        /*
         * We're counting on rollback on exception behavior in BaseAction. If we want to expose makePayments via a
         * non-Mifos-Web-UI service, we'll need to handle the rollback here.
         */
        StaticHibernateUtil.startTransaction();
        for (AccountPaymentParametersDto accountPaymentParametersDTO : accountPaymentParametersDtoList) {
            makePaymentNoCommit(accountPaymentParametersDTO);
        }
        StaticHibernateUtil.commitTransaction();
    }

    public void makePaymentNoCommit(AccountPaymentParametersDto accountPaymentParametersDto)
            throws PersistenceException, AccountException {
        makePaymentNoCommit(accountPaymentParametersDto, null);
    }

    public void makePaymentNoCommit(AccountPaymentParametersDto accountPaymentParametersDto, Integer savingsPaymentId)
            throws PersistenceException, AccountException {
    	
        final int accountId = accountPaymentParametersDto.getAccountId();
        final AccountBO account = this.legacyAccountDao.getAccount(accountId);
        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);
        
        try {
            personnelDao.checkAccessPermission(userContext, account.getOfficeId(), account.getCustomer().getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED, e);
        }

        monthClosingServiceFacade.validateTransactionDate(accountPaymentParametersDto.getPaymentDate().toDateMidnight().toDate());
        
        PersonnelBO loggedInUser = ApplicationContextProvider.getBean(LegacyPersonnelDao.class).findPersonnelById(accountPaymentParametersDto.getUserMakingPayment().getUserId());
        List<InvalidPaymentReason> validationErrors = validatePayment(accountPaymentParametersDto);
        if (!(account instanceof CustomerAccountBO) && validationErrors.contains(InvalidPaymentReason.INVALID_DATE)) {
            throw new AccountException("errors.invalidTxndate");
        }

        Money overpaymentAmount = null;
        Money amount = new Money(account.getCurrency(), accountPaymentParametersDto.getPaymentAmount());

        if (account instanceof LoanBO &&
                accountPaymentParametersDto.getPaymentOptions().contains(AccountPaymentParametersDto.PaymentOptions.ALLOW_OVERPAYMENTS) &&
                amount.isGreaterThan(((LoanBO) account).getTotalRepayableAmount())) {
            overpaymentAmount = amount.subtract(((LoanBO) account).getTotalRepayableAmount());
            amount = ((LoanBO) account).getTotalRepayableAmount();
        }

        Date receiptDate = null;
        if (accountPaymentParametersDto.getReceiptDate() != null) {
            receiptDate = accountPaymentParametersDto.getReceiptDate().toDateMidnight().toDate();
        }

        PaymentData paymentData = account.createPaymentData(amount, accountPaymentParametersDto.getPaymentDate().toDateMidnight().toDate(),
                accountPaymentParametersDto.getReceiptId(), receiptDate, accountPaymentParametersDto.getPaymentType()
                        .getValue(), loggedInUser);
        if (savingsPaymentId != null) {
            AccountPaymentEntity withdrawal = legacyAccountDao.findPaymentById(savingsPaymentId);
            paymentData.setOtherTransferPayment(withdrawal);
        }

        if (accountPaymentParametersDto.getCustomer() != null) {
            paymentData.setCustomer(customerDao.findCustomerById(
                accountPaymentParametersDto.getCustomer().getCustomerId()));
        }
        paymentData.setComment(accountPaymentParametersDto.getComment());
        paymentData.setOverpaymentAmount(overpaymentAmount);

        account.applyPayment(paymentData);

        this.legacyAccountDao.createOrUpdate(account);
    }
    
    /**
     * method created for undo transaction import ability MIFOS-5702
     * changed return type 
     * */
    @Override
    public List<AccountTrxDto> makePaymentsForImport(List<AccountPaymentParametersDto> accountPaymentParametersDtoList)
            throws PersistenceException, AccountException {
        /*
         * We're counting on rollback on exception behavior in BaseAction. If we want to expose makePayments via a
         * non-Mifos-Web-UI service, we'll need to handle the rollback here.
         */
        List<AccountTrxDto> trxIds = new ArrayList<AccountTrxDto>();
        List <AccountBO> accounts = new ArrayList<AccountBO>();
        StaticHibernateUtil.startTransaction();
        int i = 0;
        for (AccountPaymentParametersDto accountPaymentParametersDTO : accountPaymentParametersDtoList) {
            accounts.add(makeImportedPayments(accountPaymentParametersDTO));
            if (i%30 == 0) {
            	StaticHibernateUtil.getSessionTL().flush();
            	StaticHibernateUtil.getSessionTL().clear();
            }
            i++;
        }
        StaticHibernateUtil.getSessionTL().flush();
    	StaticHibernateUtil.getSessionTL().clear();
        StaticHibernateUtil.commitTransaction();
        for (AccountBO account : accounts) {
        	trxIds.add(new AccountTrxDto(getAccTrxId(account)));
        }        
        return trxIds;
    }
    
    /**
     * method created for undo transaction import ability MIFOS-5702
     * changed return type 
     * */
    public AccountBO makeImportedPayments(AccountPaymentParametersDto accountPaymentParametersDto)
            throws PersistenceException, AccountException {
        return makeImportedPayments(accountPaymentParametersDto, null);
    }
    
    /**
     * method created for undo transaction import ability MIFOS-5702
     * returns Id of transaction  
     * */
    public AccountBO makeImportedPayments(AccountPaymentParametersDto accountPaymentParametersDto, Integer savingsPaymentId)
            throws PersistenceException, AccountException {
        final int accountId = accountPaymentParametersDto.getAccountId();
        final AccountBO account = this.legacyAccountDao.getAccount(accountId);
        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);
        
        try {
            personnelDao.checkAccessPermission(userContext, account.getOfficeId(), account.getCustomer().getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED, e);
        }

        monthClosingServiceFacade.validateTransactionDate(accountPaymentParametersDto.getPaymentDate().toDateMidnight().toDate());
        
        PersonnelBO loggedInUser = ApplicationContextProvider.getBean(LegacyPersonnelDao.class).findPersonnelById(accountPaymentParametersDto.getUserMakingPayment().getUserId());
        List<InvalidPaymentReason> validationErrors = validatePayment(accountPaymentParametersDto);
        if (!(account instanceof CustomerAccountBO) && validationErrors.contains(InvalidPaymentReason.INVALID_DATE)) {
            throw new AccountException("errors.invalidTxndate");
        }

        Money overpaymentAmount = null;
        Money amount = new Money(account.getCurrency(), accountPaymentParametersDto.getPaymentAmount());

        if (account instanceof LoanBO &&
                accountPaymentParametersDto.getPaymentOptions().contains(AccountPaymentParametersDto.PaymentOptions.ALLOW_OVERPAYMENTS) &&
                amount.isGreaterThan(((LoanBO) account).getTotalRepayableAmount())) {
            overpaymentAmount = amount.subtract(((LoanBO) account).getTotalRepayableAmount());
            amount = ((LoanBO) account).getTotalRepayableAmount();
        }

        Date receiptDate = null;
        if (accountPaymentParametersDto.getReceiptDate() != null) {
            receiptDate = accountPaymentParametersDto.getReceiptDate().toDateMidnight().toDate();
        }

        PaymentData paymentData = account.createPaymentData(amount, accountPaymentParametersDto.getPaymentDate().toDateMidnight().toDate(),
                accountPaymentParametersDto.getReceiptId(), receiptDate, accountPaymentParametersDto.getPaymentType()
                        .getValue(), loggedInUser);
        if (savingsPaymentId != null) {
            AccountPaymentEntity withdrawal = legacyAccountDao.findPaymentById(savingsPaymentId);
            paymentData.setOtherTransferPayment(withdrawal);
        }

        if (accountPaymentParametersDto.getCustomer() != null) {
            paymentData.setCustomer(customerDao.findCustomerById(
                accountPaymentParametersDto.getCustomer().getCustomerId()));
        }
        paymentData.setComment(accountPaymentParametersDto.getComment());
        paymentData.setOverpaymentAmount(overpaymentAmount);

        account.applyPayment(paymentData);
        this.legacyAccountDao.createOrUpdate(account);
        
        return account;
    }
    
    private Integer getAccTrxId(AccountBO account) {
        Integer ID = null; 
        Set<AccountTrxnEntity> accountTrxns = account.getAccountPayments().get(account.getAccountPayments().size() - 1).getAccountTrxns();
        for (AccountTrxnEntity trx : accountTrxns) {
            ID = trx.getAccountTrxnId();
        }
        return ID;
    }

    @Override
    public void disburseLoans(List<AccountPaymentParametersDto> accountPaymentParametersDtoList, Locale locale) throws Exception {

        StaticHibernateUtil.startTransaction();
        for (AccountPaymentParametersDto accountPaymentParametersDto : accountPaymentParametersDtoList) {
            LoanBO loan = this.legacyLoanDao.getAccount(accountPaymentParametersDto.getAccountId());
          PersonnelBO personnelBO = personnelDao.findPersonnelById(accountPaymentParametersDto.getUserMakingPayment()
          .getUserId());
            BigDecimal paymentAmount = accountPaymentParametersDto.getPaymentAmount();
            handleLoanDisbursal(locale, loan, personnelBO, paymentAmount, accountPaymentParametersDto.getPaymentType(), accountPaymentParametersDto.getReceiptDate(), accountPaymentParametersDto.getPaymentDate(), 
                    accountPaymentParametersDto.getReceiptId());
        }
        StaticHibernateUtil.commitTransaction();
    }

    public void handleLoanDisbursal(Locale locale, LoanBO loan, PersonnelBO personnelBO, BigDecimal paymentAmount, PaymentTypeDto paymentType, LocalDate receiptLocalDate, LocalDate paymentLocalDate, String receiptId)
            throws PersistenceException, AccountException {

        if ("MPESA".equals(paymentType.getName())) {
            paymentAmount = computeWithdrawnForMPESA(paymentAmount, loan);
        }

        PaymentTypeEntity paymentTypeEntity = legacyMasterDao.getPersistentObject(
                PaymentTypeEntity.class, paymentType.getValue());
        Money amount = new Money(loan.getCurrency(), paymentAmount);
        Date receiptDate = null;
        if (null != receiptLocalDate) {
            receiptDate = receiptLocalDate.toDateMidnight().toDate();
        }
        Date transactionDate = paymentLocalDate.toDateMidnight().toDate();

        AccountPaymentEntity disbursalPayment = new AccountPaymentEntity(loan, amount, receiptId, receiptDate,
                paymentTypeEntity, transactionDate);
        disbursalPayment.setCreatedByUser(personnelBO);
        Double interestRate = loan.getInterestRate();

        Date oldDisbursementDate = loan.getDisbursementDate();
        List<RepaymentScheduleInstallment> originalInstallments = loan.toRepaymentScheduleDto(locale);
        loan.disburseLoan(disbursalPayment);
        if (!loan.isVariableInstallmentsAllowed()) {
            originalInstallments = loan.toRepaymentScheduleDto(locale);
        }
        Date newDisbursementDate = loan.getDisbursementDate();
        boolean variableInstallmentsAllowed = loan.isVariableInstallmentsAllowed();
        loanBusinessService.adjustDatesForVariableInstallments(variableInstallmentsAllowed, loan.isFixedRepaymentSchedule(),
                originalInstallments, oldDisbursementDate, newDisbursementDate, loan.getOfficeId());
        Date today = new LocalDate().toDateMidnight().toDate();
        Date disburseDay = new LocalDate(oldDisbursementDate).toDateMidnight().toDate();
		if (!today.equals(disburseDay)) {
        loanBusinessService.applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(newDisbursementDate,
                loan, variableInstallmentsAllowed, amount, interestRate), originalInstallments);
        }
        loanBusinessService.persistOriginalSchedule(loan);
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromId(Integer id) throws PersistenceException {
        LoanBO loan = this.legacyLoanDao.getAccount(id);
        if (null == loan) {
            throw new PersistenceException("loan not found for id " + id);
        }
        return new AccountReferenceDto(loan.getAccountId());
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromExternalId(String externalId) throws PersistenceException {
        LoanBO loan = this.legacyLoanDao.findByExternalId(externalId);
        if (null == loan) {
            throw new PersistenceException("loan not found for external id " + externalId);
        }
        return new AccountReferenceDto(loan.getAccountId());
    }

    /**
     * Note that, since we don't store or otherwise utilize the amount disbursed (passed in
     * AccountPaymentParametersDto.paymentAmount) we <em>do not</em> validate that digits after decimal for the amount
     * disbursed fit in an allowed range. We <em>do</em> check that the amount disbursed matches the full amount of the
     * loan.
     */
    @Override
    public List<InvalidPaymentReason> validateLoanDisbursement(AccountPaymentParametersDto payment) throws Exception {
        List<InvalidPaymentReason> errors = new ArrayList<InvalidPaymentReason>();
        LoanBO loanAccount = this.legacyLoanDao.getAccount(payment.getAccountId());

        if ((loanAccount.getState() != AccountState.LOAN_APPROVED)
                && (loanAccount.getState() != AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER)) {
            errors.add(InvalidPaymentReason.INVALID_LOAN_STATE);
        }

        BigDecimal paymentAmount = payment.getPaymentAmount();

        if ("MPESA".equals(payment.getPaymentType().getName())) {
            paymentAmount = computeWithdrawnForMPESA(paymentAmount, loanAccount);
        }

        disbursalAmountMatchesFullLoanAmount(paymentAmount, errors, loanAccount);

        Date meetingDate = new CustomerPersistence().getLastMeetingDateForCustomer(loanAccount.getCustomer().getCustomerId());
        boolean repaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        if (!loanAccount.isTrxnDateValid(payment.getPaymentDate().toDateMidnight().toDate(), meetingDate, repaymentIndependentOfMeetingEnabled)) {
            errors.add(InvalidPaymentReason.INVALID_DATE);
        }
        if (!getLoanDisbursementTypes().contains(payment.getPaymentType())) {
            errors.add(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE);
        }
        if (!loanAccount.paymentAmountIsValid(new Money(loanAccount.getCurrency(), payment.getPaymentAmount()), payment.getPaymentOptions())) {
            errors.add(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT);
        }
        if (loanAccount.getCustomer().isDisbursalPreventedDueToAnyExistingActiveLoansForTheSameProduct(loanAccount.getLoanOffering())) {
            errors.add(InvalidPaymentReason.OTHER_ACTIVE_LOANS_FOR_THE_SAME_PRODUCT);
        }
        return errors;
    }

    void disbursalAmountMatchesFullLoanAmount(BigDecimal paymentAmount, List<InvalidPaymentReason> errors,
            LoanBO loanAccount) {
        /* BigDecimal.compareTo() ignores scale, .equals() was explicitly avoided */
        if (loanAccount.getLoanAmount().getAmount().compareTo(paymentAmount) != 0) {
            errors.add(InvalidPaymentReason.INVALID_LOAN_DISBURSAL_AMOUNT);
        }
    }

    @Override
    public List<InvalidPaymentReason> validatePayment(AccountPaymentParametersDto payment) throws PersistenceException,
            AccountException {
        List<InvalidPaymentReason> errors = new ArrayList<InvalidPaymentReason>();
        AccountBO accountBo = this.legacyAccountDao.getAccount(payment.getAccountId());

        Date meetingDate = new CustomerPersistence().getLastMeetingDateForCustomer(accountBo.getCustomer().getCustomerId());
        boolean repaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        if (!accountBo.isTrxnDateValid(payment.getPaymentDate().toDateMidnight().toDate(), meetingDate, repaymentIndependentOfMeetingEnabled)) {
            errors.add(InvalidPaymentReason.INVALID_DATE);
        }
        if (accountBo instanceof LoanBO) {
            if (((LoanBO)accountBo).paymentsNotAllowed()) {
                errors.add(InvalidPaymentReason.INVALID_LOAN_STATE);
            }
        }
        if (accountBo instanceof SavingsBO) {
            if (!accountBo.getState().equals(AccountState.SAVINGS_ACTIVE)) {
                errors.add(InvalidPaymentReason.INVALID_LOAN_STATE);
            }
        }
        if (AccountTypes.getAccountType(accountBo.getAccountType().getAccountTypeId()) == AccountTypes.LOAN_ACCOUNT) {
            if (!getLoanPaymentTypes().contains(payment.getPaymentType())) {
                errors.add(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE);
            }
        } else if (AccountTypes.getAccountType(accountBo.getAccountType().getAccountTypeId()) == AccountTypes.SAVINGS_ACCOUNT) {
            if (!getSavingsPaymentTypes().contains(payment.getPaymentType())) {
                errors.add(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE);
            }
        } else if (AccountTypes.getAccountType(accountBo.getAccountType().getAccountTypeId()) == AccountTypes.CUSTOMER_ACCOUNT) {
            if (!getFeePaymentTypes().contains(payment.getPaymentType())) {
                errors.add(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE);
            }
        }
        if (!accountBo.paymentAmountIsValid(new Money(accountBo.getCurrency(), payment.getPaymentAmount()), payment.getPaymentOptions())) {
            errors.add(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT);
        }
        return errors;
    }

    @Override
    public List<AccountPaymentParametersDto> lookupPayments(AccountReferenceDto accountRef)
    throws PersistenceException {
        final int accountId = accountRef.getAccountId();
        final AccountBO account = this.legacyAccountDao.getAccount(accountId);
        List<AccountPaymentParametersDto> paymentDtos = new ArrayList<AccountPaymentParametersDto>();
        for (AccountPaymentEntity paymentEntity : account.getAccountPayments()) {
            paymentDtos.add(makePaymentDto(paymentEntity));
        }
        return paymentDtos;
    }

    public AccountPaymentParametersDto makePaymentDto(AccountPaymentEntity paymentEntity) {
        AccountPaymentParametersDto paymentDto = new AccountPaymentParametersDto(
                paymentEntity.getCreatedByUser() == null ?
                        new UserReferenceDto(paymentEntity.getAccountTrxns().iterator().next().
                                getPersonnel().getPersonnelId()) :
                                    new UserReferenceDto(paymentEntity.getCreatedByUser().getPersonnelId()),
                                    new AccountReferenceDto(paymentEntity.getAccount().getAccountId()),
                                    paymentEntity.getAmount().getAmount(),
                                    LocalDate.fromDateFields(paymentEntity.getPaymentDate()),
                                    new PaymentTypeDto(paymentEntity.getPaymentType().getId(),
                                            paymentEntity.getPaymentType().toString()),
                                            paymentEntity.getComment() == null ?
                                                    paymentEntity.toString() :
                                                        paymentEntity.getComment(),
                                                        paymentEntity.getReceiptDate() == null ? null :
                                                            LocalDate.fromDateFields(paymentEntity.getReceiptDate()),
                                                            paymentEntity.getReceiptNumber(), null);
        return paymentDto;
    }


    public List<PaymentTypeDto> getSavingsPaymentTypes() throws PersistenceException {
        return getPaymentTypes(TrxnTypes.savings_deposit.getValue());
    }

    public List<PaymentTypeDto> getSavingsWithdrawalTypes() throws PersistenceException {
        return getPaymentTypes(TrxnTypes.savings_withdrawal.getValue());
    }

    @Override
	public List<PaymentTypeDto> getFeePaymentTypes() throws PersistenceException {
        return getPaymentTypes(TrxnTypes.fee.getValue());
    }

    @Override
    public List<PaymentTypeDto> getLoanPaymentTypes() throws PersistenceException {
        return getPaymentTypes(TrxnTypes.loan_repayment.getValue());
    }

    @Override
    public List<PaymentTypeDto> getLoanDisbursementTypes() throws PersistenceException {
        return getPaymentTypes(TrxnTypes.loan_disbursement.getValue());
    }

    private List<PaymentTypeDto> getPaymentTypes(short transactionType) throws PersistenceException {
        final Short IGNORED_LOCALE_ID = 1;
        List<PaymentTypeEntity> paymentTypeEntities = this.acceptedPaymentTypePersistence
                .getAcceptedPaymentTypesForATransaction(IGNORED_LOCALE_ID, transactionType);
        List<PaymentTypeDto> paymentTypeDtos = new ArrayList<PaymentTypeDto>();
        for (PaymentTypeEntity paymentTypeEntity : paymentTypeEntities) {
            paymentTypeDtos.add(new PaymentTypeDto(paymentTypeEntity.getId(), paymentTypeEntity.getName()));
        }
        return paymentTypeDtos;
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromGlobalAccountNumber(String globalAccountNumber)
            throws PersistenceException {
        AccountBO accountBo = this.legacyAccountDao.findBySystemId(globalAccountNumber);
        if (null == accountBo) {
            throw new PersistenceException("loan not found for global account number " + globalAccountNumber);
        }
        return new AccountReferenceDto(accountBo.getAccountId());
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName(
            String clientGovernmentId, String loanProductShortName) throws Exception {
        AccountBO accountBo = this.legacyAccountDao.findLoanByClientGovernmentIdAndProductShortName(
                clientGovernmentId, loanProductShortName);
        if (null == accountBo) {
            throw new PersistenceException("loan not found for client government id " + clientGovernmentId
                    + " and loan product short name " + loanProductShortName);
        }
        return new AccountReferenceDto(accountBo.getAccountId());
    }

    @Override
    public AccountReferenceDto lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(
            String clientGovernmentId, String savingsProductShortName) throws Exception {
        AccountBO accountBo = this.legacyAccountDao.findSavingsByClientGovernmentIdAndProductShortName(
                clientGovernmentId, savingsProductShortName);
        if (null == accountBo) {
            throw new PersistenceException("savings not found for client government id " + clientGovernmentId
                    + " and savings product short name " + savingsProductShortName);
        }
        return new AccountReferenceDto(accountBo.getAccountId());
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromClientPhoneNumberAndLoanProductShortName(
            String phoneNumber, String loanProductShortName) throws Exception {
        AccountBO accountBo = this.legacyAccountDao.findLoanByClientPhoneNumberAndProductShortName(
                phoneNumber, loanProductShortName);
        if (null == accountBo) {
            throw new PersistenceException("loan not found for client phone number " + phoneNumber
                    + " and loan product short name " + loanProductShortName);
        }
        return new AccountReferenceDto(accountBo.getAccountId());
    }

    @Override
    public boolean existsMoreThanOneLoanAccount(String phoneNumber, String loanProductShortName) {
        try {
            lookupLoanAccountReferenceFromClientPhoneNumberAndLoanProductShortName(phoneNumber, loanProductShortName);
        } catch (Exception e) {
            if (e.getCause() instanceof NonUniqueResultException) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AccountReferenceDto lookupSavingsAccountReferenceFromClientPhoneNumberAndSavingsProductShortName(
            String phoneNumber, String savingsProductShortName) throws Exception {
        AccountBO accountBo = this.legacyAccountDao.findSavingsByClientPhoneNumberAndProductShortName(
                phoneNumber, savingsProductShortName);
        if (null == accountBo) {
            throw new PersistenceException("savings not found for client phone number " + phoneNumber
                    + " and savings product short name " + savingsProductShortName);
        }
        return new AccountReferenceDto(accountBo.getAccountId());
    }

    @Override
    public boolean existsMoreThanOneSavingsAccount(String phoneNumber, String loanProductShortName) {
        try {
            lookupSavingsAccountReferenceFromClientPhoneNumberAndSavingsProductShortName(phoneNumber, loanProductShortName);
        } catch (Exception e) {
            if (e.getCause() instanceof NonUniqueResultException) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BigDecimal getTotalPaymentDueAmount(AccountReferenceDto account) throws Exception {
        AccountBO accountBo = this.legacyAccountDao.getAccount(account.getAccountId());
        return accountBo.getTotalAmountDue().getAmount();
    }

    @Override
    public Object getMifosConfiguration(String propertyKey) {
        MifosConfigurationManager cfgMng = MifosConfigurationManager.getInstance();
        return cfgMng.getProperty(propertyKey);
    }

    @Override
    public boolean receiptExists(String receiptNumber) throws Exception {
        List<AccountPaymentEntity> existentPaymentsWIthGivenReceiptNumber = this.legacyAccountDao.findAccountPaymentsByReceiptNumber(receiptNumber);
        return existentPaymentsWIthGivenReceiptNumber != null && !existentPaymentsWIthGivenReceiptNumber.isEmpty();
    }

    /**
     * Warning - this should be only used from MPESA plugin!
     */
    @Override
    public List<AccountReferenceDto> lookupLoanAccountReferencesFromClientPhoneNumberAndWithdrawAmount(
            String phoneNumber, BigDecimal withdrawAmount) throws Exception {
        List<AccountBO> accounts = this.legacyAccountDao.findApprovedLoansForClientWithPhoneNumber(phoneNumber);
        List<AccountReferenceDto> result = new ArrayList<AccountReferenceDto>();
        for (AccountBO account : accounts) {
            LoanBO loanAccount = (LoanBO)account;
            if (loanAccount.getLoanAmount().getAmount().compareTo(computeWithdrawnForMPESA(withdrawAmount, loanAccount)) == 0) {
                result.add(new AccountReferenceDto(account.getAccountId()));
            }
        }
        return result;
    }

    @Override
    public OverpaymentDto getOverpayment(String overpaymentId) throws PersistenceException {
        AccountOverpaymentEntity overpaymentEntity = this.legacyAccountDao.findOverpaymentById(Integer.valueOf(overpaymentId));
        if (overpaymentEntity == null) {
            throw new PersistenceException("Overpayment not found for id " + overpaymentId);
        }

        return new OverpaymentDto(overpaymentEntity.getOverpaymentId().toString(),
                overpaymentEntity.getOriginalOverpaymentAmount().getAmount(),
                overpaymentEntity.getActualOverpaymentAmount().getAmount());
    }

    private BigDecimal computeWithdrawnForMPESA(BigDecimal withdrawAmount, LoanBO loanAccount) {
        Set<AccountFeesEntity> fees = loanAccount.getAccountFees();
        for (AccountFeesEntity fee : fees) {
            if (fee.isActive() && fee.isOneTime() && fee.isTimeOfDisbursement()) {
                withdrawAmount = withdrawAmount.add(new BigDecimal(fee.getFeeAmount()));
            }
        }
        return withdrawAmount;
    }
}

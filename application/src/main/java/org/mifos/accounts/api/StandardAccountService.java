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

package org.mifos.accounts.api;

import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.LoanScheduleGenerationDto;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.ConfigurationManager;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.mifos.customers.business.CustomerAccountBO;

/**
 * A service class implementation to expose basic functions on loans. As an external API, this class should not expose
 * business objects, only DTOs.
 */
public class StandardAccountService implements AccountService {

    private AccountPersistence accountPersistence;
    private LoanPersistence loanPersistence;
    private AcceptedPaymentTypePersistence acceptedPaymentTypePersistence;
    private PersonnelDao personnelDao;
    private CustomerDao customerDao;
    private LoanBusinessService loanBusinessService;
    private HibernateTransactionHelper transactionHelper;

    public StandardAccountService(AccountPersistence accountPersistence, LoanPersistence loanPersistence,
                                  AcceptedPaymentTypePersistence acceptedPaymentTypePersistence, PersonnelDao personnelDao,
                                  CustomerDao customerDao, LoanBusinessService loanBusinessService,
                                  HibernateTransactionHelper transactionHelper) {
        this.accountPersistence = accountPersistence;
        this.loanPersistence = loanPersistence;
        this.acceptedPaymentTypePersistence = acceptedPaymentTypePersistence;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
        this.loanBusinessService = loanBusinessService;
        this.transactionHelper = transactionHelper;
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

        PersonnelBO loggedInUser = new PersonnelPersistence().findPersonnelById(accountPaymentParametersDto.getUserMakingPayment().getUserId());
        final int accountId = accountPaymentParametersDto.getAccountId();
        final AccountBO account = this.accountPersistence.getAccount(accountId);
        List<InvalidPaymentReason> validationErrors = validatePayment(accountPaymentParametersDto);
        if (!(account instanceof CustomerAccountBO) && validationErrors.contains(InvalidPaymentReason.INVALID_DATE)) {
            throw new AccountException("errors.invalidTxndate");
        }

        Money amount = new Money(account.getCurrency(), accountPaymentParametersDto.getPaymentAmount());

        Date receiptDate = null;
        if (accountPaymentParametersDto.getReceiptDate() != null) {
            receiptDate = accountPaymentParametersDto.getReceiptDate().toDateMidnight().toDate();
        }

        PaymentData paymentData = account.createPaymentData(amount, accountPaymentParametersDto.getPaymentDate().toDateMidnight().toDate(),
                accountPaymentParametersDto.getReceiptId(), receiptDate, accountPaymentParametersDto.getPaymentType()
                        .getValue(), loggedInUser);
        if (accountPaymentParametersDto.getCustomer() != null) {
            paymentData.setCustomer(customerDao.findCustomerById(
                accountPaymentParametersDto.getCustomer().getCustomerId()));
        }
        paymentData.setComment(accountPaymentParametersDto.getComment());

        account.applyPayment(paymentData);

        this.accountPersistence.createOrUpdate(account);
    }

    @Override
    public void disburseLoans(List<AccountPaymentParametersDto> accountPaymentParametersDtoList, Locale locale) throws Exception {
        StaticHibernateUtil.startTransaction();
        for (AccountPaymentParametersDto accountPaymentParametersDto : accountPaymentParametersDtoList) {
            LoanBO loan = this.loanPersistence.getAccount(accountPaymentParametersDto.getAccountId());

            PaymentTypeEntity paymentTypeEntity = (PaymentTypeEntity) new MasterPersistence().getMasterDataEntity(
                    PaymentTypeEntity.class, accountPaymentParametersDto.getPaymentType().getValue());
            Money amount = new Money(loan.getCurrency(), accountPaymentParametersDto.getPaymentAmount());
            Date receiptDate = null;
            if (null != accountPaymentParametersDto.getReceiptDate()) {
                receiptDate = accountPaymentParametersDto.getReceiptDate().toDateMidnight().toDate();
            }
            Date transactionDate = accountPaymentParametersDto.getPaymentDate().toDateMidnight().toDate();
            String receiptId = accountPaymentParametersDto.getReceiptId();

            AccountPaymentEntity disbursalPayment = new AccountPaymentEntity(loan, amount, receiptId, receiptDate,
                    paymentTypeEntity, transactionDate);
            PersonnelBO personnelBO = personnelDao.findPersonnelById(accountPaymentParametersDto.getUserMakingPayment()
                    .getUserId());
            disbursalPayment.setCreatedByUser(personnelBO);
            Double interestRate = loan.getInterestRate();

            Date oldDisbursementDate = loan.getDisbursementDate();
            List<RepaymentScheduleInstallment> originalInstallments = loan.toRepaymentScheduleDto(locale);
            loan.disburseLoan(disbursalPayment);
            boolean variableInstallmentsAllowed = loan.isVariableInstallmentsAllowed();
            Date newDisbursementDate = loan.getDisbursementDate();
            loanBusinessService.adjustDatesForVariableInstallments(variableInstallmentsAllowed, originalInstallments,
                    oldDisbursementDate, newDisbursementDate, loan.getOfficeId());
            loanBusinessService.applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(newDisbursementDate,
                    loan, variableInstallmentsAllowed, amount, interestRate), originalInstallments);
        }
        StaticHibernateUtil.commitTransaction();
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromId(Integer id) throws PersistenceException {
        LoanBO loan = this.loanPersistence.getAccount(id);
        if (null == loan) {
            throw new PersistenceException("loan not found for id " + id);
        }
        return new AccountReferenceDto(loan.getAccountId());
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromExternalId(String externalId) throws PersistenceException {
        LoanBO loan = this.loanPersistence.findByExternalId(externalId);
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
        LoanBO loanAccount = this.loanPersistence.getAccount(payment.getAccountId());

        if ((loanAccount.getState() != AccountState.LOAN_APPROVED)
                && (loanAccount.getState() != AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER)) {
            errors.add(InvalidPaymentReason.INVALID_LOAN_STATE);
        }
        disbursalAmountMatchesFullLoanAmount(payment, errors, loanAccount);

        Date meetingDate = new CustomerPersistence().getLastMeetingDateForCustomer(loanAccount.getCustomer().getCustomerId());
        boolean repaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        if (!loanAccount.isTrxnDateValid(payment.getPaymentDate().toDateMidnight().toDate(), meetingDate, repaymentIndependentOfMeetingEnabled)) {
            errors.add(InvalidPaymentReason.INVALID_DATE);
        }
        if (!getLoanDisbursementTypes().contains(payment.getPaymentType())) {
            errors.add(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE);
        }
        if (!loanAccount.paymentAmountIsValid(new Money(loanAccount.getCurrency(), payment.getPaymentAmount()))) {
            errors.add(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT);
        }
        return errors;
    }

    void disbursalAmountMatchesFullLoanAmount(AccountPaymentParametersDto payment, List<InvalidPaymentReason> errors,
            LoanBO loanAccount) {
        /* BigDecimal.compareTo() ignores scale, .equals() was explicitly avoided */
        if (loanAccount.getLoanAmount().getAmount().compareTo(payment.getPaymentAmount()) != 0) {
            errors.add(InvalidPaymentReason.INVALID_LOAN_DISBURSAL_AMOUNT);
        }
    }

    @Override
    public List<InvalidPaymentReason> validatePayment(AccountPaymentParametersDto payment) throws PersistenceException,
            AccountException {
        List<InvalidPaymentReason> errors = new ArrayList<InvalidPaymentReason>();
        AccountBO accountBo = this.accountPersistence.getAccount(payment.getAccountId());

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
        if (!accountBo.paymentAmountIsValid(new Money(accountBo.getCurrency(), payment.getPaymentAmount()))) {
            errors.add(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT);
        }
        return errors;
    }

    @Override
    public List<AccountPaymentParametersDto> lookupPayments(AccountReferenceDto accountRef)
    throws PersistenceException {
        final int accountId = accountRef.getAccountId();
        final AccountBO account = this.accountPersistence.getAccount(accountId);
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
        AccountBO accountBo = this.accountPersistence.findBySystemId(globalAccountNumber);
        if (null == accountBo) {
            throw new PersistenceException("loan not found for global account number " + globalAccountNumber);
        }
        return new AccountReferenceDto(accountBo.getAccountId());
    }

    @Override
    public AccountReferenceDto lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName(
            String clientGovernmentId, String loanProductShortName) throws Exception {
        AccountBO accountBo = this.accountPersistence.findLoanByClientGovernmentIdAndProductShortName(
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
        AccountBO accountBo = this.accountPersistence.findSavingsByClientGovernmentIdAndProductShortName(
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
        AccountBO accountBo = this.accountPersistence.findLoanByClientPhoneNumberAndProductShortName(
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
            if (e.getMessage().contains("org.hibernate.NonUniqueResultException")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AccountReferenceDto lookupSavingsAccountReferenceFromClientPhoneNumberAndSavingsProductShortName(
            String phoneNumber, String savingsProductShortName) throws Exception {
        AccountBO accountBo = this.accountPersistence.findSavingsByClientPhoneNumberAndProductShortName(
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
            if (e.getMessage().contains("org.hibernate.NonUniqueResultException")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BigDecimal getTotalPaymentDueAmount(AccountReferenceDto account) throws Exception {
        AccountBO accountBo = this.accountPersistence.getAccount(account.getAccountId());
        return accountBo.getTotalAmountDue().getAmount();
    }

    @Override
    public Object getMifosConfiguration(String propertyKey) {
        ConfigurationManager cfgMng = ConfigurationManager.getInstance();
        return cfgMng.getProperty(propertyKey);
    }

	@Override
	public boolean receiptExists(String receiptNumber) throws Exception {
        List<AccountPaymentEntity> existentPaymentsWIthGivenReceiptNumber = this.accountPersistence.findAccountPaymentsByReceiptNumber(receiptNumber);
		return existentPaymentsWIthGivenReceiptNumber != null && !existentPaymentsWIthGivenReceiptNumber.isEmpty();
	}
}

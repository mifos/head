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

package org.mifos.application.collectionsheet.business.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.persistance.service.BulkEntryPersistenceService;
import org.mifos.application.collectionsheet.persistence.BulkEntryPersistence;
import org.mifos.application.collectionsheet.util.helpers.BulkEntrySavingsCache;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.client.business.service.ClientService;
import org.mifos.application.customer.client.business.service.StandardClientService;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

public class CollectionSheetEntryBusinessService implements BusinessService {
    private final MifosLogger logger = MifosLogManager.getLogger(CollectionSheetEntryBusinessService.class.getName());

    private final BulkEntryPersistenceService bulkEntryPersistanceService;
    private final CustomerPersistence customerPersistence;
    private ClientService clientService;

    public CollectionSheetEntryBusinessService() {
        bulkEntryPersistanceService = new BulkEntryPersistenceService();
        customerPersistence = new CustomerPersistence();
    }

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return new CollectionSheetEntryGridDto(userContext);
    }

    public Date getLastMeetingDateForCustomer(Integer customerId) throws ServiceException {
        try {
            return customerPersistence.getLastMeetingDateForCustomer(customerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public void setData(List<CollectionSheetEntryView> customerViews, Map<Integer, BulkEntrySavingsCache> savingsCache,
            List<String> savingsDepNames, List<String> savingsWithNames,
            Short personnelId, String receiptId,
            Short paymentId, Date receiptDate,
            Date transactionDate) {

        for (CollectionSheetEntryView parent : customerViews) {

            setSavingsDepositDetails(parent.getSavingsAccountDetails(), personnelId, receiptId, paymentId, receiptDate,
                    transactionDate, savingsDepNames, parent.getCustomerDetail().getCustomerLevelId(), parent
                            .getCustomerDetail().getCustomerId(), savingsCache);

            setSavingsWithdrawalsDetails(parent.getSavingsAccountDetails(), personnelId, receiptId, paymentId,
                    receiptDate, transactionDate, savingsWithNames, parent.getCustomerDetail().getCustomerId(),
                    savingsCache);

        }
    }

    private Double getDoubleValue(String str) {
        return StringUtils.isNullAndEmptySafe(str) ? LocalizationConverter.getInstance()
                .getDoubleValueForCurrentLocale(str) : null;
    }

    private void setSavingsWithdrawalsDetails(List<SavingsAccountView> accountViews, Short personnelId,
            String receiptId, Short paymentId, Date receiptDate, Date transactionDate, List<String> accountNums,
            Integer customerId, Map<Integer, BulkEntrySavingsCache> savings) {
        if (null != accountViews) {
            for (SavingsAccountView accountView : accountViews) {
                String amount = accountView.getWithDrawalAmountEntered();
                if (null != amount && !"".equals(amount.trim()) && !getDoubleValue(amount).equals(0.0)) {
                    try {
                        setSavingsWithdrawalAccountDetails(accountView, personnelId, receiptId, paymentId, receiptDate,
                                transactionDate, customerId, savings);
                    } catch (ServiceException be) {
                        if (savings.containsKey(accountView.getAccountId())) {
                            savings.get(accountView.getAccountId()).setYesNoFlag(YesNoFlag.NO);
                        }
                        accountNums.add((String) (be.getValues()[0]));

                    } catch (Exception e) {
                        if (savings.containsKey(accountView.getAccountId())) {
                            savings.get(accountView.getAccountId()).setYesNoFlag(YesNoFlag.NO);
                        }
                        accountNums.add(accountView.getAccountId().toString());
                    } finally {
                        StaticHibernateUtil.closeSession();
                    }
                }
            }
        }
    }

    private void setSavingsDepositDetails(List<SavingsAccountView> accountViews, Short personnelId, String receiptId,
            Short paymentId, Date receiptDate, Date transactionDate, List<String> accountNums, Short levelId,
            Integer customerId, Map<Integer, BulkEntrySavingsCache> savings) {

        if (null != accountViews) {

            for (SavingsAccountView accountView : accountViews) {
                String amount = accountView.getDepositAmountEntered();
                if (null != amount && !getDoubleValue(amount).equals(0.0)) {
                    if ((!savings.containsKey(accountView.getAccountId()))
                            || (savings.containsKey(accountView.getAccountId()) && (!savings.get(
                                    accountView.getAccountId()).getYesNoFlag().equals(YesNoFlag.NO)))) {
                        try {

                            boolean isCenterGroupIndvAccount = false;
                            if (levelId.equals(CustomerLevel.CENTER.getValue())
                                    || (levelId.equals(CustomerLevel.GROUP.getValue()) && accountView
                                            .getSavingsOffering().getRecommendedAmntUnit().getId().equals(
                                                    RecommendedAmountUnit.PER_INDIVIDUAL.getValue()))) {
                                isCenterGroupIndvAccount = true;
                            }

                            setSavingsDepositAccountDetails(accountView, personnelId, receiptId, paymentId,
                                    receiptDate, transactionDate, isCenterGroupIndvAccount, customerId, savings);

                        } catch (ServiceException be) {
                            if (savings.containsKey(accountView.getAccountId())) {
                                savings.get(accountView.getAccountId()).setYesNoFlag(YesNoFlag.NO);
                            }
                            accountNums.add((String) (be.getValues()[0]));

                        } catch (Exception e) {
                            if (savings.containsKey(accountView.getAccountId())) {
                                savings.get(accountView.getAccountId()).setYesNoFlag(YesNoFlag.NO);
                            }
                            accountNums.add(accountView.getAccountId().toString());

                        } finally {
                            StaticHibernateUtil.closeSession();
                        }
                    }
                }
            }
        }
    }

    public void saveData(List<LoanAccountsProductView> accountViews, Short personnelId, String receiptId,
            Short paymentId, Date receiptDate, Date transactionDate, List<String> accountNums, List<SavingsBO> savings,
            List<String> savingsNames, List<ClientBO> clients, List<String> customerNames,
            List<CustomerAccountView> customerAccounts, List<String> customerAccountNums,
            List<CollectionSheetEntryView> collectionSheetEntryViews) {
        logger.debug("Running non-threaded saveData");

        StaticHibernateUtil.startTransaction();
        saveAttendance(collectionSheetEntryViews, transactionDate);
        StaticHibernateUtil.commitTransaction();

        saveMultipleLoanAccounts(accountViews, customerAccountNums, personnelId, receiptId, paymentId, receiptDate,
                transactionDate);
        saveMultipleCustomerAccountCollections(customerAccounts, customerAccountNums, personnelId, receiptId,
                paymentId, receiptDate, transactionDate);
        saveSavingsAccount(savings, savingsNames);
    }

    private void saveAttendance(List<CollectionSheetEntryView> collectionSheetEntryViews, Date transactionDate) {
        ClientService clientService = getClientService();
        for (CollectionSheetEntryView collectionSheetEntryView : collectionSheetEntryViews) {
            Short levelId = collectionSheetEntryView.getCustomerDetail().getCustomerLevelId();
            if (levelId.equals(CustomerLevel.CLIENT.getValue())) {
                try {
                    ClientAttendanceDto clientAttendanceDto = new ClientAttendanceDto(collectionSheetEntryView
                            .getCustomerDetail().getCustomerId(), new LocalDate(transactionDate), AttendanceType
                            .fromShort(collectionSheetEntryView.getAttendence()));
                    clientService.setClientAttendance(clientAttendanceDto);
                } catch (ServiceException e) {
                    throw new MifosRuntimeException("Unrecoverable service error trying to save client attendance: ", e);
                }
            }
        }
    }

    private void saveSavingsAccount(List<SavingsBO> savings, List<String> customerNames) {
        for (SavingsBO saving : savings) {
            try {
                saveSavingsAccount(saving);
                StaticHibernateUtil.commitTransaction();
            } catch (ServiceException e) {
                StaticHibernateUtil.rollbackTransaction();
                customerNames.add(saving.getGlobalAccountNum());
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }
    }

    public void saveLoanAccount(LoanAccountsProductView loanAccountsProductView, Short personnelId, String receiptId,
            Short paymentId, Date receiptDate, Date transactionDate) throws ServiceException {
        for (LoanAccountView accountView : loanAccountsProductView.getLoanAccountViews()) {
            Integer accountId = accountView.getAccountId();
            if (accountView.isDisbursalAccount()) {
                saveLoanDisbursement(accountId, personnelId, receiptId, paymentId, transactionDate,
                        loanAccountsProductView.getDisBursementAmountEntered(), receiptDate);
            } else {
                saveLoanAccountPayment(accountId, personnelId, receiptId, paymentId, receiptDate, transactionDate,
                        loanAccountsProductView, accountView);
            }
        }
    }

    public void setSavingsDepositAccountDetails(SavingsAccountView accountView, Short personnelId, String receiptId,
            Short paymentId, Date receiptDate, Date transactionDate, boolean isCenterGroupIndvAccount,
            Integer customerId, Map<Integer, BulkEntrySavingsCache> savings) throws ServiceException {

        final Integer accountId = accountView.getAccountId();

        final PaymentData accountPaymentDataView = getSavingsAccountPaymentData(accountView, customerId, personnelId,
                receiptId, paymentId, receiptDate, transactionDate, isCenterGroupIndvAccount);

        saveSavingsAccountPayment(accountId, accountPaymentDataView, savings);
    }

    public void setSavingsWithdrawalAccountDetails(SavingsAccountView accountView, Short personnelId, String receiptId,
            Short paymentId, Date receiptDate, Date transactionDate, Integer customerId,
            Map<Integer, BulkEntrySavingsCache> savings) throws ServiceException {
        if (null != accountView) {
            Integer accountId = accountView.getAccountId();
            if (null != accountId) {
                PaymentData accountPaymentDataView = getWithdrawalSavingsPaymentDataView(accountView, customerId,
                        personnelId, receiptId, paymentId, receiptDate, transactionDate);
                saveSavingsWithdrawal(accountId, accountPaymentDataView, savings);
            }
        }
    }

    public void saveCustomerAccountCollections(CustomerAccountView customerAccountView, Short personnelId,
            String receiptId, Short paymentId, Date receiptDate, Date transactionDate) throws ServiceException {
        Integer accountId = customerAccountView.getAccountId();
        PaymentData accountPaymentDataView = getCustomerAccountPaymentDataView(customerAccountView
                .getAccountActionDates(), customerAccountView.getTotalAmountDue(), personnelId, receiptId, paymentId,
                receiptDate, transactionDate);
        AccountBO account = null;
        try {
            account = getAccount(accountId, AccountTypes.CUSTOMER_ACCOUNT);
            account.applyPaymentWithPersist(accountPaymentDataView);
        } catch (AccountException ae) {
            throw new ServiceException("errors.update", ae, new String[] { account.getGlobalAccountNum() });
        }
    }

   public void saveLoanAccount(LoanBO loan) throws ServiceException {
        try {
            new BulkEntryPersistence().createOrUpdate(loan);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void saveSavingsAccount(SavingsBO savings) throws ServiceException {
        try {
            new BulkEntryPersistence().createOrUpdate(savings);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    private AccountBO getAccount(Integer accountId, AccountTypes type) throws ServiceException {
        AccountBO account = null;
        try {
            if (type.equals(AccountTypes.LOAN_ACCOUNT)) {
                account = bulkEntryPersistanceService.getLoanAccountWithAccountActionsInitialized(accountId);
            } else if (type.equals(AccountTypes.SAVINGS_ACCOUNT)) {
                account = bulkEntryPersistanceService.getSavingsAccountWithAccountActionsInitialized(accountId);
            } else if (type.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
                account = bulkEntryPersistanceService.getCustomerAccountWithAccountActionsInitialized(accountId);
            }
        } catch (PersistenceException e) {
            throw new ServiceException("errors.update", e, new String[] { accountId.toString() });
        }
        return account;
    }

    private CustomerBO getCustomer(Integer customerId) throws ServiceException {
        try {
            return bulkEntryPersistanceService.getCustomer(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException("errors.update", pe, new String[] { customerId.toString() });
        }
    }

    private PersonnelBO getPersonnel(Short personnelId) throws ServiceException {
        try {
            return bulkEntryPersistanceService.getPersonnel(personnelId);
        } catch (PersistenceException pe) {
            throw new ServiceException("errors.update", pe, new String[] { personnelId.toString() });
        }
    }

    private void saveLoanDisbursement(Integer accountId, Short personnelId, String receiptId, Short paymentId,
            Date transactionDate, String disbursementAmountEntered, Date receiptDate) throws ServiceException {
        Double amount = getDoubleValue(disbursementAmountEntered);
        if ((amount != null) && (amount.doubleValue() > 0)) {
            LoanBO account = null;
            try {
                account = (LoanBO) getAccount(accountId, AccountTypes.LOAN_ACCOUNT);
                account.disburseLoan(receiptId, transactionDate, paymentId, getPersonnel(personnelId), receiptDate,
                        paymentId);
            } catch (Exception ae) {
                throw new ServiceException("errors.update", ae, new String[] { account.getGlobalAccountNum() });
            }
        }
    }

    private void saveLoanAccountPayment(Integer accountId, Short personnelId, String receiptId, Short paymentId,
            Date receiptDate, Date transactionDate, LoanAccountsProductView loanAccountsProductView,
            LoanAccountView loanAccountView) throws ServiceException {
        Double amount = getDoubleValue(loanAccountsProductView.getEnteredAmount());
        if ((amount != null) && (amount > 0.0)) {
            Money enteredAmount;
            if (loanAccountsProductView.getLoanAccountViews().size() > 1) {
                enteredAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), String
                        .valueOf(loanAccountView.getTotalAmountDue()));
            } else {
                enteredAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                        loanAccountsProductView.getEnteredAmount());
            }
            PaymentData paymentData = getLoanAccountPaymentData(loanAccountView.getAccountTrxnDetails(), enteredAmount,
                    personnelId, receiptId, paymentId, receiptDate, transactionDate);
            AccountBO account = null;
            try {
                account = getAccount(accountId, AccountTypes.LOAN_ACCOUNT);
                account.applyPaymentWithPersist(paymentData);
            } catch (Exception ae) {
                throw new ServiceException("errors.update", ae, new String[] { account.getGlobalAccountNum() });
            }
        }
    }

    private PaymentData getLoanAccountPaymentData(List<CollectionSheetEntryInstallmentView> accountActions,
            Money totalAmount, Short personnelId, String receiptNum, Short paymentId, Date receiptDate,
            Date transactionDate) throws ServiceException {
        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, getPersonnel(personnelId), paymentId,
                transactionDate);
        paymentData.setRecieptDate(receiptDate);
        paymentData.setRecieptNum(receiptNum);
        return paymentData;
    }

    private void saveSavingsAccountPayment(Integer accountId, PaymentData accountPaymentDataView,
            Map<Integer, BulkEntrySavingsCache> savings) throws ServiceException {
        AccountBO account = null;
        try {
            if (savings.containsKey(accountId)) {
                account = savings.get(accountId).getAccount();
            } else {
                account = getAccount(accountId, AccountTypes.SAVINGS_ACCOUNT);
            }
            boolean persist = false;
            account.applyPayment(accountPaymentDataView, persist);
            savings.put(account.getAccountId(), new BulkEntrySavingsCache((SavingsBO) account, YesNoFlag.YES));
        } catch (Exception ae) {
            if (savings.containsKey(accountId)) {
                savings.get(accountId).setYesNoFlag(YesNoFlag.NO);
            }
            throw new ServiceException("errors.update", ae, new String[] { account.getGlobalAccountNum() });
        }
    }

    private PaymentData getSavingsAccountPaymentData(SavingsAccountView savingsAccountView, Integer customerId,
            Short personnelId, String receiptNum, Short paymentId, Date receiptDate, Date transactionDate,
            boolean isCenterGroupIndvAccount) throws ServiceException {

        final Money enteredAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                savingsAccountView
                .getDepositAmountEntered());
        final PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, getPersonnel(personnelId),
                paymentId,
                transactionDate);

        if (!isCenterGroupIndvAccount && savingsAccountView.getAccountTrxnDetails().size() > 0) {
            buildIndividualAccountSavingsPayments(paymentData, savingsAccountView, enteredAmount);
        }

        paymentData.setCustomer(getCustomer(customerId));
        paymentData.setRecieptDate(receiptDate);
        paymentData.setRecieptNum(receiptNum);
        return paymentData;
    }

    private void buildIndividualAccountSavingsPayments(PaymentData paymentData, SavingsAccountView savingsAccountView,
            Money enteredAmount) {
        for (CollectionSheetEntryInstallmentView accountActionDate : savingsAccountView.getAccountTrxnDetails()) {
            SavingsPaymentData savingsPaymentData = new SavingsPaymentData(accountActionDate);
            paymentData.addAccountPaymentData(savingsPaymentData);
        }
    }

    private void saveSavingsWithdrawal(Integer accountId, PaymentData accountPaymentDataView,
            Map<Integer, BulkEntrySavingsCache> savings) throws ServiceException {
        SavingsBO account = null;
        try {
            if (savings.containsKey(accountId)) {
                account = savings.get(accountId).getAccount();
            } else {
                account = (SavingsBO) getAccount(accountId, AccountTypes.SAVINGS_ACCOUNT);
            }
            boolean persist = false;
            account.withdraw(accountPaymentDataView, persist);
            savings.put(account.getAccountId(), new BulkEntrySavingsCache(account, YesNoFlag.YES));
        } catch (Exception ae) {
            if (savings.containsKey(accountId)) {
                savings.get(accountId).setYesNoFlag(YesNoFlag.NO);
            }
            throw new ServiceException("errors.update", ae, new String[] { account.getGlobalAccountNum() });
        }
    }

    private PaymentData getWithdrawalSavingsPaymentDataView(SavingsAccountView savingsAccountView, Integer customerId,
            Short personnelId, String receiptNum, Short paymentId, Date receiptDate, Date transactionDate)
            throws ServiceException {
        Money enteredAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(), savingsAccountView
                .getWithDrawalAmountEntered());
        PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, getPersonnel(personnelId), paymentId,
                transactionDate);
        paymentData.setCustomer(getCustomer(customerId));
        paymentData.setRecieptDate(receiptDate);
        paymentData.setRecieptNum(receiptNum);
        return paymentData;
    }

    private PaymentData getCustomerAccountPaymentDataView(List<CollectionSheetEntryInstallmentView> accountActions,
            Money totalAmount, Short personnelId, String receiptNum, Short paymentId, Date receiptDate,
            Date transactionDate) throws ServiceException {
        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, getPersonnel(personnelId), paymentId,
                transactionDate);
        paymentData.setRecieptDate(receiptDate);
        paymentData.setRecieptNum(receiptNum);
        for (CollectionSheetEntryInstallmentView actionDate : accountActions) {
            CustomerAccountPaymentData customerAccountPaymentData = new CustomerAccountPaymentData(actionDate);
            paymentData.addAccountPaymentData(customerAccountPaymentData);
        }
        return paymentData;
    }

    public void saveMultipleLoanAccounts(List<LoanAccountsProductView> accountViews, List<String> accountNums,
            Short personnelId, String receiptId, Short paymentId, Date receiptDate, Date transactionDate) {
        for (LoanAccountsProductView loanAccountsProductView : accountViews) {
            try {
                saveLoanAccount(loanAccountsProductView, personnelId, receiptId, paymentId, receiptDate,
                        transactionDate);
                StaticHibernateUtil.commitTransaction();
            } catch (ServiceException be) {
                accountNums.add((String) (be.getValues()[0]));
                StaticHibernateUtil.rollbackTransaction();
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }
    }

    public void saveMultipleCustomerAccountCollections(List<CustomerAccountView> customerAccounts,
            List<String> accountNums, Short personnelId, String receiptId, Short paymentId, Date receiptDate,
            Date transactionDate) {
        for (CustomerAccountView customerAccountView : customerAccounts) {
            if (null != customerAccountView) {
                String amount = customerAccountView.getCustomerAccountAmountEntered();
                if (null != amount
                        && !LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(amount).equals(0.0)) {
                    try {
                        saveCustomerAccountCollections(customerAccountView, personnelId, receiptId, paymentId,
                                receiptDate, transactionDate);
                        StaticHibernateUtil.commitTransaction();
                    } catch (ServiceException be) {
                        accountNums.add((String) (be.getValues()[0]));
                        StaticHibernateUtil.rollbackTransaction();
                    } catch (Exception e) {
                        accountNums.add(customerAccountView.getAccountId().toString());
                        StaticHibernateUtil.rollbackTransaction();
                    } finally {
                        StaticHibernateUtil.closeSession();
                    }
                }
            }
        }
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public ClientService getClientService() {
        if (clientService == null) {
            clientService = new StandardClientService();
            clientService.setClientAttendanceDao(new StandardClientAttendanceDao());
        }
        return clientService;
    }

}

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

package org.mifos.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.config.AccountingRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class LoanBusinessService implements BusinessService {

    private LoanPersistence loanPersistence;
    private ConfigurationBusinessService configService;
    private AccountBusinessService accountBusinessService;
    private HolidayService holidayService;
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;


    public LoanPersistence getLoanPersistence() {
        if (loanPersistence == null) {
            loanPersistence = new LoanPersistence();
        }
        return loanPersistence;
    }

    public ConfigurationBusinessService getConfigService() {
        if (configService == null) {
            configService = new ConfigurationBusinessService();
        }
        return configService;
    }

    public AccountBusinessService getAccountBusinessService() {
        if (accountBusinessService == null) {
            accountBusinessService = new AccountBusinessService();
        }
        return accountBusinessService;
    }

    /**
     * Use {@link org.mifos.application.servicefacade.DependencyInjectedServiceLocator} to create instances of this service.
     * Does not instantiate HolidayService & ScheduleCalculatorAdaptor.
     */
    @Deprecated
    public LoanBusinessService() {
        this(new LoanPersistence(), new ConfigurationBusinessService(), new AccountBusinessService(), null, null);
    }

    public LoanBusinessService(LoanPersistence loanPersistence, ConfigurationBusinessService configService,
                               AccountBusinessService accountBusinessService, HolidayService holidayService,
                               ScheduleCalculatorAdaptor scheduleCalculatorAdaptor) {
        this.loanPersistence = loanPersistence;
        this.configService = configService;
        this.accountBusinessService = accountBusinessService;
        this.holidayService = holidayService;
        this.scheduleCalculatorAdaptor = scheduleCalculatorAdaptor;
    }

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
        return null;
    }

    /**
     * @deprecated use {@link LoanDao#findByGlobalAccountNum(String)}
     */
    @Deprecated
    public LoanBO findBySystemId(final String accountGlobalNum) throws ServiceException {
        try {
            return getLoanPersistence().findBySystemId(accountGlobalNum);
        } catch (PersistenceException e) {
            throw new ServiceException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
                    new Object[] { accountGlobalNum });
        }
    }

    public List<LoanBO> findIndividualLoans(final String accountId) throws ServiceException {
        try {
            return getLoanPersistence().findIndividualLoans(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
                    new Object[] { accountId });
        }
    }

    /**
     * @deprecated - use {@link LoanDao#findById(Integer)}
     */
    @Deprecated
    public LoanBO getAccount(final Integer accountId) throws ServiceException {
        try {
            return getLoanPersistence().getAccount(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<LoanBO> getLoanAccountsActiveInGoodBadStanding(final Integer customerId) throws ServiceException {
        try {
            return getLoanPersistence().getLoanAccountsActiveInGoodBadStanding(customerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Short getLastPaymentAction(final Integer accountId) throws ServiceException {
        try {
            return getLoanPersistence().getLastPaymentAction(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<LoanBO> getSearchResults(final String officeId, final String personnelId, final String currentStatus)
            throws ServiceException {
        try {
            return getLoanPersistence().getSearchResults(officeId, personnelId, currentStatus);
        } catch (PersistenceException he) {
            throw new ServiceException(he);
        }
    }

    public List<LoanBO> getAllLoanAccounts() throws ServiceException {
        try {
            return getLoanPersistence().getAllLoanAccounts();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public void initialize(final Object object) {
        getLoanPersistence().initialize(object);
    }

    public List<LoanBO> getAllChildrenForParentGlobalAccountNum(final String globalAccountNum) throws ServiceException {
        return findIndividualLoans(findBySystemId(globalAccountNum).getAccountId().toString());
    }

    public List<LoanBO> getActiveLoansForAllClientsAssociatedWithGroupLoan(final LoanBO loan) throws ServiceException {
        List<LoanBO> activeLoans = new ArrayList<LoanBO>();
        Collection<CustomerBO> clients = getClientsAssociatedWithGroupLoan(loan);
        if (clients != null && clients.size() > 0) {
            for (CustomerBO customerBO : clients) {
                for (AccountBO accountBO : customerBO.getAccounts()) {
                    if (accountBO.isActiveLoanAccount()) {
                        activeLoans.add((LoanBO) accountBO);
                    }
                }
            }
        }
        return activeLoans;
    }

    private Collection<CustomerBO> getClientsAssociatedWithGroupLoan(final LoanBO loan) throws ServiceException {
        Collection<CustomerBO> clients;

        if (getConfigService().isGlimEnabled()) {
            clients = getAccountBusinessService().getCoSigningClientsForGlim(loan.getAccountId());
        } else {
            clients = loan.getCustomer().getChildren();
        }
        return clients;
    }

    public List<RepaymentScheduleInstallment> applyDailyInterestRatesWhereApplicable(LoanScheduleGenerationDto loanScheduleGenerationDto, Locale locale) {
        LoanBO loanBO = loanScheduleGenerationDto.getLoanBO();
        List<RepaymentScheduleInstallment> installments = loanBO.toRepaymentScheduleDto(locale);
        return applyDailyInterestRatesWhereApplicable(loanScheduleGenerationDto, installments);
    }

    private boolean dailyInterestRatesApplicable(LoanScheduleGenerationDto loanScheduleGenerationDto, LoanBO loanBO) {
        return loanScheduleGenerationDto.isVariableInstallmentsAllowed() || loanBO.isDecliningBalanceInterestRecalculation();
    }

    public void applyDailyInterestRates(LoanScheduleGenerationDto loanScheduleGenerationDto) {
        Double dailyInterestFactor = loanScheduleGenerationDto.getInterestRate() / (AccountingRules.getNumberOfInterestDays() * 100d);
        Money principalOutstanding = loanScheduleGenerationDto.getLoanAmountValue();
        Money runningPrincipal = new Money(loanScheduleGenerationDto.getLoanAmountValue().getCurrency());
        Date initialDueDate = loanScheduleGenerationDto.getDisbursementDate();
        int installmentIndex, numInstallments;
        for (installmentIndex = 0, numInstallments = loanScheduleGenerationDto.getInstallments().size(); installmentIndex < numInstallments - 1; installmentIndex++) {
            RepaymentScheduleInstallment installment = loanScheduleGenerationDto.getInstallments().get(installmentIndex);
            Date currentDueDate = installment.getDueDateValue();
            long duration = DateUtils.getNumberOfDaysBetweenTwoDates(currentDueDate, initialDueDate);
            Money fees = installment.getFees();
            Money interest = computeInterestAmount(dailyInterestFactor, principalOutstanding, installment, duration);
            Money total = installment.getTotalValue();
            Money principal = total.subtract(interest.add(fees));
            installment.setPrincipalAndInterest(interest, principal);
            initialDueDate = currentDueDate;
            principalOutstanding = principalOutstanding.subtract(principal);
            runningPrincipal = runningPrincipal.add(principal);
        }

        RepaymentScheduleInstallment lastInstallment = loanScheduleGenerationDto.getInstallments().get(installmentIndex);
        long duration = DateUtils.getNumberOfDaysBetweenTwoDates(lastInstallment.getDueDateValue(), initialDueDate);
        Money interest = computeInterestAmount(dailyInterestFactor, principalOutstanding, lastInstallment, duration);
        Money fees = lastInstallment.getFees();
        Money principal = loanScheduleGenerationDto.getLoanAmountValue().subtract(runningPrincipal);
        Money total = principal.add(interest).add(fees);
        lastInstallment.setTotalAndTotalValue(total);
        lastInstallment.setPrincipalAndInterest(interest, principal);
    }

    private Money computeInterestAmount(Double dailyInterestFactor, Money principalOutstanding,
                                        RepaymentScheduleInstallment installment, long duration) {
        Double interestForInstallment = dailyInterestFactor * duration * principalOutstanding.getAmountDoubleValue();
        return new Money(installment.getCurrency(), interestForInstallment);
    }

    public void adjustInstallmentGapsPostDisbursal(List<RepaymentScheduleInstallment> installments,
                                                   Date oldDisbursementDate, Date newDisbursementDate, Short officeId) {
        Date oldPrevDate = oldDisbursementDate, newPrevDate = newDisbursementDate;
        for (RepaymentScheduleInstallment installment : installments) {
            Date currentDueDate = installment.getDueDateValue();
            long delta = DateUtils.getNumberOfDaysBetweenTwoDates(currentDueDate, oldPrevDate);
            Date newDueDate = DateUtils.addDays(newPrevDate, (int) delta);
            installment.setDueDateValue(holidayService.getNextWorkingDay(newDueDate, officeId));
            oldPrevDate = currentDueDate;
            newPrevDate = installment.getDueDateValue();
        }
    }

    public void adjustDatesForVariableInstallments(boolean variableInstallmentsAllowed, List<RepaymentScheduleInstallment> originalInstallments,
                                                   Date oldDisbursementDate, Date newDisbursementDate, Short officeId) {
        if (variableInstallmentsAllowed) {
            adjustInstallmentGapsPostDisbursal(originalInstallments, oldDisbursementDate, newDisbursementDate, officeId);
        }
    }


    public List<RepaymentScheduleInstallment> applyDailyInterestRatesWhereApplicable(
            LoanScheduleGenerationDto loanScheduleGenerationDto, List<RepaymentScheduleInstallment> installments) {
        LoanBO loanBO = loanScheduleGenerationDto.getLoanBO();
        if (dailyInterestRatesApplicable(loanScheduleGenerationDto, loanBO)) {
            loanScheduleGenerationDto.setInstallments(installments);
            applyDailyInterestRates(loanScheduleGenerationDto);
            loanBO.copyInstallmentSchedule(installments);
        }
        return installments;
    }

    public void applyPayment(PaymentData paymentData, LoanBO loanBO, AccountPaymentEntity accountPaymentEntity) {
        Money balance = paymentData.getTotalAmount();
        PersonnelBO personnel = paymentData.getPersonnel();
        Date transactionDate = paymentData.getTransactionDate();
        if (loanBO.isDecliningBalanceInterestRecalculation()) {
            scheduleCalculatorAdaptor.applyPayment(loanBO, balance, transactionDate, personnel, accountPaymentEntity);
        } else {
            for (AccountActionDateEntity accountActionDate : loanBO.getAccountActionDatesSortedByInstallmentId()) {
                balance = ((LoanScheduleEntity) accountActionDate).applyPayment(accountPaymentEntity, balance, personnel, transactionDate);
            }
        }
    }

    public void persistOriginalSchedule(LoanBO loan) throws PersistenceException {
        Collection<LoanScheduleEntity> loanScheduleEntities = loan.getLoanScheduleEntities();
        Collection<OriginalLoanScheduleEntity> originalLoanScheduleEntities = new ArrayList<OriginalLoanScheduleEntity>();
        for (LoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
                   originalLoanScheduleEntities.add(new OriginalLoanScheduleEntity(loanScheduleEntity));
        }
        loanPersistence.saveOriginalSchedule(originalLoanScheduleEntities);
    }

    public List<OriginalLoanScheduleEntity> retrieveOriginalLoanSchedule(Integer accountId) throws PersistenceException {
        return loanPersistence.getOriginalLoanScheduleEntity(accountId);
    }
}

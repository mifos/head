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
import java.util.List;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanActivityDto;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class LoanBusinessService implements BusinessService {

    private LoanPersistence loanPersistence;
    private ConfigurationBusinessService configService;
    private AccountBusinessService accountBusinessService;

    public LoanPersistence getLoanPersistence() {
        if (loanPersistence == null) {
            loanPersistence = new LoanPersistence();
        }
        return loanPersistence;
    }

    public void setLoanPersistence(final LoanPersistence loanPersistence) {
        this.loanPersistence = loanPersistence;
    }

    public ConfigurationBusinessService getConfigService() {
        if (configService == null) {
            configService = new ConfigurationBusinessService();
        }
        return configService;
    }

    public void setConfigService(final ConfigurationBusinessService configService) {
        this.configService = configService;
    }

    public AccountBusinessService getAccountBusinessService() {
        if (accountBusinessService == null) {
            accountBusinessService = new AccountBusinessService();
        }
        return accountBusinessService;
    }

    public void setAccountBusinessService(final AccountBusinessService accountBusinessService) {
        this.accountBusinessService = accountBusinessService;
    }

    public LoanBusinessService() {
        this(new LoanPersistence(), new ConfigurationBusinessService(), new AccountBusinessService());
    }

    LoanBusinessService(final LoanPersistence loanPersistence, final ConfigurationBusinessService configService,
            final AccountBusinessService accountBusinessService) {
        this.setLoanPersistence(loanPersistence);
        this.setConfigService(configService);
        this.setAccountBusinessService(accountBusinessService);
    }

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
        return null;
    }

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

    public List<LoanActivityDto> getRecentActivityView(final String globalAccountNumber) throws ServiceException {
        LoanBO loanBO = findBySystemId(globalAccountNumber);
        List<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
        List<LoanActivityDto> recentActivityView = new ArrayList<LoanActivityDto>();

        int count = 0;
        for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
            recentActivityView.add(getLoanActivityView(loanActivity));
            if (++count == 3) {
                break;
            }
        }
        return recentActivityView;
    }

    public List<LoanActivityDto> getAllActivityView(final String globalAccountNumber) throws ServiceException {
        LoanBO loanBO = findBySystemId(globalAccountNumber);
        List<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
        List<LoanActivityDto> loanActivityViewSet = new ArrayList<LoanActivityDto>();
        for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
            loanActivityViewSet.add(getLoanActivityView(loanActivity));
        }
        return loanActivityViewSet;
    }

    private LoanActivityDto getLoanActivityView(final LoanActivityEntity loanActivity) {
        LoanActivityDto loanActivityDto = new LoanActivityDto(loanActivity.getAccount().getCurrency());
        loanActivityDto.setId(loanActivity.getAccount().getAccountId());
        loanActivityDto.setActionDate(loanActivity.getTrxnCreatedDate());
        loanActivityDto.setActivity(loanActivity.getComments());
        loanActivityDto.setPrincipal(removeSign(loanActivity.getPrincipal()));
        loanActivityDto.setInterest(removeSign(loanActivity.getInterest()));
        loanActivityDto.setPenalty(removeSign(loanActivity.getPenalty()));
        loanActivityDto.setFees(removeSign(loanActivity.getFee()));
        loanActivityDto.setTotal(removeSign(loanActivity.getFee()).add(removeSign(loanActivity.getPenalty())).add(
                removeSign(loanActivity.getPrincipal())).add(removeSign(loanActivity.getInterest())));
        loanActivityDto.setTimeStamp(loanActivity.getTrxnCreatedDate());
        loanActivityDto.setRunningBalanceInterest(loanActivity.getInterestOutstanding());
        loanActivityDto.setRunningBalancePrinciple(loanActivity.getPrincipalOutstanding());
        loanActivityDto.setRunningBalanceFees(loanActivity.getFeeOutstanding());
        loanActivityDto.setRunningBalancePenalty(loanActivity.getPenaltyOutstanding());

        return loanActivityDto;
    }

    private Money removeSign(final Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }

        return amount;
    }

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
}

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

package org.mifos.application.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class LoanBusinessService implements BusinessService {

    private LoanPersistence loanPersistence;
    private ConfigurationBusinessService configService;
    private AccountBusinessService accountBusinessService;

    public LoanPersistence getLoanPersistence() {
        if(loanPersistence == null) {
            loanPersistence = new LoanPersistence();
        }
        return loanPersistence;
    }

    public void setLoanPersistence(LoanPersistence loanPersistence) {
        this.loanPersistence = loanPersistence;
    }

    public ConfigurationBusinessService getConfigService() {
        if(configService == null) {
            configService = new ConfigurationBusinessService();
        }
        return configService;
    }

    public void setConfigService(ConfigurationBusinessService configService) {
        this.configService = configService;
    }

    public AccountBusinessService getAccountBusinessService() {
        if(accountBusinessService == null){
            accountBusinessService = new AccountBusinessService();
        }
        return accountBusinessService;
    }

    public void setAccountBusinessService(AccountBusinessService accountBusinessService) {
        this.accountBusinessService = accountBusinessService;
    }

    public LoanBusinessService() {
        this(new LoanPersistence(), new ConfigurationBusinessService(), new AccountBusinessService());
    }

    LoanBusinessService(LoanPersistence loanPersistence, ConfigurationBusinessService configService,
            AccountBusinessService accountBusinessService) {
        this.setLoanPersistence(loanPersistence);
        this.setConfigService(configService);
        this.setAccountBusinessService(accountBusinessService);
    }

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public LoanBO findBySystemId(String accountGlobalNum) throws ServiceException {
        try {
            return getLoanPersistence().findBySystemId(accountGlobalNum);
        } catch (PersistenceException e) {
            throw new ServiceException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
                    new Object[] { accountGlobalNum });
        }
    }

    public List<LoanBO> findIndividualLoans(String accountId) throws ServiceException {
        try {
            return getLoanPersistence().findIndividualLoans(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
                    new Object[] { accountId });
        }
    }

    public List<LoanActivityView> getRecentActivityView(String globalAccountNumber, Short localeId)
            throws ServiceException {
        LoanBO loanBO = findBySystemId(globalAccountNumber);
        Set<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
        List<LoanActivityView> recentActivityView = new ArrayList<LoanActivityView>();

        int count = 0;
        for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
            recentActivityView.add(getLoanActivityView(loanActivity));
            if (++count == 3)
                break;
        }
        return recentActivityView;
    }

    public List<LoanActivityView> getAllActivityView(String globalAccountNumber, Short localeId)
            throws ServiceException {
        LoanBO loanBO = findBySystemId(globalAccountNumber);
        Set<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
        List<LoanActivityView> loanActivityViewSet = new ArrayList<LoanActivityView>();
        for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
            loanActivityViewSet.add(getLoanActivityView(loanActivity));
        }
        return loanActivityViewSet;
    }

    private LoanActivityView getLoanActivityView(LoanActivityEntity loanActivity) {
        LoanActivityView loanActivityView = new LoanActivityView();
        loanActivityView.setId(loanActivity.getAccount().getAccountId());
        loanActivityView.setActionDate(loanActivity.getTrxnCreatedDate());
        loanActivityView.setActivity(loanActivity.getComments());
        loanActivityView.setPrincipal(removeSign(loanActivity.getPrincipal()));
        loanActivityView.setInterest(removeSign(loanActivity.getInterest()));
        loanActivityView.setPenalty(removeSign(loanActivity.getPenalty()));
        loanActivityView.setFees(removeSign(loanActivity.getFee()));
        loanActivityView.setTotal(removeSign(loanActivity.getFee()).add(removeSign(loanActivity.getPenalty())).add(
                removeSign(loanActivity.getPrincipal())).add(removeSign(loanActivity.getInterest())));
        loanActivityView.setTimeStamp(loanActivity.getTrxnCreatedDate());
        loanActivityView.setRunningBalanceInterest(loanActivity.getInterestOutstanding());
        loanActivityView.setRunningBalancePrinciple(loanActivity.getPrincipalOutstanding());
        loanActivityView.setRunningBalanceFees(loanActivity.getFeeOutstanding());
        loanActivityView.setRunningBalancePenalty(loanActivity.getPenaltyOutstanding());

        return loanActivityView;
    }

    private Money removeSign(Money amount) {
        if (amount != null && amount.getAmountDoubleValue() < 0)
            return amount.negate();
        else
            return amount;
    }

    public LoanBO getAccount(Integer accountId) throws ServiceException {
        try {
            return getLoanPersistence().getAccount(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<LoanBO> getLoanAccountsActiveInGoodBadStanding(Integer customerId) throws ServiceException {
        try {
            return getLoanPersistence().getLoanAccountsActiveInGoodBadStanding(customerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public Short getLastPaymentAction(Integer accountId) throws ServiceException {
        try {
            return getLoanPersistence().getLastPaymentAction(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<LoanBO> getSearchResults(String officeId, String personnelId, String type, String currentStatus)
            throws ServiceException {
        try {
            return getLoanPersistence().getSearchResults(officeId, personnelId, type, currentStatus);
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

    public void initialize(Object object) {
        getLoanPersistence().initialize(object);
    }

    public List<LoanBO> getAllChildrenForParentGlobalAccountNum(String globalAccountNum) throws ServiceException {
        return findIndividualLoans(findBySystemId(globalAccountNum).getAccountId().toString());
    }

    public List<LoanBO> getActiveLoansForAllClientsAssociatedWithGroupLoan(LoanBO loan) throws ServiceException {
        List<LoanBO> activeLoans = new ArrayList<LoanBO>();
        Collection<CustomerBO> clients = getClientsAssociatedWithGroupLoan(loan);

        for (CustomerBO customerBO : clients) {
            for (AccountBO accountBO : customerBO.getAccounts()) {
                if (accountBO.isActiveLoanAccount()) {
                    activeLoans.add((LoanBO) accountBO);
                }
            }
        }
        return activeLoans;
    }

    private Collection<CustomerBO> getClientsAssociatedWithGroupLoan(LoanBO loan) throws ServiceException {
        Collection<CustomerBO> clients;

        if (getConfigService().isGlimEnabled()) {
            clients = getAccountBusinessService().getCoSigningClientsForGlim(loan.getAccountId());
        } else {
            clients = loan.getCustomer().getChildren();
        }
        return clients;
    }
}

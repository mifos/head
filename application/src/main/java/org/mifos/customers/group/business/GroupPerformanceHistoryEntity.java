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

package org.mifos.customers.group.business;

import static org.mifos.customers.group.business.GroupLoanCounter.TRANSFORM_GROUP_LOAN_COUNTER_TO_LOAN_CYCLE;
import static org.mifos.framework.util.CollectionUtils.find;
import static org.mifos.framework.util.CollectionUtils.select;
import static org.apache.commons.lang.math.NumberUtils.SHORT_ZERO;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerPerformanceHistory;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnDisbursement;
import org.mifos.customers.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnRepayment;
import org.mifos.customers.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnReversal;
import org.mifos.customers.group.business.GroupPerformanceHistoryUpdater.UpdateClientPerfHistoryForGroupLoanOnWriteOff;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Predicate;

public class GroupPerformanceHistoryEntity extends CustomerPerformanceHistory {

    private Integer id;

    private Integer clientCount;

    private Money lastGroupLoanAmount;

    private Money avgLoanForMember;

    private Money totalOutstandingPortfolio;

    private Money totalSavings;

    private Money portfolioAtRisk;

    private GroupBO group;

    /**
     * stores the loan cycle information based on products
     */
    private Set<GroupLoanCounter> loanCounters;

    private ConfigurationBusinessService configService;

    private AccountBusinessService accountBusinessService;

    public GroupPerformanceHistoryEntity(GroupBO group) {
        this(0, null, null, null, null, null);
        this.group = group;
    }

    private GroupPerformanceHistoryEntity(Integer clientCount, Money lastGroupLoanAmount, Money avgLoanForMember,
            Money totalOutstandingPortfolio, Money totalSavings, Money portfolioAtRisk,
            ConfigurationBusinessService configService, AccountBusinessService accountBusinessService) {
        if (null == portfolioAtRisk) {
            this.portfolioAtRisk = null;
        } else {
            this.portfolioAtRisk = portfolioAtRisk;
        }

        if (null == totalOutstandingPortfolio) {
            this.totalOutstandingPortfolio = null;
        } else {
            this.totalOutstandingPortfolio = totalOutstandingPortfolio;
        }

        if (null == totalSavings) {
            this.totalSavings = null;
        } else {
            this.totalSavings = totalSavings;
        }

        if (null == avgLoanForMember) {
            this.avgLoanForMember = null;
        } else {
            this.avgLoanForMember = avgLoanForMember;
        }

        if (null == lastGroupLoanAmount) {
            this.lastGroupLoanAmount = null;
        } else {
            this.lastGroupLoanAmount = lastGroupLoanAmount;
        }

        this.clientCount = clientCount;
        this.configService = configService;
        this.accountBusinessService = accountBusinessService;
        this.loanCounters = new HashSet<GroupLoanCounter>();
        this.id = null;
    }

    GroupPerformanceHistoryEntity(ConfigurationBusinessService configService,
            AccountBusinessService accountBusinessService) {
        this(0, null, null, null, null, null, configService, accountBusinessService);
    }

    public GroupPerformanceHistoryEntity(Integer clientCount, Money lastGroupLoanAmount, Money avgLoanForMember,
            Money totalOutstandingPortfolio, Money totalSavings, Money portfolioAtRisk) {
        this(clientCount, lastGroupLoanAmount, avgLoanForMember, totalOutstandingPortfolio, totalSavings,
                portfolioAtRisk, new ConfigurationBusinessService(), new AccountBusinessService());
    }

    protected GroupPerformanceHistoryEntity() {
        this(0, null, null, null, null, null);
    }

    public Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    private Money getAvgLoanForMember() {
        return avgLoanForMember;
    }

    void setAvgLoanForMember(Money avgLoanForMember) {
        this.avgLoanForMember = avgLoanForMember;
    }

    public Integer getClientCount() {
        return clientCount;
    }

    void setClientCount(Integer clientCount) {
        this.clientCount = clientCount;
    }

    public Money getLastGroupLoanAmount() {
        return lastGroupLoanAmount;
    }

    public void setLastGroupLoanAmount(Money lastGroupLoanAmount) {
        this.lastGroupLoanAmount = lastGroupLoanAmount;
    }

    private Money getTotalOutstandingPortfolio() {
        return totalOutstandingPortfolio;
    }

    void setTotalOutstandingPortfolio(Money totalOutstandingPortfolio) {
        this.totalOutstandingPortfolio = totalOutstandingPortfolio;
    }

    public Money getPortfolioAtRisk() {
        return portfolioAtRisk;
    }

    void setPortfolioAtRisk(Money portfolioAtRisk) {
        this.portfolioAtRisk = portfolioAtRisk;
    }

    private Money getTotalSavings() {
        return totalSavings;
    }

    void setTotalSavings(Money totalSavings) {
        this.totalSavings = totalSavings;
    }

    public GroupBO getGroup() {
        return group;
    }

    void setGroup(GroupBO group) {
        this.group = group;
    }

    public Money getAvgLoanAmountForMember() throws CustomerException {
        Money amountForActiveAccount = new Money(getCurrency());
        Integer countOfActiveLoans = 0;
        List<CustomerBO> clients = getChildren();
        if (clients != null) {
            for (CustomerBO client : clients) {
                Money outstandingLoanAmount = client.getOutstandingLoanAmount(getCurrency());
                if (amountForActiveAccount.getAmount().equals(BigDecimal.ZERO)) {
                    amountForActiveAccount = outstandingLoanAmount;
                } else {
                    amountForActiveAccount = amountForActiveAccount.add(outstandingLoanAmount);
                }
                countOfActiveLoans += client.getActiveLoanCounts();
            }
        }
        if (countOfActiveLoans.intValue() > 0) {
            return amountForActiveAccount.divide(countOfActiveLoans);
        }
        return new Money(getCurrency());
    }

    public Integer getActiveClientCount() throws CustomerException {
        List<CustomerBO> clients = getChildren();
        if (clients != null) {
            return Integer.valueOf(clients.size());
        }
        return Integer.valueOf(0);
    }

    public Money getTotalOutStandingLoanAmount() throws CustomerException {
        Money amount = group.getOutstandingLoanAmount(getCurrency());
        List<CustomerBO> clients = getChildren();
        if (clients != null) {
            for (CustomerBO client : clients) {
                Money outstandingLoanAmount = client.getOutstandingLoanAmount(getCurrency());
                if (amount.getAmount().equals(BigDecimal.ZERO)) {
                    amount = outstandingLoanAmount;
                } else {
                    amount = amount.add(outstandingLoanAmount);
                }
            }
        }
        return amount;
    }

    public Money getTotalSavingsAmount() throws CustomerException {
        Money amount = group.getSavingsBalance(getCurrency());
        List<CustomerBO> clients = getChildren();
        if (clients != null) {
            for (CustomerBO client : clients) {
                Money savingsBalance = client.getSavingsBalance(getCurrency());
                if (amount.getAmount().equals(BigDecimal.ZERO)) {
                    amount = savingsBalance;
                } else {
                    amount = amount.add(savingsBalance);
                }
            }
        }
        return amount;
    }

    public MifosCurrency getCurrency() {
        // TODO: performance history will only work if all accounts use the default (or same?) currency
        return Money.getDefaultCurrency();
    }

    private Money getBalanceForAccountsAtRisk(CustomerBO customer) {
        Money amount = new Money(getCurrency());
        for (AccountBO account : customer.getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) account).isAccountActive()) {
                LoanBO loan = (LoanBO) account;
                if (loan.hasPortfolioAtRisk()) {
                    Money remainingPrincipal = loan.getRemainingPrincipalAmount();
                    if (amount.getAmount().equals(BigDecimal.ZERO)) {
                        amount = remainingPrincipal;
                    } else {
                        amount = amount.add(remainingPrincipal);
                    }
                }
            }
        }
        return amount;
    }

    // this method calculates the PAR using the method
    // LoanBO.hasPortfolioAtRisk() to determine if a loan is in arrears
    public void generatePortfolioAtRisk() throws CustomerException {
        Money amount = getBalanceForAccountsAtRisk(group);
        List<CustomerBO> clients = getChildren();
        if (clients != null) {
            for (CustomerBO client : clients) {
                Money balanceAtRisk = getBalanceForAccountsAtRisk(client);
                if (amount.getAmount().equals(BigDecimal.ZERO)) {
                    amount = balanceAtRisk;
                } else {
                    amount = amount.add(balanceAtRisk);
                }
            }
        }
        Money totalOutstandingLoanAmount = getTotalOutStandingLoanAmount();
        if (!totalOutstandingLoanAmount.getAmount().equals(BigDecimal.ZERO)) {
            setPortfolioAtRisk(new Money(amount.getCurrency(), amount.divide(totalOutstandingLoanAmount)));
        }
    }

    private List<CustomerBO> getChildren() throws CustomerException {
        return group.getChildren(CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
    }

    public void updateLoanCounter(final LoanOfferingBO loanOffering, YesNoFlag yesNoFlag) {
        GroupLoanCounter loanCounter = null;
        try {
            loanCounter = findLoanCounterForProduct(loanOffering);
        } catch (Exception e) {
        }
        if (loanCounter == null) {
            loanCounter = new GroupLoanCounter(this, loanOffering, yesNoFlag);
            loanCounters.add(loanCounter);
        } else {
            loanCounter.updateLoanCounter(yesNoFlag);
        }
    }

    GroupLoanCounter findLoanCounterForProduct(final LoanOfferingBO loanOffering) throws Exception {
        return find(loanCounters, new Predicate<GroupLoanCounter>() {
            public boolean evaluate(GroupLoanCounter loanCounter) throws Exception {
                return loanOffering.isOfSameOffering(loanCounter.getLoanOffering());
            }
        });
    }

    public Short getMaxLoanCycleForProduct(final PrdOfferingBO prdOffering) {
        {
            Set<GroupLoanCounter> loanCounters = getLoanCounters();
            try {
                Collection<Short> loanCyclesForProduct = select(loanCounters, new Predicate<GroupLoanCounter>() {
                    public boolean evaluate(GroupLoanCounter counter) throws Exception {
                        return counter.isOfSameProduct(prdOffering);
                    }
                }, TRANSFORM_GROUP_LOAN_COUNTER_TO_LOAN_CYCLE);
                return loanCyclesForProduct.isEmpty() ? SHORT_ZERO : Collections.max(loanCyclesForProduct);
            } catch (Exception e) {
                return SHORT_ZERO;
            }
        }
    }

    public void updateOnDisbursement(LoanBO loan, Money disburseAmount) throws AccountException {
        setLastGroupLoanAmount(disburseAmount);
        LoanOfferingBO loanOffering = loan.getLoanOffering();
        updateLoanCounter(loanOffering, YesNoFlag.YES);
        try {
            if (configService.isGlimEnabled()) {
                CollectionUtils.forAllDo(accountBusinessService.getCoSigningClientsForGlim(loan.getAccountId()),
                        new UpdateClientPerfHistoryForGroupLoanOnDisbursement(loan));
            }
        } catch (ServiceException e) {
            throw new AccountException(e);
        }
    }

    public Set<GroupLoanCounter> getLoanCounters() {
        return loanCounters;
    }

    public void updateOnWriteOff(LoanBO loan) throws AccountException {
        updateLoanCounter(loan.getLoanOffering(), YesNoFlag.NO);
        try {
            if (configService.isGlimEnabled()) {
                CollectionUtils.forAllDo(accountBusinessService.getCoSigningClientsForGlim(loan.getAccountId()),
                        new UpdateClientPerfHistoryForGroupLoanOnWriteOff(loan));
            }
        } catch (ServiceException e) {
            throw new AccountException(e);
        }
    }

    public void updateOnReversal(LoanBO loan, Money lastLoanAmount) throws AccountException {
        setLastGroupLoanAmount(lastLoanAmount);
        updateLoanCounter(loan.getLoanOffering(), YesNoFlag.NO);
        try {
            if (configService.isGlimEnabled()) {
                CollectionUtils.forAllDo(accountBusinessService.getCoSigningClientsForGlim(loan.getAccountId()),
                        new UpdateClientPerfHistoryForGroupLoanOnReversal(loan));
            }
        } catch (ServiceException e) {
            throw new AccountException(e);
        }
    }

    public void updateOnRepayment(LoanBO loan, Money totalAmount) throws AccountException {
        try {
            if (configService.isGlimEnabled()) {
                CollectionUtils.forAllDo(accountBusinessService.getCoSigningClientsForGlim(loan.getAccountId()),
                        new UpdateClientPerfHistoryForGroupLoanOnRepayment(loan));
            }
        } catch (ServiceException e) {
            throw new AccountException(e);
        }
    }

}

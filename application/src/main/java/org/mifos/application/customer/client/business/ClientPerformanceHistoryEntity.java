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

package org.mifos.application.customer.client.business;

import static org.mifos.application.customer.client.business.LoanCounter.TRANSFORM_LOAN_COUNTER_TO_LOAN_CYCLE;
import static org.mifos.framework.util.CollectionUtils.find;
import static org.mifos.framework.util.CollectionUtils.select;
import static org.apache.commons.lang.math.NumberUtils.SHORT_ZERO;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Predicate;
import java.math.BigDecimal;

public class ClientPerformanceHistoryEntity extends CustomerPerformanceHistory {

    private final Integer id;

    private Set<LoanCounter> loanCounters;

    private Integer noOfActiveLoans;

    private Money lastLoanAmount;

    private Money delinquentPortfolio;

    private Money totalSavings;

    private ClientBO client;

    public void setClient(ClientBO client) {
        this.client = client;
    }

    public ClientPerformanceHistoryEntity(ClientBO client) {
        super();
        this.id = null;
        this.client = client;
        this.loanCounters = new HashSet<LoanCounter>();
        this.noOfActiveLoans = 0;
        this.lastLoanAmount = new Money(getCurrency());
        this.delinquentPortfolio = new Money(getCurrency());
        this.totalSavings = new Money(getCurrency());
    }

    protected ClientPerformanceHistoryEntity() {
        this.id = null;
        this.client = null;
        this.loanCounters = new HashSet<LoanCounter>();
        this.noOfActiveLoans = 0;
        this.lastLoanAmount = null;
        this.delinquentPortfolio = null;
        this.totalSavings = null;
    }

    public Integer getId() {
        return id;
    }

    @SuppressWarnings("unused")
    private Money getDelinquentPortfolio() {
        return delinquentPortfolio;
    }

    @SuppressWarnings("unused")
    private void setDelinquentPortfolio(Money delinquentPortfolio) {
        this.delinquentPortfolio = delinquentPortfolio;
    }

    public Money getLastLoanAmount() {
        return lastLoanAmount;
    }

    public void setLastLoanAmount(Money lastLoanAmount) {
        this.lastLoanAmount = lastLoanAmount;
    }

    public Set<LoanCounter> getLoanCounters() {
        return loanCounters;
    }

    public void setLoanCounters(Set<LoanCounter> loanCounters) {
        this.loanCounters = loanCounters;
    }

    private void addLoanCounter(LoanCounter loanCounter) {
        this.loanCounters.add(loanCounter);
    }

    public Integer getNoOfActiveLoans() {
        return noOfActiveLoans;
    }

    public void setNoOfActiveLoans(Integer noOfActiveLoans) {
        this.noOfActiveLoans = noOfActiveLoans;
    }

    @SuppressWarnings("unused")
    private Money getTotalSavings() {
        return totalSavings;
    }

    public Money getTotalSavingsAmount() {
        return getClient().getSavingsBalance(getCurrency());
    }

    @SuppressWarnings("unused")
    private void setTotalSavings(Money totalSavings) {
        this.totalSavings = totalSavings;
    }

    public ClientBO getClient() {
        return client;
    }

    public void updateLoanCounter(final LoanOfferingBO loanOffering, YesNoFlag counterFlag) {
        if (loanCounters == null)
            loanCounters = new HashSet<LoanCounter>();
        LoanCounter loanCounter = null;
        try {
            loanCounter = findLoanCounterForProduct(loanOffering);
        } catch (Exception e) {
        }
        if (loanCounter == null) {
            loanCounter = new LoanCounter(this, loanOffering, counterFlag);
            addLoanCounter(loanCounter);
        } else {
            loanCounter.updateLoanCounter(counterFlag);
        }
    }

    public LoanCounter findLoanCounterForProduct(final LoanOfferingBO loanOffering) throws Exception {
        return find(loanCounters, new Predicate<LoanCounter>() {
            public boolean evaluate(LoanCounter loanCounter) throws Exception {
                return loanOffering.isOfSameOffering(loanCounter.getLoanOffering());
            }
        });
    }

    public MifosCurrency getCurrency() {
        // TODO: performance history will only work if all accounts use the default (or same?) currency
        return Money.getDefaultCurrency();
    }
    
    public Integer getLoanCycleNumber() {
        Integer loanCount = 0;
        for (LoanCounter loanCounter : getLoanCounters()) {
            if (loanCounter.getLoanOffering().isIncludeInLoanCounter())
                loanCount += loanCounter.getLoanCycleCounter();
        }
        if (client.getHistoricalData() != null) {
            if (client.getHistoricalData().getLoanCycleNumber() == null)
                client.getHistoricalData().setLoanCycleNumber(0);
            loanCount += client.getHistoricalData().getLoanCycleNumber();
        }
        return loanCount;
    }

    public Money getDelinquentPortfolioAmount() {
        // we don't know what currency to use when we start totaling these
        // so don't initialize them with a currency
        Money amountOverDue = null;
        Money totalOutStandingAmount = null;
        for (AccountBO accountBO : client.getAccounts()) {
            if (accountBO.isLoanAccount() && ((LoanBO) accountBO).isAccountActive()) {
                Money totalPrincipalAmountInArrears = ((LoanBO) accountBO).getTotalPrincipalAmountInArrears();
                if (amountOverDue == null) {
                    amountOverDue = totalPrincipalAmountInArrears; 
                } else {
                    amountOverDue = amountOverDue.add(totalPrincipalAmountInArrears);
                }
                Money originalPrincipal = ((LoanBO) accountBO).getLoanSummary().getOriginalPrincipal();
                if (totalOutStandingAmount == null) {
                    totalOutStandingAmount = originalPrincipal;
                } else {
                    totalOutStandingAmount = totalOutStandingAmount.add(originalPrincipal);
                }
            }
        }
        // FIXME: this seems like it should just be returning a BigDecimal rather than a Money object
        if (totalOutStandingAmount != null && !totalOutStandingAmount.getAmount().equals(BigDecimal.ZERO)) {
            return new Money(amountOverDue.getCurrency(), amountOverDue.divide(totalOutStandingAmount));
        }
        return new Money(getCurrency());
    }

    public Short getMaxLoanCycleForProduct(final PrdOfferingBO prdOffering) {
        {
            Set<LoanCounter> clientLoanCounters = getLoanCounters();
            try {
                Collection<Short> loanCyclesForProduct = select(clientLoanCounters, new Predicate<LoanCounter>() {
                    public boolean evaluate(LoanCounter counter) throws Exception {
                        return counter.isOfSameProduct(prdOffering);
                    }
                }, TRANSFORM_LOAN_COUNTER_TO_LOAN_CYCLE);
                return loanCyclesForProduct.isEmpty() ? SHORT_ZERO : Collections.max(loanCyclesForProduct);
            } catch (Exception e) {
                return SHORT_ZERO;
            }
        }
    }

    public void updateOnDisbursement(LoanOfferingBO loanOffering) {
        updateLoanCounter(loanOffering, YesNoFlag.YES);
        setNoOfActiveLoans(getNoOfActiveLoans() + 1);
    }

    public void updateOnWriteOff(LoanOfferingBO loanOffering) {
        updateLoanCounter(loanOffering, YesNoFlag.NO);
        setNoOfActiveLoans(getNoOfActiveLoans() - 1);
    }

    public void updateOnReversal(LoanOfferingBO loanOffering, Money lastLoanAmount) {
        updateCommonHistoryOnReversal(loanOffering);
        setLastLoanAmount(lastLoanAmount);
    }

    public void updateCommonHistoryOnReversal(LoanOfferingBO loanOffering) {
        updateLoanCounter(loanOffering, YesNoFlag.NO);
        setNoOfActiveLoans(getNoOfActiveLoans() - 1);
    }

    public void updateOnRepayment(Money totalAmount) {
        setLastLoanAmount(totalAmount);
        setNoOfActiveLoans(getNoOfActiveLoans() - 1);
    }
}

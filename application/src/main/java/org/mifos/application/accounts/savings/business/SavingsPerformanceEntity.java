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

package org.mifos.application.accounts.savings.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class SavingsPerformanceEntity extends PersistentObject {

    @SuppressWarnings("unused")
    // See Hibernate mapping
    private final Integer id;

    private Money totalDeposits;

    private Money totalWithdrawals;

    private Money totalInterestEarned;

    private Integer missedDeposits;

    private final SavingsBO savings;

    protected SavingsPerformanceEntity() {
        id = null;
        savings = null;
    }

    protected SavingsPerformanceEntity(SavingsBO savings) {
        id = null;
        this.savings = savings;
    }

    public SavingsBO getSavings() {
        return savings;
    }

    public Integer getMissedDeposits() {
        return missedDeposits;
    }

    void setMissedDeposits(Integer missedDeposits) {
        this.missedDeposits = missedDeposits;
    }

    public Money getTotalDeposits() {
        return totalDeposits;
    }

    void setTotalDeposits(Money totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public Money getTotalInterestEarned() {
        return totalInterestEarned;
    }

    void setTotalInterestEarned(Money totalInterstEarned) {
        this.totalInterestEarned = totalInterstEarned;
    }

    public Money getTotalWithdrawals() {
        return totalWithdrawals;
    }

    void setTotalWithdrawals(Money totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    void setPaymentDetails(Money totalAmount) {
        if (totalDeposits == null)
            totalDeposits = new Money(savings.getCurrency());
        totalDeposits = totalDeposits.add(totalAmount);
    }

    void setWithdrawDetails(Money totalAmount) {
        if (totalWithdrawals == null)
            totalWithdrawals = new Money(savings.getCurrency());
        totalWithdrawals = totalWithdrawals.add(totalAmount);
    }

    void setTotalInterestDetails(Money totalAmount) {
        if (totalInterestEarned == null)
            totalInterestEarned = new Money(savings.getCurrency());
        totalInterestEarned = totalInterestEarned.add(totalAmount);
    }

    void addMissedDeposits(int missedDeposits) {
        if (this.missedDeposits == null) {
            this.missedDeposits = Integer.valueOf(0);
        }
        this.missedDeposits = this.missedDeposits + missedDeposits;
    }

}

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

package org.mifos.accounts.savings.business;

import java.math.BigDecimal;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyCompositeUserType;
import org.mifos.framework.util.helpers.MoneyUtils;

public class SavingsPerformanceEntity extends AbstractEntity {

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
        if (this.missedDeposits == null) {
            this.missedDeposits = 0;
        }
        return missedDeposits;
    }

    void setMissedDeposits(Integer missedDeposits) {
        this.missedDeposits = missedDeposits;
    }

    /**
     * <b>FIXME :</b> Since {@link MoneyCompositeUserType} is not be hit by a transactional(no commit) integration test
     * this will be initialised as null, which can result in null pointer exception. Previously the integration tests
     * were doing a transaction commit and then retrieve this object from database, if this value is null in database
     * {@link MoneyCompositeUserType} will initialise it as zero {@link Money}, but not null. Initialising this value as
     * zero {@link Money} will help in testing without transaction commit and it's a safe thing to do the initialisation
     * if this value is null<br/>
     * <br/>
     *
     * <b>TODO :</b> Add the database constraint "not null" on column for this property, and initialise this object as
     * zero {@link Money} at creation time.
     */
    public Money getTotalDeposits() {
        if (totalDeposits == null) {
            totalDeposits = MoneyUtils.createMoney(savings.getCurrency(), BigDecimal.ZERO);
        }
        return totalDeposits;
    }

    void setTotalDeposits(Money totalDeposits) {
        this.totalDeposits = totalDeposits;
    }


    /**
     * <b>FIXME :</b> Since {@link MoneyCompositeUserType} is not be hit by a transactional(no commit) integration test
     * this will be initialised as null, which can result in null pointer exception. Previously the integration tests
     * were doing a transaction commit and then retrieve this object from database, if this value is null in database
     * {@link MoneyCompositeUserType} will initialise it as zero {@link Money}, but not null. Initialising this value as
     * zero {@link Money} will help in testing without transaction commit and it's a safe thing to do the initialisation
     * if this value is null<br/>
     * <br/>
     *
     * <b>TODO :</b> Add the database constraint "not null" on column for this property, and initialise this object as
     * zero {@link Money} at creation time.
     */
    public Money getTotalInterestEarned() {
        if (totalInterestEarned == null) {
            totalInterestEarned = MoneyUtils.createMoney(savings.getCurrency(), BigDecimal.ZERO);
        }
        return totalInterestEarned;
    }

    void setTotalInterestEarned(Money totalInterstEarned) {
        this.totalInterestEarned = totalInterstEarned;
    }


    /**
     * <b>FIXME :</b> Since {@link MoneyCompositeUserType} is not be hit by a transactional(no commit) integration test
     * this will be initialised as null, which can result in null pointer exception. Previously the integration tests
     * were doing a transaction commit and then retrieve this object from database, if this value is null in database
     * {@link MoneyCompositeUserType} will initialise it as zero {@link Money}, but not null. Initialising this value as
     * zero {@link Money} will help in testing without transaction commit and it's a safe thing to do the initialisation
     * if this value is null<br/>
     * <br/>
     *
     * <b>TODO :</b> Add the database constraint "not null" on column for this property, and initialise this object as
     * zero {@link Money} at creation time.
     */
    public Money getTotalWithdrawals() {
        if (totalWithdrawals == null) {
            totalWithdrawals = MoneyUtils.createMoney(savings.getCurrency(), BigDecimal.ZERO);
        }
        return totalWithdrawals;
    }

    void setTotalWithdrawals(Money totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    void setPaymentDetails(Money totalAmount) {
        if (totalDeposits == null) {
            totalDeposits = new Money(savings.getCurrency());
        }
        totalDeposits = totalDeposits.add(totalAmount);
    }

    void setWithdrawDetails(Money totalAmount) {
        if (totalWithdrawals == null) {
            totalWithdrawals = new Money(savings.getCurrency());
        }
        totalWithdrawals = totalWithdrawals.add(totalAmount);
    }

    void setTotalInterestDetails(Money totalAmount) {
        if (totalInterestEarned == null) {
            totalInterestEarned = new Money(savings.getCurrency());
        }
        totalInterestEarned = totalInterestEarned.add(totalAmount);
    }

    void addMissedDeposits(int missedDeposits) {
        if (this.missedDeposits == null) {
            this.missedDeposits = 0;
        }
        this.missedDeposits = this.missedDeposits + missedDeposits;
    }

}

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

package org.mifos.application.accounts.util.helpers;

import org.mifos.framework.util.helpers.Money;

/**
 * This class acts as wrapper class for overdue amounts.
 */
public class OverDueAmounts {
    private Money principalOverDue;

    private Money interestOverdue;

    private Money penaltyOverdue;

    private Money feesOverdue;

    private Money totalPrincipalPaid;

    public Money getFeesOverdue() {
        return feesOverdue;
    }

    public void setFeesOverdue(Money feesOverdue) {
        this.feesOverdue = feesOverdue;
    }

    public Money getInterestOverdue() {
        return interestOverdue;
    }

    public void setInterestOverdue(Money interestOverdue) {
        this.interestOverdue = interestOverdue;
    }

    public Money getPenaltyOverdue() {
        return penaltyOverdue;
    }

    public void setPenaltyOverdue(Money penaltyOverdue) {
        this.penaltyOverdue = penaltyOverdue;
    }

    public Money getPrincipalOverDue() {
        return principalOverDue;
    }

    public void setPrincipalOverDue(Money principalOverDue) {
        this.principalOverDue = principalOverDue;
    }

    public void setTotalPrincipalPaid(Money principalPaid) {
        this.totalPrincipalPaid = principalPaid;

    }

    public Money getTotalPrincipalPaid() {

        return this.totalPrincipalPaid;
    }

    public void add(OverDueAmounts dueAmounts) {
        this.principalOverDue = new Money().add(principalOverDue).add(dueAmounts.getPrincipalOverDue());
        this.interestOverdue = new Money().add(interestOverdue).add(dueAmounts.getInterestOverdue());
        this.penaltyOverdue = new Money().add(penaltyOverdue).add(dueAmounts.getPenaltyOverdue());
        this.feesOverdue = new Money().add(feesOverdue).add(dueAmounts.getFeesOverdue());

    }
}

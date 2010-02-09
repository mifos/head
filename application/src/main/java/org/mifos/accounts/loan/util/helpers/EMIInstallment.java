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

package org.mifos.accounts.loan.util.helpers;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

/*
 * What does EMI stand for?  Equal Monthly Installment,
 * Estimated Monthly Installment?  In any case this seems
 * like a poor name since this class is used for both 
 * weekly and monthly installments.
 */
public class EMIInstallment {

    private Money principal;
    private Money interest;

    public EMIInstallment(MifosCurrency currency) {
        this(new Money(currency), new Money(currency));
    }

    public EMIInstallment(Money principal, Money interest) {
        this.principal = principal;
        this.interest = interest;
    }

    public void setPrincipal(Money principal) {
        this.principal = principal;
    }

    public void setInterest(Money interest) {
        this.interest = interest;
    }

    public Money getInterest() {
        return interest;
    }

    public Money getPrincipal() {
        return principal;
    }
}

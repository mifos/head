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

package org.mifos.application.accounts.business;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class ViewInstallmentDetails extends View {

    private final Money principal;

    private final Money interest;

    private final Money fees;

    private final Money penalty;

    public ViewInstallmentDetails(Money principal, Money interest, Money fees, Money penalty) {
        if (principal == null || interest == null || fees == null || penalty == null) {
            throw new IllegalArgumentException("Illegal null argument passed");
        }
        this.principal = principal;
        this.interest = interest;
        this.fees = fees;
        this.penalty = penalty;
    }

    public Money getFees() {
        return fees;
    }

    public Money getInterest() {
        return interest;
    }

    public Money getPenalty() {
        return penalty;
    }

    public Money getPrincipal() {
        return principal;
    }

    public Money getSubTotal() {
        return this.principal.add(this.interest).add(this.fees).add(this.penalty);
    }

}

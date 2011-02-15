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

package org.mifos.accounts.loan.util.helpers;

import org.mifos.framework.util.helpers.Money;

public class InstallmentPrincipalAndInterest {

    private final Money principal;
    private final Money interest;

    public InstallmentPrincipalAndInterest(Money principal, Money interest) {
        this.principal = principal;
        this.interest = interest;
    }

    public Money getInterest() {
        return interest;
    }

    public Money getPrincipal() {
        return principal;
    }

//    @Override
//    public boolean equals(Object obj) {
//        return principal.equals(obj) && interest.equals(obj);
//    }
//
//    @Override
//    public int hashCode() {
//        return 33 * principal.hashCode() + 33 * interest.hashCode();
//    }

    @Override
    public String toString() {
        return principal.toString() + " : " +  interest.toString();
    }
}
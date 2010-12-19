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

package org.mifos.dto.domain;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class InstallmentDetailsDto implements Serializable {

    private final String principal;
    private final String interest;
    private final String fees;
    private final String penalty;
    private String subTotal;

    public InstallmentDetailsDto(String principal, String interest, String fees, String penalty, String subTotal) {
        if (StringUtils.isBlank(principal) || StringUtils.isBlank(interest) ||
            StringUtils.isBlank(fees) || StringUtils.isBlank(penalty) || StringUtils.isBlank(subTotal)) {
            throw new IllegalArgumentException("Illegal null argument passed");
        }
        this.principal = principal;
        this.interest = interest;
        this.fees = fees;
        this.penalty = penalty;
        this.subTotal = subTotal;
    }

    public String getFees() {
        return fees;
    }

    public String getInterest() {
        return interest;
    }

    public String getPenalty() {
        return penalty;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getSubTotal() {
        return this.subTotal;
    }
}
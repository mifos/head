/*
 * Copyright Grameen Foundation USA
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

package org.mifos.dto.screen;

import org.joda.time.LocalDate;

public class LoanScheduledInstallmentDto {

    private final Integer installmentNumber;
    private final String principal;
    private final String interest;
    private final LocalDate dueDate;

    public LoanScheduledInstallmentDto(Integer installmentNumber, String principal, String interest, LocalDate dueDate) {
        this.installmentNumber = installmentNumber;
        this.principal = principal;
        this.interest = interest;
        this.dueDate = dueDate;
    }

    public String getPrincipal() {
        return this.principal;
    }

    public String getInterest() {
        return this.interest;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public Integer getInstallmentNumber() {
        return this.installmentNumber;
    }
}
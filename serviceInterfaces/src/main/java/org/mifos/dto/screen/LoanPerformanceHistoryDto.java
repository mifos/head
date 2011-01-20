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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.sql.Date;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanPerformanceHistoryDto implements Serializable {

    private final Integer noOfPayments;
    private final Integer totalNoOfMissedPayments;
    private final Short daysInArrears;
    private final Date loanMaturityDate;

    public LoanPerformanceHistoryDto(Integer noOfPayments, Integer totalNoOfMissedPayments, Short daysInArrears,
            Date loanMaturityDate) {
        super();
        this.noOfPayments = noOfPayments;
        this.totalNoOfMissedPayments = totalNoOfMissedPayments;
        this.daysInArrears = daysInArrears;
        this.loanMaturityDate = loanMaturityDate;
    }

    public Integer getNoOfPayments() {
        return this.noOfPayments;
    }

    public Integer getTotalNoOfMissedPayments() {
        return this.totalNoOfMissedPayments;
    }

    public Short getDaysInArrears() {
        return this.daysInArrears;
    }

    public Date getLoanMaturityDate() {
        return this.loanMaturityDate;
    }
}

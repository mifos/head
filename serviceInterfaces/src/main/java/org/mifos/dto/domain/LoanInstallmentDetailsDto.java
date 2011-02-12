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

package org.mifos.dto.domain;

import java.util.Date;


@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanInstallmentDetailsDto {

    private final InstallmentDetailsDto upcomingInstallmentDetails;
    private final InstallmentDetailsDto overDueInstallmentDetails;
    private final String totalAmountDue;
    private final Date nextMeetingDate;

    public LoanInstallmentDetailsDto(InstallmentDetailsDto upcomingInstallmentDetails,
            InstallmentDetailsDto overDueInstallmentDetails, String totalAmountDue, Date nextMeetingDate) {
        this.upcomingInstallmentDetails = upcomingInstallmentDetails;
        this.overDueInstallmentDetails = overDueInstallmentDetails;
        this.totalAmountDue = totalAmountDue;
        this.nextMeetingDate = nextMeetingDate;
    }

    public InstallmentDetailsDto getUpcomingInstallmentDetails() {
        return this.upcomingInstallmentDetails;
    }

    public InstallmentDetailsDto getOverDueInstallmentDetails() {
        return this.overDueInstallmentDetails;
    }

    public String getTotalAmountDue() {
        return this.totalAmountDue;
    }

    public Date getNextMeetingDate() {
        return this.nextMeetingDate;
    }

}

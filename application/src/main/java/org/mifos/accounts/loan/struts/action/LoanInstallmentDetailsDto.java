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

package org.mifos.accounts.loan.struts.action;

import java.util.Date;

import org.mifos.accounts.business.InstallmentDetailsDto;
import org.mifos.framework.util.helpers.Money;

public class LoanInstallmentDetailsDto {

    private final InstallmentDetailsDto upcomingInstallmentDetails;
    private final InstallmentDetailsDto overDueInstallmentDetails;
    private final Money totalAmountDue;
    private final Date nextMeetingDate;

    public LoanInstallmentDetailsDto(InstallmentDetailsDto upcomingInstallmentDetails,
            InstallmentDetailsDto overDueInstallmentDetails, Money totalAmountDue, Date nextMeetingDate) {
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

    public Money getTotalAmountDue() {
        return this.totalAmountDue;
    }

    public Date getNextMeetingDate() {
        return this.nextMeetingDate;
    }

}

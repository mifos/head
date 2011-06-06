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

package org.mifos.clientportfolio.newloan.domain.service;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.clientportfolio.newloan.domain.LoanProductOverridenDetail;
import org.mifos.clientportfolio.newloan.domain.LoanSchedule;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleConfiguration;
import org.mifos.customers.business.CustomerBO;

/**
 * A domain only service for generating {@link LoanScheduleEntity}'s
 */
public interface LoanScheduleService {

    LoanSchedule generate(LoanOfferingBO loanProduct, CustomerBO customer, MeetingBO loanMeeting,LoanProductOverridenDetail overridenDetail, 
            LoanScheduleConfiguration configuration, List<AccountFeesEntity> accountFees, LocalDate disbursementDate, 
            List<DateTime> loanScheduleDates, List<Number> totalInstallmentAmounts);
    
    // FIXME - keithw - when struts/jsp is gone, will be able to refactor away use of userBranchOfficeId
    LoanSchedule generate(LoanOfferingBO loanProduct, CustomerBO customer, MeetingBO loanRepaymentMeeting,
            LoanProductOverridenDetail overridenDetail, LoanScheduleConfiguration configuration, 
            Short userBranchOfficeId, List<AccountFeesEntity> accountFees, LocalDate disbursementDate);

}
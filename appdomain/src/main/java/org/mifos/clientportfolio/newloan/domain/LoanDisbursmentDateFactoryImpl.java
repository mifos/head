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

package org.mifos.clientportfolio.newloan.domain;

import java.util.Date;

import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.schedule.ScheduledEvent;

public class LoanDisbursmentDateFactoryImpl implements LoanDisbursementDateFactory {

    @Override
    public LoanDisbursementStrategy create(CustomerBO customer, LoanOfferingBO loanProduct,
            boolean isRepaymentIndependentOfMeetingEnabled, boolean isLoanWithBackdatedPayments) {
        
        RecurringScheduledEventFactory recurringScheduledEventFactory = new RecurringScheduledEventFactoryImpl();
        ScheduledEvent customerMeetingSchedule = recurringScheduledEventFactory.createScheduledEventFrom(customer.getCustomerMeetingValue());
        final Date nextMeetingDate = customer.getCustomerAccount().getNextMeetingDate();
        LocalDate nextValidCustomerMeetingDate = new LocalDate(nextMeetingDate);
        LoanDisbursementStrategy loanDisbursementStrategy = new LoanDisbursementCoupledToCustomerMeetingScheduleStrategyImpl(customerMeetingSchedule, nextValidCustomerMeetingDate);
        if (isLoanWithBackdatedPayments) {
            loanDisbursementStrategy = new LoanWithBackdatedPaymentsDisbursementStrategyImpl();
        } else if (loanProduct.isVariableInstallmentsAllowed() || isRepaymentIndependentOfMeetingEnabled) {
            // LSIM ON
            loanDisbursementStrategy = new VariableInstallmentsLoanDisbursementStrategyImpl(customerMeetingSchedule);
        }

        return loanDisbursementStrategy;
    }
}
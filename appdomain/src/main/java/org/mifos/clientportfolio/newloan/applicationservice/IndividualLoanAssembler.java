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

package org.mifos.clientportfolio.newloan.applicationservice;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.clientportfolio.newloan.domain.IndividualLoan;
import org.mifos.clientportfolio.newloan.domain.IndividualLoanImpl;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleFactory;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

public class IndividualLoanAssembler implements LoanAssembler {

    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;
    private final LoanScheduleFactory loanRepaymentFactory;
    private final ScheduledDateGeneration scheduledDateGeneration;

    public IndividualLoanAssembler(LoanProductDao loanProductDao, CustomerDao customerDao, LoanScheduleFactory loanRepaymentFactory, ScheduledDateGeneration scheduledDateGeneration) {
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
        this.loanRepaymentFactory = loanRepaymentFactory;
        this.scheduledDateGeneration = scheduledDateGeneration;
    }

    @Override
    public IndividualLoan assembleFrom(IndividualLoanRequest individualLoan) {

        LoanOfferingBO loanProduct = this.loanProductDao.findBySystemId(individualLoan.getLoanProductId().globalId());
        ClientBO client = this.customerDao.findClientBySystemId(individualLoan.getClientId().globalId());

        int occurences = 12;
        DateTime lastScheduledDate = new DateTime();

        // create scheduled event factory
        ScheduledEvent scheduledEvent = new ScheduledEventFactory().createScheduledEventFrom(loanProduct.getLoanOfferingMeetingValue());
        List<DateTime> loanScheduleDates = scheduledDateGeneration.generateScheduledDates(occurences, lastScheduledDate, scheduledEvent);

        return new IndividualLoanImpl();
    }
}
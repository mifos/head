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
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.clientportfolio.newloan.domain.IndividualLoan;
import org.mifos.clientportfolio.newloan.domain.IndividualLoanImpl;
import org.mifos.clientportfolio.newloan.domain.IndividualLoanSchedule;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleFactory;
import org.mifos.clientportfolio.newloan.domain.RecurringScheduledEventFactory;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;

public class IndividualLoanAssembler implements LoanAssembler {

    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;
    private final LoanScheduleFactory loanScheduleFactory;
    private final ScheduledDateGeneration scheduledDateGeneration;
    private final RecurringScheduledEventFactory scheduledEventFactory;

    public IndividualLoanAssembler(LoanProductDao loanProductDao, CustomerDao customerDao, LoanScheduleFactory loanScheduleFactory,
            ScheduledDateGeneration scheduledDateGeneration, RecurringScheduledEventFactory scheduledEventFactory) {
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
        this.loanScheduleFactory = loanScheduleFactory;
        this.scheduledDateGeneration = scheduledDateGeneration;
        this.scheduledEventFactory = scheduledEventFactory;
    }

    @Override
    public IndividualLoan assembleFrom(IndividualLoanRequest individualLoan) {

        LoanOfferingBO loanProduct = this.loanProductDao.findBySystemId(individualLoan.getLoanProductId().globalId());
        ClientBO client = this.customerDao.findClientBySystemId(individualLoan.getClientId().globalId());

        int occurences = 12;
        DateTime lastScheduledDate = new DateTime();

        ScheduledEvent scheduledEvent = scheduledEventFactory.createScheduledEventFrom(loanProduct.getLoanOfferingMeetingValue());
        List<DateTime> loanScheduleDates = scheduledDateGeneration.generateScheduledDates(occurences, lastScheduledDate, scheduledEvent);

        // FIXME - assemble loanAmount from dto
        Money loanAmount = null;
        IndividualLoanSchedule loanSchedule = loanScheduleFactory.create(loanScheduleDates, loanProduct, loanAmount);

        return new IndividualLoanImpl();
    }
}
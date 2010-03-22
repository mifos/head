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
package org.mifos.application.collectionsheet.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

/**
 *
 */
public class CustomerAccountBuilder {

    private CustomerAccountBO customerAccount;
    private CustomerBO customer;
    private OfficeBO office;
    private PersonnelBO loanOfficer;
    private final Set<AmountFeeBO> fees = new HashSet<AmountFeeBO>();

    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final Date createdDate = new DateTime().minusDays(14).toDate();

    public CustomerAccountBO buildForUnitTests() {

        customerAccount = new CustomerAccountBO(customer, fees, office, loanOfficer, createdDate,
                createdByUserId, false);
        return customerAccount;
    }

    public CustomerAccountBO buildForIntegrationTests() {

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
        List<Holiday> thisAndNextYearsHolidays = holidayDao.findAllHolidaysThisYearAndNext();

        DateTime startFromMeetingDate = new DateTime(customer.getCustomerMeetingValue().getMeetingStartDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customer.getCustomerMeetingValue());

        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays,
                thisAndNextYearsHolidays);
        List<DateTime> installmentDates = dateGeneration.generateScheduledDates(10, startFromMeetingDate,
                scheduledEvent);

        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        for (AmountFeeBO fee : fees) {
            accountFees.add(new AccountFeesEntity(null, fee, fee.getFeeAmount().getAmountDoubleValue()));
        }

        CustomerAccountBO customerAccount = CustomerAccountBO.createNew(customer, accountFees, installmentDates);
        customer.addAccount(customerAccount);
        customerAccount.setUserContext(TestUtils.makeUser());
        customerAccount.setUpdateDetails();

        return customerAccount;
    }

    public CustomerAccountBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
        return this;
    }

    public CustomerAccountBuilder withFee(final AmountFeeBO withFee) {
        fees.add(withFee);
        return this;
    }

    public CustomerAccountBuilder withOffice(final OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }

    public CustomerAccountBuilder withLoanOfficer(final PersonnelBO withLoanOfficer) {
        this.loanOfficer = withLoanOfficer;
        return this;
    }
}

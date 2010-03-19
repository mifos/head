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

package org.mifos.accounts.loan.business;

import java.util.ArrayList;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;

/**
 * These tests validate new schedule-generating code for loan repayment
 */
public class LoanScheduleGenerationIntegrationTest extends MifosIntegrationTestCase {

    //Things you need to set up before you can create a loan
    private MeetingBO meeting;
    private OfficeBO office;
    private CenterBO center;
    private GroupBO group;
    private LoanOfferingBO loanOffering;


    public LoanScheduleGenerationIntegrationTest() throws Exception {
        super();
    }
    
    @Override
    protected void setUp() throws Exception {
        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.rollbackTransaction();
    }

    public void testNewWeeklyLoanNoFeesNoHoliday() throws Exception {

        //setup
        DateTime startDate = new DateTime().withDate(2010, 10, 15).toDateMidnight().toDateTime();
        setupWeeklyScheduleStartingOn(startDate);
        
        //Make loan and schedule
        LoanBO loan = LoanBO
                .createLoan(TestUtils.makeUser(), loanOffering, group, AccountState.LOAN_APPROVED, new Money(
                        getCurrency(), "300.0"), Short.valueOf("6"), startDate.toDate(), false, loanOffering.getDefInterestRate(),
                        (short) 0, null, new ArrayList<FeeView>(), null, 300.0, 300.0,
                        loanOffering.getEligibleInstallmentSameForAllLoan().getMaxNoOfInstall(), 
                        loanOffering.getEligibleInstallmentSameForAllLoan().getMinNoOfInstall(),
                        false, null);
        
        //Validate
        Assert.assertEquals(6, loan.getAccountActionDates().size());
        Assert.assertEquals(loan.getNoOfInstallments().intValue(), loan.getAccountActionDates().size());
        
        //since disbursal is on a meeting day, the first installment date is one week from disbursement date 
        /*
        for (short installmentId = 1; installmentId <= 6; installmentId++) {
            Date expectedDate = startDate.plusWeeks(installmentId).toDate();
            Assert.assertEquals(expectedDate, loan.getAccountActionDate(installmentId).getActionDate());
        }
        */
        validateDates(loan, startDate.plusWeeks(1), startDate.plusWeeks(2), startDate.plusWeeks(3),
                            startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6));
    }

    public void testNewWeeklyLoanNoFeesSpansMoratorium() throws Exception {

        //setup
        DateTime startDate = new DateTime().withDate(2010, 10, 15).toDateMidnight().toDateTime();
        setupWeeklyScheduleStartingOn(startDate);

        //Moratorium starts Friday (when 1st payment due) thru the Thursday before the 2nd payment
        buildAndPersistMoratorium(startDate.plusWeeks(1), startDate.plusWeeks(2).minusDays(1));
        
        //Make loan and schedule
        LoanBO loan = LoanBO
                .createLoan(TestUtils.makeUser(), loanOffering, group, AccountState.LOAN_APPROVED, new Money(
                        getCurrency(), "300.0"), Short.valueOf("6"), startDate.toDate(), false, 
                        loanOffering.getDefInterestRate(), (short) 0, null, new ArrayList<FeeView>(), null, 300.0, 300.0,
                        loanOffering.getEligibleInstallmentSameForAllLoan().getMaxNoOfInstall(), 
                        loanOffering.getEligibleInstallmentSameForAllLoan().getMinNoOfInstall(),
                        false, null);
        
        /*
        * Validate. Since start date is a meeting date, disbursal is on that day and
        * the first installment date is one week later. Since the first installment date falls in the moratorium,
        * all installments should get pushed out one week.
         * 
         */
        validateDates(loan, startDate.plusWeeks(2), startDate.plusWeeks(3), startDate.plusWeeks(4),
                startDate.plusWeeks(5), startDate.plusWeeks(6), startDate.plusWeeks(7));
    }

    private void setupWeeklyScheduleStartingOn(DateTime startDate) {
        meeting = new MeetingBuilder().weekly().withStartDate(startDate).build();
        office = new OfficeBuilder().withGlobalOfficeNum("12345").build();
        center = new CenterBuilder().withMeeting(meeting).withOffice(office).build();
        group = new GroupBuilder().withParentCustomer(center).withOffice(office).withMeeting(meeting).build();
        loanOffering = new LoanProductBuilder().withMeeting(meeting).buildForIntegrationTests();
    }
    
    private Holiday buildAndPersistMoratorium (DateTime start, DateTime through) {
        HolidayBO moratorium = (HolidayBO) new HolidayBuilder().from(start)
                                                               .to(through)
                                                               .withRepaymentMoratoriumRule().build();
        GenericDao genericDao = new GenericDaoHibernate();
        genericDao.createOrUpdate(moratorium);
        return moratorium;
    }
    
    private void validateDates (LoanBO loan, DateTime... dates) {
        Assert.assertEquals(dates.length, loan.getAccountActionDates().size());
        Assert.assertEquals(dates.length, loan.getNoOfInstallments().intValue());
        for (short installmentId = 1; installmentId <= loan.getNoOfInstallments(); installmentId++) {
            Assert.assertEquals(dates[installmentId-1].toDate(), loan.getAccountActionDate(installmentId).getActionDate());
        }

    }
}

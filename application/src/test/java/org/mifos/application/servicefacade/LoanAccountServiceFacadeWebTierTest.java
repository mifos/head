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
package org.mifos.application.servicefacade;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LoanAccountServiceFacadeWebTierTest {

    // class under test
    private LoanAccountServiceFacade loanAccountServiceFacade;

    // collaborators
    @Mock
    private LoanProductDao loanProductDao;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PersonnelDao personnelDao;

    @Mock
    private FundDao fundDao;

    @Mock
    private LoanDao loanDao;

    @Mock
    private AccountService accountService;

    @Mock
    private LoanBusinessService loanBusinessService;

    // test data
    @Mock
    private LoanBO loanBO;

    @Mock
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    @Mock
    private OfficeDao officeDao;
    private MifosCurrency rupee;

    @Before
    public void setupAndInjectDependencies() {
        loanAccountServiceFacade = new LoanAccountServiceFacadeWebTier(officeDao, loanProductDao, customerDao, personnelDao,
                fundDao, loanDao, accountService, scheduleCalculatorAdaptor, loanBusinessService);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @Test
    public void testMakeEarlyRepayment() throws AccountException {
        when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        java.sql.Date date = new java.sql.Date(new Date().getTime());
        when(loanBO.getCurrency()).thenReturn(rupee);
        boolean waiveInterest = true;
        when(loanBO.isInterestWaived()).thenReturn(waiveInterest);
        String paymentMethod = "Cash";
        String receiptNumber = "001";

        loanAccountServiceFacade.makeEarlyRepayment(new RepayLoanInfoDto("1", "100", receiptNumber, date,
                paymentMethod, (short) 1, waiveInterest, date,BigDecimal.TEN,BigDecimal.ZERO));

        verify(loanBO).makeEarlyRepayment(new Money(rupee, "100"), receiptNumber, date,
                paymentMethod, (short) 1, waiveInterest, new Money(rupee, BigDecimal.ZERO));
        verify(loanBusinessService).computeExtraInterest(loanBO, date);
    }

    @Test
    public void testMakeEarlyRepaymentForNotWaiverInterestLoanProduct() throws AccountException {
        when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        boolean waiveInterest = false;
        when(loanBO.getCurrency()).thenReturn(rupee);
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity() {};
        loanScheduleEntity.setInterest(new Money(rupee, 100d));
        when(loanBO.getDetailsOfNextInstallment()).thenReturn(loanScheduleEntity);
        java.sql.Date date = mock(java.sql.Date.class);
        String paymentMethod = "Cash";
        String receiptNumber = "001";

        loanAccountServiceFacade.makeEarlyRepayment(new RepayLoanInfoDto("1", "100", receiptNumber, date, paymentMethod, (short) 1,
                waiveInterest, date,BigDecimal.ZERO,BigDecimal.ZERO));

        short userId = (short) 1;
        verify(loanBO).makeEarlyRepayment(new Money(rupee, "100"), receiptNumber, date, paymentMethod, userId, waiveInterest, new Money(rupee, 100d));
        verify(loanBusinessService).computeExtraInterest(loanBO, date);
    }

    @Test
    public void testValidateMakeEarlyRepayment() throws AccountException {
        when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        boolean actualWaiveInterestValue = false;
        java.sql.Date date = mock(java.sql.Date.class);
        when(loanBO.isInterestWaived()).thenReturn(actualWaiveInterestValue);
        try {
            loanAccountServiceFacade.makeEarlyRepayment(new RepayLoanInfoDto("1", "100", "001", mock(java.sql.Date.class),
                    "Cash", (short) 1, true, date,BigDecimal.ZERO,BigDecimal.ZERO));
        } catch (BusinessRuleException e) {
            verify(loanBO, never()).makeEarlyRepayment((Money) anyObject(), anyString(), (Date) anyObject(), anyString(), (Short) anyObject(), anyBoolean(), Matchers.<Money>anyObject());
            verify(loanBO, never()).getCurrency();
            verify(loanBusinessService,never()).computeExtraInterest(eq(loanBO), Matchers.<Date>anyObject());
            assertThat(e.getMessageKey(), is(LoanConstants.WAIVER_INTEREST_NOT_CONFIGURED));
        }
    }

    @Test
    public void shouldReturnRepayLoanDtoWithAllDataPopulated() {

        String accountNumber = "1234";
        LoanBO loanBO = mock(LoanBO.class);
        Money repaymentAmount = TestUtils.createMoney("1234");
        Money interest = TestUtils.createMoney("100");

        when(loanDao.findByGlobalAccountNum(accountNumber)).thenReturn(loanBO);
        when(loanBO.getEarlyRepayAmount()).thenReturn(repaymentAmount);
        when(loanBO.waiverAmount()).thenReturn(interest);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(false);

        Money waivedAmount = repaymentAmount.subtract(interest);

        RepayLoanDto repayLoanDto = this.loanAccountServiceFacade.retrieveLoanRepaymentDetails(accountNumber);

        verify(loanDao).findByGlobalAccountNum(accountNumber);
        verify(loanBO).waiverAmount();
        verify(loanBO).getEarlyRepayAmount();
        verify(loanBO).isDecliningBalanceInterestRecalculation();

        assertEquals(repayLoanDto.getEarlyRepaymentMoney(), repaymentAmount.toString());
        assertEquals(repayLoanDto.getWaivedRepaymentMoney(), waivedAmount.toString());
    }

    @Test
    public void shouldReturnRepayLoanDtoWithAllDataPopulatedForDecliningBalanceInterestRecalculation() {

        String accountNumber = "1234";
        LoanBO loanBO = mock(LoanBO.class);
        Money repaymentAmount = TestUtils.createMoney("1234");
        Money interest = TestUtils.createMoney("100");
        DateTime currentDate = new DateTime(2010, 10, 30, 0, 0, 0, 0);
        new DateTimeService().setCurrentDateTime(currentDate);

        RepaymentResultsHolder repaymentResultsHolder = new RepaymentResultsHolder();
        repaymentResultsHolder.setTotalRepaymentAmount(repaymentAmount.getAmount());
        repaymentResultsHolder.setWaiverAmount(interest.getAmount());

        when(loanDao.findByGlobalAccountNum(accountNumber)).thenReturn(loanBO);
        when(scheduleCalculatorAdaptor.computeRepaymentAmount(loanBO, currentDate.toDate())).thenReturn(repaymentResultsHolder);
        when(loanBO.getCurrency()).thenReturn(repaymentAmount.getCurrency());
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);


        RepayLoanDto repayLoanDto = this.loanAccountServiceFacade.retrieveLoanRepaymentDetails(accountNumber);

        verify(loanDao).findByGlobalAccountNum(accountNumber);
        verify(scheduleCalculatorAdaptor).computeRepaymentAmount(loanBO, currentDate.toDate());
        verify(loanBO,never()).waiverAmount();
        verify(loanBO,never()).getEarlyRepayAmount();
        verify(loanBO).isDecliningBalanceInterestRecalculation();

        assertEquals(repayLoanDto.getEarlyRepaymentMoney(), repaymentAmount.toString());
        assertEquals(repayLoanDto.getWaivedRepaymentMoney(), repaymentAmount.subtract(interest).toString());
    }

    @Test
    public void shouldReturnInterestDueForNextInstallmentWhenWaiveInterestFlagIsOnAndInterestIsWaived() {
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity() {};
        loanScheduleEntity.setInterest(new Money(rupee, 100d));
        when(loanBO.getDetailsOfNextInstallment()).thenReturn(loanScheduleEntity);
        BigDecimal interestDue = ((LoanAccountServiceFacadeWebTier) loanAccountServiceFacade).
                interestDueForNextInstallment(BigDecimal.TEN, BigDecimal.ZERO, loanBO, true);
        assertThat(interestDue, is(BigDecimal.ZERO));
    }

    @Test
    public void shouldReturnInterestDueForNextInstallmentWhenWaiveInterestFlagIsOnAndInterestIsNotWaived() {
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(){};
        Money interest = TestUtils.createMoney("1234");
        Money interestPaid = TestUtils.createMoney("1200");
        loanScheduleEntity.setInterest(interest);
        loanScheduleEntity.setInterestPaid(interestPaid);
        when(loanBO.getDetailsOfNextInstallment()).thenReturn(loanScheduleEntity);
        BigDecimal interestDue = ((LoanAccountServiceFacadeWebTier) loanAccountServiceFacade).
                interestDueForNextInstallment(BigDecimal.TEN, BigDecimal.ZERO, loanBO, false);
        Double expectedDue = interest.subtract(interestPaid).getAmount().doubleValue();
        assertThat(interestDue.doubleValue(), is(expectedDue));
    }
    
    @Test
    public void shouldReturnInterestDueForNextInstallmentForDIPBLoanWithDoNotWaiveInterest() {
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(){};
        Money interest = TestUtils.createMoney("1234");
        Money interestPaid = TestUtils.createMoney("1200");
        loanScheduleEntity.setInterest(interest);
        loanScheduleEntity.setInterestPaid(interestPaid);
        when(loanBO.getDetailsOfNextInstallment()).thenReturn(loanScheduleEntity);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        BigDecimal interestDue = ((LoanAccountServiceFacadeWebTier) loanAccountServiceFacade).
                interestDueForNextInstallment(BigDecimal.TEN, BigDecimal.ZERO, loanBO, false);
        assertThat(interestDue.doubleValue(), is(10d));
    }

    @Test
    public void shouldReturnInterestDueForNextInstallmentForDIPBLoanWithWaiveInterest() {
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(){};
        Money interest = TestUtils.createMoney("1234");
        Money interestPaid = TestUtils.createMoney("1200");
        loanScheduleEntity.setInterest(interest);
        loanScheduleEntity.setInterestPaid(interestPaid);
        when(loanBO.getDetailsOfNextInstallment()).thenReturn(loanScheduleEntity);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        BigDecimal interestDue = ((LoanAccountServiceFacadeWebTier) loanAccountServiceFacade).
                interestDueForNextInstallment(BigDecimal.TEN, BigDecimal.ZERO, loanBO, true);
        assertThat(interestDue.doubleValue(), is(0d));
    }

    @Test
    public void interestDueForNextInstallmentShouldReturnZeroIfNextInstallmentIsNull() {
        LoanScheduleEntity loanScheduleEntity = null;
        when(loanBO.getDetailsOfNextInstallment()).thenReturn(loanScheduleEntity);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(false);
        BigDecimal interestDue = ((LoanAccountServiceFacadeWebTier) loanAccountServiceFacade).
                interestDueForNextInstallment(BigDecimal.TEN, BigDecimal.ZERO, loanBO, false);
        assertThat(interestDue.doubleValue(), is(0d));
    }
}
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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

    @Mock
    private LoanPrdBusinessService loanPrdBusinessService;

    // test data
    @Mock
    private LoanBO loanBO;

    @Mock
    private InstallmentsValidator installmentsValidator;

    @Mock
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    @Mock
    private HolidayServiceFacade holidayServiceFacade;

    @Mock
    private OfficeDao officeDao;

    @Before
    public void setupAndInjectDependencies() {
        loanAccountServiceFacade = new LoanAccountServiceFacadeWebTier(officeDao, loanProductDao, customerDao, personnelDao,
                fundDao, loanDao, accountService, installmentsValidator, scheduleCalculatorAdaptor, loanBusinessService, holidayServiceFacade, loanPrdBusinessService);
    }

    @Test
    public void testMakeEarlyRepayment() throws AccountException {
        Mockito.when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        MifosCurrency dollar = new MifosCurrency(Short.valueOf("1"), "Dollar", BigDecimal.valueOf(1), "USD");
        Mockito.when(loanBO.getCurrency()).thenReturn(dollar);
        java.sql.Date date = new java.sql.Date(new Date().getTime());
        boolean waiveInterest = true;
        Mockito.when(loanBO.isInterestWaived()).thenReturn(waiveInterest);
        String paymentMethod = "Cash";
        String receiptNumber = "001";

        loanAccountServiceFacade.makeEarlyRepayment("1", "100", receiptNumber, date, paymentMethod, (short) 1, waiveInterest);

        verify(loanBO).makeEarlyRepayment(new Money(dollar, "100"), receiptNumber, date, paymentMethod, (short) 1, waiveInterest);
    }

    @Test
    public void testMakeEarlyRepaymentForNotWaiverInterestLoanProduct() throws AccountException {
        Mockito.when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        MifosCurrency dollar = new MifosCurrency(Short.valueOf("1"), "Dollar", BigDecimal.valueOf(1), "USD");
        boolean waiveInterest = false;
        Mockito.when(loanBO.isInterestWaived()).thenReturn(waiveInterest);
        Mockito.when(loanBO.getCurrency()).thenReturn(dollar);
        java.sql.Date date = mock(java.sql.Date.class);
        String paymentMethod = "Cash";
        String receiptNumber = "001";

        loanAccountServiceFacade.makeEarlyRepayment("1", "100", receiptNumber, date, paymentMethod, (short) 1, waiveInterest);

        short userId = (short) 1;
        verify(loanBO).makeEarlyRepayment(new Money(dollar, "100"), receiptNumber, date, paymentMethod, userId, waiveInterest);
    }

    @Test
    public void testValidateMakeEarlyRepayment() throws AccountException {
        Mockito.when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        boolean actualWaiveInterestValue = false;
        Mockito.when(loanBO.isInterestWaived()).thenReturn(actualWaiveInterestValue);
        try {
            loanAccountServiceFacade.makeEarlyRepayment("1", "100", "001", mock(java.sql.Date.class),
                    "Cash", (short) 1, true);
        } catch (BusinessRuleException e) {
            verify(loanBO, never()).makeEarlyRepayment((Money) anyObject(), anyString(), (Date) anyObject(), anyString(), (Short) anyObject(), anyBoolean());
            verify(loanBO, never()).getCurrency();
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
    public void shouldReturnRepayLoanDtoWithAllDataPopulatedForDIPB() {

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
}
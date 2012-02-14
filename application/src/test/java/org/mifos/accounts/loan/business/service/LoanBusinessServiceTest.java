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

package org.mifos.accounts.loan.business.service;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.matchers.OriginalLoanScheduleEntitiesMatcher;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanBusinessServiceTest {
    private LoanBusinessService loanBusinessService;
    private Locale locale;
    private RepaymentScheduleInstallmentBuilder installmentBuilder;
    private MifosCurrency rupee;

    @Mock
    private LoanBO loanBO;

    @Mock
    private HolidayService holidayService;

    @Mock
    private AccountPaymentEntity accountPaymentEntity;

    @Mock
    ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    @Mock
    private PaymentData paymentData;

    @Mock
    private PersonnelBO personnel;

    @Mock
    private LegacyLoanDao legacyLoanDao;
    private Short officeId;


    @Before
    public void setupAndInjectDependencies() {
        loanBusinessService = new LoanBusinessService(legacyLoanDao, null, null, holidayService, scheduleCalculatorAdaptor);
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        officeId = Short.valueOf("1");
    }

    @Test
    public void shouldApplyPaymentForInterestRecalculationLoanInterestType() {
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        Date transactionDate = new Date();
        Money totalAmount = new Money(rupee, 10.0);
        when(paymentData.getTransactionDate()).thenReturn(transactionDate);
        when(paymentData.getTotalAmount()).thenReturn(totalAmount);
        when(paymentData.getPersonnel()).thenReturn(personnel);
        loanBusinessService.applyPayment(paymentData, loanBO, accountPaymentEntity);
        verify(scheduleCalculatorAdaptor, times(1)).applyPayment(loanBO, totalAmount,
                transactionDate, personnel, accountPaymentEntity);
        verify(loanBO, times(1)).isDecliningBalanceInterestRecalculation();
        verify(paymentData).getTransactionDate();
        verify(paymentData).getTotalAmount();
        verify(paymentData).getPersonnel();
    }

    @Test
    public void shouldApplyPaymentForNonInterestRecalculationLoanInterestType() {
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(false);
        List<AccountActionDateEntity> installments = new ArrayList<AccountActionDateEntity>();
        LoanScheduleEntity installment1 = mock(LoanScheduleEntity.class);
        LoanScheduleEntity installment2 = mock(LoanScheduleEntity.class);
        when(installment1.applyPayment(Mockito.<AccountPaymentEntity>any(), Mockito.<Money>any(), Mockito.eq(personnel), Mockito.<Date>any())).thenReturn(new Money(rupee, 100d));
        when(installment2.applyPayment(Mockito.<AccountPaymentEntity>any(), Mockito.<Money>any(), Mockito.eq(personnel), Mockito.<Date>any())).thenReturn(new Money(rupee, 0d));
        installments.add(installment1);
        installments.add(installment2);
        when(loanBO.getAccountActionDatesSortedByInstallmentId()).thenReturn(installments);
        Date transactionDate = new Date();
        Money totalAmount = new Money(rupee, 10.0);
        when(paymentData.getTransactionDate()).thenReturn(transactionDate);
        when(paymentData.getTotalAmount()).thenReturn(totalAmount);
        when(paymentData.getPersonnel()).thenReturn(personnel);
        loanBusinessService.applyPayment(paymentData, loanBO, accountPaymentEntity);
        verify(scheduleCalculatorAdaptor, times(0)).applyPayment(loanBO, totalAmount,
                transactionDate, personnel, accountPaymentEntity);
        verify(loanBO, times(1)).getAccountActionDatesSortedByInstallmentId();
        verify(loanBO, times(1)).isDecliningBalanceInterestRecalculation();
        verify(paymentData).getTransactionDate();
        verify(paymentData).getTotalAmount();
        verify(paymentData).getPersonnel();
        verify(installment1).applyPayment(Matchers.<AccountPaymentEntity>any(), Matchers.<Money>any(), Matchers.eq(personnel), Matchers.<Date>any());
        verify(installment2).applyPayment(Matchers.<AccountPaymentEntity>any(), Matchers.<Money>any(), Matchers.eq(personnel), Matchers.<Date>any());
    }

    @Test
    public void shouldComputeVariableInstallmentScheduleForVariableInstallmentsIfVariableInstallmentsIsEnabled() throws Exception {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100", "0", "0");
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        when(loanAccountActionForm.getLoanAmount()).thenReturn("1000");
        when(loanAccountActionForm.getLoanAmountValue()).thenReturn(new Money(rupee, "1000"));
        when(loanAccountActionForm.getInterestRate()).thenReturn("24");
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(true);
        when(legacyLoanDao.findBySystemId(anyString())).thenReturn(loanBO);
        when(loanBO.toRepaymentScheduleDto(locale)).thenReturn(installments);
        loanBusinessService.
                applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(TestUtils.getDate(22, 8, 2010),
                        loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), locale);
        verify(loanBO).updateInstallmentSchedule(installments);
        verify(loanBO).toRepaymentScheduleDto(locale);
    }

    @Test
    public void shouldComputeVariableInstallmentScheduleForVariableInstallmentsIfVariableInstallmentsIsEnabledAndInstallmentsArePassed() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100", "0", "0");
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        when(loanAccountActionForm.getLoanAmount()).thenReturn("1000");
        when(loanAccountActionForm.getLoanAmountValue()).thenReturn(new Money(rupee, "1000"));
        when(loanAccountActionForm.getInterestRate()).thenReturn("24");
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(true);
        loanBusinessService.
                applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(TestUtils.getDate(22, 8, 2010),
                        loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), installments);
        verify(loanBO).updateInstallmentSchedule(installments);
    }

    @Test
    public void shouldComputeVariableInstallmentScheduleForVariableInstallmentsIfInterestTypeIsDecliningPrincipalBalance() throws Exception {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100", "0", "0");
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        when(loanAccountActionForm.getLoanAmount()).thenReturn("1000");
        when(loanAccountActionForm.getLoanAmountValue()).thenReturn(new Money(rupee, "1000"));
        when(loanAccountActionForm.getInterestRate()).thenReturn("24");
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(false);
        when(legacyLoanDao.findBySystemId(anyString())).thenReturn(loanBO);
        when(loanBO.toRepaymentScheduleDto(locale)).thenReturn(installments);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        loanBusinessService.applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(TestUtils.getDate(22, 8, 2010),
                loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), locale);
        verify(loanBO).updateInstallmentSchedule(installments);
        verify(loanBO).toRepaymentScheduleDto(locale);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotComputeVariableInstallmentSchedule() throws Exception {
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(false);
        InterestTypesEntity interestTypesEntity = new InterestTypesEntity(InterestType.DECLINING);
        when(loanBO.getInterestType()).thenReturn(interestTypesEntity);
        when(legacyLoanDao.findBySystemId(anyString())).thenReturn(loanBO);

        loanBusinessService.applyDailyInterestRatesWhereApplicable(new LoanScheduleGenerationDto(TestUtils.getDate(22, 8, 2010),
                loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), locale);
        verify(loanBO, never()).updateInstallmentSchedule(any(List.class));
    }

    @Test
    public void shouldGenerateMonthlyInstallmentScheduleFromRepaymentScheduleUsingDailyInterest() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("25-Sep-2010", 1, "78.6", "20.4", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("25-Oct-2010", 2, "182.8", "16.2", "1", "200", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("25-Nov-2010", 3, "186.0", "13.0", "1", "200", "0", "0");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("25-Dec-2010", 4, "452.6", "8.9", "1", "462.5", "0", "0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4);
        Date disbursementDate = TestUtils.getDate(25, 8, 2010);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.applyDailyInterestRates(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments), false);

        assertInstallment(installment1, "78.6", "20.4");
        assertInstallment(installment2, "180.8", "18.2");
        assertInstallment(installment3, "183.9", "15.1");
        assertInstallment(installment4, "556.7", "11.0");
    }

    //Test for the bug fix
    @Test
    public void shouldNotModifyPrincipalEvenIfMiscChargeIsPresent() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("25-Sep-2010", 1, "78.6", "20.4", "1", "200", "100", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("25-Oct-2010", 2, "182.8", "16.2", "1", "200", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("25-Nov-2010", 3, "186.0", "13.0", "1", "200", "0", "0");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("25-Dec-2010", 4, "452.6", "8.9", "1", "462.5", "0", "0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4);
        Date disbursementDate = TestUtils.getDate(25, 8, 2010);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.applyDailyInterestRates(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments), false);

        assertInstallment(installment1, "78.6", "20.4");
        assertInstallment(installment2, "180.8", "18.2");
        assertInstallment(installment3, "183.9", "15.1");
        assertInstallment(installment4, "556.7", "11.0");
    }

    @Test
    public void shouldGenerateWeeklyInstallmentScheduleFromRepaymentScheduleUsingDailyInterest() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "194.4", "4.6", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "195.3", "3.7", "1", "200", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "196.2", "2.8", "1", "200", "0", "0");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("22-Sep-2010", 4, "414.1", "1.9", "1", "417.0", "0", "0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4);
        Date disbursementDate = TestUtils.getDate(25, 8, 2010);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.applyDailyInterestRates(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments), false);

        assertInstallment(installment1, "94.4", "4.6");
        assertInstallment(installment2, "194.8", "4.2");
        assertInstallment(installment3, "195.7", "3.3");
        assertInstallment(installment4, "515.0", "2.4");
    }

    @Test
    public void shouldGenerateInstallmentScheduleFromRepaymentScheduleUsingDailyInterest() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("15-Oct-2010", 4, "84.9", "14.1", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment5 =
                getRepaymentScheduleInstallment("25-Oct-2010", 5, "94.9", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment6 =
                getRepaymentScheduleInstallment("01-Nov-2010", 6, "96.5", "2.5", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment7 =
                getRepaymentScheduleInstallment("18-Nov-2010", 7, "439.2", "4.9", "1", "445.1", "0", "0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3,
                installment4, installment5, installment6, installment7);
        Date disbursementDate = TestUtils.getDate(25, 8, 2010);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.applyDailyInterestRates(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments), false);

        assertInstallment(installment1, "69.4", "4.6");
        assertInstallment(installment2, "94.7", "4.3");
        assertInstallment(installment3, "95.2", "3.8");
        assertInstallment(installment4, "84.4", "14.6");
        assertInstallment(installment5, "94.7", "4.3");
        assertInstallment(installment6, "96.4", "2.6");
        assertInstallment(installment7, "465.2", "5.2");
    }

    @Test
    public void shouldMaintainInstallmentGapsPostDisbursal() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("15-Oct-2010", 4, "84.9", "14.1", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment5 =
                getRepaymentScheduleInstallment("25-Oct-2010", 5, "94.9", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment6 =
                getRepaymentScheduleInstallment("01-Nov-2010", 6, "96.5", "2.5", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment7 =
                getRepaymentScheduleInstallment("18-Nov-2010", 7, "439.2", "4.9", "1", "445.1", "0", "0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3,
                installment4, installment5, installment6, installment7);
        Date initialDisbursementDate = TestUtils.getDate(25, 8, 2010);
        Date disbursementDate = TestUtils.getDate(30, 8, 2010);
        when(holidayService.isFutureRepaymentHoliday(Matchers.<Calendar>any(), eq(officeId))).thenReturn(true);
        when(holidayService.getNextWorkingDay(Matchers.<Date>any(), eq(officeId))).thenReturn(TestUtils.getDate(6, 9, 2010),
                TestUtils.getDate(13, 9, 2010), TestUtils.getDate(20, 9, 2010), TestUtils.getDate(20, 10, 2010), TestUtils.getDate(30, 10, 2010),
                TestUtils.getDate(6, 11, 2010), TestUtils.getDate(23, 11, 2010));
        loanBusinessService.adjustInstallmentGapsPostDisbursal(installments, initialDisbursementDate, disbursementDate, officeId);
        assertInstallmentDueDate(installment1, "06-Sep-2010");
        assertInstallmentDueDate(installment2, "13-Sep-2010");
        assertInstallmentDueDate(installment3, "20-Sep-2010");
        assertInstallmentDueDate(installment4, "20-Oct-2010");
        assertInstallmentDueDate(installment5, "30-Oct-2010");
        assertInstallmentDueDate(installment6, "06-Nov-2010");
        assertInstallmentDueDate(installment7, "23-Nov-2010");
        verify(holidayService, times(7)).isFutureRepaymentHoliday(Matchers.<Calendar>any(), eq(officeId));
        verify(holidayService, times(7)).getNextWorkingDay(Matchers.<Date>any(), eq(officeId));
    }

    @Test
    public void shouldMaintainInstallmentGapsPostDisbursalWithNewDisbursalDateBeforeOldDisbursalDate() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75", "0", "0");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("15-Oct-2010", 4, "84.9", "14.1", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment5 =
                getRepaymentScheduleInstallment("25-Oct-2010", 5, "94.9", "4.2", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment6 =
                getRepaymentScheduleInstallment("01-Nov-2010", 6, "96.5", "2.5", "1", "100", "0", "0");
        RepaymentScheduleInstallment installment7 =
                getRepaymentScheduleInstallment("18-Nov-2010", 7, "439.2", "4.9", "1", "445.1", "0", "0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3,
                installment4, installment5, installment6, installment7);
        Date initialDisbursementDate = TestUtils.getDate(25, 8, 2010);
        Date disbursementDate = TestUtils.getDate(20, 8, 2010);
        when(holidayService.isFutureRepaymentHoliday(Matchers.<Calendar>any(), eq(officeId))).thenReturn(true);
        when(holidayService.getNextWorkingDay(Matchers.<Date>any(), eq(officeId))).thenReturn(TestUtils.getDate(27, 8, 2010),
                TestUtils.getDate(3, 9, 2010), TestUtils.getDate(10, 9, 2010), TestUtils.getDate(11, 10, 2010), TestUtils.getDate(21, 10, 2010),
                TestUtils.getDate(28, 10, 2010), TestUtils.getDate(15, 11, 2010));
        loanBusinessService.adjustInstallmentGapsPostDisbursal(installments, initialDisbursementDate, disbursementDate, officeId);
        assertInstallmentDueDate(installment1, "27-Aug-2010");
        assertInstallmentDueDate(installment2, "03-Sep-2010");
        assertInstallmentDueDate(installment3, "10-Sep-2010");
        assertInstallmentDueDate(installment4, "11-Oct-2010");
        assertInstallmentDueDate(installment5, "21-Oct-2010");
        assertInstallmentDueDate(installment6, "28-Oct-2010");
        assertInstallmentDueDate(installment7, "15-Nov-2010");
        verify(holidayService, times(7)).isFutureRepaymentHoliday(Matchers.<Calendar>any(), eq(officeId));
        verify(holidayService, times(7)).getNextWorkingDay(Matchers.<Date>any(), eq(officeId));
    }

    @Test
    public void persistOriginalSchedule() throws PersistenceException {
        Set<LoanScheduleEntity> installments = new LinkedHashSet<LoanScheduleEntity>();
        MifosCurrency mifosCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        Money money = new Money(mifosCurrency,"123");
        AccountBO accountBO = mock(AccountBO.class);
        CustomerBO customerBO = mock(CustomerBO.class);
        when(accountBO.getCurrency()).thenReturn(mifosCurrency);
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(accountBO, customerBO, new Short("1"),
                                        new java.sql.Date(new Date().getTime()), PaymentStatus.UNPAID, money,money);
        installments.add(loanScheduleEntity);
        when(loanBO.getLoanScheduleEntities()).thenReturn(installments);
        loanBusinessService.persistOriginalSchedule(loanBO);
        ArrayList<OriginalLoanScheduleEntity> expected = new ArrayList<OriginalLoanScheduleEntity>();
        expected.add(new OriginalLoanScheduleEntity(loanScheduleEntity));
        verify(legacyLoanDao).saveOriginalSchedule(Mockito.argThat(
                new OriginalLoanScheduleEntitiesMatcher(expected)
        ));
    }

    @Test
    public void originalLoanScheduleShouldPersistMiscFee() throws PersistenceException {
        Set<LoanScheduleEntity> installments = new LinkedHashSet<LoanScheduleEntity>();
        MifosCurrency mifosCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        Money money = new Money(mifosCurrency,"123");
        AccountBO accountBO = mock(AccountBO.class);
        CustomerBO customerBO = mock(CustomerBO.class);
        when(accountBO.getCurrency()).thenReturn(mifosCurrency);
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(accountBO, customerBO, new Short("1"),
                                        new java.sql.Date(new Date().getTime()), PaymentStatus.UNPAID, money,money);
        loanScheduleEntity.setMiscFee(money);
        installments.add(loanScheduleEntity);
        when(loanBO.getLoanScheduleEntities()).thenReturn(installments);
        loanBusinessService.persistOriginalSchedule(loanBO);
        ArrayList<OriginalLoanScheduleEntity> expected = new ArrayList<OriginalLoanScheduleEntity>();
        OriginalLoanScheduleEntity originalLoanScheduleEntity = new OriginalLoanScheduleEntity(loanScheduleEntity);
        assertEquals(originalLoanScheduleEntity.getMiscFee(),loanScheduleEntity.getMiscFee());
        expected.add(originalLoanScheduleEntity);
        verify(legacyLoanDao).saveOriginalSchedule(Mockito.argThat(
                new OriginalLoanScheduleEntitiesMatcher(expected)
        ));
    }
    @Test
    public void shouldRetrieveOriginalLoanSchedule() throws PersistenceException {
        Integer accountId = 1;

        ArrayList<OriginalLoanScheduleEntity> expected = new ArrayList<OriginalLoanScheduleEntity>();
        when(legacyLoanDao.getOriginalLoanScheduleEntity(accountId)).thenReturn(expected);
        List<OriginalLoanScheduleEntity> loanScheduleEntities = loanBusinessService.retrieveOriginalLoanSchedule(accountId);
        Assert.assertNotNull(loanScheduleEntities);
        verify(legacyLoanDao).getOriginalLoanScheduleEntity(accountId);
        Assert.assertEquals(expected,loanScheduleEntities);
    }

    @Test
    public void shouldComputeExtraInterestAsOfDateLaterThanLastPaymentDate() {
        Date asOfDate = TestUtils.getDate(10, 9, 2010);
        Date lastPaymentDate = TestUtils.getDate(1, 9, 2010);
        when(accountPaymentEntity.getPaymentDate()).thenReturn(lastPaymentDate);
        when(loanBO.findMostRecentNonzeroPaymentByPaymentDate()).thenReturn(accountPaymentEntity);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        Errors errors = loanBusinessService.computeExtraInterest(loanBO, asOfDate);
        assertThat(errors, is(not(nullValue())));
        assertThat(errors.hasErrors(), is(false));
        verify(scheduleCalculatorAdaptor).computeExtraInterest(loanBO, asOfDate);
    }

    @Test
    public void shouldComputeExtraInterestAsOfDateLaterThanLastPaymentDateAndNoLastPayment() {
        Date asOfDate = TestUtils.getDate(10, 9, 2010);
        when(loanBO.findMostRecentNonzeroPaymentByPaymentDate()).thenReturn(null);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        Errors errors = loanBusinessService.computeExtraInterest(loanBO, asOfDate);
        assertThat(errors, is(not(nullValue())));
        assertThat(errors.hasErrors(), is(false));
        verify(scheduleCalculatorAdaptor).computeExtraInterest(loanBO, asOfDate);
    }

    @Test
    public void shouldComputeExtraInterestAsOfDateEarlierThanLastPaymentDate() {
        Date asOfDate = TestUtils.getDate(1, 9, 2010);
        Date lastPaymentDate = TestUtils.getDate(10, 9, 2010);
        when(accountPaymentEntity.getPaymentDate()).thenReturn(lastPaymentDate);
        when(loanBO.findMostRecentNonzeroPaymentByPaymentDate()).thenReturn(accountPaymentEntity);
        when(loanBO.isDecliningBalanceInterestRecalculation()).thenReturn(true);
        Errors errors = loanBusinessService.computeExtraInterest(loanBO, asOfDate);
        assertThat(errors, is(not(nullValue())));
        assertThat(errors.hasErrors(), is(true));
        List<ErrorEntry> errorEntries = errors.getErrorEntries();
        assertThat(errorEntries, is(not(nullValue())));
        assertThat(errorEntries.size(), is(1));
        assertThat(errorEntries.get(0).getErrorCode(), is(LoanConstants.CANNOT_VIEW_REPAYMENT_SCHEDULE));
        verify(scheduleCalculatorAdaptor, never()).computeExtraInterest(loanBO, asOfDate);
    }

    private void assertInstallmentDueDate(RepaymentScheduleInstallment installment, String expectedDueDate) {
        String actualDueDate = DateUtils.getDBtoUserFormatString(installment.getDueDateValue(), locale);
        assertThat(actualDueDate, is(expectedDueDate));
    }

    private void assertInstallment(RepaymentScheduleInstallment installment, String principal, String interest) {
        assertThat(installment.getPrincipal().toString(Short.valueOf("1")), is(principal));
        assertThat(installment.getInterest().toString(Short.valueOf("1")), is(interest));
    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         String principal, String interest,
                                                                         String fees, String total, String miscFee, String miscPenality) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(new Money(rupee, principal)).withInterest(new Money(rupee, interest)).

                withFees(new Money(rupee, fees)).withMiscFee(new Money(rupee, miscFee)).withMiscPenality(new Money(rupee, miscPenality)).withTotalValue(total).build();
    }

}
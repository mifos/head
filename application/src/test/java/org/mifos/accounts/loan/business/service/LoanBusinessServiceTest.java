package org.mifos.accounts.loan.business.service;
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoanBusinessServiceTest {
    private LoanBusinessService loanBusinessService;
    private Locale locale;
    private RepaymentScheduleInstallmentBuilder installmentBuilder;
    private MifosCurrency rupee;

    @Mock
    private LoanBO loanBO;

    @Before
    public void setupAndInjectDependencies() {
        loanBusinessService = new LoanBusinessService();
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @Test
    public void shouldComputeVariableInstallmentScheduleForVariableInstallmentsIfVariableInstallmentsIsEnabled() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100");
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        when(loanAccountActionForm.getLoanAmount()).thenReturn("1000");
        when(loanAccountActionForm.getLoanAmountValue()).thenReturn(new Money(rupee, "1000"));
        when(loanAccountActionForm.getInterestRate()).thenReturn("24");
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(true);
        when(loanBO.toRepaymentScheduleDto(locale)).thenReturn(installments);
        loanBusinessService.
                computeInstallmentScheduleUsingDailyInterest(new LoanScheduleGenerationDto(getDate(2010, 8, 22),
                        loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), locale);
        verify(loanBO).copyInstallmentSchedule(installments);
        verify(loanBO).toRepaymentScheduleDto(locale);
    }

    @Test
    public void shouldComputeVariableInstallmentScheduleForVariableInstallmentsIfInterestTypeIsDecliningPrincipalBalance() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100");
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        when(loanAccountActionForm.getLoanAmount()).thenReturn("1000");
        when(loanAccountActionForm.getLoanAmountValue()).thenReturn(new Money(rupee, "1000"));
        when(loanAccountActionForm.getInterestRate()).thenReturn("24");
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(false);
        when(loanBO.toRepaymentScheduleDto(locale)).thenReturn(installments);
        when(loanBO.isDecliningPrincipalBalance()).thenReturn(true);
        loanBusinessService.computeInstallmentScheduleUsingDailyInterest(new LoanScheduleGenerationDto(getDate(2010, 8, 22),
                loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), locale);
        verify(loanBO).copyInstallmentSchedule(installments);
        verify(loanBO).toRepaymentScheduleDto(locale);
    }


    @Test
    public void shouldNotComputeVariableInstallmentSchedule() {
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(false);
        InterestTypesEntity interestTypesEntity = new InterestTypesEntity(InterestType.DECLINING);
        when(loanBO.getInterestType()).thenReturn(interestTypesEntity);
        loanBusinessService.computeInstallmentScheduleUsingDailyInterest(new LoanScheduleGenerationDto(getDate(2010, 8, 22),
                loanBO, loanAccountActionForm.isVariableInstallmentsAllowed(), loanAccountActionForm.getLoanAmountValue(),
                loanAccountActionForm.getInterestDoubleValue()), locale);
        verify(loanBO, never()).copyInstallmentSchedule(any(List.class));
    }

    @Test
    public void shouldGenerateMonthlyInstallmentScheduleFromRepaymentSchedule() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("25-Sep-2010", 1, "178.6", "20.4", "1", "100");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("25-Oct-2010", 2, "182.8", "16.2", "1", "200");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("25-Nov-2010", 3, "186.0", "13.0", "1", "200");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("25-Dec-2010", 4, "452.6", "8.9", "1", "462.5");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4);
        Date disbursementDate = getDate(2010, 8, 25);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.generateInstallmentSchedule(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments));

        assertInstallment(installment1, "78.6", "20.4");
        assertInstallment(installment2, "180.8", "18.2");
        assertInstallment(installment3, "183.9", "15.1");
        assertInstallment(installment4, "556.7", "11.0");
    }

    @Test
    public void shouldGenerateWeeklyInstallmentScheduleFromRepaymentSchedule() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "194.4", "4.6", "1", "100");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "195.3", "3.7", "1", "200");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "196.2", "2.8", "1", "200");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("22-Sep-2010", 4, "414.1", "1.9", "1", "417.0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4);
        Date disbursementDate = getDate(2010, 8, 25);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.generateInstallmentSchedule(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments));

        assertInstallment(installment1, "94.4", "4.6");
        assertInstallment(installment2, "194.8", "4.2");
        assertInstallment(installment3, "195.7", "3.3");
        assertInstallment(installment4, "515.0", "2.4");
    }

    @Test
    public void shouldGenerateInstallmentScheduleFromRepaymentSchedule() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "94.4", "4.6", "1", "75");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "94.8", "4.2", "1", "100");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "95.3", "3.7", "1", "100");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("15-Oct-2010", 4, "84.9", "14.1", "1", "100");
        RepaymentScheduleInstallment installment5 =
                getRepaymentScheduleInstallment("25-Oct-2010", 5, "94.9", "4.2", "1", "100");
        RepaymentScheduleInstallment installment6 =
                getRepaymentScheduleInstallment("01-Nov-2010", 6, "96.5", "2.5", "1", "100");
        RepaymentScheduleInstallment installment7 =
                getRepaymentScheduleInstallment("18-Nov-2010", 7, "439.2", "4.9", "1", "445.1");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3,
                installment4, installment5, installment6, installment7);
        Date disbursementDate = getDate(2010, 8, 25);
        final Money loanAmount = new Money(rupee, "1000");
        loanBusinessService.generateInstallmentSchedule(new LoanScheduleGenerationDto(disbursementDate, loanAmount, 24d, installments));

        assertInstallment(installment1, "69.4", "4.6");
        assertInstallment(installment2, "94.7", "4.3");
        assertInstallment(installment3, "95.2", "3.8");
        assertInstallment(installment4, "84.4", "14.6");
        assertInstallment(installment5, "94.7", "4.3");
        assertInstallment(installment6, "96.4", "2.6");
        assertInstallment(installment7, "465.2", "5.2");
    }

    private void assertInstallment(RepaymentScheduleInstallment installment, String principal, String interest) {
        assertThat(installment.getPrincipal().toString(), is(principal));
        assertThat(installment.getInterest().toString(), is(interest));
    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         String principal, String interest,
                                                                         String fees, String total) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(new Money(rupee, principal)).withInterest(new Money(rupee, interest)).
                withFees(new Money(rupee, fees)).withTotalValue(total).build();
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }
}
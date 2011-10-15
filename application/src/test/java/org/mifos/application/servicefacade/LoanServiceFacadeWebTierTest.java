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
package org.mifos.application.servicefacade;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.matchers.OriginalScheduleInfoDtoMatcher;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.validations.Errors;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LoanServiceFacadeWebTierTest {

    // class under test
    private LoanServiceFacade loanServiceFacade;

    // collaborators
    @Mock
    private LoanDao loanDao;

    @Mock
    private LoanBusinessService loanBusinessService;

    // test data
    @Mock
    private LoanBO loanBO;

    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency rupee;

    private Locale locale;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        loanServiceFacade = new LoanServiceFacadeWebTier(loanDao, loanBusinessService);
    }

    @Test
    public void retrieveOriginalLoanSchedule() throws PersistenceException {
        Integer accountId = new Integer(1);
        List<OriginalLoanScheduleEntity> loanScheduleEntities = new ArrayList<OriginalLoanScheduleEntity>();
        OriginalLoanScheduleEntity originalLoanScheduleEntity1 = mock(OriginalLoanScheduleEntity.class);
        OriginalLoanScheduleEntity originalLoanScheduleEntity2 = mock(OriginalLoanScheduleEntity.class);
        loanScheduleEntities.add(originalLoanScheduleEntity1);
        loanScheduleEntities.add(originalLoanScheduleEntity2);
        RepaymentScheduleInstallment installment1 = new RepaymentScheduleInstallment();
        RepaymentScheduleInstallment installment2 = new RepaymentScheduleInstallment();
        when(originalLoanScheduleEntity1.toDto()).thenReturn(installment1);
        when(originalLoanScheduleEntity2.toDto()).thenReturn(installment2);

        List<RepaymentScheduleInstallment> expected = new ArrayList<RepaymentScheduleInstallment>();
        expected.add(installment1);
        expected.add(installment2);
        Date date = new Date();
        when(loanBO.getDisbursementDate()).thenReturn(date);
        Money money = new Money(rupee, "4.9");
        when(loanBO.getLoanAmount()).thenReturn(money);
        when(loanBusinessService.retrieveOriginalLoanSchedule(accountId)).thenReturn(loanScheduleEntities);
        when(loanDao.findById(accountId)).thenReturn(loanBO);
        OriginalScheduleInfoDto expectedOriginalScheduleInfoDto = new OriginalScheduleInfoDto(money.toString(), date,expected);
        OriginalScheduleInfoDto originalScheduleInfoDto = loanServiceFacade.retrieveOriginalLoanSchedule(accountId);
        assertThat(originalScheduleInfoDto, is(new OriginalScheduleInfoDtoMatcher(expectedOriginalScheduleInfoDto)));
    }

    @Ignore
    @Test
    public void shouldValidateForRepaymentCapacity() {
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        BigDecimal loanAmount = new BigDecimal(1000);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, loanAmount, 10d);
        cashFlowForm.setTotalExpenses(BigDecimal.valueOf(76));
        cashFlowForm.setTotalRevenues(BigDecimal.valueOf(55).add(loanAmount));

        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).withTotalValue("10").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).withTotalValue("20").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).withTotalValue("30").build();
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);

        // calcuated repayment capacity is 3298.33
        Errors errors;
//        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1600d);
//        assertThat(errors.hasErrorEntryWithCode(AccountConstants.REPAYMENT_CAPACITY_LESS_THAN_ALLOWED), is(false));

//        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 3300d);
//        assertThat(errors.hasErrorEntryWithCode(AccountConstants.REPAYMENT_CAPACITY_LESS_THAN_ALLOWED), is(true));
    }

    @Ignore
    @Test
    public void shouldValidateForInstallmentDateBeyondCashFlowData() {
        ArrayList<MonthlyCashFlowDetail> monthlyCashFlows = new ArrayList<MonthlyCashFlowDetail>();
        DateTime dateTime = new DateTime().withDate(2010, 10, 30);
        monthlyCashFlows.add(new MonthlyCashFlowDetail(dateTime,new BigDecimal(123), new BigDecimal(234),""));
        monthlyCashFlows.add(new MonthlyCashFlowDetail(dateTime.plusMonths(1),new BigDecimal(123), new BigDecimal(234),""));
        monthlyCashFlows.add(new MonthlyCashFlowDetail(dateTime.plusMonths(2),new BigDecimal(123), new BigDecimal(234),""));
        CashFlowDetail cashFlowDetail = new CashFlowDetail(monthlyCashFlows);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(1000), 10d);
        cashFlowForm.setTotalExpenses(BigDecimal.valueOf(76));
        cashFlowForm.setTotalRevenues(BigDecimal.valueOf(55));

        RepaymentScheduleInstallment installment = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).
                                                        withTotalValue("10").withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installmentBeforeCashFlowDate = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).
                                                        withTotalValue("10").withDueDateValue("30-Nov-2010").build();
        List<RepaymentScheduleInstallment> installments = asList(installment);

        Errors errors;
//        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1600d);
//        assertThat(errors.hasErrorEntryWithCode(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE), is(false));

        RepaymentScheduleInstallment installmentBeyondCashFlowDate = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).
                                                        withTotalValue("10").withDueDateValue("30-Jan-2011").build();

        installments = asList(installmentBeyondCashFlowDate);
//        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1600d);
//        assertThat(errors.hasErrorEntryWithCode(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE), is(true));

//        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1700d);
//        assertThat(errors.hasErrorEntryWithCode(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE), is(true));
    }
}

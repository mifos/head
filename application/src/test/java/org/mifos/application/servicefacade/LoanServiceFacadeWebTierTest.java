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

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.validators.InstallmentValidationContext;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.validations.Errors;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LoanServiceFacadeWebTierTest {

    // class under test
    private LoanServiceFacade loanServiceFacade;

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
    private LoanBusinessService loanBusinessService;

    @Mock
    private LoanPrdBusinessService loanPrdBusinessService;

    // test data
    @Mock
    private CustomerBO customer;

    @Mock
    private ClientBO client;

    @Mock
    private LoanOfferingBO activeLoanProduct;

    @Mock
    private LoanBO loanBO;

    @Mock
    private InstallmentsValidator installmentsValidator;

    @Mock
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    @Mock
    private HolidayServiceFacade holidayServiceFacade;

    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency rupee;

    private Locale locale;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao, personnelDao,
                fundDao, loanDao, installmentsValidator, scheduleCalculatorAdaptor,loanBusinessService, holidayServiceFacade, loanPrdBusinessService);
    }

    @Test
    public void shouldFindAllActiveLoanProductsWithMeetingThatMatchCustomerMeeting() {

        // setup
        MeetingBO meeting = new MeetingBuilder().build();
        CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel.GROUP);

        List<LoanOfferingBO> activeLoanProducts = asList(activeLoanProduct);

        // stubbing
        when(customer.getCustomerLevel()).thenReturn(customerLevelEntity);
        when(loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(customerLevelEntity)).thenReturn(
                activeLoanProducts);
        when(customer.getCustomerMeetingValue()).thenReturn(meeting);
        when(activeLoanProduct.getLoanOfferingMeetingValue()).thenReturn(meeting);

        // exercise test
        List<PrdOfferingDto> activeLoanProductsForCustomer = loanServiceFacade.retrieveActiveLoanProductsApplicableForCustomer(customer);

        // verification
        assertThat(activeLoanProductsForCustomer, hasItem(activeLoanProduct.toDto()));
    }

    @Test
    public void shouldFindGlimDataForGroupCustomer() {

        // setup
        ValueListElement valueListElement = new BusinessActivityEntity(1, "", "");
        List<ValueListElement> loanPurposes = asList(valueListElement);

        List<ClientBO> clients = asList(client);

        // stubbing
        when(loanProductDao.findAllLoanPurposes()).thenReturn(loanPurposes);
        when(customerDao.findActiveClientsUnderGroup(customer)).thenReturn(clients);

        // exercise test
        LoanCreationGlimDto glimData = loanServiceFacade.retrieveGlimSpecificDataForGroup(customer);

        // verification
        assertThat(glimData.getLoanPurposes(), hasItem(valueListElement));
        assertThat(glimData.getActiveClientsOfGroup(), hasItem(client));
    }

    @Test
    public void shouldReturnRepayLoanDtoWithAllDataPopulated() throws PersistenceException {
        String accountNumber = "1234";
        LoanBO loanBO = mock(LoanBO.class);
        Money repaymentAmount = TestUtils.createMoney("1234");
        AcceptedPaymentTypePersistence persistence = mock(AcceptedPaymentTypePersistence.class);
        List<PaymentTypeEntity> paymentTypeEntities = new ArrayList<PaymentTypeEntity>();
        when(loanDao.findByGlobalAccountNum(accountNumber)).thenReturn(loanBO);
        when(persistence.getAcceptedPaymentTypesForATransaction(TestObjectFactory.TEST_LOCALE, TrxnTypes.loan_repayment.getValue())).thenReturn(paymentTypeEntities);
        when(loanBO.getEarlyRepayAmount()).thenReturn(repaymentAmount);
        Money interest = TestUtils.createMoney("100");
        when(loanBO.waiverAmount()).thenReturn(interest);
        Money waivedAmount = repaymentAmount.subtract(interest);
        RepayLoanDto repayLoanDto = loanServiceFacade.getRepaymentDetails(accountNumber, TestObjectFactory.TEST_LOCALE, persistence);
        assertEquals(repayLoanDto.getEarlyRepaymentMoney(), repaymentAmount);
        assertEquals(repayLoanDto.getWaivedRepaymentMoney(), waivedAmount);
        assertEquals(repayLoanDto.getPaymentTypeEntities(), paymentTypeEntities);
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
        try {
            loanServiceFacade.makeEarlyRepayment("1", "100", receiptNumber, date, paymentMethod, (short) 1, waiveInterest);
        } catch (AccountException e) {
            Assert.fail("Accounting exception should not have been thrown");
        }
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
        try {
            loanServiceFacade.makeEarlyRepayment("1", "100", receiptNumber,
                    date, paymentMethod, (short) 1, waiveInterest);
        } catch (AccountException e) {
            Assert.fail("Accounting exception should not have been thrown");
        }
        short userId = (short) 1;
        verify(loanBO).makeEarlyRepayment(new Money(dollar, "100"), receiptNumber, date, paymentMethod, userId, waiveInterest);
    }

    @Test
    public void shouldValidateInstallments() {
        int customerId = 121;
        Errors errors = new Errors();
        when(installmentsValidator.validateInputInstallments(anyListOf(RepaymentScheduleInstallment.class), any(InstallmentValidationContext.class))).thenReturn(errors);
        when(customerDao.findCustomerById(customerId)).thenReturn(customer);
        when(customer.getOfficeId()).thenReturn(Short.valueOf("1"));
        Errors actual = loanServiceFacade.validateInputInstallments(null, null, new ArrayList<RepaymentScheduleInstallment>(), customerId);
        assertThat(actual, is(errors));
        verify(installmentsValidator).validateInputInstallments(anyListOf(RepaymentScheduleInstallment.class), any(InstallmentValidationContext.class));
        verify(customerDao).findCustomerById(customerId);
        verify(customer).getOfficeId();
    }

    @Test
    public void shouldValidateInstallmentSchedule() {
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        Errors expectedErrors = new Errors();
        VariableInstallmentDetailsBO variableInstallmentDetailsBO = new VariableInstallmentDetailsBO();
        when(installmentsValidator.validateInstallmentSchedule(installments, variableInstallmentDetailsBO)).thenReturn(expectedErrors);
        Errors errors = loanServiceFacade.validateInstallmentSchedule(installments, variableInstallmentDetailsBO);
        assertThat(errors, is(expectedErrors));
        verify(installmentsValidator).validateInstallmentSchedule(installments, variableInstallmentDetailsBO);
    }

    @Test
    public void retrieveLoanRepaymentScheduleShouldComputeOverdue() {
        LoanBO loanBO = new LoanBO() {};
        Mockito.when(loanDao.findById(1)).thenReturn(loanBO);
        loanServiceFacade.retrieveLoanRepaymentSchedule(new UserContext(Locale.getDefault(), Short.valueOf("1")), 1, new DateMidnight().toDate());
        Mockito.verify(scheduleCalculatorAdaptor, Mockito.times(1)).computeExtraInterest(Mockito.eq(loanBO), Mockito.<Date>any());
        Mockito.verify(loanDao, Mockito.times(1)).findById(1);
    }

    @Test
    public void testValidateMakeEarlyRepayment() throws AccountException {
        Mockito.when(loanDao.findByGlobalAccountNum("1")).thenReturn(loanBO);
        boolean actualWaiveInterestValue = false;
        Mockito.when(loanBO.isInterestWaived()).thenReturn(actualWaiveInterestValue);
        try {
            loanServiceFacade.makeEarlyRepayment("1", "100", "001", mock(java.sql.Date.class),
                    "Cash", (short) 1, true);
        } catch (AccountException e) {
            verify(loanBO,never()).makeEarlyRepayment((Money) anyObject(), anyString(), (Date)anyObject(), anyString(), (Short)anyObject(),
                            anyBoolean());
            verify(loanBO,never()).getCurrency();
            assertThat(e.getKey(), is(LoanConstants.WAIVER_INTEREST_NOT_CONFIGURED));
        }
    }

    @Test
    public void shouldValidateForRepaymentCapacity() {
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(1000), 10d);
        cashFlowForm.setTotalExpenses(BigDecimal.valueOf(76));
        cashFlowForm.setTotalRevenues(BigDecimal.valueOf(55));

        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).withTotalValue("10").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).withTotalValue("20").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).withTotalValue("30").build();
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);

        // calcuated repayment capacity is 1631.67
        Errors errors;
        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1600d);
        assertThat(errors.hasErrorEntryWithCode(AccountConstants.REPAYMENT_CAPACITY_LESS_THAN_ALLOWED), is(false));

        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1700d);
        assertThat(errors.hasErrorEntryWithCode(AccountConstants.REPAYMENT_CAPACITY_LESS_THAN_ALLOWED), is(true));
    }

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
        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1600d);
        assertThat(errors.hasErrorEntryWithCode(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE), is(false));

        RepaymentScheduleInstallment installmentBeyondCashFlowDate = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "4.9")).
                                                        withTotalValue("10").withDueDateValue("30-Jan-2011").build();

        installments = asList(installmentBeyondCashFlowDate);
        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1600d);
        assertThat(errors.hasErrorEntryWithCode(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE), is(true));

        errors = loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, 1700d);
        assertThat(errors.hasErrorEntryWithCode(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE), is(true));
    }
}

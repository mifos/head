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
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.platform.validations.Errors;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 *
 */
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
    private ClientBusinessService clientBusinessService;

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
    ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private Locale locale;

    private MifosCurrency rupee;

    @Before
    public void setupAndInjectDependencies() {
        loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao, personnelDao, fundDao, loanDao, installmentsValidator, scheduleCalculatorAdaptor);
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
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
        ValueListElement valueListElement = new BusinessActivityEntity(Integer.valueOf(1), "", "");
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
        Errors errors = new Errors();
        when(installmentsValidator.validateInputInstallments(anyListOf(RepaymentScheduleInstallment.class), any(InstallmentValidationContext.class))).thenReturn(errors);
        Errors actual = loanServiceFacade.validateInputInstallments(null, null, new ArrayList<RepaymentScheduleInstallment>());
        assertThat(actual, is(errors));
        verify(installmentsValidator).validateInputInstallments(anyListOf(RepaymentScheduleInstallment.class), any(InstallmentValidationContext.class));
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
        loanServiceFacade.generateInstallmentSchedule(installments, new Money(rupee, "1000"), 24d, disbursementDate);

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
        loanServiceFacade.generateInstallmentSchedule(installments, new Money(rupee, "1000"), 24d, disbursementDate);

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
        loanServiceFacade.generateInstallmentSchedule(installments, new Money(rupee, "1000"), 24d, disbursementDate);

        assertInstallment(installment1, "69.4", "4.6");
        assertInstallment(installment2, "94.7", "4.3");
        assertInstallment(installment3, "95.2", "3.8");
        assertInstallment(installment4, "84.4", "14.6");
        assertInstallment(installment5, "94.7", "4.3");
        assertInstallment(installment6, "96.4", "2.6");
        assertInstallment(installment7, "465.2", "5.2");
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
        ((LoanServiceFacadeWebTier) loanServiceFacade).computeInstallmentScheduleUsingDailyInterest(loanAccountActionForm, loanBO, getDate(2010, 8, 22), locale);
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
         InterestTypesEntity interestTypesEntity = new InterestTypesEntity(InterestType.DECLINING_PB);
         when(loanBO.getInterestType()).thenReturn(interestTypesEntity);
         ((LoanServiceFacadeWebTier) loanServiceFacade).computeInstallmentScheduleUsingDailyInterest(loanAccountActionForm, loanBO, getDate(2010, 8, 22), locale);
         verify(loanBO).copyInstallmentSchedule(installments);
         verify(loanBO).toRepaymentScheduleDto(locale);
     }


    @Test
    public void shouldNotComputeVariableInstallmentSchedule() {
        LoanAccountActionForm loanAccountActionForm = mock(LoanAccountActionForm.class);
        when(loanAccountActionForm.isVariableInstallmentsAllowed()).thenReturn(false);
        InterestTypesEntity interestTypesEntity = new InterestTypesEntity(InterestType.DECLINING);
        when(loanBO.getInterestType()).thenReturn(interestTypesEntity);
        ((LoanServiceFacadeWebTier) loanServiceFacade).computeInstallmentScheduleUsingDailyInterest(loanAccountActionForm, loanBO, getDate(2010, 8, 22), null);
        verify(loanBO, never()).copyInstallmentSchedule(any(List.class));
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
        loanServiceFacade.retrieveLoanRepaymentSchedule(new UserContext(),1, new DateMidnight().toDate());
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

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
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
}

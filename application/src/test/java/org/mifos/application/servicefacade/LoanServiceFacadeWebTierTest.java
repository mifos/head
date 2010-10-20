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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.validators.InstallmentValidationContext;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.BusinessActivityEntity;
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
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.platform.validations.Errors;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private Locale locale;

    @Before
    public void setupAndInjectDependencies() {
        loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao, personnelDao, fundDao, loanDao, installmentsValidator);
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
    }

    @Test
    public void shouldFindAllActiveLoanProductsWithMeetingThatMatchCustomerMeeting() {

        // setup
        MeetingBO meeting = new MeetingBuilder().build();
        CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel.GROUP);

        List<LoanOfferingBO> activeLoanProducts = Arrays.asList(activeLoanProduct);

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
        List<ValueListElement> loanPurposes = Arrays.asList(valueListElement);

        List<ClientBO> clients = Arrays.asList(client);

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
        String paymentMethod = "Cash";
        String receiptNumber = "001";
        loanServiceFacade.makeEarlyRepayment("1", "100", receiptNumber,
                date, paymentMethod, (short) 1, waiveInterest);
        short userId = (short) 1;
        verify(loanBO).makeEarlyRepayment(new Money(dollar, "100"), receiptNumber, date, paymentMethod, userId, waiveInterest);
    }

    @Test
    public void shouldValidateInstallments() {
        Errors errors = new Errors();
        when(installmentsValidator.validate(anyListOf(RepaymentScheduleInstallment.class), any(InstallmentValidationContext.class))).thenReturn(errors);
        Errors actual = loanServiceFacade.validateInstallments(null, null, new ArrayList<RepaymentScheduleInstallment>());
        assertThat(actual, is(errors));
        verify(installmentsValidator).validate(anyListOf(RepaymentScheduleInstallment.class), any(InstallmentValidationContext.class));
    }

    @Test
    public void shouldGenerateMonthlyInstallmentScheduleFromRepaymentSchedule() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("25-Sep-2010", 1, rupee, "178.6", "20.4", "1", "100");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("25-Oct-2010", 2, rupee, "182.8", "16.2", "1", "200");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("25-Nov-2010", 3, rupee, "186.0", "13.0", "1", "200");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("25-Dec-2010", 4, rupee, "452.6", "8.9", "1", "462.5");
        List<RepaymentScheduleInstallment> installments = Arrays.asList(installment1, installment2, installment3, installment4);
        List<LoanScheduleEntity> scheduleEntities = new ArrayList<LoanScheduleEntity>();
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment1.getDueDateValue(), "178.6", "20.4"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment2.getDueDateValue(), "182.8", "16.2"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment3.getDueDateValue(), "186.0", "13.0"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment4.getDueDateValue(), "452.6", "8.9"));
        Date disbursementDate = getDate(2010, 8, 25);
        loanServiceFacade.generateInstallmentSchedule(installments, scheduleEntities, new Money(rupee, "1000"), 24d, disbursementDate);

        assertLoanScheduleEntity(scheduleEntities.get(0), "78.6", "20.4");
        assertLoanScheduleEntity(scheduleEntities.get(1), "180.8", "18.2");
        assertLoanScheduleEntity(scheduleEntities.get(2), "183.9", "15.1");
        assertLoanScheduleEntity(scheduleEntities.get(3), "556.7", "11.0");
    }

    @Test
    public void shouldGenerateWeeklyInstallmentScheduleFromRepaymentSchedule() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, rupee, "194.4", "4.6", "1", "100");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, rupee, "195.3", "3.7", "1", "200");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, rupee, "196.2", "2.8", "1", "200");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("22-Sep-2010", 4, rupee, "414.1", "1.9", "1", "417.0");
        List<RepaymentScheduleInstallment> installments = Arrays.asList(installment1, installment2, installment3, installment4);
        List<LoanScheduleEntity> scheduleEntities = new ArrayList<LoanScheduleEntity>();
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment1.getDueDateValue(), "194.4", "4.6"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment2.getDueDateValue(), "195.3", "3.7"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment3.getDueDateValue(), "196.2", "2.8"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment4.getDueDateValue(), "414.1", "1.9"));
        Date disbursementDate = getDate(2010, 8, 25);
        loanServiceFacade.generateInstallmentSchedule(installments, scheduleEntities, new Money(rupee, "1000"), 24d, disbursementDate);

        assertLoanScheduleEntity(scheduleEntities.get(0), "94.4", "4.6");
        assertLoanScheduleEntity(scheduleEntities.get(1), "194.8", "4.2");
        assertLoanScheduleEntity(scheduleEntities.get(2), "195.7", "3.3");
        assertLoanScheduleEntity(scheduleEntities.get(3), "515.0", "2.4");
    }

    @Test
    public void shouldGenerateInstallmentScheduleFromRepaymentSchedule() {
        MifosCurrency rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, rupee, "94.4", "4.6", "1", "75");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, rupee, "94.8", "4.2", "1", "100");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, rupee, "95.3", "3.7", "1", "100");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("15-Oct-2010", 4, rupee, "84.9", "14.1", "1", "100");
        RepaymentScheduleInstallment installment5 =
                getRepaymentScheduleInstallment("25-Oct-2010", 5, rupee, "94.9", "4.2", "1", "100");
        RepaymentScheduleInstallment installment6 =
                getRepaymentScheduleInstallment("01-Nov-2010", 6, rupee, "96.5", "2.5", "1", "100");
        RepaymentScheduleInstallment installment7 =
                getRepaymentScheduleInstallment("18-Nov-2010", 7, rupee, "439.2", "4.9", "1", "445.1");
        List<RepaymentScheduleInstallment> installments = Arrays.asList(installment1, installment2, installment3,
                installment4, installment5, installment6, installment7);
        List<LoanScheduleEntity> scheduleEntities = new ArrayList<LoanScheduleEntity>();
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment1.getDueDateValue(), "94.4", "4.6"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment2.getDueDateValue(), "94.8", "4.2"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment3.getDueDateValue(), "95.3", "3.7"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment4.getDueDateValue(), "84.9", "14.1"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment5.getDueDateValue(), "94.9", "4.2"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment6.getDueDateValue(), "96.5", "2.5"));
        scheduleEntities.add(getLoanScheduleEntity(rupee, installment7.getDueDateValue(), "439.2", "4.9"));
        Date disbursementDate = getDate(2010, 8, 25);
        loanServiceFacade.generateInstallmentSchedule(installments, scheduleEntities, new Money(rupee, "1000"), 24d, disbursementDate);

        assertLoanScheduleEntity(scheduleEntities.get(0), "69.4", "4.6");
        assertLoanScheduleEntity(scheduleEntities.get(1), "94.7", "4.3");
        assertLoanScheduleEntity(scheduleEntities.get(2), "95.2", "3.8");
        assertLoanScheduleEntity(scheduleEntities.get(3), "84.4", "14.6");
        assertLoanScheduleEntity(scheduleEntities.get(4), "94.7", "4.3");
        assertLoanScheduleEntity(scheduleEntities.get(5), "96.4", "2.6");
        assertLoanScheduleEntity(scheduleEntities.get(6), "465.2", "5.2");
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    private void assertLoanScheduleEntity(LoanScheduleEntity loanScheduleEntity, String principal, String interest) {
        assertThat(loanScheduleEntity.getPrincipal().toString(), is(principal));
        assertThat(loanScheduleEntity.getInterest().toString(), is(interest));
    }

    private LoanScheduleEntity getLoanScheduleEntity(MifosCurrency currency, Date date, String principal, String interest) {
        LoanBO loanBO = mock(LoanBO.class);
        when(loanBO.getCurrency()).thenReturn(currency);
        return new LoanScheduleEntity(loanBO, mock(CustomerBO.class), Short
                .valueOf("1"), new java.sql.Date(date.getTime()), PaymentStatus.UNPAID, new Money(currency, principal),
                new Money(currency, interest));
    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         MifosCurrency currency, String principal,
                                                                         String interest, String fees, String total) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(new Money(currency, principal)).withInterest(new Money(currency, interest)).
                withFees(new Money(currency, fees)).withTotalValue(total).build();
    }
}

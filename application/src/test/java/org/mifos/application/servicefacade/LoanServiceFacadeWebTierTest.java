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
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;
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

    private MeetingBO meeting;

    @Before
    public void setupAndInjectDependencies() {
        loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao, personnelDao, fundDao, loanDao);
    }

    @Test
    public void shouldFindAllActiveLoanProductsWithMeetingThatMatchCustomerMeeting() {

        // setup
        meeting = new MeetingBuilder().build();
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
        Date date = new Date(new java.util.Date().getTime());
        boolean waiveInterest = true;
        String paymentMethod = "Cash";
        String receiptNumber = "001";
        loanServiceFacade.makeEarlyRepayment("1", "100", receiptNumber,
                date, paymentMethod, (short) 1, waiveInterest);
        short userId = (short) 1;
        Mockito.verify(loanBO).makeEarlyRepayment(new Money(dollar, "100"), receiptNumber, date, paymentMethod, userId, waiveInterest);
    }
}

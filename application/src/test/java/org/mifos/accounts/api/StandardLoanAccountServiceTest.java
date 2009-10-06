/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.api;

import java.math.BigDecimal;

import org.easymock.internal.matchers.Any;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StandardLoanAccountServiceTest {

    private StandardLoanAccountService standardLoanAccountService;
    
    @Mock
    private AccountPersistence accountPersistence;

    @Mock
    private CustomerPersistence customerPersistence;
    
    @Mock
    private ConfigurationPersistence configurationPersistence;
    
    @Mock CustomerBO customerBO;
    
    private LoanBO accountBO;

    @BeforeClass
    public static void classSetup() {
        MifosLogManager.configureLogging();
    }
        
    @Before
    public void setup() {
        standardLoanAccountService = new StandardLoanAccountService();
        standardLoanAccountService.setAccountPersistence(accountPersistence);
        accountBO = new LoanAccountBuilder().withCustomer(customerBO).build();
        accountBO.setAccountPersistence(accountPersistence);
        accountBO.setCustomerPersistence(customerPersistence);
        accountBO.setConfigurationPersistence(configurationPersistence);
    }
    
    @Test
    public void testMakeLoanPayment() throws PersistenceException, AccountException {
        
 
        short userId = 1;
        int accountId = 100;
        UserReferenceDTO userMakingPayment = new UserReferenceDTO(userId);
        AccountReferenceDTO loanAccount = new AccountReferenceDTO(accountId);
        BigDecimal paymentAmount = new BigDecimal("100");
        LocalDate paymentDate = new LocalDate(2009,10,1);
        String comment = "some comment";
        
        when(accountPersistence.getAccount(accountId)).thenReturn(accountBO);      
        when(accountBO.isTrxnDateValid(paymentDate.toDateMidnight().toDate())).thenReturn(true);
        when(configurationPersistence.isRepaymentIndepOfMeetingEnabled()).thenReturn(true);
        
        // FIXME - work in progress Vanmh
        //standardLoanAccountService.makeLoanPayment(new AccountPaymentParametersDTO(userMakingPayment, loanAccount, paymentAmount, paymentDate, PaymentTypeDTO.CASH, comment));
    }
}

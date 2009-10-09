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

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.Matchers.*;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.api.accounts.AccountPaymentParametersDTO;
import org.mifos.api.accounts.AccountReferenceDTO;
import org.mifos.api.accounts.PaymentTypeDTO;
import org.mifos.api.accounts.UserReferenceDTO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.application.collectionsheet.persistence.LoanProductBuilder;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
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
    
    @Mock
    private PersonnelPersistence personnelPersistence;
    
    @Mock
    private PersonnelBO personnelBO;
    
    @Mock 
    private CustomerBO customerBO;
    
    private LoanBO accountBO;

    @BeforeClass
    public static void classSetup() {
        MifosLogManager.configureLogging();
    }
        
    @Before
    public void setup() throws AccountException {
        standardLoanAccountService = new StandardLoanAccountService();
        standardLoanAccountService.setAccountPersistence(accountPersistence);
        accountBO = new LoanAccountBuilder().withCustomer(customerBO).build();
        accountBO.setAccountPersistence(accountPersistence);
        accountBO.setCustomerPersistence(customerPersistence);
        accountBO.setConfigurationPersistence(configurationPersistence);
        accountBO.setPersonnelPersistence(personnelPersistence);
    }
    
    @Test
    public void testMakeLoanPayment() throws PersistenceException, AccountException {
        
 
        short userId = 1;
        int accountId = 100;
        int customerId = 1;
        UserReferenceDTO userMakingPayment = new UserReferenceDTO(userId);
        AccountReferenceDTO loanAccount = new AccountReferenceDTO(accountId);
        BigDecimal paymentAmount = new BigDecimal("100");
        LocalDate paymentDate = new LocalDate(2009,10,1);
        String comment = "some comment";
        java.sql.Date lastMeetingDate = new java.sql.Date(paymentDate.minusWeeks(3).toDateMidnight().getMillis());
        
        when(customerBO.getCustomerId()).thenReturn(customerId);
        when(accountPersistence.getAccount(accountId)).thenReturn(accountBO);      
        //when(accountBO.isTrxnDateValid(paymentDate.toDateMidnight().toDate())).thenReturn(true);
        when(configurationPersistence.isRepaymentIndepOfMeetingEnabled()).thenReturn(false);
        when(customerPersistence.getLastMeetingDateForCustomer(anyInt())).thenReturn(lastMeetingDate);
        when(personnelPersistence.getPersonnel(anyShort())).thenReturn(personnelBO);
        
        // FIXME - work in progress Vanmh
        //standardLoanAccountService.makeLoanPayment(new AccountPaymentParametersDTO(userMakingPayment, loanAccount, paymentAmount, paymentDate, PaymentTypeDTO.CASH, comment));
    }
    
    private class PrivateLoanAccountBuilder {
        
        private LoanOfferingBO loanProduct = new LoanProductBuilder().buildForUnitTests();
        private final Short numOfInstallments = Short.valueOf("5");
        private final GraceType gracePeriodType = GraceType.NONE;
        private final AccountTypes accountType = AccountTypes.LOAN_ACCOUNT;
        private final AccountState accountState = AccountState.LOAN_ACTIVE_IN_GOOD_STANDING;
        private CustomerBO customer;
        private final Integer offsettingAllowable = Integer.valueOf(1);
        
        private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
        private final java.util.Date createdDate = new DateTime().minusDays(14).toDate();
        private final Money loanAmount = new Money("1000");
        private final boolean noInterestDeductedAtDisbursement = false;
        private final double interestRate = 20;
        private final short gracePeriodDuration = 0;
        private final double maxLoan = 10000.0;
        private final double minLoan = 100.0;
        private final short maxInstall = 100;
        private final short minInstall = 2;
        /*
    public LoanBO(final UserContext userContext, 
        final LoanOfferingBO loanOffering, 
        final CustomerBO customer, 
        final AccountState accountState,
        final Money loanAmount, 
        final Short noOfinstallments, 
        final Date disbursementDate, 
        final boolean interestDeductedAtDisbursement,
        final Double interestRate, 
        final Short gracePeriodDuration, 
        final FundBO fund, 
        final List<FeeView> feeViews,
        final List<CustomFieldView> customFields, 
        final Boolean isRedone, 
        final Double maxLoanAmount, 
        final Double minLoanAmount,
        final Short maxNoOfInstall, 
        final Short minNoOfInstall, 
        final boolean isRepaymentIndepOfMeetingEnabled,
        final MeetingBO newMeetingForRepaymentDay)         
         */
        public LoanBO build() throws AccountException {
            final LoanBO loanAccount = new LoanBO(
                    TestUtils.makeUserWithLocales(), 
                    loanProduct, 
                    customer, 
                    accountState,
                    loanAmount, 
                    numOfInstallments, 
                    createdDate, 
                    noInterestDeductedAtDisbursement, 
                    interestRate, 
                    gracePeriodDuration,
                    null, 
                    null, 
                    null, 
                    false, 
                    maxLoan,
                    minLoan,
                    maxInstall,
                    minInstall,
                    false, 
                    null);
            return loanAccount;
        }

        public PrivateLoanAccountBuilder withLoanProduct(final LoanOfferingBO withLoanProduct) {
            this.loanProduct = withLoanProduct;
            return this;
        }
        
        public PrivateLoanAccountBuilder withCustomer(final CustomerBO withCustomer) {
            this.customer = withCustomer;
            return this;
        }
    }    
}

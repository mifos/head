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
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.mifos.api.accounts.AccountPaymentParametersDto;
import org.mifos.api.accounts.AccountReferenceDto;
import org.mifos.api.accounts.InvalidPaymentReason;
import org.mifos.api.accounts.PaymentTypeDto;
import org.mifos.api.accounts.UserReferenceDto;
import org.mifos.application.accounts.AccountIntegrationTestCase;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class StandardAccountServiceIntegrationTest extends AccountIntegrationTestCase {

    private StandardAccountService standardAccountService;
    
    public StandardAccountServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StaticHibernateUtil.commitTransaction();
    }
    
    public void testMakePayment() throws Exception {
        String paymentAmount = "700";
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                PaymentTypeDto.CASH,"");
        standardAccountService.makePayment(accountPaymentParametersDto);

        TestObjectFactory.updateObject(accountBO);
        Assert.assertEquals("The amount returned for the payment should have been " + paymentAmount, 
                Double.parseDouble(paymentAmount), accountBO.getLastPmntAmnt());
    }

    public void testValidateValidPayment() throws Exception {
        String paymentAmount = "700";
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                PaymentTypeDto.CASH,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertEquals(0, errors.size());
    }

    public void testValidatePaymentWithInvalidDate() throws Exception {
        String paymentAmount = "700";
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(1980, 1, 1), 
                PaymentTypeDto.CASH,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_DATE));
    }

    public void testMakePaymentComment() throws Exception {
        String paymentAmount = "700";
        String comment = "test comment";
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                PaymentTypeDto.CASH,
                comment);
        standardAccountService.makePayment(accountPaymentParametersDto);

        TestObjectFactory.updateObject(accountBO);
        Assert.assertEquals("We should get the comment back",
                comment, accountBO.getLastPmnt().getComment());
    }
    
    public void testLookupLoanIdFromExternalId() throws Exception {
        String externalId = "ABC";
        accountBO.setExternalId(externalId);
        accountBO.save();
        StaticHibernateUtil.commitTransaction();
        
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());
        standardAccountService.setLoanPersistence(new LoanPersistence());
        
        AccountReferenceDto accountReferenceDto = standardAccountService.lookupLoanAccountReferenceFromExternalId(externalId);

        Assert.assertEquals(accountBO.getAccountId().intValue(), accountReferenceDto.getAccountId());
    }       
}

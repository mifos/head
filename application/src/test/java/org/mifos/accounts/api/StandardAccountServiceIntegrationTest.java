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
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.AccountIntegrationTestCase;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class StandardAccountServiceIntegrationTest extends AccountIntegrationTestCase {

    private StandardAccountService standardAccountService;
    private List<PaymentTypeDto> paymentTypeDtos;
    private PaymentTypeDto defaultPaymentType;
    
    public StandardAccountServiceIntegrationTest() throws Exception {
        super();
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        standardAccountService = new StandardAccountService(new AccountPersistence(),
                new LoanPersistence(), new AcceptedPaymentTypePersistence());
        paymentTypeDtos = standardAccountService.getLoanPaymentTypes();   
        defaultPaymentType = paymentTypeDtos.get(0);
        StaticHibernateUtil.commitTransaction();
    }
    
    public void testMakePayment() throws Exception {
        String paymentAmount = "700";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                defaultPaymentType,"");
        standardAccountService.makePayment(accountPaymentParametersDto);

        TestObjectFactory.updateObject(accountBO);
        Assert.assertEquals("The amount returned for the payment should have been " + paymentAmount, 
                Double.parseDouble(paymentAmount), accountBO.getLastPmntAmnt());
    }

    public void testValidateValidPayment() throws Exception {
        String paymentAmount = "10";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertEquals(0, errors.size());
    }

    public void testValidatePaymentWithInvalidDate() throws Exception {
        String paymentAmount = "700";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(1980, 1, 1), 
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_DATE));
    }

    public void testMakePaymentComment() throws Exception {
        String paymentAmount = "700";
        String comment = "test comment";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                defaultPaymentType,
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
        
        standardAccountService.setAccountPersistence(new AccountPersistence());
        standardAccountService.setLoanPersistence(new LoanPersistence());
        
        AccountReferenceDto accountReferenceDto = standardAccountService.lookupLoanAccountReferenceFromExternalId(externalId);

        Assert.assertEquals(accountBO.getAccountId().intValue(), accountReferenceDto.getAccountId());
    }       

    public void testValidatePaymentWithInvalidPaymentType() throws Exception {
        String paymentAmount = "700";
        standardAccountService.setAccountPersistence(new AccountPersistence());
        PaymentTypeDto invalidPaymentType = new PaymentTypeDto((short)-1, "pseudo payment type! Not cash, check, etc.");

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                invalidPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE));
    }
    
    public void testValidatePaymentWithInvalidLargePaymentAmount() throws Exception {
        String paymentAmount = "700000";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT));
    }    

    public void testValidatePaymentWithInvalidNegativePaymentAmount() throws Exception {
        String paymentAmount = "-1";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDto(accountBO.getAccountId()), 
                new BigDecimal(paymentAmount), 
                new LocalDate(), 
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT));
    }
}

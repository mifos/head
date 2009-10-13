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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.mifos.api.accounts.AccountPaymentParametersDTO;
import org.mifos.api.accounts.AccountReferenceDTO;
import org.mifos.api.accounts.PaymentTypeDTO;
import org.mifos.api.accounts.UserReferenceDTO;
import org.mifos.application.accounts.AccountIntegrationTestCase;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * @author van
 *
 */
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
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDTO accountPaymentParametersDTO = new AccountPaymentParametersDTO(
                new UserReferenceDTO(accountBO.getPersonnel().getPersonnelId()), 
                new AccountReferenceDTO(accountBO.getAccountId()), 
                new BigDecimal("700"), 
                new LocalDate(), 
                PaymentTypeDTO.CASH,"");
        standardAccountService.makePayment(accountPaymentParametersDTO);

        TestObjectFactory.updateObject(accountBO);
        Assert.assertEquals("The amount returned for the payment should have been 700", 700.0, accountBO.getLastPmntAmnt());
    }
    
    public void testLookupLoanIdFromExternalId() throws Exception {
        String externalId = "ABC";
        accountBO.setExternalId(externalId);
        accountBO.save();
        StaticHibernateUtil.commitTransaction();
        
        standardAccountService = new StandardAccountService();
        standardAccountService.setAccountPersistence(new AccountPersistence());
        standardAccountService.setLoanPersistence(new LoanPersistence());
        
        AccountReferenceDTO accountReferenceDTO = standardAccountService.lookupLoanAccountReferenceFromExternalId(externalId);

        Assert.assertEquals(accountBO.getAccountId().intValue(), accountReferenceDTO.getAccountId());
    }       
}

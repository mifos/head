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

package org.mifos.accounts.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import junit.framework.Assert;
import org.junit.Test;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mockito.Mockito;

import java.util.ArrayList;

/**
 * In memory unit tests for AccountBO.
 *
 */

public class AccountBOTest {

    @Test
    public void testSetAndGetExternalId() {
        int accountId = 1;
        String externalId = "123ABC";

        AccountBO account = new AccountBO(accountId);
        account.setExternalId(externalId);

        assertThat(account.getExternalId(), is(externalId));
    }
    
    @Test
    public void testGetLastPmntToBeAdjustedReturnsFirstNonZeroElementInTheList(){
        AccountBO account = new AccountBO(1);
        AccountPaymentEntity accountPaymentEntity1 = Mockito.mock(AccountPaymentEntity.class);
        AccountPaymentEntity accountPaymentEntity2 = Mockito.mock(AccountPaymentEntity.class);
        AccountPaymentEntity accountPaymentEntity3 = Mockito.mock(AccountPaymentEntity.class);

        ArrayList<AccountPaymentEntity> accountPayments = new ArrayList<AccountPaymentEntity>();
        accountPayments.add(accountPaymentEntity1);
        accountPayments.add(accountPaymentEntity2);
        accountPayments.add(accountPaymentEntity3);

        account.setAccountPayments(accountPayments);
        Mockito.when(accountPaymentEntity1.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"0"));
        Mockito.when(accountPaymentEntity2.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"2"));
        Mockito.when(accountPaymentEntity3.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"3"));

        Assert.assertSame(accountPaymentEntity2,account.getLastPmntToBeAdjusted());
    }
    
    @Test
    public void testGetLastPmntToBeAdjustedForAListWithOnePayment(){
        AccountBO account = new AccountBO(1);
        AccountPaymentEntity accountPaymentEntity = Mockito.mock(AccountPaymentEntity.class);

        ArrayList<AccountPaymentEntity> accountPayments = new ArrayList<AccountPaymentEntity>();
        accountPayments.add(accountPaymentEntity);

        account.setAccountPayments(accountPayments);
        Mockito.when(accountPaymentEntity.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"100"));

        Assert.assertEquals(accountPaymentEntity,account.getLastPmntToBeAdjusted());
    }


    @Test
    public void testGetLastPmntToBeAdjustedReturnsNullIfNoNonZerPaymentIsDone(){
        AccountBO account = new AccountBO(1);
        AccountPaymentEntity accountPaymentEntity1 = Mockito.mock(AccountPaymentEntity.class);
        AccountPaymentEntity accountPaymentEntity2 = Mockito.mock(AccountPaymentEntity.class);
        AccountPaymentEntity accountPaymentEntity3 = Mockito.mock(AccountPaymentEntity.class);

        ArrayList<AccountPaymentEntity> accountPayments = new ArrayList<AccountPaymentEntity>();
        accountPayments.add(accountPaymentEntity1);
        accountPayments.add(accountPaymentEntity2);
        accountPayments.add(accountPaymentEntity3);

        account.setAccountPayments(accountPayments);
        Mockito.when(accountPaymentEntity1.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"0"));
        Mockito.when(accountPaymentEntity2.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"0"));
        Mockito.when(accountPaymentEntity3.getAmount()).thenReturn(new Money(TestUtils.RUPEE,"0"));

        Assert.assertNull(account.getLastPmntToBeAdjusted());

    }
}

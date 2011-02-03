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

package org.mifos.accounts.productsmix.persistence;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productsmix.business.ProductMixBO;
import org.mifos.accounts.productsmix.util.ProductMixTestHelper;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class ProductMixPersistenceIntegrationTest extends MifosIntegrationTestCase {

    MeetingBO meeting;
    MeetingBO meeting1;
    SavingsOfferingBO saving1;
    SavingsOfferingBO saving2;
    ProductMixBO prdmix;
    LegacyProductMixDao productMixPersistence = new LegacyProductMixDao();

    @After
    public void tearDown() throws Exception {
        prdmix = null;
        saving1 = null;
        saving2 = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetAllProductMix() throws PersistenceException {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        meeting1 = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        saving1 = ProductMixTestHelper.createSavingOffering("Savings Product1", "S1", meeting, meeting);
        saving2 = ProductMixTestHelper.createSavingOffering("Savings Product2", "S2", meeting1, meeting1);
        prdmix = TestObjectFactory.createAllowedProductsMix(saving1, saving2);
       Assert.assertEquals(1, (productMixPersistence.getAllProductMix()).size());

    }

    @Test
    public void testGetNotAllowedProducts() throws PersistenceException {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        meeting1 = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        saving1 = ProductMixTestHelper.createSavingOffering("Savings Product1", "S1", meeting, meeting);
        saving2 = ProductMixTestHelper.createSavingOffering("Savings Product2", "S2", meeting1, meeting1);
        prdmix = TestObjectFactory.createAllowedProductsMix(saving1, saving2);
       Assert.assertEquals(1, (productMixPersistence.getNotAllowedProducts(saving1.getPrdOfferingId())).size());

    }
}

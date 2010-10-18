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

package org.mifos.accounts.productsmix.business;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class ProductMixBOIntegrationTest extends MifosIntegrationTestCase {

    private SavingsOfferingBO savingsOffering;
    private SavingsTestHelper helper = new SavingsTestHelper();
    private ProductMixBO prdMix;

    @Before
    public void setUp() throws Exception {
        savingsOffering = helper.createSavingsOffering("Eddikhar", "Edd");
    }

    @After
    public void tearDown() throws Exception {

        TestObjectFactory.removeObject(prdMix);
        savingsOffering = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testUpdate() throws PersistenceException, ProductDefinitionException {
        prdMix = new ProductMixBO(savingsOffering, savingsOffering);
        prdMix.update();
       Assert.assertEquals(savingsOffering.getPrdOfferingId(), prdMix.getPrdOfferingId().getPrdOfferingId());
       Assert.assertEquals(savingsOffering.getPrdOfferingId(), prdMix.getPrdOfferingNotAllowedId().getPrdOfferingId());
    }

    @Test
    public void testDelete() throws PersistenceException, ProductDefinitionException {
        prdMix = new ProductMixBO(savingsOffering, savingsOffering);
        prdMix.update();
       Assert.assertEquals(savingsOffering.getPrdOfferingId(), prdMix.getPrdOfferingId().getPrdOfferingId());
        prdMix.delete();

    }
}

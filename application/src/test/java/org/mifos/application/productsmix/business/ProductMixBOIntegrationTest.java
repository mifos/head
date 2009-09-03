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

package org.mifos.application.productsmix.business;

import junit.framework.Assert;

import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.testng.annotations.Test;

@Test(groups={"integration", "productMixTestSuite"},  dependsOnGroups={"configTestSuite"})
public class ProductMixBOIntegrationTest extends MifosIntegrationTestCase {

    public ProductMixBOIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private SavingsOfferingBO savingsOffering;
    private SavingsTestHelper helper = new SavingsTestHelper();
    private ProductMixBO prdMix;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        savingsOffering = helper.createSavingsOffering("Eddikhar", "Edd");
    }

    @Override
    protected void tearDown() throws Exception {

        TestObjectFactory.removeObject(prdMix);
        TestObjectFactory.removeObject(savingsOffering);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testUpdate() throws PersistenceException, ProductDefinitionException {
        prdMix = new ProductMixBO(savingsOffering, savingsOffering);
        prdMix.update();
       Assert.assertEquals(savingsOffering.getPrdOfferingId(), prdMix.getPrdOfferingId().getPrdOfferingId());
       Assert.assertEquals(savingsOffering.getPrdOfferingId(), prdMix.getPrdOfferingNotAllowedId().getPrdOfferingId());
    }

    public void testDelete() throws PersistenceException, ProductDefinitionException {
        prdMix = new ProductMixBO(savingsOffering, savingsOffering);
        prdMix.update();
       Assert.assertEquals(savingsOffering.getPrdOfferingId(), prdMix.getPrdOfferingId().getPrdOfferingId());
        prdMix.delete();

    }
}

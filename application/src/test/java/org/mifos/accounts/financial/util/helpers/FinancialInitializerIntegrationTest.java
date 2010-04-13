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

package org.mifos.accounts.financial.util.helpers;
import junit.framework.Assert;

import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosIntegrationTestCase;

public class FinancialInitializerIntegrationTest extends MifosIntegrationTestCase {

    public FinancialInitializerIntegrationTest() throws Exception {
        super();
    }

    public void testAssetsCategoryIsCached() throws Exception {
        String assetsGlCode = "10000";
        COABO account1 = ChartOfAccountsCache.get(assetsGlCode);
       Assert.assertEquals(GLCategoryType.ASSET, account1.getCategoryType());
    }

    public void testFinancialActionInitializer() throws FinancialException {
        FinancialInitializer.initalizeFinancialAction();
        FinancialActionTypeEntity financialActionPrincipal = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

       Assert.assertEquals(financialActionPrincipal.getId().shortValue(), FinancialActionConstants.PRINCIPALPOSTING.value);
    }

    public void testCOACacherException() throws Exception {
        try {
            ChartOfAccountsCache.get("-1");
            Assert.fail("Expected FinancialException.");
        } catch (FinancialException e) {
            // do nothing
        }
    }

}

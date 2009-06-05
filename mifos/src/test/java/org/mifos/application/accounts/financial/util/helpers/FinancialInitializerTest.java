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

package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class FinancialInitializerTest {
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FinancialInitializerTest.class);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws SystemException, ApplicationException {
        new TestCaseInitializer().initialize();
    }

    @Test
    public void testAssetsCategoryIsCached() throws Exception {
        String assetsGlCode = "10000";
        COABO account1 = ChartOfAccountsCache.get(assetsGlCode);
        assertEquals(GLCategoryType.ASSET, account1.getCategoryType());
    }

    @Test
    public void testFinancialActionInitializer() throws FinancialException {
        FinancialInitializer.initalizeFinancialAction();
        FinancialActionBO financialActionPrincipal = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

        assertEquals(financialActionPrincipal.getId().shortValue(), FinancialActionConstants.PRINCIPALPOSTING.value);
    }

    @Test(expected = FinancialException.class)
    public void testCOACacherException() throws Exception {
        ChartOfAccountsCache.get("-1");
    }

}

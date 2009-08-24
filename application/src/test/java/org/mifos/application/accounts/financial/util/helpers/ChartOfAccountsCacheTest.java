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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import junit.framework.TestCase;

import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.AccountPersistence;

public class ChartOfAccountsCacheTest extends TestCase {

    private static final Short GL_CODE_ENTITY_ID = 197;
    private static final String GL_CODE = "17777";
    private static final int ACCOUNT_ID = 197;
    private static final String ACCOUNT_NAME = "test account";
    private static final String INCOME_GL_ACCOUNT_CODE = "30000";

    private COABO testAccount = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        GLCodeEntity glCodeEntityForStandardAccount = new GLCodeEntity(GL_CODE_ENTITY_ID, GL_CODE);
        testAccount = new COABO(ACCOUNT_ID, ACCOUNT_NAME, glCodeEntityForStandardAccount);

        /*
         * Hack. Has no effect when this test is run alone. When running as part
         * of a full unit/integration test, the ChartOfAccounts static cache
         * will have already been populated, so the add (below) would otherwise
         * cause an exception to be thrown.
         */
        ChartOfAccountsCache.remove(testAccount);

        ChartOfAccountsCache.add(testAccount);

        GLCodeEntity glCodeEntityForTopLevelAccount = new GLCodeEntity(GL_CODE_ENTITY_ID, INCOME_GL_ACCOUNT_CODE);
        COABO testIncomeAccount = new COABO(ACCOUNT_ID, ACCOUNT_NAME, glCodeEntityForTopLevelAccount);

        /*
         * Hack. Has no effect when this test is run alone. When running as part
         * of a full unit/integration test, the ChartOfAccounts static cache
         * will have already been populated, so the add (below) would otherwise
         * cause an exception to be thrown.
         */
        ChartOfAccountsCache.remove(testIncomeAccount);

        ChartOfAccountsCache.add(testIncomeAccount);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testAccount = null;
    }

    public void testAddAndGet() throws FinancialException {
        COABO cachedAccount = ChartOfAccountsCache.get(GL_CODE);
        assertEquals(GL_CODE, cachedAccount.getGlCode());
        assertEquals(ACCOUNT_NAME, cachedAccount.getAccountName());
        assertEquals(testAccount, cachedAccount);
    }

    public void testTopLevelAccountCached() throws Exception {
        AccountPersistence ap = createMockAccountPersistance();
        COABO income = ap.getCategory(GLCategoryType.INCOME);
        COABO cachedIncome = ChartOfAccountsCache.get(INCOME_GL_ACCOUNT_CODE);
        assertEquals(income, cachedIncome);
    }

    private AccountPersistence createMockAccountPersistance() {
        AccountPersistence ap = createMock(AccountPersistence.class);
        expect(ap.getCategory(GLCategoryType.INCOME)).andReturn(new COABO(ACCOUNT_ID, ACCOUNT_NAME));
        replay(ap);
        return ap;
    }
}

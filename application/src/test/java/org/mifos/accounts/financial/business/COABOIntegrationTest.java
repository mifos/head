/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounts.financial.business;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class COABOIntegrationTest extends MifosIntegrationTestCase {

    private final int FINANCIAL_DIRECTINCOME_SIZE = 10;

    @Test
    public void testGetCOAHead() throws FinancialException {
        String interestOnLoansGlCode = "31101";
        COABO coaInterestLoans = ChartOfAccountsCache.get(interestOnLoansGlCode);
        COABO coaHead = coaInterestLoans.getCOAHead();
       Assert.assertEquals(GLCategoryType.INCOME, coaHead.getCategoryType());
    }

    @Test
    public void testGetCurrentSubCategory() throws FinancialException {
        String directIncomeGlCode = "31000";
        COABO coaDirectIncome = ChartOfAccountsCache.get(directIncomeGlCode);
        Set<COABO> currentSubCategory = coaDirectIncome.getCurrentSubCategory();
       Assert.assertEquals(FINANCIAL_DIRECTINCOME_SIZE, currentSubCategory.size());

        GLCodeEntity g = (GLCodeEntity) StaticHibernateUtil.getSessionTL().load(GLCodeEntity.class, new Short("1"));
        System.out.println(g.getGlcode());
    }

    @Test
    public void testEqualsAndHasCode() throws Exception {
        COABO x = new COABO(53, "Example 1");
        COABO notx = new COABO(54, "Example 1");
        COABO y = new COABO(53, "ChangedName");
        COABO z = new COABO(53, "Example 1") {
        };

        TestUtils.assertEqualsAndHashContract(x, notx, y, z);
    }

    @Test
    public void testGetSubCategoryCOABOs() throws FinancialException {
        String[] SUB_CATEGORY_NAMES = { "Petty Cash Accounts", "Bank Balances", "Transfers" };
        String[] SUB_CATEGORY_GLCODES = { "11100", "11200", "11300"};

        String cashBankBalanceGlCode = "11000";
        COABO coa = ChartOfAccountsCache.get(cashBankBalanceGlCode);
        List<COABO> subCategories = coa.getSubCategoryCOABOs();
        for (int index = 0; index < subCategories.size(); ++index) {
            COABO subcat1 = subCategories.get(index);
           Assert.assertEquals(SUB_CATEGORY_NAMES[index], subcat1.getAccountName());
           Assert.assertEquals(SUB_CATEGORY_GLCODES[index], subcat1.getAssociatedGlcode().getGlcode());
        }
    }

}

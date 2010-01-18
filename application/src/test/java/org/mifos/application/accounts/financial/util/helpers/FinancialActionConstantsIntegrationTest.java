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

import junit.framework.Assert;

import org.hibernate.Query;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class FinancialActionConstantsIntegrationTest extends MifosIntegrationTestCase {

    public FinancialActionConstantsIntegrationTest() throws Exception {
        super();
    }

    /*
     * Verify that the number of elements in the enum {@link
     * FinancialActionConstants} matches the number of elements in the
     * corresponding table "financial_actions".
     */
    public void testGetFinancialAction() {
        Query queryFinancialAction = StaticHibernateUtil.getSessionTL().getNamedQuery(
                FinancialQueryConstants.GET_ALL_FINANCIAL_ACTION);
        // NOTE: the following 2 database entries are not currently represented
        // in the enum. Should they be added to the enum or removed from the db?
        // FIN_ACTION_ID = 15 (Interest_Posting)
        // FIN_ACTION_ID = 17 (Customer_Adjustment)
        // The (-2) adjustment to the assert compensates for this difference

       Assert.assertEquals(FinancialActionConstants.values().length, queryFinancialAction.list().size() - 2);
    }

}

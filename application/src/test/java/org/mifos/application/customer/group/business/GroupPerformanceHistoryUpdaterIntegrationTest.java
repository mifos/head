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

package org.mifos.application.customer.group.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import junit.framework.Assert;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.framework.MifosIntegrationTestCase;

public class GroupPerformanceHistoryUpdaterIntegrationTest extends MifosIntegrationTestCase {

    public GroupPerformanceHistoryUpdaterIntegrationTest() throws Exception {
        super();
    }

    public void testShouldMatchClientsWithParentAccounts() throws Exception {
        LoanBO parentLoanMock1 = createMock(LoanBO.class);
        expect(parentLoanMock1.getAccountId()).andReturn(1).times(3);

        LoanBO parentLoanMock2 = createMock(LoanBO.class);
        expect(parentLoanMock2.getAccountId()).andReturn(2);

        LoanBO childLoanMock1 = createMock(LoanBO.class);
        expect(childLoanMock1.getParentAccount()).andReturn(parentLoanMock1);
        expect(childLoanMock1.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT)).andReturn(true);

        LoanBO childLoanMock2 = createMock(LoanBO.class);
        expect(childLoanMock2.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT)).andReturn(true);
        expect(childLoanMock2.getParentAccount()).andReturn(parentLoanMock2);

        replay(parentLoanMock1, parentLoanMock2, childLoanMock1, childLoanMock2);
        GroupPerformanceHistoryUpdater.ClientAccountWithParentAccountMatcher matcher = new GroupPerformanceHistoryUpdater.ClientAccountWithParentAccountMatcher(
                parentLoanMock1);
       Assert.assertTrue(matcher.evaluate(childLoanMock1));
        Assert.assertFalse(matcher.evaluate(childLoanMock2));
        verify(parentLoanMock1, parentLoanMock2, childLoanMock1, childLoanMock2);
    }
}

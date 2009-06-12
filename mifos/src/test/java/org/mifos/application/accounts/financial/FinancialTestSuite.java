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

package org.mifos.application.accounts.financial;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.financial.business.COABOIntegrationTest;
import org.mifos.application.accounts.financial.business.FinancialBOIntegrationTest;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCacheTest;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCacheIntegrationTest;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstantsIntegrationTest;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializerTest;
import org.mifos.application.accounts.financial.util.helpers.FinancialRulesTest;

public class FinancialTestSuite extends TestSuite {

    public FinancialTestSuite() throws Exception {
        super();
    }

    public static void main(String[] args) throws Exception {
        Test testSuite = suite();

        TestRunner.run(testSuite);
    }

    public static Test suite() throws Exception {
        TestSuite testSuite = new FinancialTestSuite();
        testSuite.addTestSuite(FinancialActionCacheIntegrationTest.class);
        testSuite.addTestSuite(FinancialInitializerTest.class);
        testSuite.addTestSuite(ChartOfAccountsCacheTest.class);
        testSuite.addTestSuite(FinancialRulesTest.class);
        testSuite.addTestSuite(COABOIntegrationTest.class);
        testSuite.addTestSuite(FinancialBOIntegrationTest.class);
        testSuite.addTestSuite(FinancialActionConstantsIntegrationTest.class);

        return testSuite;

    }

}

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

package org.mifos.config;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.mifos.config.LocalizationIntegrationTest;
import org.mifos.config.FiscalCalendarRulesTest;

public class ConfigTestSuite extends TestSuite {

    public ConfigTestSuite() throws Exception {
    }

    public static void main(String[] args) throws Exception {
        Test testSuite = suite();
        TestRunner.run(testSuite);
    }

    public static Test suite() throws Exception {
        TestSuite suite = new ConfigTestSuite();
        suite.addTest(AccountingRulesIntegrationTest.suite());
        suite.addTestSuite(LocalizationIntegrationTest.class);
        suite.addTest(FiscalCalendarRulesTest.suite());
        suite.addTest(ClientRulesIntegrationTest.suite());
        suite.addTest(ProcessFlowRulesTest.suite());
        suite.addTest(ChartOfAccountsConfigTest.suite());
        suite.addTest(GeneralConfigIntegrationTest.suite());

        return suite;
    }
}

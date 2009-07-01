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

package org.mifos.framework.components.batchjobs;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.components.batchjobs.helpers.CollectionSheetHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.PortfolioAtRiskCalculationIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.ApplyCustomerFeeChangesHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.ApplyHolidayChangesHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.CustomerFeeHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.GenerateMeetingsForCustomerAndSavingsHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.LoanArrearsAgingHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.LoanArrearsHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.LoanArrearsTaskIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.PortfolioAtRiskHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.ProductStatusHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.RegenerateScheduleHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.SavingsIntCalcHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.helpers.SavingsIntPostingHelperIntegrationTest;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistenceIntegrationTest;
import org.mifos.framework.components.batchjobs.MifosSchedulerIntegrationTest;

public class BatchJobTestSuite extends TestSuite {

    public BatchJobTestSuite() {
        super();
    }

    public static Test suite() throws Exception {
        TestSuite testSuite = new BatchJobTestSuite();
        testSuite.addTestSuite(LoanArrearsTaskIntegrationTest.class);
        testSuite.addTestSuite(LoanArrearsHelperIntegrationTest.class);
        testSuite.addTestSuite(SavingsIntCalcHelperIntegrationTest.class);
        testSuite.addTestSuite(SavingsIntPostingHelperIntegrationTest.class);
        testSuite.addTestSuite(CustomerFeeHelperIntegrationTest.class);
        testSuite.addTestSuite(RegenerateScheduleHelperIntegrationTest.class);
        testSuite.addTestSuite(ApplyCustomerFeeChangesHelperIntegrationTest.class);
        testSuite.addTestSuite(ProductStatusHelperIntegrationTest.class);
        testSuite.addTestSuite(LoanArrearsAgingHelperIntegrationTest.class);
        testSuite.addTestSuite(PortfolioAtRiskHelperIntegrationTest.class);
        testSuite.addTestSuite(MifosSchedulerIntegrationTest.class);
        testSuite.addTestSuite(TaskPersistenceIntegrationTest.class);
        testSuite.addTestSuite(GenerateMeetingsForCustomerAndSavingsHelperIntegrationTest.class);
        testSuite.addTestSuite(CollectionSheetHelperIntegrationTest.class);
        testSuite.addTestSuite(ApplyHolidayChangesHelperIntegrationTest.class);
        testSuite.addTestSuite(PortfolioAtRiskCalculationIntegrationTest.class);
        return testSuite;
    }
}

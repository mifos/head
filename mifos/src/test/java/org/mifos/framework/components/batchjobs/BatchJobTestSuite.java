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

import org.mifos.framework.components.batchjobs.helpers.CollectionSheetHelperTest;
import org.mifos.framework.components.batchjobs.helpers.PortfolioAtRiskCalculationTest;
import org.mifos.framework.components.batchjobs.helpers.TestApplyCustomerFeeChangesHelper;
import org.mifos.framework.components.batchjobs.helpers.TestApplyHolidayChangesHelper;
import org.mifos.framework.components.batchjobs.helpers.TestCustomerFeeHelper;
import org.mifos.framework.components.batchjobs.helpers.TestGenerateMeetingsForCustomerAndSavingsHelper;
import org.mifos.framework.components.batchjobs.helpers.TestLoanArrearsAgingHelper;
import org.mifos.framework.components.batchjobs.helpers.TestLoanArrearsHelper;
import org.mifos.framework.components.batchjobs.helpers.TestLoanArrearsTask;
import org.mifos.framework.components.batchjobs.helpers.TestPortfolioAtRiskHelper;
import org.mifos.framework.components.batchjobs.helpers.TestProductStatusHelper;
import org.mifos.framework.components.batchjobs.helpers.TestRegenerateScheduleHelper;
import org.mifos.framework.components.batchjobs.helpers.TestSavingsIntCalcHelper;
import org.mifos.framework.components.batchjobs.helpers.TestSavingsIntPostingHelper;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistenceTest;

public class BatchJobTestSuite extends TestSuite {

	public BatchJobTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new BatchJobTestSuite();
		testSuite.addTestSuite(TestLoanArrearsTask.class);
		testSuite.addTestSuite(TestLoanArrearsHelper.class);
		testSuite.addTestSuite(TestSavingsIntCalcHelper.class);
		testSuite.addTestSuite(TestSavingsIntPostingHelper.class);
		testSuite.addTestSuite(TestCustomerFeeHelper.class);
		testSuite.addTestSuite(TestRegenerateScheduleHelper.class);
		testSuite.addTestSuite(TestApplyCustomerFeeChangesHelper.class);
		testSuite.addTestSuite(TestProductStatusHelper.class);
		testSuite.addTestSuite(TestLoanArrearsAgingHelper.class);
		testSuite.addTestSuite(TestPortfolioAtRiskHelper.class);
		testSuite.addTestSuite(MifosSchedulerTest.class);
		testSuite.addTestSuite(TaskPersistenceTest.class);
		testSuite.addTestSuite(TestGenerateMeetingsForCustomerAndSavingsHelper.class);
		testSuite.addTestSuite(CollectionSheetHelperTest.class);
		testSuite.addTestSuite(TestApplyHolidayChangesHelper.class);
		testSuite.addTestSuite(PortfolioAtRiskCalculationTest.class);
		return testSuite;
	}
}

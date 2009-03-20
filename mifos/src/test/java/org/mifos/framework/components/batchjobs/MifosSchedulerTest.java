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

import java.util.List;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class MifosSchedulerTest extends MifosIntegrationTest {

	public MifosSchedulerTest() throws SystemException, ApplicationException {
        super();
    }

    @Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRegisterTasks() throws Exception {
		MifosScheduler mifosScheduler = new MifosScheduler();
		mifosScheduler.registerTasks();
		List<String> taskNames = mifosScheduler.getTaskNames();
		assertEquals(13, taskNames.size());
		assertTrue(taskNames.contains("ProductStatus"));
		//assertTrue(taskNames.contains("CollectionSheetTask"));
		assertTrue(taskNames.contains("LoanArrearsTask"));
		assertTrue(taskNames.contains("SavingsIntCalcTask"));
		assertTrue(taskNames.contains("SavingsIntPostingTask"));
		assertTrue(taskNames.contains("ApplyCustomerFeeTask"));
		assertTrue(taskNames.contains("RegenerateScheduleTask"));
		assertTrue(taskNames.contains("PortfolioAtRiskTask"));
		assertTrue(taskNames.contains("ApplyCustomerFeeChangesTask"));
		assertTrue(taskNames.contains("GenerateMeetingsForCustomerAndSavingsTask"));
		assertTrue(taskNames.contains("LoanArrearsAgingTask"));
		assertTrue(taskNames.contains("ApplyHolidayChangesTask"));
		assertTrue(taskNames.contains("CollectionSheetReportParameterCachingTask"));
		assertTrue(taskNames.contains("BranchReportTask"));
		// Temporarily commented out as requested by issue 1881
		//assertTrue(taskNames.contains("BranchCashConfirmationTask"));
	}
}

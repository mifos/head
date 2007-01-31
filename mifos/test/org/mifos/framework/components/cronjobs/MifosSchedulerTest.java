package org.mifos.framework.components.cronjobs;

import java.util.List;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.MifosScheduler;

public class MifosSchedulerTest extends MifosTestCase {

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
		assertEquals(11, taskNames.size());
		assertTrue(taskNames.contains("ProductStatus"));
		assertTrue(taskNames.contains("CollectionSheetTask"));
		assertTrue(taskNames.contains("LoanArrearsTask"));
		assertTrue(taskNames.contains("SavingsIntCalcTask"));
		assertTrue(taskNames.contains("SavingsIntPostingTask"));
		assertTrue(taskNames.contains("ApplyCustomerFeeTask"));
		assertTrue(taskNames.contains("RegenerateScheduleTask"));
		assertTrue(taskNames.contains("PortfolioAtRiskTask"));
		assertTrue(taskNames.contains("ApplyCustomerFeeChangesTask"));
		assertTrue(taskNames.contains("GenerateMeetingsForCustomerAndSavingsTask"));
		assertTrue(taskNames.contains("LoanArrearsAgingTask"));
	}
}

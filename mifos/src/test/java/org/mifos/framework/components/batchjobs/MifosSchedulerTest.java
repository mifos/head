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

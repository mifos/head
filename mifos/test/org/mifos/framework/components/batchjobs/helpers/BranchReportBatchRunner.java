package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.MifosTestCase;
//TODO remove this class in final commit
public class BranchReportBatchRunner extends MifosTestCase {
	public void testRunBatch() throws Exception {
//		new LoanArrearsAgingHelper(new LoanArrearsAgingTask()).execute(System.currentTimeMillis());
//		new BranchCashConfirmationReportHelper(new BranchCashConfirmationTask())
//				.execute(System.currentTimeMillis());
		new BranchReportHelper(new BranchReportTask()).execute(System
				.currentTimeMillis());
	}
}

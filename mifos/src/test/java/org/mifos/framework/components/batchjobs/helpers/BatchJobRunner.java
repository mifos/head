package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.MifosTestCase;
public class BatchJobRunner extends MifosTestCase {
	public void testRunBatch() throws Exception {
//		new CollectionSheetHelper(new CollectionSheetTask()).execute(System.currentTimeMillis());
		new BranchReportHelper(new BranchReportTask()).execute(System.currentTimeMillis());
	}
}
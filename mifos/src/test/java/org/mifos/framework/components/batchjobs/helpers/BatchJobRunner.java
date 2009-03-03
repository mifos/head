package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
public class BatchJobRunner extends MifosIntegrationTest {
	public BatchJobRunner() throws SystemException, ApplicationException {
        super();
    }

    public void testRunBatch() throws Exception {
//		new CollectionSheetHelper(new CollectionSheetTask()).execute(System.currentTimeMillis());
		new BranchReportHelper(new BranchReportTask()).execute(System.currentTimeMillis());
	}
}
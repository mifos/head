package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.MifosBatchJob;
import org.mifos.framework.components.batchjobs.TaskHelper;

public class ETLReportDWTask extends MifosBatchJob {

    @Override
    public TaskHelper getTaskHelper() {
        return new BranchReportHelper();
    }

    @Override
    public void requiresExclusiveAccess() {
        MifosBatchJob.batchJobRequiresExclusiveAccess(false);
    }

}

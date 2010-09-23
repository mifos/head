package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;

public class MockLoanArrearsHelper extends TaskHelper {

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        throw new BatchJobException(null);
    }

}

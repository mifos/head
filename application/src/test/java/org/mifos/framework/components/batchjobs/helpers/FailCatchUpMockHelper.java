package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;

/**
 * This superclass of ProductStatusHelper is set to do the following:
 * Fail to run execute method (throw an exception) three times, then
 * continue to work correctly afterwards.
 * <br /><br />
 * It's used in catch-up mechanism testing.
 */
public class FailCatchUpMockHelper extends ProductStatusHelper {

    private static int FAIL_COUNT = 3;

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        if(FAIL_COUNT > 0) {
            FAIL_COUNT--;
            throw new BatchJobException(null);
        }
        super.execute(timeInMillis);
    }

}

package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;

/**
 * This superclass of ProductStatusHelper is set to do the following:
 * Fail to run execute method (throw an exception) three times, then
 * run execute correctly two times, the fail once more and continue to
 * work correctly afterwards.
 * <br /><br />
 * It's used in catch-up mechanism testing.
 */
public class CatchUpErrorMockHelper extends ProductStatusHelper {

    private static int COUNTER = 0;

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        if(COUNTER < 3) {
            COUNTER++;
            throw new BatchJobException(null);
        }
        if(COUNTER == 5) {
            COUNTER++;
            throw new BatchJobException(null);
        }
        super.execute(timeInMillis);
        COUNTER++;
    }

}

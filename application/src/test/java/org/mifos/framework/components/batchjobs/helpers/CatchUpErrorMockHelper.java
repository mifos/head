package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;

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

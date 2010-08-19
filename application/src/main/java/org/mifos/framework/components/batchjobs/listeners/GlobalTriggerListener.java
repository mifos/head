package org.mifos.framework.components.batchjobs.listeners;

import org.mifos.framework.components.batchjobs.BatchJobTriggerListener;
import org.mifos.framework.components.batchjobs.SchedulerConstants;

public class GlobalTriggerListener extends BatchJobTriggerListener {

    private final String name = SchedulerConstants.GLOBAL_TRIGGER_LISTENER_NAME;

    @Override
    public String getName() {
        return name;
    }

}

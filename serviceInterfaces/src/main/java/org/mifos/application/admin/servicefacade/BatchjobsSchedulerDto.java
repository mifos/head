package org.mifos.application.admin.servicefacade;

public class BatchjobsSchedulerDto {
    private final boolean status;

    public BatchjobsSchedulerDto(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return this.status;
    }

}

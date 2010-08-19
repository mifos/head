package org.mifos.application.admin.servicefacade;

public class BatchjobsDto {
    private final String name;

    public BatchjobsDto(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}

package org.mifos.dto.screen;

public class PaymentTypeDto {

    private final Short id;
    private final String name;

    public PaymentTypeDto(Short id, String name) {
        this.id = id;
        this.name = name;
    }

    public Short getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}

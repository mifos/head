package org.mifos.dto.screen;

public class PaymentTypeDto {

    private final Short id;
    private final String name;
    private Short acceptedPaymentTypeId;

    public PaymentTypeDto(Short id, String name, Short acceptedPaymentTypeId) {
        this.id = id;
        this.name = name;
        this.acceptedPaymentTypeId = acceptedPaymentTypeId;
    }

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

    public Short getAcceptedPaymentTypeId() {
        return this.acceptedPaymentTypeId;
    }
}

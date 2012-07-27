package org.mifos.application.servicefacade;

import org.mifos.dto.domain.CreateAccountPenaltyDto;


public class CreationAccountPenaltyDto {

    private Integer penaltyId;
    private String amount;
    
    public CreationAccountPenaltyDto(Integer penaltyId, String amount) {
        this.penaltyId = penaltyId;
        this.amount = amount;
    }
    public Integer getPenaltyId() {
        return penaltyId;
    }
    public void setPenaltyId(Integer penaltyId) {
        this.penaltyId = penaltyId;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public CreateAccountPenaltyDto toDto() {
        return new CreateAccountPenaltyDto(getPenaltyId(), getAmount());
    }
}

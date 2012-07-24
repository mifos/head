package org.mifos.application.servicefacade;

import org.mifos.dto.domain.CreateAccountFeeDto;

public class CreationFeeDto {
    private Integer feeId;
    private String amount;
   
    public Integer getFeeId() {
        return feeId;
    }
    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public CreateAccountFeeDto toDto() {
        return new CreateAccountFeeDto(getFeeId(), getAmount());
    }
}

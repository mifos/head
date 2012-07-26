package org.mifos.application.servicefacade;

import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.springframework.beans.factory.annotation.Autowired;

public class CreationFeeDto {
    
    @Autowired
    private FeeDao feeDao;
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

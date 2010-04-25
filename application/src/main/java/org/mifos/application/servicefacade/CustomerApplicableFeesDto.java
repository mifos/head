package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.security.util.UserContext;

public class CustomerApplicableFeesDto {

    private final List<FeeDto> defaultFees;
    private final List<FeeDto> additionalFees;

    public static CustomerApplicableFeesDto toDto(List<FeeBO> fees, UserContext userContext) {

        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        List<FeeDto> defaultFees = new ArrayList<FeeDto>();
        for (FeeBO fee : fees) {
            if (fee.isCustomerDefaultFee()) {
                defaultFees.add(new FeeDto(userContext, fee));
            } else {
                additionalFees.add(new FeeDto(userContext, fee));
            }
        }

        return new CustomerApplicableFeesDto(defaultFees, additionalFees);
    }

    public CustomerApplicableFeesDto(List<FeeDto> defaultFees, List<FeeDto> additionalFees) {
        this.defaultFees = defaultFees;
        this.additionalFees = additionalFees;
    }

    public List<FeeDto> getDefaultFees() {
        return this.defaultFees;
    }

    public List<FeeDto> getAdditionalFees() {
        return this.additionalFees;
    }

    public static CustomerApplicableFeesDto empty() {
        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        List<FeeDto> defaultFees = new ArrayList<FeeDto>();
        
        return new CustomerApplicableFeesDto(defaultFees, additionalFees);
    }
}

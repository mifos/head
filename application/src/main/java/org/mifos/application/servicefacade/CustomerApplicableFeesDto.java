package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.security.util.UserContext;

public class CustomerApplicableFeesDto {

    private final List<FeeView> defaultFees;
    private final List<FeeView> additionalFees;

    public static CustomerApplicableFeesDto toDto(List<FeeBO> fees, UserContext userContext) {

        List<FeeView> additionalFees = new ArrayList<FeeView>();
        List<FeeView> defaultFees = new ArrayList<FeeView>();
        for (FeeBO fee : fees) {
            if (fee.isCustomerDefaultFee()) {
                defaultFees.add(new FeeView(userContext, fee));
            } else {
                additionalFees.add(new FeeView(userContext, fee));
            }
        }

        return new CustomerApplicableFeesDto(defaultFees, additionalFees);
    }

    public CustomerApplicableFeesDto(List<FeeView> defaultFees, List<FeeView> additionalFees) {
        this.defaultFees = defaultFees;
        this.additionalFees = additionalFees;
    }

    public List<FeeView> getDefaultFees() {
        return this.defaultFees;
    }

    public List<FeeView> getAdditionalFees() {
        return this.additionalFees;
    }

    public static CustomerApplicableFeesDto empty() {
        List<FeeView> additionalFees = new ArrayList<FeeView>();
        List<FeeView> defaultFees = new ArrayList<FeeView>();
        
        return new CustomerApplicableFeesDto(defaultFees, additionalFees);
    }
}

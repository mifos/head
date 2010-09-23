package org.mifos.accounts.productdefinition.business;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class VariableInstallmentDetailsBO extends AbstractEntity {

    private Short id;

    private Integer minGapInDays;

    private Integer maxGapInDays;

    private Money minInstallmentAmount;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Integer getMinGapInDays() {
        return minGapInDays;
    }

    public void setMinGapInDays(Integer minGapInDays) {
        this.minGapInDays = minGapInDays;
    }

    public Integer getMaxGapInDays() {
        return maxGapInDays;
    }

    public void setMaxGapInDays(Integer maxGapInDays) {
        this.maxGapInDays = maxGapInDays;
    }

    public Money getMinInstallmentAmount() {
        return minInstallmentAmount;
    }

    public void setMinInstallmentAmount(Money minInstallmentAmount) {
        this.minInstallmentAmount = minInstallmentAmount;
    }
}

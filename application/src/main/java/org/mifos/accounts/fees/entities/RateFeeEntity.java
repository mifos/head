package org.mifos.accounts.fees.entities;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.customers.office.business.OfficeBO;

import javax.persistence.*;


@Entity
@Table(name = "FEES")
@DiscriminatorValue("RATE")
public class RateFeeEntity extends FeeEntity {

    @Column(name = "RATE")
    private Double rate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="FORMULA_ID")
//    @Column(name="FORMULA_ID",insertable=true, updatable=false)
    private FeeFormulaEntity feeFormula;

    public RateFeeEntity(String feeName, CategoryTypeEntity feeCategoryType, GLCodeEntity glCode, Double rate,
            FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee, OfficeBO office) throws FeeException {
        super(feeName, feeCategoryType, glCode, isCustomerDefaultFee, office);
        this.rate = rate;
        this.feeFormula = feeFormula;
    }

    protected RateFeeEntity() {
        super();
        this.feeFormula = null;
    }

    public Double getRate() {
        return this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public FeeFormulaEntity getFeeFormula() {
        return this.feeFormula;
    }

    public void setFeeFormula(FeeFormulaEntity feeFormula) {
        this.feeFormula = feeFormula;
    }

    public FeeChangeType calculateChangeType(Double newRate, FeeStatus newStatus) {
        if (!this.rate.equals(newRate)) {
            if (!getFeeStatus().getId().equals(newStatus.getValue())) {
                return FeeChangeType.AMOUNT_AND_STATUS_UPDATED;
            }
            return FeeChangeType.AMOUNT_UPDATED;
        } else if (!getFeeStatus().getId().equals(newStatus.getValue())) {
            return FeeChangeType.STATUS_UPDATED;
        } else {
            return FeeChangeType.NOT_UPDATED;
        }
    }

}

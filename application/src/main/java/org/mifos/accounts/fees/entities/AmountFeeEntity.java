package org.mifos.accounts.fees.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.util.helpers.Money;

@Entity
@Table(name = "FEES")
@DiscriminatorValue("AMOUNT")
@TypeDef(name = "feeAmount", typeClass = org.mifos.framework.util.helpers.MoneyCompositeUserType.class)
public class AmountFeeEntity extends FeeEntity {

    @Basic(optional = false)
    @Type(type = "feeAmount")
    @Columns(columns = { @Column(name = "FEE_AMOUNT_CURRENCY_ID"), @Column(name = "FEE_AMOUNT") })
    protected Money feeAmount;

    public AmountFeeEntity(String feeName, CategoryTypeEntity feeCategoryType, GLCodeEntity glCode, Money feeMoney,
            boolean isCustomerDefaultFee, OfficeBO office) throws FeeException {
        super(feeName, feeCategoryType, glCode, isCustomerDefaultFee, office);
        this.feeAmount = feeMoney;
    }

    public Money getFeeAmount() {
        return this.feeAmount;
    }

    public void setFeeAmount(Money feeAmount) {
        this.feeAmount = feeAmount;
    }

}

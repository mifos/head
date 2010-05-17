package org.mifos.accounts.fees.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifos.accounts.fees.util.helpers.FeeLevel;
import org.mifos.framework.business.AbstractEntity;

@Entity
@Table(name = "FEELEVEL")
public class FeeLevelEntity  extends AbstractEntity {

    @Id
    @GeneratedValue
    @Column(name = "FEELEVEL_ID", nullable = false)
    private Short feeLevelId;

    @Column(name = "LEVEL_ID")
    private Short levelId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FEE_ID")
    private FeeEntity fee;

    public FeeLevelEntity(FeeEntity fee, FeeLevel feeLevel) {
        this.fee = fee;
        levelId = feeLevel.getValue();
    }

    protected FeeLevelEntity() {
        fee = null;
    }

    protected Short getFeeLevelId() {
        return feeLevelId;
    }

    public Short getLevelId() {
        return levelId;
    }

    public FeeEntity getFee() {
        return fee;
    }

    protected void setFeeLevelId(Short feeLevelId) {
        this.feeLevelId = feeLevelId;
    }

    protected void setLevelId(Short levelId) {
        this.levelId = levelId;
    }

    protected void setFee(FeeEntity fee) {
        this.fee = fee;
    }

}


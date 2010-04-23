package org.mifos.accounts.fees.servicefacade;

import java.io.Serializable;

public class FeeDto implements Serializable {

    private String id;

    private String name;

    private String categoryType;

    private FeeStatusDto feeStatus;

    private boolean active;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setFeeStatus(FeeStatusDto feeStatus) {
        this.feeStatus = feeStatus;
    }

    public FeeStatusDto getFeeStatus() {
        return feeStatus;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}

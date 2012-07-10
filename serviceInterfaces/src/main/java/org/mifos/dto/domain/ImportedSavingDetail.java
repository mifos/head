package org.mifos.dto.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class ImportedSavingDetail implements Serializable {

	private static final long serialVersionUID = -6458510092009432439L;

	private String accountNumber;
    private Integer customerId;
    private Short prdOfferingId;
    private Short status;
    private Short flag;
    private BigDecimal savingsAmount;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ImportedSavingDetail(String accountNumber, Integer customerId, Short prdOfferingId, Short status,
            Short flag, BigDecimal savingsAmount) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.prdOfferingId = prdOfferingId;
        this.status = status;
        this.flag = flag;
        this.savingsAmount = savingsAmount;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public Short getPrdOfferingId() {
        return prdOfferingId;
    }
    
    public void setPrdOfferingId(Short prdOfferingId) {
        this.prdOfferingId = prdOfferingId;
    }
    
    public Short getStatus() {
        return status;
    }
    
    public void setStatus(Short status) {
        this.status = status;
    }
    
    public Short getFlag() {
        return flag;
    }
    
    public void setFlag(Short flag) {
        this.flag = flag;
    }
    
    public BigDecimal getSavingsAmount() {
        return savingsAmount;
    }
    
    public void setSavingsAmount(BigDecimal savingsAmount) {
        this.savingsAmount = savingsAmount;
    }
}

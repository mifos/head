package org.mifos.dto.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class ImportedSavingDetail implements Serializable {

	private static final long serialVersionUID = -6458510092009432439L;

	private String accountNumber;
    private String customerId;
    private String prdOfferingId;
    private Short status;
    private Short flag;
    private BigDecimal savingsAmount;
    private BigDecimal savingsBalance;
    private LocalDate date;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ImportedSavingDetail(String accountNumber, String customerId, String prdOfferingId, Short status,
            Short flag, BigDecimal savingsAmount, BigDecimal savingsBalance, LocalDate date) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.prdOfferingId = prdOfferingId;
        this.status = status;
        this.flag = flag;
        this.savingsAmount = savingsAmount;
        this.savingsBalance = savingsBalance;
        this.date = date;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getPrdOfferingId() {
        return prdOfferingId;
    }
    
    public void setPrdOfferingId(String prdOfferingId) {
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

    public BigDecimal getSavingsBalance() {
        return savingsBalance;
    }

    public void setSavingsBalance(BigDecimal savingsBalance) {
        this.savingsBalance = savingsBalance;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
}

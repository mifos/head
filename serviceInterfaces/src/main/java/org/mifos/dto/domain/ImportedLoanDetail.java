package org.mifos.dto.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * Contains details from parsed loan accounts data
 * @author lgadomski
 */
public class ImportedLoanDetail implements Serializable{

    private static final long serialVersionUID = -4458590797231567690L;

    private String accountNumber;
    private Integer customerId;
    private Short prdOfferingId;
    private Short status;
    private Short flag;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
    private Date disbursalDate;
    private Integer gracePeriod;
    private Integer sourceOfFundId;
    private Integer loanPurposeId;
    private Integer collateralTypeId;
    private String collateralNotes;
    private String externalId;
        

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ImportedLoanDetail(String accountNumber, Integer customerId,
            Short prdOfferingId, Short status, Short flag,
            BigDecimal loanAmount, BigDecimal interestRate,
            Integer numberOfInstallments, Date disbursalDate,
            Integer gracePeriod, Integer sourceOfFundId, Integer loanPurposeId, 
            Integer collateralTypeId, String collateralNotes, String externalId) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.prdOfferingId = prdOfferingId;
        this.status = status;
        this.flag = flag;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.numberOfInstallments = numberOfInstallments;
        this.disbursalDate = (Date)disbursalDate.clone();
        this.gracePeriod = gracePeriod;
        this.sourceOfFundId=sourceOfFundId;
        this.loanPurposeId=loanPurposeId;
        this.collateralTypeId=collateralTypeId;
        this.collateralNotes=collateralNotes;
        this.externalId=externalId;
    }
    public Integer getSourceOfFundId() {
        return sourceOfFundId;
    }
    public void setSourceOfFundId(Integer sourceOfFundId) {
        this.sourceOfFundId = sourceOfFundId;
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
    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }
    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }
    public Date getDisbursalDate() {
        return (Date)disbursalDate.clone();
    }
    public void setDisbursalDate(Date disbursalDate) {
        this.disbursalDate = (Date)disbursalDate.clone();
    }
    public Integer getGracePeriod() {
        return gracePeriod==null ? Integer.valueOf(0)  : gracePeriod;
    }
    public void setGracePeriod(Integer gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    public Integer getLoanPurposeId() {
        return loanPurposeId;
    }
    public void setLoanPurposeId(Integer loanPurposeId) {
        this.loanPurposeId = loanPurposeId;
    }
    public Integer getCollateralTypeId() {
        return collateralTypeId;
    }
    public void setCollateralTypeId(Integer collateralTypeId) {
        this.collateralTypeId = collateralTypeId;
    }
    public String getCollateralNotes() {
        return collateralNotes;
    }
    public void setCollateralNotes(String collateralNotes) {
        this.collateralNotes = collateralNotes;
    }
    public String getExternalId() {
        return externalId;
    }
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}

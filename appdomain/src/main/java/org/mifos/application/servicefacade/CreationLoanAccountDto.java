package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;

public class CreationLoanAccountDto implements RecurringSchedule {

    private Integer customerId;
    private Integer productId;
    private Integer accountState;
    private Double loanAmount;
    private Double interestRate;
    private LocalDate disbursementDate;
    private Short disbursalPaymentTypeId;
    private Integer numberOfInstallments;
    private Integer graceDuration;
    private Integer sourceOfFundId;
    private Integer loanPurposeId;
    private Integer collateralTypeId;
    private String collateralNotes;
    private String externalId;
    private Boolean lsim;
    private RecurringSchedule recurringSchedule;
    private List<CreationFeeDto> accountFees;
    private List<CreationAccountPenaltyDto> accountPenalties;
    private Integer minNumOfInstallments;
    private Integer maxNumOfInstallments;
    private Double minAllowedLoanAmount;
    private Double maxAllowedLoanAmount;
    private String predefinedAccountNumber;
    private Short flagId;
    private Boolean glim;
    private List<CreationGLIMAccountsDto> glimAccounts;

    public List<CreationGLIMAccountsDto> getGlimAccounts() {
        return glimAccounts;
    }

    public void setGlimAccounts(List<CreationGLIMAccountsDto> glimAccounts) {
        this.glimAccounts = glimAccounts;
    }
    
    public List<GroupMemberAccountDto> glimsAsGroupMemberAccountDto(List<CreationGLIMAccountsDto> glimAccounts) {
        List<GroupMemberAccountDto> glims = new ArrayList<GroupMemberAccountDto>();
        for (CreationGLIMAccountsDto g : glimAccounts) {
            glims.add(g.toDto());
        }
        return glims;
    }

    public Boolean getGlim() {
        return glim;
    }

    public void setGlim(Boolean glim) {
        this.glim = glim;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getAccountState() {
        return accountState;
    }

    public void setAccountState(Integer accountState) {
        this.accountState = accountState;
    }

    public BigDecimal getLoanAmount() {
        return new BigDecimal(loanAmount);
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount.doubleValue();
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(LocalDate disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Short getDisbursalPaymentTypeId() {
        return disbursalPaymentTypeId;
    }

    public void setDisbursalPaymentTypeId(Short disbursalPaymentTypeId) {
        this.disbursalPaymentTypeId = disbursalPaymentTypeId;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public Integer getGraceDuration() {
        return graceDuration;
    }

    public void setGraceDuration(Integer graceDuration) {
        this.graceDuration = graceDuration;
    }

    public Integer getSourceOfFundId() {
        return sourceOfFundId;
    }

    public void setSourceOfFundId(Integer sourceOfFundId) {
        this.sourceOfFundId = sourceOfFundId;
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

    public Boolean getLsim() {
        return lsim;
    }

    public void setLsim(Boolean lsim) {
        this.lsim = lsim;
    }

    public RecurringSchedule getRecurringSchedule() {
        return recurringSchedule;
    }

    public void setRecurringSchedule(RecurringSchedule recurringSchedule) {
        this.recurringSchedule = recurringSchedule;
    }

    public List<CreationFeeDto> getAccountFees() {
        return accountFees;
    }

    public void setAccountFees(List<CreationFeeDto> accountFees) {
        this.accountFees = accountFees;
    }

    public List<CreationAccountPenaltyDto> getAccountPenalties() {
        return accountPenalties;
    }

    public void setAccountPenalties(List<CreationAccountPenaltyDto> accountPenalties) {
        this.accountPenalties = accountPenalties;
    }

    public Integer getMinNumOfInstallments() {
        return minNumOfInstallments;
    }

    public void setMinNumOfInstallments(Integer minNumOfInstallments) {
        this.minNumOfInstallments = minNumOfInstallments;
    }

    public Integer getMaxNumOfInstallments() {
        return maxNumOfInstallments;
    }

    public void setMaxNumOfInstallments(Integer maxNumOfInstallments) {
        this.maxNumOfInstallments = maxNumOfInstallments;
    }

    public BigDecimal getMinAllowedLoanAmount() {
        return new BigDecimal(minAllowedLoanAmount);
    }

    public void setMinAllowedLoanAmount(BigDecimal minAllowedLoanAmount) {
        this.minAllowedLoanAmount = minAllowedLoanAmount.doubleValue();
    }

    public BigDecimal getMaxAllowedLoanAmount() {
        return new BigDecimal(maxAllowedLoanAmount);
    }

    public void setMaxAllowedLoanAmount(BigDecimal maxAllowedLoanAmount) {
        this.maxAllowedLoanAmount = maxAllowedLoanAmount.doubleValue();
    }

    public String getPredefinedAccountNumber() {
        return predefinedAccountNumber;
    }

    public void setPredefinedAccountNumber(String predefinedAccountNumber) {
        this.predefinedAccountNumber = predefinedAccountNumber;
    }

    public Short getFlagId() {
        return flagId;
    }

    public void setFlagId(Short flagId) {
        this.flagId = flagId;
    }
    
    public List<CreateAccountFeeDto> feeAsAccountFeeDto(List<CreationFeeDto> accountFees) {
        List<CreateAccountFeeDto> feeDto = new ArrayList<CreateAccountFeeDto>();
        for (CreationFeeDto f : accountFees) {
            feeDto.add(f.toDto());
        }
        return feeDto;
    }
    
    public List<CreateAccountPenaltyDto> penaltiesAsAccountPenaltiesDto(List<CreationAccountPenaltyDto> accountPenalties) {
        List<CreateAccountPenaltyDto> penaltyDto = new ArrayList<CreateAccountPenaltyDto>();
        for (CreationAccountPenaltyDto p : accountPenalties) {
            penaltyDto.add(p.toDto());
        }
        return penaltyDto;
    }

    @Override
    public boolean isWeekly() {
        return this.recurringSchedule.isWeekly();
    }

    @Override
    public boolean isMonthly() {
        return this.recurringSchedule.isMonthly();
    }

    @Override
    public boolean isMonthlyOnDayOfMonth() {
        return this.recurringSchedule.isMonthlyOnDayOfMonth();
    }

    @Override
    public boolean isMonthlyOnWeekAndDayOfMonth() {
        return this.recurringSchedule.isMonthlyOnWeekAndDayOfMonth();
    }

    @Override
    public Integer getEvery() {
        return this.recurringSchedule.getEvery();
    }

    @Override
    public Integer getDay() {
        return this.recurringSchedule.getDay();
    }

    @Override
    public Integer getWeek() {
        return this.recurringSchedule.getWeek();
    }

}

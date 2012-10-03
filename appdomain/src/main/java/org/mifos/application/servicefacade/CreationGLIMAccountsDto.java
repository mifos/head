package org.mifos.application.servicefacade;

import java.math.BigDecimal;

import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;

public class CreationGLIMAccountsDto {

    private String globalId;
    private Double loanAmount;
    private Integer loanPurposeId;
    
    public String getGlobalId() {
        return globalId;
    }
    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }
    public BigDecimal getLoanAmount() {
        return new BigDecimal(loanAmount);
    }
    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount.doubleValue();
    }
    public Integer getLoanPurposeId() {
        return loanPurposeId;
    }
    public void setLoanPurposeId(Integer loanPurposeId) {
        this.loanPurposeId = loanPurposeId;
    }
    
    public GroupMemberAccountDto toDto() {
        return new GroupMemberAccountDto(getGlobalId(), getLoanAmount(), getLoanPurposeId());
    }
    
}

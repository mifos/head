package org.mifos.clientportfolio.loan.ui;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.dto.screen.LoanInformationDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanAccountStatusFormBean implements Serializable {

    @NotNull
    private Integer status;
    
    @NotEmpty
    private String note;

    private LoanApplicationStateDto loanApplicationState;
    
    private LoanInformationDto loanInformation;
    
    // custom validation
    private Integer cancelReason;
    
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Integer getCancelReason() {
        return cancelReason;
    }
    
    public boolean isLoanApproved() {
        return this.status == this.loanApplicationState.getApprovedApplicationId();
    }

    public void setLoanApplicationState(LoanApplicationStateDto loanApplicationState) {
        this.loanApplicationState = loanApplicationState;
    }

    public LoanApplicationStateDto getLoanApplicationState() {
        return loanApplicationState;
    }

    public void setLoanInformation(LoanInformationDto loanInformation) {
        this.loanInformation = loanInformation;
    }
    
    public LoanInformationDto getLoanInformation() {
        return loanInformation;
    }
}

package org.mifos.ui.loan.controller;


public class DeleteLoanProductDto {

    private boolean delete;
    private Integer loanProductId;
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public DeleteLoanProductDto() {
        // for dozer
    }
    
    public DeleteLoanProductDto(Integer loanProductId, boolean delete) {
        this.loanProductId = loanProductId;
        this.delete = delete;
    }
    
    public Integer getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Integer loanProductId) {
        this.loanProductId = loanProductId;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

}

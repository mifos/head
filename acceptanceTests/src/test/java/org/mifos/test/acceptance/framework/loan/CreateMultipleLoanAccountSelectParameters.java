package org.mifos.test.acceptance.framework.loan;

public class CreateMultipleLoanAccountSelectParameters {
    public String getBranch() {
        return this.branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getLoanOfficer() {
        return this.loanOfficer;
    }
    public void setLoanOfficer(String loanOfficer) {
        this.loanOfficer = loanOfficer;
    }
    public String getCenter() {
        return this.center;
    }
    public void setCenter(String center) {
        this.center = center;
    }
    public String getLoanProduct() {
        return this.loanProduct;
    }
    public void setLoanProduct(String loanProduct) {
        this.loanProduct = loanProduct;
    }
    
    private String branch;
    private String loanOfficer;
    private String center;
    private String loanProduct;
}

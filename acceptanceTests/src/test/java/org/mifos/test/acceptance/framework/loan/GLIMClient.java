package org.mifos.test.acceptance.framework.loan;

public class GLIMClient {
    private int clientNumber;
    private String clientName;
    private String loanAmount;
    private String loanPurpose;

    public GLIMClient(int clientNumber, String clientName, String loanAmount, String loanPurpose) {
        super();
        this.clientNumber = clientNumber;
        this.clientName = clientName;
        this.loanAmount = loanAmount;
        this.loanPurpose = loanPurpose;
    }

    public String getLoanPurpose() {
        return this.loanPurpose;
    }

    public void setLoanPurpose(String loanPurpose) {
        this.loanPurpose = loanPurpose;
    }

    public int getClientNumber() {
        return this.clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getLoanAmount() {
        return this.loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }
}

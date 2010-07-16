package org.mifos.ui.core.controller;

public class AcceptedPaymentTypesBean {

    private String feesRight="";
    private String disbursementsRight="";
    private String repaymentsRight="";
    private String withdrawalsRight="";
    private String depositsRight="";

    public String getFeesRight() {
        return this.feesRight;
    }

    public void setFeesRight(String feesRight) {
        this.feesRight = feesRight;
    }

    public String getDisbursementsRight() {
        return this.disbursementsRight;
    }

    public void setDisbursementsRight(String disbursementsRight) {
        this.disbursementsRight = disbursementsRight;
    }

    public String getRepaymentsRight() {
        return this.repaymentsRight;
    }

    public void setRepaymentsRight(String repaymentsRight) {
        this.repaymentsRight = repaymentsRight;
    }

    public String getWithdrawalsRight() {
        return this.withdrawalsRight;
    }

    public void setWithdrawalsRight(String withdrawalsRight) {
        this.withdrawalsRight = withdrawalsRight;
    }

    public String getDepositsRight() {
        return this.depositsRight;
    }

    public void setDepositsRight(String depositsRight) {
        this.depositsRight = depositsRight;
    }
}

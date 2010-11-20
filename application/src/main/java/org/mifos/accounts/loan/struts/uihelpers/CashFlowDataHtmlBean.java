package org.mifos.accounts.loan.struts.uihelpers;

import java.util.Date;

public class CashFlowDataHtmlBean {
    private String month = new String();
    private String year = new String();
    private String cumulativeCashflow = new String();
    private String diffCumulativeCashflowAndInstallment = new String();
    private String diffCumulativeCashflowAndInstallmentPercent = new String();
    private String notes = new String();
    private Date monthYear;

    public String getMonth() {
        return this.month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getCumulativeCashFlow() {
        return this.cumulativeCashflow;
    }
    public void setCumulativeCashFlow(String cumulativeCashflow) {
        this.cumulativeCashflow = cumulativeCashflow;
    }
    public String getDiffCumulativeCashflowAndInstallment() {
        return this.diffCumulativeCashflowAndInstallment;
    }
    public void setDiffCumulativeCashflowAndInstallment(String diffCumulativeCashflowAndInstallment) {
        this.diffCumulativeCashflowAndInstallment = diffCumulativeCashflowAndInstallment;
    }
    public String getDiffCumulativeCashflowAndInstallmentPercent() {
        return this.diffCumulativeCashflowAndInstallmentPercent;
    }
    public void setDiffCumulativeCashflowAndInstallmentPercent(String diffCumulativeCashflowAndInstallmentPercent) {
        this.diffCumulativeCashflowAndInstallmentPercent = diffCumulativeCashflowAndInstallmentPercent;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getYear() {
        return year;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getNotes() {
        return notes;
    }
    public void setMonthYear(Date monthYear) {
        this.monthYear = monthYear;
    }
    public Date getMonthYear() {
        return monthYear;
    }

}

package org.mifos.accounts.fees.servicefacade;

public class FeeFrequencyDto {

    private String type;

    private String payment;

    private String recurAfterPeriod;

    private boolean weekly;

    private boolean monthly;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setRecurAfterPeriod(String recurAfterPeriod) {
        this.recurAfterPeriod = recurAfterPeriod;
    }

    public String getRecurAfterPeriod() {
        return recurAfterPeriod;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public boolean isMonthly() {
        return monthly;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPayment() {
        return payment;
    }


}

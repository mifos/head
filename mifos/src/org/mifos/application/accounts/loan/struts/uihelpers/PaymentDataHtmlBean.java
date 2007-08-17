package org.mifos.application.accounts.loan.struts.uihelpers;

import org.mifos.application.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

import java.util.Date;
import java.util.Locale;

public class PaymentDataHtmlBean implements PaymentDataTemplate {
    private String amount;
    private String date;
    private Locale locale;
    private Short paymentTypeId;
    private PersonnelBO personnel;

    public PaymentDataHtmlBean(Locale locale, PersonnelBO personnel) {
        this.locale = locale;
        this.personnel = personnel;
        this.date = DateUtils.getUserLocaleDate(locale,
                new java.sql.Date(new java.util.Date().getTime()).toString());
    }

    public Money getTotalAmount() {
        if (getAmount() == null || getAmount().equals("")) {
            return null;
        }
        else {
            return new Money(amount);
        }
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

    public Short getPaymentTypeId() {
        return this.paymentTypeId;
    }

    public Date getTransactionDate() {
        return DateUtils.getLocaleDate(locale, getAmount());
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPaymentTypeId(Short paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }
}

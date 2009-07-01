/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.accounts.struts.actionforms;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.FilePaths;

public class AccountApplyPaymentActionForm extends BaseActionForm {
    private String input;

    private String transactionDateDD;

    private String transactionDateMM;

    private String transactionDateYY;

    private Money amount;

    private String receiptId;

    private String receiptDateDD;

    private String receiptDateMM;

    private String receiptDateYY;

    private String paymentTypeId;

    private String globalAccountNum;

    private String accountId;

    private String prdOfferingName;

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public void setPrdOfferingName(String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getInput() {
        return input;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String methodCalled = request.getParameter(MethodNameConstants.METHOD);
        ActionErrors errors = null;
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE,
                getUserLocale(request));

        if (methodCalled != null && methodCalled.equals("preview")) {
            errors = new ActionErrors();
            ActionErrors errors2 = validateDate(getTransactionDate(), resources.getString("accounts.date_of_trxn"),
                    request);
            if (null != errors2 && !errors2.isEmpty())
                errors.add(errors2);
            if (this.paymentTypeId == null || this.paymentTypeId.equals("")) {
                errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                        resources.getString("accounts.mode_of_payment")));
            }
            if (getReceiptDate() != null && !getReceiptDate().equals("")) {
                errors2 = validateDate(getReceiptDate(), resources.getString("accounts.receiptdate"), request);
                if (null != errors2 && !errors2.isEmpty())
                    errors.add(errors2);
            }
            String accountType = request.getParameter("accountType");
            if (accountType != null && Short.valueOf(accountType).equals(AccountTypes.LOAN_ACCOUNT.getValue())) {
                if (amount == null || amount.getAmountDoubleValue() <= 0.0) {
                    errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                            resources.getString("accounts.amt")));
                }
            }
        }
        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", methodCalled);
        }
        return errors;
    }

    protected ActionErrors validateDate(String date, String fieldName, HttpServletRequest request) {
        ActionErrors errors = null;
        java.sql.Date sqlDate = null;
        if (date != null && !date.equals("")) {
            try {
                sqlDate = DateUtils.getDateAsSentFromBrowser(date);
                if (DateUtils.whichDirection(sqlDate) > 0) {
                    errors = new ActionErrors();
                    errors.add(AccountConstants.ERROR_FUTUREDATE, new ActionMessage(AccountConstants.ERROR_FUTUREDATE,
                            fieldName));
                }
            } catch (InvalidDateException e) {
                errors = new ActionErrors();
                errors.add(AccountConstants.ERROR_INVALIDDATE, new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
                        fieldName));
            }
        } else {
            errors = new ActionErrors();
            errors
                    .add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                            fieldName));
        }
        return errors;
    }

    protected Locale getUserLocale(HttpServletRequest request) {
        Locale locale = null;
        HttpSession session = request.getSession();
        if (session != null) {
            UserContext userContext = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);
            if (null != userContext) {
                locale = userContext.getCurrentLocale();

            }
        }
        return locale;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getReceiptDate() {
        if (StringUtils.isNullAndEmptySafe(receiptDateDD) && StringUtils.isNullAndEmptySafe(receiptDateMM)
                && StringUtils.isNullAndEmptySafe(receiptDateYY)) {

            return receiptDateDD + "/" + receiptDateMM + "/" + receiptDateYY;
        } else {
            return null;
        }
    }

    public void setReceiptDate(String receiptDate) {
        if (StringUtils.isNullOrEmpty(receiptDate)) {
            receiptDateDD = null;
            receiptDateMM = null;
            receiptDateYY = null;
        } else {
            Calendar cal = new GregorianCalendar();
            java.sql.Date date = DateUtils.getDateAsSentFromBrowser(receiptDate);
            cal.setTime(date);
            receiptDateDD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            receiptDateMM = Integer.toString(cal.get(Calendar.MONTH) + 1);
            receiptDateYY = Integer.toString(cal.get(Calendar.YEAR));
        }
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getTransactionDate() {
        if (StringUtils.isNullAndEmptySafe(transactionDateDD) && StringUtils.isNullAndEmptySafe(transactionDateMM)
                && StringUtils.isNullAndEmptySafe(transactionDateYY)) {
            String transactionDate = "";
            if (transactionDateDD.length() < 2)
                transactionDate = transactionDate + "0" + transactionDateDD;
            else
                transactionDate = transactionDate + transactionDateDD;
            if (transactionDateMM.length() < 2)
                transactionDate = transactionDate + "/" + "0" + transactionDateMM;
            else
                transactionDate = transactionDate + "/" + transactionDateMM;
            transactionDate = transactionDate + "/" + transactionDateYY;
            return transactionDate;
        } else {
            return null;
        }
    }

    public void setTransactionDate(String receiptDate) {
        if (StringUtils.isNullOrEmpty(receiptDate)) {
            transactionDateDD = null;
            transactionDateMM = null;
            transactionDateYY = null;
        } else {
            Calendar cal = new GregorianCalendar();
            java.sql.Date date = DateUtils.getDateAsSentFromBrowser(receiptDate);
            cal.setTime(date);
            transactionDateDD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            transactionDateMM = Integer.toString(cal.get(Calendar.MONTH) + 1);
            transactionDateYY = Integer.toString(cal.get(Calendar.YEAR));
        }
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    protected void clear() {
        this.amount = null;
        this.paymentTypeId = null;
        setReceiptDate(null);
        this.receiptId = null;
    }

    public String getReceiptDateDD() {
        return receiptDateDD;
    }

    public void setReceiptDateDD(String receiptDateDD) {
        this.receiptDateDD = receiptDateDD;
    }

    public String getReceiptDateMM() {
        return receiptDateMM;
    }

    public void setReceiptDateMM(String receiptDateMM) {
        this.receiptDateMM = receiptDateMM;
    }

    public String getReceiptDateYY() {
        return receiptDateYY;
    }

    public void setReceiptDateYY(String receiptDateYY) {
        this.receiptDateYY = receiptDateYY;
    }

    public String getTransactionDateDD() {
        return transactionDateDD;
    }

    public void setTransactionDateDD(String transactionDateDD) {
        this.transactionDateDD = transactionDateDD;
    }

    public String getTransactionDateMM() {
        return transactionDateMM;
    }

    public void setTransactionDateMM(String transactionDateMM) {
        this.transactionDateMM = transactionDateMM;
    }

    public String getTransactionDateYY() {
        return transactionDateYY;
    }

    public void setTransactionDateYY(String transactionDateYY) {
        this.transactionDateYY = transactionDateYY;
    }

}

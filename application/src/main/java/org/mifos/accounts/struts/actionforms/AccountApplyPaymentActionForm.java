/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounts.struts.actionforms;

import static org.mifos.framework.util.helpers.DateUtils.dateFallsBeforeDate;
import static org.mifos.framework.util.helpers.DateUtils.getDateAsSentFromBrowser;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.joda.time.LocalDate;
import org.mifos.accounts.servicefacade.AccountTypeDto;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.UserContext;

public class AccountApplyPaymentActionForm extends BaseActionForm {
    private String input;

    private String transactionDateDD;

    private String transactionDateMM;

    private String transactionDateYY;

    private String amount;

    private Short currencyId;

    private String receiptId;

    private String receiptDateDD;

    private String receiptDateMM;

    private String receiptDateYY;

    /*
     * Among other things, this field holds the PaymentTypes value for disbursements.
     */
    private String paymentTypeId;

    private String waiverInterest;

    private String globalAccountNum;

    private String accountId;

    private String prdOfferingName;

    private boolean amountCannotBeZero = true;

    private java.util.Date lastPaymentDate;

    private String accountForTransfer;

    private Short transferPaymentTypeId;

    public boolean amountCannotBeZero() {
        return this.amountCannotBeZero;
    }

    public void setAmountCannotBeZero(boolean amountCannotBeZero) {
        this.amountCannotBeZero = amountCannotBeZero;
    }

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public void setPrdOfferingName(String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInput() {
        return input;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String methodCalled = request.getParameter(MethodNameConstants.METHOD);
        ActionErrors errors = new ActionErrors();

        if (methodCalled != null && methodCalled.equals("preview")) {
            validateTransfer(errors);
            validateTransactionDate(errors);
            validatePaymentType(errors);
            validateReceiptDate(errors);
            String accountType = (String) request.getSession().getAttribute(Constants.ACCOUNT_TYPE);
            validateAccountType(errors, accountType);
            validateAmount(errors);
            validateModeOfPaymentSecurity(request, errors);
        }
        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", methodCalled);
        }
        return errors;
    }

    private void validateModeOfPaymentSecurity(HttpServletRequest request, ActionErrors errors){
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
        if(getPaymentTypeId().equals("4") && !ActivityMapper.getInstance().isModeOfPaymentSecurity(userContext)){
            errors.add(AccountConstants.LOAN_TRANSFER_PERMISSION, new ActionMessage(AccountConstants.LOAN_TRANSFER_PERMISSION,
                    getLocalizedMessage("accounts.mode_of_payment_permission")));
        }
    }
    
    private void validateTransfer(ActionErrors errors) {
        if (paymentTypeId.equals(String.valueOf(transferPaymentTypeId))
                && StringUtils.isBlank(accountForTransfer)) {
            errors.add(AccountConstants.NO_ACCOUNT_FOR_TRANSFER, new ActionMessage(AccountConstants.NO_ACCOUNT_FOR_TRANSFER));
        }
    }

    private void validateAccountType(ActionErrors errors, String accountType) {
        if (accountType != null && accountType.equals(AccountTypeDto.LOAN_ACCOUNT.name())) {
            if (getAmount() == null || getAmount().equals("")) {
                errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                        getLocalizedMessage("accounts.amt")));
            }
        }
    }

    private void validatePaymentType(ActionErrors errors) {
        if (StringUtils.isEmpty(getPaymentTypeId())) {
            errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                    getLocalizedMessage("accounts.mode_of_payment")));
        }
    }

    private void validateReceiptDate(ActionErrors errors) {
        if (getReceiptDate() != null && !getReceiptDate().equals("")) {
            ActionErrors validationErrors = validateDate(getReceiptDate(), getLocalizedMessage("accounts.receiptdate"));
            if (null != validationErrors && !validationErrors.isEmpty()) {
                errors.add(validationErrors);
            }
        }
    }

    private void validateTransactionDate(ActionErrors errors) {
        String fieldName = "accounts.date_of_trxn";
        ActionErrors validationErrors = validateDate(getTransactionDate(), getLocalizedMessage(fieldName));
        if (null != validationErrors && !validationErrors.isEmpty()) {
            errors.add(validationErrors);
        }
        if (null != getTransactionDate()){
            validationErrors = validatePaymentDate(getTransactionDate(), getLocalizedMessage(fieldName));
            if (validationErrors != null && !validationErrors.isEmpty()) {
                errors.add(validationErrors);
            }
        }
    }
    //exposed for testing
    public ActionErrors validatePaymentDate(String transactionDate, String fieldName) {
        ActionErrors errors = null;
        try {
            if (lastPaymentDate != null && dateFallsBeforeDate(getDateAsSentFromBrowser(transactionDate), lastPaymentDate)) {
                errors = new ActionErrors();
                errors.add(AccountConstants.ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT,
                        new ActionMessage(AccountConstants.ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT,
                                fieldName));
            }
        } catch (InvalidDateException ide) {
            errors = new ActionErrors(); //dont add a message, since it was already added in validateDate()
        }
        return errors;
    }

    protected ActionErrors validateDate(String date, String fieldName) {
        ActionErrors errors = null;
        java.sql.Date sqlDate = null;
        if (date != null && !date.equals("")) {
            try {
                sqlDate = getDateAsSentFromBrowser(date);
                if (DateUtils.whichDirection(sqlDate) > 0) {
                    errors = new ActionErrors();
                    errors.add(AccountConstants.ERROR_FUTUREDATE, new ActionMessage(AccountConstants.ERROR_FUTUREDATE,
                            fieldName));
                }
            } catch (InvalidDateException ide) {
                errors = new ActionErrors();
                errors.add(AccountConstants.ERROR_INVALIDDATE, new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
                        fieldName));
            }
        } else {
            errors = new ActionErrors();
            errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
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

    protected void validateAmount(ActionErrors errors) {
        MifosCurrency currency = null;
        if (getCurrencyId() != null && AccountingRules.isMultiCurrencyEnabled()) {
            currency = AccountingRules.getCurrencyByCurrencyId(getCurrencyId());
        }
        DoubleConversionResult conversionResult = validateAmount(getAmount(), currency , AccountConstants.ACCOUNT_AMOUNT, errors, "");
        if (amountCannotBeZero() && conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, AccountConstants.ACCOUNT_AMOUNT, AccountConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    getLocalizedMessage(AccountConstants.ACCOUNT_AMOUNT));
        }
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
        return compileDateString(receiptDateDD, receiptDateMM, receiptDateYY);
    }

    public void setReceiptDate(String receiptDate) throws InvalidDateException {
        if (StringUtils.isBlank(receiptDate)) {
            receiptDateDD = null;
            receiptDateMM = null;
            receiptDateYY = null;
        } else {
            Calendar cal = new GregorianCalendar();
            java.sql.Date date = getDateAsSentFromBrowser(receiptDate);
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
        return compileDateString(transactionDateDD, transactionDateMM, transactionDateYY);
    }

    public void setTransactionDate(String receiptDate) throws InvalidDateException {
        if (StringUtils.isBlank(receiptDate)) {
            transactionDateDD = null;
            transactionDateMM = null;
            transactionDateYY = null;
        } else {
            Calendar cal = new GregorianCalendar();
            java.sql.Date date = getDateAsSentFromBrowser(receiptDate);
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

    protected void clear() throws InvalidDateException {
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

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public String getWaiverInterest() {
        return waiverInterest;
    }

    public void setWaiverInterest(String waiverInterest) {
        this.waiverInterest = waiverInterest;
    }

    public LocalDate getReceiptDateAsLocalDate() throws InvalidDateException {
        Date receiptDateStr = getDateAsSentFromBrowser(getReceiptDate());
        return (receiptDateStr != null) ? new LocalDate(receiptDateStr.getTime()) : null;
    }

    public LocalDate getTrxnDateAsLocalDate() throws InvalidDateException {
        return new LocalDate(getTrxnDate().getTime());
    }

    public Date getTrxnDate() throws InvalidDateException {
        return getDateAsSentFromBrowser(getTransactionDate());
    }

    public void setLastPaymentDate(java.util.Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getAccountForTransfer() {
        return accountForTransfer;
    }

    public void setAccountForTransfer(String accountForTransfer) {
        this.accountForTransfer = accountForTransfer;
    }

    public Short getTransferPaymentTypeId() {
        return transferPaymentTypeId;
    }

    public void setTransferPaymentTypeId(Short transferPaymentTypeId) {
        this.transferPaymentTypeId = transferPaymentTypeId;
    }
}

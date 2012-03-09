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

import static org.mifos.framework.util.helpers.DateUtils.getDateAsSentFromBrowser;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.dto.domain.AdjustedPaymentDto;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;

/**
 * This class is the action form for Applying adjustments.
 */
public class ApplyAdjustmentActionForm extends BaseActionForm {

    private static final long serialVersionUID = -5747465818008310010L;

    private String input;

    private String adjustmentNote;

    private String globalAccountNum;

    private boolean adjustcheckbox;

    private Integer paymentId;
    
    private String amount;
    
    private String paymentType;
    
    private Short currencyId;
    
    private String transactionDateDD;

    private String transactionDateMM;

    private String transactionDateYY;
    
    private boolean adjustData;
    
    private Date previousPaymentDate;
    
    private Date nextPaymentDate;
    
    public Date getPreviousPaymentDate() {
        return previousPaymentDate;
    }

    public void setPreviousPaymentDate(Date previousPaymentDate) {
        this.previousPaymentDate = previousPaymentDate;
    }

    public Date getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(Date nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public boolean isAdjustData() {
        return adjustData;
    }

    public void setAdjustData(boolean adjustData) {
        this.adjustData = adjustData;
    }

    public Short getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAdjustmentNote() {
        return adjustmentNote;
    }

    public void setAdjustmentNote(String adjustmentNote) {
        this.adjustmentNote = adjustmentNote;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    public boolean getAdjustcheckbox() {
        return adjustcheckbox;
    }

    public void setAdjustcheckbox(boolean adjustcheckbox) {
        this.adjustcheckbox = adjustcheckbox;

    }
    
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    ResourceBundle getResourceBundle(Locale userLocale) {
        return ResourceBundle.getBundle(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE,
                userLocale);
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
    
    public String getTransactionDate() {
        if (StringUtils.isNotBlank(transactionDateDD) && StringUtils.isNotBlank(transactionDateMM)
                && StringUtils.isNotBlank(transactionDateYY)) {
            String transactionDate = "";
            if (transactionDateDD.length() < 2) {
                transactionDate = transactionDate + "0" + transactionDateDD;
            } else {
                transactionDate = transactionDate + transactionDateDD;
            }
            if (transactionDateMM.length() < 2) {
                transactionDate = transactionDate + "/" + "0" + transactionDateMM;
            } else {
                transactionDate = transactionDate + "/" + transactionDateMM;
            }
            transactionDate = transactionDate + "/" + transactionDateYY;
            return transactionDate;
        }
        return null;
    }
    
    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest request) {
        this.adjustcheckbox = false;

    }

    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY)) {
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        }
        ActionErrors actionErrors = new ActionErrors();
        String method = request.getParameter("method");
        if (null != method && method.equals("previewAdjustment")) {
/*            if (!adjustcheckbox) {
                request.setAttribute("method", "loadAdjustment");
                actionErrors.add("", new ActionMessage("errors.mandatorycheckbox"));
            }*/
            if (!adjustcheckbox) {
                Locale userLocale = getUserLocale(request);
                ResourceBundle resources = getResourceBundle(userLocale);
                validateAmount(actionErrors, userLocale);
                validatePaymentType(actionErrors, resources);
                validateTransactionDate(actionErrors, resources);
            }
            if (adjustmentNote == null || adjustmentNote.trim() == "") {
                request.setAttribute("method", "loadAdjustment");
                actionErrors.add("", new ActionMessage("errors.mandatorytextarea"));
            } else if (adjustmentNote.length() > 300) {
                request.setAttribute("method", "loadAdjustment");
                actionErrors.add("", new ActionMessage("errors.adjustmentNoteTooBig"));
            }
            if (!actionErrors.isEmpty()) {
                request.setAttribute("method", "loadAdjustment");
                return actionErrors;
            }
        }
        return actionErrors;
    }
    
    
    protected void validateAmount(ActionErrors errors, Locale locale) {
        MifosCurrency currency = null;
        if (getCurrencyId() != null && AccountingRules.isMultiCurrencyEnabled()) {
            currency = AccountingRules.getCurrencyByCurrencyId(getCurrencyId());
        }
        DoubleConversionResult conversionResult = validateAmount(getAmount(), currency , AccountConstants.ACCOUNT_AMOUNT, errors, locale,
                FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE, "");
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, AccountConstants.ACCOUNT_AMOUNT, AccountConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    lookupLocalizedPropertyValue(AccountConstants.ACCOUNT_AMOUNT, locale, FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE));
        }
    }
    
    private void validateTransactionDate(ActionErrors errors, ResourceBundle resources) {
        String fieldName = "accounts.date_of_trxn";
        ActionErrors validationErrors = validateDate(getTransactionDate(), resources.getString(fieldName));

        if (null != validationErrors && !validationErrors.isEmpty()) {
            errors.add(validationErrors);
        }
    }

    private void validatePaymentType(ActionErrors errors, ResourceBundle resources) {
        if (StringUtils.isEmpty(getPaymentType())) {
            errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                    resources.getString("accounts.mode_of_payment")));
        }
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
                } else if (previousPaymentDate != null && sqlDate.compareTo(previousPaymentDate) < 0) {
                    errors = new ActionErrors();
                    errors.add(AccountConstants.ERROR_ADJUSTMENT_PREVIOUS_DATE, new ActionMessage(AccountConstants.ERROR_ADJUSTMENT_PREVIOUS_DATE,
                            fieldName));
                } else if (nextPaymentDate != null && sqlDate.compareTo(nextPaymentDate) > 0) {
                    errors = new ActionErrors();
                    errors.add(AccountConstants.ERROR_ADJUSTMENT_NEXT_DATE, new ActionMessage(AccountConstants.ERROR_ADJUSTMENT_NEXT_DATE,
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
    
    public Date getTrxnDate() throws InvalidDateException {
        return getDateAsSentFromBrowser(getTransactionDate());
    }
    
    public void setTrxnDate(Date date) {
        if (date == null) {
            transactionDateDD = null;
            transactionDateMM = null;
            transactionDateYY = null;
        } else {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(date);
            transactionDateDD = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
            transactionDateMM = Integer.toString(cal.get(Calendar.MONTH) + 1);
            transactionDateYY = Integer.toString(cal.get(Calendar.YEAR));
        }
    }
    
    public AdjustedPaymentDto getPaymentData() throws InvalidDateException {
        AdjustedPaymentDto adjustedPaymentDto = null;
        if (adjustData) {
            adjustedPaymentDto = new AdjustedPaymentDto(amount, getTrxnDate(), Short.parseShort(paymentType));
        }
        return adjustedPaymentDto;
    }
}

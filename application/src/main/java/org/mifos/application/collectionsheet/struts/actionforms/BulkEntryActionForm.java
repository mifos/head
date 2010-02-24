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

package org.mifos.application.collectionsheet.struts.actionforms;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;

/**
 *
 */
public class BulkEntryActionForm extends BaseActionForm {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);
    private String customerId;
    private String loanOfficerId;
    private String paymentId;
    private String receiptId;
    private String receiptDateDD;
    private String receiptDateMM;
    private String receiptDateYY;
    private String transactionDateDD;
    private String transactionDateMM;
    private String transactionDateYY;
    private String officeId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReceiptDate() {
        if (StringUtils.isBlank(getReceiptDateDD()) || StringUtils.isBlank(getReceiptDateMM())
                || StringUtils.isBlank(getReceiptDateYY())) {
            return null;
        }
        return getReceiptDateDD() + "/" + getReceiptDateMM() + "/" + getReceiptDateYY();
    }

    public void setReceiptDate(String s) {
        if (StringUtils.isNotBlank(s)) {
            setReceiptDate(DateUtils.getDate(s));
        }
    }

    public void setReceiptDate(java.util.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // note that Calendar retrieves 0-based month, so increment month field
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        setReceiptDateDD(Integer.toString(day));
        setReceiptDateMM(Integer.toString(month));
        setReceiptDateYY(Integer.toString(year));
    }

    public void setReceiptDateDD(String receiptDateDD) {
        this.receiptDateDD = receiptDateDD;
    }

    public String getReceiptDateDD() {
        return receiptDateDD;
    }

    public void setReceiptDateMM(String receiptDateMM) {
        this.receiptDateMM = receiptDateMM;
    }

    public String getReceiptDateMM() {
        return receiptDateMM;
    }

    public void setReceiptDateYY(String receiptDateYY) {
        this.receiptDateYY = receiptDateYY;
    }

    public String getReceiptDateYY() {
        return receiptDateYY;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getTransactionDate() {
        if (StringUtils.isBlank(transactionDateDD) || StringUtils.isBlank(transactionDateMM)
                || StringUtils.isBlank(transactionDateYY)) {
            return null;
        }
        return transactionDateDD + "/" + transactionDateMM + "/" + transactionDateYY;
    }

    public void setTransactionDate(String s) {
        setTransactionDate(DateUtils.getDate(s));
    }

    public void setTransactionDate(java.util.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // note that Calendar retrieves 0-based month, so increment month field
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        setTransactionDateDD(Integer.toString(day));
        setTransactionDateMM(Integer.toString(month));
        setTransactionDateYY(Integer.toString(year));
    }

    public void setTransactionDateDD(String transactionDateDD) {
        this.transactionDateDD = transactionDateDD;
    }

    public String getTransactionDateDD() {
        return transactionDateDD;
    }

    public void setTransactionDateMM(String transactionDateMM) {
        this.transactionDateMM = transactionDateMM;
    }

    public String getTransactionDateMM() {
        return transactionDateMM;
    }

    public void setTransactionDateYY(String transactionDateYY) {
        this.transactionDateYY = transactionDateYY;
    }

    public String getTransactionDateYY() {
        return transactionDateYY;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("BulkEntryActionForm.validate");
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        ActionErrors errors = new ActionErrors();

        if (request.getParameter(CollectionSheetEntryConstants.METHOD).equalsIgnoreCase(
                CollectionSheetEntryConstants.GETMETHOD)) {
            java.sql.Date meetingDate = null;
            try {
                Object lastMeetingDate = SessionUtils.getAttribute("LastMeetingDate", request);
                if (lastMeetingDate != null) {
                    meetingDate = new java.sql.Date(((java.util.Date) lastMeetingDate).getTime());
                }

            } catch (PageExpiredException e) {
                throw new RuntimeException(e);
            }
            try {
                short isCenterHierarchyExists = (Short) SessionUtils.getAttribute(
                        CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS, request);
                return mandatoryCheck(meetingDate, getUserContext(request), isCenterHierarchyExists);
            } catch (PageExpiredException e) {
                errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                        ExceptionConstants.PAGEEXPIREDEXCEPTION));
            }
        }
        return errors;
    }

    private ActionErrors receiptDateValidate(ActionErrors errors, Locale locale) {
        if (StringUtils.isNotBlank(getReceiptDate()) && !DateUtils.isValidDate(getReceiptDate())) {
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
            String rcptdate = resources.getString(CollectionSheetEntryConstants.RECEIPTDATE);
            errors.add(CollectionSheetEntryConstants.INVALID_RECEIPT_DATE, new ActionMessage(
                    CollectionSheetEntryConstants.INVALID_RECEIPT_DATE, rcptdate));
        }
        return errors;
    }

    private ActionErrors mandatoryCheck(Date meetingDate, UserContext userContext, short isCenterHierarchyExists) {
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String loanOfficer = resources.getString(CollectionSheetEntryConstants.LOANOFFICERS);
        String modeOfPayment = resources.getString(CollectionSheetEntryConstants.MODE_OF_PAYMENT);
        String dateOfTransaction = resources.getString(CollectionSheetEntryConstants.DATEOFTRXN);
        ActionErrors errors = receiptDateValidate(new ActionErrors(), locale);
        java.sql.Date currentDate = null;
        try {
            currentDate = DateUtils.getLocaleDate(userContext.getPreferredLocale(), DateUtils
                    .getCurrentDate(userContext.getPreferredLocale()));
        } catch (InvalidDateException ide) {
            errors.add(CollectionSheetEntryConstants.INVALIDDATE, new ActionMessage(
                    CollectionSheetEntryConstants.INVALIDDATE));
        }
        java.sql.Date trxnDate = null;
        String customerLabel = isCenterHierarchyExists == Constants.YES ? ConfigurationConstants.CENTER
                : ConfigurationConstants.GROUP;
        if (officeId == null || "".equals(officeId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(
                    CollectionSheetEntryConstants.MANDATORYFIELDS, getMessageText(ConfigurationConstants.BRANCHOFFICE,
                            userContext)));
        }
        if (loanOfficerId == null || "".equals(loanOfficerId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(
                    CollectionSheetEntryConstants.MANDATORYFIELDS, loanOfficer));
        }
        if (customerId == null || "".equals(customerId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(
                    CollectionSheetEntryConstants.MANDATORYFIELDS, getLabel(customerLabel, userContext)));
        }
        if (paymentId == null || "".equals(paymentId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(
                    CollectionSheetEntryConstants.MANDATORYFIELDS, modeOfPayment));
        }
        if (getTransactionDate() != null && !getTransactionDate().equals("")) {
            try {
                trxnDate = DateUtils.getDateAsSentFromBrowser(getTransactionDate());
            } catch (InvalidDateException ide) {
                errors.add(AccountConstants.ERROR_INVALID_TRXN, new ActionMessage(AccountConstants.ERROR_INVALID_TRXN));
            }
        } else {
            errors.add(CollectionSheetEntryConstants.MANDATORYENTER, new ActionMessage(
                    CollectionSheetEntryConstants.MANDATORYENTER, dateOfTransaction));
        }
        if (currentDate != null && meetingDate != null && trxnDate != null
                && (meetingDate.compareTo(trxnDate) > 0 || trxnDate.compareTo(currentDate) > 0)) {
            errors.add(CollectionSheetEntryConstants.INVALIDENDDATE, new ActionMessage(
                    CollectionSheetEntryConstants.INVALIDENDDATE, dateOfTransaction));
        } else if (meetingDate == null && trxnDate != null && trxnDate.compareTo(currentDate) != 0) {
            errors.add(CollectionSheetEntryConstants.MEETINGDATEEXCEPTION, new ActionMessage(
                    CollectionSheetEntryConstants.MEETINGDATEEXCEPTION, dateOfTransaction));
        }

        return errors;
    }
}

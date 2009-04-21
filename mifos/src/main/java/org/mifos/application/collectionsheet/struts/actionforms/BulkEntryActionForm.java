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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryBO;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.util.helpers.BulkEntryDataView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class BulkEntryActionForm extends BaseActionForm {

    private static final long serialVersionUID = 5558673873893675965L;
    private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);
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
        if (StringUtils.isNullOrEmpty(getReceiptDateDD()) || StringUtils.isNullOrEmpty(getReceiptDateMM())
                || StringUtils.isNullOrEmpty(getReceiptDateYY()))
            return null;
        return getReceiptDateDD() + "/" + getReceiptDateMM() + "/" + getReceiptDateYY();
    }

    public void setReceiptDate(String s) {
        if (!StringUtils.isNullOrEmpty(s))
            setReceiptDate(DateUtils.getDate(s));
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
        if (StringUtils.isNullOrEmpty(transactionDateDD) || StringUtils.isNullOrEmpty(transactionDateMM)
                || StringUtils.isNullOrEmpty(transactionDateYY))
            return null;
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
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("BulkEntryActionForm.reset");
        if (request.getParameter(CollectionSheetEntryConstants.METHOD).equalsIgnoreCase(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
            try {
                CollectionSheetEntryBO bulkEntry = (CollectionSheetEntryBO) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY, request);
                
                int customers = bulkEntry.getTotalCustomers();
                int loanProductsSize = bulkEntry.getLoanProducts().size();
                int savingsProductSize = bulkEntry.getSavingsProducts().size();
                BulkEntryDataView bulkEntryDataView = new BulkEntryDataView();
                String enteredAmount[][] = new String[customers + 1][loanProductsSize];
                String disbursalAmount[][] = new String[customers + 1][loanProductsSize];
                String depositAmountEntered[][] = new String[customers + 1][savingsProductSize];
                String withdrawalAmountEntered[][] = new String[customers + 1][savingsProductSize];
                String attendance[] = new String[customers + 1];
                String customerAccountAmountEntered[] = new String[customers + 1];

                for (int rowIndex = 0; rowIndex <= customers; rowIndex++) {
                    attendance[rowIndex] = request.getParameter("attendanceSelected[" + rowIndex + "]");
                    for (int columnIndex = 0; columnIndex < loanProductsSize; columnIndex++) {
                        enteredAmount[rowIndex][columnIndex] = request.getParameter("enteredAmount[" + rowIndex + "]["
                                + columnIndex + "]");
                        disbursalAmount[rowIndex][columnIndex] = request.getParameter("enteredAmount[" + rowIndex
                                + "][" + (loanProductsSize + savingsProductSize + columnIndex) + "]");

                    }
                    for (int columnIndex = 0; columnIndex < savingsProductSize; columnIndex++) {
                        depositAmountEntered[rowIndex][columnIndex] = request.getParameter("depositAmountEntered["
                                + rowIndex + "][" + (loanProductsSize + columnIndex) + "]");
                        withdrawalAmountEntered[rowIndex][columnIndex] = request
                                .getParameter("withDrawalAmountEntered[" + rowIndex + "]["
                                        + ((2 * loanProductsSize) + savingsProductSize + columnIndex) + "]");
                    }
                    customerAccountAmountEntered[rowIndex] = request.getParameter("customerAccountAmountEntered["
                            + rowIndex + "][" + (2 * (loanProductsSize + savingsProductSize)) + "]");
                }
                bulkEntryDataView.setDisbursementAmountEntered(disbursalAmount);
                bulkEntryDataView.setLoanAmountEntered(enteredAmount);

                bulkEntryDataView.setWithDrawalAmountEntered(withdrawalAmountEntered);
                bulkEntryDataView.setDepositAmountEntered(depositAmountEntered);
                bulkEntryDataView.setCustomerAccountAmountEntered(customerAccountAmountEntered);
                bulkEntryDataView.setAttendance(attendance);
                bulkEntry.setBulkEntryDataView(bulkEntryDataView);
            } catch (PageExpiredException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("BulkEntryActionForm.validate");
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        ActionErrors errors = new ActionErrors();
        UserContext userContext = getUserContext(request);
        Locale locale = userContext.getPreferredLocale();
        if (request.getParameter(CollectionSheetEntryConstants.METHOD).equalsIgnoreCase(CollectionSheetEntryConstants.PREVIEWMETHOD)) {
            try {
                CollectionSheetEntryBO bulkEntry = (CollectionSheetEntryBO) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY, request);
                return validatePopulatedData(bulkEntry.getBulkEntryParent(), errors, locale);
            } catch (PageExpiredException e) {
                errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                        ExceptionConstants.PAGEEXPIREDEXCEPTION));
            }

        } else if (request.getParameter(CollectionSheetEntryConstants.METHOD).equalsIgnoreCase(CollectionSheetEntryConstants.GETMETHOD)) {
            java.sql.Date meetingDate = null;
            try {
                meetingDate = (Date) SessionUtils.getAttribute("LastMeetingDate", request);

            } catch (PageExpiredException e) {
                throw new RuntimeException(e);
            }
            try {
                short isCenterHeirarchyExists = (Short) SessionUtils.getAttribute(
                        CollectionSheetEntryConstants.ISCENTERHEIRARCHYEXISTS, request);
                return mandatoryCheck(meetingDate, getUserContext(request), isCenterHeirarchyExists);
            } catch (PageExpiredException e) {
                errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                        ExceptionConstants.PAGEEXPIREDEXCEPTION));
            }
        }
        return errors;
    }

    private ActionErrors validatePopulatedData(CollectionSheetEntryView parent, ActionErrors errors, Locale locale) {
        logger.debug("validatePopulatedData");
        List<CollectionSheetEntryView> children = parent.getCollectionSheetEntryChildren();

        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String acCollections = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);
        if (null != children) {
            for (CollectionSheetEntryView collectionSheetEntryView : children) {
                validatePopulatedData(collectionSheetEntryView, errors, locale);
            }
        }
        for (LoanAccountsProductView accountView : parent.getLoanAccountDetails()) {
            if (accountView.isDisburseLoanAccountPresent() || accountView.getLoanAccountViews().size() > 1) {
                Double enteredAmount = 0.0;
                if (null != accountView.getEnteredAmount() && accountView.isValidAmountEntered())
                    enteredAmount = getDoubleValue(accountView.getEnteredAmount());
                Double enteredDisbursalAmount = 0.0;
                if (null != accountView.getDisBursementAmountEntered() && accountView.isValidDisbursementAmount())
                    enteredDisbursalAmount = getDoubleValue(accountView.getDisBursementAmountEntered());
                Double totalDueAmount = accountView.getTotalAmountDue();
                Double totalDisburtialAmount = accountView.getTotalDisburseAmount();
                if (totalDueAmount.doubleValue() <= 0.0 && totalDisburtialAmount > 0.0) {
                    if (!accountView.isValidDisbursementAmount()
                            || (!enteredDisbursalAmount.equals(totalDisburtialAmount) && !enteredDisbursalAmount
                                    .equals(0.0)))
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView.getPrdOfferingShortName(),
                                parent.getCustomerDetail().getDisplayName()));
                }
                if (totalDisburtialAmount <= 0.0 && totalDueAmount > 0.0) {
                    if (!accountView.isValidAmountEntered()
                            || (!enteredAmount.equals(totalDueAmount) && !enteredAmount.equals(0.0)))
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView.getPrdOfferingShortName(),
                                parent.getCustomerDetail().getDisplayName()));
                }
                if (totalDueAmount.doubleValue() > 0.0 && totalDisburtialAmount > 0.0) {
                    if (!accountView.isValidAmountEntered()
                            || !accountView.isValidDisbursementAmount()
                            || (accountView.getEnteredAmount() == null)
                            || (accountView.getDisBursementAmountEntered() == null)
                            || (enteredAmount.equals(0.0) && !enteredDisbursalAmount.equals(0.0))
                            || (enteredDisbursalAmount.equals(0.0) && !enteredAmount.equals(0.0))
                            || (enteredDisbursalAmount.equals(totalDisburtialAmount) && !enteredAmount
                                    .equals(totalDueAmount))
                            || (enteredAmount.equals(totalDueAmount) && !enteredDisbursalAmount
                                    .equals(totalDisburtialAmount))
                            || (!enteredAmount.equals(totalDueAmount)
                                    && !enteredDisbursalAmount.equals(totalDisburtialAmount)
                                    && !enteredDisbursalAmount.equals(0.0) && !enteredAmount.equals(0.0)))
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView.getPrdOfferingShortName(),
                                parent.getCustomerDetail().getDisplayName()));
                }
                if (totalDisburtialAmount <= 0.0 && totalDueAmount <= 0.0) {
                    if (!accountView.isValidAmountEntered() || !accountView.isValidDisbursementAmount()
                            || !enteredDisbursalAmount.equals(0.0) || !enteredAmount.equals(0.0))
                        errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                                CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, accountView.getPrdOfferingShortName(),
                                parent.getCustomerDetail().getDisplayName()));
                }
            }
        }
        for (SavingsAccountView savingsAccountView : parent.getSavingsAccountDetails()) {
            if (!savingsAccountView.isValidDepositAmountEntered()
                    || !savingsAccountView.isValidWithDrawalAmountEntered()) {
                errors.add(CollectionSheetEntryConstants.ERRORINVALIDAMOUNT, new ActionMessage(
                        CollectionSheetEntryConstants.ERRORINVALIDAMOUNT, savingsAccountView.getSavingsOffering()
                                .getPrdOfferingShortName(), parent.getCustomerDetail().getDisplayName()));
            }
        }
        CustomerAccountView customerAccountView = parent.getCustomerAccountDetails();
        Double customerAccountAmountEntered = 0.0;
        if (null != customerAccountView.getCustomerAccountAmountEntered()
                && customerAccountView.isValidCustomerAccountAmountEntered())
            customerAccountAmountEntered = getDoubleValue(customerAccountView.getCustomerAccountAmountEntered());
        if (!customerAccountView.isValidCustomerAccountAmountEntered()
                || ((!customerAccountAmountEntered.equals(customerAccountView.getTotalAmountDue()
                        .getAmountDoubleValue())) && (!customerAccountAmountEntered.equals(0.0)))) {
            errors.add(CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, new ActionMessage(
                    CollectionSheetEntryConstants.BULKENTRYINVALIDAMOUNT, acCollections, parent.getCustomerDetail()
                            .getDisplayName()));
        }
        return errors;
    }

    private ActionErrors receiptDateValidate(ActionErrors errors, Locale locale) {
        if (!StringUtils.isNullOrEmpty(getReceiptDate()) && !DateUtils.isValidDate(getReceiptDate())) {
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
            String rcptdate = resources.getString(CollectionSheetEntryConstants.RECEIPTDATE);
            errors.add(CollectionSheetEntryConstants.INVALID_RECEIPT_DATE, new ActionMessage(
                    CollectionSheetEntryConstants.INVALID_RECEIPT_DATE, rcptdate));
        }
        return errors;
    }

    private ActionErrors mandatoryCheck(Date meetingDate, UserContext userContext, short isCenterHeirarchyExists) {
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, locale);
        String loanOfficer = resources.getString(CollectionSheetEntryConstants.LOANOFFICERS);
        String modeOfPayment = resources.getString(CollectionSheetEntryConstants.MODE_OF_PAYMENT);
        String dateOfTransaction = resources.getString(CollectionSheetEntryConstants.DATEOFTRXN);
        ActionErrors errors = receiptDateValidate(new ActionErrors(), locale);
        java.sql.Date currentDate = DateUtils.getLocaleDate(userContext.getPreferredLocale(), DateUtils
                .getCurrentDate(userContext.getPreferredLocale()));
        java.sql.Date trxnDate = null;
        String customerLabel = isCenterHeirarchyExists == Constants.YES ? ConfigurationConstants.CENTER
                : ConfigurationConstants.GROUP;
        if (getTransactionDate() != null && !getTransactionDate().equals("")) {
            trxnDate = DateUtils.getDateAsSentFromBrowser(getTransactionDate());
        }
        if (officeId == null || "".equals(officeId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(CollectionSheetEntryConstants.MANDATORYFIELDS,
                    getMessageText(ConfigurationConstants.BRANCHOFFICE, userContext)));
        }
        if (loanOfficerId == null || "".equals(loanOfficerId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(CollectionSheetEntryConstants.MANDATORYFIELDS,
                    loanOfficer));
        }
        if (customerId == null || "".equals(customerId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(CollectionSheetEntryConstants.MANDATORYFIELDS,
                    getLabel(customerLabel, userContext)));
        }
        if (paymentId == null || "".equals(paymentId.trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYFIELDS, new ActionMessage(CollectionSheetEntryConstants.MANDATORYFIELDS,
                    modeOfPayment));
        }
        if (getTransactionDate() == null || "".equals(getTransactionDate().trim())) {
            errors.add(CollectionSheetEntryConstants.MANDATORYENTER, new ActionMessage(CollectionSheetEntryConstants.MANDATORYENTER,
                    dateOfTransaction));
        } else if (!DateUtils.isValidDate(getTransactionDate())) {
            errors.add(CollectionSheetEntryConstants.INVALID_TRANSACTION_DATE, new ActionMessage(
                    CollectionSheetEntryConstants.INVALID_TRANSACTION_DATE));
        }
        if (currentDate != null && meetingDate != null && trxnDate != null
                && (meetingDate.compareTo(trxnDate) > 0 || trxnDate.compareTo(currentDate) > 0)) {
            errors.add(CollectionSheetEntryConstants.INVALIDENDDATE, new ActionMessage(CollectionSheetEntryConstants.INVALIDENDDATE,
                    dateOfTransaction));
        } else if (meetingDate == null && trxnDate != null && trxnDate.compareTo(currentDate) != 0) {
            errors.add(CollectionSheetEntryConstants.MEETINGDATEEXCEPTION, new ActionMessage(
                    CollectionSheetEntryConstants.MEETINGDATEEXCEPTION, dateOfTransaction));
        }

        return errors;
    }

}

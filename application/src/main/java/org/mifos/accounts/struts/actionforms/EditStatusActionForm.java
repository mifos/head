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

import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.questionnaire.struts.QuestionResponseCapturer;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

import static org.mifos.framework.util.helpers.DateUtils.dateFallsBeforeDate;
import static org.mifos.framework.util.helpers.DateUtils.getDateAsSentFromBrowser;

public class EditStatusActionForm extends BaseActionForm implements QuestionResponseCapturer{

    public EditStatusActionForm() {
        selectedItems = new String[50];
    }

    private String accountId;

    private String globalAccountNum;

    private String accountName;

    private String accountTypeId;

    private String currentStatusId;

    private String newStatusId;

    private String flagId;

    private String notes;

    private String[] selectedItems;

    private String input;

    private List<QuestionGroupDetail> questionGroups;

    private String transactionDate;

    private java.util.Date lastPaymentDate;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(String accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getCurrentStatusId() {
        return currentStatusId;
    }

    public void setCurrentStatusId(String currentStatusId) {
        this.currentStatusId = currentStatusId;
    }

    public String getFlagId() {
        return flagId;
    }

    public void setFlagId(String flagId) {
        this.flagId = flagId;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    public String getNewStatusId() {
        return newStatusId;
    }

    public void setNewStatusId(String newStatusId) {
        this.newStatusId = newStatusId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String[] getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(String[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDateValue(Locale preferredLocale) throws InvalidDateException {
        if (transactionDate == null) {
            transactionDate = DateUtils.makeDateAsSentFromBrowser();
        }
        return new Date(DateUtils.getLocaleDate(preferredLocale, transactionDate).getTime());
    }

    @Override
    public void reset(@SuppressWarnings("unused") ActionMapping mapping, HttpServletRequest request) {
        String methodCalled = request.getParameter(Methods.method.toString());
        if (null != methodCalled) {
            if ((Methods.preview.toString()).equals(methodCalled)) {
                this.flagId = null;
                this.selectedItems = null;
            }
        }
    }

    @Override
    public ActionErrors validate(@SuppressWarnings("unused") ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String methodCalled = request.getParameter(Methods.method.toString());
        if (null != methodCalled) {
            if ((Methods.preview.toString()).equals(methodCalled)) {
                errors.add(super.validate(mapping, request));
                handleStatusPreviewValidations(request, errors);
            } else if ((Methods.update.toString()).equals(methodCalled)) {
                handleUpdateStatus(request, errors);
            }
        }
        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", methodCalled);
        }
        return errors;
    }

    private ActionErrors handleUpdateStatus(HttpServletRequest request, ActionErrors errors) {
        String chklistSize = request.getParameter("chklistSize");
        if (request.getParameter("selectedItems") == null) {
            selectedItems = null;
        }
        if (chklistSize != null) {
            if (isCheckListNotComplete(chklistSize)) {
                addError(errors, LoanConstants.INCOMPLETE_CHECKLIST, LoanConstants.INCOMPLETE_CHECKLIST);
            }
        }
        return errors;
    }

    private ActionErrors handleStatusPreviewValidations(HttpServletRequest request, ActionErrors errors) {
        Locale locale = getUserContext(request).getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE, locale);
        String notesString = resources.getString("Account.Notes");
        String status = resources.getString("accounts.status");
        String flag = resources.getString("accounts.flag");
        if (newStatusId == null) {
            addError(errors, LoanConstants.MANDATORY, LoanConstants.MANDATORY, status);
        } else if (isNewStatusHasFlag() && StringUtils.isBlank(flagId)) {
            addError(errors, LoanConstants.MANDATORY_SELECT, LoanConstants.MANDATORY_SELECT, flag);
        }
        if (StringUtils.isBlank(notes)) {
            addError(errors, LoanConstants.MANDATORY_TEXTBOX, LoanConstants.MANDATORY_TEXTBOX, notesString);
        } else if (notes.length() > LoanConstants.COMMENT_LENGTH) {
            addError(errors, LoanConstants.MAX_LENGTH, LoanConstants.MAX_LENGTH, notesString, String
                    .valueOf(LoanConstants.COMMENT_LENGTH));
        }
        validateTransactionDate(errors, resources);
        return errors;
    }

    private boolean isCheckListNotComplete(String chklistSize) {
        return (isPartialSelected(chklistSize) || isNoneSelected(chklistSize));
    }

    private boolean isPartialSelected(String chklistSize) {
        return (selectedItems != null) && (Integer.valueOf(chklistSize).intValue() != selectedItems.length);
    }

    private boolean isNoneSelected(String chklistSize) {
        return (Integer.valueOf(chklistSize).intValue() > 0) && (selectedItems == null);
    }

    private boolean isNewStatusHasFlag() {
        return (Short.valueOf(newStatusId).equals(AccountState.LOAN_CANCELLED.getValue()))
                || (Short.valueOf(newStatusId).equals(AccountState.SAVINGS_CANCELLED.getValue()));
    }

    @Override
    public void setQuestionGroups(List<QuestionGroupDetail> questionGroups) {
        this.questionGroups = questionGroups;
    }

    @Override
    public List<QuestionGroupDetail> getQuestionGroups() {
        return questionGroups;
    }

    private void validateTransactionDate(ActionErrors errors, ResourceBundle resources) {
        String fieldName = "accounts.date_of_trxn";
        ActionErrors validationErrors = validateDate(getTransactionDate(), resources.getString(fieldName));
        if (null != validationErrors && !validationErrors.isEmpty()) {
            errors.add(validationErrors);
        }
        validationErrors = validatePaymentDate(getTransactionDate(), resources.getString(fieldName));
        if (null != validationErrors && !validationErrors.isEmpty()) {
            errors.add(validationErrors);
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

    public ActionErrors validatePaymentDate(String transactionDate, String fieldName) {
        ActionErrors errors = null;
        if (transactionDate != null && !transactionDate.equals("")) {
	        try {
	            if (lastPaymentDate != null && dateFallsBeforeDate(getDateAsSentFromBrowser(transactionDate), lastPaymentDate)) {
	                errors = new ActionErrors();
	                errors.add(AccountConstants.ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT,
	                        new ActionMessage(AccountConstants.ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT,
	                                fieldName));
	            }
	        } catch (InvalidDateException ide) {
	            errors = new ActionErrors();
	            errors.add(AccountConstants.ERROR_INVALIDDATE, new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
	                    fieldName));
	        }
        }
        return errors;
    }

    public void setLastPaymentDate(java.util.Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
}

/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.savings.struts.actionforms;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;

public class SavingsClosureActionForm extends ValidatorActionForm {
    private String receiptId;

    private String receiptDate;

    private String customerId;

    private String paymentTypeId;

    private String trxnDate;

    private String notes;

    private String amount;

    public SavingsClosureActionForm() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getTrxnDate() {
        return trxnDate;
    }

    public void setTrxnDate(String trxnDate) {
        this.trxnDate = trxnDate;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
        ActionErrors errors = new ActionErrors();

        if (method.equals("load") || method.equals("previous") || method.equals("validate") || method.equals("close")
                || method.equals("cancel")) {
        } else {
            UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                    .getSession());
            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SAVING_UI_RESOURCE_PROPERTYFILE, userContext
                    .getPreferredLocale());

            String amount = getAmount();
            if (!("0.0".equals(amount))) {
                if (StringUtils.isNotBlank(amount)) {
                    if (StringUtils.isBlank(getPaymentTypeId())) {
                        errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
                                AccountConstants.ERROR_MANDATORY, resources.getString("Savings.paymentType")));
                    }
                }
            }

            if (this.getReceiptDate() != null && !this.getReceiptDate().equals("")) {
                ActionErrors dateError = validateDate(this.getReceiptDate(),
                        resources.getString("Savings.receiptDate"), userContext);
                if (dateError != null && !dateError.isEmpty()) {
                    errors.add(dateError);
                }
            }

            if (StringUtils.isBlank(getCustomerId())) {
                errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                        resources.getString("Savings.ClientName")));
            }

            if (StringUtils.isBlank(getNotes())) {
                errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                        resources.getString("Savings.notes")));
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }

        return errors;
    }

    private ActionErrors validateDate(String date, String fieldName, UserContext userContext) {
        ActionErrors errors = new ActionErrors();
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

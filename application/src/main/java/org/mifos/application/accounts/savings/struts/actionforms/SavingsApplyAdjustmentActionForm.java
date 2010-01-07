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

package org.mifos.application.accounts.savings.struts.actionforms;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class SavingsApplyAdjustmentActionForm extends BaseActionForm {

    public String lastPaymentAmount;
    public String note;
    public String input;
    public String lastPaymentAmountOption;

    public SavingsApplyAdjustmentActionForm() {
    }

    public String getLastPaymentAmount() {
        return lastPaymentAmount;
    }

    public void setLastPaymentAmount(String lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLastPaymentAmountOption() {
        return lastPaymentAmountOption;
    }

    public void setLastPaymentAmountOption(String lastPaymentAmountOption) {
        this.lastPaymentAmountOption = lastPaymentAmountOption;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
        ActionErrors errors = new ActionErrors();
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY))
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        try {
            if (method != null && method.equals("preview")) {
                SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
                AccountPaymentEntity payment = savings.getLastPmnt();
                if (payment == null || savings.getLastPmntAmnt() == 0 
                    || !(new SavingsHelper().getPaymentActionType(payment).equals(
                                AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()) || new SavingsHelper()
                                .getPaymentActionType(payment).equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue()))) {
                    errors.add(SavingsConstants.INVALID_LAST_PAYMENT, new ActionMessage(
                            SavingsConstants.INVALID_LAST_PAYMENT));
                } else {
                    if (StringUtils.isBlank(getLastPaymentAmount())) {
                        errors.add(SavingsConstants.INVALID_ADJUSTMENT_AMOUNT, new ActionMessage(
                                SavingsConstants.INVALID_ADJUSTMENT_AMOUNT));
                    }
                    
                    if(StringUtils.isNotBlank(getLastPaymentAmount())) {
                        Locale locale = getUserContext(request).getPreferredLocale();
                        validateAmount(errors, locale);
                    }

                    if (StringUtils.isNotBlank(getNote())
                            && getNote().length() > CustomerConstants.COMMENT_LENGTH) {
                        errors.add(AccountConstants.MAX_NOTE_LENGTH, new ActionMessage(
                                AccountConstants.MAX_NOTE_LENGTH, AccountConstants.COMMENT_LENGTH));
                    }
                    errors.add(super.validate(mapping, request));
                }
            }
        } catch (ApplicationException ae) {
            errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae.getValues()));
        }
        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }
        return errors;
    }
    
    private void validateAmount(ActionErrors errors, Locale locale) {
        DoubleConversionResult conversionResult = validateAmount(getLastPaymentAmount(), SavingsConstants.AMOUNT, errors, locale, 
                FilePaths.SAVING_UI_RESOURCE_PROPERTYFILE);
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, SavingsConstants.AMOUNT, AccountConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO, 
                    lookupLocalizedPropertyValue(SavingsConstants.AMOUNT, locale, FilePaths.SAVING_UI_RESOURCE_PROPERTYFILE));
        }
    }
}

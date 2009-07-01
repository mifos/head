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

package org.mifos.application.accounts.loan.struts.actionforms;

import static org.mifos.framework.util.helpers.StringUtils.isNullOrEmpty;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;

public class LoanDisbursmentActionForm extends AccountApplyPaymentActionForm {

    private Money loanAmount;

    private String paymentModeOfPayment;

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        ActionErrors errors1 = super.validate(mapping, request);
        if (errors1 != null)
            errors.add(errors1);

        String method = request.getParameter(MethodNameConstants.METHOD);
        if (isPreviewMethod(method) && isAmountGreaterThanZero(getAmount()) && isNullOrEmpty(paymentModeOfPayment)) {
            String errorMessage = getResourceBundle(getUserLocale(request)).getString("loan.paymentid");
            errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY,
                    errorMessage));
        }

        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }
        return errors;
    }

    private ResourceBundle getResourceBundle(Locale userLocale) {
        return ResourceBundle.getBundle(FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, userLocale);
    }

    private boolean isAmountGreaterThanZero(Money amount) {
        return amount != null && amount.getAmountDoubleValue() > 0.0;
    }

    private boolean isPreviewMethod(String methodName) {
        return methodName != null && methodName.equals(MethodNameConstants.PREVIEW);
    }

    public String getPaymentModeOfPayment() {
        return paymentModeOfPayment;
    }

    public void setPaymentModeOfPayment(String paymentModeOfPayment) {
        this.paymentModeOfPayment = paymentModeOfPayment;
    }

    public Money getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Money loanAmount) {
        this.loanAmount = loanAmount;
    }

    @Override
    public void clear() {
        super.clear();
        this.loanAmount = null;
    }

}

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

package org.mifos.accounts.struts.actionforms;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;

public class ApplyChargeActionForm extends BaseActionForm {

    private String accountId;

    // chargeType is constructed in the jsp page applyCharges.jsp as "<feeId>:<isRateType>"
    // where <feeId> is the id of the fee that has been selected and
    // <isRateType> is "1" if the fee is a rate type (% of some amount) or "0" if it is
    // a simple amount (money value)
    private String chargeType;

    private String chargeAmount;

    private String charge;

    private String selectedChargeFormula;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getSelectedChargeFormula() {
        return selectedChargeFormula;
    }

    public void setSelectedChargeFormula(String selectedChargeFormula) {
        this.selectedChargeFormula = selectedChargeFormula;
    }

    /*
     * Extract the <isRateType> boolean value from the chargeType (see note above about chargeType)
     */
    public boolean isRateType() {
        String[] values = getChargeType().split(":");
        return values[1].equals("1");
    }

    /*
     * Extract the <feeId> value from the chargeType (see note above about chargeType)
     */
    public String getFeeId() {
        String[] values = getChargeType().split(":");
        return values[0];
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        ActionErrors errors = new ActionErrors();
        String methodCalled = request.getParameter(Methods.method.toString());
        if (null != methodCalled) {
            if ((Methods.update.toString()).equals(methodCalled)) {
                if (StringUtils.isNotBlank(selectedChargeFormula)) {
                    validateRate(errors, request);

                }
                validateAmount(errors, locale);
                errors.add(super.validate(mapping, request));
            }
        }
        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", methodCalled);
        }
        return errors;
    }

    private void validateRate(ActionErrors errors, HttpServletRequest request) {
        //FIXME Do not use hard coded values for properties local.properties
        if (getDoubleValue(chargeAmount) > Double.valueOf("999")) {
            errors.add(AccountConstants.RATE, new ActionMessage(AccountConstants.RATE_ERROR));
            request.setAttribute("selectedChargeFormula", selectedChargeFormula);
        }
    }

    protected void validateAmount(ActionErrors errors, Locale locale) {
        DoubleConversionResult conversionResult;

        if (isRateType()) {
            conversionResult = validateInterest(getCharge(), AccountConstants.ACCOUNT_AMOUNT, errors, locale,
                FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE);
        } else {
            conversionResult = validateAmount(getCharge(), AccountConstants.ACCOUNT_AMOUNT, errors, locale,
                FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE);
        }
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, AccountConstants.ACCOUNT_AMOUNT, AccountConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    lookupLocalizedPropertyValue(AccountConstants.ACCOUNT_AMOUNT, locale, FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE));
        }
    }

}

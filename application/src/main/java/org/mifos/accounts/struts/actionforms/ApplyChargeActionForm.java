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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.AccountingRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;

public class ApplyChargeActionForm extends BaseActionForm {

    private String accountId;

    // chargeType is constructed in the jsp page applyCharges.jsp as "<isPenaltyType>:<feeId>:<isRateType>"
    // where <feeId> is the id of the fee that has been selected and
    // <isRateType> is "1" if the fee is a rate type (% of some amount) or "0" if it is
    // a simple amount (money value)
    private String chargeType;

    private String chargeAmount;

    private String charge;

    private String selectedChargeFormula;
    
    private Map<Integer,String> individualValues = new HashMap<Integer, String>();
    
    public Map<Integer, String> getIndividualValues() {
        return individualValues;
    }

    public void setIndividualValues(Map<Integer, String> individualValues) {
        this.individualValues = individualValues;
    }
    
    public void setUpdateIndividualValues(String accountId, String value) {
        individualValues.put(Integer.valueOf(accountId), value);
    }
    
    public String getIndividualValues(Integer accountId) {
        return individualValues.get(accountId);
    }

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
        return values[2].equals("1");
    }
    
    /*
     * Extract the <isPenaltyType> boolean value from the chargeType (see note above about chargeType)
     */
    public boolean isPenaltyType() {
        String[] values = getChargeType().split(":");
        return values[0].equals("1");
    }

    /*
     * Extract the <feeId> value from the chargeType (see note above about chargeType)
     */
    public String getFeeId() {
        String[] values = getChargeType().split(":");
        return values[1];
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        ActionErrors errors = new ActionErrors();
        
        String methodCalled = request.getParameter(MethodNameConstants.METHOD);
        
        boolean groupLoanWithMembers = AccountingRules.isGroupLoanWithMembers();
        
        AccountBusinessService service = new AccountBusinessService();
        AccountBO accountBO = null;
        try {
            accountBO = service.getAccount(Integer.valueOf(getAccountId()));
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
        
        if (groupLoanWithMembers && accountBO.isParentGroupLoanAccount()) {
            if (methodCalled != null && methodCalled.equals("divide")) {
                if (StringUtils.isNotBlank(selectedChargeFormula)) {
                    validateRate(errors, request);
                }
                validateAmount(errors, locale);
            }
            if (methodCalled != null && methodCalled.equals("update")) {
                validateHashMap(errors);
            }

            if (!errors.isEmpty()) {
                request.setAttribute(Globals.ERROR_KEY, errors);

                if (methodCalled.equals("divide")) {
                    request.setAttribute("methodCalled", "update");
                } else if (methodCalled.equals("update")) {
                    request.setAttribute("methodCalled", "create");
                } else {
                    request.setAttribute("methodCalled", methodCalled);
                }

            }
        } else {
            if (null != methodCalled) {
                if ((Methods.update.toString()).equals(methodCalled)) {
                    if (StringUtils.isNotBlank(selectedChargeFormula)) {
                        validateRate(errors, request);
                    }
                    validateAmount(errors, locale);

                }
            }
            if (!errors.isEmpty()) {
                request.setAttribute(Globals.ERROR_KEY, errors);
                request.setAttribute("methodCalled", methodCalled);
            }
        }
        return errors;
        
        
    }
    
    private void validateHashMap(ActionErrors errors) {
        ArrayList<String> mapValue = new ArrayList<String>(individualValues.values());
        double total = 0.0;
        for (int i=0; i<individualValues.size(); i++){
            DoubleConversionResult conversionResult = validateAmount(mapValue.get(i), getChargeCurrency() , AccountConstants.ACCOUNT_AMOUNT, errors, "");
            if (conversionResult.getErrors().size() > 0 || conversionResult.getDoubleValue() < 0.0) {
                addError(errors, AccountConstants.ACCOUNT_AMOUNT, AccountConstants.ERRORS_MUST_BE_GREATER_OR_EQUAL_ZERO,
                        getLocalizedMessage(AccountConstants.ACCOUNT_AMOUNT));
            } else {
            	total += conversionResult.getDoubleValue();
            }
        }
        
        if (!isRateType() && total != getDoubleValue(charge)) {
        	addError(errors, AccountConstants.ACCOUNT_AMOUNT, AccountConstants.ERRORS_MUST_SUM_TO_VALID_AMOUNT,
        			getLocalizedMessage(AccountConstants.ACCOUNT_AMOUNT), charge);
        }
    }

    protected void validateRate(ActionErrors errors, HttpServletRequest request) {
        Double chargeAmountDoubleValue;
        try {
            chargeAmountDoubleValue = getDoubleValue(chargeAmount);
        } catch (NumberFormatException e) {
            return;
        }
        if (chargeAmountDoubleValue != null) {
            //FIXME Do not use hard coded values for properties local.properties
            if (chargeAmountDoubleValue > Double.valueOf("999")) {
                errors.add(AccountConstants.RATE, new ActionMessage(AccountConstants.RATE_ERROR));
                request.setAttribute("selectedChargeFormula", selectedChargeFormula);
            }
        }
    }

    protected void validateAmount(ActionErrors errors, Locale locale) {

        if(StringUtils.isBlank(getChargeType())){
            addError(errors, FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE, "errors.mandatoryselect",
                    getLocalizedMessage("account.chargetype"));
            return;
        }

        DoubleConversionResult conversionResult = null;
        String chargeAmount = getCharge();
        if (StringUtils.isBlank(chargeAmount)) {
            Double amount = 0.0;
            for(Map.Entry<Integer, String> entry : individualValues.entrySet()) {
                amount += Double.valueOf(entry.getValue());
            }
            chargeAmount = amount.toString();
        }
        if (!StringUtils.isBlank(chargeAmount)) {
            if (isRateType()) {
                conversionResult = validateInterest(getCharge(), AccountConstants.ACCOUNT_AMOUNT, errors);
            } else {
                conversionResult = validateAmount(getCharge(), getChargeCurrency(), AccountConstants.ACCOUNT_AMOUNT,
                        errors,"");
            }
        }
        
        else {
        	   addError(errors, AccountConstants.ACCOUNT_AMOUNT, "errors.mandatory", "amount");
        }
        	
        if (conversionResult != null && conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, AccountConstants.ACCOUNT_AMOUNT, AccountConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    getLocalizedMessage(AccountConstants.ACCOUNT_AMOUNT));
        }
    }

    private MifosCurrency getChargeCurrency() {
        try {
            AccountBusinessService service = new AccountBusinessService();
            AccountBO accountBO = service.getAccount(Integer.valueOf(getAccountId()));
            return accountBO.getCurrency();
        }
        catch(Exception ex) {
            return null;
        }
    }

    public void clear() {
        setAccountId(null);
        setChargeType(null);
        setChargeAmount(null);
    }
}
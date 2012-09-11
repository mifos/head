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

package org.mifos.accounting.struts.actionform;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenBalanceActionForm extends BaseActionForm {
    private static final Logger logger = LoggerFactory.getLogger(OpenBalanceActionForm.class);
    private String financialYear;
    private String officeHierarchy;
    private String office;
    private String coaName;
    private String openBalance;
    private String financialYearId;
    private String amountAction;


	public String getAmountAction() {
		return amountAction;
	}

	public void setAmountAction(String amountAction) {
		this.amountAction = amountAction;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getOfficeHierarchy() {
		return officeHierarchy;
	}

	public void setOfficeHierarchy(String officeHierarchy) {
		this.officeHierarchy = officeHierarchy;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}
	public String getCoaName() {
		return coaName;
	}

	public void setCoaName(String coaName) {
		this.coaName = coaName;
	}

	public String getOpenBalance() {
		return openBalance;
	}

	public void setOpenBalance(String openBalance) {
		this.openBalance = openBalance;
	}

	public String getFinancialYearId() {
		return financialYearId;
	}

	public void setFinancialYearId(String financialYearId) {
		this.financialYearId = financialYearId;
	}

	@Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("reset method called");
        if(request.getParameter(SimpleAccountingConstants.METHOD).equals("load")){
		this.financialYear=null;
		this.financialYearId=null;
		this.office=null;
		this.officeHierarchy=null;
		this.coaName=null;
		this.openBalance=null;
        }

    }

	   @Override
	   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	        logger.debug("OpenBalanceActionForm.validate");
//	        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
	        ActionErrors errors = new ActionErrors();

	        if (request.getParameter(SimpleAccountingConstants.METHOD).equalsIgnoreCase(
				SimpleAccountingConstants.PREVIEW)) {
	                return mandatoryCheck(getUserContext(request));
	        }
	        return errors;
	    }





	    private ActionErrors mandatoryCheck(UserContext userContext) {
	        Locale locale = userContext.getPreferredLocale();
	        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

	        String financial_year = resources.getString(SimpleAccountingConstants.FINANCIALYEAR);
	        String office_Hierarchy = resources.getString(SimpleAccountingConstants.OFFICE_HIERARCHY);
	        String officeId = resources.getString(SimpleAccountingConstants.OFFICE);
	        String coa_name = resources.getString(SimpleAccountingConstants.COANAME);
	        String open_balance = resources.getString(SimpleAccountingConstants.OPENBALANCE);
	        ActionErrors errors = new ActionErrors();

	        if (financialYear == null || "".equals(financialYear.trim())) {
	            errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(
				SimpleAccountingConstants.MANDATORYFIELDS,financial_year));
	        }

	        if (officeHierarchy == null || "".equals(officeHierarchy.trim())) {
	            errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(
				SimpleAccountingConstants.MANDATORYFIELDS,office_Hierarchy ));
	        }


	        if (office == null || "".equals(office.trim())) {
	            errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(
				SimpleAccountingConstants.MANDATORYFIELDS, officeId));
	        }


	        if (coaName == null || "".equals(coaName.trim())) {
	            errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(
				SimpleAccountingConstants.MANDATORYFIELDS, coa_name));
	        }



	        if (openBalance == null || "".equals(openBalance.trim())) {
	            errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(
				SimpleAccountingConstants.MANDATORYFIELDS, open_balance));
	        }
	        
	        
	        if(StringUtils.isNotBlank(getOpenBalance())) {
                DoubleConversionResult conversionResult = validateAmount(getOpenBalance(), open_balance, errors);
                if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
                    addError(errors, SimpleAccountingConstants.AMOUNT, SimpleAccountingConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    		open_balance);
                }
            }

	        return errors;
	    }
	    
	    protected DoubleConversionResult validateAmount(String amountString, MifosCurrency currency,
                String fieldPropertyKey, ActionErrors errors, String installmentNo) {
			String fieldName = fieldPropertyKey;
			DoubleConversionResult conversionResult = parseDoubleForMoney(amountString, currency);
			for (ConversionError error : conversionResult.getErrors()) {
			String errorText = error.toLocalizedMessage(currency);
			addError(errors, fieldName, "errors.generic", fieldName, errorText);
			}
			return conversionResult;
		}
	    
	    protected DoubleConversionResult validateAmount(String amountString, String fieldPropertyKey, ActionErrors errors) {
		     return validateAmount(amountString, null, fieldPropertyKey, errors, "");
	    }
}

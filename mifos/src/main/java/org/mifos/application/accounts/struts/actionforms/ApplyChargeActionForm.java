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
 
package org.mifos.application.accounts.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class ApplyChargeActionForm extends BaseActionForm {

	private String accountId;

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

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter(Methods.method.toString());
		if (null != methodCalled) {
			if ((Methods.update.toString()).equals(methodCalled)) {
				if(!StringUtils.isNullOrEmpty(selectedChargeFormula)){
					validateRate(errors, request);
					
				}
				errors.add(super.validate(mapping, request));
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

	private void validateRate(ActionErrors errors,HttpServletRequest request) {
		if(getDoubleValue(chargeAmount) > Double.valueOf("999")){
			errors.add(AccountConstants.RATE,
					new ActionMessage(AccountConstants.RATE_ERROR));
			request.setAttribute("selectedChargeFormula" ,selectedChargeFormula);
		}
		
	}

}

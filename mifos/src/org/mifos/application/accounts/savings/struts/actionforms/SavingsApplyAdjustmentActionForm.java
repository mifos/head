/**
 
 * SavingsAdjustmentActionForm.java    version: 1.0
 
 
 
 * Copyright (c) 2005-2006 Grameen Foundation USA
 
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 
 * All rights reserved.
 
 
 
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 
 * and how it is applied. 
 
 *
 
 */
package org.mifos.application.accounts.savings.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class SavingsApplyAdjustmentActionForm extends ValidatorActionForm{

	public Money lastPaymentAmount;
	public String note;
	public String input;
	public String lastPaymentAmountOption;
	
	public SavingsApplyAdjustmentActionForm(){
		lastPaymentAmount=new Money();
	}
	
	public Money getLastPaymentAmount() {
		return lastPaymentAmount;
	}

	public void setLastPaymentAmount(Money lastPaymentAmount) {
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
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		String method = request.getParameter("method");
		ActionErrors errors = null;
		if (null == request.getAttribute(Constants.CURRENTFLOWKEY))
			request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
		try{
			if(method!=null && method.equals("load")||
							method.equals("previous")||
							method.equals("validate")||
							method.equals("adjustLastUserAction")||
							method.equals("cancel")){
			}else{
				if(method.equals("preview")){
					SavingsBO savings = (SavingsBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
					AccountPaymentEntity payment = savings.getLastPmnt();
					if(payment==null || savings.getLastPmntAmnt()==0 || !(new SavingsHelper().getPaymentActionType(payment).equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL) || new SavingsHelper().getPaymentActionType(payment).equals(AccountConstants.ACTION_SAVINGS_DEPOSIT))){
						if(errors==null)
							errors = new ActionErrors();
						errors.add(SavingsConstants.INVALID_LAST_PAYMENT,new ActionMessage(SavingsConstants.INVALID_LAST_PAYMENT));
					}
				}
				if(errors==null){
					errors = new ActionErrors();
					errors.add(super.validate(mapping,request));
				}
			}
		}catch(ApplicationException ae){
			errors = new ActionErrors();
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		return errors;
	}	
}

/**
 
 * AccountAction.java    version: xxx
 
 
 
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

package org.mifos.application.accounts.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.action.MifosSearchWizardAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.MethodInvoker;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as base class for account related actions like 
 * loan action etc.
 */
public abstract class AccountAction extends MifosSearchWizardAction {
	
	/**
	 * 
	 */
	public AccountAction() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	/**
	 * This method gets the applicable product  offerings to be displayed in the UI. It also sets the customer master 
	 * object in the session as a removable attribute.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getPrdOfferings(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside getPrdOfferings method");
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(AccountConstants.GETPRDOFFERINGS);
		forward = (ActionForward)MethodInvoker.invokeWithNoException(this, AccountConstants.CUSTOMGETPRDOFFERINGS, new Object[]{mapping,form,request,response}, new Class[]{ActionMapping.class,ActionForm.class,HttpServletRequest.class,HttpServletResponse.class});
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After invoking  customGetPrdOfferings method");
		delegate(context,request);
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("getting the control back after invoking method on business processor");
		// this sets the customermaster object in the session as a removable attribute hence it would be removed after the action changes.
		SessionUtils.setRemovableAttribute(AccountConstants.CUSTOMERMASTER, context.getBusinessResults(AccountConstants.CUSTOMERMASTER),getPath(),request.getSession());
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After setting customerMaster in session as removable attribute");
		if(null != forward){
			return forward;
		}else{
			return mapping.findForward(AccountConstants.GETPRDOFFERINGS_SUCCESS);
		}
	}
	
}

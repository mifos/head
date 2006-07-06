/**

 * AccountsApplyChargesAction.java    version: 1.0



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
import org.mifos.application.accounts.struts.actionforms.AccountsApplyChargesActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * @author rajenders
 * 
 */
public class AccountsApplyChargesAction extends MifosBaseAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {
		return AccountConstants.ACCOUNTSAPPLYCHARGESDEPENDENCY;
	}

	/**
	 * This method is called before converting action form to value object on
	 * every method call. if on a particular method conversion is not required ,
	 * it returns false, otherwise returns true
	 * 
	 * @return Returns whether the action form should be converted or not
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info(
				"Before converting action form to value object checking for method name "
						+ methodName);
		if (null != methodName
				&& (methodName.equals(MethodNameConstants.MANAGE) || methodName
						.equals(MethodNameConstants.CANCEL))) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * This method is called on cancel.
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be
	 *         forwarded.
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		AccountsApplyChargesActionForm actionForm = (AccountsApplyChargesActionForm) form;
		String inputPage = actionForm.getInput();
		if (inputPage != null) {
			if (inputPage.equals("reviewTransactionPage")) {
				forward = MethodNameConstants.CANCEL_LOAN_SUCCESS;

			} else if (inputPage.equals(GroupConstants.VIEW_GROUP_CHARGES)) {
				forward = GroupConstants.VIEW_GROUP_CHARGES;
			} else if (inputPage.equals("ViewCenterCharges")) {
				forward = CenterConstants.CENTER_CHARGES_DETAILS_PAGE;
			} else if (inputPage.equals("ViewClientCharges")) {
				forward = "ViewClientCharges";
			} else if (inputPage.equals("installmentDetailsPage")) {				
				forward = AccountConstants.LOAN_DETAILS_PAGE;
			} 
			
		}
		return mapping.findForward(forward);
	}

	/**
	 * This method is called on create.
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be
	 *         forwarded.
	 * @throws Exception
	 */
	public ActionForward customCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		AccountsApplyChargesActionForm actionForm = (AccountsApplyChargesActionForm) form;
		String inputPage = actionForm.getInput();		
		if (inputPage != null) {
			if (inputPage.equals("reviewTransactionPage")) {
				forward = MethodNameConstants.CREATE_LOAN_SUCCESS;

			} else if (inputPage.equals(GroupConstants.VIEW_GROUP_CHARGES)) {
				forward = GroupConstants.VIEW_GROUP_CHARGES;
			} else if (inputPage.equals("ViewCenterCharges")) {
				forward = CenterConstants.CENTER_CHARGES_DETAILS_PAGE;
			} else if (inputPage.equals("ViewClientCharges")) {
				forward = "ViewClientCharges";
			} else if (inputPage.equals("installmentDetailsPage")) {				
				forward = AccountConstants.LOAN_DETAILS_PAGE;
			}
		}
		return mapping.findForward(forward);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#load(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		AccountsApplyChargesActionForm aacaf = (AccountsApplyChargesActionForm)form;
		aacaf.setChargeAmount("");
		aacaf.setChargeType("");
		aacaf.setFormula("");
		aacaf.setFormulaplaceHolder("");
		aacaf.setSelectedChargeAmount("");
		ActionForward forward = super.load(mapping, form, request, response);
		Context context = (Context) SessionUtils.getAttribute(
				Constants.CONTEXT, request.getSession());

		HeaderObject headerObject = (HeaderObject) context
				.getBusinessResults("header_load");
		// It is being set as attribute and not as removable attribute because
		// we want it to
		// be accessed even when the action changes.
		SessionUtils.setAttribute("header_load", headerObject, request
				.getSession());

		return forward;
	}

}

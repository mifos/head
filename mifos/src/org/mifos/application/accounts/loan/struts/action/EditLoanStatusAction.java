/**

 * EditLoanStatusAction.java    version: 1.0



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
package org.mifos.application.accounts.loan.struts.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.EditLoanStatusActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.EditLoanStatus;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

public class EditLoanStatusAction extends MifosBaseAction {
	/**
	 * Returns the path which uniquely identifies the element in the
	 * dependency.xml. This method implementaion is the framework requirement.
	 */
	public String getPath() {
		return LoanConstants.LOAN_STATUS_ACTION;
	}

	/**
	 * This method is called to load the status page
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		EditLoanStatusActionForm actionForm = (EditLoanStatusActionForm) form;
		actionForm.setSelectedItems(null);
		actionForm.getNotes().setComment(null);
		actionForm.setFlagId(null);
		actionForm.setNewStatusId(null);
		return null;
	}

	/**
	 * This method is called to show history of account status change
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(LoanConstants.GET_STATUS_HISTORY);
		delegate(context, request);
		return mapping.findForward(LoanConstants.SEARCH_SUCCESS);
	}

	/**
	 * Method which is called to decide the pages on which the errors on failure
	 * of validation will be displayed this method forwards as per the
	 * respective input page
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward customValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = null;
		String methodCalled = (String) request.getAttribute("methodCalled");

		// deciding forward page
		if (null != methodCalled) {
			if ((CustomerConstants.METHOD_PREVIEW).equals(methodCalled))
				forward = AccountConstants.LOAD_SUCCESS;
		}
		return mapping.findForward(forward);
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
		if (null != methodName && (methodName.equals(MethodNameConstants.CANCEL) || methodName.equals(MethodNameConstants.WRITEOFF))) {
			return false;
		} else {
			return true;
		}
	}
	
	public Map<String,String> appendToMap()
	{
		Map<String,String> keyMethodMap = super.appendToMap();
		keyMethodMap.put(MethodNameConstants.WRITEOFF,   MethodNameConstants.WRITEOFF);
		return keyMethodMap;
	}

	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		EditLoanStatusActionForm editLoanStatusActionForm = (EditLoanStatusActionForm) form;
		EditLoanStatus loanStatus = (EditLoanStatus) context.getValueObject();
		if (loanStatus.getNewStatusId().shortValue() != LoanConstants.CANCELLED
				&& (!loanStatus.getFlagId().equals(null) || loanStatus
						.getFlagId().shortValue() == 0)) {
			loanStatus.setFlagId(null);
			editLoanStatusActionForm.setFlagId(null);
		}
		return mapping.findForward(MethodNameConstants.PREVIEW_SUCCESS);
	}
	
	public ActionForward writeOff(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws SystemException, ApplicationException {
		ActionForward forward = null;
		LoanBO loanBO = null;
		EditLoanStatusActionForm editLoanStatusActionForm = (EditLoanStatusActionForm) form;
		
		UserContext userContext = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		try {
			loanBO = new LoanBusinessService().findBySystemId(editLoanStatusActionForm.getGlobalAccountNum());
			loanBO.setUserContext(userContext);
			loanBO.writeOff(editLoanStatusActionForm.getNotes().getComment());
			HibernateUtil.commitTransaction();
			forward = mapping.findForward(MethodNameConstants.UPDATE_SUCCESS);
		}catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add(LoanConstants.STATUS_CHANGE_NOT_ALLOWED,new ActionMessage(LoanConstants.STATUS_CHANGE_NOT_ALLOWED));
			request.setAttribute(Globals.ERROR_KEY, errors);
			forward = mapping.findForward(MethodNameConstants.UPDATE_FAILURE);
			HibernateUtil.rollbackTransaction();
		}finally {
			HibernateUtil.closeSession();
		}
		return forward;
	}
}

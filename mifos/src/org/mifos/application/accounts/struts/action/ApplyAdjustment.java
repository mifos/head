/**

 * ApplyAdjustment.java    version: 1.0

 

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
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.struts.actionforms.ApplyAdjustmentActionForm;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

/**
 * This is the action class for applying adjustment. This action is to be merged
 * with AccountAction once an AccountAction for M2 is done.
 */
public class ApplyAdjustment extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return new AccountBusinessService();
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("applyAdjustment");
		security.allow("loadAdjustment", SecurityConstants.VIEW);
		security.allow("previewAdjustment", SecurityConstants.VIEW);
		security.allow("applyAdjustment", SecurityConstants.VIEW);
		security.allow("cancelAdjustment", SecurityConstants.VIEW);
		return security;
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward loadAdjustment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
		AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, accnt, request);
		request.setAttribute("method", "loadAdjustment");
		if (null == accnt.getLastPmnt() || accnt.getLastPmntAmnt() == 0) {
			request.setAttribute("isDisabled", "true");
			throw new ApplicationException(
					AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		return mapping.findForward("loadadjustment_success");

	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewAdjustment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute("method", "previewAdjustment");
		ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
		AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
		if (null == accnt.getLastPmnt() || accnt.getLastPmntAmnt() == 0) {
			request.setAttribute("method", "loadAdjustment");
			throw new ApplicationException(
					AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		return mapping.findForward("previewadj_success");
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward applyAdjustment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute("method", "applyAdjustment");
		ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
		AccountBO accountBOInSession = (AccountBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		AccountBO accnt = getBizService().findBySystemId(appAdjustActionForm.getGlobalAccountNum());
		checkVersionMismatch(accountBOInSession.getVersionNo(),accnt.getVersionNo());
		if (null == accnt.getLastPmnt() || accnt.getLastPmntAmnt() == 0) {
			request.setAttribute("method", "previewAdjustment");
			throw new ApplicationException(
					AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		accnt.setUserContext(uc);
		if (accnt.getPersonnel() != null)
			getBizService().checkPermissionForAdjustment(AccountTypes.LOAN_ACCOUNT, null, uc,
					accnt.getOffice().getOfficeId(), accnt.getPersonnel()
							.getPersonnelId());
		else
			getBizService().checkPermissionForAdjustment(AccountTypes.LOAN_ACCOUNT, null, uc,
					accnt.getOffice().getOfficeId(), uc.getId());
		try {
			accnt.adjustPmnt(appAdjustActionForm.getAdjustmentNote());
		} catch (ApplicationException ae) {
			request.setAttribute("method", "previewAdjustment");
			throw ae;
		}
		resetActionFormFields(appAdjustActionForm);
		return mapping.findForward("applyadj_success");
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelAdjustment(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ApplyAdjustmentActionForm appAdjustActionForm = (ApplyAdjustmentActionForm) form;
		resetActionFormFields(appAdjustActionForm);
		return mapping.findForward("canceladj_success");
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {

		return true;

	}

	/**
	 * This method resets action form fields after successfully applying payment
	 * or on cancel.
	 */
	private void resetActionFormFields(
			ApplyAdjustmentActionForm appAdjustActionForm) {
		appAdjustActionForm.setAdjustmentNote(null);
	}

	@Override
	protected boolean isNewBizRequired(HttpServletRequest request)
			throws ServiceException {
		if (request.getAttribute(Constants.BUSINESS_KEY) != null) {
			return false;
		}
		return true;
	}
	
	private AccountBusinessService getBizService(){
		return new AccountBusinessService();
	}
}

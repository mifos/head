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
 
package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.struts.actionforms.CustomerApplyAdjustmentActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomerApplyAdjustmentAction extends BaseAction {
	private CustomerBusinessService customerBusinessService;
	
	public CustomerApplyAdjustmentAction() throws ServiceException {
		customerBusinessService = (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return customerBusinessService;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("custApplyAdjustment");
		security.allow("loadAdjustment", SecurityConstants.VIEW);
		security.allow("previewAdjustment", SecurityConstants.VIEW);
		security.allow("applyAdjustment", SecurityConstants.VIEW);
		security.allow("cancelAdjustment", SecurityConstants.VIEW);
		return security;
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward loadAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		resetActionFormFields(applyAdjustmentActionForm);
		CustomerBO customerBO = ((CustomerBusinessService) getService()).findBySystemId(applyAdjustmentActionForm.getGlobalCustNum());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerBO,request);
		request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
		if(null == customerBO.getCustomerAccount().getLastPmnt() || customerBO.getCustomerAccount().getLastPmntAmnt() == 0){
			request.setAttribute("isDisabled", "true");
			throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		return mapping.findForward(CustomerConstants.METHOD_LOAD_ADJUSTMENT_SUCCESS);
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previewAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		CustomerBO customerBO = ((CustomerBusinessService) getService()).findBySystemId(applyAdjustmentActionForm.getGlobalCustNum());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerBO,request);
		if(null == customerBO.getCustomerAccount().getLastPmnt() || customerBO.getCustomerAccount().getLastPmntAmnt() == 0){
			request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
			request.setAttribute("isDisabled", "true");
			throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		return mapping.findForward(CustomerConstants.METHOD_PREVIEW_ADJUSTMENT_SUCCESS);
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward applyAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String forward = null;
		request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_APPLY_ADJUSTMENT);
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		CustomerBO customerBOInSession = (CustomerBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
		CustomerBO customerBO = ((CustomerBusinessService) getService()).findBySystemId(applyAdjustmentActionForm.getGlobalCustNum());
		checkVersionMismatch(customerBOInSession.getVersionNo(),customerBO.getVersionNo());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerBO,request);
		if(null == customerBO.getCustomerAccount().getLastPmnt() || customerBO.getCustomerAccount().getLastPmntAmnt() == 0){
			request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
			throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		customerBO.setUserContext(uc);
		customerBO.getCustomerAccount().setUserContext(uc);
		if (customerBO.getPersonnel() != null)
			getAccountBizService().checkPermissionForAdjustment(
					AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel(), uc,
					customerBO.getOffice().getOfficeId(),
					customerBO.getPersonnel().getPersonnelId());
		else
			getAccountBizService().checkPermissionForAdjustment(
					AccountTypes.CUSTOMER_ACCOUNT, customerBO.getLevel(), uc,
					customerBO.getOffice().getOfficeId(), uc.getId());
		try {
		customerBO.adjustPmnt(applyAdjustmentActionForm.getAdjustmentNote());
		}catch(ApplicationException ae) {
			request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
			throw ae;
		}
		
		String inputPage = applyAdjustmentActionForm.getInput();
		resetActionFormFields(applyAdjustmentActionForm);
		if(inputPage!=null){
			if(inputPage.equals(CustomerConstants.VIEW_GROUP_CHARGES)){
				forward= CustomerConstants.APPLY_ADJUSTMENT_GROUP_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CENTER_CHARGES)){
				forward= CustomerConstants.APPLY_ADJUSTMENT_CENTER_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CLIENT_CHARGES)){
				forward= CustomerConstants.APPLY_ADJUSTMENT_CLIENT_SUCCESS;
			}
		}
		return mapping.findForward(forward);
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String forward = null;
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		resetActionFormFields(applyAdjustmentActionForm);
		String inputPage = applyAdjustmentActionForm.getInput();
		resetActionFormFields(applyAdjustmentActionForm);
		if(inputPage!=null){
			if(inputPage.equals(CustomerConstants.VIEW_GROUP_CHARGES)){
				forward= CustomerConstants.CANCELADJ_GROUP_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CENTER_CHARGES)){
				forward= CustomerConstants.CANCELADJ_CENTER_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CLIENT_CHARGES)){
				forward= CustomerConstants.CANCELADJ_CLIENT_SUCCESS;
			}
		}
		return mapping.findForward(forward);
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method)  {
		return true;
		
	}
	
	private void resetActionFormFields(CustomerApplyAdjustmentActionForm applyAdjustmentActionForm){
		applyAdjustmentActionForm.setAdjustmentNote(null);
	}
	
	private AccountBusinessService getAccountBizService(){
		return new AccountBusinessService();
	}
}

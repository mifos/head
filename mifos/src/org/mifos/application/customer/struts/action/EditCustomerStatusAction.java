/**

* EditCustomerStatusAction.java version: 1.0



* Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.customer.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.EditStatusActionForm;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.struts.actionforms.EditCustomerStatusActionForm;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class EditCustomerStatusAction extends BaseAction {

	private CustomerBusinessService customerService;
	private AccountBusinessService accountBusinessService;

	private PersonnelPersistenceService personnelPersistenceService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CENTERLOGGER);

	public EditCustomerStatusAction() throws ServiceException {
		customerService = (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
		personnelPersistenceService = (PersonnelPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.Personnel);
		accountBusinessService = (AccountBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
	}

	protected BusinessService getService() {
		return customerService;
	}

	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
		
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try{
		logger.debug("In EditSavingsStatusAction:load()");
		doCleanUp(form, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerBO customerBO = customerService.getCustomer(Integer.valueOf(((EditCustomerStatusActionForm) form).getCustomerId()));
		customerBO.setUserContext(userContext);
		customerService.initializeStateMachine(userContext.getLocaleId(),customerBO.getOffice().getOfficeId(),CustomerLevel.CENTER.getValue());
		setFormAttributes(form,customerBO);
		customerBO.getCustomerStatus().setLocaleId(userContext.getLocaleId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request
				.getSession());
		
		SessionUtils.setAttribute(SavingsConstants.STATUS_LIST, customerBO
				.getStatusList(), request.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, NumberFormatException {
		EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
		CustomerBO customerBO = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request.getSession());
		UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
		getCheckList(form,customerBO,request.getSession(),userContext);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, NumberFormatException {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}
	
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
		UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerBO customerBO = customerService.getCustomer(Integer.valueOf(editStatusActionForm.getCustomerId()));
		customerBO.setUserContext(userContext);
		customerBO.getCustomerStatus().setLocaleId(userContext.getLocaleId());
		try {
			Short flagId = null;
			Short newStatusId = null;
			if(StringUtils.isNullAndEmptySafe(editStatusActionForm.getFlagId()))
				flagId = Short.valueOf(editStatusActionForm.getFlagId());
			if(StringUtils.isNullAndEmptySafe(editStatusActionForm.getNewStatusId()))
				newStatusId = Short.valueOf(editStatusActionForm.getNewStatusId());
			customerBO.changeStatus(newStatusId,flagId,editStatusActionForm.getNotes());
			customerBO.update();
			} catch (ApplicationException ae) {
			
			ActionErrors errors = new ActionErrors();
			errors.add(ae.getKey(),	new ActionMessage(ae.getKey(),ae.getValues()));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(ActionForwards.update_failure.toString());
		}
		SessionUtils.setAttribute(SavingsConstants.NEW_FLAG_NAME,SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request.getSession()), request.getSession());
		return mapping.findForward(getDetailAccountPage(form));
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(getDetailAccountPage(form));
	}
	
	private void getCheckList(ActionForm form, CustomerBO customerBO, HttpSession session, UserContext userContext) throws Exception{
		EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
		editCustomerStatusActionForm.setCommentDate(DateHelper.getCurrentDate(userContext.getPereferedLocale()));
		String newStatusName = null;
		String flagName = null;
		List<CustomerCheckListBO> checklist = customerService.getStatusChecklist(Short.valueOf(editCustomerStatusActionForm.getNewStatusId()), Short.valueOf(editCustomerStatusActionForm.getLevelId()));
		SessionUtils.setAttribute(SavingsConstants.STATUS_CHECK_LIST,checklist, session);
		if (StringUtils.isNullAndEmptySafe(editCustomerStatusActionForm.getNewStatusId())) 
			newStatusName = customerBO.getStatusName(userContext.getLocaleId(),Short.valueOf(editCustomerStatusActionForm.getNewStatusId()));
		SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME,	newStatusName, session);
		if (StringUtils.isNullAndEmptySafe(editCustomerStatusActionForm.getNewStatusId()) && isNewStatusCancelledOrClosed(Short.valueOf(editCustomerStatusActionForm.getNewStatusId()))) 
			flagName = customerBO.getFlagName(new Short(editCustomerStatusActionForm.getFlagId()));
		SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, flagName, session);
		
	}

	private boolean isNewStatusCancelledOrClosed(Short newStatusId) {
		return newStatusId.equals(CustomerStatus.CLIENT_CANCELLED.getValue()) || newStatusId.equals(CustomerStatus.CLIENT_CANCELLED.getValue())|| newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue())|| newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue());
	}

	private void setFormAttributes(ActionForm form, CustomerBO customerBO) {
		EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
		editCustomerStatusActionForm.setLevelId(customerBO.getCustomerLevel().getId().toString());
		editCustomerStatusActionForm.setCurrentStatusId(customerBO.getCustomerStatus().getId().toString());
		editCustomerStatusActionForm.setGlobalAccountNum(customerBO.getGlobalCustNum());
		editCustomerStatusActionForm.setCustomerName(customerBO.getDisplayName());
		if(customerBO instanceof CenterBO) {
			editCustomerStatusActionForm.setInput("center");
		}else if(customerBO instanceof GroupBO) {
			editCustomerStatusActionForm.setInput("group");
		}else if(customerBO instanceof ClientBO) {
			editCustomerStatusActionForm.setInput("client");
		}
		
	}

	private void doCleanUp(ActionForm form, HttpServletRequest request) {
		EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
		editCustomerStatusActionForm.setSelectedItems(null);
		editCustomerStatusActionForm.setNotes(null);
		editCustomerStatusActionForm.setNewStatusId(null);
		editCustomerStatusActionForm.setFlagId(null);
		request.getSession().removeAttribute(Constants.BUSINESS_KEY);
	}
	private String getDetailAccountPage(ActionForm form) {
		EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
		String input = editStatusActionForm.getInput();
		String forward = null;
		if(input.equals("center"))
			forward = ActionForwards.center_detail_page.toString();
		return forward;
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(Methods.preview.toString()))
				forward = ActionForwards.preview_failure.toString();
			else if (method.equals(Methods.load.toString()))
				forward = getDetailAccountPage(form);
			else if (method.equals(Methods.update.toString()))
				forward = ActionForwards.update_failure.toString();
		}
		return mapping.findForward(forward);
	}

}

/**

 * EditCustomerStatusAction.java version: 1.0



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

package org.mifos.application.customer.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.struts.actionforms.EditCustomerStatusActionForm;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class EditCustomerStatusAction extends BaseAction {

	private CustomerBusinessService customerService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CENTERLOGGER);

	public EditCustomerStatusAction() throws ServiceException {
		customerService = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer);
	}

	@Override
	protected BusinessService getService() {
		return customerService;
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;

	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In EditSavingsStatusAction:load()");
		doCleanUp(form, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerBO customerBO = customerService
				.getCustomer(Integer
						.valueOf(((EditCustomerStatusActionForm) form)
								.getCustomerId()));
		customerBO.setUserContext(userContext);
		customerService.initializeStateMachine(userContext.getLocaleId(),
				customerBO.getOffice().getOfficeId(),
				AccountTypes.CUSTOMERACCOUNT.getValue(),
				getLevelIdBasedOnCustomer(customerBO));
		setFormAttributes(form, customerBO);
		customerBO.getCustomerStatus().setLocaleId(userContext.getLocaleId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request
				.getSession());

		SessionUtils.setAttribute(SavingsConstants.STATUS_LIST, customerService
				.getStatusList(customerBO.getCustomerStatus(),
						getLevelIdBasedOnCustomer(customerBO), getUserContext(
								request).getLocaleId()), request.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, NumberFormatException {
		CustomerBO customerBO = (CustomerBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		setStatusDetails(form, customerBO, request.getSession(), userContext);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, NumberFormatException {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException {
		EditCustomerStatusActionForm editStatusActionForm = (EditCustomerStatusActionForm) form;
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerBO customerBO = customerService.getCustomer(Integer
				.valueOf(editStatusActionForm.getCustomerId()));
		customerBO.setUserContext(userContext);
		customerBO.getCustomerStatus().setLocaleId(userContext.getLocaleId());
		Short flagId = null;
		Short newStatusId = null;
		if (StringUtils.isNullAndEmptySafe(editStatusActionForm.getFlagId()))
			flagId = Short.valueOf(editStatusActionForm.getFlagId());
		if (StringUtils.isNullAndEmptySafe(editStatusActionForm
				.getNewStatusId()))
			newStatusId = Short.valueOf(editStatusActionForm.getNewStatusId());
		checkPermission(customerBO, request, newStatusId, flagId);
		customerBO.changeStatus(newStatusId, flagId, editStatusActionForm
				.getNotes());
		customerBO.update();
		SessionUtils.setAttribute(SavingsConstants.NEW_FLAG_NAME,
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()), request.getSession());
		return mapping.findForward(getDetailAccountPage(form));
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(getDetailAccountPage(form));
	}

	private void setStatusDetails(ActionForm form, CustomerBO customerBO,
			HttpSession session, UserContext userContext) throws Exception {
		EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
		editCustomerStatusActionForm.setCommentDate(DateHelper
				.getCurrentDate(userContext.getPereferedLocale()));
		String newStatusName = null;
		String flagName = null;
		List<CustomerCheckListBO> checklist = customerService
				.getStatusChecklist(Short.valueOf(editCustomerStatusActionForm
						.getNewStatusId()), Short
						.valueOf(editCustomerStatusActionForm.getLevelId()));
		SessionUtils.setAttribute(SavingsConstants.STATUS_CHECK_LIST,
				checklist, session);
		if (StringUtils.isNullAndEmptySafe(editCustomerStatusActionForm
				.getNewStatusId())) {
			newStatusName = customerService.getStatusName(userContext
					.getLocaleId(), Short.valueOf(editCustomerStatusActionForm
					.getNewStatusId()), getLevelIdBasedOnCustomer(customerBO));
		}
		SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME,
				newStatusName, session);
		if (StringUtils.isNullAndEmptySafe(editCustomerStatusActionForm
				.getNewStatusId())
				&& isNewStatusCancelledOrClosed(Short
						.valueOf(editCustomerStatusActionForm.getNewStatusId()))) {
			flagName = customerService.getFlagName(userContext.getLocaleId(),
					new Short(editCustomerStatusActionForm.getFlagId()),
					getLevelIdBasedOnCustomer(customerBO));
		}
		SessionUtils
				.setAttribute(SavingsConstants.FLAG_NAME, flagName, session);

	}

	private boolean isNewStatusCancelledOrClosed(Short newStatusId) {
		return newStatusId.equals(CustomerStatus.CLIENT_CANCELLED.getValue())
				|| newStatusId.equals(CustomerStatus.CLIENT_CLOSED.getValue())
				|| newStatusId
						.equals(CustomerStatus.GROUP_CANCELLED.getValue())
				|| newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue());
	}

	private void setFormAttributes(ActionForm form, CustomerBO customerBO) {
		EditCustomerStatusActionForm editCustomerStatusActionForm = (EditCustomerStatusActionForm) form;
		editCustomerStatusActionForm.setLevelId(customerBO.getCustomerLevel()
				.getId().toString());
		editCustomerStatusActionForm.setCurrentStatusId(customerBO
				.getCustomerStatus().getId().toString());
		editCustomerStatusActionForm.setGlobalAccountNum(customerBO
				.getGlobalCustNum());
		editCustomerStatusActionForm.setCustomerName(customerBO
				.getDisplayName());
		if (customerBO instanceof CenterBO) {
			editCustomerStatusActionForm.setInput("center");
		} else if (customerBO instanceof GroupBO) {
			editCustomerStatusActionForm.setInput("group");
		} else if (customerBO instanceof ClientBO) {
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
		if (input.equals("center"))
			forward = ActionForwards.center_detail_page.toString();
		else if (input.equals("group"))
			forward = ActionForwards.group_detail_page.toString();
		else if (input.equals("client"))
			forward = ActionForwards.client_detail_page.toString();
		return forward;
	}

	private void checkPermission(CustomerBO customerBO,
			HttpServletRequest request, Short newStatusId, Short flagId) {
		if (null != customerBO.getPersonnel())
			customerService.checkPermissionForStatusChange(newStatusId,
					getUserContext(request), flagId, customerBO.getOffice()
							.getOfficeId(), customerBO.getPersonnel()
							.getPersonnelId());
		else
			customerService.checkPermissionForStatusChange(newStatusId,
					getUserContext(request), flagId, customerBO.getOffice()
							.getOfficeId(), getUserContext(request).getId());
	}

	private Short getLevelIdBasedOnCustomer(CustomerBO customerBO) {
		if (customerBO instanceof CenterBO) {
			return CustomerLevel.CENTER.getValue();
		} else if (customerBO instanceof GroupBO) {
			return CustomerLevel.GROUP.getValue();
		} else if (customerBO instanceof ClientBO) {
			return CustomerLevel.CLIENT.getValue();
		}
		return null;
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request
				.getAttribute(SavingsConstants.METHODCALLED);
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

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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.struts.actionforms.CustomerAccountActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomerAccountAction extends AccountAppAction {
	public CustomerAccountAction() throws Exception {
		super();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("customerAccountAction");
		security.allow("load", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalCustNum = ((CustomerAccountActionForm) form).getGlobalCustNum();
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer);
		CustomerBO customerBO = customerService.findBySystemId(globalCustNum);
		CustomerAccountBO customerAccount = customerBO.getCustomerAccount();
		List<CustomerRecentActivityView> recentActivities = customerService
				.getRecentActivityView(customerBO.getCustomerId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerAccount, request);
		//SessionUtils.setAttribute(CustomerConstants.CUSTOMER_ACCOUNT,customerAccount, request);
		SessionUtils.setCollectionAttribute(CustomerConstants.RECENT_ACTIVITIES,recentActivities, request);
		ActionForwards forward = getForward(customerBO);
		return mapping.findForward(forward.toString());
	}

	private ActionForwards getForward(CustomerBO customerBO) {
		if (customerBO.getCustomerLevel().getId().equals(
				CustomerLevel.CLIENT.getValue()))
			return ActionForwards.client_detail_page;
		if (customerBO.getCustomerLevel().getId().equals(
				CustomerLevel.GROUP.getValue()))
			return ActionForwards.group_detail_page;
		if (customerBO.getCustomerLevel().getId().equals(
				CustomerLevel.CENTER.getValue()))
			return ActionForwards.center_detail_page;
		return null;
	}
}

package org.mifos.application.customer.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.struts.actionforms.CustomerAccountActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class CustomerAccountAction extends AccountAppAction {
	public CustomerAccountAction() throws Exception {
		super();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalCustNum = ((CustomerAccountActionForm) form)
				.getGlobalCustNum();
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer);
		CustomerBO customerBO = customerService.findBySystemId(globalCustNum);
		CustomerAccountBO customerAccount = customerBO.getCustomerAccount();
		List<CustomerRecentActivityView> recentActivities = customerService
				.getRecentActivityView(customerBO.getCustomerId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request
				.getSession());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_ACCOUNT,
				customerAccount, request.getSession());
		SessionUtils.setAttribute(CustomerConstants.RECENT_ACTIVITIES,
				recentActivities, request.getSession());
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

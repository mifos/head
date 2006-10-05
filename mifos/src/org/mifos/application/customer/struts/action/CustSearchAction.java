package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.actionforms.CustSearchActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustSearchAction extends SearchAction {

	@TransactionDemarcate(saveToken = true)
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		actionForm.setSearchString(null);
		cleanUpSearch(request);
		return mapping
				.findForward(ActionForwards.loadSearch_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;

		String searchString = actionForm.getSearchString();
		if (searchString == null)
			throw new CustomerException(CenterConstants.NO_SEARCH_STING);
		searchString = searchString.trim();
		if (searchString.equals(""))
			throw new CustomerException(CenterConstants.NO_SEARCH_STING);

		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());

		if (actionForm.getInput() != null
				&& actionForm.getInput().equals("loan"))
			SessionUtils.setAttribute(Constants.SEARCH_RESULTS,
					getCustomerBusinessService().searchGroupClient(
							searchString, userContext.getId()), request);
		else if (actionForm.getInput() != null
				&& actionForm.getInput().equals("savings"))
			SessionUtils.setAttribute(Constants.SEARCH_RESULTS,
					getCustomerBusinessService().searchCustForSavings(
							searchString, userContext.getId()), request);
		return super.search(mapping, form, request, response);

	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	protected CustomerBusinessService getCustomerBusinessService() {
		return (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return getCustomerBusinessService();
	}
}

package org.mifos.application.customer.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.actionforms.CustSearchActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.ClientRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustSearchAction extends SearchAction {
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("custSearchAction");
		security.allow("loadSearch", SecurityConstants.VIEW);
		security.allow("search", SecurityConstants.VIEW);
		security.allow("load", SecurityConstants.VIEW);
		security.allow("loadMainSearch", SecurityConstants.VIEW);
		security.allow("mainSearch", SecurityConstants.VIEW);
		security.allow("getHomePage", SecurityConstants.VIEW);
		security.allow("loadAllBranches", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("getOfficeHomePage", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;

		if (actionForm.getLoanOfficerId() != null) {
			loadLoanOfficer(
					new PersonnelBusinessService()
							.getPersonnel(getShortValue(actionForm
									.getLoanOfficerId())), request);
		}
		String officeName = null;
		if (actionForm.getOfficeId() != null
				&& !actionForm.getOfficeId().equals(""))
			officeName = new OfficeBusinessService().getOffice(
					getShortValue(actionForm.getOfficeId())).getOfficeName();
		else
			officeName = new OfficeBusinessService().getOffice(
					getUserContext(request).getBranchId()).getOfficeName();
		SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, officeName,
				request);

		SessionUtils.setAttribute("isCenterHeirarchyExists", ClientRules.getCenterHierarchyExists(), request);

		SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD,
				CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);
		return mapping
				.findForward(CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS);

	}

	@TransactionDemarcate(conditionToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		if (actionForm.getOfficeId() != null) {
			List<PersonnelBO> personnelList = new PersonnelBusinessService()
					.getActiveLoanOfficersUnderOffice(getShortValue(actionForm
							.getOfficeId()));
			SessionUtils.setCollectionAttribute(CustomerSearchConstants.LOANOFFICERSLIST,
					personnelList, request);
		}
		String officeName = null;
		if (actionForm.getOfficeId() != null
				&& !actionForm.getOfficeId().equals(""))
			officeName = new OfficeBusinessService().getOffice(
					getShortValue(actionForm.getOfficeId())).getOfficeName();
		else
			officeName = new OfficeBusinessService().getOffice(
					getUserContext(request).getBranchId()).getOfficeName();
		SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, officeName,
				request);

		SessionUtils.setAttribute("isCenterHeirarchyExists", ClientRules.getCenterHierarchyExists(), request);
		SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD,
				CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);

		return mapping
				.findForward(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS);

	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward loadAllBranches(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		actionForm.setOfficeId("0");
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		SessionUtils.setAttribute("isCenterHeirarchyExists", ClientRules.getCenterHierarchyExists(), request);

		loadMasterData(userContext.getId(), request, actionForm);
		return mapping
				.findForward(CustomerSearchConstants.LOADALLBRANCHES_SUCCESS);

	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward getHomePage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		actionForm.setSearchString(null);

		cleanUpSearch(request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		SessionUtils.setAttribute("isCenterHeirarchyExists", ClientRules.getCenterHierarchyExists(), request);
		loadMasterData(userContext.getId(), request, actionForm);
		
		fixUpReportSecurity();
		
		return mapping.findForward(CustomerConstants.GETHOMEPAGE_SUCCESS);
	}

    private static void fixUpReportSecurity() {
        ActivityMapper.getInstance().getActivityMap().put("/reportsUserParamsAction-loadAddList-"+ 4, (short) -1);
        try {
             AuthorizationManager.getInstance().init();
         } catch (ApplicationException e) {
             e.printStackTrace();
         }
     }
	
	
	@TransactionDemarcate(saveToken = true)
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		actionForm.setSearchString(null);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        cleanUpSearch(request);
		return mapping
				.findForward(ActionForwards.loadSearch_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward loadMainSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		actionForm.setSearchString(null);
		actionForm.setOfficeId("0");
		cleanUpSearch(request);
		UserContext userContext = getUserContext(request);
		SessionUtils.setAttribute("isCenterHeirarchyExists", ClientRules.getCenterHierarchyExists(), request);

		forward = loadMasterData(userContext.getId(), request, actionForm);
		return mapping.findForward(forward);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward mainSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		Short officeId = getShortValue(actionForm.getOfficeId());
		String searchString = actionForm.getSearchString();
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USERCONTEXT, request.getSession());
		super.search(mapping, form, request, response);
		if (searchString == null || searchString.equals("")) {

			ActionErrors errors = new ActionErrors();

			errors.add(CustomerSearchConstants.NAMEMANDATORYEXCEPTION,
					new ActionMessage(
							CustomerSearchConstants.NAMEMANDATORYEXCEPTION));

			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(ActionForwards.mainSearch_success
					.toString());
		}

		if (officeId != null && officeId != 0)
			addSeachValues(searchString, officeId.toString(),
					new OfficeBusinessService().getOffice(officeId)
							.getOfficeName(), request);
		else
			addSeachValues(searchString, officeId.toString(),
					new OfficeBusinessService().getOffice(
							userContext.getBranchId()).getOfficeName(), request);
		searchString = StringUtils.normalizeSearchString(searchString);
		if (searchString.equals(""))
			throw new CustomerException(
					CustomerSearchConstants.NAMEMANDATORYEXCEPTION);
		SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,
				getCustomerBusinessService().search(searchString, officeId,
						userContext.getId(), userContext.getBranchId()),
				request);
		return mapping
				.findForward(ActionForwards.mainSearch_success.toString());

	}

	@TransactionDemarcate(conditionToken = true)
	public ActionForward getOfficeHomePage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Short loggedUserLevel = getUserContext(request).getLevelId();
		if (loggedUserLevel.equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
			return loadMainSearch(mapping, form, request, response);
		} else {
			return preview(mapping, form, request, response);
		}

	}

	private String loadMasterData(Short userId, HttpServletRequest request,
			CustSearchActionForm form) throws Exception {
		PersonnelBO personnel = new PersonnelBusinessService()
				.getPersonnel(userId);
		SessionUtils.setAttribute(CustomerSearchConstants.OFFICE, personnel
				.getOffice().getOfficeName(), request);
		if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER)
			return loadLoanOfficer(personnel, request);
		else
			return loadNonLoanOfficer(personnel, request, form);

	}

	private String loadLoanOfficer(PersonnelBO personnel,
			HttpServletRequest request) throws Exception {

		List<CustomerBO> customerList = null;

		boolean isCenterHierarchyExist = ClientRules.getCenterHierarchyExists();

		if (isCenterHierarchyExist)
			customerList = getCustomerBusinessService()
					.getActiveCentersUnderUser(personnel);
		else
			customerList = getCustomerBusinessService().getGroupsUnderUser(
					personnel);
		SessionUtils.setCollectionAttribute(CustomerSearchConstants.CUSTOMERLIST,
				customerList, request);
		SessionUtils.setAttribute("GrpHierExists", isCenterHierarchyExist,
				request);
		SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD,
				CustomerSearchConstants.LOADFORWARDLOANOFFICER, request);

		return CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS;
	}

	private String loadNonLoanOfficer(PersonnelBO personnel,
			HttpServletRequest request, CustSearchActionForm form)
			throws Exception {
		if (personnel.getOffice().getOfficeLevel().equals(
				OfficeLevel.BRANCHOFFICE)) {
			List<PersonnelBO> personnelList = new PersonnelBusinessService()
					.getActiveLoanOfficersUnderOffice(personnel.getOffice().getOfficeId());
			SessionUtils.setCollectionAttribute(CustomerSearchConstants.LOANOFFICERSLIST,
					personnelList, request);
			SessionUtils.setAttribute(CustomerSearchConstants.LOADFORWARD,
					CustomerSearchConstants.LOADFORWARDNONLOANOFFICER, request);
			form.setOfficeId(personnel.getOffice().getOfficeId().toString());
			return CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS;
		} else {
			SessionUtils.setCollectionAttribute(CustomerSearchConstants.OFFICESLIST,
					new OfficeBusinessService()
							.getActiveBranchesUnderUser(personnel), request);
			SessionUtils
					.setAttribute(CustomerSearchConstants.LOADFORWARD,
							CustomerSearchConstants.LOADFORWARDNONBRANCHOFFICE,
							request);

			return CustomerSearchConstants.LOADFORWARDOFFICE_SUCCESS;
		}

	}

	@Override
	@TransactionDemarcate(joinToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward actionForward = super.search(mapping, form, request,
				response);
		;
		CustSearchActionForm actionForm = (CustSearchActionForm) form;
		UserContext userContext = getUserContext(request);
		String searchString = actionForm.getSearchString();
		if (searchString == null)
			throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
		addSeachValues(searchString, userContext.getBranchId().toString(),
				new OfficeBusinessService()
						.getOffice(userContext.getBranchId()).getOfficeName(),
				request);
		searchString = StringUtils.normalizeSearchString(searchString);
		if (searchString.equals(""))
			throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
		if (actionForm.getInput() != null
				&& actionForm.getInput().equals("loan"))
			SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,
					getCustomerBusinessService().searchGroupClient(
							searchString, userContext.getId()), request);
		else if (actionForm.getInput() != null
				&& actionForm.getInput().equals("savings"))
			SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,
					getCustomerBusinessService().searchCustForSavings(
							searchString, userContext.getId()), request);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        return actionForward;

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

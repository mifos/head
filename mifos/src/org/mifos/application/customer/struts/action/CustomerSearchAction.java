/**

 * CustomerSearchAction.java    version: xxx



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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.customer.business.handlers.CustomerSearchBusinessProcessor;
import org.mifos.application.customer.struts.actionforms.CustomerSearchActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceConstants;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class is used as Action to search for customers.
 */
public class CustomerSearchAction extends MifosSearchAction {

	/**
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */

	@Override
	protected String getPath() {
		return CustomerSearchConstants.GETPATHCUSTOMERSEARCH;
	}

	/**
	 * This method we are using to get to the home page
	 */
	@Override
	public Map<String, String> appendToMap() {
		Map<String, String> customerMap = super.appendToMap();

		customerMap.put(CustomerConstants.GETHOMEPAGE,
				CustomerConstants.GETHOMEPAGE);
		customerMap.put(CustomerConstants.LOADALLBRANCHES,
				CustomerConstants.LOADALLBRANCHES);
		customerMap.put(CustomerConstants.GET_OFFICE_HOMEPAGE,
				CustomerConstants.GET_OFFICE_HOMEPAGE);
		return customerMap;
	}

	/**
	 * The method is called when the user clicks home tab of the header.
	 * 
	 * @return ActionForward-- Forwards to Homepage
	 */
	public ActionForward getHomePage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).debug(
				"getHomePage method is called");
		boolean isCenterHeirarchyExists = Configuration.getInstance()
				.getCustomerConfig(
						new OfficePersistence().getHeadOffice().getOfficeId())
				.isCenterHierarchyExists();
		request.getSession().setAttribute(
				BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
				isCenterHeirarchyExists ? Constants.YES : Constants.NO);
		CustomerSearchActionForm cusForm = (CustomerSearchActionForm) form;
		cusForm.setSearchNode("searchString", null);
		loadAllBranches(mapping, form, request, response);
		return mapping.findForward(CustomerConstants.GETHOMEPAGE_SUCCESS);
	}

	/**
	 * No need to override
	 */
	@Override
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside Load method of CustomerSearchAction");
		CustomerSearchActionForm cusForm = (CustomerSearchActionForm) form;
		cusForm.setSearchNode("searchString", null);
		ActionForward actionForward = null;
		delegateTo(mapping, form, request, response);
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside Load method of CustomerSearchAction After delegate");
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Short loadFordward = (Short) context
				.getBusinessResults(CustomerSearchConstants.LOADFORWARD);
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside Load method of CustomerSearchAction loadFordward"
						+ loadFordward);
		if (loadFordward.equals(CustomerSearchConstants.LOADFORWARDLOANOFFICER)) {
			actionForward = mapping
					.findForward(CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS);
		} else if (loadFordward
				.equals(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER)) {
			actionForward = mapping
					.findForward(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS);
		} else {
			actionForward = mapping
					.findForward(CustomerSearchConstants.LOADFORWARDOFFICE_SUCCESS);
		}
		return actionForward;

	}

	public ActionForward customGet(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping
				.findForward(CustomerSearchConstants.LOADFORWARDLOANOFFICER_SUCCESS);
	}

	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping
				.findForward(CustomerSearchConstants.LOADFORWARDNONLOANOFFICER_SUCCESS);
	}

	public ActionForward getOfficeHomePage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);

		Short loggedUserLevel = context.getUserContext().getLevelId();
		if (loggedUserLevel.shortValue() == PersonnelConstants.LOAN_OFFICER
				.shortValue()) {
			// context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDLOANOFFICER);
			return load(mapping, form, request, response);
		} else {
			// context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDNONLOANOFFICER);
			return preview(mapping, form, request, response);
		}

	}

	/**
	 * It returns an action forward based on the searchParameter coming from the
	 * UI which can be obatined from the search node map.
	 */
	public ActionForward customSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(CustomerSearchConstants.SEARCH_SUCCESS);

	}

	protected void delegateTo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(ResourceConstants.LOAD);
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside delegateTo method of CustomerSearchAction ");
		delegate(context, request);
	}

	public ActionForward loadAllBranches(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		CustomerSearchBusinessProcessor customerSearchBusinessProcessor = new CustomerSearchBusinessProcessor();
		customerSearchBusinessProcessor.loadInitial(context);
		return mapping
				.findForward(CustomerSearchConstants.LOADALLBRANCHES_SUCCESS);

	}
}

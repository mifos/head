/**

 * OfficeAction.java    version: 1.0

 

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
package org.mifos.application.office.struts.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.office.struts.actionforms.OfficeActionForm;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.application.office.util.valueobjects.OfficeLevelView;
import org.mifos.application.office.util.valueobjects.ParentOffice;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.Context;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

/**
 * OfficeAction is the class which encapsulates all the struts action for the
 * office module
 * 
 * @author rajenders
 * 
 */
public class OfficeAction extends MifosBaseAction {

	// logger for the officeaction

	MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.OFFICELOGGER);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	@Override
	public Map<String, String> appendToMap() {

		Map<String, String> methodHashMap = new HashMap<String, String>();

		methodHashMap.put(OfficeConstants.LOADPARENT,
				OfficeConstants.LOADPARENT);
		methodHashMap.put(OfficeConstants.LOADALL, OfficeConstants.LOADALL);

		return methodHashMap;
	}

	/**
	 * This method is called to set the proper forward based upon the values of
	 * from where we are coming
	 * 
	 * @param mapping
	 *            ActionMapping object
	 * @param form
	 *            ActionForm object
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// managePreview_success
		logger.debug("Inside CustomPreview .....");
		ActionForward forward = null;
		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();
		if (fromPage.equalsIgnoreCase(OfficeConstants.MANAGEEDIT)) {

			// if we are coming from manage page then we want to go to the
			// managepreview page
			forward = mapping.findForward(OfficeConstants.MANAGEPREVIEWSUCESS);

		}

		return forward;
	}

	/**
	 * This method is called to set the proper forward based upon the values of
	 * from where we are coming
	 * 
	 * @param mapping
	 *            ActionMapping object
	 * @param form
	 *            ActionForm object
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// managePreview_success
		ActionForward forward = null;

		logger.debug("inside custom previous method");

		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();
		logger.debug("the value of input is " + fromPage);
		if (fromPage.equalsIgnoreCase(OfficeConstants.MANAGE)) {

			// if we are coming from the manage page then we need to got to the
			// manage previous
			forward = mapping
					.findForward(OfficeConstants.FORWARDMANAGEPREVOISSUCESS);

		}
		return forward;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#previous(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loadParentPrevousHelper(mapping, form, request, response);
		return super.previous(mapping, form, request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {

		return OfficeConstants.OFFICE_DEPENDENCY_NAME;
	}

	/**
	 * This function is called when user press the cancel button on any page
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = null;
		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();

		// deciding forward page
		if (OfficeConstants.CREATE.equalsIgnoreCase(fromPage)) {
			forward = OfficeConstants.FORWARDCREATECANCEL;
		} else if (OfficeConstants.MANAGEEDIT.equalsIgnoreCase(fromPage)) {
			forward = OfficeConstants.FORWARDGETSUCESS;
		}
		return mapping.findForward(forward);
	}

	/**
	 * This is custom action which is called when we select the office type then
	 * parent selectbox is filled with propervalues
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward loadParent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = OfficeConstants.FORWARDLOADPARENTSUCESS;

		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();

		// this the function where we are loading the parent map into the
		// request
		loadParentPrevousHelper(mapping, form, request, response);
		if (fromPage.equalsIgnoreCase(OfficeConstants.MANAGE)
				|| fromPage.equalsIgnoreCase(OfficeConstants.MANAGEEDIT)) {
			forward = OfficeConstants.FORWARDMANAGEGETSUCESS;

		}

		return mapping.findForward(forward);
	}

	/**
	 * This function loads the parent offices for a perticular level
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	private void loadParentPrevousHelper(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		// get the proper values form the form

		OfficeActionForm oaf = (OfficeActionForm) form;

		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();

		// get the select values from the form
		String officeType = oaf.getFormOfficeType();

		Short selectedLevel = null;
		if (null != officeType && officeType.equals("")) {

			try {
				selectedLevel = Short.valueOf(officeType);
			} catch (NumberFormatException exp) {
				// do nothing if nothing got selected default is there
			}

		}
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.removeAttribute(OfficeConstants.PARENTS);
		Office office = (Office) context.getValueObject();

		if (null == selectedLevel) {
			logger.debug("value of Office is " + office.toString());

			// try to get it from the value object
			if (null != office) {
				if (null != office.getLevel()) {
					selectedLevel = office.getLevel().getLevelId();
				}
			}

		}

		// show the proper list for parent or let the default list
		// be there based on the default value

		if (null != selectedLevel && selectedLevel.shortValue() > 0) {

			logger.debug("value of selectedLevel is " + selectedLevel);

			OfficeLevelView olv = (OfficeLevelView) context
					.getSearchResultBasedOnName(
							OfficeConstants.PARENTOFFICESMAP).getValue();
			List parentList = (List) olv.getParentOffices(selectedLevel);

			if (null != parentList) {

				// we need to update this list if we are on update page

				if (fromPage.equalsIgnoreCase(OfficeConstants.MANAGEEDIT)) {

					try {
						Short officeId = Short.valueOf(oaf.getOfficeId());
					} catch (ParseException e) {
						// TODO how to handle this
					}
					for (int i = 0; i < parentList.size(); i++) {

						ParentOffice po = (ParentOffice) parentList.get(i);

						if (po.getOfficeId() == office.getOfficeId()
								.shortValue())

						{
							parentList.remove(i);
							break;

						}

					}

				}
				if (null != request.getSession().getAttribute(
						OfficeConstants.PARENTS)) {
					request.getSession().removeAttribute(
							OfficeConstants.PARENTS);
				} else if (null != request
						.getAttribute(OfficeConstants.PARENTS)) {
					request.removeAttribute(OfficeConstants.PARENTS);
				}
				request.getSession().setAttribute(OfficeConstants.PARENTS,
						parentList);
			}

		}

	}

	/*
	 * bug 26496 added methodName.equals("cancel") in if condition
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#isActionFormToValueObjectConversionReq(java.lang.String)
	 */
	@Override
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {

		logger
				.debug("Inside the method isActionFormToValueObjectConversionReq.........");
		logger.debug("value of method is " + methodName);

		if (null != methodName
				&& (methodName.equals("create") || methodName.equals("update")
						|| methodName.equals("manage") || methodName
						.equals("cancel"))) {

			return false;
		} else {
			return true;
		}

	}

	/**
	 * This function is called to handle the show office page ,it will load all
	 * the data we need to show in context
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward loadall(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = OfficeConstants.FORWARDLOADALL;

		// OfficeDAO officedao = new OfficeDAO();
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		/*
		 * List tillOfficeLevel = officedao.getTillBranchOffice();
		 * OfficeHelper.saveInContext(
		 * OfficeConstants.OFFICESTILLBRANCHOFFICESLIST, tillOfficeLevel,
		 * context); List branchOffices = officedao.getBranchOffice();
		 * OfficeHelper.saveInContext(OfficeConstants.OFFICESBRANCHOFFICESLIST,
		 * branchOffices, context);
		 */
		if (null != context) {
			context.setBusinessAction(OfficeConstants.LOADALL);
			delegate(context, request);
		}
		return mapping.findForward(forward);
	}

	/**
	 * This function is called when user press the create new office link or
	 * button
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		OfficeActionForm oaf = (OfficeActionForm) form;
		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Office office = (Office) context.getValueObject();
		oaf.cleanForm();
		OfficeLevel ol = office.getLevel();

		// we want to set this if we are coming from search page
		if (null != fromPage
				&& fromPage.equalsIgnoreCase(OfficeConstants.SEARCH)) {

			if (null != ol) {
				Short level = ol.getLevelId();
				if (null != level) {
					try {
						oaf.setFormOfficeType(String
								.valueOf(level.shortValue()));
					} catch (NumberFormatException e) {
						// TODO what to do here
						// let it be like this as user may not see that
						// correct value in the office type box but anyway he
						// can choose
						// it again and create the correct office type
					}

				}

			}
		}
		return null;
	}

	/**
	 * This function is called by framework when validation is called
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		OfficeActionForm officeActionForm = (OfficeActionForm) form;
		String fromPage = officeActionForm.getInput();

		// deciding forward page
		if (OfficeConstants.MANAGEEDIT.equalsIgnoreCase(fromPage)) {
			forward = OfficeConstants.FORWARDMANAGEGETSUCESS;
		}
		return mapping.findForward(forward);

	}

}

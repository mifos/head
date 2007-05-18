/**

 * MifosRequestProcessor.java    version: 1.0



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

package org.mifos.framework.struts.action;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.tiles.TilesRequestProcessor;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PreviousRequestValues;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

public class MifosRequestProcessor extends TilesRequestProcessor {

	private ActivityContext setActivityContextFromRequest(
			HttpServletRequest request, Short activityId) {
		HttpSession session = request.getSession();
		ActivityContext activityContext = (ActivityContext) session
				.getAttribute("ActivityContext");
		if (activityContext != null) {
			// get the values from the request
			String recordOfficeId = request.getParameter("recordOfficeId");
			String recordLoanOfficerId = request
					.getParameter("recordLoanOfficerId");
			short recordOffId;
			short recordLoOffId;
			try {
				recordOffId = Short.valueOf(recordOfficeId).shortValue();
				recordLoOffId = Short.valueOf(recordLoanOfficerId)
						.shortValue();
			} catch (ParseException e) {
				throw new RuntimeException(e);
			} catch (NumberFormatException e) {
				/* Can happen if one or both parameters was omitted.
				   Do we want to allow this case?  (If so, we can
				   check for it more cleanly).  What's the difference
				   between supplying these as parameters versus the
				   UserContext, versus just using what is in
				   the ActivityContext? */
				throw new RuntimeException(e);
			}
			if (recordOffId > 0 && recordLoOffId > 0) {
				activityContext.setRecordOfficeId(recordOffId);
				activityContext.setRecordLoanOfficer(recordLoOffId);
			}
			else if (recordOffId == 0 && recordLoOffId == 0) {

				if (session.getAttribute("UserContext") != null) {
					UserContext uc = (UserContext) session
							.getAttribute("UserContext");

					activityContext.setRecordOfficeId(uc.getBranchId());
					activityContext.setRecordLoanOfficer(uc.getId());
				}

			}
			activityContext.setActivityId(activityId);
			return activityContext;
		}
		else {
			// No activity context
			// TODO: Can this happen? Why? Is null right?
			return null;
		}
	}

	protected boolean checkProcessRoles(HttpServletRequest request,
			HttpServletResponse response, ActionMapping mapping)
			throws IOException, ServletException {
		boolean returnValue = true;
		if (request.getSession() != null
				&& request.getSession().getAttribute("UserContext") != null)

		{
			HttpSession session = request.getSession();
			ActivityMapper activityMapper = ActivityMapper.getInstance();
			String path = mapping.getPath();
			String method = request.getParameter("method");
			String key = path + "-" + method;
			Short activityId = null;
			if (null != method
					&& (method.equals("cancel") || method.equals("validate")
							|| method.equals("searchPrev") || method
							.equals("searchNext")))
				return true;
			else {
				activityId = activityMapper.getActivityId(key);
				request.setAttribute(Globals.ERROR_KEY, null);
				String activityKey = null;

				// if it is null Check for report actions

				if (null == activityId) {
					activityKey = path + "-" + request.getParameter("viewPath");
					activityId = activityMapper.getActivityId(activityKey);
				}
				// Check for fingrained permissions
				if (null == activityId) {
					activityKey = key
							+ "-"
							+ session
									.getAttribute(SecurityConstants.SECURITY_PARAM);
					activityId = activityMapper.getActivityId(activityKey);
				}
				if (null == activityId)
					return false;
				else if (activityId.shortValue() == 0)
					return true;
			}
			returnValue = AuthorizationManager.getInstance().isActivityAllowed(
					(UserContext) session.getAttribute("UserContext"),
					setActivityContextFromRequest(request, activityId));
		}
		return returnValue;
	}

	/**
	 * This method is overridden because in case of exception we need to
	 * populate the request with the old values so that when the user goes back
	 * to the previous page it has all the values in the request.For this we
	 * create an object which will store the values of previous request in case
	 * the request is successful and if there is an exception it reads values
	 * from that object and context and dups all in the request.
	 */
	@Override
	protected ActionForward processActionPerform(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm form,
			ActionMapping mapping) throws IOException, ServletException {

		ActivityContext activityContext = null;
		ActionForward forward = null;
		HttpSession session = request.getSession();

		// gets the object where we will store values from request.
		PreviousRequestValues previousRequestValues = (PreviousRequestValues) session
				.getAttribute(Constants.PREVIOUS_REQUEST);
		if (null == previousRequestValues) {
			previousRequestValues = new PreviousRequestValues();
			session.setAttribute(Constants.PREVIOUS_REQUEST,
					previousRequestValues);
		}

		// getting the activity context from the session
		activityContext = (ActivityContext) session
				.getAttribute("ActivityContext");

		// check if the action desired is permissible or not.
		// if allowed invoke the execute method of the action class

		try {
			String currentFlowKey = request.getParameter(Constants.CURRENTFLOWKEY);
			if(currentFlowKey != null) {
				previousRequestValues.getPreviousRequestValueMap().put(Constants.CURRENTFLOWKEY, currentFlowKey);
			}
					
			forward = (action.execute(mapping, form, request, response));
			
			String method = request.getParameter("method");
			if (method.equals(ClientConstants.METHOD_RETRIEVE_PICTURE)) {
				forward = mapping.findForward("get_success");
			}

			// set the last forward in the activity context
			if (activityContext != null)
				activityContext.setLastForward(forward);

			// read the request and add the values to the
			// PreviousRequestValues object.
			// this will set every thing in the request apart from context
			// and value object.
			Enumeration requestAttributes = request.getAttributeNames();
			while (requestAttributes.hasMoreElements()) {
				String nextName = (String) requestAttributes.nextElement();
				if (nextName.startsWith(Constants.STORE_ATTRIBUTE)
						|| nextName.equalsIgnoreCase(Constants.CURRENTFLOWKEY)) {
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER)
							.debug(
									"The attribute name is  " + nextName
											+ " and value is : "
											+ request.getAttribute(nextName));
					previousRequestValues.getPreviousRequestValueMap().put(
							nextName, request.getAttribute(nextName));
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
			forward = (processException(request, response, e, form, mapping));
			// set the last forward in the activity context
			if (activityContext != null)
				activityContext.setLastForward(forward);
			populateTheRequestFromPreviousValues(request, previousRequestValues);

		} finally {
			try {
				session.removeAttribute(SecurityConstants.SECURITY_PARAM);
			} catch (Exception e) {
			}
		}

		return forward;
	}

	private void populateTheRequestFromPreviousValues(
			HttpServletRequest request,
			PreviousRequestValues previousRequestValues) {
		// also read the previous request values map and put things in
		// request.
		Set<String> keySet = previousRequestValues.getPreviousRequestValueMap()
				.keySet();
		if (null != keySet) {
			for (String key : keySet) {
				request.setAttribute(key, previousRequestValues
						.getPreviousRequestValueMap().get(key));
			}
		}

	}

	@Override
	protected boolean processRoles(HttpServletRequest request,
			HttpServletResponse response, ActionMapping mapping)
			throws IOException, ServletException {
		HttpSession session = request.getSession();

		PreviousRequestValues previousRequestValues = (PreviousRequestValues) session
				.getAttribute(Constants.PREVIOUS_REQUEST);
		if (null == previousRequestValues) {
			previousRequestValues = new PreviousRequestValues();
			session.setAttribute(Constants.PREVIOUS_REQUEST,
					previousRequestValues);
		}
		if (!checkProcessRoles(request, response, mapping)) {

			ActionErrors error = new ActionErrors();
			error.add(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED,
					new ActionMessage(
							SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED));
			request.setAttribute(Globals.ERROR_KEY, error);
			ActivityContext activityContext = (ActivityContext) request
					.getSession().getAttribute("ActivityContext");
			populateTheRequestFromPreviousValues(request, previousRequestValues);
			processForwardConfig(request, response, activityContext
					.getLastForward());
			return false;
		}
		return true;
	}

}

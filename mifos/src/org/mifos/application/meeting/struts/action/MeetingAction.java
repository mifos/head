/**

 * MeetingAction.java    version: 1.0

 

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
package org.mifos.application.meeting.struts.action;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.PathConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.struts.actionforms.MeetingActionForm;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.office.util.helpers.OfficeHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * @author rajenders
 * 
 */
public class MeetingAction extends MifosBaseAction {

	// get the logger for logging
	MifosLogger meetingLogger = MifosLogManager
			.getLogger(LoggerConstants.MEETINGLOGGER);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	@Override
	public Map<String, String> appendToMap() {
		Map<String, String> methodHashMap = new HashMap<String, String>();

		methodHashMap.put(MeetingConstants.LOADMEETING,
				MeetingConstants.LOADMEETING);

		return methodHashMap;
	}

	protected boolean performCleanUp() {

		meetingLogger.info("performCleanUp returning the value as false");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {

		return MeetingConstants.MEETINGDEPENDENCY;
	}

	/**
	 * This method select the proper forward if user select the cancel button
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MeetingActionForm maf = (MeetingActionForm) form;
		String forward = forwardHelper(maf);
		meetingLogger.info("Forwarding the action to =" + forward);
		return mapping.findForward(forward);
	}

	/**
	 * This function is called to set the defaults before loading the create
	 * page
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

		
		MeetingActionForm maf = (MeetingActionForm) form;
		maf.clearForm();
		Meeting meeting =getMeetingFromContext(request,form);
		Object obj =null;
		//Code added for M2 Center.
		if(meeting == null){
			if (maf.input.equalsIgnoreCase(MeetingConstants.CLIENT)) {
				obj = SessionUtils.getAttribute(ClientConstants.CLIENT_MEETING, request.getSession());
			}
			else if (maf.input.equalsIgnoreCase(MeetingConstants.CENTER)) {
				obj = SessionUtils.getAttribute(CenterConstants.CENTER_MEETING, request.getSession());
			}
			if(obj!=null)
				meeting = MeetingHelper.convertMeetingM2toM1((MeetingBO)obj);
		}
		// Code for M2 center ends
		
		if (null !=meeting ) {
			
			initActionForm(meeting,form);
		} else {

			meetingLogger.info("Setting the frequency to ="
					+ MeetingConstants.WEEKLY);

			maf.setFrequency(MeetingConstants.WEEKLY);
		}
		return null;
	}

	/**
	 * This function is called to set the proper value for frequency if
	 * validation fails
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

		MeetingActionForm maf = (MeetingActionForm) form;
		String frequency = maf.getFrequency();
		if (frequency != null) {
			meetingLogger.info("Setting the frequency to =" + frequency);

			maf.setFrequency(frequency);
		} else {
			meetingLogger.info("Setting the frequency to ="
					+ MeetingConstants.WEEKLY);

			maf.setFrequency(MeetingConstants.WEEKLY);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#get(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward af = super.get(mapping, form, request, response);
		 Context ctx = (Context) request.getAttribute(Constants.CONTEXT);
		Meeting meeting = (Meeting) ctx.getValueObject();
		if (null != meeting) {
			meetingLogger.info("Initializing the meetingActionForm ...");
			initActionForm(meeting, form);
		} else {
			throw new MeetingException(MeetingConstants.KEYLOADFAILED);
		}

		return af;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#create(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		MeetingActionForm maf = (MeetingActionForm) form;
		Context ctx = (Context) request.getAttribute(Constants.CONTEXT);
		Meeting meeting = (Meeting) ctx.getValueObject();

		// set the dates
		Calendar calender = new GregorianCalendar();
		calender.setTimeInMillis(System.currentTimeMillis());
		meeting.setMeetingStartDate(calender);
		meeting.setMeetingStartTime(calender);

		if (null != maf.input) {

			meetingLogger.info("We are coming from " + maf.input);

			if (maf.input.equalsIgnoreCase(MeetingConstants.GROUP)) {
				setInContext(GroupConstants.GROUP_ACTION, request.getSession(),
						meeting);
				forward = MeetingConstants.FORWARD_GROUP_SUCESS;
			} else if (maf.input.equalsIgnoreCase(MeetingConstants.CLIENT)) {
				setInContext(PathConstants.CLIENT_CREATION, request
						.getSession(), meeting);
				//forward = "clientCreate_success";
				forward = MeetingConstants.FORWARD_CLIENT_SUCESS;
			} else if (maf.input.equalsIgnoreCase(MeetingConstants.CENTER)) {
				setInContext(
						org.mifos.application.customer.center.util.helpers.PathConstants.CENTER,
						request.getSession(), meeting);
				forward = MeetingConstants.FORWARD_CENTER_SUCESS;
			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.CENTER_DETAILS)) {
				setInContext(
						org.mifos.application.customer.center.util.helpers.PathConstants.CENTER,
						request.getSession(), meeting);
				forward = MeetingConstants.FORWARD_EDIT_CENTER_MEETING_SUCESS;
			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.CLIENT_DETAILS)) {
				setInContext(PathConstants.CLIENT_CREATION, request
						.getSession(), meeting);
				forward = MeetingConstants.FORWARD_EDIT_CLIENT_MEETING_SUCESS;
			}
		}
		
		if (maf.input.equalsIgnoreCase(MeetingConstants.CLIENT)) {
			//Meeting is being stored in session for client create
			SessionUtils.setAttribute(ClientConstants.CLIENT_MEETING, MeetingHelper.convertMeetingM1oM2(meeting), request.getSession());
		}
		else{
			//Meeting is being stored in session for center create
			SessionUtils.setAttribute(CenterConstants.CENTER_MEETING, MeetingHelper.convertMeetingM1oM2(meeting), request.getSession());
		}
		meetingLogger.info("We are forwarding to " + forward);
		return mapping.findForward(forward);

	}

	private void setInContext(String actionPath, HttpSession session,
			Meeting meeting) {
		Enumeration keys = session.getAttributeNames();
		String attributeKey = null;
		if (null != keys) {
			while (keys.hasMoreElements()) {
				attributeKey = (String) keys.nextElement();
				if (attributeKey.equals(actionPath + "_" + Constants.CONTEXT)) {
					Context gpContext = (Context) session
							.getAttribute(attributeKey);
					gpContext.addBusinessResults(MeetingConstants.MEETING,
							meeting);
				}
			}// end-while
		}// end-if
	}

	/**
	 * This function initialize the action form based on the meeting value
	 * object
	 * 
	 * @param meeting
	 * @param form
	 */
	private void initActionForm(Meeting meeting, ActionForm form) {
		MeetingActionForm maf = (MeetingActionForm) form;

		MeetingDetails meetingDetails = meeting.getMeetingDetails();
		Short recurrenceId = null;
		if (null != meetingDetails) {
			RecurrenceType rt = meetingDetails.getRecurrenceType();
			if (null != rt) {
				recurrenceId = rt.getRecurrenceId();
				// Set the type of meeting in action form
				if (null != recurrenceId) {

					meetingLogger.info("Setting the recuranceId ="
							+ recurrenceId);

					maf.setFrequency(String.valueOf(recurrenceId));

				}

			}

		}

		// Get the meetingRecurrence
		MeetingRecurrence meetingRecurrence = meetingDetails
				.getMeetingRecurrence();
		// set the recur after
		Short recurAfter = meetingDetails.getRecurAfter();

		// set the location of meeting
		String meetingLocation = meeting.getMeetingPlace();
		if (null != meetingLocation) {
			meetingLogger.info("Setting the meetingLocation ="
					+ meetingLocation);

			maf.setMeetingPlace(meetingLocation);
		}

		// all other values we have to set based on the recurance type
		if (null != recurrenceId
				&& recurrenceId.shortValue() == MeetingConstants.WEEK)

		{

			if (null != recurAfter) {
				meetingLogger.info("Setting the recurAfter =" + recurAfter);

				maf.setRecurWeek(String.valueOf(recurAfter));
			}
			if (null != meetingRecurrence) {
				Short day = meetingRecurrence.getWeekDay();
				if (null != day) {
					meetingLogger.info("Setting the WeekDay =" + day);

					maf.setWeekDay(String.valueOf(day));
				}
			}
			// set all other fields as blank
			maf.setMonthDay("");
			maf.setMonthMonth("");
			maf.setMonthType("");
			maf.setMonthRank("");
			maf.setMonthMonthRank("");
			maf.setMonthWeek("");
		} else if (null != recurrenceId
				&& recurrenceId.shortValue() == MeetingConstants.MONTH)

		{

			// set week related data as null
			maf.setWeekDay("");
			maf.setRecurWeek("");
			Short day = meetingRecurrence.getWeekDay();
			Short rank = meetingRecurrence.getRankOfDays();
			Short dayNumber = meetingRecurrence.getDayNumber();
			if (null != dayNumber)

			{
				if (null != recurAfter)

				{
					meetingLogger.info("Setting the recurAfter =" + recurAfter);

					maf.setMonthMonth(String.valueOf(recurAfter));
				}

				maf.setMonthDay(String.valueOf(dayNumber));
				maf
						.setMonthType(String
								.valueOf(MeetingConstants.MONTHRECURDAY));

				maf.setMonthRank("");
				maf.setMonthMonthRank("");
				maf.setMonthWeek("");

			}

			else

			{
				maf.setMonthType(String
						.valueOf(MeetingConstants.MONTHRECURRANK));

				if (null != day) {
					maf.setMonthWeek(String.valueOf(day));

				}
				if (null != rank) {
					maf.setMonthRank(String.valueOf(rank));

				}
				if (null != recurAfter)

				{
					maf.setMonthMonthRank(String.valueOf(recurAfter));
				}

				maf.setMonthDay("");
				maf.setMonthMonth("");

			}

		}

	}

	// Do'nt go by name this function will actully help create customer meeting
	/**
	 * This function is customized to validate the creation of the customer
	 * meeting
	 */
	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		MeetingActionForm maf = (MeetingActionForm) form;
		String forward = forwardHelper(maf);

		// get the context from the request
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		if (null != context) {

			Customer cust = getCustomerFromTheContext(request, form);
			if (null != cust) {
				OfficeHelper.saveInContext(MeetingConstants.CUSTOMER, cust,
						context);
			} else {
				throw new MeetingException(MeetingConstants.KEYCREATEFAILED);
			}
		}
		meetingLogger.info("Forwarding the action to  =" + forward);
		return mapping.findForward(forward);

	}

	/**
	 * This function set the correct forward based on the input
	 * 
	 * @param maf
	 * @return
	 */
	private String forwardHelper(MeetingActionForm maf) {
		String forward = null;
		if (null != maf.input) {
			if (maf.input.equalsIgnoreCase(MeetingConstants.GROUP)) {
				forward = MeetingConstants.FORWARD_GROUP_SUCESS;
			} else if (maf.input.equalsIgnoreCase(MeetingConstants.CLIENT)) {
				forward = MeetingConstants.FORWARD_CLIENT_SUCESS;
			} else if (maf.input.equalsIgnoreCase(MeetingConstants.CENTER)) {
				forward = MeetingConstants.FORWARD_CENTER_SUCESS;
			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.CENTER_DETAILS)) {
				forward = MeetingConstants.FORWARD_EDIT_CENTER_MEETING_SUCESS;
			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.CLIENT_DETAILS)) {
				forward = MeetingConstants.FORWARD_EDIT_CLIENT_MEETING_SUCESS;
			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.GROUP_DETAILS)) {
				forward = MeetingConstants.FORWARD_GROUP_DETAILS_PAGE;
			}
		}

		return forward;
	}

	/**
	 * This function set the correct forward based on the input
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MeetingActionForm maf = (MeetingActionForm) form;
		String forward = forwardHelper(maf);
		meetingLogger.info("Forwarding the action to  =" + forward);
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Customer customer = getCustomerFromTheContext(request,form);
		if ( customer!=null)
		context.addBusinessResults("Customer",customer);
		else
		{
			 Object object= request.getSession().getAttribute("CustomerVO");
			 context.addBusinessResults("Customer",object);

		}
		return mapping.findForward(forward);
	}

	/**
	 * This function is called before we show the customermeeting create page to
	 * the user
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward loadMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// get the context from the request
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		MeetingActionForm maf = (MeetingActionForm) form;
		maf.clearForm();
		meetingLogger
				.info("Setting the frequency  =" + MeetingConstants.WEEKLY);

		maf.setFrequency(MeetingConstants.WEEKLY);
		if (null != context) {
			context.setBusinessAction(MeetingConstants.LOADMEETING);
			delegate(context, request);
		}

		return mapping
				.findForward(MeetingConstants.FORWARD_LOAD_MEETING_SUCESS);
	}

	/**
	 * This method is user to retrive the customer form the context while
	 * creating the customer meeting
	 * 
	 * @param request
	 * @param form
	 * @return
	 */
	private Customer getCustomerFromTheContext(HttpServletRequest request,
			ActionForm form) {
		MeetingActionForm maf = (MeetingActionForm) form;
		String actionPath = getActionPath(maf);
		
		HttpSession session = request.getSession();
		Enumeration keys = session.getAttributeNames();
		String attributeKey = null;
		if (null != keys) {
			while (keys.hasMoreElements()) {
				attributeKey = (String) keys.nextElement();
				if (attributeKey.equals(actionPath + "_" + Constants.CONTEXT)) {
					Context gpContext = (Context) session
							.getAttribute(attributeKey);
					return (Customer) gpContext.getValueObject();
				}
			}// end-while
		}// end-if
		return null;
	}
	
	private String getActionPath(MeetingActionForm maf)
	{
		String actionPath=null;
		if (null != maf.input) {
			if (maf.input.equalsIgnoreCase(MeetingConstants.GROUP)) {
				actionPath = GroupConstants.GROUP_ACTION;

			} else if (maf.input.equalsIgnoreCase(MeetingConstants.CLIENT)) {
				actionPath = PathConstants.CLIENT_CREATION;

			} else if (maf.input.equalsIgnoreCase(MeetingConstants.CENTER)) {
				actionPath = org.mifos.application.customer.center.util.helpers.PathConstants.CENTER;

			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.CENTER_DETAILS)) {
				actionPath = org.mifos.application.customer.center.util.helpers.PathConstants.CENTER;

			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.CLIENT_DETAILS)) {
				actionPath = PathConstants.CLIENT_CREATION;
			} else if (maf.input
					.equalsIgnoreCase(MeetingConstants.GROUP_DETAILS)) {
				actionPath = GroupConstants.GROUP_ACTION;
			}
		}
		return actionPath;
	}
	
	private Meeting getMeetingFromContext(HttpServletRequest request,
			ActionForm form )
	{
		MeetingActionForm maf = (MeetingActionForm) form;
		String actionPath = getActionPath(maf);
		
		HttpSession session = request.getSession();
		Enumeration keys = session.getAttributeNames();
		String attributeKey = null;
		if (null != keys) {
			while (keys.hasMoreElements()) {
				attributeKey = (String) keys.nextElement();
				if (attributeKey.equals(actionPath + "_" + Constants.CONTEXT)) {
					Context gpContext = (Context) session
							.getAttribute(attributeKey);
					return (Meeting) gpContext.getBusinessResults(MeetingConstants.MEETING);
				}
			}// end-while
		}// end-if
		return null;
		
	}
	
	
}

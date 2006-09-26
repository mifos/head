/**

 * ClientCreationAction.java    version: xxx



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

package org.mifos.application.customer.client.struts.action;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.bulkentry.util.helpers.BulkEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.struts.actionforms.ClientCreationActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.PathConstants;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerDetail;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerNameDetail;
import org.mifos.application.customer.util.valueobjects.CustomerSearchInput;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelMaster;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.MifosWizardAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class acts as Action for creating/updating a client.
 */
public class ClientCreationAction extends MifosWizardAction {

	/** An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CLIENTLOGGER);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */

	protected String getPath() {

		return PathConstants.CLIENT_CREATION;
	}

	/**
	 * Returns keyMethod map to be added to the keyMethodMap of the base Action
	 * class. This method is called from
	 * <code>getKeyMethodMap<code> of the base Action Class.
	 * The map returned from this method is appended to the map of the <code>getKeyMethodMap<code> in the base action class and that will form the complete map for that action.
	 * This map will have method names of extra methods to be called in case of ClientCreationAction.
	 * @return
	 */
	public Map<String, String> appendToMap() {
		Map<String, String> methodHashMap = new HashMap<String, String>();

		// TODO change this code------------------------
		methodHashMap.putAll(super.appendToMap());
		methodHashMap.put(ClientConstants.METHOD_PRELOAD,
				ClientConstants.METHOD_PRELOAD);
		methodHashMap.put(ClientConstants.METHOD_EDIT_PERSONAL_INFO,
				ClientConstants.METHOD_EDIT_PERSONAL_INFO);
		methodHashMap.put(ClientConstants.METHOD_EDIT_MFI_INFO,
				ClientConstants.METHOD_EDIT_MFI_INFO);
		methodHashMap.put(ClientConstants.METHOD_PREVIOUS_PERSONAL_INFO,
				ClientConstants.METHOD_PREVIOUS_PERSONAL_INFO);
		methodHashMap.put(ClientConstants.METHOD_PREVIOUS_MFI_INFO,
				ClientConstants.METHOD_PREVIOUS_MFI_INFO);
		methodHashMap.put(ClientConstants.METHOD_PREVIEW_PERSONAL_INFO,
				ClientConstants.METHOD_PREVIEW_PERSONAL_INFO);
		methodHashMap.put(ClientConstants.METHOD_PREVIEW_MFI_INFO,
				ClientConstants.METHOD_PREVIEW_MFI_INFO);
		methodHashMap.put(ClientConstants.METHOD_LOAD_STATUS,
				ClientConstants.METHOD_LOAD_STATUS);
		methodHashMap.put(ClientConstants.METHOD_LOAD_TRANSFER,
				ClientConstants.METHOD_LOAD_TRANSFER);
		methodHashMap.put(ClientConstants.METHOD_LOAD_BRANCH_TRANSFER,
				ClientConstants.METHOD_LOAD_BRANCH_TRANSFER);
		methodHashMap.put(ClientConstants.METHOD_LOAD_MEETING,
				ClientConstants.METHOD_LOAD_MEETING);
		methodHashMap.put(ClientConstants.METHOD_UPDATE_MEETING,
				ClientConstants.METHOD_UPDATE_MEETING);
		methodHashMap.put(ClientConstants.METHOD_RETRIEVE_PICTURE,
				ClientConstants.METHOD_RETRIEVE_PICTURE);
		methodHashMap.put(ClientConstants.METHOD_CHOOSE_OFFICE,
				ClientConstants.METHOD_CHOOSE_OFFICE);
		methodHashMap.put(ClientConstants.METHOD_RETRIEVE_PICTURE_PREVIEW,
				ClientConstants.METHOD_RETRIEVE_PICTURE_PREVIEW);
		methodHashMap.put(ClientConstants.METHOD_LOAD_HISTORICAL_DATA,
				ClientConstants.METHOD_LOAD_HISTORICAL_DATA);
		methodHashMap.put(ClientConstants.METHOD_UPDATE_MFI,
				ClientConstants.METHOD_UPDATE_MFI);
		methodHashMap.put(ClientConstants.METHOD_SET_DEFAULT_FORMEDBY,
				ClientConstants.METHOD_SET_DEFAULT_FORMEDBY);
		methodHashMap.put(ClientConstants.METHOD_SHOW_PICTURE,
				ClientConstants.METHOD_SHOW_PICTURE);
		methodHashMap.put(CustomerConstants.METHOD_GET_DETAILS,
				CustomerConstants.METHOD_GET_DETAILS);
		return methodHashMap;
	}

	protected void handleTransaction(ActionForm actionForm,
			HttpServletRequest request) throws SystemException,
			ApplicationException {
		String method = request.getParameter("method");
		if (method.equals("create")) {
			Context context = ((Context) SessionUtils.getContext(getPath(),
					request.getSession()));
			ClientCreationActionForm clientActionForm = (ClientCreationActionForm) actionForm;
			Client client = (Client) context.getValueObject();
			Short clientStatus = Short.valueOf(clientActionForm.getStatusId());
			if (clientActionForm.getIsClientUnderGrp() == Constants.YES) {
				SearchResults obj = context
						.getSearchResultBasedOnName("ParentGroup");
				Group group = (Group) obj.getValue();
				checkPermissionForCreate(clientStatus,
						context.getUserContext(), null, group.getOffice()
								.getOfficeId(), group.getPersonnel()
								.getPersonnelId());
			} else {
				if (client.getPersonnel() != null)
					checkPermissionForCreate(clientStatus, context
							.getUserContext(), null, client.getOffice()
							.getOfficeId(), client.getPersonnel()
							.getPersonnelId());
				else
					checkPermissionForCreate(clientStatus, context
							.getUserContext(), null, client.getOffice()
							.getOfficeId(), context.getUserContext().getId());
			}
		}
		super.handleTransaction(actionForm, request);
	}

	private boolean isPermissionAllowed(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId, boolean saveFlag) {
		if (saveFlag)
			return ActivityMapper.getInstance().isSavePermittedForCustomer(
					newState.shortValue(), userContext, recordOfficeId,
					recordLoanOfficerId);
		else
			return ActivityMapper.getInstance()
					.isStateChangePermittedForCustomer(
							newState.shortValue(),
							null != flagSelected ? flagSelected.shortValue()
									: 0, userContext, recordOfficeId,
							recordLoanOfficerId);
	}

	private void checkPermissionForCreate(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) throws SecurityException {
		if (!isPermissionAllowed(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId, true))
			throw new SecurityException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	/**
	 * No need to override.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = super.load(mapping, form, request, response);
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		List<CustomFieldDefinition> customFields = (List) context
				.getSearchResultBasedOnName(
						CustomerConstants.CUSTOM_FIELDS_LIST).getValue();
		for (int i = 0; i < customFields.size(); i++) {
			String defaultValue = customFields.get(i).getDefaultValue();
			if (defaultValue == null
					|| defaultValue.equals(CustomerConstants.BLANK)) {
				clientForm.getCustomField(i).setFieldValue("");
			} else {
				if (customFields.get(i).getFieldType() == CustomerConstants.DATE_FIELD_TYPE
						&& !ValidateMethods.isNullOrBlank(defaultValue)) {
					// SimpleDateFormat sdf =
					// (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT,
					// context.getUserContext().getMfiLocale());
					// String userfmt =
					// DateHelper.convertToCurrentDateFormat(((SimpleDateFormat)
					// sdf).toPattern());
					clientForm.getCustomField(i).setFieldValue(
							DateHelper.getUserLocaleDate(context
									.getUserContext().getMfiLocale(),
									defaultValue));

				} else
					clientForm.getCustomField(i).setFieldValue(
							customFields.get(i).getDefaultValue());
			}
		}
		return forward;
	}

	public ActionForward setDefaultFormedByPersonnel(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		String forward = ClientConstants.CLIENT_CREATE_MFI_PAGE;
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		clientForm.setCustomerFormedById(clientForm.getLoanOfficerId());

		return mapping.findForward(forward);
	}

	/**
	 * No need to override.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ClientConstants.CHOOSE_OFFICE_PAGE);
	}

	/**
	 * This method figures out whether clients can exist outside the group or
	 * not as that is a configurable item If the client is not supposed to exist
	 * outside the group that link will not be shown on the jsp page.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward preLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);

		HttpSession session = request.getSession();
		if (Configuration.getInstance().getCustomerConfig(
				context.getUserContext().getBranchId())
				.canClientExistOutsideGroup())
			session.setAttribute(CustomerConstants.GROUP_HIERARCHY_REQUIRED,
					CustomerConstants.NO);
		else
			session.setAttribute(CustomerConstants.GROUP_HIERARCHY_REQUIRED,
					CustomerConstants.YES);

		this.clearActionForm(form);
		Short officeId = context.getUserContext().getBranchId();
		CustomerSearchInput clientSearchInput = new CustomerSearchInput();
		clientSearchInput.setOfficeId(officeId);
		clientSearchInput
				.setCustomerInputPage(ClientConstants.INPUT_CREATE_CLIENT);
		SessionUtils.setAttribute(CustomerConstants.CUSTOMER_SEARCH_INPUT,
				clientSearchInput, request.getSession());
		return mapping.findForward(ClientConstants.PRELOAD_SUCCESS);
	}

	private boolean checkIfLsmOrRsm() {
		// return false;
		return ConfigurationConstants.IS_LSM;
	}

	/**
	 * This method checks if client is part of group or not based on a parameter
	 * passed in request from UI. It sets this information in session and
	 * context.This should be removed after client was successfully created.
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
		this.clearActionForm(form);
		boolean isCenterHeirarchyExists = Configuration.getInstance()
				.getCustomerConfig(
						new OfficePersistence().getHeadOffice().getOfficeId())
				.isCenterHierarchyExists();
		SessionUtils.setAttribute(BulkEntryConstants.ISCENTERHEIRARCHYEXISTS,
				isCenterHeirarchyExists ? Constants.YES : Constants.NO, request
						.getSession());
		return null;
	}

	public ActionForward customNext(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		clientForm.setCustomerFormedById(clientForm.getLoanOfficerId());
		return null;
	}

	/**
	 * It reads the ids set in the valueobject and gets the corresponding locale
	 * specific name from the master data in the context sets them as attributes
	 * in the request.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		HttpSession session = request.getSession();
		if (Configuration.getInstance().getCustomerConfig(
				context.getUserContext().getBranchId())
				.isPendingApprovalStateDefinedForClient() == true) {
			session.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
					CustomerConstants.YES);

		} else
			session.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
					CustomerConstants.NO);
		ClientCreationActionForm clientCreationForm = (ClientCreationActionForm) form;
		Client client = (Client) context.getValueObject();
		String forward = null;

		// obtaining the input page, so that the respective forward can be
		// decided
		String fromPage = clientCreationForm.getInput();

		// if the information coming from the create page then the list of
		// selected fees is set
		if (ClientConstants.INPUT_MFI_INFO.equals(fromPage)) {
			List<FeeMaster> fees = clientCreationForm.getSelectedFeeList();
			setFeeTypeList(context, fees);
			// checks if the trained checkbox was unchecked
			checkForTrainedChecked(request, clientCreationForm);
			// puts only those fees that have not been checked
			checkForFeesChecked(request, clientCreationForm, client);
		}

		// deciding forward page based on the input page
		if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage)
				|| ClientConstants.INPUT_MFI_INFO.equals(fromPage)) {
			this.handlePersonalInfoPreview(request, clientCreationForm);
			forward = ClientConstants.CLIENT_CREATE_PREVIEW_PAGE;

		}

		// if the input is from the edit personal info page
		else if (ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage)) {
			this.handlePersonalInfoPreview(request, clientCreationForm);
			forward = ClientConstants.CLIENT_EDIT_PREVIEW_PERSONAL_PAGE;
		}

		// if the preview is for edit mfi information then the details fo mfi
		// info entered is taken from the new object and
		// saved into the old client object in the session and this client
		// object is put into the context as the value object
		else if (ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage)) {
			Client oldClient = (Client) SessionUtils.getAttribute(
					ClientConstants.OLDCLIENT, request.getSession());
			Client changedClient = new CustomerHelper()
					.convertOldClientMFIDetails(oldClient, client);
			logger.debug("changedClient.getCustomerAddressDetail(): "
					+ changedClient.getCustomerAddressDetail()
							.getCustomerAddressId());
			context.setValueObject(changedClient);
			/** Checks for the trained status */
			Short isTrained = (Short) SessionUtils.getAttribute(
					CustomerConstants.IS_TRAINED, request.getSession());
			if (isTrained != null && isTrained.shortValue() == Constants.NO) {
				checkForTrainedChecked(request, clientCreationForm);

			}

			forward = ClientConstants.CLIENT_EDIT_PREVIEW_MFI_PAGE;
		}
		setPersonnelAndMeeting(request, clientCreationForm);
		return mapping.findForward(forward);
	}

	private void checkForFeesChecked(HttpServletRequest request,
			ClientCreationActionForm clientCreationForm, Client client) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		for (int i = 0; i < clientCreationForm.getAdminFeeList().size(); i++) {
			if (request.getParameter("adminFee[" + i + "].checkedFee") == null) {
				clientCreationForm.getAdminFeeList().get(i).setCheckedFee(
						Short.valueOf("0"));
				client.setSelectedFeeSet(formFeeSet(clientCreationForm
						.getAdminFeeList(), clientCreationForm
						.getSelectedFeeList(), context));
			}
		}

	}

	/**
	 * Method to reset values for trained checkbox
	 * 
	 * @param request
	 * @param clientCreationForm
	 * @param client
	 */
	private void checkForTrainedChecked(HttpServletRequest request,
			ClientCreationActionForm clientCreationForm) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client client = (Client) context.getValueObject();
		if (request.getParameter("trained") == null) {
			clientCreationForm.setTrained("0");
			client.setTrained(new Short("0"));
		}

	}

	/**
	 * Method to reset values for trained checkbox
	 * 
	 * @param request
	 * @param clientCreationForm
	 * @param client
	 */
	private void handlePersonalInfoPreview(HttpServletRequest request,
			ClientCreationActionForm clientCreationForm) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client client = (Client) context.getValueObject();
		// setting client display name
		String displayName = new CustomerHelper()
				.setDisplayNames(clientCreationForm.getCustomerNameDetail(0)
						.getFirstName(), clientCreationForm
						.getCustomerNameDetail(0).getMiddleName(),
						clientCreationForm.getCustomerNameDetail(0)
								.getSecondLastName(), clientCreationForm
								.getCustomerNameDetail(0).getLastName());
		client.setDisplayName(displayName);
		clientCreationForm.setDisplayName(displayName);
		// setting spouse/father display name
		String displaySpouseFatherName = new CustomerHelper()
				.setDisplayNames(clientCreationForm.getCustomerNameDetail(1)
						.getFirstName(), clientCreationForm
						.getCustomerNameDetail(1).getMiddleName(),
						clientCreationForm.getCustomerNameDetail(1)
								.getSecondLastName(), clientCreationForm
								.getCustomerNameDetail(1).getLastName());
		clientCreationForm.getCustomerNameDetail(1).setDisplayName(
				displaySpouseFatherName);
		clientCreationForm.setSpouseFatherDisplayName(displaySpouseFatherName);
		request.setAttribute(ClientConstants.SPOUSE_FATHER_NAME_VALUE,
				displaySpouseFatherName);
		// setting display address
		String displayAddress = new CustomerHelper()
				.getDisplayAddress(clientCreationForm
						.getCustomerAddressDetail());
		client.setDisplayAddress(displayAddress);
		int age = new CustomerHelper().calculateAge(client.getDateOfBirth());
		clientCreationForm.setAge(age);
		request.setAttribute("age", age);
	}

	/**
	 * Method to set loan office and meeting values for client
	 * 
	 * @param request
	 * @param clientCreationForm
	 * @param client
	 */
	private void setPersonnelAndMeeting(HttpServletRequest request,
			ClientCreationActionForm clientCreationForm) {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client client = (Client) context.getValueObject();
		// obtaining the input page, so that the respective forward can be
		// decided
		String fromPage = clientCreationForm.getInput();
		// get selected loan officer name and set in client value object
		if (ClientConstants.INPUT_MFI_INFO.equals(fromPage)) {
			if (!ValidateMethods.isNullOrBlank(clientCreationForm
					.getCustomerFormedById())) {
				Personnel loanOfficer = new CustomerHelper().getFormedByLO(
						context, Short.parseShort(clientCreationForm
								.getCustomerFormedById()));
				client.setCustomerFormedByPersonnel(loanOfficer);
			} else {
				client.setCustomerFormedByPersonnel(null);

			}
		}

		if (clientCreationForm.getIsClientUnderGrp() != ClientConstants.CLIENT_BELONGS_TO_GROUP
				&& (ClientConstants.INPUT_MFI_INFO.equals(fromPage) || ClientConstants.INPUT_EDIT_MFI_INFO
						.equals(fromPage))) {
			logger
					.debug("LOAN OFF ID:"
							+ clientCreationForm.getLoanOfficerId());
			if (!ValidateMethods.isNullOrBlank(clientCreationForm
					.getLoanOfficerId())) {
				Personnel loanOfficer = getLO(context, Short
						.parseShort(clientCreationForm.getLoanOfficerId()));
				client.setPersonnel(loanOfficer);
				logger.debug("LOAN OFF ID in VO:"
						+ ((Client) context.getValueObject()).getPersonnel()
								.getDisplayName());
			} else {
				client.setPersonnel(null);

			}
			client.setParentCustomer(null);
		}
		// if the client belongs to a group then set the personnel of the group
		// as the personnel of the client
		else if (clientCreationForm.getIsClientUnderGrp() == ClientConstants.CLIENT_BELONGS_TO_GROUP
				&& ClientConstants.INPUT_MFI_INFO.equals(fromPage)) {
			client.setParentCustomer((Customer) context
					.getSearchResultBasedOnName("ParentGroup").getValue());
			request.setAttribute("parentStatus", client.getParentCustomer()
					.getStatusId());
			client.setPersonnel(client.getParentCustomer().getPersonnel());
			if (client.getParentCustomer().getCustomerMeeting() != null) {

				client.getCustomerMeeting().setMeeting(
						client.getParentCustomer().getCustomerMeeting()
								.getMeeting());
			}
			client.setOffice(client.getParentCustomer().getOffice());
		} else if (clientCreationForm.getIsClientUnderGrp() == ClientConstants.CLIENT_BELONGS_TO_GROUP
				&& ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage)) {
			/*
			 * If client belongs to group adn if group changes its loan officer
			 * Condition 1: Group didnt have loan officer initially when the
			 * client was being created Condition 2: GRoup's loan officer
			 * changes Condition 3. Group had loan officer and now it has been
			 * made null
			 * 
			 */
			if ((client.getPersonnel() == null)
					|| (!ValidateMethods.isNullOrBlank(clientCreationForm
							.getLoanOfficerId()) && client.getPersonnel()
							.getPersonnelId() != Short
							.valueOf(clientCreationForm.getLoanOfficerId()))) {
				logger.debug("LOAN OFF ID:"
						+ clientCreationForm.getLoanOfficerId());
				if (!ValidateMethods.isNullOrBlank(clientCreationForm
						.getLoanOfficerId())) {
					Personnel loanOfficer = new Personnel();
					loanOfficer.setPersonnelId(Short.valueOf(clientCreationForm
							.getLoanOfficerId()));
					loanOfficer.setDisplayName(client.getParentCustomer()
							.getPersonnel().getDisplayName());
					loanOfficer.setVersionNo(client.getParentCustomer()
							.getPersonnel().getVersionNo());
					client.setPersonnel(loanOfficer);
					logger.debug("LOAN OFF ID in VO:"
							+ ((Client) context.getValueObject())
									.getPersonnel().getDisplayName());
				}
			} else if (client.getParentCustomer().getPersonnel() == null) {
				client.setPersonnel(null);
			}
		}

	}

	/**
	 * This method is called on create method It chooses forwards for previous
	 * page based on input page
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		// set version number of fees
		ClientCreationActionForm actionForm = (ClientCreationActionForm) form;
		List<FeeMaster> adminFeeList = actionForm.getAdminFeeList();
		List<FeeMaster> selectedFeeList = actionForm.getSelectedFeeList();

		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client client = (Client) context.getValueObject();
		client.setSelectedFeeSet(formFeeSet(adminFeeList, selectedFeeList,
				context));
		// client.setCustomerMeeting(actionForm.getCustomerMeeting());

		return null;
	}

	/**
	 * This method is called on create method It does the explicit cleanup
	 * required after gorup creation
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward = super.create(mapping, form, request, response);
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		// set meeetings to null, after group is created
		context.addBusinessResults(MeetingConstants.MEETING, null);
		return forward;
	}

	/**
	 * Helper method used to form feeSet to be set in the valueObject
	 * 
	 * @param selectedFeeIdList
	 * @param selectedFeeAmntList
	 * @return
	 */
	private Set formFeeSet(List<FeeMaster> adminFeeList,
			List<FeeMaster> selectedFeeList, Context context) {
		Set<AccountFees> accountFeeSet = new HashSet<AccountFees>();
		List<FeeMaster> adminFeeMaster = ((List) context
				.getSearchResultBasedOnName(CenterConstants.ADMIN_FEES_LIST)
				.getValue());
		List<FeeMaster> selectedFeeMaster = ((List) context
				.getSearchResultBasedOnName(CenterConstants.FEES_LIST)
				.getValue());

		// put administrative fees in set if any
		for (int index = 0; index < adminFeeList.size(); index++) {
			if (adminFeeList.get(index).getCheckedFee() != null) {
				if (adminFeeList.get(index).getCheckedFee().shortValue() != 1) {
					for (int i = 0; i < adminFeeMaster.size(); i++) {
						if (adminFeeMaster.get(i).getFeeId().shortValue() == adminFeeList
								.get(index).getFeeId()) {
							AccountFees accountFees = new AccountFees();
							Fees fee = new Fees();
							fee.setFeeId(adminFeeList.get(index).getFeeId());
							fee.setVersionNo(adminFeeMaster.get(i)
									.getVersionNo());
							// fee.setRateOrAmount(adminFeeList.get(index).getRateOrAmount());
							accountFees.setFees(fee);
							accountFees.setAccountFeeAmount(new Money(
									Configuration.getInstance()
											.getSystemConfig().getCurrency(),
									adminFeeList.get(index).getRateOrAmount()));
							accountFees.setFeeAmount(adminFeeList.get(index)
									.getRateOrAmount());
							accountFeeSet.add(accountFees);
							break;
						}
					}
				}
			}
		}
		// put additional fees in set if any
		for (int index = 0; index < selectedFeeList.size(); index++) {
			if (selectedFeeList.get(index).getFeeId() != null) {
				if (selectedFeeList.get(index).getFeeId().shortValue() != 0) {
					for (int i = 0; i < selectedFeeMaster.size(); i++) {
						if (selectedFeeMaster.get(i).getFeeId().shortValue() == selectedFeeList
								.get(index).getFeeId()) {
							AccountFees accountFees = new AccountFees();
							Fees fee = new Fees();
							fee.setFeeId(selectedFeeList.get(index).getFeeId());
							fee.setVersionNo(selectedFeeMaster.get(i)
									.getVersionNo());
							// fee.setRateOrAmount(selectedFeeList.get(index).getRateOrAmount());
							accountFees.setFees(fee);
							accountFees.setAccountFeeAmount(new Money(
									Configuration.getInstance()
											.getSystemConfig().getCurrency(),
									selectedFeeList.get(index)
											.getRateOrAmount()));
							accountFees.setFeeAmount(selectedFeeList.get(index)
									.getRateOrAmount());
							accountFeeSet.add(accountFees);
							break;
						}
					}
				}
			}
		}

		return accountFeeSet;
	}

	/**
	 * No need to override.
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#get(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward = super.get(mapping, form, request, response);
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		HttpSession session = request.getSession();
		session.removeAttribute("age");
		SessionUtils.setAttribute(CustomerConstants.LINK_VALUES,
				(LinkParameters) context
						.getBusinessResults(CustomerConstants.LINK_VALUES),
				session);
		request
				.setAttribute(
						ClientConstants.SPOUSE_FATHER_VALUE,
						((Short) context
								.getBusinessResults(ClientConstants.SPOUSE_FATHER_VALUE))
								.shortValue());
		request
				.setAttribute(
						ClientConstants.SPOUSE_FATHER_NAME_VALUE,
						(String) context
								.getBusinessResults(ClientConstants.SPOUSE_FATHER_NAME_VALUE));
		// set user age
		SessionUtils.setAttribute("age", new CustomerHelper()
				.calculateAge(((Client) context.getValueObject())
						.getDateOfBirth()), session);
		if (checkIfLsmOrRsm() == true) {
			session.setAttribute(CustomerConstants.CONFIGURATION_LSM,
					CustomerConstants.YES);
		} else
			session.setAttribute(CustomerConstants.CONFIGURATION_LSM,
					CustomerConstants.NO);
		SessionUtils.setAttribute("noPictureOnGet", context
				.getBusinessResults("noPictureOnGet"), request.getSession());
		Integer customerId = ((Client) context.getValueObject())
				.getCustomerId();
		ClientBO clientBO = (ClientBO) new CustomerBusinessService()
				.getCustomer(customerId);
		ClientPerformanceHistoryEntity clientPerfHistoryEntity = clientBO
				.getPerformanceHistory();
		SessionUtils.setAttribute(ClientConstants.CLIENTPERFORMANCEHISTORY,
				clientPerfHistoryEntity, session);
		return forward;

	}

	/**
	 * No need to call any method on the business processor. All the values to
	 * be displayed in the UI are already in the context and value object.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward prevPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		String forward = null;
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		ClientCreationActionForm clientCreationForm = (ClientCreationActionForm) form;
		// obtaining the input page, so that the repective forward can be
		// decided
		String fromPage = clientCreationForm.getInput();
		// deciding forward page based on the input page
		if (ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage)) {
			((Client) context.getValueObject())
					.convertCustomFieldDateToDbformat(context.getUserContext()
							.getMfiLocale());
			forward = ClientConstants.CLIENT_PREV_EDIT_PERSONAL_INFO_PAGE;
		} else
			forward = ClientConstants.CLIENT_PREV_PERSONAL_INFO_PAGE;

		return mapping.findForward(forward);
	}

	/**
	 * No need to call any method on the business processor. All the values to
	 * be displayed in the UI are already in the context and value object.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward prevMFIInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		String forward = null;
		ClientCreationActionForm clientCreationForm = (ClientCreationActionForm) form;
		// obtaining the input page, so that the repective forward can be
		// decided
		String fromPage = clientCreationForm.getInput();
		// deciding forward page based on the input page
		if (ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage))
			forward = ClientConstants.CLIENT_PREV_EDIT_MFI_INFO_PAGE;
		else
			forward = ClientConstants.CLIENT_PREV_MFI_INFO_PAGE;

		return mapping.findForward(forward);
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward editPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		// setting the method being called in the action to editPersonalInfo .
		// This ensures the appropriate method is called
		// in the ClientCreationBusiness Processor
		context.setBusinessAction(ClientConstants.METHOD_EDIT_PERSONAL_INFO);
		delegate(context, request);
		return mapping.findForward(ClientConstants.CLIENT_EDIT_PERSONAL_PAGE);
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param httpservletresponse
	 * @return
	 * @throws Exception
	 */
	public ActionForward editMFIInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		Client oldClient = null;
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		ClientCreationActionForm clientCreationForm = (ClientCreationActionForm) form;
		// setting the method being called in the action to loadStatus . This
		// ensures the appropriate method is called
		// in the ClientCreationBusiness Processor
		String fromPage = clientCreationForm.getInput();
		Client client = new Client();
		oldClient = (Client) context.getValueObject();
		SessionUtils.setAttribute(ClientConstants.OLDCLIENT, oldClient, request
				.getSession());
		// obtaining the value of trained fromt eh client. If it is trained then
		// a variable isTrained is put in
		// session. On the basis of this value the trained checkbox is shown as
		// disabled on the edit Mfi page

		if (oldClient.getTrained() == null
				|| oldClient.getTrained().shortValue() == CustomerConstants.TRAINED_NO) {
			SessionUtils.setAttribute(CustomerConstants.IS_TRAINED,
					Constants.NO, request.getSession());
			clientCreationForm.setTrained("0");
		} else if (oldClient.getTrained() == Constants.NO && fromPage != null
				&& fromPage.equals("previous")) {
			short trainedValue = (Short) request.getSession().getAttribute(
					CustomerConstants.IS_TRAINED);
			SessionUtils.setAttribute(CustomerConstants.IS_TRAINED,
					trainedValue, request.getSession());
		} else if (oldClient.getTrained() == Constants.YES && fromPage != null
				&& fromPage.equals("previous")) {
			short trainedValue = (Short) request.getSession().getAttribute(
					CustomerConstants.IS_TRAINED);
			SessionUtils.setAttribute(CustomerConstants.IS_TRAINED,
					trainedValue, request.getSession());
		} else {
			clientCreationForm.setTrained("1");
			SessionUtils.setAttribute(CustomerConstants.IS_TRAINED,
					Constants.YES, request.getSession());
			SessionUtils.setAttribute(CustomerConstants.TRAINED_DATE, oldClient
					.getTrainedDate(), request.getSession());
		}

		client.setTrained(oldClient.getTrained());
		client.setTrainedDate(oldClient.getTrainedDate());
		client.setCustomerFormedByPersonnel(oldClient
				.getCustomerFormedByPersonnel());
		client.setExternalId(oldClient.getExternalId());
		client.setClientConfidential(oldClient.getClientConfidential());
		client.setPersonnel(oldClient.getPersonnel());
		client.setOffice(oldClient.getOffice());
		client.setParentCustomer(oldClient.getParentCustomer());
		client.setGroupFlag(oldClient.getGroupFlag());
		clientCreationForm.setIsClientUnderGrp(oldClient.getGroupFlag());
		client.setStatusId(oldClient.getStatusId());
		client.setCreatedDate(oldClient.getCreatedDate());
		client.setDisplayName(oldClient.getDisplayName());
		context.setValueObject(client);
		context.setBusinessAction(ClientConstants.METHOD_EDIT_MFI_INFO);
		delegate(context, request);

		return mapping.findForward(ClientConstants.CLIENT_EDIT_MFI_PAGE);
	}

	/**
	 * This method obtains the selected fees from the list of additional fees
	 * that can be applied to the cneter.
	 * 
	 * @param context
	 * @param fees
	 */
	private void setFeeTypeList(Context context, List<FeeMaster> fees) {
		List<FeeMaster> additionalFees = new ArrayList<FeeMaster>();
		List<FeeMaster> AdditionalFeesMaster = (List) context
				.getSearchResultBasedOnName(CenterConstants.FEES_LIST)
				.getValue();
		// The list of selected fee ids is obtained and those fee objects are
		// added to an other array to display on
		// the preview page for the create page information
		for (int i = 0; i < fees.size(); i++) {
			FeeMaster selectedFee = fees.get(i);
			for (int j = 0; j < AdditionalFeesMaster.size(); j++) {

				FeeMaster additionalFee = AdditionalFeesMaster.get(j);
				if (fees.get(i).getFeeId() != null) {
					if (fees.get(i).getFeeId().shortValue() == additionalFee
							.getFeeId().shortValue()) {
						selectedFee.setFeeName(additionalFee.getFeeName());
						selectedFee.setFeeFrequencyTypeId(additionalFee
								.getFeeFrequencyTypeId());
						selectedFee
								.setFeeMeeting(additionalFee.getFeeMeeting());
						additionalFees.add(selectedFee);
						break;
					}
				}
			}
		}
		// The selected additional fees are added to the search results which is
		// in turn added to the context
		SearchResults searchResults = new SearchResults();
		searchResults.setResultName(CenterConstants.ADDITIONAL_FEES_LIST);
		searchResults.setValue(additionalFees);
		context.addAttribute(searchResults);

	}

	/**
	 * This method is used to retrieve the name of the loan officer for the
	 * selcted loan officer id
	 * 
	 * @param context
	 *            The object containing the request paramters
	 * @param personnelId
	 * @return
	 */
	private Personnel getLO(Context context, short personnelId) {
		Personnel loanOfficer = new Personnel();
		Iterator iteratorLO = ((List) context.getSearchResultBasedOnName(
				CenterConstants.LOAN_OFFICER_LIST).getValue()).iterator();
		// Obtaining the name of the selected loan officer from the master list
		// of loan officers
		while (iteratorLO.hasNext()) {
			PersonnelMaster lo = (PersonnelMaster) iteratorLO.next();
			if (lo.getPersonnelId().shortValue() == personnelId) {
				loanOfficer.setPersonnelId(lo.getPersonnelId());
				loanOfficer.setDisplayName(lo.getDisplayName());
				loanOfficer.setVersionNo(lo.getVersionNo());

			}
		}
		return loanOfficer;
	}

	/**
	 * Method which is called to decide the pages on which the errors on failure
	 * of validation will be displayed this method forwards to the respective
	 * input page
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward customValidate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws SystemException {

		String forward = null;
		String methodCalled = (String) request.getAttribute("methodCalled");

		ClientCreationActionForm clientCreationForm = (ClientCreationActionForm) form;
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		String fromPage = clientCreationForm.getInput();
		// deciding forward page
		if (null != methodCalled) {
			// depending on where the preview was called from, the action
			// forwards to the page where the errors will be displayed
			if ("next".equals(methodCalled)) {
				if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage))
					forward = ClientConstants.NEXT_FAILURE;
			} else if (CustomerConstants.METHOD_PREVIEW.equals(methodCalled)
					&& ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage))
				forward = ClientConstants.CLIENT_CREATE_PERSONAL_PAGE;

			else if (CustomerConstants.METHOD_PREVIEW.equals(methodCalled)
					&& ClientConstants.INPUT_MFI_INFO.equals(fromPage))
				forward = ClientConstants.CLIENT_CREATE_MFI_PAGE;
			else if (CustomerConstants.METHOD_PREVIEW.equals(methodCalled)
					&& ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage))
				forward = ClientConstants.CLIENT_EDIT_MFI_FAILURE_PAGE;
			else if (CustomerConstants.METHOD_PREVIEW.equals(methodCalled)
					&& ClientConstants.INPUT_EDIT_PERSONAL_INFO
							.equals(fromPage)) {
				((Client) context.getValueObject())
						.convertCustomFieldDateToDbformat(context
								.getUserContext().getMfiLocale());
				forward = ClientConstants.CLIENT_EDIT_PERSONAL_FAILURE_PAGE;
			} else if (CustomerConstants.METHOD_CREATE.equals(methodCalled))
				forward = ClientConstants.CLIENT_CREATE_FAILURE_PAGE;

		}
		return mapping.findForward(forward);

	}

	/**
	 * This method checks if the action form to value object conversion is
	 * needed. When the methods being called are either manage or load status
	 * this conversion is not done
	 * 
	 * @return Returns whether the action form should be converted or not
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {

		if (null != methodName
				&& (methodName.equals(ClientConstants.METHOD_EDIT_MFI_INFO)
						|| methodName
								.equals(ClientConstants.METHOD_EDIT_PERSONAL_INFO)
						|| methodName
								.equals(ClientConstants.METHOD_LOAD_STATUS)
						|| methodName
								.equals(ClientConstants.METHOD_LOAD_TRANSFER)
						|| methodName
								.equals(ClientConstants.METHOD_LOAD_BRANCH_TRANSFER)
						|| methodName
								.equals(ClientConstants.METHOD_LOAD_MEETING)
						|| methodName
								.equals(CustomerConstants.METHOD_GET_DETAILS)
						|| methodName
								.equals(ClientConstants.METHOD_SHOW_PICTURE)
						|| methodName
								.equals(ClientConstants.METHOD_RETRIEVE_PICTURE)
						|| methodName.equals(ClientConstants.METHOD_PRELOAD)
						|| methodName.equals("loadFormedByPersonnel")
				// ||
				// methodName.equals(ClientConstants.METHOD_PREVIOUS_MFI_INFO)
				|| methodName.equals("updateMfi"))) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Method which is called to load the page on clicking a cancel. Depending
	 * from where the cancel was initially called, this method forwards to the
	 * respective page
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */

	public ActionForward customCancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String forward = null;
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		String fromPage = clientForm.getInput();
		// deciding forward page based on the from page
		if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage)
				|| ClientConstants.INPUT_MFI_INFO.equals(fromPage)
				|| CenterConstants.INPUT_CREATE.equals(fromPage))
			forward = ClientConstants.CLIENT_CREATE_CANCEL_PAGE;
		else if (ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage)
				|| ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage))
			forward = ClientConstants.CLIENT_EDIT_CANCEL_PAGE;

		request.getSession().removeAttribute(ClientConstants.OLDCLIENT);
		return mapping.findForward(forward);
	}

	/**
	 * Method which is called to load the page on clicking a cancel. Depending
	 * from where the cancel was initially called, this method forwards to the
	 * respective page
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = super.update(mapping, form, request, response);
		request.getSession().removeAttribute(ClientConstants.OLDCLIENT);

		return forward;
	}

	/**
	 * Method which is called to load the page on clicking a cancel. Depending
	 * from where the cancel was initially called, this method forwards to the
	 * respective page
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward updateMfi(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client client = (Client) context.getValueObject();
		// set to null as address details dont have to be updated
		client.setCustomerAddressDetail(null);
		client.setCustomerDetail(null);
		// client.setCustomerNameDetailSet(null);
		ActionForward forward = super.update(mapping, form, request, response);
		request.getSession().removeAttribute(ClientConstants.OLDCLIENT);
		request.getSession().removeAttribute(CustomerConstants.IS_TRAINED);
		return forward;
	}

	/**
	 * This method is called to clear action form values, whenever a fresh
	 * request to create a group comes in This is necessary because action form
	 * is stored in session
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 */
	private void clearActionForm(ActionForm form) {
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		clientForm.setCustomerAddressDetail(new CustomerAddressDetail());
		clientForm.setCustomerDetail(new CustomerDetail());
		clientForm.setOffice(new Office());
		clientForm.setGlobalCustNum("");
		clientForm.setExternalId("");
		clientForm.setClientConfidential("");
		clientForm.setTrainedDate("");
		clientForm.setTrained("");
		clientForm.setGovernmentId("");
		clientForm.setDateOfBirth("");
		clientForm.setLoanOfficerId("");
		clientForm.setCustomerFormedById("");
		clientForm.setNextOrPreview("next");
		// groupForm.setPrograms(null);
		clientForm.setCustomerMeeting(new CustomerMeeting());
		clientForm.setCustomerAccount(new CustomerAccount());
		clientForm.setAdminFeeList(new ArrayList<FeeMaster>());
		clientForm.setSelectedFeeList(new ArrayList<FeeMaster>());
		clientForm
				.setCustomerNameDetailSet(new ArrayList<CustomerNameDetail>(2));
		clientForm.setCustomFieldSet(new ArrayList<CustomerCustomField>());
	}

	/**
	 * Method called to load the page used to change the status of the client
	 * The page displays only those states to which the center can be changed
	 * to. Eg: If the client status is partial, then the page displays cancelled ,
	 * pending or approved
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward loadStatus(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client oldClient = (Client) context.getValueObject();
		SessionUtils.setAttribute(ClientConstants.OLDCLIENT, oldClient, request
				.getSession());

		return mapping.findForward(ClientConstants.CLIENT_STATUS_PAGE);
	}

	/**
	 * Method called to load the page used to change the status of the client
	 * The page displays only those states to which the center can be changed
	 * to. Eg: If the client status is partial, then the page displays cancelled ,
	 * pending or approved
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward loadTransfer(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client oldClient = (Client) context.getValueObject();
		SessionUtils.setAttribute(ClientConstants.OLDCLIENT, oldClient, request
				.getSession());

		return mapping.findForward(ClientConstants.CLIENT_TRANSFER_PAGE);
	}

	/**
	 * Method called to load the page used to change the branch membership of
	 * the client This puts the current client details in the session and
	 * transfers control to the clientTransfer action
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward loadBranchTransfer(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client oldClient = (Client) context.getValueObject();
		SessionUtils.setAttribute(ClientConstants.OLDCLIENT, oldClient, request
				.getSession());
		logger
				.debug("CUSTOMER VERSION IN loadBranchTransfer cleint creation ACTION: "
						+ ((Client) SessionUtils
								.getAttribute(ClientConstants.OLDCLIENT,
										request.getSession())).getVersionNo());
		return mapping.findForward(ClientConstants.CLIENT_BRANCH_TRANSFER_PAGE);
	}

	/**
	 * Method called to load the page used to view or edit historical data of
	 * the client
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward loadHistoricalData(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ClientConstants.CLIENT_HISTORICAL_DATA_PAGE);
	}

	/**
	 * Method called to load the page used to change the branch membership of
	 * the client This puts the current client details in the session and
	 * transfers control to the clientTransfer action
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward loadMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		String forward = null;
		putMasterDataInSession(request, context);
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		String fromPage = clientForm.getInput();
		logger.debug("FRam page: " + fromPage);
		if (null != fromPage && fromPage.equals("ClientDetails")) {
			forward = ClientConstants.CLIENT_EDIT_MEETING_PAGE;
		} else {
			logger.debug("Inside else of load Meethin");
			forward = ClientConstants.CLIENT_MEETING_PAGE;
		}
		logger.debug("Forward page: " + fromPage);
		return mapping.findForward(forward);
	}

	/**
	 * Method called to update the meeting of a client
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward updateMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		Meeting meeting = (Meeting) context
				.getBusinessResults(MeetingConstants.MEETING);
		clientForm.getCustomerMeeting().setMeeting(meeting);
		context.setBusinessAction(ClientConstants.METHOD_UPDATE_MEETING);
		delegate(context, request);

		return mapping.findForward(ClientConstants.CLIENT_MEETING_UPDATE_PAGE);
	}

	private void putMasterDataInSession(HttpServletRequest request,
			Context context) {
		HttpSession session = request.getSession();
		SessionUtils.setAttribute(CenterConstants.LOAN_OFFICER_LIST,
				(List) context.getSearchResultBasedOnName(
						CenterConstants.LOAN_OFFICER_LIST).getValue(), session);
		SessionUtils.setAttribute(CustomerConstants.ADMIN_FEES_LIST,
				(List) context.getSearchResultBasedOnName(
						CustomerConstants.ADMIN_FEES_LIST).getValue(), session);
		SessionUtils.setAttribute(CustomerConstants.FEES_LIST, (List) context
				.getSearchResultBasedOnName(CustomerConstants.FEES_LIST)
				.getValue(), session);

		SessionUtils.setAttribute(ClientConstants.SALUTATION_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.SALUTATION_ENTITY).getValue(), session);
		SessionUtils.setAttribute(ClientConstants.MARITAL_STATUS_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.MARITAL_STATUS_ENTITY).getValue(),
				session);
		SessionUtils
				.setAttribute(ClientConstants.CITIZENSHIP_ENTITY,
						(EntityMaster) context.getSearchResultBasedOnName(
								ClientConstants.CITIZENSHIP_ENTITY).getValue(),
						session);

		SessionUtils.setAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.BUSINESS_ACTIVITIES_ENTITY).getValue(),
				session);
		SessionUtils.setAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.EDUCATION_LEVEL_ENTITY).getValue(),
				session);
		SessionUtils.setAttribute(ClientConstants.GENDER_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.GENDER_ENTITY).getValue(), session);

		SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.SPOUSE_FATHER_ENTITY).getValue(),
				session);
		SessionUtils
				.setAttribute(ClientConstants.HANDICAPPED_ENTITY,
						(EntityMaster) context.getSearchResultBasedOnName(
								ClientConstants.HANDICAPPED_ENTITY).getValue(),
						session);
		SessionUtils.setAttribute(ClientConstants.ETHINICITY_ENTITY,
				(EntityMaster) context.getSearchResultBasedOnName(
						ClientConstants.ETHINICITY_ENTITY).getValue(), session);

	}

	/**
	 * Method called to load the mfi information page after entering meeting
	 * details This puts the master data needed for this page from the session
	 * back to the context
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// forwards to the page where the center status can be changed
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		// putMasterDataInContext(request, context);
		ClientCreationActionForm clientForm = (ClientCreationActionForm) form;
		Meeting meeting = (Meeting) context
				.getBusinessResults(MeetingConstants.MEETING);
		CustomerMeeting customerMeeting = new CustomerMeeting();
		if (meeting != null) {
			customerMeeting.setMeeting(meeting);
			clientForm.setCustomerMeeting(customerMeeting);
		} else {
			customerMeeting.setMeeting(null);
			clientForm.setCustomerMeeting(customerMeeting);
		}
		logger.debug("GET CUSTMER MEETING WITH VALUE IN ACTION "
				+ clientForm.getCustomerMeeting());
		// logger.debug("---------GET CUSTMER MEETING Meeting WITH VALUE IN
		// ACTION " + clientForm.getCustomerMeeting().getMeeting());
		return mapping.findForward(ClientConstants.PREVIOUS_SUCCESS);
	}

	private void putMasterDataInContext(HttpServletRequest request,
			Context context) {
		HttpSession session = request.getSession();
		CustomerHelper customerHelper = new CustomerHelper();
		context.addAttribute(customerHelper.getResultObject(
				CenterConstants.LOAN_OFFICER_LIST, (List) session
						.getAttribute(CenterConstants.LOAN_OFFICER_LIST)));
		context.addAttribute(customerHelper.getResultObject(
				CustomerConstants.ADMIN_FEES_LIST, (List) session
						.getAttribute(CustomerConstants.ADMIN_FEES_LIST)));
		context.addAttribute(customerHelper.getResultObject(
				CustomerConstants.FEES_LIST, (List) session
						.getAttribute(CustomerConstants.FEES_LIST)));

		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.SALUTATION_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.SALUTATION_ENTITY)));
		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.MARITAL_STATUS_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.MARITAL_STATUS_ENTITY)));
		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.CITIZENSHIP_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.CITIZENSHIP_ENTITY)));

		context
				.addAttribute(customerHelper
						.getResultObject(
								ClientConstants.BUSINESS_ACTIVITIES_ENTITY,
								(EntityMaster) session
										.getAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY)));
		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.EDUCATION_LEVEL_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY)));
		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.GENDER_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.GENDER_ENTITY)));

		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.SPOUSE_FATHER_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.SPOUSE_FATHER_ENTITY)));
		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.HANDICAPPED_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.HANDICAPPED_ENTITY)));
		context.addAttribute(customerHelper.getResultObject(
				ClientConstants.ETHINICITY_ENTITY, (EntityMaster) session
						.getAttribute(ClientConstants.ETHINICITY_ENTITY)));

	}

	/**
	 * Method to handle the picture being uploaded This puts the current client
	 * details in the session and transfers control to the clientTransfer action
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward retrievePicture(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		/*
		 * context.setBusinessAction(ClientConstants.METHOD_RETRIEVE_PICTURE);
		 * delegate(context,request);
		 */
		Client client = (Client) context.getValueObject();
		InputStream in = client.getCustomerPicture();

		/*
		 * BufferedReader buffered = new BufferedReader(new
		 * InputStreamReader(in)); while((line =buffered.readLine())!=null){
		 * logger.debug("Line: "+line); }
		 */
		response.setContentType("image/jpeg");
		BufferedOutputStream out = new BufferedOutputStream(response
				.getOutputStream());

		byte[] by = new byte[32768]; // 4K buffer buf, 0, buf.length
		int index = in.read(by, 0, 32768);

		while (index != -1) {
			out.write(by, 0, index);
			index = in.read(by, 0, 32768);

		}
		out.flush();
		String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
		/*
		 * ActivityContext lastActivity =
		 * (ActivityContext)request.getSession().getAttribute("ActivityContext");
		 * lastActivity.setLastForward(mapping.findForward("get_success"));
		 */
		return mapping.findForward(forward);
	}

	/**
	 * Method to handle the picture being uploaded This puts the current client
	 * details in the session and transfers control to the clientTransfer action
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward showPicture(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(ClientConstants.METHOD_RETRIEVE_PICTURE);
		delegate(context, request);
		SessionUtils.setAttribute("noPictureOnGet", context
				.getBusinessResults("noPictureOnGet"), request.getSession());
		String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
		return mapping.findForward(forward);
	}

	/**
	 * Method to handle the picture being uploaded This puts the current client
	 * details in the session and transfers control to the clientTransfer action
	 * 
	 * @param mapping
	 *            The page to which the control passes to. This is specified in
	 *            the struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward retrievePictureOnPreview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// System.out.println("------------In Retireive On preview");
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		Client client = (Client) context.getValueObject();
		InputStream in = client.getCustomerPicture();
		in.mark(0);
		/*
		 * BufferedReader buffered = new BufferedReader(new
		 * InputStreamReader(in)); while((line =buffered.readLine())!=null){
		 * logger.debug("Line: "+line); }
		 */
		response.setContentType("image/jpeg");
		BufferedOutputStream out = new BufferedOutputStream(response
				.getOutputStream());

		byte[] by = new byte[1024 * 4]; // 4K buffer buf, 0, buf.length
		int index = in.read(by, 0, 1024 * 4);

		while (index != -1) {
			out.write(by, 0, index);
			index = in.read(by, 0, 1024 * 4);
			// System.out.println("while loop");

		}
		out.flush();
		out.close();
		in.reset();
		String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
		return mapping.findForward(forward);
	}

	/**
	 * This method is called to retrieve client details if the user is comign
	 * from a search. It calls get in business processor to get all client
	 * details
	 * 
	 * @param mapping
	 *            indicates action mapping defined in struts-config.xml
	 * @param form
	 *            The form bean associated with this action
	 * @param request
	 *            Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward getDetails(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalCustNum = request
				.getParameter(CustomerConstants.GLOBAL_CUST_NUM);
		Client client = (Client) getValueObject(getPath());
		client.setGlobalCustNum(globalCustNum);
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setValueObject(client);
		return get(mapping, form, request, response);
		/*
		 * context.setBusinessAction(ClientConstants.METHOD_GET_BY_GLOBAL);
		 * delegate(context,request); HttpSession session =
		 * request.getSession();
		 * SessionUtils.setAttribute(CustomerConstants.LINK_VALUES,(LinkParameters)context.getBusinessResults(CustomerConstants.LINK_VALUES),session);
		 * request.setAttribute(ClientConstants.SPOUSE_FATHER_VALUE ,
		 * ((Short)context.getBusinessResults(ClientConstants.SPOUSE_FATHER_VALUE)).shortValue());
		 * request.setAttribute(ClientConstants.SPOUSE_FATHER_NAME_VALUE ,
		 * (String)context.getBusinessResults(ClientConstants.SPOUSE_FATHER_NAME_VALUE));
		 * //set user age request.setAttribute("age",new
		 * CustomerHelper().calculateAge(((Client)context.getValueObject()).getDateOfBirth()));
		 * if(checkIfLsmOrRsm() == true){
		 * session.setAttribute(CustomerConstants.CONFIGURATION_LSM,
		 * CustomerConstants.YES); } else
		 * session.setAttribute(CustomerConstants.CONFIGURATION_LSM,
		 * CustomerConstants.NO);
		 * 
		 * String forward ="get_success"; return mapping.findForward(forward);
		 */
	}
}

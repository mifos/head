/**

 * ClientCustAction.java version: 1.0



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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailEntity;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailEntity;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.client.struts.actionforms.ClientCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class ClientCustAction extends CustAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return getClientBusinessService();
	}

	private ClientBusinessService getClientBusinessService()
			throws ServiceException {
		return (ClientBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Client);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		actionForm.setGroupFlag(ClientConstants.NO);
		return mapping.findForward(ActionForwards.chooseOffice_success
				.toString());
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		doCleanUp(actionForm, request);
		request.getSession().removeAttribute(ClientConstants.CLIENT_MEETING);
		if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
			actionForm.setParentGroup(getCustomerBusinessService().getCustomer(Integer
					.valueOf(actionForm.getParentGroupId())));
			actionForm.setOfficeId(actionForm.getParentGroup().getOffice()
					.getOfficeId().toString());
			actionForm.setFormedByPersonnel(actionForm.getParentGroup()
					.getPersonnel().getPersonnelId().toString());
		}
		loadCreateMasterData(actionForm, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward next(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.next_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		HttpSession session = request.getSession();
		if (Configuration.getInstance().getCustomerConfig(
				getUserContext(request).getBranchId())
				.isPendingApprovalStateDefinedForClient() == true) {
			session.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
					CustomerConstants.YES);

		} else
			session.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
					CustomerConstants.NO);
		actionForm.setAge(new CustomerHelper().calculateAge(DateHelper
				.getLocaleDate(getUserContext(request).getPereferedLocale(),
						actionForm.getDateOfBirth())));
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		String fromPage = actionForm.getInput();
		if (ClientConstants.INPUT_PERSONAL_INFO.equals(fromPage)
				|| ClientConstants.INPUT_MFI_INFO.equals(fromPage)
				|| CenterConstants.INPUT_CREATE.equals(fromPage))
			forward = ActionForwards.cancelCreate_success.toString();
		else if (ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(fromPage)
				|| ClientConstants.INPUT_EDIT_MFI_INFO.equals(fromPage))
			forward = ActionForwards.cancelEdit_success.toString();
		return mapping.findForward(forward);
	}

	private void loadCreateMasterData(ClientCustActionForm actionForm,
			HttpServletRequest request) throws ApplicationException,
			SystemException {
		Short officeId = null;
		loadMasterDataEntities(actionForm, request);
		loadCreateCustomFields(actionForm, EntityType.CLIENT, request);
		loadFees(actionForm, request);
		if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
			loadLoanOfficers(actionForm.getOfficeIdValue(), request);
			officeId = actionForm.getOfficeIdValue();
		} else
			officeId = actionForm.getParentGroup().getOffice().getOfficeId();
		loadFormedByPersonnel(officeId, request);
	}

	private void loadMasterDataEntities(ClientCustActionForm actionForm,
			HttpServletRequest request) throws ApplicationException,
			SystemException {
		SessionUtils.setAttribute(ClientConstants.SALUTATION_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.SALUTATION, getUserContext(request)
								.getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.MARITAL_STATUS_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.MARITAL_STATUS, getUserContext(request)
								.getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.CITIZENSHIP_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.CITIZENSHIP, getUserContext(request)
								.getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.BUSINESS_ACTIVITIES_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.BUSINESS_ACTIVITIES, getUserContext(
								request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.EDUCATION_LEVEL,
						getUserContext(request).getLocaleId()), request
						.getSession());
		SessionUtils.setAttribute(ClientConstants.GENDER_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(MasterConstants.GENDER,
						getUserContext(request).getLocaleId()), request
						.getSession());
		SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_ENTITY,
				getMasterEntities(SpouseFatherLookupEntity.class,
						getUserContext(request).getLocaleId()), request
						.getSession());
		SessionUtils.setAttribute(ClientConstants.HANDICAPPED_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.HANDICAPPED, getUserContext(request)
								.getLocaleId()), request.getSession());
		SessionUtils.setAttribute(ClientConstants.ETHINICITY_ENTITY,
				getCustomerBusinessService().retrieveMasterEntities(
						MasterConstants.ETHINICITY, getUserContext(request)
								.getLocaleId()), request.getSession());
	}

	public ActionForward retrievePictureOnPreview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		InputStream in = actionForm.getPicture().getInputStream();
		in.mark(0);
		response.setContentType("image/jpeg");
		BufferedOutputStream out = new BufferedOutputStream(response
				.getOutputStream());
		byte[] by = new byte[1024 * 4]; // 4K buffer buf, 0, buf.length
		int index = in.read(by, 0, 1024 * 4);
		while (index != -1) {
			out.write(by, 0, index);
			index = in.read(by, 0, 1024 * 4);
		}
		out.flush();
		out.close();
		in.reset();
		String forward = ClientConstants.CUSTOMER_PICTURE_PAGE;
		return mapping.findForward(forward);
	}

	public ActionForward previewPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		actionForm.setAge(new CustomerHelper().calculateAge(DateHelper
				.getLocaleDate(getUserContext(request).getPereferedLocale(),
						actionForm.getDateOfBirth())));
		return mapping.findForward(ActionForwards.previewPersonalInfo_success
				.toString());

	}

	public ActionForward prevPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		return mapping.findForward(ActionForwards.prevPersonalInfo_success
				.toString());
	}

	public ActionForward prevMFIInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		return mapping.findForward(ActionForwards.prevMFIInfo_success
				.toString());
	}

	public ActionForward prevMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.next_success.toString());
	}

	public ActionForward loadMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loadMeeting_success
				.toString());
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws ApplicationException, IOException {
		ClientBO client = null;
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(
				ClientConstants.CLIENT_MEETING, request.getSession());
		List<CustomFieldView> customFields = actionForm.getCustomFields();
		UserContext userContext = getUserContext(request);
		convertCustomFieldDateToUniformPattern(customFields, userContext
				.getPereferedLocale());
		Short personnelId = null;
		Short officeId = null;
		if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
			personnelId = actionForm.getParentGroup().getPersonnel()
					.getPersonnelId();
			officeId = actionForm.getParentGroup().getOffice().getOfficeId();
		} else {
			personnelId = actionForm.getLoanOfficerIdValue();
			officeId = actionForm.getOfficeIdValue();
		}
		if (personnelId != null)
			checkPermissionForCreate(actionForm.getStatusValue(),
					getUserContext(request), null, officeId, personnelId);
		else
			checkPermissionForCreate(actionForm.getStatusValue(),
					getUserContext(request), null, officeId, getUserContext(
							request).getId());
		try {
			if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
				client = new ClientBO(userContext, actionForm.getClientName()
						.getDisplayName(), CustomerStatus.getStatus(actionForm
						.getStatusValue()), actionForm.getExternalId(),
						getDateFromString(actionForm.getMfiJoiningDate(),
								userContext.getPereferedLocale()), actionForm
								.getAddress(), customFields, actionForm
								.getFeesToApply(), actionForm
								.getFormedByPersonnelValue(), actionForm
								.getOfficeIdValue(), meeting, actionForm
								.getLoanOfficerIdValue(), getDateFromString(
								actionForm.getDateOfBirth(), userContext
										.getPereferedLocale()), actionForm
								.getGovernmentId(), actionForm
								.getTrainedValue(), getDateFromString(
								actionForm.getTrainedDate(), userContext
										.getPereferedLocale()), actionForm
								.getGroupFlagValue(), actionForm
								.getClientName(), actionForm.getSpouseName(),
						actionForm.getClientDetailView(), actionForm
								.getCustomerPicture());
			} else {
				client = new ClientBO(userContext, actionForm.getClientName()
						.getDisplayName(), CustomerStatus.getStatus(actionForm
						.getStatusValue()), actionForm.getExternalId(),
						getDateFromString(actionForm.getMfiJoiningDate(),
								userContext.getPereferedLocale()), actionForm
								.getAddress(), customFields, actionForm
								.getFeesToApply(), actionForm
								.getFormedByPersonnelValue(), actionForm
								.getParentGroup().getOffice().getOfficeId(),
						actionForm.getParentGroup(), getDateFromString(
								actionForm.getDateOfBirth(), userContext
										.getPereferedLocale()), actionForm
								.getGovernmentId(), actionForm
								.getTrainedValue(), getDateFromString(
								actionForm.getTrainedDate(), userContext
										.getPereferedLocale()), actionForm
								.getGroupFlagValue(), actionForm
								.getClientName(), actionForm.getSpouseName(),
						actionForm.getClientDetailView(), actionForm
								.getCustomerPicture());
			}
			client.save();
			actionForm.setCustomerId(client.getCustomerId().toString());
			actionForm.setGlobalCustNum(client.getGlobalCustNum());
			client = null;
		} catch (ApplicationException ae) {
			ae.printStackTrace();
			ActionErrors errors = new ActionErrors();
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping
					.findForward(ActionForwards.create_failure.toString());
		}
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	private void doCleanUp(ClientCustActionForm actionForm,
			HttpServletRequest request) {
		clearActionForm(actionForm);
		SessionUtils.setAttribute(ClientConstants.CLIENT_MEETING, null, request
				.getSession());
	}

	private void clearActionForm(ClientCustActionForm actionForm) {
		actionForm.setDefaultFees(new ArrayList<FeeView>());
		actionForm.setAdditionalFees(new ArrayList<FeeView>());
		actionForm.setCustomFields(new ArrayList<CustomFieldView>());
		actionForm.setAddress(new Address());
		actionForm.setDisplayName(null);
		actionForm.setDateOfBirth(null);
		actionForm.setGovernmentId(null);
		actionForm.setMfiJoiningDate(null);
		actionForm.setGlobalCustNum(null);
		actionForm.setCustomerId(null);
		actionForm.setExternalId(null);
		actionForm.setLoanOfficerId(null);
		actionForm.setFormedByPersonnel(null);
		actionForm.setTrained(null);
		actionForm.setTrainedDate(null);
		actionForm.setParentGroup(null);
		actionForm.setClientName(new ClientNameDetailView());
		actionForm.setSpouseName(new ClientNameDetailView());
		actionForm.setClientDetailView(new ClientDetailView());
		actionForm.setNextOrPreview("next");
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		ClientBO clientBO = (ClientBO) getCustomerBusinessService().getBySystemId(actionForm
				.getGlobalCustNum(), CustomerLevel.CLIENT.getValue());
		clientBO.setUserContext(getUserContext(request));
		clientBO.getCustomerStatus().setLocaleId(
				getUserContext(request).getLocaleId());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request.getSession());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request
				.getSession());
		request.getSession().removeAttribute(ClientConstants.AGE);
		loadMasterDataForDetailsPage(request, clientBO);
		setSpouseOrFatherName(request, clientBO);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	public ActionForward editPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		clearActionForm(actionForm);
		ClientBO client = (ClientBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		ClientBO clientBO = (ClientBO) getCustomerBusinessService().getBySystemId(client
				.getGlobalCustNum(), CustomerLevel.CLIENT.getValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request
				.getSession());
		client = null;
		loadUpdateMasterData(actionForm, request);
		setValuesInActionForm(actionForm, request);
		return mapping.findForward(ActionForwards.editPersonalInfo_success
				.toString());
	}

	public ActionForward previewEditPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		actionForm.setAge(new CustomerHelper().calculateAge(DateHelper
				.getLocaleDate(getUserContext(request).getPereferedLocale(),
						actionForm.getDateOfBirth())));
		return mapping
				.findForward(ActionForwards.previewEditPersonalInfo_success
						.toString());
	}

	public ActionForward prevEditPersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		actionForm.setAge(new CustomerHelper().calculateAge(DateHelper
				.getLocaleDate(getUserContext(request).getPereferedLocale(),
						actionForm.getDateOfBirth())));
		return mapping.findForward(ActionForwards.prevEditPersonalInfo_success
				.toString());
	}

	@CloseSession
	public ActionForward updatePersonalInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws ApplicationException {
		try {
			ClientBO client = (ClientBO) SessionUtils.getAttribute(
					Constants.BUSINESS_KEY, request.getSession());
			ClientCustActionForm actionForm = (ClientCustActionForm) form;
			client.updateAddress(actionForm.getAddress());
			convertCustomFieldDateToUniformPattern(
					actionForm.getCustomFields(), getUserContext(request)
							.getPereferedLocale());
			for (CustomFieldView fieldView : actionForm.getCustomFields())
				for (CustomerCustomFieldEntity fieldEntity : client
						.getCustomFields())
					if (fieldView.getFieldId().equals(fieldEntity.getFieldId()))
						fieldEntity.setFieldValue(fieldView.getFieldValue());
			client.getClientName()
					.updateNameDetails(actionForm.getClientName());
			client.getSpouseName()
					.updateNameDetails(actionForm.getSpouseName());
			client.setDisplayName(actionForm.getClientName().getDisplayName());
			client.setFirstName(actionForm.getClientName().getFirstName());
			client.setLastName(actionForm.getClientName().getLastName());
			client.setSecondLastName(actionForm.getClientName()
					.getSecondLastName());
			client.setDateOfBirth(getDateFromString(
					actionForm.getDateOfBirth(), getUserContext(request)
							.getPereferedLocale()));
			client.setGovernmentId(actionForm.getGovernmentId());

			if (actionForm.getPicture() != null
					&& !StringUtils.isNullOrEmpty(actionForm.getPicture()
							.getFileName())) {
				client.updatePicture(actionForm.getCustomerPicture());
			}
			client.setUserContext(getUserContext(request));
			client.updateClientDetails(actionForm.getClientDetailView());
			client.updatePersonalInfo();
		} catch (ApplicationException ae) {
			ae.printStackTrace();
			ActionErrors errors = new ActionErrors();
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping
					.findForward(ActionForwards.updatePersonalInfo_failure
							.toString());
		}
		return mapping.findForward(ActionForwards.updatePersonalInfo_success
				.toString());
	}

	public ActionForward editMfiInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ClientCustActionForm actionForm = (ClientCustActionForm) form;
		clearActionForm(actionForm);
		ClientBO client = (ClientBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		ClientBO clientBO = (ClientBO) getCustomerBusinessService().getBySystemId(client
				.getGlobalCustNum(), CustomerLevel.CLIENT.getValue());
		client = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientBO, request
				.getSession());
		if (!clientBO.isClientUnderGroup())
			loadUpdateMfiMasterData(clientBO.getOffice().getOfficeId(), request);
		setValuesForMfiEditInActionForm(actionForm, request);
		return mapping.findForward(ActionForwards.editMfiInfo_success
				.toString());
	}

	public ActionForward previewEditMfiInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		return mapping.findForward(ActionForwards.previewEditMfiInfo_success
				.toString());
	}

	public ActionForward prevEditMfiInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		return mapping.findForward(ActionForwards.prevEditMfiInfo_success
				.toString());
	}

	@CloseSession
	public ActionForward updateMfiInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			ClientBO client = (ClientBO) SessionUtils.getAttribute(
					Constants.BUSINESS_KEY, request.getSession());
			ClientCustActionForm actionForm = (ClientCustActionForm) form;
			client.setExternalId(actionForm.getExternalId());
			if (actionForm.getTrainedValue() != null
					&& actionForm.getTrainedValue().equals(
							YesNoFlag.YES.getValue()))
				client.setTrained(true);
			else
				client.setTrained(false);
			
			client.setTrainedDate(getDateFromString(
					actionForm.getTrainedDate(), getUserContext(request)
							.getPereferedLocale()));
			if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
				if (actionForm.getLoanOfficerIdValue() != null) {
					client.setPersonnel(getPersonnelBusinessService()
							.getPersonnel(actionForm.getLoanOfficerIdValue()));
				} else
					client.setPersonnel(null);
			}
			client.setUserContext(getUserContext(request));
			client.update();
		} catch (ApplicationException ae) {
			ae.printStackTrace();
			ActionErrors errors = new ActionErrors();
			errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae
					.getValues()));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(ActionForwards.updateMfiInfo_failure
					.toString());
		}
		return mapping.findForward(ActionForwards.updateMfiInfo_success
				.toString());
	}

	private void setValuesInActionForm(ClientCustActionForm actionForm,
			HttpServletRequest request) throws  Exception {
		ClientBO client = (ClientBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		if (client.getPersonnel() != null) {
			actionForm.setLoanOfficerId(client.getPersonnel().getPersonnelId()
					.toString());
		}
		actionForm.setCustomerId(client.getCustomerId().toString());
		actionForm.setGlobalCustNum(client.getGlobalCustNum());
		actionForm.setExternalId(client.getExternalId());
		actionForm.setAddress(client.getAddress());
		actionForm.setCustomFields(createCustomFieldViews(client
				.getCustomFields(), request));
		for (ClientNameDetailView nameView : createNameViews(client
				.getNameDetailSet())) {
			if (nameView.getNameType().equals(ClientConstants.CLIENT_NAME_TYPE)) {
				actionForm.setClientName(nameView);
			} else {
				actionForm.setSpouseName(nameView);
			}
		}
		actionForm.setClientDetailView(createClientDetailView(client
				.getCustomerDetail()));
		actionForm.setGovernmentId(client.getGovernmentId());
		actionForm.setDateOfBirth(DateHelper.getUserLocaleDate(getUserContext(
				request).getPereferedLocale(), client.getDateOfBirth()
				.toString()));

	}

	private void setValuesForMfiEditInActionForm(
			ClientCustActionForm actionForm, HttpServletRequest request) {
		ClientBO client = (ClientBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		if (client.getPersonnel() != null) {
			actionForm.setLoanOfficerId(client.getPersonnel().getPersonnelId()
					.toString());
			client.getPersonnel().getDisplayName();
		}
		actionForm.setCustomerId(client.getCustomerId().toString());
		actionForm.setGlobalCustNum(client.getGlobalCustNum());
		actionForm.setExternalId(client.getExternalId());
		if (client.isClientUnderGroup()) {
			actionForm.setGroupFlag(ClientConstants.YES);
			actionForm.setParentGroup(client.getParentCustomer());
		} else
			actionForm.setGroupFlag(ClientConstants.NO);
		if (client.isTrained())
			actionForm.setTrained(ClientConstants.YES);
		else
			actionForm.setTrained(ClientConstants.NO);
		if (client.getTrainedDate() != null)
			actionForm.setTrainedDate(DateHelper.getUserLocaleDate(
					getUserContext(request).getPereferedLocale(), client
							.getTrainedDate().toString()));
	}

	private void loadUpdateMfiMasterData(Short officeId,
			HttpServletRequest request) throws Exception {
		loadLoanOfficers(officeId, request);
	}

	private ClientDetailView createClientDetailView(
			ClientDetailEntity customerDetail) {
		return new ClientDetailView(customerDetail.getEthinicity(),
				customerDetail.getCitizenship(), customerDetail
						.getHandicapped(), customerDetail
						.getBusinessActivities(), customerDetail
						.getMaritalStatus(),
				customerDetail.getEducationLevel(), customerDetail
						.getNumChildren(), customerDetail.getGender());
	}

	private List<ClientNameDetailView> createNameViews(
			Set<ClientNameDetailEntity> nameDetailSet) {
		List<ClientNameDetailView> clientNameDetailViews = new ArrayList<ClientNameDetailView>();
		for (ClientNameDetailEntity clientNameDetail : nameDetailSet) {
			clientNameDetailViews.add(createNameViewObject(clientNameDetail));
		}
		return clientNameDetailViews;
	}

	private ClientNameDetailView createNameViewObject(
			ClientNameDetailEntity clientNameDetail) {
		ClientNameDetailView nameView = new ClientNameDetailView(
				clientNameDetail.getNameType(), clientNameDetail
						.getSalutation(), new StringBuilder(clientNameDetail
						.getDisplayName()), clientNameDetail.getName()
						.getFirstName(), clientNameDetail.getName()
						.getMiddleName(), clientNameDetail.getName()
						.getLastName(), clientNameDetail.getName()
						.getSecondLastName());
		return nameView;
	}

	private void loadUpdateMasterData(ClientCustActionForm actionForm,
			HttpServletRequest request) throws ApplicationException,
			SystemException {
		loadMasterDataEntities(actionForm, request);
		loadCustomFieldDefinitions(EntityType.CLIENT, request);

	}

	private void loadMasterDataForDetailsPage(HttpServletRequest request,
			ClientBO clientBO) throws  ApplicationException {
		try {
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setAttribute(ClientConstants.AGE, new CustomerHelper()
				.calculateAge(new java.sql.Date((clientBO.getDateOfBirth())
						.getTime())), request.getSession());
		
			SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_ENTITY,
					getMasterEntities(SpouseFatherLookupEntity.class, localeId),
					request.getSession());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMERPERFORMANCE,
				getCustomerBusinessService()
						.numberOfMeetings(true, clientBO.getCustomerId()),
				request.getSession());
		SessionUtils.setAttribute(CustomerConstants.CUSTOMERPERFORMANCEHISTORY,
				getCustomerBusinessService().numberOfMeetings(false, clientBO
						.getCustomerId()), request.getSession());
		SessionUtils
				.setAttribute(ClientConstants.LOANCYCLECOUNTER, getCustomerBusinessService()
						.fetchLoanCycleCounter(clientBO.getCustomerId()),
						request.getSession());
		List<LoanBO> loanAccounts = clientBO.getOpenLoanAccounts();
		List<SavingsBO> savingsAccounts = clientBO.getOpenSavingAccounts();
		setLocaleIdToLoanStatus(loanAccounts, localeId);
		setLocaleIdToSavingsStatus(savingsAccounts, localeId);
		SessionUtils.setAttribute(ClientConstants.CUSTOMERLOANACCOUNTSINUSE,
				loanAccounts, request.getSession());
		SessionUtils.setAttribute(ClientConstants.CUSTOMERSAVINGSACCOUNTSINUSE,
				savingsAccounts, request.getSession());
		SessionUtils.setAttribute(
				ClientConstants.BUSINESS_ACTIVITIES_ENTITY_NAME,
				getNameForBusinessActivityEntity(clientBO.getCustomerDetail()
						.getBusinessActivities(), localeId), request
						.getSession());
		SessionUtils.setAttribute(ClientConstants.HANDICAPPED_ENTITY_NAME,
				getNameForBusinessActivityEntity(clientBO.getCustomerDetail()
						.getHandicapped(), localeId), request.getSession());
		SessionUtils.setAttribute(ClientConstants.MARITAL_STATUS_ENTITY_NAME,
				getNameForBusinessActivityEntity(clientBO.getCustomerDetail()
						.getMaritalStatus(), localeId), request.getSession());
		SessionUtils.setAttribute(ClientConstants.CITIZENSHIP_ENTITY_NAME,
				getNameForBusinessActivityEntity(clientBO.getCustomerDetail()
						.getCitizenship(), localeId), request.getSession());
		SessionUtils.setAttribute(ClientConstants.ETHINICITY_ENTITY_NAME,
				getNameForBusinessActivityEntity(clientBO.getCustomerDetail()
						.getEthinicity(), localeId), request.getSession());
		SessionUtils.setAttribute(ClientConstants.EDUCATION_LEVEL_ENTITY_NAME,
				getNameForBusinessActivityEntity(clientBO.getCustomerDetail()
						.getEducationLevel(), localeId), request.getSession());
		SessionUtils.setAttribute(ClientConstants.SPOUSE_FATHER_ENTITY,
				getMasterEntities(SpouseFatherLookupEntity.class, localeId),
				request.getSession());
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		}
	}

	private void setLocaleIdToLoanStatus(List<LoanBO> accountList,
			Short localeId) {
		for (LoanBO accountBO : accountList)
			setLocaleForAccount((AccountBO) accountBO, localeId);
	}

	private void setLocaleIdToSavingsStatus(List<SavingsBO> accountList,
			Short localeId) {
		for (SavingsBO accountBO : accountList)
			setLocaleForAccount((AccountBO) accountBO, localeId);
	}

	private void setLocaleForAccount(AccountBO account, Short localeId) {
		account.getAccountState().setLocaleId(localeId);
	}

	private String getNameForBusinessActivityEntity(Integer entityId,
			Short localeId) throws PersistenceException, ServiceException {
		if (entityId != null)
			return ((MasterDataService) ServiceFactory.getInstance()
					.getBusinessService(BusinessServiceName.MasterDataService))
					.retrieveMasterEntities(entityId, localeId);
		return "";
	}

	private void setSpouseOrFatherName(HttpServletRequest request,
			ClientBO clientBO) throws NumberFormatException,
			PersistenceException, ServiceException {
		for (ClientNameDetailEntity clientNameDetailEntity : clientBO
				.getNameDetailSet()) {
			if (clientNameDetailEntity.getNameType().shortValue() != ClientConstants.CLIENT_NAME_TYPE) {
				SessionUtils.setAttribute(
						ClientConstants.SPOUSE_FATHER_NAME_VALUE,
						clientNameDetailEntity.getDisplayName(), request
								.getSession());
				SessionUtils
						.setAttribute(ClientConstants.SPOUSE_FATHER_VALUE,
								findMasterEntity(request.getSession(),
										ClientConstants.SPOUSE_FATHER_ENTITY,
										clientNameDetailEntity.getNameType())
										.getName(), request.getSession());
				break;
			}
		}
	}

	private MasterDataEntity findMasterEntity(HttpSession session,
			String collectionName, Short value) {
		List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
				.getAttribute(collectionName, session);
		for (MasterDataEntity entity : entities)
			if (entity.getId().equals(value))
				return entity;
		return null;
	}

	private PersonnelBusinessService getPersonnelBusinessService()
			throws ServiceException {
		return (PersonnelBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Personnel);
	}
}

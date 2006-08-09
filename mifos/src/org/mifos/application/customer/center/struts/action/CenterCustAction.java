/**

 * CenterCustomerAction.java    version: 1.0

 

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

package org.mifos.application.customer.center.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class CenterCustAction extends BaseAction {
	@Override
	protected BusinessService getService() throws ServiceException {
		return getCustomerBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	private CustomerBusinessService getCustomerBusinessService()
			throws ServiceException {
		return (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
	}

	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.chooseOffice_success
				.toString());
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		doCleanUp(request, actionForm);
		request.getSession().removeAttribute(CenterConstants.CENTER_MEETING);
		loadCreateMasterData(actionForm, request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadMeeting(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loadMeeting_success
				.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		MeetingBO meeting = (MeetingBO) SessionUtils.getAttribute(
				CenterConstants.CENTER_MEETING, request.getSession());
		List<CustomFieldView> customFields = actionForm.getCustomFields();
		convertCustomFieldDateToUniformPattern(customFields, request);

		CenterBO center = new CenterBO(getUserContext(request), actionForm
				.getDisplayName(), actionForm.getAddress(), customFields,
				actionForm.getFeesToApply(), actionForm.getOfficeIdValue(),
				meeting, actionForm.getLoanOfficerIdValue());
		center.save();

		actionForm.setCustomerId(center.getCustomerId().toString());
		actionForm.setGlobalCustNum(center.getGlobalCustNum());
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	private void convertCustomFieldDateToUniformPattern(
			List<CustomFieldView> customFields, HttpServletRequest request) {
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request
						.getSession());
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			if (customFieldDef.getFieldType().equals(
					CustomFieldType.DATE.getValue())) {
				for (CustomFieldView customField : customFields) {
					if (StringUtils.isNullAndEmptySafe(customField
							.getFieldValue())
							&& customField.getFieldId().equals(
									customFieldDef.getFieldId())) {
						customField.convertDateToUniformPattern(getUserContext(
								request).getPereferedLocale());
					}
				}
			}

		}
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	public ActionForward Cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		ActionForwards forward = null;
		if (CenterConstants.INPUT_CREATE.equals(actionForm.getInput()))
			forward = ActionForwards.cancel_success;
		return mapping.findForward(forward.toString());
	}

	private void loadCreateMasterData(CenterCustActionForm actionForm,
			HttpServletRequest request) throws ApplicationException,
			SystemException {
		loadLoanOfficers(request, actionForm.getOfficeIdValue());
		loadCustomFields(actionForm, request);
		loadFees(actionForm, request);
	}

	private void loadLoanOfficers(HttpServletRequest request, Short officeId)
			throws ServiceException {
		PersonnelBusinessService personnelService = (PersonnelBusinessService) ServiceFactory
				.getInstance()
				.getBusinessService(BusinessServiceName.Personnel);
		UserContext userContext = getUserContext(request);
		List<PersonnelView> personnelList = personnelService
				.getActiveLoanOfficersInBranch(officeId, userContext.getId(),
						userContext.getLevelId());
		SessionUtils.setAttribute(CustomerConstants.LOAN_OFFICER_LIST,
				personnelList, request.getSession());
	}

	private void loadCustomFields(CenterCustActionForm actionForm,
			HttpServletRequest request) throws SystemException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(EntityType.CENTER);
		// Set Default values for custom fields
		int i = 0;
		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			actionForm.getCustomField(i).setFieldId(fieldDef.getFieldId());
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldId().equals(
							CustomFieldType.DATE.getValue())) {
				actionForm.getCustomField(i).setFieldValue(
						DateHelper.getUserLocaleDate(getUserContext(request)
								.getPereferedLocale(), fieldDef
								.getDefaultValue()));
			} else
				actionForm.getCustomField(i).setFieldValue(
						fieldDef.getDefaultValue());
			i++;
		}

		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request.getSession());
	}

	private void loadFees(CenterCustActionForm actionForm,
			HttpServletRequest request) throws FeeException, ServiceException {
		FeeBusinessService feeService = (FeeBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.FeesService);
		List<FeeBO> fees = feeService
				.retrieveCustomerFeesByCategaroyType(FeeCategory.CENTER);
		List<FeeView> additionalFees = new ArrayList<FeeView>();
		List<FeeView> defaultFees = new ArrayList<FeeView>();
		for (FeeBO fee : fees) {
			if (fee.isCustomerDefaultFee())
				defaultFees.add(new FeeView(fee));
			else
				additionalFees.add(new FeeView(fee));
		}
		actionForm.setDefaultFees(defaultFees);
		SessionUtils.setAttribute(CustomerConstants.ADDITIONAL_FEES_LIST,
				additionalFees, request.getSession());
	}

	private void doCleanUp(HttpServletRequest request, CenterCustActionForm actionForm){
		clearActionForm(actionForm);
		SessionUtils.setAttribute(CenterConstants.CENTER_MEETING, null, request.getSession());
	}
	
	private void clearActionForm(CenterCustActionForm actionForm) {
		actionForm.setDefaultFees(new ArrayList<FeeView>());
		actionForm.setAdditionalFees(new ArrayList<FeeView>());
		actionForm.setAddress(new Address());
		actionForm.setDisplayName(null);
		actionForm.setMfiJoiningDate(null);
		actionForm.setGlobalCustNum(null);
		actionForm.setCustomerId(null);
		actionForm.setExternalId(null);
		actionForm.setLoanOfficerId(null);
		actionForm.setCustomFields(new ArrayList<CustomFieldView>());
	}
}

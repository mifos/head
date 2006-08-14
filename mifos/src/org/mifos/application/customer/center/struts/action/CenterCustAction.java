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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.center.dao.CenterDAO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.valueobjects.Customer;
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
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class CenterCustAction extends BaseAction {
	@Override
	protected BusinessService getService() throws ServiceException {
		return getCenterBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	private CenterBusinessService getCenterBusinessService()
			throws ServiceException {
		return (CenterBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Center);
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
		doCleanUp(actionForm, request);
		request.getSession().removeAttribute(CenterConstants.CENTER_MEETING);
		loadCreateMasterData(actionForm, request);
		actionForm.setMfiJoiningDate(DateHelper.getCurrentDate(getUserContext(request).getPereferedLocale()));
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
		UserContext userContext = getUserContext(request);
		convertCustomFieldDateToUniformPattern(customFields, userContext.getPereferedLocale());
		
		CenterBO center = new CenterBO(userContext,
				actionForm.getDisplayName(), actionForm.getAddress(),
				customFields, actionForm.getFeesToApply(), actionForm
						.getExternalId(),
				getDateFromString(actionForm.getMfiJoiningDate(), userContext
						.getPereferedLocale()), actionForm.getOfficeIdValue(),
				meeting, actionForm.getLoanOfficerIdValue());
		center.save();

		actionForm.setCustomerId(center.getCustomerId().toString());
		actionForm.setGlobalCustNum(center.getGlobalCustNum());
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm((CenterCustActionForm) form);
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		CenterBO centerBO = getCenterBusinessService().getCenter(center.getCustomerId());
		
		
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,centerBO
				, request
						.getSession());
		loadUpdateMasterData(center.getOffice().getOfficeId(), request);
		setValuesInActionForm((CenterCustActionForm) form, request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	private void setValuesInActionForm(CenterCustActionForm actionForm,
			HttpServletRequest request) {
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		actionForm.setLoanOfficerId(center.getPersonnel().getPersonnelId()
				.toString());
		actionForm.setCustomerId(center.getCustomerId().toString());
		actionForm.setGlobalCustNum(center.getGlobalCustNum());
		actionForm.setExternalId(center.getExternalId());

		if (center.getMfiJoiningDate() != null)
			actionForm.setMfiJoiningDate(DateHelper.getUserLocaleDate(
					getUserContext(request).getPereferedLocale(), center
							.getMfiJoiningDate().toString()));

		actionForm.setAddress(center.getAddress());
		actionForm.setCustomerPositions(createCustomerPositionViews(center
				.getCustomerPositions(), request));
		actionForm.setCustomFields(createCustomFieldViews(center
				.getCustomFields(), request));
	}

	private List<CustomerPositionView> createCustomerPositionViews(
			Set<CustomerPositionEntity> custPosEntities, HttpServletRequest request) {
		List<PositionEntity> positions = (List<PositionEntity>)SessionUtils.getAttribute(CustomerConstants.POSITIONS, request.getSession());
		List<CustomerPositionView> customerPositions = new ArrayList<CustomerPositionView>();
		for(PositionEntity position: positions)
			for (CustomerPositionEntity entity : custPosEntities){
				if(position.getId().equals(entity.getPosition().getId())){
					if(entity.getCustomer()!=null)
						customerPositions.add(new CustomerPositionView(entity.getCustomer()
							.getCustomerId(), entity.getPosition().getId()));
					else
						customerPositions.add(new CustomerPositionView(null, entity.getPosition().getId()));
				}
			}
		return customerPositions;
	}

	private List<CustomFieldView> createCustomFieldViews(
			Set<CustomerCustomFieldEntity> customFieldEntities,
			HttpServletRequest request) {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request
						.getSession());

		Locale locale = getUserContext(request).getPereferedLocale();
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			for (CustomerCustomFieldEntity customFieldEntity : customFieldEntities) {
				if (customFieldDef.getFieldId().equals(
						customFieldEntity.getFieldId())) {
					if (customFieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
						customFields.add(new CustomFieldView(customFieldEntity
								.getFieldId(), DateHelper.getUserLocaleDate(
								locale, customFieldEntity.getFieldValue()),
								customFieldDef.getFieldType()));
					} else {
						customFields
								.add(new CustomFieldView(customFieldEntity
										.getFieldId(), customFieldEntity
										.getFieldValue(), customFieldDef
										.getFieldType()));
					}
				}
			}
		}
		return customFields;
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}
	
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		if(!center.getPersonnel().getPersonnelId().equals(actionForm.getLoanOfficerIdValue()))
			   center.setPersonnel(getPersonnelBusinessService().getPersonnel(actionForm.getLoanOfficerIdValue()));
		center.setExternalId(actionForm.getExternalId());
		if(actionForm.getMfiJoiningDate()!=null)
			center.setMfiJoiningDate(getDateFromString(actionForm.getMfiJoiningDate(), getUserContext(request)
				.getPereferedLocale()));
		center.updateAddress(actionForm.getAddress());
		convertCustomFieldDateToUniformPattern(actionForm.getCustomFields(), getUserContext(request).getPereferedLocale());
		for(CustomFieldView fieldView : actionForm.getCustomFields())
			for(CustomerCustomFieldEntity fieldEntity: center.getCustomFields())
				if(fieldView.getFieldId().equals(fieldEntity.getFieldId()))
					fieldEntity.setFieldValue(fieldView.getFieldValue());
		
		for(CustomerPositionView positionView: actionForm.getCustomerPositions()){
			boolean isPositionFound = false;
			for(CustomerPositionEntity positionEntity: center.getCustomerPositions()){
				if(positionView.getPositionId().equals(positionEntity.getPosition().getId())){
					positionEntity.setCustomer(findClientObject(request, positionView.getCustomerId()));
					isPositionFound = true;
					break;
				}
				
			}
			if(!isPositionFound){
				center.addCustomerPosition(new CustomerPositionEntity(findPosition(request,positionView.getPositionId()),findClientObject(request,positionView.getCustomerId()), center));
			}
		}
		
		center.setUserContext(getUserContext(request));
		center.update();
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private CustomerBO findClientObject(HttpServletRequest request, Integer customerId){
		if(customerId!=null){
			List<CustomerBO> clientList = (List<CustomerBO>) SessionUtils.getAttribute(CustomerConstants.CLIENT_LIST, request.getSession());
			for(CustomerBO customer: clientList){
				if(customer.getCustomerId().equals(customerId))
					return customer;
			}
		}
		return null;
	}
	
	private PositionEntity findPosition(HttpServletRequest request, Short positionId){
		if(positionId!=null){
			List<PositionEntity> positions = (List<PositionEntity>) SessionUtils.getAttribute(CustomerConstants.POSITIONS, request.getSession());
			for(PositionEntity position : positions){
				if(position.getId().equals(positionId))
					return position;
			}
		}
		return null;
	}

	private PersonnelBusinessService getPersonnelBusinessService()throws ServiceException{
		return (PersonnelBusinessService) ServiceFactory.getInstance()
		.getBusinessService(BusinessServiceName.Personnel);
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		ActionForwards forward = null;
		if (actionForm.getInput().equals(Methods.create.toString()))
			forward = ActionForwards.cancel_success;
		else if (actionForm.getInput().equals(Methods.manage.toString()))
			forward = ActionForwards.editcancel_success;
		return mapping.findForward(forward.toString());
	}

	private void loadCreateMasterData(CenterCustActionForm actionForm,
			HttpServletRequest request) throws ApplicationException,
			SystemException {
		loadLoanOfficers(actionForm.getOfficeIdValue(), request);
		loadCreateCustomFields(actionForm, request);
		loadFees(actionForm, request);
	}

	private void loadUpdateMasterData(Short officeId, HttpServletRequest request)
			throws ApplicationException, SystemException {
		loadLoanOfficers(officeId, request);
		loadCustomFieldDefinitions(request);
		loadPositions(request);
		loadClients(request);
	}

	private void loadLoanOfficers(Short officeId, HttpServletRequest request)
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

	private void loadCreateCustomFields(CenterCustActionForm actionForm,
			HttpServletRequest request) throws SystemException {
		loadCustomFieldDefinitions(request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request
						.getSession());
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						DateHelper.getUserLocaleDate(getUserContext(request)
								.getPereferedLocale(), fieldDef
								.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}

	private void loadCustomFieldDefinitions(HttpServletRequest request)
			throws SystemException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(EntityType.CENTER);
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

	private void loadPositions(HttpServletRequest request)
			throws PersistenceException, ServiceException {
		SessionUtils.setAttribute(CustomerConstants.POSITIONS,
				getMasterEntities(PositionEntity.class, getUserContext(request)
						.getLocaleId()), request.getSession());
	}

	private void loadClients(HttpServletRequest request)
			throws PropertyNotFoundException, SystemException {
		CenterBO center = (CenterBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		List<CustomerBO> customerList = center.getChildren(CustomerLevel.CLIENT.getValue());
		List<CustomerBO> customerListToPopulate = new ArrayList<CustomerBO>();
		for(CustomerBO customer: customerList){
			if(!(customer.getStatus().equals(CustomerStatus.CLIENT_CANCELLED) || customer.getStatus().equals(CustomerStatus.CLIENT_CLOSED)))
				customerListToPopulate.add(customer);
		}
		SessionUtils.setAttribute(CustomerConstants.CLIENT_LIST, customerListToPopulate , request
				.getSession());
	}
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		
		
		CustomerBusinessService customerBusinessService = ((CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer));
		CenterBO centerBO =(CenterBO) customerBusinessService.getBySystemId(actionForm.getGlobalCustNum(),CustomerLevel.CENTER.getValue());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,centerBO
				, request
						.getSession());
		centerBO.getCustomerStatus().setLocaleId(getUserContext(request).getLocaleId());
		SessionUtils.setAttribute(CenterConstants.GROUP_LIST,centerBO.getChildren(CustomerLevel.GROUP.getValue()),request
				.getSession());
	  CenterPerformanceHistory centerPerformanceHistory = 	customerBusinessService.getCenterPerformanceHistory(centerBO.getSearchId(),centerBO.getOffice().getOfficeId());
	  SessionUtils.setAttribute(CenterConstants.PERFORMANCE_HISTORY,centerPerformanceHistory,request
				.getSession());
	  
	  //	TODO : get value object 
	  CenterDAO centerDAO =  new CenterDAO();
	  Center center = centerDAO.findBySystemId(centerBO.getGlobalCustNum());
	  
	  
	  SessionUtils.setAttribute("CustomerVO",center
				, request
						.getSession());  
	  
	  SessionUtils.setAttribute(CustomerConstants.LINK_VALUES,getLinkValues(center),request
						.getSession());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	private void convertCustomFieldDateToUniformPattern(
			List<CustomFieldView> customFields, Locale locale) {		
		for (CustomFieldView customField : customFields) {
			if (customField.getFieldType().equals(CustomFieldType.DATE.getValue()) && StringUtils.isNullAndEmptySafe(customField
					.getFieldValue()))
					customField.convertDateToUniformPattern(locale);
		}
	}

	private void doCleanUp(CenterCustActionForm actionForm,
			HttpServletRequest request) {
		clearActionForm(actionForm);
		SessionUtils.setAttribute(CenterConstants.CENTER_MEETING, null, request
				.getSession());
	}

	private Date getDateFromString(String strDate, Locale locale) {
		Date date = null;
		if (StringUtils.isNullAndEmptySafe(strDate))
			date = new Date(DateHelper.getLocaleDate(locale, strDate).getTime());
		return date;
	}

	private void clearActionForm(CenterCustActionForm actionForm) {
		actionForm.setDefaultFees(new ArrayList<FeeView>());
		actionForm.setAdditionalFees(new ArrayList<FeeView>());
		actionForm.setCustomerPositions(new ArrayList<CustomerPositionView>());
		actionForm.setCustomFields(new ArrayList<CustomFieldView>());
		actionForm.setAddress(new Address());
		actionForm.setDisplayName(null);
		actionForm.setMfiJoiningDate(null);
		actionForm.setGlobalCustNum(null);
		actionForm.setCustomerId(null);
		actionForm.setExternalId(null);
		actionForm.setLoanOfficerId(null);
	}
	
	//TODO: remove this function after complete migration 
	LinkParameters getLinkValues(Center center){
		LinkParameters linkParams = new LinkParameters();
		linkParams.setCustomerId(center.getCustomerId());
		linkParams.setCustomerName(center.getDisplayName());
		linkParams.setGlobalCustNum(center.getGlobalCustNum());
		linkParams.setCustomerOfficeId(center.getOffice().getOfficeId());
		linkParams.setCustomerOfficeName(center.getOffice().getOfficeName());
		linkParams.setLevelId(CustomerConstants.CENTER_LEVEL_ID);
		Customer parent = center.getParentCustomer();
		if(parent!=null){
			linkParams.setCustomerParentGCNum(parent.getGlobalCustNum());
			linkParams.setCustomerParentName(parent.getDisplayName());
		}
		return linkParams;
	}
}

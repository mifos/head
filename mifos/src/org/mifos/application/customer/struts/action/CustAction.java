/**

* CustAction.java version: 1.0



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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class CustAction extends BaseAction {
	
	@Override
	protected BusinessService getService() throws ServiceException {
		return null;
	}
	
	protected void loadCreateCustomFields(CustomerActionForm actionForm, EntityType entityType,
			HttpServletRequest request) throws SystemException, ApplicationException {
		loadCustomFieldDefinitions(entityType ,request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
	
	protected List<CustomFieldView> createCustomFieldViews(
			Set<CustomerCustomFieldEntity> customFieldEntities,
			HttpServletRequest request)throws ApplicationException {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
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
	
	protected void loadCustomFieldDefinitions(EntityType entityType , HttpServletRequest request )
			throws SystemException, ApplicationException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(entityType);
		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request);
	}

	protected void loadFees(CustomerActionForm actionForm,
			HttpServletRequest request) throws FeeException, ServiceException , ApplicationException{
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
				additionalFees, request);
	}
	
	protected void loadFormedByPersonnel(Short officeId,
			HttpServletRequest request) throws SystemException, CustomerException, ApplicationException{
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
		List<PersonnelView> formedByPersonnel;
		try {
			formedByPersonnel = customerService.getFormedByPersonnel(ClientConstants.LOAN_OFFICER_LEVEL, officeId);
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		} 
		SessionUtils.setAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,	formedByPersonnel, request);
		
	}
	protected void loadLoanOfficers(Short officeId, HttpServletRequest request)
	throws ServiceException , ApplicationException{
		PersonnelBusinessService personnelService = (PersonnelBusinessService) ServiceFactory
				.getInstance()
				.getBusinessService(BusinessServiceName.Personnel);
		UserContext userContext = getUserContext(request);
		List<PersonnelView> personnelList = personnelService
				.getActiveLoanOfficersInBranch(officeId, userContext.getId(),
						userContext.getLevelId());
		SessionUtils.setAttribute(CustomerConstants.LOAN_OFFICER_LIST,
				personnelList, request);
	}
	
	protected void convertCustomFieldDateToUniformPattern(
			List<CustomFieldView> customFields, Locale locale) {		
		for (CustomFieldView customField : customFields) {
			if (customField.getFieldType().equals(CustomFieldType.DATE.getValue()) && StringUtils.isNullAndEmptySafe(customField
					.getFieldValue()))
					customField.convertDateToUniformPattern(locale);
		}
	}
	
	protected void checkPermissionForCreate(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	
	protected boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId){
		return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(),userContext,recordOfficeId,recordLoanOfficerId);
	}
	
	protected List<CustomerPositionView> createCustomerPositionViews(
			Set<CustomerPositionEntity> custPosEntities, HttpServletRequest request) throws ApplicationException{
		List<PositionEntity> positions = (List<PositionEntity>)SessionUtils.getAttribute(CustomerConstants.POSITIONS, request);
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

	
	protected void loadPositions(HttpServletRequest request)
	throws PersistenceException, ServiceException, ApplicationException{
	SessionUtils.setAttribute(CustomerConstants.POSITIONS,
			getMasterEntities(PositionEntity.class, getUserContext(request)
					.getLocaleId()), request);
	}
	
	protected void loadClients(HttpServletRequest request)
		throws CustomerException, SystemException, ApplicationException{
	CustomerBO customerBO = (CustomerBO) SessionUtils.getAttribute(
			Constants.BUSINESS_KEY, request.getSession());
	List<CustomerBO> customerList;
	try {
		customerList = customerBO.getChildren(CustomerLevel.CLIENT.getValue());
	} catch (PersistenceException e) {
		throw new CustomerException(e);
	}
	List<CustomerBO> customerListToPopulate = new ArrayList<CustomerBO>();
	for(CustomerBO customer: customerList){
		if(!(customer.getStatus().equals(CustomerStatus.CLIENT_CANCELLED) || customer.getStatus().equals(CustomerStatus.CLIENT_CLOSED)))
			customerListToPopulate.add(customer);
	}
	SessionUtils.setAttribute(CustomerConstants.CLIENT_LIST, customerListToPopulate , request);
	}
}

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
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
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class CustAction extends BaseAction {
	
	@Override
	protected BusinessService getService() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void loadCreateCustomFields(CustomerActionForm actionForm, EntityType entityType,
			HttpServletRequest request) throws SystemException {
		loadCustomFieldDefinitions(entityType ,request);
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

	public void loadCustomFieldDefinitions(EntityType entityType , HttpServletRequest request )
			throws SystemException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(entityType);
		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request.getSession());
	}

	public void loadFees(CustomerActionForm actionForm,
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
	
	public void loadFormedByPersonnel(CustomerActionForm actionForm,
			HttpServletRequest request) throws SystemException{
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
		List<PersonnelView> formedByPersonnel = customerService.getFormedByPersonnel(ClientConstants.LOAN_OFFICER_LEVEL, actionForm.getOfficeIdValue());
		SessionUtils.setAttribute(CustomerConstants.FORMEDBY_LOAN_OFFICER_LIST,	formedByPersonnel, request.getSession());
		
	}
	public void loadLoanOfficers(Short officeId, HttpServletRequest request)
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
	
	public Date getDateFromString(String strDate, Locale locale) {
		Date date = null;
		if (StringUtils.isNullAndEmptySafe(strDate))
			date = new Date(DateHelper.getLocaleDate(locale, strDate).getTime());
		return date;
	}
	
	public void convertCustomFieldDateToUniformPattern(
			List<CustomFieldView> customFields, Locale locale) {		
		for (CustomFieldView customField : customFields) {
			if (customField.getFieldType().equals(CustomFieldType.DATE.getValue()) && StringUtils.isNullAndEmptySafe(customField
					.getFieldValue()))
					customField.convertDateToUniformPattern(locale);
		}
	}



}

/**

* GroupCustAction.java version: 1.0



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

package org.mifos.application.customer.group.struts.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.struts.actionforms.GroupActionForm;
import org.mifos.application.customer.group.struts.actionforms.GroupCustActionForm;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;

public class GroupCustAction extends CustAction {

	private CustomerBusinessService customerService;

	public GroupCustAction() throws ServiceException {
		customerService = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer);
	}
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}	
	
	@Override
	protected BusinessService getService() throws ServiceException {
		return (CustomerBusinessService) ServiceFactory
		.getInstance().getBusinessService(BusinessServiceName.Customer);
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm((GroupCustActionForm) form);
		GroupBO group = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		GroupBO groupBO = (GroupBO) customerService.getBySystemId(group
				.getGlobalCustNum(), CustomerLevel.GROUP.getValue());
		group = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, groupBO, request
				.getSession());
		
		loadUpdateMasterData(groupBO.getOffice().getOfficeId(), request);
		setValuesInActionForm((GroupCustActionForm) form, request);		
		return mapping.findForward(ActionForwards.manage_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previewManage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(ActionForwards.previewManage_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previousManage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		return mapping.findForward(ActionForwards.previousManage_success.toString());
	}
	
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupBO group = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request.getSession());
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		Date trainedDate = null; 
		if(actionForm.getTrainedDate()!=null)
			trainedDate = getDateFromString(actionForm.getTrainedDate(), getUserContext(request)
				.getPereferedLocale());
			
		//group.update(getUserContext(request),actionForm.getDisplayName(), actionForm.getLoanOfficerIdValue(), actionForm.getExternalId(),actionForm.getTrained(),trainedDate, actionForm.getAddress(), actionForm.getCustomFields(), actionForm.getCustomerPositions());
		return mapping.findForward(ActionForwards.update_success.toString());
	}
	
	private void loadUpdateMasterData(Short officeId, HttpServletRequest request)throws ApplicationException, SystemException {
		 if(!Configuration.getInstance().getCustomerConfig(getUserContext(request).getBranchId()).isCenterHierarchyExists()){
			 loadLoanOfficers(officeId, request);
		 }
		loadCustomFieldDefinitions(EntityType.GROUP, request);
		loadPositions(request);
		loadClients(request);
	}
	
	private void setValuesInActionForm(GroupCustActionForm actionForm,
			HttpServletRequest request) throws Exception{
		GroupBO group = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		if (group.getPersonnel() != null) {
			actionForm.setLoanOfficerId(group.getPersonnel().getPersonnelId()
					.toString());
		}
		actionForm.setDisplayName(group.getDisplayName());
		actionForm.setCustomerId(group.getCustomerId().toString());
		actionForm.setGlobalCustNum(group.getGlobalCustNum());
		actionForm.setExternalId(group.getExternalId());
		actionForm.setAddress(group.getAddress());
		actionForm.setCustomerPositions(createCustomerPositionViews(group
				.getCustomerPositions(), request));
		actionForm.setCustomFields(createCustomFieldViews(group
				.getCustomFields(), request));
		/*if (group.isTrained())
			actionForm.setTrained(GroupConstants.TRAINED);
		else
			actionForm.setTrained(GroupConstants.NOT_TRAINED);
		if (group.getTrainedDate() != null)
			actionForm.setTrainedDate(DateHelper.getUserLocaleDate(
					getUserContext(request).getPereferedLocale(), group.getTrainedDate().toString()));*/
	}
		
	private void clearActionForm(GroupCustActionForm actionForm) {
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
		actionForm.setTrained(null);
		actionForm.setTrainedDate(null);
		actionForm.setFormedByPersonnel(null);
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}
}

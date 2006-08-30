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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.center.struts.actionforms.CenterCustActionForm;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.service.GroupBusinessService;
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
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class GroupCustAction extends CustAction {

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return getGroupBusinessService();
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws PageExpiredException, CustomerException {
		GroupCustActionForm actionForm = (GroupCustActionForm) form;
		GroupBO groupBO;
		try {
			groupBO = (GroupBO) getGroupBusinessService().getGroupBySystemId(
					actionForm.getGlobalCustNum());
		} catch (ServiceException se) {
			throw new CustomerException(se);
		}
		groupBO.setUserContext(getUserContext(request));
		groupBO.getCustomerStatus().setLocaleId(
				getUserContext(request).getLocaleId());
		loadMasterDataForDetailsPage(request, groupBO, getUserContext(request)
				.getLocaleId());
		setLocaleForMasterEntities(groupBO, getUserContext(request)
				.getLocaleId());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, groupBO, request);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm((GroupCustActionForm) form);
		GroupBO group = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		GroupBO groupBO = (GroupBO) getCustomerBusinessService().getBySystemId(
				group.getGlobalCustNum(), CustomerLevel.GROUP.getValue());
		group = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, groupBO, request
				.getSession());

		loadUpdateMasterData(groupBO.getOffice().getOfficeId(), request);
		setValuesInActionForm((GroupCustActionForm) form, request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previewManage_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previousManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previousManage_success
				.toString());
	}

	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GroupBO group = (GroupBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		CenterCustActionForm actionForm = (CenterCustActionForm) form;
		Date trainedDate = null;
		if (actionForm.getTrainedDate() != null)
			trainedDate = getDateFromString(actionForm.getTrainedDate(),
					getUserContext(request).getPereferedLocale());

		// group.update(getUserContext(request),actionForm.getDisplayName(),
		// actionForm.getLoanOfficerIdValue(),
		// actionForm.getExternalId(),actionForm.getTrained(),trainedDate,
		// actionForm.getAddress(), actionForm.getCustomFields(),
		// actionForm.getCustomerPositions());
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private void loadMasterDataForDetailsPage(HttpServletRequest request,
			GroupBO groupBO, Short localeId) throws PageExpiredException,
			CustomerException {
		SessionUtils.setAttribute(GroupConstants.IS_GROUP_LOAN_ALLOWED,
				Configuration.getInstance().getCustomerConfig(
						groupBO.getOffice().getOfficeId())
						.canGroupApplyForLoan(), request);
		SessionUtils.setAttribute(GroupConstants.CENTER_HIERARCHY_EXIST,
				Configuration.getInstance().getCustomerConfig(
						groupBO.getOffice().getOfficeId())
						.isCenterHierarchyExists(), request);
		SessionUtils.setAttribute(ClientConstants.LOANCYCLECOUNTER,
				getCustomerBusinessService().fetchLoanCycleCounter(
						groupBO.getCustomerId()), request);
		List<LoanBO> loanAccounts = groupBO.getOpenLoanAccounts();
		List<SavingsBO> savingsAccounts = groupBO.getOpenSavingAccounts();
		setLocaleIdToLoanStatus(loanAccounts, localeId);
		setLocaleIdToSavingsStatus(savingsAccounts, localeId);
		SessionUtils.setAttribute(GroupConstants.GROUPLOANACCOUNTSINUSE,
				loanAccounts, request);
		SessionUtils.setAttribute(GroupConstants.GROUPSAVINGSACCOUNTSINUSE,
				savingsAccounts, request);
		try {
			SessionUtils
					.setAttribute(
							GroupConstants.CLIENT_LIST,
							groupBO
									.getAllCustomerOtherThanCancelledAndClosed(CustomerLevel.CLIENT),
							request);
			loadCustomFieldDefinitions(EntityType.GROUP, request);
		} catch (PersistenceException pe) {
			throw new CustomerException(pe);
		} catch (SystemException se) {
			throw new CustomerException(se);
		} catch (ApplicationException ae) {
			throw new CustomerException(ae);
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

	private void setLocaleForMasterEntities(GroupBO groupBO, Short localeId) {
		for (CustomerPositionEntity customerPositionEntity : groupBO
				.getCustomerPositions())
			customerPositionEntity.getPosition().setLocaleId(localeId);
	}

	private GroupBusinessService getGroupBusinessService()
			throws ServiceException {
		return (GroupBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Group);
	}

	private void loadUpdateMasterData(Short officeId, HttpServletRequest request)
			throws ApplicationException, SystemException {
		if (!Configuration.getInstance().getCustomerConfig(
				getUserContext(request).getBranchId())
				.isCenterHierarchyExists()) {
			loadLoanOfficers(officeId, request);
		}
		loadCustomFieldDefinitions(EntityType.GROUP, request);
		loadPositions(request);
		loadClients(request);
	}

	private void setValuesInActionForm(GroupCustActionForm actionForm,
			HttpServletRequest request) throws Exception {
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
		/*
		 * if (group.isTrained()) actionForm.setTrained(GroupConstants.TRAINED);
		 * else actionForm.setTrained(GroupConstants.NOT_TRAINED); if
		 * (group.getTrainedDate() != null)
		 * actionForm.setTrainedDate(DateHelper.getUserLocaleDate(
		 * getUserContext(request).getPereferedLocale(),
		 * group.getTrainedDate().toString()));
		 */
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

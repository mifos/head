/**

 * AccountStatusAction.java    version: xxx



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
package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.AccountStatusActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AccountStatusAction extends BaseAction {
	private MasterDataService masterService;

	private LoanBusinessService loanService;

	public AccountStatusAction() {
		super();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return new OfficeBusinessService();
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("ChangeAccountStatus");
		security.allow("load", SecurityConstants.CAN_APPROVE_LOANS_IN_BULK);
		security.allow("searchResults", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("getLoanOfficers", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		cleanUp(form, request);

		masterService = (MasterDataService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.MasterDataService);

		UserContext userContext = getUserContext(request);
		List<OfficeView> activeBranches = masterService
				.getActiveBranches(userContext.getBranchId());

		SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST,
				activeBranches, request);

		if (activeBranches.size() == 1) {
			List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(
					userContext, activeBranches.get(0).getOfficeId());
			SessionUtils.setCollectionAttribute(LoanConstants.LOAN_OFFICERS,
					loanOfficers, request);
		} else {
			SessionUtils.setAttribute(LoanConstants.LOAN_OFFICERS,
					new ArrayList<PersonnelView>(), request);
		}

		return mapping.findForward(ActionForwards.changeAccountStatus_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward searchResults(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;

		List<LoanBO> searchResults;

		searchResults = getSearchResults(accountStatusActionForm.getOfficeId(),
				accountStatusActionForm.getPersonnelId(),
				accountStatusActionForm.getType(), accountStatusActionForm
						.getCurrentStatus());
		for (LoanBO loanBO : searchResults)
			loanBO.getAccountState().setLocaleId(
					getUserContext(request).getLocaleId());
		if (searchResults == null) {
			throw new AccountException(LoanConstants.NOSEARCHRESULTS);
		}
		if (searchResults.size() == 0) {
			return mapping.findForward(ActionForwards.noresultfound.toString());
		}

		SessionUtils.setCollectionAttribute(LoanConstants.SEARCH_RESULTS, searchResults,
				request);

		return mapping
				.findForward(ActionForwards.changeAccountStatusSearch_success
						.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;

		List accountList = updateAccountsStatus(accountStatusActionForm
				.getAccountRecords(), accountStatusActionForm.getNewStatus(),
				accountStatusActionForm.getComments(), getUserContext(request),
				new CustomerPersistence());
		request.setAttribute(LoanConstants.ACCOUNTS_LIST, accountList);

		return mapping
				.findForward(ActionForwards.changeAccountStatusConfirmation_success
						.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getLoanOfficers(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse httpservletresponse) throws Exception {
		AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;
		Short officeId = Short.valueOf(accountStatusActionForm.getOfficeId());
		List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(
				getUserContext(request), officeId);
		SessionUtils.setCollectionAttribute(LoanConstants.LOAN_OFFICERS, loanOfficers,
				request);

		if (officeId != null) {
			AccountStateEntity accountStateEntity = null;
			if (ProcessFlowRules.isLoanPendingApprovalStateEnabled()) {
				accountStateEntity = (AccountStateEntity) new MasterDataService()
						.getMasterDataEntity(AccountStateEntity.class,
								AccountState.LOAN_PENDING_APPROVAL.getValue());
			} else {
				accountStateEntity = (AccountStateEntity) new MasterDataService()
						.getMasterDataEntity(AccountStateEntity.class,
								AccountState.LOAN_PARTIAL_APPLICATION
										.getValue());
			}
			accountStateEntity.setLocaleId(getUserContext(request)
					.getLocaleId());
			SessionUtils.setAttribute(LoanConstants.LOANACCOUNTSTAES,
					accountStateEntity, request);
		}

		return mapping.findForward(ActionForwards.changeAccountStatus_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");

		if (method.equalsIgnoreCase(Methods.searchResults.toString())) {
			return mapping
					.findForward(ActionForwards.changeAccountStatus_success
							.toString());
		}

		if (method.equalsIgnoreCase(Methods.update.toString())) {
			return mapping
					.findForward(ActionForwards.changeAccountStatusSearch_success
							.toString());
		}

		return null;
	}

	private List<PersonnelView> loadLoanOfficersForBranch(
			UserContext userContext, Short officeId) throws Exception {
		masterService = (MasterDataService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.MasterDataService);

		return masterService.getListOfActiveLoanOfficers(
				PersonnelConstants.LOAN_OFFICER, officeId, userContext.getId(),
				userContext.getLevelId());
	}

	private List<LoanBO> getSearchResults(String officeId, String personnelId,
			String type, String currentStatus) throws Exception {
		loanService = new LoanBusinessService();

		return loanService.getSearchResults(officeId, personnelId, type,
				currentStatus);
	}

	private List updateAccountsStatus(List<String> accountList,
			String newStatus, String comments, UserContext userContext,
			CustomerPersistence customerPersistence)
			throws Exception {
		loanService = new LoanBusinessService();

		List accountNumbers = new ArrayList();

		for (String accountId : accountList) {
			if (!accountId.equals("")) {
				LoanBO loanBO = loanService.getAccount(Integer
						.parseInt(accountId));

				accountNumbers.add(loanBO.getGlobalAccountNum());
				loanBO.setUserContext(userContext);
				loanBO.changeStatus(getShortValue(newStatus), null, comments, customerPersistence);
				loanBO.update();
			}
		}

		return accountNumbers;
	}

	private void cleanUp(ActionForm form, HttpServletRequest request) {
		AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;
		accountStatusActionForm.setAccountRecords(new ArrayList<String>());
		accountStatusActionForm.setComments("");
		accountStatusActionForm.setCurrentStatus("");
		accountStatusActionForm.setLoadOfficer("");
		accountStatusActionForm.setNewStatus("");
		accountStatusActionForm.setOfficeId("");
		accountStatusActionForm.setOfficeName("");
		accountStatusActionForm.setPersonnelId("");
		accountStatusActionForm.setType("");
		request.setAttribute("session", null);
	}
}

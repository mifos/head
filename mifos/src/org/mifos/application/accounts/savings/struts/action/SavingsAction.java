/**

* SavingsAction.java    version: xxx



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

package org.mifos.application.accounts.savings.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTransactionHistoryView;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class SavingsAction extends AccountAppAction {

	private SavingsBusinessService savingsService;

	private MasterDataService masterDataService;

	private SavingsPrdBusinessService savingsPrdService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public SavingsAction() throws Exception {
		savingsService = (SavingsBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Savings);
		masterDataService = (MasterDataService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.MasterDataService);
		savingsPrdService = (SavingsPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.SavingsProduct);
	}

	@Override
	protected BusinessService getService() {
		return savingsService;
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward getPrdOfferings(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
		logger.debug(" customerId: " + savingsActionForm.getCustomerId());
		doCleanUp(savingsActionForm, request);
		CustomerBO customer = getCustomer(getIntegerValue(savingsActionForm
				.getCustomerId()));
		SessionUtils.setAttribute(SavingsConstants.CLIENT, customer, request);
		List<PrdOfferingView> savingPrds = savingsService.getSavingProducts(
				customer.getOffice(), customer.getCustomerLevel(),
				SavingsConstants.SAVINGS_ALL);
		SessionUtils.setAttribute(SavingsConstants.SAVINGS_PRD_OFFERINGS,
				savingPrds, request);
		logger.info(" Retrieved " + savingPrds.size()
				+ " Products for customerId: " + customer.getCustomerId());
		return mapping.findForward(AccountConstants.GET_PRDOFFERINGS_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
		logger.debug(" selectedPrdOfferingId: "
				+ savingsActionForm.getSelectedPrdOfferingId());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		loadMasterData(uc, request);
		loadPrdoffering(savingsActionForm, request);
		logger.info(" Data loaded successfully ");
		return mapping.findForward("load_success");
	}

	private void loadMasterData(UserContext uc, HttpServletRequest request)
			throws ApplicationException, SystemException {

		SessionUtils.setAttribute(MasterConstants.INTEREST_CAL_TYPES,
				masterDataService.retrieveMasterEntities(
						InterestCalcTypeEntity.class, uc.getLocaleId()),
				request);
		loadMasterDataPartail(uc, request);

	}

	private void loadMasterDataPartail(UserContext uc,
			HttpServletRequest request) throws PageExpiredException,
			ApplicationException, SystemException {
		SessionUtils.setAttribute(MasterConstants.SAVINGS_TYPE,
				masterDataService.retrieveMasterEntities(
						SavingsTypeEntity.class, uc.getLocaleId()), request);
		SessionUtils.setAttribute(MasterConstants.RECOMMENDED_AMOUNT_UNIT,
				masterDataService.retrieveMasterEntities(
						RecommendedAmntUnitEntity.class, uc.getLocaleId()),
				request);
		SessionUtils.setAttribute(SavingsConstants.CUSTOM_FIELDS,
				savingsService.retrieveCustomFieldsDefinition(), request);

	}

	private void loadPrdoffering(SavingsActionForm savingsActionForm,
			HttpServletRequest request) throws ServiceException,
			PageExpiredException {

		SavingsOfferingBO savingsOfferingBO = savingsPrdService
				.getSavingsProduct(getShortValue(savingsActionForm
						.getSelectedPrdOfferingId()));
		SessionUtils.setAttribute(SavingsConstants.PRDOFFCERING,
				savingsOfferingBO, request);

		savingsActionForm.setRecommendedAmount(savingsOfferingBO
				.getRecommendedAmount().toString());

	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward reLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
		logger.debug(" selectedPrdOfferingId: "
				+ (savingsActionForm).getSelectedPrdOfferingId());
		loadPrdoffering(savingsActionForm, request);
		logger.info("Data loaded successfully ");
		return mapping.findForward("load_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::preview()");
		CustomerBO customer = (CustomerBO) SessionUtils.getAttribute(
				SavingsConstants.CLIENT, request);
		customer = getCustomer(customer.getCustomerId());
		SessionUtils.setAttribute(SavingsConstants.IS_PENDING_APPROVAL,
				Configuration.getInstance().getAccountConfig(
						customer.getOffice().getOfficeId())
						.isPendingApprovalStateDefinedForSavings(), request
						.getSession());
		return mapping.findForward("preview_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction:previous()");
		return mapping.findForward("previous_success");
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsActionForm savingsActionForm = ((SavingsActionForm) form);
		logger.debug("In SavingsAction::create(), accountStateId: "
				+ savingsActionForm.getStateSelected());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		CustomerBO customer = (CustomerBO) SessionUtils.getAttribute(
				SavingsConstants.CLIENT, request);
		Integer version = customer.getVersionNo();
		customer = getCustomer(customer.getCustomerId());
		customer.setVersionNo(version);

		SavingsOfferingBO savingsOfferingBO = (SavingsOfferingBO) SessionUtils
				.getAttribute(SavingsConstants.PRDOFFCERING, request);
		version = savingsOfferingBO.getVersionNo();
		savingsOfferingBO = savingsPrdService
				.getSavingsProduct(savingsOfferingBO.getPrdOfferingId());
		savingsOfferingBO.setVersionNo(version);

		checkPermissionForCreate(getShortValue(savingsActionForm
				.getStateSelected()), uc, null, customer.getOffice()
				.getOfficeId(), customer.getPersonnel().getPersonnelId());

		SavingsBO saving = new SavingsBO(uc, savingsOfferingBO, customer,
				AccountState.getStatus(getShortValue(savingsActionForm
						.getStateSelected())), savingsActionForm
						.getRecommendedAmntValue(),
				getAccountCustomFieldView(savingsActionForm));
		saving.save();

		request.setAttribute(SavingsConstants.GLOBALACCOUNTNUM, saving
				.getGlobalAccountNum());
		request.setAttribute(SavingsConstants.RECORD_OFFICE_ID, saving
				.getOffice().getOfficeId());
		request.setAttribute(SavingsConstants.CLIENT_NAME, customer
				.getDisplayName());
		request.setAttribute(SavingsConstants.CLIENT_ID, customer
				.getCustomerId());
		request.setAttribute(SavingsConstants.CLIENT_LEVEL, customer
				.getCustomerLevel().getId());

		logger
				.info("In SavingsAction::create(), Savings object saved successfully ");
		return mapping.findForward("create_success");
	}

	private List<CustomFieldView> getAccountCustomFieldView(
			SavingsActionForm savingsActionForm) {
		List<CustomFieldView> customfield = null;
		if (savingsActionForm.getAccountCustomFieldSet() != null) {
			customfield = new ArrayList<CustomFieldView>();
			for (AccountCustomFieldEntity entity : savingsActionForm
					.getAccountCustomFieldSet()) {
				customfield.add(new CustomFieldView(entity.getFieldId(), entity
						.getFieldValue(), null));
			}
		}
		return customfield;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		SavingsActionForm actionForm = (SavingsActionForm) form;
		actionForm.setInput(null);
		logger.debug(" Retrieving for globalAccountNum: "
				+ actionForm.getGlobalAccountNum());
		SavingsBO savings = savingsService.findBySystemId(actionForm
				.getGlobalAccountNum());
		savings.getSavingPerformanceHistory();

		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		savings.getAccountState().setLocaleId(uc.getLocaleId());
		savings.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);

		loadMasterDataPartail(uc, request);

		SessionUtils.setAttribute(SavingsConstants.PRDOFFCERING, savings
				.getSavingsOffering(), request);
		actionForm.setRecommendedAmount(savings.getSavingsOffering()
				.getRecommendedAmount().toString());

		actionForm.clear();
		actionForm.setAccountCustomFieldSet(new ArrayList(savings
				.getAccountCustomFields()));
		SessionUtils.setAttribute(
				SavingsConstants.RECENTY_ACTIVITY_DETAIL_PAGE, savings
						.getRecentAccountActivity(3), request);
		SessionUtils.setAttribute(SavingsConstants.NOTES, savings
				.getRecentAccountNotes(), request);
		logger.info(" Savings object retrieved successfully");
		return mapping.findForward("get_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::edit()");

		return mapping.findForward("edit_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::editPreview()");
		return mapping.findForward("editPreview_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::editPrevious()");
		return mapping.findForward("editPrevious_success");
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::update()");
		SavingsActionForm actionForm = (SavingsActionForm) form;
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		Integer version = savings.getVersionNo();
		savings = new SavingsBusinessService().findById(savings.getAccountId());
		savings.setVersionNo(version);
		savings.setUserContext(uc);
		setInitialObjectForAuditLogging(savings);
		savings.update(actionForm.getRecommendedAmntValue(),
				getAccountCustomFieldView(actionForm));
		request.setAttribute(SavingsConstants.GLOBALACCOUNTNUM, savings
				.getGlobalAccountNum());
		logger
				.info("In SavingsAction::update(), Savings object updated successfully");

		doCleanUp(actionForm, request);
		return mapping.findForward("update_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getRecentActivity(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getRecentActivity()");
		// Check for order-by clause in AccountBO.hbm.xml and
		// AccountPayment.hbm.xml for accountPaymentSet and
		// accountTrxnSet. It should be set for the primay key column desc in
		// both. If stated is not there, the code
		// below will behave abnormally.
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(uc);
		SessionUtils.setAttribute(SavingsConstants.RECENTY_ACTIVITY_LIST,
				savings.getRecentAccountActivity(null), request);
		return mapping.findForward("getRecentActivity_success");
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward getTransactionHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getRecentActivity()");
		Short localeId = ((UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession()))
				.getLocaleId();
		String globalAccountNum = request.getParameter("globalAccountNum");
		SavingsBO savings = savingsService.findBySystemId(globalAccountNum);
		List<SavingsTransactionHistoryView> savingsTransactionHistoryViewList = new ArrayList<SavingsTransactionHistoryView>();
		// Check for order-by clause in AccountBO.hbm.xml,
		// AccountPayment.hbm.xml and AccountTrxnEntity.hbm.xml for
		// accountPaymentSet ,
		// accountTrxnSet and financialBoSet. They all should be set for their
		// primay key column desc in both. If stated is not there, the code
		// below will behave abnormally.
		Set<AccountPaymentEntity> accountPaymentSet = savings
				.getAccountPayments();
		for (AccountPaymentEntity accountPaymentEntity : accountPaymentSet) {
			Set<AccountTrxnEntity> accountTrxnEntitySet = accountPaymentEntity
					.getAccountTrxns();
			for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntitySet) {
				Set<FinancialTransactionBO> financialTransactionBOSet = accountTrxnEntity
						.getFinancialTransactions();
				for (FinancialTransactionBO financialTransactionBO : financialTransactionBOSet) {
					SavingsTransactionHistoryView savingsTransactionHistoryView = new SavingsTransactionHistoryView();
					savingsTransactionHistoryView
							.setTransactionDate(financialTransactionBO
									.getActionDate());
					savingsTransactionHistoryView
							.setPaymentId(accountTrxnEntity.getAccountPayment()
									.getPaymentId());
					savingsTransactionHistoryView
							.setAccountTrxnId(accountTrxnEntity
									.getAccountTrxnId());
					savingsTransactionHistoryView
							.setType(financialTransactionBO
									.getFinancialAction().getName(localeId));
					savingsTransactionHistoryView
							.setGlcode(financialTransactionBO.getGlcode()
									.getGlcode());
					if (financialTransactionBO.isDebitEntry()) {
						savingsTransactionHistoryView.setDebit(String
								.valueOf(removeSign(financialTransactionBO
										.getPostedAmount())));
					} else if (financialTransactionBO.isCreditEntry()) {
						savingsTransactionHistoryView.setCredit(String
								.valueOf(removeSign(financialTransactionBO
										.getPostedAmount())));
					}
					savingsTransactionHistoryView
							.setBalance(String
									.valueOf(removeSign(((SavingsTrxnDetailEntity) accountTrxnEntity)
											.getBalance())));
					savingsTransactionHistoryView
							.setClientName(accountTrxnEntity.getCustomer()
									.getDisplayName());
					savingsTransactionHistoryView
							.setPostedDate(financialTransactionBO
									.getPostedDate());
					if (accountTrxnEntity.getPersonnel() != null)
						savingsTransactionHistoryView
								.setPostedBy(accountTrxnEntity.getPersonnel()
										.getDisplayName());
					if (financialTransactionBO.getNotes() != null
							&& !financialTransactionBO.getNotes().equals(""))
						savingsTransactionHistoryView
								.setNotes(financialTransactionBO.getNotes());
					savingsTransactionHistoryViewList
							.add(savingsTransactionHistoryView);
				}
			}
		}
		SessionUtils.setAttribute(SavingsConstants.TRXN_HISTORY_LIST,
				savingsTransactionHistoryViewList, request);
		return mapping.findForward("getTransactionHistory_success");
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward getStatusHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getRecentActivity()");
		String globalAccountNum = request.getParameter("globalAccountNum");
		SavingsBO savings = savingsService.findBySystemId(globalAccountNum);
		Hibernate.initialize(savings.getAccountStatusChangeHistory());
		savings.setUserContext((UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession()));
		List<AccountStatusChangeHistoryEntity> savingsStatusHistoryViewList = new ArrayList<AccountStatusChangeHistoryEntity>(
				savings.getAccountStatusChangeHistory());
		SessionUtils.setAttribute(SavingsConstants.STATUS_CHANGE_HISTORY_LIST,
				savingsStatusHistoryViewList, request);

		return mapping.findForward("getStatusHistory_success");
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		logger.debug("In SavingsAction::validate(), method: " + method);
		String forward = null;
		if (method != null) {
			if (method.equals("preview"))
				forward = "preview_faliure";
			else if (method.equals("editPreview"))
				forward = "editPreview_faliure";
			else if (method.equals("load"))
				forward = "load_faliure";
			else if (method.equals("reLoad"))
				forward = "reLoad_faliure";
		}
		return mapping.findForward(forward);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getDepositDueDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getDepositDueDetails()");
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SavingsBO savings = savingsService
				.findBySystemId(((SavingsActionForm) form)
						.getGlobalAccountNum());
		for (AccountActionDateEntity actionDate : savings
				.getAccountActionDates())
			Hibernate.initialize(actionDate);

		Hibernate.initialize(savings.getAccountNotes());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		for (AccountFlagMapping accountFlagMapping : savings.getAccountFlags()) {
			Hibernate.initialize(accountFlagMapping.getFlag());
			accountFlagMapping.getFlag().setLocaleId(uc.getLocaleId());
		}
		Hibernate.initialize(savings.getAccountFlags());
		savings.getAccountState().setLocaleId(uc.getLocaleId());
		savings.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
		return mapping.findForward("depositduedetails_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward waiveAmountDue(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::waiveAmountDue()");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		savings = savingsService.findBySystemId(((SavingsActionForm) form)
				.getGlobalAccountNum());
		savings.setUserContext(uc);
		savings.waiveAmountDue(WaiveEnum.ALL);
		return mapping.findForward("waiveAmount_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward waiveAmountOverDue(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::waiveAmountOverDue()");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		savings = savingsService.findBySystemId(((SavingsActionForm) form)
				.getGlobalAccountNum());
		savings.setUserContext(uc);
		savings.waiveAmountOverDue();
		return mapping.findForward("waiveAmount_success");
	}

	private void doCleanUp(SavingsActionForm savingsActionForm,
			HttpServletRequest request) throws Exception {
		savingsActionForm.clear();
		// remove old savings object
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
	}

	private Double removeSign(Money amount) {
		if (amount.getAmountDoubleValue() < 0)
			return amount.negate().getAmountDoubleValue();
		else
			return amount.getAmountDoubleValue();
	}

	protected void checkPermissionForCreate(Short newState,
			UserContext userContext, Short flagSelected, Short officeId,
			Short loanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(newState, userContext, officeId,
				loanOfficerId, true))
			throw new AccountException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(Short newSate, UserContext userContext,
			Short officeId, Short loanOfficerId, boolean saveFlag) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(ActivityMapper.getInstance()
						.getActivityIdForState(newSate), officeId,
						loanOfficerId));
	}
}

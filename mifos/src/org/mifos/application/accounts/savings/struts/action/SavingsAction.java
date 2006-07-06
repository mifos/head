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
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTransactionHistoryView;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

/**
 * This class is for savings account related actions like creating, retrieving,
 * updating savings account etc.
 */
public class SavingsAction extends AccountAppAction {

	private SavingsBusinessService savingsService;

	private MasterDataService masterDataService;

	private SavingsPrdBusinessService savingsPrdService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public SavingsAction() throws ServiceException {
		savingsService = (SavingsBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Savings);
		masterDataService = (MasterDataService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.MasterDataService);
		savingsPrdService = (SavingsPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.SavingsProduct);
	}

	protected BusinessService getService() {
		return savingsService;
	}

	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		if (method.equals("edit") || method.equals("create")
				|| method.equals("update") || method.equals("previous")
				|| method.equals("get") || method.equals("editPrevious")
				|| method.equals("getRecentActivity")
				|| method.equals("getTransactionHistory")
				|| method.equals("getDepositDueDetails")
				|| method.equals("waiveAmountDue")
				|| method.equals("waiveAmountOverDue")) {
			logger
					.debug("In SavingsAction::skipActionFormToBusinessObjectConversion(), Skipping for Method: "
							+ method);
			return true;
		} else
			return false;
	}

	/**
	 * This method gets the applicable product offerings to be displayed in the
	 * UI for the selected customer.
	 */
	public ActionForward getPrdOfferings(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getPrdOfferings(), customerId: "
				+ ((SavingsActionForm) form).getCustomerId());
		doCleanUp(form, request);
		CustomerBO customer = getCustomer(new Integer(
				((SavingsActionForm) form).getCustomerId()));
		SavingsBO savings = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		savings.setCustomer(customer);
		if (customer.getCustomerMeeting() != null
				&& customer.getCustomerMeeting().getMeeting() != null)
			Hibernate.initialize(customer.getCustomerMeeting().getMeeting());
		List<PrdOfferingView> savingPrds = savingsService.getSavingProducts(
				customer.getOffice(), customer.getCustomerLevel(),
				SavingsConstants.SAVINGS_ALL);
		SessionUtils.setAttribute(SavingsConstants.SAVINGS_PRD_OFFERINGS,
				savingPrds, request.getSession());
		logger.info("In SavingsAction::getPrdOfferings(), Retrieved "
				+ savingPrds.size() + " Products for customerId: "
				+ customer.getCustomerId());
		return mapping.findForward(AccountConstants.GET_PRDOFFERINGS_SUCCESS);
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::load(), selectedPrdOfferingId: "
				+ ((SavingsActionForm) form).getSelectedPrdOfferingId());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		loadAndSetSavingsOffering(new Short(((SavingsActionForm) form)
				.getSelectedPrdOfferingId()), savings);
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils
				.setAttribute(
						MasterConstants.INTEREST_CAL_TYPES,
						masterDataService
								.getMasterData(
										MasterConstants.INTEREST_CAL_TYPES,
										uc.getLocaleId(),
										"org.mifos.application.master.util.valueobjects.InterestCalcType",
										"interestCalculationTypeID"), request
								.getSession());
		SessionUtils
				.setAttribute(
						MasterConstants.SAVINGS_TYPE,
						masterDataService
								.getMasterData(
										MasterConstants.SAVINGS_TYPE,
										uc.getLocaleId(),
										"org.mifos.application.master.util.valueobjects.SavingsType",
										"savingsTypeId"), request.getSession());
		SessionUtils
				.setAttribute(
						MasterConstants.RECOMMENDED_AMOUNT_UNIT,
						masterDataService
								.getMasterData(
										MasterConstants.RECOMMENDED_AMOUNT_UNIT,
										uc.getLocaleId(),
										"org.mifos.application.master.util.valueobjects.RecommendedAmntUnit",
										"recommendedAmntUnitId"), request
								.getSession());
		SessionUtils.setAttribute(SavingsConstants.CUSTOM_FIELDS,
				savingsService.retrieveCustomFieldsDefinition(), request
						.getSession());
		logger.info("In SavingsAction::load(), Data loaded successfully ");
		return mapping.findForward("load_success");
	}

	public ActionForward reLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::reLoad(), selectedPrdOfferingId: "
				+ ((SavingsActionForm) form).getSelectedPrdOfferingId());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		loadAndSetSavingsOffering(new Short(((SavingsActionForm) form)
				.getSelectedPrdOfferingId()), savings);
		logger.info("In SavingsAction::reLoad(), Data loaded successfully ");
		return mapping.findForward("load_success");
	}

	private void loadAndSetSavingsOffering(Short savingsOfferingId,
			SavingsBO savings) throws Exception {
		SavingsOfferingBO savingsOffering = savingsPrdService
				.getSavingsProduct(savingsOfferingId);
		savings.setSavingsOffering(savingsOffering);
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.setSavingsType(savingsOffering.getSavingsType());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::preview()");
		SavingsBO savings = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		SessionUtils.setAttribute(SavingsConstants.IS_PENDING_APPROVAL,
				Configuration.getInstance().getAccountConfig(
						savings.getCustomer().getOffice().getOfficeId())
						.isPendingApprovalStateDefinedForSavings(), request
						.getSession());
		return mapping.findForward("preview_success");
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction:previous()");
		return mapping.findForward("previous_success");
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::create(), accountStateId: "
				+ ((SavingsActionForm) form).getStateSelected());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		savings.setAccountState(new AccountStateEntity(new Short(
				((SavingsActionForm) form).getStateSelected())));
		savings.save();
		logger
				.info("In SavingsAction::create(), Savings object saved successfully ");
		return mapping.findForward("create_success");
	}

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::get()");
		SavingsActionForm actionForm = (SavingsActionForm) form;
		actionForm.setInput(null);
		logger
				.debug("In SavingsAction::get(), Retrieving for globalAccountNum: "
						+ actionForm.getGlobalAccountNum());
		String systemId = actionForm.getGlobalAccountNum();
		request.getSession().removeAttribute(Constants.BUSINESS_KEY);

		SavingsBO savings = savingsService.findBySystemId(systemId);
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

		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request
				.getSession());
		SessionUtils
				.setAttribute(
						MasterConstants.SAVINGS_TYPE,
						masterDataService
								.getMasterData(
										MasterConstants.SAVINGS_TYPE,
										uc.getLocaleId(),
										"org.mifos.application.master.util.valueobjects.SavingsType",
										"savingsTypeId"), request.getSession());
		SessionUtils.setAttribute(SavingsConstants.CUSTOM_FIELDS,
				savingsService.retrieveCustomFieldsDefinition(), request
						.getSession());
		SessionUtils
				.setAttribute(
						MasterConstants.RECOMMENDED_AMOUNT_UNIT,
						masterDataService
								.getMasterData(
										MasterConstants.RECOMMENDED_AMOUNT_UNIT,
										uc.getLocaleId(),
										"org.mifos.application.master.util.valueobjects.RecommendedAmntUnit",
										"recommendedAmntUnitId"), request
								.getSession());
		SessionUtils.setAttribute(
				SavingsConstants.RECENTY_ACTIVITY_DETAIL_PAGE, savings
						.getRecentAccountActivity(3), request.getSession());
		logger
				.info("In SavingsAction::get(), Savings object retrieved successfully");
		return mapping.findForward("get_success");
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::edit()");
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		if (savings.isDepositScheduleBeRegenerated())
			SessionUtils.setAttribute(SavingsConstants.RECOMMENDED_AMOUNT,
					savings.getRecommendedAmount(), request.getSession());
		return mapping.findForward("edit_success");
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::editPreview()");
		return mapping.findForward("editPreview_success");
	}

	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::editPrevious()");
		return mapping.findForward("editPrevious_success");
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::update()");
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		if (savings.isDepositScheduleBeRegenerated()) {
			Money oldAmount = (Money) SessionUtils.getAttribute(
					SavingsConstants.RECOMMENDED_AMOUNT, request.getSession());
			if (oldAmount.getAmountDoubleValue() != savings
					.getRecommendedAmount().getAmountDoubleValue())
				savings.updateAndGenerateSchedule();
			else
				savings.update();
		} else
			savings.update();
		logger
				.info("In SavingsAction::update(), Savings object updated successfully");
		return mapping.findForward("update_success");
	}

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
				Constants.BUSINESS_KEY, request.getSession());
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(uc);
		SessionUtils.setAttribute(SavingsConstants.RECENTY_ACTIVITY_LIST,
				savings.getRecentAccountActivity(null), request.getSession());
		return mapping.findForward("getRecentActivity_success");
	}

	public ActionForward getTransactionHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getRecentActivity()");
		Short localeId = ((UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession()))
				.getLocaleId();
		// SavingsBO savings
		// =(SavingsBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
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
					if(accountTrxnEntity.getPersonnel()!=null)
						savingsTransactionHistoryView.setPostedBy(accountTrxnEntity.getPersonnel().getDisplayName());
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
				savingsTransactionHistoryViewList, request.getSession());
		return mapping.findForward("getTransactionHistory_success");
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

	public ActionForward getDepositDueDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::getDepositDueDetails()");
		request.getSession().removeAttribute(Constants.BUSINESS_KEY);
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
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request
				.getSession());
		return mapping.findForward("depositduedetails_success");
	}

	public ActionForward waiveAmountDue(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAction::waiveAmountDue()");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		savings = savingsService.findBySystemId(((SavingsActionForm) form)
				.getGlobalAccountNum());
		savings.setUserContext(uc);
		savings.waiveAmountDue(WaiveEnum.ALL);
		return mapping.findForward("waiveAmount_success");
	}

	public ActionForward waiveAmountOverDue(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAction::waiveAmountOverDue()");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		savings = savingsService.findBySystemId(((SavingsActionForm) form)
				.getGlobalAccountNum());
		savings.setUserContext(uc);
		savings.waiveAmountOverDue();
		return mapping.findForward("waiveAmount_success");
	}

	private void clearActionForm(ActionForm form) {
		SavingsActionForm actionForm = (SavingsActionForm) form;
		actionForm.setAccountId(null);
		actionForm.setSelectedPrdOfferingId(null);
		for (AccountCustomFieldEntity customField : actionForm
				.getAccountCustomFieldSet()) {
			customField.setFieldId(null);
			customField.setFieldValue(null);
			customField.setAccount(null);
			customField.setAccountCustomFieldId(null);
		}
	}

	private void doCleanUp(ActionForm form, HttpServletRequest request) {
		clearActionForm(form);
		// remove old savings object
		request.getSession().removeAttribute(Constants.BUSINESS_KEY);
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, new SavingsBO(uc),
				request.getSession());
	}

	private Double removeSign(Money amount) {
		if (amount.getAmountDoubleValue() < 0)
			return amount.negate().getAmountDoubleValue();
		else
			return amount.getAmountDoubleValue();
	}
}

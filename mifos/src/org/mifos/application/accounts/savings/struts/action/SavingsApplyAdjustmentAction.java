/**
 
 * SavingsAdjustmentAction.java    version: 1.0
 
 
 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsApplyAdjustmentActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class SavingsApplyAdjustmentAction extends BaseAction {
	private SavingsBusinessService savingsService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	@Override
	protected BusinessService getService() throws ServiceException {
		return getSavingsService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	@Override
	protected boolean startSession() {
		return false;
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm(form);
		doCleanUp(request);
		SavingsApplyAdjustmentActionForm actionForm = (SavingsApplyAdjustmentActionForm) form;
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		Integer accountId = savings.getAccountId();
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY , request);
		savings = getSavingsService().findById(accountId);
		savings.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
		AccountPaymentEntity lastPayment = savings.getLastPmnt();
		if (null != lastPayment
				&& lastPayment.getAmount().getAmountDoubleValue() != 0
				&& (new SavingsHelper().getPaymentActionType(lastPayment)
						.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT) || new SavingsHelper()
						.getPaymentActionType(lastPayment).equals(
								AccountConstants.ACTION_SAVINGS_WITHDRAWAL))) {
			actionForm.setLastPaymentAmount(savings.getLastPmnt().getAmount());
			AccountActionEntity accountAction = (AccountActionEntity) new MasterPersistence()
					.getPersistentObject(AccountActionEntity.class,
							new SavingsHelper()
									.getPaymentActionType(lastPayment));
			accountAction.setLocaleId(uc.getLocaleId());
			Hibernate.initialize(savings.getLastPmnt().getAccountTrxns());
			SessionUtils.setAttribute(SavingsConstants.ACCOUNT_ACTION,
					accountAction, request);
			SessionUtils.setAttribute(SavingsConstants.CLIENT_NAME,
					getClientName(savings, lastPayment), request);
			SessionUtils.setAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID,
					Constants.YES, request);
		} else
			SessionUtils.setAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID,
					Constants.NO, request);

		logger.debug("In SavingsAdjustmentAction::load(), accountId: "
				+ savings.getAccountId());
		return mapping.findForward("load_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAdjustmentAction::preview()");
		return mapping.findForward("preview_success");
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In SavingsAdjustmentAction::previous()");
		return mapping.findForward("previous_success");
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward adjustLastUserAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In SavingsAdjustmentAction::adjustLastUserPayment()");
		request.setAttribute("method", "adjustLastUserAction");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		Integer accountId = savings.getAccountId();
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY , request);
		savings = getSavingsService().findById(accountId);
		savings.setUserContext(uc);
		if (savings.getPersonnel() != null)
			getBizService().checkPermissionForAdjustment(AccountTypes.SAVINGSACCOUNT, null, uc,
					savings.getOffice().getOfficeId(), savings.getPersonnel()
							.getPersonnelId());
		else
			getBizService().checkPermissionForAdjustment(AccountTypes.SAVINGSACCOUNT, null, uc,
					savings.getOffice().getOfficeId(), uc.getId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savings, request);
		SavingsApplyAdjustmentActionForm actionForm = (SavingsApplyAdjustmentActionForm) form;
		savings.adjustLastUserAction(actionForm.getLastPaymentAmount(),
				actionForm.getNote());
		doCleanUp(request);
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().evict(savings);
		return mapping.findForward("account_detail_page");
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request);
		logger.debug("In SavingsAdjustmentAction::cancel()");
		return mapping.findForward("account_detail_page");
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		logger.debug("In SavingsAdjustmentAction::validate(), method: "
				+ method);
		String forward = null;
		if (method != null && method.equals("preview"))
			forward = "preview_failure";
		else if (method != null && method.equals("SavingsAdjustmentAction")) {
			forward = "adjustLastUserAction_failure";
		}
		return mapping.findForward(forward);
	}

	private void clearActionForm(ActionForm form) {
		SavingsApplyAdjustmentActionForm actionForm = (SavingsApplyAdjustmentActionForm) form;
		actionForm.setLastPaymentAmountOption("1");
		actionForm.setLastPaymentAmount(new Money());
		actionForm.setNote(null);
	}

	private void doCleanUp(HttpServletRequest request) throws Exception {
		SessionUtils.removeAttribute(SavingsConstants.ACCOUNT_ACTION,request);
		SessionUtils.removeAttribute(SavingsConstants.CLIENT_NAME,request);
	}

	private SavingsBusinessService getSavingsService() throws ServiceException {
		if (savingsService == null)
			savingsService = (SavingsBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Savings);
		return savingsService;
	}

	private String getClientName(SavingsBO savings,
			AccountPaymentEntity lastPayment) {
		if (savings.getCustomer().getCustomerLevel().getId().equals(
				CustomerConstants.CLIENT_LEVEL_ID))
			return null;
		String clientName = null;
		CustomerBO customer = null;
		for (AccountTrxnEntity accountTrxn : lastPayment.getAccountTrxns()) {
			customer = accountTrxn.getCustomer();
			break;
		}
		if (customer != null
				&& customer.getCustomerLevel().getId().equals(
						CustomerConstants.CLIENT_LEVEL_ID)) {
			return customer.getDisplayName();
		}

		return clientName;
	}
	
	private AccountBusinessService getBizService(){
		return (AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts);
	}
}

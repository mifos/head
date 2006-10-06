package org.mifos.application.accounts.savings.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsDepositWithdrawalActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class SavingsDepositWithdrawalAction extends BaseAction {
	private SavingsBusinessService savingsService;

	private AccountBusinessService accountsService;

	private MasterDataService masterDataService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	protected BusinessService getService() throws ServiceException {
		return getSavingsService();
	}

	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		logger.debug("In SavingsDepositWithdrawalAction::load(), accountId: "
				+ savings.getAccountId());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsDepositWithdrawalActionForm actionForm = (SavingsDepositWithdrawalActionForm) form;
		clearActionForm(actionForm);

		List<AccountActionEntity> trxnTypes = new ArrayList<AccountActionEntity>();
		trxnTypes.add(getAccountsService().getAccountAction(
				AccountConstants.ACTION_SAVINGS_DEPOSIT, uc.getLocaleId()));
		trxnTypes.add(getAccountsService().getAccountAction(
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, uc.getLocaleId()));
		SessionUtils.setAttribute(AccountConstants.TRXN_TYPES, trxnTypes,
				request);

		if (savings.getCustomer().getCustomerLevel().getId().shortValue() == CustomerConstants.CENTER_LEVEL_ID
				|| (savings.getCustomer().getCustomerLevel().getId()
						.shortValue() == CustomerConstants.GROUP_LEVEL_ID && savings
						.getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.PERINDIVIDUAL.getValue())))
			SessionUtils.setAttribute(SavingsConstants.CLIENT_LIST, savings
					.getCustomer().getChildren(
							CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD), request);
		else
			SessionUtils.setAttribute(SavingsConstants.CLIENT_LIST, null,
					request);
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,
				new ArrayList<PaymentTypeEntity>(), request);
		SessionUtils.setAttribute(SavingsConstants.IS_BACKDATED_TRXN_ALLOWED,
				new Boolean(Configuration.getInstance().getAccountConfig(
						savings.getCustomer().getOffice().getOfficeId())
						.isBackDatedTxnAllowed()), request);

		actionForm.setTrxnDate(DateHelper.getCurrentDate(uc
				.getPereferedLocale()));
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward reLoad(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsBO savings = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		logger.debug("In SavingsDepositWithdrawalAction::reload(), accountId: "
				+ savings.getAccountId());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsDepositWithdrawalActionForm actionForm = (SavingsDepositWithdrawalActionForm) form;
		if (actionForm.getTrxnTypeId() != null
				&& actionForm.getTrxnTypeId() != "") {
			Short trxnTypeId = Short.valueOf(actionForm.getTrxnTypeId());
			if (trxnTypeId.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
				SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,
						getMasterDataService().getSupportedPaymentModes(
								uc.getLocaleId(),
								TrxnTypes.savings_deposit.getValue()), request);
				if (actionForm.getCustomerId() != null
						&& actionForm.getCustomerId() != "")
					actionForm.setAmount(savings.getTotalPaymentDue(Integer
							.valueOf(actionForm.getCustomerId())).toString());
			} else {
				actionForm.setAmount(new Money().toString());
				SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,
						getMasterDataService().getSupportedPaymentModes(
								uc.getLocaleId(),
								TrxnTypes.savings_withdrawal.getValue()),
						request);
			}
		}
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request);
		return mapping.findForward(ActionForwards.account_details_page
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward makePayment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsBO savedAccount = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		SavingsBO savings = getSavingsService().findById(
				savedAccount.getAccountId());
		logger
				.debug("In SavingsDepositWithdrawalAction::makePayment(), accountId: "
						+ savings.getAccountId());
		SavingsDepositWithdrawalActionForm actionForm = (SavingsDepositWithdrawalActionForm) form;
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());

		Date trxnDate = getDateFromString(actionForm.getTrxnDate(), uc
				.getPereferedLocale());

		if (!savings.isTrxnDateValid(trxnDate))
			throw new AccountException(AccountConstants.ERROR_INVALID_TRXN);

		Short trxnTypeId = Short.valueOf(actionForm.getTrxnTypeId());
		if (trxnTypeId.equals(AccountConstants.ACTION_SAVINGS_DEPOSIT)) {
			savings.applyPayment(createPaymentDataForDeposit(actionForm, uc,
					savings));
		} else if (trxnTypeId
				.equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL))
			savings.withdraw(createPaymentData(actionForm, uc));

		return mapping.findForward(ActionForwards.account_details_page
				.toString());
	}

	private void clearActionForm(SavingsDepositWithdrawalActionForm actionForm) {
		actionForm.setReceiptDate(null);
		actionForm.setReceiptId(null);
		actionForm.setPaymentTypeId(null);
		actionForm.setTrxnTypeId(null);
		actionForm.setCustomerId(null);
		actionForm.setAmount("");
	}

	private PaymentData createPaymentData(
			SavingsDepositWithdrawalActionForm actionForm, UserContext uc)
			throws Exception {
		Date trxnDate = getDateFromString(actionForm.getTrxnDate(), uc
				.getPereferedLocale());
		Date receiptDate = getDateFromString(actionForm.getReceiptDate(), uc
				.getPereferedLocale());
		PaymentData paymentData = new PaymentData(actionForm.getAmountValue(),
				new PersonnelPersistence().getPersonnel(uc.getId()),
				Short.valueOf(actionForm.getPaymentTypeId()), trxnDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(actionForm.getReceiptId());
		CustomerBusinessService customerService = (CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer);
		paymentData.setCustomer(customerService.getCustomer(Integer
				.valueOf(actionForm.getCustomerId())));
		return paymentData;
	}

	private PaymentData createPaymentDataForDeposit(
			SavingsDepositWithdrawalActionForm actionForm, UserContext uc,
			SavingsBO savings) throws Exception {
		PaymentData paymentData = createPaymentData(actionForm, uc);
		for (AccountActionDateEntity installment : savings
				.getTotalInstallmentsDue(Integer.valueOf(actionForm
						.getCustomerId()))) {
			AccountPaymentData accountPaymentData = new SavingsPaymentData(
					installment);
			paymentData.addAccountPaymentData(accountPaymentData);
		}
		return paymentData;

	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		String forward = null;
		if (method != null) {
			forward = method + "_failure";
		}
		return mapping.findForward(forward);
	}

	private void doCleanUp(HttpServletRequest request) throws Exception{
		SessionUtils.removeAttribute(AccountConstants.TRXN_TYPES,request);
		SessionUtils.removeAttribute(SavingsConstants.CLIENT_LIST,request);
		SessionUtils.removeAttribute(MasterConstants.PAYMENT_TYPE,request);
	}

	private MasterDataService getMasterDataService() throws ServiceException {
		if (masterDataService == null)
			masterDataService = (MasterDataService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.MasterDataService);
		return masterDataService;
	}

	private SavingsBusinessService getSavingsService() throws ServiceException {
		if (savingsService == null)
			savingsService = (SavingsBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Savings);
		return savingsService;
	}

	private AccountBusinessService getAccountsService() throws ServiceException {
		if (accountsService == null)
			accountsService = (AccountBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Accounts);
		return accountsService;
	}
}

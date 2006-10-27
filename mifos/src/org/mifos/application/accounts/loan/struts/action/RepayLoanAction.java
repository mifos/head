/**
 * 
 */
package org.mifos.application.accounts.loan.struts.action;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
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

public class RepayLoanAction extends BaseAction {

	private LoanBusinessService loanBusinessService;

	private MasterDataService masterDataService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public RepayLoanAction() throws ServiceException {
		loanBusinessService = (LoanBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Loan);
		masterDataService = (MasterDataService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.MasterDataService);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return loanBusinessService;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadRepayment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm(form);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info(
				"Loading repay loan page");
		String globalAccountNum = request.getParameter("globalAccountNum");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		LoanBO loanBO = ((LoanBusinessService) getService())
				.findBySystemId(globalAccountNum);
		SessionUtils
				.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT, Money
						.round(loanBO.getTotalEarlyRepayAmount()), request
						);
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,
				masterDataService.retrievePaymentTypes(uc.getLocaleId()),
				request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,loanBO,request);
		return mapping.findForward(Constants.LOAD_SUCCESS);
	}

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward makeRepayment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionUtils.removeAttribute(
				LoanConstants.TOTAL_REPAYMENT_AMOUNT,request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info(
				"Performing loan repayment");
				String globalAccountNum = request.getParameter("globalAccountNum");
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		LoanBO loanBOInSession = (LoanBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
		LoanBO loanBO = ((LoanBusinessService) getService()).findBySystemId(globalAccountNum);
		checkVersionMismatch(loanBOInSession.getVersionNo(),loanBO.getVersionNo());
		RepayLoanActionForm repayLoanActionForm = (RepayLoanActionForm) form;
		Date receiptDate = null;
		if (repayLoanActionForm.getRecieptDate() != null
				&& repayLoanActionForm.getRecieptDate() != "")
			receiptDate = new Date(DateHelper.getLocaleDate(
					uc.getPereferedLocale(),
					repayLoanActionForm.getRecieptDate()).getTime());
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(),
				repayLoanActionForm.getReceiptNumber(), receiptDate,
				repayLoanActionForm.getPaymentTypeId(), uc.getId());
		return mapping.findForward(Constants.UPDATE_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(Constants.PREVIEW_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(Constants.PREVIOUS_SUCCESS);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		logger.debug("In RepayLoanAction::validate(), method: " + method);
		String forward = null;
		if (method != null && method.equals("preview"))
			forward = ActionForwards.preview_failure.toString();
		return mapping.findForward(forward);
	}

	private void clearActionForm(ActionForm form) {
		RepayLoanActionForm actionForm = (RepayLoanActionForm) form;
		actionForm.setReceiptNumber(null);
		actionForm.setRecieptDate(null);
		actionForm.setPaymentTypeId(null);
	}
}

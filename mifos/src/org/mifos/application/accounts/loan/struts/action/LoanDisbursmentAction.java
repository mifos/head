/**
 * 
 */
package org.mifos.application.accounts.loan.struts.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanDisbursmentActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class LoanDisbursmentAction extends BaseAction {

	private LoanBusinessService loanBusinessService = null;

	private MasterDataService masterDataService = null;
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("loanDisbursmentAction");
		security.allow("load", SecurityConstants.LOAN_CAN_DISBURSE_LOAN);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("previous", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		LoanDisbursmentActionForm loanDisbursmentActionForm = (LoanDisbursmentActionForm) form;
		loanDisbursmentActionForm.clear();
		Date currentDate = new Date(System.currentTimeMillis());

		LoanBO loan = ((LoanBusinessService) getService()).getAccount(Integer
				.valueOf(loanDisbursmentActionForm.getAccountId()));
		checkIfProductsOfferingCanCoexist(mapping,form,request,response);
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, loan
				.getDisbursementDate(), request);
		loanDisbursmentActionForm.setTransactionDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), SessionUtils.getAttribute(
		LoanConstants.PROPOSEDDISBDATE, request)
		.toString()));
		loan.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		SessionUtils.setCollectionAttribute(MasterConstants.PAYMENT_TYPE,
				getMasterDataService().getSupportedPaymentModes(
						uc.getLocaleId(),
						TrxnTypes.loan_disbursement.getValue()), request);
		loanDisbursmentActionForm.setAmount(loan
				.getAmountTobePaidAtdisburtail(currentDate));
		loanDisbursmentActionForm.setLoanAmount(loan.getLoanAmount());
		return mapping.findForward(Constants.LOAD_SUCCESS);
	}
	
	
	private void checkIfProductsOfferingCanCoexist(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanDisbursmentActionForm loanDisbursmentActionForm = (LoanDisbursmentActionForm) form;
		
		LoanBO newloan = ((LoanBusinessService) getService()).getAccount(Integer
				.valueOf(loanDisbursmentActionForm.getAccountId()));
		List<LoanBO> loanList = ((LoanBusinessService) getService())
				.getLoanAccountsActiveInGoodBadStanding(newloan.getCustomer().getCustomerId());
		// Check if the client has an active loan accounts
		if(null!=loanList){
		for (LoanBO oldloan : loanList){
		// Check if the new loan product to disburse is allowed to the existent active loan product
			if(!oldloan.prdOfferingsCanCoexist(newloan.getLoanOffering().getPrdOfferingId()))
			{
				String[] param={oldloan.getLoanOffering().getPrdOfferingName(),newloan.getLoanOffering().getPrdOfferingName()};
				throw new AccountException(LoanExceptionConstants.LOANCOULDNOTCOEXIST,param);
			} 
			
			
		}
		}	
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

	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO savedloan = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		LoanDisbursmentActionForm actionForm = (LoanDisbursmentActionForm) form;
		LoanBO loan = ((LoanBusinessService) getService()).getAccount(Integer
				.valueOf(actionForm.getAccountId()));
		checkVersionMismatch(savedloan.getVersionNo(),loan.getVersionNo());
		loan.setVersionNo(savedloan.getVersionNo());
		UserContext uc = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		Date trxnDate = getDateFromString(actionForm.getTransactionDate(), uc
				.getPreferredLocale());
		trxnDate = DateUtils.getDateWithoutTimeStamp(trxnDate.getTime());
		Date receiptDate = getDateFromString(actionForm.getReceiptDate(), uc
				.getPreferredLocale());
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(uc
				.getId());
		if (!loan.isTrxnDateValid(trxnDate))
			throw new AccountException("errors.invalidTxndate");
		if (actionForm.getPaymentModeOfPayment() != null
				&& actionForm.getPaymentModeOfPayment().equals(""))
			loan.disburseLoan(actionForm.getReceiptId(), trxnDate, Short
					.valueOf(actionForm.getPaymentTypeId()), personnel,
					receiptDate, Short.valueOf(actionForm
							.getPaymentModeOfPayment()));
		else
			loan.disburseLoan(actionForm.getReceiptId(), trxnDate, Short
					.valueOf(actionForm.getPaymentTypeId()), personnel,
					receiptDate, Short.valueOf("1"));
		return mapping.findForward(Constants.UPDATE_SUCCESS);
	}

	private LoanBusinessService getLoanBusinessService()
			throws ServiceException {
		if (loanBusinessService == null) {
			loanBusinessService = new LoanBusinessService();
		}
		return loanBusinessService;
	}

	private MasterDataService getMasterDataService() throws ServiceException {
		if (masterDataService == null)
			masterDataService = (MasterDataService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.MasterDataService);
		return masterDataService;
	}

	@TransactionDemarcate(joinToken = true)
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

	@Override
	protected BusinessService getService() throws ServiceException {
		return getLoanBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected boolean isNewBizRequired(HttpServletRequest request)
			throws ServiceException {
		return false;
	}

}

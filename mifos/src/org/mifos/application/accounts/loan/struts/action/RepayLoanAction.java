/**
 * 
 */
package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.RepayLoanActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.RepayLoanView;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsClosureActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

public class RepayLoanAction extends BaseAction {
	
	private LoanBusinessService loanBusinessService;
	private MasterDataService masterDataService;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public RepayLoanAction()throws ServiceException{
		loanBusinessService=(LoanBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Loan);
		masterDataService = (MasterDataService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.MasterDataService);
	}
	
	@Override
	protected BusinessService getService() throws ServiceException {
		return loanBusinessService;
	}

	public ActionForward loadRepayment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		clearActionForm(form);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Loading repay loan page");
		String globalAccountNum=request.getParameter("globalAccountNum");
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		LoanBO loanBO = ((LoanBusinessService)getService()).findBySystemId(globalAccountNum);
		SessionUtils.setAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT,Money.round(loanBO.getTotalEarlyRepayAmount()),request.getSession());
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,	masterDataService.retrievePaymentTypes(uc.getLocaleId()),request.getSession());
		return mapping.findForward(Constants.LOAD_SUCCESS);
	}
	
	
	public ActionForward makeRepayment(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		request.getSession().removeAttribute(LoanConstants.TOTAL_REPAYMENT_AMOUNT);	
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Performing loan repayment");
		String globalAccountNum=request.getParameter("globalAccountNum");
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		LoanBO loanBO = ((LoanBusinessService)getService()).findBySystemId(globalAccountNum);
		RepayLoanActionForm repayLoanActionForm=(RepayLoanActionForm)form;
		Date receiptDate=null;
		if(repayLoanActionForm.getRecieptDate()!=null && repayLoanActionForm.getRecieptDate()!="")
			receiptDate = new Date(DateHelper.getLocaleDate(uc.getPereferedLocale(),repayLoanActionForm.getRecieptDate()).getTime());
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(),repayLoanActionForm.getReceiptNumber(),receiptDate,repayLoanActionForm.getPaymentTypeId(),uc.getId());
		return mapping.findForward(Constants.UPDATE_SUCCESS);
	}
	
	
	public ActionForward preview(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		return mapping.findForward(Constants.PREVIEW_SUCCESS);
	}
	
	
	public ActionForward previous(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		return mapping.findForward(Constants.PREVIOUS_SUCCESS);
	}
	
	protected boolean skipActionFormToBusinessObjectConversion(String method)  {
		return true;
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String method = (String)request.getAttribute("methodCalled");
		logger.debug("In RepayLoanAction::validate(), method: "+ method);
		String forward=null;
		if(method!=null && method.equals("preview"))
				forward="preview_faliure";
		return mapping.findForward(forward);
	}
	
	private void clearActionForm(ActionForm form){
		RepayLoanActionForm actionForm =(RepayLoanActionForm)form;
		actionForm.setReceiptNumber(null);
		actionForm.setRecieptDate(null);
		actionForm.setPaymentTypeId(null);
	}
}

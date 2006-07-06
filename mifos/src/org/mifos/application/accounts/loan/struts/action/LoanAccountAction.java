package org.mifos.application.accounts.loan.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.ViewInstallmentDetails;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class LoanAccountAction extends AccountAppAction {	
	
	private LoanBusinessService loanBusinessService;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	
	protected boolean skipActionFormToBusinessObjectConversion(String method)  {
		return true;
	}
	
	public LoanAccountAction() throws ServiceException {
		loanBusinessService = (LoanBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Loan);
	}
	
	protected BusinessService getService() {
		return loanBusinessService;
	}	
	
	public ActionForward getInstallmentDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
		Integer accountId = Integer.valueOf(request.getParameter("accountId"));		
		LoanBO loanBO = loanBusinessService.getAccount(accountId);		
		ViewInstallmentDetails viewUpcomingInstallmentDetails = loanBusinessService.getUpcomingInstallmentDetails(loanBO.getDetailsOfNextInstallment());
		ViewInstallmentDetails viewOverDueInstallmentDetails = loanBusinessService.getOverDueInstallmentDetails(loanBO.getDetailsOfInstallmentsInArrears());
		Money totalAmountDue = viewUpcomingInstallmentDetails.getSubTotal().add(viewOverDueInstallmentDetails.getSubTotal());
		SessionUtils.setAttribute(LoanConstants.VIEW_UPCOMING_INSTALLMENT_DETAILS,viewUpcomingInstallmentDetails,request.getSession());
		SessionUtils.setAttribute(LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS,viewOverDueInstallmentDetails,request.getSession());
		SessionUtils.setAttribute(LoanConstants.TOTAL_AMOUNT_OVERDUE,totalAmountDue,request.getSession());		
		return mapping.findForward(LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS);
	}
	
	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("In loanAccountAction::getAllActivity()");
		String globalAccountNum=request.getParameter("globalAccountNum");
		SessionUtils.setAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW,loanBusinessService.getAllActivityView(globalAccountNum,((UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession())).getLocaleId()),request.getSession());
		return mapping.findForward(MethodNameConstants.GETALLACTIVITY_SUCCESS);
	}
	
	public ActionForward forwardWaiveCharge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {		
		String type = request.getParameter("type");
		return mapping.findForward("waive"+type+"Charges_Success");
	}	
	
}

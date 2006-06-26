/**
 * 
 */
package org.mifos.application.accounts.loan.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.MethodInvoker;
import org.mifos.framework.util.valueobjects.Context;

/**
 * @author krishankg
 * 
 */
public class LoanDisbursmentAction extends MifosBaseAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {
		return "loanDisbursment";
	}

	/**
	 * This method is called to load disbursment page
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults("accountId",request.getParameter("accountId"));
		context.setBusinessAction(LoanConstants.GET_LOAD_DISBURSEMENT_DATA);
		delegate(context, request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Loading disbursment page");
		return mapping.findForward(Constants.LOAD_SUCCESS);
	}
	
	
	/**
	 * This is the method which would be called for any "preview" operation in the application.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward preview(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		return mapping.findForward(Constants.PREVIEW_SUCCESS);
	}
	
	
	/**
	 * This is the method which would be called for any "previous" operation in the application.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward previous(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		ActionForward forward = null;
		return mapping.findForward(Constants.PREVIOUS_SUCCESS);
	}
	
	
	/**
	 * This is the method which would be called for any "update" operation in the application.
	 * It delegates the call to the business processor and  passing the {@link Context} object.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward update(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		ActionForward forward = null;
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(LoanConstants.DISBURSE_LOAN);
		delegate(context, request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Disbursing of loan action invoked");
		return mapping.findForward(Constants.UPDATE_SUCCESS);
	}
}

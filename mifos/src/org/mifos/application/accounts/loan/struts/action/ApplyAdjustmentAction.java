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
import org.mifos.framework.util.valueobjects.Context;

public class ApplyAdjustmentAction extends MifosBaseAction {

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	@Override
	protected String getPath() {
		return "applyAdjustment";
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#load(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(LoanConstants.LOAD_ADJUSTMENTS);
		context.addBusinessResults("accountId",request.getParameter("accountId"));
		delegate(context, request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Loading adjustments page");
		return mapping.findForward(Constants.LOAD_SUCCESS);
		
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#update(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(LoanConstants.MAKE_ADJUSTMENTS);
		delegate(context, request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("applying adjustments ");
		return mapping.findForward(Constants.UPDATE_SUCCESS);
	}
	
	
	

}

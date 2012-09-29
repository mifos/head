package org.mifos.application.financialaccounting.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.AccountingRules;
import org.mifos.framework.struts.action.BaseAction;

public class FinancialAccountingAction extends BaseAction {
	 public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
	            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
	    	boolean flag= AccountingRules.getSimpleAccountingStatus();
	    	request.getSession().setAttribute("accountingActivationStatus", flag);
	        return mapping.findForward(ActionForwards.load_success.toString());
	    }

}

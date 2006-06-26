package org.mifos.application.customer.struts.action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.struts.actionforms.CustomerHistoricalDataActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class CustomerHistoricalDataAction extends MifosBaseAction {

	/** 
	 *  Returns the path which uniquely identifies the element in the dependency.xml.
	 *  This method implementaion is the framework requirement. 
	 */
	public String getPath() {
		return CustomerConstants.CUSTOMER_HISTORICAL_DATA_ACTION;
	}
  
	public ActionForward customGet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		HttpSession session= request.getSession();
		LinkParameters params = (LinkParameters)session.getAttribute(CustomerConstants.LINK_VALUES);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(CustomerConstants.LINK_VALUES,params);
		return null;
	}
  
	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		ActionForward forward = super.get(mapping,form,request,response);
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		Object obj=context.getBusinessResults(CustomerConstants.IS_HISTORICAL_DATA_PRESENT);
		if(obj!=null)
			request.getSession().setAttribute(CustomerConstants.IS_HISTORICAL_DATA_PRESENT,(String)obj);
		return forward;
	}
	
	public ActionForward customUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(CustomerConstants.IS_HISTORICAL_DATA_PRESENT,request.getSession().getAttribute(CustomerConstants.IS_HISTORICAL_DATA_PRESENT));
		return null;
	}
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		clearActionForm(form);
		return mapping.findForward(chooseForward(request)); 
	}
	
	/** 
	 * This method is called on cancel to choose cancel forward.
	 * Based on input page on which cancel has been clicked it forwards to appropriate page
	 * @param fromPage 
	 * @return page to which it has to forward
	 */
	private String chooseForward(HttpServletRequest request){
		String forward=null;
		HttpSession session= request.getSession();
		LinkParameters params = (LinkParameters)session.getAttribute(CustomerConstants.LINK_VALUES);
		switch(params.getLevelId()){
			case CustomerConstants.CLIENT_LEVEL_ID:
				forward=CustomerConstants.CLIENT_DETAILS_PAGE;
				break;
			case CustomerConstants.GROUP_LEVEL_ID:
				forward=CustomerConstants.GROUP_DETAILS_PAGE;
				break;
			case CustomerConstants.CENTER_LEVEL_ID:
				forward=CustomerConstants.CENTER_DETAILS_PAGE;
				break;
		}
		return forward;
	}
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Before converting action form to value object checking for method name " + methodName);
		if(null !=methodName && (methodName.equals(CustomerConstants.METHOD_LOAD)||methodName.equals(CustomerConstants.METHOD_GET)||methodName.equals(CustomerConstants.METHOD_CANCEL) )){
			return false;
		}else{
			return true;
		}		
	}
	void clearActionForm(ActionForm form){
		CustomerHistoricalDataActionForm customerHistoricalDataActionForm = (CustomerHistoricalDataActionForm)form;
		customerHistoricalDataActionForm.setCustomerId(CustomerConstants.BLANK);
		customerHistoricalDataActionForm.setInterestPaid(new Money());
		customerHistoricalDataActionForm.setMfiJoiningDate(CustomerConstants.BLANK);
		customerHistoricalDataActionForm.setLoanAmount(new Money());
		customerHistoricalDataActionForm.setLoanCycleNumber(CustomerConstants.BLANK);
		customerHistoricalDataActionForm.setMissedPaymentsCount(CustomerConstants.BLANK);
		customerHistoricalDataActionForm.setNotes(CustomerConstants.BLANK);
	}
	
	 /**
	  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed 
	  * this method forwards as per the respective input page 
	  * @param mapping indicates action mapping defined in struts-config.xml 
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;	
		String methodCalled= (String)request.getAttribute("methodCalled");
			 //deciding forward page
			if(null !=methodCalled) {
				if(CustomerConstants.METHOD_PREVIEW.equals(methodCalled))
					forward = CustomerConstants.LOAD_SUCCESS;
			}
			return mapping.findForward(forward); 
	}
}




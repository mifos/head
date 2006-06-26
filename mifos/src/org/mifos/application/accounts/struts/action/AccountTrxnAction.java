package org.mifos.application.accounts.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.struts.actionforms.AccountTrxnActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceConstants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;


public class AccountTrxnAction extends MifosBaseAction {

	protected String getPath() {
		return AccountConstants.ACCOUNT_TRXN;
	}

	public Map<String,String> appendToMap(){
		Map<String, String> newMap = new HashMap<String, String>();
		newMap.put(AccountConstants.ACCOUNT_GETINSTALLMENTS,
				AccountConstants.ACCOUNT_GETINSTALLMENTS);
		return newMap;

	} 
	
	/**
	 * This method is called before converting action form to value object on every method call.
	 * if on a particular method conversion is not required , it returns false, otherwise returns true
	 * @return Returns whether the action form should be converted or not
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Before converting action form to value object checking for method name " + methodName);
		if(null !=methodName && (methodName.equals(MethodNameConstants.MANAGE)|| methodName.equals(MethodNameConstants.CANCEL))){
			return false;
		}else{
			return true;
		}
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		
		ActionForward forward = super.load(mapping, form, request, response);
		//check if the value object is null
		if(context.getValueObject() == null){
			request.setAttribute("isDisabled", "true");
			ActionErrors errors = new ActionErrors();
			errors.add(AccountConstants.NO_TRANSACTION_POSSIBLE,new ActionMessage(AccountConstants.NO_TRANSACTION_POSSIBLE));		
			request.setAttribute(Globals.ERROR_KEY, errors);
			
		}
		// this we are using because we can go to payment page from any page.
		HeaderObject headerObject = (HeaderObject)context.getBusinessResults("header_load");
		SessionUtils.setAttribute("header_load", headerObject, request.getSession());
		
		return forward;
	}

	public ActionForward getInstallmentHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionForward forward = null;
		String accountId = request.getParameter("accountId");


		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		context.setBusinessAction(AccountConstants.ACCOUNT_GETINSTALLMENTS);
		AccountPayment accountPmntVO = (AccountPayment) context.getValueObject();

		if (accountId != null)
			accountPmntVO.setAccountId(Integer.parseInt(accountId));

		delegate(context, request);
		// this clean up the context because it won't be required on the create
		// page.
		cleanUpContext(request);
		form.reset(mapping, request);
		forward = mapping.findForward(AccountConstants.ACCOUNT_GETINSTALLMENTS); 
		return forward;
	}
	
	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;	
		
		
		String methodCalled= (String)request.getAttribute("methodCalled");
		AccountTrxnActionForm accountTrxnActionForm = (AccountTrxnActionForm)form;
		String fromPage = accountTrxnActionForm.getInput();
			 //deciding forward page
			if(null !=methodCalled) {
				if((ResourceConstants.PREVIEW).equals(methodCalled)) {
					if("reviewTransaction".equals(fromPage)) {
						forward= "preview_failure";
					}
				}
			}
			return mapping.findForward(forward); 
		}

	/**
	 * This method is called on create.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded.
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		String forward = null;
		AccountTrxnActionForm accountTrxnActionForm = (AccountTrxnActionForm)form;
		String inputPage =accountTrxnActionForm.getInput();
		if(inputPage!=null){
			if(inputPage.equals("reviewTransactionPage")){
				forward= MethodNameConstants.CANCEL_LOAN_SUCCESS;
				
			}else if(inputPage.equals(GroupConstants.VIEW_GROUP_CHARGES)){
				forward= GroupConstants.VIEW_GROUP_CHARGES;
			}
			else if(inputPage.equals("ViewCenterCharges")){
				forward= CenterConstants.CENTER_CHARGES_DETAILS_PAGE;
			}
			else if(inputPage.equals("ViewClientCharges")){
				forward= "ViewClientCharges";
			}
		}
		return mapping.findForward(forward);
	}

	/**
	 * This method is called on create.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded.
	 * @throws Exception
	 */
	public ActionForward customCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		String forward = null;
		AccountTrxnActionForm accountTrxnActionForm = (AccountTrxnActionForm)form;
		String inputPage =accountTrxnActionForm.getInput();
		if(inputPage!=null){
			if(inputPage.equals("reviewTransactionPage")){
				forward= MethodNameConstants.CREATE_LOAN_SUCCESS;
				
			}else if(inputPage.equals(GroupConstants.VIEW_GROUP_CHARGES)){
				forward= GroupConstants.VIEW_GROUP_CHARGES;
			}
			else if(inputPage.equals("ViewCenterCharges")){
				forward= CenterConstants.CENTER_CHARGES_DETAILS_PAGE;
			}
			else if(inputPage.equals("ViewClientCharges")){
				forward= "ViewClientCharges";
			}
		}
		return mapping.findForward(forward);
	}
}

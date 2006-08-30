package org.mifos.application.accounts.loan.struts.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.loan.util.valueobjects.LoanPerfHistory;
import org.mifos.application.accounts.loan.util.valueobjects.Waive;
import org.mifos.application.accounts.struts.action.AccountAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PathConstants;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceConstants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;


/**
 *  This class is the Action class for creating/updating loan accounts.
 */

public class LoanAction extends AccountAction{



	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	protected String getPath() {

		return PathConstants.LOANACCOUNTSPATH;
	}

	/**
	 * Returns a <code>HashMap<String,String></code> which has a entry for the method
	 * to be called when the user tries to get applicable loan offerings
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	public Map<String,String> appendToMap()
	{
		Map<String,String> keyMethodMap = super.appendToMap();
		keyMethodMap.put(AccountConstants.GETPRDOFFERINGS,   AccountConstants.GETPRDOFFERINGS);
		keyMethodMap.put(LoanConstants.GET_INSTALLMENT_DETAILS,LoanConstants.GET_INSTALLMENT_DETAILS);
		keyMethodMap.put(LoanConstants.WAIVE,LoanConstants.WAIVE);
		return keyMethodMap;
	}

	/**
	 * @return
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Before converting action form to value object checking for method name " + methodName);
		if(null !=methodName && (methodName.equals(MethodNameConstants.MANAGE) || methodName.equals(MethodNameConstants.UPDATE)|| methodName.equals(MethodNameConstants.SEARCH))){

			return false;
		}else{
			return true;
		}

	}


	/**
	 * This method returns the ActionForward for the Loan Action .This sets the action form to null, so that whenever user first comes to this page
	 * the action form will not have any values.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customGetPrdOfferigs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		form=null;
		return null;
	}


	/**
	 * This method is added because we might require to do some cleanup whenever the user is clicking on cancel.
	 * The operation carried out in this method is again based on the input parameter which comes as a hiiden field
	 * from jsp, because the same cancel leads the user to differnt pages depending on which jsp pages it is clicked.
	 * @param inputPage Hidden variable in the jsp
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded based on the input parameter.
	 * @throws Exception
	 */
	public ActionForward customCancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{

		LoanActionForm loanActionForm= (LoanActionForm)form;
		String inputPage= loanActionForm.getInput();
		String forward=null;
		if (inputPage.equals("editDetails")){
			forward=MethodNameConstants.CANCEL_LOAN_SUCCESS;
		}else if (inputPage.equals("previewdt")){
			forward=MethodNameConstants.CANCEL_LOAN_SUCCESS;
		}else {
			forward=Constants.CANCEL_SUCCESS;
		}
		return mapping.findForward(forward);
	}


	/**
	 *    This would return a forward based on the input because it is the same preview method called
	 *    from various jsp pages one while creating and one while updating.The input is a hidden variable in the jsp
	 *    which indicates the flow so that it can be determined wheteher we are creating or updating the loan product.
	 *    This might also need to put certian attributes in request like product category name etc. because when it is selected from ui on id would be set in the action form.
	 *    Based on that action form we need to get name from the master data which is in context and set it in request.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		LoanActionForm loanActionForm= (LoanActionForm)form;
		String inputPage= loanActionForm.getInput();
		Context context = (Context)SessionUtils.getAttribute(Constants.CONTEXT, request.getSession());
		context.addBusinessResults(LoanConstants.INPUTPAGE, inputPage);
		return mapping.findForward(this.chooseForward(inputPage));
	}

	/**
	 * This method is called on previous method
	 * It chooses forwards for previous page based on input page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customPrevious(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		LoanActionForm loanActionForm= (LoanActionForm)form;
		String inputPage= loanActionForm.getInput();
		String forward=null;
		if (inputPage.equals("accountPreview")){
			forward=Constants.PREVIOUS_SUCCESS;
		}else if(inputPage.equals("previewdt")){
			forward=Constants.MANAGE_PREVIOUS;
		}
		return mapping.findForward(forward);
	}

	/**
	 * This method is called on search.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return -- Returns the actionforward where the request is supposed to be forwarded.
	 * @throws Exception
	 */
	public ActionForward customSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		LoanActionForm loanActionForm= (LoanActionForm)form;
		String inputPage =loanActionForm.getInput();
		// System.out.println("----------------------------from Page is : "+ inputPage);
		if(inputPage.equals(LoanConstants.LOAN_CHANGE_LOG)){
			loanActionForm.setSearchNode(Constants.SEARCH_NAME,LoanConstants.LOAN_CHANGE_LOG);
			return mapping.findForward(MethodNameConstants.LOAN_CHANGE_LOG_SUCCESS);
		}
		return null;
	}

	/**This would return a forwards based on input page
	 * @param fromPage Contains the request parameters
	 * @return
	 */
	private String chooseForward(String fromPage) {
		String forward= null;
		if(fromPage.equals("installation")) {
			forward= Constants.PREVIEW_SUCCESS;
		}
		else if(fromPage.equals("editDetails")) {
			forward= Constants.MANAGE_PREVIEW;
		}
		return forward;

	}

	/**
	 * Method which is called to decide the pages on which the errors on failure of validation will be displayed
	 * this method forwards as per the respective fromPage
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;

		//cleanUpFeesMap(form,request);
		String methodCalled= (String)request.getAttribute("methodCalled");
		LoanActionForm loanActionForm = (LoanActionForm)form;
		String fromPage = loanActionForm.getInput();
		//deciding forward page
		if(null !=methodCalled) {
			if((ResourceConstants.LOAD).equals(methodCalled)) {
				if("createPage".equals(fromPage)) {
					forward= "load_failure";
				}
			}
			else if("next".equals(methodCalled)) {
				if("nextPage".equals(fromPage)); {
					forward="next_failure";
				}
			}
		}
		return mapping.findForward(forward);
	}

	/**
	 * This method cleans up the fees map in the action if it is not null, this cleaning up is required because
	 * the drop down in the UI should be cleared.
	 * @param form
	 * @param request
	 */
	private void cleanUpFeesMap(ActionForm form, HttpServletRequest request) {
		LoanActionForm loanActionForm = (LoanActionForm)form;
		loanActionForm.getAccountFeesMap().clear();
		
	}

	/**
	 * This method just clears the accountFees map , this is to clear the map when the user comes to the load page
	 * and then changes the loan offering drop down.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward customLoad(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {

		cleanUpFeesMap(form,request);
		return null;
	}

	/**
	 * This method has been overridden be cause after get the header which is being generated is to be set in session.
	 * It is being set in session so that it could be used on actions other than loan action such as applypayment,applycharges etc.,
	 * also if the user open the link in a new window the context is lost and now any click on the loan action throws an exception.
	 * @see org.mifos.framework.struts.action.MifosBaseAction#get(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@TransactionDemarcate(saveToken=true)
	public ActionForward get(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{


		ActionForward forward = super.get(mapping, form, request, response);

		Context context = (Context)SessionUtils.getAttribute(Constants.CONTEXT, request.getSession());

		HeaderObject headerObject = (HeaderObject)context.getBusinessResults("header_get");
		// It is being set as attribute and not as removable attribute because we want it to
		// be accessed even when the action changes.
		SessionUtils.setAttribute("header_get", headerObject, request.getSession());
		
		
		return forward;

	}

	
	/**
	 * This method is called when view installment details link is clicked.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getInstallmentDetails(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults("accountId",request.getParameter("accountId"));
		context.setBusinessAction(LoanConstants.GET_INSTALLMENT_DETAILS);
		delegate(context, request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Get installment details");
		return mapping.findForward(LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS);

	}
	
	
	/**
	 * This method is called when view installment details link is clicked.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward waive(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		Waive waiveObject=new Waive(); 
		waiveObject.setAccountId(Integer.valueOf(request.getParameter("accountId")));
		waiveObject.setType(request.getParameter("type"));
		waiveObject.setInstallmentType(request.getParameter("installmentType"));
		waiveObject.setNextInstallmentId(Short.valueOf(request.getParameter("installmentId")));
		waiveObject.setPersonnelId(context.getUserContext().getId()); 
		context.addBusinessResults("waive",waiveObject);
		context.setBusinessAction(LoanConstants.WAIVE);
		delegate(context, request);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("Waiving");
		return mapping.findForward(LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS);

	}
	
	protected void handleTransaction(ActionForm actionForm,HttpServletRequest request)throws SystemException,ApplicationException{
		String method = request.getParameter(MethodNameConstants.METHOD);
		if(method.equals(MethodNameConstants.CREATE)){
			Context context = ((Context)SessionUtils.getContext(getPath(), request.getSession()));
			LoanActionForm loanActionForm = (LoanActionForm)actionForm;
			Loan loan = (Loan)context.getValueObject();
			CustomerMaster customerMaster = (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
			Short loanStatus = Short.valueOf(loanActionForm.getStateSelected());
			checkPermissionForCreate(loanStatus,context.getUserContext(),null,customerMaster.getOfficeId(),context.getUserContext().getId());
		}
		super.handleTransaction(actionForm,request);
	}
	
	private void checkPermissionForCreate(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	private boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId){
		return ActivityMapper.getInstance().isSavePermittedForAccount(newState.shortValue(),userContext,recordOfficeId,recordLoanOfficerId);
	}	
}
package org.mifos.application.customer.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.struts.actionforms.CustomerApplyAdjustmentActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CustomerApplyAdjustmentAction extends BaseAction {
	private CustomerBusinessService customerBusinessService;
	
	public CustomerApplyAdjustmentAction() throws ServiceException {
		customerBusinessService = (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return customerBusinessService;
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward loadAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		resetActionFormFields(applyAdjustmentActionForm);
		CustomerBO customerBO = ((CustomerBusinessService) getService()).findBySystemId(applyAdjustmentActionForm.getGlobalCustNum());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerBO,request);
		request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
		if(null == customerBO.getCustomerAccount().getLastPmnt() || customerBO.getCustomerAccount().getLastPmntAmnt() == 0){
			request.setAttribute("isDisabled", "true");
			throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		return mapping.findForward(CustomerConstants.METHOD_LOAD_ADJUSTMENT_SUCCESS);
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previewAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		CustomerBO customerBO = ((CustomerBusinessService) getService()).findBySystemId(applyAdjustmentActionForm.getGlobalCustNum());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerBO,request);
		if(null == customerBO.getCustomerAccount().getLastPmnt() || customerBO.getCustomerAccount().getLastPmntAmnt() == 0){
			request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
			request.setAttribute("isDisabled", "true");
			throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		return mapping.findForward(CustomerConstants.METHOD_PREVIEW_ADJUSTMENT_SUCCESS);
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	@CloseSession
	public ActionForward applyAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String forward = null;
		request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_APPLY_ADJUSTMENT);
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		CustomerBO customerBO = ((CustomerBusinessService) getService()).findBySystemId(applyAdjustmentActionForm.getGlobalCustNum());
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,customerBO,request);
		if(null == customerBO.getCustomerAccount().getLastPmnt() || customerBO.getCustomerAccount().getLastPmntAmnt() == 0){
			request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
			throw new ApplicationException(AccountExceptionConstants.ZEROAMNTADJUSTMENT);
		}
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		customerBO.setUserContext(uc);
		customerBO.getCustomerAccount().setUserContext(uc);
		try {
		customerBO.adjustPmnt(applyAdjustmentActionForm.getAdjustmentNote());
		}catch(ApplicationException ae) {
			request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_PREVIEW_ADJUSTMENT);
			throw ae;
		}
		
		String inputPage = applyAdjustmentActionForm.getInput();
		resetActionFormFields(applyAdjustmentActionForm);
		if(inputPage!=null){
			if(inputPage.equals(CustomerConstants.VIEW_GROUP_CHARGES)){
				forward= CustomerConstants.APPLY_ADJUSTMENT_GROUP_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CENTER_CHARGES)){
				forward= CustomerConstants.APPLY_ADJUSTMENT_CENTER_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CLIENT_CHARGES)){
				forward= CustomerConstants.APPLY_ADJUSTMENT_CLIENT_SUCCESS;
			}
		}
		return mapping.findForward(forward);
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelAdjustment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String forward = null;
		CustomerApplyAdjustmentActionForm applyAdjustmentActionForm = (CustomerApplyAdjustmentActionForm)form;
		resetActionFormFields(applyAdjustmentActionForm);
		String inputPage = applyAdjustmentActionForm.getInput();
		resetActionFormFields(applyAdjustmentActionForm);
		if(inputPage!=null){
			if(inputPage.equals(CustomerConstants.VIEW_GROUP_CHARGES)){
				forward= CustomerConstants.CANCELADJ_GROUP_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CENTER_CHARGES)){
				forward= CustomerConstants.CANCELADJ_CENTER_SUCCESS;
			}
			else if(inputPage.equals(CustomerConstants.VIEW_CLIENT_CHARGES)){
				forward= CustomerConstants.CANCELADJ_CLIENT_SUCCESS;
			}
		}
		return mapping.findForward(forward);
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method)  {
		return true;
		
	}
	
	private void resetActionFormFields(CustomerApplyAdjustmentActionForm applyAdjustmentActionForm){
		applyAdjustmentActionForm.setAdjustmentNote(null);
	}

}

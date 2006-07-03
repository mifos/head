/**

 * AccountApplyPaymentAction.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.accounts.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class AccountApplyPaymentAction extends BaseAction{
	AccountBusinessService accountBusinessService=null;
	LoanBusinessService loanBusinessService=null;
	private MasterDataService masterDataService;
	
	public AccountApplyPaymentAction(){}	
	
	@Override
	protected BusinessService getService() throws ServiceException{
		return getAccountBusinessService();
	}
	
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public ActionForward load(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		AccountApplyPaymentActionForm actionForm =(AccountApplyPaymentActionForm)form;
		clearActionForm(actionForm);
		AccountBO account = getAccountBusinessService().getAccount(Integer.valueOf(actionForm.getAccountId()));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request.getSession());
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,	getMasterDataService().retrievePaymentTypes(uc.getLocaleId()),request.getSession());
		actionForm.setAmount(account.getTotalAmountDue());
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward previous(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		return mapping.findForward(ActionForwards.previous_success.toString());
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		return mapping.findForward(getForward(((AccountApplyPaymentActionForm)form).getInput()));
	}
	
	private void clearActionForm(AccountApplyPaymentActionForm actionForm){
		actionForm.setReceiptDate(null);
		actionForm.setReceiptId(null);
		actionForm.setPaymentTypeId(null);
	}
	
	private String getForward(String input){
		if(input.equals("loan")){
			return ActionForwards.loan_detail_page.toString();
		}
		return null;
	}
	
	private PaymentTypeEntity getPaymentType(HttpServletRequest request, Short paymentTypeId){
		List<PaymentTypeEntity> paymentTypeList  = (List<PaymentTypeEntity>)SessionUtils.getAttribute(MasterConstants.PAYMENT_TYPE,request.getSession());
		for(PaymentTypeEntity paymentType: paymentTypeList){
			if(paymentType.getId().equals(paymentTypeId))
				return paymentType;
		}
		return null;
	}
	
	private AccountBusinessService getAccountBusinessService()throws ServiceException{
		if(accountBusinessService==null)
			accountBusinessService =(AccountBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
		return accountBusinessService;
	}
	
	private MasterDataService getMasterDataService()throws ServiceException{
		if(masterDataService==null)
			masterDataService =(MasterDataService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.MasterDataService);
		return masterDataService;
	}
}

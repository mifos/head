/**
 
 * SavingsClosureAction.java    version: 1.0
 
 
 
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
package org.mifos.application.accounts.savings.struts.action;



import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.SavingsClosureActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class SavingsClosureAction extends BaseAction {
	private SavingsBusinessService savingsService;
	private MasterDataService masterDataService;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public SavingsClosureAction() throws ServiceException{
		savingsService=(SavingsBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Savings);
		masterDataService = (MasterDataService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.MasterDataService);
	}
	
	protected BusinessService getService() {
		return savingsService;
	}
	
	protected boolean skipActionFormToBusinessObjectConversion(String method)  {
		if(method.equals("load")||method.equals("preview")||method.equals("previous")|| method.equals("close")|| method.equals("cancel")||method.equals("closeAccount")){
			logger.debug("In SavingsClosureAction::skipActionFormToBusinessObjectConversion(), Skipping for Method: "+ method);
			return true;
		}
		return false;
	}
	
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		clearActionForm(form);
		
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		SavingsBO savings =(SavingsBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
		logger.debug("In SavingsClosureAction::load(), accountId: "+ savings.getAccountId());
		//retrieve the savings object

		savings=savingsService.findById(savings.getAccountId());
		Hibernate.initialize(savings.getCustomer());
		Hibernate.initialize(savings.getCustomer().getPersonnel());
		Hibernate.initialize(savings.getAccountNotes());
		Hibernate.initialize(savings.getAccountStatusChangeHistory());
		Hibernate.initialize(savings.getSavingsActivityDetails());
		initialize(savings.getSavingsOffering().getDepositGLCode());
		initialize(savings.getSavingsOffering().getInterestGLCode());

		savings.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,savings,request.getSession());
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,	masterDataService.retrievePaymentTypes(uc.getLocaleId()),request.getSession());
		//client list will be loaded only if it is center savings account, 
		//or group savings account with deposit schedule of per client
		
		if(savings.getCustomer().getCustomerLevel().getId().shortValue()==CustomerConstants.CENTER_LEVEL_ID || 
				(savings.getCustomer().getCustomerLevel().getId().shortValue()==CustomerConstants.GROUP_LEVEL_ID &&
					savings.getRecommendedAmntUnit().getRecommendedAmntUnitId().shortValue()==ProductDefinitionConstants.PERINDIVIDUAL))
			SessionUtils.setAttribute(SavingsConstants.CLIENT_LIST,savings.getCustomer().getChildren(CustomerConstants.CLIENT_LEVEL_ID),request.getSession());
		else
			SessionUtils.setAttribute(SavingsConstants.CLIENT_LIST,null,request.getSession());
		
		Money interestAmount = savings.calculateInterestForClosure(new SavingsHelper().getCurrentDate());
		logger.debug("In SavingsClosureAction::load(), Interest calculated:  "+ interestAmount.getAmountDoubleValue());
		AccountPaymentEntity payment = new AccountPaymentEntity(savings,savings.getSavingsBalance().add(interestAmount),null,null,null);
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT,payment, request.getSession());
		return mapping.findForward("load_success");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("In SavingsClosureAction::preview()");
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		SavingsClosureActionForm actionForm = (SavingsClosureActionForm)form;
		AccountPaymentEntity payment = (AccountPaymentEntity)SessionUtils.getAttribute(SavingsConstants.ACCOUNT_PAYMENT,request.getSession());
		AccountPaymentEntity accountPaymentEntity=null;
		if(actionForm.getReceiptDate()!=null && actionForm.getReceiptDate()!="")
			accountPaymentEntity = new AccountPaymentEntity(payment
					.getAccount(), payment.getAmount(), actionForm
					.getReceiptId(), new java.util.Date(DateHelper
					.getLocaleDate(uc.getPereferedLocale(),
							actionForm.getReceiptDate()).getTime()),
					new PaymentTypeEntity(Short.valueOf(actionForm
							.getPaymentTypeId())));
		else
			accountPaymentEntity = new AccountPaymentEntity(payment
					.getAccount(), payment.getAmount(), actionForm
					.getReceiptId(),null,
					new PaymentTypeEntity(Short.valueOf(actionForm
							.getPaymentTypeId())));
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_PAYMENT,accountPaymentEntity, request.getSession());
		return mapping.findForward("preview_success");
	}
	
	public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("In SavingsClosureAction::previous()");
		return mapping.findForward("previous_success");
	}
	
	public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		AccountPaymentEntity payment = (AccountPaymentEntity)SessionUtils.getAttribute(SavingsConstants.ACCOUNT_PAYMENT,request.getSession());
		SavingsBO savings =(SavingsBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
		logger.debug("In SavingsClosureAction::close(), accountId: "+ savings.getAccountId());
		SavingsClosureActionForm actionForm = (SavingsClosureActionForm)form;
		AccountNotesEntity notes = new AccountNotesEntity();
		notes.setComment(actionForm.getNotes());
		CustomerBO customer = searchForCustomer(request,actionForm.getCustomerId());
		if (customer==null)
			customer = savings.getCustomer();
		savings.closeAccount(payment,notes,customer);
		request.getSession().removeAttribute(SavingsConstants.CLIENT_LIST);
		request.getSession().removeAttribute(SavingsConstants.ACCOUNT_PAYMENT);
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().evict(savings);
		return mapping.findForward("close_success");
	}

	private CustomerBO searchForCustomer(HttpServletRequest request, String customerId){
		Object obj=SessionUtils.getAttribute(SavingsConstants.CLIENT_LIST,request.getSession());
		if(obj!=null && customerId!=null && customerId!=""){
			List<CustomerBO> customerList=(List<CustomerBO>)obj;
			for(CustomerBO customer:customerList){
				if(customer.getCustomerId().equals(Integer.valueOf(customerId)))
					return customer;
			}
		}
		return null;
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		logger.debug("In SavingsClosureAction::cancel()");
		return mapping.findForward("close_success");
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		String method = (String)request.getAttribute("methodCalled");
		logger.debug("In SavingsClosureAction::validate(), method: "+ method);
		String forward=null;
		if(method!=null && method.equals("preview"))
				forward="preview_faliure";
		return mapping.findForward(forward);
	}
	
	private void clearActionForm(ActionForm form){
		SavingsClosureActionForm actionForm =(SavingsClosureActionForm)form;
		actionForm.setNotes(null);
		actionForm.setReceiptDate(null);
		actionForm.setReceiptId(null);
		actionForm.setPaymentTypeId(null);
		actionForm.setCustomerId(null);
	}
	
	private void initialize(GLCodeEntity glCode)
	{
		Hibernate.initialize(glCode);
		Hibernate.initialize(glCode.getAssociatedCOA());
		
		Hibernate.initialize(glCode.getAssociatedCOA().getCOAHead());
		Hibernate.initialize(glCode.getAssociatedCOA().getAssociatedGlcode());
		Hibernate.initialize(glCode.getAssociatedCOA().getSubCategory());

	}
}

/**

 * AcceptedPaymentTypeAction.java    version: 1.0

 

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


package org.mifos.application.acceptedpaymenttype.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.application.acceptedpaymenttype.struts.actionform.AcceptedPaymentTypeActionForm;
import org.mifos.application.acceptedpaymenttype.util.helpers.AcceptedPaymentTypeConstants;
import org.mifos.application.master.MessageLookup;
import java.util.ArrayList;
import java.util.List;
import org.mifos.application.master.util.helpers.PaymentTypes;
import java.util.Locale;
import org.mifos.application.acceptedpaymenttype.util.helpers.PaymentTypeData;
import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.accounts.business.AccountActionEntity;

public class AcceptedPaymentTypeAction extends BaseAction{
	
	private MifosLogger logger = MifosLogManager
	.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	private AccountActionTypes[] accountActionTypesForAcceptedPaymentType =
	{AccountActionTypes.FEE_REPAYMENT, AccountActionTypes.LOAN_REPAYMENT, 
			AccountActionTypes.DISBURSAL, AccountActionTypes.SAVINGS_DEPOSIT, 
			AccountActionTypes.SAVINGS_WITHDRAWAL};
	
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("acceptedPaymentTypeAction");
		
		security.allow("load", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);
		return security;
	}
	
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	private List<PaymentTypeData> getAllPaymentTypes(Locale locale)
	{
	     List<PaymentTypeData> paymentTypeList = new ArrayList();
	     PaymentTypeData payment = null;
	     Short id = 0;
	     for (PaymentTypes paymentType : PaymentTypes.values()) {
	    	 id = paymentType.getValue();
	    	 payment = new PaymentTypeData(id);
	    	 String paymentName = MessageLookup.getInstance().lookup(paymentType, locale);
	    	 payment.setName(paymentName);
	    	 paymentTypeList.add(payment);
			}
	     
	     return paymentTypeList;
	}
	
	private void RemoveFromInList(List<PaymentTypeData> list, Short paymentTypeId)
	{
		for (int i=list.size()-1; i >= 0; i--) 
		{
			if (list.get(i).getId().shortValue() == paymentTypeId.shortValue())
				list.remove(i);
		}
	}
	
	private void setPaymentTypesForAnAccountAction(List<PaymentTypeData> payments, AccountActionTypes accountAction,
			AcceptedPaymentTypePersistence paymentTypePersistence, HttpServletRequest request) throws Exception
	{
		
		Short accountActionId = accountAction.getValue();
		List<AcceptedPaymentType> paymentTypeList = paymentTypePersistence.getAcceptedPaymentTypesForAnAccountAction(accountActionId);
		List<PaymentTypeData> inList = new ArrayList(payments);
		List<PaymentTypeData> outList = new ArrayList();
	
		PaymentTypeData data = null;
		for (AcceptedPaymentType paymentType : paymentTypeList)
		{
			Short paymentTypeId = paymentType.getPaymentType().getId();
			data = new PaymentTypeData(paymentTypeId);
			data.setName(GetPaymentTypeName(paymentTypeId, payments));
			data.setAcceptedPaymentTypeId(paymentType.getAcceptedPaymentTypeId());
			outList.add(data);
			RemoveFromInList(inList, paymentTypeId);
		}
		if (accountAction == AccountActionTypes.LOAN_REPAYMENT)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_REPAYMENT_LIST,
					inList, request);
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.FEE_REPAYMENT)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_FEE_LIST,
					inList, request);
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.DISBURSAL)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DISBURSEMENT_LIST,
					inList, request);
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_DEPOSIT)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_DEPOSIT_LIST,
					inList, request);
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST,
					outList, request);
		}
		else if (accountAction == AccountActionTypes.SAVINGS_WITHDRAWAL)
		{		
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.IN_WITHDRAWAL_LIST,
					inList, request);
			SessionUtils.setCollectionAttribute(AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST,
					outList, request);
		}
		else
			throw new Exception("Unknow account action for accepted payment type " + accountAction.toString());
	}
	
	private String GetPaymentTypeName(Short paymentTypeId, List<PaymentTypeData> paymentTypes)
	{
		
		for (PaymentTypeData paymentTypeData : paymentTypes)
		{
			Short paymentId = paymentTypeData.getId();
			if (paymentId.shortValue() == paymentTypeId.shortValue())
				 return paymentTypeData.getName();
		}
		return "";
	}
	
	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = (AcceptedPaymentTypeActionForm) form;
		acceptedPaymentTypeActionForm.clear();
		UserContext userContext = getUserContext(request);
		Locale locale = userContext.getPreferredLocale();
		
		List<PaymentTypeData> payments = getAllPaymentTypes(locale);
		acceptedPaymentTypeActionForm.setAllPaymentTypes(payments);
		AcceptedPaymentTypePersistence paymentTypePersistence = new AcceptedPaymentTypePersistence();
		for (int i=0; i < accountActionTypesForAcceptedPaymentType.length; i++)
			setPaymentTypesForAnAccountAction(payments, accountActionTypesForAcceptedPaymentType[i], paymentTypePersistence, request);
		
		logger.debug("Outside load method");
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	
	private boolean FindDelete(PaymentTypeData paymentType, String[] paymentTypes)
	{
		if (paymentTypes == null)
			return true;
		Short paymentTypeId = paymentType.getId();
		for (int i=0; i < paymentTypes.length; i++)
		{
			Short paymentId = Short.parseShort(paymentTypes[i]);
			if (paymentId.shortValue() == paymentTypeId.shortValue())
				 return false;
		}
		return true;
	}
	
	private boolean FindNew(Short paymentTypeId, List<PaymentTypeData> paymentTypes)
	{
		
		for (PaymentTypeData paymentTypeData : paymentTypes)
		{
			Short paymentId = paymentTypeData.getId();
			if (paymentId.shortValue() == paymentTypeId.shortValue())
				 return false;
		}
		return true;
	}
	
	private void Process(String[] selectedPaymentTypes, List<PaymentTypeData> outList, 
			List<AcceptedPaymentType> deletedPaymentTypeList, List<AcceptedPaymentType> addedPaymentTypeList, 
			AcceptedPaymentTypePersistence persistence, AccountActionTypes accountActionType)
	{
		AcceptedPaymentType acceptedPaymentType = null;
		if ((outList != null) && (outList.size() > 0))
		{
			for (PaymentTypeData paymentType :  outList)
			{
				 if (FindDelete(paymentType, selectedPaymentTypes))
				 {
					 acceptedPaymentType = persistence.getAcceptedPaymentType(paymentType.getAcceptedPaymentTypeId());
					 deletedPaymentTypeList.add(acceptedPaymentType);
				 }
			}
		}
		if (selectedPaymentTypes != null)
		{
			for (int i = 0; i < selectedPaymentTypes.length; i++) 
			{
				Short paymentTypeId = Short.parseShort(selectedPaymentTypes[i]);
				 if (FindNew(paymentTypeId, outList))
				 {
					 acceptedPaymentType = new AcceptedPaymentType();
					 PaymentTypeEntity paymentTypeEntity = new PaymentTypeEntity(paymentTypeId);
					 acceptedPaymentType.setPaymentType(paymentTypeEntity);
					 AccountActionTypes accountActionEnum = accountActionType;
					 AccountActionEntity accountAction = new AccountActionEntity(accountActionEnum);
					 acceptedPaymentType.setAccountAction(accountAction); 
					 addedPaymentTypeList.add(acceptedPaymentType);
				 }
			}
		}
	}
	
	private void ProcessOneAccountActionAcceptedPaymentTypes(AccountActionTypes accountActionType, AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm,
			List<AcceptedPaymentType> deletedPaymentTypeList, List<AcceptedPaymentType> addedPaymentTypeList, 
			HttpServletRequest request, AcceptedPaymentTypePersistence persistence)
	                     throws Exception
	{
		//	new accepted payments 
		String[] selectedPaymentTypes = null;
		//  old accepted payments 
		List<PaymentTypeData> outList = null;
		if (accountActionType == AccountActionTypes.FEE_REPAYMENT)
		{
			selectedPaymentTypes = acceptedPaymentTypeActionForm.getFees();
			outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_FEE_LIST, request);
		}
		else if (accountActionType == AccountActionTypes.DISBURSAL)
		{
			selectedPaymentTypes = acceptedPaymentTypeActionForm.getDisbursements();
			outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_DISBURSEMENT_LIST, request);
		}
		else if (accountActionType == AccountActionTypes.LOAN_REPAYMENT)
		{
			selectedPaymentTypes = acceptedPaymentTypeActionForm.getRepayments();
			outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_REPAYMENT_LIST, request);
		}
		else if (accountActionType == AccountActionTypes.SAVINGS_DEPOSIT)
		{
			selectedPaymentTypes = acceptedPaymentTypeActionForm.getDeposits();
			outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_DEPOSIT_LIST, request);
		}
		else if (accountActionType == AccountActionTypes.SAVINGS_WITHDRAWAL)
		{
			selectedPaymentTypes = acceptedPaymentTypeActionForm.getWithdrawals();
			outList = (List<PaymentTypeData>)SessionUtils.getAttribute(AcceptedPaymentTypeConstants.OUT_WITHDRAWAL_LIST, request);
		}
		else
			throw new Exception("Unknow account action for accepted payment type " + accountActionType.toString());
		Process(selectedPaymentTypes, outList, deletedPaymentTypeList, addedPaymentTypeList, persistence, accountActionType);
	}
	
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
		AcceptedPaymentTypeActionForm acceptedPaymentTypeActionForm = (AcceptedPaymentTypeActionForm) form;
		AcceptedPaymentTypePersistence persistence = new AcceptedPaymentTypePersistence();
		
		List<AcceptedPaymentType> deletedPaymentTypeList = new ArrayList();
		List<AcceptedPaymentType> addedPaymentTypeList = new ArrayList();
		for (int i=0; i < accountActionTypesForAcceptedPaymentType.length; i++)
			ProcessOneAccountActionAcceptedPaymentTypes(accountActionTypesForAcceptedPaymentType[i], acceptedPaymentTypeActionForm,
					deletedPaymentTypeList, addedPaymentTypeList, request, persistence);
		
		
		if (addedPaymentTypeList.size() > 0)
			persistence.addAcceptedPaymentTypes(addedPaymentTypeList);
		if (deletedPaymentTypeList.size() > 0)
			persistence.deleteAcceptedPaymentTypes(deletedPaymentTypeList);
		
		return mapping.findForward(ActionForwards.update_success.toString());
		
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("cancel method called");
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}
	
	@Override
	protected BusinessService getService() {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Configuration);
	}

}


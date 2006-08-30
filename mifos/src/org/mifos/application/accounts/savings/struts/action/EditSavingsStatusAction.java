/**

 * EditSavingsStatusAction.java    version: 1.0

 

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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountNotesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.struts.actionforms.EditSavingsStatusActionForm;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.customer.center.exception.StateChangeException;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.SessionUtils;

public class EditSavingsStatusAction extends AccountAppAction {

	private SavingsBusinessService savingsService;
	private AccountBusinessService accountBusinessService;

	private PersonnelPersistenceService personnelPersistenceService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public EditSavingsStatusAction() throws ServiceException {
		savingsService = (SavingsBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Savings);
		personnelPersistenceService = (PersonnelPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.Personnel);
		accountBusinessService = (AccountBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Accounts);
	}

	protected BusinessService getService() {
		return savingsService;
	}

	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		if (method.equals(MethodNameConstants.PREVIOUS)
				|| method.equals(MethodNameConstants.UPDATE)
				|| method.equals(MethodNameConstants.LOAD)
				|| method.equals(MethodNameConstants.PREVIEW)
				|| method.equals(MethodNameConstants.CANCEL)) {
			logger
					.debug("In EditSavingsStatusAction::skipActionFormToBusinessObjectConversion(), Skipping for Method: "
							+ method);
			return true;
		} else
			return false;
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In EditSavingsStatusAction:load()");
		doCleanUp(form, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savingsBO = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		Integer accountId = savingsBO.getAccountId();
		request.getSession().removeAttribute(Constants.BUSINESS_KEY);
		savingsBO = savingsService.findById(accountId);
		savingsBO.setUserContext(userContext);
		savingsBO.getAccountState().setLocaleId(userContext.getLocaleId());
		for (AccountActionDateEntity actionDate : savingsBO
				.getAccountActionDates())
			Hibernate.initialize(actionDate);
		Hibernate.initialize(savingsBO.getAccountStatusChangeHistory());
		Hibernate.initialize(savingsBO.getAccountNotes());
		Hibernate.initialize(savingsBO.getSavingsOffering());
		Hibernate.initialize(savingsBO.getAccountFlags());
		Hibernate.initialize(savingsBO.getTimePerForInstcalc());
		Hibernate.initialize(savingsBO.getCustomer().getPersonnel());
		Hibernate.initialize(savingsBO.getSavingsOffering().getFreqOfPostIntcalc().getMeeting());
		
		if (savingsBO.getCustomer().getCustomerMeeting() != null
				&& savingsBO.getCustomer().getCustomerMeeting().getMeeting() != null)
			Hibernate.initialize(savingsBO.getCustomer().getCustomerMeeting()
					.getMeeting());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsBO, request
				.getSession());
		savingsBO.initializeStateMachine(userContext.getLocaleId());
		SessionUtils.setAttribute(SavingsConstants.STATUS_LIST, savingsBO
				.getStatusList(), request.getSession());
		return mapping.findForward(MethodNameConstants.LOAD_SUCCESS);
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception, NumberFormatException {
		logger.debug("In EditSavingsStatusAction:preview()");
		SavingsBO savingsBO = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());

		List<CheckListMaster> checklist = accountBusinessService.getStatusChecklist(
				Short.valueOf(((EditSavingsStatusActionForm) form)
						.getNewStatusId()), savingsBO.getAccountType()
						.getAccountTypeId());
		SessionUtils.setAttribute(SavingsConstants.STATUS_CHECK_LIST,
				checklist, request.getSession());
		AccountNotesEntity accountNotes = savingsBO.createAccountNotes(((EditSavingsStatusActionForm) form).getAccountNotes().getComment());
		SessionUtils.setAttribute(SavingsConstants.ACCOUNT_NOTES, accountNotes,
				request.getSession());

		String newStatusName = null;
		if (null != ((EditSavingsStatusActionForm) form).getNewStatusId()
				&& !"".equals(((EditSavingsStatusActionForm) form)
						.getNewStatusId())) {
			newStatusName = savingsBO.getStatusName(userContext.getLocaleId(),
					new Short(((EditSavingsStatusActionForm) form)
							.getNewStatusId()));
		}
		SessionUtils.setAttribute(SavingsConstants.NEW_STATUS_NAME,
				newStatusName, request.getSession());
		String flagName = null;

		if (null != ((EditSavingsStatusActionForm) form).getFlagId()
				&& !"".equals(((EditSavingsStatusActionForm) form).getFlagId())
				&& (Short.valueOf(
						((EditSavingsStatusActionForm) form).getNewStatusId())
						.shortValue() == AccountStates.SAVINGS_ACC_CANCEL)) {

			flagName = savingsBO.getFlagName(new Short(
					((EditSavingsStatusActionForm) form).getFlagId()));
		}
		SessionUtils.setAttribute(SavingsConstants.FLAG_NAME, flagName, request
				.getSession());
		return mapping.findForward(MethodNameConstants.PREVIEW_SUCCESS);
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In EditSavingsStatusAction:previous()");
		return mapping.findForward(MethodNameConstants.PREVIOUS_SUCCESS);
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In EditSavingsStatusAction:cancel()");
		return mapping.findForward(MethodNameConstants.CANCEL_SUCCESS);
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ServiceException, PersistenceException{
		logger.debug("In EditSavingsStatusAction:update()");
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SavingsBO savingsBO = (SavingsBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		//HibernateUtil.getSessionTL().lock(savingsBO.getCustomer(),LockMode.UPGRADE);
		AccountNotesEntity accountNotesEntity = (AccountNotesEntity) SessionUtils
				.getAttribute(SavingsConstants.ACCOUNT_NOTES, request
						.getSession());
		try {
			AccountStateEntity accountStateEntity = new AccountStateEntity(
					new Short(((EditSavingsStatusActionForm) form)
							.getNewStatusId()));
			AccountStateFlagEntity accountStateFlagEntity = null;
			if(((EditSavingsStatusActionForm) form).getFlagId() != null) {
				accountStateFlagEntity = accountBusinessService.getAccountStateFlag(new Short(((EditSavingsStatusActionForm) form).getFlagId()));
				accountStateFlagEntity.setLocaleId(userContext.getLocaleId());
			}
			savingsBO.changeStatus(accountStateEntity, accountNotesEntity,accountStateFlagEntity,userContext);
		}catch(AccountException e){
			ActionErrors errors = new ActionErrors();
			errors.add(SavingsConstants.STATUS_CHANGE_NOT_ALLOWED,
					new ActionMessage(
							SavingsConstants.STATUS_CHANGE_NOT_ALLOWED));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(MethodNameConstants.UPDATE_FAILURE);
		} catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		SessionUtils.setAttribute(SavingsConstants.NEW_FLAG_NAME,
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()), request.getSession());
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().evict(savingsBO);
		return mapping.findForward(MethodNameConstants.UPDATE_SUCCESS);
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
		String forward = null;
		if (method != null) {
			if (method.equals(MethodNameConstants.PREVIEW))
				forward = MethodNameConstants.PREVIEW_FAILURE;
			else if (method.equals(MethodNameConstants.LOAD))
				forward = MethodNameConstants.LOAD_FAILURE;
			else if (method.equals(MethodNameConstants.UPDATE))
				forward = MethodNameConstants.UPDATE_FAILURE;
		}
		return mapping.findForward(forward);
	}

	private void doCleanUp(ActionForm form, HttpServletRequest request) {
		EditSavingsStatusActionForm editSavingsStatusActionForm = (EditSavingsStatusActionForm) form;
		editSavingsStatusActionForm.setSelectedItems(null);
		editSavingsStatusActionForm.getAccountNotes().setComment(null);
		editSavingsStatusActionForm.setNewStatusId(null);
		editSavingsStatusActionForm.setFlagId(null);
	}
}

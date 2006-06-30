/**

 * FeeAction.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.application.fees.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.business.service.FeesBusinessService;
import org.mifos.application.fees.struts.actionforms.FeeActionForm;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class FeeAction extends BaseAction {

	public FeeAction() throws Exception {
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return (FeesBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.FeesService);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return !method.equals(Methods.preview.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession session = request.getSession();
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, session);
		Short localeId = userContext.getLocaleId();
		doCleanUp(session, userContext);
		obtainCreateMasterData(localeId, session);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		FeeActionForm feeActionForm = (FeeActionForm) form;
		boolean adminCheck = feeActionForm.getAdminCheck() != null
				&& feeActionForm.getAdminCheck().equals(
						FeesConstants.ADMINCHECK);
		fees.save(adminCheck);
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = null;
		String input = request.getParameter("input");
		if (input == null || "".equals(input.trim()))
			forward = ActionForwards.preview_failure.toString();
		else if (input.equals("previewFees"))
			forward = ActionForwards.preview_success.toString();
		else if (input.equals("previewEditFees"))
			forward = ActionForwards.editpreview_success.toString();
		else
			forward = ActionForwards.editpreview_failure.toString();
		return mapping.findForward(forward);
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeesBusinessService feesBusinessService = (FeesBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.FeesService);

		HttpSession session = request.getSession();
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, session);
		Short localeId = userContext.getLocaleId();
		doCleanUp(session, userContext);
		Short feeId = Short.valueOf(request.getParameter("feeId"));

		FeesBO fees = feesBusinessService.getFees(feeId);
		fees.setUserContext(userContext);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, fees, session);
		obtainUpdateMasterData(localeId, session);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeeActionForm feeActionForm = (FeeActionForm) form;
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		FeeStatus status = feeActionForm.getFeeStatus().getStatusId().equals(
				FeeStatus.ACTIVE.getValue()) ? FeeStatus.ACTIVE
				: FeeStatus.INACTIVE;
		fees.modifyStatus(status);
		if (fees.getRate() == null)
			fees.setAmount(feeActionForm.getAmount());
		else
			fees.setRate(Double.valueOf(feeActionForm.getRate()));

		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeesBO fees = (FeesBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		fees.update();
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private List<GLCodeEntity> getGLCodes() throws SystemException,
			ApplicationException {

		FinancialBusinessService financialBusinessService = new FinancialBusinessService();
		return financialBusinessService.getGLCodes(
				FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);
	}

	private List<LookUpMaster> getTimeOfChargeForCustomer(
			List<LookUpMaster> timeOfCharges) {
		List<LookUpMaster> customerTimeOfCharges = new ArrayList<LookUpMaster>();
		for (LookUpMaster lookUpMaster : timeOfCharges)
			if (lookUpMaster.getId().intValue() == FeePayment.UPFRONT
					.getValue().intValue())
				customerTimeOfCharges.add(lookUpMaster);
		return customerTimeOfCharges;
	}

	private void doCleanUp(HttpSession session, UserContext userContext) {
		session.setAttribute("feeactionform", null);
		session.removeAttribute(Constants.BUSINESS_KEY);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, new FeesBO(
				userContext), session);
	}

	private void obtainCreateMasterData(Short localeId, HttpSession session)
			throws ApplicationException, SystemException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		SessionUtils
				.setAttribute(
						FeesConstants.CATEGORYLIST,
						masterDataService
								.getMasterData(
										FeesConstants.FEECATEGORY,
										localeId,
										"org.mifos.application.fees.business.CategoryTypeEntity",
										"categoryId").getLookUpMaster(),
						session);
		EntityMaster timeOfChargesEntityMaster = masterDataService
				.getMasterData(FeesConstants.FEEPAYMENT, localeId,
						"org.mifos.application.fees.business.FeePaymentEntity",
						"feePaymentId");
		List<LookUpMaster> timeOfCharges = timeOfChargesEntityMaster
				.getLookUpMaster();
		SessionUtils.setAttribute(FeesConstants.LOANTIMEOFCHARGES,
				timeOfCharges, session);
		SessionUtils.setAttribute(FeesConstants.CUSTOMERTIMEOFCHARGES,
				getTimeOfChargeForCustomer(timeOfCharges), session);
		SessionUtils.setAttribute(FeesConstants.FORMULALIST, masterDataService
				.getMasterData(FeesConstants.FEEFORMULA, localeId,
						"org.mifos.application.fees.business.FeeFormulaEntity",
						"feeFormulaId").getLookUpMaster(), session);
		SessionUtils.setAttribute(FeesConstants.GLCODE_LIST, getGLCodes(),
				session);
	}

	private void obtainUpdateMasterData(Short localeId, HttpSession session)
			throws ApplicationException, SystemException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		SessionUtils.setAttribute(FeesConstants.STATUSLIST, masterDataService
				.getMasterData(FeesConstants.FEESTATUS, localeId,
						"org.mifos.application.fees.business.FeeStatusEntity",
						"statusId").getLookUpMaster(), session);
		SessionUtils.setAttribute(FeesConstants.FORMULALIST, masterDataService
				.getMasterData(FeesConstants.FEEFORMULA, localeId,
						"org.mifos.application.fees.business.FeeFormulaEntity",
						"feeFormulaId").getLookUpMaster(), session);
	}
}

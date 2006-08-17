/**

 * FeeAction.java    version: 1.0

 

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
package org.mifos.application.fees.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.CategoryTypeEntity;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeFormulaEntity;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeePaymentEntity;
import org.mifos.application.fees.business.FeeStatusEntity;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.struts.actionforms.FeeActionForm;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
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
		return getFeeBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		doCleanUp(request);
		loadCreateMasterData(request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeeActionForm actionForm = (FeeActionForm) form;
		FeeBO fee = createFee(actionForm, request);
		fee.save();
		actionForm.setFeeId(fee.getFeeId().toString());
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForwards forward = null;
		String method = (String) request.getAttribute("methodCalled");
		if (method != null) {
			if (method.equals(Methods.preview.toString()))
				forward = ActionForwards.preview_failure;
			else if (method.equals(Methods.editPreview.toString()))
				forward = ActionForwards.editpreview_failure;
			else if (method.equals(Methods.previous.toString())
					|| method.equals(Methods.create.toString()))
				forward = ActionForwards.previous_failure;
			else if (method.equals(Methods.editPrevious.toString())
					|| method.equals(Methods.update.toString()))
				forward = ActionForwards.editprevious_failure;
		}
		return mapping.findForward(forward.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeeActionForm actionForm = (FeeActionForm) form;
		FeeBO fee = getFeeBusinessService().getFee(actionForm.getFeeIdValue());
		setLocaleForMasterEntities(fee, getUserContext(request).getLocaleId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, fee, request);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeeBO fee = (FeeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				request);
		FeeActionForm feeActionForm = (FeeActionForm) form;
		feeActionForm.setFeeStatus(fee.getFeeStatus().getId().toString());
		if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
			feeActionForm.setAmount(((AmountFeeBO) fee).getFeeAmount()
					.toString());
			feeActionForm.setRate(null);
			feeActionForm.setFeeFormula(null);
		} else {
			feeActionForm.setRate(((RateFeeBO) fee).getRate().toString());
			feeActionForm.setFeeFormula(((RateFeeBO) fee).getFeeFormula()
					.getId().toString());
			feeActionForm.setAmount(null);
		}
		loadUpdateMasterData(request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		FeeActionForm feeActionForm = (FeeActionForm) form;
		FeeBO fee = (FeeBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
				request);
		fee.setUserContext(getUserContext(request));
		if (fee.getFeeType().equals(RateAmountFlag.AMOUNT))
			((AmountFeeBO) fee).setFeeAmount(feeActionForm.getAmountValue());
		else
			((RateFeeBO) fee).setRate(feeActionForm.getRateValue());

		fee.updateStatus(feeActionForm.getFeeStatusValue());
		fee.update();
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward viewAll(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Short localeId = getUserContext(request).getLocaleId();

		List<FeeBO> customerFees = getFeeBusinessService()
				.retrieveCustomerFees();
		List<FeeBO> productFees = getFeeBusinessService().retrieveProductFees();

		for (FeeBO fee : customerFees)
			setLocaleForMasterEntities(fee, localeId);
		for (FeeBO fee : productFees)
			setLocaleForMasterEntities(fee, localeId);

		SessionUtils.setAttribute(FeeConstants.CUSTOMER_FEES, customerFees,
				request);
		SessionUtils.setAttribute(FeeConstants.PRODUCT_FEES, productFees,
				request);
		return mapping.findForward(ActionForwards.viewAll_success.toString());
	}

	private FeeBO createFee(FeeActionForm actionForm, HttpServletRequest request)
			throws ApplicationException {

		CategoryTypeEntity feeCategory = (CategoryTypeEntity) findMasterEntity(
				request, FeeConstants.CATEGORYLIST, actionForm
						.getCategoryTypeValue().getValue());
		FeeFrequencyTypeEntity feeFrequencyType = (FeeFrequencyTypeEntity) findMasterEntity(
				request, FeeConstants.FEE_FREQUENCY_TYPE_LIST, actionForm
						.getFeeFrequencyTypeValue().getValue());
		GLCodeEntity glCode = findGLCodeEntity(request,
				FeeConstants.GLCODE_LIST, actionForm.getGlCodeValue());

		if (feeFrequencyType.isOneTime())
			return createOneTimeFee(actionForm, request, feeCategory,
					feeFrequencyType, glCode);
		else
			return createPeriodicFee(actionForm, request, feeCategory,
					feeFrequencyType, glCode);

	}

	private FeeBO createOneTimeFee(FeeActionForm actionForm,
			HttpServletRequest request, CategoryTypeEntity feeCategory,
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCode)
			throws ApplicationException {
		UserContext userContext = getUserContext(request);
		FeePaymentEntity feePayment = (FeePaymentEntity) findMasterEntity(
				request, FeeConstants.TIMEOFCHARGES, actionForm
						.getFeePaymentTypeValue().getValue());
		if (actionForm.isRateFee()) {
			FeeFormulaEntity feeFormula = (FeeFormulaEntity) findMasterEntity(
					request, FeeConstants.FORMULALIST, actionForm
							.getFeeFormulaValue().getValue());
			return new RateFeeBO(userContext, actionForm.getFeeName(),
					feeCategory, feeFrequencyType, glCode, actionForm
							.getRateValue(), feeFormula, actionForm
							.isCustomerDefaultFee(), feePayment);
		} else {
			return new AmountFeeBO(userContext, actionForm.getFeeName(),
					feeCategory, feeFrequencyType, glCode, actionForm
							.getAmountValue(), actionForm
							.isCustomerDefaultFee(), feePayment);
		}
	}

	private FeeBO createPeriodicFee(FeeActionForm actionForm,
			HttpServletRequest request, CategoryTypeEntity feeCategory,
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCode)
			throws ApplicationException {
		UserContext userContext = getUserContext(request);
		MeetingBO feeFrequency = actionForm.getFeeRecurrenceTypeValue().equals(
				MeetingFrequency.MONTHLY) ? new MeetingBO(actionForm
				.getFeeRecurrenceTypeValue(), actionForm
				.getMonthRecurAfterValue(), MeetingType.FEEMEETING)
				: new MeetingBO(actionForm.getFeeRecurrenceTypeValue(),
						actionForm.getWeekRecurAfterValue(),
						MeetingType.FEEMEETING);
		if (actionForm.isRateFee()) {
			FeeFormulaEntity feeFormula = (FeeFormulaEntity) findMasterEntity(
					request, FeeConstants.FORMULALIST, actionForm
							.getFeeFormulaValue().getValue());
			return new RateFeeBO(userContext, actionForm.getFeeName(),
					feeCategory, feeFrequencyType, glCode, actionForm
							.getRateValue(), feeFormula, actionForm
							.isCustomerDefaultFee(), feeFrequency);
		} else {
			return new AmountFeeBO(userContext, actionForm.getFeeName(),
					feeCategory, feeFrequencyType, glCode, actionForm
							.getAmountValue(), actionForm
							.isCustomerDefaultFee(), feeFrequency);
		}
	}

	private List<GLCodeEntity> getGLCodes() throws SystemException,
			ApplicationException {

		return new FinancialBusinessService().getGLCodes(
				FinancialActionConstants.FEEPOSTING, FinancialConstants.CREDIT);
	}

	private List<MasterDataEntity> getTimeOfChargeForCustomer(
			List<MasterDataEntity> timeOfCharges) {
		List<MasterDataEntity> customerTimeOfCharges = new ArrayList<MasterDataEntity>();
		for (MasterDataEntity entity : timeOfCharges)
			if (entity.getId().equals(FeePayment.UPFRONT.getValue()))
				customerTimeOfCharges.add(entity);
		return customerTimeOfCharges;
	}

	private void doCleanUp(HttpServletRequest request) {
		request.getSession().setAttribute("feeactionform", null);
	}

	private void loadCreateMasterData(HttpServletRequest request)
			throws ApplicationException, SystemException {
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setAttribute(FeeConstants.CATEGORYLIST, getMasterEntities(
				CategoryTypeEntity.class, localeId), request);

		List<MasterDataEntity> timeOfCharges = getMasterEntities(
				FeePaymentEntity.class, localeId);
		SessionUtils.setAttribute(FeeConstants.TIMEOFCHARGES, timeOfCharges,
				request);
		SessionUtils.setAttribute(FeeConstants.CUSTOMERTIMEOFCHARGES,
				getTimeOfChargeForCustomer(timeOfCharges), request);
		SessionUtils.setAttribute(FeeConstants.FORMULALIST, getMasterEntities(
				FeeFormulaEntity.class, localeId), request);
		SessionUtils.setAttribute(FeeConstants.FEE_FREQUENCY_TYPE_LIST,
				getMasterEntities(FeeFrequencyTypeEntity.class, localeId),
				request);
		SessionUtils.setAttribute(FeeConstants.GLCODE_LIST, getGLCodes(),
				request);
	}

	private void loadUpdateMasterData(HttpServletRequest request)
			throws ApplicationException, SystemException {

		SessionUtils.setAttribute(FeeConstants.STATUSLIST, getMasterEntities(
				FeeStatusEntity.class, getUserContext(request).getLocaleId()),
				request);
	}

	private void setLocaleForMasterEntities(FeeBO fee, Short localeId) {
		fee.getCategoryType().setLocaleId(localeId);
		fee.getFeeFrequency().getFeeFrequencyType().setLocaleId(localeId);
		fee.getFeeStatus().setLocaleId(localeId);
		if (fee.isOneTime())
			fee.getFeeFrequency().getFeePayment().setLocaleId(localeId);
		if (fee.getFeeType().equals(RateAmountFlag.RATE))
			((RateFeeBO) fee).getFeeFormula().setLocaleId(localeId);
	}

	private MasterDataEntity findMasterEntity(HttpServletRequest request,
			String collectionName, Short value) throws PageExpiredException {
		List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (MasterDataEntity entity : entities)
			if (entity.getId().equals(value))
				return entity;
		return null;
	}

	private GLCodeEntity findGLCodeEntity(HttpServletRequest request,
			String collectionName, Short value) throws PageExpiredException {
		List<GLCodeEntity> glCodeList = (List<GLCodeEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (GLCodeEntity glCode : glCodeList)
			if (glCode.getGlcodeId().equals(value))
				return glCode;
		return null;
	}

	private FeeBusinessService getFeeBusinessService() throws ServiceException {
		return (FeeBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.FeesService);
	}
}

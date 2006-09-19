/**

 * LoanPrdAction.java    version: 1.0

 

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
package org.mifos.application.productdefinition.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class LoanPrdAction extends BaseAction {
	private MifosLogger prdDefLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	@Override
	protected BusinessService getService() {
		return new LoanBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start Load method of loan Product Action");
		request.getSession().setAttribute(
				ProductDefinitionConstants.LOANPRODUCTACTIONFORM, null);
		loadMasterData(request);
		prdDefLogger.debug("Load method of loan Product Action called");
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start preview method of loan Product Action");
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start previous method of Loan Product Action");
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start cancelCreate method of loan Product Action");
		return mapping.findForward(ActionForwards.cancelCreate_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request
				.getAttribute(ProductDefinitionConstants.METHODCALLED);
		prdDefLogger.debug("start validate method of Loan Product Action"
				+ method);
		if (method != null) {
			return mapping.findForward(method + "_failure");
		}
		return mapping.findForward(ActionForwards.preview_failure.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start create method of Savings Product Action");
		LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
		UserContext userContext = getUserContext(request);
		Locale locale = getLocale(userContext);
		LoanOfferingBO loanOffering = new LoanOfferingBO(
				userContext,
				loanPrdActionForm.getPrdOfferingName(),
				loanPrdActionForm.getPrdOfferingShortName(),
				getProductCategory(
						((List<ProductCategoryBO>) SessionUtils
								.getAttribute(
										ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST,
										request)), loanPrdActionForm
								.getPrdCategoryValue()),
				(PrdApplicableMasterEntity) findMasterEntity(request,
						ProductDefinitionConstants.LOANAPPLFORLIST,
						loanPrdActionForm.getPrdApplicableMasterValue()),
				loanPrdActionForm.getStartDateValue(locale), loanPrdActionForm
						.getEndDateValue(locale), loanPrdActionForm
						.getDescription(),
				(GracePeriodTypeEntity) findMasterEntity(request,
						ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
						loanPrdActionForm.getGracePeriodTypeValue()),
				loanPrdActionForm.getGracePeriodDurationValue(),
				(InterestTypesEntity) findMasterEntity(request,
						ProductDefinitionConstants.INTERESTTYPESLIST,
						loanPrdActionForm.getInterestTypesValue()),
				loanPrdActionForm.getMinLoanAmountValue(), loanPrdActionForm
						.getMaxLoanAmountValue(), loanPrdActionForm
						.getDefaultLoanAmountValue(), loanPrdActionForm
						.getMaxInterestRateValue(), loanPrdActionForm
						.getMinInterestRateValue(), loanPrdActionForm
						.getDefInterestRateValue(), loanPrdActionForm
						.getMaxNoInstallmentsValue(), loanPrdActionForm
						.getMinNoInstallmentsValue(), loanPrdActionForm
						.getDefNoInstallmentsValue(), loanPrdActionForm
						.isLoanCounterValue(), loanPrdActionForm
						.isIntDedAtDisbValue(), loanPrdActionForm
						.isPrinDueLastInstValue(), getFundsFromList(
						(List<Fund>) SessionUtils.getAttribute(
								ProductDefinitionConstants.SRCFUNDSLIST,
								request), loanPrdActionForm
								.getLoanOfferingFunds()),
				getFeeList((List<FeeBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRDFEE, request),
						loanPrdActionForm.getPrdOfferinFees()), new MeetingBO(
						RecurrenceType.getRecurrenceType(loanPrdActionForm
								.getFreqOfInstallmentsValue()),
						loanPrdActionForm.getRecurAfterValue(),
						loanPrdActionForm.getStartDateValue(locale),
						MeetingType.LOANFREQUENCYOFINSTALLMENTS),
				findGLCodeEntity(request,
						ProductDefinitionConstants.LOANPRICIPALGLCODELIST,
						loanPrdActionForm.getPrincipalGLCode()),
				findGLCodeEntity(request,
						ProductDefinitionConstants.LOANINTERESTGLCODELIST,
						loanPrdActionForm.getInterestGLCode()));
		loanOffering.save();
		request.setAttribute(ProductDefinitionConstants.LOANPRODUCTID,
				loanOffering.getPrdOfferingId());
		request.setAttribute(
				ProductDefinitionConstants.LOANPRDGLOBALOFFERINGNUM,
				loanOffering.getGlobalPrdOfferingNum());

		return mapping.findForward(ActionForwards.create_success.toString());
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		prdDefLogger
				.debug("start Load master data method of Loan Product Action ");
		LoanPrdBusinessService service = (LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct);
		FeeBusinessService feeService = (FeeBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.FeesService);
		List<FeeBO> fees = feeService.getAllAppllicableFeeForLoanCreation();
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setAttribute(
				ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, service
						.getActiveLoanProductCategories(), request);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANAPPLFORLIST,
				service.getLoanApplicableCustomerTypes(localeId), request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
				getMasterEntities(GracePeriodTypeEntity.class, localeId),
				request);
		SessionUtils
				.setAttribute(ProductDefinitionConstants.INTERESTTYPESLIST,
						getMasterEntities(InterestTypesEntity.class, localeId),
						request);
		SessionUtils.setAttribute(ProductDefinitionConstants.INTCALCTYPESLIST,
				getMasterEntities(InterestCalcTypeEntity.class, localeId),
				request);
		SessionUtils.setAttribute(ProductDefinitionConstants.SRCFUNDSLIST,
				service.getSourcesOfFund(), request);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANFEESLIST,
				getFeeViewList(fees), request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.LOANPRICIPALGLCODELIST, getGLCodes(
						FinancialActionConstants.PRINCIPALPOSTING,
						FinancialConstants.CREDIT), request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.LOANINTERESTGLCODELIST, getGLCodes(
						FinancialActionConstants.INTERESTPOSTING,
						FinancialConstants.CREDIT), request);
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDFEE, fees,
				request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.LOANPRDFEESELECTEDLIST,
				new ArrayList<FeeView>(), request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST,
				new ArrayList<Fund>(), request);
		prdDefLogger
				.debug("Load master data method of Loan Product Action called");
	}

	private List<GLCodeEntity> getGLCodes(short financialAction,
			Short debitCredit) throws Exception {
		prdDefLogger.debug("getGLCodes method of Loan Product Action called");
		return new FinancialBusinessService().getGLCodes(financialAction,
				debitCredit);
	}

	private MasterDataEntity findMasterEntity(HttpServletRequest request,
			String collectionName, Short value) throws Exception {
		if (value != null) {
			List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
					.getAttribute(collectionName, request);
			for (MasterDataEntity entity : entities)
				if (entity.getId().equals(value))
					return entity;
		}
		return null;
	}

	private GLCodeEntity findGLCodeEntity(HttpServletRequest request,
			String collectionName, String value) throws PageExpiredException {
		List<GLCodeEntity> glCodeList = (List<GLCodeEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (GLCodeEntity glCode : glCodeList)
			if (glCode.getGlcodeId().equals(getShortValue(value)))
				return glCode;
		return null;
	}

	private ProductCategoryBO getProductCategory(
			List<ProductCategoryBO> productCategories, Short productCategoryId) {
		for (ProductCategoryBO productCategory : productCategories) {
			if (productCategory.getProductCategoryID()
					.equals(productCategoryId))
				return productCategory;
		}
		return null;
	}

	private List<Fund> getFundsFromList(List<Fund> funds, String[] fundsSelected) {
		List<Fund> fundList = new ArrayList<Fund>();
		if (fundsSelected != null && fundsSelected.length > 0 && funds != null
				&& funds.size() > 0) {
			for (String fundSelected : fundsSelected) {
				Fund fund = getFundFromList(funds, fundSelected);
				if (fund != null)
					fundList.add(fund);
			}
		}
		return fundList;
	}

	private Fund getFundFromList(List<Fund> funds, String fundSelected) {
		for (Fund fund : funds)
			if (fund.getFundId().equals(getShortValue(fundSelected)))
				return fund;
		return null;
	}

	private List<FeeView> getFeeViewList(List<FeeBO> fees) {
		List<FeeView> feeViews = new ArrayList<FeeView>();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				feeViews.add(new FeeView(fee));
			}
		}
		return feeViews;
	}

	private List<FeeBO> getFeeList(List<FeeBO> fees, String[] feesSelected) {
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		if (feesSelected != null && feesSelected.length > 0 && fees != null
				&& fees.size() > 0) {
			for (String feeSelected : feesSelected) {
				FeeBO fee = getFeeFromList(fees, feeSelected);
				if (fee != null)
					feeList.add(fee);
			}
		}
		return feeList;
	}

	private FeeBO getFeeFromList(List<FeeBO> fees, String feeSelected) {
		for (FeeBO fee : fees)
			if (fee.getFeeId().equals(getShortValue(feeSelected)))
				return fee;
		return null;
	}
}

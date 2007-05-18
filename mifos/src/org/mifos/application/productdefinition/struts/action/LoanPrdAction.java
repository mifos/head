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

import java.math.BigDecimal;
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
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.fund.business.service.FundBusinessService;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFeesEntity;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.struts.actionforms.LoanPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
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
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("loanproductaction");
		security.allow("load",
				SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("previous", SecurityConstants.VIEW);
		security.allow("cancelCreate", SecurityConstants.VIEW);
		security.allow("validate", SecurityConstants.VIEW);
		security.allow("create",
				SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
		security.allow("viewAllLoanProducts", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("editPreview", SecurityConstants.VIEW);
		security.allow("editPrevious", SecurityConstants.VIEW);
		security.allow("editCancel", SecurityConstants.VIEW);
		security.allow("manage", SecurityConstants.EDIT_LOAN_PRODUCT);
		security.allow("update", SecurityConstants.EDIT_LOAN_PRODUCT);
		security.allow("update", SecurityConstants.EDIT_LOAN_PRODUCT);
		security.allow("loadChangeLog", SecurityConstants.VIEW);
		security.allow("cancelChangeLog", SecurityConstants.VIEW);
		return security;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start Load method of loan Product Action");
		request.getSession().setAttribute(
				ProductDefinitionConstants.LOANPRODUCTACTIONFORM, null);
		loadMasterData(request);
		loadSelectedFeesAndFunds(new ArrayList<FeeView>(),
				new ArrayList<FundBO>(), request);
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
		prdDefLogger.debug("start create method of Loan Product Action");
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
						loanPrdActionForm.getPrdApplicableMasterEnum().getValue()),
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
						(List<FundBO>) SessionUtils.getAttribute(
								ProductDefinitionConstants.SRCFUNDSLIST,
								request), loanPrdActionForm
								.getLoanOfferingFunds()),
				getFeeList((List<FeeBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRDFEE, request),
						loanPrdActionForm.getPrdOfferinFees()), new MeetingBO(
						RecurrenceType.fromInt(loanPrdActionForm
								.getFreqOfInstallmentsValue()),
						loanPrdActionForm.getRecurAfterValue(),
						loanPrdActionForm.getStartDateValue(locale),
						MeetingType.LOAN_INSTALLMENT),
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

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
		prdDefLogger.debug("start manage of Loan Product Action "
				+ loanPrdActionForm.getPrdOfferingId());
		Short prdOfferingId = loanPrdActionForm.getPrdOfferingIdValue();
		loanPrdActionForm.clear();
		LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct))
				.getLoanOffering(prdOfferingId);
		loadMasterData(request);
		List<FundBO> fundsSelected = new ArrayList<FundBO>();
		for (LoanOfferingFundEntity loanOfferingFund : loanOffering
				.getLoanOfferingFunds()) {
			fundsSelected.add(loanOfferingFund.getFund());
		}
		List<FeeView> feeSelected = new ArrayList<FeeView>();
		for (LoanOfferingFeesEntity prdOfferingFees : loanOffering
				.getLoanOfferingFees()) {
			FeeBO fee = prdOfferingFees.getFees();
			fee = new LoanPrdBusinessService().getfee(fee.getFeeId(),fee.getFeeType());
			feeSelected.add(new FeeView(getUserContext(request),fee
					));
		}
		loadSelectedFeesAndFunds(feeSelected, fundsSelected, request);
		loadStatusList(request);
		setDataIntoActionForm(loanOffering, loanPrdActionForm, request);

		prdDefLogger.debug("manage of Loan Product Action called"
				+ prdOfferingId);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start editPreview of Loan Product Action ");
		return mapping.findForward(ActionForwards.editPreview_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward editPrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start editPrevious of Loan Product Action ");
		return mapping.findForward(ActionForwards.editPrevious_success
				.toString());
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward editCancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start cancelCreate method of loan Product Action");
		return mapping
				.findForward(ActionForwards.editcancel_success.toString());
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
		prdDefLogger.debug("start update method of Loan Product Action"
				+ loanPrdActionForm.getPrdOfferingId());
		UserContext userContext = getUserContext(request);
		Locale locale = getLocale(userContext);
		LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct))
				.getLoanOffering(loanPrdActionForm.getPrdOfferingIdValue());
		loanOffering.setUserContext(userContext);
		setInitialObjectForAuditLogging(loanOffering);
		loanOffering.update(userContext.getId(), loanPrdActionForm
				.getPrdOfferingName(), loanPrdActionForm
				.getPrdOfferingShortName(), getProductCategory(
				((List<ProductCategoryBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST,
						request)), loanPrdActionForm.getPrdCategoryValue()),
				(PrdApplicableMasterEntity) findMasterEntity(request,
						ProductDefinitionConstants.LOANAPPLFORLIST,
						loanPrdActionForm.getPrdApplicableMasterEnum().getValue()),
				loanPrdActionForm.getStartDateValue(locale), loanPrdActionForm
						.getEndDateValue(locale), loanPrdActionForm
						.getDescription(), PrdStatus
						.fromInt(loanPrdActionForm.getPrdStatusValue()),
				(GracePeriodTypeEntity) findMasterEntity(request,
						ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
						loanPrdActionForm.getGracePeriodTypeValue()),
				(InterestTypesEntity) findMasterEntity(request,
						ProductDefinitionConstants.INTERESTTYPESLIST,
						loanPrdActionForm.getInterestTypesValue()),
				loanPrdActionForm.getGracePeriodDurationValue(),
				loanPrdActionForm.getMaxLoanAmountValue(), loanPrdActionForm
						.getMinLoanAmountValue(), loanPrdActionForm
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
						(List<FundBO>) SessionUtils.getAttribute(
								ProductDefinitionConstants.SRCFUNDSLIST,
								request), loanPrdActionForm
								.getLoanOfferingFunds()),
				getFeeList((List<FeeBO>) SessionUtils.getAttribute(
						ProductDefinitionConstants.LOANPRDFEE, request),
						loanPrdActionForm.getPrdOfferinFees()),
				loanPrdActionForm.getRecurAfterValue(), RecurrenceType
						.fromInt(loanPrdActionForm
								.getFreqOfInstallmentsValue()));
		prdDefLogger.debug("update method of Loan Product Action called"
				+ loanPrdActionForm.getPrdOfferingId());
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanPrdActionForm loanPrdActionForm = (LoanPrdActionForm) form;
		prdDefLogger.debug("start get method of Loan Product Action"
				+ loanPrdActionForm.getPrdOfferingId());
		LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct)).getLoanOffering(
				loanPrdActionForm.getPrdOfferingIdValue(), getUserContext(
						request).getLocaleId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanOffering,request);
		loanPrdActionForm.clear();
		loanPrdActionForm.setPrdOfferingId(getStringValue(loanOffering
				.getPrdOfferingId()));
		prdDefLogger.debug("get method of Loan Product Action called"
				+ loanOffering.getPrdOfferingId());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward viewAllLoanProducts(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		prdDefLogger
				.debug("start viewAllLoanProducts method of Loan Product Action");
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRODUCTLIST,
				((LoanPrdBusinessService) ServiceFactory.getInstance()
						.getBusinessService(BusinessServiceName.LoanProduct))
						.getAllLoanOfferings(getUserContext(request)
								.getLocaleId()), request);
		return mapping.findForward(ActionForwards.viewAllLoanProducts_success
				.toString());
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		prdDefLogger
				.debug("start Load master data method of Loan Product Action ");
		LoanPrdBusinessService service = new LoanPrdBusinessService();
		FeeBusinessService feeService = new FeeBusinessService();
		FundBusinessService fundService = new FundBusinessService();
		List<FeeBO> fees = feeService.getAllAppllicableFeeForLoanCreation();
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.LOANPRODUCTCATEGORYLIST, service
						.getActiveLoanProductCategories(), request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANAPPLFORLIST,
				service.getLoanApplicableCustomerTypes(localeId), request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.LOANGRACEPERIODTYPELIST,
				getMasterEntities(GracePeriodTypeEntity.class, localeId),
				request);
		SessionUtils
				.setCollectionAttribute(ProductDefinitionConstants.INTERESTTYPESLIST,
						getMasterEntities(InterestTypesEntity.class, localeId),
						request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.INTCALCTYPESLIST,
				getMasterEntities(InterestCalcTypeEntity.class, localeId),
				request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SRCFUNDSLIST,
				fundService.getSourcesOfFund(), request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANFEESLIST,
				getFeeViewList(getUserContext(request), fees), request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.LOANPRICIPALGLCODELIST, getGLCodes(
						FinancialActionConstants.PRINCIPALPOSTING,
						FinancialConstants.CREDIT), request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.LOANINTERESTGLCODELIST, getGLCodes(
						FinancialActionConstants.INTERESTPOSTING,
						FinancialConstants.CREDIT), request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDFEE, fees,
				request);
		prdDefLogger
				.debug("Load master data method of Loan Product Action called");
	}

	private void loadSelectedFeesAndFunds(List<FeeView> feesSelected,
			List<FundBO> fundsSelected, HttpServletRequest request)
			throws Exception {
		prdDefLogger
				.debug("start loadSelectedFeesAndFunds method of Loan Product Action ");
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.LOANPRDFEESELECTEDLIST,
				feesSelected, request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.LOANPRDFUNDSELECTEDLIST,
				fundsSelected, request);
		prdDefLogger
				.debug("loadSelectedFeesAndFunds method of Loan Product Action called ");
	}

	private void loadStatusList(HttpServletRequest request) throws Exception {
		prdDefLogger
				.debug("start Load Status list method of Loan Product Action ");
		LoanPrdBusinessService service = (LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct);
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.LOANPRDSTATUSLIST,
				service.getApplicablePrdStatus(localeId), request);
		prdDefLogger
				.debug("Load Status list method of Loan Product Action called");
	}

	private List<GLCodeEntity> getGLCodes(short financialAction,
			Short debitCredit) throws Exception {
		prdDefLogger.debug("getGLCodes method of Loan Product Action called");
		return new FinancialBusinessService().getGLCodes(financialAction,
				debitCredit);
	}

	private MasterDataEntity findMasterEntity(HttpServletRequest request,
			String collectionName, Short value) throws Exception {
		prdDefLogger
				.debug("start findMasterEntity method of Loan Product Action ");
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
		prdDefLogger
				.debug("start findGLCodeEntity method of Loan Product Action ");
		List<GLCodeEntity> glCodeList = (List<GLCodeEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (GLCodeEntity glCode : glCodeList)
			if (glCode.getGlcodeId().equals(getShortValue(value)))
				return glCode;
		return null;
	}

	private ProductCategoryBO getProductCategory(
			List<ProductCategoryBO> productCategories, Short productCategoryId) {
		prdDefLogger
				.debug("start getProductCategory method of Loan Product Action ");
		for (ProductCategoryBO productCategory : productCategories) {
			if (productCategory.getProductCategoryID()
					.equals(productCategoryId))
				return productCategory;
		}
		return null;
	}

	private List<FundBO> getFundsFromList(List<FundBO> funds,
			String[] fundsSelected) {
		prdDefLogger
				.debug("start getFundsFromList method of Loan Product Action ");
		List<FundBO> fundList = new ArrayList<FundBO>();
		if (fundsSelected != null && fundsSelected.length > 0 && funds != null
				&& funds.size() > 0) {
			for (String fundSelected : fundsSelected) {
				FundBO fund = getFundFromList(funds, fundSelected);
				if (fund != null)
					fundList.add(fund);
			}
		}
		prdDefLogger
				.debug("getFundsFromList method of Loan Product Action called");
		return fundList;
	}

	private FundBO getFundFromList(List<FundBO> funds, String fundSelected) {
		prdDefLogger
				.debug("start getFundFromList method of Loan Product Action ");
		for (FundBO fund : funds)
			if (fund.getFundId().equals(getShortValue(fundSelected)))
				return fund;
		return null;
	}

	private List<FeeView> getFeeViewList(UserContext userContext,
			List<FeeBO> fees) {
		prdDefLogger
				.debug("start getFeeViewList method of Loan Product Action ");
		List<FeeView> feeViews = new ArrayList<FeeView>();
		if (fees != null && fees.size() > 0) {
			for (FeeBO fee : fees) {
				feeViews.add(new FeeView(userContext, fee));
			}
		}
		prdDefLogger
				.debug("getFeeViewList method of Loan Product Action called");
		return feeViews.size()>0?feeViews:null;
	}

	private List<FeeBO> getFeeList(List<FeeBO> fees, String[] feesSelected) {
		prdDefLogger.debug("start getFeeList method of Loan Product Action ");
		List<FeeBO> feeList = new ArrayList<FeeBO>();
		if (feesSelected != null && feesSelected.length > 0 && fees != null
				&& fees.size() > 0) {
			for (String feeSelected : feesSelected) {
				FeeBO fee = getFeeFromList(fees, feeSelected);
				if (fee != null)
					feeList.add(fee);
			}
		}
		prdDefLogger.debug("getFeeList method of Loan Product Action called");
		return feeList;
	}

	private FeeBO getFeeFromList(List<FeeBO> fees, String feeSelected) {
		prdDefLogger
				.debug("start getFeeFromList method of Loan Product Action ");
		for (FeeBO fee : fees)
			if (fee.getFeeId().equals(getShortValue(feeSelected)))
				return fee;
		return null;
	}

	private void setDataIntoActionForm(LoanOfferingBO loanProduct,
			LoanPrdActionForm loanPrdActionForm, HttpServletRequest request)
			throws Exception {
		prdDefLogger
				.debug("start setDataIntoActionForm method of Loan Product Action ");
		loanPrdActionForm.setPrdOfferingId(getStringValue(loanProduct
				.getPrdOfferingId()));
		loanPrdActionForm.setPrdOfferingName(loanProduct.getPrdOfferingName());
		loanPrdActionForm.setPrdOfferingShortName(loanProduct
				.getPrdOfferingShortName());
		loanPrdActionForm.setPrdCategory(getStringValue(loanProduct
				.getPrdCategory().getProductCategoryID()));
		loanPrdActionForm.setPrdStatus(getStringValue(loanProduct
				.getStatus().getValue()));
		loanPrdActionForm.setPrdApplicableMaster(
				loanProduct.getPrdApplicableMasterEnum());
		loanPrdActionForm.setStartDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), DateUtils.toDatabaseFormat(loanProduct.getStartDate())));
		loanPrdActionForm
				.setEndDate(loanProduct.getEndDate() != null ? DateUtils.getUserLocaleDate(getUserContext(request)
				.getPreferredLocale(), DateUtils.toDatabaseFormat(loanProduct.getEndDate()))
						: null);
		loanPrdActionForm.setDescription(loanProduct.getDescription());
		loanPrdActionForm.setGracePeriodType(getStringValue(loanProduct
				.getGracePeriodType().getId()));
		loanPrdActionForm.setGracePeriodDuration(getStringValue(loanProduct
				.getGracePeriodDuration()));
		loanPrdActionForm.setInterestTypes(getStringValue(loanProduct
				.getInterestTypes().getId()));
		loanPrdActionForm.setMaxLoanAmount(getStringValue(loanProduct
				.getMaxLoanAmount()));
		loanPrdActionForm.setMinLoanAmount(getStringValue(loanProduct
				.getMinLoanAmount()));
		if (!(loanProduct.getDefaultLoanAmount().getAmountDoubleValue() == 0.0 && loanProduct
				.getMinLoanAmount().getAmountDoubleValue() != 0.0))
			loanPrdActionForm.setDefaultLoanAmount(getStringValue(loanProduct
					.getDefaultLoanAmount()));
		loanPrdActionForm.setMaxInterestRate(BigDecimal.valueOf(loanProduct.getMaxInterestRate()).toString());
		loanPrdActionForm.setMinInterestRate(BigDecimal.valueOf(loanProduct.getMinInterestRate()).toString());
		loanPrdActionForm.setDefInterestRate(BigDecimal.valueOf(loanProduct.getDefInterestRate()).toString());
		loanPrdActionForm.setMaxNoInstallments(getStringValue(loanProduct
				.getMaxNoInstallments()));
		loanPrdActionForm.setMinNoInstallments(getStringValue(loanProduct
				.getMinNoInstallments()));
		loanPrdActionForm.setDefNoInstallments(getStringValue(loanProduct
				.getDefNoInstallments()));

		loanPrdActionForm.setIntDedDisbursementFlag(getStringValue(loanProduct
				.isIntDedDisbursement()));
		loanPrdActionForm.setPrinDueLastInstFlag(getStringValue(loanProduct
				.isPrinDueLastInst()));
		loanPrdActionForm.setLoanCounter(getStringValue(loanProduct
				.isIncludeInLoanCounter()));
		loanPrdActionForm.setRecurAfter(getStringValue(loanProduct
				.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurAfter()));
		loanPrdActionForm.setFreqOfInstallments(getStringValue(loanProduct
				.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId()));
		loanPrdActionForm.setPrincipalGLCode(getStringValue(loanProduct
				.getPrincipalGLcode().getGlcodeId()));
		loanPrdActionForm.setInterestGLCode(getStringValue(loanProduct
				.getInterestGLcode().getGlcodeId()));
		SessionUtils.setAttribute(ProductDefinitionConstants.LOANPRDSTARTDATE,
				loanProduct.getStartDate(), request);
		prdDefLogger
				.debug("setDataIntoActionForm method of Loan Product Action called");
	}
}

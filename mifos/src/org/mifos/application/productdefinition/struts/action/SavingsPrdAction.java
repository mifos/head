package org.mifos.application.productdefinition.struts.action;

import java.util.ArrayList;
import java.util.Date;
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
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.productdefinition.struts.actionforms.SavingsPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class SavingsPrdAction extends BaseAction {

	private MifosLogger prdDefLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	@Override
	protected BusinessService getService() {
		return new SavingsPrdBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start Load method of Savings Product Action");
		request.getSession().setAttribute(
				ProductDefinitionConstants.SAVINGSPRODUCTACTIONFORM, null);
		loadMasterData(request);
		prdDefLogger.debug("Load method of Savings Product Action called");
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start validate method of Savings Product Action");
		String forward = null;
		String method = (String) request.getAttribute("methodCalled");
		if (method != null)
			forward = method + "_failure";
		return mapping.findForward(forward);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start preview method of Savings Product Action");
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start previous method of Savings Product Action");
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start create method of Savings Product Action");
		SavingsPrdActionForm savingsprdForm = (SavingsPrdActionForm) form;
		UserContext userContext = getUserContext(request);
		Locale locale = getLocale(userContext);
		MasterDataEntity recommendedAmountUnit = findMasterEntity(request,
				ProductDefinitionConstants.RECAMNTUNITLIST, savingsprdForm
						.getRecommendedAmntUnitValue());
		SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
				userContext,
				savingsprdForm.getPrdOfferingName(),
				savingsprdForm.getPrdOfferingShortName(),
				getProductCategory(
						((List<ProductCategoryBO>) SessionUtils
								.getAttribute(
										ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST,
										request)), savingsprdForm
								.getPrdCategoryValue()),
				(PrdApplicableMasterEntity) findMasterEntity(request,
						ProductDefinitionConstants.SAVINGSAPPLFORLIST,
						savingsprdForm.getPrdApplicableMasterValue()),
				savingsprdForm.getStartDateValue(locale), savingsprdForm
						.getEndDateValue(locale), savingsprdForm
						.getDescription(), recommendedAmountUnit == null ? null
						: (RecommendedAmntUnitEntity) recommendedAmountUnit,
				(SavingsTypeEntity) findMasterEntity(request,
						ProductDefinitionConstants.SAVINGSTYPELIST,
						savingsprdForm.getSavingsTypeValue().getValue()),
				(InterestCalcTypeEntity) findMasterEntity(request,
						ProductDefinitionConstants.INTCALCTYPESLIST,
						savingsprdForm.getInterestCalcTypeValue()),
				new MeetingBO(RecurrenceType.fromInt(savingsprdForm
						.getRecurTypeFortimeForInterestCaclValue()),
						savingsprdForm.getTimeForInterestCalcValue(),
						new Date(), MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD),
				new MeetingBO(RecurrenceType.MONTHLY, savingsprdForm
						.getFreqOfInterestValue(), new Date(),
						MeetingType.SAVINGS_INTEREST_POSTING), savingsprdForm
						.getRecommendedAmountValue(), savingsprdForm
						.getMaxAmntWithdrawlValue(), savingsprdForm
						.getMinAmntForIntValue(), savingsprdForm
						.getInterestRateValue(), findGLCodeEntity(request,
						ProductDefinitionConstants.SAVINGSDEPOSITGLCODELIST,
						savingsprdForm.getDepositGLCodeValue()),
				findGLCodeEntity(request,
						ProductDefinitionConstants.SAVINGSINTERESTGLCODELIST,
						savingsprdForm.getInterestGLCodeValue()));
		savingsOffering.save();
		request.setAttribute(ProductDefinitionConstants.SAVINGSPRODUCTID,
				savingsOffering.getPrdOfferingId());
		request.setAttribute(
				ProductDefinitionConstants.SAVINGSPRDGLOBALOFFERINGNUM,
				savingsOffering.getGlobalPrdOfferingNum());

		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger
				.debug("start cancelCreate method of Savings Product Action");
		return mapping.findForward(ActionForwards.cancelCreate_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancelEdit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger
				.debug("start cancelCreate method of Savings Product Action");
		return mapping
				.findForward(ActionForwards.cancelEdit_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start get method of Savings Product Action");
		SavingsPrdActionForm savingsprdForm = (SavingsPrdActionForm) form;
		SavingsOfferingBO savingsOffering = ((SavingsPrdBusinessService) getService())
				.getSavingsProduct(getShortValue(savingsprdForm
						.getPrdOfferingId()));
		savingsOffering.getPrdStatus().getPrdState().setLocaleId(
				getUserContext(request).getLocaleId());

		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsOffering,
				request);
		loadMasterData(request);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsPrdActionForm actionform = (SavingsPrdActionForm) form;
		actionform.clear();
		SavingsOfferingBO savingsOff = (SavingsOfferingBO) SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request);
		SavingsOfferingBO savingsOffering = ((SavingsPrdBusinessService) getService())
				.getSavingsProduct(savingsOff.getPrdOfferingId());
		savingsOff = null;
		savingsOffering.getPrdStatus().getPrdState().setLocaleId(
				getUserContext(request).getLocaleId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsOffering,
				request);
		loadUpdateMasterData(request);
		setValuesInActionForm(actionform, request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previewManage_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previousManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previousManage_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start create method of Savings Product Action");
		SavingsPrdActionForm savingsprdForm = (SavingsPrdActionForm) form;
		UserContext userContext = getUserContext(request);
		Locale locale = getLocale(userContext);
		MasterDataEntity recommendedAmountUnit = findMasterEntity(request,
				ProductDefinitionConstants.RECAMNTUNITLIST, savingsprdForm
						.getRecommendedAmntUnitValue());
		SavingsOfferingBO savingsOffering = (SavingsOfferingBO) SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request);
		SavingsOfferingBO savingsOfferingBO = ((SavingsPrdBusinessService) getService())
		.getSavingsProduct(savingsOffering.getPrdOfferingId());
		savingsOfferingBO.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(savingsOfferingBO);
		savingsOfferingBO
				.update(
						userContext.getId(),
						savingsprdForm.getPrdOfferingName(),
						savingsprdForm.getPrdOfferingShortName(),
						getProductCategory(
								((List<ProductCategoryBO>) SessionUtils
										.getAttribute(
												ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST,
												request)), savingsprdForm
										.getPrdCategoryValue()),
						(PrdApplicableMasterEntity) findMasterEntity(request,
								ProductDefinitionConstants.SAVINGSAPPLFORLIST,
								savingsprdForm.getPrdApplicableMasterValue()),
						savingsprdForm.getStartDateValue(locale),
						savingsprdForm.getEndDateValue(locale),
						savingsprdForm.getDescription(),
						PrdStatus.getPrdStatus(savingsprdForm.getStatusValue()),
						recommendedAmountUnit == null ? null
								: (RecommendedAmntUnitEntity) recommendedAmountUnit,
						(SavingsTypeEntity) findMasterEntity(request,
								ProductDefinitionConstants.SAVINGSTYPELIST,
								savingsprdForm.getSavingsTypeValue().getValue()),
						(InterestCalcTypeEntity) findMasterEntity(request,
								ProductDefinitionConstants.INTCALCTYPESLIST,
								savingsprdForm.getInterestCalcTypeValue()),
						new MeetingBO(
								RecurrenceType
										.fromInt(savingsprdForm
												.getRecurTypeFortimeForInterestCaclValue()),
								savingsprdForm.getTimeForInterestCalcValue(),
								new Date(),
								MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD),
						new MeetingBO(RecurrenceType.MONTHLY, savingsprdForm
								.getFreqOfInterestValue(), new Date(),
								MeetingType.SAVINGS_INTEREST_POSTING),
						savingsprdForm.getRecommendedAmountValue(),
						savingsprdForm.getMaxAmntWithdrawlValue(),
						savingsprdForm.getMinAmntForIntValue(), savingsprdForm
								.getInterestRateValue());
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SavingsPrdBusinessService service = new SavingsPrdBusinessService();
		prdDefLogger.debug("start Load method of Savings Product Action");
		List <SavingsOfferingBO>savingsOfferingList = service.getAllSavingsProducts();
		for(SavingsOfferingBO savingsOffering : savingsOfferingList ){
			savingsOffering.getPrdStatus().getPrdState().setLocaleId(
					getUserContext(request).getLocaleId());
		}
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.SAVINGSPRODUCTLIST, service
						.getAllSavingsProducts(), request);
		prdDefLogger.debug("Load method of Savings Product Action called");
		return mapping.findForward(ActionForwards.search_success.toString());
	}

	private void setValuesInActionForm(SavingsPrdActionForm actionForm,
			HttpServletRequest request) throws Exception {
		SavingsOfferingBO savingsOffering = (SavingsOfferingBO) SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request);
		actionForm.setPrdOfferingId(savingsOffering.getPrdOfferingId()
				.toString());
		actionForm.setPrdOfferingName(savingsOffering.getPrdOfferingName());
		actionForm.setPrdOfferingShortName(savingsOffering
				.getPrdOfferingShortName());
		actionForm.setDescription(savingsOffering.getDescription());
		if (savingsOffering.getPrdCategory() != null)
			actionForm.setPrdCategory(savingsOffering.getPrdCategory()
					.getProductCategoryID().toString());
		if (savingsOffering.getStartDate() != null)
			actionForm.setStartDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), savingsOffering.getStartDate().toString()));
		if (savingsOffering.getEndDate() != null)
			actionForm.setEndDate(DateUtils.getUserLocaleDate(getUserContext(
			request).getPreferredLocale(), savingsOffering.getEndDate()
			.toString()));
		if (savingsOffering.getPrdApplicableMaster() != null)
			actionForm.setPrdApplicableMaster(savingsOffering
					.getPrdApplicableMasterEnum().getValue().toString());
		if (savingsOffering.getSavingsType() != null)
			actionForm.setSavingsType(savingsOffering.getSavingsType().getId()
					.toString());
		if (savingsOffering.getRecommendedAmount() != null)
			actionForm.setRecommendedAmount(savingsOffering
					.getRecommendedAmount().toString());
		if (savingsOffering.getRecommendedAmntUnit() != null)
			actionForm.setRecommendedAmntUnit(savingsOffering
					.getRecommendedAmntUnit().getId().toString());
		if (savingsOffering.getMaxAmntWithdrawl() != null)
			actionForm.setMaxAmntWithdrawl(savingsOffering
					.getMaxAmntWithdrawl().toString());
		if (savingsOffering.getPrdStatus() != null)
			actionForm.setStatus(savingsOffering.getPrdStatus()
					.getOfferingStatusId().toString());
		if (savingsOffering.getInterestRate() != null)
			actionForm.setInterestRate(savingsOffering.getInterestRate()
					.toString());
		if (savingsOffering.getInterestCalcType() != null)
			actionForm.setInterestCalcType(savingsOffering
					.getInterestCalcType().getId().toString());
		if (savingsOffering.getTimePerForInstcalc() != null
				&& savingsOffering.getTimePerForInstcalc().getMeeting() != null
				&& savingsOffering.getTimePerForInstcalc().getMeeting()
						.getMeetingDetails() != null) {
			actionForm.setTimeForInterestCacl(savingsOffering
					.getTimePerForInstcalc().getMeeting().getMeetingDetails()
					.getRecurAfter().toString());
			actionForm.setRecurTypeFortimeForInterestCacl(savingsOffering
					.getTimePerForInstcalc().getMeeting().getMeetingDetails()
					.getRecurrenceType().getRecurrenceId().toString());
		}
		if (savingsOffering.getFreqOfPostIntcalc() != null
				&& savingsOffering.getFreqOfPostIntcalc().getMeeting() != null
				&& savingsOffering.getFreqOfPostIntcalc().getMeeting()
						.getMeetingDetails() != null) {
			actionForm.setFreqOfInterest(savingsOffering.getFreqOfPostIntcalc()
					.getMeeting().getMeetingDetails().getRecurAfter()
					.toString());
		}
		if (savingsOffering.getMinAmntForInt() != null)
			actionForm.setMinAmntForInt(savingsOffering.getMinAmntForInt()
					.toString());
		if (savingsOffering.getDepositGLCode() != null)
			actionForm.setDepositGLCode(savingsOffering.getDepositGLCode()
					.getGlcodeId().toString());
		if (savingsOffering.getInterestGLCode() != null)
			actionForm.setInterestGLCode(savingsOffering.getInterestGLCode()
					.getGlcodeId().toString());
	}

	private void loadUpdateMasterData(HttpServletRequest request)
			throws Exception {
		loadMasterData(request);
		SavingsPrdBusinessService service = new SavingsPrdBusinessService();
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.PRDCATEGORYSTATUSLIST, service
						.getApplicablePrdStatus(localeId), request);
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		prdDefLogger
				.debug("start Load master data method of Savings Product Action ");
		SavingsPrdBusinessService service = new SavingsPrdBusinessService();
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST, service
						.getActiveSavingsProductCategories(), request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.SAVINGSAPPLFORLIST,
				getMasterEntities(PrdApplicableMasterEntity.class, localeId),
				request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SAVINGSTYPELIST,
				getMasterEntities(SavingsTypeEntity.class, localeId), request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.RECAMNTUNITLIST,
				getMasterEntities(RecommendedAmntUnitEntity.class, localeId),
				request);
		SessionUtils.setCollectionAttribute(ProductDefinitionConstants.INTCALCTYPESLIST,
				getMasterEntities(InterestCalcTypeEntity.class, localeId),
				request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.SAVINGSRECURRENCETYPELIST, service
						.getSavingsApplicableRecurrenceTypes(), request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.SAVINGSDEPOSITGLCODELIST,
				getGLCodesForDeposit(), request);
		SessionUtils.setCollectionAttribute(
				ProductDefinitionConstants.SAVINGSINTERESTGLCODELIST,
				getGLCodes(FinancialActionConstants.SAVINGS_INTERESTPOSTING,
						FinancialConstants.DEBIT), request);
		prdDefLogger
				.debug("Load master data method of Savings Product Action called");
	}

	private List<GLCodeEntity> getGLCodes(short financialAction,
			Short debitCredit) throws Exception {
		prdDefLogger
				.debug("getGLCodes method of Savings Product Action called");
		return new FinancialBusinessService().getGLCodes(financialAction,
				debitCredit);
	}

	private List<GLCodeEntity> getGLCodesForDeposit() throws Exception {
		prdDefLogger
				.debug("getGLCodes for deposit method of Savings Product Action called");
		List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
		glCodeList.addAll(getGLCodes(FinancialActionConstants.MANDATORYDEPOSIT,
				FinancialConstants.CREDIT));
		glCodeList.addAll(getGLCodes(FinancialActionConstants.VOLUNTORYDEPOSIT,
				FinancialConstants.CREDIT));
		return glCodeList;
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
			String collectionName, Short value) throws PageExpiredException {
		List<GLCodeEntity> glCodeList = (List<GLCodeEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (GLCodeEntity glCode : glCodeList)
			if (glCode.getGlcodeId().equals(value))
				return glCode;
		return null;
	}

}

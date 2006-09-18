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
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.business.SavingsTypeEntity;
import org.mifos.application.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.application.productdefinition.dao.SavingsProductDAO;
import org.mifos.application.productdefinition.struts.actionforms.SavingsPrdActionForm;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.SavingsOffering;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;

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
		String method = (String) request
				.getAttribute(ProductDefinitionConstants.METHODCALLED);
		if (method != null) {
			if (method.equals(Methods.preview.toString())) {
				return mapping.findForward(ActionForwards.preview_failure
						.toString());
			} else if (method.equals(Methods.create.toString())) {
				return mapping.findForward(ActionForwards.create_failure
						.toString());
			}
		}
		prdDefLogger.debug("preview validate of Savings Product Action called");
		return mapping.findForward(ActionForwards.preview_success.toString());
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
				new MeetingBO(RecurrenceType
						.getRecurrenceType(savingsprdForm
								.getRecurTypeFortimeForInterestCaclValue()),
						savingsprdForm.getTimeForInterestCalcValue(),
						new Date(), MeetingType.SAVINGSTIMEPERFORINTCALC), new MeetingBO(
						RecurrenceType.MONTHLY, savingsprdForm
								.getFreqOfInterestValue(), new Date(),
						MeetingType.SAVINGSFRQINTPOSTACC), savingsprdForm
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
		request.setAttribute(ProductDefinitionConstants.SAVINGSPRDGLOBALOFFERINGNUM,
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

@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		prdDefLogger.debug("start get method of Savings Product Action");
		UserContext userContext = getUserContext(request);
		Locale locale = getLocale(userContext);
		SavingsPrdActionForm savingsprdForm = (SavingsPrdActionForm) form;
		//TODO: remove this piece of code once edit has been migrated
		conversionForMI(savingsprdForm , request);
			
		
		SavingsOfferingBO savingsOffering = ((SavingsPrdBusinessService)getService()).getSavingsProduct(getShortValue(savingsprdForm.getPrdOfferingId()));
		savingsOffering.getPrdStatus().getPrdState().setLocaleId(getUserContext(request).getLocaleId());
		
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, savingsOffering, request);
		loadMasterData(request);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

		//TODO: remove this piece of code once edit has been migrated		
		private void conversionForMI(SavingsPrdActionForm savingsprdForm , HttpServletRequest request) throws Exception {
			Context context = new Context();
			SavingsOffering oldSavingsOffering = new SavingsProductDAO().get(getShortValue(savingsprdForm.getPrdOfferingId()));
			context.setValueObject(oldSavingsOffering);
			context.setPath(ProductDefinitionConstants.GETPATHSAVINGSPRODUCT);
			context.setUserContext(getUserContext(request));
			SessionUtils.setContext(ProductDefinitionConstants.GETPATHSAVINGSPRODUCT ,context,request.getSession() );
					
		}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		prdDefLogger
				.debug("start Load master data method of Savings Product Action ");
		SavingsPrdBusinessService service = (SavingsPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.SavingsProduct);
		Short locaeId = getUserContext(request).getLocaleId();
		SessionUtils.setAttribute(
				ProductDefinitionConstants.SAVINGSPRODUCTCATEGORYLIST, service
						.getActiveSavingsProductCategories(), request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.SAVINGSAPPLFORLIST,
				getMasterEntities(PrdApplicableMasterEntity.class, locaeId),
				request);
		SessionUtils.setAttribute(ProductDefinitionConstants.SAVINGSTYPELIST,
				getMasterEntities(SavingsTypeEntity.class, locaeId), request);
		SessionUtils.setAttribute(ProductDefinitionConstants.RECAMNTUNITLIST,
				getMasterEntities(RecommendedAmntUnitEntity.class, locaeId),
				request);
		SessionUtils.setAttribute(ProductDefinitionConstants.INTCALCTYPESLIST,
				getMasterEntities(InterestCalcTypeEntity.class, locaeId),
				request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.SAVINGSRECURRENCETYPELIST, service
						.getSavingsApplicableRecurrenceTypes(), request);
		SessionUtils.setAttribute(
				ProductDefinitionConstants.SAVINGSDEPOSITGLCODELIST,
				getGLCodesForDeposit(), request);
		SessionUtils.setAttribute(
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

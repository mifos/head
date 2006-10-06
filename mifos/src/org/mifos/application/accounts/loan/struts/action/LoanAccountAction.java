package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.ViewInstallmentDetails;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class LoanAccountAction extends AccountAppAction {

	private LoanBusinessService loanBusinessService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public LoanAccountAction() throws Exception {
		loanBusinessService = (LoanBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Loan);
	}

	@Override
	protected BusinessService getService() {
		return loanBusinessService;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getInstallmentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Integer accountId = Integer.valueOf(request.getParameter("accountId"));
		LoanBO loanBO = loanBusinessService.getAccount(accountId);
		ViewInstallmentDetails viewUpcomingInstallmentDetails = getUpcomingInstallmentDetails(loanBO
				.getDetailsOfNextInstallment());
		ViewInstallmentDetails viewOverDueInstallmentDetails = getOverDueInstallmentDetails(loanBO
				.getDetailsOfInstallmentsInArrears());
		Money totalAmountDue = viewUpcomingInstallmentDetails.getSubTotal()
				.add(viewOverDueInstallmentDetails.getSubTotal());
		SessionUtils.setAttribute(
				LoanConstants.VIEW_UPCOMING_INSTALLMENT_DETAILS,
				viewUpcomingInstallmentDetails, request);
		SessionUtils.setAttribute(
				LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS,
				viewOverDueInstallmentDetails, request);
		SessionUtils.setAttribute(LoanConstants.TOTAL_AMOUNT_OVERDUE,
				totalAmountDue, request);

		SessionUtils.setAttribute(LoanConstants.NEXTMEETING_DATE,
				loanBO.getNextMeetingDate(), request);
		loanBO=null;
		return mapping
				.findForward(LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In loanAccountAction::getAllActivity()");
		String globalAccountNum = request.getParameter("globalAccountNum");
		SessionUtils.setAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW,
				loanBusinessService.getAllActivityView(globalAccountNum,
						getUserContext(request).getLocaleId()), request);
		return mapping.findForward(MethodNameConstants.GETALLACTIVITY_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward forwardWaiveCharge(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward("waive" + request.getParameter("type")
				+ "Charges_Success");
	}
	@TransactionDemarcate(saveToken=true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");

		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		Hibernate.initialize(loanBO.getLoanMeeting());
		for (AccountActionDateEntity accountActionDateEntity : loanBO
				.getAccountActionDates()) {
			Hibernate.initialize(accountActionDateEntity);
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity) accountActionDateEntity)
					.getAccountFeesActionDetails()) {
				Hibernate.initialize(accountFeesActionDetailEntity);
			}
		}
		setLocaleForMasterEntities(loanBO, getUserContext(request)
				.getLocaleId());
		loadLoanDetailPageInfo(loanBO, request);
		loadMasterData(request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getLoanRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.getLoanRepaymentSchedule
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward viewStatusHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		Hibernate.initialize(loanBO.getAccountStatusChangeHistory());
		loanBO.setUserContext(getUserContext(request));
		List<AccountStatusChangeHistoryEntity> accStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>(
				loanBO.getAccountStatusChangeHistory());
		SessionUtils.setAttribute(LoanConstants.STATUS_HISTORY,
				accStatusChangeHistory, request.getSession());
		loanBO = null;
		return mapping.findForward(ActionForwards.viewStatusHistory.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForwards actionForward = null;
		String method = (String) request.getAttribute("methodCalled");
		if (method.equals(Methods.getPrdOfferings.toString())
				|| method.equals(Methods.load.toString()))
			actionForward = ActionForwards.getPrdOfferigs_success;
		else if (method.equals(Methods.schedulePreview.toString()))
			actionForward = ActionForwards.load_success;
		else if (method.equals(Methods.managePreview.toString()))
			actionForward = ActionForwards.managepreview_failure;

		return mapping.findForward(actionForward.toString());
	}
	
	@TransactionDemarcate(saveToken=true)
	public ActionForward getPrdOfferings(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("Inside getPrdOfferings method");

		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		CustomerBO customer = getCustomer(loanActionForm.getCustomerIdValue());

		doCleanUp(request.getSession());
		List<LoanOfferingBO> loanOfferings = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct))
				.getApplicablePrdOfferings(customer.getCustomerLevel());
		removePrdOfferingsNotMachingCustomerMeeting(loanOfferings, customer);
		SessionUtils.setAttribute(LoanConstants.LOANPRDOFFERINGS,
				loanOfferings, request);
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, customer,
				request);
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, customer
				.getCustomerAccount().getNextMeetingDate(), request);
		return mapping.findForward(ActionForwards.getPrdOfferigs_success
				.toString());
	}

	@TransactionDemarcate(joinToken=true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		loadFees(loanActionForm, loanActionForm
				.getPrdOfferingIdValue(), request);
		LoanOfferingBO loanOffering = getLoanOffering(loanActionForm
				.getPrdOfferingIdValue(), getUserContext(request).getLocaleId());
		setDataIntoForm(loanOffering, loanActionForm, request);
		loadMasterData(request);
		SessionUtils.removeAttribute(LoanConstants.LOANOFFERING,request);

		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				getFunds(loanOffering), request);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	@TransactionDemarcate(joinToken=true)
	public ActionForward schedulePreview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct)).getLoanOffering(((LoanOfferingBO) SessionUtils
								.getAttribute(LoanConstants.LOANOFFERING,request))
						.getPrdOfferingId(), getUserContext(request)
						.getLocaleId());

		CustomerBO oldCustomer = (CustomerBO) SessionUtils.getAttribute(LoanConstants.LOANACCOUNTOWNER,request);
		CustomerBO customer = getCustomer(oldCustomer.getCustomerId());
		customer.setVersionNo(oldCustomer.getVersionNo());
		oldCustomer = null;
		LoanBO loan = null;
		loan = new LoanBO(getUserContext(request), loanOffering, customer,
					AccountState.LOANACC_PARTIALAPPLICATION, loanActionForm
							.loanAmountValue(), loanActionForm
							.getNoOfInstallmentsValue(), loanActionForm
							.getDisbursementDateValue(getUserContext(request)
									.getPereferedLocale()), loanActionForm
							.isInterestDedAtDisbValue(), loanActionForm
							.getInterestDoubleValue(), loanActionForm
							.getGracePeriodDurationValue(), getFund(request,
							loanActionForm.getLoanOfferingFundValue()),
					loanActionForm.getFeesToApply());
		loan.setBusinessActivityId(loanActionForm.getBusinessActivityIdValue());
		loan.setCollateralNote(loanActionForm.getCollateralNote());
		CollateralTypeEntity collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
				request, MasterConstants.COLLATERAL_TYPES,
				loanActionForm.getCollateralTypeIdValue());
		loan.setCollateralType(collateralTypeEntity);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		SessionUtils.setAttribute(LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS,
				getLoanSchedule(loan), request.getSession());
		boolean isPendingApprovalDefined = Configuration.getInstance()
				.getAccountConfig(getUserContext(request).getBranchId())
				.isPendingApprovalStateDefinedForLoan();
		SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
				isPendingApprovalDefined, request);
		return mapping.findForward(ActionForwards.schedulePreview_success
				.toString());
	}

	@TransactionDemarcate(joinToken=true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken=true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		loan.setAccountState(new AccountStateEntity(loanActionForm.getState()));
		loan.save();
		CustomerBO customer = (CustomerBO) SessionUtils.getAttribute(LoanConstants.LOANACCOUNTOWNER,request);
		loanActionForm.setAccountId(loan.getAccountId().toString());
		request.setAttribute("customer", customer);
		request.setAttribute("globalAccountNum", loan.getGlobalAccountNum());
		request.setAttribute("loan", loan);
		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	@TransactionDemarcate(joinToken=true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBO = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, loanBO
				.getDisbursementDate(), request);
		SessionUtils.removeAttribute(LoanConstants.LOANOFFERING,request);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, getLoanOffering(loanBO.getLoanOffering().getPrdOfferingId(), getUserContext(request).getLocaleId()),
				request);
		setFormAttributes(loanBO, form, request);
		loadMasterData(request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken=true)
	public ActionForward managePrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.manageprevious_success
				.toString());
	}

	@TransactionDemarcate(joinToken=true)
	public ActionForward managePreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;

		SessionUtils.removeAttribute(MasterConstants.COLLATERAL_TYPE_NAME,request);
		SessionUtils.removeAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME,request);
		if (loanAccountActionForm.getCollateralTypeIdValue() != null) {
			CollateralTypeEntity collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
					request, MasterConstants.COLLATERAL_TYPES,
					loanAccountActionForm.getCollateralTypeIdValue());
			collateralTypeEntity.setLocaleId(getUserContext(request)
					.getLocaleId());
			SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPE_NAME,
					collateralTypeEntity.getName(), request);
		}
		if (loanAccountActionForm.getBusinessActivityIdValue() != null) {
			SessionUtils.setAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME,
					getNameForBusinessActivityEntity(loanAccountActionForm.getBusinessActivityIdValue(), getUserContext(request).getLocaleId()), request);

		}
		return mapping.findForward(ActionForwards.managepreview_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loan_detail_page.toString());
	}

	@TransactionDemarcate(validateAndResetToken=true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.findBySystemId(loanBOInSession
				.getGlobalAccountNum());
		loanBO.setVersionNo(loanBOInSession.getVersionNo());
		loanBO.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(loanBO);
		updateBusinessData(loanBO, form, request);
		loanBO.updateLoan();
		loanBOInSession = null;

		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO,request);
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private void updateBusinessData(LoanBO loanBO, ActionForm form,
			HttpServletRequest request) throws Exception {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		if (loanAccountActionForm.getIntDedDisbursement().equals("1")) {
			try {
				loanBO
						.setGracePeriodType((GracePeriodTypeEntity) getMasterEntities(
								GraceTypeConstants.NONE.getValue(),
								GracePeriodTypeEntity.class, getUserContext(
										request).getLocaleId()));
			} catch (ServiceException e) {
				throw new AccountException(e);
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}

		} else {
			loanBO.setGracePeriodType(loanBO.getLoanOffering()
					.getGracePeriodType());
		}
		loanBO.setLoanAmount(loanAccountActionForm.getLoanAmountValue());
		loanBO.setInterestRate(loanAccountActionForm.getInterestRateValue());
		loanBO.setNoOfInstallments(loanAccountActionForm
				.getNoOfInstallmentsValue());
		loanBO.setDisbursementDate(loanAccountActionForm
				.getDisbursementDateValue(getUserContext(request)
						.getPereferedLocale()));
		loanBO.setGracePeriodDuration(loanAccountActionForm
				.getGracePeriodDurationValue());
		loanBO.setInterestDeductedAtDisbursement(loanAccountActionForm
				.getIntDedDisbursement().equals("1") ? true : false);
		loanBO.setBusinessActivityId(loanAccountActionForm
				.getBusinessActivityIdValue());
		loanBO.setCollateralNote(loanAccountActionForm.getCollateralNote());
		CollateralTypeEntity collateralTypeEntity = null;
		if (loanAccountActionForm.getCollateralTypeIdValue() != null) {
			collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
					request, MasterConstants.COLLATERAL_TYPES,
					loanAccountActionForm.getCollateralTypeIdValue());
			collateralTypeEntity.setLocaleId(getUserContext(request)
					.getLocaleId());
		}
		loanBO.setCollateralType(collateralTypeEntity);
	}

	private void setLocaleForMasterEntities(LoanBO loanBO, Short localeId) {
		if (loanBO.getGracePeriodType() != null)
			loanBO.getGracePeriodType().setLocaleId(localeId);
		if (loanBO.getCollateralType() != null)
			loanBO.getCollateralType().setLocaleId(localeId);
		loanBO.getInterestType().setLocaleId(localeId);
		loanBO.getAccountState().setLocaleId(localeId);
		for (AccountFlagMapping accountFlagMapping : loanBO.getAccountFlags()) {
			accountFlagMapping.getFlag().setLocaleId(localeId);
		}
	}

	private void loadLoanDetailPageInfo(LoanBO loanBO,
			HttpServletRequest request) throws Exception {
		SessionUtils.setAttribute(LoanConstants.RECENTACCOUNTACTIVITIES,
				loanBusinessService.getRecentActivityView(loanBO
						.getGlobalAccountNum(), getUserContext(request)
						.getLocaleId()), request.getSession());
		SessionUtils
				.setAttribute(AccountConstants.LAST_PAYMENT_ACTION,
						loanBusinessService.getLastPaymentAction(loanBO
								.getAccountId()), request);
		SessionUtils.setAttribute(LoanConstants.NOTES, loanBO
				.getRecentAccountNotes(), request);
	}

	private void removePrdOfferingsNotMachingCustomerMeeting(
			List<LoanOfferingBO> loanOfferings, CustomerBO customer) {
		MeetingBO customerMeeting = customer.getCustomerMeeting().getMeeting();
		for (Iterator<LoanOfferingBO> iter = loanOfferings.iterator(); iter
				.hasNext();) {
			LoanOfferingBO loanOffering = iter.next();
			if (!isMeetingMatched(customerMeeting, loanOffering
					.getLoanOfferingMeeting().getMeeting()))
				iter.remove();
		}
	}

	private boolean isMeetingMatched(MeetingBO customerMeeting,
			MeetingBO loanOfferingMeeting) {
		return customerMeeting != null
				&& loanOfferingMeeting != null
				&& customerMeeting.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId().equals(
								loanOfferingMeeting.getMeetingDetails()
										.getRecurrenceType().getRecurrenceId())
				&& isMultiple(loanOfferingMeeting.getMeetingDetails()
						.getRecurAfter(), customerMeeting.getMeetingDetails()
						.getRecurAfter());
	}

	private boolean isMultiple(Short valueToBeChecked,
			Short valueToBeCheckedWith) {
		return valueToBeChecked % valueToBeCheckedWith == 0;
	}

	private void doCleanUp(HttpSession session) {
		session.setAttribute("loanAccountActionForm", null);
	}

	private LoanOfferingBO getLoanOffering(Short loanOfferingId, short localeId)
			throws Exception {
		return ((LoanPrdBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.LoanProduct))
				.getLoanOffering(loanOfferingId, localeId);
	}

	private void setDataIntoForm(LoanOfferingBO loanOffering,
			LoanAccountActionForm loanAccountActionForm,
			HttpServletRequest request)throws Exception {
		loanAccountActionForm.setLoanAmount(getStringValue(loanOffering
				.getDefaultLoanAmount()));
		loanAccountActionForm.setInterestRate(getStringValue(loanOffering
				.getDefInterestRate()));
		loanAccountActionForm.setNoOfInstallments(getStringValue(loanOffering
				.getDefNoInstallments()));
		loanAccountActionForm.setIntDedDisbursement(getStringValue(loanOffering
				.isIntDedDisbursement()));
		loanAccountActionForm
				.setGracePeriodDuration(getStringValue(loanOffering
						.getGracePeriodDuration()));
		loanAccountActionForm.setDisbursementDate(DateHelper.getUserLocaleDate(
				getUserContext(request).getPereferedLocale(), SessionUtils.getAttribute(
								LoanConstants.PROPOSEDDISBDATE,request).toString()));
	}

	private List<FundBO> getFunds(LoanOfferingBO loanOffering) {
		List<FundBO> funds = new ArrayList<FundBO>();
		if (loanOffering.getLoanOfferingFunds() != null
				&& loanOffering.getLoanOfferingFunds().size() > 0)
			for (LoanOfferingFundEntity loanOfferingFund : loanOffering
					.getLoanOfferingFunds()) {
				funds.add(loanOfferingFund.getFund());
			}
		return funds;
	}

	private void loadFees(LoanAccountActionForm actionForm,
			Short loanOfferingId, HttpServletRequest request) throws Exception {
		FeeBusinessService feeService = (FeeBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.FeesService);
		LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct))
				.getLoanOffering(loanOfferingId);
		UserContext userContext = getUserContext(request);
		List<FeeBO> fees = feeService.getAllAppllicableFeeForLoanCreation();
		List<FeeView> additionalFees = new ArrayList<FeeView>();
		List<FeeView> defaultFees = new ArrayList<FeeView>();
		for (FeeBO fee : fees) {
			FeeView feeView = new FeeView(userContext, fee);
			if (loanOffering.isFeePresent(fee))
				defaultFees.add(feeView);
			else
				additionalFees.add(feeView);
		}
		actionForm.setDefaultFees(defaultFees);
		SessionUtils.setAttribute(LoanConstants.ADDITIONAL_FEES_LIST,
				additionalFees, request);
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				getMasterEntities(CollateralTypeEntity.class, getUserContext(
						request).getLocaleId()), request);
		SessionUtils.setAttribute(MasterConstants.BUSINESS_ACTIVITIES,
		((MasterDataService) ServiceFactory.getInstance()
						.getBusinessService(
								BusinessServiceName.MasterDataService))
						.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES,
								getUserContext(request).getLocaleId()), request);
	}

	private String getNameForBusinessActivityEntity(Integer entityId,
			Short localeId) throws Exception {
		if (entityId != null)
			return ((MasterDataService) ServiceFactory.getInstance()
					.getBusinessService(BusinessServiceName.MasterDataService))
					.retrieveMasterEntities(entityId, localeId);
		return "";
	}


	private FundBO getFund(HttpServletRequest request, Short fundId) throws Exception{
		List<FundBO> funds = (List<FundBO>) SessionUtils
		.getAttribute(LoanConstants.LOANFUNDS,request);
		for (FundBO fund : funds) {
			if (fund.getFundId().equals(fundId))
				return fund;
		}
		return null;
	}

	private List<RepaymentScheduleInstallment> getLoanSchedule(LoanBO loan) {
		List<RepaymentScheduleInstallment> schedule = new ArrayList<RepaymentScheduleInstallment>();
		for (AccountActionDateEntity actionDate : loan.getAccountActionDates()) {
			LoanScheduleEntity loanSchedule = (LoanScheduleEntity) actionDate;
			schedule.add(getRepaymentScheduleInstallment(loanSchedule));
		}
		Collections.sort(schedule,
				new Comparator<RepaymentScheduleInstallment>() {
					public int compare(RepaymentScheduleInstallment act1,
							RepaymentScheduleInstallment act2) {
						return act1.getInstallment().compareTo(
								act2.getInstallment());
					}
				});
		return schedule;
	}

	private RepaymentScheduleInstallment getRepaymentScheduleInstallment(
			LoanScheduleEntity loanSchedule) {
		return new RepaymentScheduleInstallment(
				loanSchedule.getInstallmentId(), loanSchedule.getActionDate(),
				loanSchedule.getPrincipal(), loanSchedule.getInterest(),
				loanSchedule.getTotalFeeDue(), loanSchedule.getMiscFee(),
				loanSchedule.getMiscPenalty());
	}

	private MasterDataEntity findMasterEntity(HttpServletRequest request,
			String collectionName, Short value) throws Exception{
		List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (MasterDataEntity entity : entities)
			if (entity.getId().equals(value))
				return entity;
		return null;
	}

	private void setFormAttributes(LoanBO loan, ActionForm form,
			HttpServletRequest request) throws Exception{
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		loanAccountActionForm
				.setLoanAmount(getStringValue(loan.getLoanAmount()));

		java.util.Date proposedDisbursement = (Date)
			SessionUtils.getAttribute(LoanConstants.PROPOSEDDISBDATE,request);
		loanAccountActionForm.setDisbursementDate(
			DateHelper.getUserLocaleDate(
				getUserContext(request).getPereferedLocale(), 
				DateHelper.toDatabaseFormat(proposedDisbursement)
			)
		);

		loanAccountActionForm.setIntDedDisbursement(loan
				.isInterestDeductedAtDisbursement() ? "1" : "0");
		loanAccountActionForm.setBusinessActivityId(getStringValue(loan
				.getBusinessActivityId()));
		if (loan.getCollateralType() != null)
			loanAccountActionForm.setCollateralTypeId(getStringValue(loan
					.getCollateralType().getId()));
		loanAccountActionForm.setCollateralNote(loan.getCollateralNote());
		loanAccountActionForm.setInterestRate(getStringValue(loan
				.getInterestRate()));
		loanAccountActionForm.setNoOfInstallments(getStringValue(loan
				.getNoOfInstallments()));
		loanAccountActionForm.setGracePeriodDuration(getStringValue(loan
				.getGracePeriodDuration()));
	}

	private ViewInstallmentDetails getUpcomingInstallmentDetails(
			AccountActionDateEntity upcomingAccountActionDate) {
		if (upcomingAccountActionDate != null) {
			LoanScheduleEntity upcomingInstallment = (LoanScheduleEntity) upcomingAccountActionDate;
			return new ViewInstallmentDetails(upcomingInstallment
					.getPrincipalDue(), upcomingInstallment.getInterestDue(),
					upcomingInstallment.getTotalFeeDueWithMiscFeeDue(),
					upcomingInstallment.getPenaltyDue());
		}
		return null;
	}

	private ViewInstallmentDetails getOverDueInstallmentDetails(
			List<AccountActionDateEntity> overDueInstallmentList) {
		Money principalDue = new Money();
		Money interestDue = new Money();
		Money feesDue = new Money();
		Money penaltyDue = new Money();
		for (AccountActionDateEntity accountActionDate : overDueInstallmentList) {
			LoanScheduleEntity installment = (LoanScheduleEntity) accountActionDate;
			principalDue = principalDue.add(installment.getPrincipalDue());
			interestDue = interestDue.add(installment.getInterestDue());
			feesDue = feesDue.add(installment.getTotalFees());
			penaltyDue = penaltyDue.add(installment.getPenaltyDue());
		}
		return new ViewInstallmentDetails(principalDue, interestDue, feesDue,
				penaltyDue);
	}

}

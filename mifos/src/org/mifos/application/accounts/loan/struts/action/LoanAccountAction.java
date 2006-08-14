package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
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
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.util.helpers.GracePeriodTypeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInstallment;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class LoanAccountAction extends AccountAppAction {

	private LoanBusinessService loanBusinessService;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public LoanAccountAction() throws ServiceException {
		loanBusinessService = (LoanBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Loan);
	}

	protected BusinessService getService() {
		return loanBusinessService;
	}

	public ActionForward getInstallmentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Integer accountId = Integer.valueOf(request.getParameter("accountId"));
		LoanBO loanBO = loanBusinessService.getAccount(accountId);
		ViewInstallmentDetails viewUpcomingInstallmentDetails = loanBusinessService
				.getUpcomingInstallmentDetails(loanBO
						.getDetailsOfNextInstallment());
		ViewInstallmentDetails viewOverDueInstallmentDetails = loanBusinessService
				.getOverDueInstallmentDetails(loanBO
						.getDetailsOfInstallmentsInArrears());
		Money totalAmountDue = viewUpcomingInstallmentDetails.getSubTotal()
				.add(viewOverDueInstallmentDetails.getSubTotal());
		SessionUtils.setAttribute(
				LoanConstants.VIEW_UPCOMING_INSTALLMENT_DETAILS,
				viewUpcomingInstallmentDetails, request.getSession());
		SessionUtils.setAttribute(
				LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS,
				viewOverDueInstallmentDetails, request.getSession());
		SessionUtils.setAttribute(LoanConstants.TOTAL_AMOUNT_OVERDUE,
				totalAmountDue, request.getSession());
		return mapping
				.findForward(LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS);
	}

	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In loanAccountAction::getAllActivity()");
		String globalAccountNum = request.getParameter("globalAccountNum");
		SessionUtils.setAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW,
				loanBusinessService.getAllActivityView(globalAccountNum,
						((UserContext) SessionUtils.getAttribute(
								Constants.USER_CONTEXT_KEY, request
										.getSession())).getLocaleId()), request
						.getSession());
		return mapping.findForward(MethodNameConstants.GETALLACTIVITY_SUCCESS);
	}

	public ActionForward forwardWaiveCharge(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type = request.getParameter("type");
		return mapping.findForward("waive" + type + "Charges_Success");
	}

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");
		request.getSession().removeAttribute(Constants.BUSINESS_KEY);
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
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request
				.getSession());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	public ActionForward getLoanRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.getLoanRepaymentSchedule
				.toString());
	}

	public ActionForward viewStatusHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		Hibernate.initialize(loanBO.accountStatusChangeHistory);
		loanBO.setUserContext(getUserContext(request));
		List<AccountStatusChangeHistoryEntity> accStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>(
				loanBO.accountStatusChangeHistory);
		SessionUtils.setAttribute(LoanConstants.STATUS_HISTORY,
				accStatusChangeHistory, request.getSession());
		return mapping.findForward(ActionForwards.viewStatusHistory.toString());
	}

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

	public ActionForward getPrdOfferings(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("Inside getPrdOfferings method");

		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		CustomerBO customer = getCustomer(loanActionForm.getCustomerIdValue());
		doCleanUp(request.getSession());
		List<LoanOfferingBO> loanOfferings = loanBusinessService
				.getApplicablePrdOfferings(customer.getCustomerLevel());
		removePrdOfferingsNotMachingCustomerMeeting(loanOfferings, customer);
		SessionUtils.setAttribute(LoanConstants.LOANPRDOFFERINGS,
				loanOfferings, request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, customer,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, customer
				.getCustomerAccount().getNextMeetingDate(), request
				.getSession());
		return mapping.findForward(ActionForwards.getPrdOfferigs_success
				.toString());
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		LoanOfferingBO loanOffering = getLoanOffering(loanActionForm
				.getPrdOfferingIdValue(), getUserContext(request).getLocaleId());
		setDataIntoForm(loanOffering, loanActionForm, request);
		loadFees(loanActionForm, loanOffering, request);
		loadMasterData(request);
		request.getSession().removeAttribute(LoanConstants.LOANOFFERING);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request.getSession());
		SessionUtils.setAttribute(LoanConstants.LOANFUNDS,
				getFunds(loanOffering), request.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward schedulePreview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		HttpSession session = request.getSession();
		LoanBO loan = null;
		try {
			loan = new LoanBO(getUserContext(request), (LoanOfferingBO) session
					.getAttribute(LoanConstants.LOANOFFERING),
					(CustomerBO) session
							.getAttribute(LoanConstants.LOANACCOUNTOWNER),
					AccountState.LOANACC_PARTIALAPPLICATION, loanActionForm
							.loanAmountValue(), loanActionForm
							.getNoOfInstallmentsValue(), loanActionForm
							.getDisbursementDateValue(getUserContext(request)
									.getPereferedLocale()), loanActionForm
							.isInterestDedAtDisbValue(), loanActionForm
							.getInterestDoubleValue(), loanActionForm
							.getGracePeriodDurationValue(), getFund(session,
							loanActionForm.getLoanOfferingFundValue()),
					loanActionForm.getFeesToApply());
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("Proposed/Actual disbursal date", new ActionMessage(
					"errors.mandatory", "Proposed/Actual disbursal date"));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(ActionForwards.load_success.toString());
		}
		loan.setBusinessActivityId(loanActionForm.getBusinessActivityIdValue());
		loan.setCollateralNote(loanActionForm.getCollateralNote());
		CollateralTypeEntity collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
				request.getSession(), MasterConstants.COLLATERAL_TYPES,
				loanActionForm.getCollateralTypeIdValue());
		loan.setCollateralType(collateralTypeEntity);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request
				.getSession());
		SessionUtils.setAttribute(LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS,
				getLoanSchedule(loan), request.getSession());
		return mapping.findForward(ActionForwards.schedulePreview_success
				.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loan = (LoanBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		loan.setAccountState(new AccountStateEntity(loanActionForm.getState()));
		loan.save();
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBO = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, loanBO
				.getDisbursementDate(), request.getSession());
		request.getSession().removeAttribute(LoanConstants.LOANOFFERING);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, getLoanOffering(loanBO.getLoanOffering().getPrdOfferingId(), getUserContext(request).getLocaleId()),
				request.getSession());
		setFormAttributes(loanBO, form, request);
		loadMasterData(request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	public ActionForward managePrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.manageprevious_success
				.toString());
	}

	public ActionForward managePreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBO = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		CollateralTypeEntity collateralTypeEntity = null;
		if (loanAccountActionForm.getCollateralTypeIdValue() != null) {
			collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
					request.getSession(), MasterConstants.COLLATERAL_TYPES,
					loanAccountActionForm.getCollateralTypeIdValue());
			collateralTypeEntity.setLocaleId(getUserContext(request)
					.getLocaleId());
		}
		loanBO.setCollateralType(collateralTypeEntity);
		loanBO.setBusinessActivityId(loanAccountActionForm.getBusinessActivityIdValue());
		return mapping.findForward(ActionForwards.managepreview_success
				.toString());
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loan_detail_page.toString());
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBO = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request.getSession());
		Integer versionNumber = loanBO.getVersionNo();
		loanBO = loanBusinessService.findBySystemId(loanBO
				.getGlobalAccountNum());
		/*if(! loanBO.getVersionNo().equals(versionNumber))
			throw new AccountException("error.update");*/
		loanBO.setUserContext(getUserContext(request));
		updateBusinessData(loanBO, form, request);
		loanBO.updateLoan();
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private void updateBusinessData(LoanBO loanBO, ActionForm form,
			HttpServletRequest request) throws AccountException {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		if (loanAccountActionForm.getIntDedDisbursement().equals("1")) {
			try {
				loanBO.setGracePeriodType((GracePeriodTypeEntity)getMasterEntities(GracePeriodTypeConstants.NONE,GracePeriodTypeEntity.class,getUserContext(request).getLocaleId()));
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
					request.getSession(), MasterConstants.COLLATERAL_TYPES,
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
			HttpServletRequest request) throws SystemException,
			ApplicationException {		
		SessionUtils.setAttribute(LoanConstants.RECENTACCOUNTACTIVITIES,
				loanBusinessService.getRecentActivityView(loanBO
						.getGlobalAccountNum(), getUserContext(request)
						.getLocaleId()), request.getSession());
		SessionUtils
				.setAttribute(AccountConstants.LAST_PAYMENT_ACTION,
						loanBusinessService.getLastPaymentAction(loanBO
								.getAccountId()), request.getSession());
		SessionUtils.setAttribute(LoanConstants.NOTES, loanBO
				.getRecentAccountNotes(), request.getSession());
	}

	private void removePrdOfferingsNotMachingCustomerMeeting(
			List<LoanOfferingBO> loanOfferings, CustomerBO customer) {
		MeetingBO customerMeeting = customer.getCustomerMeeting().getMeeting();
		for (Iterator<LoanOfferingBO> iter = loanOfferings.iterator(); iter
				.hasNext();) {
			LoanOfferingBO loanOffering = iter.next();
			if (!isMeetingMatched(customerMeeting, loanOffering
					.getPrdOfferingMeeting().getMeeting()))
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
		session.removeAttribute(Constants.BUSINESS_KEY);
		session.removeAttribute(LoanConstants.LOANACCOUNTOWNER);
		session.removeAttribute(LoanConstants.PRDOFFERINGID);
	}

	private LoanOfferingBO getLoanOffering(Short loanOfferingId, short localeId)
			throws ServiceException {
		return loanBusinessService.getLoanOffering(loanOfferingId, localeId);
	}

	private void setDataIntoForm(LoanOfferingBO loanOffering,
			LoanAccountActionForm loanAccountActionForm,
			HttpServletRequest request) {
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
				getUserContext(request).getPereferedLocale(), request
						.getSession().getAttribute(
								LoanConstants.PROPOSEDDISBDATE).toString()));
	}

	private List<Fund> getFunds(LoanOfferingBO loanOffering) {
		List<Fund> funds = new ArrayList<Fund>();
		if (loanOffering.getLoanOfferingFunds() != null
				&& loanOffering.getLoanOfferingFunds().size() > 0)
			for (LoanOfferingFundEntity loanOfferingFund : loanOffering
					.getLoanOfferingFunds()) {
				funds.add(loanOfferingFund.getFund());
			}
		return funds;
	}

	private void loadFees(LoanAccountActionForm actionForm,
			LoanOfferingBO loanOffering, HttpServletRequest request)
			throws FeeException, ServiceException {
		FeeBusinessService feeService = (FeeBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.FeesService);
		Short localeId = getUserContext(request).getLocaleId();
		List<FeeBO> fees = feeService.getAllAppllicableFeeForLoanCreation();
		List<FeeView> additionalFees = new ArrayList<FeeView>();
		List<FeeView> defaultFees = new ArrayList<FeeView>();
		for (FeeBO fee : fees) {
			if (loanOffering.isFeePresent(fee))
				defaultFees.add(new FeeView(fee, localeId));
			else
				additionalFees.add(new FeeView(fee, localeId));
		}
		actionForm.setDefaultFees(defaultFees);
		SessionUtils.setAttribute(LoanConstants.ADDITIONAL_FEES_LIST,
				additionalFees, request.getSession());
	}

	private void loadMasterData(HttpServletRequest request)
			throws ApplicationException, SystemException {
		SessionUtils.setAttribute(MasterConstants.COLLATERAL_TYPES,
				getMasterEntities(CollateralTypeEntity.class, getUserContext(
						request).getLocaleId()), request.getSession());
		SessionUtils.setAttribute(MasterConstants.BUSINESS_ACTIVITIES,
				loanBusinessService.retrieveMasterEntities(
						MasterConstants.LOAN_PURPOSES, getUserContext(request)
								.getLocaleId()), request.getSession());
	}

	private Fund getFund(HttpSession session, Short fundId) {
		List<Fund> funds = (List<Fund>) session
				.getAttribute(LoanConstants.LOANFUNDS);
		for (Fund fund : funds) {
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

	private MasterDataEntity findMasterEntity(HttpSession session,
			String collectionName, Short value) {
		List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
				.getAttribute(collectionName, session);
		for (MasterDataEntity entity : entities)
			if (entity.getId().equals(value))
				return entity;
		return null;
	}

	private void setFormAttributes(LoanBO loan, ActionForm form,
			HttpServletRequest request) {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		loanAccountActionForm
				.setLoanAmount(getStringValue(loan.getLoanAmount()));
		loanAccountActionForm.setDisbursementDate(DateHelper.getUserLocaleDate(
				getUserContext(request).getPereferedLocale(), request
						.getSession().getAttribute(
								LoanConstants.PROPOSEDDISBDATE).toString()));
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
}

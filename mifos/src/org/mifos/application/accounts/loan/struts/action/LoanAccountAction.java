package org.mifos.application.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.ViewInstallmentDetails;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
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
		loanBusinessService = new LoanBusinessService();
	}

	@Override
	protected BusinessService getService() {
		return loanBusinessService;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("loanAccountAction");
		security.allow("getAllActivity", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("getLoanRepaymentSchedule", SecurityConstants.VIEW);
		security.allow("viewStatusHistory", SecurityConstants.VIEW);
		security.allow("manage", SecurityConstants.LOAN_UPDATE_LOAN);
		security.allow("managePreview", SecurityConstants.VIEW);
		security.allow("managePrevious", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.LOAN_UPDATE_LOAN);

		security.allow("getPrdOfferings", SecurityConstants.VIEW);
		security.allow("load", SecurityConstants.VIEW);
		security.allow("schedulePreview", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("previous", SecurityConstants.VIEW);
		security.allow("create", SecurityConstants.VIEW);

		security.allow("loadChangeLog", SecurityConstants.VIEW);
		security.allow("cancelChangeLog", SecurityConstants.VIEW);
		security.allow("waiveChargeDue", SecurityConstants.VIEW);
		security.allow("forwardWaiveCharge", SecurityConstants.VIEW);
		security.allow("waiveChargeOverDue", SecurityConstants.VIEW);
        security.allow("redoLoanBegin", SecurityConstants.CAN_REDO_LOAN_DISPURSAL);
        return security;
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

		SessionUtils.setAttribute(LoanConstants.NEXTMEETING_DATE, loanBO
				.getNextMeetingDate(), request);
		loanBO = null;
		return mapping
				.findForward(LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In loanAccountAction::getAllActivity()");
		String globalAccountNum = request.getParameter("globalAccountNum");
		SessionUtils.setCollectionAttribute(LoanConstants.LOAN_ALL_ACTIVITY_VIEW,
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

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");

		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		loanBusinessService.initialize(loanBO.getLoanMeeting());
		for (AccountActionDateEntity accountActionDateEntity : loanBO
				.getAccountActionDates()) {
			loanBusinessService.initialize(accountActionDateEntity);
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity) accountActionDateEntity)
					.getAccountFeesActionDetails()) {
				loanBusinessService.initialize(accountFeesActionDetailEntity);
			}
		}
		setLocaleForMasterEntities(loanBO, getUserContext(request)
				.getLocaleId());
		loadLoanDetailPageInfo(loanBO, request);
		loadMasterData(request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
		
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		List<SurveyInstance> surveys = surveysPersistence.retrieveInstancesByAccount(loanBO);
		boolean activeSurveys =
        	surveysPersistence.retrieveSurveysByTypeAndState(
        			SurveyType.LOAN, SurveyState.ACTIVE).size() > 0;
		request.setAttribute(CustomerConstants.SURVEY_KEY, surveys);
               request.setAttribute(CustomerConstants.SURVEY_COUNT, activeSurveys);
		request.setAttribute(AccountConstants.SURVEY_KEY, surveys);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getLoanRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanBO loanBO = loanBusinessService.getAccount((getIntegerValue(request.getParameter("accountId"))));
		loanBO.setUserContext(getUserContext(request));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
		return mapping.findForward(ActionForwards.getLoanRepaymentSchedule
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward viewStatusHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String globalAccountNum = request.getParameter("globalAccountNum");
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		loanBusinessService.initialize(loanBO.getAccountStatusChangeHistory());
		loanBO.setUserContext(getUserContext(request));
		List<AccountStatusChangeHistoryEntity> accStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>(
				loanBO.getAccountStatusChangeHistory());
		SessionUtils.setCollectionAttribute(LoanConstants.STATUS_HISTORY,
				accStatusChangeHistory, request);
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

	@TransactionDemarcate(saveToken = true)
	public ActionForward getPrdOfferings(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("Inside getPrdOfferings method");

		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		CustomerBO customer = getCustomer(loanActionForm.getCustomerIdValue());
		customer.getOffice().getOfficeId();
		customer.getPersonnel().getPersonnelId();

        // See commented out doCleanUp() method
        //doCleanUp(request.getSession());
		List<LoanOfferingBO> loanOfferings = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct))
				.getApplicablePrdOfferings(customer.getCustomerLevel());
		removePrdOfferingsNotMachingCustomerMeeting(loanOfferings, customer);
		SessionUtils.setCollectionAttribute(LoanConstants.LOANPRDOFFERINGS,
				loanOfferings, request);
		SessionUtils.setAttribute(LoanConstants.LOANACCOUNTOWNER, customer,
				request);
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, customer
				.getCustomerAccount().getNextMeetingDate(), request);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        return mapping.findForward(ActionForwards.getPrdOfferigs_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		loadFees(loanActionForm, loanActionForm.getPrdOfferingIdValue(),
				request);
		LoanOfferingBO loanOffering = getLoanOffering(loanActionForm
				.getPrdOfferingIdValue(), getUserContext(request).getLocaleId());
		setDataIntoForm(loanOffering, loanActionForm, request);
		loadCreateMasterData(loanActionForm,request);
		SessionUtils.removeAttribute(LoanConstants.LOANOFFERING, request);

		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setCollectionAttribute(LoanConstants.LOANFUNDS,
				getFunds(loanOffering), request);
        if (request.getParameter("perspective") != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        return mapping.findForward(ActionForwards.load_success.toString());
	}

    public ActionForward redoLoanBegin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.beginRedoLoanDisbursal_success.toString());
    }

    private void loadCreateMasterData(LoanAccountActionForm actionForm, HttpServletRequest request) throws Exception {
		loadMasterData(request);
		loadCreateCustomFields(actionForm,request);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward schedulePreview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;

		LoanBO loan = constructLoan(loanActionForm, request);

		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
        List<RepaymentScheduleInstallment> installments = getLoanSchedule(loan);
        SessionUtils.setCollectionAttribute(LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS,
				installments, request);

        String perspective = request.getParameter("perspective");
        if (perspective != null) {
            request.setAttribute("perspective", request.getParameter("perspective"));
        }
        loanActionForm.initializeTransactionFields(getUserContext(request), installments.size());

        boolean isPendingApprovalDefined = Configuration.getInstance()
				.getAccountConfig(getUserContext(request).getBranchId())
				.isPendingApprovalStateDefinedForLoan();
		SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
				isPendingApprovalDefined, request);

        return mapping.findForward(ActionForwards.schedulePreview_success
				.toString());
	}

    private CustomerBO getCustomer(HttpServletRequest request)
            throws PageExpiredException, ServiceException {
        CustomerBO oldCustomer = (CustomerBO) SessionUtils.getAttribute(
				LoanConstants.LOANACCOUNTOWNER, request);
		CustomerBO customer = getCustomer(oldCustomer.getCustomerId());
		customer.getPersonnel().getDisplayName();
		customer.getOffice().getOfficeName();
		// TODO: I'm not sure why we're resetting version number - need to investigate this
        customer.setVersionNo(oldCustomer.getVersionNo());
        return customer;
    }

    private LoanBO constructLoan(LoanAccountActionForm loanActionForm, HttpServletRequest request)
            throws AccountException, ServiceException, PageExpiredException {
        LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct)).getLoanOffering(
				((LoanOfferingBO) SessionUtils.getAttribute(
						LoanConstants.LOANOFFERING, request))
						.getPrdOfferingId(), getUserContext(request)
						.getLocaleId());
        String perspective = request.getParameter("perspective");
        CustomerBO customer = getCustomer(request);
        LoanBO loan;
        if (perspective != null && perspective.equalsIgnoreCase("redoloan")) {
            loan = LoanBO.redoLoan(getUserContext(request), loanOffering, customer,
                            AccountState.LOANACC_PARTIALAPPLICATION, loanActionForm
                            .loanAmountValue(), loanActionForm
                            .getNoOfInstallmentsValue(), loanActionForm
                            .getDisbursementDateValue(getUserContext(request)
                            .getPreferredLocale()), loanActionForm
                            .isInterestDedAtDisbValue(), loanActionForm
                            .getInterestDoubleValue(), loanActionForm
                            .getGracePeriodDurationValue(), getFund(request,
                            loanActionForm.getLoanOfferingFundValue()),
                            loanActionForm.getFeesToApply(),loanActionForm
                            .getCustomFields());
        }
        else {
            loan = LoanBO.createLoan(getUserContext(request), loanOffering, customer,
                            AccountState.LOANACC_PARTIALAPPLICATION, loanActionForm
                            .loanAmountValue(), loanActionForm
                            .getNoOfInstallmentsValue(), loanActionForm
                            .getDisbursementDateValue(getUserContext(request)
                            .getPreferredLocale()), loanActionForm
                            .isInterestDedAtDisbValue(), loanActionForm
                            .getInterestDoubleValue(), loanActionForm
                            .getGracePeriodDurationValue(), getFund(request,
                            loanActionForm.getLoanOfferingFundValue()),
                            loanActionForm.getFeesToApply(),loanActionForm
                            .getCustomFields());
        }
        loan.setBusinessActivityId(loanActionForm.getBusinessActivityIdValue());
		loan.setCollateralNote(loanActionForm.getCollateralNote());
		CollateralTypeEntity collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
				request, MasterConstants.COLLATERAL_TYPES, loanActionForm
						.getCollateralTypeIdValue());
		loan.setCollateralType(collateralTypeEntity);

        return loan;
    }

    public LoanBO redoLoan(LoanAccountActionForm loanActionForm,
                           HttpServletRequest request)
            throws PageExpiredException, AccountException, ServiceException {
        LoanBO loan = constructLoan(loanActionForm, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

        loan.changeStatus(AccountState.LOANACC_ACTIVEINGOODSTANDING,
                null, "Automatic Status Update (Redo Loan)");

        PersonnelBO personnel = null;
        try {
            personnel = new PersonnelPersistence()
                    .getPersonnel(getUserContext(request).getId());
        } catch (PersistenceException e) {
            throw new IllegalStateException(e);
        }

        // We're assuming cash disbursal for this situation right now
        loan.disburseLoan(personnel, Short.valueOf((short)1), false);

        List<PaymentDataHtmlBean> paymentDataBeans =
                loanActionForm.getPaymentDataBeans();
        PaymentData payment;
        CustomerBO customer = getCustomer(request);
        try {
            for (PaymentDataTemplate template : paymentDataBeans) {
                if (template.getTotalAmount() != null
                        && template.getTransactionDate() != null) {
                        if (! customer.getCustomerMeeting().getMeeting().isValidMeetingDate(
                            template.getTransactionDate(), DateUtils.getLastDayOfNextYear())) {
                            throw new AccountException("errors.invalidTxndate");
                        }
                    payment = PaymentData.createPaymentData(template);
                    loan.applyPayment(payment, false);
                }
            }
        }
        catch (MeetingException e) {
            throw new ServiceException(e);
        }

        return loan;
    }

    @TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
            throws PageExpiredException, AccountException, CustomerException, ServiceException {
        LoanAccountActionForm loanAccountForm = (LoanAccountActionForm)form;
        String perspective = loanAccountForm.getPerspective();
        if (perspective != null) {
            request.setAttribute("perspective", perspective);

            if (perspective.equals(LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN)) {
                LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;

                LoanBO loan = redoLoan(loanActionForm, request);
                SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            }
        }

        return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
        String perspective = loanActionForm.getPerspective();
        CustomerBO customer = (CustomerBO) SessionUtils.getAttribute(
				LoanConstants.LOANACCOUNTOWNER, request);
        LoanBO loan;
        if (perspective != null &&
                perspective.equals(LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN)) {
            loan = redoLoan(loanActionForm, request);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
            loan.save();
        }
        else {
            checkPermissionForCreate(loanActionForm.getState().getValue(),
                    getUserContext(request), null, customer.getOffice()
                            .getOfficeId(), customer.getPersonnel()
                            .getPersonnelId());
            loan = (LoanBO) SessionUtils.getAttribute(
                    Constants.BUSINESS_KEY, request);
            loan.save(loanActionForm.getState());
        }
		loanActionForm.setAccountId(loan.getAccountId().toString());
		request.setAttribute("customer", customer);
		request.setAttribute("globalAccountNum", loan.getGlobalAccountNum());
		request.setAttribute("loan", loan);
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.getAccount(loanBOInSession.getAccountId());
		loanBO.setUserContext(getUserContext(request));
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, loanBO
				.getDisbursementDate(), request);
		SessionUtils.removeAttribute(LoanConstants.LOANOFFERING, request);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, getLoanOffering(
				loanBO.getLoanOffering().getPrdOfferingId(), getUserContext(
						request).getLocaleId()), request);
		loadUpdateMasterData(request);
		setFormAttributes(loanBO, form, request);
		loanBOInSession = null;
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward managePrevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.manageprevious_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward managePreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;

		SessionUtils.removeAttribute(MasterConstants.COLLATERAL_TYPE_NAME,
				request);
		SessionUtils.removeAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME,
				request);
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
					getNameForBusinessActivityEntity(loanAccountActionForm
							.getBusinessActivityIdValue(), getUserContext(
							request).getLocaleId()), request);

		}
		return mapping.findForward(ActionForwards.managepreview_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.loan_detail_page.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.findBySystemId(loanBOInSession
				.getGlobalAccountNum());
		checkVersionMismatch(loanBOInSession.getVersionNo(),loanBO.getVersionNo());
		loanBO.setVersionNo(loanBOInSession.getVersionNo());
		loanBO.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(loanBO);
		
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		loanBO.updateLoan(loanAccountActionForm.isInterestDedAtDisbValue(),
				loanAccountActionForm.getLoanAmountValue(),
				loanAccountActionForm.getInterestDoubleValue(),
				loanAccountActionForm.getNoOfInstallmentsValue(),
				loanAccountActionForm.getDisbursementDateValue(getUserContext(
						request).getPreferredLocale()), loanAccountActionForm
						.getGracePeriodDurationValue(), loanAccountActionForm
						.getBusinessActivityIdValue(), loanAccountActionForm
						.getCollateralNote(), getCollateralTypeEntity(form,
						request),loanAccountActionForm.getCustomFields());
		loanBOInSession = null;
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private CollateralTypeEntity getCollateralTypeEntity(ActionForm form,
			HttpServletRequest request) throws Exception {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		if (loanAccountActionForm.getCollateralTypeIdValue() != null) {
			CollateralTypeEntity collateralTypeEntity = (CollateralTypeEntity) findMasterEntity(
					request, MasterConstants.COLLATERAL_TYPES,
					loanAccountActionForm.getCollateralTypeIdValue());
			collateralTypeEntity.setLocaleId(getUserContext(request)
					.getLocaleId());
			return collateralTypeEntity;
		}
		return null;
	}

	private void setLocaleForMasterEntities(LoanBO loanBO, Short localeId) {
		if (loanBO.getGracePeriodType() != null) {
			// Is this locale ever consulted?  I don't see a place...
			loanBO.getGracePeriodType().setLocaleId(localeId);
		}
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
		SessionUtils.setCollectionAttribute(LoanConstants.RECENTACCOUNTACTIVITIES,
				loanBusinessService.getRecentActivityView(loanBO
						.getGlobalAccountNum(), getUserContext(request)
						.getLocaleId()), request);
		SessionUtils
				.setAttribute(AccountConstants.LAST_PAYMENT_ACTION,
						loanBusinessService.getLastPaymentAction(loanBO
								.getAccountId()), request);
		SessionUtils.setCollectionAttribute(LoanConstants.NOTES, loanBO
				.getRecentAccountNotes(), request);
		 loadCustomFieldDefinitions(request);
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

	private boolean isMeetingMatched(MeetingBO meetingToBeMatched,
			MeetingBO meetingToBeMatchedWith) {
		return meetingToBeMatched != null
				&& meetingToBeMatchedWith != null
				&& meetingToBeMatched.getMeetingDetails().getRecurrenceType()
						.getRecurrenceId().equals(
								meetingToBeMatchedWith.getMeetingDetails()
										.getRecurrenceType().getRecurrenceId())
				&& isMultiple(meetingToBeMatchedWith.getMeetingDetails()
						.getRecurAfter(), meetingToBeMatched
						.getMeetingDetails().getRecurAfter());
	}

	private boolean isMultiple(Short valueToBeChecked,
			Short valueToBeCheckedWith) {
		return valueToBeChecked % valueToBeCheckedWith == 0;
	}

	// Temporarily commenting this method out because it's preventing
    // request parameters from propogating to the response page.
    // Manually tested behavioral changes and found no affects.
    // Should remove commented out code after a few months which
    // should be around Sept 2007.
    /*private void doCleanUp(HttpSession session) {
		session.setAttribute("loanAccountActionForm", null);
	}*/

	private LoanOfferingBO getLoanOffering(Short loanOfferingId, short localeId)
			throws Exception {
		return ((LoanPrdBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.LoanProduct))
				.getLoanOffering(loanOfferingId, localeId);
	}

	private void setDataIntoForm(LoanOfferingBO loanOffering,
			LoanAccountActionForm loanAccountActionForm,
			HttpServletRequest request) throws Exception {
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
		loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), SessionUtils
		.getAttribute(LoanConstants.PROPOSEDDISBDATE, request)
		.toString()));
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
		for (Iterator<FeeBO> iter = fees.iterator(); iter.hasNext();) {
			FeeBO fee = iter.next();
			if (fee.isPeriodic()
					&& !isMeetingMatched(fee.getFeeFrequency()
							.getFeeMeetingFrequency(), loanOffering
							.getLoanOfferingMeeting().getMeeting())) {
				iter.remove();
			} else {
				FeeView feeView = new FeeView(userContext, fee);
				if (loanOffering.isFeePresent(fee))
					defaultFees.add(feeView);
				else
					additionalFees.add(feeView);
			}
		}
		actionForm.setDefaultFees(defaultFees);
		SessionUtils.setCollectionAttribute(LoanConstants.ADDITIONAL_FEES_LIST,
				additionalFees, request);
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		
		
		SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES,
				getMasterEntities(CollateralTypeEntity.class, getUserContext(
						request).getLocaleId()), request);
		SessionUtils
				.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES,
						((MasterDataService) ServiceFactory.getInstance()
								.getBusinessService(
										BusinessServiceName.MasterDataService))
								.retrieveMasterEntities(
										MasterConstants.LOAN_PURPOSES,
										getUserContext(request).getLocaleId()),
						request);
		
		
	}

	private String getNameForBusinessActivityEntity(Integer entityId,
			Short localeId) throws Exception {
		if (entityId != null)
			return ((MasterDataService) ServiceFactory.getInstance()
					.getBusinessService(BusinessServiceName.MasterDataService))
					.retrieveMasterEntities(entityId, localeId);
		return "";
	}

	private FundBO getFund(HttpServletRequest request, Short fundId)
            throws PageExpiredException {
		List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(
				LoanConstants.LOANFUNDS, request);
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
			String collectionName, Short value) throws PageExpiredException {
		List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
				.getAttribute(collectionName, request);
		for (MasterDataEntity entity : entities)
			if (entity.getId().equals(value))
				return entity;
		return null;
	}

	private void setFormAttributes(LoanBO loan, ActionForm form,
			HttpServletRequest request) throws Exception {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		loanAccountActionForm.setStateSelected(getStringValue(loan.getAccountState().getId()));
		loanAccountActionForm
				.setLoanAmount(getStringValue(loan.getLoanAmount()));

		java.util.Date proposedDisbursement = (Date) SessionUtils.getAttribute(
				LoanConstants.PROPOSEDDISBDATE, request);
		loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(getUserContext(request).getPreferredLocale(), DateUtils.toDatabaseFormat(proposedDisbursement)));

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
		loanAccountActionForm.setCustomFields(createCustomFieldViews(loan
				.getAccountCustomFields(), request));
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
		return new ViewInstallmentDetails(new Money(), new Money(),
				new Money(), new Money());
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
			feesDue = feesDue.add(installment.getTotalFeeDueWithMiscFeeDue());
			penaltyDue = penaltyDue.add(installment.getPenaltyDue());
		}
		return new ViewInstallmentDetails(principalDue, interestDue, feesDue,
				penaltyDue);
	}

	protected void checkPermissionForCreate(Short newState,
			UserContext userContext, Short flagSelected, Short officeId,
			Short loanOfficerId) throws ApplicationException {
		if (!isPermissionAllowed(newState, userContext, officeId,
				loanOfficerId, true))
			throw new AccountException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(Short newSate, UserContext userContext,
			Short officeId, Short loanOfficerId, boolean saveFlag) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(ActivityMapper.getInstance()
						.getActivityIdForState(newSate), officeId,
						loanOfficerId));
	}
	
	private void loadCreateCustomFields(LoanAccountActionForm actionForm,
			HttpServletRequest request) throws Exception {
		loadCustomFieldDefinitions(request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs =
			(List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						DateUtils.getUserLocaleDate(getUserContext(request)
						.getPreferredLocale(), fieldDef
						.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}
	private void loadCustomFieldDefinitions(HttpServletRequest request)
	throws Exception {
		SessionUtils.setCollectionAttribute(LoanConstants.CUSTOM_FIELDS,
				getAccountBizService().retrieveCustomFieldsDefinition(EntityType.LOAN) , request);
	}
	
	private List<CustomFieldView> createCustomFieldViews(
			Set<AccountCustomFieldEntity> customFieldEntities,
			HttpServletRequest request) throws ApplicationException {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		Locale locale = getUserContext(request).getPreferredLocale();
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			for (AccountCustomFieldEntity customFieldEntity : customFieldEntities) {
				if (customFieldDef.getFieldId().equals(
						customFieldEntity.getFieldId())) {
					if (customFieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
						customFields.add(new CustomFieldView(customFieldEntity
								.getFieldId(), DateUtils.getUserLocaleDate(locale, customFieldEntity.getFieldValue()),
								customFieldDef.getFieldType()));
					} else {
						customFields
								.add(new CustomFieldView(customFieldEntity
										.getFieldId(), customFieldEntity
										.getFieldValue(), customFieldDef
										.getFieldType()));
					}
				}
			}
		}
		return customFields;
	}
	
	private void loadUpdateMasterData(HttpServletRequest request)throws Exception {
		loadMasterData(request);
		loadCustomFieldDefinitions(request);
	}
}

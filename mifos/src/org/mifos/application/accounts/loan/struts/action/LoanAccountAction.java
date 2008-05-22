package org.mifos.application.accounts.loan.struts.action;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.ADDITIONAL_FEES_LIST;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.ADMINISTRATIVE_DOCUMENT_IS_ENABLED;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.CLIENT_LIST;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.CUSTOM_FIELDS;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANACCOUNTOWNER;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANACCOUNTOWNERISACLIENT;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANACCOUNTOWNERISAGROUP;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANFUNDS;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANINDIVIDUALMONITORINGENABLED;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANOFFERING;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOANPRDOFFERINGS;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOAN_ALL_ACTIVITY_VIEW;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.MAX_RANGE_IS_NOT_MET;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.METHODCALLED;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.MIN_RANGE_IS_NOT_MET;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.NEXTMEETING_DATE;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.NOTES;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.PROPOSEDDISBDATE;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.RECENTACCOUNTACTIVITIES;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.RECURRENCEID;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.STATUS_HISTORY;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.TOTAL_AMOUNT_OVERDUE;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.VIEWINSTALLMENTDETAILS_SUCCESS;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.VIEW_OVERDUE_INSTALLMENT_DETAILS;
import static org.mifos.application.accounts.loan.util.helpers.LoanConstants.VIEW_UPCOMING_INSTALLMENT_DETAILS;
import static org.mifos.framework.http.request.RequestConstants.ACCOUNT_ID;
import static org.mifos.framework.http.request.RequestConstants.CUSTOMER_ID;
import static org.mifos.framework.http.request.RequestConstants.GLOBAL_ACCOUNT_NUM;
import static org.mifos.framework.http.request.RequestConstants.PERSPECTIVE;
import static org.mifos.framework.util.helpers.Constants.BUSINESS_KEY;

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
import org.mifos.application.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.application.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.admindocuments.persistence.AdminDocAccStateMixPersistence;
import org.mifos.application.admindocuments.persistence.AdminDocumentPersistence;
import org.mifos.application.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.business.service.MeetingBusinessService;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanAmountOption;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
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
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class LoanAccountAction extends AccountAppAction {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	private LoanBusinessService loanBusinessService;
	private FeeBusinessService feeService;
	private LoanPrdBusinessService loanPrdBusinessService;
	private ClientBusinessService clientBusinessService;

	private MasterDataService masterDataService;

	private MeetingBusinessService meetingBusinessService;

	private ConfigurationPersistence configurationPersistence;

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public LoanAccountAction() throws Exception {
		loanBusinessService = new LoanBusinessService();
		feeService = new FeeBusinessService();
		loanPrdBusinessService = new LoanPrdBusinessService();
		clientBusinessService = new ClientBusinessService();
		masterDataService = new MasterDataService();
		meetingBusinessService = new MeetingBusinessService();
		configurationPersistence = new ConfigurationPersistence();
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
		security.allow("redoLoanBegin",
				SecurityConstants.CAN_REDO_LOAN_DISPURSAL);
		return security;
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getInstallmentDetails(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Integer accountId = Integer.valueOf(request.getParameter(ACCOUNT_ID));
		LoanBO loanBO = loanBusinessService.getAccount(accountId);
		ViewInstallmentDetails viewUpcomingInstallmentDetails = getUpcomingInstallmentDetails(loanBO
				.getDetailsOfNextInstallment());
		ViewInstallmentDetails viewOverDueInstallmentDetails = getOverDueInstallmentDetails(loanBO
				.getDetailsOfInstallmentsInArrears());
		Money totalAmountDue = viewUpcomingInstallmentDetails.getSubTotal()
				.add(viewOverDueInstallmentDetails.getSubTotal());
		SessionUtils.setAttribute(
				VIEW_UPCOMING_INSTALLMENT_DETAILS,
				viewUpcomingInstallmentDetails, request);
		SessionUtils.setAttribute(
				VIEW_OVERDUE_INSTALLMENT_DETAILS,
				viewOverDueInstallmentDetails, request);
		SessionUtils.setAttribute(TOTAL_AMOUNT_OVERDUE,
				totalAmountDue, request);

		SessionUtils.setAttribute(NEXTMEETING_DATE, loanBO
				.getNextMeetingDate(), request);
		loanBO = null;
		return mapping
				.findForward(VIEWINSTALLMENTDETAILS_SUCCESS);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getAllActivity(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In loanAccountAction::getAllActivity()");
		String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
		SessionUtils.setCollectionAttribute(
				LOAN_ALL_ACTIVITY_VIEW, loanBusinessService
						.getAllActivityView(globalAccountNum, getUserContext(
								request).getLocaleId()), request);
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
		String customerId = request.getParameter(CUSTOMER_ID);
		SessionUtils.removeAttribute(BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.findBySystemId(request
				.getParameter(GLOBAL_ACCOUNT_NUM));
		if (customerId == null)
			customerId = loanBO.getCustomer().getCustomerId().toString();
		CustomerBO customer = null;
		if (null != customerId)
			customer = getCustomer(Integer.valueOf(customerId));

		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LOAN_INDIVIDUAL_MONITORING_IS_ENABLED)
				.getValue();

		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			SessionUtils.setAttribute(
					LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer.isGroup()) {
				SessionUtils.setAttribute(
						LOANACCOUNTOWNERISAGROUP, "yes", request);
			}
		}
		
		SessionUtils
				.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES,
						masterDataService.retrieveMasterEntities(
								MasterConstants.LOAN_PURPOSES, getUserContext(
										request).getLocaleId()), request);

		if (null != loanIndividualMonitoringIsEnabled
				&& 0 != loanIndividualMonitoringIsEnabled.intValue()
				&& customer.isGroup()) {

			List<LoanBO> individualLoans = loanBusinessService
					.findIndividualLoans(Integer.valueOf(loanBO.getAccountId())
							.toString());

			List<LoanAccountDetailsViewHelper> loanAccountDetailsViewList = new ArrayList<LoanAccountDetailsViewHelper>();

			for (LoanBO individualLoan : individualLoans) {
				LoanAccountDetailsViewHelper loandetails = new LoanAccountDetailsViewHelper();
				loandetails.setClientId(individualLoan.getCustomer()
						.getGlobalCustNum());
				loandetails.setClientName(individualLoan.getCustomer()
						.getDisplayName());
				loandetails
						.setLoanAmount(null != individualLoan.getLoanAmount()
								&& !EMPTY.equals(individualLoan.getLoanAmount().toString()) ? individualLoan
								.getLoanAmount().getAmountDoubleValue()
								: Double.valueOf(0.0));

				if (null != individualLoan.getBusinessActivityId()) {
					loandetails.setBusinessActivity(individualLoan
							.getBusinessActivityId().toString());


					List<BusinessActivityEntity> businessActEntity = (List<BusinessActivityEntity>) SessionUtils
							.getAttribute("BusinessActivities", request);

					for (ValueListElement busact : businessActEntity) {
						if (busact.getId().toString().equals(
								individualLoan.getBusinessActivityId()
										.toString())) {
							loandetails.setBusinessActivityName(busact
									.getName());
						}
					}
				}
				String governmentId = clientBusinessService.getClient(
						individualLoan.getCustomer().getCustomerId())
						.getGovernmentId();
				loandetails.setGovermentId(!StringUtils
						.isNullOrEmpty(governmentId) ? governmentId : "-");
				loanAccountDetailsViewList.add(loandetails);
			}
			SessionUtils.setAttribute(CUSTOMER_ID, customerId,
					request);
			SessionUtils.setCollectionAttribute("loanAccountDetailsView",
					loanAccountDetailsViewList, request);
		}

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
		List<SurveyInstance> surveys = surveysPersistence
				.retrieveInstancesByAccount(loanBO);
		boolean activeSurveys = surveysPersistence
				.retrieveSurveysByTypeAndState(SurveyType.LOAN,
						SurveyState.ACTIVE).size() > 0;
		request.setAttribute(CustomerConstants.SURVEY_KEY, surveys);
		request.setAttribute(CustomerConstants.SURVEY_COUNT, activeSurveys);
		request.setAttribute(AccountConstants.SURVEY_KEY, surveys);

		Integer administrativeDocumentsIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						ADMINISTRATIVE_DOCUMENT_IS_ENABLED)
				.getValue();

		if (null != administrativeDocumentsIsEnabled
				&& administrativeDocumentsIsEnabled.intValue() == 1) {
			SessionUtils.setCollectionAttribute(
					AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSLIST,
					new AdminDocumentPersistence().getAllAdminDocuments(),
					request);

			SessionUtils
					.setCollectionAttribute(
							AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSACCSTATEMIXLIST,
							new AdminDocAccStateMixPersistence()
									.getAllMixedAdminDocuments(), request);

		}

		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getLoanRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanBO loanBO = loanBusinessService.getAccount((getIntegerValue(request
				.getParameter(ACCOUNT_ID))));
		loanBO.setUserContext(getUserContext(request));
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
		return mapping.findForward(ActionForwards.getLoanRepaymentSchedule
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward viewStatusHistory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		loanBusinessService.initialize(loanBO.getAccountStatusChangeHistory());
		loanBO.setUserContext(getUserContext(request));
		List<AccountStatusChangeHistoryEntity> accStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>(
				loanBO.getAccountStatusChangeHistory());
		SessionUtils.setCollectionAttribute(STATUS_HISTORY,
				accStatusChangeHistory, request);
		loanBO = null;
		return mapping.findForward(ActionForwards.viewStatusHistory.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
		String perspective = loanAccountForm.getPerspective();
		if (perspective != null) {
			request.setAttribute(PERSPECTIVE, perspective);
		}
		ActionForwards actionForward = null;
		String method = (String) request.getAttribute(METHODCALLED);
		if (method.equals(Methods.getPrdOfferings.toString()))
			actionForward = ActionForwards.getPrdOfferigs_failure;
		else if (method.equals(Methods.load.toString()))
			actionForward = ActionForwards.getPrdOfferigs_success;
		else if (method.equals(Methods.schedulePreview.toString()))
			actionForward = ActionForwards.load_success;
		else if (method.equals(Methods.managePreview.toString()))
			actionForward = ActionForwards.managepreview_failure;
		else if (method.equals(Methods.preview.toString()))
			actionForward = ActionForwards.preview_failure;
		return mapping.findForward(actionForward.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward getPrdOfferings(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("Inside getPrdOfferings method");
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		CustomerBO customer = getCustomer(loanActionForm.getCustomerIdValue());
		List<LoanOfferingBO> loanOfferings = loanPrdBusinessService
				.getApplicablePrdOfferings(customer.getCustomerLevel());

		removePrdOfferingsNotMachingCustomerMeeting(loanOfferings, customer);
		SessionUtils.setCollectionAttribute(LOANPRDOFFERINGS, loanOfferings,
				request);
		SessionUtils.setAttribute(LOANACCOUNTOWNER, customer, request);
		SessionUtils.setAttribute(PROPOSEDDISBDATE, customer
				.getCustomerAccount().getNextMeetingDate(), request);
		if (request.getParameter(PERSPECTIVE) != null) {
			request.setAttribute(PERSPECTIVE, request
					.getParameter(PERSPECTIVE));
		}
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LOAN_INDIVIDUAL_MONITORING_IS_ENABLED)
				.getValue();
		Integer repaymentIndepOfMeetingIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED)
				.getValue();
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			SessionUtils.setAttribute(
					LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			request.setAttribute(METHODCALLED, "getPrdOfferings");
			if (customer.isGroup()) {
				SessionUtils.setAttribute(
						LOANACCOUNTOWNERISAGROUP, "yes", request);
				SessionUtils.setCollectionAttribute(
						MasterConstants.BUSINESS_ACTIVITIES,
						masterDataService
								.retrieveMasterEntities(
										MasterConstants.LOAN_PURPOSES,
										getUserContext(request).getLocaleId()),
						request);

				List<ClientBO> clients = clientBusinessService
						.getActiveClientsUnderGroup(customer.getCustomerId()
								.shortValue());
				if (clients == null || clients.size() == 0) {
					throw new ApplicationException(
							GroupConstants.IMPOSSIBLETOCREATEGROUPLOAN);
				}

				setClientDetails(loanActionForm, clients);
				SessionUtils.setCollectionAttribute(CLIENT_LIST,
						clients, request);
				SessionUtils.setAttribute("clientListSize", clients.size(),
						request);

			}
		}


		if (null != repaymentIndepOfMeetingIsEnabled
				&& repaymentIndepOfMeetingIsEnabled.intValue() != 0) {
			SessionUtils
					.setAttribute(
							REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED,
							repaymentIndepOfMeetingIsEnabled.intValue(),
							request);
			SessionUtils.setAttribute(LOANACCOUNTOWNERISACLIENT,
					"yes", request);
			loadDataForRepaymentDate(request);
			loanActionForm.setRecurMonth(customer.getCustomerMeeting()
					.getMeeting().getMeetingDetails().getRecurAfter()
					.toString());
		}


		return mapping.findForward(ActionForwards.getPrdOfferigs_success
				.toString());
	}

	private void setClientDetails(LoanAccountActionForm loanActionForm,
			List<ClientBO> clients) {
		logger.debug("inside setClientDetails method");
		List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
		if (clients != null && clients.size() > 0) {
			for (ClientBO client : clients) {
				LoanAccountDetailsViewHelper clientDetail = new LoanAccountDetailsViewHelper();
				clientDetail
						.setClientId(getStringValue(client.getCustomerId()));
				clientDetail.setClientName(client.getDisplayName());

				clientDetails.add(clientDetail);
			}
		}
		loanActionForm.setClientDetails(clientDetails);
		logger.debug("outside setClientDetails method");
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
		loadCreateMasterData(loanActionForm, request);
		RecurrenceType recurrenceType = loanOffering.getLoanOfferingMeeting()
				.getMeeting().getMeetingDetails().getRecurrenceTypeEnum();
		SessionUtils.setAttribute(RECURRENCEID, recurrenceType
				.getValue(), request);
		CustomerBO customerBO = (CustomerBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		if (customerBO == null) {
			customerBO = (CustomerBO) SessionUtils.getAttribute(
					LOANACCOUNTOWNER, request);

		}
		MeetingDetailsEntity meetingDetails = customerBO.getCustomerMeeting()
				.getMeeting().getMeetingDetails();
		if (recurrenceType == RecurrenceType.WEEKLY)
			loanActionForm.setMonthWeek(meetingDetails.getWeekDay().getValue()
					.toString());
		if (recurrenceType == RecurrenceType.MONTHLY
				&& meetingDetails.getWeekRank() != null)
			loanActionForm.setMonthRank(meetingDetails.getWeekRank().getValue()
					.toString());

		SessionUtils.removeAttribute(LOANOFFERING, request);
		SessionUtils.setAttribute(LOANOFFERING, loanOffering,
				request);
		SessionUtils.setCollectionAttribute(LOANFUNDS,
				getFunds(loanOffering), request);
		if (request.getParameter(PERSPECTIVE) != null) {
			request.setAttribute(PERSPECTIVE, request
					.getParameter(PERSPECTIVE));
		}
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward redoLoanBegin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping
				.findForward(ActionForwards.beginRedoLoanDisbursal_success
						.toString());
	}

	private void loadCreateMasterData(LoanAccountActionForm actionForm,
			HttpServletRequest request) throws Exception {
		loadMasterData(request);
		loadCreateCustomFields(actionForm, request);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward schedulePreview(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		CustomerBO customer = getCustomer(request);
		Integer loanIndividualMonitoringIsEnabled = (Integer) SessionUtils
				.getAttribute(LOANINDIVIDUALMONITORINGENABLED,
						request);

		if (null != loanIndividualMonitoringIsEnabled
				&& 0 != loanIndividualMonitoringIsEnabled.intValue()
				&& customer.isGroup()) {
			SessionUtils.setAttribute(
					LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			List<String> ids_clients_selected = loanActionForm.getClients();
			List<LoanAccountDetailsViewHelper> listdetail = loanActionForm
					.getClientDetails();

			double totalAmout = new Double(0);
			for (LoanAccountDetailsViewHelper tempAccount : listdetail) {
				if (ids_clients_selected.contains(tempAccount.getClientId())) {
					if (tempAccount.getLoanAmount() != null) {
						totalAmout = totalAmout
								+ tempAccount.getLoanAmount().doubleValue();
					}
				}

			}
			loanActionForm.setLoanAmount(Double.toString(totalAmout));
		}

		LoanBO loan = constructLoan(loanActionForm, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		List<RepaymentScheduleInstallment> installments = getLoanSchedule(loan);
		SessionUtils.setCollectionAttribute(
				REPAYMENTSCHEDULEINSTALLMENTS, installments,
				request);
		String perspective = request.getParameter(PERSPECTIVE);
		if (perspective != null) {
			request.setAttribute(PERSPECTIVE, request
					.getParameter(PERSPECTIVE));
		}
		loanActionForm.initializeTransactionFields(getUserContext(request),
				installments);

		boolean isPendingApprovalDefined = ProcessFlowRules
				.isLoanPendingApprovalStateEnabled();
		SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
				isPendingApprovalDefined, request);
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		Integer repaymentIndepOfMeetingIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED)
				.getValue();
		if (null != repaymentIndepOfMeetingIsEnabled
				&& 1 == repaymentIndepOfMeetingIsEnabled.intValue()) {
			checkIntervalBetweenTwoDates(getTheFirstRepaymentDay(installments), loanActionForm
					.getDisbursementDateValue(getUserContext(request)
							.getPreferredLocale()));
		}
		return mapping.findForward(ActionForwards.schedulePreview_success
				.toString());
	}
	private static boolean checkIntervalBetweenTwoDates(
			Date firstRepaymentDate, Date disbursementDate) throws PersistenceException, AccountException {
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		Integer minDaysInterval = configurationPersistence
				.getConfigurationKeyValueInteger(MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY)
				.getValue();
		Integer maxDaysInterval = configurationPersistence
				.getConfigurationKeyValueInteger(MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY)
				.getValue();
		if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils
				.getDateWithoutTimeStamp(firstRepaymentDate),
				DateUtils.getDateWithoutTimeStamp(disbursementDate)) < minDaysInterval) {
			throw new AccountException(MIN_RANGE_IS_NOT_MET,
					new String[] { minDaysInterval.toString() });

		}
		else if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils
				.getDateWithoutTimeStamp(firstRepaymentDate),
				DateUtils.getDateWithoutTimeStamp(disbursementDate)) > maxDaysInterval) {
			throw new AccountException(MAX_RANGE_IS_NOT_MET,
					new String[] { maxDaysInterval.toString() });

		}
		
		return true;
	}

	private Date getTheFirstRepaymentDay(List<RepaymentScheduleInstallment> installments) {
		for (Iterator<RepaymentScheduleInstallment> iter = installments
				.iterator(); iter.hasNext();) {
			return iter.next().getDueDate();
		}
		return null;

	}

	private CustomerBO getCustomer(HttpServletRequest request)
			throws PageExpiredException, ServiceException {
		CustomerBO oldCustomer = (CustomerBO) SessionUtils.getAttribute(
				LOANACCOUNTOWNER, request);
		CustomerBO customer = getCustomer(oldCustomer.getCustomerId());
		customer.getPersonnel().getDisplayName();
		customer.getOffice().getOfficeName();
		// TODO: I'm not sure why we're resetting version number - need to investigate this
		customer.setVersionNo(oldCustomer.getVersionNo());
		return customer;
	}

	private LoanBO constructLoan(LoanAccountActionForm loanActionForm,
			HttpServletRequest request) throws AccountException,
			ServiceException, PageExpiredException, PersistenceException, NumberFormatException, MeetingException {
		LoanOfferingBO loanOffering = loanPrdBusinessService.getLoanOffering(
				((LoanOfferingBO) SessionUtils.getAttribute(
						LOANOFFERING, request))
						.getPrdOfferingId(), getUserContext(request)
						.getLocaleId());
		String perspective = request.getParameter(PERSPECTIVE);
		CustomerBO customer = getCustomer(request);
		LoanBO loan;
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		MeetingBO newMeetingForRepaymentDay = null;
        Integer repIndepOfMeetingEnabled = configurationPersistence.getConfigurationKeyValueInteger(REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED).getValue();
        boolean isRepaymentIndepOfMeetingEnabled = !(repIndepOfMeetingEnabled == null || repIndepOfMeetingEnabled == 0);
        
        if (isRepaymentIndepOfMeetingEnabled){
        	RecurrenceType recurrenceType = loanOffering.getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurrenceTypeEnum();
        	if(recurrenceType == RecurrenceType.WEEKLY)
        		newMeetingForRepaymentDay = new MeetingBO(recurrenceType.getValue(), Short.valueOf(loanActionForm.getMonthWeek()), 
        				Short.valueOf(loanActionForm.getRecurMonth()), loanActionForm.getDisbursementDateValue(getUserContext(request).getPreferredLocale()), 
        				MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());    	
        	else if(recurrenceType == RecurrenceType.MONTHLY)
        		newMeetingForRepaymentDay = new MeetingBO(Short.valueOf(loanActionForm.getMonthWeek()), Short.valueOf(loanActionForm.getRecurMonth()), 
        				loanActionForm.getDisbursementDateValue(getUserContext(request).getPreferredLocale()), 
        				MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace(),
        				Short.valueOf(loanActionForm.getMonthRank()));
        }
        

  		if (perspective != null && perspective.equalsIgnoreCase("redoloan")) {
			loan = LoanBO.redoLoan(getUserContext(request), loanOffering,
					customer, AccountState.LOAN_PARTIAL_APPLICATION,
					loanActionForm.loanAmountValue(), loanActionForm
							.getNoOfInstallmentsValue(), loanActionForm
							.getDisbursementDateValue(getUserContext(request)
									.getPreferredLocale()), loanActionForm
							.isInterestDedAtDisbValue(), loanActionForm
							.getInterestDoubleValue(), loanActionForm
							.getGracePeriodDurationValue(), getFund(request,
							loanActionForm.getLoanOfferingFundValue()),
					loanActionForm.getFeesToApply(), loanActionForm
							.getCustomFields(),isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
		}
		else {
				loan = LoanBO.createLoan(getUserContext(request), loanOffering,
					customer, AccountState.LOAN_PARTIAL_APPLICATION,
					loanActionForm.loanAmountValue(), loanActionForm
									.getNoOfInstallmentsValue(),
							loanActionForm
									.getDisbursementDateValue(getUserContext(
											request).getPreferredLocale()),
							loanActionForm.isInterestDedAtDisbValue(),
							loanActionForm.getInterestDoubleValue(),
							loanActionForm.getGracePeriodDurationValue(),
							getFund(request, loanActionForm
									.getLoanOfferingFundValue()),
							loanActionForm.getFeesToApply(),
							loanActionForm.getCustomFields(), 
							loanActionForm.getMinLoanAmountValue(),
							loanActionForm.getMaxLoanAmountValue(),
							loanActionForm.getMinNoInstallmentsValue(),
							loanActionForm.getMaxNoInstallmentsValue(),
					isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
		}
		loan.setBusinessActivityId(loanActionForm.getBusinessActivityIdValue());
		loan.setCollateralNote(loanActionForm.getCollateralNote());
		loan.setCollateralTypeId(loanActionForm.getCollateralTypeIdValue());
		return loan;
	}

	private LoanBO redoLoan(LoanAccountActionForm loanActionForm,
			HttpServletRequest request) throws PageExpiredException,
			AccountException, ServiceException, PersistenceException, NumberFormatException, MeetingException {
		LoanBO loan = constructLoan(loanActionForm, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);

		loan.changeStatus(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, null,
				"Automatic Status Update (Redo Loan)");

		PersonnelBO personnel = null;
		try {
			personnel = new PersonnelPersistence().getPersonnel(getUserContext(
					request).getId());
		}
		catch (PersistenceException e) {
			throw new IllegalStateException(e);
		}

		// We're assuming cash disbursal for this situation right now
		loan.disburseLoan(personnel, Short.valueOf((short) 1), false);

		List<PaymentDataHtmlBean> paymentDataBeans = loanActionForm
				.getPaymentDataBeans();
		PaymentData payment;
		CustomerBO customer = getCustomer(request);
		try {
			for (PaymentDataTemplate template : paymentDataBeans) {
				if (template.getTotalAmount() != null
						&& template.getTransactionDate() != null) {
					if (!customer.getCustomerMeeting().getMeeting()
							.isValidMeetingDate(template.getTransactionDate(),
									DateUtils.getLastDayOfNextYear())) {
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
			throws PageExpiredException, AccountException, CustomerException,
			ServiceException, PersistenceException, NumberFormatException, MeetingException {
		LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
		String perspective = loanAccountForm.getPerspective();
		if (perspective != null) {
			if (perspective.equals(PERSPECTIVE_VALUE_REDO_LOAN)) {
				LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
				LoanBO loan = redoLoan(loanActionForm, request);
				SessionUtils
						.setAttribute(Constants.BUSINESS_KEY, loan, request);
			}

			request.setAttribute(PERSPECTIVE, perspective);

			ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
			CustomerBO customer = getCustomer(loanAccountForm
					.getCustomerIdValue());
			Integer loanIndividualMonitoringIsEnabled;
			try {
				loanIndividualMonitoringIsEnabled = configurationPersistence
						.getConfigurationKeyValueInteger(
								LOAN_INDIVIDUAL_MONITORING_IS_ENABLED)
						.getValue();
			}
			catch (PersistenceException e) {
				throw new RuntimeException(e);
			}
			if (null != loanIndividualMonitoringIsEnabled
					&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
				SessionUtils.setAttribute(
						LOANINDIVIDUALMONITORINGENABLED,
						loanIndividualMonitoringIsEnabled.intValue(), request);
				if (customer.getCustomerLevel().isGroup()) {
					SessionUtils.setAttribute(
							LOANACCOUNTOWNERISAGROUP, "yes",
							request);
				}
			}

			if (perspective != null) {
				request.setAttribute(PERSPECTIVE, perspective);

				if (null != loanIndividualMonitoringIsEnabled
						&& loanIndividualMonitoringIsEnabled.intValue() != 0
						&& customer.getCustomerLevel().isGroup()) {

					List<String> ids_clients_selected = loanAccountForm
							.getClients();

					List<LoanAccountDetailsViewHelper> loanAccountDetailsView = new ArrayList<LoanAccountDetailsViewHelper>();
					List<LoanAccountDetailsViewHelper> listdetail = loanAccountForm
							.getClientDetails();
					for (String index : ids_clients_selected) {
						if (isNotEmpty(index)) {
							LoanAccountDetailsViewHelper tempLoanAccount = new LoanAccountDetailsViewHelper();
							ClientBO clt = clientBusinessService
									.getClient(getIntegerValue(index));
							LoanAccountDetailsViewHelper account = null;
							for (LoanAccountDetailsViewHelper tempAccount : listdetail) {
								if (tempAccount.getClientId().equals(index)) {
									account = tempAccount;
								}
							}
							tempLoanAccount.setClientId(clt.getGlobalCustNum()
									.toString());
							tempLoanAccount.setClientName(clt.getDisplayName());
							tempLoanAccount.setLoanAmount(((null != account
									.getLoanAmount() && !EMPTY.equals(account
									.getLoanAmount().toString())) ? account
									.getLoanAmount() : Double.valueOf(0.0)));

							List<BusinessActivityEntity> businessActEntity = (List<BusinessActivityEntity>) SessionUtils
									.getAttribute("BusinessActivities", request);

							String businessActName = null;
							for (ValueListElement busact : businessActEntity) {


								if (busact.getId().toString().equals(
										account.getBusinessActivity())) {
									businessActName = busact.getName();

								}
							}
							tempLoanAccount.setBusinessActivity(account
									.getBusinessActivity());
							tempLoanAccount
									.setBusinessActivityName((!StringUtils
											.isNullOrEmpty(businessActName) ? businessActName
											: "-").toString());
							tempLoanAccount.setGovermentId((!StringUtils
									.isNullOrEmpty(clt.getGovernmentId()) ? clt
									.getGovernmentId() : "-").toString());
							loanAccountDetailsView.add(tempLoanAccount);
						}
					}
					SessionUtils.setCollectionAttribute(
							"loanAccountDetailsView", loanAccountDetailsView,
							request);
				}
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
		
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		Integer repaymentIndepOfMeetingIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED)
				.getValue();
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		String perspective = loanActionForm.getPerspective();
		if (perspective != null) {
			request.setAttribute(PERSPECTIVE, perspective);
		}
		CustomerBO customer = getCustomer(((CustomerBO) SessionUtils.getAttribute(
						LOANACCOUNTOWNER, request)).getCustomerId());
		LoanBO loan;
		Short recurrenceId = (Short) SessionUtils.getAttribute(RECURRENCEID,request);
		if (perspective != null
				&& perspective.equals(PERSPECTIVE_VALUE_REDO_LOAN)) {
			loan = redoLoan(loanActionForm, request);
			SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
			if (null != repaymentIndepOfMeetingIsEnabled
					&& repaymentIndepOfMeetingIsEnabled.intValue() != 0) {

				if (recurrenceId == RecurrenceType.MONTHLY.getValue())
					loan
							.setMonthRank((RankOfDaysEntity) new MasterPersistence()
									.retrieveMasterEntity(Short
											.valueOf(loanActionForm
													.getMonthRank()),
											RankOfDaysEntity.class, null));

				loan.setMonthWeek((WeekDaysEntity) new MasterPersistence()
						.retrieveMasterEntity(Short.valueOf(loanActionForm
								.getMonthWeek()), WeekDaysEntity.class, null));
				loan.setRecurMonth(Short
						.valueOf(loanActionForm.getRecurMonth()));

			}
			loan.save();
		}
		else {
			checkPermissionForCreate(loanActionForm.getState().getValue(),
					getUserContext(request), null, customer.getOffice()
							.getOfficeId(), customer.getPersonnel()
							.getPersonnelId());
			loan = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,
					request);
			if (null != repaymentIndepOfMeetingIsEnabled
					&& repaymentIndepOfMeetingIsEnabled.intValue() != 0) {

				if (recurrenceId == RecurrenceType.MONTHLY.getValue())
					loan
							.setMonthRank((RankOfDaysEntity) new MasterPersistence()
									.retrieveMasterEntity(Short
											.valueOf(loanActionForm
													.getMonthRank()),
											RankOfDaysEntity.class, null));
				loan.setMonthWeek((WeekDaysEntity) new MasterPersistence()
						.retrieveMasterEntity(Short.valueOf(loanActionForm
								.getMonthWeek()), WeekDaysEntity.class, null));
				loan.setRecurMonth(Short
						.valueOf(loanActionForm.getRecurMonth()));
			}
			loan.save(loanActionForm.getState());

		}


		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		boolean isRepaymentIndepOfMeetingEnabled = (null != repaymentIndepOfMeetingIsEnabled
				&& 0 != repaymentIndepOfMeetingIsEnabled.intValue());
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0
				&& customer.getCustomerLevel().isGroup()) {

			List<LoanAccountDetailsViewHelper> loanAccountDetailsList = (List<LoanAccountDetailsViewHelper>) SessionUtils
					.getAttribute("loanAccountDetailsView", request);
			for (LoanAccountDetailsViewHelper loanAccountDetail : loanAccountDetailsList) {

				LoanBO individualLoan = LoanBO.createIndividualLoan(loan
						.getUserContext(), loan.getLoanOffering(),
						getCustomerBySystemId(loanAccountDetail.getClientId()),
						loanActionForm.getState(), new Money(loanAccountDetail
								.getLoanAmount().toString()), loan
								.getNoOfInstallments(), loan
								.getDisbursementDate(), false,
						isRepaymentIndepOfMeetingEnabled, loan
								.getInterestRate(), loan
								.getGracePeriodDuration(), loan.getFund(),
						new ArrayList<FeeView>(),
						new ArrayList<CustomFieldView>());

				individualLoan.setParentAccount(loan);

				if (null != loanAccountDetail.getBusinessActivity())
					individualLoan.setBusinessActivityId(Integer
							.valueOf(loanAccountDetail.getBusinessActivity()));

				individualLoan.save();

			}
		}

		loanActionForm.setAccountId(loan.getAccountId().toString());
		request.setAttribute("customer", customer);
		request.setAttribute(GLOBAL_ACCOUNT_NUM, loan.getGlobalAccountNum());
		request.setAttribute("loan", loan);
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		String globalAccountNum = request.getParameter(GLOBAL_ACCOUNT_NUM);

		String customerId = request.getParameter(CUSTOMER_ID);

		CustomerBO customer = null;
		if (isNotEmpty(customerId)) {
			customer = getCustomer(Integer.valueOf(customerId));
		}

		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LOAN_INDIVIDUAL_MONITORING_IS_ENABLED)
				.getValue();

		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			SessionUtils.setAttribute(
					LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer.isGroup()) {
				SessionUtils.setAttribute(
						LOANACCOUNTOWNERISAGROUP, "yes", request);
			}
		}

		if (null != loanIndividualMonitoringIsEnabled
				&& 0 != loanIndividualMonitoringIsEnabled.intValue()
				&& customer.isGroup()) {
			LoanBO loanBO = loanBusinessService
					.findBySystemId(globalAccountNum);
			List<LoanBO> individualLoans = loanBusinessService
					.findIndividualLoans(Integer.valueOf(loanBO.getAccountId())
							.toString());

			List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
			List<ClientBO> clients = new ArrayList<ClientBO>();

			for (LoanBO temploan : individualLoans) {
				LoanAccountDetailsViewHelper clientDetail = new LoanAccountDetailsViewHelper();
				clients.add(clientBusinessService.getClient(temploan
						.getCustomer().getCustomerId().intValue()));
				clientDetail.setClientId(getStringValue(temploan.getCustomer()
						.getCustomerId()));
				clientDetail.setClientName(temploan.getCustomer()
						.getDisplayName());
				clientDetail.setBusinessActivity(Integer.toString(
						temploan.getBusinessActivityId()));
				clientDetail
						.setLoanAmount(temploan.getLoanAmount() != null ? temploan
								.getLoanAmount().getAmountDoubleValue()
								: new Double(0));

				clientDetails.add(clientDetail);
			}

			loanActionForm.setClientDetails(clientDetails);
			SessionUtils.setCollectionAttribute(CLIENT_LIST,
					clients, request);

		}

		LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.getAccount(loanBOInSession
				.getAccountId());
		loanBO.setUserContext(getUserContext(request));
		SessionUtils.setAttribute(PROPOSEDDISBDATE, loanBO
				.getDisbursementDate(), request);

		SessionUtils.removeAttribute(LOANOFFERING, request);
		LoanOfferingBO loanOffering = getLoanOffering(loanBO.getLoanOffering()
				.getPrdOfferingId(), getUserContext(request).getLocaleId());
		loanActionForm.setInstallmentRange(loanBO.getMaxMinNoOfInstall());
		loanActionForm.setLoanAmountRange(loanBO.getMaxMinLoanAmount());
		SessionUtils.setAttribute(LOANOFFERING, loanOffering,
				request);
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
		LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		CustomerBO customer = getCustomer(loanAccountForm.getCustomerIdValue());
		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			
			SessionUtils.setAttribute(
					LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer.getCustomerLevel().isGroup()) {
				SessionUtils.setAttribute(
						LOANACCOUNTOWNERISAGROUP, "yes", request);
				

			}
		}

		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0
				&& customer.getCustomerLevel().isGroup()) {

			List<String> ids_clients_selected = loanAccountForm
					.getClients();

			List<LoanAccountDetailsViewHelper> loanAccountDetailsView = new ArrayList<LoanAccountDetailsViewHelper>();
			List<LoanAccountDetailsViewHelper> listdetail = loanAccountForm
					.getClientDetails();
			for (String index : ids_clients_selected) {
				if (null != index && !EMPTY.equals(index)) {
					LoanAccountDetailsViewHelper tempLoanAccount = new LoanAccountDetailsViewHelper();
					ClientBO clt = clientBusinessService
							.getClient(getIntegerValue(index));
					LoanAccountDetailsViewHelper account = null;
					for (LoanAccountDetailsViewHelper tempAccount : listdetail) {
						if (tempAccount.getClientId().equals(index)) {
							account = tempAccount;
						}
					}
					tempLoanAccount.setClientId(clt.getGlobalCustNum()
							.toString());
					tempLoanAccount.setClientName(clt.getDisplayName());
					tempLoanAccount.setLoanAmount(((null != account
							.getLoanAmount() && !"".equals(account
							.getLoanAmount())) ? account.getLoanAmount()
							: Double.valueOf(0.0)));

					
					List<BusinessActivityEntity>  businessActEntity=(List<BusinessActivityEntity>) SessionUtils.getAttribute(
							"BusinessActivities", request);
						
					
					for(ValueListElement busact:businessActEntity){
						
						if(busact.getId().toString().equals(account.getBusinessActivity()))
						{
							tempLoanAccount.setBusinessActivity(busact.getId().toString());
							tempLoanAccount.setBusinessActivityName(busact.getName());
						}
					}
					
					tempLoanAccount.setGovermentId((!StringUtils
							.isNullOrEmpty(clt.getGovernmentId()) ? clt
							.getGovernmentId() : "-").toString());
					loanAccountDetailsView.add(tempLoanAccount);
				}
			}
			SessionUtils.setCollectionAttribute("loanAccountDetailsView",
					loanAccountDetailsView, request);
		}
	
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		SessionUtils.removeAttribute(MasterConstants.BUSINESS_ACTIVITIE_NAME,
				request);
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
		checkVersionMismatch(loanBOInSession.getVersionNo(), loanBO
				.getVersionNo());
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
						.getCollateralNote(), loanAccountActionForm.getCollateralTypeIdValue(),
						loanAccountActionForm.getCustomFields());		
	      
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		
		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		  
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0
				&& loanBO.getCustomer().getCustomerLevel().isGroup()) {
		
		List<LoanAccountDetailsViewHelper> loanAccountDetailsList= (List<LoanAccountDetailsViewHelper>) SessionUtils.getAttribute("loanAccountDetailsView",request);
		
        for (LoanAccountDetailsViewHelper loanAccountDetail : loanAccountDetailsList) {
        	
        	loanBO.updateLoan(
        			new Money(!loanAccountDetail
        					.getLoanAmount().toString().equals("-")?loanAccountDetail
					.getLoanAmount().longValue()+"":"0"),
    				!loanAccountDetail.getBusinessActivity().equals("-")?Integer.valueOf(loanAccountDetail.getBusinessActivity()):0);
       	request.setAttribute(CUSTOMER_ID,loanBO.getCustomer().getCustomerId().toString());

		}
    }

		
		loanBOInSession = null;
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loanBO, request);
		
		
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private void setLocaleForMasterEntities(LoanBO loanBO, Short localeId) {
		if (loanBO.getGracePeriodType() != null) {
			// Is this locale ever consulted?  I don't see a place...
			loanBO.getGracePeriodType().setLocaleId(localeId);
		}
		loanBO.getInterestType().setLocaleId(localeId);
		loanBO.getAccountState().setLocaleId(localeId);
		for (AccountFlagMapping accountFlagMapping : loanBO.getAccountFlags()) {
			accountFlagMapping.getFlag().setLocaleId(localeId);
		}
	}

	private void loadLoanDetailPageInfo(LoanBO loanBO,
			HttpServletRequest request) throws Exception {
		SessionUtils
				.setCollectionAttribute(RECENTACCOUNTACTIVITIES,
						loanBusinessService.getRecentActivityView(loanBO
								.getGlobalAccountNum(), getUserContext(request)
								.getLocaleId()), request);
		SessionUtils
				.setAttribute(AccountConstants.LAST_PAYMENT_ACTION,
						loanBusinessService.getLastPaymentAction(loanBO
								.getAccountId()), request);
		SessionUtils.setCollectionAttribute(NOTES, loanBO
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
		return loanPrdBusinessService.getLoanOffering(loanOfferingId, localeId);
	}

	private void setDataIntoForm(LoanOfferingBO loanOffering,
			LoanAccountActionForm loanAccountActionForm,
			HttpServletRequest request) throws Exception {
		updateForm(loanOffering, loanAccountActionForm);
		loanAccountActionForm.setInterestRate(getStringValue(loanOffering
				.getDefInterestRate()));
		loanAccountActionForm.setIntDedDisbursement(getStringValue(loanOffering
				.isIntDedDisbursement()));
		loanAccountActionForm
				.setGracePeriodDuration(getStringValue(loanOffering
						.getGracePeriodDuration()));
		loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(
				getUserContext(request).getPreferredLocale(), SessionUtils
						.getAttribute(PROPOSEDDISBDATE, request)
						.toString()));
	}

	private void updateForm(LoanOfferingBO loanOffering,
			LoanAccountActionForm loanAccountActionForm) throws Exception {
		CustomerBO customer = getCustomer(loanAccountActionForm
				.getCustomerIdValue());
		LoanAmountOption eligibleLoanAmount = loanOffering.eligibleLoanAmount(
				customer.getMaxLoanAmount(loanOffering),
				customer.getMaxLoanCycleForProduct(loanOffering));
		loanAccountActionForm.setLoanAmountRange(eligibleLoanAmount);
		loanAccountActionForm.setLoanAmount(getStringValue(eligibleLoanAmount
				.getDefaultLoanAmount()));
		LoanOfferingInstallmentRange eligibleNoOfInstall = loanOffering
				.eligibleNoOfInstall(customer.getMaxLoanAmount(loanOffering),
						customer.getMaxLoanCycleForProduct(loanOffering));
		loanAccountActionForm.setInstallmentRange(eligibleNoOfInstall);
		loanAccountActionForm
				.setNoOfInstallments(getStringValue(eligibleNoOfInstall
						.getDefaultNoOfInstall()));
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
		LoanOfferingBO loanOffering = loanPrdBusinessService
				.getLoanOffering(loanOfferingId);
		List<FeeBO> fees = feeService.getAllAppllicableFeeForLoanCreation();
		List<FeeView> additionalFees = new ArrayList<FeeView>();
		List<FeeView> defaultFees = new ArrayList<FeeView>();
		for (FeeBO fee : fees) {
			if (!fee.isPeriodic()
					|| (isMeetingMatched(fee.getFeeFrequency()
							.getFeeMeetingFrequency(), loanOffering
							.getLoanOfferingMeeting().getMeeting()))) {
				FeeView feeView = new FeeView(getUserContext(request), fee);
				if (loanOffering.isFeePresent(fee))
					defaultFees.add(feeView);
				else additionalFees.add(feeView);
			}
		}
		actionForm.setDefaultFees(defaultFees);
		SessionUtils.setCollectionAttribute(ADDITIONAL_FEES_LIST,
				additionalFees, request);
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		// Retrieve and set into the session all collateral types from the 
		// lookup_value_locale table associated with the current user context locale
		SessionUtils.setCollectionAttribute(MasterConstants.COLLATERAL_TYPES,
				new MasterPersistence().getLookUpEntity(
						MasterConstants.COLLATERAL_TYPES,
						getUserContext(request).getLocaleId())
						.getCustomValueListElements(), request);

		SessionUtils
				.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES,
						masterDataService
								.retrieveMasterEntities(
										MasterConstants.LOAN_PURPOSES,
										getUserContext(request).getLocaleId()),
						request);
	}

	private String getNameForBusinessActivityEntity(Integer entityId,
			Short localeId) throws Exception {
		if (entityId != null)
			return masterDataService
					.retrieveMasterEntities(entityId, localeId);
		return "";
	}

	private FundBO getFund(HttpServletRequest request, Short fundId)
			throws PageExpiredException {
		List<FundBO> funds = (List<FundBO>) SessionUtils.getAttribute(
				LOANFUNDS, request);
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

//	private MasterDataEntity findMasterEntity(HttpServletRequest request,
//			String collectionName, Short value) throws PageExpiredException {
//		List<MasterDataEntity> entities = (List<MasterDataEntity>) SessionUtils
//				.getAttribute(collectionName, request);
//		for (MasterDataEntity entity : entities)
//			if (entity.getId().equals(value))
//				return entity;
//		return null;
//	}

	private void setFormAttributes(LoanBO loan, ActionForm form,
			HttpServletRequest request) throws Exception {
		LoanAccountActionForm loanAccountActionForm = (LoanAccountActionForm) form;
		loanAccountActionForm.setStateSelected(getStringValue(loan
				.getAccountState().getId()));
		loanAccountActionForm
				.setLoanAmount(getStringValue(loan.getLoanAmount()));

		java.util.Date proposedDisbursement = (Date) SessionUtils.getAttribute(
				PROPOSEDDISBDATE, request);
		loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(
				getUserContext(request).getPreferredLocale(), DateUtils
						.toDatabaseFormat(proposedDisbursement)));

		loanAccountActionForm.setIntDedDisbursement(loan
				.isInterestDeductedAtDisbursement() ? "1" : "0");
		loanAccountActionForm.setBusinessActivityId(getStringValue(loan
				.getBusinessActivityId()));
		if (loan.getCollateralTypeId() != null)
			loanAccountActionForm.setCollateralTypeId(getStringValue(loan.getCollateralTypeId()));		
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
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
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
			}
			else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}

	private void loadCustomFieldDefinitions(HttpServletRequest request)
			throws Exception {
		SessionUtils.setCollectionAttribute(CUSTOM_FIELDS,
				getAccountBizService().retrieveCustomFieldsDefinition(
						EntityType.LOAN), request);
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
								.getFieldId(), DateUtils.getUserLocaleDate(
								locale, customFieldEntity.getFieldValue()),
								customFieldDef.getFieldType()));
					}
					else {
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

	private void loadUpdateMasterData(HttpServletRequest request)
			throws Exception {
		loadMasterData(request);
		loadCustomFieldDefinitions(request);
	}

	private void loadDataForRepaymentDate(HttpServletRequest request) throws Exception {
		Short localeId = getUserContext(request).getLocaleId();
		SessionUtils.setCollectionAttribute(MeetingConstants.WEEKDAYSLIST,
				getMeetingBusinessService().getWorkingDays(), request);
		SessionUtils.setCollectionAttribute(MeetingConstants.WEEKRANKLIST,
				getMasterEntities(RankOfDaysEntity.class, localeId), request);
	}
	private MeetingBusinessService getMeetingBusinessService()
			throws ServiceException {
		return meetingBusinessService;
	}
}

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
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFlagMapping;
import org.mifos.application.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.application.accounts.business.ViewInstallmentDetails;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.MaxMinLoanAmount;
import org.mifos.application.accounts.loan.business.MaxMinNoOfInstall;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.application.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountDetailsViewHelper;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.admindocuments.persistence.AdminDocAccStateMixPersistence;
import org.mifos.application.admindocuments.persistence.AdminDocumentPersistence;
import org.mifos.application.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.business.LoanCounter;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.RankOfDaysEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.business.service.MeetingBusinessService;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
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
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
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
		security.allow("redoLoanBegin",
				SecurityConstants.CAN_REDO_LOAN_DISPURSAL);
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
		SessionUtils.setCollectionAttribute(
				LoanConstants.LOAN_ALL_ACTIVITY_VIEW, loanBusinessService
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
		
		String globalAccountNum = request.getParameter("globalAccountNum");
		String customerId = request.getParameter("customerId");
		if(customerId==null)
			customerId = (String)request.getAttribute("customerId");
		
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
		
		CustomerBO customer =null;
		if (null != customerId) 
			customer = getCustomer(Integer.valueOf(customerId));
		
	    ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        Integer loanIndividualMonitoringIsEnabled=configurationPersistence.getConfigurationKeyValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
      
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			SessionUtils.setAttribute(
					LoanConstants.LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer!=null && customer.getCustomerLevel().isGroup()) {
				SessionUtils.setAttribute(
						LoanConstants.LOANACCOUNTOWNERISAGROUP, "yes", request);
			}
		}
		SessionUtils
		.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES,
				((MasterDataService) ServiceFactory.getInstance()
						.getBusinessService(
								BusinessServiceName.MasterDataService))
						.retrieveMasterEntities(
								MasterConstants.LOAN_PURPOSES,
								getUserContext(request).getLocaleId()),
				request);

		if (null != loanIndividualMonitoringIsEnabled
				&& 0 != loanIndividualMonitoringIsEnabled.intValue()
				&& customer.getCustomerLevel().isGroup()) {
			
			List<LoanBO> individualLoans = loanBusinessService.findIndividualLoans(Integer.valueOf(loanBO.getAccountId()).toString());
			
			List<LoanAccountDetailsViewHelper> loanAccountDetailsViewList=new ArrayList<LoanAccountDetailsViewHelper>();
			
			for(LoanBO individualLoan:individualLoans){
								
				LoanAccountDetailsViewHelper loandetails = new LoanAccountDetailsViewHelper();
				loandetails.setClientName(individualLoan.getCustomer().getDisplayName());
				loandetails.setLoanAmount((null != individualLoan
						.getLoanAmount() && !"".equals(individualLoan
						.getLoanAmount())) ? individualLoan.getLoanAmount()
						.getAmountDoubleValue() : Double.valueOf(0.0));
				
				
				if(null!=individualLoan.getBusinessActivityId())
				{
					loandetails.setBusinessActivity(individualLoan
						.getBusinessActivityId().toString());
				
					
				List<BusinessActivityEntity>  businessActEntity=(List<BusinessActivityEntity>) SessionUtils.getAttribute(
						"BusinessActivities", request);
				
				for (ValueListElement busact : businessActEntity) {

						if (busact.getId().toString().equals(
								individualLoan.getBusinessActivityId()
										.toString())) {
							loandetails.setBusinessActivityName(busact
									.getName());
						}
					}

				
				}
				
				
				
				loandetails
						.setGovermentId((!StringUtils
								.isNullOrEmpty(((ClientBusinessService) ServiceFactory
										.getInstance().getBusinessService(
												BusinessServiceName.Client))
										.getClient(individualLoan
												.getCustomer().getCustomerId()).getGovernmentId()) ? ((ClientBusinessService) ServiceFactory
														.getInstance().getBusinessService(
																BusinessServiceName.Client))
														.getClient(individualLoan
																.getCustomer().getCustomerId()).getGovernmentId()
								: "-").toString());
				
				
				
				
				loanAccountDetailsViewList.add(loandetails);
			}
			SessionUtils.setAttribute("customerId",customerId, request);
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
						LoanConstants.ADMINISTRATIVE_DOCUMENT_IS_ENABLED)
				.getValue();
      
		if (null != administrativeDocumentsIsEnabled
				&& administrativeDocumentsIsEnabled.intValue() == 1) {
			SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSLIST,
					new AdminDocumentPersistence().getAllAdminDocuments(),request); 
			
			SessionUtils.setCollectionAttribute(AdminDocumentsContants.ADMINISTRATIVEDOCUMENTSACCSTATEMIXLIST,
					new AdminDocAccStateMixPersistence().getAllMixedAdminDocuments(),request);  
					
		}
			
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward getLoanRepaymentSchedule(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LoanBO loanBO = loanBusinessService.getAccount((getIntegerValue(request
				.getParameter("accountId"))));
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
		LoanAccountActionForm loanAccountForm = (LoanAccountActionForm) form;
		String perspective = loanAccountForm.getPerspective();
		if (perspective != null) {
			request.setAttribute("perspective", perspective);
		}

		ActionForwards actionForward = null;
		String method = (String) request.getAttribute("methodCalled");
		if (method.equals(Methods.getPrdOfferings.toString())
				|| method.equals(Methods.load.toString()))
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
		customer.getOffice().getOfficeId();
		customer.getPersonnel().getPersonnelId();

		// See commented out doCleanUp() method
		//doCleanUp(request.getSession());
		List<LoanOfferingBO> loanOfferings = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct))
				.getApplicablePrdOfferings(customer.getCustomerLevel());
		removePrdOfferingNotMatchingPrdType(loanOfferings, customer);
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
                ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
                Integer loanIndividualMonitoringIsEnabled=configurationPersistence.getConfigurationKeyValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
                Integer repaymentIndepOfMeetingIsEnabled=configurationPersistence.getConfigurationKeyValueInteger(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED).getValue();
                if ( null != loanIndividualMonitoringIsEnabled && loanIndividualMonitoringIsEnabled.intValue()!=0) {
                    SessionUtils.setAttribute(LoanConstants.LOANINDIVIDUALMONITORINGENABLED,
            				loanIndividualMonitoringIsEnabled.intValue(),request);
                    if (customer.getCustomerLevel().isGroup()) {
        				SessionUtils.setAttribute(
        						LoanConstants.LOANACCOUNTOWNERISAGROUP, "yes", request);
        				SessionUtils.setCollectionAttribute(
        						MasterConstants.BUSINESS_ACTIVITIES,
        						((MasterDataService) ServiceFactory.getInstance()
        								.getBusinessService(
        										BusinessServiceName.MasterDataService))
        								.retrieveMasterEntities(
        										MasterConstants.LOAN_PURPOSES,
        										getUserContext(request).getLocaleId()),
        						request);
        	
        				List<ClientBO> clients = new ClientBusinessService()
        				.getActiveClientsUnderGroup(customer.getCustomerId().shortValue());
        		if (clients == null || clients.size() == 0) {
        			throw new ApplicationException(LoanConstants.NOSEARCHRESULTS);
        		}
        				
        				setClientDetails(loanActionForm,clients);
        				SessionUtils.setCollectionAttribute(LoanConstants.CLIENT_LIST,
        						clients, request);
        
        			}
                }
              
              
                if ( null != repaymentIndepOfMeetingIsEnabled && repaymentIndepOfMeetingIsEnabled.intValue()!=0) {
                	 SessionUtils.setAttribute(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED,
                			 repaymentIndepOfMeetingIsEnabled.intValue(),request);
                		SessionUtils.setAttribute(
         						LoanConstants.LOANACCOUNTOWNERISACLIENT, "yes", request);
         				 loadDataForRepaymentDate(request);
         				 loanActionForm.setRecurMonth(customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter().toString());
         				
                	
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
		SessionUtils.removeAttribute(LoanConstants.LOANOFFERING, request);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
				request);
		SessionUtils.setCollectionAttribute(LoanConstants.LOANFUNDS,
				getFunds(loanOffering), request);
		if (request.getParameter("perspective") != null) {
			request.setAttribute("perspective", request
					.getParameter("perspective"));
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
		LoanOfferingBO loanOffering = ((LoanPrdBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.LoanProduct)).getLoanOffering(
				((LoanOfferingBO) SessionUtils.getAttribute(
						LoanConstants.LOANOFFERING, request))
						.getPrdOfferingId(), getUserContext(request)
						.getLocaleId()); 
		updateLoanOffering(loanOffering, loanActionForm);
	    LoanBO loan = constructLoan(loanActionForm, request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
		List<RepaymentScheduleInstallment> installments = getLoanSchedule(loan);
		SessionUtils.setCollectionAttribute(
				LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS, installments,
				request);
		String perspective = request.getParameter("perspective");
		if (perspective != null) {
			request.setAttribute("perspective", request
					.getParameter("perspective"));
		}
		loanActionForm.initializeTransactionFields(getUserContext(request),
				installments);

		boolean isPendingApprovalDefined = ProcessFlowRules
				.isLoanPendingApprovalStateEnabled();
		SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED,
				isPendingApprovalDefined, request);
	    ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
	    Integer repaymentIndepOfMeetingIsEnabled=configurationPersistence.getConfigurationKeyValueInteger(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED).getValue();
	       if (null != repaymentIndepOfMeetingIsEnabled
				&& 1 == repaymentIndepOfMeetingIsEnabled.intValue()) {
			checkIntervalBetweenTwoDates(getTheFirstRepaymentDay(installments,
					loanActionForm, loanActionForm
							.getDisbursementDateValue(getUserContext(request)
									.getPreferredLocale()), loanActionForm
							.getRecurMonth(), loanActionForm.getMonthWeek(),
					loanActionForm.getMonthRank(), request), loanActionForm
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
				.getConfigurationKeyValueInteger(
						LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY)
				.getValue();
		Integer maxDaysInterval = configurationPersistence
				.getConfigurationKeyValueInteger(
						LoanConstants.MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY)
				.getValue();
		if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils
				.getDateWithoutTimeStamp(firstRepaymentDate.getTime()),
				DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime())) <= minDaysInterval) {
			throw new AccountException(LoanConstants.MIN_RANGE_IS_NOT_MET,
					new String[] { minDaysInterval.toString() });

		}
		else if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils
				.getDateWithoutTimeStamp(firstRepaymentDate.getTime()),
				DateUtils.getDateWithoutTimeStamp(disbursementDate.getTime())) >= maxDaysInterval) {
			throw new AccountException(LoanConstants.MAX_RANGE_IS_NOT_MET,
					new String[] { maxDaysInterval.toString() });

		}
		
		return true;
	}

	public Date getTheFirstRepaymentDay(List<RepaymentScheduleInstallment> installments,LoanAccountActionForm form,java.sql.Date disbursementDateValue,
			String recurMonth, String monthWeek, String monthRank,
			HttpServletRequest request) {
	
    for (Iterator<RepaymentScheduleInstallment> iter = installments.iterator(); iter.hasNext();) {
			return iter.next().getDueDate();
		}
		return null;
	
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

	private LoanBO constructLoan(LoanAccountActionForm loanActionForm,
			HttpServletRequest request) throws AccountException,
			ServiceException, PageExpiredException, PersistenceException, NumberFormatException, MeetingException {
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
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        boolean isRepaymentIndepOfMeetingEnabled=false;
        MeetingBO newMeetingForRepaymentDay=null;
        Integer repIndepOfMeetingEnabled=configurationPersistence.getConfigurationKeyValueInteger(LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED).getValue();
        if (null != repIndepOfMeetingEnabled
				&& 0 != repIndepOfMeetingEnabled.intValue()){
        	isRepaymentIndepOfMeetingEnabled=true;
				newMeetingForRepaymentDay = new MeetingBO(Short.valueOf(loanActionForm.getMonthWeek()),Short.valueOf(loanActionForm.getRecurMonth()), loanActionForm.getDisbursementDateValue(getUserContext(request)
					.getPreferredLocale()),
					MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
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
							.getNoOfInstallmentsValue(), loanActionForm
							.getDisbursementDateValue(getUserContext(request)
									.getPreferredLocale()), loanActionForm
							.isInterestDedAtDisbValue(), loanActionForm
							.getInterestDoubleValue(), loanActionForm
							.getGracePeriodDurationValue(), getFund(request,
							loanActionForm.getLoanOfferingFundValue()),
					loanActionForm.getFeesToApply(), loanActionForm
							.getCustomFields(), getDoubleValue(loanOffering
							.getMaxLoanAmount().toString()),
					getDoubleValue(loanOffering.getMinLoanAmount().toString()),
					loanOffering.getMaxNoInstallments(), loanOffering
							.getMinNoInstallments(),isRepaymentIndepOfMeetingEnabled,newMeetingForRepaymentDay);
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
			request.setAttribute("perspective", perspective);

			if (perspective.equals(LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN)) {
				LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;

		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		CustomerBO customer = getCustomer(loanActionForm.getCustomerIdValue());
		Integer loanIndividualMonitoringIsEnabled;
		try {
			loanIndividualMonitoringIsEnabled = configurationPersistence
					.getConfigurationKeyValueInteger(
							LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		}
		catch (PersistenceException e) {
			throw new RuntimeException(e);
		}
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			SessionUtils.setAttribute(
					LoanConstants.LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer.getCustomerLevel().isGroup()) {
				SessionUtils.setAttribute(
						LoanConstants.LOANACCOUNTOWNERISAGROUP, "yes", request);
			}
		}

		if (perspective != null) {
			request.setAttribute("perspective", perspective);

			if (null != loanIndividualMonitoringIsEnabled
					&& loanIndividualMonitoringIsEnabled.intValue() != 0
					&& customer.getCustomerLevel().isGroup()) {
				
				List<String> ids_clients_selected = loanAccountForm
						.getClients();
				
				List<LoanAccountDetailsViewHelper> loanAccountDetailsView = new ArrayList<LoanAccountDetailsViewHelper>();
				List<LoanAccountDetailsViewHelper> listdetail = loanAccountForm
						.getClientDetails();
				for (String index : ids_clients_selected) {
					if (null != index && !index.equals("")) {
						LoanAccountDetailsViewHelper tempLoanAccount = new LoanAccountDetailsViewHelper();
						ClientBO clt = ((ClientBusinessService) ServiceFactory
								.getInstance().getBusinessService(
										BusinessServiceName.Client))
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
					
						String businessActName=null;
						for(ValueListElement busact:businessActEntity){
							
							
							if(busact.getId().toString().equals(account
										.getBusinessActivity()))
							{
								businessActName=busact.getName();
								
										
							}
						}
						tempLoanAccount
								.setBusinessActivity(account.getBusinessActivity());			
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
				SessionUtils.setCollectionAttribute("loanAccountDetailsView",
						loanAccountDetailsView, request);
			}
			if (perspective.equals(LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN)) {

				LoanBO loan = redoLoan(loanActionForm, request);
				SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan,
								request);
					}
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
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		String perspective = loanActionForm.getPerspective();
		if (perspective != null) {
			request.setAttribute("perspective", perspective);
		}
		CustomerBO customer = (CustomerBO) SessionUtils.getAttribute(
				LoanConstants.LOANACCOUNTOWNER, request);
		LoanBO loan;
        if (perspective != null &&
                perspective.equals(LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN)) {
        	loan = redoLoan(loanActionForm, request);
            SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request);
           
            ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
			Integer repaymentIndepOfMeetingIsEnabled = configurationPersistence
					.getConfigurationKeyValueInteger(
							LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED)
					.getValue();
			if (null != repaymentIndepOfMeetingIsEnabled
					&& repaymentIndepOfMeetingIsEnabled.intValue() != 0) {

				loan
						.setMonthRank((RankOfDaysEntity) new MasterPersistence()
								.retrieveMasterEntity(
										Short.valueOf(loanActionForm
												.getMonthRank()),
										RankOfDaysEntity.class, null));
				loan.setMonthWeek((WeekDaysEntity) new MasterPersistence()
						.retrieveMasterEntity(Short.valueOf(loanActionForm
								.getMonthWeek()), WeekDaysEntity.class, null));
				loan
						.setRecurMonth(Short.valueOf(loanActionForm
								.getRecurMonth()));
				
			}
            loan.save();
        }
        else {
            checkPermissionForCreate(loanActionForm.getState().getValue(),
                    getUserContext(request), null, customer.getOffice()
                            .getOfficeId(), customer.getPersonnel()
                            .getPersonnelId());
            loan = (LoanBO) SessionUtils.getAttribute(
                    Constants.BUSINESS_KEY, request);
            ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
			Integer repaymentIndepOfMeetingIsEnabled = configurationPersistence
					.getConfigurationKeyValueInteger(
							LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED)
					.getValue();
    					if (null != repaymentIndepOfMeetingIsEnabled
					&& repaymentIndepOfMeetingIsEnabled.intValue() != 0) {

				loan
						.setMonthRank((RankOfDaysEntity) new MasterPersistence()
								.retrieveMasterEntity(
										Short.valueOf(loanActionForm
												.getMonthRank()),
										RankOfDaysEntity.class, null));
				loan.setMonthWeek((WeekDaysEntity) new MasterPersistence()
						.retrieveMasterEntity(Short.valueOf(loanActionForm
								.getMonthWeek()), WeekDaysEntity.class, null));
				loan
						.setRecurMonth(Short.valueOf(loanActionForm
								.getRecurMonth()));
			}        
    		loan.save(loanActionForm.getState());
            
   		}
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		
		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0
				&& customer.getCustomerLevel().isGroup()) {
		List<LoanAccountDetailsViewHelper> loanAccountDetailsList= (List<LoanAccountDetailsViewHelper>) SessionUtils.getAttribute("loanAccountDetailsView",request);
        for (LoanAccountDetailsViewHelper loanAccountDetail : loanAccountDetailsList) {
        	LoanBO individualLoan = LoanBO.createIndividualLoan(loan
					.getUserContext(), loan.getLoanOffering(),
					getCustomerBySystemId(loanAccountDetail.getClientId()),
					loanActionForm.getState(), new Money(loanAccountDetail
					.getLoanAmount().toString()), loan
					.getNoOfInstallments(), loan.getDisbursementDate(),
					false, loan.getInterestRate(), loan
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
		request.setAttribute("globalAccountNum", loan.getGlobalAccountNum());
		request.setAttribute("loan", loan);
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanAccountActionForm loanActionForm = (LoanAccountActionForm) form;
		String globalAccountNum = request.getParameter("globalAccountNum");
		
		String customerId = request.getParameter("customerId");
		
		CustomerBO customer =null;
		if (null != customerId && !"".equals(customerId)) {
			customer = getCustomer(Integer.valueOf(customerId));
		}
		
		
        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        Integer loanIndividualMonitoringIsEnabled=configurationPersistence.getConfigurationKeyValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
      
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			SessionUtils.setAttribute(
					LoanConstants.LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer.getCustomerLevel().isGroup()) {
				SessionUtils.setAttribute(
						LoanConstants.LOANACCOUNTOWNERISAGROUP, "yes", request);
			}
		}


		if (null != loanIndividualMonitoringIsEnabled
				&& 0 != loanIndividualMonitoringIsEnabled.intValue()
				&& customer.getCustomerLevel().isGroup()) {
			
			
			
			LoanBO loanBO = loanBusinessService.findBySystemId(globalAccountNum);
			List<LoanBO> individualLoans = loanBusinessService.findIndividualLoans(Integer.valueOf(loanBO.getAccountId()).toString());
					
			List<LoanAccountDetailsViewHelper> clientDetails = new ArrayList<LoanAccountDetailsViewHelper>();
			List<ClientBO> clients=new ArrayList<ClientBO>() ;
			
				for (LoanBO temploan : individualLoans) {
					LoanAccountDetailsViewHelper clientDetail = new LoanAccountDetailsViewHelper();
					clients.add(new ClientBusinessService()
					.getClient(temploan.getCustomer().getCustomerId().intValue()));
					clientDetail
							.setClientId(getStringValue(temploan.getCustomer().getCustomerId()));
					clientDetail.setClientName(temploan.getCustomer().getDisplayName());
					clientDetail.setBusinessActivity(""+temploan.getBusinessActivityId());
					clientDetail.setLoanAmount(temploan.getLoanAmount()!=null?temploan.getLoanAmount().getAmountDoubleValue():new Double(0));

					clientDetails.add(clientDetail);
				}
				
			loanActionForm.setClientDetails(clientDetails);

	
			SessionUtils.setCollectionAttribute(LoanConstants.CLIENT_LIST,
					clients, request);
			
		}

		LoanBO loanBOInSession = (LoanBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		LoanBO loanBO = loanBusinessService.getAccount(loanBOInSession.getAccountId());
		loanBO.setUserContext(getUserContext(request));
		SessionUtils.setAttribute(LoanConstants.PROPOSEDDISBDATE, loanBO
				.getDisbursementDate(), request);
		
		SessionUtils.removeAttribute(LoanConstants.LOANOFFERING, request);
		LoanOfferingBO loanOffering = getLoanOffering(loanBO.getLoanOffering()
				.getPrdOfferingId(), getUserContext(request).getLocaleId());
		MaxMinLoanAmount maxMinLoanAmount = loanBO.getMaxMinLoanAmount();
		setUpdatedLoanOfferingLoanAmount(loanOffering, maxMinLoanAmount);
		MaxMinNoOfInstall maxMinNoOfInstall = loanBO.getMaxMinNoOfInstall();
		setUpdatedLoanOfferingNoOfInstall(loanOffering, maxMinNoOfInstall);
		SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering,
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
						LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		
		if (null != loanIndividualMonitoringIsEnabled
				&& loanIndividualMonitoringIsEnabled.intValue() != 0) {
			
			SessionUtils.setAttribute(
					LoanConstants.LOANINDIVIDUALMONITORINGENABLED,
					loanIndividualMonitoringIsEnabled.intValue(), request);
			if (customer.getCustomerLevel().isGroup()) {
				SessionUtils.setAttribute(
						LoanConstants.LOANACCOUNTOWNERISAGROUP, "yes", request);
				

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
				if (null != index && !index.equals("")) {
					LoanAccountDetailsViewHelper tempLoanAccount = new LoanAccountDetailsViewHelper();
					ClientBO clt = ((ClientBusinessService) ServiceFactory
							.getInstance().getBusinessService(
									BusinessServiceName.Client))
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
						.getCollateralNote(), getCollateralTypeEntity(form,
						request),loanAccountActionForm.getCustomFields());
		
		
	      
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		
		Integer loanIndividualMonitoringIsEnabled = configurationPersistence
				.getConfigurationKeyValueInteger(
						LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED).getValue();
		  
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
       	request.setAttribute("customerId",loanBO.getCustomer().getCustomerId().toString());

		}
    }

		
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
		LoanOfferingBO loanOffering = loanBO.getLoanOffering();
		MaxMinLoanAmount maxMinLoanAmount = loanBO.getMaxMinLoanAmount();
		MaxMinNoOfInstall maxMinNoOfInstall = loanBO.getMaxMinNoOfInstall();
		setUpdatedLoanOfferingLoanAmount(loanOffering, maxMinLoanAmount);
		setUpdatedLoanOfferingNoOfInstall(loanOffering, maxMinNoOfInstall);
		SessionUtils
				.setCollectionAttribute(LoanConstants.RECENTACCOUNTACTIVITIES,
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
		updateLoanOffering(loanOffering, loanAccountActionForm);
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
		loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(
				getUserContext(request).getPreferredLocale(), SessionUtils
						.getAttribute(LoanConstants.PROPOSEDDISBDATE, request)
						.toString()));
	}

	public void updateLoanOffering(LoanOfferingBO loanOffering,
			LoanAccountActionForm loanAccountActionForm) throws Exception {

		if (loanOffering.checkLoanAmountType(loanOffering) == 2) {
			CustomerBO customer = getCustomer(loanAccountActionForm
					.getCustomerIdValue());
			setUpdatedLoanOfferingLoanAmountFromLastLoan(loanOffering,
					getCustomerLastMaxLoanAmount(customer));
		}
		if (loanOffering.checkNoOfInstallType(loanOffering) == 2) {
			CustomerBO customer = getCustomer(loanAccountActionForm
					.getCustomerIdValue());
			setUpdatedLoanOfferingNoOfInstallFromLastLoan(loanOffering,
					getCustomerLastMaxLoanAmount(customer));
		}
		if (loanOffering.checkLoanAmountType(loanOffering) == 1) {
			setUpdatedLoanOfferingLoanAmountSameForAllLoan(loanOffering);
		}
		if (loanOffering.checkNoOfInstallType(loanOffering) == 1) {
			setUpdatedLoanOfferingNoOfInstallSameForAllLoan(loanOffering);
		}
		if (loanOffering.checkLoanAmountType(loanOffering) == 3) {
			CustomerBO customer = getCustomer(loanAccountActionForm
					.getCustomerIdValue());
			setUpdatedLoanOfferingLoanAmountFromLoanCycle(loanOffering,
					getCustomerMaxLoanCycle(customer, loanOffering
							.getPrdOfferingId()));
		}

		if (loanOffering.checkNoOfInstallType(loanOffering) == 3) {
			CustomerBO customer = getCustomer(loanAccountActionForm
					.getCustomerIdValue());
			setUpdatedLoanOfferingNoOfInstallFromLoanCycle(loanOffering,
					getCustomerMaxLoanCycle(customer, loanOffering
							.getPrdOfferingId()));
		}
	}

	private void setUpdatedLoanOfferingLoanAmount(
			LoanOfferingBO loanOfferingBO, MaxMinLoanAmount maxMinLoanAmount) {
		if (!(maxMinLoanAmount == null)) {
			loanOfferingBO.setMaxLoanAmount(getMoney(maxMinLoanAmount
					.getMaxLoanAmount().toString()));
			loanOfferingBO.setMinLoanAmount(getMoney(maxMinLoanAmount
					.getMinLoanAmount().toString()));
		}
	}

	private void setUpdatedLoanOfferingNoOfInstall(
			LoanOfferingBO loanOfferingBO, MaxMinNoOfInstall maxMinNoOfInstall) {
		if (!(maxMinNoOfInstall == null)) {
			loanOfferingBO.setMaxNoInstallments(maxMinNoOfInstall
					.getMaxNoOfInstallt());
			loanOfferingBO.setMinNoInstallments(maxMinNoOfInstall
					.getMinNoOfInstall());
		}
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
			}
			else {
				FeeView feeView = new FeeView(userContext, fee);
				if (loanOffering.isFeePresent(fee))
					defaultFees.add(feeView);
				else additionalFees.add(feeView);
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
		loanAccountActionForm.setStateSelected(getStringValue(loan
				.getAccountState().getId()));
		loanAccountActionForm
				.setLoanAmount(getStringValue(loan.getLoanAmount()));

		java.util.Date proposedDisbursement = (Date) SessionUtils.getAttribute(
				LoanConstants.PROPOSEDDISBDATE, request);
		loanAccountActionForm.setDisbursementDate(DateUtils.getUserLocaleDate(
				getUserContext(request).getPreferredLocale(), DateUtils
						.toDatabaseFormat(proposedDisbursement)));

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
		SessionUtils.setCollectionAttribute(LoanConstants.CUSTOM_FIELDS,
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

	private void removePrdOfferingNotMatchingPrdType(
			List<LoanOfferingBO> loanOfferings, CustomerBO customer) {
		Set<AccountBO> accounts = customer.getAccounts();
		Iterator<AccountBO> itr = accounts.iterator();
		ArrayList listAccountStateID = new ArrayList();
		while (itr.hasNext()) {
			AccountBO accountBo = itr.next();
			listAccountStateID.add(accountBo.getAccountState().getId());

		}
		if (!(listAccountStateID.contains(AccountStates.LOANACC_OBLIGATIONSMET))) {
			for (Iterator<LoanOfferingBO> iter = loanOfferings.iterator(); iter
					.hasNext();) {
				LoanOfferingBO loanOffering = iter.next();
				if (!(loanOffering.getLoanAmountFromLastLoan().isEmpty())) {
					iter.remove();
				}
				else if (!(loanOffering.getNoOfInstallFromLastLoan().isEmpty())) {
					iter.remove();
				}
			}

		}
		else {
			for (Iterator<LoanOfferingBO> iter = loanOfferings.iterator(); iter
					.hasNext();) {
				LoanOfferingBO loanOffering = iter.next();
				if (!loanOffering.getLoanAmountFromLastLoan().isEmpty()) {
					removePrdOfferingNotMatchingCustomerLastLoanAmount(
							customer, loanOffering, iter);
				}
				else if (!loanOffering.getNoOfInstallFromLastLoan().isEmpty()) {
					removePrdOfferingNotMatchingCustomerLastLoanAmount(
							customer, loanOffering, iter);
				}

			}
		}
	}

	private void removePrdOfferingNotMatchingCustomerLastLoanAmount(
			CustomerBO customer, LoanOfferingBO loanOffering,
			Iterator<LoanOfferingBO> iter) {
		Double lastLoanAmount = getCustomerLastMaxLoanAmount(customer);
		List listLoanAmount = loanOffering.eligibleLoanAmount(lastLoanAmount
				.toString(), loanOffering);
		List listNoOfInstall = loanOffering.eligibleNoOfInstall(lastLoanAmount
				.toString(), loanOffering);
		if (listLoanAmount.isEmpty()) {
			iter.remove();
		}
		else if (listNoOfInstall.isEmpty()) {
			iter.remove();
		}
	}

	public Double getCustomerLastMaxLoanAmount(CustomerBO customer) {
		ArrayList list = new ArrayList();
		Set<AccountBO> accounts = customer.getAccounts();
		Iterator<AccountBO> itr = accounts.iterator();
		while (itr.hasNext()) {
			AccountBO accountBo = itr.next();
			if (accountBo.getAccountState().getId().equals(new Short("6"))) {
				Integer accountID = accountBo.getAccountId();
				List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
				for (AccountBO account : accounts) {
					if (account.getType().equals(AccountTypes.LOAN_ACCOUNT))
						loanAccounts.add((LoanBO) account);
				}

				Iterator<LoanBO> LoanBOItr = loanAccounts.iterator();
				while (LoanBOItr.hasNext()) {
					LoanBO loanBO = LoanBOItr.next();
					if (accountID.equals(loanBO.getAccountId())) {
						list.add(getStringValue(loanBO.getLoanAmount()));
					}

				}
			}

		}
		return Double.parseDouble(Collections.max(list).toString());

	}

	private void setUpdatedLoanOfferingLoanAmountFromLastLoan(
			LoanOfferingBO loanOfferingBO, Double lastLoanMaxAmount) {
		Iterator itr = loanOfferingBO.eligibleLoanAmount(
				lastLoanMaxAmount.toString(), loanOfferingBO).iterator();
		while (itr.hasNext()) {
			loanOfferingBO
					.setMaxLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
			loanOfferingBO
					.setMinLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
			loanOfferingBO
					.setDefaultLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
		}
	}

	private void setUpdatedLoanOfferingNoOfInstallFromLastLoan(
			LoanOfferingBO loanOfferingBO, Double lastLoanMaxAmount) {
		Iterator itrInstall = loanOfferingBO.eligibleNoOfInstall(
				lastLoanMaxAmount.toString(), loanOfferingBO).iterator();
		while (itrInstall.hasNext()) {
			loanOfferingBO.setMaxNoInstallments((Short) itrInstall.next());
			loanOfferingBO.setMinNoInstallments((Short) itrInstall.next());
			loanOfferingBO.setDefNoInstallments((Short) itrInstall.next());
		}
	}

	private void setUpdatedLoanOfferingLoanAmountSameForAllLoan(
			LoanOfferingBO loanOfferingBO) {
		String value = null;
		Iterator itr = loanOfferingBO.eligibleLoanAmount(value, loanOfferingBO)
				.iterator();
		while (itr.hasNext()) {
			loanOfferingBO
					.setMaxLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
			loanOfferingBO
					.setMinLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
			loanOfferingBO
					.setDefaultLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
		}
	}

	private void setUpdatedLoanOfferingNoOfInstallSameForAllLoan(
			LoanOfferingBO loanOfferingBO) {
		String value = null;
		Iterator itrInstall = loanOfferingBO.eligibleNoOfInstall(value,
				loanOfferingBO).iterator();
		while (itrInstall.hasNext()) {
			loanOfferingBO.setMaxNoInstallments((Short) itrInstall.next());
			loanOfferingBO.setMinNoInstallments((Short) itrInstall.next());
			loanOfferingBO.setDefNoInstallments((Short) itrInstall.next());
		}
	}

	private void setUpdatedLoanOfferingLoanAmountFromLoanCycle(
			LoanOfferingBO loanOfferingBO, Integer LoanCounter) {
		Iterator itr = loanOfferingBO.eligibleLoanAmount(
				LoanCounter.toString(), loanOfferingBO).iterator();
		while (itr.hasNext()) {
			loanOfferingBO
					.setMaxLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
			loanOfferingBO
					.setMinLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
			loanOfferingBO
					.setDefaultLoanAmount(getMoney(getStringValue((Double) itr
							.next())));
		}
	}

	private void setUpdatedLoanOfferingNoOfInstallFromLoanCycle(
			LoanOfferingBO loanOfferingBO, Integer LoanCounter) {
		Iterator itrInstall = loanOfferingBO.eligibleNoOfInstall(
				LoanCounter.toString(), loanOfferingBO).iterator();
		while (itrInstall.hasNext()) {
			loanOfferingBO.setMaxNoInstallments((Short) itrInstall.next());
			loanOfferingBO.setMinNoInstallments((Short) itrInstall.next());
			loanOfferingBO.setDefNoInstallments((Short) itrInstall.next());
		}
	}

	private Integer getCustomerMaxLoanCycle(CustomerBO customer,
			Short prdOfferingID) {
		ArrayList listLoanCounter = new ArrayList();
		ClientPerformanceHistoryEntity gg1;
		if (customer.getPerformanceHistory() instanceof ClientPerformanceHistoryEntity) {
			gg1 = (ClientPerformanceHistoryEntity) customer
					.getPerformanceHistory();
			Iterator<LoanCounter> itr = gg1.getLoanCounters().iterator();
			while (itr.hasNext()) {
				LoanCounter counter = itr.next();
				if (prdOfferingID == counter.getLoanOffering()
						.getPrdOfferingId())
					listLoanCounter.add(counter.getLoanCycleCounter());
			}

		}
		if (listLoanCounter.isEmpty()) {
			listLoanCounter.add(new Integer("0"));
		}
		return Integer.parseInt(Collections.max(listLoanCounter).toString());
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
		return new MeetingBusinessService();
	}
}

package org.mifos.application.accounts.loan.business.handlers;




import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.accounts.business.handler.AccountsBusinessProcessor;
import org.mifos.application.accounts.dao.AccountNotesDAO;
import org.mifos.application.accounts.dao.ClosedAccSearchDAO;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.business.util.helpers.LoanHeaderObject;
import org.mifos.application.accounts.loan.dao.LoanDAO;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.util.helpers.IDGenerator;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.loan.util.valueobjects.LoanPenalty;
import org.mifos.application.accounts.loan.util.valueobjects.LoanSummary;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PathConstants;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountStatusChangeHistory;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.AccountState;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.productdefinition.dao.LoanProductDAO;
import org.mifos.application.productdefinition.util.helpers.GracePeriodTypeConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.LoanOffering;
import org.mifos.application.productdefinition.util.valueobjects.PrdOffering;
import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.AccountFeeInstallment;
import org.mifos.framework.components.repaymentschedule.FeeInstallment;
import org.mifos.framework.components.repaymentschedule.MeetingScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInstallment;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.DataTypeConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;





/**
 * This class acts as businessProcessor for creating/updating loan accounts.
 * @author ashishsm
 *
 */

public class LoanBusinessProcessor extends AccountsBusinessProcessor{
	

	/**
	 * This gets the product offering related data to be shown in the UI.
	 * This is a common method which could be used by all account types hence this method is in the base class.
	 * @param prdOfferingId
	 */
	private LoanOffering getLoanOffering(Short prdOfferingId)throws SystemException{
		// gets the loanProductDAO
		LoanProductDAO  loanProductDAO = (LoanProductDAO)getDAO(ProductDefinitionConstants.GETPATHLOANPRODUCT);
		LoanOffering loanOffering = null;
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			// gets the ProductOfferingObject
			loanOffering = (LoanOffering)loanProductDAO.getPrdOffering(prdOfferingId,session);
			initializePrdOffering(loanOffering,session);
		}catch(HibernateException he){
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION,he);
		}finally{
			HibernateUtil.closeSession(session);
		}
		return loanOffering;
	}
	/**
	 * This method loads the master data required for the page to  display and also loads the related productOffering
	 * data to be displayed on the page.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context)throws SystemException,ApplicationException {
		Loan loan = (Loan)context.getValueObject();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The selected prd offering is " + loan.getSelectedPrdOfferingId());
		// this sets the initialized loanPrdOffering object in the loan valueobject so that it could
		// retrieve and display the required fields in the UI.
		loan.setLoanOffering(getLoanOffering(loan.getSelectedPrdOfferingId()));
		//also setting the same in the context as business result because we need some info on preview page
		// this would be the info that is inherited from prdOffering and which the user can not change.
		context.addBusinessResults(AccountConstants.SELECTEDPRDOFFERING, loan.getLoanOffering());
		// gets the business activities
		context.addAttribute(getMasterData(MasterConstants.LOAN_PURPOSES,context.getUserContext().getLocaleId(),MasterConstants.BUSINESS_ACTIVITIES));

		// gets the collateral types
		context.addAttribute(getMasterData(MasterConstants.COLLATERAL_TYPES,context.getUserContext().getLocaleId(),MasterConstants.COLLATERAL_TYPES,"org.mifos.application.master.util.valueobjects.CollateralType","collateralTypeId"));

		//gets the interest rate types to be dispalyed in UI
		context.addAttribute(getMasterData(MasterConstants.INTERESTTYPES,context.getUserContext().getLocaleId(),MasterConstants.INTERESTTYPES,"org.mifos.application.master.util.valueobjects.InterestTypes","interestTypeId"));

		//gets the interest calculation rule to be dispalyed in UI
		context.addAttribute(getMasterData(MasterConstants.INTERESTCALCRULE,context.getUserContext().getLocaleId(),MasterConstants.INTERESTCALCRULE,"org.mifos.application.master.util.valueobjects.InterestCalcRule","interestCalcRuleId"));

		//gets the grace period types to be dispalyed in UI
		context.addAttribute(getMasterData(MasterConstants.GRACEPERIODTYPES,context.getUserContext().getLocaleId(),MasterConstants.GRACEPERIODTYPES,"org.mifos.application.productdefinition.util.valueobjects.GracePeriodType","gracePeriodTypeId"));

		
		// gets the applicable fees apart from the ones already in prd Offering selected by the user.
		LoanDAO loanDAO = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
		context.addAttribute(new SearchResults(LoanConstants.APPLICALEFEES,loanDAO.getApplicableFees(loan.getLoanOffering().getPrdOfferingId())));
		
		// gets the potential disbursement date.
		CustomerMaster customerMaster = (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
		String systemDate = DateHelper.getCurrentDate(context.getUserContext().getMfiLocale()); 
		Date todaysDate = DateHelper.getLocaleDate(context.getUserContext().getMfiLocale(), systemDate);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Before trying to get potential disbursement date with todays date being : -" + todaysDate);
		loan.setDisbursementDate(loanDAO.getPotentialDisbursementDate(customerMaster.getCustomerId(),todaysDate));
		
		//Fetching the fees with formulaId
		context.addAttribute(new SearchResults(LoanConstants.FEEFORMULALIST,loanDAO.getFeeFormula(context.getUserContext().getLocaleId())));


	}



	/**
	 * This method initializes the required loan product offering fields.
	 * @param prdOffering
	 * @param session
	 */
	protected void initializePrdOffering(PrdOffering prdOffering, Session session)throws SystemException {

		LoanDAO loanDAO = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
		loanDAO.initializePrdOffering(prdOffering,session);
	}

	/**
	 * It generates the system id for the loan account using IDGenerator.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context)throws SystemException,ApplicationException {

		Loan loan = (Loan)context.getValueObject();
		// prepare the loan account object for saving complete with all fields and associations
		prepareLoanForSaving(context);

	}
	/**
	 * This method prepares the loan object for saving with all its associations
	 * @param context
	 */
	private void prepareLoanForSaving(Context context)throws ApplicationException {
		Loan loan = (Loan)context.getValueObject();

		//MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("prd offering name set is " + loan.getPrdOfferingName());
		// set the global account num using the IDGenerator
		loan.setGlobalAccountNum(IDGenerator.generateIdForLoan(context.getUserContext().getBranchGlobalNum()));
		// also adding the global account number to the business results to be accessed on the account creation confirmation page.
		context.addBusinessResults(LoanConstants.LOANACCGLOBALNUM, loan.getGlobalAccountNum());

		// create and set the customer object this will have just the id and the version number for saving
		// this object will not be saved.
		Customer customer = new Customer();
		CustomerMaster customerMaster = (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
		customer.setCustomerId(customerMaster.getCustomerId());
		customer.setVersionNo(customerMaster.getVersionNo());
		loan.setCustomer(customer);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("customer id set is " + loan.getCustomer().getCustomerId() + " and version number being set is " + loan.getCustomer().getVersionNo());

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("interest at disbursement is set as  " + loan.getIntrestAtDisbursement());
		// set the account type to loan account
		loan.setAccountTypeId(new Short(AccountTypes.LOANACCOUNT));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("account type is " + loan.getAccountTypeId());
		// set personnel id to the personnel who is creating the account
		loan.setPersonnelId(context.getUserContext().getId());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("personnel id  is " + loan.getPersonnelId());
		// set the office id to be the logged in user
		loan.setOfficeId(customerMaster.getOfficeId());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("branch id  is " + customerMaster.getOfficeId());
		// setting the interest type id same as that is present for prdOffering
		loan.setInterestTypeId(loan.getLoanOffering().getInterestTypes().getInterestTypeId());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Interest type is " + loan.getInterestTypeId());

		
		
		// setting the grace period type id based on the user selection of interest deducted at disbursement.
		// the logic is if interest dedcuted at disbursement is true set the grace period type = none
		// else inherit the grace period type from loan offering.
		if(null != loan.getIntrestAtDisbursement() && 1 == loan.getIntrestAtDisbursement()){
			loan.setGracePeriodTypeId(GracePeriodTypeConstants.NONE);
		}else{
			loan.setGracePeriodTypeId(loan.getLoanOffering().getGracePeriodType().getGracePeriodTypeId());
		}

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("grace period type is " + loan.getGracePeriodTypeId());

		// set the grace period for penalty to be the same as that for product offering.
		loan.setGracePeriodPenalty(loan.getLoanOffering().getPenaltyGrace());
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("grace period type is " + loan.getGracePeriodPenalty());

		// set created by to the id of the logged in user.
		loan.setCreatedBy(context.getUserContext().getId());
		// set the created date to current date
		String currentDate = DateHelper.getCurrentDate(context.getUserContext().getMfiLocale());
		loan.setCreatedDate(DateHelper.getLocaleDate(context.getUserContext().getMfiLocale(),currentDate));
		// setting the state as selected by the user
		loan.setAccountStateId(Short.valueOf(loan.getStateSelected()));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("state in which it is being saved is  " + loan.getAccountStateId());
		// setting the currency for loan account.
		Currency currency = new Currency();
		currency.setCurrencyId(Short.valueOf("1"));
		loan.setCurrency(currency);

		//security checking 
		
		//we need the customer loan officerId for recordLoanOfficeis
		
		//checkPermissionForCreate(loan.getAccountStateId(),context.getUserContext(),null,loan.getOfficeId(),context.getUserContext().getId());

		
		RepaymentSchedule repaymentSchedule = (RepaymentSchedule)context.getBusinessResults(LoanConstants.REPAYMENTSCHEDULE);
		// set the values in the loan Summary
		LoanSummary loanSummary = new LoanSummary();

		loanSummary.setOriginalPrincipal(loan.getLoanAmount());
		loanSummary.setOriginalPenalty(new Money());
		loanSummary.setOriginalInterest(repaymentSchedule.getInterest());
		loanSummary.setOriginalFees(repaymentSchedule.getFees());
		loanSummary.setFeesPaid(new Money());
		loanSummary.setPenaltyPaid(new Money());
		loanSummary.setInterestPaid(new Money());
		loanSummary.setPrincipalPaid(new Money());

		// setting the bidirectional relationship for the loan summary
		loan.setLoanSummary(loanSummary);


		// iterate over the account fee set to set the account relationship and get only those fees where feeId is not null.
		Set<AccountFees> targetAccountFeeSet = new HashSet<AccountFees>();
		Set<AccountFees> accountFeeSet = loan.getAccountFeesSet();
		if(null != accountFeeSet && ! accountFeeSet.isEmpty()){
			for(AccountFees accountFee : accountFeeSet){
				Fees fee = accountFee.getFees();
				if(null == fee.getFeeId()){

					continue;
				}else{
					if(accountFee.getCheckToRemove()==null || accountFee.getCheckToRemove().equals(Short.valueOf("0")))
					{
						accountFee.setAccount(loan);
						targetAccountFeeSet.add(accountFee);
					}
						
				}
			}
		}

		loan.setAccountFeesSet(targetAccountFeeSet);



		// get the penalty from prdOffering and add it to the set of loan.
		LoanPenalty loanPenalty = new LoanPenalty();
		if(loan.getLoanOffering().getPenalty() != null)
		{
			loanPenalty.setPenaltyId(loan.getLoanOffering().getPenalty().getPenaltyID());
			loanPenalty.setPenaltyType(loan.getLoanOffering().getPenalty().getPenaltyType());
			loanPenalty.setPenaltyRate(loan.getLoanOffering().getPenaltyRate());
			loanPenalty.setLoan(loan);
			Set<LoanPenalty> loanPenaltySet = new HashSet<LoanPenalty>();
			loanPenaltySet.add(loanPenalty);
			loan.setLoanPenaltySet(loanPenaltySet);

		}
		else
			 loan.setLoanPenaltySet(null);
		// this could be zero if the user does not select anything in the ui.
		if(0 == loan.getBusinessActivityId()){
			loan.setBusinessActivityId(null);
		}

		if(0 == loan.getCollateralTypeId()){
			loan.setCollateralTypeId(null);
		}

	}
	/**
	 *  This gets the list of possible states and also sets business result in context indicating if
	 *  pending approval is possible or not.If the user comes to manage page it also gets the customer master and puts it in context.
	 *  Also regenrates the repayment schedule in case of manage.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void previewInitial(Context context)throws SystemException,ApplicationException {
		Loan loan = (Loan)context.getValueObject();
		// same preview called from various pages
		// checking if the inputPage is creation do the following
		if(null != context.getBusinessResults(LoanConstants.INPUTPAGE) && !"editDetails".equals((String)context.getBusinessResults(LoanConstants.INPUTPAGE))){
			
			boolean isPendingApproval = false;
			LoanDAO loanDAO = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
	
			// gets the states currently is use.
			List statesInUse = loanDAO.getStatesCurrentlyInUse(ProductDefinitionConstants.LOANID);
	
			if(statesInUse != null && !statesInUse.isEmpty()){
				// check if pending approval is a valid state.
				for(Object obj : statesInUse){
					AccountState accountState = (AccountState)obj;
					if(accountState.getAccountStateId().equals(Short.valueOf(AccountStates.LOANACC_PENDINGAPPROVAL) )){
						isPendingApproval = true;
						MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Checking  pending approval value is: "  + isPendingApproval);
					}
				}
			}
			
			//interest deducted at disbursement cannot be selected if
			// grace period type is grace on all repayments.
			if(null != loan.getIntrestAtDisbursement() && 1 == loan.getIntrestAtDisbursement()){
				loan.setGracePeriodTypeId(GracePeriodTypeConstants.NONE);
			}else{
				loan.setGracePeriodTypeId(loan.getLoanOffering().getGracePeriodType().getGracePeriodTypeId());
			}	
			
			// setting in context if pending approval state is possible or not because it is an optional state.
			context.addBusinessResults(LoanConstants.ISPENDINGAPPROVAL, isPendingApproval);
		}else{
			
			//interest deducted at disbursement cannot be selected if
			// grace period type is grace on all repayments.
			if(null != loan.getIntrestAtDisbursement() && 1 == loan.getIntrestAtDisbursement()){
				loan.setGracePeriodTypeId(GracePeriodTypeConstants.NONE);
			}else{
				loan.setGracePeriodTypeId(loan.getLoanOffering().getGracePeriodType().getGracePeriodTypeId());
			}
			
			//Checking the disbursment date based on accountState.
			if(!isDisbursmentDateValid(loan.getAccountId(),loan.getAccountStateId(),loan.getDisbursementDate())){
				throw new  RepaymentScheduleException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
			}
			
			// also need to regenerate the repayment schedule because the user might have updated some of the fields.
			//schedule is regenerated only if the account state is one of the following.  
			if(loan.getAccountStateId()== AccountStates.LOANACC_APPROVED ||
					loan.getAccountStateId() == AccountStates.LOANACC_DBTOLOANOFFICER || 
					loan.getAccountStateId()== AccountStates.LOANACC_PARTIALAPPLICATION ||
					loan.getAccountStateId()== AccountStates.LOANACC_PENDINGAPPROVAL){
				// get the previous loan meeting object
				Meeting oldLoanMeeting = loan.getLoanMeeting();
				
				Iterator itr=loan.getAccountFeesSet().iterator();
				while(itr.hasNext()){
					AccountFees accountFees=(AccountFees)itr.next();
					if(accountFees.getFeeStatus()!=null && accountFees.getFeeStatus().equals(AccountConstants.INACTIVE_FEES)){
						itr.remove();
					}
				}
				
				LoanDAO loanDAO = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
				loan.setAccountFeesSet(loanDAO.getAccountFeesSet(loan.getAccountId()));
				
				regenerateRepaymentSchedule(context);
				oldLoanMeeting.setMeetingStartDate(loan.getLoanMeeting().getMeetingStartDate());
				loan.setLoanMeeting(oldLoanMeeting);
				RepaymentSchedule repaymentSchedule = (RepaymentSchedule)context.getBusinessResults(LoanConstants.REPAYMENTSCHEDULE);
				// set the values in the loan Summary
				LoanSummary loanSummary = loan.getLoanSummary();
				loanSummary.setOriginalPrincipal(loan.getLoanAmount());
				loanSummary.setOriginalPenalty(new Money());
				loanSummary.setOriginalInterest(repaymentSchedule.getInterest());
				loanSummary.setOriginalFees(repaymentSchedule.getFees());
				loanSummary.setFeesPaid(new Money());
				loanSummary.setPenaltyPaid(new Money());
				loanSummary.setInterestPaid(new Money());
				loanSummary.setPrincipalPaid(new Money());
			}
		}

		

	}


	/**
	 * This method is called before get is called . It loads the master data required to display certain fields on the jsp.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getInitial(Context context)throws SystemException,ApplicationException{
		// 	gets the business activities
		context.addAttribute(getMasterData(MasterConstants.LOAN_PURPOSES,context.getUserContext().getLocaleId(),MasterConstants.BUSINESS_ACTIVITIES));

		// gets the collateral types
		context.addAttribute(getMasterData(MasterConstants.COLLATERAL_TYPES,context.getUserContext().getLocaleId(),MasterConstants.COLLATERAL_TYPES,"org.mifos.application.master.util.valueobjects.CollateralType","collateralTypeId"));

		//gets the interest rate types to be dispalyed in UI
		context.addAttribute(getMasterData(MasterConstants.INTERESTTYPES,context.getUserContext().getLocaleId(),MasterConstants.INTERESTTYPES,"org.mifos.application.master.util.valueobjects.InterestTypes","interestTypeId"));

		//gets the interest calculation rule to be dispalyed in UI
		context.addAttribute(getMasterData(MasterConstants.INTERESTCALCRULE,context.getUserContext().getLocaleId(),MasterConstants.INTERESTCALCRULE,"org.mifos.application.master.util.valueobjects.InterestCalcRule","interestCalcRuleId"));

		//gets the grace period types to be dispalyed in UI
		context.addAttribute(getMasterData(MasterConstants.GRACEPERIODTYPES,context.getUserContext().getLocaleId(),MasterConstants.GRACEPERIODTYPES,"org.mifos.application.productdefinition.util.valueobjects.GracePeriodType","gracePeriodTypeId"));
		
		//gets account states																																												   
		context.addAttribute(getMasterData(MasterConstants.ACCOUNT_STATES,context.getUserContext().getLocaleId(),MasterConstants.ACCOUNT_STATES,"org.mifos.application.master.util.valueobjects.AccountState","accountStateId"));
		
		// gets account state flags
		context.addAttribute(getMasterData(MasterConstants.ACCOUNT_STATE_FLAGS,context.getUserContext().getLocaleId(),MasterConstants.ACCOUNT_STATE_FLAGS,"org.mifos.application.accounts.util.valueobjects.AccountStateFlag","flagId"));
	}

	/**
	 * It calls the get and once the initialized value object and associated prdOffering object
	 * is obtained , it sets the prdOffering object in context.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */
	public void get(Context context)throws SystemException,ApplicationException{

		Loan loan = (Loan)context.getValueObject();
		LoanDAO loanDAO = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
		if(null != loan.getGlobalAccountNum() && ! "".equals(loan.getGlobalAccountNum())){
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The global account number to query is " + loan.getGlobalAccountNum());
			context.setValueObject(loanDAO.findBySystemId(loan.getGlobalAccountNum()));

		}else{
			context.setValueObject(loanDAO.findByPK(loan.getAccountId()));
		}
		context.addBusinessResults(AccountConstants.CUSTOMERMASTER, getCustomerMaster(((Loan)context.getValueObject()) ,context.getPath()));
		// this sets the prdOffering object in the context business result. So that it could be
		// used on the update and other related pages.
		context.addBusinessResults(AccountConstants.SELECTEDPRDOFFERING, ((Loan)context.getValueObject()).getLoanOffering());
		// sets the recent account activity in the attributes.

		context.addAttribute(new SearchResults(AccountConstants.NOTES, new AccountNotesDAO().getLatestNotesByCount(AccountConstants.NOTES_COUNT,((Loan)context.getValueObject()).getAccountId())));

		context.addBusinessResults(AccountConstants.LAST_PAYMENT_ACTION,loanDAO.getLastPaymentAction(((Loan)context.getValueObject()).getAccountId()));

		HibernateUtil.closeSession();
		//For meeting Date, amount due and amount in arrears. These are done using M2 style object.
		LoanBO loanBO = new LoanBusinessService().findBySystemId(((Loan)context.getValueObject()).getGlobalAccountNum());
		List<LoanActivityView> loanRecentActivityView = new LoanBusinessService().getRecentActivityView(loanBO.getGlobalAccountNum(),context.getUserContext().getLocaleId());
		context.addAttribute(new SearchResults(LoanConstants.RECENTACCOUNTACTIVITIES, loanRecentActivityView));
		context.addAttribute(new SearchResults(AccountConstants.LOAN_NEXT_MEETING_DATE, loanBO.getNextMeetingDate()));
		context.addAttribute(new SearchResults(AccountConstants.LOAN_AMOUNT_DUE, loanBO.getTotalAmountDue()));
		context.addAttribute(new SearchResults(AccountConstants.LOAN_AMOUNT_IN_ARREARS,loanBO.getTotalAmountInArrears()));
	}

	private void generateRepaymentSchedule(Context context, Loan loan,String type)throws ApplicationException,SystemException
	{
		
		Meeting meeting =null ;
		CustomerMeeting customerMeeting =null;
		boolean isDisbursementDateValid = false;
		LoanDAO loanDAO = null;
		
		CustomerMaster customerMaster = ((CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER));
		loanDAO  = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
		customerMeeting = loanDAO.getCustomerMeeting(customerMaster.getCustomerId());

		// only if customer meeting is not null get the meeting from customer meeting
		// this could be null if customer does not have a customer meeting.
		if(null != customerMeeting){
			meeting = customerMeeting.getMeeting();
		}
		
		// this method returns the initialized account fees object based on the fee ids selected by the user in the ui.
		Set<AccountFees> accountFeeSet = loanDAO.initalizeAccountFees(loan.getAccountFeesSet());
		loan.setAccountFeesSet(accountFeeSet);

		// get the repayment schedule input object which would be passed to repayment schedule generator
		RepaymentScheduleInputsIfc repaymntScheduleInputs = RepaymentScheduleFactory.getRepaymentScheduleInputs();
		RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory.getRepaymentScheduler();
		repaymentScheduler.setRepaymentScheduleInputs(repaymntScheduleInputs);
		//set the customer'sMeeting , this is required to check if the disbursement date is valid
		// this would be null if customer does not have a meeting.
		repaymntScheduleInputs.setMeeting(meeting);
		
		
		
		Meeting loanMeeting = null;
		if(type.equals("transient"))
		{
			Meeting prdOfferingMeeting = loan.getLoanOffering().getPrdOfferingMeeting().getMeeting();
			// check if the prodOffering meeting is a multiple of customer meeting else
				// throw an exception specifying that recurrence is not compatible
				if(null != customerMeeting)
				{
					
					if(0!=(prdOfferingMeeting.getMeetingDetails().getRecurAfter().shortValue())% meeting.getMeetingDetails().getRecurAfter().shortValue() )
					{
						throw new RepaymentScheduleException(LoanExceptionConstants.INCOMPATIBLERECCURENCE);
					}
				}
				// also iterate over the accountFeesSet to check if the recurrence of fee matches the recurrence of loan offering
				for(Object obj : loan.getAccountFeesSet())
				{
					AccountFees accountFees = (AccountFees)obj;
					if(null != accountFees.getFees().getFeeFrequency() && 2 != accountFees.getFees().getFeeFrequency().getFeeFrequencyTypeId().shortValue() && (accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency().getMeetingDetails().getRecurrenceType().getRecurrenceId().intValue() != 
							prdOfferingMeeting.getMeetingDetails().getRecurrenceType().getRecurrenceId().intValue())){
						throw new RepaymentScheduleException(LoanExceptionConstants.INCOMPATIBLEFEERECCURENCE, new Object[]{accountFees.getFees().getFeeName()});
					}
				}

				// merge the customer and prd offering meetings to create a new meeting object.
				if(null != customerMeeting)
				{
					loanMeeting = MeetingScheduleHelper.mergeFrequency(meeting, prdOfferingMeeting);
				}
				else{
					loanMeeting = MeetingScheduleHelper.getFrequency(prdOfferingMeeting, loan.getDisbursementDate());
				}

		}
		else
			loanMeeting = loan.getLoanMeeting();
		
		//this methods prepares the input object for the repayment schedule generator.
		prepareInputsForScheduler(repaymntScheduleInputs,loan,type);
		java.util.Date dt = loan.getDisbursementDate();
		// for a loan meeting get the meeting start date based on the disbursement date
		if(null != customerMeeting)
		{
			isDisbursementDateValid = repaymentScheduler.isDisbursementDateValid();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("id disbursement date valid" + isDisbursementDateValid);
			if(!isDisbursementDateValid){
				throw new  RepaymentScheduleException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
			}
		}

		
		// set this date into the repayment schedule inputs
		java.util.Calendar cal = new java.util.GregorianCalendar();
		cal.setTime(dt);
		loanMeeting.setMeetingStartDate(cal);

		loan.setLoanMeeting(loanMeeting);
		repaymntScheduleInputs.setRepaymentFrequency(loan.getLoanMeeting());
		
		// this method invokes the  repayment schedule generator.
		RepaymentSchedule repaymentSchedule = invokeRepmntScheduleGenerator(repaymentScheduler);
		if(!loan.getIntrestAtDisbursement().equals(LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT))
			removeDisbursementFees(repaymentSchedule);
		// setting the generated repayment schedule in context business results because this is required while creating.
		context.addBusinessResults(LoanConstants.REPAYMENTSCHEDULE, repaymentSchedule);

		// get the repayment schedule installments and set them in the context attributes for them to be accessible
		// to the tag on the jsp.
		SearchResults searchResults = new SearchResults();
		searchResults.setValue(repaymentSchedule.getRepaymentScheduleInstallment());
		searchResults.setResultName(LoanConstants.REPAYMENTSCHEDULEINSTALLMENTS);
		context.addAttribute(searchResults);
	
	}

	
	// This method removes the disburesment fees that would be part of the repaymentschedule , 
	// it's removed as it's not part of any installment
	private void removeDisbursementFees(RepaymentSchedule repaymentSchedule)
	{
		List<RepaymentScheduleInstallment> repaymentScheduleInstallmentList = repaymentSchedule.getRepaymentScheduleInstallment();
		FeeInstallment feeInstallment=null;
		Map<Short,AccountFeeInstallment> summaryFeeInstallmentMap=null;
		Set<Short> summaryFeeInstallmentKeySet=null;
		double disbursementAmount = 0.0;
		for(RepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheduleInstallmentList)
		{
			if(repaymentScheduleInstallment.getInstallment() == 1)
			{
				feeInstallment = repaymentScheduleInstallment.getFeeInstallment();
				if(feeInstallment!=null){
					summaryFeeInstallmentMap= feeInstallment.getSummaryFeeInstallmentMap();
					summaryFeeInstallmentKeySet=summaryFeeInstallmentMap.keySet();
					Iterator iterator=summaryFeeInstallmentKeySet.iterator();
					while(iterator.hasNext())
					{
						AccountFeeInstallment accountfeeInstallment = summaryFeeInstallmentMap.get(iterator.next());
						Short paymentType=accountfeeInstallment.getAccountFee().getFees().getFeeFrequency().getFeePaymentId();
						if(paymentType!=null && paymentType.equals(FeesConstants.TIME_OF_DISBURSMENT))
						{
							disbursementAmount=accountfeeInstallment.getAccountFeeAmount().getAmountDoubleValue();
							Money disbursementAmountMoney = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),disbursementAmount);
							repaymentScheduleInstallment.setFees(disbursementAmountMoney.negate());
							iterator.remove();
						}
					}
				}
				break;
			}
		}
		
	}

	
	/**
	 * This method generates the repayment schedule , it is called while creating a new account as well as while updating
	 * the account information.
	 * @param context
	 * @throws ApplicationException - Exceptions arising out of generation of repayment schedule.
	 * @throws SystemException
	 */
	private void regenerateRepaymentSchedule(Context context)throws ApplicationException,SystemException
	{
		Loan loan = (Loan)context.getValueObject();
		generateRepaymentSchedule(context , loan, "transient");
		
	}

	/**
	 *This would invoke the componenet which generates the repayment schedule.
	 * @param context
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void nextInitial(Context context) throws ApplicationException,SystemException{
		Loan loan = (Loan)context.getValueObject();
		//interest deducted at disbursement cannot be selected if
		// grace period type is grace on all repayments.
		if(null != loan.getIntrestAtDisbursement() && 1 == loan.getIntrestAtDisbursement()){
			loan.setGracePeriodTypeId(GracePeriodTypeConstants.NONE);
		}else{
			loan.setGracePeriodTypeId(loan.getLoanOffering().getGracePeriodType().getGracePeriodTypeId());
		}
		
		//Checking the disbursment date based on accountState.
		if(!isDisbursmentDateValid(loan.getAccountId(),AccountStates.LOANACC_PARTIALAPPLICATION,loan.getDisbursementDate())){
			throw new  RepaymentScheduleException(LoanExceptionConstants.INVALIDDISBURSEMENTDATE);
		}
		SearchResults searchResults =context.getSearchResultBasedOnName(LoanConstants.APPLICALEFEES);
		List<FeeMaster> feeMasterList=(List<FeeMaster>)searchResults.getValue();
		checkForDuplicatePeriodicFeeExist(loan,feeMasterList);
		regenerateRepaymentSchedule(context);

	}

	/**
	 * This methods prepares the input object for the repayment schedule generator.
	 * It merges the customer meeting and prd offering meeting and thus forms the loanMeeting.
	 * It then creates the inputs for repayment schedule generator and invokes repayment schduler.
	 * @param context
	 */
	private void prepareInputsForScheduler(RepaymentScheduleInputsIfc repaymntScheduleInputs,Loan loan,String type)throws ApplicationException {

		// set the inputs for repaymentSchedule
		repaymntScheduleInputs.setGracePeriod(loan.getGracePeriodDuration());
		repaymntScheduleInputs.setGraceType(loan.getGracePeriodTypeId());
		
		repaymntScheduleInputs.setIsInterestDedecutedAtDisburesement(loan.getIntrestAtDisbursement().equals(Short.valueOf("1"))?true:false);
		if(type.equals("transient"))
			repaymntScheduleInputs.setIsPrincipalInLastPayment(loan.getLoanOffering().getPrinDueLastInstFlag().equals(Short.valueOf("1"))?true:false);
		else
			repaymntScheduleInputs.setIsPrincipalInLastPayment(false);
		repaymntScheduleInputs.setPrincipal(loan.getLoanAmount());
		repaymntScheduleInputs.setInterestRate(loan.getInterestRateAmount());
		repaymntScheduleInputs.setNoOfInstallments(loan.getNoOfInstallments());
		repaymntScheduleInputs.setInterestType(loan.getInterestTypeId());
		repaymntScheduleInputs.setAccountFee(loan.getAccountFeesSet());
		repaymntScheduleInputs.setDisbursementDate(loan.getDisbursementDate());
		repaymntScheduleInputs.setMiscFees(getMiscFee(loan.getAccountActionDateSet()));
		repaymntScheduleInputs.setMiscPenlty(getMiscPenalty(loan.getAccountActionDateSet()));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("after preparing inputs" );
	}

	/**
	 * This method invokes the  repayment schedule generator.
	 */
	private RepaymentSchedule invokeRepmntScheduleGenerator(RepaymentScheduleIfc repaymentScheduler)throws RepaymentScheduleException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("before invoking scheduler" );
		return repaymentScheduler.getRepaymentSchedule();
	}

	/**
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context)throws SystemException,ApplicationException {
		Loan loan = (Loan)context.getValueObject();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside update inital method");

		// setting the grace period type id based on the user selection of interest deducted at disbursement.
		// the logic is if interest dedcuted at disbursement is true set the grace period type = none
		// else inherit the grace period type from loan offering.
		if(null != loan.getIntrestAtDisbursement() && 1 == loan.getIntrestAtDisbursement()){
			loan.setGracePeriodTypeId(GracePeriodTypeConstants.NONE);
		}else{
			loan.setGracePeriodTypeId(loan.getLoanOffering().getGracePeriodType().getGracePeriodTypeId());
		}

		



		loan.setUpdatedBy(context.getUserContext().getId());

		// 	set the updated date to current date
		String updatedDate = DateHelper.getCurrentDate(context.getUserContext().getMfiLocale());
		loan.setCreatedDate(DateHelper.getLocaleDate(context.getUserContext().getMfiLocale(),updatedDate));

		// this could be zero if the user does not select anything in the ui.
		if(0 == loan.getBusinessActivityId()){
			loan.setBusinessActivityId(null);
		}

		if(0 == loan.getCollateralTypeId()){
			loan.setCollateralTypeId(null);
		}

	}

	/**This method updates the loan account.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context)throws SystemException,ApplicationException {
		Set<AccountFees> accountFeesSet = null;
		
		Loan loan = (Loan)context.getValueObject();
		// we are setting this in a seperate variable because we don't wan't this to be updated and hence would be set
		// to null later but then again we need to set it in the value object because this is needed to be displayed on the 
		// jsp page in the UI.
		accountFeesSet = loan.getAccountFeesSet();
		// setting currency to null because it should not try to save currency.
		loan.setCurrency(null);
		loan.setCustomer(null);
		loan.setLoanOffering(null);
		loan.setLoanPenaltySet(null);
		loan.setAccountFeesSet(null);
		loan.setFund(null);
		super.update(context);
		// this is being set here because  this association was set to null
		// as we don't wan't this to get updated.
		loan.setAccountFeesSet(accountFeesSet);
		loan.setLoanOffering((LoanOffering)context.getBusinessResults(AccountConstants.SELECTEDPRDOFFERING));
	}


	/**
	 * This method fetches the header for the get call to be displayed on loan account detail page.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#fetchHeader(org.mifos.framework.util.valueobjects.Context, java.lang.String)
	 */
	public HeaderObject fetchHeader(Context context , String businessAction)throws SystemException,ApplicationException{
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside fetch header with method : " + businessAction);
		if("get".equals(businessAction)){
			Loan loan = ((Loan)context.getValueObject());
			LoanHeaderObject loanHeader = new LoanHeaderObject();
			CustomerMaster customerMaster = (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
			List<CustomerMaster> customerMasterList = CustomerUtilDAO.getParentHierarchy(loan.getCustomer().getCustomerId());
			Office office = (Office)DAO.getEntity("org.mifos.application.office.util.valueobjects.Office",loan.getOfficeId() ,DataTypeConstants.Short );
			String OfficeName = office.getOfficeName();
			loanHeader.setCustomerMasterList(customerMasterList);
			loanHeader.setOfficeName(OfficeName);
			loanHeader.setOfficeId(loan.getOfficeId());

			return loanHeader;
		}
		return null;
	}

	/**
	 * This method returns a list of changelogs for loan account.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void getLoanChangeLog(Context context)throws ApplicationException,SystemException{
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside LoanChangeLog ");
		Loan loan = (Loan)context.getValueObject();
		List changeLogList= new ClosedAccSearchDAO().getClientChangeLog(new Integer(loan.getAccountId()),LoanConstants.LOAN_ENTITY_TYPE);
		SearchResults searchResults = new SearchResults();
		searchResults.setValue(changeLogList);
		searchResults.setResultName(LoanConstants.LOAN_CHANGE_LOG_LIST);
		context.addAttribute(searchResults);
	}

	
   private Boolean isDisbursmentDateValid(Integer accountId,Short accountState,Date disbursmentDate) throws SystemException,ApplicationException{
	   if(accountState.equals(AccountStates.LOANACC_PARTIALAPPLICATION) || accountState.equals(AccountStates.LOANACC_PENDINGAPPROVAL)){
		   Calendar currentDateCalendar = new GregorianCalendar();
			int year = currentDateCalendar.get(Calendar.YEAR);
			int month = currentDateCalendar.get(Calendar.MONTH);
			int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
			currentDateCalendar = new GregorianCalendar(year, month, day);
		   if(disbursmentDate.compareTo(new Date(currentDateCalendar.getTimeInMillis()))>=0){
			   return true;
		   }else{
			   return false;
		   }
	   }else{
		   LoanDAO loanDAO = (LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
		   AccountStatusChangeHistory accountStatusChangeHistory=loanDAO.getAccountStatusChangeHistory(accountId,accountState);
		   if(disbursmentDate.compareTo(accountStatusChangeHistory.getChangedDate())>=0){
			   return true;
		   }else{
			   return false;
		   }
	   }
   }
   
   private void checkForDuplicatePeriodicFeeExist(Loan loan,List<FeeMaster> feeMasterList)throws SystemException,ApplicationException{
	   Set<AccountFees> accountFeesSet=loan.getAccountFeesSet();
	   for(AccountFees accountFees : accountFeesSet){
		   Set<AccountFees> duplicateAccountFeeSet = loan.getAccountFeesSet();
		   int i=0;
		   for(AccountFees duplicateAccountFees : duplicateAccountFeeSet){
			   if(accountFees.getFees().getFeeId() != null && accountFees.getFees().getFeeId().equals(duplicateAccountFees.getFees().getFeeId())){
				   Short feeFrequencyType=null;
				   String feeName=null;
				   for(FeeMaster feeMaster : feeMasterList){
					   if(accountFees.getFees().getFeeId().equals(feeMaster.getFeeId()))
					   {
						   feeFrequencyType=feeMaster.getFeeFrequencyTypeId();
						   feeName=feeMaster.getFeeName();
						   break;
					   }
				   }
				   if(feeFrequencyType!=null  && feeFrequencyType.equals(FeesConstants.PERIODIC)){
					   i++;
					   if(i>=2)
							throw new  ApplicationException(LoanExceptionConstants.DUPLICATEPERIODICFEE,new Object[]{feeName});
				   }
			   }
		   }
	   }
	}
   
 
   private Money getMiscFee(Set<AccountActionDate> accountActionDateSet){
		Money miscFees=new Money();
		if(accountActionDateSet!=null)
			for(AccountActionDate accountActionDate : accountActionDateSet){				
				if(accountActionDate.getMiscFee()!=null){
					miscFees=miscFees.add(accountActionDate.getMiscFee());
				}
			}
		return miscFees;
	}
	
	private Money getMiscPenalty(Set<AccountActionDate> accountActionDateSet){
		Money miscPenalty=new Money();
		if(accountActionDateSet!=null)
			for(AccountActionDate accountActionDate : accountActionDateSet ){
				if(accountActionDate.getMiscPenalty()!=null){
					miscPenalty=miscPenalty.add(accountActionDate.getMiscPenalty());
				}
			}
		return miscPenalty;
	}
}
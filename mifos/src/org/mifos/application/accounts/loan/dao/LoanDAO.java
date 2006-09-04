package org.mifos.application.accounts.loan.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.dao.AccountsDAO;
import org.mifos.application.accounts.loan.exceptions.LoanExceptionConstants;
import org.mifos.application.accounts.loan.exceptions.LoanUpdationException;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.loan.util.valueobjects.LoanPerformanceHistory;
import org.mifos.application.accounts.loan.util.valueobjects.RecentAccountActivity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountFlagDetail;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.application.accounts.util.valueobjects.AccountStatusChangeHistory;
import org.mifos.application.accounts.util.valueobjects.AccountTrxn;
import org.mifos.application.customer.client.util.valueobjects.ClientPerformanceHistory;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.valueobjects.FeeFrequency;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.master.util.valueobjects.FeeFormulaMaster;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.LoanOffering;
import org.mifos.application.productdefinition.util.valueobjects.LoanOfferingFund;
import org.mifos.application.productdefinition.util.valueobjects.PrdOffering;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingFees;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMaster;
import org.mifos.application.productdefinition.util.valueobjects.PrdOfferingMeeting;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.dao.helpers.DataTypeConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;
import org.mifos.framework.util.valueobjects.ValueObject;

public class LoanDAO extends AccountsDAO {

	public LoanDAO() {
		super();
	}

	/**
	 * This method initializes the required loan product offering fields.
	 * 
	 * @param prdOffering
	 * @param session
	 */
	public void initializePrdOffering(PrdOffering prdOffering, Session session)
			throws SystemException {
		LoanOffering loanOffering = (LoanOffering) prdOffering;
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"inside initialize product offering method. ");
		// initialize penalty object

		if (null != loanOffering.getPenalty()) {
			loanOffering.getPenalty().getPenaltyType();
		}

		// initialize sources of fund object.
		Set<LoanOfferingFund> loanOfferingFundSet = loanOffering
				.getLoanOffeingFundSet();
		if (null != loanOfferingFundSet && !loanOfferingFundSet.isEmpty()) {
			for (LoanOfferingFund loanOfferingFund : loanOfferingFundSet) {
				loanOfferingFund.getFund().getFundId();
				loanOfferingFund.getFund().getFundName();
			}
		}

		// get the fees from prdOffering and initializes it.
		Set<PrdOfferingFees> prdOfferingFeeSet = loanOffering
				.getPrdOfferingFeesSet();
		if (null != prdOfferingFeeSet && !prdOfferingFeeSet.isEmpty()) {
			for (PrdOfferingFees prdOfferingFee : prdOfferingFeeSet) {
				prdOfferingFee.getFees().getFeeId();
				prdOfferingFee.getFees().getFeeName();
				prdOfferingFee.getFees().getRateOrAmount();
				prdOfferingFee.getFees().getRateFlatFalg();
				prdOfferingFee.getFees().getFeeFrequency()
						.getFeeFrequencyTypeId();
				// if the fees is periodic type , the meeting details are being
				// retrieved for displaying on the jsp.
				if (FeeFrequencyType.PERIODIC.getValue().shortValue() == prdOfferingFee
						.getFees().getFeeFrequency().getFeeFrequencyTypeId()
						.shortValue()) {
					prdOfferingFee.getFees().getFeeFrequency()
							.getFeeMeetingFrequency().getMeetingPlace();
					Hibernate.initialize(prdOfferingFee.getFees()
							.getFeeFrequency().getFeeMeetingFrequency());
					prdOfferingFee.getFees().getFeeFrequency()
							.getFeeMeetingFrequency().getMeetingDetails()
							.getRecurAfter();
					prdOfferingFee.getFees().getFeeFrequency()
							.getFeeMeetingFrequency().getMeetingDetails()
							.getRecurrenceType().getDescription();

				}
			}

		}

		// initialize the meeting object.
		PrdOfferingMeeting prdOfferingMeeting = loanOffering
				.getPrdOfferingMeeting();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).warn(
				" prd offering meeting id is "
						+ prdOfferingMeeting.getMeetingType());
		prdOfferingMeeting.getMeeting().getMeetingDetails().getRecurAfter();
		prdOfferingMeeting.getMeeting().getMeetingDetails().getRecurrenceType()
				.getRecurrenceName();

	}

	/**
	 * This method gets the applicable product offerings.
	 * 
	 * @param customer
	 */
	public SearchResults getApplicableLoanProducts(Customer customer,
			Short recurAfter) throws SystemException {

		SearchResults applicableLoanPrdOfferings = new SearchResults();
		HashMap queryParameters = new HashMap();
		queryParameters.put(LoanConstants.PRDTYPEID,
				ProductDefinitionConstants.LOANID);
		queryParameters.put(LoanConstants.PRDSTATUS,
				ProductDefinitionConstants.LOANACTIVE);

		if (customer.getCustomerLevel().getLevelId().shortValue() == CustomerConstants.CLIENT_LEVEL_ID) {
			queryParameters.put(new String("prdApplicableMaster1"),
					PrdApplicableMaster.CLIENTS.getValue());
		} else {
			queryParameters.put(new String("prdApplicableMaster1"),
					PrdApplicableMaster.GROUPS.getValue());
		}
		queryParameters.put(new String("prdApplicableMaster2"),
				PrdApplicableMaster.ALLCUSTOMERS.getValue());
		queryParameters.put("customerId", customer.getCustomerId());
		List<PrdOfferingMaster> prdOfferingMasterList = executeNamedQuery(
				NamedQueryConstants.APPLICABLEPRODUCTOFFERINGS, queryParameters);
		List<PrdOfferingMaster> applicablePrdOfferingMasterList = new ArrayList<PrdOfferingMaster>();
		for (PrdOfferingMaster prdOfferingMaster : prdOfferingMasterList) {
			if (prdOfferingMaster.getRecurAfter() % recurAfter == 0)
				applicablePrdOfferingMasterList.add(prdOfferingMaster);
		}
		applicableLoanPrdOfferings.setValue(applicablePrdOfferingMasterList);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"above query executed successfully ");
		applicableLoanPrdOfferings
				.setResultName(LoanConstants.LOANPRDOFFERINGS);
		return applicableLoanPrdOfferings;

	}

	/**
	 * This method gets the applicable fees for loan accounts.These fee are
	 * apart from the ones inherited from prdOffering.
	 * 
	 * @throws SystemException
	 */
	public List getApplicableFees(Short prdOfferingId) throws SystemException {
		List queryList = null;
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Inside getApplicableFees method");
		// preparing the parameters for the query
		HashMap queryParameters = new HashMap();
		queryParameters.put(LoanConstants.CATEGORYID, Short
				.valueOf(LoanConstants.CATEGORYIDVALUE));
		queryParameters.put(LoanConstants.PRDOFFERINGID, prdOfferingId);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"setting the prdOfferingId for query "
						+ NamedQueryConstants.LOANACCOUNTFEES + " to be : "
						+ prdOfferingId);
		queryList = executeNamedQuery(NamedQueryConstants.LOANACCOUNTFEES,
				queryParameters);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"After executing the query named "
						+ NamedQueryConstants.LOANACCOUNTFEES);
		return queryList;

	}

	/**
	 * This method returnS a list of feeformulamaster with contains feeid and
	 * formula string.
	 * 
	 * @throws SystemException
	 */
	public List<FeeFormulaMaster> getFeeFormula(Short localeId)
			throws SystemException {
		Session session = null;
		List queryList = null;
		List<FeeFormulaMaster> feeForumulaList = null;
		try {
			session = HibernateUtil.getSession();
			HashMap queryParameters = new HashMap();
			queryParameters.put("localeId", localeId);
			queryParameters.put(LoanConstants.CATEGORYID, FeeCategory.LOAN
					.getValue());
			feeForumulaList = executeNamedQuery(
					NamedQueryConstants.GET_FEES_WITH_FORMULA_FOR_LOAN,
					queryParameters);
		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return feeForumulaList;
	}

	/**
	 * This method is called from Business Processor to find the loan account
	 * based on the global Account number.
	 * 
	 * @param globalAccountNum
	 * @return
	 * @throws SystemException
	 */
	public Account findBySystemId(String globalAccountNum)
			throws ApplicationException, SystemException {
		Session session = null;
		List queryResult = null;
		Loan loan = null;
		HashMap queryParameters = new HashMap();
		queryParameters
				.put(AccountConstants.GLOBALACCOUNTNUM, globalAccountNum);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"GLOBAL ACCOUNT NUMBER PASSED TO SEARCH IS "
								+ globalAccountNum);

		try {
			session = HibernateUtil.getSession();
			queryResult = executeNamedQuery(
					NamedQueryConstants.FINDBYGLOBALACCNUM, queryParameters,
					session);
			if (null != queryResult && queryResult.size() != 0
					&& null != queryResult.get(0)) {
				// getting only the first result because global account number
				// is unique and hence the query should
				// return only one row.
				loan = (Loan) queryResult.get(0);
				// initializing the things that would be needed on the jsp from
				// loan offering.
				LoanOffering loanOffering = (LoanOffering) getEntity(
						"org.mifos.application.productdefinition.util.valueobjects.LoanOffering",
						loan.getLoanOffering().getPrdOfferingId(),
						DataTypeConstants.Short, session);
				initializePrdOffering(loanOffering, session);
				// setting the loan offering in the loan object.
				loan.setLoanOffering(loanOffering);
				initializeAccount(loan, session);
			}

		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return loan;
	}

	/**
	 * This is to initialize the accounts object.This is required if one gets an
	 * account object which is lazily loaded.
	 * 
	 * @param loan
	 * @param session
	 */
	private void initializeAccount(Loan loan, Session session) {
		HashMap queryParameters = new HashMap();

		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"inside initializeAccount method ");
		// initializing the account fees.
		Set<AccountFees> accountFeeSet = loan.getAccountFeesSet();
		if (null != accountFeeSet && !accountFeeSet.isEmpty()) {
			for (AccountFees accountFees : accountFeeSet) {
				accountFees.getFees().getFeeFrequency().getFeeFrequencyTypeId();
				if (null != (accountFees.getFees().getFeeFrequency()
						.getFeeMeetingFrequency())) {
					accountFees.getFees().getFeeFrequency()
							.getFeeMeetingFrequency().getFeeMeetingSchedule();
				}
			}
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"after initializing the account fees");

		// initialize the loan summary object also
		loan.getLoanSummary().getOriginalPrincipal();
		// initialize the loan meeting object
		loan.getLoanMeeting().getMeetingStartDate();
		// initialize the account flag object
		queryParameters.put("accountId", loan.getAccountId());
		// query the account flag detail table for a given account id.
		List queryResult = executeNamedQuery(
				NamedQueryConstants.ACCOUNTFLAGFORGIVENACCOUNT,
				queryParameters, session);
		if (null != queryResult && queryResult.size() > 0) {
			AccountFlagDetail accountFlagDetail = (AccountFlagDetail) queryResult
					.get(0);
			loan.setAccountFlagDetail(accountFlagDetail);
		}
		Hibernate.initialize(loan.getAccountActionDateSet());

	}

	/**
	 * This method is called from business processor to find the loan object
	 * based on pk.
	 * 
	 * @param accountId
	 * @return
	 */
	public ValueObject findByPK(Integer accountId) throws SystemException,
			ApplicationException {
		Loan loan = new Loan();
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			// loan =
			// (Loan)getEntity("org.mifos.application.loan.util.valueobjects.Loan",accountId,DataTypeConstants.Integer,session);
			loan = (Loan) session.get(Loan.class, accountId);

			if (null != loan) {
				if (null != loan.getCustomer())
					loan.getCustomer().getPersonnelId();
			}
			initializeAccount(loan, session);
			Meeting loanMeeting = loan.getLoanMeeting();
			if (loanMeeting != null)
				loanMeeting.getMeetingPlace();

		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return loan;
	}

	/**
	 * Returns the initialized customer meeting.This method returns null if the
	 * customer does not have a customer meeting.
	 * 
	 * @param customerId
	 * @return
	 */
	public CustomerMeeting getCustomerMeeting(Integer customerId)
			throws SystemException, ApplicationException {
		Session session = null;
		HashMap queryParameters = new HashMap();
		CustomerMeeting customerMeeting = null;
		List queryResult = null;
		try {
			session = HibernateUtil.getSession();
			queryParameters.put("customerId", customerId);
			// customerMeeting =
			// (CustomerMeeting)((executeNamedQuery(NamedQueryConstants.GETCUSTOMERMEETING,queryParameters,session)).get(0));
			queryResult = executeNamedQuery(
					NamedQueryConstants.GETCUSTOMERMEETING, queryParameters,
					session);
			if (null == queryResult || queryResult.isEmpty()) {
				return customerMeeting;
			}
			// if the query result is not null , the first record should get me
			// the customer meeting.
			customerMeeting = (CustomerMeeting) queryResult.get(0);
			customerMeeting.getMeeting().getMeetingDetails().getRecurAfter();
			customerMeeting.getMeeting().getMeetingDetails()
					.getRecurrenceType().getRecurrenceName();
			customerMeeting.getMeeting().getMeetingDetails()
					.getMeetingRecurrence().getWeekDay();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"the week day loaded is "
							+ customerMeeting.getMeeting().getMeetingDetails()
									.getMeetingRecurrence().getWeekDay());
		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return customerMeeting;
	}

	/**
	 * This method returns the recent account activity list for the given
	 * account Id. It runs a query which orders the result in the descending
	 * order of account trxn id and then gets the first three results out.
	 * 
	 * @param accountId
	 * @param size
	 * @return
	 * @throws SystemException
	 */
	public List<RecentAccountActivity> getRecentAccountActivity(
			Integer accountId, int size) throws SystemException {
		Session session = null;
		List<RecentAccountActivity> recentAccActivityList = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.RECENTACCACTIVITY);
			query.setInteger("accountId", accountId);
			// since same query is being used for two jsp so firstly we will
			// check size, if size is equal to -1 then we will display full
			// result otherwise 3 results.
			if (size != (-1)) {
				// setting the fetch size to three because we need to display
				// only three records on the jsp.
				query.setFetchSize(size);
			}
			recentAccActivityList = query.list();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"after retrieving the recent account ActivityList ");
		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return recentAccActivityList;
	}

	/**
	 * It creates the ValueObject instance passed in the Context object in the
	 * database. This method gets the hibernate session , starts a transaction ,
	 * calls create on hibernate session and then commits and closes the
	 * hibernate session. It is overridden because apart from the loan object we
	 * also need to store account action dates , both of them would be saved as
	 * part of the same transaction and failure in insertion on one would cause
	 * the entire transaction to roll back.
	 * 
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void create(org.mifos.framework.util.valueobjects.Context context)
			throws ApplicationException, SystemException {
		Transaction tx = null;
		Session session = null;
		AccountStatusChangeHistory accntStateChangeHist = new AccountStatusChangeHistory();
		Loan loan = (Loan) context.getValueObject();
		RepaymentSchedule repaymentSchedule = (RepaymentSchedule) context
				.getBusinessResults(LoanConstants.REPAYMENTSCHEDULE);
		Set<AccountActionDate> accntActionDateSet = RepaymentScheduleHelper
				.getActionDateValueObject(repaymentSchedule,"Loan");
		try {
			session = getHibernateSession();
			tx = session.beginTransaction();
			if(loan.getCustomer().getCustomerLevel().getLevelId().equals(Short.valueOf(CustomerConstants.CLIENT_LEVEL_ID)) && loan.getCustomer().getCustomerPerformanceHistory()!=null)
				session.update((ClientPerformanceHistory)loan.getCustomer().getCustomerPerformanceHistory());
			session.save(loan);
			session.flush();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Account id after flushing the hibernate session is : "
							+ loan.getAccountId());

			// insert a record in account_status_change_history table
			// this will indicate the state chnage from New to Open.
			accntStateChangeHist.setAccountId(loan.getAccountId());
			accntStateChangeHist.setChangedDate(loan.getCreatedDate());
			accntStateChangeHist.setNewStatus(loan.getAccountStateId());
			accntStateChangeHist.setOldStatus(loan.getAccountStateId());
			accntStateChangeHist.setPersonnelId(loan.getPersonnelId());
			session.save(accntStateChangeHist);

			// this will insert records in account action date which is noting
			// but installments.
			if (null != accntActionDateSet && !accntActionDateSet.isEmpty()) {
				// iterate over account action date set and set the relation
				// ship.
				for (AccountActionDate accountActionDate : accntActionDateSet) {
					accountActionDate.setAccount(loan);
					accountActionDate.setCustomerId(loan.getCustomer()
							.getCustomerId());
					accountActionDate.setCurrencyId(Short.valueOf("1"));
					session.save(accountActionDate);
					MifosLogManager
							.getLogger(LoggerConstants.ACCOUNTSLOGGER)
							.debug(
									"After saving a record of account action dates. ");
				}
				
				LoanPerformanceHistory loanPerformanceHistory=new LoanPerformanceHistory();
				loan.setPerformanceHistory(loanPerformanceHistory);
				loanPerformanceHistory.setLoanMaturityDate(new Date(getLastActionDate(accntActionDateSet).getActionDate().getTime()));
			}
			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			throw new HibernateProcessException(e);

		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This method creates a new account fee set based on the account fee set
	 * passed to it. The passed set might have certain account fee objects with
	 * feeId as null.This would happen if the user does not select all the fee
	 * from the UI.So it adds only those account fee objects to the set where
	 * fee id is not null.It loads the new fee object with that fee id and also
	 * initializes the fee frequency object and sets that in the set.
	 * 
	 * @param accountFeesSet
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Set<AccountFees> initalizeAccountFees(Set<AccountFees> accountFeesSet)
			throws SystemException, ApplicationException {
		Session session = null;
		Set<AccountFees> returnableAccountFees = new HashSet<AccountFees>();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Inside get account fees method. ");
		try {
			session = HibernateUtil.getSession();
			if (null != accountFeesSet && !accountFeesSet.isEmpty()) {
				for (AccountFees accountFee : accountFeesSet) {
					Fees fee = accountFee.getFees();
					if (null == fee.getFeeId()) {
						MifosLogManager
								.getLogger(LoggerConstants.ACCOUNTSLOGGER)
								.debug(
										"The fee id is null hence this record should not be added to the set. ");
						continue;
					} else {
						MifosLogManager
								.getLogger(LoggerConstants.ACCOUNTSLOGGER)
								.debug(
										"while iterating over the account fee found an object where fee id is not null and is . "
												+ fee.getFeeId());
						MifosLogManager
								.getLogger(LoggerConstants.ACCOUNTSLOGGER)
								.debug(
										"while iterating over the account fee found an object where fee id is not null and check to remove value is  . "
												+ accountFee.getCheckToRemove());
						// This would be null if user selects the check to
						// remove check box in the UI.
						if (null == accountFee.getCheckToRemove()
								|| accountFee.getCheckToRemove().shortValue() == 0) {
							Fees fees = (Fees) getEntity(
									"org.mifos.application.fees.util.valueobjects.Fees",
									fee.getFeeId(), DataTypeConstants.Short,
									session);
							FeeFrequency feeFrequency = fees.getFeeFrequency();
							Hibernate.initialize(feeFrequency);
							// this would be null in case of one time fees.
							if (null != feeFrequency
									&& null != feeFrequency
											.getFeeMeetingFrequency()) {
								Hibernate.initialize(feeFrequency
										.getFeeMeetingFrequency());
							}
							accountFee.setFees(fees);
							returnableAccountFees.add(accountFee);
						}

					}
				}
			}
		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return returnableAccountFees;
	}

	/**
	 * This method is called to update loan account.It has been overridden
	 * because we need to delete the records from account action date and re -
	 * insert them if the repayment schedule was regenrated.
	 * 
	 * @param context
	 *            instance of Context
	 * @throws HibernateProcessException
	 */
	public void update(Context context) throws SystemException,
			ApplicationException {
		Loan loan = (Loan) context.getValueObject();
		// RepaymentSchedule repaymentSchedule =
		// (RepaymentSchedule)context.getBusinessResults(LoanConstants.REPAYMENTSCHEDULE);
		// Set<AccountActionDate> accntActionDateSet =
		// RepaymentScheduleHelper.getActionDateValueObject(repaymentSchedule);
		// CustomerMaster customerMaster =
		// (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
		// HashMap queryParameters = new HashMap();
		Transaction tx = null;
		Session session = null;
		// List<AccountActionDate> accountActionsDateList = null;
		try {
			LogValueMap logValueMap = new LogValueMap();
			logValueMap.put(AuditConstants.REALOBJECT, new Loan());
			// logValueMap.put("perfHistory",new LoanPerfHistory());
			// logValueMap.put("customer",new Customer());
			// logValueMap.put("account",new Account());
			// logValueMap.put("fund",new Fund());
			logValueMap.put("loanMeeting", AuditConstants.REALOBJECT);
			logValueMap.put("loanSummary", AuditConstants.REALOBJECT);
			// logValueMap.put("currency",new Currency());

			LogInfo logInfo = new LogInfo(loan.getAccountId(), "Accounts",
					context, logValueMap);
			session = HibernateUtil.getSessionWithInterceptor(logInfo);

			// session = HibernateUtil.getSession();
			tx = session.beginTransaction();

			updateAccountDateAndFeesDetails(session, context, loan);

			// commit the transaction
			tx.commit();
		} catch (HibernateException he) {
			tx.rollback();
			if (he instanceof StaleObjectStateException) {
				throw new ConcurrencyException(
						ExceptionConstants.CONCURRENCYEXCEPTION, he);
			} else {
				throw new LoanUpdationException(
						LoanExceptionConstants.LOANUPDATIONEXCEPTION, he);
			}

		} catch (HibernateProcessException hpe) {
			throw hpe;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This is an intermediate method that first deletes the records from the
	 * accountActionDate and accountFeesActionDetails table and then updates
	 * loan object
	 * 
	 * @param session
	 * @param context
	 * @param loan
	 * @param repaymentSchedule
	 */
	public void updateAccountDateAndFeesDetails(Session session,
			Context context, Loan loan) {
		HashMap queryParameters = new HashMap();
		if (loan.getAccountStateId() == AccountStates.LOANACC_APPROVED
				|| loan.getAccountStateId() == AccountStates.LOANACC_DBTOLOANOFFICER
				|| loan.getAccountStateId() == AccountStates.LOANACC_PARTIALAPPLICATION
				|| loan.getAccountStateId() == AccountStates.LOANACC_PENDINGAPPROVAL) {
			RepaymentSchedule repaymentSchedule = (RepaymentSchedule) context
					.getBusinessResults(LoanConstants.REPAYMENTSCHEDULE);
			Set<AccountActionDate> accntActionDateSet = RepaymentScheduleHelper
					.getActionDateValueObject(repaymentSchedule,"Loan");
			CustomerMaster customerMaster = (CustomerMaster) context
					.getBusinessResults(AccountConstants.CUSTOMERMASTER);
			List<AccountActionDate> accountActionsDateList = null;
			// get the list of account action dates
			queryParameters.put("accountId", loan.getAccountId());
			accountActionsDateList = executeNamedQuery(
					NamedQueryConstants.RETRIEVE_INSTALLMENTS, queryParameters,
					session);

			// iterate over the query and delete them one by one.
			if (null != accountActionsDateList) {
				for (AccountActionDate accountActionDate : accountActionsDateList) {
					session.delete(accountActionDate);
					loan.removeAccountActionDate(accountActionDate);
				}
			}

			// update loan
			// session.update(loan);

			// this will insert records in account action date which is noting
			// but installments.
			if (null != accntActionDateSet && !accntActionDateSet.isEmpty()) {
				// iterate over account action date set and set the relation
				// ship.
				for (AccountActionDate accountActionDate : accntActionDateSet) {
					accountActionDate.setAccount(loan);
					accountActionDate.setCustomerId(customerMaster
							.getCustomerId());
					accountActionDate.setCurrencyId(Short.valueOf("1"));
					// session.save(accountActionDate);
					loan.addAccountActionDate(accountActionDate);

					MifosLogManager
							.getLogger(LoggerConstants.ACCOUNTSLOGGER)
							.debug(
									"After saving a record of account action dates. ");
				}
				
				List<AccountActionDate> accountActionList =new ArrayList<AccountActionDate>(accntActionDateSet);
				loan.getPerformanceHistory().setLoanMaturityDate(new Date(getLastActionDate(accntActionDateSet).getActionDate().getTime()));
			}
		}
		session.flush();
		session.saveOrUpdate(loan);
		// loan.setAccountActionDateSet(accntActionDateSet);
	}

	/*
	 * This method returns the set of account fees which are active.
	 */
	public Set<AccountFees> getAccountFeesSet(Integer accountId)
			throws SystemException, ApplicationException {
		Transaction tx = null;
		Session session = null;
		List<AccountFees> accountFeesList = null;
		Set<AccountFees> accountFeesSet = null;
		try {
			session = HibernateUtil.getSession();
			HashMap queryParameters = new HashMap();
			queryParameters.put("accountId", accountId);
			queryParameters.put("feeStatus", AccountConstants.ACTIVE_FEES);
			accountFeesList = executeNamedQuery(
					NamedQueryConstants.ACCOUNT_GETACTIVEFEES, queryParameters,
					session);
			if (accountFeesList != null) {
				accountFeesSet = new HashSet<AccountFees>();
				for (AccountFees accountFees : accountFeesList) {
					accountFeesSet.add(accountFees);
				}
			}
		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return accountFeesSet;
	}

	public Date getPotentialDisbursementDate(Integer customerId, Date todaysDate)
			throws SystemException {
		HashMap queryParameters = new HashMap();
		queryParameters.put("customerId", customerId);
		queryParameters.put("todaysDate", todaysDate);
		MifosLogManager
				.getLogger(LoggerConstants.ACCOUNTSLOGGER)
				.debug(
						"Before executing the query for getting potential disbursement date. ");
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GETPOTENTIAL_DISBDATE, queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"After executing the query . ");
			return new Date(((java.util.Date) queryResult.get(0)).getTime());
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"After executing the query it returned no results. ");
		return null;
	}

	public AccountStatusChangeHistory getAccountStatusChangeHistory(
			Integer accountId, Short newState) throws SystemException {
		HashMap queryParameters = new HashMap();
		queryParameters.put("accountId", accountId);
		queryParameters.put("newState", newState);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_APPROVED_ACCOUNT_STATE, queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"After executing the query . ");
			return ((AccountStatusChangeHistory) queryResult.get(0));
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"After executing the query it returned no results. ");
		return null;

	}

	public Short getLastPaymentAction(Integer accountId) throws SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			HashMap queryParameters = new HashMap();
			queryParameters.put("accountId", accountId);
			List<AccountPayment> accountPaymentList = executeNamedQuery(
					NamedQueryConstants.RETRIEVE_MAX_ACCOUNTPAYMENT,
					queryParameters, session);
			if (accountPaymentList != null && accountPaymentList.size() > 0) {
				AccountPayment accountPayment = (AccountPayment) accountPaymentList
						.get(0);
				Set<AccountTrxn> accountTrxnSet = accountPayment
						.getAccountTrxn();
				for (AccountTrxn accountTrxn : accountTrxnSet) {
					if (accountTrxn.getAccountAction().getActionId()
							.shortValue() == AccountConstants.ACTION_DISBURSAL)
						return accountTrxn.getAccountAction().getActionId();
				}
			}
		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return null;
	}
	
	public Customer getCustomer(Integer customerId) throws SystemException {
		Session session = null;
		Customer customer = null;
		try {
			session = HibernateUtil.getSessionTL();
			customer = (Customer) session.get(Customer.class,customerId);
		}catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return customer;
	}
	
	public AccountActionDate getLastActionDate(Set<AccountActionDate> actionDateSet){
		AccountActionDate acctActionDate=null;
		for(AccountActionDate accountActionDate : actionDateSet){
			if(acctActionDate==null)
				acctActionDate=accountActionDate;
			else if(acctActionDate.getInstallmentId().compareTo(accountActionDate.getInstallmentId())<0)
				acctActionDate=accountActionDate;
		}
		return acctActionDate;
	}
}

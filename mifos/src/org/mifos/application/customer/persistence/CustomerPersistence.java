package org.mifos.application.customer.persistence;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerStateEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.struts.tags.DateHelper;

public class CustomerPersistence extends Persistence {

	public CustomerPersistence() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<CustomerView> getChildrenForParent(Integer customerId,
			String searchId, Short officeId) throws SystemException,
			ApplicationException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		List<CustomerView> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CHILDREN_FORPARENT,
				queryParameters);
		return queryResult;

	}

	public List<CustomerView> getActiveParentList(Short personnelId,
			Short customerLevelId, Short officeId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("personnelId", personnelId);
		queryParameters.put("customerLevelId", customerLevelId);
		queryParameters.put("officeId", officeId);

		List<CustomerView> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_PARENTCUSTOMERS_FOR_LOANOFFICER,
				queryParameters);
		return queryResult;

	}

	public List<PrdOfferingBO> getLoanProducts(Date meetingDate,
			String searchId, Short personnelId) throws ApplicationException,
			SystemException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("meetingDate", meetingDate);
		queryParameters.put("searchId", searchId + "%");
		queryParameters.put("personnelId", personnelId);
		List<PrdOfferingBO> queryResult = executeNamedQuery(
				NamedQueryConstants.BULKENTRYPRODUCTS, queryParameters);
		return queryResult;

	}

	public List<PrdOfferingBO> getSavingsProducts(Date meetingDate,
			String searchId, Short personnelId) {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("meetingDate", meetingDate);
		queryParameters.put("searchId", searchId + "%");
		queryParameters.put("personnelId", personnelId);
		List<PrdOfferingBO> queryResult = executeNamedQuery(
				NamedQueryConstants.BULKENTRYSAVINGSPRODUCTS, queryParameters);
		return queryResult;

	}

	public Date getLastMeetingDateForCustomer(Integer customerId)
			throws ApplicationException, SystemException {

		Date meetingDate = null;
		Date actionDate = new java.sql.Date(Calendar.getInstance().getTime()
				.getTime());
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CUSTOMER_ID", customerId);
		queryParameters.put("ACTION_DATE", actionDate);
		List<AccountActionDateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_LAST_MEETINGDATE_FOR_CUSTOMER,
				queryParameters);
		if (queryResult != null && queryResult.size() != 0)
			meetingDate = queryResult.get(0).getActionDate();
		return meetingDate;

	}

	public CustomerBO getCustomer(Integer customerId) {
		Session session = HibernateUtil.getSessionTL();
		CustomerBO customer = (CustomerBO) session.get(CustomerBO.class,
				customerId);
		return customer;
	}
	
	public CustomerBO findBySystemId(String globalCustNum)throws PersistenceException {
		Map<String,String> queryParameters = new HashMap<String,String>();
		CustomerBO customer = null;
		queryParameters.put("globalCustNum", globalCustNum);
		try{
			List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
			if(null != queryResult && queryResult.size() > 0){
				customer = queryResult.get(0);
			}
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		
		return customer;
	}
	public CustomerBO getBySystemId(String globalCustNum, Short levelId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		CustomerBO customer = null;
		queryParameters.put("globalCustNum", globalCustNum);
		try {

			if (levelId.shortValue() == CustomerConstants.CENTER_LEVEL_ID) {
				List<CenterBO> queryResult = executeNamedQuery(
						NamedQueryConstants.GET_CENTER_BY_SYSTEMID,
						queryParameters);
				if (null != queryResult && queryResult.size() > 0) {
					customer = queryResult.get(0);
					initializeCustomer(customer);
				}
			} else if (levelId.shortValue() == CustomerConstants.GROUP_LEVEL_ID) {
				List<GroupBO> queryResult = executeNamedQuery(
						NamedQueryConstants.GET_GROUP_BY_SYSTEMID,
						queryParameters);
				if (null != queryResult && queryResult.size() > 0) {
					customer = queryResult.get(0);
					initializeCustomer(customer);
				}


			} else if (levelId.shortValue() == CustomerConstants.CLIENT_LEVEL_ID) {
				List<ClientBO> queryResult = executeNamedQuery(
						NamedQueryConstants.GET_CLIENT_BY_SYSTEMID,
						queryParameters);
				if (null != queryResult && queryResult.size() > 0) {
					customer = queryResult.get(0);
					initializeCustomer(customer);
				}


			}
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}

		return customer;
	} 
	
	private void initializeCustomer(CustomerBO customer){
		customer.getGlobalCustNum();
		customer.getOffice().getOfficeId();
		customer.getOffice().getOfficeName();
		customer.getCustomerLevel().getLevelId();
		customer.getDisplayName();
		if (customer.getParentCustomer()!=null){
			customer.getParentCustomer().getGlobalCustNum();
			customer.getParentCustomer().getCustomerId();
			customer.getParentCustomer().getCustomerLevel().getLevelId();
			if( customer.getParentCustomer().getParentCustomer()!=null){
				customer.getParentCustomer().getParentCustomer().getGlobalCustNum();
				customer.getParentCustomer().getParentCustomer().getCustomerId();
				customer.getParentCustomer().getParentCustomer().getCustomerLevel().getLevelId();
			}
		}

	}
	public List<CustomerBO> getChildrenForParent(String searchId,
			Short officeId, Short customerLevelId) throws PersistenceException {
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("SEARCH_STRING", searchId + ".%");
			queryParameters.put("OFFICE_ID", officeId);
			queryParameters.put("LEVEL_ID", customerLevelId);
			List<CustomerBO> queryResult = executeNamedQuery(
					NamedQueryConstants.GET_CHILDREN, queryParameters);
			return queryResult;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}
	
	public List<SavingsBO> retrieveSavingsAccountForCustomer(Integer customerId)throws PersistenceException{
		try{
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("customerId", customerId);
			return (List<SavingsBO>) executeNamedQuery(NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER,queryParameters);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
	}
	public CustomerPerformanceHistoryView numberOfMeetings(boolean isPresent , Integer customerId) throws HibernateProcessException{				
		Session session = null;
		Query query = null;
		CustomerPerformanceHistoryView customerPerformanceHistoryView = new CustomerPerformanceHistoryView();		
		try {
			session = HibernateUtil.getSessionTL();
			String systemDate = DateHelper.getCurrentDate(Configuration.getInstance().getSystemConfig().getMFILocale()); 
			Date localDate = DateHelper.getLocaleDate(Configuration.getInstance().getSystemConfig().getMFILocale(), systemDate);
			Calendar currentDate = new GregorianCalendar();
			currentDate.setTime(localDate);
			currentDate.add(currentDate.YEAR,-1);			
			Date dateOneYearBefore = new Date(currentDate.getTimeInMillis());			
			if(isPresent){
				query = session.getNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSATTENDED);				
				query.setInteger("CUSTOMERID",customerId);
				query.setDate("DATEONEYEARBEFORE",dateOneYearBefore);
				customerPerformanceHistoryView.setMeetingsAttended((Integer) query.uniqueResult());				
			}
			else{
				query = session.getNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSMISSED);
				query.setInteger("CUSTOMERID",customerId);
				query.setDate("DATEONEYEARBEFORE",dateOneYearBefore);
				customerPerformanceHistoryView.setMeetingsMissed((Integer) query.uniqueResult());				
			}		
		}
		catch(HibernateException he){
			throw he;
		}
		
		return customerPerformanceHistoryView;
	}
	
	public CustomerPerformanceHistoryView getLastLoanAmount(Integer customerId)throws PersistenceException{
		try{
			Query query = null;
			Session session = HibernateUtil.getSessionTL();
			CustomerPerformanceHistoryView customerPerformanceHistoryView = null;
			if(null != session){
				query = session.getNamedQuery(NamedQueryConstants.GETLASTLOANAMOUNT);				
			}	
			query.setInteger("CUSTOMERID",customerId);			
			Object obj = query.uniqueResult();			
			if(obj!=null){
				customerPerformanceHistoryView = new CustomerPerformanceHistoryView();
				customerPerformanceHistoryView.setLastLoanAmount(query.uniqueResult().toString());				
			}				
			
			return customerPerformanceHistoryView;
			
		}catch(HibernateException he){
			he.printStackTrace();
			throw new PersistenceException(he);
		}
	}
	
	public List<CustomerStateEntity> getCustomerStates(Short optionalFlag)throws PersistenceException{
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OPTIONAL_FLAG", optionalFlag);
		List<CustomerStateEntity> queryResult=null;
		try{
			queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATES, queryParameters);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return queryResult;
	}
}

package org.mifos.application.customer.persistence.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerStateEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.service.PersistenceService;

public class CustomerPersistenceService extends PersistenceService {

	private CustomerPersistence serviceImpl = new CustomerPersistence();

	public List<CustomerView> getListOfActiveParentsUnderLoanOfficer(
			Short personnelId, Short customerLevel, Short officeId) {
		return serviceImpl.getActiveParentList(personnelId, customerLevel,
				officeId);
	}

	public List<CustomerView> getChildrenForParent(Integer customerId,
			String searchId, Short officeId) throws SystemException,
			ApplicationException {
		return serviceImpl.getChildrenForParent(customerId, searchId, officeId);
	}
	
	public List<Integer> getChildrenForParent(
			String searchId, Short officeId) throws SystemException,
			ApplicationException {
		return serviceImpl.getChildrenForParent(searchId, officeId);
	}
	
	public List<PrdOfferingBO> getLoanProductsAsOfMeetingDate(Date meetingDate,
			String searchId, Short personnelId) throws SystemException,
			ApplicationException {
		return serviceImpl.getLoanProducts(meetingDate, searchId, personnelId);
	}

	public List<PrdOfferingBO> getSavingsProductsAsOfMeetingDate(
			Date meetingDate, String searchId, Short personnelId) {
		return serviceImpl.getSavingsProducts(meetingDate, searchId,
				personnelId);
	}

	public Date getLastMeetingDateForCustomer(Integer customerId)
			throws SystemException, ApplicationException {
		return serviceImpl.getLastMeetingDateForCustomer(customerId);
	}

	public CustomerBO getCustomer(Integer customerId) {

		return serviceImpl.getCustomer(customerId);
	}
	
	public CustomerBO findBySystemId(String globalCustNum) throws PersistenceException {
		return serviceImpl.findBySystemId(globalCustNum);
	}
	public CustomerBO getBySystemId(String globalCustNum,Short levelId) throws PersistenceException {
		return serviceImpl.getBySystemId(globalCustNum,levelId);
	}	
	public List<CustomerBO> getChildrenForParent(String searchId, Short officeId, Short customerLevelId) throws PersistenceException {
		return serviceImpl.getChildrenForParent(searchId, officeId,customerLevelId);
	}
	
	public List<SavingsBO> retrieveSavingsAccountForCustomer(Integer customerId)throws PersistenceException{
		return serviceImpl.retrieveSavingsAccountForCustomer(customerId);
	}

	public List<LoanCycleCounter> fetchLoanCycleCounter(Integer customerId) {
		HashMap<String,Integer> queryParameters = new HashMap<String,Integer>();
		queryParameters.put("customerId", customerId);
		List queryResult = serviceImpl.executeNamedQuery(NamedQueryConstants.FETCH_LOANCOUNTERS, queryParameters);
		if(null != queryResult && queryResult.size()>0){
			MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug("Fetch loan cycle counter query returned : " +queryResult.size()+" rows" );
			List<LoanCycleCounter> loanCycleCounters = new ArrayList<LoanCycleCounter>();
			for(Object obj:queryResult){
				String prdOfferingName = (String)obj;
				MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug("Prd offering name of the loan account is " + prdOfferingName );
				int counter = 1;
				LoanCycleCounter loanCycleCounter = new LoanCycleCounter(prdOfferingName,counter);
				if(!loanCycleCounters.contains(loanCycleCounter)){
					MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug("Prd offering name " + prdOfferingName + " does not already exist in the list hence adding it to the list");
					loanCycleCounters.add(loanCycleCounter);
				}else{
					MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug("Prd offering name " + prdOfferingName + " already exists in the list hence incrementing the counter.");
					for(LoanCycleCounter loanCycle:loanCycleCounters){
						if(loanCycle.getOfferingName().equals(prdOfferingName)){
							loanCycle.incrementCounter();
						}
					}
				}
			}
			return loanCycleCounters;
		}
		MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug("Fetch loan cycle counter query returned : 0 rows" );
		return null;
	}
	public CustomerPerformanceHistoryView getLastLoanAmount(Integer customerId)throws PersistenceException{
		return serviceImpl.getLastLoanAmount(customerId);
	}
	public CustomerPerformanceHistoryView numberOfMeetings(boolean isPresent , Integer customerId)throws HibernateProcessException{
		return serviceImpl.numberOfMeetings(isPresent,customerId);
	}
	
	public void update(CustomerBO customer) {
		serviceImpl.createOrUpdate(customer);
	}
	
	public List<CustomerStateEntity> getCustomerStates(Short optionalFlag)throws PersistenceException{
		return serviceImpl.getCustomerStates(optionalFlag);
	}
	
	public List<Integer> getCustomersWithUpdatedMeetings() throws PersistenceException{
		return serviceImpl.getCustomersWithUpdatedMeetings();
	}
	
	public List<AccountBO> retrieveAccountsUnderCustomer(String searchId, Short officeId, Short accountTypeId)throws PersistenceException{
		return serviceImpl.retrieveAccountsUnderCustomer(searchId,officeId,accountTypeId);
	}
	
	public List<CustomerBO> getAllChildrenForParent(String searchId, Short officeId,Short customerLevelId) throws PersistenceException {
		return serviceImpl.getAllChildrenForParent(searchId, officeId,customerLevelId);
	}
}

/**

 * CustomerSearchDAO.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.customer.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerLevel;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelLevel;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;

/**
 * This class is used as DAO to search for customers.
 * @author ashishsm
 *
 */
public class CustomerSearchDAO extends DAO {

	/**
	 * default constructor
	 */
	public CustomerSearchDAO() {
	}
	
	/**
	 * Returns the Personnel Object
	 * @param personnelId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Personnel getPersonnel(Short personnelId) throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getPersonnel method of CustomerSearchDAO ");
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Personnel personnel= (Personnel)session.get(Personnel.class,personnelId);
			PersonnelLevel personnelLevel= personnel.getLevel();
			if(null !=personnelLevel) {
				personnelLevel.getLevelId();
			}
			return  personnel;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	} 
	
	/**
	 * Reurns the office object of the given officeId
	 * @param officeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Office getOffice(Short officeId) throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getOffice method of CustomerSearchDAO ");
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Office  office= (Office)session.get(Office.class,officeId);
			OfficeLevel officeLevel=office.getLevel();
			if(null != officeLevel) {
				officeLevel.getLevelId();
			}
			return  office;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	} 
	/**
	 * Returns a list of objects of type PersonnelMaster for an OfficeId .  
	 * It gets the Loan Officers irrespective of their status. 
	 * @param officeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<Personnel> getLoanOfficers(Short officeId)  throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getCustomers method of CustomerSearchDAO ");
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery(NamedQueryConstants.CUSTOMERGETACTIVELOANOFFICERS);
			query.setShort(CustomerSearchConstants.OFFICEID,officeId);
			query.setShort(CustomerSearchConstants.PERSONNELLEVELID,PersonnelConstants.LOAN_OFFICER);
			query.setShort(PersonnelConstants.LOANOFFICERACTIVE,PersonnelConstants.ACTIVE);
			List<Personnel> personnelList=query.list();
			return personnelList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}
	/**
	 * Returns a list of objects of type CustomerMaster for a loanOfficerId .  
	 * It gets the centers irrespective of their status. 
	 * @param officeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<Customer>  getCustomers(Short personnelId,Short officeId,Short custometLevelId) throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getCustomers method of CustomerSearchDAO ");
		Session session = null;
		Query query = null;
		try {
			session = HibernateUtil.getSession();
			if(custometLevelId.shortValue()==3)
			{			
			query = session.getNamedQuery(NamedQueryConstants.GETLOANOFFICERCENTERS);
			query.setShort(CustomerSearchConstants.PERSONNELID,personnelId);
			query.setShort(CustomerSearchConstants.OFFICEID,officeId);
			query.setShort(CustomerSearchConstants.CUSTOMERLEVELID,custometLevelId);	
			query.setShort(CustomerSearchConstants.CENTER_ACTIVE,CustomerSearchConstants.CENTERACTIVE);			
			}
			else
			{			
				query = session.getNamedQuery(NamedQueryConstants.LOANOFFICERGROUPS);
				query.setShort(CustomerSearchConstants.PERSONNELID,personnelId);
				query.setShort(CustomerSearchConstants.OFFICEID,officeId);
				query.setShort(CustomerSearchConstants.CUSTOMERLEVELID,custometLevelId);
				query.setShort(CustomerSearchConstants.GROUP_ACTIVE,CustomerSearchConstants.GROUPACTIVE);
				query.setShort(CustomerSearchConstants.GROUP_ONHOLD,CustomerSearchConstants.GROUPONHOLD);				
			}			
			List<Customer> customerList=query.list();			
			return customerList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}
	/**
	 * Returns List which contain list of objects of type OfficeMaster based on the datascope of the 
	 * passed office id . The search returns only offices of type BranchOffice. The search is irrespective of the
	 * status of the branch.
	 * @param officeId - Office Id of the logged in user
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<Office> getOffices(String officeSearchId) throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getOffices method of CustomerSearchDAO ");
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery(NamedQueryConstants.CUSTOMERGETACTIVEOFFICES);
			query.setShort(CustomerSearchConstants.OFFICELEVELID,OfficeConstants.BRANCHOFFICE);
			query.setString(CustomerSearchConstants.OFFICESEARCHID,officeSearchId+"%");			
			query.setShort(OfficeConstants.OFFICE_ACTIVE,OfficeConstants.ACTIVE);
			List<Office> officeList=query.list();			
			return officeList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception exception) {
			throw new ApplicationException(exception);
		}finally {
			HibernateUtil.closeSession(session);
		}
	}
	/**
	 * Returns a list of Customers for an officeId wrapped in QueryResult object.  
	 * It gets the centers irrespective of their status.This result is displayed on the UI using the 
	 * table tag. 
	 * @param officeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public QueryResult getCustomerSearchResults(Short officeId,String searchString) throws SystemException,ApplicationException {
		QueryResult customerSearchResults=null;
		try{
			Session session=null;
			customerSearchResults = QueryFactory.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
			session = customerSearchResults.getSession();
			Query query=null;
			//TODO change this
			if(officeId.shortValue()!=0) {
				query= session.createQuery(
		 				" select c.displayName ,c.globalCustNum,c.customerLevel.levelId,"+
		 				" off.officeId,off.officeName,per.displayName,"+
		 				" per.personnelId,c.statusId, " +
		 				" pa.displayName, pa.globalCustNum, "+
		 				" papa.displayName,papa.globalCustNum,a.globalAccountNum "+
		 				" from org.mifos.application.customer.util.valueobjects.Customer as c left join "+
		 				" c.customerAccounts as a  left join c.parentCustomer as pa " +
		 				" left join pa.parentCustomer as papa"+
		 				" left join c.office as off  left join c.personnel as per" +
		 				" where c.office.officeId="+officeId+" and c.displayName like '"+searchString+"%'");
			}
			else {
				query= session.createQuery(
						" select c.displayName ,c.globalCustNum,c.customerLevel.levelId,"+
		 				" off.officeId,off.officeName,per.displayName,"+
		 				" per.personnelId,c.statusId, " +
		 				" pa.displayName, pa.globalCustNum, "+
		 				" papa.displayName,papa.globalCustNum,a.globalAccountNum "+
		 				" from org.mifos.application.customer.util.valueobjects.Customer as c left join "+
		 				" c.customerAccounts as a  left join c.parentCustomer as pa " +
		 				" left join pa.parentCustomer as papa"+
		 				" left join c.office as off  left join c.personnel as per" +
		 				" where c.displayName like '"+searchString+"%'");
			}
			customerSearchResults.executeQuery(query);
	 		 String[] aliasNames = {"centerName" , "centerGlobalCustNum" , "customerType" , "branchGlobalNum",
	 				 "branchName" , "loanOfficerName" , "loanOffcerGlobalNum","customerStatus",
	 				 "groupName","groupGlobalCustNum","clientName","clientGlobalCustNum","loanGlobalAccountNumber"};
			 QueryInputs inputs = new QueryInputs();
			 inputs.setPath("org.mifos.application.customer.util.valueobjects.CustomerSearch");
			 inputs.setAliasNames(aliasNames);
			 inputs.setTypes(query.getReturnTypes());

			 customerSearchResults.setQueryInputs(inputs);
	 	}
		catch(HibernateProcessException  hpe) {
			throw new SystemException();
		}
      return customerSearchResults;
	}
}

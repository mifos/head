package org.mifos.application.personnel.persistence;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class PersonnelPersistence extends Persistence {

	public List<PersonnelView> getActiveLoanOfficersInBranch(Short levelId,
			Short officeId, Short userId, Short userLevelId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("levelId", levelId);
		queryParameters.put("userId", userId);
		queryParameters.put("userLevelId", userLevelId);
		queryParameters.put("officeId", officeId);
		queryParameters.put("statusId",
				CustomerConstants.LOAN_OFFICER_ACTIVE_STATE);
		List<PersonnelView> queryResult = executeNamedQuery(
				NamedQueryConstants.MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH,
				queryParameters);
		return queryResult;
	}

	public PersonnelBO getPersonnel(Short personnelId)  {
		
		Session session = HibernateUtil.getSessionTL();
		PersonnelBO personnel = (PersonnelBO) session.get(PersonnelBO.class,
				personnelId);
		return personnel;
		
	}

	public boolean isUserExist(String userName) throws PersistenceException{
		try{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("USER_NAME", userName);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_NAME,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		}
		catch ( HibernateException e ){
			throw new PersistenceException(e);
		}
		return false;
	}
	public boolean isUserExistWithGovernmentId(String governmentId) throws PersistenceException{
		try{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("GOVT_ID", governmentId);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_GOVERNMENTID,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		}
		catch ( HibernateException e ){
			throw new PersistenceException(e);
		}
		return false;
	}
	public boolean isUserExist(String displayName,Date dob) throws PersistenceException{
		
		try{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("DISPLAY_NAME", displayName);
		queryParameters.put("DOB",dob);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		}
		catch ( HibernateException e ){
			throw new PersistenceException(e);
		}
		
		return false;
	}

	public boolean getActiveChildrenForLoanOfficer(Short personnelId, Short officeId) throws PersistenceException {
		try{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("userId", personnelId);
		queryParameters.put("officeId",officeId);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMERS_FOR_LO,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		}
		catch ( HibernateException e ){
			throw new PersistenceException(e);
		}
		
		return false;
	}
	
	public boolean getAllChildrenForLoanOfficer(Short personnelId,
			Short officeId) throws PersistenceException {
		
		try{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("userId", personnelId);
		queryParameters.put("officeId",officeId);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_ALL_CUSTOMERS_FOR_LO,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		}
		catch ( HibernateException e ){
			throw new PersistenceException(e);
		}
		
		return false;
	}
	
	public PersonnelBO getPersonnelByGlobalPersonnelNum(String globalPersonnelNum
			) throws PersistenceException {
		
		try{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("globalPersonnelNum", globalPersonnelNum);
		
		PersonnelBO personnelBO = (PersonnelBO)execUniqueResultNamedQuery(NamedQueryConstants.PERSONNEL_BY_SYSTEM_ID,queryParameters);
		if(personnelBO!=null ){
			return personnelBO;
		}
		}
		catch ( HibernateException e ){
			throw new PersistenceException(e);
		}
		
		return null;
	}
}

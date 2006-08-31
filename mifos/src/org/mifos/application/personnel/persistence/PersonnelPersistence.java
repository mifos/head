package org.mifos.application.personnel.persistence;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
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

	public PersonnelBO getPersonnel(Short personnelId) {
		Session session = HibernateUtil.getSessionTL();
		PersonnelBO personnel = (PersonnelBO) session.get(PersonnelBO.class,
				personnelId);
		return personnel;
	}

	public boolean isUserExist(String userName){
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("USER_NAME", userName);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_NAME,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		return false;
	}
	public boolean isUserExistWithGovernmentId(String governmentId){
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("GOVT_ID", governmentId);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_GOVERNMENTID,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		return false;
	}
	public boolean isUserExist(String displayName,Date dob){
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("DISPLAY_NAME", displayName);
		queryParameters.put("DOB",dob);
		Integer count = (Integer)execUniqueResultNamedQuery(NamedQueryConstants.GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME,queryParameters);
		if(count!=null ){
			return count>0?true:false;
		}
		return false;
	}
}

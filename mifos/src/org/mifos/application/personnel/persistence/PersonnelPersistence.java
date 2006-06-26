package org.mifos.application.personnel.persistence;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class PersonnelPersistence extends Persistence {

	
	/**
	 * This method retrieves the list of loan officers under the office in which the search is being done
	 * If the logged user is a loan officer only his record is retrieved. else all loan officers in the data scope
	 * of the branch is retrieved.
	 * @return The list of loan officers that can be assigned for the group.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List<PersonnelView> getActiveLoanOfficersInBranch(Short levelId, Short officeId , Short userId, Short userLevelId){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("levelId",levelId);
		queryParameters.put("userId",userId);
		queryParameters.put("userLevelId",userLevelId);
		queryParameters.put("officeId",officeId);
		queryParameters.put("statusId",CustomerConstants.LOAN_OFFICER_ACTIVE_STATE);
		List<PersonnelView> queryResult = executeNamedQuery(NamedQueryConstants.MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH,queryParameters);	
		return queryResult;
				
	}
	
	public PersonnelBO getPersonnel(Short personnelId) {
		Session session = HibernateUtil.getSessionTL();
		PersonnelBO personnel = (PersonnelBO) session.get(PersonnelBO.class,
				personnelId);
		return personnel;
	}

}

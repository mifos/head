package org.mifos.application.office.persistence;

import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.OfficeCacheView;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.security.util.resources.SecurityConstants;

public class OfficePersistence extends Persistence {

	public OfficePersistence() {
		super();
		
	}

	public List<OfficeView> getActiveOffices(Short officeId) {
		 String searchId = HierarchyManager.getInstance().getSearchId(officeId);
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("levelId",OfficeConstants.BRANCHOFFICE);
		queryParameters.put("OFFICESEARCHID",searchId);
		queryParameters.put("OFFICE_LIKE_SEARCHID",searchId+".%");
		queryParameters.put("statusId",OfficeConstants.ACTIVE);
		List<OfficeView> queryResult = executeNamedQuery(NamedQueryConstants.MASTERDATA_ACTIVE_BRANCHES,queryParameters);	
		return queryResult;
		
	}
	
	public List<OfficeCacheView> getAllOffices()throws PersistenceException{
		List<OfficeCacheView> officeList = null;
		try {
			officeList = executeNamedQuery(NamedQueryConstants.GET_ALL_OFFICES,null);
		}catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return officeList;
	}
	
	public String getSearchId(Short officeId) {
		String searchId = "";
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("OFFICE_ID",officeId);
		List<OfficeSearch> queryResult = executeNamedQuery(NamedQueryConstants.OFFICE_GET_SEARCHID,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			OfficeSearch officeSearch = (OfficeSearch)queryResult.get(0);
			//searchId=officeSearch.getSearchId();
		}
		return searchId;
		
	}
	
	public OfficeBO getOffice(Short officeId) {
		Session session = HibernateUtil.getSessionTL();
		return (OfficeBO)session.get(OfficeBO.class,officeId);
		
	}
	
	public OfficeBO getHeadOffice() {
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("LEVEL_ID",OfficeConstants.HEADOFFICE);
		List<OfficeBO> queryResult = executeNamedQuery(NamedQueryConstants.OFFICE_GET_HEADOFFICE,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return queryResult.get(0);
		}
		return null;
	}

}

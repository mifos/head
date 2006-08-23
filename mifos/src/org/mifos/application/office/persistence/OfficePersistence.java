package org.mifos.application.office.persistence;

import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
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
		List<String> queryResult = executeNamedQuery(NamedQueryConstants.OFFICE_GET_SEARCHID,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			searchId = (String)queryResult.get(0);
			
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

	public Short getMaxOfficeId(){
		List queryResult = executeNamedQuery(NamedQueryConstants.GETMAXOFFICEID,null);	
		if(queryResult !=null && queryResult.size()!=0){
			return (Short)queryResult.get(0);
		}
		return null;
	}
	public Integer getChildCount(Short officeId){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("OFFICE_ID",officeId);
		List queryResult = executeNamedQuery(NamedQueryConstants.GETCHILDCOUNT,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return (Integer)queryResult.get(0);
		}
		return null;
		
	}
	public boolean isOfficeNameExist(String  officeName){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("OFFICE_NAME",officeName);
		List queryResult = executeNamedQuery(NamedQueryConstants.CHECKOFFICENAMEUNIQUENESS,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return (Integer)queryResult.get(0) >0 ?true:false;
		}
		return false;
	}
	public boolean isOfficeShortNameExist(String  shortName){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("SHORT_NAME",shortName);
		List queryResult = executeNamedQuery(NamedQueryConstants.CHECKOFFICESHORTNAMEUNIQUENESS,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return (Integer)queryResult.get(0) >0 ?true:false;
		}
		return false;
	}
	public boolean hasActiveChildern(Short officeId){
		
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("OFFICE_ID",officeId);
		List queryResult = executeNamedQuery(NamedQueryConstants.GETCOUNTOFACTIVECHILDERN,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return (Integer)queryResult.get(0)>0?true:false;
		}
		return false;
	}
	public boolean hasActivePeronnel(Short officeId){
		
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("OFFICE_ID",officeId);
		queryParameters.put("STATUS_ID",PersonnelConstants.ACTIVE);
		List queryResult = executeNamedQuery(NamedQueryConstants.GETOFFICEACTIVEPERSONNEL,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return (Integer)queryResult.get(0)>0?true:false;
		}
		return false;
	}
	public List<OfficeView> getActiveParents(OfficeLevel level,Short localeId){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("LEVEL_ID",level.getValue());
		queryParameters.put("STATUS_ID",OfficeStatus.ACTIVE.getValue());
		queryParameters.put("LOCALE_ID",localeId);
		List<OfficeView> queryResult = executeNamedQuery(NamedQueryConstants.GETACTIVEPARENTS,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return queryResult; 
		}
		return null;
		
	}
	
	public List<OfficeView> getActiveLevels(Short localeId){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("LOCALE_ID",localeId);
		List<OfficeView> queryResult = executeNamedQuery(NamedQueryConstants.GETACTIVELEVELS,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return queryResult; 
		}
		return null;
		
	}
	public List<OfficeView> getStatusList(Short localeId){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("LOCALE_ID",localeId);
		List<OfficeView> queryResult = executeNamedQuery(NamedQueryConstants.GETOFFICESTATUS,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return queryResult; 
		}
		return null;
	}
	public List<OfficeBO> getChildern(Short officeId){
		HashMap<String , Object> queryParameters = new HashMap<String , Object>();
		queryParameters.put("OFFICE_ID",officeId);
		List<OfficeBO> queryResult = executeNamedQuery(NamedQueryConstants.GETCHILDERN,queryParameters);	
		if(queryResult !=null && queryResult.size()!=0){
			return queryResult; 
		}
		return null;
	}
}

package org.mifos.application.personnel.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;

public class PersonnelPersistence extends Persistence {

	public List<PersonnelView> getActiveLoanOfficersInBranch(Short levelId,
			Short officeId, Short userId, Short userLevelId)
			throws PersistenceException {
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

	public PersonnelBO getPersonnel(Short personnelId)
			throws PersistenceException {
		return (PersonnelBO) getPersistentObject(PersonnelBO.class, personnelId);
	}

	public boolean isUserExist(String userName) throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("USER_NAME", userName);
		Number count = (Number) execUniqueResultNamedQuery(
				NamedQueryConstants.GET_PERSONNEL_WITH_NAME, queryParameters);
		if (count != null) {
			return count.longValue() > 0;
		}

		return false;
	}

	public boolean isUserExistWithGovernmentId(String governmentId)
			throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("GOVT_ID", governmentId);
		Number count = (Number) execUniqueResultNamedQuery(
				NamedQueryConstants.GET_PERSONNEL_WITH_GOVERNMENTID,
				queryParameters);
		if (count != null) {
			return count.longValue() > 0;
		}
		return false;
	}

	public boolean isUserExist(String displayName, Date dob)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("DISPLAY_NAME", displayName);
		queryParameters.put("DOB", dob);
		Number count = (Number) execUniqueResultNamedQuery(
				NamedQueryConstants.GET_PERSONNEL_WITH_DOB_AND_DISPLAYNAME,
				queryParameters);
		if (count != null) {
			return count.longValue() > 0;
		}
		return false;
	}

	public boolean getActiveChildrenForLoanOfficer(Short personnelId,
			Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("userId", personnelId);
		queryParameters.put("officeId", officeId);
		Number count = (Number) execUniqueResultNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CUSTOMERS_FOR_LO,
				queryParameters);
		if (count != null) {
			return count.longValue() > 0;
		}
		return false;
	}

	public boolean getAllChildrenForLoanOfficer(Short personnelId,
			Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("userId", personnelId);
		queryParameters.put("officeId", officeId);
		Integer count = (Integer) execUniqueResultNamedQuery(
				NamedQueryConstants.GET_ALL_CUSTOMERS_FOR_LO, queryParameters);
		if (count != null) {
			return count > 0 ? true : false;
		}
		return false;
	}

	public PersonnelBO getPersonnelByGlobalPersonnelNum(
			String globalPersonnelNum) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("globalPersonnelNum", globalPersonnelNum);

		PersonnelBO personnelBO = (PersonnelBO) execUniqueResultNamedQuery(
				NamedQueryConstants.PERSONNEL_BY_SYSTEM_ID, queryParameters);
		if (personnelBO != null) {
			return personnelBO;
		}
		return null;
	}

	public QueryResult getAllPersonnelNotes(Short personnelId)
			throws PersistenceException {
		QueryResult notesResult = null;
		try {
			Session session = null;
			notesResult = QueryFactory.getQueryResult("NotesSearch");
			session = notesResult.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETALLPERSONNELNOTES);
			query.setInteger("PERSONNEL_ID", personnelId);
			notesResult.executeQuery(query);
		} catch (HibernateProcessException hpe) {
			throw new PersistenceException(hpe);
		} catch (HibernateSearchException hse) {
			throw new PersistenceException(hse);
		}
		return notesResult;
	}

	public Integer getPersonnelRoleCount(Short roleId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("roleId", roleId);
		Number count = (Number) execUniqueResultNamedQuery(
			NamedQueryConstants.GET_PERSONNEL_ROLE_COUNT, queryParameters);
		return count.intValue();
	}

	public PersonnelBO getPersonnel(String personnelName)
			throws PersistenceException {
		PersonnelBO personnelBO = null;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("USER_NAME", personnelName);
		personnelBO = (PersonnelBO) execUniqueResultNamedQuery(
				NamedQueryConstants.GETPERSONNELBYNAME, queryParameters);
		return personnelBO;
	}

	public void updateWithCommit(PersonnelBO personnelBO)
			throws PersistenceException {
		super.createOrUpdate(personnelBO);
		try {
			HibernateUtil.commitTransaction();
		} catch (HibernateException e) {
			HibernateUtil.rollbackTransaction();
			throw new PersistenceException(e);
		}

	}

	public QueryResult search(String searchString, Short userId)
			throws PersistenceException {
		String[] namedQuery = new String[2];
		List<Param> paramList = getParamList(new PersonnelPersistence()
				.getPersonnel(userId));

		if (searchString.contains(" ")) {
			paramList.add(typeNameValue("String", "USER_NAME1", searchString
					.substring(0, searchString.indexOf(" "))));
			paramList.add(typeNameValue("String", "USER_NAME2", searchString
					.substring(searchString.indexOf(" ") + 1, searchString
							.length())));
		} else {
			paramList.add(typeNameValue("String", "USER_NAME1", searchString));
			paramList.add(typeNameValue("String", "USER_NAME2", ""));
		}
		namedQuery[0] = NamedQueryConstants.PERSONNEL_SEARCH_COUNT;
		namedQuery[1] = NamedQueryConstants.PERSONNEL_SEARCH;
		paramList.add(typeNameValue("String", "USER_NAME", searchString + "%"));
		return getQueryResults(paramList, namedQuery);
	}

	private List<Param> getParamList(PersonnelBO personnel) {
		List<Param> paramList = new ArrayList<Param>();
		paramList.add(typeNameValue("String", "SEARCH_ID", personnel
				.getOffice().getSearchId()));
		paramList.add(typeNameValue("String", "SEARCH_ALL", personnel
				.getOffice().getSearchId()
				+ ".%"));
		paramList.add(typeNameValue("Short", "USERID", personnel
				.getPersonnelId()));
		paramList.add(typeNameValue("Short", "LOID",
				PersonnelLevel.LOAN_OFFICER.getValue()));
		paramList.add(typeNameValue("Short", "USERLEVEL_ID", personnel
				.getLevel().getId()));

		return paramList;

	}

	private QueryResult getQueryResults(List<Param> paramList,
			String[] namedQuery) throws PersistenceException {

		QueryResult queryResult = QueryFactory
				.getQueryResult(PersonnelConstants.USER_LIST);
		QueryInputs queryInputs = new QueryInputs();
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setParamList(paramList);
		queryInputs
				.setPath("org.mifos.application.personnel.util.helpers.UserSearchResultsView");
		queryInputs.setAliasNames(getAliasNames());
		try {
			queryResult.setQueryInputs(queryInputs);
		} catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}
		return queryResult;
	}

	private String[] getAliasNames() {
		String[] aliasNames = { "officeId", "officeName", "personnelId",
				"globalPersonnelNum", "personnelName" };
		return aliasNames;

	}

	public List<PersonnelBO> getActiveLoUnderUser(Short officeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerSearchConstants.OFFICEID, officeId);
		queryParameters.put(CustomerSearchConstants.PERSONNELLEVELID,
				PersonnelLevel.LOAN_OFFICER.getValue());
		queryParameters.put(PersonnelConstants.LOANOFFICERACTIVE,
				PersonnelStatus.ACTIVE.getValue());
		return executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_LOAN_OFFICER_UNDER_USER,
				queryParameters);
	}

	public List<SupportedLocalesEntity> getSupportedLocales()
			throws PersistenceException {
		return executeNamedQuery(NamedQueryConstants.SUPPORTED_LOCALE_LIST,
				null);
	}
}

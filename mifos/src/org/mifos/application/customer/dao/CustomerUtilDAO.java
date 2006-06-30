/**

 * CustomerUtilDAO.java   version: 1.0



 * Copyright © 2005-2006 Grameen Foundation USA

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.master.util.valueobjects.StatusMaster;
import org.mifos.application.meeting.dao.MeetingDAO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.DataTypeConstants;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSystemException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.valueobjects.ReturnType;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * @author sumeethaec
 * 
 */
public class CustomerUtilDAO extends DAO {
	/**
	 * This method retrieves the list of loan officers under the office the
	 * group is being created in This method is called only when center
	 * hierarchy does not exists.
	 * 
	 * @return The list of loan officers that can be assigned for the group.
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getLoanOfficersMaster(short levelId, short officeId,
			short userId, short userLevelId, String searchResultName)
			throws ApplicationException, SystemException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(
				NamedQueryConstants.MASTERDATA_LOANOFFICERS, searchResultName);
		masterDataRetriever.setParameter("levelId", levelId);
		masterDataRetriever.setParameter("userId", userId);
		masterDataRetriever.setParameter("userLevelId", userLevelId);
		masterDataRetriever.setParameter("officeId", officeId);
		masterDataRetriever.setParameter("statusId",
				CustomerConstants.LOAN_OFFICER_ACTIVE_STATE);
		return masterDataRetriever.retrieve();
	}

	public ReturnType getFormedByLoanOfficersMaster(short levelId,
			Short officeId, String searchResultName)
			throws ApplicationException, SystemException {
		Session session = null;
		List queryResult = null;
		try {
			session = HibernateUtil.getSession();
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("levelId", levelId);
			queryParameters.put("officeId", officeId);
			queryParameters.put("statusId",
					CustomerConstants.LOAN_OFFICER_ACTIVE_STATE);
			queryResult = executeNamedQuery(
					NamedQueryConstants.MASTERDATA_FORMEDBY_LOANOFFICERS,
					queryParameters, session);

		} catch (HibernateException he) {
			throw new HibernateSystemException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		SearchResults searchResults = new SearchResults();
		searchResults.setResultName(searchResultName);
		searchResults.setValue(queryResult);
		return searchResults;

	}

	/**
	 * This method retireves the list of fees that would have been assigned to
	 * the group. These will be the administrative set fees for the group
	 * 
	 * @param levelId
	 *            level Id of the customer
	 * @return The list of administrative fees for the group
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getFeesMasterWithLevel(short levelId,
			String searchResultName) throws ApplicationException,
			SystemException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(
				NamedQueryConstants.MASTERDATA_FEES_WITH_LEVEL,
				searchResultName);
		masterDataRetriever.setParameter("levelId", levelId);
		masterDataRetriever.setParameter("statusId", FeeStatus.ACTIVE
				.getValue());
		return masterDataRetriever.retrieve();

	}

	/**
	 * This method retrieves meeting for fees, if fees is periodic
	 * 
	 * @param feesMaster
	 *            List of FeeMaster instances
	 * @return The list of administrative fees for the group
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void setMeetingForFees(List feesMasterList)
			throws ApplicationException, SystemException {
		if (feesMasterList != null && feesMasterList.size() > 0) {
			for (int i = 0; i < feesMasterList.size(); i++) {
				FeeMaster feeMaster = (FeeMaster) feesMasterList.get(i);
				if (feeMaster.getMeetingId() != null
						&& feeMaster.getFeeFrequencyTypeId().shortValue() == FeeFrequencyType.PERIODIC
								.getValue().shortValue()) {
					Meeting meeting = new MeetingDAO().get(feeMaster
							.getMeetingId());
					feeMaster.setFeeMeeting(meeting);
				}
			}
		}
	}

	/**
	 * This method retrieves the list of custom fields for the center
	 * 
	 * @param levelId
	 *            level Id of the customer
	 * @param entityType
	 *            entity type of the customer
	 * @return The list of custom field types that can be assigned for the
	 *         center
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getCustomFieldDefnMaster(short levelId,
			short entityType, String searchResultName)
			throws ApplicationException, SystemException {
		Session session = null;
		List queryResult = null;
		try {
			session = HibernateUtil.getSession();
			HashMap queryParameters = new HashMap();
			queryParameters.put("levelId", levelId);
			queryParameters.put("entityType", entityType);
			queryResult = executeNamedQuery(
					NamedQueryConstants.MASTERDATA_CUSTOMERCUSTOMFIELDDEFINITION,
					queryParameters, session);
			if (null != queryResult && queryResult.size() > 0) {
				for (int i = 0; i < queryResult.size(); i++) {
					((CustomFieldDefinition) queryResult.get(i))
							.getLookUpEntity().getEntityType();
				}
			}
		} catch (HibernateException he) {
			throw new HibernateSystemException(he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		SearchResults searchResults = new SearchResults();
		searchResults.setResultName(searchResultName);
		searchResults.setValue(queryResult);
		return searchResults;
	}

	/**
	 * This method returns the check list associated with a customer level and
	 * status for the given locale. All the items in this check list have to be
	 * ticked before the customer status can be changed
	 * 
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getCheckList(short statusId, short levelId,
			String searchResultName) throws ApplicationException,
			SystemException {
		MasterDataRetriever masterDataRetriever = null;
		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_CHECKLIST,
				searchResultName);
		masterDataRetriever.setParameter("level", levelId);
		masterDataRetriever.setParameter("status", statusId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method returns the list of fees that can be associated for a
	 * customer. These will not include the list of fees already assigned as
	 * admin fees for the customer
	 * 
	 * @return The list of fees
	 * @throws SystemException
	 */
	public SearchResults getFeesMasterWithoutLevel(short allCatId,
			short custCatId, short levelId, String searchResultName)
			throws SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			Query query = session
					.getNamedQuery(NamedQueryConstants.MASTERDATA_FEES_WITHOUT_LEVEL);
			query.setShort("levelId", levelId);
			query.setShort("allCategoryId", allCatId);
			query.setShort("customerCategoryId", custCatId);
			query.setShort("statusId", FeeStatus.ACTIVE.getValue());
			List feesList = query.list(); // - execute (this list will contian
											// duplicate , need to make it
											// distinct)
			List distinctFeesList = new ArrayList(); // - create another list
			for (int i = 0; i < feesList.size(); i++) {
				FeeMaster fees = (FeeMaster) feesList.get(i);
				if (!distinctFeesList.contains(fees))
					distinctFeesList.add(fees);

			}
			SearchResults searchResults = new SearchResults();
			searchResults.setResultName(searchResultName);
			searchResults.setValue(distinctFeesList);
			return searchResults;
		} catch (HibernateException he) {
			throw new SystemException();
		} catch (HibernateProcessException he) {
			throw new SystemException();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method is used to retrieve MasterDataRetriver instance
	 * 
	 * @return instance of MasterDataRetriever
	 * @throws HibernateProcessException
	 */
	public MasterDataRetriever getMasterDataRetriever()
			throws HibernateProcessException {
		return new MasterDataRetriever();
	}

	/**
	 * This method returns the name of status configured for a level in the
	 * given locale
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return string status name
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public String getStatusName(short localeId, short statusId, short levelId)
			throws ApplicationException, SystemException {
		String statusName = null;
		List<StatusMaster> statusList = (List) getStatusMaster(localeId,
				statusId, levelId).getValue();
		// System.out.println("--------Size of status master list while
		// retrieving name: "+ statusList.size());
		if (statusList != null) {
			StatusMaster sm = (StatusMaster) statusList.get(0);
			statusName = sm.getStatusName();
		}
		return statusName;
	}

	/**
	 * This method returns the name of flag
	 * 
	 * @param flagId
	 *            customer flag
	 * @param localeId
	 *            user locale
	 * @return string flag name
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public String getFlagName(short flagId, short localeId)
			throws ApplicationException, SystemException {
		MasterDataRetriever masterDataRetriever = null;

		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(
				NamedQueryConstants.MASTERDATA_GET_FLAGNAME,
				GroupConstants.CURRENT_FLAG);
		masterDataRetriever.setParameter("flagId", flagId);
		masterDataRetriever.setParameter("localeId", localeId);
		SearchResults sr = masterDataRetriever.retrieve();
		return (String) ((List) sr.getValue()).get(0);
	}

	/**
	 * This method tells whether a given flag for the given status is
	 * blacklisted or not
	 * 
	 * @param flagId
	 *            customer flag
	 * @return true if flag is blacklisted, otherwise false
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public boolean isBlacklisted(short flagId) throws ApplicationException,
			SystemException {
		MasterDataRetriever masterDataRetriever = null;

		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(
				NamedQueryConstants.MASTERDATA_IS_BLACKLISTED,
				GroupConstants.IS_BLACKLISTED);
		masterDataRetriever.setParameter("flagId", flagId);
		SearchResults sr = masterDataRetriever.retrieve();
		return ((Short) ((List) sr.getValue()).get(0)).shortValue() == 1 ? true
				: false;
	}

	/**
	 * This method returns the list of next available status to which a customer
	 * can move as per the customer state flow diagram
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return List next applicable status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List getStatusListForClient(short localeId, short status,
			short levelId, short officeId) throws ApplicationException,
			SystemException {
		List<StatusMaster> statusList = new ArrayList<StatusMaster>();
		switch (status) {
		case CustomerConstants.CLIENT_PARTIAL:
			if (Configuration.getInstance().getCustomerConfig(officeId)
					.isPendingApprovalStateDefinedForClient())
				statusList.add(getStatusWithFlags(localeId,
						CustomerConstants.CLIENT_PENDING, levelId));
			else
				statusList.add(getStatusWithFlags(localeId,
						CustomerConstants.CLIENT_APPROVED, levelId));

			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_CANCELLED, levelId));
			break;
		case CustomerConstants.CLIENT_PENDING:
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_PARTIAL, levelId));
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_APPROVED, levelId));
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_CANCELLED, levelId));
			break;
		case CustomerConstants.CLIENT_APPROVED:
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_ONHOLD, levelId));
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_CLOSED, levelId));
			break;
		case CustomerConstants.CLIENT_ONHOLD:
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_APPROVED, levelId));
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_CLOSED, levelId));
			break;
		case CustomerConstants.CLIENT_CANCELLED:
			statusList.add(getStatusWithFlags(localeId,
					CustomerConstants.CLIENT_PARTIAL, levelId));
		default:
		}
		return statusList;
	}

	/**
	 * This method returns the list of next available status to which a customer
	 * can move as per the customer state flow diagram
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return List next applicable status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public List getStatusListForCenter(short localeId, short status,
			short levelId) throws ApplicationException, SystemException {
		List<StatusMaster> statusList = new ArrayList<StatusMaster>();
		switch (status) {
		case CustomerConstants.CENTER_ACTIVE_STATE:
			statusList.add(getStatusWithLevel(localeId,
					CustomerConstants.CENTER_INACTIVE_STATE, levelId));
			break;
		case CustomerConstants.CENTER_INACTIVE_STATE:
			statusList.add(getStatusWithLevel(localeId,
					CustomerConstants.CENTER_ACTIVE_STATE, levelId));
			break;
		default:
		}
		return statusList;
	}

	/**
	 * This method returns flag list associated with passed in status Id
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return SearchResults flag list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getStatusFlags(short localeId, short statusId,
			short levelId) throws ApplicationException, SystemException {
		MasterDataRetriever masterDataRetriever = null;

		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(
				NamedQueryConstants.MASTERDATA_SPECIFIC_STATUS_FLAG,
				GroupConstants.STATUS_FLAG);
		masterDataRetriever.setParameter("localeId", localeId);
		masterDataRetriever.setParameter("levelId", levelId);
		masterDataRetriever.setParameter("specificStatusId", statusId);
		return masterDataRetriever.retrieve();
		// masterDataRetriever.retrieveMasterData(MasterConstants.STATUS_FLAG
		// ,localeId ,GroupConstants.STATUS_FLAG
		// ,"org.mifos.application.master.util.valueobjects.StatusFlag"
		// ,"flagId");
	}

	/**
	 * This method returns list of all available status for a level
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getStatusMaster(short localeId, short statusId,
			short levelId) throws ApplicationException, SystemException {
		MasterDataRetriever masterDataRetriever = null;

		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(
				NamedQueryConstants.MASTERDATA_SPECIFIC_STATUS,
				GroupConstants.STATUS);
		masterDataRetriever.setParameter("localeId", localeId);
		masterDataRetriever.setParameter("levelId", levelId);
		masterDataRetriever.setParameter("specificStatusId", statusId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method is the helper method that returns a status along with its
	 * assoicated flags
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private StatusMaster getStatusWithFlags(short locale, short status,
			short levelId) throws ApplicationException, SystemException {
		SearchResults sr = this.getStatusMaster(locale, status, levelId);
		StatusMaster statusMaster = null;
		Object obj = sr.getValue();
		if (obj != null) {
			statusMaster = (StatusMaster) ((List) obj).get(0);
			sr = this.getStatusFlags(locale, status, levelId);
			obj = sr.getValue();
			if (obj != null) {
				statusMaster.setFlagList((List) obj);
			} else {
				statusMaster.setFlagList(null);
			}
		}
		return statusMaster;
	}

	/**
	 * This method is the helper method that returns a status along with its
	 * assoicated flags
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	private StatusMaster getStatusWithLevel(short locale, short status,
			short levelId) throws ApplicationException, SystemException {
		SearchResults sr = this.getStatusMaster(locale, status, levelId);
		StatusMaster statusMaster = null;
		Object obj = sr.getValue();
		if (obj != null) {
			statusMaster = (StatusMaster) ((List) obj).get(0);

		}
		return statusMaster;
	}

	/**
	 * This method returns list of all available status for a level
	 * 
	 * @param localeId
	 *            user locale
	 * @param statusId
	 *            status id
	 * @param levelId
	 *            customer level
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getStatusForLevel(short localeId, short levelId)
			throws ApplicationException, SystemException {
		MasterDataRetriever masterDataRetriever = null;

		try {
			masterDataRetriever = getMasterDataRetriever();
		} catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.MASTERDATA_STATUS,
				GroupConstants.STATUS_LIST);
		masterDataRetriever.setParameter("localeId", localeId);
		masterDataRetriever.setParameter("levelId", levelId);
		return masterDataRetriever.retrieve();
	}

	/**
	 * This method checks if the loan officer selected by the user during
	 * creation is inactive. This takes care of a scenario where before creating
	 * the center an active loan officer is made inactive in the system If the
	 * loan officer is inactive, it returns true, otherwise false. This is
	 * called just before creating a new center in the database.
	 * 
	 * @param personnelId
	 *            The personnel whose status is checked
	 * @return Returns true or false as to whether the loan officer is inactive
	 * @throws HibernateProcessException,HibernateSystemException
	 */
	public boolean isLoanOfficerInactive(short personnelId, short officeId)
			throws SystemException {

		Session session = null;

		try {
			session = HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			Query query = session
					.createQuery("select count(*) from Personnel personnel where personnel.personnelId = :personnelId and (personnel.personnelStatus = :statusId and personnel.level.levelId = :levelId and personnel.office.officeId = :officeId)");
			query.setShort("personnelId", personnelId);
			query.setShort("statusId", PersonnelConstants.ACTIVE);
			query.setShort("levelId", PersonnelConstants.LOAN_OFFICER);
			query.setShort("officeId", officeId);
			Integer count = (Integer) query.uniqueResult();

			trxn.commit();
			return (count.intValue() != 0) ? false : true;
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method checks if the branch selected by the user during creation of
	 * the center is inactive. This takes care of a scenario where before
	 * creating the center an active branch is made inactive in the system If
	 * the branch is inactive, it returns true, otherwise false. This is called
	 * just before creating a new center in the database.
	 * 
	 * @param officeId
	 *            The branch whose status is checked
	 * @return Returns true or false as to whether the branch is inactive
	 * @throws HibernateProcessException,HibernateSystemException
	 */
	public boolean isBranchInactive(short officeId) throws SystemException {

		Session session = null;

		try {
			session = HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			Query query = session
					.createQuery("select count(*) from Office office where office.officeId = :officeId and office.status.statusId = :statusId");
			query.setShort("officeId", officeId);
			query.setShort("statusId", OfficeConstants.INACTIVE);
			Integer count = (Integer) query.uniqueResult();
			trxn.commit();
			return (count.intValue() != 0) ? true : false;
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method checks if any of the admin or additional fees selected by the
	 * user during creation of the center is inactive. This takes care of a
	 * scenario where, before creating the center a fee with an active status is
	 * made inactive in the system If the fee status is inactive, it returns
	 * true, otherwise false. This is called just before creating a new center
	 * in the database.
	 * 
	 * @param feeIds
	 *            The fee ids whose status is checked
	 * @return Returns true or false as to whether the branch is inactive
	 * @throws HibernateProcessException,HibernateSystemException
	 */
	public boolean isFeesStatusInactive(List feeIds) throws SystemException {

		Session session = null;
		StringBuilder feeList = new StringBuilder();
		try {
			session = HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			feeList.append("(");
			for (int i = 0; i < feeIds.size(); i++) {
				feeList.append(feeIds.get(i));
				feeList.append(",");
			}
			feeList.deleteCharAt(feeList.lastIndexOf(","));
			feeList.insert(feeList.length(), ")");
			Query query = session
					.createQuery("select count(*) from Fees fees where fees.feeId in "
							+ feeList.toString()
							+ " and fees.status = :statusId");
			query.setShort("statusId", FeeStatus.INACTIVE.getValue());
			Integer count = (Integer) query.uniqueResult();
			// System.out.println("----------Count for branch:
			// "+count.toString());
			trxn.commit();
			return (count.intValue() != 0) ? true : false;
		} catch (HibernateProcessException hpe) {
			throw new SystemException(hpe);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method creates a list of customerMaster objects corresponding to
	 * each customer level and returns it. It assumes that customer levels are
	 * 1,2,3 for client,groups and centers.
	 * 
	 * @param customerId
	 * @return
	 * @throws SystemException
	 */
	public static List<CustomerMaster> getParentHierarchy(Integer customerId)
			throws SystemException, ApplicationException {
		Session session = null;
		Customer customer, parentCustomer = null;
		List<CustomerMaster> customerMasterList = new ArrayList<CustomerMaster>();
		Short customerLevel = 3;

		try {
			session = HibernateUtil.getSession();
			// this loop iterates till it traverses all the hierarchy or one of
			// the parent link is broken.
			do {
				customer = (Customer) DAO
						.getEntity(
								"org.mifos.application.customer.util.valueobjects.Customer",
								customerId, DataTypeConstants.Integer, session);
				MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug(
						"After retrieving the customer with customerId :"
								+ customerId);
				customerLevel = customer.getCustomerLevel().getLevelId();
				CustomerMaster customermaster = new CustomerMaster(customer
						.getCustomerId(), customer.getDisplayName(), customer
						.getGlobalCustNum(), customer.getStatusId(),
						customerLevel, customer.getVersionNo(), customer
								.getOffice().getOfficeId(), customer
								.getPersonnel().getPersonnelId());
				customerMasterList.add(customermaster);
				MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug(
						"After adding the customer master to list for  customer with customerId :"
								+ customerId);
				parentCustomer = customer.getParentCustomer();
				if (null != parentCustomer) {
					customerId = parentCustomer.getCustomerId();
				}
			} while (customerLevel <= 3 && null != parentCustomer);

		} catch (HibernateException he) {
			throw new SystemException(ExceptionConstants.SYSTEMEXCEPTION, he);
		} finally {
			HibernateUtil.closeSession(session);
		}
		// this just for ease of garbage collection.
		customer = null;
		parentCustomer = null;
		return customerMasterList;
	}

	/**
	 * This method to obtain maximum number of customerId that has been assigned
	 * to the customers till now in the given office
	 * 
	 * @param officeId,
	 *            the office in which max customerId is to be found
	 * @return max personnelId
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public Integer getMaxCustomerId(Short officeId)
			throws ApplicationException, SystemException {
		Integer count = 0;
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Transaction trxn = session.beginTransaction();
			Query query = session
					.createQuery("select max(customer.customerId) from Customer customer where customer.office.officeId=:officeId");
			query.setShort("officeId", officeId);
			count = (Integer) query.uniqueResult();
			trxn.commit();
			if (count == null) {
				count = Integer.valueOf(0);
			}
			return count;
		} catch (HibernateException he) {
			throw he;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method returns office, for the given officeId
	 * 
	 * @param officeId
	 * @return an instance of Office
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public Office getOffice(short officeId) throws ApplicationException,
			SystemException {
		OfficeDAO officeDAO = new OfficeDAO();
		return officeDAO.getOffice(officeId);
	}

	public static Short getFieldType(Short fieldId) throws SystemException {
		CustomFieldDefinition customFieldDefn = null;
		HashMap<String, Short> queryParameters = new HashMap<String, Short>();
		queryParameters.put("FIELDID", fieldId);
		List<Object> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_FIELD_TYPE, queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			Object obj = queryResult.get(0);
			if (obj != null) {
				customFieldDefn = (CustomFieldDefinition) obj;

			}
		}
		return customFieldDefn.getFieldType();

	}

	public List<LoanBO> getActiveLoanAccountsForCustomer(Integer customerId,
			Short localeId) throws SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.CUSTOMER_ACTIVE_LOAN_ACCOUNTS);
			query.setInteger("CUSTOMER_ID", customerId);
			List<LoanBO> loanAccounts = query.list();
			if (loanAccounts != null && loanAccounts.size() > 0) {
				for (LoanBO loan : loanAccounts) {
					LoanSummaryEntity loanSummary = loan.getLoanSummary();
					Double outstandingBalance = loanSummary
							.getOustandingBalance().getAmountDoubleValue();
					Double dueAmount = loan.getTotalAmountDue()
							.getAmountDoubleValue();
					String prdOfferingName = loan.getLoanOffering()
							.getPrdOfferingName();
					String globalAccountNum = loan.getGlobalAccountNum();
					Short accountStateId = loan.getAccountState().getId();
					loan.getAccountState().setLocaleId(localeId);
					String accountStateName = loan.getAccountState().getName();
				}
			}
			return loanAccounts;
		} catch (HibernateException he) {
			throw he;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<SavingsBO> getActiveSavingsAccountsForCustomer(
			Integer customerId, Short localeId) throws SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS);
			query.setInteger("CUSTOMER_ID", customerId);
			List<SavingsBO> savingsAccounts = query.list();
			if (savingsAccounts != null && savingsAccounts.size() > 0) {
				for (SavingsBO savings : savingsAccounts) {
					Double balance = savings.getSavingsBalance()
							.getAmountDoubleValue();
					String prdOfferingName = savings.getSavingsOffering()
							.getPrdOfferingName();
					String globalAccountNum = savings.getGlobalAccountNum();
					Short accountStateId = savings.getAccountState().getId();
					savings.getAccountState().setLocaleId(localeId);
					String accountStateName = savings.getAccountState()
							.getName();
				}
			}
			return savingsAccounts;
		} catch (HibernateException he) {
			throw he;
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method obtains list of all the clients that belong to customer with
	 * given searchId.
	 */
	public List<CustomerMaster> getCustomerChildren(String searchIdStr,
			short officeId, Session session) throws SystemException {
		List<CustomerMaster> customerChildren = new ArrayList<CustomerMaster>();
		List queryResult = null;
		HashMap queryParameters = new HashMap();
		queryParameters.put("SEARCH_STRING", searchIdStr + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		if (session == null)
			queryResult = executeNamedQuery(
					NamedQueryConstants.GET_CUSTOMER_MASTER_BY_SEARCH_ID,
					queryParameters);
		else
			queryResult = executeNamedQuery(
					NamedQueryConstants.GET_CUSTOMER_MASTER_BY_SEARCH_ID,
					queryParameters, session);
		if (null != queryResult && queryResult.size() > 0) {
			for (int i = 0; i < queryResult.size(); i++) {
				Object obj = queryResult.get(i);
				if (obj != null) {
					customerChildren.add((CustomerMaster) obj);
				}
			}
		}
		return customerChildren;

	}

	public List<Customer> getChildListForParent(String searchString,
			short officeId, Session session) throws HibernateProcessException,
			HibernateSystemException {
		boolean wasSessionNull = false;
		try {
			if (session == null) {
				wasSessionNull = true;
				session = HibernateUtil.getSession();
			}
			Query query = session
					.createQuery("from Customer customer where customer.searchId like :SEARCH_STRING and customer.office.officeId = :OFFICE_ID");
			query.setString("SEARCH_STRING", searchString + ".%");
			query.setShort("OFFICE_ID", officeId);
			return query.list();
		} catch (HibernateException he) {
			throw new HibernateSystemException(he);
		} finally {
			if (wasSessionNull)
				HibernateUtil.closeSession(session);
		}
	}
}

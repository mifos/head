/**

 * ClosedAccSearchDAO.java    version: xxx

 

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
package org.mifos.application.accounts.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.util.helpers.ClosedAccSearchConstants;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.ClientUpcomingFeecahrges;
import org.mifos.application.customer.client.util.valueobjects.ClientChangeLog;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.framework.components.audit.util.valueobjects.AuditLog;
import org.mifos.framework.components.audit.util.valueobjects.AuditLogRecord;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * @author mohammedn
 * 
 */
public class ClosedAccSearchDAO extends DAO {

	/**
	 * default constructor
	 */
	public ClosedAccSearchDAO() {
	}

	/**
	 * this method is used to get all closed accounts of the client
	 * 
	 * @param customerId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<Loan> getAllClosedAccounts(Integer customerId)
			throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.VIEWALLCLIENTCLOSEDACCOUNTS);
			query.setInteger(ClosedAccSearchConstants.CUSTOMERID, customerId);
			List<Loan> accountList = query.list();
			for (Loan loan : accountList)
				loan.getLoanOffering().getPrdOfferingName();
			return accountList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method is used to get the master data for the account states
	 * 
	 * @param localeId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public SearchResults getAccStates(Short localeId) throws SystemException,
			ApplicationException {
		return new MasterDataRetriever().retrieveMasterData(
				MasterConstants.ACCOUNT_STATES, localeId,
				"ClosedAccSearchStatusList",
				"org.mifos.application.master.util.valueobjects.AccountState",
				"accountStateId");
	}

	/**
	 * This method is used to get all the upcoming charges of the client
	 * 
	 * @param accountId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<ClientUpcomingFeecahrges> getClientUpcomingFeeCharges(
			Integer accountId) throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETCLIENTCHARGES);
			query.setInteger(ClosedAccSearchConstants.ACCOUNTID, accountId);
			List<ClientUpcomingFeecahrges> clientUpcomingFeecahrgesList = query
					.list();
			if (clientUpcomingFeecahrgesList != null) {
				Iterator<ClientUpcomingFeecahrges> itr = clientUpcomingFeecahrgesList
						.iterator();
				while (itr.hasNext()) {
					ClientUpcomingFeecahrges clientUpcomingFeecahrges = (ClientUpcomingFeecahrges) itr
							.next();
					if (clientUpcomingFeecahrges.getAmount()
							.getAmountDoubleValue() == 0.0)
						itr.remove();
				}
			}
			return clientUpcomingFeecahrgesList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<ClientUpcomingFeecahrges> getRecurrenceFeeCharges(
			Integer accountId) throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETRECURRENCEFEESCHARGES);
			query.setInteger(ClosedAccSearchConstants.ACCOUNTID, accountId);
			query.setShort(FeesConstants.INACTIVE, FeeStatus.INACTIVE
					.getValue());
			List<ClientUpcomingFeecahrges> clientUpcomingFeecahrgesList = query
					.list();
			for (ClientUpcomingFeecahrges clientUpcomingFeecahrges : clientUpcomingFeecahrgesList) {
				clientUpcomingFeecahrges.getMeeting()
						.getSimpleMeetingSchedule();
			}
			return clientUpcomingFeecahrgesList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method is used to get the total fee amount due for the client
	 * 
	 * @param accountId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Money getTotalClientFeeChargesDue(Integer accountId)
			throws SystemException, ApplicationException {
		return getClientFeeCahrgesDue(accountId).add(
				getClientFeeCahrgesOverDue(accountId));
	}

	/**
	 * This method is used to get the total fee amount due for the client
	 * 
	 * @param accountId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Money getClientFeeCahrgesDue(Integer accountId)
			throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETSUMCLIENTDUECHARGES);
			query.setInteger(ClosedAccSearchConstants.ACCOUNTID, accountId);
			List<AccountActionDate> accountActionDateList = (List<AccountActionDate>) query
					.list();
			Money dueAmount = new Money();
			for (AccountActionDate accountActionDate : accountActionDateList) {
				dueAmount = dueAmount.add(accountActionDate
						.getTotalChargesDue());
			}
			return dueAmount;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method is used to get the total fee amount over due for the client
	 * 
	 * @param accountId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public Money getClientFeeCahrgesOverDue(Integer accountId)
			throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETSUMCLIENTOVERDUECHARGES);
			query.setInteger(ClosedAccSearchConstants.ACCOUNTID, accountId);
			List<AccountActionDate> accountActionDateList = (List<AccountActionDate>) query
					.list();
			Money overDueAmount = new Money();
			for (AccountActionDate accountActionDate : accountActionDateList) {
				overDueAmount = overDueAmount.add(accountActionDate
						.getTotalChargesDue());
			}
			return overDueAmount;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public String getUpcomingChargesDate(Integer accountId)
			throws SystemException, ApplicationException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETUPCOMINGCHARGESDATE);
			query.setInteger(ClosedAccSearchConstants.ACCOUNTID, accountId);
			String upcomingChargesDate = query.uniqueResult() != null ? query
					.uniqueResult().toString() : "";
			return upcomingChargesDate;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method is used to get all the change log details of the client.
	 * 
	 * @param customerId
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public List<ClientChangeLog> getClientChangeLog(Integer customerId,
			Short entityType) throws SystemException, ApplicationException {
		Session session = null;
		List<ClientChangeLog> clientChangeLogList = new ArrayList<ClientChangeLog>();
		try {
			session = HibernateUtil.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETSUMCLIENTCHANGELOG);
			query.setInteger(ClosedAccSearchConstants.CUSTOMERID, customerId);
			query.setShort(ClosedAccSearchConstants.ENTITYTYPE, entityType);
			List<AuditLog> auditLogList = query.list();
			for (AuditLog auditLog : auditLogList) {
				Collection<AuditLogRecord> auditLogCollection = auditLog
						.getAuditLogRecords();
				for (AuditLogRecord auditLogRecord : auditLogCollection) {
					ClientChangeLog clientChangeLog = new ClientChangeLog();
					clientChangeLog.setDate(auditLog.getUpdatedDate());
					clientChangeLog.setFieldName(auditLogRecord.getFieldName());
					clientChangeLog.setOldValue(auditLogRecord.getOldValue());
					clientChangeLog.setNewValue(auditLogRecord.getNewValue());
					clientChangeLog.setUserName(auditLog.getActualName());
					clientChangeLogList.add(clientChangeLog);
				}

			}
			return clientChangeLogList;
		} catch (HibernateProcessException hbe) {
			throw new SystemException();
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}
}

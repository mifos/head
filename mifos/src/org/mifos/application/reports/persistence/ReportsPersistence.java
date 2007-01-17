/**

 * ReportsPersistence.java    version: 1.0

 

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

package org.mifos.application.reports.persistence;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.exceptions.ReportException;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class ReportsPersistence extends Persistence {

	public ReportsPersistence() {

	}

	/**
	 * Lists all the Report Categories. The list also contains the set of
	 * Reports within a particular category
	 */
	public List<ReportsCategoryBO> getAllReportCategories() {
		Query query = HibernateUtil.getSessionTL().getNamedQuery(
				ReportsConstants.GETALLREPORTS);
		return query.list();
	}

	public List<ReportsParams> getAllReportParams() {
		Query query = HibernateUtil.getSessionTL().getNamedQuery(
				ReportsConstants.GETALLREPORTSPARAMS);
		return query.list();
	}

	public void createReportParams(ReportsParamsValue reportsParams)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.save(reportsParams);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void deleteReportParams(ReportsParamsValue reportsParams)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.delete(reportsParams);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<ReportsDataSource> getAllReportDataSource() {
		Query query = HibernateUtil.getSessionTL().getNamedQuery(
				ReportsConstants.GETALLREPORTSDATASOURCE);
		return query.list();
	}

	public void createReportsDataSource(ReportsDataSource reportsDataSource)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.save(reportsDataSource);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void deleteReportsDataSource(ReportsDataSource reportsDataSource)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.delete(reportsDataSource);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<ReportsParamsMap> getAllReportParamsMap() {
		Query query = HibernateUtil.getSessionTL().getNamedQuery(
				ReportsConstants.GETALLREPORTSPARAMSMAP);
		return query.list();
	}

	public List<ReportsParamsMap> findParamsOfReportId(int reportId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		List<ReportsParamsMap> queryResult = null;
		queryParameters.put("reportId", reportId + "");
		queryResult = executeNamedQuery(
				ReportsConstants.FIND_PARAMS_OF_REPORTID, queryParameters);
		return queryResult;

	}

	public List<ReportsParamsMap> findInUseParameter(int parameterId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		List<ReportsParamsMap> queryResult = null;
		queryParameters.put("parameterId", parameterId + "");
		queryResult = executeNamedQuery(ReportsConstants.FIND_IN_USE_PARAMETER,
				queryParameters);
		return queryResult;

	}

	public List<ReportsParams> findInUseDataSource(int dataSourceId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		List<ReportsParams> queryResult = null;
		queryParameters.put("dataSourceId", dataSourceId + "");
		queryResult = executeNamedQuery(
				ReportsConstants.FIND_IN_USE_DATASOURCE, queryParameters);
		return queryResult;

	}

	public List<ReportsParams> viewParameter(int parameterId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("parameterId", parameterId + "");
		return executeNamedQuery(ReportsConstants.VIEW_PARAMETER,
				queryParameters);
	}

	public List<ReportsDataSource> viewDataSource(int dataSourceId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		List<ReportsDataSource> queryResult = null;
		queryParameters.put("dataSourceId", dataSourceId + "");
		queryResult = executeNamedQuery(ReportsConstants.VIEW_DATASOURCE,
				queryParameters);
		return queryResult;

	}

	/**
	 * Creates a link between a report and a parameter
	 */
	public void createReportsParamsMap(
			ReportsParamsMapValue reportsParamsMapValue)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.save(reportsParamsMapValue);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * Deletes a link between report and a parameter
	 */
	public void deleteReportsParamsMap(
			ReportsParamsMapValue reportsParamsMapValue)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.delete(reportsParamsMapValue);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * sets a link between report and a jasper file
	 */
	public void updateReportsJasperMap(ReportsJasperMap reportsJasperMap)
			throws ApplicationException, SystemException {
		Session session = null;
		Transaction trxn = null;
		try {
			session = HibernateUtil.openSession();
			trxn = session.beginTransaction();
			session.update(reportsJasperMap);
			session.flush();
			trxn.commit();
		}
		catch (HibernateProcessException hpe) {
			trxn.rollback();
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();
			trxn.rollback();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			trxn.rollback();
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<ReportsJasperMap> findJasperOfReportId(int reportId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		List<ReportsJasperMap> queryResult = null;
		queryParameters.put("reportId", reportId + "");
		queryResult = executeNamedQuery(
				ReportsConstants.FIND_JASPER_OF_REPORTID, queryParameters);

		return queryResult;

	}

	/**
	 * Creates a connection.
	 * (Is this actually used?  I don't see how it could do anything
	 * useful, since it calls HibernateUtil.closeSession before
	 * returning).
	 */
	public Connection getJasperConnection() throws ApplicationException,
			SystemException {
		Session session = null;
		Connection con = null;
		try {
			session = HibernateUtil.openSession();
			con = session.connection();
		}
		catch (HibernateProcessException hpe) {
			throw new ApplicationException(hpe);
		}
		catch (HibernateException hpe) {
			hpe.printStackTrace();

			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
		}
		catch (Exception e) {
			throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,
					e);
		}
		finally {
			HibernateUtil.closeSession(session);
		}
		return con;
	}

}

/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.reports.persistence;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.reports.business.ReportsBO;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.business.ReportsDataSource;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.business.ReportsParams;
import org.mifos.reports.business.ReportsParamsMap;
import org.mifos.reports.business.ReportsParamsMapValue;
import org.mifos.reports.business.ReportsParamsValue;
import org.mifos.reports.exceptions.ReportException;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.mifos.security.rolesandpermission.business.ActivityEntity;

public class ReportsPersistence extends Persistence {

    public ReportsPersistence() {

    }

    /**
     * Lists all the Report Categories. The list also contains the set of
     * Reports within a particular category
     */
    public List<ReportsCategoryBO> getAllReportCategories() {
        Query query = StaticHibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTS);
        return query.list();
    }

    public List<ReportsBO> getAllReports() {
        List<ReportsBO> allReports = new ArrayList<ReportsBO>();
        for (ReportsCategoryBO reportCategory : new ReportsPersistence().getAllReportCategories()) {
            allReports.addAll(reportCategory.getReportsSet());
        }
        return allReports;
    }

    public List<ReportsParams> getAllReportParams() {
        return getAllReportParams(StaticHibernateUtil.getSessionTL());
    }

    public List<ReportsParams> getAllReportParams(Session session) {
        Query query = session.getNamedQuery(ReportsConstants.GETALLREPORTSPARAMS);
        return query.list();
    }

    public void createReportParams(ReportsParamsValue reportsParams) throws ApplicationException, SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.save(reportsParams);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException hpe) {
            trxn.rollback();
            throw new ApplicationException(hpe);
        } catch (HibernateException hpe) {
            trxn.rollback();

            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    public void deleteReportParams(ReportsParamsValue reportsParams) throws ApplicationException, SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.delete(reportsParams);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException hpe) {
            trxn.rollback();
            throw new ApplicationException(hpe);
        } catch (HibernateException e) {
            trxn.rollback();

            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    public List<ReportsDataSource> getAllReportDataSource() {
        Query query = StaticHibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTSDATASOURCE);
        return query.list();
    }

    public void createReportsDataSource(ReportsDataSource reportsDataSource) throws ApplicationException,
            SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.save(reportsDataSource);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException hpe) {
            trxn.rollback();
            throw new ApplicationException(hpe);
        } catch (HibernateException e) {
            trxn.rollback();

            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    public void deleteReportsDataSource(ReportsDataSource reportsDataSource) throws ApplicationException,
            SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.delete(reportsDataSource);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException hpe) {
            trxn.rollback();
            throw new ApplicationException(hpe);
        } catch (HibernateException e) {
            trxn.rollback();

            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    /* What is this for? I don't see anyone calling it. */
    public List<ReportsParamsMap> getAllReportParamsMap() {
        Query query = StaticHibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTSPARAMSMAP);
        return query.list();
    }

    public List<ReportsParamsMap> findParamsOfReportId(int reportId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        List<ReportsParamsMap> queryResult = null;
        queryParameters.put("reportId", reportId + "");
        queryResult = executeNamedQuery(ReportsConstants.FIND_PARAMS_OF_REPORTID, queryParameters);
        return queryResult;

    }

    public List<ReportsParamsMap> findInUseParameter(int parameterId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        List<ReportsParamsMap> queryResult = null;
        queryParameters.put("parameterId", parameterId + "");
        queryResult = executeNamedQuery(ReportsConstants.FIND_IN_USE_PARAMETER, queryParameters);
        return queryResult;

    }

    public List<ReportsParams> findInUseDataSource(int dataSourceId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        List<ReportsParams> queryResult = null;
        queryParameters.put("dataSourceId", dataSourceId + "");
        queryResult = executeNamedQuery(ReportsConstants.FIND_IN_USE_DATASOURCE, queryParameters);
        return queryResult;

    }

    public List<ReportsParams> viewParameter(int parameterId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("parameterId", parameterId + "");
        return executeNamedQuery(ReportsConstants.VIEW_PARAMETER, queryParameters);
    }

    public List<ReportsDataSource> viewDataSource(int dataSourceId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        List<ReportsDataSource> queryResult = null;
        queryParameters.put("dataSourceId", dataSourceId + "");
        queryResult = executeNamedQuery(ReportsConstants.VIEW_DATASOURCE, queryParameters);
        Iterator itrQueryResult = queryResult.iterator();
        while (itrQueryResult.hasNext()) {
            ReportsDataSource objReportsDataSource = (ReportsDataSource) itrQueryResult.next();
            objReportsDataSource.setPassword(ReportsConstants.HIDDEN_PASSWORD);
        }
        return queryResult;
    }

    /**
     * Creates a link between a report and a parameter
     */
    public void createReportsParamsMap(ReportsParamsMapValue reportsParamsMapValue) throws ApplicationException,
            SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.save(reportsParamsMapValue);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException hpe) {
            trxn.rollback();
            throw new ApplicationException(hpe);
        } catch (HibernateException e) {
            trxn.rollback();

            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    /**
     * Deletes a link between report and a parameter
     */
    public void deleteReportsParamsMap(ReportsParamsMapValue reportsParamsMapValue) throws ApplicationException,
            SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.delete(reportsParamsMapValue);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException hpe) {
            trxn.rollback();
            throw new ApplicationException(hpe);
        } catch (HibernateException e) {
            trxn.rollback();

            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    /**
     * sets a link between report and a jasper file
     */
    public void updateReportsJasperMap(ReportsJasperMap reportsJasperMap) throws ApplicationException, SystemException {
        Session session = null;
        Transaction trxn = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            trxn = session.beginTransaction();
            session.update(reportsJasperMap);
            session.flush();
            trxn.commit();
        } catch (HibernateProcessException e) {
            trxn.rollback();
            throw new ApplicationException(e);
        } catch (HibernateException e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            trxn.rollback();
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    /**
     * Why a list? Is it to deal with the case of "zero or one", or can there be
     * more than one?
     */
    public List<ReportsJasperMap> findJasperOfReportId(int reportId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        List<ReportsJasperMap> queryResult = null;
        queryParameters.put("reportId", reportId + "");
        queryResult = executeNamedQuery(ReportsConstants.FIND_JASPER_OF_REPORTID, queryParameters);

        return queryResult;
    }

    public List<ReportsJasperMap> findJasperOfReportId(Session session, int reportId) throws PersistenceException {
        Query query = session.getNamedQuery(ReportsConstants.FIND_JASPER_OF_REPORTID);
        query.setParameter("reportId", reportId);
        return query.list();
    }

    public ReportsJasperMap oneJasperOfReportId(Session session, short reportId) throws PersistenceException {
        List<ReportsJasperMap> all = findJasperOfReportId(session, reportId);
        if (all.size() != 1) {
            throw new RuntimeException("expected one jasper for report ID " + reportId + " but got " + all.size());
        }
        return all.get(0);
    }

    public ReportsJasperMap oneJasperOfReportId(short reportId) throws PersistenceException {
        return oneJasperOfReportId(StaticHibernateUtil.getSessionTL(), reportId);
    }

    public void createJasperMap(ReportsJasperMap map) {
        Session session = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            createJasperMap(session, map);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    public void createJasperMap(Session session, ReportsJasperMap map) {
        session.save(map);
        session.flush();
    }

    /**
     * Creates a connection. (This function calls
     * StaticHibernateUtil.closeSession before returning. I think what it is
     * doing is closing the session but keeping open the underlying connection.
     * Certainly looks ugly, not sure whether it is buggy too...).
     */
    public Connection getJasperConnection() throws ApplicationException, SystemException {
        Session session = null;
        try {
            session = StaticHibernateUtil.getSessionTL();
            return session.connection();
        } catch (HibernateProcessException e) {
            throw new ApplicationException(e);
        } catch (HibernateException e) {
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } catch (Exception e) {
            throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession(session);
        }
    }

    public ReportsBO getReport(Short reportId) {
        Session session = null;
        session = StaticHibernateUtil.getSessionTL();
        return (ReportsBO) session.load(ReportsBO.class, reportId);
    }

    public ReportsCategoryBO getReportCategoryByCategoryId(Short reportCategoryId) {
        Session session = null;
        session = StaticHibernateUtil.getSessionTL();
        return (ReportsCategoryBO) session.load(ReportsCategoryBO.class, reportCategoryId);
    }

    public void updateLookUpValue(Short activityId, String inputCategoryName) {
        ActivityEntity activityEntity = null;
        try {
            activityEntity = (ActivityEntity) getPersistentObject(ActivityEntity.class, activityId);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        if (activityEntity != null) {
            LookUpValueEntity lookUpValueEntity = activityEntity.getDescriptionLookupValues();
            MessageLookup.getInstance().updateLookupValue(lookUpValueEntity, inputCategoryName);
        }
    }
}

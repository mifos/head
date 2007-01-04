/**

 * ReportsPersistenceService.java    version: 1.0

 

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



package org.mifos.application.reports.persistence.service;

import java.sql.Connection;
import java.util.List;

import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.service.PersistenceService;

/**
 * class associated with Report Persistence Services
 */
public class ReportsPersistenceService extends PersistenceService {
	
	private ReportsPersistence reportsPersistence ;
	
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public ReportsPersistenceService() {
		reportsPersistence = new ReportsPersistence();
	}

	public List<ReportsCategoryBO> getAllReportCategories()
	{
		logger.debug("In getAllReports of ReportPersistenceService ");
		return reportsPersistence.getAllReportCategories();
	}
	
	/**
	 * List all Report Parameters
	 */
	public List<ReportsParams> getAllReportParams()
    {
        logger.debug("In getAllReportParams of ReportPersistenceService ");
        return reportsPersistence.getAllReportParams();
    }
    
    /**
     * Create Report Parameter
     */
    public void createReportParams(ReportsParamsValue reportsParams)
    {
        //System.out.println("vvv-->"+reportsParams.getDatasourceId());
    	//logger.debug("In createReportParams of ReportPersistenceService ");
        
        try {
        	reportsPersistence.createReportParams(reportsParams);
        }
        catch (ApplicationException e) {
        	throw new RuntimeException(e);
        }
        catch (SystemException e) {
        	throw new RuntimeException(e);
        }
    }
    
    /**
     * Deletes Report Parameter
     * @param reportsParams
     */
    
    public void deleteReportParams(ReportsParamsValue reportsParams)
    {
        logger.debug("In deleteReportParams of ReportPersistenceService ");
        try{
        	reportsPersistence.deleteReportParams(reportsParams);
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
    }
    
    /**
	 * List all DataSource
	 * @return
	 */
    public List<ReportsDataSource> getAllReportDataSource()
    {
        logger.debug("In getAllReportDatasource of ReportPersistenceService ");
        return reportsPersistence.getAllReportDataSource();
    }
    
    /**
     * Create Report DataSource
     * @param reportsParams
     */
    
    public void createReportsDataSource(ReportsDataSource reportsDataSource)
    {
        logger.debug("In createReportsDataSource of ReportPersistenceService ");
        try{
        	reportsPersistence.createReportsDataSource(reportsDataSource);
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
    }
    
    /**
     * Deletes Report DataSource
     * @param reportsDataSource
     */
    
    public void deleteReportsDataSource(ReportsDataSource reportsDataSource)
    {
        logger.debug("In deleteReportsDataSource of ReportPersistenceService ");
        try{
        	reportsPersistence.deleteReportsDataSource(reportsDataSource);
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
    }
   
    
    /**
	 * List all Reports PArameter Map
	 * @return
	 */
    public List<ReportsParamsMap> getAllReportParamsMap()
    {
        logger.debug("In getAllReportParamsMap of ReportPersistenceService ");
        return reportsPersistence.getAllReportParamsMap();
    }
    /**
     * Finds the parameters for a given reportid
     * @param reportId
     * @return
     */
    
    public List<ReportsParamsMap> findParamsOfReportId(int reportId) throws PersistenceException
    {
    	logger.debug("In findParamsOfReportId of ReportPersistenceService ");
        return reportsPersistence.findParamsOfReportId(reportId);
    }
    
    /**
     * Finds In Use parameter
     * @param parameterId
     * @return
     */
    
    public List<ReportsParamsMap> findInUseParameter(int parameterId) throws PersistenceException
    {
    	logger.debug("In findInUseParameter of ReportPersistenceService ");
        return reportsPersistence.findInUseParameter(parameterId);
    }
    
    /**
     * Finds In Use datasource
     * @param datasourceId
     * @return
     */
    
    public List<ReportsParams> findInUseDataSource(int dataSourceId) throws PersistenceException
    {
    	logger.debug("In findInUseParameter of ReportPersistenceService ");
        return reportsPersistence.findInUseDataSource(dataSourceId);
    }
    
    /**
     * view Parameter
     * @param datasourceId
     * @return
     */
    
    public List<ReportsParams> viewParameter(int parameterId) throws PersistenceException
    {
    	logger.debug("In viewParameter of ReportPersistenceService ");
        return reportsPersistence.viewParameter(parameterId);
    }
    
    /**
     * View DataSource
     * @param datasourceId
     * @return
     */
    
    public List<ReportsDataSource> viewDataSource(int dataSourceId) throws PersistenceException
    {
    	logger.debug("In findInUseParameter of ReportPersistenceService ");
        return reportsPersistence.viewDataSource(dataSourceId);
    }
    
    /**
     * Create link between report and parameter
     * @param reportsParamsMapValue
     */
    
    public void createReportsParamsMap(ReportsParamsMapValue reportsParamsMapValue)
    {
        logger.debug("In createReportsParamsMap of ReportPersistenceService ");
        try{
        	reportsPersistence.createReportsParamsMap(reportsParamsMapValue);
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
    }
    
    /**
     * Deletes link between a report and parameter
     * @param reportsParamsMapValue
     */
    
    public void deleteReportsParamsMap(ReportsParamsMapValue reportsParamsMapValue)
    {
        logger.debug("In deleteReportsParamsMap of ReportPersistenceService ");
        try{
        	reportsPersistence.deleteReportsParamsMap(reportsParamsMapValue);
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
    }
    
    /**
     * Sets link between report and jasper report file
     * @param reportsJasperMap
     */
    
    public void updateReportsJasperMap(ReportsJasperMap reportsJasperMap)
    {
        logger.debug("In updateReportsJasperMap of ReportPersistenceService ");
        try{
        	reportsPersistence.updateReportsJasperMap(reportsJasperMap);
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
    }
    
    /**
     * Finds a jasper for a given reportid
     * @param reportId
     * @return
     */
    
    public List<ReportsJasperMap> findJasperOfReportId(int reportId) throws PersistenceException
    {
    	logger.debug("In findParamsOfReportId of ReportPersistenceService ");
        return reportsPersistence.findJasperOfReportId(reportId);
    }
    
    /**
     * Creates a connection for Jasper
     * @return
     */
    public Connection getJasperConnection()
    {
    	logger.debug("In getConnection of ReportPersistenceService");
    	Connection con = null;
    	try{
    		con =  reportsPersistence.getJasperConnection();
        }catch(ApplicationException ape)
        {
        	ape.printStackTrace();
        }catch(SystemException se)
        {
        	se.printStackTrace();
        }
        return con;
    	 
    }
}

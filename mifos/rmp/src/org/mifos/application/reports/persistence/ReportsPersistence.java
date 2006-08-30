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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;


import org.hibernate.Query;
import org.mifos.application.reports.business.ReportsParams;
import org.mifos.application.reports.business.ReportsParamsValue;
import org.mifos.application.reports.business.ReportsParamsMap;
import org.mifos.application.reports.business.ReportsParamsMapValue;
import org.mifos.application.reports.business.ReportsDataSource;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.reports.exceptions.ReportException;

public class ReportsPersistence extends Persistence {	
	
	public ReportsPersistence(){
		
	}	
	/**
	 * Lists all the Report Categories. 
	 * The list also contains the set of Reports within a particular category
	 * @return
	 */	
	public List<ReportsCategoryBO> getAllReportCategories()
	{					
		Query query = HibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTS);
		return query.list();
	}
	
	/**
	 * List all Report Parameters
	 * @return
	 */
	
	public List<ReportsParams> getAllReportParams()
    {
    	Query query = HibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTSPARAMS);
        return query.list();
    }
	
	
    
    /**
     * Create Report Parameter
     * @param reportsParams
     * @throws ApplicationException
     * @throws SystemException
     */
    
     public void createReportParams(ReportsParamsValue reportsParams) throws ApplicationException,SystemException
    {
    	
      Session session = null;
    	Transaction trxn = null;
    	try {
    	
    		session = HibernateUtil.getSession();
    		trxn = session.beginTransaction();
    		session.save(reportsParams);
    		session.flush();
    		trxn.commit();
    	} catch (HibernateProcessException hpe) {
    		trxn.rollback();
    		throw new ApplicationException(hpe);
    	}
    	catch (HibernateException hpe) 
    	{ 
    		hpe.printStackTrace();
    	trxn.rollback();
    		
    		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
    	}
    	catch (Exception e) 
    	{ 	trxn.rollback();
    		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
    	}
    	finally {
    		HibernateUtil.closeSession(session);
    	}
    }
     
     /**
      * Deletes Report Parameter
      * @param reportsParams
      * @throws ApplicationException
      * @throws SystemException
      */
     
     public void deleteReportParams(ReportsParamsValue reportsParams) throws ApplicationException,SystemException
     {
       Session session = null;
     	Transaction trxn = null;
     	try {
     		session = HibernateUtil.getSession();
     		trxn = session.beginTransaction();
     		session.delete(reportsParams);
     		session.flush();
     		trxn.commit();
     	} catch (HibernateProcessException hpe) {
     		trxn.rollback();
     		throw new ApplicationException(hpe);
     	}
     	catch (HibernateException hpe) 
     	{ 
     		hpe.printStackTrace();
     	trxn.rollback();
     		
     		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
     	}
     	catch (Exception e) 
     	{ 	trxn.rollback();
     		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
     	}
     	finally {
     		HibernateUtil.closeSession(session);
     	}
     }
     
     /**
 	 * List all DataSource
 	 * @return
 	 */
 	 
 	
     public List<ReportsDataSource> getAllReportDataSource()
     {
     	Query query = HibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTSDATASOURCE);
         return query.list();
     }
     
     /**
      * Create Report DataSource
      * @param reportsDataSource
      * @throws ApplicationException
      * @throws SystemException
      */
     
      public void createReportsDataSource(ReportsDataSource reportsDataSource) throws ApplicationException,SystemException
     {
       Session session = null;
     	Transaction trxn = null;
     	try {
     		session = HibernateUtil.getSession();
     		trxn = session.beginTransaction();
     		session.save(reportsDataSource);
     		session.flush();
     		trxn.commit();
     	} catch (HibernateProcessException hpe) {
     		trxn.rollback();
     		throw new ApplicationException(hpe);
     	}
     	catch (HibernateException hpe) 
     	{ 
     		hpe.printStackTrace();
     	trxn.rollback();
     		
     		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
     	}
     	catch (Exception e) 
     	{ 	trxn.rollback();
     		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
     	}
     	finally {
     		HibernateUtil.closeSession(session);
     	}
     }
      
      /**
       * Deletes Report DataSource
       * @param reportsDataSource
       * @throws ApplicationException
       * @throws SystemException
       */
      
      public void deleteReportsDataSource(ReportsDataSource reportsDataSource) throws ApplicationException,SystemException
      {
        Session session = null;
      	Transaction trxn = null;
      	try {
      		session = HibernateUtil.getSession();
      		trxn = session.beginTransaction();
      		session.delete(reportsDataSource);
      		session.flush();
      		trxn.commit();
      	} catch (HibernateProcessException hpe) {
      		trxn.rollback();
      		throw new ApplicationException(hpe);
      	}
      	catch (HibernateException hpe) 
      	{ 
      		hpe.printStackTrace();
      	trxn.rollback();
      		
      		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
      	}
      	catch (Exception e) 
      	{ 	trxn.rollback();
      		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
      	}
      	finally {
      		HibernateUtil.closeSession(session);
      	}
      }
      
      /**
   	 * List all ReportParamsMap
   	 * @return
   	 */
   	 
   	
       public List<ReportsParamsMap> getAllReportParamsMap()
       {
       	Query query = HibernateUtil.getSessionTL().getNamedQuery(ReportsConstants.GETALLREPORTSPARAMSMAP);
           return query.list();
       }
       
   /**
  	 * List all Parameters of given Report Id
  	 * @return
  	 */
      	 
      	
      public List<ReportsParamsMap> findParamsOfReportId(int reportId)
      {
    	  Map<String,String> queryParameters = new HashMap<String,String>();
    	  List<ReportsParamsMap> queryResult = null;
    	  queryParameters.put("reportId", reportId+"");
  		  queryResult = executeNamedQuery(ReportsConstants.FIND_PARAMS_OF_REPORTID,queryParameters);
  		  return queryResult;

      }
      
      /**
    	 * Finds is in use parameter
    	 * @return
    	 */
        	 
        	
        public List<ReportsParamsMap> findInUseParameter(int parameterId)
        {
      	  Map<String,String> queryParameters = new HashMap<String,String>();
      	  List<ReportsParamsMap> queryResult = null;
      	  queryParameters.put("parameterId", parameterId+"");
    		  queryResult = executeNamedQuery(ReportsConstants.FIND_IN_USE_PARAMETER,queryParameters);
    		  return queryResult;

        }
        
     /**
   	 * Finds is in use DataSource
   	 * @return
   	 */
       	 
       	
       public List<ReportsParams> findInUseDataSource(int dataSourceId)
       {
     	  Map<String,String> queryParameters = new HashMap<String,String>();
     	  List<ReportsParams> queryResult = null;
     	  queryParameters.put("dataSourceId", dataSourceId+"");
   		  queryResult = executeNamedQuery(ReportsConstants.FIND_IN_USE_DATASOURCE,queryParameters);
   		  return queryResult;

       }
       
       /**
      	 * view parameter
      	 * @return
      	 */
          	 
          	
          public List<ReportsParams> viewParameter(int parameterId)
          {
        	  Map<String,String> queryParameters = new HashMap<String,String>();
        	  List<ReportsParams> queryResult = null;
        	  queryParameters.put("parameterId", parameterId+"");
      		  queryResult = executeNamedQuery(ReportsConstants.VIEW_PARAMETER,queryParameters);
      		  return queryResult;

          }
       
          /**
        	 * view DATASOURCE
        	 * @return
        	 */
            	 
            	
            public List<ReportsDataSource> viewDataSource(int dataSourceId)
            {
          	  Map<String,String> queryParameters = new HashMap<String,String>();
          	  List<ReportsDataSource> queryResult = null;
          	  queryParameters.put("dataSourceId", dataSourceId+"");
        		  queryResult = executeNamedQuery(ReportsConstants.VIEW_DATASOURCE,queryParameters);
        		  return queryResult;

            }
       
       /**
        * Creates a link between a report and a parameter
        * @param reportsParamsMapValue
        * @throws ApplicationException
        * @throws SystemException
        */
       
        public void createReportsParamsMap(ReportsParamsMapValue reportsParamsMapValue) throws ApplicationException,SystemException
       {
         Session session = null;
       	Transaction trxn = null;
       	try {
       		session = HibernateUtil.getSession();
       		trxn = session.beginTransaction();
       		session.save(reportsParamsMapValue);
       		session.flush();
       		trxn.commit();
       	} catch (HibernateProcessException hpe) {
       		trxn.rollback();
       		throw new ApplicationException(hpe);
       	}
       	catch (HibernateException hpe) 
       	{ 
       		hpe.printStackTrace();
       	trxn.rollback();
       		
       		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
       	}
       	catch (Exception e) 
       	{ 	trxn.rollback();
       		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
       	}
       	finally {
       		HibernateUtil.closeSession(session);
       	}
       }
        
        /**
         * Deletes a link between report and a parameter
         * @param reportsParamsMapValue
         * @throws ApplicationException
         * @throws SystemException
         */
        
        public void deleteReportsParamsMap(ReportsParamsMapValue reportsParamsMapValue) throws ApplicationException,SystemException
        {
          Session session = null;
        	Transaction trxn = null;
        	try {
        		session = HibernateUtil.getSession();
        		trxn = session.beginTransaction();
        		session.delete(reportsParamsMapValue);
        		session.flush();
        		trxn.commit();
        	} catch (HibernateProcessException hpe) {
        		trxn.rollback();
        		throw new ApplicationException(hpe);
        	}
        	catch (HibernateException hpe) 
        	{ 
        		hpe.printStackTrace();
        	trxn.rollback();
        		
        		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
        	}
        	catch (Exception e) 
        	{ 	trxn.rollback();
        		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
        	}
        	finally {
        		HibernateUtil.closeSession(session);
        	}
        }
       /**
        * sets a link between report and a jasper file
        * @param reportsJasperMap
        * @throws ApplicationException
        * @throws SystemException
        */
        
        public void updateReportsJasperMap(ReportsJasperMap reportsJasperMap) throws ApplicationException,SystemException
        {
          Session session = null;
        	Transaction trxn = null;
        	try {
        		session = HibernateUtil.getSession();
        		trxn = session.beginTransaction();
        		session.update(reportsJasperMap);
        		session.flush();
        		trxn.commit();
        	} catch (HibernateProcessException hpe) {
        		trxn.rollback();
        		throw new ApplicationException(hpe);
        	}
        	catch (HibernateException hpe) 
        	{ 
        		hpe.printStackTrace();
        	trxn.rollback();
        		
        		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
        	}
        	catch (Exception e) 
        	{ 	trxn.rollback();
        		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
        	}
        	finally {
        		HibernateUtil.closeSession(session);
        	}
        }
        
        
        /**
      	 * List a Jasper of given Report Id
      	 * @return
      	 */
          	 
          	
          public List<ReportsJasperMap> findJasperOfReportId(int reportId)
          {
        	  Map<String,String> queryParameters = new HashMap<String,String>();
        	  List<ReportsJasperMap> queryResult = null;
        	  queryParameters.put("reportId", reportId+"");
      		  queryResult = executeNamedQuery(ReportsConstants.FIND_JASPER_OF_REPORTID,queryParameters);
      		
      		  return queryResult;

          }
          
          /**
           * Creates a connection
           * @return
           * @throws ApplicationException
           * @throws SystemException
           */
          
          public Connection getJasperConnection() throws ApplicationException,SystemException
          {
            Session session = null;
            Connection con = null;
          	try {
          		session = HibernateUtil.getSession();
          		con = session.connection();
          		
          	} catch (HibernateProcessException hpe) {
          		throw new ApplicationException(hpe);
          	}
          	catch (HibernateException hpe) 
          	{ 
          		hpe.printStackTrace();
          	
          		throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION);
          	}
          	catch (Exception e) 
          	{ 	throw new ReportException(ReportsConstants.CREATE_FAILED_EXCEPTION,e);
          	}
          	finally {
          		HibernateUtil.closeSession(session);
          	}
          	return con;
          }
          
}

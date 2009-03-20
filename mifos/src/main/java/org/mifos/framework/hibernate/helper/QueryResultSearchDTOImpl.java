/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.framework.hibernate.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.HibernateSearchException;

/**
 *  This is the class that is returned on a search operation. 
 *  Search would typically result in a set of search result objects , 
 *  these search result objects would be obtained through hibernate 
 *  scroll for pagination in the front end , 
 *  the associate hibernate session would be held in this object , 
 *  a call to close from the front end on this interface would result 
 *  in the hibernate session object getting closed.
 */

public class QueryResultSearchDTOImpl extends QueryResultDTOImpl
{

  java.util.List list = new java.util.ArrayList(); 
  private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);
  
    /** Set the query inputs which will be used for query execution */
    @Override
    public void setQueryInputs(QueryInputs queryInputs) throws HibernateSearchException
    {
	  if(queryInputs == null)
		  throw new HibernateSearchException(HibernateConstants.SEARCH_INPUTNULL);

       if(queryInputs.getBuildDTO())
       {
			this.queryInputs = queryInputs;
			dtoBuilder = new DTOBuilder();			
			this.buildDTO = queryInputs.getBuildDTO();
	   }

  }


    /**
	 * Returns the requested set of search result objects based on the
	 * pagination at the front end.
	 */
    @Override
	public java.util.List get(int position, int noOfObjects) 
    throws HibernateSearchException
    {   
    	Session session = null;
    	java.util.List returnList = new java.util.ArrayList();
    	java.util.List list = new java.util.ArrayList();
    	try
    	{
    		session=QuerySession.openSession();
    		Query query=prepareQuery(session,queryInputs.getQueryStrings()[1]);        	
        	query.setFirstResult(position);
        	query.setMaxResults(noOfObjects);        	
        	list=query.list();   
        	logger.debug("\n\nInside get of QueryResultSearchDTOImpl.java . size of main query="+list.size());
        	this.queryInputs.setTypes(query.getReturnTypes());
        	dtoBuilder.setInputs(queryInputs);	
    		returnList = new java.util.ArrayList();    
    		if(list!=null)
 		   	{		    	   
	    	   for(int i=0;i < list.size(); i++)	  	     
	     	   {  
	    		   	  if(buildDTO)
	    		   	  {	    
			    		  returnList.add(buildDTO((Object[])list.get(i)));
			    		  
	    		   	  }
	  		 		  else
	  		 		  {
		    			  if(i<noOfObjects)
		 	    		  {		
		    				  returnList.add(list.get(i));
		 	    		  }		  
	  		 		  }
	    	   }    	  
 		   }	       	
	       QuerySession.closeSession(session);				     
	   }
	   catch(Exception e)
	   {		   
		   throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED,e);
	   }
	  
	   return returnList;
	 }
    
    /**
     * Returns the records valid for the query
     */
    @Override
	public int getSize() throws HibernateSearchException
    {    
    	Session session = null;
    	try
    	{
    		session=QuerySession.openSession();
	   		if(this.queryInputs == null)
	   		{
	   			  throw new HibernateSearchException(HibernateConstants.SEARCH_INPUTNULL);
	   		}	   			   		
	   		Query query=prepareQuery(session,queryInputs.getQueryStrings()[0]);	   		
	   		Integer resultSetCount=((Number) query.uniqueResult()).intValue();
	   		logger.debug("\n\nInside get of QueryResultSearchDTOImpl.java . size of count query="+resultSetCount);
	   		this.queryInputs.setTypes(query.getReturnTypes());
	   		dtoBuilder.setInputs(queryInputs);	   		
	   		if(resultSetCount!=null && resultSetCount>0)
			size= resultSetCount;
	   		QuerySession.closeSession(session);	     
 	   }
 	   catch(Exception e)
 	   {		   
 		   throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED,e);
 	   }	   
 	   return size;
	}

 

  public Query prepareQuery(Session session,String namedQuery) throws HibernateSearchException
  {	  		
		if(this.queryInputs == null){
			  throw new HibernateSearchException(HibernateConstants.SEARCH_INPUTNULL);
		}
		List<Param> paramList=queryInputs.getParamList();		
		Query query = null;		
		query = session.getNamedQuery(namedQuery);
		if(paramList!=null){		
			for(int i=0;i<paramList.size();i++){
				if(paramList.get(i)!=null)
				{
					query.setParameter(paramList.get(i).getName(),paramList.get(i).getValue());
				}
			}
		}		
		return query;		
  }
 
}

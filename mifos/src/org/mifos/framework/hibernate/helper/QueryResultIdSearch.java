/**

 * QueryResultIdSearch  version: 1.0



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
package org.mifos.framework.hibernate.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.valueobjects.CustomerSearch;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.SystemException;
/**
 * @author imtiyazmb
 *
 */
public class QueryResultIdSearch extends QueryResultSearchDTOImpl {

	java.util.List list = null ;
	
	public List get(int position, int noOfObjects) throws HibernateSearchException {
		java.util.List returnList = new java.util.ArrayList();
		
		Session session = null;	
		Query query = null;
    	try
    	{
    		session=QuerySession.getSession();	
			if(list!=null)
 		   	{		    			
	    	   for(int i=0;i < list.size(); i++)	  	     
	     	   {  
	    		   	  if(buildDTO)
	    		   	  {	    
			    		  Object record = buildDTO((Object[])list.get(i));
			    		  CustomerSearch cs = ((CustomerSearch)record);
			    		  Integer customerId = cs.getCustomerId();
			    		  query= session.createQuery("select account.globalAccountNum from Account account where account.customer.customerId=:customerId and account.accountTypeId=:accountTypeId");
			    		  query.setInteger("customerId",customerId).setShort("accountTypeId",(short)1);
			    		  List lst = query.list();			    		  
			    		  query= session.createQuery("select account.globalAccountNum from Account account where account.customer.customerId=:customerId and account.accountTypeId=:accountTypeId");
			    		  query.setInteger("customerId",customerId).setShort("accountTypeId",(short)2);
			    		  List listOfSavingAccounts = query.list();			    		  
			    		  cs.setSavingsGlobalAccountNum(listOfSavingAccounts);
			    		  cs.setLoanGlobalAccountNum(lst);			    		 
			    		  returnList.add(cs);
			    		  
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

	
	public int getSize() throws HibernateSearchException {		
 	   if(list!=null)
 		   return list.size();
		return 1;
	}
	
	public List customerIdSearch(String searchString,Short officeId) throws SystemException
	{
		Session session=null;
		Query query=null;								
		session=QuerySession.getSession();				
		if(officeId != null && officeId.shortValue()==0)
		{				
			query=session.getNamedQuery(NamedQueryConstants.CUSTOMER_IDSEARCH_WITHOUTOFFICE); 
			query.setString("SEARCH_STRING",searchString);
		}
		else
		{			
			query=session.getNamedQuery(NamedQueryConstants.CUSTOMER_IDSEARCH); 
			query.setString("SEARCH_STRING",searchString);
			query.setShort("OFFICEID",officeId);
							
		}		
		list=query.list();		
		this.queryInputs.setTypes(query.getReturnTypes());
		dtoBuilder.setInputs(queryInputs);
		QuerySession.closeSession(session);		
		return list;
	}
	
	
}

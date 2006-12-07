/**

 * QueryResultLoanAccountIdSearch  version: 1.0



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
package org.mifos.framework.hibernate.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.business.CustomerSearch;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.framework.exceptions.HibernateSearchException;

public class QueryResultAccountIdSearch extends QueryResultsMainSearchImpl{
	


		String searchString = null;
		
		@Override
		public List get(int position, int noOfObjects) throws HibernateSearchException {
			java.util.List returnList = new java.util.ArrayList();
			
			Session session = null;	
			Query query = null;
			Short accountTypeId = null ;
			Short customerType = null ;
	    	try
	    	{
	    		session=QuerySession.getSession();	
	    		  query =prepareQuery(session,queryInputs.getQueryStrings()[1]);
	    		  list = query.list();
	    		  this.queryInputs.setTypes(query.getReturnTypes());
	          	dtoBuilder.setInputs(queryInputs);	
				if(list!=null)
	 		   	{		    			
		    	   for(int i=0;i < list.size(); i++)	  	     
		     	   {  
		    		   	  if(buildDTO)
		    		   	  {	    
				    		  Object record = buildDTO((Object[])list.get(i));
				    		  CustomerSearch cs = ((CustomerSearch)record);				    						    		 
				    		  Integer customerId = cs.getCustomerId();	
				    		  short customerLevel = cs.getCustomerType();				    		  
				    		  query =  session.getNamedQuery(NamedQueryConstants.ACCOUNT_LIST_ID_SEARCH);
				    		  query.setInteger("customerId",customerId).setShort("loanAccountTypeId",CustomerSearchConstants.LOAN_TYPE);
				    		  query.setShort("savingsAccountTypeId",CustomerSearchConstants.SAVINGS_TYPE);
				    		  query.setString("searchString",searchString);
				    		  List accountNumAndTypeId = query.list();				    		  
				    		  Object[] obj2 = (Object[])accountNumAndTypeId.get(0);
							  cs.setLoanGlobalAccountNumber(obj2[0].toString());							  
							  accountTypeId = (Short)obj2[1];			    		  
				    		  
				    		  if(accountTypeId !=null  && customerLevel==CustomerConstants.CLIENT_LEVEL_ID && accountTypeId==CustomerSearchConstants.LOAN_TYPE)
				    			  customerType = (short)4;
				    		  else if(accountTypeId !=null  && customerLevel==CustomerConstants.CLIENT_LEVEL_ID && accountTypeId==CustomerSearchConstants.SAVINGS_TYPE)
				    			  customerType = (short)6;
				    		  else if(accountTypeId !=null  && customerLevel==CustomerConstants.GROUP_LEVEL_ID && accountTypeId==CustomerSearchConstants.LOAN_TYPE)
				    			  customerType = (short)5;
				    		  else if(accountTypeId !=null  && customerLevel==CustomerConstants.GROUP_LEVEL_ID && accountTypeId==CustomerSearchConstants.SAVINGS_TYPE)
				    			  customerType = (short)7;
				    		  else if(accountTypeId !=null  && customerLevel==CustomerConstants.CENTER_LEVEL_ID && accountTypeId==CustomerSearchConstants.SAVINGS_TYPE)
				    			  customerType = (short)8;			    			  
				    		  	
				    		  cs.setCustomerType(customerType);
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
/*		
		public List accountIdSearch(String searchString,Short officeId) throws SystemException
		{
			this.searchString = searchString;
			
			try{
				Session session=null;			
				session= QuerySession.getSession();			
				Query query=null;	
				if( officeId.shortValue()==0)
				{			
					query=session.getNamedQuery(NamedQueryConstants.ACCOUNT_ID_SEARCH_NOOFFICEID); 
					query.setString("SEARCH_STRING",searchString);	
				}
				else
				{			
				
					query=session.getNamedQuery(NamedQueryConstants.ACCOUNT_ID_SEARCH); 
					query.setString("SEARCH_STRING",searchString);							
					query.setShort("OFFICEID",officeId);
				}
					
					
				list=query.list();				
				this.queryInputs.setTypes(query.getReturnTypes());
				dtoBuilder.setInputs(queryInputs);
				QuerySession.closeSession(session);
			}
			catch(HibernateProcessException  hpe) {
					throw new SystemException();
			}
			return list;
		}
	*/

		@Override
		public int getSize() throws HibernateSearchException {
		   	Session session = null;
	    	try
	    	{
	    		session=QuerySession.getSession();
		   		if(this.queryInputs == null)
		   		{
		   			  throw new HibernateSearchException(HibernateConstants.SEARCH_INPUTNULL);
		   		}	   			   		
		   		Query query=prepareQuery(session,queryInputs.getQueryStrings()[0]);	   		
		   		Integer resultSetCount=(Integer) query.uniqueResult();
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

		public void setSearchString(String searchString){
			
			this.searchString = searchString;
		}
}

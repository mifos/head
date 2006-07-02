/**

 * EditClientStatusDAO.java    version: xxx

 

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

package org.mifos.application.customer.client.dao;

import java.sql.Date;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.EditClientStatus;
import org.mifos.application.customer.dao.CustomerNoteDAO;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerFlag;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.LogInfo;
import org.mifos.framework.components.audit.util.helpers.LogValueMap;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class acts as DAO for edit client status module.
 * @author ashishsm
 *
 */
public class EditClientStatusDAO extends DAO {

/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();

	/**
	 * It updates the client record with the state change. It also checks for the flags 
	 * if the status is being changed to closed or cancelled
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context)throws SystemException,ApplicationException{
		Transaction tx = null;
		Session session = null;
		
		EditClientStatus editClientStatusVO =(EditClientStatus)context.getValueObject();
		
		//retrieves the old client details from the session
		Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_STATUS_CHANGE);	
		
		//obtains the old status
		short oldStatus=client.getStatusId();
		
		//obtains the status id to which the client is being changed
		short newStatus=editClientStatusVO.getStatusId();
		
		//user id of the logged in user
		short userId = context.getUserContext().getId();
		CustomerNoteDAO customerNoteDAO = new CustomerNoteDAO();
		CustomerNote customerNote = editClientStatusVO.getCustomerNote();
		
		  try {
			  LogValueMap logValueMap=new LogValueMap();
			  logValueMap.put(AuditConstants.REALOBJECT,new Customer());
			  logValueMap.put("customerFlag",AuditConstants.REALOBJECT);
			  LogInfo logInfo= new LogInfo(client.getCustomerId(),"Client",context,logValueMap);
			  session = HibernateUtil.getSessionWithInterceptor(logInfo) ;
			//session = getHibernateSession();
			tx = session.beginTransaction();
			//sets the old client with the new status id  and updates the client
			client.setStatusId(newStatus);
			CustomerHelper helper=new CustomerHelper();
			if(client.getParentCustomer()==null && client.getStatusId().shortValue()==CustomerConstants.CLIENT_APPROVED && client.getCustomerActivationDate()==null)
				helper.saveMeetingDetails(client,session, context.getUserContext());
			
			if(client.getStatusId() == CustomerConstants.CLIENT_APPROVED && client.getCustomerActivationDate()==null){
				CustomerUtilDAO.applyFees(client,session);
				client.setCustomerActivationDate(new Date(new java.util.Date().getTime()));
			}
			
			session.update(client);
			
			//calls the addNotes in customerNoteDAO passing the current session
			customerNoteDAO.addNotes(session,customerNote);
			
			// handle group flags
			handleGroupFlags(session,editClientStatusVO,client, userId);
			tx.commit();
		  }catch(StaleStateException sse){
			  tx.rollback();
			  Object values[]=new Object[1];
			  values[0]=client.getDisplayName();
			  throw new CustomerException(CustomerConstants.CUSTOMER_INVALID_VERSION_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.CENTER,context.getUserContext().getPereferedLocale())});
		  }
		  catch (HibernateProcessException hpe) {
			  tx.rollback();
			  throw hpe;
			}catch (HibernateException hpe) {
				tx.rollback();
				System.out.println("-------------------------Inside update");
				throw new CustomerException(ClientConstants.UPDATE_FAILED , hpe);
			}  finally {
				HibernateUtil.closeSession(session);
			}
  	}
  	
  	/**
  	 * Method to handle the flags associated to the status. If the client has already been blacklisted, that flag will
  	 * always be associated with the client. In the case of a status change and a new flag being associated with the status, the
  	 * old flag except for blacklisted will be deleted
  	 * @param session
  	 * @param editStatusVO
  	 * @param oldCustomer
  	 * @param createdBy
  	 * @throws ApplicationException
  	 * @throws SystemException
  	 */
	private void handleGroupFlags(Session session, EditClientStatus editStatusVO, Customer oldCustomer, short createdBy)throws ApplicationException,SystemException{
		
		boolean isBlacklisted=false;
		try{
			Set customerFlag = oldCustomer.getCustomerFlag();
			if(customerFlag!=null){
				Object flagObjects[]=customerFlag.toArray();
				//checking the client has alrady been blacklisted
				for(int i=0;i<flagObjects.length;i++){
					CustomerFlag flag = (CustomerFlag)flagObjects[i];
					if(new CustomerUtilDAO().isBlacklisted(flag.getFlagId())){
				
							isBlacklisted = true;
					}else{
				
						session.delete(flag);
					}
				}
			}
			if(editStatusVO.getFlagId()!=null &&  editStatusVO.getFlagId().shortValue()!=0 ){
				//if the flag is not blacklisted then save the new flag
				if(editStatusVO.getFlagId()!=GroupConstants.BLACKLISTED || !isBlacklisted){
					CustomerFlag cf = new CustomerFlag();
					cf.setCustomer(oldCustomer);
					cf.setFlagId((short)editStatusVO.getFlagId().intValue());
					cf.setCreatedBy(createdBy);
					cf.setCreatedDate(new Date(new java.util.Date().getTime()));
					cf.setFlagStatus(Short.valueOf("1"));
					session.save(cf);
				}	
			}
			
		}catch (HibernateException hpe) {
			System.out.println("-------------------------Inside update flag");
			throw new CustomerException(ClientConstants.UPDATE_FAILED , hpe);
		}  
	}

		
}



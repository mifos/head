/**

 * EditGrpMembershipBusinessProcessor.java    version: xxx

 

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

package org.mifos.application.customer.client.business.handlers;

import java.util.List;

import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.client.dao.EditGrpMembershipDAO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.helpers.ClientHelper;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.EditGrpMembership;
import org.mifos.application.customer.dao.ViewClosedAccountsDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerStateChangeException;
import org.mifos.application.customer.group.dao.GroupDAO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.GroupHelper;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.helpers.PathConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerSearchInput;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class acts as a Business Processor for editing group membership of a client.
 * @author ashishsm
 *
 */
public class EditGrpMembershipBusinessProcessor extends MifosBusinessProcessor {

	/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	/**
	/** 
	 * This method is called whenever client transfer across groups in same branch is requested.
	 * It loads group search page
	 * @param context an instance of Context
	 * @throws ApplicationException
	 */ 
  	public void loadGroupTransfer(Context context)throws ApplicationException  {
  		try{
			//TODO:get the office id from the context
			Short officeId=context.getUserContext().getBranchId();
			//Short officeId=(short)13;
			CustomerSearchInput clientSearchInput= new CustomerSearchInput();
			clientSearchInput.setOfficeId(officeId);
			clientSearchInput.setCustomerInputPage(ClientConstants.INPUT_GROUP_TRANSFER);
			context.addBusinessResults(CustomerConstants.CUSTOMER_SEARCH_INPUT,clientSearchInput);
  		}catch(Exception e){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
  	}
	
	
	
	/**
	 * Checks if the group is still in the desired state.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context)throws SystemException,ApplicationException{
		
		try{
			Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_TRANSFER);
			short newStatus=client.getStatusId().shortValue();
			GroupDAO groupDAO=this.getGroupDAO();
			EditGrpMembership groupTransferVO = (EditGrpMembership)context.getValueObject();
			if(client.getParentCustomer().getCustomerId().intValue() == groupTransferVO.getParentGroupId().intValue())
				throw new CustomerException(ClientConstants.SAME_GROUP_TRANSFER_EXCEPTION);

			Customer oldGroup=groupDAO.findByGroupId(groupTransferVO.getParentGroupId());
			boolean isNotValidStatusChange = false;
			//GroupDAO groupDAO=this.getGroupDAO();
			//if(newStatus==GroupConstants.CLOSED)
			//	 	checkIfGroupCanBeClosed(groupVO.getCustomerId());
			if( (newStatus == CustomerConstants.CLIENT_APPROVED 
					  ||newStatus == CustomerConstants.CLIENT_PENDING )
					  && client.getGroupFlag().shortValue() != 0 ) {
				short parentStatusId = oldGroup.getStatusId().shortValue();
				isNotValidStatusChange = new ClientHelper().checkGroupStatus(newStatus ,parentStatusId);
				logger.debug("In isNotValidStatusChange-------------------------------------------"+isNotValidStatusChange);
				if(isNotValidStatusChange){
					throw new CustomerStateChangeException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale()),labelConfig.getLabel(ConfigurationConstants.CLIENT,context.getUserContext().getPereferedLocale())});
				}
				
			}
			if(!validateGroupTransfer(client,oldGroup))
				context.addAttribute(new CustomerHelper().getResultObject(GroupConstants.GROUP_PARENT, oldGroup));
			else{
				throw new CustomerStateChangeException(CustomerConstants.CLIENT_HAS_ACTIVE_LOAN);
			}
				//groupDAO.updateParent(client,context);
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}	
	}
		/** 
	   * This method returns instance of GroupDAO
	   * @return GroupDAO instance
	   * @throws SystemException
	   */  
		private GroupDAO getGroupDAO()throws SystemException{
			GroupDAO groupDAO=null;
			try{
				groupDAO = (GroupDAO)getDAO(org.mifos.application.configuration.util.helpers.PathConstants.GROUP_PATH);
			}catch(ResourceNotCreatedException rnce){
			}
		return groupDAO;
		}
		
		/** 
		 *  This method is called to validate, whether a client can be transferred to another group. It checks the status of the current loan accoutns. 
		 *  All loan accoutns have to be closed before transferring 
		 *  @param group 
		 *  @param parent  
		 *  @throws ApplicationException
		 *  @throws SystemException
		 */   	
		public boolean validateGroupTransfer(Customer client, Customer parent)throws ApplicationException,SystemException{
			return new ViewClosedAccountsDAO().isCustomerWithActiveAccounts(client.getCustomerId());
			
		}
		/** 
		 * This method is called whenever client transfer in different office is requested.
		 * It loads branch search page
		 * @param context an instance of Context
		 * @throws ApplicationException
		 */   	
	  	public void loadTransfer(Context context) throws SystemException,ApplicationException {
			 try{
				  List branchList= new GroupHelper().getBranchList();
				  context.addAttribute(new CustomerHelper().getResultObject(GroupConstants.BRANCH_LIST, branchList));
			 }catch(SystemException se){
					throw se;
			 }catch(ApplicationException ae){
					throw ae;
			 }catch(Exception e){
					throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
			 }
	  	}
	  	/** 
		 *  This method is called whenever a client has chosen another branch to be transferred to. 
		 *  The client details till the point of transfer will be stored in the old branch. The new branch will have only 
		 *  those records after the transfer. 
		 *  The client will get a new system id. Hence the client is created as another record in the customer table
		 *   
		 *  otherwise it directly shows confirmation page
		 *  @param context an instance of Context
		 *  @throws ApplicationException
		 *  @throws SystemException
		 */   	

	 public void updateBranchRSM(Context context) throws ApplicationException,SystemException {
		  try{
			  
			 // if(ConfigurationConstants.)
			  Client client = (Client)context.getBusinessResults("transferBranchObject");
			  logger.debug("CUSTOMER VERSION IN EDIT GRP MEMSHIP BUSINESS PROCESSOR: "+ client.getVersionNo());
			  EditGrpMembership branchTransferVO = (EditGrpMembership)context.getValueObject();
			  /*LoanDAO loanDAO= new LoanDAO();
			  loanDAO.isLoanAccountActive(client.getCustomerId());*/
			  
			   	  
			  //client status set to partial
			  if(client.getStatusId() == CustomerConstants.CLIENT_APPROVED){
					client.setStatusId(CustomerConstants.CLIENT_ONHOLD); 
			  }
			  
			     
			  EditGrpMembershipDAO clientTransferDAO = this.getClientTransferDAO();
			  logger.debug("OFFICEiD IN TRANSFER bp: "+branchTransferVO.getOffice().getOfficeId());
			  if(client.getOffice().getOfficeId().shortValue() == branchTransferVO.getOffice().getOfficeId().shortValue())
					throw new CustomerException(ClientConstants.SAME_OFFICE_TRANSFER_EXCEPTION);

			  clientTransferDAO.updateBranchRSM(client , branchTransferVO.getOffice().getOfficeId(), context.getUserContext());
			  
			
		  }catch(SystemException se){
				throw se;
		  }catch(ApplicationException ae){
				throw ae;
		  }catch(Exception e ){
				throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		  }
	 }
	 /** 
	   * This method returns instance of ClientCreationDAO
	   * @return ClientCreationDAO instance
	   * @throws SystemException
	   */  
	private EditGrpMembershipDAO getClientTransferDAO()throws SystemException{
		EditGrpMembershipDAO clientTransferDAO=null;
		try{
			clientTransferDAO = (EditGrpMembershipDAO)getDAO(PathConstants.CLIENT_TRANSFER_CHANGE);
		}catch(ResourceNotCreatedException rnce){
			throw rnce;
		}
		return clientTransferDAO;
	}

}

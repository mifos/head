/**

 * EditClientStatusBusinessProcessor.java    version: xxx

 

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

package org.mifos.application.customer.client.business.handlers;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessage;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.EditClientStatus;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.dao.ViewClosedAccountsDAO;
import org.mifos.application.customer.exceptions.AssociatedObjectStaleException;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerStateChangeException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.valueobjects.Group;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class acts as Business Processor for edit client status module.
 * @author ashishsm
 *
 */
public class EditClientStatusBusinessProcessor extends MifosBusinessProcessor {
/**An insatnce of the logger which is used to log statements */
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	/**
	/**
	 * 
	 */
	public EditClientStatusBusinessProcessor() {
		super();
		
	}
	
	/**
	 * This method is called to load the page with the list of possible status that a client can be moved to
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context) throws SystemException,ApplicationException {
		
		try{
			//short localeId=context.getUserContext().getLocaleId();
			short localeId=1;
			CustomerUtilDAO customerUtilDAO =new CustomerUtilDAO();
			Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_STATUS_CHANGE);
			EditClientStatus editClientStatus =(EditClientStatus)context.getValueObject();
			editClientStatus.setCurrentStatusId(client.getStatusId());
			editClientStatus.setCustomerId(client.getCustomerId());
			editClientStatus.setDisplayName(client.getDisplayName());
			context.addAttribute(this.getResultObject(GroupConstants.CURRENT_STATUS,  customerUtilDAO.getStatusName(localeId,client.getStatusId(),CustomerConstants.CLIENT_LEVEL_ID)));
			List statusList= customerUtilDAO.getStatusListForClient(localeId, client.getStatusId(), CustomerConstants.CLIENT_LEVEL_ID,context.getUserContext().getBranchId());
			context.addAttribute(getResultObject(CustomerConstants.STATUS_LIST, statusList));
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}
	
	
	
	/**
	 * This method gets the next posssible state id based on the current state.
	 * @param currentStateId
	 * @return
	 */
	private int getNextStateId(int currentStateId){
		return 0;
	}
	
	
	/**
	 * Called before the preview function so that a checklist of items to be done before for a status change can be 
	 * retrieved. This is placed in the context and retrieved on the jsp page.
	 * @param context The object containing the paramters to be passed from the action to business processor
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void previewInitial(Context context)throws SystemException,ApplicationException{
		
		try{
						
					setCheckList(context);
					EditClientStatus editClientStatusVO =(EditClientStatus)context.getValueObject();
					Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_STATUS_CHANGE);
					int customerId =client.getCustomerId();
					short statusId = editClientStatusVO.getStatusId();
					Short flagId =editClientStatusVO.getFlagId();
					short levelId = client.getCustomerLevel().getLevelId();
					//short localeId = Short.valueOf("1");
					short localeId = context.getUserContext().getLocaleId();
					context.addAttribute(new CustomerUtilDAO().getCheckList(statusId , levelId , CustomerConstants.CHECKLISTS )); 
					CustomerNote statusNotes = editClientStatusVO.getCustomerNote();
					statusNotes.setCommentDate(new java.sql.Date(new Date().getTime()));
					Personnel loggedUser = new Personnel();
					loggedUser.setPersonnelId(context.getUserContext().getId());
					loggedUser.setDisplayName(context.getUserContext().getName());
					statusNotes.setPersonnel(loggedUser);
					statusNotes.setPersonnelId(context.getUserContext().getId());
					statusNotes.setCustomerId(customerId) ;
					context.removeAttribute(CustomerConstants.NEW_STATUS);
					context.removeAttribute(CustomerConstants.NEW_FLAG);			
					context.addAttribute(this.getResultObject(CustomerConstants.NEW_STATUS, new CustomerUtilDAO().getStatusName(localeId, statusId, CustomerConstants.CLIENT_LEVEL_ID)));
					if(flagId!=null &&  flagId.shortValue()!=0 )
						context.addAttribute(this.getResultObject(CustomerConstants.NEW_FLAG, new CustomerUtilDAO().getFlagName(flagId,localeId)));

			}
			catch(SystemException se){
				throw se;
			}
			catch(ApplicationException ae){
				throw ae;
			}
			catch(Exception e){
				throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
			}
   }
				
	
	/**
	 * This method calls the dao and obtains a list of checklist for the customer
	 * with level as that of client.
	 * @param customerLevel
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private void setCheckList(Context context)throws SystemException,ApplicationException{
		EditClientStatus editClientStatusVO =(EditClientStatus)context.getValueObject();
		Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_STATUS_CHANGE);
		int customerId = editClientStatusVO.getCustomerId();
		short statusId = editClientStatusVO.getStatusId();
		short levelId = client.getCustomerLevel().getLevelId();
		
		//short localeId = Short.valueOf("1");
		short localeId = context.getUserContext().getLocaleId();
		context.addAttribute(new CustomerUtilDAO().getCheckList(statusId , levelId , CustomerConstants.CHECKLISTS )); 

	}
/** 
	 *  This method is called to update the group status with the new status
	 *  It validates for whether grop status can be changed. If yes, it updates group status by calling 
	 *  updateStatus on GroupDAO.
	 *  @param context an instance of Context 
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
	public void updateInitial(Context context)throws ApplicationException,SystemException {
		try{
			EditClientStatus editClientStatusVO =(EditClientStatus)context.getValueObject();
			Client client = (Client)context.getBusinessResults(ClientConstants.CLIENT_STATUS_CHANGE);
			
			
			Short oldStatus=client.getStatusId();
			
			//validate group status
			if(oldStatus.shortValue()!=editClientStatusVO.getStatusId().shortValue())
				validateStatus(client, editClientStatusVO,context.getUserContext());
			if(client.getPersonnelId()!=null)
			checkPermissionForStatusChange(editClientStatusVO.getStatusId(),context.getUserContext(),editClientStatusVO.getFlagId(),client.getOffice().getOfficeId(),client.getPersonnelId());
			else
				checkPermissionForStatusChange(editClientStatusVO.getStatusId(),context.getUserContext(),editClientStatusVO.getFlagId(),client.getOffice().getOfficeId(),context.getUserContext().getId());
			//update group status
			//TODO: change userId
			//groupDAO.updateStatus(groupVO, oldCustomer, context.getUserContext().getId());
			//groupDAO.updateStatus(groupVO, oldCustomer, Short.valueOf("1"));
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}

	/** 
	 *  This method is called before updating the group status. 
	 *  It validates whether this group be changed to new status
	 *  If no it throws customer state change exception with respective key
	 *  @param oldCustomer group with old status 
	 *  @param groupVO group with new staus id
	 *  @throws ApplicationException
	 *  @throws SystemException
	 */	
	private void validateStatus(Client client, EditClientStatus editedClient,UserContext userContext)throws ApplicationException,SystemException{
		short oldStatus=client.getStatusId().shortValue();
		short newStatus=editedClient.getStatusId().shortValue();
		short groupFlagValue = client.getGroupFlag().shortValue();
		
		
		
		boolean isNotValidStatusChange = false;
		//GroupDAO groupDAO=this.getGroupDAO();
		//if(newStatus==GroupConstants.CLOSED)
		//	 	checkIfGroupCanBeClosed(groupVO.getCustomerId());
		if( (Short.valueOf(newStatus).shortValue() == CustomerConstants.CLIENT_APPROVED 
				  ||Short.valueOf(newStatus).shortValue() == CustomerConstants.CLIENT_PENDING )
				  && groupFlagValue != 0 ) {
			short parentStatusId = client.getParentCustomer().getStatusId().shortValue();
			isNotValidStatusChange = checkGroupStatus(Short.valueOf(newStatus).shortValue() ,parentStatusId);
			logger.debug("In isNotValidStatusChange-------------------------------------------"+isNotValidStatusChange);
			if(isNotValidStatusChange){
				
				throw new CustomerStateChangeException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,userContext.getPereferedLocale()),labelConfig.getLabel(ConfigurationConstants.CLIENT,userContext.getPereferedLocale())});
			}
			
		}
		if(newStatus==CustomerConstants.CLIENT_APPROVED)
			checkIfClientCanBeActive(client ,newStatus,userContext);	
		//check  if client has any active loan account
		if(newStatus==CustomerConstants.CLIENT_CLOSED){
			if (new ViewClosedAccountsDAO().isCustomerWithActiveAccounts(client.getCustomerId()))
				throw new CustomerStateChangeException(CustomerConstants.CLIENT_HAS_ACTIVE_LOAN_EXCEPTION);
			/*if (new ViewClosedAccountsDAO().isCustomerHavingActiveSavingsAccount(client.getCustomerId()))
				throw new CustomerStateChangeException(CustomerConstants.CUSTOMER_HAS_ACTIVE_SAVINGS_EXCEPTION);*/
		}
		
	}
	
	private boolean checkGroupStatus(short status , short parentStatus) {
		boolean isNotValid = false ;
		if(status == 2){
			if(parentStatus == 7){
				isNotValid = true;
			}
		}
		else if(status == 3){
			if(parentStatus == 7 || parentStatus == 8){
				isNotValid = true;
			}
		}
		return isNotValid;
	}
	/** 
	 * This method is called before updating the group status to active. 
	 * It checks whether a group can be made active
	 * If not it throws customer state change exception with respective key
	 * @param group with new staus id
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public void checkIfClientCanBeActive(Customer client , short newStatus,UserContext userContext) throws SystemException , ApplicationException{
		// check if loan office is assigned to group
		Personnel LO = client.getPersonnel();
		boolean loanOfficerInactive = false;
		boolean branchInactive = false;
		short officeId = client.getOffice().getOfficeId();
		
		
		try {
			if(LO==null || LO.getPersonnelId()==null){
				throw new CustomerStateChangeException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
			}
			//check if meeting is assigned to the group
			if(client.getCustomerMeeting()==null||client.getCustomerMeeting().getMeeting()==null){
				throw new CustomerStateChangeException(GroupConstants.MEETING_NOT_ASSIGNED);
			}
			if( client.getPersonnel() !=null){
				Short loanOfficerId = client.getPersonnel().getPersonnelId();
				loanOfficerInactive =new CustomerUtilDAO().isLoanOfficerInactive(loanOfficerId , officeId);
			}
			 
			branchInactive = new CustomerUtilDAO().isBranchInactive(officeId);
			if(loanOfficerInactive ==true){
				throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,userContext.getPereferedLocale())});
								
			}
			if(branchInactive ==true){
				throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,userContext.getPereferedLocale())});
				
			}
			
			// TODO:check for meetings, if no meetings is assigned group cannot be made active
		}
		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}
	}
	
	/**
	 * It performs business logic checks trying to figure out if data integrity would be mentioned after the 
	 * specified state change.When updating and changing the status to active we need to update the customerActivationdate.
	 * It throes customer state change exception if the desired state change is not acceptable.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws CustomerStateChangeException
	 */
	public void customUpdate(Context context) throws SystemException,CustomerStateChangeException {
	}
	
	
	/**
	 * It performs the checks trying to see if the status change is acceptable.
	 * The various changes need to be done are:-
	 * Active - all associated entities are active like loan offier,branch etc.
	 * 			Loan officer is associated.
	 * Closed/Cancelled - all accounts are closed/cancelled.
	 * @param currentStateId
	 * @param nextStateId
	 * @return Return true if the state change is acceptable false otherwise.
	 */
	private boolean isStateChangeAcceptable(short currentStateId , short nextStateId){
		return false;
	}
	
	/** 
	 * This method creates a new SearchResults object with values as passed in parameters
	 * @param resultName the name with which framework will put resultvalue in request
	 * @param resultValue that need to be put in request
	 * @return SearchResults instance
	 */ 
	private SearchResults getResultObject(String resultName, Object resultValue){
		SearchResults result = new SearchResults();
		result.setResultName(resultName);
		result.setValue(resultValue);
		return result;
	}
	
	private void checkPermissionForStatusChange(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	private boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId){
		
		return ActivityMapper.getInstance().isStateChangePermittedForCustomer(newState.shortValue(),null!=flagSelected?flagSelected.shortValue():0,userContext,recordOfficeId,recordLoanOfficerId);
	}
}

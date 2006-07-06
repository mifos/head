/**

* CenterBusinessProcessor    version: 1.0



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

package org.mifos.application.customer.center.business.handlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.dao.ClosedAccSearchDAO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.center.dao.CenterDAO;
import org.mifos.application.customer.center.exception.DuplicateCustomerException;
import org.mifos.application.customer.center.exception.StateChangeException;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.PathConstants;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.center.util.valueobjects.CenterSearchResults;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.customer.dao.ViewClosedAccountsDAO;
import org.mifos.application.customer.exceptions.AssociatedObjectStaleException;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.exceptions.CustomerStateChangeException;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * @author sumeethaec
 *
 */
public class CenterBusinessProcessor extends MifosBusinessProcessor {
	/**An insatnce of the logger which is used to log statements */
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
	private ConfigurationIntf labelConfig=MifosConfiguration.getInstance();
	
	
	/**
	 * This method loads the page with the master objects required for center ui like loan officers , fees and list of custom fields etc
	 * @param context The object containing the paramters to be passed from the action to business processor
	 * @throws System and Application Exceptions
	 */
	public void loadInitial(Context context)throws SystemException,ApplicationException{

			try{
				CenterDAO centerDAO = (CenterDAO)getDAO(context.getPath());	
				//calls the load method in the center DAO which in turn calls the MasterDataRetriever to obtain the 
				//list of loan officers, fees, custom fields and collection sheets associated with that branch or level
				centerDAO.OnLoad(context);
			}
			catch(ResourceNotCreatedException rnce){
				logger.error("Center DAO cant be found",false,null,rnce);
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
	 * This method is called to create the center. Before a center is created a search Id and global customer number
	 * is assigned to the center. The center's status is made active and center level id is set as 3
	 * @param context The object containing the paramters to be passed from the action to business processor
	 * @throws System and Application Exceptions
	 */
	public void create(Context context)throws SystemException,ApplicationException{
		logger.info("Inside create method");
		int count =0;
		int customerCount =0;
		//the search string for the each customer starts with "1."
		String searchString = "1.";
		try{
			//obtaining instance of the center DAO
			CenterDAO centerDAO = (CenterDAO)getDAO(context.getPath());	
			//the center value object stored in teh context is retrieved
			Center center =((Center)context.getValueObject());
			//The number of centers currently present is retrieved
			count = (centerDAO.getCenterCount(center.getOffice().getOfficeId()))+1;
			customerCount =centerDAO.getCustomerCount(center.getOffice().getOfficeId()) + 1;
			
			
			//TODO remove this line once globalcust num of office is added to User context
			/*String officeGlobalNum = "BRANCH01";
			//String officeGlobalNum = context.getUserContext().getBranchGlobalNum();
			//level of the center 
			String centerLevelId = String.valueOf(CustomerConstants.CENTER_LEVEL_ID);
			String globalCustomerNumber = IdGenerator.generateId(officeGlobalNum , centerLevelId , String.valueOf(customerCount));
			
			
			center.setGlobalCustNum(globalCustomerNumber);*/
			center.setSearchId(searchString + String.valueOf(count));
			
			// setting the customer level as it is not coming from jsp
			center.getCustomerLevel().setLevelId(CustomerConstants.CENTER_LEVEL_ID);
			
			// setting the center status explicitly because whenever center is created it is in active state.
			center.setStatusId(CustomerConstants.ACTIVE_STATE);
			
			// setting the created date of the center to current date.
			center.setCreatedDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
			//setting the created by id of the center to the logged in user
			center.setCreatedBy(context.getUserContext().getId());
			center.setCustomerHierarchy(null);
			//save meeting only if user has selected any
			if(center.getCustomerMeeting()!=null && center.getCustomerMeeting().getMeeting()!=null){
				MeetingType meetingType = new MeetingType();
			 	meetingType.setMeetingTypeId(CustomerConstants.CUSTOMER_MEETING_TYPE);
			 	center.getCustomerMeeting().getMeeting().setMeetingType(meetingType);
				
			}
			logger.debug("Center GCN: "+ center.getGlobalCustNum());
						
			super.create(context);
			logger.info("After calling super create");
		}
		catch(ResourceNotCreatedException rnce){
			rnce.printStackTrace();
			logger.error("Center DAO cant be found",false,null,rnce);
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
	 * This method is called before the actual create. Here a check is made as to whether the center name that has 
	 * been entered already exists. If it does a exception is thorwn and the error message is shown to the user
	 * param context Parameters passed fromt eh action to the business processor
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context)throws SystemException,ApplicationException{
		CenterDAO centerDAO = null;
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO(); 
		boolean centerNameExists = false;
		boolean loanOfficerInactive = false;
		boolean branchInactive = false;
		boolean feeStatusInactive = false;
		List<Short> feeIds = new ArrayList();
		Center center=(Center)context.getValueObject();
		center.setOffice(new CustomerUtilDAO().getOffice(center.getOffice().getOfficeId()));
		try{
				centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
				//making a check to see if the center name exists. If it exists an exception is thrown
				String name = ((Center)context.getValueObject()).getDisplayName();
				//System.out.println("*****************Name of center"+name);
				Short loanOfficerId = ((Center)context.getValueObject()).getPersonnel().getPersonnelId();
				Short officeId = ((Center)context.getValueObject()).getOffice().getOfficeId();
				//short personnelOfficeId = ((Center)context.getValueObject()).getPersonnel().getOffice().getOfficeId().shortValue();
				Set accountFeesSet = ((Center)context.getValueObject()).getCustomerAccount().getAccountFeesSet();
				if(accountFeesSet !=null){
					if(accountFeesSet.size() != 0){
						Iterator <AccountFees>it = accountFeesSet.iterator();
						while(it.hasNext()){
							feeIds.add((it.next()).getFees().getFeeId());
						}
						feeStatusInactive = customerUtilDAO.isFeesStatusInactive(feeIds);
					}
				}
				centerNameExists = centerDAO.ifCenterNameExists(name);
				//System.out.println("*****************centerNameExists"+centerNameExists);
				loanOfficerInactive =customerUtilDAO.isLoanOfficerInactive(loanOfficerId , officeId);
				branchInactive = customerUtilDAO.isBranchInactive(officeId);
				
				if(centerNameExists ==true){
					Object[] values = new Object[1];
					values[0] = name;
					throw new DuplicateCustomerException(CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION,values);
					
				}
				if(loanOfficerInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
									
				}
				/*if(officeId.shortValue() != personnelOfficeId){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
				}*/
				if(branchInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
					
				}
				if(feeStatusInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_FEE_INACTIVE_EXCEPTION);
					
				}
				
		}
		catch(ResourceNotCreatedException rnce){
			logger.error("Center DAO cant be found",false,null,rnce);
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
	 *  This method is called to obtain all the center details. It retrieves the center information based on the global customer number
	 *  @param context This includes all the parameters passed from the action to the business processor layer 
	 */
	
	public void get(Context context)throws SystemException,ApplicationException{
		String centerId =CenterConstants.BLANK;
		String searchString =CenterConstants.BLANK;
		short officeId=0;
		List<CustomerMaster> groups =null;
		List<CustomerMaster> clients =null;
		CenterDAO centerDAO = null;
		Center center=null;
		Center centerVO=null;
		//obtaining instance of the center DAO
		try{
				centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
		}
		catch(ResourceNotCreatedException rnce){
			logger.error("Center DAO cant be found",false,null,rnce);
		}										
		
		
		try{
			center =(Center)context.getValueObject();
			centerId = center.getGlobalCustNum();
			
			logger.debug("Center Id in BP---"+centerId);
			//obtaining the center details
			centerVO =centerDAO.getCenterDetails(centerId);
			context.setValueObject(centerVO);
			//retrieving search string
			searchString = ((Center)context.getValueObject()).getSearchId();
			logger.debug("searchString in BP---"+searchString);
			officeId = ((Center)context.getValueObject()).getOffice().getOfficeId();
			/* TODO THIS CODE WILL BE REFACTORED TO OBTAIN THE LIST OF GROUPS AND CLIENTS SEPERATELY BY PASSING THE LEVEL ID
			 * TO THE FUNCTION getCenterChildDetails(searchString , officeId) IN THE CENTER DAO. CURRENTLY THE FUNCTION RETIREVES
			 * ALL THE CHILDREN IRRESPECTIVE OF THE STATUS. THE LIST OF CLIENTS IS BEING RETRIEVED AS IT IS BEING USED ON THE EDIT DETAILS
			 * PAGE AND A COUNT OF CLIENTS IS NEEDED AS PART OF PERFORMANCE HISTORY. 
			 * PERFORMANCE HISTORY DETAILS WILL LATER BE OBTAINED FROM A SUMMARISED TABLE. 
			 * THE FUNCTION WILL ALSO BE MODIFIED TO RETRIEVE ONLY THOSE CLIENTS THAT ARE NOT CLOSED OR CANCELLED, 
			 * AND WILL BE DONE ON LOAD OF THE EDIT PAGE
			 */
			//obtaining the child details
			List<Customer> centerChildren = centerDAO.getCenterChildDetails(searchString , officeId);
			//obtaining the list of groups
			groups = getChildList(centerChildren , CustomerConstants.GROUP_LEVEL_ID );
			logger.debug("-----Number of Groups: "+groups.size());
			//obtaining the list of clients under the center
			clients = getChildList(centerChildren , CenterConstants.CLIENT_LEVEL_ID );
			logger.debug("-----Number of Clients: "+clients.size());
			//setting the groups and clients in the context, so it can be accessed as request attributes on the jsp page 
			setSearchResults(context,CenterConstants.GROUP_LIST,groups);
			setSearchResults(context,CenterConstants.CLIENT_LIST,clients);
			
			for(int i=0;i<clients.size();i++){
				logger.debug("Client Name: " + clients.get(i).getDisplayName());
			}
			//obtaining the custom field names
			centerDAO.onGetLoad(context);
			Iterator<CustomerPosition> i = centerVO.getCustomerPositions().iterator();
			while(i.hasNext()){
				logger.debug("Position name and customer: "+i.next().getCustomerName());
			}
			
			CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO();
			CustomerHelper customerHelper = new CustomerHelper();
			short localeId=context.getUserContext().getLocaleId();
			context.addAttribute(customerHelper.getResultObject(CustomerConstants.CURRENT_CUSTOMER_STATUS,  customerHelper.getStatusName(localeId,centerVO.getStatusId(),CustomerConstants.CENTER_LEVEL_ID)));
			context.addAttribute(centerDAO.getPositions(localeId));
			context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_POSITIONS, customerHelper.loadCustomerPositions(centerVO,localeId,(List)(context.getSearchResultBasedOnName(CenterConstants.POSITIONS).getValue()))));
			context.addAttribute(this.getResultObject(GroupConstants.NOTES, customerHelper.getLatestNotes(GroupConstants.NOTES_COUNT,((Center)context.getValueObject()).getCustomerId())));
			context.addBusinessResults(CustomerConstants.LINK_VALUES,getLinkValues(centerVO));
			context.addBusinessResults(CustomerConstants.TOTAL_FEE_DUE,new ClosedAccSearchDAO().getTotalClientFeeChargesDue(centerVO.getCustomerAccount().getAccountId()));
			
			//TO display Loan nad Savings Accounts of a client
			List<SavingsBO> savingsAcounts = customerUtilDAO.getActiveSavingsAccountsForCustomer(centerVO.getCustomerId(),localeId);
			context.addAttribute(customerHelper.getResultObject(CustomerConstants.CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS,savingsAcounts));
			
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
	 * This method returns instance of LinkParameters
	 * @return group 
	 */ 
	LinkParameters getLinkValues(Center center){
		LinkParameters linkParams = new LinkParameters();
		linkParams.setCustomerId(center.getCustomerId());
		linkParams.setCustomerName(center.getDisplayName());
		linkParams.setGlobalCustNum(center.getGlobalCustNum());
		linkParams.setCustomerOfficeId(center.getOffice().getOfficeId());
		linkParams.setCustomerOfficeName(center.getOffice().getOfficeName());
		linkParams.setLevelId(CustomerConstants.CENTER_LEVEL_ID);
		Customer parent = center.getParentCustomer();
		if(parent!=null){
			linkParams.setCustomerParentGCNum(parent.getGlobalCustNum());
			linkParams.setCustomerParentName(parent.getDisplayName());
		}
		return linkParams;
	}
	
	/**
	 * This method is called when the center details have to be edited. In addition to the list of loan officers and
	 * custom fields that are loaded, the list of mfi titles and the list of clients under the center are also retrieved,
	 * so that they can be assigned to any one of the positions
	 * @param context This includes all the parameters passed from the action to the business processor layer 
	 * 
	 */
	public void manage(Context context)throws SystemException,ApplicationException{
		CenterDAO centerDAO =null;
		try{
				centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
		}
		catch(ResourceNotCreatedException rnce){
			logger.error("Center DAO cant be found",false,null,rnce);
		}
		//calls the load method in the center DAO which in turn calls the MasterDataRetriever to obtain the 
		//list of loan officers, fees, custom fields and collection sheets associated with that branch or level
		try{
			logger.debug("The value of center id in business processor before dao is " +((Center) context.getValueObject()).getCustomerId());
			centerDAO.OnLoad(context);
			//TODO remove this line once localeId is added to User context
			//short localeId = Short.valueOf("1");
			short localeId = context.getUserContext().getLocaleId();
			context.addAttribute(centerDAO.getPositions(localeId));
			context.addBusinessResults(CenterConstants.OLD_PERSONNEL ,((Center)context.getValueObject()).getPersonnel());
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
	/***
	 * This method obtains either the list of groups or list of clients for the center. The children are retrieved 
	 * based on the childLevelId
	 * @param centerChildren The list of either the groups or clients
	 * @param childLevelId The level denoting the child. level id 2 denotes the groups and level id 1 denotes clients
	 * @return The list of groups or clients under the center
	 */
	public List<CustomerMaster> getChildList(List<Customer> centerChildren , short childLevelId){
		logger.debug("childLevelId: ----------"+childLevelId);
		List<CustomerMaster> children = new ArrayList<CustomerMaster>();
		for(int i=0;i<centerChildren.size();i++){
			Customer customer = centerChildren.get(i);
			logger.debug("childCustomerLevelId: ----------"+customer.getCustomerLevel().getLevelId());
			if( customer.getCustomerLevel().getLevelId().shortValue() == childLevelId )
			{
				//logger.debug("inside if: ----------" + customer.getCustomerLevel().getLevelId());
				CustomerMaster child = new CustomerMaster();
				child.setCustomerId(customer.getCustomerId());
				child.setDisplayName(customer.getDisplayName());
				//logger.debug("child name"+child.getDisplayName());
				child.setGlobalCustNum(customer.getGlobalCustNum());
				child.setStatusId(customer.getStatusId());
				children.add(child);
			}
		}
		return children;
	}
	/**
	 * This method searches for the list of centers under a particualr office or under the child offices of the parent office
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * A search can also be done on the center name
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getSearchCenter(Context context)throws SystemException,ApplicationException {
		CenterSearchResults centerSearchResults = new CenterSearchResults();
		CenterDAO centerDAO =null;
		try{
				centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
		}
		catch(ResourceNotCreatedException rnce){
			logger.error("Center DAO cant be found",false,null,rnce);
		}
		//The search string which is entered byt he user 
		String searchString = context.getSearchObject().getFromSearchNodeMap("searchString");
		CenterSearchInput centerSearchInput = (CenterSearchInput)context.getBusinessResults(GroupConstants.CENTER_SEARCH_INPUT);
		//office id under which the list of centers should be displayed
		short branchId = centerSearchInput.getOfficeId();
		//short branchId = Short.valueOf("13");
		logger.debug("--------------branch id obtained in getSearchCenter: "+ branchId);
		SearchDAO searchDAO=new SearchDAO();
		String searchType="centerSearch";
		String searchId = HierarchyManager.getInstance().getSearchId(branchId);
		try{
			//puts the results obtained after the search into the context
			context.setSearchResult(searchDAO.search(searchType, searchString ,context.getUserContext().getLevelId(),searchId,context.getUserContext().getId(),null ));
			logger.debug("Values put into context");
			
		}
		catch(SystemException se){
			logger.error("centerDAO search threw exception ",false, null,se);
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}
		
	}
	/**
	 * This method sets an object as the value of the search results object with a particuar name
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @param resultName The name with which the object will be associated with
	 * @param results The object to be put as the value of the search results
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	
	public void setSearchResults(Context context , String resultName , List results){
		SearchResults masterInfo = new SearchResults();
		masterInfo.setResultName(resultName);
		masterInfo.setValue(results);
		context.addAttribute(masterInfo);
	}
	
	/**
	 * This method is called just before the call to the update function. It makes a check on the status of the loan
	 * officer and branch. If either of them are inactive an exception is thrown
	 * 
	 * before it is updated
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context)throws SystemException,ApplicationException{
		CenterDAO centerDAO = null;
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO();
		boolean loanOfficerInactive = false;
		boolean branchInactive = false;
		Short loanOfficerId = 0;
		Center center =((Center)context.getValueObject());
		try{
				centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
				
				Short officeId = center.getOffice().getOfficeId();
				if( center.getPersonnel() !=null){
					loanOfficerId = center.getPersonnel().getPersonnelId();
					loanOfficerInactive =customerUtilDAO.isLoanOfficerInactive(loanOfficerId , officeId);
				}
				//obtaining the  branch id
				branchInactive = customerUtilDAO.isBranchInactive(officeId);
				//A check is made as to whether the loan officer
				//or the branch to which the center belongs is inactive. If so, an exception is thrown
				if(loanOfficerInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
									
				}
				
				if(branchInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
					
				}
				
		}
		catch(ResourceNotCreatedException rnce){
			logger.error("Center DAO cant be found",false,null,rnce);
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
	 * This method is called to update the center details. It sets the necessary information for the center
	 * before it is updated
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context)throws SystemException,ApplicationException{
		try{
			//setting the updated_date and updated_by fields to the current date and cutrrent logged in user
			Center center = (Center)context.getValueObject();
			center.setUpdatedDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
			center.setUpdatedBy(context.getUserContext().getId());
			center.setCustomerHierarchy(null);
			
			super.update(context);
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
	 * Called before the preview function so that checklist is only dispalyed if the preview is for a status change
	 * @param context The object containing the paramters to be passed from the action to business processor
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void previewInitial(Context context)throws SystemException,ApplicationException{
		String fromPage= (String)context.getBusinessResults("fromPage");
		CenterDAO centerDAO =null;
		try{
				if(CenterConstants.INPUT_STATUS.equals(fromPage)){
					centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
					int customerId =((Center)context.getValueObject()).getCustomerId();
					short statusId = ((Center)context.getValueObject()).getStatusId();
					short levelId = ((Center)context.getValueObject()).getCustomerLevel().getLevelId();
					short localeId = context.getUserContext().getLocaleId();
					context.addAttribute(centerDAO.getChecklists(statusId , levelId)); 
					CustomerNote statusNotes = ((Center)context.getValueObject()).getCustomerNote();
					statusNotes.setCommentDate(new java.sql.Date(new Date().getTime()));
					Personnel loggedUser = new Personnel();
					
					//TODO remove this line once personnel is added to User context
					//loggedUser.setPersonnelId(Short.valueOf("2"));
					//loggedUser.setDisplayName("Mark");
					loggedUser.setPersonnelId(context.getUserContext().getId());
					loggedUser.setDisplayName(context.getUserContext().getName());
					statusNotes.setPersonnel(loggedUser);
					statusNotes.setPersonnelId(context.getUserContext().getId());
					statusNotes.setCustomerId(customerId) ;
					context.removeAttribute(CustomerConstants.NEW_STATUS);
					context.addAttribute(this.getResultObject(CustomerConstants.NEW_STATUS, new CustomerUtilDAO().getStatusName(localeId, statusId, CustomerConstants.CENTER_LEVEL_ID)));

				}
				else if(fromPage.equals(CenterConstants.INPUT_MANAGE)){
					short localeId=context.getUserContext().getLocaleId();
					Center centerVO = (Center)context.getValueObject();
					context.addAttribute(this.getResultObject(GroupConstants.CUSTOMER_POSITIONS, new CustomerHelper().loadCustomerPositions(centerVO,localeId,(List)(context.getSearchResultBasedOnName(CenterConstants.POSITIONS).getValue()))));
				}
			}
			catch(ResourceNotCreatedException rnce){
				logger.error("Center DAO cant be found",false,null,rnce);
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
	 * This will check if there are any active clients for a center by calling appropriate method on the DAO. 
	 * This is called just before a status is updated. In the case of status change from inactive to active,
	 * a check on the status of the laon officer and branch is done
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @throws ResourceNotCreatedException
	 * @throws StateChangeException - This indicates that state change for a center is not possible as some of the mandatory criteria required has not been fulfilled.
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	/*
	 * TODO DURING UPDATING OF STATUS FROM ACTIVE TO INACTIVE, THE SAVINGS ACCOUNT STATUS UNDER THE CENTER SHOULD
	 * ALSO BE CHECKED FOR CLOSED STATUS. THIS WILLBE INTEGRATED ONCE THE SAVINGS MODULE IS DONE 
	 */
	public void updateStatusInitial(Context context)throws SystemException,ApplicationException{
		
		Center center = (Center)context.getValueObject();
		CenterDAO centerDAO = null;
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO();
		boolean centerNameExists = false;
		boolean loanOfficerInactive = false;
		boolean branchInactive = false;
		boolean feeStatusInactive = false;
		List<Short> feeIds = new ArrayList();
		int noOfActiveChildren = 0;
		//System.out.println("************88CEnter: " +center);
		//System.out.println("************88CEnter Office: " +center.getOffice());
		Short officeId = center.getOffice().getOfficeId();
		try {
			centerDAO = (CenterDAO)getDAO(PathConstants.CENTER);	
			Personnel LO = ((Center)context.getValueObject()).getPersonnel();
			//If the status is being changed from active to inactive, a check is made as to whether the groups under the center are closed 
			//If any of them have a status other  than closed or cancelled, an exception is thrown.
			if(center.getStatusId() == CustomerConstants.INACTIVE_STATE){
				logger.debug("Before calling method on the DAO to get the number of active clients ." );
				noOfActiveChildren = centerDAO.countOfActiveChildren(center.getSearchId(),officeId);
				if(noOfActiveChildren > 0){
					logger.debug("Throwing state change exception because noOfActiveChildren is  ."+noOfActiveChildren );
					throw new StateChangeException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.GROUP,context.getUserContext().getPereferedLocale())});
				}
				if (new ViewClosedAccountsDAO().isCustomerWithActiveAccounts(center.getCustomerId())){
					throw new CustomerStateChangeException(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
				}
			}
			//If the status is being changed from inactive to active, a check is made as to whether the loan officer
			//or the branch to which the center belongs is inactive. If so, an exception is thrown
			else if(center.getStatusId() == CustomerConstants.ACTIVE_STATE){
				if(LO==null || LO.getPersonnelId()==null){
					throw new CustomerStateChangeException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
				}
				if( ((Center)context.getValueObject()).getPersonnel() !=null){
					Short loanOfficerId = ((Center)context.getValueObject()).getPersonnel().getPersonnelId();
					loanOfficerInactive =customerUtilDAO.isLoanOfficerInactive(loanOfficerId , officeId);
				}
				 
				branchInactive = customerUtilDAO.isBranchInactive(officeId);
				if(loanOfficerInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
									
				}
				if(branchInactive ==true){
					throw new AssociatedObjectStaleException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,new Object[]{labelConfig.getLabel(ConfigurationConstants.BRANCHOFFICE,context.getUserContext().getPereferedLocale())});
					
				}
				
			}
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
	 * This updates the status of the customer.
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadStatus(Context context)throws SystemException,ApplicationException{
		logger.debug("loadStatus");
		
		
		try{
			short localeId =Short.valueOf("1");
			short statusId = ((Center)context.getValueObject()).getStatusId();
			short levelId = ((Center)context.getValueObject()).getCustomerLevel().getLevelId();
			List statusList= new CustomerUtilDAO().getStatusListForCenter(localeId, statusId, CustomerConstants.CENTER_LEVEL_ID);
			context.addAttribute(getResultObject(CustomerConstants.STATUS_LIST, statusList));
		}
		catch(ResourceNotCreatedException rnce){
			
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
	 * This updates the status of the customer.
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateStatus(Context context)throws SystemException,ApplicationException{
		logger.debug("updateStatus");
		Center center=(Center)context.getValueObject();
		CustomerNote statusNotes = center.getCustomerNote();
		String centerId = center.getGlobalCustNum();
		
		try{
			CenterDAO centerDAO = (CenterDAO)getDAO(context.getPath());	
			//updating the status
			centerDAO.updateCenterStatus(center , centerDAO.getCenterDetails(centerId),context);
		}
		catch(ResourceNotCreatedException rnce){
			
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
	 * Sets an object in search results with a particualr name
	 * @param resultName The name with which the search results will be asociated
	 * @param resultValue The object to be set as the value forthe search results
	 * @return
	 */
	/**
	 * @param resultName
	 * @param resultValue
	 * @return
	 */
	private SearchResults getResultObject(String resultName, Object resultValue){
		  SearchResults result = new SearchResults();
		  result.setResultName(resultName);
		  result.setValue(resultValue);
		  return result;
	  }
}




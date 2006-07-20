
/**

* CenterAction    version: 1.0



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

package org.mifos.application.customer.center.struts.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.CustomerAccount;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.center.struts.actionforms.CenterActionForm;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.PathConstants;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.customer.util.valueobjects.CustomerAddressDetail;
import org.mifos.application.customer.util.valueobjects.CustomerCustomField;
import org.mifos.application.customer.util.valueobjects.CustomerMeeting;
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.application.customer.util.valueobjects.CustomerPosition;
import org.mifos.application.fees.util.valueobjects.FeeMaster;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.application.personnel.util.valueobjects.PersonnelMaster;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.MifosSearchAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This denotes the action class for the center module. It uses the base class functions of create, update. In addition
 * it includes methods to update status, to load center search pages, and implementations for preview, cancel and validate
 * @author sumeethaec
 *
 */
public class CenterAction extends MifosSearchAction{

	/**An instance of the logger which is used to log statements */
	private MifosLogger logger =MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
    /**
     * Returns the path which uniquely identifies the element in the dependency.xml
     * @return The path
     * */
	public String getPath(){
		return PathConstants.CENTER;
	}
	/**
	 * This method is called before th laod page for center is called
	 * It sets this information in session and context.This should be removed after center was successfully created.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward customLoad(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		this.clearActionForm(form);
		
		return null;
	}
	/**
	 * This method is called to retrieve center details.
	 * It calls get in business processor to get all center details
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken =true)
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
	  	ActionForward forward =super.load(mapping,form,request,response);
	  	
	  	Context context=(Context)request.getAttribute(Constants.CONTEXT);
	  	CenterActionForm centerForm = (CenterActionForm)form;
	    List<CustomFieldDefinition> customFields = (List)context.getSearchResultBasedOnName(CustomerConstants.CUSTOM_FIELDS_LIST).getValue();
		for(int i=0;i<customFields.size();i++){
			String defaultValue = customFields.get(i).getDefaultValue();
			if(defaultValue == null || defaultValue.equals(CustomerConstants.BLANK)){
				centerForm.getCustomField(i).setFieldValue("");	
			}
			else{
				if(customFields.get(i).getFieldType() == CustomerConstants.DATE_FIELD_TYPE 
						&&! ValidateMethods.isNullOrBlank(defaultValue)){
						centerForm.getCustomField(i).setFieldValue(DateHelper.getUserLocaleDate(
								context.getUserContext().getMfiLocale(), defaultValue));
						
				}
				else
					centerForm.getCustomField(i).setFieldValue(customFields.get(i).getDefaultValue());
			}
		}
		centerForm.setMfiJoiningDate(DateHelper.getCurrentDate(getUserLocale(request)));
	    return forward;
	}
	/**
	 * This is called when one clicks to edit users link in the ui to navigate to a search page.
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)throws Exception{
		// System.out.println("------------------- in choose office");
		return mapping.findForward(CenterConstants.CHOOSE_OFFICE_SUCCESS);
	}
	/**
	 * Method called to load the page used to change the status of the center
	 * The center status can be changed to either active or inactive. The page displays only
	 * those states to which the center can be changed to. Eg: If the center status is active,
	 * then the page displays only the inactive state.
	 * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 */
	public ActionForward loadStatus(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//forwards to the page where the center status can be changed
		CenterActionForm centerForm = (CenterActionForm)form;
		centerForm.setSelectedItems(null);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		request.getSession().setAttribute(CustomerConstants.OLD_STATUS,((Center)context.getValueObject()).getStatusId());
		//setting the method being called in the action to loadStatus . This ensures the appropriate method is called
		//in the CenterBusiness Processor
		context.setBusinessAction(CenterConstants.LOAD_STATUS);
		delegate(context,request);
		centerForm.setCustomerNote(new CustomerNote());
		return mapping.findForward(CenterConstants.CENTER_CHANGE_STATUS_PAGE);
	}
	/**
	 * This method is called to update the status of the center. On success the action forwards to the center
	 * details page where the change in the status will be reflected. Since an implementation for this method is
	 * not present in the base action class, an entry is made in method hash map of the MifosBase action
	 * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @return The mapping to the next page
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken =true)
	public ActionForward updateStatus(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {


		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		//setting the method being called in the action to updateStatus . This ensures the appropriate method is called
		//in the CenterBusiness Processor
		context.setBusinessAction(CenterConstants.UPDATE_STATUS);
		delegate(context,request);
		//forward to the center details page
		request.getSession().removeAttribute(CustomerConstants.OLD_STATUS) ;
		return mapping.findForward(CenterConstants.CENTER_STATUS_UPDATE_PAGE);
	}

	/**
	  *	Method which is called to load the page to preview the details entered by the user. Depending on the input
	  * page, this method forwards to the respective preview page
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */

	 public ActionForward customPreview(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		CenterActionForm centerForm = (CenterActionForm)form;
		String forward = null;
		//obtaining the input page, so that the repective forward can be decided
		String fromPage =centerForm.getInput();
		Context context =(Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(CenterConstants.FROM_PAGE,fromPage);
		logger.debug("Inside customPreview and input page is: "+fromPage);
		//personnel object and display address is set only if input page is create or manage
		if(  CenterConstants.INPUT_CREATE.equals(fromPage) || CenterConstants.INPUT_MANAGE.equals(fromPage )){

			Center center = (Center)context.getValueObject();
			logger.debug("-------------------loan officer id:-----"+centerForm.getLoanOfficerId());

			//get selected loan officer name and set in center value object
			if(!ValidateMethods.isNullOrBlank(centerForm.getLoanOfficerId()) ){
				Personnel loanOfficer = getLO(context,Short.parseShort(centerForm.getLoanOfficerId()));
				center.setPersonnel(loanOfficer);
			}
			else{
				center.setPersonnel(null);
			}

			//setting display address
			String displayAddress = new CustomerHelper().getDisplayAddress(centerForm.getCustomerAddressDetail()); 
			center.setDisplayAddress(displayAddress);
			//if the information coming from the create page then the list of selected fees is set
			if( CenterConstants.INPUT_CREATE.equals(fromPage )){
				List<FeeMaster> fees = centerForm.getSelectedFeeList();
				setFeeTypeList(context , fees);
			}
		}
		//deciding forward page based on the input page
		if(  CenterConstants.INPUT_CREATE.equals(fromPage))
			forward =CenterConstants.CENTER_CREATE_PREVIEW_PAGE;
		else if( CenterConstants.INPUT_STATUS.equals(fromPage))
			forward = CenterConstants.CENTER_STATUS_PREVIEW_PAGE;
		else if(CenterConstants.INPUT_MANAGE.equals(fromPage ))
			forward = CenterConstants.CENTER_MANAGE_PREVIEW_PAGE;
		logger.debug("forward: "+forward);
		return mapping.findForward(forward);
	}
	/**
	 * This method obtains the selected fees from the list of additional fees that can be applied to the cneter.
	 * @param context
	 * @param fees
	 */
	private void setFeeTypeList(Context context , List<FeeMaster> fees) {
		List<FeeMaster> additionalFees = new ArrayList<FeeMaster>();
		List<FeeMaster> AdditionalFeesMaster =(List)context.getSearchResultBasedOnName(CenterConstants.FEES_LIST).getValue();
		//The list of selected fee ids is obtained and those fee objects are added to an other array to display on
		//the preview page for the create page information
		for(int i=0;i<fees.size();i++){

			FeeMaster selectedFee = fees.get(i);
			for(int j=0;j<AdditionalFeesMaster.size();j++){

				FeeMaster additionalFee =AdditionalFeesMaster.get(j);
				logger.debug(" additional master fee ids: "+additionalFee.getFeeId());
				if(fees.get(i).getFeeId() !=null){
					if(fees.get(i).getFeeId().shortValue() == additionalFee.getFeeId().shortValue())
					{
						selectedFee.setFeeName(additionalFee.getFeeName());
						//System.out.println("**************************Fee Name: " + selectedFee.getFeeName());
						selectedFee.setFeeFrequencyTypeId(additionalFee.getFeeFrequencyTypeId());
						//System.out.println("**************************Fee Frequency: " + selectedFee.getFeeFrequencyTypeId());
						selectedFee.setFeeMeeting(additionalFee.getFeeMeeting());
						//System.out.println("**************************Fee Meeting Periodicty: " + selectedFee.getFeeMeeting());
						additionalFees.add(selectedFee);
						break;
					}
				}
			}
		}
		//The selected additional fees are added to the search results which is in turn added to the context
		SearchResults searchResults = new SearchResults();
 		searchResults.setResultName(CenterConstants.ADDITIONAL_FEES_LIST);
 		searchResults.setValue(additionalFees);
 		context.addAttribute(searchResults);

	}
/**
	 * This method is called on create method
	 * It chooses forwards for previous page based on input page
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	public ActionForward customCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {

		//set version number of fees
		CenterActionForm actionForm=(CenterActionForm)form;
		List<FeeMaster> adminFeeList =actionForm.getAdminFeeList();
		List<FeeMaster> selectedFeeList = actionForm.getSelectedFeeList();

		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		Center center=(Center)context.getValueObject();
		center.setSelectedFeeSet(formFeeSet(adminFeeList,selectedFeeList,context));


		return null;
	}
	
	public ActionForward customManage(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		CenterActionForm centerForm = (CenterActionForm)form;
		clearActionForm(centerForm);
		return null;
	}

	/**
	 * This method is called on create method
	 * It does the explicit cleanup required after gorup creation
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(validateAndResetToken =true)
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		ActionForward forward = super.create(mapping,form,request,response);
		Context context=(Context)request.getAttribute(Constants.CONTEXT);
		//set meeetings to null, after group is created
		context.addBusinessResults(MeetingConstants.MEETING,null);
		return forward;
	}
	/**
	 * Helper method used to form feeSet to be set in the valueObject
	 * @param selectedFeeIdList
	 * @param selectedFeeAmntList
	 * @return
	 */
	private Set formFeeSet(List<FeeMaster> adminFeeList,List<FeeMaster> selectedFeeList,Context context){
		Set<AccountFees> accountFeeSet = new HashSet<AccountFees>();
		List<FeeMaster> adminFeeMaster=((List)context.getSearchResultBasedOnName(CenterConstants.ADMIN_FEES_LIST).getValue());
		List<FeeMaster> selectedFeeMaster=((List)context.getSearchResultBasedOnName(CenterConstants.FEES_LIST).getValue());

		//put administrative fees in set if any
		for(int index=0;index<adminFeeList.size();index++ ){
			if(adminFeeList.get(index).getCheckedFee()!=null){
				if(adminFeeList.get(index).getCheckedFee().shortValue()!=1 ){
					for(int i=0 ;i<adminFeeMaster.size();i++){
						if(adminFeeMaster.get(i).getFeeId().shortValue()== adminFeeList.get(index).getFeeId()){
							AccountFees accountFees = new AccountFees();
							Fees fee = new Fees();
							fee.setFeeId(adminFeeList.get(index).getFeeId());
							fee.setVersionNo(adminFeeMaster.get(i).getVersionNo());
							//fee.setRateOrAmount(adminFeeList.get(index).getRateOrAmount());
							accountFees.setFees(fee);
							accountFees.setAccountFeeAmount(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),adminFeeList.get(index).getRateOrAmount()));
							accountFees.setFeeAmount(adminFeeList.get(index).getRateOrAmount());
							accountFeeSet.add(accountFees);
							break;
						}
					}
				}
			}
		}
		//put additional fees in set if any
		for(int index=0;index<selectedFeeList.size();index++ ){
			if(selectedFeeList.get(index).getFeeId()!=null){
				if(selectedFeeList.get(index).getFeeId().shortValue()!=0 ){
					for(int i=0 ;i<selectedFeeMaster.size();i++){
						if(selectedFeeMaster.get(i).getFeeId().shortValue()== selectedFeeList.get(index).getFeeId()){
							AccountFees accountFees = new AccountFees();
							Fees fee = new Fees();
							fee.setFeeId(selectedFeeList.get(index).getFeeId());
							fee.setVersionNo(selectedFeeMaster.get(i).getVersionNo());
							//fee.setRateOrAmount(selectedFeeList.get(index).getRateOrAmount());
							accountFees.setFees(fee);
							accountFees.setAccountFeeAmount(new Money(Configuration.getInstance().getSystemConfig().getCurrency(),selectedFeeList.get(index).getRateOrAmount()));
							accountFees.setFeeAmount(selectedFeeList.get(index).getRateOrAmount());
							accountFeeSet.add(accountFees);
							break;
						}
					}
				}
			}
		}

		return accountFeeSet;
	}
	/**
	  *	Method which is called to load the page to edit the details that were previewed. Depending from where the
	  * preview was initially called, this method forwards to the respective input page
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */

	 public ActionForward customPrevious(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws SystemException{
		 String forward = null;
		 CenterActionForm centerForm = (CenterActionForm)form;
		 Context context=(Context)request.getAttribute(Constants.CONTEXT);
		 String fromPage =centerForm.getInput();
		 //deciding forward page
		if(  CenterConstants.INPUT_CREATE.equals(fromPage))
			forward =CenterConstants.CENTER_CREATE_EDIT_PAGE;
		else if( CenterConstants.INPUT_STATUS.equals(fromPage)){
			centerForm.setSelectedItems(null);
			forward = CenterConstants.CENTER_STATUS_EDIT_PAGE;
		}
		else if(CenterConstants.INPUT_MANAGE.equals(fromPage )){
			((Center)context.getValueObject()).convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
			forward = CenterConstants.CENTER_MANAGE_EDIT_PAGE;
		}
		else if(CenterConstants.INPUT_MEETING.equals(fromPage)){
			Meeting meeting =(Meeting) context.getBusinessResults(MeetingConstants.MEETING);
			centerForm.getCustomerMeeting().setMeeting(meeting);
			forward=CenterConstants.CENTER_CREATE_EDIT_PAGE;
		}
		logger.debug("forward: "+forward);
		//forward to the page as decided by the input page
		return mapping.findForward(forward);
	}


	/**
	  *	Method which is called to load the  search page which searchs for all the centers under a branch
	  *
	  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward loadSearch(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
		this.clearActionForm(form);
		//CenterActionForm centerForm = (CenterActionForm)form;
		//centerForm.setSearchNode("searchString",null);
		String forward = CenterConstants.CENTER_SEARCH_PAGE;
		//obtains a search input object which contains parameters like the office id under which the center should be
		//searched. Also contains parameters which helps to decide if a center is being searched to create a new
		//group or to edit the center membership of an already existing group
		CenterSearchInput centerSearchInput = (CenterSearchInput)request.getSession().getAttribute(GroupConstants.CENTER_SEARCH_INPUT);
		Context context = (Context)request.getAttribute(Constants.CONTEXT);
		context.addBusinessResults(GroupConstants.CENTER_SEARCH_INPUT , centerSearchInput);
		return mapping.findForward(forward);
	}
	/**
	 * This method is called to retrieve center details.
	 * It calls get in business processor to get all center details
	 * @param mapping indicates action mapping defined in struts-config.xml
	 * @param form The form bean associated with this action
	 * @param request Contains the request parameters
	 * @param response
	 * @throws Exception
	 */
	@TransactionDemarcate(saveToken =true)
	public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
	  	ActionForward forward =super.get(mapping,form,request,response);
	    Context context=(Context)request.getAttribute(Constants.CONTEXT);
	    String centerId = ((Center)context.getValueObject()).getGlobalCustNum();
	    String searchString = ((Center)context.getValueObject()).getSearchId();
	    Short officeId = ((Center)context.getValueObject()).getOffice().getOfficeId();
	    CenterPerformanceHistory centerPerformanceHistory = new CustomerBusinessService().getCenterPerformanceHistory(searchString,officeId);
	    HttpSession session = request.getSession();
	    session.setAttribute(CustomerConstants.LINK_VALUES,(LinkParameters)context.getBusinessResults(CustomerConstants.LINK_VALUES));
	    SessionUtils.setAttribute(CenterConstants.PERFORMANCE_HISTORY,centerPerformanceHistory,session);
	    return forward;
	}

/**
	 * This method is used to retrieve the name of the loan officer for the selcted loan officer id
	 * @param context The object containing the request paramters
	 * @param personnelId
	 * @return
	 */
	 private Personnel getLO(Context context, short personnelId){
			Personnel loanOfficer = new Personnel();
			Iterator iteratorLO=((List)context.getSearchResultBasedOnName(CenterConstants.LOAN_OFFICER_LIST).getValue()).iterator();
			//Obtaining the name of the selected loan officer from the master list of loan officers
			while (iteratorLO.hasNext()){
				PersonnelMaster lo=(PersonnelMaster)iteratorLO.next();
				if(lo.getPersonnelId().shortValue()==personnelId){
					loanOfficer.setPersonnelId(lo.getPersonnelId());
					loanOfficer.setDisplayName(lo.getDisplayName());
					loanOfficer.setVersionNo(lo.getVersionNo());

				}
			}
			return loanOfficer;
		}

		/**
		 * This method adds the name of the new methods to the method Hash map
		 */
		public Map<String,String> appendToMap()
		{

			logger.debug("-----in append to map method");
			Map<String,String> methodHashMap = new HashMap<String,String>();

			//TODO change this code------------------------
			methodHashMap.putAll(super.appendToMap());
			//-----------------------------
			methodHashMap.put(CenterConstants.LOAD_SEARCH,CenterConstants.LOAD_SEARCH);
			methodHashMap.put(CenterConstants.LOAD_STATUS,CenterConstants.LOAD_STATUS);
			methodHashMap.put(CenterConstants.UPDATE_STATUS,CenterConstants.UPDATE_STATUS);
			methodHashMap.put(ClientConstants.METHOD_LOAD_MEETING,ClientConstants.METHOD_LOAD_MEETING);
			methodHashMap.put(ClientConstants.METHOD_UPDATE_MEETING,ClientConstants.METHOD_UPDATE_MEETING);
			methodHashMap.put(ClientConstants.METHOD_CHOOSE_OFFICE,ClientConstants.METHOD_CHOOSE_OFFICE);
			methodHashMap.put(CustomerConstants.METHOD_GET_DETAILS,CustomerConstants.METHOD_GET_DETAILS);

			return methodHashMap;

		}
		/**
		  *	Method which is called to load the page on clicking a cancel. Depending from where the cancel was initially called,
		  * this method forwards to the respective page
		  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
		  * @param form The form bean associated with this action
		  * @param request Contains the request parameters
		  * @param response
		  * @return The mapping to the next page
		  */

		 public ActionForward customCancel(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
			 String forward = null;
			 CenterActionForm centerForm = (CenterActionForm)form;
			 String fromPage =centerForm.getInput();
			 //deciding forward page based on the from page
			if(  CenterConstants.INPUT_CREATE.equals(fromPage))
				forward =CenterConstants.CENTER_CREATE_CANCEL_PAGE;
			else if(  "chooseOffice".equals(fromPage))
				forward =CenterConstants.CENTER_CREATE_CANCEL_PAGE;
			else if( CenterConstants.INPUT_STATUS.equals(fromPage))
				forward = CenterConstants.CENTER_STATUS_CANCEL_PAGE;
			else if(CenterConstants.INPUT_MANAGE.equals(fromPage ))
				forward = CenterConstants.CENTER_MANAGE_CANCEL_PAGE;
			else if(CenterConstants.INPUT_SEARCH_CREATEGROUP.equals(fromPage ))
				forward = CenterConstants.CENTER_SEARCH_CANCEL_PAGE;
			else if (fromPage.equals(CenterConstants.INPUT_SEARCH_TRANSFERGROUP)){
				forward=CenterConstants.CENTER_SEARCH_TRANSFER_CANCEL_PAGE;
			}
			logger.debug("forward: "+forward);
			return mapping.findForward(forward);
		}

		 /**
		  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed
		  * this method forwards to the respective input page
		  * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
		  * @param form The form bean associated with this action
		  * @param request Contains the request parameters
		  * @param response
		  * @return The mapping to the next page
		  */
		public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws SystemException{
			Context context=(Context)request.getAttribute(Constants.CONTEXT);
			String forward = null;
			String methodCalled= (String)request.getAttribute("methodCalled");
			logger.debug("methodCalled: "+methodCalled);
			CenterActionForm centerForm = (CenterActionForm)form;
			String fromPage =centerForm.getInput();
			//deciding forward page
			if(null !=methodCalled) {
				//depending on where the preview was called from, the action forwards to the page where the errors will be displayed
				if("preview".equals(methodCalled)){
					if(  CenterConstants.INPUT_CREATE.equals(fromPage))
						forward =CenterConstants.CENTER_CREATEPREVIEW_FAILURE_PAGE;
					else if( CenterConstants.INPUT_STATUS.equals(fromPage))
						forward = CenterConstants.CENTER_STATUSPREVIEW_FAILURE_PAGE;
					else if(CenterConstants.INPUT_MANAGE.equals(fromPage )){
						((Center)context.getValueObject()).convertCustomFieldDateToDbformat(context.getUserContext().getMfiLocale());
						forward = CenterConstants.CENTER_MANAGEPREVIEW_FAILURE_PAGE;
					}

				}
				else if(fromPage.equals(CenterConstants.INPUT_SEARCH_CREATEGROUP)){
					forward = CustomerConstants.SEARCH_FAILURE;
				}else if(fromPage.equals(CenterConstants.INPUT_SEARCH_TRANSFERGROUP)){
					forward = CustomerConstants.SEARCH_FAILURE_TRANSFER;
				}
				/*else if("search".equals(methodCalled) ){
					if(  CenterConstants.INPUT_SEARCH.equals(fromPage))
						forward =CenterConstants.CENTER_SEARCH_PAGE;


				}*/
				logger.debug("forward: "+forward);

			}
			return mapping.findForward(forward);

		}
		/**
		 * This method checks if the action form to value object conversion is needed. When the methods being
		 * called are either manage or load status this conversion is not done
		 * @return Returns whether the action form should be converted or not
		 */
		protected boolean isActionFormToValueObjectConversionReq(String methodName) {
			logger.info("Before converting action form to value object checking for method name " + methodName);
			if(null !=methodName && ( methodName.equals(CustomerConstants.METHOD_MANAGE) ||
					  methodName.equals(CustomerConstants.METHOD_LOAD_STATUS)||
					  methodName.equals(ClientConstants.METHOD_CHOOSE_OFFICE)||
					  methodName.equals(ClientConstants.METHOD_LOAD_MEETING)||
					  methodName.equals(CustomerConstants.METHOD_SEARCH)||
					  methodName.equals(CustomerConstants.METHOD_LOAD_SEARCH)||
					  methodName.equals(CustomerConstants.METHOD_GET_DETAILS) )){
				return false;
			}else{
				return true;
			}

		}

		/**
		 * Method called to load the page used to change the branch membership of the client
		 * This puts the current client details in the session and transfers control to the clientTransfer action
		 * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
		 * @param form The form bean associated with this action
		 * @param request Contains the request parameters
		 * @param response
		 * @return The mapping to the next page
		 */
		public ActionForward loadMeeting(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			// System.out.println("----------Inside load Meethin");
			String forward =null;
			Context context=(Context)request.getAttribute(Constants.CONTEXT);

			//putMasterDataInSession(request , context);
			CenterActionForm centerForm = (CenterActionForm)form;
			String fromPage =centerForm.getInput();
			// System.out.println("FRam page: "+fromPage);
			if(null!=fromPage && fromPage.equals("CenterDetails")){
				forward = CenterConstants.CENTER_EDIT_MEETING_PAGE;
			}
			else{
				// System.out.println("----------Inside else of load Meethin");
				forward=CenterConstants.CENTER_MEETING_PAGE;
			}
			// System.out.println("Forward page: "+fromPage);
			return mapping.findForward(forward);
		}
		/**
		 * Method called to update the meeting of a center
		 * @param mapping The page to which the control passes to. This is specified in the struts-config.xml
		 * @param form The form bean associated with this action
		 * @param request Contains the request parameters
		 * @param response
		 * @return The mapping to the next page
		 */
		public ActionForward updateMeeting(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			//forwards to the page where the center status can be changed
			Context context=(Context)request.getAttribute(Constants.CONTEXT);
			CenterActionForm centerForm = (CenterActionForm)form;
			Meeting meeting =(Meeting) context.getBusinessResults(MeetingConstants.MEETING);
			centerForm.getCustomerMeeting().setMeeting(meeting);
			context.setBusinessAction(ClientConstants.METHOD_UPDATE_MEETING);
			delegate(context,request);

			return mapping.findForward(CenterConstants.CENTER_MEETING_UPDATE_PAGE);
		}
		 /**
		  * This method is called to clear action form values, whenever a fresh request to create a group comes in
		  * This is necessary because action form is stored in session
		  * @param mapping indicates action mapping defined in struts-config.xml
		  * @param form The form bean associated with this action
		  */
		private void clearActionForm(ActionForm form){
			CenterActionForm centerForm = (CenterActionForm)form;
			centerForm.setCustomerAddressDetail(new CustomerAddressDetail());
			centerForm.setDisplayName("");
			centerForm.setMfiJoiningDate("");
			centerForm.setCustomerMeeting(new CustomerMeeting());
			centerForm.setGlobalCustNum("");
			centerForm.setExternalId("");
			centerForm.setLoanOfficerId("");
			centerForm.setAdminFeeList(new ArrayList<FeeMaster>());
			centerForm.setSelectedFeeList(new ArrayList<FeeMaster>());
			centerForm.setCustomFieldSet(new ArrayList<CustomerCustomField>());
			centerForm.setCustomerAccount(new CustomerAccount());
			centerForm.setCustomerPositions(new ArrayList<CustomerPosition>());
			centerForm.setCustomerNote(new CustomerNote());
			centerForm.setSearchNode("searchString",null);
			centerForm.getSearchNodeMap().clear();

		}
		/**
		 * This method is called to retrieve center details.
		 * It calls get in business processor to get all center details
		 * @param mapping indicates action mapping defined in struts-config.xml
		 * @param form The form bean associated with this action
		 * @param request Contains the request parameters
		 * @param response
		 * @throws Exception
		 */
		 @TransactionDemarcate(saveToken =true)
		 public ActionForward getDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
			String globalCustNum=request.getParameter(CustomerConstants.GLOBAL_CUST_NUM);
			Center center=(Center)getValueObject(getPath());
			center.setGlobalCustNum(globalCustNum);
			Context context=(Context)request.getAttribute(Constants.CONTEXT);
			context.setValueObject(center);
			return get(mapping,form,request,response);
		}
		 protected Locale getUserLocale(HttpServletRequest request) {
				Locale locale = null;
				HttpSession session = request.getSession();
				if (session != null) {
					UserContext userContext = (UserContext) session
							.getAttribute(LoginConstants.USERCONTEXT);
					if (null != userContext) {
						locale = userContext.getPereferedLocale();
						if (null == locale) {
							locale = userContext.getMfiLocale();
						}
					}
				}
				return locale;
			}

}

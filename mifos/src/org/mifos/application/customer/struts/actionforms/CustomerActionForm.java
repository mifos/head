/**

* CustomerActionForm    version: 1.0



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
package org.mifos.application.customer.struts.actionforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public abstract class CustomerActionForm extends BaseActionForm{

	private String customerId;
	
	private String globalCustNum;
	
	private String displayName;

	private Address address;

	private String officeId;

	private String officeName;

	private String loanOfficerId;

	private String externalId;

	private String status;

	private String mfiJoiningDate;
	
	protected String input;
	
	private String formedByPersonnel;
	
	private List<FeeView> defaultFees;

	private List<FeeView> additionalFees;

	private String selectedFeeAmntList;
	
	private List<CustomFieldView> customFields;
	
	public CustomerActionForm() {
		address = new Address();
		defaultFees = new ArrayList<FeeView>();
		additionalFees = new ArrayList<FeeView>();
		customFields = new ArrayList<CustomFieldView>();
	}

	public List<FeeView> getAdditionalFees() {
		return additionalFees;
	}

	public void setAdditionalFees(List<FeeView> additionalFees) {
		this.additionalFees = additionalFees;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getGlobalCustNum() {
		return globalCustNum;
	}

	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	public List<CustomFieldView> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<CustomFieldView> customFields) {
		this.customFields = customFields;
	}

	public List<FeeView> getDefaultFees() {
		return defaultFees;
	}

	public void setDefaultFees(List<FeeView> defaultFees) {
		this.defaultFees = defaultFees;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getStatus() {
		return status;
	}

	public String getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	public void setMfiJoiningDate(String mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getSelectedFeeAmntList() {
		return selectedFeeAmntList;
	}

	public void setSelectedFeeAmntList(String selectedFeeAmntList) {
		this.selectedFeeAmntList = selectedFeeAmntList;
	}

	public Short getOfficeIdValue() {
		return getShortValue(officeId);
	}
	
	public Short getLoanOfficerIdValue() {
		return getShortValue(loanOfficerId);
	}
	
	public CustomFieldView getCustomField(int i){
		while(i>=customFields.size()){
			customFields.add(new CustomFieldView());
		}
		return (CustomFieldView)(customFields.get(i));
	}
	
	public String getFormedByPersonnel() {
		return formedByPersonnel;
	}

	public void setFormedByPersonnel(String formedByPersonnel) {
		this.formedByPersonnel = formedByPersonnel;
	}

	public FeeView getDefaultFee(int i) {
		while(i>=defaultFees.size()){
			defaultFees.add(new FeeView());
		}
		return (FeeView)(defaultFees.get(i));
	}
	
	public List<FeeView> getFeesToApply(){
		List<FeeView> feesToApply = new ArrayList<FeeView>();
		for(FeeView fee: getAdditionalFees())
			if(fee.getFeeIdValue()!=null)
				feesToApply.add(fee);
		for(FeeView fee: getDefaultFees())
			if(!fee.isRemoved())
				feesToApply.add(fee);
		return feesToApply;
	}
	
	public FeeView getSelectedFee(int index){
		while(index>=additionalFees.size())
			additionalFees.add(new FeeView());
		return (FeeView)additionalFees.get(index);
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if (request.getParameter("displayName")!=null ){
			for(int i=0; i < defaultFees.size();i++){
				//if an already checked fee is unchecked then the value set to 0
				if(request.getParameter("defaultFee["+i+"].feeRemoved")==null){
					defaultFees.get(i).setFeeRemoved(YesNoFlag.NO.getValue());
				}
			}
		}
		super.reset(mapping, request);
	}
	
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		String method = request.getParameter("method");
		ActionErrors errors = null;
		errors = validateFields(request, method);
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		errors.add(super.validate(mapping,request));
		return errors;
	}
	
	protected abstract ActionErrors validateFields(HttpServletRequest request, String method);
	
	protected void validateName(ActionErrors errors){
		if (StringUtils.isNullOrEmpty(getDisplayName()))
			errors.add(CustomerConstants.NAME, new ActionMessage(CustomerConstants.ERRORS_SPECIFY_NAME));
	}
	
	protected void validateLO(ActionErrors errors){
		if (StringUtils.isNullOrEmpty(getLoanOfficerId()))
			errors.add(CustomerConstants.LOAN_OFFICER, new ActionMessage(CustomerConstants.ERRORS_SELECT_LOAN_OFFICER));
	}
	
	protected void validateFormedByPersonnel(ActionErrors errors){
		if (StringUtils.isNullOrEmpty(getFormedByPersonnel()))
			errors.add(CustomerConstants.FORMED_BY_LOANOFFICER, new ActionMessage(CustomerConstants.FORMEDBY_LOANOFFICER_BLANK_EXCEPTION));
	}
	
	protected void validateMeeting(HttpServletRequest request, ActionErrors errors){
		Object meeting = SessionUtils.getAttribute(CenterConstants.CENTER_MEETING, request.getSession());
		if(meeting == null || !(meeting instanceof MeetingBO))
			errors.add(CustomerConstants.MEETING, new ActionMessage(CustomerConstants.ERRORS_SPECIFY_MEETING));
	}
	
	protected void validateConfigurableMandatoryFields(HttpServletRequest request, ActionErrors errors, EntityType entityType){
		checkForMandatoryFields(entityType.getValue(), errors, request);
	}
	
	protected  void validateCustomFields(HttpServletRequest request, ActionErrors errors){
		List<CustomFieldDefinitionEntity> customFieldDefs =(List<CustomFieldDefinitionEntity>) SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request.getSession());
		for(CustomFieldView customField : customFields){
			for(CustomFieldDefinitionEntity customFieldDef : customFieldDefs){
				if(customField.getFieldId().equals(customFieldDef.getFieldId())&& customFieldDef.isMandatory()){
					if(StringUtils.isNullOrEmpty(customField.getFieldValue()))
						errors.add(CustomerConstants.CUSTOM_FIELD, new ActionMessage(CustomerConstants.ERRORS_SPECIFY_CUSTOM_FIELD_VALUE));
				}
			}
		}
	}
	
	protected  void validateFees(HttpServletRequest request, ActionErrors errors){
		validateForFeeAmount(errors);
		validateForDuplicatePeriodicFee(request, errors);
	}
	
	protected void validateForFeeAmount(ActionErrors errors){
		List<FeeView> feeList = getFeesToApply();
		for(FeeView fee: feeList){
			if(StringUtils.isNullOrEmpty(fee.getAmount()))
				errors.add(CustomerConstants.FEE,new ActionMessage(CustomerConstants.ERRORS_SPECIFY_FEE_AMOUNT));
		}
	}
	
	protected void validateForDuplicatePeriodicFee(HttpServletRequest request, ActionErrors errors) {
		List<FeeView> additionalFeeList = (List<FeeView>)SessionUtils.getAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, request.getSession());
		for(FeeView selectedFee: getAdditionalFees()){
			int count = 0;
			for(FeeView duplicateSelectedfee: getAdditionalFees()){
				if(selectedFee.getFeeIdValue()!=null && selectedFee.getFeeId().equals(duplicateSelectedfee.getFeeId())){
					if(isSelectedFeePeriodic(selectedFee, additionalFeeList))
						count++;
				}
			}
			if(count>1){
				errors.add(CustomerConstants.FEE,new ActionMessage(CustomerConstants.ERRORS_DUPLICATE_PERIODIC_FEE));
				break;
			}
		}
	}
	
	private boolean isSelectedFeePeriodic(FeeView selectedFee, List<FeeView> additionalFeeList){
		for(FeeView fee: additionalFeeList)
			if(fee.getFeeId().equals(selectedFee.getFeeId()))
					return fee.isPeriodic();
		return false;
	}
	
	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale=null;
		HttpSession session= request.getSession();
		if(session !=null) {
			UserContext userContext= (UserContext)session.getAttribute(LoginConstants.USERCONTEXT);
			if(null !=userContext) {
				locale=userContext.getPereferedLocale();
				if(null==locale) {
					locale=userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}
}

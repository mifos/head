/**

* ClientCustActionForm.java version: 1.0



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

package org.mifos.application.customer.client.struts.actionforms;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class ClientCustActionForm extends CustomerActionForm {

	private CustomerBO parentGroup;
	private String groupFlag;
	private ClientDetailView clientDetailView;
	private ClientNameDetailView clientName;
	private ClientNameDetailView spouseName;
	private String parentGroupId;
	private String governmentId;
	private String dateOfBirth;
	private String trained;
	private String trainedDate;
	private String nextOrPreview;
	private FormFile picture;
	private InputStream customerPicture;
	private int age;
	
	public String getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(String groupFlag) {
		this.groupFlag = groupFlag;
	}

	public CustomerBO getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(CustomerBO parentGroup) {
		this.parentGroup = parentGroup;
	}

	
	public ClientDetailView getClientDetailView() {
		return clientDetailView;
	}


	public void setClientDetailView(ClientDetailView clientDetailView) {
		this.clientDetailView = clientDetailView;
	}


	public ClientNameDetailView getClientName() {
		return clientName;
	}


	public void setClientName(ClientNameDetailView clientName) {
		this.clientName = clientName;
	}


	public ClientNameDetailView getSpouseName() {
		return spouseName;
	}


	public void setSpouseName(ClientNameDetailView spouseName) {
		this.spouseName = spouseName;
	}


	public String getParentGroupId() {
		return parentGroupId;
	}


	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}


	public String getGovernmentId() {
		return governmentId;
	}


	public void setGovernmentId(String governmentId) {
		this.governmentId = governmentId;
	}


	public String getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public FormFile getPicture() {
		return picture;
	}

	public String getTrained() {
		return trained;
	}

	public void setTrained(String trained) {
		this.trained = trained;
	}

	public String getTrainedDate() {
		return trainedDate;
	}

	public void setTrainedDate(String trainedDate) {
		this.trainedDate = trainedDate;
	}

	public String getNextOrPreview() {
		return nextOrPreview;
	}


	public void setNextOrPreview(String nextOrPreview) {
		this.nextOrPreview = nextOrPreview;
	}


	public void setPicture(FormFile picture) {
		this.picture = picture;
		try{
			customerPicture = picture.getInputStream();
		}
		catch(IOException ioe){

		}
	}
	
	
	public InputStream getCustomerPicture() {
		return customerPicture;
	}

	public int getAge() {
		return age;
		 
	}
	
	public void setAge(int age) {
		this.age = age;
		 
	}

	@Override
	protected ActionErrors validateFields(HttpServletRequest request, String method) {
		
		ActionErrors errors = new ActionErrors();
		if( ( method.equals(Methods.preview.toString()) || method.equals(Methods.next.toString()) )&& ( ClientConstants.INPUT_PERSONAL_INFO.equals(input) || ClientConstants.INPUT_EDIT_PERSONAL_INFO.equals(input) )){
			System.out.println("inside next method");
			validateClientandSpouseNames(errors);
			validateDateOfBirth(request,errors);
			validateGender(errors);
			validateConfigurableMandatoryFields(request,errors,EntityType.CLIENT);
			validateCustomFields(request,errors);
			if(picture !=null){
				String fileName = picture.getFileName();
				if(picture.getFileSize() > ClientConstants.PICTURE_ALLOWED_SIZE){
					errors.add(ClientConstants.PICTURE_SIZE_EXCEPTION,new ActionMessage(ClientConstants.PICTURE_SIZE_EXCEPTION));
				}
				if(!ValidateMethods.isNullOrBlank(fileName)){
					String fileExtension =fileName.substring(fileName.lastIndexOf(".")+1 , fileName.length());
					if(!(fileExtension.equalsIgnoreCase("jpeg") || fileExtension.equalsIgnoreCase("jpg")) )
						errors.add(ClientConstants.PICTURE_EXCEPTION,new ActionMessage(ClientConstants.PICTURE_EXCEPTION));

				}
				if(picture.getFileSize() == 0||picture.getFileSize() < 0){

					SessionUtils.setAttribute("noPicture" , "Yes" ,request.getSession());
				}
				else{
					SessionUtils.setAttribute("noPicture" , "No" ,request.getSession());
				}
			}
		}
		if(method.equals(Methods.preview.toString()) && ClientConstants.INPUT_MFI_INFO.equals(input) ){
			validateConfigurableMandatoryFields(request,errors,EntityType.CLIENT);
			validateFormedByPersonnel(errors);
			validateFees(request, errors);
			validateTrained(request, errors);
		}
		return errors;
	}

	private void validateGender(ActionErrors errors) {
		if (clientDetailView.getGender() == null)
			errors.add(CustomerConstants.GENDER, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.GENDER));		
	}
	
	private void validateClientandSpouseNames(ActionErrors errors) {
		if (clientName.getSalutation() == null)
			errors.add(CustomerConstants.SALUTATION, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.SALUTATION));		
		if (StringUtils.isNullOrEmpty(clientName.getFirstName()))
			errors.add(CustomerConstants.FIRST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.FIRST_NAME));
		if (StringUtils.isNullOrEmpty(clientName.getLastName()))
			errors.add(CustomerConstants.LAST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.LAST_NAME));
		if (spouseName.getNameType() == null)
			errors.add(CustomerConstants.SPOUSE_TYPE, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.SPOUSE_TYPE));		
		if (StringUtils.isNullOrEmpty(spouseName.getFirstName()))
					errors.add(CustomerConstants.SPOUSE_FIRST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.SPOUSE_FIRST_NAME));
		if (StringUtils.isNullOrEmpty(spouseName.getLastName()))
			errors.add(CustomerConstants.SPOUSE_LAST_NAME, new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.SPOUSE_LAST_NAME));

	}

	private void validateDateOfBirth(HttpServletRequest request , ActionErrors errors) {
		if(StringUtils.isNullOrEmpty(dateOfBirth))
			errors.add(CustomerConstants.DOB,new ActionMessage(CustomerConstants.ERRORS_MANDATORY, CustomerConstants.DOB));
		if(( new CustomerHelper().isValidDOB(dateOfBirth , getUserLocale(request)) ) == false ){
			errors.add(ClientConstants.INVALID_DOB_EXCEPTION,new ActionMessage(ClientConstants.INVALID_DOB_EXCEPTION));
		}
		
	}
	
	private void validateTrained(HttpServletRequest request ,ActionErrors errors) {
		
		if(request.getParameter("trained")==null) {
			trained=null;
		}
		else if( trained.equals("1")){
			if(ValidateMethods.isNullOrBlank(trainedDate)){
				if(errors == null){
					errors = new ActionErrors();
				}
				errors.add(ClientConstants.TRAINED_DATE_MANDATORY,new ActionMessage(ClientConstants.TRAINED_DATE_MANDATORY));
			}
	
		}
		//if training date is entered and trained is not selected, throw an error
		if(!ValidateMethods.isNullOrBlank(trainedDate)&&ValidateMethods.isNullOrBlank(trained)){
			if(errors == null){
				errors = new ActionErrors();
			}
			errors.add(ClientConstants.TRAINED_CHECKED,new ActionMessage(ClientConstants.TRAINED_CHECKED));
		}
		
	}
	@Override
	public void checkForMandatoryFields(Short entityId, ActionErrors errors, HttpServletRequest request) {
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryFieldMap=(Map<Short,List<FieldConfigurationEntity>>)request.getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
		List<FieldConfigurationEntity> mandatoryfieldList=entityMandatoryFieldMap.get(entityId);
		for(FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList){
			String propertyName=request.getParameter(fieldConfigurationEntity.getLabel());
			Locale locale=((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPereferedLocale();
			if(propertyName!=null && !propertyName.equals("") && !propertyName.equalsIgnoreCase("picture")){
				String propertyValue=request.getParameter(propertyName);
				if(propertyValue==null || propertyValue.equals(""))
					errors.add(fieldConfigurationEntity.getLabel(),
							new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
									FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
			}else if(propertyName!=null && !propertyName.equals("") && propertyName.equalsIgnoreCase("picture")){
					try {
						if(getCustomerPicture()==null || getCustomerPicture().read()==-1 ){
							errors.add(fieldConfigurationEntity.getLabel(),
									new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
											FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
						
						}
						getCustomerPicture().reset();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

}

/**

 * OfficeActionForm.java    version: 1.0

 

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
package org.mifos.application.office.struts.actionforms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.CustomFieldDefinition;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.office.util.valueobjects.OfficeAddress;
import org.mifos.application.office.util.valueobjects.OfficeCode;
import org.mifos.application.office.util.valueobjects.OfficeCustomField;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.application.office.util.valueobjects.OfficeStatus;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class represents the action form for the office module it contains all
 * important fields for the office
 */
public class OfficeActionForm extends MifosActionForm {

	// getting the logger
	MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.OFFICELOGGER);

	private static final long serialVersionUID = 0l;

	/**
	 * This would hold the OfficeId for the office
	 */
	private String officeId;

	/**
	 * This would hold the officeName
	 */
	private String officeName;

	/**
	 * This would hold the office shortName
	 */
	private String shortName;

	/**
	 * This would hold the OfficeCode for the office
	 */
	private String formOfficeCode;

	/**
	 * This would hold the office type for the office
	 */
	private String formOfficeType;

	/**
	 * This would hold the parent office for the office
	 */
	private String formParentOffice;

	/**
	 * This would hold the status for the office
	 */
	private String formOfficeStatus;

	/**
	 * This would hold the address of the office
	 */
	private OfficeAddress address;

	/**
	 * This would hold the custom field for the office
	 */
	private List<OfficeCustomField> customFieldSet;

	/**
	 * This would hold the version for the office
	 */

	private String versionNo;

	/**
	 * @return Returns the version.
	 */

	/**
	 * This would hold the parent version no
	 */
	private String parentVersion;

	/**
	 * @return Returns the parentVersion.
	 */
	public String getParentVersion() {
		return parentVersion;
	}

	/**
	 * @param parentVersion
	 *            The parentVersion to set.
	 */
	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	public String getVersionNo() {
		return versionNo;
	}

	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * Default constructor for the officeActionForm
	 */
	public OfficeActionForm() {
		super();
		address = new OfficeAddress();
		customFieldSet = new ArrayList<OfficeCustomField>();
	}

	/**
	 * This function returns the address
	 * 
	 * @return Returns the address.
	 */
	public OfficeAddress getAddress() {
		logger.debug("getting address" + address);
		return address;
	}

	/**
	 * This function sets the address
	 * 
	 * @param address
	 *            The address to set.
	 */
	public void setAddress(OfficeAddress address) {
		this.address = address;
	}

	/**
	 * This function returns the custom field based on the index
	 * 
	 * @return Returns the OfficeCustomField.
	 */
	public OfficeCustomField getCustomField(int index) {
		logger.info("getting singlegetCustomField with index"
				+ index);
		while (index >= customFieldSet.size()) {
			customFieldSet.add(new OfficeCustomField());
		}
		return (OfficeCustomField) ((customFieldSet.toArray())[index]);
	}

	/**
	 * This function returns the customFieldSet
	 * 
	 * @return Returns the customFieldSet.
	 */
	public Set getCustomFieldSet() {

		return new HashSet(this.customFieldSet);
	}

	/**
	 * This function sets the customFieldSet
	 * 
	 * @param customFieldSet
	 *            The customFieldSet to set.
	 */
	public void setCustomFieldSet(List<OfficeCustomField> customFieldSet) {
		this.customFieldSet = customFieldSet;
	}

	/**
	 * This function returns the officeName
	 * 
	 * @return Returns the officeName.
	 */
	public String getOfficeName() {
		return officeName;
	}

	/**
	 * This function sets the officeName
	 * 
	 * @param officeName
	 *            The officeName to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	/**
	 * This function returns the officeCode
	 * 
	 * @return Returns the officeCode.
	 */
	public OfficeCode getOfficeCode() {

		OfficeCode code = new OfficeCode();
		if (null == this.formOfficeCode
				|| this.formOfficeCode.equals("")) {
			return null;
		} else {

		try {
			code.setCodeId(Short.valueOf(this.formOfficeCode));
		} catch (Exception e) {

		}
		}
		return code;
	}

	/**
	 * This function would return the parent office object for the current
	 * office this would convert the selected parent to the office object and
	 * set the id
	 * 
	 * @return
	 */
	public Office getParentOffice() {
		Office office = new Office();
		if (null == this.formParentOffice
				|| this.formParentOffice.equalsIgnoreCase("")) {

			logger.info("returning parent office as null");
			return null;
		} else {
			try {
				office.setOfficeId(Short.valueOf(this.formParentOffice));
				office.setVersionNo(Integer.valueOf(this.parentVersion));
			} catch (Exception e) {

			}
			logger.info("returning parent office as"
					+ office.getOfficeId() + office.getVersionNo());

			return office;
		}
	}

	/**
	 * This function returns the shortName
	 * 
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * This function sets the shortName
	 * 
	 * @param shortName
	 *            The shortName to set.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * This function would return the selected office level .This function will
	 * covert the select level to OfficeLevel object
	 * 
	 * @return OfficeLevel object levelId set
	 */
	public OfficeLevel getLevel() {
		logger.info("getting OfficeLevel"
				+ this.formOfficeType);
		OfficeLevel ol = new OfficeLevel();

		try {
			ol.setLevelId(Short.valueOf(this.formOfficeType));
		} catch (Exception e) {

		}

		return ol;
	}

	/**
	 * This function would set the office level in the action form
	 * 
	 * @param level
	 *            OfficeLevel object
	 */
	public void setLevel(OfficeLevel level) {

		if (null != level) {
			if (null != level.getLevelId()) {
				this.formOfficeType = String.valueOf(level.getLevelId()
						.intValue());
			}
		}
	}

	/**
	 * This function would return the selected office ststus .This function will
	 * covert the selected status to OfficeStatus object
	 * 
	 * @return OfficeLevel object levelId set
	 */

	public OfficeStatus getStatus() {
		OfficeStatus os = new OfficeStatus();
		try {
			os.setStatusId(Short.valueOf(this.formOfficeStatus));
		} catch (Exception e) {

		}
		return os;
	}

	/**
	 * This function returns the formOfficeCode
	 * 
	 * @return Returns the formOfficeCode.
	 */
	public String getFormOfficeCode() {
		return formOfficeCode;
	}

	/**
	 * This function sets the formOfficeCode
	 * 
	 * @param formOfficeCode
	 *            The formOfficeCode to set.
	 */
	public void setFormOfficeCode(String formOfficeCode) {

		this.formOfficeCode = formOfficeCode;
	}

	/**
	 * This function returns the formOfficeType
	 * 
	 * @return Returns the formOfficeType.
	 */
	public String getFormOfficeType() {
		return formOfficeType;
	}

	/**
	 * This function sets the formOfficeType
	 * 
	 * @param formOfficeType
	 *            The formOfficeType to set.
	 */
	public void setFormOfficeType(String formOfficeType) {

		this.formOfficeType = formOfficeType;
	}

	/**
	 * This function returns the formParentOffice
	 * 
	 * @return Returns the formParentOffice.
	 */
	public String getFormParentOffice() {
		return formParentOffice;
	}

	/**
	 * This function sets the formParentOffice
	 * 
	 * @param formParentOffice
	 *            The formParentOffice to set.
	 */
	public void setFormParentOffice(String formParentOffice) {

		this.formParentOffice = formParentOffice;
	}

	/**
	 * This function returns the formOfficeStatus
	 * 
	 * @return Returns the formOfficeStatus.
	 */
	public String getFormOfficeStatus() {
		return formOfficeStatus;
	}

	/**
	 * This function sets the formOfficeStatus
	 * 
	 * @param formOfficeStatus
	 *            The formOfficeStatus to set.
	 */
	public void setFormOfficeStatus(String formOfficeStatus) {
		this.formOfficeStatus = formOfficeStatus;
	}

	/**
	 * This function returns the officeId
	 * 
	 * @return Returns the officeId.
	 */
	public String getOfficeId() {
		
		logger.info("current officeId is "+officeId);
		return officeId;
	}

	/**
	 * This function sets the officeId
	 * 
	 * @param officeId
	 *            The officeId to set.
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	/**
	 * This function would handle the special cases wher we do not want the
	 * validation to happens
	 * 
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		String methodCalled = request.getParameter("method");
		logger.info("Action form method called; " + methodCalled);
		ActionErrors errors=null;
		if (null != methodCalled) {
			
			if ("cancel".equals(methodCalled) || "get".equals(methodCalled)
					|| "load".equals(methodCalled)
					|| "manage".equals(methodCalled)
					|| "previous".equals(methodCalled)
					|| "loadParent".equals(methodCalled)
					|| "loadall".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}else{
				if("preview".equals(methodCalled)){
					errors=new ActionErrors();
					checkForMandatoryFields(EntityMasterConstants.Office,errors,request);
					errors.add(validateCustomFields(request,errors));
				}
			}

		}
		return errors;
	}
	
	private ActionMessages validateCustomFields(HttpServletRequest request,ActionErrors errors) {
		ActionMessages actionMessages = null;
		Context context=(Context)SessionUtils.getAttribute(Constants.CONTEXT , request.getSession());
		List<CustomFieldDefinition> customFields = (List)context.getSearchResultBasedOnName(OfficeConstants.CUSTOM_FIELDS).getValue();
		for(int i=0;i<customFieldSet.size();i++){
			for(int j=0;j<customFields.size();j++){
				if(customFieldSet.get(i).getFieldId().shortValue() == customFields.get(j).getFieldId().shortValue() 
					&& customFields.get(j).isMandatory()){
					if(ValidateMethods.isNullOrBlank(customFieldSet.get(i).getFieldValue())){
						actionMessages=new ActionMessages();
						actionMessages.add(OfficeConstants.KEYCUSTOMFIELDREQUIRED,new ActionMessage(OfficeConstants.KEYCUSTOMFIELDREQUIRED));
					}
					break;
				}
				
			}
		
		}
		return actionMessages;
	}
	public void cleanForm()
	{
		if( null!=this.address)
		{
			// 26206  office address id was not getting cleared
			this.address.setOfficeAdressId(null);
			this.address.setAddress1("");
			this.address.setAddress2("");
			this.address.setAddress3("");
			this.address.setCity("");
			this.address.setState("");
			this.address.setCountry("");
			this.address.setPostalCode("");
			this.address.setTelephoneNo("");
		}
		this.officeName="";
		this.shortName="";
		this.formOfficeType="";
		this.formOfficeCode="";
		this.formParentOffice="";
	}
}

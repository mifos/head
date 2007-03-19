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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
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

	private String nextOrPreview;

	private FormFile picture;

	private InputStream customerPicture;

	private int age;

	private List<Short> selectedOfferings;

	public ClientCustActionForm() {
		super();
		selectedOfferings = new ArrayList<Short>(
				ClientConstants.MAX_OFFERINGS_SIZE);
		for (int i = 0; i < ClientConstants.MAX_OFFERINGS_SIZE; i++)
			selectedOfferings.add(null);
	}

	public String getGroupFlag() {
		return groupFlag;
	}

	public Short getGroupFlagValue() {
		return getShortValue(groupFlag);
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

	public String getNextOrPreview() {
		return nextOrPreview;
	}

	public void setNextOrPreview(String nextOrPreview) {
		this.nextOrPreview = nextOrPreview;
	}

	public void setPicture(FormFile picture) {
		this.picture = picture;
		try {
			customerPicture = picture.getInputStream();
		} catch (IOException ioe) {

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

	public List<Short> getSelectedOfferings() {
		return selectedOfferings;
	}

	public Short getSavingsOffering(int i) {
		return (i < ClientConstants.MAX_OFFERINGS_SIZE) ? selectedOfferings
				.get(i) : null;
	}

	public void setSavingsOffering(int i, Short value) {
		if (i < ClientConstants.MAX_OFFERINGS_SIZE)
			selectedOfferings.set(i, value);
	}

	@Override
	protected ActionErrors validateFields(HttpServletRequest request,
			String method) throws ApplicationException {

		ActionErrors errors = new ActionErrors();
		if ((method.equals(Methods.previewPersonalInfo.toString())
				|| method.equals(Methods.next.toString()) || method
				.equals(Methods.previewEditPersonalInfo.toString()))
				&& (ClientConstants.INPUT_PERSONAL_INFO.equals(input) || ClientConstants.INPUT_EDIT_PERSONAL_INFO
						.equals(input))) {
			validateClientNames(errors);
			validateDateOfBirth(request, errors);
			validateGender(errors);
			validateSpouseNames(errors);
			checkForMandatoryFields(EntityType.CLIENT.getValue(), errors,request);
			validateCustomFields(request, errors);
			validatePicture(request, errors);
		}
		if (method.equals(Methods.preview.toString())
				&& ClientConstants.INPUT_MFI_INFO.equals(input)) {
			validateFormedByPersonnel(errors);
			validateConfigurableMandatoryFields(request, errors,
					EntityType.CLIENT);
			validateTrained(request, errors);
			validateFees(request, errors);
			validateSelectedOfferings(errors, request);
		}

		if (method.equals(Methods.previewEditMfiInfo.toString())) {
			checkForMandatoryFields(EntityType.CLIENT.getValue(), errors,request);
			validateTrained(request, errors);
		}
		if (method.equals(Methods.updateMfiInfo.toString())) {
			validateTrained(request, errors);   
		}
		return errors;
	}

	private void validatePicture(HttpServletRequest request, ActionErrors errors)
			throws PageExpiredException {
		if (picture != null) {
			String fileName = picture.getFileName();
			if (picture.getFileSize() > ClientConstants.PICTURE_ALLOWED_SIZE) {
				errors.add(ClientConstants.PICTURE_SIZE_EXCEPTION,
						new ActionMessage(
								ClientConstants.PICTURE_SIZE_EXCEPTION));
			}
			if (!ValidateMethods.isNullOrBlank(fileName)) {
				String fileExtension = fileName.substring(fileName
						.lastIndexOf(".") + 1, fileName.length());
				if (!(fileExtension.equalsIgnoreCase("jpeg") || fileExtension
						.equalsIgnoreCase("jpg")))
					errors
							.add(ClientConstants.PICTURE_EXCEPTION,
									new ActionMessage(
											ClientConstants.PICTURE_EXCEPTION));

			}
			if (picture.getFileSize() == 0 || picture.getFileSize() < 0) {
				SessionUtils.setAttribute("noPicture", "Yes", request);
			} else {
				SessionUtils.setAttribute("noPicture", "No", request);
			}
		}
	}

	private void validateGender(ActionErrors errors) {
		if (clientDetailView.getGender() == null)
			errors.add(CustomerConstants.GENDER, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.GENDER));
	}

	private void validateClientNames(ActionErrors errors) {
		if (clientName.getSalutation() == null)
			errors.add(CustomerConstants.SALUTATION, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.SALUTATION));
		if (StringUtils.isNullOrEmpty(clientName.getFirstName()))
			errors.add(CustomerConstants.FIRST_NAME, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.FIRST_NAME));
		if (StringUtils.isNullOrEmpty(clientName.getLastName()))
			errors.add(CustomerConstants.LAST_NAME, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.LAST_NAME));

	}

	private void validateSpouseNames(ActionErrors errors) {
		if (spouseName.getNameType() == null)
			errors.add(CustomerConstants.SPOUSE_TYPE, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.SPOUSE_TYPE));
		if (StringUtils.isNullOrEmpty(spouseName.getFirstName()))
			errors.add(CustomerConstants.SPOUSE_FIRST_NAME, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.SPOUSE_FIRST_NAME));
		if (StringUtils.isNullOrEmpty(spouseName.getLastName()))
			errors.add(CustomerConstants.SPOUSE_LAST_NAME, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY,
					CustomerConstants.SPOUSE_LAST_NAME));

	}

	private void validateDateOfBirth(HttpServletRequest request,
			ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(dateOfBirth)) {
			errors.add(CustomerConstants.DOB, new ActionMessage(
					CustomerConstants.ERRORS_MANDATORY, CustomerConstants.DOB));
		}
		
		else {
			try {
				Date date = DateHelper.getDateAsSentFromBrowser(dateOfBirth);
				if (DateHelper.whichDirection(date) > 0) {
					throw new InvalidDateException(dateOfBirth);
				}
			}
			
			catch (InvalidDateException e) {
				errors.add(ClientConstants.INVALID_DOB_EXCEPTION,
						new ActionMessage(ClientConstants.INVALID_DOB_EXCEPTION));
			}
			
		}
	}

	@Override
	public void checkForMandatoryFields(Short entityId, ActionErrors errors,
			HttpServletRequest request) {
		Map<Short, List<FieldConfigurationEntity>> entityMandatoryFieldMap = (Map<Short, List<FieldConfigurationEntity>>) request
				.getSession().getServletContext().getAttribute(
						Constants.FIELD_CONFIGURATION);
		List<FieldConfigurationEntity> mandatoryfieldList = entityMandatoryFieldMap
				.get(entityId);
		for (FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList) {
			String propertyName = request.getParameter(fieldConfigurationEntity
					.getLabel());
			Locale locale = ((UserContext) request.getSession().getAttribute(
					LoginConstants.USERCONTEXT)).getPereferedLocale();
			if (propertyName != null && !propertyName.equals("")
					&& !propertyName.equalsIgnoreCase("picture")) {
				String propertyValue = request.getParameter(propertyName);
				if (propertyValue == null || propertyValue.equals(""))
					errors
							.add(
									fieldConfigurationEntity.getLabel(),
									new ActionMessage(
											FieldConfigurationConstant.EXCEPTION_MANDATORY,
											FieldConfigurationHelper
													.getLocalSpecificFieldNames(
															fieldConfigurationEntity
																	.getLabel(),
															locale)));
			} else if (propertyName != null && !propertyName.equals("")
					&& propertyName.equalsIgnoreCase("picture")) {
				try {
					if (getCustomerPicture() == null
							|| getCustomerPicture().read() == -1) {
						errors
								.add(
										fieldConfigurationEntity.getLabel(),
										new ActionMessage(
												FieldConfigurationConstant.EXCEPTION_MANDATORY,
												FieldConfigurationHelper
														.getLocalSpecificFieldNames(
																fieldConfigurationEntity
																		.getLabel(),
																locale)));

					}
					getCustomerPicture().reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void validateSelectedOfferings(ActionErrors errors, HttpServletRequest request) {
		boolean duplicateFound = false;
		for (int i = 0; i < selectedOfferings.size() - 1; i++) {
			for (int j = i + 1; j < selectedOfferings.size(); j++)
				if (selectedOfferings.get(i) != null
						&& selectedOfferings.get(j) != null
						&& selectedOfferings.get(i).equals(
								selectedOfferings.get(j))) {
					String selectedOffering = "";
					try {
						List<SavingsOfferingBO> offeringsList = (List<SavingsOfferingBO>) SessionUtils
							.getAttribute(ClientConstants.SAVINGS_OFFERING_LIST, request);
						for (SavingsOfferingBO savingsOffering : offeringsList) {
							if (selectedOfferings.get(i).equals(savingsOffering.getPrdOfferingId()))
								selectedOffering = savingsOffering.getPrdOfferingName();
							break;
						}
					} catch (PageExpiredException pee) { }
					errors.add(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED,
						new ActionMessage(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED, selectedOffering));
					duplicateFound = true;
					break;
				}
			if (duplicateFound)
				break;
		}
	}

	@Override
	protected MeetingBO getCustomerMeeting(HttpServletRequest request)
			throws ApplicationException {
		if (groupFlag.equals(ClientConstants.YES) && parentGroup.getCustomerMeeting()!=null)
			return parentGroup.getCustomerMeeting().getMeeting();
		else
			return (MeetingBO) SessionUtils.getAttribute(
					CustomerConstants.CUSTOMER_MEETING, request);
	}

	private boolean isValidDOB(String dob, Locale mfiLocale) {
		java.sql.Date sqlDOB = null;
		boolean isValidDate = true;

		if (!ValidateMethods.isNullOrBlank(dob)) {
			sqlDOB = DateHelper.getLocaleDate(mfiLocale, dob);
			Calendar currentCalendar = new GregorianCalendar();
			int year = currentCalendar.get(Calendar.YEAR);
			int month = currentCalendar.get(Calendar.MONTH);
			int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
			currentCalendar = new GregorianCalendar(year, month, day);
			java.sql.Date currentDate = new java.sql.Date(currentCalendar
					.getTimeInMillis());
			if (currentDate.compareTo(sqlDOB) < 0) {
				isValidDate = false;

			}
		}
		return isValidDate;
	}

}

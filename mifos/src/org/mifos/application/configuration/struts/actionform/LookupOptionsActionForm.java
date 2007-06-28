/**

 * LookupOptionsActionForm.java    version: xxx



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

package org.mifos.application.configuration.struts.actionform;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class LookupOptionsActionForm extends BaseActionForm {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

	private String salutation;
	private String userTitle;
	private String maritalStatus;
	private String educationLevel;
	private String citizenship;
	private String handicapped;
	private String officerTitle;
	private String ethnicity;
	private String purposeOfLoan;
	private String collateralType;
	private String attendance;


	private List<CustomValueListElement> salutations;
	private List<CustomValueListElement> userTitles;
	private List<CustomValueListElement> maritalStatuses;
	private List<CustomValueListElement> ethnicities;
	private List<CustomValueListElement> educationLevels;
	private List<CustomValueListElement> citizenships;
	private List<CustomValueListElement> purposesOfLoan;
	private List<CustomValueListElement> officerTitles;
	private List<CustomValueListElement> handicappeds;
	private List<CustomValueListElement> collateralTypes;
	private List<CustomValueListElement> attendances;
	

	private String[] salutationList;
	private String[] userTitleList;
	private String[] maritalStatusList;
	private String[] ethnicityList;
	private String[] educationLevelList;
	private String[] citizenshipList;
	private String[] purposeOfLoanList;
	private String[] officerTitleList;
	private String[] handicappedList;
	private String[] collateralTypeList;
	private String[] attendanceList;
	private String lookupValue;

	public String getLookupValue() {
		return lookupValue;
	}

	public void setLookupValue(String lookupValue) {
		this.lookupValue = lookupValue;
	}
	
	public String[] getSalutationList() {
		return salutationList;
	}

	public void setSalutationList(String[] salutationList) {
		this.salutationList = salutationList;
	}
	
	
	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}
	
	public String getUserTitle() {
		return userTitle;
	}

	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	
	
	public String getEducationLevel() {
		return educationLevel;
	}

	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}
	public String getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}
	
	public String getHandicapped() {
		return handicapped;
	}

	public void setHandicapped(String handicapped) {
		this.handicapped = handicapped;
	}
	
	public String getOfficerTitle() {
		return officerTitle;
	}

	public void setOfficerTitle(String officerTitle) {
		this.officerTitle = officerTitle;
	}
	
	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}
	public String getPurposeOfLoan() {
		return purposeOfLoan;
	}

	public void setPurposeOfLoan(String purposeOfLoan) {
		this.purposeOfLoan = purposeOfLoan;
	}
	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}
	
	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}
	
	public List<CustomValueListElement> getSalutations() {
		return salutations;
	}

	public void setSalutations(List<CustomValueListElement> salutations) {
		this.salutations = salutations;
	}
	
	public List<CustomValueListElement> getUserTitles() {
		return userTitles;
	}

	public void setUserTitles(List<CustomValueListElement> userTitles) {
		this.userTitles = userTitles;
	}
	
	public List<CustomValueListElement> getMaritalStatuses() {
		return maritalStatuses;
	}

	public void setMaritalStatuses(List<CustomValueListElement> maritalStatuses) {
		this.maritalStatuses = maritalStatuses;
	}
	
	public List<CustomValueListElement> getEthnicities() {
		return ethnicities;
	}

	public void setEthnicities(List<CustomValueListElement> ethnicities) {
		this.ethnicities = ethnicities;
	}
	
	public List<CustomValueListElement> getPurposesOfLoan() {
		return purposesOfLoan;
	}

	public void setPurposesOfLoan(List<CustomValueListElement> purposesOfLoan) {
		this.purposesOfLoan = purposesOfLoan;
	}
	
	public List<CustomValueListElement> getEducationLevels() {
		return educationLevels;
	}

	public void setEducationLevels(List<CustomValueListElement> educationLevels) {
		this.educationLevels = educationLevels;
	}
	public List<CustomValueListElement> getCitizenships() {
		return citizenships;
	}

	public void setCitizenships(List<CustomValueListElement> citizenships) {
		this.citizenships = citizenships;
	}
	
	public List<CustomValueListElement> getHandicappeds() {
		return handicappeds;
	}

	public void setHandicappeds(List<CustomValueListElement> handicappeds) {
		this.handicappeds = handicappeds;
	}
	
	public List<CustomValueListElement> getOfficerTitles() {
		return officerTitles;
	}

	public void setOfficerTitles(List<CustomValueListElement> officerTitles) {
		this.officerTitles = officerTitles;
	}
	
	public List<CustomValueListElement> getCollateralTypes() {
		return collateralTypes;
	}

	public void setCollateralTypes(List<CustomValueListElement> collateralTypes) {
		this.collateralTypes = collateralTypes;
	}
	
	public List<CustomValueListElement> getAttendances() {
		return attendances;
	}

	public void setAttendances(List<CustomValueListElement> attendances) {
		this.attendances = attendances;
	}
	
	
	
	
	
	public LookupOptionsActionForm() {
		super();

	}
	
	public void BuildLink()
	{
	}
	
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
	}


	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("Inside validate method");
		
		String method = request.getParameter(Methods.method.toString());
		ActionErrors errors = new ActionErrors();
		if (method.equals(Methods.update.toString())) {
			errors = super.validate(mapping, request);
		}
		if (!errors.isEmpty()) {
			request.setAttribute("methodCalled", method);
		}

		logger.debug("outside validate method");
		return errors;

	}

	
	public void clear() {
		this.salutation = null;
		this.userTitle = null;
		this.maritalStatus = null;
		this.educationLevel = null;
		this.citizenship = null;
		this.handicapped = null;
		this.officerTitle = null;
		this.ethnicity = null;
		this.purposeOfLoan = null;
		this.collateralType = null;
		this.attendance = null;
		this.salutationList = null;
		this.userTitleList = null;
		this.maritalStatusList = null;
		this.ethnicityList = null;
		this.educationLevelList = null;
		this.citizenshipList = null;
		this.purposeOfLoanList = null;
		this.officerTitleList = null;
		this.handicappedList = null;
		this.collateralTypeList = null;
		this.attendanceList = null;
		this.salutations = null;
		this.userTitles = null;
		this.maritalStatuses = null;
		this.ethnicities = null;
		this.educationLevels = null;
		this.citizenships = null;
		this.purposesOfLoan = null;
		this.officerTitles = null;
		this.handicappeds = null;
		this.collateralTypes = null;
		this.attendances = null;
		
	}

	public String[] getAttendanceList() {
		return attendanceList;
	}

	public void setAttendanceList(String[] attendanceList) {
		this.attendanceList = attendanceList;
	}

	public String[] getCitizenshipList() {
		return citizenshipList;
	}

	public void setCitizenshipList(String[] citizenshipList) {
		this.citizenshipList = citizenshipList;
	}

	public String[] getCollateralTypeList() {
		return collateralTypeList;
	}

	public void setCollateralTypeList(String[] collateralTypeList) {
		this.collateralTypeList = collateralTypeList;
	}

	public String[] getEducationLevelList() {
		return educationLevelList;
	}

	public void setEducationLevelList(String[] educationLevelList) {
		this.educationLevelList = educationLevelList;
	}

	public String[] getEthnicityList() {
		return ethnicityList;
	}

	public void setEthnicityList(String[] ethnicityList) {
		this.ethnicityList = ethnicityList;
	}

	public String[] getHandicappedList() {
		return handicappedList;
	}

	public void setHandicappedList(String[] handicappedList) {
		this.handicappedList = handicappedList;
	}

	public String[] getMaritalStatusList() {
		return maritalStatusList;
	}

	public void setMaritalStatusList(String[] maritalStatusList) {
		this.maritalStatusList = maritalStatusList;
	}

	public String[] getOfficerTitleList() {
		return officerTitleList;
	}

	public void setOfficerTitleList(String[] officerTitleList) {
		this.officerTitleList = officerTitleList;
	}

	public String[] getPurposesOfLoanList() {
		return purposeOfLoanList;
	}

	public void setPurposesOfLoanList(String[] purposeOfLoanList) {
		this.purposeOfLoanList = purposeOfLoanList;
	}

	public String[] getUserTitleList() {
		return userTitleList;
	}

	public void setUserTitleList(String[] userTitleList) {
		this.userTitleList = userTitleList;
	}

}


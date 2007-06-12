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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
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
	private String[] salutations;
	private String[] userTitles;
	private String[] maritalStatuses;
	private String[] ethnicities;
	private String[] educationLevels;
	private String[] citizenships;
	private String[] purposesOfLoan;
	private String[] officerTitles;
	private String[] handicappeds;
	private String[] collateralTypes;
	private String[] attendances;
	
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
	
	public String[] getSalutations() {
		return salutations;
	}

	public void setSalutations(String[] salutations) {
		this.salutations = salutations;
	}
	
	public String[] getUserTitles() {
		return userTitles;
	}

	public void setUserTitles(String[] userTitles) {
		this.userTitles = userTitles;
	}
	
	public String[] getMaritalStatuses() {
		return maritalStatuses;
	}

	public void setMaritalStatuses(String[] maritalStatuses) {
		this.maritalStatuses = maritalStatuses;
	}
	
	public String[] getEthnicities() {
		return ethnicities;
	}

	public void setEthnicities(String[] ethnicities) {
		this.ethnicities = ethnicities;
	}
	
	public String[] getPurposesOfLoan() {
		return purposesOfLoan;
	}

	public void setPurposesOfLoan(String[] purposesOfLoan) {
		this.purposesOfLoan = purposesOfLoan;
	}
	
	public String[] getEducationLevels() {
		return educationLevels;
	}

	public void setEducationLevels(String[] educationLevels) {
		this.educationLevels = educationLevels;
	}
	public String[] getCitizenships() {
		return citizenships;
	}

	public void setCitizenships(String[] citizenships) {
		this.citizenships = citizenships;
	}
	
	public String[] getHandicappeds() {
		return handicappeds;
	}

	public void setHandicappeds(String[] handicappeds) {
		this.handicappeds = handicappeds;
	}
	
	public String[] getOfficerTitles() {
		return officerTitles;
	}

	public void setOfficerTitles(String[] officerTitles) {
		this.officerTitles = officerTitles;
	}
	
	public String[] getCollateralTypes() {
		return collateralTypes;
	}

	public void setCollateralTypes(String[] collateralTypes) {
		this.collateralTypes = collateralTypes;
	}
	
	public String[] getAttendances() {
		return attendances;
	}

	public void setAttendances(String[] attendances) {
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

		
	}

}


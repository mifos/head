/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
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

public class HiddenMandatoryConfigurationActionForm extends BaseActionForm {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

    private String hideSystemExternalId;

    private String mandatorySystemExternalId;

    private String hideSystemEthnicity;

    private String mandatorySystemEthnicity;

    private String hideSystemCitizenShip;

    private String mandatorySystemCitizenShip;

    private String hideSystemHandicapped;

    private String mandatorySystemHandicapped;

    private String hideSystemEducationLevel;

    private String mandatorySystemEducationLevel;

    private String hideSystemPhoto;

    private String mandatorySystemPhoto;

    private String hideSystemAssignClientPostions;

    private String mandatorySystemAddress1;

    private String hideSystemAddress2;

    private String hideSystemAddress3;

    private String hideSystemCity;

    private String hideSystemState;

    private String hideSystemCountry;

    private String hideSystemPostalCode;

    private String hideSystemReceiptIdDate;

    private String hideSystemCollateralTypeNotes;

    private String hideClientMiddleName;

    private String hideClientSecondLastName;

    private String mandatoryClientSecondLastName;

    private String hideClientGovtId;

    private String mandatoryClientGovtId;

    private String hideClientPovertyStatus;

    private String mandatoryClientPovertyStatus;

    private String hideClientSpouseFatherMiddleName;

    private String hideClientSpouseFatherSecondLastName;

    private String mandatoryClientSpouseFatherSecondLastName;

    private String hideClientPhone;

    private String mandatoryClientPhone;

    private String hideClientTrained;

    private String mandatoryClientTrained;

    private String mandatoryClientTrainedOn;

    private String hideClientBusinessWorkActivities;

    private String hideGroupTrained;

    private String mandatoryLoanAccountPurpose;

    private String mandatoryLoanSourceOfFund;

    public String getHideClientBusinessWorkActivities() {
        return hideClientBusinessWorkActivities;
    }

    public void setHideClientBusinessWorkActivities(String hideClientBusinessWorkActivities) {
        this.hideClientBusinessWorkActivities = hideClientBusinessWorkActivities;
    }

    public String getHideClientGovtId() {
        return hideClientGovtId;
    }

    public void setHideClientGovtId(String hideClientGovtId) {
        this.hideClientGovtId = hideClientGovtId;
    }

    public String getHideClientMiddleName() {
        return hideClientMiddleName;
    }

    public void setHideClientMiddleName(String hideClientMiddleName) {
        this.hideClientMiddleName = hideClientMiddleName;
    }

    public String getHideClientPhone() {
        return hideClientPhone;
    }

    public void setHideClientPhone(String hideClientPhone) {
        this.hideClientPhone = hideClientPhone;
    }

    public String getHideClientSecondLastName() {
        return hideClientSecondLastName;
    }

    public void setHideClientSecondLastName(String hideClientSecondLastName) {
        this.hideClientSecondLastName = hideClientSecondLastName;
    }

    public String getHideClientSpouseFatherMiddleName() {
        return hideClientSpouseFatherMiddleName;
    }

    public void setHideClientSpouseFatherMiddleName(String hideClientSpouseFatherMiddleName) {
        this.hideClientSpouseFatherMiddleName = hideClientSpouseFatherMiddleName;
    }

    public String getHideClientSpouseFatherSecondLastName() {
        return hideClientSpouseFatherSecondLastName;
    }

    public void setHideClientSpouseFatherSecondLastName(String hideClientSpouseFatherSecondLastName) {
        this.hideClientSpouseFatherSecondLastName = hideClientSpouseFatherSecondLastName;
    }

    public String getHideClientTrained() {
        return hideClientTrained;
    }

    public void setHideClientTrained(String hideClientTrained) {
        this.hideClientTrained = hideClientTrained;
    }

    public String getHideGroupTrained() {
        return hideGroupTrained;
    }

    public void setHideGroupTrained(String hideGroupTrained) {
        this.hideGroupTrained = hideGroupTrained;
    }

    public String getHideSystemAddress3() {
        return hideSystemAddress3;
    }

    public void setHideSystemAddress3(String hideSystemAddress3) {
        this.hideSystemAddress3 = hideSystemAddress3;
    }

    public String getHideSystemAssignClientPostions() {
        return hideSystemAssignClientPostions;
    }

    public void setHideSystemAssignClientPostions(String hideSystemAssignClientPostions) {
        this.hideSystemAssignClientPostions = hideSystemAssignClientPostions;
    }

    public String getHideSystemCitizenShip() {
        return hideSystemCitizenShip;
    }

    public void setHideSystemCitizenShip(String hideSystemCitizenShip) {
        this.hideSystemCitizenShip = hideSystemCitizenShip;
    }

    public String getHideSystemCity() {
        return hideSystemCity;
    }

    public void setHideSystemCity(String hideSystemCity) {
        this.hideSystemCity = hideSystemCity;
    }

    public String getHideSystemCollateralTypeNotes() {
        return hideSystemCollateralTypeNotes;
    }

    public void setHideSystemCollateralTypeNotes(String hideSystemCollateralTypeNotes) {
        this.hideSystemCollateralTypeNotes = hideSystemCollateralTypeNotes;
    }

    public String getHideSystemCountry() {
        return hideSystemCountry;
    }

    public void setHideSystemCountry(String hideSystemCountry) {
        this.hideSystemCountry = hideSystemCountry;
    }

    public String getHideSystemEducationLevel() {
        return hideSystemEducationLevel;
    }

    public void setHideSystemEducationLevel(String hideSystemEducationLevel) {
        this.hideSystemEducationLevel = hideSystemEducationLevel;
    }

    public String getHideSystemEthnicity() {
        return hideSystemEthnicity;
    }

    public void setHideSystemEthnicity(String hideSystemEthnicity) {
        this.hideSystemEthnicity = hideSystemEthnicity;
    }

    public String getHideSystemExternalId() {
        return hideSystemExternalId;
    }

    public void setHideSystemExternalId(String hideSystemExternalId) {
        this.hideSystemExternalId = hideSystemExternalId;
    }

    public String getHideSystemHandicapped() {
        return hideSystemHandicapped;
    }

    public void setHideSystemHandicapped(String hideSystemHandicapped) {
        this.hideSystemHandicapped = hideSystemHandicapped;
    }

    public String getHideSystemPhoto() {
        return hideSystemPhoto;
    }

    public void setHideSystemPhoto(String hideSystemPhoto) {
        this.hideSystemPhoto = hideSystemPhoto;
    }

    public String getHideSystemPostalCode() {
        return hideSystemPostalCode;
    }

    public void setHideSystemPostalCode(String hideSystemPostalCode) {
        this.hideSystemPostalCode = hideSystemPostalCode;
    }

    public String getHideSystemReceiptIdDate() {
        return hideSystemReceiptIdDate;
    }

    public void setHideSystemReceiptIdDate(String hideSystemReceiptIdDate) {
        this.hideSystemReceiptIdDate = hideSystemReceiptIdDate;
    }

    public String getHideSystemState() {
        return hideSystemState;
    }

    public void setHideSystemState(String hideSystemState) {
        this.hideSystemState = hideSystemState;
    }

    public String getMandatoryClientGovtId() {
        return mandatoryClientGovtId;
    }

    public void setMandatoryClientGovtId(String mandatoryClientGovtId) {
        this.mandatoryClientGovtId = mandatoryClientGovtId;
    }

    public String getMandatoryClientPhone() {
        return mandatoryClientPhone;
    }

    public void setMandatoryClientPhone(String mandatoryClientPhone) {
        this.mandatoryClientPhone = mandatoryClientPhone;
    }

    public String getMandatoryClientSecondLastName() {
        return mandatoryClientSecondLastName;
    }

    public void setMandatoryClientSecondLastName(String mandatoryClientSecondLastName) {
        this.mandatoryClientSecondLastName = mandatoryClientSecondLastName;
    }

    public String getMandatoryClientSpouseFatherSecondLastName() {
        return mandatoryClientSpouseFatherSecondLastName;
    }

    public void setMandatoryClientSpouseFatherSecondLastName(String mandatoryClientSpouseFatherSecondLastName) {
        this.mandatoryClientSpouseFatherSecondLastName = mandatoryClientSpouseFatherSecondLastName;
    }

    public String getMandatoryClientTrained() {
        return mandatoryClientTrained;
    }

    public void setMandatoryClientTrained(String mandatoryClientTrained) {
        this.mandatoryClientTrained = mandatoryClientTrained;
    }

    public String getMandatoryClientTrainedOn() {
        return mandatoryClientTrainedOn;
    }

    public void setMandatoryClientTrainedOn(String mandatoryClientTrainedOn) {
        this.mandatoryClientTrainedOn = mandatoryClientTrainedOn;
    }

    public String getMandatorySystemAddress1() {
        return mandatorySystemAddress1;
    }

    public void setMandatorySystemAddress1(String mandatorySystemAddress1) {
        this.mandatorySystemAddress1 = mandatorySystemAddress1;
    }

    public String getMandatorySystemCitizenShip() {
        return mandatorySystemCitizenShip;
    }

    public void setMandatorySystemCitizenShip(String mandatorySystemCitizenShip) {
        this.mandatorySystemCitizenShip = mandatorySystemCitizenShip;
    }

    public String getMandatoryLoanAccountPurpose() {
        return mandatoryLoanAccountPurpose;
    }

    public void setMandatoryLoanAccountPurpose(String mandatoryLoanAccountPurpose) {
        this.mandatoryLoanAccountPurpose = mandatoryLoanAccountPurpose;
    }

    public String getMandatoryLoanSourceOfFund() {
        return mandatoryLoanSourceOfFund;
    }

    public void setMandatoryLoanSourceOfFund(String mandatoryLoanSourceOfFund) {
        this.mandatoryLoanSourceOfFund = mandatoryLoanSourceOfFund;
    }

    public String getMandatorySystemEducationLevel() {
        return mandatorySystemEducationLevel;
    }

    public void setMandatorySystemEducationLevel(String mandatorySystemEducationLevel) {
        this.mandatorySystemEducationLevel = mandatorySystemEducationLevel;
    }

    public String getMandatorySystemEthnicity() {
        return mandatorySystemEthnicity;
    }

    public void setMandatorySystemEthnicity(String mandatorySystemEthnicity) {
        this.mandatorySystemEthnicity = mandatorySystemEthnicity;
    }

    public String getMandatorySystemExternalId() {
        return mandatorySystemExternalId;
    }

    public void setMandatorySystemExternalId(String mandatorySystemExternalId) {
        this.mandatorySystemExternalId = mandatorySystemExternalId;
    }

    public String getMandatorySystemHandicapped() {
        return mandatorySystemHandicapped;
    }

    public void setMandatorySystemHandicapped(String mandatorySystemHandicapped) {
        this.mandatorySystemHandicapped = mandatorySystemHandicapped;
    }

    public String getMandatorySystemPhoto() {
        return mandatorySystemPhoto;
    }

    public void setMandatorySystemPhoto(String mandatorySystemPhoto) {
        this.mandatorySystemPhoto = mandatorySystemPhoto;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        String method = request.getParameter(Methods.method.toString());
        if (method.equals(Methods.update.toString())) {
            clear();
        }
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
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
        this.hideSystemExternalId = "0";
        this.mandatorySystemExternalId = "0";
        this.hideSystemEthnicity = "0";
        this.mandatorySystemEthnicity = "0";
        this.hideSystemCitizenShip = "0";
        this.mandatorySystemCitizenShip = "0";
        this.mandatoryLoanAccountPurpose = "0";
        this.mandatoryLoanSourceOfFund = "0";
        this.hideSystemHandicapped = "0";
        this.mandatorySystemHandicapped = "0";
        this.hideSystemEducationLevel = "0";
        this.mandatorySystemEducationLevel = "0";
        this.hideSystemPhoto = "0";
        this.mandatorySystemPhoto = "0";
        this.hideSystemAssignClientPostions = "0";
        this.mandatorySystemAddress1 = "0";
        this.hideSystemAddress2 = "0";
        this.hideSystemAddress3 = "0";
        this.hideSystemCity = "0";
        this.hideSystemState = "0";
        this.hideSystemCountry = "0";
        this.hideSystemPostalCode = "0";
        this.hideSystemReceiptIdDate = "0";
        this.hideSystemCollateralTypeNotes = "0";
        this.hideClientMiddleName = "0";
        this.hideClientSecondLastName = "0";
        this.mandatoryClientSecondLastName = "0";
        this.hideClientGovtId = "0";
        this.mandatoryClientGovtId = "0";
        this.hideClientPovertyStatus = "0";
        this.mandatoryClientPovertyStatus = "0";
        this.hideClientSpouseFatherMiddleName = "0";
        this.hideClientSpouseFatherSecondLastName = "0";
        this.mandatoryClientSpouseFatherSecondLastName = "0";
        this.hideClientPhone = "0";
        this.mandatoryClientPhone = "0";
        this.hideClientTrained = "0";
        this.mandatoryClientTrained = "0";
        this.mandatoryClientTrainedOn = "0";
        this.hideClientBusinessWorkActivities = "0";
        this.hideGroupTrained = "0";
    }

    public String getHideSystemAddress2() {
        return hideSystemAddress2;
    }

    public void setHideSystemAddress2(String hideSystemAddress2) {
        this.hideSystemAddress2 = hideSystemAddress2;
    }

    public String getHideClientPovertyStatus() {
        return hideClientPovertyStatus;
    }

    public void setHideClientPovertyStatus(String hideClientPovertyStatus) {
        this.hideClientPovertyStatus = hideClientPovertyStatus;
    }

    public String getMandatoryClientPovertyStatus() {
        return mandatoryClientPovertyStatus;
    }

    public void setMandatoryClientPovertyStatus(String mandatoryClientPovertyStatus) {
        this.mandatoryClientPovertyStatus = mandatoryClientPovertyStatus;
    }
}

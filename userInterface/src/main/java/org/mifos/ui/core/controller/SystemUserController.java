/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SystemUserController {

    @Autowired
    private OfficeServiceFacade officeServiceFacade;

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    protected SystemUserController() {
        // default contructor for spring autowiring
    }

    public SystemUserController(final OfficeServiceFacade serviceFacade, final PersonnelServiceFacade personnelServiceFacade) {
        this.officeServiceFacade = serviceFacade;
        this.personnelServiceFacade = personnelServiceFacade;
    }

    @SuppressWarnings("PMD")
    public OfficeHierarchyFormBean showBranchHierarchy() {

        OnlyBranchOfficeHierarchyDto hierarchy = this.officeServiceFacade.retrieveBranchOnlyOfficeHierarchy();

        List<BranchOnlyHierarchyBean> branchOnlyHierarchyList = new ArrayList<BranchOnlyHierarchyBean>();

        for (OfficeHierarchyDto office : hierarchy.getBranchOnlyOfficeHierarchy()) {

            BranchOnlyHierarchyBean branchOnlyHierarchyBean = new BranchOnlyHierarchyBean();
            branchOnlyHierarchyBean.setId(office.getOfficeId().intValue());
            branchOnlyHierarchyBean.setName(office.getOfficeName());

            List<ListElement> branches = new ArrayList<ListElement>();

            for (OfficeHierarchyDto child : office.getChildren()) {
                branches.add(new ListElement(child.getOfficeId().intValue(), child.getOfficeName()));
            }

            branchOnlyHierarchyBean.setChildren(branches);
            branchOnlyHierarchyList.add(branchOnlyHierarchyBean);
        }

        List<OfficeDto> nonBranchOffices = this.officeServiceFacade.retrieveAllNonBranchOfficesApplicableToLoggedInUser();

        List<ListElement> nonBranches = new ArrayList<ListElement>();

        for (OfficeDto office : nonBranchOffices) {
            nonBranches.add(new ListElement(office.getId().intValue(), office.getName()));
        }

        OfficeHierarchyFormBean bean = new OfficeHierarchyFormBean();
        bean.setNonBranches(nonBranches);
        bean.setBranchOnlyOfficeHierarchy(branchOnlyHierarchyList);

        return bean;
    }

    public UserFormBean createUserFormBean(final Long officeId, final UserFormBean formBean) {

        OfficeDto selectedOffice = this.officeServiceFacade.retrieveOfficeById(officeId.shortValue());

        formBean.setOfficeId(officeId);
        formBean.setOfficeName(selectedOffice.getName());

        MandatoryHiddenFieldsDto dtoFields = this.adminServiceFacade.retrieveHiddenMandatoryFields();

        formBean.getAddress().setAddress1Mandatory(dtoFields.isMandatorySystemAddress1());
        formBean.getAddress().setAddress2Hidden(dtoFields.isHideSystemAddress2());
        formBean.getAddress().setAddress3Hidden(dtoFields.isHideSystemAddress3());
        formBean.getAddress().setCityDistrictHidden(dtoFields.isHideSystemCity());
        formBean.getAddress().setStateHidden(dtoFields.isHideSystemState());
        formBean.getAddress().setCountryHidden(dtoFields.isHideSystemCountry());
        formBean.getAddress().setPostalCodeHidden(dtoFields.isHideSystemPostalCode());

        DefinePersonnelDto userRefData = this.personnelServiceFacade.retrieveInfoForNewUserDefinition(officeId.shortValue(), Locale.getDefault());

        formBean.setCustomFields(userRefData.getCustomFields());

        Map<String, String> genderOptions = new LinkedHashMap<String, String>();
        for (ListElement option : userRefData.getGenderList()) {
            genderOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setGenderOptions(genderOptions);

        Map<String, String> maritalStatusOptions = new LinkedHashMap<String, String>();
        for (ListElement option : userRefData.getMaritalStatusList()) {
            maritalStatusOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setMaritalStatusOptions(maritalStatusOptions);

        Map<String, String> preferredLanguageOptions = new LinkedHashMap<String, String>();
        for (ListElement option : userRefData.getLanguageList()) {
            preferredLanguageOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setPreferredLanguageOptions(preferredLanguageOptions);

        Map<String, String> userTitleOptions = new LinkedHashMap<String, String>();
        for (ListElement option : userRefData.getTitleList()) {
            userTitleOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setUserTitleOptions(userTitleOptions);

        Map<String, String> userHierarchyOptions = new LinkedHashMap<String, String>();
        for (ListElement option : userRefData.getPersonnelLevelList()) {
            userHierarchyOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setUserHierarchyOptions(userHierarchyOptions);

        Map<String, String> availableRolesOptions = new LinkedHashMap<String, String>();
        for (ListElement option : userRefData.getRolesList()) {
            availableRolesOptions.put(option.getId().toString(), option.getName());
        }
        formBean.setAvailableRolesOptions(availableRolesOptions);
        formBean.setSelectedRolesOptions(new LinkedHashMap<String, String>());

        DateTime today = new DateTime();
        formBean.setMfiJoiningDateDay(today.getDayOfMonth());
        formBean.setMfiJoiningDateMonth(today.getMonthOfYear());
        formBean.setMfiJoiningDateYear(today.getYearOfEra());

        return formBean;
    }

    @SuppressWarnings("PMD")
    public UserDetailDto createUser(final UserFormBean userFormBean) {

        Short officeId = userFormBean.getOfficeId().shortValue();
        String firstName = userFormBean.getFirstName();
        String middleName = userFormBean.getMiddleName();
        String secondLastName = userFormBean.getSecondLastName();
        String lastName = userFormBean.getLastName();
        String governmentIdNumber = userFormBean.getGovernmentId();
        DateTime dateOfBirth = userFormBean.getDateOfBirthAsDateTime();
        DateTime mfiJoiningDate = userFormBean.getMfiJoiningDateAsDateTime();
        DateTime branchJoiningDate = userFormBean.getMfiJoiningDateAsDateTime();

        String email = userFormBean.getEmail();

        Integer maritalStatus = null;
        if (StringUtils.isNotBlank(userFormBean.getSelectedMaritalStatus())) {
            maritalStatus = Integer.valueOf(userFormBean.getSelectedMaritalStatus());
        }
        Integer gender = Integer.valueOf(userFormBean.getSelectedGender());

        Integer title = null;

        if (StringUtils.isNotBlank(userFormBean.getSelectedUserTitle())) {
            title = Integer.valueOf(userFormBean.getSelectedUserTitle());
        }

        Short personnelLevelId = Short.valueOf(userFormBean.getSelectedUserHierarchy());

        List<ListElement> roles = new ArrayList<ListElement>();
        String[] selectedRoles = userFormBean.getSelectedRoles();
        if (selectedRoles != null) {
            for (String role : selectedRoles) {
                roles.add(new ListElement(Integer.valueOf(role), userFormBean.getSelectedRolesOptions().get(role)));
            }
        }

        AddressBean bean = userFormBean.getAddress();
        AddressDto address = new AddressDto(bean.getAddress1(), bean.getAddress2(), bean.getAddress3(), bean.getCityDistrict(),
                bean.getState(), bean.getCountry(), bean.getPostalCode(), bean.getTelephoneNumber());

        Short preferredLocale = Short.valueOf("1");
        String password = userFormBean.getPassword();
        String username = userFormBean.getUsername();

        Short personnelStatusId = Short.valueOf("1"); // active

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        CreateOrUpdatePersonnelInformation personnel = new CreateOrUpdatePersonnelInformation(personnelLevelId, officeId, title, preferredLocale,
                password, username, email, roles, customFields, firstName, middleName, lastName, secondLastName,
                governmentIdNumber, dateOfBirth, maritalStatus, gender, mfiJoiningDate, branchJoiningDate, address, personnelStatusId);

        UserDetailDto userDetails = this.personnelServiceFacade.createPersonnelInformation(personnel);

        return userDetails;
    }
}
/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.mifos.dto.screen.PersonnelDetailsDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        DefinePersonnelDto userRefData = this.personnelServiceFacade.retrieveInfoForNewUserDefinition(officeId.shortValue());

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

    public void updateUser(final UserFormBean userFormBean) {
        CreateOrUpdatePersonnelInformation personnel = translateUserFormBeanToDto(userFormBean);

        this.personnelServiceFacade.updatePersonnel(personnel);
    }

    public UserDetailDto createUser(final UserFormBean userFormBean) {

        CreateOrUpdatePersonnelInformation personnel = translateUserFormBeanToDto(userFormBean);

        return this.personnelServiceFacade.createPersonnelInformation(personnel);
    }

    @SuppressWarnings("PMD")
    private CreateOrUpdatePersonnelInformation translateUserFormBeanToDto(final UserFormBean userFormBean) {
        Short officeId = userFormBean.getOfficeId().shortValue();
        String firstName = userFormBean.getFirstName();
        String middleName = userFormBean.getMiddleName();
        String secondLastName = userFormBean.getSecondLastName();
        String lastName = userFormBean.getLastName();
        String governmentIdNumber = userFormBean.getGovernmentId();
        DateTime dateOfBirth = userFormBean.getDateOfBirthAsDateTime();
        DateTime mfiJoiningDate = userFormBean.getMfiJoiningDateAsDateTime();
        DateTime branchJoiningDate = userFormBean.getMfiJoiningDateAsDateTime();
        Date passwordExpirationDate = userFormBean.getPasswordExpirationDate();

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


        Short preferredLocale = null;
        if (StringUtils.isNotBlank(userFormBean.getSelectedPreferredLanguage())) {
            preferredLocale = Short.valueOf(userFormBean.getSelectedPreferredLanguage());
        }
        String password = userFormBean.getPassword();
        String username = userFormBean.getUsername();

        // FIXME - add status to screen and support translation from bean to DTO
        Short personnelStatusId = Short.valueOf("1"); // active

        List<CustomFieldDto> customFields = userFormBean.getCustomFields();

        CreateOrUpdatePersonnelInformation personnel = new CreateOrUpdatePersonnelInformation(userFormBean.getUserId(), personnelLevelId, officeId, title, preferredLocale,
                password, username, email, roles, customFields, firstName, middleName, lastName, secondLastName,
                governmentIdNumber, dateOfBirth, maritalStatus, gender, mfiJoiningDate, branchJoiningDate, address, personnelStatusId, passwordExpirationDate);
        return personnel;
    }

    @ModelAttribute("userFormBean")
    @RequestMapping(value="viewSystemUserDetails.ftl")
    public UserFormBean viewSystemUserDetails(@RequestParam(value="id", required=true) Long userId) {

        UserFormBean formBean = new UserFormBean();
        formBean.setUserId(userId);

        UserFormBean userFormBean = createPopulatedUserFormBean(userId, formBean);

        return userFormBean;
    }

    public UserFormBean createPopulatedUserFormBean(final Long userId, final UserFormBean formBean) {

        PersonnelInformationDto personnelInformation = this.personnelServiceFacade.getPersonnelInformationDto(userId, "");

        UserFormBean populatedBean = createUserFormBean(personnelInformation.getOfficeId().longValue(), formBean);

        populatedBean.setUserId(userId);
        populatedBean.setStatusId(personnelInformation.getStatus().getId());
        populatedBean.setDisplayName(personnelInformation.getDisplayName());

        PersonnelDetailsDto details = personnelInformation.getPersonnelDetails();
        populatedBean.setFirstName(details.getFirstName());
        populatedBean.setMiddleName(details.getMiddleName());
        populatedBean.setSecondLastName(details.getSecondLastName());
        populatedBean.setLastName(details.getLastName());
        populatedBean.setGovernmentId(details.getGovernmentIdNumber());
        populatedBean.setEmail(personnelInformation.getEmailId());

        populatedBean.setDateOfBirthDay(details.getDob().getDayOfMonth());
        populatedBean.setDateOfBirthMonth(details.getDob().getMonthOfYear());
        populatedBean.setDateOfBirthYear(details.getDob().getYearOfEra());

        if (details.getDateOfJoiningMFI() != null) {
            populatedBean.setMfiJoiningDateDay(details.getDateOfJoiningMFI().getDayOfMonth());
            populatedBean.setMfiJoiningDateMonth(details.getDateOfJoiningMFI().getMonthOfYear());
            populatedBean.setMfiJoiningDateYear(details.getDateOfJoiningMFI().getYearOfEra());
        }

        populatedBean.setSelectedGender(details.getGender().toString());
        if (details.getMaritalStatus() != null) {
            populatedBean.setSelectedMaritalStatus(details.getMaritalStatus().toString());
        }
        if (personnelInformation.getPreferredLanguageId() != null) {
            populatedBean.setSelectedPreferredLanguage(personnelInformation.getPreferredLanguageId().toString());
        }

        AddressDto address = details.getAddress();
        AddressBean bean = populatedBean.getAddress();
        bean.setAddress1(address.getLine1());
        bean.setAddress2(address.getLine2());
        bean.setAddress3(address.getLine3());
        bean.setCityDistrict(address.getCity());
        bean.setState(address.getState());
        bean.setCountry(address.getCountry());
        bean.setPostalCode(address.getZip());
        bean.setTelephoneNumber(address.getPhoneNumber());

        populatedBean.setAddress(bean);

        if (personnelInformation.getTitle() != null) {
            populatedBean.setSelectedUserTitle(personnelInformation.getTitle().toString());
        }
        populatedBean.setSelectedUserHierarchy(personnelInformation.getLevelId().toString());

        Set<ListElement> roles = personnelInformation.getPersonnelRoles();
        String[] selectedRoles = new String[roles.size()];
        int roleIndex = 0;
        for (ListElement listElement : roles) {
            selectedRoles[roleIndex] = listElement.getId().toString();
            roleIndex++;
        }
        populatedBean.setSelectedRoles(selectedRoles);

        populatedBean.setUsername(personnelInformation.getUserName());

        List<CustomFieldDto> currentBeanFields = new ArrayList<CustomFieldDto>();
        List<CustomFieldDto> defaultBeanFields = populatedBean.getCustomFields();
        for (CustomFieldDto customFieldDto : defaultBeanFields) {
            CustomFieldDto matchingField = findMatchingAndSetFieldValue(customFieldDto, personnelInformation.getCustomFields());
            if (matchingField != null) {
                currentBeanFields.add(matchingField);
            }
        }

        populatedBean.setRecentNotes(personnelInformation.getRecentPersonnelNotes());

        populatedBean.setCustomFields(currentBeanFields);

        populatedBean.prepareForPreview();
        populatedBean.prepateForReEdit();

        return populatedBean;
    }

    private CustomFieldDto findMatchingAndSetFieldValue(CustomFieldDto source, Set<CustomFieldDto> customFields) {
        CustomFieldDto match = null;
        for (CustomFieldDto possibleMatch : customFields) {
            if (source.getFieldId().equals(possibleMatch.getFieldId())) {
                source.setFieldValue(possibleMatch.getFieldValue());
                match = source;
            }
        }
        return match;
    }
}
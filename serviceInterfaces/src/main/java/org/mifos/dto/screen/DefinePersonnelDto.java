package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.List;

import org.mifos.dto.domain.CustomFieldDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class DefinePersonnelDto implements Serializable {

    private final String officeName;
    private final List<ListElement> titleList;
    private final List<ListElement> personnelLevelList;
    private final List<ListElement> genderList;
    private final List<ListElement> maritalStatusList;
    private final List<ListElement> languageList;
    private final List<ListElement> rolesList;
    private final List<CustomFieldDto> customFields;

    public DefinePersonnelDto(String officeName, List<ListElement> titleList,
            List<ListElement> personnelLevelList, List<ListElement> genderList,
            List<ListElement> maritalStatusList, List<ListElement> languageList,
            List<ListElement> rolesList, List<CustomFieldDto> customFields) {
        super();
        this.officeName = officeName;
        this.titleList = titleList;
        this.personnelLevelList = personnelLevelList;
        this.genderList = genderList;
        this.maritalStatusList = maritalStatusList;
        this.languageList = languageList;
        this.rolesList = rolesList;
        this.customFields = customFields;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public List<ListElement> getTitleList() {
        return this.titleList;
    }

    public List<ListElement> getPersonnelLevelList() {
        return this.personnelLevelList;
    }

    public List<ListElement> getGenderList() {
        return this.genderList;
    }

    public List<ListElement> getMaritalStatusList() {
        return this.maritalStatusList;
    }

    public List<ListElement> getLanguageList() {
        return this.languageList;
    }

    public List<ListElement> getRolesList() {
        return this.rolesList;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }
}

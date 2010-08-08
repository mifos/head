package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.rolesandpermission.business.RoleBO;

public class PersonnelServiceFacadeWebTier implements PersonnelServiceFacade {

    private final OfficeDao officeDao;
    private final CustomerDao customerDao;

    public PersonnelServiceFacadeWebTier(OfficeDao officeDao, CustomerDao customerDao) {
        super();
        this.officeDao = officeDao;
        this.customerDao = customerDao;
    }

    @Override
    public void searchUser(String searchString, Short userId, HttpServletRequest request) {
        PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
        try {
            PersonnelBO personnel = personnelBusinessService.getPersonnel(userId);

            addSearchValues(searchString, personnel.getOffice().getOfficeId().toString(), personnel.getOffice()
                    .getOfficeName(), request);
            searchString = org.mifos.framework.util.helpers.SearchUtils.normalizeSearchString(searchString);

            SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS, new PersonnelPersistence().search(searchString, userId), request);
        } catch (PageExpiredException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void addSearchValues(String searchString, String officeId, String officeName, HttpServletRequest request)
    throws PageExpiredException {
        SessionUtils.setAttribute(Constants.SEARCH_STRING, searchString, request);
        SessionUtils.setAttribute(Constants.BRANCH_ID, officeId, request);
        SessionUtils.setAttribute(Constants.OFFICE_NAME, officeName, request);
    }

    @Override
    public DefinePersonnelDto retrieveInfoForNewUserDefinition(Short officeId, Locale preferredLocale) {
        String officeName = officeDao.findOfficeDtoById(officeId).getLookupNameKey();
        List<ValueListElement> titles = customerDao.retrieveTitles();
        List<ListElement> titleList = new ArrayList<ListElement>();
        for (ValueListElement element: titles) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            titleList.add(listElement);
        }

        List<PersonnelLevelEntity> personnelLevels = customerDao.retrievePersonnelLevels();
        List<ListElement> personnelLevelList = new ArrayList<ListElement>();
        for (PersonnelLevelEntity level: personnelLevels) {
            ListElement listElement = new ListElement(new Integer(level.getId()), level.getLookUpValue().getLookUpName());
            personnelLevelList.add(listElement);
        }

        List<ValueListElement> genders = customerDao.retrieveGenders();
        List<ListElement> genderList = new ArrayList<ListElement>();
        for (ValueListElement element: genders) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            genderList.add(listElement);
        }

        List<ValueListElement> maritalStatuses = customerDao.retrieveMaritalStatuses();
        List<ListElement> maritalStatusList = new ArrayList<ListElement>();
        for (ValueListElement element: maritalStatuses) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            maritalStatusList.add(listElement);
        }

        List<ValueListElement> languages = customerDao.retrieveLanguages();
        List<ListElement> languageList = new ArrayList<ListElement>();
        for (ValueListElement element: languages) {
            ListElement listElement = new ListElement(element.getId(), element.getName());
            languageList.add(listElement);
        }

        List<RoleBO> roles;
        try {
            roles = new PersonnelBusinessService().getRoles();
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }

        List<ListElement> roleList = new ArrayList<ListElement>();
        for (RoleBO element: roles) {
            ListElement listElement = new ListElement(new Integer(element.getId()), element.getName());
            roleList.add(listElement);
        }

        List<CustomFieldDto> customFields = customerDao.retrieveCustomFieldsForPersonnel(preferredLocale);
        DefinePersonnelDto defineUserDto = new DefinePersonnelDto(officeName, titleList, personnelLevelList, genderList, maritalStatusList, languageList, roleList,
                                        customFields);
        return defineUserDto;
    }
}

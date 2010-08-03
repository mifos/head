package org.mifos.application.servicefacade;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.UserServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class UserServiceFacadeWebTier implements UserServiceFacade {

    @Override
    public void searchUser(String searchString, Short userId, HttpServletRequest request) {
        PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
        try {
            PersonnelBO personnel = personnelBusinessService.getPersonnel(userId);

            addSeachValues(searchString, personnel.getOffice().getOfficeId().toString(), personnel.getOffice()
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

    private void addSeachValues(String searchString, String officeId, String officeName, HttpServletRequest request)
    throws PageExpiredException {
        SessionUtils.setAttribute(Constants.SEARCH_STRING, searchString, request);
        SessionUtils.setAttribute(Constants.BRANCH_ID, officeId, request);
        SessionUtils.setAttribute(Constants.OFFICE_NAME, officeName, request);
    }
}

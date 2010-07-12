package org.mifos.customers.office.business.service;

import java.util.List;

import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.persistence.OfficeHierarchyPersistence;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.dto.screen.OfficeLevelDto;
import org.mifos.framework.exceptions.PersistenceException;

public class OfficeHierarchyServiceImpl implements OfficeHierarchyService {

    @Override
    public void updateOfficeHierarchyConfiguration(List<OfficeLevelDto> officeLevels) throws OfficeException {
        for (OfficeLevelDto dto: officeLevels) {
            try {
                if (!dto.isConfigured() && new OfficeHierarchyPersistence().isOfficePresentForLevel(dto.getLevelId())) {
                    throw new OfficeException(OfficeConstants.KEYHASACTIVEOFFICEWITHLEVEL);
                }
                new OfficeHierarchyPersistence().createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new OfficeException(e);
            }
        }
    }
}

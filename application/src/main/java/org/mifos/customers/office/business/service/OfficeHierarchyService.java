package org.mifos.customers.office.business.service;

import java.util.List;

import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.dto.domain.OfficeLevelDto;

public interface OfficeHierarchyService {

    void updateOfficeHierarchyConfiguration(List<OfficeLevelDto> officeLevels) throws OfficeException;
}

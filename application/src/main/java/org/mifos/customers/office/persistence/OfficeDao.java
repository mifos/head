package org.mifos.customers.office.persistence;

import java.util.List;

import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.security.util.UserContext;

public interface OfficeDao {

    OfficeBO findOfficeById(Short officeIdValue);

    OfficeDto findOfficeDtoById(Short valueOf);

    List<OfficeBO> findBranchsOnlyWithParentsMatching(String searchId);

    List<OfficeDetailsDto> findActiveOfficeLevels();

    void validateBranchIsActiveWithNoActivePersonnel(Short officeId, UserContext userContext) throws CustomerException;
}

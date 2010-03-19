package org.mifos.customers.office.persistence;

import java.util.List;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeView;

public interface OfficeDao {

    OfficeBO findOfficeById(Short officeIdValue);
    
    OfficeDto findOfficeDtoById(Short valueOf);

    List<OfficeBO> findBranchsOnlyWithParentsMatching(String searchId);

    List<OfficeView> findActiveOfficeLevels();
}

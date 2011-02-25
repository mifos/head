package org.mifos.datasetup.repositories;

import org.hibernate.Session;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;

public class TestOffices extends TestEntities<OfficeBO> {
    public TestOffices(Session session) {
        super(session, OfficeBO.class);
    }

    public OfficeBO add(String shortName, String globalOfficeNum, int parentOfficeId, OfficeLevel level, String searchId, OfficeStatus status) {
        OfficeBO officeBO = new OfficeBO(shortName, shortName, globalOfficeNum, get((short)parentOfficeId), level, searchId, status, 1);
        session.saveOrUpdate(officeBO);
        return officeBO;
    }

    public OfficeBO addAny(String shortName) {
        return add(shortName, "1200", 0, OfficeLevel.BRANCHOFFICE, "1:1", OfficeStatus.ACTIVE);
    }
}

package org.mifos.customers.office.business;

import org.junit.Test;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.framework.TestUtils;

public class OfficeBOTest {

    @Test
    public void verifyEqualAndHashcodeContract() {
        OfficeBO x = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        OfficeBO notx = new OfficeBO(null, "C", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        OfficeBO y = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        OfficeBO z = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        TestUtils.assertEqualsAndHashContract(x, notx, y, z);

        x = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        notx = new OfficeBO(null, "A", "C", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        y = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        z = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        TestUtils.assertEqualsAndHashContract(x, notx, y, z);

        x = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        notx = new OfficeBO(null, "A", "B", "2", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        y = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        z = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        TestUtils.assertEqualsAndHashContract(x, notx, y, z);

        x = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        notx = new OfficeBO(null, "A", "C", "1", null, OfficeLevel.AREAOFFICE, "X", OfficeStatus.ACTIVE);
        y = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        z = new OfficeBO(null, "A", "B", "1", null, OfficeLevel.AREAOFFICE, "S", OfficeStatus.ACTIVE);
        TestUtils.assertEqualsAndHashContract(x, notx, y, z);
    }

}

package org.mifos.application.holiday.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.Mockito;

public class HolidayServiceFacadeWebTierIntegrationTest extends MifosIntegrationTestCase{


    HolidayDetails holidayDetails;
    HolidayServiceFacade holidayServiceFacade;
    OfficePersistence officePersistence;

    public HolidayServiceFacadeWebTierIntegrationTest() throws Exception {
        super();
    }

    @Override
    public void setUp() throws Exception{
        String name = "testHoliday";
        DateTime dateTime = new DateTime();
        Date fromDate = dateTime.plusDays(10).toDate();
        Date thruDate = dateTime.plusDays(20).toDate();
        RepaymentRuleTypes repaymentRule = RepaymentRuleTypes.SAME_DAY;
        holidayDetails = new HolidayDetails(name, fromDate, thruDate, repaymentRule);
        officePersistence = Mockito.mock(OfficePersistence.class);
        holidayServiceFacade = new HolidayServiceFacadeWebTier(officePersistence);
    }

    public void testShouldCreateHoliday() throws ServiceException {
        List<Short> officeIds = new ArrayList<Short>();
        Short officeId = new Short((short) 1);
        officeIds.add(officeId);
        holidayServiceFacade = new HolidayServiceFacadeWebTier(new OfficePersistence());
        holidayServiceFacade.createHoliday(holidayDetails, officeIds);
    }
}

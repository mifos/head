package org.mifos.application.holiday.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HolidayDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public HolidayDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    private HolidayDao holidayDao;

    // collaborators
    private final GenericDao genericDao = new GenericDaoHibernate();
    
    // test data
    private HolidayBO holiday;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        holidayDao = new HolidayDaoHibernate(genericDao);
    }

    public void testShouldSaveHoliday() throws Exception {

        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short) 1, "RepaymentRule-SameDay");
        holiday = new HolidayBO(holidayPK, null, "Test Holiday Dao", repaymentRuleEntity);
        // Disable date Validation because startDate is less than today
        holiday.setValidationEnabled(false);
        
        // verification
        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();
        assertTrue(holidays.isEmpty());
        
        holidayDao.save(holiday);
        
        // verification
        holidays = holidayDao.findAllHolidaysThisYearAndNext();
        assertFalse(holidays.isEmpty());
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList(holiday));
    }

    public void testShouldFindAllHolidaysWithinThisAndNextYear() {

        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2).toDateMidnight().toDateTime();
        DateTime secondOfJanTwoYears = secondOfJanNextYear.plusYears(1);
        
        Holiday holidayThisYear = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holidayNextYear = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holidayTwoYearsAway = new HolidayBuilder().from(secondOfJanTwoYears).to(secondOfJanTwoYears).build();
        insert(holidayThisYear);
        insert(holidayNextYear);
        insert(holidayTwoYearsAway);
        
        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();
        
        assertFalse(holidays.isEmpty());
        assertThat(holidays.size(), is(2));
        
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList((HolidayBO)holidayThisYear, (HolidayBO)holidayNextYear, (HolidayBO)holidayTwoYearsAway));
    }
    
    public void ignore_testShouldFindAllHolidaysOrderedByFromDateAscending() {

        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        DateTime lastDayOfYear = secondlastDayOfYear.plusDays(1);
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2).toDateMidnight().toDateTime();
        DateTime thirdOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2).toDateMidnight().toDateTime();
        
        Holiday holiday1 = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holiday2 = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holiday3 = new HolidayBuilder().from(thirdOfJanNextYear).to(thirdOfJanNextYear).build();
        Holiday holiday4 = new HolidayBuilder().from(lastDayOfYear).to(lastDayOfYear).build();
        insert(holiday2);
        insert(holiday3);
        insert(holiday1);
        insert(holiday4);
        
        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();
        
        assertTrue(holidays.get(0).encloses(secondlastDayOfYear.toDate()));
        assertTrue(holidays.get(1).encloses(lastDayOfYear.toDate()));
        assertTrue(holidays.get(2).encloses(secondOfJanNextYear.toDate()));
        assertTrue(holidays.get(3).encloses(thirdOfJanNextYear.toDate()));
        // cleanup
        TestObjectFactory.cleanUpHolidays(Arrays.asList((HolidayBO)holiday1, (HolidayBO)holiday2, (HolidayBO)holiday3, (HolidayBO)holiday4));
    }

    private void insert(final Holiday holiday) {
        holidayDao.save((HolidayBO)holiday);
    }
}

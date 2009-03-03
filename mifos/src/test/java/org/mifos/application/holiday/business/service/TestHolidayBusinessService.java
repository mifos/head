package org.mifos.application.holiday.business.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayBusinessService extends MifosIntegrationTest {

	public TestHolidayBusinessService() throws SystemException, ApplicationException {
        super();
    }


    private HolidayBO holidayEntity;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(holidayEntity);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetHolidays() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
		RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short)1);
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<HolidayBO> holidays = new HolidayBusinessService().getHolidays(Calendar.getInstance().get(Calendar.YEAR));
		assertNotNull(holidays);
		assertEquals(1, holidays.size());
	}

	public void testGetRepaymentRuleTypes() throws Exception {
		List<RepaymentRuleEntity> repaymentRules = new HolidayBusinessService().getRepaymentRuleTypes();
		assertNotNull(repaymentRules);
		assertEquals(3, repaymentRules.size());
	}
	
	
	public void testGetDistinctYears() throws Exception {
		List<HolidayBO> distinctYears = new HolidayBusinessService().getDistinctYears();
		assertNotNull(distinctYears);		
	}
	
}

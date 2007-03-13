package org.mifos.application.holiday.business.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayBusinessService extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetHolidays() throws Exception {
		
		HolidayPK holidayPK = new HolidayPK((short)1, new Date());
		HolidayBO holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		List<HolidayBO> holidays = new HolidayBusinessService()
				.getHolidays(Calendar.getInstance().get(Calendar.YEAR), (short)1);
		
		assertNotNull(holidays);
		assertEquals(1, holidays.size());
		//TestObjectFactory.cleanUp(holidayEntity);
		TestObjectFactory.cleanUpHolidays(holidays);
	}

	public void getRepaymentRuleTypes() throws Exception {
		List<RepaymentRuleEntity> repaymentRules = new HolidayBusinessService()
		.getRepaymentRuleTypes((short) 1);
		assertNotNull(repaymentRules);
		assertEquals(3, repaymentRules.size());
	}
}

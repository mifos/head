package org.mifos.application.holiday.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayPersistence extends MifosTestCase {

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
		RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short)1, "RepaymentRule-SameDay");
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", repaymentRuleEntity);
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<HolidayBO> holidays = new HolidayPersistence().getHolidays(Calendar.getInstance().get(Calendar.YEAR));
		assertNotNull(holidays);
		assertEquals(1, holidays.size());

		TestObjectFactory.cleanUpHolidays(holidays);
		holidayEntity = null;

		holidays = new HolidayPersistence().getHolidays(Calendar.getInstance().get(Calendar.YEAR) - 1);
		assertNotNull(holidays);
		assertEquals(holidays.size(), 0);
	}

	public void testGetRepaymentRuleTypes() throws Exception {
		List<RepaymentRuleEntity> repaymentRules = new HolidayPersistence().getRepaymentRuleTypes();
		assertNotNull(repaymentRules);
		assertEquals(3, repaymentRules.size());
	}
	
	public void testGetUnAppliedHolidaysAgainstAppliedOnes() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
		RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short)1, "RepaymentRule-SameDay");
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", repaymentRuleEntity);
		holidayEntity.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();
		
		//There should not be any UnappliedHolidays
		assertNotNull(holidays);
		assertEquals(holidays.size(), 0);
		
		TestObjectFactory.cleanUpHolidays(holidays);
		holidayEntity = null;
	}
	
	public void testGetUnAppliedHolidaysAgainst_Un_AppliedOnes() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
		RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short)1, "RepaymentRule-SameDay");
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", repaymentRuleEntity);
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();
		
		//There should be exactly one UnappliedHoliday
		assertNotNull(holidays);
		assertEquals(1, holidays.size());
		
		TestObjectFactory.cleanUpHolidays(holidays);
		holidayEntity = null;
	}
	

	public void testGetDistinctYears() throws Exception {
		List<HolidayBO> distinctYears = new HolidayPersistence().getDistinctYears();
		assertNotNull(distinctYears);		
	}
	
}

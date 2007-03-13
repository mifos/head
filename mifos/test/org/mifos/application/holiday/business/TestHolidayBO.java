package org.mifos.application.holiday.business;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayBO extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testAddHoliday() throws Exception {
		long fromDateMillis = new DateMidnight().getMillis();
		HolidayPK holidayPK = new HolidayPK((short)1, new Date(fromDateMillis));
		HolidayBO holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate().getTime());
		assertEquals(fromDateMillis, holidayEntity.getHolidayThruDate().getTime());
		TestObjectFactory.cleanUp(holidayEntity);
	}
	
	/**
	 * test Holiday From Date Validations Failure.
	 */
	public void testHolidayFromDateValidationFailure() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short)1, new Date());
		HolidayBO holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		try {
			holidayEntity.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			
			TestObjectFactory.cleanUp(holidayEntity);
			// If it succedded to create this holiday
			// Asssert false as it shouldn't by validations.
			assertTrue(false); 
		} catch(ApplicationException e) {
			return;
		}
	}
	
	/**
	 * test Holiday From Date Validations Success.
	 */
	public void testHolidayFromDateValidationSuccess() throws Exception {
		long fromDateMillis = new DateMidnight().getMillis();
		Calendar fromDate = Calendar.getInstance();
		fromDate.setTimeInMillis(fromDateMillis);
		fromDate.add(Calendar.DAY_OF_MONTH, 1);
		fromDateMillis = fromDate.getTimeInMillis();
		
		HolidayPK holidayPK = new HolidayPK((short)1, new Date(fromDateMillis));
		
		HolidayBO holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate().getTime());
		// automatically Fromdate is copied into thruDate if thrudate is null.
		assertEquals(fromDateMillis, holidayEntity.getHolidayThruDate().getTime());
		
		TestObjectFactory.cleanUp(holidayEntity);
	}
	
	/** 
	 * test Holiday From Date Against Thru Date Failure.
	 */
	public void testHolidayFromDateAgainstThruDateFailure() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short)1, Calendar.getInstance().getTime());
		Calendar thruDate = Calendar.getInstance();
		thruDate.add(Calendar.DAY_OF_MONTH, -1);
		
		HolidayBO holidayEntity = new HolidayBO(holidayPK, thruDate.getTime(), "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		try {
			holidayEntity.save();
			
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			
			TestObjectFactory.cleanUp(holidayEntity);
			
			// If it succedded to create this holiday
			// Asssert false as it shouldn't by validation rules.
			assertTrue(false); 
		} catch(ApplicationException e) {
			return;
		}
	}
	
	/** 
	 * test Holiday From Date Against Thru Date Success.
	 */
	public void testHolidayFromDateAgainstThruDateSucces() throws Exception {
		long fromDateMillis = new DateMidnight().getMillis();
		Calendar fromDate = Calendar.getInstance();
		fromDate.setTimeInMillis(fromDateMillis);
		fromDate.add(Calendar.DAY_OF_MONTH, 1);
		fromDateMillis = fromDate.getTimeInMillis();
		
		HolidayPK holidayPK = new HolidayPK((short)1, new Date(fromDateMillis));
		
		Calendar thruDate = Calendar.getInstance();
		long thruDateMillis = new DateMidnight().getMillis();
		thruDate.setTimeInMillis(thruDateMillis);
		thruDate.add(Calendar.DAY_OF_MONTH, 1);
		thruDateMillis = thruDate.getTimeInMillis();
		
		HolidayBO holidayEntity = new HolidayBO(holidayPK, thruDate.getTime(), "Test Holiday",
				(short) 1, (short) 1, "Same Day");// the last string has no effect.
		
		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate().getTime());
		assertEquals(thruDateMillis, holidayEntity.getHolidayThruDate().getTime());
		
		TestObjectFactory.cleanUp(holidayEntity);
	}
	
	/* Do we need an update script like
	   UPDATE HOLIDAY SET HOLIDAY_THRU_DATE = HOLIDAY_FROM_DATE WHERE 
       HOLIDAY_THRU_DATE is NULL;
       ?  Doing this for save and update will fix it for future cases.
	 */
	public void testSaveSuppliesThruDate() throws Exception {		
		long startDate = new DateMidnight(2003, 1, 26).getMillis();
		HolidayBO holiday =
			new HolidayBO(
				new HolidayPK((short)1, new Date(startDate)),
				null,
				"Test Day",
				TestObjectFactory.TEST_LOCALE,
				RepaymentRuleTypes.SAME_DAY.getValue(),
				null
			);
		
		// Disable date Validation because startDate is less than today
		holiday.setValidationEnabled(false);
		
		holiday.save();
		assertEquals(startDate, holiday.getHolidayThruDate().getTime());
	}

	public void testUpdateSuppliesThruDate() throws Exception {
		long startDate = new DateMidnight(2003, 1, 26).getMillis();
		HolidayBO holiday =
			new HolidayBO(
				new HolidayPK((short)1, new Date(startDate)),
				null,
				"Test Day",
				TestObjectFactory.TEST_LOCALE,
				RepaymentRuleTypes.SAME_DAY.getValue(),
				null
			);

		// Disable date Validation because startDate is less than today
		holiday.setValidationEnabled(false);
		
		holiday.update(holiday.getHolidayPK(), null, "New Name");
		assertEquals(startDate, holiday.getHolidayThruDate().getTime());
	}
}

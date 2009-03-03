package org.mifos.application.holiday.business;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateMidnight;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestHolidayBO extends MifosIntegrationTest {

	public TestHolidayBO() throws SystemException, ApplicationException {
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

	public void testAddHoliday() throws Exception {
		long fromDateMillis = new DateMidnight().getMillis();
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date(fromDateMillis));
		RepaymentRuleEntity entity = new HolidayPersistence()
				.getRepaymentRule((short) 1);
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
		// Disable date Validation because startDate is less than today
		holidayEntity.setValidationEnabled(false);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate()
				.getTime());
		assertEquals(fromDateMillis, holidayEntity.getHolidayThruDate()
				.getTime());
	}

	/**
	 * test Holiday From Date Validations Failure.
	 */
	public void testHolidayFromDateValidationFailure() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
		RepaymentRuleEntity entity = new HolidayPersistence()
				.getRepaymentRule((short) 1);
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);

		try {
			holidayEntity.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();
			// If it succedded to create this holiday
			// Asssert false as it shouldn't by validations.
			assertTrue(false);
		}
		catch (ApplicationException e) {
			holidayEntity = null;
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

		HolidayPK holidayPK = new HolidayPK((short) 1, new Date(fromDateMillis));
		RepaymentRuleEntity entity = new HolidayPersistence()
				.getRepaymentRule((short) 1);
		holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate()
				.getTime());
		// automatically Fromdate is copied into thruDate if thrudate is null.
		assertEquals(fromDateMillis, holidayEntity.getHolidayThruDate()
				.getTime());
	}

	/** 
	 * test Holiday From Date Against Thru Date Failure.
	 */
	public void testHolidayFromDateAgainstThruDateFailure() throws Exception {
		HolidayPK holidayPK = new HolidayPK((short) 1, Calendar.getInstance()
				.getTime());
		Calendar thruDate = Calendar.getInstance();
		thruDate.add(Calendar.DAY_OF_MONTH, -1);
		RepaymentRuleEntity entity = new HolidayPersistence()
				.getRepaymentRule((short) 1);
		holidayEntity = new HolidayBO(holidayPK, thruDate.getTime(),
				"Test Holiday", entity);

		try {
			holidayEntity.save();

			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();

			// If it succedded to create this holiday
			// Asssert false as it shouldn't by validation rules.
			assertTrue(false);
		}
		catch (ApplicationException e) {
			holidayEntity = null;
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

		HolidayPK holidayPK = new HolidayPK((short) 1, new Date(fromDateMillis));

		Calendar thruDate = Calendar.getInstance();
		long thruDateMillis = new DateMidnight().getMillis();
		thruDate.setTimeInMillis(thruDateMillis);
		thruDate.add(Calendar.DAY_OF_MONTH, 1);
		thruDateMillis = thruDate.getTimeInMillis();
		RepaymentRuleEntity entity = new HolidayPersistence()
				.getRepaymentRule((short) 1);
		holidayEntity = new HolidayBO(holidayPK, thruDate.getTime(),
				"Test Holiday", entity);

		holidayEntity.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		holidayEntity = (HolidayBO) TestObjectFactory.getObject(
				HolidayBO.class, holidayEntity.getHolidayPK());

		assertEquals("Test Holiday", holidayEntity.getHolidayName());
		assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate()
				.getTime());
		assertEquals(thruDateMillis, holidayEntity.getHolidayThruDate()
				.getTime());
	}

	/* Do we need an update script like
	 UPDATE HOLIDAY SET HOLIDAY_THRU_DATE = HOLIDAY_FROM_DATE WHERE 
	 HOLIDAY_THRU_DATE is NULL;
	 ?  Doing this for save and update will fix it for future cases.
	 */
	public void testSaveSuppliesThruDate() throws Exception {
		long startDate = new DateMidnight(2003, 1, 26).getMillis();
		RepaymentRuleEntity entity = new HolidayPersistence()
				.getRepaymentRule((short) 1);
		HolidayBO holiday = new HolidayBO(new HolidayPK((short) 1, new Date(
				startDate)), null, "Test Day", entity);

		// Disable date Validation because startDate is less than today
		holiday.setValidationEnabled(false);

		holiday.save();
		assertEquals(startDate, holiday.getHolidayThruDate().getTime());
	}

	public void testUpdateSuppliesThruDate() throws Exception {
		long startDate = new DateMidnight(2003, 1, 26).getMillis();
		RepaymentRuleEntity entity = new RepaymentRuleEntity((short)1, "RepaymentRule-SameDay");
		HolidayBO holiday =
			new HolidayBO(
				new HolidayPK((short)1, new Date(startDate)),
				null,
				"Test Day",entity
			);

		// Disable date Validation because startDate is less than today
		holiday.setValidationEnabled(false);

		holiday.update(holiday.getHolidayPK(), null, "New Name");
		assertEquals(startDate, holiday.getHolidayThruDate().getTime());
	}
}
